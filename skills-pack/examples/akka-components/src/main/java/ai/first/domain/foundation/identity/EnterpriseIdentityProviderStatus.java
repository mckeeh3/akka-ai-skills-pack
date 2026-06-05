package ai.first.domain.foundation.identity;

import java.util.List;

/** Browser-safe IAM/SCIM/SSO foundation status; contains no provider secrets or private provider ids. */
public record EnterpriseIdentityProviderStatus(
    String tenantId,
    String customerId,
    boolean workosAuthKitBoundaryPreserved,
    boolean scimFoundationEnabled,
    boolean productionScimConfigured,
    boolean productionSsoConfigured,
    String productionReadiness,
    List<String> providerLimits,
    List<String> requiredSecretNames,
    String traceId) {

  public EnterpriseIdentityProviderStatus {
    providerLimits = List.copyOf(providerLimits == null ? List.of() : providerLimits);
    requiredSecretNames = List.copyOf(requiredSecretNames == null ? List.of() : requiredSecretNames);
  }
}
