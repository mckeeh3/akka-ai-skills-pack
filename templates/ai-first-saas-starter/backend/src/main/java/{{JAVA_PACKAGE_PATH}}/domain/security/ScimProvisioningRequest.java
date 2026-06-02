package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.util.List;

/** SCIM-style validated provisioning command envelope for the provider-neutral enterprise foundation. */
public record ScimProvisioningRequest(
    String operation,
    String externalId,
    String email,
    String displayName,
    ScopeType scopeType,
    String tenantId,
    String customerId,
    List<FoundationRole> roles,
    String reason,
    String idempotencyKey) {

  public ScimProvisioningRequest {
    operation = operation == null ? "" : operation.trim().toUpperCase();
    email = email == null ? "" : email.trim().toLowerCase();
    displayName = displayName == null || displayName.isBlank() ? email : displayName.trim();
    roles = List.copyOf(roles == null ? List.of() : roles);
    reason = reason == null ? "scim-foundation-validation" : reason;
  }
}
