package ai.first.domain.foundation.agent;

import java.time.Instant;
import java.util.List;

/**
 * Immutable tenant-scoped managed-agent behavior profile assignment snapshot.
 *
 * <p>The profile version pins the runtime behavior seams that Agent Admin may change without
 * mutating generated agent identity, generated tool code, skill document versions, or provider
 * credentials. Provider aliases remain in {@link ModelConfigRef}; this record stores only safe ids
 * and assignment summaries needed by catalog/detail views and the runtime loader.
 */
public record AgentBehaviorProfileVersion(
    String tenantId,
    String agentDefinitionId,
    int profileVersion,
    ScopeProvenance scopeProvenance,
    String clonedFromTenantId,
    Integer clonedFromProfileVersion,
    AgentLifecycleStatus status,
    String promptDocumentId,
    int activePromptVersion,
    String skillManifestId,
    int activeSkillManifestVersion,
    String referenceManifestId,
    int activeReferenceManifestVersion,
    String modelConfigRefId,
    String modelPolicyRefId,
    String toolBoundaryId,
    int activeToolBoundaryVersion,
    List<String> assignedSkillDocumentIds,
    List<String> assignedGeneratedToolIds,
    String profileChecksum,
    String changeSummary,
    String actorAccountId,
    Instant createdAt) {
  public AgentBehaviorProfileVersion {
    scopeProvenance = scopeProvenance == null ? ScopeProvenance.GLOBAL_DEFAULT : scopeProvenance;
    status = status == null ? AgentLifecycleStatus.ACTIVE : status;
    assignedSkillDocumentIds = List.copyOf(assignedSkillDocumentIds == null ? List.of() : assignedSkillDocumentIds);
    assignedGeneratedToolIds = List.copyOf(assignedGeneratedToolIds == null ? List.of() : assignedGeneratedToolIds);
  }

  public enum ScopeProvenance {
    GLOBAL_DEFAULT,
    TENANT_OVERRIDE
  }
}
