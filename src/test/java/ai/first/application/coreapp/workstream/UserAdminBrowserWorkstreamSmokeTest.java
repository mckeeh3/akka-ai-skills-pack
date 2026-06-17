package ai.first.application.coreapp.workstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    seedSaasOwnerIdentity(repository, "owner@example.test", "SaaS Owner", "membership-owner");
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

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected bootstrap must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-dashboard")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected dashboard surface must reject missing bearer tokens.");

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
    assertEquals("corr-browser-smoke-dashboard", dashboard.correlationId());
    assertTrue(dashboard.traceIds().stream().anyMatch(traceId -> traceId.contains("surface-user-admin-tenant-dashboard")));
    assertTrue(String.valueOf(dashboard.data().get("hero")).contains("traceRefs"));
    assertNotNull(dashboard.redaction());
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-show-users")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-invitation-create")));
    assertBrowserSafe(dashboard);

    var accessReviewBlocked = runAction(new CapabilityActionRequest(
        "action-useradmin-start-access-review",
        "action-useradmin-start-access-review",
        "user_admin.access_review.start",
        "user_admin.access_review.start",
        Map.of("scope", "tenant"),
        "idem-browser-smoke-access-review",
        SELECTED_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-browser-smoke-access-review"));
    assertEquals("blocked-runtime", accessReviewBlocked.status());
    assertEquals("corr-browser-smoke-access-review", accessReviewBlocked.correlationId());
    assertTrue(accessReviewBlocked.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-access-review")));
    assertEquals("surface-user-admin-access-review-task", accessReviewBlocked.resultSurface().surfaceId());
    assertEquals("blocked_provider_or_runtime", accessReviewBlocked.resultSurface().data().get("status"));
    assertTrue(accessReviewBlocked.resultSurface().toString().contains("modelToolDataPolicyUsage"));
    assertTrue(accessReviewBlocked.resultSurface().toString().contains("surface-audit-trace-detail"));
    assertTrue(accessReviewBlocked.resultSurface().toString().contains("noDirectMutation=true"));
    assertBrowserSafe(accessReviewBlocked.resultSurface());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-browser-smoke-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-user-admin-show-users",
            "user-admin.show-users",
            "search-user-directory",
            "USERADMIN_LIST_MEMBERS",
            null,
            null,
            SELECTED_CONTEXT_ID,
            dashboard.surfaceId(),
            "corr-browser-smoke-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected dashboard action path must reject missing bearer tokens.");

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

    var identityReview = runAction(new CapabilityActionRequest(
        "action-open-useradmin-identity-exception-review",
        "action-open-useradmin-identity-exception-review",
        "user_admin.identity_relink.review",
        "user_admin.identity_relink.review",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-browser-smoke-identity-open"));
    assertEquals("accepted", identityReview.status());
    assertEquals("surface-user-admin-identity-exception-review", identityReview.resultSurface().surfaceId());
    assertEquals("request-required", identityReview.resultSurface().data().get("status"));
    assertBrowserSafe(identityReview.resultSurface());

    var identityRequested = runAction(new CapabilityActionRequest(
        "action-useradmin-request-identity-relink",
        "action-useradmin-request-identity-relink",
        "user_admin.identity_relink.request",
        "user_admin.identity_relink.request",
        Map.of("accountId", "member@example.test", "reason", "provider mismatch smoke"),
        "idem-browser-smoke-identity-request",
        SELECTED_CONTEXT_ID,
        identityReview.resultSurface().surfaceId(),
        "corr-browser-smoke-identity-request"));
    assertEquals("approval-required", identityRequested.status());
    assertEquals("needs-review", identityRequested.resultSurface().data().get("lifecycleStatus"));
    assertFalse(identityRequested.resultSurface().toString().contains("workos-admin"));
    assertFalse(identityRequested.resultSurface().toString().contains("Bearer "));
    assertBrowserSafe(identityRequested.resultSurface());

    var identityApproved = runAction(new CapabilityActionRequest(
        "action-useradmin-approve-identity-relink",
        "action-useradmin-approve-identity-relink",
        "user_admin.identity_relink.approve",
        "user_admin.identity_relink.approve",
        Map.of("accountId", "member@example.test", "reason", "reviewed browser smoke evidence", "approvalRef", "approval-browser-smoke-identity"),
        "idem-browser-smoke-identity-approve",
        SELECTED_CONTEXT_ID,
        identityRequested.resultSurface().surfaceId(),
        "corr-browser-smoke-identity-approve"));
    assertEquals("approved-for-recovery", identityApproved.status());
    assertEquals("approved-for-recovery", identityApproved.resultSurface().data().get("lifecycleStatus"));
    assertBrowserSafe(identityApproved.resultSurface());

    var identityCompleted = runAction(new CapabilityActionRequest(
        "action-useradmin-complete-identity-relink",
        "action-useradmin-complete-identity-relink",
        "user_admin.identity_relink.complete",
        "user_admin.identity_relink.complete",
        Map.of("accountId", "member@example.test", "approvalRef", "approval-browser-smoke-identity"),
        "idem-browser-smoke-identity-complete",
        SELECTED_CONTEXT_ID,
        identityApproved.resultSurface().surfaceId(),
        "corr-browser-smoke-identity-complete"));
    assertEquals("accepted", identityCompleted.status());
    assertEquals("completed", identityCompleted.resultSurface().data().get("lifecycleStatus"));
    assertFalse(identityCompleted.resultSurface().toString().contains("workos-admin"));
    assertFalse(identityCompleted.resultSurface().toString().contains("Bearer "));
    assertBrowserSafe(identityCompleted.resultSurface());

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

  @Test
  void protectedWorkstreamApiExercisesSaasOwnerAdminsRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-saas-owner-admins")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .responseBodyAs(String.class)
        .invoke(), "SaaS Owner Admins surface must reject missing bearer tokens.");

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "SaaS Owner"))
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-saas-owner-admins-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals("membership-owner", bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertBrowserSafe(bootstrap.body());

    var dashboard = getSurfaceAs("surface-user-admin-dashboard", "corr-saas-owner-admins-dashboard", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-saas-owner-dashboard", dashboard.surfaceId());
    assertEquals("user_admin.saas_owner_dashboard.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-show-saas-owner-admins")));
    assertBrowserSafe(dashboard);

    var list = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-saas-owner-admins",
        "user-admin.show-saas-owner-admins",
        "manage-saas-owner-admins",
        "saas_owner.admin.list",
        Map.of("scope", "saas-owner"),
        null,
        "membership-owner",
        dashboard.surfaceId(),
        "corr-saas-owner-admins-list"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", list.status());
    assertEquals("corr-saas-owner-admins-list", list.correlationId());
    assertEquals("surface-user-admin-saas-owner-admins", list.resultSurface().surfaceId());
    assertEquals("list-search", list.resultSurface().surfaceType());
    assertEquals("user_admin.saas_owner_admins.v1", list.resultSurface().data().get("surfaceContract"));
    assertEquals("saas_owner", list.resultSurface().data().get("scopeType"));
    assertEquals("surface-user-admin-saas-owner-admins", list.resultSurface().data().get("branchRootSurfaceId"));
    assertTrue(String.valueOf(list.resultSurface().data().get("summary")).contains("visibleAdminCount="));
    assertTrue(list.resultSurface().toString().contains("owner@example.test"));
    assertTrue(list.resultSurface().toString().contains("targetSurfaceId=surface-user-admin-user-detail"));
    assertTrue(list.resultSurface().toString().contains("action-open-saas-owner-admin-invitation-create"));
    assertTrue(list.resultSurface().toString().contains("trace-saas-owner-admin"));
    assertBrowserSafe(list.resultSurface());

    var createForm = runActionAs(new CapabilityActionRequest(
        "action-open-saas-owner-admin-invitation-create",
        "user-admin.open-saas-owner-admin-invite",
        "manage-saas-owner-admins",
        "saas_owner.admin.invite",
        Map.of("scope", "saas-owner"),
        null,
        "membership-owner",
        list.resultSurface().surfaceId(),
        "corr-saas-owner-admins-create-form"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", createForm.status());
    assertEquals("surface-user-admin-saas-owner-admin-invitation-create", createForm.resultSurface().surfaceId());
    assertEquals("user_admin.saas_owner_admin_invitation_create.v1", createForm.resultSurface().data().get("surfaceContract"));
    assertTrue(createForm.resultSurface().toString().contains("action-submit-saas-owner-admin-invitation"));
    assertTrue(createForm.resultSurface().toString().contains("deliveryReadiness"));
    assertBrowserSafe(createForm.resultSurface());

    var submitted = runActionAs(new CapabilityActionRequest(
        "action-submit-saas-owner-admin-invitation",
        "user-admin.submit-saas-owner-admin-invite",
        "manage-saas-owner-admins",
        "saas_owner.admin.invite",
        Map.of("email", "browser-owner-admin@example.test", "displayName", "Browser Owner Admin", "roles", "SAAS_OWNER_ADMIN", "reason", "browser smoke"),
        "idem-browser-owner-admin",
        "membership-owner",
        createForm.resultSurface().surfaceId(),
        "corr-saas-owner-admins-submit"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertTrue(List.of("accepted", "blocked-runtime").contains(submitted.status()));
    assertEquals("surface-user-admin-invitation-detail", submitted.resultSurface().surfaceId());
    assertTrue(submitted.resultSurface().toString().contains("browser-owner-admin@example.test"));
    assertFalse(submitted.resultSurface().toString().contains("invite-token"));
    assertFalse(submitted.resultSurface().toString().contains("tokenHash"));
    assertBrowserSafe(submitted.resultSurface());

    var directList = getSurfaceAs("surface-user-admin-saas-owner-admins", "corr-saas-owner-admins-direct", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-saas-owner-admins", directList.surfaceId());
    assertEquals("corr-saas-owner-admins-direct", directList.correlationId());
    assertBrowserSafe(directList);

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-user-admin-show-saas-owner-admins",
        "user-admin.show-saas-owner-admins",
        "manage-saas-owner-admins",
        "saas_owner.admin.list",
        Map.of("scope", "saas-owner"),
        null,
        SELECTED_CONTEXT_ID,
        "surface-user-admin-dashboard",
        "corr-saas-owner-admins-tenant-denied")),
        "Tenant Admin selected contexts must not open the SaaS Owner Admins branch through the protected action API.");
  }

  private SurfaceEnvelope getSurface(String surfaceId, String correlationId) throws Exception {
    return getSurfaceAs(surfaceId, correlationId, "workos-admin", "admin@example.test", "Tenant Admin", SELECTED_CONTEXT_ID);
  }

  private SurfaceEnvelope getSurfaceAs(String surfaceId, String correlationId, String subject, String email, String name, String selectedContextId) throws Exception {
    var response = httpClient
        .GET("/api/workstream/surfaces/" + surfaceId)
        .addHeader("Authorization", "Bearer " + bearerToken(subject, email, name))
        .addHeader("X-Selected-Context-Id", selectedContextId)
        .addHeader("X-Correlation-Id", correlationId)
        .responseBodyAs(SurfaceEnvelope.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    return response.body();
  }

  private CapabilityActionResult runAction(CapabilityActionRequest request) throws Exception {
    return runActionAs(request, "workos-admin", "admin@example.test", "Tenant Admin", SELECTED_CONTEXT_ID);
  }

  private CapabilityActionResult runActionAs(CapabilityActionRequest request, String subject, String email, String name, String selectedContextId) throws Exception {
    var response = httpClient
        .POST("/api/workstream/actions")
        .addHeader("Authorization", "Bearer " + bearerToken(subject, email, name))
        .addHeader("X-Selected-Context-Id", selectedContextId)
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

  private void seedSaasOwnerIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership(membershipId, email, ScopeType.SAAS_OWNER, null, null, List.of(FoundationRole.SAAS_OWNER_ADMIN), MembershipStatus.ACTIVE, false, null));
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
