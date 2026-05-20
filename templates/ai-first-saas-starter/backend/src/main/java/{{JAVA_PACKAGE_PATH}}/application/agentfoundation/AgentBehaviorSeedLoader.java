package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SeedProvenance;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

/** Imports implementation-packaged default agent behavior records into tenant-scoped governed state. */
public final class AgentBehaviorSeedLoader {
  public static final String SEED_BUNDLE_ID = "starter-agent-behavior-v1";
  public static final String CONTENT_VERSION = "1";
  public static final String USER_ADMIN_AGENT_ID = "agent-user-admin";
  public static final String USER_ADMIN_PROMPT_ID = "prompt-user-admin-system";
  public static final String ACCESS_REVIEW_SKILL_DOC_ID = "skill-access-review";
  public static final String USER_ADMIN_MANIFEST_ID = "manifest-user-admin";
  public static final String USER_ADMIN_BOUNDARY_ID = "tool-boundary-user-admin";

  private final AgentBehaviorRepository repository;
  private final Clock clock;

  public AgentBehaviorSeedLoader(AgentBehaviorRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  public SeedImportResult importStarterDefaults(String tenantId, String importerActor, String correlationId) {
    var result = new SeedImportResult(tenantId, SEED_BUNDLE_ID, CONTENT_VERSION, correlationId);
    var now = Instant.now(clock);
    var promptContent = readSeedResource("/agent-behavior-seeds/starter-v1/user-admin-system.md");
    var skillContent = readSeedResource("/agent-behavior-seeds/starter-v1/access-review.md");
    validateContent(promptContent, "prompt-user-admin-system");
    validateContent(skillContent, "skill-access-review");

    var promptChecksum = checksum(promptContent);
    var skillChecksum = checksum(skillContent);
    var prompt = repository.promptDocument(tenantId, USER_ADMIN_PROMPT_ID)
        .map(existing -> skipOrDraftPrompt(existing, promptChecksum, result))
        .orElseGet(() -> createPrompt(tenantId, promptContent, promptChecksum, importerActor, correlationId, now, result));
    var skill = repository.skillDocument(tenantId, ACCESS_REVIEW_SKILL_DOC_ID)
        .map(existing -> skipOrDraftSkill(existing, skillChecksum, result))
        .orElseGet(() -> createSkill(tenantId, skillContent, skillChecksum, importerActor, correlationId, now, result));
    var manifest = repository.skillManifest(tenantId, USER_ADMIN_MANIFEST_ID)
        .map(existing -> skipExisting("AgentSkillManifest", existing.manifestId(), result, existing))
        .orElseGet(() -> createManifest(tenantId, skill, importerActor, correlationId, now, result));
    var boundary = repository.toolBoundary(tenantId, USER_ADMIN_BOUNDARY_ID)
        .map(existing -> skipExisting("ToolPermissionBoundary", existing.boundaryId(), result, existing))
        .orElseGet(() -> createBoundary(tenantId, importerActor, correlationId, now, result));
    repository.agentDefinition(tenantId, USER_ADMIN_AGENT_ID)
        .map(existing -> skipExisting("AgentDefinition", existing.agentDefinitionId(), result, existing))
        .orElseGet(() -> createAgentDefinition(tenantId, prompt, manifest, boundary, importerActor, correlationId, now, result));
    return result;
  }

  private PromptDocument createPrompt(String tenantId, String content, String checksum, String actor, String correlationId, Instant now, SeedImportResult result) {
    var prompt = new PromptDocument(tenantId, USER_ADMIN_PROMPT_ID, USER_ADMIN_AGENT_ID, "User Admin system prompt", "system", AgentLifecycleStatus.ACTIVE, 1, content, checksum, "Initial implementation-packaged prompt seed.", provenance("prompts/user-admin-system.md", checksum, actor, correlationId, now, false), now, now);
    repository.savePromptDocument(prompt);
    result.created("PromptDocument", USER_ADMIN_PROMPT_ID);
    return prompt;
  }

  private SkillDocument createSkill(String tenantId, String content, String checksum, String actor, String correlationId, Instant now, SeedImportResult result) {
    var skill = new SkillDocument(tenantId, ACCESS_REVIEW_SKILL_DOC_ID, "access-review", "Access review", "Evaluate tenant-scoped user and membership changes before recommending action.", "Use before changing roles, support access, invitations, or membership status.", List.of("foundation", "user-admin"), AgentLifecycleStatus.ACTIVE, 1, content, checksum, provenance("skills/access-review.md", checksum, actor, correlationId, now, false), now, now);
    repository.saveSkillDocument(skill);
    result.created("SkillDocument", ACCESS_REVIEW_SKILL_DOC_ID);
    return skill;
  }

  private AgentSkillManifest createManifest(String tenantId, SkillDocument skill, String actor, String correlationId, Instant now, SeedImportResult result) {
    var entries = List.of(new AgentSkillManifest.Entry(skill.stableSkillId(), skill.skillDocumentId(), skill.activeVersion(), skill.title(), skill.purpose(), skill.whenToUse()));
    var checksum = checksum(entries.toString());
    var manifest = new AgentSkillManifest(tenantId, USER_ADMIN_MANIFEST_ID, USER_ADMIN_AGENT_ID, AgentLifecycleStatus.ACTIVE, 1, entries, checksum, provenance("manifests/user-admin-agent-manifest", checksum, actor, correlationId, now, false), now, now);
    repository.saveSkillManifest(manifest);
    result.created("AgentSkillManifest", USER_ADMIN_MANIFEST_ID);
    return manifest;
  }

  private ToolPermissionBoundary createBoundary(String tenantId, String actor, String correlationId, Instant now, SeedImportResult result) {
    var grants = List.of(
        new ToolPermissionBoundary.ToolGrant("readSkill", ToolPermissionBoundary.Category.READ_SKILL, "agent.skills.read", List.of("READ"), List.of("runtime", "test", "replay"), "none", "bounded_autonomous", false, "full_work_trace"),
        new ToolPermissionBoundary.ToolGrant("userAdminEvidence.read", ToolPermissionBoundary.Category.DATA_LOOKUP, "tenant.user.read", List.of("READ"), List.of("runtime", "test"), "none", "bounded_autonomous", false, "access"));
    var checksum = checksum(grants.toString());
    var boundary = new ToolPermissionBoundary(tenantId, USER_ADMIN_BOUNDARY_ID, USER_ADMIN_AGENT_ID, AgentLifecycleStatus.ACTIVE, 1, grants, checksum, provenance("tool-boundaries/user-admin-agent-tools", checksum, actor, correlationId, now, false), now, now);
    repository.saveToolBoundary(boundary);
    result.created("ToolPermissionBoundary", USER_ADMIN_BOUNDARY_ID);
    return boundary;
  }

  private AgentDefinition createAgentDefinition(String tenantId, PromptDocument prompt, AgentSkillManifest manifest, ToolPermissionBoundary boundary, String actor, String correlationId, Instant now, SeedImportResult result) {
    var checksum = checksum(prompt.contentChecksum() + manifest.compactManifestChecksum() + boundary.checksum());
    var definition = new AgentDefinition(tenantId, USER_ADMIN_AGENT_ID, "User Admin Agent", "Role-authorized functional agent for user, invitation, membership, and access-review administration.", AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA, "user-admin", AgentDefinition.AuthorityLevel.APPROVAL_REQUIRED, AgentLifecycleStatus.ACTIVE, prompt.promptDocumentId(), prompt.activeVersion(), manifest.manifestId(), manifest.manifestVersion(), boundary.boundaryId(), boundary.boundaryVersion(), "starter-default-model", "starter-default-model-policy", "{{JAVA_BASE_PACKAGE}}.application.agent.UserAdminAgent", List.of("PromptAssemblyTrace", "SkillLoadTrace", "AgentWorkTrace"), provenance("agents/user-admin-agent", checksum, actor, correlationId, now, false), now, now);
    repository.saveAgentDefinition(definition);
    result.created("AgentDefinition", USER_ADMIN_AGENT_ID);
    return definition;
  }

  private PromptDocument skipOrDraftPrompt(PromptDocument existing, String packagedChecksum, SeedImportResult result) {
    if (existing.seedProvenance() != null && packagedChecksum.equals(existing.seedProvenance().checksum()) && !existing.seedProvenance().tenantCustomized()) {
      result.skippedUnchanged("PromptDocument", existing.promptDocumentId());
    } else {
      result.proposedDraft("PromptDocument", existing.promptDocumentId(), "existing tenant prompt differs from packaged seed; active prompt preserved");
    }
    return existing;
  }

  private SkillDocument skipOrDraftSkill(SkillDocument existing, String packagedChecksum, SeedImportResult result) {
    if (existing.seedProvenance() != null && packagedChecksum.equals(existing.seedProvenance().checksum()) && !existing.seedProvenance().tenantCustomized()) {
      result.skippedUnchanged("SkillDocument", existing.skillDocumentId());
    } else {
      result.proposedDraft("SkillDocument", existing.skillDocumentId(), "existing tenant skill differs from packaged seed; active skill preserved");
    }
    return existing;
  }

  private <T> T skipExisting(String type, String id, SeedImportResult result, T existing) {
    result.skippedUnchanged(type, id);
    return existing;
  }

  private SeedProvenance provenance(String resourceId, String checksum, String actor, String correlationId, Instant now, boolean tenantCustomized) {
    return new SeedProvenance(SEED_BUNDLE_ID, CONTENT_VERSION, resourceId, checksum, now, actor, correlationId, tenantCustomized);
  }

  private void validateContent(String content, String artifactId) {
    if (content == null || content.isBlank()) throw new IllegalStateException("missing seed content: " + artifactId);
    var lower = content.toLowerCase();
    if (lower.contains("api_key") || lower.contains("workos_api") || lower.contains("resend_api") || lower.contains("jwt_secret")) {
      throw new IllegalStateException("secret-like seed content blocked: " + artifactId);
    }
  }

  private String readSeedResource(String path) {
    try (var input = AgentBehaviorSeedLoader.class.getResourceAsStream(path)) {
      if (input == null) throw new IllegalStateException("missing seed resource: " + path);
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String checksum(String content) {
    try {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
