package com.example.domain.agentfoundation;

/** Minimal tenant-scoped AgentDefinition record for executable reference slices. */
public record ReferenceAgentDefinition(
    String tenantId,
    String agentDefinitionId,
    String displayName,
    LifecycleStatus lifecycleStatus,
    String promptDocumentId,
    String activePromptVersionId,
    String skillManifestId,
    String toolBoundaryId,
    String modelConfigRef,
    String authorityLevel) {

  public enum LifecycleStatus {
    DRAFT,
    ACTIVE,
    DISABLED,
    ARCHIVED
  }

  public boolean activeForRuntime() {
    return lifecycleStatus == LifecycleStatus.ACTIVE;
  }

  public ReferenceAgentDefinition disabled() {
    return new ReferenceAgentDefinition(
        tenantId,
        agentDefinitionId,
        displayName,
        LifecycleStatus.DISABLED,
        promptDocumentId,
        activePromptVersionId,
        skillManifestId,
        toolBoundaryId,
        modelConfigRef,
        authorityLevel);
  }
}
