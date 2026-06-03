package ai.first.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Tenant-scoped governed model policy for provider aliases, fallback behavior, and trace level. */
public record ModelPolicy(
    String tenantId,
    String modelPolicyRefId,
    String displayName,
    AgentLifecycleStatus status,
    List<String> allowedProviderAliases,
    List<String> deniedProviderAliases,
    List<String> fallbackOrder,
    boolean noFallback,
    String traceLevel,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {
  public ModelPolicy {
    allowedProviderAliases = List.copyOf(allowedProviderAliases == null ? List.of() : allowedProviderAliases);
    deniedProviderAliases = List.copyOf(deniedProviderAliases == null ? List.of() : deniedProviderAliases);
    fallbackOrder = List.copyOf(fallbackOrder == null ? List.of() : fallbackOrder);
  }
}
