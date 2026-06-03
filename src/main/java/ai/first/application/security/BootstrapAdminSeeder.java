package ai.first.application.security;

import ai.first.domain.security.Account;
import ai.first.domain.security.AccountStatus;
import ai.first.domain.security.FoundationRole;
import ai.first.domain.security.Membership;
import ai.first.domain.security.MembershipStatus;
import ai.first.domain.security.ScopeType;
import ai.first.domain.security.Tenant;
import ai.first.domain.security.UserProfile;
import ai.first.domain.security.UserSettings;
import java.util.List;
import java.util.Locale;

/** Seeds explicitly configured first-admin accounts without allowing open self-registration. */
public final class BootstrapAdminSeeder {
  public static final String DEFAULT_TENANT_ID = "tenant-starter";
  public static final String DEFAULT_TENANT_NAME = "Starter Tenant";
  private BootstrapAdminSeeder() {}

  public static void seedConfiguredAdmins(IdentityRepository repository, String adminUsersConfig) {
    repository.saveTenant(new Tenant(DEFAULT_TENANT_ID, DEFAULT_TENANT_NAME, true));
    var normalizedConfig = adminUsersConfig == null ? "" : adminUsersConfig.trim();
    if (normalizedConfig.isBlank()) {
      return;
    }

    for (String entry : normalizedConfig.split(",")) {
      seedConfiguredAdminEntry(repository, entry.trim());
    }
  }

  private static void seedConfiguredAdminEntry(IdentityRepository repository, String entry) {
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
      IdentityRepository repository,
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
      IdentityRepository repository,
      String email,
      String workosSubject,
      Membership membership) {
    var existingAccount = repository.findAccountByEmail(email).orElse(null);
    var nextWorkosSubject = workosSubject != null ? workosSubject : existingAccount == null ? null : existingAccount.workosUserId();
    repository.saveAccount(new Account(email, nextWorkosSubject, email, email, AccountStatus.ACTIVE, nextWorkosSubject == null ? "UNLINKED" : "LINKED"));
    if (repository.profile(email) == null) {
      repository.saveProfile(new UserProfile(email, email, displayName(email), null, null, null));
    }
    if (repository.settings(email) == null) {
      repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    }
    repository.saveMembership(membership);
  }

  private static String displayName(String email) {
    var localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
    return localPart.replace('.', ' ').replace('-', ' ');
  }
}
