package com.example.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.security.AdminAuditEntry;
import com.example.domain.security.LocalAccount;
import com.example.domain.security.RoleAssignment;
import com.example.domain.security.SecurityRole;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Cross-cutting acceptance coverage for the authenticated DCA seed foundation. */
class DcaSeedSecurityAcceptanceIntegrationTest extends TestKitSupport {

  private static final Instant NOW = Instant.parse("2026-05-01T00:00:00Z");

  record InviteUserRequest(String email, String displayName, List<RoleAssignment> roles) {}

  record UserActionResponse(UserResponse user, String auditId) {}

  record UserResponse(
      String userId,
      String workosUserId,
      String email,
      String displayName,
      String status,
      List<RoleAssignment> roles) {}

  record UpsertTenantRequest(String tenantId, String name) {}

  record TenantActionResponse(TenantResponse tenant, String auditId) {}

  record TenantResponse(String tenantId, String name, boolean active, List<TenantCustomerResponse> customers) {}

  record TenantCustomerResponse(String customerId, String name, boolean active) {}

  record UpsertCustomerRequest(String customerId, String name) {}

  record CustomerActionResponse(CustomerResponse customer, String auditId) {}

  record CustomerResponse(String tenantId, String customerId, String name, boolean active) {}

  record MeResponse(
      String userId,
      String email,
      String displayName,
      String status,
      List<String> roles,
      List<ScopeResponse> scopes,
      List<String> capabilities) {}

  record ScopeResponse(String role, String tenantId, String customerId) {}

  @Test
  void authenticatedSeedFoundationCoversInviteLinkScopeAuditFrontendAndSecretBoundaries()
      throws Exception {
    var bootstrap = new AdminUserBootstrap(componentClient);
    var bootstrapResult =
        bootstrap.bootstrapFrom("seed-acceptance-admin@example.com:APP_ADMIN:ALL", NOW);
    var repeatedBootstrap =
        bootstrap.bootstrapFrom("seed-acceptance-admin@example.com:APP_ADMIN:ALL", NOW.plusSeconds(1));

    assertEquals(1, bootstrapResult.size());
    assertTrue(bootstrapResult.getFirst().created());
    assertFalse(repeatedBootstrap.getFirst().created());
    assertEquals(AdminAuditEntry.AdminAuditAction.BOOTSTRAP_ADMIN, audit(bootstrapResult.getFirst().auditId()).action());

    var appAdminJwt = token("workos-seed-acceptance-admin", "seed-acceptance-admin@example.com", "Seed Admin");
    var appAdminMe = getMe(appAdminJwt).body();
    assertEquals("ACTIVE", appAdminMe.status());
    assertTrue(appAdminMe.roles().contains("APP_ADMIN"));
    assertTrue(appAdminMe.capabilities().contains("ADMIN_USERS"));

    assertRejected(
        () -> httpClient.GET("/api/admin/users?userIds=seed-acceptance-admin@example.com").responseBodyAs(String.class).invoke());

    var tenant = postTenant(appAdminJwt, "tenant-acceptance", "Acceptance Dealer").body();
    assertEquals("tenant-acceptance", tenant.tenant().tenantId());
    assertEquals(AdminAuditEntry.AdminAuditAction.UPSERT_TENANT, audit(tenant.auditId()).action());

    var customer = postCustomer(appAdminJwt, "tenant-acceptance", "customer-acceptance", "Acceptance Customer").body();
    assertEquals("customer-acceptance", customer.customer().customerId());
    assertEquals(AdminAuditEntry.AdminAuditAction.UPSERT_CUSTOMER, audit(customer.auditId()).action());

    var invite =
        invite(
                appAdminJwt,
                "ops-acceptance@example.com",
                "Ops Acceptance",
                List.of(new RoleAssignment(SecurityRole.OPERATIONS_SUPERVISOR, "tenant-acceptance", null)))
            .body();
    assertEquals("INVITED", invite.user().status());
    assertEquals(AdminAuditEntry.AdminAuditAction.INVITE_USER, audit(invite.auditId()).action());

    var opsJwt = token("workos-ops-acceptance", "ops-acceptance@example.com", "Ops Acceptance");
    var opsMe = getMe(opsJwt).body();
    assertEquals("ACTIVE", opsMe.status());
    assertEquals(List.of("OPERATIONS_SUPERVISOR"), opsMe.roles());
    assertEquals("tenant-acceptance", opsMe.scopes().getFirst().tenantId());
    assertTrue(opsMe.capabilities().contains("REVIEW_DECISIONS"));

    assertRejected(
        () ->
            invite(
                opsJwt,
                "bad-platform-admin@example.com",
                "Bad Platform Admin",
                List.of(new RoleAssignment(SecurityRole.APP_ADMIN, null, null))));
    assertRejected(
        () ->
            invite(
                opsJwt,
                "other-tenant-user@example.com",
                "Other Tenant User",
                List.of(new RoleAssignment(SecurityRole.USER, "tenant-other", "customer-other"))));

    var disabled =
        httpClient
            .POST("/api/admin/users/ops-acceptance@example.com/disable")
            .addHeader("Authorization", "Bearer " + appAdminJwt)
            .responseBodyAs(UserActionResponse.class)
            .invoke()
            .body();
    assertEquals("DISABLED", disabled.user().status());
    assertEquals(AdminAuditEntry.AdminAuditAction.DISABLE_USER, audit(disabled.auditId()).action());
    assertRejected(() -> getMe(opsJwt));

    var page = httpClient.GET("/").responseBodyAs(String.class).invoke();
    assertTrue(page.status().isSuccess());
    assertTrue(page.body().contains("AI-first SaaS Workstream Shell"));
    var jsPath = firstMatch(page.body(), "src=\"([^\"]+\\.js)\"");
    var js = httpClient.GET(jsPath).responseBodyAs(String.class).invoke();
    assertTrue(js.body().contains("/api/me"));
    assertTrue(js.body().contains("Authorization"));
    assertTrue(js.body().contains("Backend capabilities remain authorized"));
    assertNoBackendSecrets(page.body() + "\n" + js.body());
  }

  private akka.javasdk.http.StrictResponse<MeResponse> getMe(String jwt) {
    return httpClient.GET("/api/me").addHeader("Authorization", "Bearer " + jwt).responseBodyAs(MeResponse.class).invoke();
  }

  private akka.javasdk.http.StrictResponse<UserActionResponse> invite(
      String jwt, String email, String displayName, List<RoleAssignment> roles) {
    return httpClient
        .POST("/api/admin/users/invite")
        .addHeader("Authorization", "Bearer " + jwt)
        .withRequestBody(new InviteUserRequest(email, displayName, roles))
        .responseBodyAs(UserActionResponse.class)
        .invoke();
  }

  private akka.javasdk.http.StrictResponse<TenantActionResponse> postTenant(String jwt, String tenantId, String name) {
    return httpClient
        .POST("/api/tenants")
        .addHeader("Authorization", "Bearer " + jwt)
        .withRequestBody(new UpsertTenantRequest(tenantId, name))
        .responseBodyAs(TenantActionResponse.class)
        .invoke();
  }

  private akka.javasdk.http.StrictResponse<CustomerActionResponse> postCustomer(
      String jwt, String tenantId, String customerId, String name) {
    return httpClient
        .POST("/api/tenants/" + tenantId + "/customers")
        .addHeader("Authorization", "Bearer " + jwt)
        .withRequestBody(new UpsertCustomerRequest(customerId, name))
        .responseBodyAs(CustomerActionResponse.class)
        .invoke();
  }

  private AdminAuditEntry audit(String auditId) {
    assertNotNull(auditId);
    return componentClient.forKeyValueEntity(auditId).method(AdminAuditEntryEntity::get).invoke();
  }

  private String token(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload =
        Base64.getEncoder()
            .encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }

  private void assertRejected(ThrowingRunnable action) {
    var error = assertThrows(RuntimeException.class, action::run);
    assertTrue(
        error.getMessage().contains("400")
            || error.getMessage().contains("401")
            || error.getMessage().contains("403")
            || error.getMessage().contains("404"),
        error.getMessage());
  }

  private static String firstMatch(String body, String regex) {
    var matcher = Pattern.compile(regex).matcher(body);
    assertTrue(matcher.find(), "Expected match for " + regex + " in " + body);
    return matcher.group(1);
  }

  private static void assertNoBackendSecrets(String content) {
    assertFalse(content.contains("WORKOS_API_KEY"));
    assertFalse(content.contains("RESEND_API_KEY"));
    assertFalse(content.contains("ADMIN_USERS="));
    assertFalse(content.contains("INVITE_EMAIL_FROM"));
    assertFalse(content.contains("sk_test_"));
    assertFalse(content.contains("sk_live_"));
    assertFalse(content.contains("re_x"));
  }

  @FunctionalInterface
  interface ThrowingRunnable {
    void run() throws Exception;
  }
}
