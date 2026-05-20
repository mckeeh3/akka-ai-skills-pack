package {{JAVA_BASE_PACKAGE}}.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Tenant-scoped governed behavior profile for a runtime or functional agent. */
public record AgentDefinition(
    String tenantId,
    String agentDefinitionId,
    String displayName,
    String description,
    Placement placement,
    String functionalAreaId,
    AuthorityLevel authorityLevel,
    AgentLifecycleStatus status,
    String promptDocumentId,
    int activePromptVersion,
    String skillManifestId,
    int activeSkillManifestVersion,
    String toolBoundaryId,
    int activeToolBoundaryVersion,
    String modelConfigRefId,
    String modelPolicyRefId,
    String runtimeClassRef,
    List<String> traceRequirements,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {
  public AgentDefinition {
    traceRequirements = List.copyOf(traceRequirements == null ? List.of() : traceRequirements);
  }

  public enum Placement {
    FUNCTIONAL_CONTEXT_AREA,
    INTERNAL_WORKER
  }

  public enum AuthorityLevel {
    ADVISORY,
    DRAFT_ONLY,
    APPROVAL_REQUIRED,
    BOUNDED_AUTONOMOUS
  }
}
