package ai.first.domain.foundation.identity;

import java.util.List;

/** Browser-safe SSO configuration validation result for the provider-neutral enterprise foundation. */
public record SsoConfigurationValidation(
    String status,
    String message,
    String tenantId,
    String domain,
    String issuer,
    boolean productionRequested,
    boolean productionProviderConfigured,
    List<String> missingSecretNames,
    String traceId) {

  public SsoConfigurationValidation {
    missingSecretNames = List.copyOf(missingSecretNames == null ? List.of() : missingSecretNames);
  }
}
