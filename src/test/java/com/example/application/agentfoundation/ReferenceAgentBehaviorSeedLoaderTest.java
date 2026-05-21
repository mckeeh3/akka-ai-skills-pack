package com.example.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.domain.agentfoundation.ReferenceAgentBehaviorSeedManifest;
import com.example.domain.agentfoundation.ReferenceSeededAgentBehaviorState;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReferenceAgentBehaviorSeedLoaderTest {
  private static final String TENANT_ID = "tenant-seed-reference";
  private static final String MANIFEST_V1 = "agent-behavior-seeds/reference-v1/manifest.properties";
  private static final String MANIFEST_V2 = "agent-behavior-seeds/reference-v2/manifest.properties";

  @Test
  void freshTenantBootstrapImportsDefaultGovernedAgentBehaviorRecords() {
    var loader = new ReferenceAgentBehaviorSeedLoader();
    var manifest = loader.loadManifest(MANIFEST_V1);

    var result =
        loader.importSeed(
            TENANT_ID, manifest, ReferenceSeededAgentBehaviorState.empty(), "corr-seed-v1");
    var state = result.state();

    assertTrue(result.events().contains("agent.created:" + manifest.agentDefinitionId()));
    assertTrue(result.events().contains("prompt.created:" + manifest.promptDocumentId()));
    assertTrue(result.events().contains("manifest.created:" + manifest.skillManifestId()));
    assertTrue(result.events().contains("tool-boundary.created:" + manifest.toolBoundaryId()));
    assertTrue(state.agentDefinitions().get(manifest.agentDefinitionId()).activeForRuntime());
    assertEquals(
        manifest.promptVersionId(),
        state.promptDocuments().get(manifest.promptDocumentId()).activePromptVersionId());
    assertEquals(
        manifest.promptChecksum(), state.promptVersions().get(manifest.promptVersionId()).checksum());
    assertEquals(2, state.skillDocuments().size());
    var seededSkillManifest = state.skillManifests().get(manifest.skillManifestId());
    assertEquals(2, seededSkillManifest.assignedSkillIds().size());
    assertEquals(
        "Access Review", seededSkillManifest.skillEntries().get("skill-access-review-reference").displayName());
    assertTrue(
        seededSkillManifest
            .skillEntries()
            .get("skill-access-review-reference")
            .whenToUse()
            .contains("User Admin requests about access review"));
    assertTrue(
        state
            .toolBoundaries()
            .get(manifest.toolBoundaryId())
            .allowsTool(ReferenceSkillReadAuthorizer.READ_SKILL_TOOL_ID, "runtime"));
    assertEquals(
        "corr-seed-v1", state.provenance().get("prompt:" + manifest.promptVersionId()).correlationId());
  }

  @Test
  void repeatedBootstrapIsIdempotentAndDoesNotDuplicateVersions() {
    var loader = new ReferenceAgentBehaviorSeedLoader();
    var manifest = loader.loadManifest(MANIFEST_V1);
    var first =
        loader.importSeed(
            TENANT_ID, manifest, ReferenceSeededAgentBehaviorState.empty(), "corr-seed-v1");

    var second = loader.importSeed(TENANT_ID, manifest, first.state(), "corr-seed-v1-repeat");

    assertTrue(second.events().contains("prompt.skipped-unchanged:" + manifest.promptDocumentId()));
    assertTrue(second.events().contains("agent.skipped-existing:" + manifest.agentDefinitionId()));
    assertEquals(first.state().promptVersions().size(), second.state().promptVersions().size());
    assertEquals(first.state().skillVersions().size(), second.state().skillVersions().size());
    assertEquals(first.state().agentDefinitions().size(), second.state().agentDefinitions().size());
  }

  @Test
  void checksumMismatchFailsBeforeCreatingPartialState() {
    var loader = new ReferenceAgentBehaviorSeedLoader();
    var manifest = loader.loadManifest(MANIFEST_V1);
    var invalidManifest =
        new ReferenceAgentBehaviorSeedManifest(
            manifest.seedBundleId(),
            manifest.appVersion(),
            manifest.contentVersion(),
            manifest.agentDefinitionId(),
            manifest.agentDisplayName(),
            manifest.modelConfigRef(),
            manifest.authorityLevel(),
            manifest.promptDocumentId(),
            manifest.promptVersionId(),
            manifest.promptResource(),
            "bad-checksum",
            manifest.previousPromptChecksum(),
            manifest.skillManifestId(),
            manifest.skillManifestVersionId(),
            manifest.toolBoundaryId(),
            manifest.toolBoundaryVersionId(),
            manifest.skills());

    var failure =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                loader.importSeed(
                    TENANT_ID,
                    invalidManifest,
                    ReferenceSeededAgentBehaviorState.empty(),
                    "corr-bad-checksum"));

    assertTrue(failure.getMessage().contains("Seed checksum mismatch"));
  }

  @Test
  void upgradeActivatesNewSeedVersionWhenTenantStillMatchesPriorSeedChecksum() {
    var loader = new ReferenceAgentBehaviorSeedLoader();
    var v1 = loader.loadManifest(MANIFEST_V1);
    var v2 = loader.loadManifest(MANIFEST_V2);
    var seeded =
        loader.importSeed(TENANT_ID, v1, ReferenceSeededAgentBehaviorState.empty(), "corr-seed-v1");

    var upgraded = loader.importSeed(TENANT_ID, v2, seeded.state(), "corr-seed-v2");

    assertTrue(upgraded.events().contains("prompt.upgraded:" + v2.promptDocumentId()));
    assertEquals(
        v2.promptVersionId(),
        upgraded.state().promptDocuments().get(v2.promptDocumentId()).activePromptVersionId());
    assertEquals(v2.promptChecksum(), upgraded.state().promptVersions().get(v2.promptVersionId()).checksum());
    assertEquals(
        v2.promptVersionId(),
        upgraded.state().agentDefinitions().get(v2.agentDefinitionId()).activePromptVersionId());
    assertTrue(upgraded.proposedDiffs().isEmpty());
  }

  @Test
  void upgradePreservesTenantCustomizedPromptAndCreatesProposedDiff() {
    var loader = new ReferenceAgentBehaviorSeedLoader();
    var v1 = loader.loadManifest(MANIFEST_V1);
    var v2 = loader.loadManifest(MANIFEST_V2);
    var seeded =
        loader.importSeed(TENANT_ID, v1, ReferenceSeededAgentBehaviorState.empty(), "corr-seed-v1");
    var customized =
        seeded
            .state()
            .withPromptCustomization(
                v1.promptDocumentId(),
                v1.promptVersionId(),
                "Tenant customized prompt content.",
                ReferenceAgentBehaviorSeedLoader.sha256("Tenant customized prompt content."));

    var upgraded = loader.importSeed(TENANT_ID, v2, customized, "corr-seed-v2-customized");

    assertTrue(upgraded.events().contains("prompt.customization-preserved:" + v2.promptDocumentId()));
    assertEquals(List.of("prompt.proposed-diff:" + v2.promptDocumentId()), upgraded.proposedDiffs());
    assertEquals(
        v1.promptVersionId(),
        upgraded.state().promptDocuments().get(v1.promptDocumentId()).activePromptVersionId());
    assertFalse(upgraded.state().promptVersions().containsKey(v2.promptVersionId()));
  }
}
