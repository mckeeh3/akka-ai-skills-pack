package ai.first.application.foundation.agent;

import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.application.coreapp.myaccount.MyAccountService;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.domain.foundation.agent.ModelPolicy;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.SeedProvenance;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import ai.first.application.coreapp.agentadmin.AgentAdminService;
import ai.first.application.coreapp.audit.AuditTraceEvidenceTools;
import ai.first.application.coreapp.governance.GovernancePolicyEvidenceTools;
import ai.first.application.coreapp.myaccount.MyAccountEvidenceTools;

/** Imports implementation-packaged default agent behavior records into tenant-scoped governed state. */
public final class AgentBehaviorSeedLoader {
  public static final String SEED_BUNDLE_ID = "starter-agent-behavior-v1";
  public static final String CONTENT_VERSION = "1";
  public static final String MY_ACCOUNT_AGENT_ID = "my-account-agent";
  public static final String USER_ADMIN_AGENT_ID = "user-admin-agent";
  public static final String AGENT_ADMIN_AGENT_ID = "agent-agent-admin";
  public static final String AUDIT_TRACE_AGENT_ID = "audit-trace-agent";
  public static final String GOVERNANCE_POLICY_AGENT_ID = "governance-policy-agent";
  public static final List<String> CORE_V0_AGENT_IDS = List.of(MY_ACCOUNT_AGENT_ID, USER_ADMIN_AGENT_ID, AGENT_ADMIN_AGENT_ID, AUDIT_TRACE_AGENT_ID, GOVERNANCE_POLICY_AGENT_ID);
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
  public static final String STARTER_DEFAULT_MODEL_CONFIG_ID = "starter-default-model";
  public static final String STARTER_DEFAULT_MODEL_POLICY_ID = "starter-default-model-policy";
  public static final String MY_ACCOUNT_DEFAULT_MODEL_CONFIG_ID = "foundation-my-account-default-model";
  public static final String MY_ACCOUNT_DEFAULT_MODEL_POLICY_ID = "foundation-my-account-model-policy";

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
    var modelPolicy = repository.modelPolicy(tenantId, STARTER_DEFAULT_MODEL_POLICY_ID)
        .map(existing -> skipExisting("ModelPolicy", existing.modelPolicyRefId(), result, existing))
        .orElseGet(() -> createModelPolicy(tenantId, importerActor, correlationId, now, result));
    var modelConfig = repository.modelConfigRef(tenantId, STARTER_DEFAULT_MODEL_CONFIG_ID)
        .map(existing -> skipExisting("ModelConfigRef", existing.modelConfigRefId(), result, existing))
        .orElseGet(() -> createModelConfigRef(tenantId, modelPolicy, importerActor, correlationId, now, result));
    repository.agentDefinition(tenantId, USER_ADMIN_AGENT_ID)
        .map(existing -> skipExisting("AgentDefinition", existing.agentDefinitionId(), result, existing))
        .orElseGet(() -> createAgentDefinition(tenantId, prompt, manifest, referenceManifest, boundary, modelConfig, modelPolicy, importerActor, correlationId, now, result));
    for (var seed : additionalCoreAgentSeeds()) {
      importBasicCoreAgent(tenantId, seed, modelConfig, modelPolicy, importerActor, correlationId, now, result);
    }
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

  private ModelPolicy createModelPolicy(String tenantId, String actor, String correlationId, Instant now, SeedImportResult result) {
    return createNamedModelPolicy(tenantId, STARTER_DEFAULT_MODEL_POLICY_ID, "Starter default model policy", "model-policies/starter-default-model-policy", actor, correlationId, now, result);
  }

  private ModelPolicy createNamedModelPolicy(String tenantId, String modelPolicyId, String displayName, String provenanceId, String actor, String correlationId, Instant now, SeedImportResult result) {
    var safePolicyFacts = modelPolicyId + ":openai-low-temperature:noFallback:summary";
    var checksum = checksum(safePolicyFacts);
    var policy = new ModelPolicy(tenantId, modelPolicyId, displayName, AgentLifecycleStatus.ACTIVE, List.of("openai-low-temperature"), List.of(), List.of(), true, "summary", provenance(provenanceId, checksum, actor, correlationId, now, false), now, now);
    repository.saveModelPolicy(policy);
    result.created("ModelPolicy", modelPolicyId);
    return policy;
  }

  private ModelConfigRef createModelConfigRef(String tenantId, ModelPolicy policy, String actor, String correlationId, Instant now, SeedImportResult result) {
    return createNamedModelConfigRef(tenantId, STARTER_DEFAULT_MODEL_CONFIG_ID, "Starter default low-temperature model", policy, CORE_V0_AGENT_IDS, List.of(AgentRuntimeService.INVOKE_CAPABILITY, AgentRuntimeService.MY_ACCOUNT_INVOKE_CAPABILITY, AgentRuntimeService.AGENT_ADMIN_INVOKE_CAPABILITY, AgentRuntimeService.AUDIT_TRACE_INVOKE_CAPABILITY, AgentRuntimeService.GOVERNANCE_POLICY_INVOKE_CAPABILITY, AgentRuntimeService.AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY), "model-configs/starter-default-model", actor, correlationId, now, result);
  }

  private ModelConfigRef createNamedModelConfigRef(String tenantId, String modelConfigId, String displayName, ModelPolicy policy, List<String> agentIds, List<String> capabilityIds, String provenanceId, String actor, String correlationId, Instant now, SeedImportResult result) {
    var safeModelFacts = modelConfigId + ":openai-low-temperature:" + policy.modelPolicyRefId();
    var checksum = checksum(safeModelFacts);
    var model = new ModelConfigRef(tenantId, modelConfigId, displayName, "openai-low-temperature", AgentLifecycleStatus.ACTIVE, agentIds, capabilityIds, List.of("runtime", "test", "replay"), List.of(AgentDefinition.AuthorityLevel.APPROVAL_REQUIRED), policy.modelPolicyRefId(), provenance(provenanceId, checksum, actor, correlationId, now, false), now, now);
    repository.saveModelConfigRef(model);
    result.created("ModelConfigRef", modelConfigId);
    return model;
  }

  private AgentDefinition createAgentDefinition(String tenantId, PromptDocument prompt, AgentSkillManifest manifest, AgentReferenceManifest referenceManifest, ToolPermissionBoundary boundary, ModelConfigRef modelConfig, ModelPolicy modelPolicy, String actor, String correlationId, Instant now, SeedImportResult result) {
    var checksum = checksum(prompt.contentChecksum() + manifest.compactManifestChecksum() + referenceManifest.compactManifestChecksum() + boundary.checksum() + modelConfig.modelConfigRefId() + modelPolicy.modelPolicyRefId());
    var definition = new AgentDefinition(tenantId, USER_ADMIN_AGENT_ID, "User Admin Agent", "Role-authorized functional agent for user, invitation, membership, and access-review administration.", AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA, "user-admin", AgentDefinition.AuthorityLevel.APPROVAL_REQUIRED, AgentLifecycleStatus.ACTIVE, prompt.promptDocumentId(), prompt.activeVersion(), manifest.manifestId(), manifest.manifestVersion(), referenceManifest.manifestId(), referenceManifest.manifestVersion(), boundary.boundaryId(), boundary.boundaryVersion(), modelConfig.modelConfigRefId(), modelPolicy.modelPolicyRefId(), "ai.first.application.agent.UserAdminAgent", List.of("PromptAssemblyTrace", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"), provenance("agents/user-admin-agent", checksum, actor, correlationId, now, false), now, now);
    repository.saveAgentDefinition(definition);
    result.created("AgentDefinition", USER_ADMIN_AGENT_ID);
    return definition;
  }

  private void importBasicCoreAgent(String tenantId, BasicCoreAgentSeed seed, ModelConfigRef modelConfig, ModelPolicy modelPolicy, String actor, String correlationId, Instant now, SeedImportResult result) {
    var promptContent = readSeedResource(seed.promptResourcePath());
    var skillContent = readSeedResource(seed.skillResourcePath());
    var referenceContent = readSeedResource(seed.referenceResourcePath());
    validateContent(promptContent, seed.promptDocumentId());
    validateContent(skillContent, seed.skillDocumentId());
    validateContent(referenceContent, seed.referenceDocumentId());
    var prompt = repository.promptDocument(tenantId, seed.promptDocumentId())
        .map(existing -> skipOrDraftPrompt(existing, checksum(promptContent), result))
        .orElseGet(() -> createBasicPrompt(tenantId, seed, promptContent, checksum(promptContent), actor, correlationId, now, result));
    var skill = repository.skillDocument(tenantId, seed.skillDocumentId())
        .map(existing -> skipOrDraftSkill(existing, checksum(skillContent), result))
        .orElseGet(() -> createBasicSkill(tenantId, seed, skillContent, checksum(skillContent), actor, correlationId, now, result));
    var reference = repository.referenceDocument(tenantId, seed.referenceDocumentId())
        .map(existing -> skipOrDraftReference(existing, checksum(referenceContent), result))
        .orElseGet(() -> createBasicReference(tenantId, seed, referenceContent, checksum(referenceContent), actor, correlationId, now, result));
    var manifest = repository.skillManifest(tenantId, seed.skillManifestId())
        .map(existing -> skipExisting("AgentSkillManifest", existing.manifestId(), result, existing))
        .orElseGet(() -> createBasicSkillManifest(tenantId, seed, skill, actor, correlationId, now, result));
    var referenceManifest = repository.referenceManifest(tenantId, seed.referenceManifestId())
        .map(existing -> skipExisting("AgentReferenceManifest", existing.manifestId(), result, existing))
        .orElseGet(() -> createBasicReferenceManifest(tenantId, seed, reference, actor, correlationId, now, result));
    var boundary = repository.toolBoundary(tenantId, seed.toolBoundaryId())
        .map(existing -> skipExisting("ToolPermissionBoundary", existing.boundaryId(), result, existing))
        .orElseGet(() -> createBasicBoundary(tenantId, seed, actor, correlationId, now, result));
    final ModelPolicy selectedModelPolicy;
    final ModelConfigRef selectedModelConfig;
    if (MY_ACCOUNT_AGENT_ID.equals(seed.agentDefinitionId())) {
      var myAccountModelPolicy = repository.modelPolicy(tenantId, MY_ACCOUNT_DEFAULT_MODEL_POLICY_ID)
          .map(existing -> skipExisting("ModelPolicy", existing.modelPolicyRefId(), result, existing))
          .orElseGet(() -> createNamedModelPolicy(tenantId, MY_ACCOUNT_DEFAULT_MODEL_POLICY_ID, "Foundation My Account model policy", "model-policies/foundation-my-account-model-policy", actor, correlationId, now, result));
      selectedModelPolicy = myAccountModelPolicy;
      selectedModelConfig = repository.modelConfigRef(tenantId, MY_ACCOUNT_DEFAULT_MODEL_CONFIG_ID)
          .map(existing -> skipExisting("ModelConfigRef", existing.modelConfigRefId(), result, existing))
          .orElseGet(() -> createNamedModelConfigRef(tenantId, MY_ACCOUNT_DEFAULT_MODEL_CONFIG_ID, "Foundation My Account default model", myAccountModelPolicy, List.of(MY_ACCOUNT_AGENT_ID), List.of(AgentRuntimeService.MY_ACCOUNT_INVOKE_CAPABILITY), "model-configs/foundation-my-account-default-model", actor, correlationId, now, result));
    } else {
      selectedModelPolicy = modelPolicy;
      selectedModelConfig = modelConfig;
    }
    repository.agentDefinition(tenantId, seed.agentDefinitionId())
        .map(existing -> skipExisting("AgentDefinition", existing.agentDefinitionId(), result, existing))
        .orElseGet(() -> createBasicAgentDefinition(tenantId, seed, prompt, manifest, referenceManifest, boundary, selectedModelConfig, selectedModelPolicy, actor, correlationId, now, result));
  }

  private PromptDocument createBasicPrompt(String tenantId, BasicCoreAgentSeed seed, String content, String checksum, String actor, String correlationId, Instant now, SeedImportResult result) {
    var prompt = new PromptDocument(tenantId, seed.promptDocumentId(), seed.agentDefinitionId(), seed.displayName() + " system prompt", "system", AgentLifecycleStatus.ACTIVE, 1, content, checksum, "Initial implementation-packaged prompt seed for " + seed.displayName() + ".", provenance("prompts/" + seed.slug() + "-system.md", checksum, actor, correlationId, now, false), now, now);
    repository.savePromptDocument(prompt);
    result.created("PromptDocument", seed.promptDocumentId());
    return prompt;
  }

  private SkillDocument createBasicSkill(String tenantId, BasicCoreAgentSeed seed, String content, String checksum, String actor, String correlationId, Instant now, SeedImportResult result) {
    var skill = new SkillDocument(tenantId, seed.skillDocumentId(), seed.stableSkillId(), seed.skillTitle(), seed.skillPurpose(), seed.skillWhenToUse(), List.of("foundation", seed.functionalAreaId()), AgentLifecycleStatus.ACTIVE, 1, content, checksum, provenance("skills/" + seed.slug() + "/starter-scope.md", checksum, actor, correlationId, now, false), now, now);
    repository.saveSkillDocument(skill);
    result.created("SkillDocument", seed.skillDocumentId());
    return skill;
  }

  private ReferenceDocument createBasicReference(String tenantId, BasicCoreAgentSeed seed, String content, String checksum, String actor, String correlationId, Instant now, SeedImportResult result) {
    var reference = new ReferenceDocument(tenantId, seed.referenceDocumentId(), seed.stableReferenceId(), seed.referenceTitle(), seed.referenceSummary(), seed.referenceWhenToConsult(), ReferenceDocument.ReferenceType.PROCESS, "internal", List.of("foundation", seed.functionalAreaId()), AgentLifecycleStatus.ACTIVE, 1, content, checksum, provenance("references/" + seed.slug() + "/starter-scope.md", checksum, actor, correlationId, now, false), now, now);
    repository.saveReferenceDocument(reference);
    result.created("ReferenceDocument", seed.referenceDocumentId());
    return reference;
  }

  private AgentSkillManifest createBasicSkillManifest(String tenantId, BasicCoreAgentSeed seed, SkillDocument skill, String actor, String correlationId, Instant now, SeedImportResult result) {
    var entries = List.of(new AgentSkillManifest.Entry(skill.stableSkillId(), skill.skillDocumentId(), skill.activeVersion(), skill.title(), skill.purpose(), skill.whenToUse()));
    var checksum = checksum(entries.toString());
    var manifest = new AgentSkillManifest(tenantId, seed.skillManifestId(), seed.agentDefinitionId(), AgentLifecycleStatus.ACTIVE, 1, entries, checksum, provenance("manifests/" + seed.slug() + "-agent-skill-manifest", checksum, actor, correlationId, now, false), now, now);
    repository.saveSkillManifest(manifest);
    result.created("AgentSkillManifest", seed.skillManifestId());
    return manifest;
  }

  private AgentReferenceManifest createBasicReferenceManifest(String tenantId, BasicCoreAgentSeed seed, ReferenceDocument reference, String actor, String correlationId, Instant now, SeedImportResult result) {
    var entries = List.of(new AgentReferenceManifest.Entry(reference.stableReferenceId(), reference.referenceDocumentId(), reference.activeVersion(), reference.title(), reference.summary(), reference.whenToConsult(), "consult", reference.accessLevel()));
    var checksum = checksum(entries.toString());
    var manifest = new AgentReferenceManifest(tenantId, seed.referenceManifestId(), seed.agentDefinitionId(), seed.slug() + "-agent.expertise", AgentLifecycleStatus.ACTIVE, 1, entries, checksum, provenance("manifests/" + seed.slug() + "-agent-reference-manifest", checksum, actor, correlationId, now, false), now, now);
    repository.saveReferenceManifest(manifest);
    result.created("AgentReferenceManifest", seed.referenceManifestId());
    return manifest;
  }

  private ToolPermissionBoundary createBasicBoundary(String tenantId, BasicCoreAgentSeed seed, String actor, String correlationId, Instant now, SeedImportResult result) {
    var grants = new ArrayList<ToolPermissionBoundary.ToolGrant>();
    grants.add(new ToolPermissionBoundary.ToolGrant("readSkill", ToolPermissionBoundary.Category.READ_SKILL, "agent.skills.read", List.of("READ"), List.of("runtime", "test", "replay"), "none", "bounded_autonomous", false, "SkillLoadTrace"));
    grants.add(new ToolPermissionBoundary.ToolGrant("readReferenceDoc", ToolPermissionBoundary.Category.READ_REFERENCE, "agent.references.read", List.of("READ"), List.of("runtime", "test", "replay"), "none", "bounded_autonomous", false, "ReferenceLoadTrace"));
    if (MY_ACCOUNT_AGENT_ID.equals(seed.agentDefinitionId())) {
      grants.add(new ToolPermissionBoundary.ToolGrant("myAccountEvidence.read", ToolPermissionBoundary.Category.DATA_LOOKUP, MyAccountEvidenceTools.CAPABILITY_ID, List.of("READ"), List.of("runtime", "test"), "none", "bounded_autonomous", false, "AgentWorkTrace"));
    }
    if (AGENT_ADMIN_AGENT_ID.equals(seed.agentDefinitionId())) {
      grants.add(new ToolPermissionBoundary.ToolGrant("agentAdminEvidence.read", ToolPermissionBoundary.Category.DATA_LOOKUP, AgentAdminService.LIST_DEFINITIONS, List.of("READ"), List.of("runtime", "test"), "none", "bounded_autonomous", false, "AgentWorkTrace"));
    }
    if (AUDIT_TRACE_AGENT_ID.equals(seed.agentDefinitionId())) {
      grants.add(new ToolPermissionBoundary.ToolGrant("auditTraceEvidence.read", ToolPermissionBoundary.Category.DATA_LOOKUP, AuditTraceEvidenceTools.CAPABILITY_ID, List.of("READ"), List.of("runtime", "test"), "none", "bounded_autonomous", false, "AgentWorkTrace"));
    }
    if (GOVERNANCE_POLICY_AGENT_ID.equals(seed.agentDefinitionId())) {
      grants.add(new ToolPermissionBoundary.ToolGrant("governancePolicyEvidence.read", ToolPermissionBoundary.Category.DATA_LOOKUP, GovernancePolicyEvidenceTools.CAPABILITY_ID, List.of("READ"), List.of("runtime", "test"), "none", "bounded_autonomous", false, "AgentWorkTrace"));
    }
    var checksum = checksum(grants.toString());
    var boundary = new ToolPermissionBoundary(tenantId, seed.toolBoundaryId(), seed.agentDefinitionId(), AgentLifecycleStatus.ACTIVE, 1, grants, checksum, provenance("tool-boundaries/" + seed.slug() + "-agent-tools", checksum, actor, correlationId, now, false), now, now);
    repository.saveToolBoundary(boundary);
    result.created("ToolPermissionBoundary", seed.toolBoundaryId());
    return boundary;
  }

  private AgentDefinition createBasicAgentDefinition(String tenantId, BasicCoreAgentSeed seed, PromptDocument prompt, AgentSkillManifest manifest, AgentReferenceManifest referenceManifest, ToolPermissionBoundary boundary, ModelConfigRef modelConfig, ModelPolicy modelPolicy, String actor, String correlationId, Instant now, SeedImportResult result) {
    var checksum = checksum(prompt.contentChecksum() + manifest.compactManifestChecksum() + referenceManifest.compactManifestChecksum() + boundary.checksum() + modelConfig.modelConfigRefId() + modelPolicy.modelPolicyRefId());
    var definition = new AgentDefinition(tenantId, seed.agentDefinitionId(), seed.displayName(), seed.description(), AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA, seed.functionalAreaId(), AgentDefinition.AuthorityLevel.APPROVAL_REQUIRED, AgentLifecycleStatus.ACTIVE, prompt.promptDocumentId(), prompt.activeVersion(), manifest.manifestId(), manifest.manifestVersion(), referenceManifest.manifestId(), referenceManifest.manifestVersion(), boundary.boundaryId(), boundary.boundaryVersion(), modelConfig.modelConfigRefId(), modelPolicy.modelPolicyRefId(), "ai.first.application.agent." + seed.runtimeClassName(), List.of("PromptAssemblyTrace", "SkillLoadTrace", "ReferenceLoadTrace", "AgentWorkTrace"), provenance("agents/" + seed.slug() + "-agent", checksum, actor, correlationId, now, false), now, now);
    repository.saveAgentDefinition(definition);
    result.created("AgentDefinition", seed.agentDefinitionId());
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

  private List<BasicCoreAgentSeed> additionalCoreAgentSeeds() {
    return List.of(
        new BasicCoreAgentSeed(MY_ACCOUNT_AGENT_ID, "my-account", "My Account Agent", "Role-authorized functional agent for signed-in account, profile, settings, context, personal attention, notifications, and digest/export guidance.", "MyAccountAgent", "prompt-my-account-system", "skill-my-account-starter-guidance", "ma.account-context-explanation.v1", "My Account Context Explanation", "Explain selected context, memberships, capabilities, notification/digest boundaries, and safe recovery without hidden-scope enumeration.", "Use for My Account profile/settings guidance, context explanation, notification triage, personal digest requests, and safe self-service next steps.", "ref-my-account-starter-scope", "ma.auth-context-guide.v1", "My Account Auth Context Guide", "Available and deferred My Account context, profile/settings, notification, and digest capabilities.", "Consult before explaining profile, settings, AuthContext, notification lifecycle, digest/export, sign-out, or deferred self-service behavior.", "manifest-my-account", "reference-manifest-my-account", "tool-boundary-my-account", "/agent-behavior-seeds/starter-v1/my-account-system.md", "/agent-behavior-seeds/starter-v1/my-account-starter-guidance.md", "/agent-behavior-seeds/starter-v1/my-account-starter-scope-reference.md"),
        new BasicCoreAgentSeed(AGENT_ADMIN_AGENT_ID, "agent-admin", "Agent Admin Agent", "Role-authorized functional agent for governed agent definitions, prompts, skills, manifests, tool boundaries, model refs, tests, and traces.", "AgentAdminAgent", "prompt-agent-admin-system", "skill-agent-admin-starter-guidance", "agent-admin.starter-guidance.v1", "Agent Admin Starter Guidance", "Explain governed agent behavior records, approvals, tests, model refs, and safe deferrals in the starter.", "Use for Agent Admin bootstrap questions about prompts, skills, manifests, tool boundaries, model refs, and traces.", "ref-agent-admin-starter-scope", "agent-admin.starter-scope.v1", "Agent Admin Starter Scope", "Available and deferred Agent Admin starter capabilities.", "Consult before explaining seeded behavior records, provider-secret boundaries, approvals, or runtime tests.", "manifest-agent-admin", "reference-manifest-agent-admin", "tool-boundary-agent-admin", "/agent-behavior-seeds/starter-v1/agent-admin-system.md", "/agent-behavior-seeds/starter-v1/agent-admin-starter-guidance.md", "/agent-behavior-seeds/starter-v1/agent-admin-starter-scope-reference.md"),
        new BasicCoreAgentSeed(AUDIT_TRACE_AGENT_ID, "audit-trace", "Audit/Trace Agent", "Role-authorized functional agent for browser-safe admin audit, authorization, prompt assembly, skill/reference load, and work trace explanations.", "AuditTraceAgent", "prompt-audit-trace-system", "skill-audit-trace-starter-guidance", "audit-trace.starter-guidance.v1", "Audit/Trace Starter Guidance", "Explain trace categories, redaction boundaries, correlation ids, and starter audit visibility.", "Use for audit/trace questions, correlation ids, denial explanations, and safe evidence summaries.", "ref-audit-trace-starter-scope", "audit-trace.starter-scope.v1", "Audit/Trace Starter Scope", "Available and deferred Audit/Trace starter capabilities.", "Consult before explaining PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, AdminAuditEvent, or redaction boundaries.", "manifest-audit-trace", "reference-manifest-audit-trace", "tool-boundary-audit-trace", "/agent-behavior-seeds/starter-v1/audit-trace-system.md", "/agent-behavior-seeds/starter-v1/audit-trace-starter-guidance.md", "/agent-behavior-seeds/starter-v1/audit-trace-starter-scope-reference.md"),
        new BasicCoreAgentSeed(GOVERNANCE_POLICY_AGENT_ID, "governance-policy", "Governance/Policy Agent", "Role-authorized functional agent for policy guardrails, approvals, improvement proposals, simulations, denials, and outcome evidence.", "GovernancePolicyAgent", "prompt-governance-policy-system", "skill-governance-policy-starter-guidance", "governance-policy.starter-guidance.v1", "Governance/Policy Starter Guidance", "Explain approval-required behavior, policy simulations, deferred commits, and safe governance next steps in the starter.", "Use for governance, policy, approval, denied authority expansion, and improvement-review questions.", "ref-governance-policy-starter-scope", "governance-policy.starter-scope.v1", "Governance/Policy Starter Scope", "Available and deferred Governance/Policy starter capabilities.", "Consult before explaining policy reads, simulations, commits, proposal approval, or deferred full-core governance behavior.", "manifest-governance-policy", "reference-manifest-governance-policy", "tool-boundary-governance-policy", "/agent-behavior-seeds/starter-v1/governance-policy-system.md", "/agent-behavior-seeds/starter-v1/governance-policy-starter-guidance.md", "/agent-behavior-seeds/starter-v1/governance-policy-starter-scope-reference.md"));
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
  private record BasicCoreAgentSeed(String agentDefinitionId, String slug, String displayName, String description, String runtimeClassName, String promptDocumentId, String skillDocumentId, String stableSkillId, String skillTitle, String skillPurpose, String skillWhenToUse, String referenceDocumentId, String stableReferenceId, String referenceTitle, String referenceSummary, String referenceWhenToConsult, String skillManifestId, String referenceManifestId, String toolBoundaryId, String promptResourcePath, String skillResourcePath, String referenceResourcePath) {
    String functionalAreaId() {
      return slug;
    }
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
