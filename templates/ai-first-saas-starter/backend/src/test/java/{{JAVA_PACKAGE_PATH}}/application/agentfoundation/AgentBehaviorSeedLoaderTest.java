package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SeedProvenance;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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

    assertEquals(17, result.createdCount());
    var agent = repository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow();
    var prompt = repository.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow();
    var skill = repository.skillDocument("tenant-1", AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID).orElseThrow();
    var manifest = repository.skillManifest("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_MANIFEST_ID).orElseThrow();
    var reference = repository.referenceDocument("tenant-1", AgentBehaviorSeedLoader.ACCESS_REVIEW_POLICY_REFERENCE_DOC_ID).orElseThrow();
    var referenceManifest = repository.referenceManifest("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_REFERENCE_MANIFEST_ID).orElseThrow();
    var boundary = repository.toolBoundary("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow();

    assertEquals(AgentLifecycleStatus.ACTIVE, agent.status());
    assertEquals(AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA, agent.placement());
    assertEquals(prompt.promptDocumentId(), agent.promptDocumentId());
    assertEquals(manifest.manifestId(), agent.skillManifestId());
    assertEquals(referenceManifest.manifestId(), agent.referenceManifestId());
    assertEquals(boundary.boundaryId(), agent.toolBoundaryId());
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
  }

  @Test
  void reimportIsIdempotentForUnchangedSeededTenant() {
    loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-1");

    var second = loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-2");

    assertEquals(0, second.createdCount());
    assertEquals(17, second.skippedCount());
    assertEquals(1, repository.agentDefinitions("tenant-1").size());
    assertEquals(6, repository.skillDocuments("tenant-1").size());
    assertEquals(6, repository.referenceDocuments("tenant-1").size());
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
