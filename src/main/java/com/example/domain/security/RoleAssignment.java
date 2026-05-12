package com.example.domain.security;

/** Scoped role assignment owned by local Akka state, not by frontend state or JWT role claims. */
public record RoleAssignment(SecurityRole role, String tenantId, String customerId) {

  public boolean isAppAdmin() {
    return role == SecurityRole.APP_ADMIN;
  }

  public boolean grantsTenant(String requestedTenantId) {
    return isAppAdmin()
        || (tenantId != null
            && tenantId.equals(requestedTenantId)
            && (role == SecurityRole.DEALER_OWNER
                || role == SecurityRole.OPERATIONS_SUPERVISOR
                || role == SecurityRole.POLICY_OWNER
                || role == SecurityRole.AUDITOR));
  }

  public boolean grantsCustomer(String requestedTenantId, String requestedCustomerId) {
    return isAppAdmin()
        || (tenantId != null
            && tenantId.equals(requestedTenantId)
            && (role == SecurityRole.DEALER_OWNER
                || role == SecurityRole.OPERATIONS_SUPERVISOR
                || role == SecurityRole.POLICY_OWNER
                || role == SecurityRole.AUDITOR
                || ((role == SecurityRole.CUSTOMER_ADMIN || role == SecurityRole.USER)
                    && customerId != null
                    && customerId.equals(requestedCustomerId))));
  }
}
