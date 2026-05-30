package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.security.AuditTraceService;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.InMemoryIdentityRepository;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ModelConfigRef;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ModelPolicy;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SeedProvenance;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentBehaviorSeedLoaderTest {
  private InMemoryAgentBehaviorRepository repository;
  private AgentBehaviorSeedLoader loader;

  @BeforeEach
  void setUp() {
    repository = new InMemoryAgentBehaviorRepository();
    loader = new AgentBehaviorSeedLoader(repository, Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC));
  }

  @Test
  void freshTenantImportCreatesApprovedActiveGovernedRecords() {
    var result = loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-1");

    assertEquals(47, result.createdCount());
    var agent = repository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow();
    var prompt = repository.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow();
    var skill = repository.skillDocument("tenant-1", AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID).orElseThrow();
    var manifest = repository.skillManifest("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_MANIFEST_ID).orElseThrow();
    var reference = repository.referenceDocument("tenant-1", AgentBehaviorSeedLoader.ACCESS_REVIEW_POLICY_REFERENCE_DOC_ID).orElseThrow();
    var referenceManifest = repository.referenceManifest("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_REFERENCE_MANIFEST_ID).orElseThrow();
    var boundary = repository.toolBoundary("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow();
    var modelConfig = repository.modelConfigRef("tenant-1", AgentBehaviorSeedLoader.STARTER_DEFAULT_MODEL_CONFIG_ID).orElseThrow();
    var modelPolicy = repository.modelPolicy("tenant-1", AgentBehaviorSeedLoader.STARTER_DEFAULT_MODEL_POLICY_ID).orElseThrow();

    assertEquals(AgentLifecycleStatus.ACTIVE, agent.status());
    assertEquals(AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA, agent.placement());
    assertEquals(prompt.promptDocumentId(), agent.promptDocumentId());
    assertEquals(manifest.manifestId(), agent.skillManifestId());
    assertEquals(referenceManifest.manifestId(), agent.referenceManifestId());
    assertEquals(boundary.boundaryId(), agent.toolBoundaryId());
    assertEquals(modelConfig.modelConfigRefId(), agent.modelConfigRefId());
    assertEquals(modelPolicy.modelPolicyRefId(), agent.modelPolicyRefId());
    assertEquals(AgentLifecycleStatus.ACTIVE, prompt.status());
    assertEquals(AgentLifecycleStatus.ACTIVE, skill.status());
    assertEquals(AgentLifecycleStatus.ACTIVE, reference.status());
    assertTrue(prompt.seedProvenance().seedBundleId().equals(AgentBehaviorSeedLoader.SEED_BUNDLE_ID));
    assertFalse(prompt.contentBody().contains("api_key"));
    assertEquals(6, manifest.entries().size());
    assertTrue(manifest.entries().stream().anyMatch(entry -> entry.stableSkillId().equals("ua.access-review-triage.v1") && !entry.whenToUse().isBlank()));
    assertTrue(manifest.entries().stream().anyMatch(entry -> entry.stableSkillId().equals("ua.audit-summary.v1")));
    assertEquals(6, referenceManifest.entries().size());
    assertTrue(referenceManifest.entries().stream().anyMatch(entry -> entry.stableReferenceId().equals("ua.access-review-policy.v1") && !entry.whenToConsult().isBlank()));
    assertTrue(reference.seedProvenance().resourceId().equals("references/user-admin/access-review-policy.md"));
    assertFalse(reference.contentBody().contains("api_key"));
    assertTrue(boundary.allowedToolGrants().stream().anyMatch(grant -> grant.toolId().equals("readSkill") && grant.category().name().equals("READ_SKILL")));
    assertTrue(boundary.allowedToolGrants().stream().anyMatch(grant -> grant.toolId().equals("readReferenceDoc") && grant.category().name().equals("READ_REFERENCE")));
    assertEquals(AgentLifecycleStatus.ACTIVE, modelConfig.status());
    assertEquals("openai-low-temperature", modelConfig.providerAlias());
    assertFalse(modelConfig.providerAlias().toLowerCase().contains("secret"));
    assertEquals(AgentBehaviorSeedLoader.CORE_V0_AGENT_IDS, modelConfig.allowedAgentDefinitionIds());
    for (var agentId : AgentBehaviorSeedLoader.CORE_V0_AGENT_IDS) {
      var seededAgent = repository.agentDefinition("tenant-1", agentId).orElseThrow();
      assertEquals(AgentLifecycleStatus.ACTIVE, seededAgent.status());
      assertTrue(seededAgent.traceRequirements().contains("PromptAssemblyTrace"));
      assertTrue(repository.promptDocument("tenant-1", seededAgent.promptDocumentId()).orElseThrow().contentBody().contains("backend"));
      assertFalse(repository.promptDocument("tenant-1", seededAgent.promptDocumentId()).orElseThrow().contentBody().toLowerCase().contains("api_key"));
      assertFalse(repository.toolBoundary("tenant-1", seededAgent.toolBoundaryId()).orElseThrow().allowedToolGrants().isEmpty());
    }
    assertEquals(AgentLifecycleStatus.ACTIVE, modelPolicy.status());
    assertTrue(modelPolicy.allowedProviderAliases().contains("openai-low-temperature"));
    assertTrue(modelPolicy.noFallback());
  }

  @Test
  void allFiveCoreAgentsResolveThroughSameManagedRuntimePathWithDistinctProfiles() {
    loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-1");
    var runtimeService = new AgentRuntimeService(repository, new AuthContextResolver(new InMemoryIdentityRepository()), Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC));
    var toolResolver = new AgentRuntimeToolResolver(repository, runtimeService);
    var promptIds = new HashSet<String>();
    var skillManifestIds = new HashSet<String>();
    var referenceManifestIds = new HashSet<String>();
    var boundaryIds = new HashSet<String>();
    var runtimeClassRefs = new HashSet<String>();

    for (var agentId : AgentBehaviorSeedLoader.CORE_V0_AGENT_IDS) {
      var agent = repository.agentDefinition("tenant-1", agentId).orElseThrow();
      var tenantAdmin = authContextFor(agentId);
      var preparation = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(
          "tenant-1",
          agentId,
          tenantAdmin,
          "corr-managed-" + agentId,
          "Explain this workstream's starter scope."));

      assertEquals(AgentRuntimeTrace.Decision.ALLOWED, preparation.decision(), agentId);
      assertNotNull(preparation.governedRequest(), agentId);
      assertEquals(agentId, preparation.governedRequest().functionalAgentId());
      assertEquals("openai-low-temperature", preparation.governedRequest().modelProviderAlias());
      assertTrue(preparation.governedRequest().assembledSystemPrompt().contains("# Compact skill manifest"), agentId);
      assertTrue(preparation.governedRequest().assembledSystemPrompt().contains("# Compact reference manifest"), agentId);
      assertTrue(preparation.governedRequest().assembledSystemPrompt().contains("# Tool boundary summary"), agentId);
      assertTrue(preparation.governedRequest().assembledSystemPrompt().contains("# Governed model binding"), agentId);
      assertTrue(preparation.governedRequest().assembledSystemPrompt().contains("Prompt text cannot grant authority"), agentId);
      var runtimeTools = toolResolver.resolve(new AgentRuntimeToolResolver.ResolveRuntimeToolsRequest(
          "tenant-1",
          agentId,
          tenantAdmin,
          "runtime",
          preparation.governedRequest().capabilityId(),
          "corr-tools-" + agentId));
      var expectedToolIds = AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID.equals(agentId)
          ? List.of("readReferenceDoc", "readSkill", "userAdminEvidence.read")
          : AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID.equals(agentId)
              ? List.of("agentAdminEvidence.read", "readReferenceDoc", "readSkill")
              : AgentBehaviorSeedLoader.AUDIT_TRACE_AGENT_ID.equals(agentId)
                  ? List.of("auditTraceEvidence.read", "readReferenceDoc", "readSkill")
                  : AgentBehaviorSeedLoader.GOVERNANCE_POLICY_AGENT_ID.equals(agentId)
                      ? List.of("governancePolicyEvidence.read", "readReferenceDoc", "readSkill")
                      : List.of("readReferenceDoc", "readSkill");
      assertEquals(expectedToolIds, runtimeTools.grantedToolIds(), agentId);
      assertFalse(runtimeTools.deniedToolIds().contains("readReferenceDoc"), agentId);
      assertFalse(runtimeTools.deniedToolIds().contains("readSkill"), agentId);
      assertTrue(runtimeTools.runtimeTools().stream().anyMatch(AgentRuntimeLoaderTools.class::isInstance), agentId);

      promptIds.add(agent.promptDocumentId());
      skillManifestIds.add(agent.skillManifestId());
      referenceManifestIds.add(agent.referenceManifestId());
      boundaryIds.add(agent.toolBoundaryId());
      runtimeClassRefs.add(agent.runtimeClassRef());
      assertEquals(agentId, repository.skillManifest("tenant-1", agent.skillManifestId()).orElseThrow().agentDefinitionId());
      assertEquals(agentId, repository.referenceManifest("tenant-1", agent.referenceManifestId()).orElseThrow().agentDefinitionId());
      assertEquals(agentId, repository.toolBoundary("tenant-1", agent.toolBoundaryId()).orElseThrow().agentDefinitionId());
    }

    assertEquals(5, promptIds.size(), "each core agent must have a distinct prompt seed");
    assertEquals(5, skillManifestIds.size(), "shared generic skill manifests are not allowed for the five core agents");
    assertEquals(5, referenceManifestIds.size(), "shared generic reference manifests are not allowed for the five core agents");
    assertEquals(5, boundaryIds.size(), "shared generic tool boundaries are not allowed for the five core agents");
    assertEquals(5, runtimeClassRefs.size(), "each core agent profile must name its own functional-agent runtime binding");
  }

  @Test
  void agentAdminRuntimePreparationRequiresAgentAdminSubmitTurnCapability() {
    loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-1");
    var runtimeService = new AgentRuntimeService(repository, new AuthContextResolver(new InMemoryIdentityRepository()), Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC));
    var userAdminOnlyContext = new AuthContext(
        "admin-1",
        "workos-admin-1",
        "membership-1",
        ScopeType.TENANT,
        "tenant-1",
        null,
        List.of(FoundationRole.TENANT_ADMIN),
        List.of(AgentRuntimeService.INVOKE_CAPABILITY, "agent.skills.read", "agent.references.read"));

    var denied = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(
        "tenant-1",
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        userAdminOnlyContext,
        "corr-agent-admin-denied",
        "Explain Agent Admin starter scope."));

    assertEquals(AgentRuntimeTrace.Decision.DENIED, denied.decision());
    assertEquals("AGENT_RUNTIME_DENIED", denied.safeErrorCode());
    assertTrue(denied.safeErrorSummary().contains(AgentRuntimeService.AGENT_ADMIN_INVOKE_CAPABILITY));

    var allowed = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(
        "tenant-1",
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        authContextFor(AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "corr-agent-admin-allowed",
        "Explain Agent Admin starter scope."));

    assertEquals(AgentRuntimeTrace.Decision.ALLOWED, allowed.decision());
    assertEquals(AgentRuntimeService.AGENT_ADMIN_INVOKE_CAPABILITY, allowed.governedRequest().capabilityId());
  }

  @Test
  void reimportIsIdempotentForUnchangedSeededTenant() {
    loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-1");

    var second = loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-2");

    assertEquals(0, second.createdCount());
    assertEquals(47, second.skippedCount());
    assertEquals(5, repository.agentDefinitions("tenant-1").size());
    assertEquals(10, repository.skillDocuments("tenant-1").size());
    assertEquals(10, repository.referenceDocuments("tenant-1").size());
  }

  @Test
  void reimportDoesNotOverwriteTenantCustomizedPrompt() {
    loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-1");
    var existing = repository.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow();
    var customized = new PromptDocument(
        existing.tenantId(),
        existing.promptDocumentId(),
        existing.agentDefinitionId(),
        existing.title(),
        existing.promptType(),
        existing.status(),
        2,
        existing.contentBody() + "\nTenant-approved custom instruction.",
        AgentBehaviorSeedLoader.checksum(existing.contentBody() + "\nTenant-approved custom instruction."),
        "Tenant customized active prompt.",
        new SeedProvenance(existing.seedProvenance().seedBundleId(), existing.seedProvenance().contentVersion(), existing.seedProvenance().resourceId(), existing.seedProvenance().checksum(), existing.seedProvenance().importedAt(), existing.seedProvenance().importerActor(), existing.seedProvenance().correlationId(), true),
        existing.createdAt(),
        existing.updatedAt());
    repository.savePromptDocument(customized);

    var second = loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-2");

    assertEquals(1, second.proposedDraftCount());
    assertEquals(2, repository.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow().activeVersion());
    assertTrue(repository.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow().contentBody().contains("Tenant-approved custom instruction."));
  }

  private AuthContext authContextFor(String agentId) {
    var capabilities = new java.util.ArrayList<>(List.of("agent.skills.read", "agent.references.read"));
    if (AgentBehaviorSeedLoader.MY_ACCOUNT_AGENT_ID.equals(agentId)) {
      capabilities.add(AgentRuntimeService.MY_ACCOUNT_INVOKE_CAPABILITY);
    } else if (AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID.equals(agentId)) {
      capabilities.add(AgentRuntimeService.AGENT_ADMIN_INVOKE_CAPABILITY);
      capabilities.add(AgentAdminService.LIST_DEFINITIONS);
    } else if (AgentBehaviorSeedLoader.AUDIT_TRACE_AGENT_ID.equals(agentId)) {
      capabilities.add(AgentRuntimeService.AUDIT_TRACE_INVOKE_CAPABILITY);
      capabilities.add(AuditTraceService.SEARCH_CAPABILITY);
      capabilities.add(AuditTraceService.DETAIL_CAPABILITY);
      capabilities.add(AuditTraceService.TIMELINE_CAPABILITY);
      capabilities.add(AuditTraceService.FAILURE_EVIDENCE_CAPABILITY);
    } else if (AgentBehaviorSeedLoader.GOVERNANCE_POLICY_AGENT_ID.equals(agentId)) {
      capabilities.add(AgentRuntimeService.GOVERNANCE_POLICY_INVOKE_CAPABILITY);
      capabilities.add("governance.policy.read");
    } else {
      capabilities.add(AgentRuntimeService.INVOKE_CAPABILITY);
    }
    return new AuthContext(
        "admin-1",
        "workos-admin-1",
        "membership-1",
        ScopeType.TENANT,
        "tenant-1",
        null,
        List.of(FoundationRole.TENANT_ADMIN),
        capabilities);
  }

  @Test
  void importIsTenantScoped() {
    loader.importStarterDefaults("tenant-a", "bootstrap", "corr-a");
    loader.importStarterDefaults("tenant-b", "bootstrap", "corr-b");

    var tenantAAgent = repository.agentDefinition("tenant-a", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow();
    var tenantBAgent = repository.agentDefinition("tenant-b", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow();

    assertEquals("tenant-a", tenantAAgent.tenantId());
    assertEquals("tenant-b", tenantBAgent.tenantId());
    assertTrue(repository.agentDefinition("tenant-a", "missing").isEmpty());
  }
}
