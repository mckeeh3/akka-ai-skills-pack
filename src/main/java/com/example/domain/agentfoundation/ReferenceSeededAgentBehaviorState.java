package com.example.domain.agentfoundation;

import java.util.Map;

/** In-memory reference projection of app-managed governed agent behavior records after seed import. */
public record ReferenceSeededAgentBehaviorState(
    Map<String, ReferenceAgentDefinition> agentDefinitions,
    Map<String, ReferencePromptDocument> promptDocuments,
    Map<String, ReferencePromptVersion> promptVersions,
    Map<String, ReferenceSkillDocument> skillDocuments,
    Map<String, ReferenceSkillVersion> skillVersions,
    Map<String, ReferenceAgentSkillManifest> skillManifests,
    Map<String, ReferenceToolPermissionBoundary> toolBoundaries,
    Map<String, SeedProvenance> provenance) {

  public ReferenceSeededAgentBehaviorState {
    agentDefinitions = Map.copyOf(agentDefinitions);
    promptDocuments = Map.copyOf(promptDocuments);
    promptVersions = Map.copyOf(promptVersions);
    skillDocuments = Map.copyOf(skillDocuments);
    skillVersions = Map.copyOf(skillVersions);
    skillManifests = Map.copyOf(skillManifests);
    toolBoundaries = Map.copyOf(toolBoundaries);
    provenance = Map.copyOf(provenance);
  }

  public static ReferenceSeededAgentBehaviorState empty() {
    return new ReferenceSeededAgentBehaviorState(
        Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
  }

  public ReferenceSeededAgentBehaviorState withPromptCustomization(
      String promptDocumentId, String promptVersionId, String customizedContent, String checksum) {
    var promptVersionsCopy = new java.util.HashMap<>(promptVersions);
    promptVersionsCopy.put(
        promptVersionId,
        new ReferencePromptVersion(
            promptDocuments.get(promptDocumentId).tenantId(),
            promptDocumentId,
            promptVersionId,
            ReferencePromptVersion.VersionStatus.ACTIVE,
            customizedContent,
            checksum));
    return new ReferenceSeededAgentBehaviorState(
        agentDefinitions,
        promptDocuments,
        promptVersionsCopy,
        skillDocuments,
        skillVersions,
        skillManifests,
        toolBoundaries,
        provenance);
  }

  public record SeedProvenance(
      String artifactType,
      String artifactId,
      String versionId,
      String seedBundleId,
      String contentVersion,
      String checksum,
      String importedBy,
      String correlationId) {}
}
