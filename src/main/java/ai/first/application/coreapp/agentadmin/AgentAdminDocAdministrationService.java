package ai.first.application.coreapp.agentadmin;

import ai.first.application.foundation.agent.AgentBehaviorRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.ScopeType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SaaS-owner Agent Admin service boundary for AI-assisted prompt, skill, and reference-doc administration.
 *
 * <p>This slice defines the browser/API-facing contract and enforces SaaS-admin-only access. The durable
 * historical version store and full editing-agent lifecycle are intentionally implemented by later slices; this
 * service exposes the typed boundary those slices will fill without exposing stale tenant-scoped governance flows.
 */
public final class AgentAdminDocAdministrationService {
  public static final String LIST_AGENTS_CAPABILITY = AgentAdminService.LIST_DEFINITIONS;
  public static final String READ_AGENT_CAPABILITY = AgentAdminService.GET_DEFINITION;
  public static final String READ_PROMPT_CAPABILITY = AgentAdminService.GET_PROMPT_VERSION;
  public static final String READ_SKILL_CAPABILITY = AgentAdminService.GET_SKILL_VERSION;
  public static final String READ_REFERENCE_CAPABILITY = AgentAdminService.GET_REFERENCE_VERSION;
  public static final String UPDATE_AGENT_PROFILE_CAPABILITY = "saas_owner.admin.manage";
  public static final String DRAFT_EDIT_CAPABILITY = AgentRuntimeCapabilityIds.DRAFT_BEHAVIOR_CHANGE;
  public static final String CANCEL_EDIT_CAPABILITY = AgentRuntimeCapabilityIds.CANCEL_BEHAVIOR_CHANGE;
  public static final String SAVE_EDIT_CAPABILITY = AgentRuntimeCapabilityIds.ACTIVATE_BEHAVIOR_CHANGE;
  public static final String RESTORE_VERSION_CAPABILITY = AgentRuntimeCapabilityIds.ROLLBACK_BEHAVIOR_CHANGE;

  private final AgentBehaviorRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;
  private final Map<String, EditSessionRecord> sessions = new ConcurrentHashMap<>();

  public AgentAdminDocAdministrationService(AgentBehaviorRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
  }

  public AgentListResponse listAgents(AuthContextResolver.ResolvedMe actor, AgentListRequest request, String correlationId) {
    requireSaasAdmin(actor, LIST_AGENTS_CAPABILITY, "agent_admin.doc_agents.list.v1", correlationId);
    var filter = request == null ? new AgentListRequest(null, null) : request;
    var rows = repository.agentDefinitions(platformScopeId()).stream()
        .sorted(Comparator.comparing(AgentDefinition::displayName))
        .map(this::agentListRow)
        .toList();
    var filtered = rows.stream()
        .filter(row -> contains(row.agentName(), filter.nameContains()) || contains(row.shortPurpose(), filter.nameContains()))
        .filter(row -> filter.workstreamOrDomain() == null || filter.workstreamOrDomain().isBlank() || contains(row.workstreamDomain(), filter.workstreamOrDomain()))
        .toList();
    return new AgentListResponse(filtered, rows.size(), filtered.size(), List.of(traceId("agent-list", platformScopeId(), correlationId)));
  }

  public AgentProfileUpdateResult updateAgentProfile(AuthContextResolver.ResolvedMe actor, AgentProfileUpdateRequest request, String correlationId) {
    requireSaasAdmin(actor, UPDATE_AGENT_PROFILE_CAPABILITY, "agent_admin.doc_agent_profile.update.v1", correlationId);
    requireNonBlank(request == null ? null : request.agentDefinitionId(), "agentDefinitionId-required");
    var existing = agent(request.agentDefinitionId());
    var displayName = firstNonBlank(request.agentName(), existing.displayName()).trim();
    var purpose = firstNonBlank(request.purpose(), existing.description()).trim();
    if (displayName.isBlank()) throw new AuthorizationException(400, "agent-name-required");
    if (purpose.isBlank()) throw new AuthorizationException(400, "agent-purpose-required");
    var updated = new AgentDefinition(
        existing.tenantId(),
        existing.agentDefinitionId(),
        displayName,
        purpose,
        existing.placement(),
        existing.functionalAreaId(),
        existing.authorityLevel(),
        existing.status(),
        existing.promptDocumentId(),
        existing.activePromptVersion(),
        existing.skillManifestId(),
        existing.activeSkillManifestVersion(),
        existing.referenceManifestId(),
        existing.activeReferenceManifestVersion(),
        existing.toolBoundaryId(),
        existing.activeToolBoundaryVersion(),
        existing.modelConfigRefId(),
        existing.modelPolicyRefId(),
        existing.runtimeClassRef(),
        existing.traceRequirements(),
        existing.seedProvenance(),
        existing.createdAt(),
        Instant.now(clock));
    repository.saveAgentDefinition(updated);
    return new AgentProfileUpdateResult(agentListRow(updated), List.of(traceId("agent-profile-update", updated.agentDefinitionId(), correlationId)));
  }

  public AgentDocDetail agentDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    requireSaasAdmin(actor, READ_AGENT_CAPABILITY, "agent_admin.doc_agent.detail.v1", correlationId);
    var agent = agent(agentDefinitionId);
    var prompt = prompt(agent);
    var skills = skillSummaries(agent);
    var references = referenceSummaries(agent);
    return new AgentDocDetail(
        agent.agentDefinitionId(),
        agent.displayName(),
        agent.description(),
        agent.functionalAreaId(),
        lastEditTime(agent, prompt, skills.stream().map(SkillDocSummary::updatedAt).toList(), references.stream().map(ReferenceDocSummary::updatedAt).toList()),
        docSummary(AgentDocKind.PROMPT, prompt.promptDocumentId(), prompt.title(), prompt.changeSummary(), prompt.activeVersion(), prompt.updatedAt()),
        skills,
        references,
        List.of(traceId("agent-detail", agent.agentDefinitionId(), correlationId), traceId("runtime-doc-reads", agent.agentDefinitionId(), correlationId)));
  }

  public DocumentVersionDetail readDocumentVersion(AuthContextResolver.ResolvedMe actor, DocumentVersionRequest request, String correlationId) {
    requireDocumentCapability(actor, request == null ? null : request.kind(), "agent_admin.doc_version.read.v1", correlationId);
    var resolved = resolveDocument(request);
    if (request.version() != null && request.version() != resolved.currentVersion()) {
      throw new AuthorizationException(404, "document-version-not-found-in-current-slice");
    }
    return resolved.toVersionDetail(correlationId);
  }

  public VersionHistoryResponse versionHistory(AuthContextResolver.ResolvedMe actor, DocumentVersionRequest request, String correlationId) {
    requireDocumentCapability(actor, request == null ? null : request.kind(), "agent_admin.doc_version.history.v1", correlationId);
    var resolved = resolveDocument(request);
    var row = new VersionHistoryRow(resolved.currentVersion(), true, resolved.updatedAt(), resolved.title());
    return new VersionHistoryResponse(resolved.agentDefinitionId(), resolved.kind(), resolved.documentId(), List.of(row), List.of(traceId("version-history", resolved.documentId(), correlationId)));
  }

  public AdjacentDiffResponse adjacentDiff(AuthContextResolver.ResolvedMe actor, DocumentVersionRequest request, String correlationId) {
    requireDocumentCapability(actor, request == null ? null : request.kind(), "agent_admin.doc_version.diff.v1", correlationId);
    var resolved = resolveDocument(request);
    var selectedVersion = request.version() == null ? resolved.currentVersion() : request.version();
    if (selectedVersion != resolved.currentVersion()) {
      throw new AuthorizationException(404, "document-version-not-found-in-current-slice");
    }
    if (selectedVersion <= 1) {
      return new AdjacentDiffResponse(resolved.agentDefinitionId(), resolved.kind(), resolved.documentId(), null, selectedVersion, DiffStatus.NO_PRIOR_VERSION, "", List.of(traceId("version-diff", resolved.documentId(), correlationId)));
    }
    return new AdjacentDiffResponse(resolved.agentDefinitionId(), resolved.kind(), resolved.documentId(), selectedVersion - 1, selectedVersion, DiffStatus.PRIOR_VERSION_NOT_AVAILABLE_IN_CURRENT_SLICE, "", List.of(traceId("version-diff", resolved.documentId(), correlationId)));
  }

  public EditSessionRecord startEditSession(AuthContextResolver.ResolvedMe actor, StartEditSessionRequest request, String correlationId) {
    requireSaasAdmin(actor, DRAFT_EDIT_CAPABILITY, "agent_admin.doc_edit_session.start.v1", correlationId);
    var base = resolveDocument(new DocumentVersionRequest(request.agentDefinitionId(), request.kind(), request.documentId(), null));
    var session = new EditSessionRecord(
        "agent-doc-edit-session-" + UUID.randomUUID(),
        base.agentDefinitionId(),
        base.kind(),
        base.documentId(),
        base.currentVersion(),
        actor.account().accountId(),
        EditSessionStatus.DRAFTING,
        List.of(new EditInstruction(Instant.now(clock), actor.account().accountId(), safe(request.initialInstructions()))),
        null,
        null,
        List.of(),
        List.of(traceId("edit-session-start", base.documentId(), correlationId)),
        Instant.now(clock),
        null);
    sessions.put(session.sessionId(), session);
    return session;
  }

  public EditSessionRecord reviseEditSession(AuthContextResolver.ResolvedMe actor, ReviseEditSessionRequest request, String correlationId) {
    requireSaasAdmin(actor, DRAFT_EDIT_CAPABILITY, "agent_admin.doc_edit_session.revise.v1", correlationId);
    var existing = session(request.sessionId());
    ensureSessionActor(actor, existing);
    var instructions = new ArrayList<>(existing.instructions());
    instructions.add(new EditInstruction(Instant.now(clock), actor.account().accountId(), safe(request.instructions())));
    var revised = new EditSessionRecord(
        existing.sessionId(),
        existing.agentDefinitionId(),
        existing.kind(),
        existing.documentId(),
        existing.baseVersion(),
        existing.actorAccountId(),
        EditSessionStatus.PROPOSAL_READY,
        List.copyOf(instructions),
        safe(request.proposedContent()),
        safe(request.changeSummary()),
        List.copyOf(request.warnings() == null ? List.of() : request.warnings()),
        appendTrace(existing.traceLinks(), traceId("edit-session-revise", existing.sessionId(), correlationId)),
        existing.startedAt(),
        null);
    sessions.put(revised.sessionId(), revised);
    return revised;
  }

  public EditSessionRecord cancelEditSession(AuthContextResolver.ResolvedMe actor, EditSessionCommandRequest request, String correlationId) {
    requireSaasAdmin(actor, CANCEL_EDIT_CAPABILITY, "agent_admin.doc_edit_session.cancel.v1", correlationId);
    var existing = session(request.sessionId());
    ensureSessionActor(actor, existing);
    var cancelled = transitionSession(existing, EditSessionStatus.CANCELLED, correlationId);
    sessions.put(cancelled.sessionId(), cancelled);
    return cancelled;
  }

  public SaveEditSessionResult saveEditSession(AuthContextResolver.ResolvedMe actor, EditSessionCommandRequest request, String correlationId) {
    requireSaasAdmin(actor, SAVE_EDIT_CAPABILITY, "agent_admin.doc_edit_session.save.v1", correlationId);
    var existing = session(request.sessionId());
    ensureSessionActor(actor, existing);
    if (existing.status() == EditSessionStatus.CANCELLED) throw new AuthorizationException(409, "edit-session-cancelled");
    if (existing.proposedContent() == null || existing.proposedContent().isBlank()) throw new AuthorizationException(409, "edit-session-proposal-required");
    var current = resolveDocument(new DocumentVersionRequest(existing.agentDefinitionId(), existing.kind(), existing.documentId(), null));
    if (current.currentVersion() != existing.baseVersion()) throw new AuthorizationException(409, "edit-session-base-version-stale");
    var nextVersion = current.currentVersion() + 1;
    saveCurrentSnapshot(current, existing.proposedContent(), nextVersion, firstNonBlank(existing.changeSummary(), "Saved from Agent Admin edit session " + existing.sessionId()));
    var saved = transitionSession(existing, EditSessionStatus.SAVED, correlationId);
    sessions.put(saved.sessionId(), saved);
    return new SaveEditSessionResult(saved, nextVersion, List.of(traceId("edit-session-save", existing.sessionId(), correlationId)));
  }

  public RestoreVersionResult restoreVersion(AuthContextResolver.ResolvedMe actor, RestoreVersionRequest request, String correlationId) {
    requireSaasAdmin(actor, RESTORE_VERSION_CAPABILITY, "agent_admin.doc_version.restore.v1", correlationId);
    var current = resolveDocument(new DocumentVersionRequest(request.agentDefinitionId(), request.kind(), request.documentId(), null));
    if (request.version() != current.currentVersion()) throw new AuthorizationException(404, "document-version-not-found-in-current-slice");
    var nextVersion = current.currentVersion() + 1;
    saveCurrentSnapshot(current, current.contentBody(), nextVersion, "Restored from version " + request.version());
    return new RestoreVersionResult(current.agentDefinitionId(), current.kind(), current.documentId(), request.version(), nextVersion, "restored-current-slice-version", List.of(traceId("version-restore", current.documentId(), correlationId)));
  }

  private void requireDocumentCapability(AuthContextResolver.ResolvedMe actor, AgentDocKind kind, String surfaceContract, String correlationId) {
    var capability = switch (kind == null ? AgentDocKind.PROMPT : kind) {
      case PROMPT -> READ_PROMPT_CAPABILITY;
      case SKILL -> READ_SKILL_CAPABILITY;
      case REFERENCE -> READ_REFERENCE_CAPABILITY;
    };
    requireSaasAdmin(actor, capability, surfaceContract, correlationId);
  }

  private void requireSaasAdmin(AuthContextResolver.ResolvedMe actor, String capabilityId, String surfaceContract, String correlationId) {
    var context = actor.selectedContext();
    if (context.scopeType() != ScopeType.SAAS_OWNER || !context.roles().contains(FoundationRole.SAAS_OWNER_ADMIN)) {
      authContextResolver.appendDeniedTrace(actor, capabilityId, "agent-admin-requires-saas-owner-admin", correlationId);
      throw new AuthorizationException(403, "agent-admin-requires-saas-owner-admin");
    }
    authContextResolver.requireCapability(context, capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, surfaceContract, correlationId);
  }

  private AgentListRow agentListRow(AgentDefinition agent) {
    return new AgentListRow(agent.agentDefinitionId(), agent.displayName(), agent.description(), agent.functionalAreaId(), agent.updatedAt());
  }

  private AgentDefinition agent(String agentDefinitionId) {
    requireNonBlank(agentDefinitionId, "agentDefinitionId-required");
    return repository.agentDefinition(platformScopeId(), agentDefinitionId)
        .orElseThrow(() -> new AuthorizationException(404, "agent-not-found-or-forbidden"));
  }

  private PromptDocument prompt(AgentDefinition agent) {
    return repository.promptDocument(platformScopeId(), agent.promptDocumentId())
        .orElseThrow(() -> new AuthorizationException(404, "prompt-not-found-or-forbidden"));
  }

  private List<SkillDocSummary> skillSummaries(AgentDefinition agent) {
    var manifest = repository.skillManifest(platformScopeId(), agent.skillManifestId())
        .orElse(new AgentSkillManifest(platformScopeId(), agent.skillManifestId(), agent.agentDefinitionId(), agent.status(), 0, List.of(), "", agent.seedProvenance(), agent.createdAt(), agent.updatedAt()));
    return manifest.entries().stream().map(entry -> {
      var skill = repository.skillDocument(platformScopeId(), entry.skillDocumentId()).orElse(null);
      return new SkillDocSummary(
          entry.stableSkillId(),
          entry.skillDocumentId(),
          skill == null ? entry.title() : skill.title(),
          skill == null ? entry.purpose() : skill.purpose(),
          skill == null ? entry.pinnedVersion() : skill.activeVersion(),
          skill == null ? null : skill.updatedAt(),
          referenceSummaries(agent));
    }).toList();
  }

  private List<ReferenceDocSummary> referenceSummaries(AgentDefinition agent) {
    var manifest = repository.referenceManifest(platformScopeId(), agent.referenceManifestId())
        .orElse(new AgentReferenceManifest(platformScopeId(), agent.referenceManifestId(), agent.agentDefinitionId(), null, agent.status(), 0, List.of(), "", agent.seedProvenance(), agent.createdAt(), agent.updatedAt()));
    return manifest.entries().stream().map(entry -> {
      var reference = repository.referenceDocument(platformScopeId(), entry.referenceDocumentId()).orElse(null);
      return new ReferenceDocSummary(
          entry.stableReferenceId(),
          entry.referenceDocumentId(),
          reference == null ? entry.title() : reference.title(),
          reference == null ? entry.summary() : reference.summary(),
          reference == null ? entry.pinnedVersion() : reference.activeVersion(),
          reference == null ? null : reference.updatedAt());
    }).toList();
  }

  private DocumentSnapshot resolveDocument(DocumentVersionRequest request) {
    if (request == null) throw new AuthorizationException(400, "document-request-required");
    var agent = agent(request.agentDefinitionId());
    var kind = request.kind() == null ? AgentDocKind.PROMPT : request.kind();
    return switch (kind) {
      case PROMPT -> {
        var prompt = prompt(agent);
        if (request.documentId() != null && !request.documentId().isBlank() && !request.documentId().equals(prompt.promptDocumentId())) {
          throw new AuthorizationException(404, "document-not-found-or-forbidden");
        }
        yield new DocumentSnapshot(agent.agentDefinitionId(), kind, prompt.promptDocumentId(), prompt.title(), prompt.changeSummary(), prompt.activeVersion(), prompt.contentBody(), prompt.contentChecksum(), prompt.updatedAt());
      }
      case SKILL -> {
        var skill = skill(agent, request.documentId());
        yield new DocumentSnapshot(agent.agentDefinitionId(), kind, skill.skillDocumentId(), skill.title(), skill.purpose(), skill.activeVersion(), skill.contentBody(), skill.contentChecksum(), skill.updatedAt());
      }
      case REFERENCE -> {
        var reference = reference(agent, request.documentId());
        yield new DocumentSnapshot(agent.agentDefinitionId(), kind, reference.referenceDocumentId(), reference.title(), reference.summary(), reference.activeVersion(), reference.contentBody(), reference.contentChecksum(), reference.updatedAt());
      }
    };
  }

  private SkillDocument skill(AgentDefinition agent, String documentId) {
    var manifest = repository.skillManifest(platformScopeId(), agent.skillManifestId())
        .orElseThrow(() -> new AuthorizationException(404, "skill-manifest-not-found-or-forbidden"));
    var entry = manifest.entries().stream()
        .filter(candidate -> documentId == null || documentId.isBlank() || candidate.skillDocumentId().equals(documentId) || candidate.stableSkillId().equals(documentId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "skill-not-found-or-forbidden"));
    return repository.skillDocument(platformScopeId(), entry.skillDocumentId())
        .orElseThrow(() -> new AuthorizationException(404, "skill-not-found-or-forbidden"));
  }

  private ReferenceDocument reference(AgentDefinition agent, String documentId) {
    var manifest = repository.referenceManifest(platformScopeId(), agent.referenceManifestId())
        .orElseThrow(() -> new AuthorizationException(404, "reference-manifest-not-found-or-forbidden"));
    var entry = manifest.entries().stream()
        .filter(candidate -> documentId == null || documentId.isBlank() || candidate.referenceDocumentId().equals(documentId) || candidate.stableReferenceId().equals(documentId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "reference-not-found-or-forbidden"));
    return repository.referenceDocument(platformScopeId(), entry.referenceDocumentId())
        .orElseThrow(() -> new AuthorizationException(404, "reference-not-found-or-forbidden"));
  }

  private void saveCurrentSnapshot(DocumentSnapshot current, String contentBody, int nextVersion, String changeSummary) {
    var now = Instant.now(clock);
    var checksum = checksum(contentBody);
    switch (current.kind()) {
      case PROMPT -> {
        var existing = repository.promptDocument(platformScopeId(), current.documentId()).orElseThrow();
        repository.savePromptDocument(new PromptDocument(existing.tenantId(), existing.promptDocumentId(), existing.agentDefinitionId(), existing.title(), existing.promptType(), existing.status(), nextVersion, contentBody, checksum, changeSummary, existing.seedProvenance(), existing.createdAt(), now));
      }
      case SKILL -> {
        var existing = repository.skillDocument(platformScopeId(), current.documentId()).orElseThrow();
        repository.saveSkillDocument(new SkillDocument(existing.tenantId(), existing.skillDocumentId(), existing.stableSkillId(), existing.title(), existing.purpose(), existing.whenToUse(), existing.tags(), existing.status(), nextVersion, contentBody, checksum, existing.seedProvenance(), existing.createdAt(), now));
      }
      case REFERENCE -> {
        var existing = repository.referenceDocument(platformScopeId(), current.documentId()).orElseThrow();
        repository.saveReferenceDocument(new ReferenceDocument(existing.tenantId(), existing.referenceDocumentId(), existing.stableReferenceId(), existing.title(), existing.summary(), existing.whenToConsult(), existing.referenceType(), existing.accessLevel(), existing.tags(), existing.status(), nextVersion, contentBody, checksum, existing.seedProvenance(), existing.createdAt(), now));
      }
    }
  }

  private EditSessionRecord session(String sessionId) {
    requireNonBlank(sessionId, "editSessionId-required");
    var session = sessions.get(sessionId);
    if (session == null) throw new AuthorizationException(404, "edit-session-not-found-or-forbidden");
    return session;
  }

  private void ensureSessionActor(AuthContextResolver.ResolvedMe actor, EditSessionRecord session) {
    if (!actor.account().accountId().equals(session.actorAccountId())) throw new AuthorizationException(403, "edit-session-actor-mismatch");
  }

  private EditSessionRecord transitionSession(EditSessionRecord existing, EditSessionStatus status, String correlationId) {
    return new EditSessionRecord(
        existing.sessionId(),
        existing.agentDefinitionId(),
        existing.kind(),
        existing.documentId(),
        existing.baseVersion(),
        existing.actorAccountId(),
        status,
        existing.instructions(),
        existing.proposedContent(),
        existing.changeSummary(),
        existing.warnings(),
        appendTrace(existing.traceLinks(), traceId("edit-session-" + status.name().toLowerCase(Locale.ROOT), existing.sessionId(), correlationId)),
        existing.startedAt(),
        Instant.now(clock));
  }

  private DocumentSummary docSummary(AgentDocKind kind, String documentId, String title, String description, int currentVersion, Instant updatedAt) {
    return new DocumentSummary(kind, documentId, title, description, currentVersion, updatedAt);
  }

  private Instant lastEditTime(AgentDefinition agent, PromptDocument prompt, List<Instant> skillTimes, List<Instant> referenceTimes) {
    var times = new ArrayList<Instant>();
    if (agent.updatedAt() != null) times.add(agent.updatedAt());
    if (prompt.updatedAt() != null) times.add(prompt.updatedAt());
    skillTimes.stream().filter(Objects::nonNull).forEach(times::add);
    referenceTimes.stream().filter(Objects::nonNull).forEach(times::add);
    return times.stream().max(Comparator.naturalOrder()).orElse(null);
  }

  private static boolean contains(String haystack, String needle) {
    if (needle == null || needle.isBlank()) return true;
    return String.valueOf(haystack).toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
  }

  private static String firstNonBlank(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private static String safe(String value) {
    return value == null ? null : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  private static void requireNonBlank(String value, String reason) {
    if (value == null || value.isBlank()) throw new AuthorizationException(400, reason);
  }

  private static List<String> appendTrace(List<String> existing, String traceId) {
    var traces = new ArrayList<>(existing == null ? List.of() : existing);
    traces.add(traceId);
    return List.copyOf(traces);
  }

  private static String platformScopeId() {
    return WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID;
  }

  private static String traceId(String kind, String id, String correlationId) {
    return "trace-agent-admin-doc-" + kind + "-" + Math.abs((String.valueOf(id) + ":" + String.valueOf(correlationId)).hashCode());
  }

  private static String checksum(String content) {
    try {
      var digest = MessageDigest.getInstance("SHA-256").digest(String.valueOf(content).getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException failure) {
      throw new IllegalStateException(failure);
    }
  }

  private record DocumentSnapshot(String agentDefinitionId, AgentDocKind kind, String documentId, String title, String description, int currentVersion, String contentBody, String checksum, Instant updatedAt) {
    DocumentVersionDetail toVersionDetail(String correlationId) {
      return new DocumentVersionDetail(agentDefinitionId, kind, documentId, currentVersion, true, true, title, description, contentBody, checksum, updatedAt, List.of(traceId("version-read", documentId, correlationId)));
    }
  }

  private static final class AgentRuntimeCapabilityIds {
    private static final String DRAFT_BEHAVIOR_CHANGE = "agent_admin.draft_behavior_change";
    private static final String CANCEL_BEHAVIOR_CHANGE = "agent_admin.cancel_behavior_change";
    private static final String ACTIVATE_BEHAVIOR_CHANGE = "agent_admin.activate_behavior_change";
    private static final String ROLLBACK_BEHAVIOR_CHANGE = "agent_admin.rollback_behavior_change";
  }

  public enum AgentDocKind { PROMPT, SKILL, REFERENCE }

  public enum EditSessionStatus { DRAFTING, PROPOSAL_READY, SAVED, CANCELLED }

  public enum DiffStatus { READY, NO_PRIOR_VERSION, PRIOR_VERSION_NOT_AVAILABLE_IN_CURRENT_SLICE }

  public record AgentListRequest(String nameContains, String workstreamOrDomain) {}

  public record AgentListRow(String agentDefinitionId, String agentName, String shortPurpose, String workstreamDomain, Instant lastEditTime) {}

  public record AgentListResponse(List<AgentListRow> rows, int totalCount, int filteredCount, List<String> traceLinks) {
    public AgentListResponse {
      rows = List.copyOf(rows == null ? List.of() : rows);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record AgentProfileUpdateRequest(String agentDefinitionId, String agentName, String purpose) {}

  public record AgentProfileUpdateResult(AgentListRow row, List<String> traceLinks) {
    public AgentProfileUpdateResult {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record AgentDocDetail(String agentDefinitionId, String agentName, String purpose, String workstreamDomain, Instant lastEditTime, DocumentSummary prompt, List<SkillDocSummary> skills, List<ReferenceDocSummary> referenceDocs, List<String> traceLinks) {
    public AgentDocDetail {
      skills = List.copyOf(skills == null ? List.of() : skills);
      referenceDocs = List.copyOf(referenceDocs == null ? List.of() : referenceDocs);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record DocumentSummary(AgentDocKind kind, String documentId, String title, String description, int currentVersion, Instant updatedAt) {}

  public record SkillDocSummary(String stableSkillId, String documentId, String name, String purpose, int currentVersion, Instant updatedAt, List<ReferenceDocSummary> referenceDocs) {
    public SkillDocSummary {
      referenceDocs = List.copyOf(referenceDocs == null ? List.of() : referenceDocs);
    }
  }

  public record ReferenceDocSummary(String stableReferenceId, String documentId, String name, String description, int currentVersion, Instant updatedAt) {}

  public record DocumentVersionRequest(String agentDefinitionId, AgentDocKind kind, String documentId, Integer version) {}

  public record DocumentVersionDetail(String agentDefinitionId, AgentDocKind kind, String documentId, int version, boolean currentVersion, boolean editable, String title, String description, String contentBody, String contentChecksum, Instant createdAt, List<String> traceLinks) {
    public DocumentVersionDetail {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record VersionHistoryRow(int version, boolean currentVersion, Instant createdAt, String label) {}

  public record VersionHistoryResponse(String agentDefinitionId, AgentDocKind kind, String documentId, List<VersionHistoryRow> rows, List<String> traceLinks) {
    public VersionHistoryResponse {
      rows = List.copyOf(rows == null ? List.of() : rows);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record AdjacentDiffResponse(String agentDefinitionId, AgentDocKind kind, String documentId, Integer priorVersion, int selectedVersion, DiffStatus status, String unifiedDiff, List<String> traceLinks) {
    public AdjacentDiffResponse {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record StartEditSessionRequest(String agentDefinitionId, AgentDocKind kind, String documentId, String initialInstructions) {}

  public record ReviseEditSessionRequest(String sessionId, String instructions, String proposedContent, String changeSummary, List<String> warnings) {
    public ReviseEditSessionRequest {
      warnings = List.copyOf(warnings == null ? List.of() : warnings);
    }
  }

  public record EditSessionCommandRequest(String sessionId) {}

  public record EditInstruction(Instant at, String actorAccountId, String instructions) {}

  public record EditSessionRecord(String sessionId, String agentDefinitionId, AgentDocKind kind, String documentId, int baseVersion, String actorAccountId, EditSessionStatus status, List<EditInstruction> instructions, String proposedContent, String changeSummary, List<String> warnings, List<String> traceLinks, Instant startedAt, Instant endedAt) {
    public EditSessionRecord {
      instructions = List.copyOf(instructions == null ? List.of() : instructions);
      warnings = List.copyOf(warnings == null ? List.of() : warnings);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record SaveEditSessionResult(EditSessionRecord session, int savedVersion, List<String> traceLinks) {
    public SaveEditSessionResult {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record RestoreVersionRequest(String agentDefinitionId, AgentDocKind kind, String documentId, int version) {}

  public record RestoreVersionResult(String agentDefinitionId, AgentDocKind kind, String documentId, int restoredFromVersion, int newCurrentVersion, String summary, List<String> traceLinks) {
    public RestoreVersionResult {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }
}
