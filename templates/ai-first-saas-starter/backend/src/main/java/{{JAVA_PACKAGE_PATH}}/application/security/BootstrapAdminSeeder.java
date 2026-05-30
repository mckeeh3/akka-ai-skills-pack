package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import java.util.List;
import java.util.Locale;

/** Seeds explicitly configured first-admin accounts without allowing open self-registration. */
public final class BootstrapAdminSeeder {
  public static final String DEFAULT_TENANT_ID = "tenant-starter";
  public static final String DEFAULT_TENANT_NAME = "Starter Tenant";
  public static final String LOCAL_DEMO_ADMIN_EMAIL = "admin@example.test";

  private BootstrapAdminSeeder() {}

  public static void seedConfiguredAdmins(LocalDemoIdentityRepository repository, String adminUsersConfig) {
    repository.putTenant(new Tenant(DEFAULT_TENANT_ID, DEFAULT_TENANT_NAME, true));
    var normalizedConfig = adminUsersConfig == null ? "" : adminUsersConfig.trim();
    if (normalizedConfig.isBlank()) {
      seedTenantUser(repository, LOCAL_DEMO_ADMIN_EMAIL, "workos-admin", FoundationRole.TENANT_ADMIN, true);
      return;
    }

    for (String entry : normalizedConfig.split(",")) {
      seedConfiguredAdminEntry(repository, entry.trim());
    }
  }

  public static void seedLocalDemoMember(LocalDemoIdentityRepository repository) {
    seedTenantUser(repository, "member@example.test", null, FoundationRole.TENANT_EMPLOYEE, false);
  }

  private static void seedConfiguredAdminEntry(LocalDemoIdentityRepository repository, String entry) {
    if (entry.isBlank()) {
      return;
    }
    var parts = entry.split(":", -1);
    if (parts.length != 3) {
      throw new IllegalArgumentException("ADMIN_USERS entries must be email:ROLE:scope");
    }
    var email = parts[0].trim().toLowerCase(Locale.ROOT);
    var role = FoundationRole.valueOf(parts[1].trim().toUpperCase(Locale.ROOT));
    var scope = parts[2].trim();
    if (email.isBlank() || !email.contains("@")) {
      throw new IllegalArgumentException("Invalid email in ADMIN_USERS entry");
    }
    if (!role.name().endsWith("_ADMIN")) {
      throw new IllegalArgumentException("ADMIN_USERS roles must be admin roles");
    }
    var scopedMembership = scopedMembership(email, role, scope);
    seedScopedUser(repository, email, null, scopedMembership);
  }

  private static Membership scopedMembership(String email, FoundationRole role, String scope) {
    if (role == FoundationRole.SAAS_OWNER_ADMIN) {
      if (!"OWNER".equalsIgnoreCase(scope)) {
        throw new IllegalArgumentException("SAAS_OWNER_ADMIN scope must be OWNER");
      }
      return membership(email, ScopeType.SAAS_OWNER, null, null, role);
    }
    if (role.defaultScopeType() == ScopeType.TENANT) {
      if (scope.isBlank() || "OWNER".equalsIgnoreCase(scope)) {
        throw new IllegalArgumentException(role + " scope must include a tenant id");
      }
      return membership(email, ScopeType.TENANT, scope, null, role);
    }
    if (role.defaultScopeType() == ScopeType.CUSTOMER) {
      var scopeParts = scope.split("/", -1);
      if (scopeParts.length != 2 || scopeParts[0].isBlank() || scopeParts[1].isBlank()) {
        throw new IllegalArgumentException(role + " scope must be tenant-id/customer-id");
      }
      return membership(email, ScopeType.CUSTOMER, scopeParts[0], scopeParts[1], role);
    }
    throw new IllegalArgumentException("Unsupported ADMIN_USERS role: " + role);
  }

  private static Membership membership(String email, ScopeType scopeType, String tenantId, String customerId, FoundationRole role) {
    return new Membership(
        "membership-" + email,
        email,
        scopeType,
        tenantId,
        customerId,
        List.of(role),
        MembershipStatus.ACTIVE,
        false,
        null);
  }

  private static void seedTenantUser(
      LocalDemoIdentityRepository repository,
      String email,
      String workosSubject,
      FoundationRole role,
      boolean includeAuditorRole) {
    var roles = includeAuditorRole ? List.of(role, FoundationRole.AUDITOR) : List.of(role);
    seedScopedUser(
        repository,
        email,
        workosSubject,
        new Membership(
            "membership-" + email,
            email,
            ScopeType.TENANT,
            DEFAULT_TENANT_ID,
            null,
            roles,
            MembershipStatus.ACTIVE,
            false,
            null));
  }

  private static void seedScopedUser(
      LocalDemoIdentityRepository repository,
      String email,
      String workosSubject,
      Membership membership) {
    repository.saveAccount(new Account(email, workosSubject, email, email, AccountStatus.ACTIVE, workosSubject == null ? "UNLINKED" : "LINKED"));
    repository.putProfile(new UserProfile(email, email, displayName(email), null, null, null));
    repository.putSettings(new UserSettings(email, UserSettings.UiMode.LIGHT));
    repository.putMembership(membership);
  }

  private static String displayName(String email) {
    var localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
    return localPart.replace('.', ' ').replace('-', ' ');
  }
}
