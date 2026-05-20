package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.AdminAuditEventsResponse;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.AdminUsersResponse;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.CreateInvitationApiRequest;
import {{JAVA_BASE_PACKAGE}}.api.admin.AdminEndpoint.InvitationApiResponse;
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
  }

  @Test
  void protectedAdminApisDenyMissingForbiddenAndCrossContextAccess() throws Exception {
    var missingAuth = assertThrows(
        IllegalArgumentException.class,
        () -> httpClient.GET("/api/admin/users").responseBodyAs(String.class).invoke());
    assertTrue(missingAuth.getMessage().contains("400") || missingAuth.getMessage().contains("401") || missingAuth.getMessage().contains("403"));

    var employeeForbidden = assertThrows(
        IllegalArgumentException.class,
        () -> httpClient
            .GET("/api/admin/users")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-member", "member@example.test", "Member"))
            .addHeader("X-Correlation-Id", "corr-employee-denied")
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(employeeForbidden.getMessage().contains("403"));

    var crossContextDenied = assertThrows(
        IllegalArgumentException.class,
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
    assertTrue(audit.body().events().stream().anyMatch(event -> event.correlationId().equals("corr-cross-context-denied") && event.result().equals("denied")));
  }

  private String bearerToken(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }
}
