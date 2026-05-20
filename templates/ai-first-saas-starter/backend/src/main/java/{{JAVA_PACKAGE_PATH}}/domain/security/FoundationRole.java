package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.util.List;

/** Canonical foundation roles; app roles extend these instead of replacing them. */
public enum FoundationRole {
  SAAS_OWNER_ADMIN(
      ScopeType.SAAS_OWNER,
      List.of(
          "saas_owner.user.manage",
          "saas_owner.tenant.read",
          "saas_owner.tenant.manage",
          "saas_owner.audit.read",
          "saas_owner.billing_boundary.manage")),
  TENANT_ADMIN(
      ScopeType.TENANT,
      List.of(
          "tenant.user.read",
          "tenant.user.manage",
          "tenant.role.manage",
          "tenant.invitation.manage",
          "tenant.customer.manage",
          "tenant.support_access.manage",
          "tenant.audit.read",
          "tenant.access_review.manage",
          "agent.user_admin.use",
          "agent.behavior.manage")),
  TENANT_EMPLOYEE(ScopeType.TENANT, List.of("tenant.app.use", "agent.workstream.use")),
  CUSTOMER_ADMIN(
      ScopeType.CUSTOMER,
      List.of(
          "customer.user.read",
          "customer.user.manage",
          "customer.role.manage",
          "customer.invitation.manage",
          "customer.audit.read",
          "customer.access_review.manage",
          "agent.user_admin.use",
          "agent.behavior.manage")),
  CUSTOMER_USER(ScopeType.CUSTOMER, List.of("customer.app.use", "agent.workstream.use")),
  AUDITOR(
      ScopeType.TENANT,
      List.of("tenant.audit.read", "tenant.access_review.read", "customer.audit.read", "customer.access_review.read"));

  private final ScopeType defaultScopeType;
  private final List<String> capabilities;

  FoundationRole(ScopeType defaultScopeType, List<String> capabilities) {
    this.defaultScopeType = defaultScopeType;
    this.capabilities = List.copyOf(capabilities);
  }

  public ScopeType defaultScopeType() {
    return defaultScopeType;
  }

  public List<String> capabilities() {
    return capabilities;
  }
}
