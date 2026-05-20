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

  public static void seedConfiguredAdmins(InMemoryIdentityRepository repository, String adminUsersConfig) {
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

  public static void seedLocalDemoMember(InMemoryIdentityRepository repository) {
    seedTenantUser(repository, "member@example.test", null, FoundationRole.TENANT_EMPLOYEE, false);
  }

  private static void seedConfiguredAdminEntry(InMemoryIdentityRepository repository, String entry) {
    if (entry.isBlank()) {
      return;
    }
    var parts = entry.split(":");
    if (parts.length < 3) {
      throw new IllegalArgumentException("ADMIN_USERS entries must be email:ROLE:scope");
    }
    var email = parts[0].trim().toLowerCase(Locale.ROOT);
    var role = FoundationRole.valueOf(parts[1].trim());
    var scope = parts[2].trim();
    if (email.isBlank() || role != FoundationRole.TENANT_ADMIN) {
      throw new IllegalArgumentException("Starter bootstrap only supports explicitly configured TENANT_ADMIN emails");
    }
    if (!DEFAULT_TENANT_ID.equals(scope)) {
      throw new IllegalArgumentException("Starter bootstrap scope must be " + DEFAULT_TENANT_ID);
    }
    seedTenantUser(repository, email, null, role, false);
  }

  private static void seedTenantUser(
      InMemoryIdentityRepository repository,
      String email,
      String workosSubject,
      FoundationRole role,
      boolean includeAuditorRole) {
    repository.saveAccount(new Account(email, workosSubject, email, email, AccountStatus.ACTIVE, workosSubject == null ? "UNLINKED" : "LINKED"));
    repository.putProfile(new UserProfile(email, email, displayName(email), null, null, null));
    repository.putSettings(new UserSettings(email, UserSettings.UiMode.LIGHT));
    var roles = includeAuditorRole ? List.of(role, FoundationRole.AUDITOR) : List.of(role);
    repository.putMembership(
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

  private static String displayName(String email) {
    var localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
    return localPart.replace('.', ' ').replace('-', ' ');
  }
}
