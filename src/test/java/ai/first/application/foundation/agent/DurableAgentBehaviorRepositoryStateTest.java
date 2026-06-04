package ai.first.application.foundation.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.foundation.agent.AgentBehaviorRepositoryState;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class DurableAgentBehaviorRepositoryStateTest {
  @Test
  void durableStatePreservesTenantScopedGovernedRecordsForRuntimeResolution() {
    var seeded = seededRepository("tenant-1");
    var state = AgentBehaviorRepositoryState.empty()
        .savePromptDocument(seeded.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow());
    for (var skill : seeded.skillDocuments("tenant-1")) {
      state = state.saveSkillDocument(skill);
    }
    for (var reference : seeded.referenceDocuments("tenant-1")) {
      state = state.saveReferenceDocument(reference);
    }
    state = state
        .saveSkillManifest(seeded.skillManifest("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_MANIFEST_ID).orElseThrow())
        .saveReferenceManifest(seeded.referenceManifest("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_REFERENCE_MANIFEST_ID).orElseThrow())
        .saveToolBoundary(seeded.toolBoundary("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow())
        .saveModelPolicy(seeded.modelPolicy("tenant-1", AgentBehaviorSeedLoader.STARTER_DEFAULT_MODEL_POLICY_ID).orElseThrow())
        .saveModelConfigRef(seeded.modelConfigRef("tenant-1", AgentBehaviorSeedLoader.STARTER_DEFAULT_MODEL_CONFIG_ID).orElseThrow())
        .saveAgentDefinition(seeded.agentDefinition("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow());

    var agent = state.agentDefinition("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow();
    var prompt = state.promptDocument("tenant-1", agent.promptDocumentId()).orElseThrow();
    var manifest = state.skillManifest("tenant-1", agent.skillManifestId()).orElseThrow();
    var skill = state.skillDocument("tenant-1", manifest.entries().get(0).skillDocumentId()).orElseThrow();
    var referenceManifest = state.referenceManifest("tenant-1", agent.referenceManifestId()).orElseThrow();
    var reference = state.referenceDocument("tenant-1", referenceManifest.entries().get(0).referenceDocumentId()).orElseThrow();
    var boundary = state.toolBoundary("tenant-1", agent.toolBoundaryId()).orElseThrow();
    var modelConfig = state.modelConfigRef("tenant-1", agent.modelConfigRefId()).orElseThrow();
    var modelPolicy = state.modelPolicy("tenant-1", agent.modelPolicyRefId()).orElseThrow();

    assertEquals(AgentLifecycleStatus.ACTIVE, agent.status());
    assertEquals(agent.activePromptVersion(), prompt.activeVersion());
    assertEquals(manifest.entries().get(0).pinnedVersion(), skill.activeVersion());
    assertEquals(referenceManifest.entries().get(0).pinnedVersion(), reference.activeVersion());
    assertTrue(boundary.allowedToolGrants().stream().anyMatch(grant -> grant.toolId().equals("readSkill")));
    assertTrue(boundary.allowedToolGrants().stream().anyMatch(grant -> grant.toolId().equals("readReferenceDoc")));
    assertEquals(AgentLifecycleStatus.ACTIVE, modelConfig.status());
    assertEquals("openai-low-temperature", modelConfig.providerAlias());
    assertTrue(modelPolicy.allowedProviderAliases().contains(modelConfig.providerAlias()));
    assertTrue(state.agentDefinition("tenant-2", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).isEmpty());
    assertEquals(10, state.skillDocuments("tenant-1").size());
    assertEquals(10, state.referenceDocuments("tenant-1").size());
    assertEquals(0, state.skillDocuments("tenant-2").size());
    assertEquals(0, state.referenceDocuments("tenant-2").size());
  }

  @Test
  void durableStateCanCarryCustomizedPromptWithoutLeakingProviderSecretMarkers() {
    var seeded = seededRepository("tenant-1");
    var prompt = seeded.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow();
    var state = AgentBehaviorRepositoryState.empty().savePromptDocument(prompt);

    var durablePrompt = state.promptDocument("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID).orElseThrow();

    assertEquals(prompt.contentChecksum(), durablePrompt.contentChecksum());
    assertFalse(durablePrompt.contentBody().matches("(?is).*(api[_-]?key|secret|token)\\s*[:=].*"));
    assertFalse(durablePrompt.contentBody().contains("OPENAI_API_KEY"));
    assertFalse(durablePrompt.contentBody().contains("RESEND_API_KEY"));
  }

  private LocalDemoAgentBehaviorRepository seededRepository(String tenantId) {
    var repository = new LocalDemoAgentBehaviorRepository();
    var loader = new AgentBehaviorSeedLoader(
        repository,
        Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC));
    loader.importStarterDefaults(tenantId, "bootstrap", "corr-seed");
    return repository;
  }
}
