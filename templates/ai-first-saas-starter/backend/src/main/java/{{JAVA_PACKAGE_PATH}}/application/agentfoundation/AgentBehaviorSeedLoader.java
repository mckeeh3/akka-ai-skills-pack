package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentReferenceManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ReferenceDocument;
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
  public static final String TENANT_ROLE_CATALOG_REFERENCE_DOC_ID = "ref-ua-tenant-role-catalog";
  public static final String INVITATION_ONBOARDING_REFERENCE_DOC_ID = "ref-ua-invitation-onboarding-policy";
  public static final String ACCESS_REVIEW_POLICY_REFERENCE_DOC_ID = "ref-ua-access-review-policy";
  public static final String SUPPORT_ACCESS_PROCEDURE_REFERENCE_DOC_ID = "ref-ua-support-access-procedure";
  public static final String LAST_ADMIN_PROTECTION_REFERENCE_DOC_ID = "ref-ua-last-admin-protection";
  public static final String ADMIN_AUDIT_REDACTION_REFERENCE_DOC_ID = "ref-ua-admin-audit-redaction-guide";
  public static final String USER_ADMIN_MANIFEST_ID = "manifest-user-admin";
  public static final String USER_ADMIN_REFERENCE_MANIFEST_ID = "reference-manifest-user-admin";
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
    var referenceSeeds = userAdminReferenceSeeds();
    for (var seed : referenceSeeds) {
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
    var references = new java.util.ArrayList<ReferenceDocument>();
    for (var seed : referenceSeeds) {
      var referenceContent = readSeedResource(seed.resourcePath());
      var referenceChecksum = checksum(referenceContent);
      references.add(repository.referenceDocument(tenantId, seed.documentId())
          .map(existing -> skipOrDraftReference(existing, referenceChecksum, result))
          .orElseGet(() -> createReference(tenantId, seed, referenceContent, referenceChecksum, importerActor, correlationId, now, result)));
    }
    var manifest = repository.skillManifest(tenantId, USER_ADMIN_MANIFEST_ID)
        .map(existing -> skipExisting("AgentSkillManifest", existing.manifestId(), result, existing))
        .orElseGet(() -> createManifest(tenantId, skills, importerActor, correlationId, now, result));
    var referenceManifest = repository.referenceManifest(tenantId, USER_ADMIN_REFERENCE_MANIFEST_ID)
        .map(existing -> skipExisting("AgentReferenceManifest", existing.manifestId(), result, existing))
        .orElseGet(() -> createReferenceManifest(tenantId, references, importerActor, correlationId, now, result));
    var boundary = repository.toolBoundary(tenantId, USER_ADMIN_BOUNDARY_ID)
        .map(existing -> skipExisting("ToolPermissionBoundary", existing.boundaryId(), result, existing))
        .orElseGet(() -> createBoundary(tenantId, importerActor, correlationId, now, result));
    repository.agentDefinition(tenantId, USER_ADMIN_AGENT_ID)
        .map(existing -> skipExisting("AgentDefinition", existing.agentDefinitionId(), result, existing))
        .orElseGet(() -> createAgentDefinition(tenantId, prompt, manifest, referenceManifest, boundary, importerActor, correlationId, now, result));
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

  private ReferenceDocument createReference(String tenantId, ReferenceSeed seed, String content, String checksum, String actor, String correlationId, Instant now, SeedImportResult result) {
    var reference = new ReferenceDocument(tenantId, seed.documentId(), seed.stableReferenceId(), seed.title(), seed.summary(), seed.whenToConsult(), seed.referenceType(), seed.accessLevel(), List.of("foundation", "user-admin"), AgentLifecycleStatus.ACTIVE, 1, content, checksum, provenance(seed.resourceId(), checksum, actor, correlationId, now, false), now, now);
    repository.saveReferenceDocument(reference);
    result.created("ReferenceDocument", seed.documentId());
    return reference;
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

  private AgentReferenceManifest createReferenceManifest(String tenantId, List<ReferenceDocument> references, String actor, String correlationId, Instant now, SeedImportResult result) {
    var entries = references.stream()
        .map(reference -> new AgentReferenceManifest.Entry(reference.stableReferenceId(), reference.referenceDocumentId(), reference.activeVersion(), reference.title(), reference.summary(), reference.whenToConsult(), "consult", reference.accessLevel()))
        .toList();
    var checksum = checksum(entries.toString());
    var manifest = new AgentReferenceManifest(tenantId, USER_ADMIN_REFERENCE_MANIFEST_ID, USER_ADMIN_AGENT_ID, "user-admin-agent.expertise", AgentLifecycleStatus.ACTIVE, 1, entries, checksum, provenance("manifests/user-admin-agent-reference-manifest", checksum, actor, correlationId, now, false), now, now);
    repository.saveReferenceManifest(manifest);
    result.created("AgentReferenceManifest", USER_ADMIN_REFERENCE_MANIFEST_ID);
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

  private AgentDefinition createAgentDefinition(String tenantId, PromptDocument prompt, AgentSkillManifest manifest, AgentReferenceManifest referenceManifest, ToolPermissionBoundary boundary, String actor, String correlationId, Instant now, SeedImportResult result) {
    var checksum = checksum(prompt.contentChecksum() + manifest.compactManifestChecksum() + referenceManifest.compactManifestChecksum() + boundary.checksum());
    var definition = new AgentDefinition(tenantId, USER_ADMIN_AGENT_ID, "User Admin Agent", "Role-authorized functional agent for user, invitation, membership, and access-review administration.", AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA, "user-admin", AgentDefinition.AuthorityLevel.APPROVAL_REQUIRED, AgentLifecycleStatus.ACTIVE, prompt.promptDocumentId(), prompt.activeVersion(), manifest.manifestId(), manifest.manifestVersion(), referenceManifest.manifestId(), referenceManifest.manifestVersion(), boundary.boundaryId(), boundary.boundaryVersion(), "starter-default-model", "starter-default-model-policy", "{{JAVA_BASE_PACKAGE}}.application.agent.UserAdminAgent", List.of("PromptAssemblyTrace", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"), provenance("agents/user-admin-agent", checksum, actor, correlationId, now, false), now, now);
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

  private ReferenceDocument skipOrDraftReference(ReferenceDocument existing, String packagedChecksum, SeedImportResult result) {
    if (existing.seedProvenance() != null && packagedChecksum.equals(existing.seedProvenance().checksum()) && !existing.seedProvenance().tenantCustomized()) {
      result.skippedUnchanged("ReferenceDocument", existing.referenceDocumentId());
    } else {
      result.proposedDraft("ReferenceDocument", existing.referenceDocumentId(), "existing tenant reference differs from packaged seed; active reference preserved");
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

  private List<ReferenceSeed> userAdminReferenceSeeds() {
    return List.of(
        new ReferenceSeed(TENANT_ROLE_CATALOG_REFERENCE_DOC_ID, "ua.tenant-role-catalog.v1", "Tenant Role and Capability Catalog", "Role meanings, least-privilege alternatives, and capability ids.", "Consult when explaining roles, scope, least-privilege alternatives, or capability ids.", ReferenceDocument.ReferenceType.PRODUCT_CONFIG, "internal", "/agent-behavior-seeds/starter-v1/role-catalog-reference.md", "references/user-admin/role-catalog.md"),
        new ReferenceSeed(INVITATION_ONBOARDING_REFERENCE_DOC_ID, "ua.invitation-onboarding-policy.v1", "Invitation and Onboarding Policy", "Invitation rationale, resend/revoke behavior, expiry, and onboarding caveats.", "Consult before drafting invitation rationale, resend/revoke explanations, expiry behavior, or onboarding caveats.", ReferenceDocument.ReferenceType.POLICY, "internal", "/agent-behavior-seeds/starter-v1/invitation-onboarding-policy-reference.md", "references/user-admin/invitation-onboarding-policy.md"),
        new ReferenceSeed(ACCESS_REVIEW_POLICY_REFERENCE_DOC_ID, "ua.access-review-policy.v1", "Access Review Policy", "Stale access, review cadence, resolver expectations, and escalation triggers.", "Consult when evaluating stale access, review cadence, resolver expectations, or escalation triggers.", ReferenceDocument.ReferenceType.POLICY, "internal", "/agent-behavior-seeds/starter-v1/access-review-policy-reference.md", "references/user-admin/access-review-policy.md"),
        new ReferenceSeed(SUPPORT_ACCESS_PROCEDURE_REFERENCE_DOC_ID, "ua.support-access-procedure.v1", "Support Access Operating Procedure", "SaaS Owner support access, customer/tenant visibility, expiry, and audit obligations.", "Consult before explaining support access grants, visibility, expiry, revocation, or audit obligations.", ReferenceDocument.ReferenceType.PROCESS, "restricted", "/agent-behavior-seeds/starter-v1/support-access-procedure-reference.md", "references/user-admin/support-access-procedure.md"),
        new ReferenceSeed(LAST_ADMIN_PROTECTION_REFERENCE_DOC_ID, "ua.last-admin-protection.v1", "Last Admin Protection Rule", "Blocked removal, disablement, or role downgrade and safe recovery.", "Consult when a requested change could remove, disable, or downgrade the last tenant admin.", ReferenceDocument.ReferenceType.DOMAIN_RULE, "internal", "/agent-behavior-seeds/starter-v1/last-admin-protection-reference.md", "references/user-admin/last-admin-protection.md"),
        new ReferenceSeed(ADMIN_AUDIT_REDACTION_REFERENCE_DOC_ID, "ua.admin-audit-redaction-guide.v1", "Admin Audit Redaction Guide", "Redaction markers, safe evidence summaries, and export limits.", "Consult when summarizing audit evidence, denials, traces, or exports.", ReferenceDocument.ReferenceType.COMPLIANCE, "restricted", "/agent-behavior-seeds/starter-v1/admin-audit-redaction-guide-reference.md", "references/user-admin/admin-audit-redaction-guide.md"));
  }

  private record SkillSeed(String documentId, String stableSkillId, String title, String purpose, String whenToUse, String resourcePath, String resourceId) {}
  private record ReferenceSeed(String documentId, String stableReferenceId, String title, String summary, String whenToConsult, ReferenceDocument.ReferenceType referenceType, String accessLevel, String resourcePath, String resourceId) {}

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
