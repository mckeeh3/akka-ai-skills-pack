package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Authorization boundary for local SaaS memberships. */
public enum ScopeType {
  SAAS_OWNER,
  TENANT,
  CUSTOMER
}
