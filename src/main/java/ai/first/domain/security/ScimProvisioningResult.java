package ai.first.domain.security;

import java.util.List;

/** Browser-safe result for a SCIM-style foundation validation; never reports production success without configuration. */
public record ScimProvisioningResult(
    String status,
    String message,
    String operation,
    String externalId,
    String accountId,
    ScopeType scopeType,
    String tenantId,
    String customerId,
    List<FoundationRole> roles,
    boolean wouldMutateLocalAuthorization,
    boolean productionProviderConfigured,
    String providerDeliveryState,
    String traceId) {

  public ScimProvisioningResult {
    roles = List.copyOf(roles == null ? List.of() : roles);
  }
}
