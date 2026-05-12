package com.example.domain.security;

/** DCA seed app roles. WorkOS authenticates users; these local roles authorize app actions. */
public enum SecurityRole {
  APP_ADMIN,
  DEALER_OWNER,
  OPERATIONS_SUPERVISOR,
  POLICY_OWNER,
  AUDITOR,
  CUSTOMER_ADMIN,
  USER
}
