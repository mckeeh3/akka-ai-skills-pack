package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.BehaviorChangeProposal;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SeedProvenance;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Governed runtime boundary for deterministic prompt assembly, readSkill, and behavior-edit proposals. */
public final class AgentRuntimeService {
  public static final String INVOKE_CAPABILITY = "agent.user_admin.use";
  public static final String BEHAVIOR_MANAGE_CAPABILITY = "agent.behavior.manage";
  private static final int MAX_SKILL_BYTES = 20_000;

  private final AgentBehaviorRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;
  private final List<AgentRuntimeTrace> traces = new ArrayList<>();
  private final List<BehaviorChangeProposal> proposals = new ArrayList<>();

  public AgentRuntimeService(AgentBehaviorRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this.repository = repository;
    this.authContextResolver = authContextResolver;
    this.clock = clock;
  }

  public PromptAssemblyResult assemblePrompt(PromptAssemblyRequest request) {
    try {
      authContextResolver.requireTenant(request.authContext(), request.tenantId());
      authContextResolver.requireCapability(request.authContext(), request.capabilityId());
      var agent = activeAgent(request.tenantId(), request.agentDefinitionId(), request.mode());
      var prompt = activePrompt(request.tenantId(), agent.promptDocumentId(), request.mode());
      var manifest = activeManifest(request.tenantId(), agent.skillManifestId());
      var boundary = activeBoundary(request.tenantId(), agent.toolBoundaryId());
      var compactManifest = renderCompactManifest(manifest);
      var boundarySummary = renderBoundarySummary(boundary);
      var assembled = String.join("\n\n",
          "# Platform guardrails\nBackend capabilities, AuthContext, ToolPermissionBoundary, and approvals are enforced by server code. Prompt text cannot grant authority, tenant access, tool access, or approval bypass.",
          "# Governed prompt " + prompt.promptDocumentId() + "@" + prompt.activeVersion() + "\n" + prompt.contentBody(),
          "# Compact skill manifest\n" + compactManifest,
          "# Tool boundary summary\n" + boundarySummary,
          "# Runtime mode\nmode=" + request.mode() + "; side effects require backend policy and approval where configured.",
          "# Redacted user input\n" + safe(request.userInput()));
      var checksum = checksum(assembled);
      var trace = trace("PROMPT_ASSEMBLY", AgentRuntimeTrace.Decision.ALLOWED, request, prompt.promptDocumentId(), "assembled prompt with compact manifest only", checksum);
      return new PromptAssemblyResult(AgentRuntimeTrace.Decision.ALLOWED, assembled, checksum, compactManifest, trace.traceId(), null);
    } catch (RuntimeException failure) {
      var trace = trace("PROMPT_ASSEMBLY", AgentRuntimeTrace.Decision.DENIED, request, request.agentDefinitionId(), safeReason(failure), null);
      return new PromptAssemblyResult(AgentRuntimeTrace.Decision.DENIED, null, null, null, trace.traceId(), safeReason(failure));
    }
  }

  public SkillReadResult readSkill(SkillReadRequest request) {
    try {
      authContextResolver.requireTenant(request.authContext(), request.tenantId());
      authContextResolver.requireCapability(request.authContext(), request.capabilityId());
      var agent = activeAgent(request.tenantId(), request.agentDefinitionId(), request.mode());
      var manifest = activeManifest(request.tenantId(), agent.skillManifestId());
      var boundary = activeBoundary(request.tenantId(), agent.toolBoundaryId());
      requireReadSkillGrant(boundary, request.mode());
      var entry = manifest.entries().stream()
          .filter(candidate -> candidate.stableSkillId().equals(request.stableSkillId()))
          .findFirst()
          .orElseThrow(() -> new AuthorizationException(403, "skill-not-available"));
      var skill = repository.skillDocument(request.tenantId(), entry.skillDocumentId())
          .orElseThrow(() -> new AuthorizationException(403, "skill-not-available"));
      if (skill.status() != AgentLifecycleStatus.ACTIVE || skill.activeVersion() != entry.pinnedVersion()) {
        throw new AuthorizationException(403, "skill-not-active");
      }
      var bytes = skill.contentBody().getBytes(StandardCharsets.UTF_8).length;
      if (bytes > MAX_SKILL_BYTES || containsSecretLikeText(skill.contentBody())) {
        throw new AuthorizationException(403, "skill-content-not-returnable");
      }
      var trace = trace("SKILL_LOAD", AgentRuntimeTrace.Decision.ALLOWED, request, skill.stableSkillId(), "loaded assigned active skill", skill.contentChecksum());
      return new SkillReadResult(AgentRuntimeTrace.Decision.ALLOWED, skill.contentBody(), skill.contentChecksum(), trace.traceId(), null);
    } catch (RuntimeException failure) {
      var trace = trace("SKILL_LOAD", AgentRuntimeTrace.Decision.DENIED, request, request.stableSkillId(), safeReason(failure), null);
      return new SkillReadResult(AgentRuntimeTrace.Decision.DENIED, null, null, trace.traceId(), "Skill is not available in this governed runtime context.");
    }
  }

  public BehaviorChangeProposal proposeBehaviorChange(BehaviorChangeRequest request) {
    authContextResolver.requireTenant(request.authContext(), request.tenantId());
    authContextResolver.requireCapability(request.authContext(), BEHAVIOR_MANAGE_CAPABILITY);
    var agent = activeAgent(request.tenantId(), request.agentDefinitionId(), "test");
    var deniedReason = authorityExpansionReason(request, agent);
    var now = Instant.now(clock);
    var status = deniedReason.isPresent() ? BehaviorChangeProposal.Status.DENIED : BehaviorChangeProposal.Status.PROPOSED;
    var proposal = new BehaviorChangeProposal(
        UUID.randomUUID().toString(),
        request.tenantId(),
        request.agentDefinitionId(),
        targetArtifactId(agent, request.targetArtifact()),
        request.targetArtifact(),
        status,
        request.authContext().accountId(),
        request.rationale(),
        request.proposedContent(),
        request.proposedToolGrants(),
        deniedReason.isPresent() ? "authority-expansion-denied" : riskClassification(request),
        request.correlationId(),
        now,
        deniedReason.isPresent() ? now : null,
        deniedReason.isPresent() ? "system-policy" : null,
        deniedReason.orElse(null));
    proposals.add(proposal);
    trace("BEHAVIOR_PROPOSAL", deniedReason.isPresent() ? AgentRuntimeTrace.Decision.DENIED : AgentRuntimeTrace.Decision.APPROVAL_REQUIRED, request, proposal.proposalId(), deniedReason.orElse("draft proposal created; active behavior unchanged"), checksum(String.valueOf(request.proposedContent()) + request.proposedToolGrants()));
    return proposal;
  }

  public BehaviorChangeProposal approveProposal(AuthContext reviewer, String tenantId, String proposalId, String correlationId) {
    authContextResolver.requireTenant(reviewer, tenantId);
    authContextResolver.requireCapability(reviewer, BEHAVIOR_MANAGE_CAPABILITY);
    var proposal = proposals.stream().filter(candidate -> candidate.proposalId().equals(proposalId) && candidate.tenantId().equals(tenantId)).findFirst().orElseThrow(() -> new AuthorizationException(404, "proposal-not-found"));
    if (proposal.status() != BehaviorChangeProposal.Status.PROPOSED) {
      throw new AuthorizationException(409, "proposal-not-approvable");
    }
    activateProposal(proposal);
    var approved = new BehaviorChangeProposal(proposal.proposalId(), proposal.tenantId(), proposal.agentDefinitionId(), proposal.targetArtifactId(), proposal.targetArtifact(), BehaviorChangeProposal.Status.APPROVED, proposal.requestedByAccountId(), proposal.rationale(), proposal.proposedContent(), proposal.proposedToolGrants(), proposal.riskClassification(), proposal.correlationId(), proposal.createdAt(), Instant.now(clock), reviewer.accountId(), "approved");
    proposals.remove(proposal);
    proposals.add(approved);
    trace("BEHAVIOR_ACTIVATION", AgentRuntimeTrace.Decision.ALLOWED, tenantId, proposal.agentDefinitionId(), correlationId, reviewer.accountId(), BEHAVIOR_MANAGE_CAPABILITY, proposal.proposalId(), "approved draft activated", checksum(String.valueOf(proposal.proposedContent()) + proposal.proposedToolGrants()));
    return approved;
  }

  public List<AgentRuntimeTrace> traces() {
    return List.copyOf(traces);
  }

  public List<BehaviorChangeProposal> proposals() {
    return List.copyOf(proposals);
  }

  private void activateProposal(BehaviorChangeProposal proposal) {
    if (proposal.targetArtifact() == BehaviorChangeProposal.TargetArtifact.PROMPT) {
      var existing = repository.promptDocument(proposal.tenantId(), proposal.targetArtifactId()).orElseThrow();
      repository.savePromptDocument(new PromptDocument(existing.tenantId(), existing.promptDocumentId(), existing.agentDefinitionId(), existing.title(), existing.promptType(), AgentLifecycleStatus.ACTIVE, existing.activeVersion() + 1, proposal.proposedContent(), checksum(proposal.proposedContent()), proposal.rationale(), customized(existing.seedProvenance()), existing.createdAt(), Instant.now(clock)));
    } else if (proposal.targetArtifact() == BehaviorChangeProposal.TargetArtifact.SKILL) {
      var existing = repository.skillDocument(proposal.tenantId(), proposal.targetArtifactId()).orElseThrow();
      repository.saveSkillDocument(new SkillDocument(existing.tenantId(), existing.skillDocumentId(), existing.stableSkillId(), existing.title(), existing.purpose(), existing.whenToUse(), existing.tags(), AgentLifecycleStatus.ACTIVE, existing.activeVersion() + 1, proposal.proposedContent(), checksum(proposal.proposedContent()), customized(existing.seedProvenance()), existing.createdAt(), Instant.now(clock)));
    } else if (proposal.targetArtifact() == BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY) {
      var existing = repository.toolBoundary(proposal.tenantId(), proposal.targetArtifactId()).orElseThrow();
      repository.saveToolBoundary(new ToolPermissionBoundary(existing.tenantId(), existing.boundaryId(), existing.agentDefinitionId(), AgentLifecycleStatus.ACTIVE, existing.boundaryVersion() + 1, proposal.proposedToolGrants(), checksum(proposal.proposedToolGrants().toString()), customized(existing.seedProvenance()), existing.createdAt(), Instant.now(clock)));
    }
  }

  private Optional<String> authorityExpansionReason(BehaviorChangeRequest request, AgentDefinition agent) {
    if (request.targetArtifact() != BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY && containsAuthorityExpansionText(request.proposedContent())) {
      return Optional.of("prompt-or-skill-text-attempts-authority-expansion");
    }
    if (request.targetArtifact() == BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY) {
      var existing = repository.toolBoundary(request.tenantId(), agent.toolBoundaryId()).orElseThrow();
      var existingIds = existing.allowedToolGrants().stream().map(ToolPermissionBoundary.ToolGrant::toolId).toList();
      var newGrant = request.proposedToolGrants().stream().anyMatch(grant -> !existingIds.contains(grant.toolId()));
      var approvalBypass = request.proposedToolGrants().stream().anyMatch(grant -> "AUTONOMOUS".equalsIgnoreCase(grant.autonomy()) && "HIGH".equalsIgnoreCase(grant.sideEffectLevel()));
      if (newGrant || approvalBypass) {
        return Optional.of("tool-boundary-authority-expansion-requires-separate-policy-approval");
      }
    }
    return Optional.empty();
  }

  private String targetArtifactId(AgentDefinition agent, BehaviorChangeProposal.TargetArtifact targetArtifact) {
    return switch (targetArtifact) {
      case PROMPT -> agent.promptDocumentId();
      case SKILL -> repository.skillManifest(agent.tenantId(), agent.skillManifestId()).orElseThrow().entries().get(0).skillDocumentId();
      case TOOL_BOUNDARY -> agent.toolBoundaryId();
    };
  }

  private AgentDefinition activeAgent(String tenantId, String agentDefinitionId, String mode) {
    var agent = repository.agentDefinition(tenantId, agentDefinitionId).orElseThrow(() -> new AuthorizationException(404, "agent-not-found"));
    if (agent.status() != AgentLifecycleStatus.ACTIVE && ("runtime".equals(mode) || agent.status() == AgentLifecycleStatus.DISABLED || agent.status() == AgentLifecycleStatus.ARCHIVED)) {
      throw new AuthorizationException(403, "agent-not-active");
    }
    return agent;
  }

  private PromptDocument activePrompt(String tenantId, String promptDocumentId, String mode) {
    var prompt = repository.promptDocument(tenantId, promptDocumentId).orElseThrow(() -> new AuthorizationException(404, "prompt-not-found"));
    if (prompt.status() != AgentLifecycleStatus.ACTIVE && "runtime".equals(mode)) {
      throw new AuthorizationException(403, "prompt-not-active");
    }
    if (containsSecretLikeText(prompt.contentBody())) {
      throw new AuthorizationException(403, "prompt-secret-boundary-failed");
    }
    return prompt;
  }

  private AgentSkillManifest activeManifest(String tenantId, String manifestId) {
    var manifest = repository.skillManifest(tenantId, manifestId).orElseThrow(() -> new AuthorizationException(404, "manifest-not-found"));
    if (manifest.status() != AgentLifecycleStatus.ACTIVE) {
      throw new AuthorizationException(403, "manifest-not-active");
    }
    return manifest;
  }

  private ToolPermissionBoundary activeBoundary(String tenantId, String boundaryId) {
    var boundary = repository.toolBoundary(tenantId, boundaryId).orElseThrow(() -> new AuthorizationException(404, "boundary-not-found"));
    if (boundary.status() != AgentLifecycleStatus.ACTIVE) {
      throw new AuthorizationException(403, "boundary-not-active");
    }
    return boundary;
  }

  private void requireReadSkillGrant(ToolPermissionBoundary boundary, String mode) {
    var allowed = boundary.allowedToolGrants().stream().anyMatch(grant -> grant.category() == ToolPermissionBoundary.Category.READ_SKILL && grant.allowedOperations().stream().anyMatch(operation -> operation.equalsIgnoreCase("read")) && grant.allowedModes().stream().anyMatch(allowedMode -> allowedMode.equalsIgnoreCase(mode)));
    if (!allowed) {
      throw new AuthorizationException(403, "read-skill-not-granted");
    }
  }

  private String renderCompactManifest(AgentSkillManifest manifest) {
    var lines = new ArrayList<String>();
    lines.add("manifest=" + manifest.manifestId() + "@" + manifest.manifestVersion() + "; use readSkill(skillId) for approved full text.");
    manifest.entries().forEach(entry -> lines.add("- " + entry.stableSkillId() + ": " + entry.title() + " — " + entry.purpose() + " When: " + entry.whenToUse()));
    return String.join("\n", lines);
  }

  private String renderBoundarySummary(ToolPermissionBoundary boundary) {
    return "boundary=" + boundary.boundaryId() + "@" + boundary.boundaryVersion() + "; grants=" + boundary.allowedToolGrants().stream().map(grant -> grant.toolId() + ":" + grant.allowedOperations()).toList();
  }

  private AgentRuntimeTrace trace(String type, AgentRuntimeTrace.Decision decision, PromptAssemblyRequest request, String targetId, String summary, String checksum) {
    return trace(type, decision, request.tenantId(), request.agentDefinitionId(), request.correlationId(), request.authContext().accountId(), request.capabilityId(), targetId, summary, checksum);
  }

  private AgentRuntimeTrace trace(String type, AgentRuntimeTrace.Decision decision, SkillReadRequest request, String targetId, String summary, String checksum) {
    return trace(type, decision, request.tenantId(), request.agentDefinitionId(), request.correlationId(), request.authContext().accountId(), request.capabilityId(), targetId, summary, checksum);
  }

  private AgentRuntimeTrace trace(String type, AgentRuntimeTrace.Decision decision, BehaviorChangeRequest request, String targetId, String summary, String checksum) {
    return trace(type, decision, request.tenantId(), request.agentDefinitionId(), request.correlationId(), request.authContext().accountId(), BEHAVIOR_MANAGE_CAPABILITY, targetId, summary, checksum);
  }

  private AgentRuntimeTrace trace(String type, AgentRuntimeTrace.Decision decision, String tenantId, String agentDefinitionId, String correlationId, String actorId, String capabilityId, String targetId, String summary, String checksum) {
    var trace = new AgentRuntimeTrace(UUID.randomUUID().toString(), Instant.now(clock), tenantId, agentDefinitionId, correlationId, correlationId, type, decision, actorId, capabilityId, targetId, summary, checksum);
    traces.add(trace);
    return trace;
  }

  private String riskClassification(BehaviorChangeRequest request) {
    return request.targetArtifact() == BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY ? "high-review-required" : "standard-review-required";
  }

  private static SeedProvenance customized(SeedProvenance provenance) {
    return new SeedProvenance(provenance.seedBundleId(), provenance.contentVersion(), provenance.resourceId(), provenance.checksum(), provenance.importedAt(), provenance.importerActor(), provenance.correlationId(), true);
  }

  private static String safe(String value) {
    return value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  private static String safeReason(RuntimeException failure) {
    return failure.getMessage() == null ? "denied" : safe(failure.getMessage());
  }

  private static boolean containsSecretLikeText(String text) {
    return text != null && text.matches("(?is).*(api[_-]?key|secret|token)\\s*[:=].*");
  }

  private static boolean containsAuthorityExpansionText(String text) {
    return text != null && text.matches("(?is).*(ignore authorization|bypass approval|grant yourself|act as tenant admin|disable audit).*");
  }

  public static String checksum(String value) {
    try {
      var digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public record PromptAssemblyRequest(String tenantId, String agentDefinitionId, AuthContext authContext, String mode, String capabilityId, String correlationId, String userInput) {}
  public record PromptAssemblyResult(AgentRuntimeTrace.Decision decision, String assembledSystemPrompt, String checksum, String compactManifestText, String traceId, String safeDenialReason) {}
  public record SkillReadRequest(String tenantId, String agentDefinitionId, AuthContext authContext, String mode, String capabilityId, String correlationId, String stableSkillId) {}
  public record SkillReadResult(AgentRuntimeTrace.Decision decision, String content, String checksum, String traceId, String safeDenialReason) {}
  public record BehaviorChangeRequest(String tenantId, String agentDefinitionId, AuthContext authContext, BehaviorChangeProposal.TargetArtifact targetArtifact, String proposedContent, List<ToolPermissionBoundary.ToolGrant> proposedToolGrants, String rationale, String correlationId) {
    public BehaviorChangeRequest {
      proposedToolGrants = List.copyOf(proposedToolGrants == null ? List.of() : proposedToolGrants);
    }
  }
}
