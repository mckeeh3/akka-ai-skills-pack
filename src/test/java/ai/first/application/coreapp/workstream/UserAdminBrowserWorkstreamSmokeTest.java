package ai.first.application.coreapp.workstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.application.coreapp.workstream.WorkstreamService.CapabilityActionRequest;
import ai.first.application.coreapp.workstream.WorkstreamService.CapabilityActionResult;
import ai.first.application.coreapp.workstream.WorkstreamService.SurfaceEnvelope;
import ai.first.application.coreapp.workstream.WorkstreamService.WorkstreamBootstrapResponse;
import ai.first.application.foundation.identity.AkkaIdentityRepository;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Scriptable hosted-UI/workstream smoke for the User Admin browser surface graph. */
class UserAdminBrowserWorkstreamSmokeTest extends TestKitSupport {
  private static final String TENANT_ID = "tenant-starter";
  private static final String SELECTED_CONTEXT_ID = "membership-admin";

  @BeforeEach
  void seedUserAdminSmokeActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(TENANT_ID, "Starter Tenant", true));
    seedIdentity(repository, "admin@example.test", "Tenant Admin", SELECTED_CONTEXT_ID, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    seedIdentity(repository, "member@example.test", "Member User", "membership-member", List.of(FoundationRole.TENANT_EMPLOYEE));
  }

  @Test
  void hostedShellAndProtectedWorkstreamApiTraverseUserAdminSurfaceGraph() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load from Akka static resources.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertFalse(shell.body().contains("invite-token"));
    assertFalse(shell.body().contains("tokenHash"));
    assertFalse(shell.body().contains("providerSecret"));

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-browser-smoke-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(SELECTED_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-user-admin") && agent.availability().equals("visible")));
    assertBrowserSafe(bootstrap.body());

    var dashboard = getSurface("surface-user-admin-dashboard", "corr-browser-smoke-dashboard");
    assertEquals("surface-user-admin-tenant-dashboard", dashboard.surfaceId());
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("user_admin.tenant_dashboard.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-show-users")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-invitation-create")));
    assertBrowserSafe(dashboard);

    var users = runAction(new CapabilityActionRequest(
        "action-user-admin-show-users",
        "user-admin.show-users",
        "search-user-directory",
        "USERADMIN_LIST_MEMBERS",
        null,
        null,
        SELECTED_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-browser-smoke-users"));
    assertEquals("accepted", users.status());
    assertNotNull(users.resultSurface());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
    assertEquals("list-search", users.resultSurface().surfaceType());
    assertEquals("user_admin.users.v1", users.resultSurface().data().get("surfaceContract"));
    assertTrue(users.resultSurface().toString().contains("member@example.test"));
    assertTrue(users.resultSurface().toString().contains("targetSurfaceId=surface-user-admin-user-detail"));
    assertTrue(users.resultSurface().toString().contains("openActionId=action-display-user-detail"));
    assertBrowserSafe(users.resultSurface());

    var detail = runAction(new CapabilityActionRequest(
        "action-display-user-detail",
        "action-display-user-detail",
        "USERADMIN_LIST_MEMBERS",
        "USERADMIN_LIST_MEMBERS",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member"),
        null,
        SELECTED_CONTEXT_ID,
        users.resultSurface().surfaceId(),
        "corr-browser-smoke-detail"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertEquals("show-inspection", detail.resultSurface().surfaceType());
    assertTrue(detail.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-membership-status-confirmation")));
    assertBrowserSafe(detail.resultSurface());

    var task = runAction(new CapabilityActionRequest(
        "action-open-useradmin-invitation-create",
        "action-open-useradmin-invitation-create",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        null,
        null,
        SELECTED_CONTEXT_ID,
        users.resultSurface().surfaceId(),
        "corr-browser-smoke-invite-task"));
    assertEquals("accepted", task.status());
    assertEquals("surface-user-admin-invitation-create", task.resultSurface().surfaceId());
    assertEquals("create-form", task.resultSurface().surfaceType());
    assertEquals("user_admin.invitation_create.v1", task.resultSurface().data().get("surfaceContract"));
    assertTrue(task.resultSurface().data().containsKey("roleOptions"));
    assertTrue(task.resultSurface().toString().contains("expiry"));
    assertBrowserSafe(task.resultSurface());

    var invited = runAction(new CapabilityActionRequest(
        "action-invite-user",
        "action-invite-user",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        Map.of("email", "smoke.invitee@example.test", "displayName", "Smoke Invitee"),
        "idem-browser-smoke-invite",
        SELECTED_CONTEXT_ID,
        task.resultSurface().surfaceId(),
        "corr-browser-smoke-invite-create"));
    assertEquals("accepted", invited.status());
    assertEquals("surface-user-admin-invitation-detail", invited.resultSurface().surfaceId());
    assertTrue(invited.resultSurface().toString().contains("deliveryState"));
    assertTrue(invited.resultSurface().toString().contains("providerReadiness=ready_or_captured"));
    assertTrue(invited.resultSurface().toString().contains("recoverySurfaceId=surface-user-admin-invitation-resend-confirmation"));
    assertFalse(invited.resultSurface().toString().contains("providerMessageId"));
    assertBrowserSafe(invited.resultSurface());

    var denied = runAction(new CapabilityActionRequest(
        "action-display-invitation-detail",
        "action-display-invitation-detail",
        "USERADMIN_LIST_INVITATIONS",
        "USERADMIN_LIST_INVITATIONS",
        Map.of("invitationId", "invitation-hidden-cross-scope"),
        null,
        SELECTED_CONTEXT_ID,
        users.resultSurface().surfaceId(),
        "corr-browser-smoke-hidden-invite"));
    assertEquals("denied", denied.status());
    assertEquals("surface-user-admin-system-message", denied.resultSurface().surfaceId());
    assertEquals("user_admin.system_message.v1", denied.resultSurface().data().get("surfaceContract"));
    assertEquals(true, denied.resultSurface().data().get("noFakeSuccess"));
    assertFalse(denied.resultSurface().toString().contains("invitation-hidden-cross-scope"));
    assertBrowserSafe(denied.resultSurface());
  }

  private SurfaceEnvelope getSurface(String surfaceId, String correlationId) throws Exception {
    var response = httpClient
        .GET("/api/workstream/surfaces/" + surfaceId)
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", correlationId)
        .responseBodyAs(SurfaceEnvelope.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    return response.body();
  }

  private CapabilityActionResult runAction(CapabilityActionRequest request) throws Exception {
    var response = httpClient
        .POST("/api/workstream/actions")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", request.correlationId())
        .withRequestBody(request)
        .responseBodyAs(CapabilityActionResult.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    return response.body();
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, List<FoundationRole> roles) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership(membershipId, email, ScopeType.TENANT, TENANT_ID, null, roles, MembershipStatus.ACTIVE, false, null));
  }

  private String bearerToken(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }

  private static void assertBrowserSafe(Object payload) {
    var text = String.valueOf(payload);
    assertFalse(text.contains("invite-token"));
    assertFalse(text.contains("tokenHash"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("sk-"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }
}
