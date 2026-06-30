package ai.first.api.foundation.security;

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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import static akka.javasdk.http.HttpException.forbidden;

/** Local-only passwordless test identity endpoint for runtime validation and manual role testing. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/api/dev/auth")
public class LocalDevAuthEndpoint extends AbstractHttpEndpoint {
  private static final String BASE_TENANT_ID = BootstrapAdminSeeder.DEFAULT_TENANT_ID;
  private static final String BASE_TENANT_NAME = BootstrapAdminSeeder.DEFAULT_TENANT_NAME;
  private static final String CUSTOMER_ID = "customer-starter";
  private static final String CUSTOMER_NAME = "Starter Customer";
  private final AkkaIdentityRepository identityRepository;

  public LocalDevAuthEndpoint(ComponentClient componentClient) {
    this.identityRepository = new AkkaIdentityRepository(componentClient);
  }

  @Get("/users")
  public Map<String, Object> users() {
    requireLocalDev();
    seedCatalogIfEnabled();
    return Map.of(
        "authMode", "local-dev",
        "users", identityRepository.membershipRows().stream()
            .map(Membership::accountId)
            .distinct()
            .map(accountId -> identityRepository.findAccountByEmail(accountId).orElse(null))
            .filter(account -> account != null && (account.normalizedEmail().endsWith("@example.test") || account.normalizedEmail().endsWith("@example.com")))
            .map(account -> Map.of(
                "email", account.normalizedEmail(),
                "displayName", account.displayEmail(),
                "status", account.status().name(),
                "memberships", identityRepository.membershipsByAccount(account.accountId()).stream().map(this::membershipSummary).toList()))
            .toList(),
        "redaction", "Local-dev browser-safe identity catalog; no provider token, raw JWT, WorkOS secret, or backend secret is returned.");
  }

  @Post("/sign-in")
  public Map<String, Object> signIn(SignInRequest request) {
    requireLocalDev();
    seedCatalogIfEnabled();
    var email = normalizeEmail(request == null ? null : request.email());
    if (email == null) throw forbidden("local-dev-email-required");
    var account = identityRepository.findAccountByEmail(email).orElseThrow(() -> forbidden("local-dev-user-not-seeded"));
    var token = unsignedLocalDevJwt(account.normalizedEmail(), account.displayEmail());
    identityRepository.appendAudit(new AdminAuditEvent(
        "audit-local-dev-sign-in-" + UUID.randomUUID(),
        Instant.now(),
        correlationId(),
        "local-dev-auth",
        null,
        null,
        null,
        null,
        account.accountId(),
        null,
        "LOCAL_DEV_PASSWORDLESS_SIGN_IN",
        AdminAuditEvent.Result.ALLOWED,
        "local-dev-auth-mode",
        "Issued a local-dev runtime-validation identity token for a seeded test email; authorization remains backend membership/capability based.",
        "BROWSER_SAFE"));
    return Map.of(
        "accessToken", token,
        "tokenType", "Bearer",
        "email", account.normalizedEmail(),
        "expiresInSeconds", 8 * 60 * 60,
        "authMode", "local-dev",
        "correlationId", correlationId(),
        "redaction", "Token is returned only to the local browser/dev script in APP_AUTH_MODE=local-dev; never commit or paste it into evidence.");
  }

  private void seedCatalogIfEnabled() {
    if (!"true".equalsIgnoreCase(config("LOCAL_DEV_AUTH_AUTO_SEED", "true"))) return;
    identityRepository.saveTenant(new Tenant(BASE_TENANT_ID, BASE_TENANT_NAME, true));
    identityRepository.saveCustomer(new Customer(BASE_TENANT_ID, CUSTOMER_ID, CUSTOMER_NAME, true));
    seedAccount("saas.admin@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.SAAS_OWNER, null, null, FoundationRole.SAAS_OWNER_ADMIN, false);
    seedAccount("org1.admin1@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_ADMIN, false);
    seedAccount("org1.user3@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_EMPLOYEE, false);
    seedAccount("cust1.admin@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.CUSTOMER, BASE_TENANT_ID, CUSTOMER_ID, FoundationRole.CUSTOMER_ADMIN, false);
    seedAccount("cust1.user2@example.test", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, ScopeType.CUSTOMER, BASE_TENANT_ID, CUSTOMER_ID, FoundationRole.CUSTOMER_USER, false);
    seedAccount("disabled.user@example.test", AccountStatus.DISABLED, MembershipStatus.ACTIVE, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_EMPLOYEE, false);
    seedAccount("inactive.user@example.test", AccountStatus.ACTIVE, MembershipStatus.SUSPENDED, ScopeType.TENANT, BASE_TENANT_ID, null, FoundationRole.TENANT_EMPLOYEE, false);
  }

  private void seedAccount(String email, AccountStatus accountStatus, MembershipStatus membershipStatus, ScopeType scopeType, String tenantId, String customerId, FoundationRole role, boolean supportAccess) {
    var existing = identityRepository.findAccountByEmail(email).orElse(null);
    var workosSubject = existing == null || isLocalDevSubject(existing.workosUserId()) ? null : existing.workosUserId();
    identityRepository.saveAccount(new Account(email, workosSubject, email, email, accountStatus, workosSubject == null ? "UNLINKED" : "LINKED"));
    if (identityRepository.profile(email) == null) identityRepository.saveProfile(new UserProfile(email, email, displayName(email), null, null, null));
    if (identityRepository.settings(email) == null) identityRepository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.saveMembership(new Membership("membership-" + email, email, scopeType, tenantId, customerId, List.of(role), membershipStatus, supportAccess, null));
  }

  private Map<String, Object> membershipSummary(Membership membership) {
    return Map.of(
        "membershipId", membership.membershipId(),
        "scopeType", membership.scopeType().name(),
        "tenantId", membership.tenantId() == null ? "" : membership.tenantId(),
        "customerId", membership.customerId() == null ? "" : membership.customerId(),
        "status", membership.status().name(),
        "roles", membership.roles().stream().map(Enum::name).toList());
  }

  private String unsignedLocalDevJwt(String email, String displayName) {
    var now = Instant.now().getEpochSecond();
    var header = base64Url("{\"alg\":\"none\",\"typ\":\"JWT\"}");
    var payload = base64Url("{"
        + "\"iss\":\"local-dev-runtime-validation\","
        + "\"sub\":\"local-dev:" + json(email) + "\","
        + "\"email\":\"" + json(email) + "\","
        + "\"name\":\"" + json(displayName) + "\","
        + "\"iat\":" + now + ","
        + "\"exp\":" + (now + 8 * 60 * 60)
        + "}");
    return header + "." + payload + ".";
  }

  private void requireLocalDev() {
    if (!"local-dev".equalsIgnoreCase(config("APP_AUTH_MODE", "workos"))) throw forbidden("local-dev-auth-disabled");
    if (isDeployedEnvironment()) throw forbidden("local-dev-auth-deployed-environment-denied");
  }

  private boolean isDeployedEnvironment() {
    var env = config("APP_ENV", "local").toLowerCase(Locale.ROOT);
    return env.equals("prod") || env.equals("production") || env.equals("staging") || "true".equalsIgnoreCase(config("AKKA_PRODUCTION", "false"));
  }

  private String correlationId() {
    return requestContext().requestHeader("X-Correlation-Id").map(header -> header.value()).orElse("local-dev-auth");
  }

  private static String normalizeEmail(String email) {
    if (email == null) return null;
    var normalized = email.trim().toLowerCase(Locale.ROOT);
    return normalized.contains("@") ? normalized : null;
  }

  private static String config(String name, String fallback) {
    var value = System.getProperty(name);
    if (value == null || value.isBlank()) value = System.getenv(name);
    return value == null || value.isBlank() ? fallback : value.trim();
  }

  private static boolean isLocalDevSubject(String subject) {
    return subject != null && subject.startsWith("local-dev:");
  }

  private static String base64Url(String value) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }

  private static String json(String value) {
    return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  private static String displayName(String email) {
    var localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
    return java.util.Arrays.stream(localPart.split("[.-]"))
        .filter(part -> !part.isBlank())
        .map(part -> part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1))
        .reduce((left, right) -> left + " " + right)
        .orElse(email);
  }

  public record SignInRequest(String email) {}
}
