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
import com.example.domain.security.UserProfile;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AdminEndpointsIntegrationTest extends TestKitSupport {

  private static final Instant NOW = Instant.parse("2026-05-01T00:00:00Z");

  record InviteUserRequest(String email, String displayName, List<RoleAssignment> roles) {}

  record ReplaceRolesRequest(List<RoleAssignment> roles) {}

  record UserActionResponse(UserResponse user, String auditId) {}

  record UserResponse(
      String userId,
      String workosUserId,
      String email,
      String displayName,
      String status,
      List<RoleAssignment> roles) {}

  record UsersResponse(List<UserResponse> users) {}

  record UpsertTenantRequest(String tenantId, String name) {}

  record TenantActionResponse(TenantResponse tenant, String auditId) {}

  record TenantResponse(String tenantId, String name, boolean active, List<CustomerResponse> customers) {}

  record UpsertCustomerRequest(String customerId, String name) {}

  record CustomerActionResponse(CustomerResponse customer, String auditId) {}

  record CustomerResponse(String tenantId, String customerId, String name, boolean active) {}

  @Test
  void bootstrapCreatesInitialAdminsIdempotentlyWithAudit() {
    var bootstrap = new AdminUserBootstrap(componentClient);

    var first =
        bootstrap.bootstrapFrom(
            "seed-admin@example.com:APP_ADMIN:ALL,owner@example.com:DEALER_OWNER:tenant-1",
            NOW);
    var second =
        bootstrap.bootstrapFrom(
            "seed-admin@example.com:APP_ADMIN:ALL,owner@example.com:DEALER_OWNER:tenant-1",
            NOW.plusSeconds(60));

    assertEquals(2, first.size());
    assertTrue(first.stream().allMatch(AdminUserBootstrap.BootstrapResult::created));
    assertTrue(first.stream().allMatch(result -> result.auditId() != null && !result.auditId().isBlank()));
    assertEquals(2, second.size());
    assertTrue(second.stream().noneMatch(AdminUserBootstrap.BootstrapResult::created));
    assertTrue(second.stream().allMatch(result -> result.auditId() == null));
    assertTrue(account("seed-admin@example.com").isAppAdmin());
    assertTrue(account("owner@example.com").roles().stream().anyMatch(role -> role.tenantId().equals("tenant-1")));

    var audit = audit(first.getFirst().auditId());
    assertEquals(AdminAuditEntry.AdminAuditAction.BOOTSTRAP_ADMIN, audit.action());
    assertEquals("system", audit.actorUserId());
  }

  @Test
  void appAdminInvitesAssignsStatusAndManagesTenantCustomerWithAudit() throws Exception {
    activeAccount("app-admin@example.com", "workos-app", new RoleAssignment(SecurityRole.APP_ADMIN, null, null));
    var appAdminToken = token("workos-app", "app-admin@example.com", "App Admin");

    var tenant =
        httpClient
            .POST("/api/tenants")
            .addHeader("Authorization", "Bearer " + appAdminToken)
            .withRequestBody(new UpsertTenantRequest("tenant-a", "Dealer A"))
            .responseBodyAs(TenantActionResponse.class)
            .invoke();
    assertTrue(tenant.status().isSuccess());
    assertEquals("tenant-a", tenant.body().tenant().tenantId());
    assertEquals(AdminAuditEntry.AdminAuditAction.UPSERT_TENANT, audit(tenant.body().auditId()).action());

    var invite =
        httpClient
            .POST("/api/admin/users/invite")
            .addHeader("Authorization", "Bearer " + appAdminToken)
            .withRequestBody(
                new InviteUserRequest(
                    "ops-a@example.com",
                    "Ops A",
                    List.of(new RoleAssignment(SecurityRole.OPERATIONS_SUPERVISOR, "tenant-a", null))))
            .responseBodyAs(UserActionResponse.class)
            .invoke();
    assertTrue(invite.status().isSuccess());
    assertEquals("INVITED", invite.body().user().status());
    var inviteAudit = audit(invite.body().auditId());
    assertEquals(AdminAuditEntry.AdminAuditAction.INVITE_USER, inviteAudit.action());
    var inviteEmailStatus = inviteAudit.details().get("inviteEmailStatus");
    assertTrue(List.of("SENT", "FAILED").contains(inviteEmailStatus));
    if ("FAILED".equals(inviteEmailStatus)) {
      assertFalse(inviteAudit.details().get("inviteEmailReason").isBlank());
    }

    var replace =
        httpClient
            .POST("/api/admin/users/ops-a@example.com/roles")
            .addHeader("Authorization", "Bearer " + appAdminToken)
            .withRequestBody(
                new ReplaceRolesRequest(
                    List.of(new RoleAssignment(SecurityRole.CUSTOMER_ADMIN, "tenant-a", "customer-a"))))
            .responseBodyAs(UserActionResponse.class)
            .invoke();
    assertTrue(replace.status().isSuccess());
    assertEquals(SecurityRole.CUSTOMER_ADMIN, replace.body().user().roles().getFirst().role());

    var disabled =
        httpClient
            .POST("/api/admin/users/ops-a@example.com/disable")
            .addHeader("Authorization", "Bearer " + appAdminToken)
            .responseBodyAs(UserActionResponse.class)
            .invoke();
    assertEquals("DISABLED", disabled.body().user().status());

    var activated =
        httpClient
            .POST("/api/admin/users/ops-a@example.com/activate")
            .addHeader("Authorization", "Bearer " + appAdminToken)
            .responseBodyAs(UserActionResponse.class)
            .invoke();
    assertEquals("ACTIVE", activated.body().user().status());

    var customer =
        httpClient
            .POST("/api/tenants/tenant-a/customers")
            .addHeader("Authorization", "Bearer " + appAdminToken)
            .withRequestBody(new UpsertCustomerRequest("customer-a", "Customer A"))
            .responseBodyAs(CustomerActionResponse.class)
            .invoke();
    assertTrue(customer.status().isSuccess());
    assertEquals("customer-a", customer.body().customer().customerId());

    var list =
        httpClient
            .GET("/api/admin/users?userIds=ops-a@example.com&tenantId=tenant-a&customerId=customer-a")
            .addHeader("Authorization", "Bearer " + appAdminToken)
            .responseBodyAs(UsersResponse.class)
            .invoke();
    assertEquals(1, list.body().users().size());
  }

  @Test
  void scopedAdminsCannotEscalateOrCrossScopes() throws Exception {
    activeAccount("tenant-admin@example.com", "workos-tenant", new RoleAssignment(SecurityRole.DEALER_OWNER, "tenant-1", null));
    activeAccount("customer-admin@example.com", "workos-customer", new RoleAssignment(SecurityRole.CUSTOMER_ADMIN, "tenant-1", "customer-1"));
    activeAccount("app-admin-2@example.com", "workos-app-2", new RoleAssignment(SecurityRole.APP_ADMIN, null, null));
    var appAdminToken = token("workos-app-2", "app-admin-2@example.com", "App Admin 2");
    postTenant(appAdminToken, "tenant-1", "Tenant 1");
    postTenant(appAdminToken, "tenant-2", "Tenant 2");
    postCustomer(appAdminToken, "tenant-1", "customer-1", "Customer 1");
    postCustomer(appAdminToken, "tenant-1", "customer-2", "Customer 2");

    var tenantToken = token("workos-tenant", "tenant-admin@example.com", "Tenant Admin");
    assertForbidden(
        () ->
            httpClient
                .POST("/api/admin/users/invite")
                .addHeader("Authorization", "Bearer " + tenantToken)
                .withRequestBody(
                    new InviteUserRequest(
                        "bad-admin@example.com",
                        "Bad Admin",
                        List.of(new RoleAssignment(SecurityRole.APP_ADMIN, null, null))))
                .responseBodyAs(UserActionResponse.class)
                .invoke());
    assertForbidden(
        () ->
            httpClient
                .POST("/api/admin/users/invite")
                .addHeader("Authorization", "Bearer " + tenantToken)
                .withRequestBody(
                    new InviteUserRequest(
                        "other-tenant@example.com",
                        "Other Tenant",
                        List.of(new RoleAssignment(SecurityRole.OPERATIONS_SUPERVISOR, "tenant-2", null))))
                .responseBodyAs(UserActionResponse.class)
                .invoke());

    var scopedInvite =
        httpClient
            .POST("/api/admin/users/invite")
            .addHeader("Authorization", "Bearer " + tenantToken)
            .withRequestBody(
                new InviteUserRequest(
                    "tenant-user@example.com",
                    "Tenant User",
                    List.of(new RoleAssignment(SecurityRole.OPERATIONS_SUPERVISOR, "tenant-1", null))))
            .responseBodyAs(UserActionResponse.class)
            .invoke();
    assertTrue(scopedInvite.status().isSuccess());

    var customerToken = token("workos-customer", "customer-admin@example.com", "Customer Admin");
    assertForbidden(
        () ->
            httpClient
                .POST("/api/tenants/tenant-1/customers")
                .addHeader("Authorization", "Bearer " + customerToken)
                .withRequestBody(new UpsertCustomerRequest("customer-2", "Customer 2 updated"))
                .responseBodyAs(CustomerActionResponse.class)
                .invoke());
  }

  @Test
  void disabledUsersAndMissingJwtCannotUseAdminApis() throws Exception {
    activeAccount("disabled-admin@example.com", "workos-disabled-admin", new RoleAssignment(SecurityRole.APP_ADMIN, null, null));
    componentClient
        .forKeyValueEntity("disabled-admin@example.com")
        .method(LocalAccountEntity::disable)
        .invoke(new LocalAccount.Command.Disable(NOW.plusSeconds(20)));

    assertForbidden(
        () ->
            httpClient
                .GET("/api/admin/users?userIds=disabled-admin@example.com")
                .addHeader("Authorization", "Bearer " + token("workos-disabled-admin", "disabled-admin@example.com", "Disabled"))
                .responseBodyAs(UsersResponse.class)
                .invoke());

    assertForbidden(
        () ->
            httpClient
                .GET("/api/admin/users?userIds=disabled-admin@example.com")
                .responseBodyAs(UsersResponse.class)
                .invoke());
  }

  private void postTenant(String jwt, String tenantId, String name) {
    httpClient
        .POST("/api/tenants")
        .addHeader("Authorization", "Bearer " + jwt)
        .withRequestBody(new UpsertTenantRequest(tenantId, name))
        .responseBodyAs(TenantActionResponse.class)
        .invoke();
  }

  private void postCustomer(String jwt, String tenantId, String customerId, String name) {
    httpClient
        .POST("/api/tenants/" + tenantId + "/customers")
        .addHeader("Authorization", "Bearer " + jwt)
        .withRequestBody(new UpsertCustomerRequest(customerId, name))
        .responseBodyAs(CustomerActionResponse.class)
        .invoke();
  }

  private void activeAccount(String email, String workosUserId, RoleAssignment role) {
    componentClient
        .forKeyValueEntity(email)
        .method(LocalAccountEntity::invite)
        .invoke(new LocalAccount.Command.Invite(email, new UserProfile(email.substring(0, email.indexOf('@')), email), List.of(role), NOW));
    componentClient
        .forKeyValueEntity(email)
        .method(LocalAccountEntity::linkAndActivate)
        .invoke(new LocalAccount.Command.LinkAndActivate(workosUserId, NOW.plusSeconds(1)));
  }

  private LocalAccount.State account(String userId) {
    return componentClient.forKeyValueEntity(userId).method(LocalAccountEntity::get).invoke();
  }

  private AdminAuditEntry audit(String auditId) {
    assertNotNull(auditId);
    return componentClient.forKeyValueEntity(auditId).method(AdminAuditEntryEntity::get).invoke();
  }

  private String token(String subject, String email, String name) throws Exception {
    return bearerTokenWith(Map.of("sub", subject, "email", email, "name", name));
  }

  private String bearerTokenWith(Map<String, String> claims) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(claims));
    return header + "." + payload;
  }

  private void assertForbidden(ThrowingRunnable action) {
    var error = assertThrows(RuntimeException.class, action::run);
    assertTrue(
        error.getMessage().contains("400")
            || error.getMessage().contains("401")
            || error.getMessage().contains("403")
            || error.getMessage().contains("404"),
        error.getMessage());
  }

  @FunctionalInterface
  interface ThrowingRunnable {
    void run() throws Exception;
  }
}
