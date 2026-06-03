package ai.first.domain.security;

import java.util.List;

/** Selected backend authorization context derived from JWT identity plus local membership state. */
public record AuthContext(
    String accountId,
    String workosUserId,
    String membershipId,
    ScopeType scopeType,
    String tenantId,
    String customerId,
    List<FoundationRole> roles,
    List<String> capabilities) {
  public AuthContext {
    roles = List.copyOf(roles == null ? List.of() : roles);
    capabilities = List.copyOf(capabilities == null ? List.of() : capabilities);
  }

  public boolean hasCapability(String capability) {
    return capabilities.contains(capability);
  }
}
