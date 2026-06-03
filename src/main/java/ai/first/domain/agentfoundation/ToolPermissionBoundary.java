package ai.first.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Backend-enforced allowlist for managed-agent tools and data access. */
public record ToolPermissionBoundary(
    String tenantId,
    String boundaryId,
    String agentDefinitionId,
    AgentLifecycleStatus status,
    int boundaryVersion,
    List<ToolGrant> allowedToolGrants,
    String checksum,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {
  public ToolPermissionBoundary {
    allowedToolGrants = List.copyOf(allowedToolGrants == null ? List.of() : allowedToolGrants);
  }

  public record ToolGrant(
      String toolId,
      Category category,
      String capabilityId,
      List<String> allowedOperations,
      List<String> allowedModes,
      String sideEffectLevel,
      String autonomy,
      boolean idempotencyRequired,
      String traceLevel) {
    public ToolGrant {
      allowedOperations = List.copyOf(allowedOperations == null ? List.of() : allowedOperations);
      allowedModes = List.copyOf(allowedModes == null ? List.of() : allowedModes);
    }
  }

  public enum Category {
    LOCAL_FUNCTION,
    COMPONENT,
    MCP,
    READ_SKILL,
    READ_REFERENCE,
    DATA_LOOKUP,
    EXTERNAL_SIDE_EFFECT
  }
}
