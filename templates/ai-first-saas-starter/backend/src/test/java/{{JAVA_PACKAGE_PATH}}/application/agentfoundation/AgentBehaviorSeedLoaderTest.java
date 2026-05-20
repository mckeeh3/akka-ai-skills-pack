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

    assertEquals(5, result.createdCount());
    var agent = repository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow();
    var prompt = repository.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow();
    var skill = repository.skillDocument("tenant-1", AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID).orElseThrow();
    var manifest = repository.skillManifest("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_MANIFEST_ID).orElseThrow();
    var boundary = repository.toolBoundary("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow();

    assertEquals(AgentLifecycleStatus.ACTIVE, agent.status());
    assertEquals(AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA, agent.placement());
    assertEquals(prompt.promptDocumentId(), agent.promptDocumentId());
    assertEquals(manifest.manifestId(), agent.skillManifestId());
    assertEquals(boundary.boundaryId(), agent.toolBoundaryId());
    assertEquals(AgentLifecycleStatus.ACTIVE, prompt.status());
    assertEquals(AgentLifecycleStatus.ACTIVE, skill.status());
    assertTrue(prompt.seedProvenance().seedBundleId().equals(AgentBehaviorSeedLoader.SEED_BUNDLE_ID));
    assertFalse(prompt.contentBody().contains("api_key"));
    assertFalse(manifest.entries().get(0).whenToUse().isBlank());
    assertTrue(boundary.allowedToolGrants().stream().anyMatch(grant -> grant.toolId().equals("readSkill") && grant.category().name().equals("READ_SKILL")));
  }

  @Test
  void reimportIsIdempotentForUnchangedSeededTenant() {
    loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-1");

    var second = loader.importStarterDefaults("tenant-1", "bootstrap", "corr-seed-2");

    assertEquals(0, second.createdCount());
    assertEquals(5, second.skippedCount());
    assertEquals(1, repository.agentDefinitions("tenant-1").size());
    assertEquals(1, repository.skillDocuments("tenant-1").size());
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
