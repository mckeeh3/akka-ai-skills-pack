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
  public static final String ACCESS_REVIEW_SKILL_DOC_ID = "skill-ua-access-review-triage";
  public static final String ADMIN_RISK_SKILL_DOC_ID = "skill-ua-admin-risk-scoring";
  public static final String INVITATION_DRAFTING_SKILL_DOC_ID = "skill-ua-invitation-drafting";
  public static final String ROLE_RECOMMENDATION_SKILL_DOC_ID = "skill-ua-role-recommendation";
  public static final String SUPPORT_ACCESS_SKILL_DOC_ID = "skill-ua-support-access-review";
  public static final String AUDIT_SUMMARY_SKILL_DOC_ID = "skill-ua-audit-summary";
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
    validateContent(promptContent, "prompt-user-admin-system");
    var skillSeeds = userAdminSkillSeeds();
    for (var seed : skillSeeds) {
      validateContent(readSeedResource(seed.resourcePath()), seed.documentId());
    }

    var promptChecksum = checksum(promptContent);
    var prompt = repository.promptDocument(tenantId, USER_ADMIN_PROMPT_ID)
        .map(existing -> skipOrDraftPrompt(existing, promptChecksum, result))
        .orElseGet(() -> createPrompt(tenantId, promptContent, promptChecksum, importerActor, correlationId, now, result));
    var skills = new java.util.ArrayList<SkillDocument>();
    for (var seed : skillSeeds) {
      var skillContent = readSeedResource(seed.resourcePath());
      var skillChecksum = checksum(skillContent);
      skills.add(repository.skillDocument(tenantId, seed.documentId())
          .map(existing -> skipOrDraftSkill(existing, skillChecksum, result))
          .orElseGet(() -> createSkill(tenantId, seed, skillContent, skillChecksum, importerActor, correlationId, now, result)));
    }
    var manifest = repository.skillManifest(tenantId, USER_ADMIN_MANIFEST_ID)
        .map(existing -> skipExisting("AgentSkillManifest", existing.manifestId(), result, existing))
        .orElseGet(() -> createManifest(tenantId, skills, importerActor, correlationId, now, result));
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

  private SkillDocument createSkill(String tenantId, SkillSeed seed, String content, String checksum, String actor, String correlationId, Instant now, SeedImportResult result) {
    var skill = new SkillDocument(tenantId, seed.documentId(), seed.stableSkillId(), seed.title(), seed.purpose(), seed.whenToUse(), List.of("foundation", "user-admin"), AgentLifecycleStatus.ACTIVE, 1, content, checksum, provenance(seed.resourceId(), checksum, actor, correlationId, now, false), now, now);
    repository.saveSkillDocument(skill);
    result.created("SkillDocument", seed.documentId());
    return skill;
  }

  private AgentSkillManifest createManifest(String tenantId, List<SkillDocument> skills, String actor, String correlationId, Instant now, SeedImportResult result) {
    var entries = skills.stream()
        .map(skill -> new AgentSkillManifest.Entry(skill.stableSkillId(), skill.skillDocumentId(), skill.activeVersion(), skill.title(), skill.purpose(), skill.whenToUse()))
        .toList();
    var checksum = checksum(entries.toString());
    var manifest = new AgentSkillManifest(tenantId, USER_ADMIN_MANIFEST_ID, USER_ADMIN_AGENT_ID, AgentLifecycleStatus.ACTIVE, 1, entries, checksum, provenance("manifests/user-admin-agent-skill-manifest", checksum, actor, correlationId, now, false), now, now);
    repository.saveSkillManifest(manifest);
    result.created("AgentSkillManifest", USER_ADMIN_MANIFEST_ID);
    return manifest;
  }

  private ToolPermissionBoundary createBoundary(String tenantId, String actor, String correlationId, Instant now, SeedImportResult result) {
    var grants = List.of(
        new ToolPermissionBoundary.ToolGrant("readSkill", ToolPermissionBoundary.Category.READ_SKILL, "agent.skills.read", List.of("READ"), List.of("runtime", "test", "replay"), "none", "bounded_autonomous", false, "full_work_trace"),
        new ToolPermissionBoundary.ToolGrant("readReferenceDoc", ToolPermissionBoundary.Category.READ_REFERENCE, "agent.references.read", List.of("READ"), List.of("runtime", "test", "replay"), "none", "bounded_autonomous", false, "full_work_trace"),
        new ToolPermissionBoundary.ToolGrant("userAdminEvidence.read", ToolPermissionBoundary.Category.DATA_LOOKUP, "tenant.user.read", List.of("READ"), List.of("runtime", "test"), "none", "bounded_autonomous", false, "access"));
    var checksum = checksum(grants.toString());
    var boundary = new ToolPermissionBoundary(tenantId, USER_ADMIN_BOUNDARY_ID, USER_ADMIN_AGENT_ID, AgentLifecycleStatus.ACTIVE, 1, grants, checksum, provenance("tool-boundaries/user-admin-agent-tools", checksum, actor, correlationId, now, false), now, now);
    repository.saveToolBoundary(boundary);
    result.created("ToolPermissionBoundary", USER_ADMIN_BOUNDARY_ID);
    return boundary;
  }

  private AgentDefinition createAgentDefinition(String tenantId, PromptDocument prompt, AgentSkillManifest manifest, ToolPermissionBoundary boundary, String actor, String correlationId, Instant now, SeedImportResult result) {
    var checksum = checksum(prompt.contentChecksum() + manifest.compactManifestChecksum() + boundary.checksum());
    var definition = new AgentDefinition(tenantId, USER_ADMIN_AGENT_ID, "User Admin Agent", "Role-authorized functional agent for user, invitation, membership, and access-review administration.", AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA, "user-admin", AgentDefinition.AuthorityLevel.APPROVAL_REQUIRED, AgentLifecycleStatus.ACTIVE, prompt.promptDocumentId(), prompt.activeVersion(), manifest.manifestId(), manifest.manifestVersion(), boundary.boundaryId(), boundary.boundaryVersion(), "starter-default-model", "starter-default-model-policy", "{{JAVA_BASE_PACKAGE}}.application.agent.UserAdminAgent", List.of("PromptAssemblyTrace", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"), provenance("agents/user-admin-agent", checksum, actor, correlationId, now, false), now, now);
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

  private List<SkillSeed> userAdminSkillSeeds() {
    return List.of(
        new SkillSeed(ACCESS_REVIEW_SKILL_DOC_ID, "ua.access-review-triage.v1", "Access Review Triage", "Evaluate stale or risky memberships, roles, support access, and remediation paths.", "Use for stale memberships, risky roles, pending reviews, and proposed access remediation.", "/agent-behavior-seeds/starter-v1/access-review-triage.md", "skills/user-admin/access-review-triage.md"),
        new SkillSeed(ADMIN_RISK_SKILL_DOC_ID, "ua.admin-risk-scoring.v1", "Admin Action Risk Scoring", "Classify last-admin, role-escalation, support-access, identity, bulk, and low-confidence risks.", "Use before consequential or ambiguous User Admin actions that may require decision-card routing.", "/agent-behavior-seeds/starter-v1/admin-risk-scoring.md", "skills/user-admin/admin-risk-scoring.md"),
        new SkillSeed(INVITATION_DRAFTING_SKILL_DOC_ID, "ua.invitation-drafting.v1", "Invitation Drafting", "Draft safe invitation, resend, revoke, and onboarding explanations without exposing tokens.", "Use when preparing invitation rationale or human-confirmed invitation actions.", "/agent-behavior-seeds/starter-v1/invitation-drafting.md", "skills/user-admin/invitation-drafting.md"),
        new SkillSeed(ROLE_RECOMMENDATION_SKILL_DOC_ID, "ua.role-recommendation.v1", "Role Recommendation", "Recommend least-privilege roles and alternatives from context, policy, and audit evidence.", "Use when asked which role or capability set a user should have.", "/agent-behavior-seeds/starter-v1/role-recommendation.md", "skills/user-admin/role-recommendation.md"),
        new SkillSeed(SUPPORT_ACCESS_SKILL_DOC_ID, "ua.support-access-review.v1", "Support Access Review", "Explain support-access grants, expiry, SaaS Owner limits, and revocation risks.", "Use for support-access visibility, grant review, revocation, or expiry questions.", "/agent-behavior-seeds/starter-v1/support-access-review.md", "skills/user-admin/support-access-review.md"),
        new SkillSeed(AUDIT_SUMMARY_SKILL_DOC_ID, "ua.audit-summary.v1", "Admin Audit Summary", "Summarize scoped AdminAuditEvent and trace evidence with redaction.", "Use when explaining what changed, who acted, why access changed, or which denials occurred.", "/agent-behavior-seeds/starter-v1/audit-summary.md", "skills/user-admin/audit-summary.md"));
  }

  private record SkillSeed(String documentId, String stableSkillId, String title, String purpose, String whenToUse, String resourcePath, String resourceId) {}

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
