package ai.first.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Tenant-scoped governed model binding. Stores safe provider aliases only; provider secrets stay in backend runtime configuration. */
public record ModelConfigRef(
    String tenantId,
    String modelConfigRefId,
    String displayName,
    String providerAlias,
    AgentLifecycleStatus status,
    List<String> allowedAgentDefinitionIds,
    List<String> allowedCapabilityIds,
    List<String> allowedModes,
    List<AgentDefinition.AuthorityLevel> allowedAuthorityLevels,
    String fallbackPolicyRef,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {
  public ModelConfigRef {
    allowedAgentDefinitionIds = List.copyOf(allowedAgentDefinitionIds == null ? List.of() : allowedAgentDefinitionIds);
    allowedCapabilityIds = List.copyOf(allowedCapabilityIds == null ? List.of() : allowedCapabilityIds);
    allowedModes = List.copyOf(allowedModes == null ? List.of() : allowedModes);
    allowedAuthorityLevels = List.copyOf(allowedAuthorityLevels == null ? List.of() : allowedAuthorityLevels);
  }
}
