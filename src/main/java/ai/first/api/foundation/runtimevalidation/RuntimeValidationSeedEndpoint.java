package ai.first.api.foundation.runtimevalidation;

import ai.first.application.foundation.identity.AkkaIdentityRepository;
import ai.first.application.foundation.identity.BootstrapAdminSeeder;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static akka.javasdk.http.HttpException.forbidden;

/** Local-only runtime-validation setup endpoint used by tools/runtime-validation/seed.sh. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/internal/runtime-validation")
public class RuntimeValidationSeedEndpoint extends AbstractHttpEndpoint {
  private static final String BASE_TENANT_ID = BootstrapAdminSeeder.DEFAULT_TENANT_ID;
  private static final String BASE_TENANT_NAME = BootstrapAdminSeeder.DEFAULT_TENANT_NAME;
  private static final String BASE_CUSTOMER_ID = "customer-starter";
  private static final String BASE_CUSTOMER_NAME = "Starter Customer";
  private final AkkaIdentityRepository identityRepository;

  public RuntimeValidationSeedEndpoint(ComponentClient componentClient) {
    this.identityRepository = new AkkaIdentityRepository(componentClient);
  }

  @Get("/setup/base-organization")
  public Map<String, Object> baseOrganizationStatus() {
    requireEnabledAndAuthorized();
    return response("status", "base-organization-status", "runtime-validation-status");
  }

  @Post("/seed/base-organization")
  public Map<String, Object> seedBaseOrganization() {
    requireEnabledAndAuthorized();
    identityRepository.saveTenant(new Tenant(BASE_TENANT_ID, BASE_TENANT_NAME, true));
    identityRepository.saveCustomer(new Customer(BASE_TENANT_ID, BASE_CUSTOMER_ID, BASE_CUSTOMER_NAME, true));
    seedAccount("member@example.com", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_EMPLOYEE, false);
    seedAccount("org.admin@example.com", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_ADMIN, false);
    seedAccount("saas.admin@example.com", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.SAAS_OWNER, null, null, FoundationRole.SAAS_OWNER_ADMIN, false);
    seedAccount("support.operator@example.com", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_ADMIN, true);
    seedAccount("org1.admin1@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_ADMIN, false);
    seedAccount("org1.user3@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_EMPLOYEE, false);
    seedAccount("cust1.admin@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.CUSTOMER, BASE_TENANT_ID, BASE_CUSTOMER_ID, FoundationRole.CUSTOMER_ADMIN, false);
    seedAccount("cust1.user2@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.CUSTOMER, BASE_TENANT_ID, BASE_CUSTOMER_ID, FoundationRole.CUSTOMER_USER, false);
    seedAccount("disabled.member@example.com", AccountStatus.DISABLED, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_EMPLOYEE, false);
    seedAccount("inactive.member@example.com", AccountStatus.ACTIVE, MembershipStatus.SUSPENDED, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_EMPLOYEE, false);
    identityRepository.appendAudit(new AdminAuditEvent(
        "audit-runtime-validation-seed-" + UUID.randomUUID(),
        Instant.now(),
        correlationId(),
        "runtime-validation-seed",
        null,
        ScopeType.SAAS_OWNER,
        BASE_TENANT_ID,
        null,
        null,
        null,
        "RUNTIME_VALIDATION_BASE_ORGANIZATION_SEED",
        AdminAuditEvent.Result.ALLOWED,
        "seeded-base-organization",
        "Seeded base organization personas for local runtime validation; no scenario behavior was executed.",
        "BROWSER_SAFE"));
    return response("seeded", "base-organization", "runtime-validation-base-organization-seed");
  }

  private void seedAccount(String email, AccountStatus accountStatus, MembershipStatus membershipStatus, ScopeType scopeType, String tenantId, String customerId, FoundationRole role, boolean supportAccess) {
    var normalizedEmail = email.toLowerCase(java.util.Locale.ROOT);
    var existing = identityRepository.findAccountByEmail(normalizedEmail).orElse(null);
    var workosSubject = existing == null || (existing.workosUserId() != null && existing.workosUserId().startsWith("local-dev:")) ? null : existing.workosUserId();
    identityRepository.saveAccount(new Account(normalizedEmail, workosSubject, normalizedEmail, normalizedEmail, accountStatus, workosSubject == null ? "UNLINKED" : "LINKED"));
    if (identityRepository.profile(normalizedEmail) == null) {
      identityRepository.saveProfile(new UserProfile(normalizedEmail, normalizedEmail, displayName(normalizedEmail), null, null, null));
    }
    if (identityRepository.settings(normalizedEmail) == null) {
      identityRepository.saveSettings(new UserSettings(normalizedEmail, UserSettings.ThemeId.AURORA_LIGHT));
    }
    identityRepository.saveMembership(new Membership("membership-" + normalizedEmail, normalizedEmail, scopeType, tenantId, customerId, List.of(role), membershipStatus, supportAccess, null));
  }

  private Map<String, Object> response(String result, String setupId, String traceId) {
    var tenantPresent = identityRepository.tenant(BASE_TENANT_ID).isPresent();
    var memberships = identityRepository.membershipRows().stream()
        .filter(membership -> BASE_TENANT_ID.equals(membership.tenantId()) || membership.scopeType() == ScopeType.SAAS_OWNER)
        .map(membership -> Map.of(
            "membershipId", membership.membershipId(),
            "accountId", membership.accountId(),
            "scopeType", membership.scopeType().name(),
            "tenantId", membership.tenantId() == null ? "" : membership.tenantId(),
            "customerId", membership.customerId() == null ? "" : membership.customerId(),
            "status", membership.status().name(),
            "supportAccess", membership.supportAccess(),
            "roles", membership.roles().stream().map(Enum::name).toList()))
        .toList();
    return Map.of(
        "result", result,
        "setupId", setupId,
        "tenant", Map.of("tenantId", BASE_TENANT_ID, "displayName", BASE_TENANT_NAME, "present", tenantPresent),
        "personas", memberships,
        "disabledFixture", "disabled.member@example.com",
        "inactiveFixture", "inactive.member@example.com",
        "authMapping", "Accounts are seeded by normalized email. In WorkOS mode they link to the WorkOS subject on first valid AuthKit login; in APP_AUTH_MODE=local-dev use /api/dev/auth/sign-in with a seeded email to obtain a local bearer token. No raw provider secret is returned.",
        "traceRefs", List.of(traceId),
        "correlationId", correlationId(),
        "redaction", "Browser-safe setup metadata only; no WorkOS API key, JWT, Resend key, model key, or invitation token is exposed.");
  }

  private void requireEnabledAndAuthorized() {
    if (!"true".equalsIgnoreCase(config("RUNTIME_VALIDATION_SEED_ENABLED"))) {
      throw forbidden("runtime-validation-seed-disabled");
    }
    var expectedToken = config("RUNTIME_VALIDATION_SEED_TOKEN");
    if (expectedToken == null || expectedToken.isBlank()) {
      throw forbidden("runtime-validation-seed-token-not-configured");
    }
    var actualToken = requestContext().requestHeader("X-Runtime-Validation-Seed-Token").map(header -> header.value()).orElse("");
    if (!constantTimeEquals(expectedToken, actualToken)) {
      throw forbidden("runtime-validation-seed-token-invalid");
    }
  }

  private String correlationId() {
    return requestContext().requestHeader("X-Correlation-Id").map(header -> header.value()).orElse("rv-seed-base-organization");
  }

  private static String config(String name) {
    var value = System.getProperty(name);
    if (value == null || value.isBlank()) value = System.getenv(name);
    return value == null ? null : value.trim();
  }

  private static boolean constantTimeEquals(String expected, String actual) {
    if (expected == null || actual == null) return false;
    var diff = expected.length() ^ actual.length();
    for (int i = 0; i < Math.min(expected.length(), actual.length()); i++) {
      diff |= expected.charAt(i) ^ actual.charAt(i);
    }
    return diff == 0;
  }

  private static String displayName(String email) {
    var localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
    return java.util.Arrays.stream(localPart.split("[.-]"))
        .filter(part -> !part.isBlank())
        .map(part -> part.substring(0, 1).toUpperCase(java.util.Locale.ROOT) + part.substring(1))
        .reduce((left, right) -> left + " " + right)
        .orElse(email);
  }
}
