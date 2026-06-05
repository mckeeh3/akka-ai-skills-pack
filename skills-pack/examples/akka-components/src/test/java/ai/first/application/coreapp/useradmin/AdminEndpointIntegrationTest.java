package ai.first.application.coreapp.useradmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.api.coreapp.admin.AdminEndpoint.AccessReviewApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.AccessReviewApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.AccountActionApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.AccountActionApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.AdminAuditEventsResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.AdminUsersResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.ChangeMembershipStatusApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.ChangeRolesApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.CreateInvitationApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.InvitationActionApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.IdentityRelinkApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.IdentityRelinkApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.InvitationApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.InvitationsApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.MembershipActionApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.RoleChangePreviewApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.SupportAccessApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.SupportAccessApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.UserAdminDashboardPayload;
import ai.first.api.coreapp.admin.AdminEndpoint.UserAdminUserAccountPayload;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import ai.first.api.coreapp.admin.AdminEndpoint;

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
  void userAdminDashboardAndAccountPayloadsExposeInvitationLifecycleWithoutRawTokens() throws Exception {
    var created = httpClient
        .POST("/api/admin/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-dashboard-invite")
        .withRequestBody(new CreateInvitationApiRequest("dashboard-invite@example.test", "Dashboard Invite", List.of("TENANT_EMPLOYEE"), "idem-dashboard-invite"))
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    assertTrue(created.status().isSuccess());

    var dashboard = httpClient
        .GET("/api/admin/users/dashboard")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-dashboard")
        .responseBodyAs(UserAdminDashboardPayload.class)
        .invoke();
    assertTrue(dashboard.status().isSuccess());
    assertEquals("corr-admin-dashboard", dashboard.body().correlationId());
    assertEquals("tenant", dashboard.body().selectedScope().scopeType());
    assertTrue(dashboard.body().counts().visibleUsers() >= 1);
    assertTrue(dashboard.body().counts().pendingInvitations() >= 1);
    assertTrue(dashboard.body().invitationQueue().stream().anyMatch(invitation -> invitation.invitationId().equals(created.body().invitationId())));
    assertTrue(dashboard.body().visibleActions().contains("action-invite-user"));
    assertTrue(dashboard.body().traceIds().contains("trace-user-admin-dashboard"));
    assertTrue(dashboard.body().toString().contains("dashboard-invite@example.test"));
    assertTrue(!dashboard.body().toString().contains("invite-token-"));
    assertTrue(!dashboard.body().toString().contains("tokenHash"));

    var account = httpClient
        .GET("/api/admin/users/dashboard-invite@example.test")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-account")
        .responseBodyAs(UserAdminUserAccountPayload.class)
        .invoke();
    assertTrue(account.status().isSuccess());
    assertEquals("dashboard-invite@example.test", account.body().account().accountId());
    assertTrue(account.body().invitationHistory().stream().anyMatch(invitation -> invitation.invitationId().equals(created.body().invitationId())));
    assertTrue(account.body().redactions().contains("raw-token-redacted"));
    assertTrue(account.body().visibleActions().contains("action-useradmin-preview-role-change"));
    assertTrue(!account.body().toString().contains("invite-token-"));
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
  void adminCanPreviewRolesManageSupportAccessIdentityRelinkAndAccessReviewThroughProtectedApis() throws Exception {
    var preview = httpClient
        .POST("/api/admin/memberships/membership-member@example.test/roles/preview")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-role-preview")
        .withRequestBody(new ChangeRolesApiRequest(List.of("TENANT_EMPLOYEE"), "least privilege preview", null))
        .responseBodyAs(RoleChangePreviewApiResponse.class)
        .invoke();
    assertTrue(preview.status().isSuccess());
    assertTrue(preview.body().allowed());
    assertTrue(preview.body().traceId().contains("trace-useradmin-preview-role-change"));

    var support = httpClient
        .POST("/api/admin/support-access/membership-member@example.test/grant")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-support-grant")
        .withRequestBody(new SupportAccessApiRequest("break glass support", "2026-06-03T12:00:00Z", "idem-support-grant"))
        .responseBodyAs(SupportAccessApiResponse.class)
        .invoke();
    assertTrue(support.status().isSuccess());
    assertTrue(support.body().supportAccess());
    assertEquals("membership-member@example.test", support.body().membershipId());

    var relink = httpClient
        .POST("/api/admin/users/member@example.test/identity-relink/request")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-identity-relink")
        .withRequestBody(new IdentityRelinkApiRequest("provider subject mismatch", null, "idem-relink-request"))
        .responseBodyAs(IdentityRelinkApiResponse.class)
        .invoke();
    assertTrue(relink.status().isSuccess());
    assertEquals("approval-required", relink.body().status());

    var accessReview = httpClient
        .POST("/api/admin/access-review")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-access-review-api")
        .withRequestBody(new AccessReviewApiRequest("quarterly user admin review", "idem-access-review-api"))
        .responseBodyAs(AccessReviewApiResponse.class)
        .invoke();
    assertTrue(accessReview.status().isSuccess());
    assertTrue(List.of("queued", "running", "blocked_provider_or_runtime").contains(accessReview.body().status()));
    assertTrue(accessReview.body().traceIds().stream().anyMatch(trace -> trace.contains("trace-useradmin-access-review")));

    var readAccessReview = httpClient
        .GET("/api/admin/access-review/" + accessReview.body().taskId())
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-access-review-read")
        .responseBodyAs(AccessReviewApiResponse.class)
        .invoke();
    assertTrue(readAccessReview.status().isSuccess());
    assertEquals(accessReview.body().taskId(), readAccessReview.body().taskId());
  }

  @Test
  void adminAccountLifecycleApiIsBackendAuthorizedAndIdempotent() throws Exception {
    var disabled = httpClient
        .POST("/api/admin/users/member@example.test/disable")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-account-disable")
        .withRequestBody(new AccountActionApiRequest("offboarding", "idem-account-disable"))
        .responseBodyAs(AccountActionApiResponse.class)
        .invoke();
    assertTrue(disabled.status().isSuccess());
    assertEquals("disabled", disabled.body().accountStatus());

    var reactivated = httpClient
        .POST("/api/admin/users/member@example.test/reactivate")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-account-reactivate")
        .withRequestBody(new AccountActionApiRequest("returned", "idem-account-reactivate"))
        .responseBodyAs(AccountActionApiResponse.class)
        .invoke();
    assertTrue(reactivated.status().isSuccess());
    assertEquals("active", reactivated.body().accountStatus());
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
