package com.example.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.security.AccountStatus;
import com.example.domain.security.LocalAccount;
import com.example.domain.security.RoleAssignment;
import com.example.domain.security.SecurityRole;
import com.example.domain.security.UserProfile;
import com.example.security.AuthContext;
import com.example.security.AuthorizationService;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MeEndpointIntegrationTest extends TestKitSupport {

  private static final Instant NOW = Instant.parse("2026-05-01T00:00:00Z");

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
  void invitedWorkosUserLinksActivatesAndReceivesBrowserSafeMeResponse() throws Exception {
    invite(
        "ops@example.com",
        "Ops Supervisor",
        List.of(new RoleAssignment(SecurityRole.OPERATIONS_SUPERVISOR, "tenant-1", null)));

    var response = getMe(token("workos-ops-1", "ops@example.com", "Ops Supervisor"));

    assertTrue(response.status().isSuccess());
    assertEquals("ops@example.com", response.body().userId());
    assertEquals("ops@example.com", response.body().email());
    assertEquals("Ops Supervisor", response.body().displayName());
    assertEquals("ACTIVE", response.body().status());
    assertEquals(List.of("OPERATIONS_SUPERVISOR"), response.body().roles());
    assertEquals("tenant-1", response.body().scopes().getFirst().tenantId());
    assertTrue(response.body().capabilities().contains("REVIEW_DECISIONS"));

    var account = account("ops@example.com");
    assertEquals(AccountStatus.ACTIVE, account.status());
    assertEquals("workos-ops-1", account.workosUserId());
  }

  @Test
  void missingJwtIsRejectedByApiMe() {
    var error =
        assertThrows(
            RuntimeException.class,
            () -> httpClient.GET("/api/me").responseBodyAs(String.class).invoke());

    assertHttpRejection(error);
  }

  @Test
  void uninvitedWorkosUserIsRejected() throws Exception {
    var error =
        assertThrows(
            RuntimeException.class,
            () -> getMe(token("workos-stranger", "stranger@example.com", "Stranger")));

    assertHttpRejection(error);
  }

  @Test
  void disabledLocalAccountIsRejectedDespiteValidJwt() throws Exception {
    invite("disabled@example.com", "Disabled User", List.of(new RoleAssignment(SecurityRole.USER, "tenant-1", "customer-1")));
    componentClient
        .forKeyValueEntity("disabled@example.com")
        .method(LocalAccountEntity::disable)
        .invoke(new LocalAccount.Command.Disable(NOW.plusSeconds(60)));

    var error =
        assertThrows(
            RuntimeException.class,
            () -> getMe(token("workos-disabled", "disabled@example.com", "Disabled User")));

    assertHttpRejection(error);
  }

  @Test
  void repeatedMeCallsForSameWorkosIdentityAreIdempotent() throws Exception {
    invite("repeat@example.com", "Repeat User", List.of(new RoleAssignment(SecurityRole.USER, "tenant-1", "customer-1")));
    var jwt = token("workos-repeat", "repeat@example.com", "Repeat User");

    var first = getMe(jwt);
    var second = getMe(jwt);

    assertTrue(first.status().isSuccess());
    assertTrue(second.status().isSuccess());
    assertEquals(first.body(), second.body());
    assertEquals("workos-repeat", account("repeat@example.com").workosUserId());
  }

  @Test
  void authorizationHelperUsesLocalRolesAndScopesForBackendChecks() {
    var service = new AuthorizationService(componentClient);
    var activeAccount =
        new LocalAccount.State(
            "tenant-admin@example.com",
            "workos-tenant-admin",
            "tenant-admin@example.com",
            AccountStatus.ACTIVE,
            new UserProfile("Tenant Admin", "tenant-admin@example.com"),
            List.of(new RoleAssignment(SecurityRole.DEALER_OWNER, "tenant-1", null)),
            NOW,
            NOW,
            NOW);
    var auth = new AuthContext("tenant-admin@example.com", "workos-tenant-admin", activeAccount);

    service.requireTenantAccess(auth, "tenant-1");
    service.requireCustomerAccess(auth, "tenant-1", "customer-1");

    assertThrows(RuntimeException.class, () -> service.requireTenantAccess(auth, "tenant-2"));
  }

  private akka.javasdk.http.StrictResponse<MeResponse> getMe(String jwt) {
    return httpClient
        .GET("/api/me")
        .addHeader("Authorization", "Bearer " + jwt)
        .responseBodyAs(MeResponse.class)
        .invoke();
  }

  private void invite(String email, String displayName, List<RoleAssignment> roles) {
    var normalizedEmail = email.toLowerCase();
    componentClient
        .forKeyValueEntity(normalizedEmail)
        .method(LocalAccountEntity::invite)
        .invoke(
            new LocalAccount.Command.Invite(
                normalizedEmail,
                new UserProfile(displayName, normalizedEmail),
                roles,
                NOW));
  }

  private LocalAccount.State account(String userId) {
    return componentClient.forKeyValueEntity(userId).method(LocalAccountEntity::get).invoke();
  }

  private String token(String subject, String email, String name) throws Exception {
    return bearerTokenWith(Map.of("sub", subject, "email", email, "name", name));
  }

  private String bearerTokenWith(Map<String, String> claims) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(claims));
    return header + "." + payload;
  }

  private void assertHttpRejection(RuntimeException error) {
    assertTrue(
        error.getMessage().contains("400")
            || error.getMessage().contains("401")
            || error.getMessage().contains("403"),
        error.getMessage());
  }
}
