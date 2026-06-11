package ai.first.application.foundation.identity;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.List;
import java.util.Locale;

/** Seeds explicitly configured first-admin accounts without allowing open self-registration. */
public final class BootstrapAdminSeeder {
  public static final String DEFAULT_TENANT_ID = "tenant-starter";
  public static final String DEFAULT_TENANT_NAME = "Starter Tenant";
  private BootstrapAdminSeeder() {}

  /**
   * Production/default startup bootstrap. ADMIN_USERS is intentionally limited to SaaS Owner
   * authority; Tenant and Customer scoped admins must be created later through governed
   * Organization/Invitation flows after a valid scope exists.
   */
  public static void seedConfiguredAdmins(IdentityRepository repository, String adminUsersConfig) {
    var normalizedConfig = adminUsersConfig == null ? "" : adminUsersConfig.trim();
    if (normalizedConfig.isBlank()) {
      return;
    }

    for (String entry : normalizedConfig.split(",")) {
      seedConfiguredSaasOwnerAdminEntry(repository, entry.trim());
    }
  }

  /** Explicit local/test fixture seeding for tests and demos; never use for production startup. */
  public static void seedFixtureAdmins(IdentityRepository repository, String adminUsersConfig) {
    repository.saveTenant(new Tenant(DEFAULT_TENANT_ID, DEFAULT_TENANT_NAME, true));
    var normalizedConfig = adminUsersConfig == null ? "" : adminUsersConfig.trim();
    if (normalizedConfig.isBlank()) {
      return;
    }

    for (String entry : normalizedConfig.split(",")) {
      seedFixtureAdminEntry(repository, entry.trim());
    }
  }

  private static void seedConfiguredSaasOwnerAdminEntry(IdentityRepository repository, String entry) {
    var parsed = parseEntry(entry);
    if (parsed == null) {
      return;
    }
    if (parsed.role() != FoundationRole.SAAS_OWNER_ADMIN) {
      throw new IllegalArgumentException("ADMIN_USERS production bootstrap supports only SAAS_OWNER_ADMIN; create Tenant/Customer admins through invitation flows");
    }
    if (!"OWNER".equalsIgnoreCase(parsed.scope())) {
      throw new IllegalArgumentException("SAAS_OWNER_ADMIN scope must be OWNER");
    }
    seedScopedUser(repository, parsed.email(), null, membership(parsed.email(), ScopeType.SAAS_OWNER, null, null, parsed.role()));
  }

  private static void seedFixtureAdminEntry(IdentityRepository repository, String entry) {
    var parsed = parseEntry(entry);
    if (parsed == null) {
      return;
    }
    var scopedMembership = fixtureScopedMembership(parsed.email(), parsed.role(), parsed.scope());
    seedScopedUser(repository, parsed.email(), null, scopedMembership);
  }

  private static ParsedAdminEntry parseEntry(String entry) {
    if (entry.isBlank()) {
      return null;
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
    return new ParsedAdminEntry(email, role, scope);
  }

  private static Membership fixtureScopedMembership(String email, FoundationRole role, String scope) {
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
    throw new IllegalArgumentException("Unsupported ADMIN_USERS fixture role: " + role);
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

  private record ParsedAdminEntry(String email, FoundationRole role, String scope) {}

  private static String displayName(String email) {
    var localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
    return localPart.replace('.', ' ').replace('-', ' ');
  }
}
