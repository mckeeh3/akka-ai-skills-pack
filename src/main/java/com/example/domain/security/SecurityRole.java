package com.example.domain.security;

/**
 * Reference app roles. WorkOS authenticates users; these local roles authorize app actions.
 *
 * <p>These roles are reference-specific capability roles, not the generic SaaS foundation baseline.
 * New generated SaaS apps should start from foundation roles such as SAAS_OWNER_ADMIN,
 * TENANT_ADMIN, TENANT_EMPLOYEE, CUSTOMER_ADMIN, CUSTOMER_USER, and AUDITOR, then map
 * app-specific roles like DEALER_OWNER or POLICY_OWNER to capabilities within those scopes.
 */
public enum SecurityRole {
  APP_ADMIN,
  DEALER_OWNER,
  OPERATIONS_SUPERVISOR,
  POLICY_OWNER,
  AUDITOR,
  CUSTOMER_ADMIN,
  USER
}
