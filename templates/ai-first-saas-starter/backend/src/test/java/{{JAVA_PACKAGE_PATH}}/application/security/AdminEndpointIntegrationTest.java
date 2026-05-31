package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.AdminAuditEventsResponse;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.AdminUsersResponse;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.ChangeMembershipStatusApiRequest;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.ChangeRolesApiRequest;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.CreateInvitationApiRequest;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.InvitationActionApiRequest;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.InvitationApiResponse;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.InvitationsApiResponse;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.MembershipActionApiResponse;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AdminEndpointIntegrationTest extends TestKitSupport {

  @Test
  void adminCanSearchUsersThroughConcreteProtectedApiAndAuditIsVisible() throws Exception {
    var users = httpClient
        .GET("/api/admin/users?query=admin")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-users")
        .responseBodyAs(AdminUsersResponse.class)
        .invoke();

    assertTrue(users.status().isSuccess());
    assertEquals("corr-admin-users", users.body().correlationId());
    assertTrue(users.body().users().stream().anyMatch(user -> user.accountId().equals("admin@example.test")));
    assertTrue(users.body().users().stream().allMatch(user -> user.tenantId().equals("tenant-starter")));

    var audit = httpClient
        .GET("/api/admin/audit-events?limit=100")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-audit")
        .responseBodyAs(AdminAuditEventsResponse.class)
        .invoke();

    assertTrue(audit.status().isSuccess());
    assertTrue(audit.body().events().stream().anyMatch(event -> event.actionType().equals("USER_DIRECTORY_SEARCH") && event.correlationId().equals("corr-admin-users")));
    assertTrue(audit.body().events().stream().allMatch(event -> event.tenantId() == null || event.tenantId().equals("tenant-starter")));
  }

  @Test
  void adminInvitationApiRequiresIdempotencyAndReplaysToSameInvitation() throws Exception {
    var missingKey = assertThrows(
        IllegalArgumentException.class,
        () -> httpClient
            .POST("/api/admin/invitations")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .withRequestBody(new CreateInvitationApiRequest("api-invite@example.test", "API Invite", List.of("TENANT_EMPLOYEE"), null))
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(missingKey.getMessage().contains("400"));

    var request = new CreateInvitationApiRequest("api-invite@example.test", "API Invite", List.of("TENANT_EMPLOYEE"), "idem-api-invite");
    var first = httpClient
        .POST("/api/admin/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-invite")
        .withRequestBody(request)
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    var replay = httpClient
        .POST("/api/admin/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-invite-replay")
        .withRequestBody(request)
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();

    assertTrue(first.status().isSuccess());
    assertTrue(replay.status().isSuccess());
    assertEquals(first.body().invitationId(), replay.body().invitationId());
    assertEquals("pending_delivery", first.body().status());
    assertNotNull(first.body().deliveryStatus());

    var listed = httpClient
        .GET("/api/admin/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .responseBodyAs(InvitationsApiResponse.class)
        .invoke();
    assertTrue(listed.body().invitations().stream().anyMatch(invitation -> invitation.invitationId().equals(first.body().invitationId())));

    var resent = httpClient
        .POST("/api/admin/invitations/" + first.body().invitationId() + "/resend")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .withRequestBody(new InvitationActionApiRequest("repair delivery", "idem-api-resend"))
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    assertEquals(1, resent.body().resendCount());

    var revoked = httpClient
        .POST("/api/admin/invitations/" + first.body().invitationId() + "/revoke")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .withRequestBody(new InvitationActionApiRequest("wrong recipient", null))
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    assertEquals("revoked", revoked.body().status());
  }

  @Test
  void adminCanUseConcreteMembershipRoleAndStatusApiActions() throws Exception {
    var roleChange = httpClient
        .POST("/api/admin/memberships/membership-member@example.test/roles")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-role-change")
        .withRequestBody(new ChangeRolesApiRequest(List.of("TENANT_EMPLOYEE"), "least privilege replay", "idem-api-role"))
        .responseBodyAs(MembershipActionApiResponse.class)
        .invoke();
    assertTrue(roleChange.status().isSuccess());
    assertEquals("no-op", roleChange.body().status());
    assertEquals("membership-member@example.test", roleChange.body().membershipId());
    assertTrue(roleChange.body().traceId().contains("trace-useradmin-change-member-roles"));

    var disabled = httpClient
        .POST("/api/admin/memberships/membership-member@example.test/status")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-member-disable")
        .withRequestBody(new ChangeMembershipStatusApiRequest("SUSPENDED", "offboarding", "idem-api-disable"))
        .responseBodyAs(MembershipActionApiResponse.class)
        .invoke();
    assertTrue(disabled.status().isSuccess());
    assertEquals("accepted", disabled.body().status());
    assertEquals("suspended", disabled.body().membershipStatus());
  }

  @Test
  void protectedAdminApisDenyMissingForbiddenAndCrossContextAccess() throws Exception {
    var missingAuth = assertThrows(
        IllegalArgumentException.class,
        () -> httpClient.GET("/api/admin/users").responseBodyAs(String.class).invoke());
    assertTrue(missingAuth.getMessage().contains("400") || missingAuth.getMessage().contains("401") || missingAuth.getMessage().contains("403"));

    var employeeForbidden = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .GET("/api/admin/users")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-member", "member@example.test", "Member"))
            .addHeader("X-Correlation-Id", "corr-employee-denied")
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(employeeForbidden.getMessage().contains("403"));

    var crossContextDenied = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .GET("/api/admin/users")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .addHeader("X-Selected-Context-Id", "membership-member@example.test")
            .addHeader("X-Correlation-Id", "corr-cross-context-denied")
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(crossContextDenied.getMessage().contains("403"));

    var audit = httpClient
        .GET("/api/admin/audit-events?limit=100")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .responseBodyAs(AdminAuditEventsResponse.class)
        .invoke();
    assertTrue(audit.body().events().stream().anyMatch(event -> event.correlationId().equals("corr-employee-denied") && event.result().equals("denied")));
  }

  private String bearerToken(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }
}
