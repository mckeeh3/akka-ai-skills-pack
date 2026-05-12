package com.example.domain;

public record RoleAssignment(
  Role role,
  String tenantId,
  String customerId
) {
  public boolean isAppAdmin() {
    return role == Role.APP_ADMIN;
  }

  public boolean grantsTenant(String requestedTenantId) {
    return isAppAdmin() ||
      ((role == Role.TENANT_ADMIN || role == Role.CUSTOMER_ADMIN || role == Role.USER) &&
        tenantId != null && tenantId.equals(requestedTenantId));
  }

  public boolean grantsCustomer(String requestedTenantId, String requestedCustomerId) {
    return isAppAdmin() ||
      (tenantId != null && tenantId.equals(requestedTenantId) &&
        (role == Role.TENANT_ADMIN ||
          ((role == Role.CUSTOMER_ADMIN || role == Role.USER) &&
            customerId != null && customerId.equals(requestedCustomerId))));
  }
}
