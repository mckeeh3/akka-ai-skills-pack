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

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-users")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected users directory surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-user-detail")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected user detail surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-role-change-preview")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected role-change preview surface must reject missing bearer tokens.");

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

    var directUsers = getSurface("surface-user-admin-users", "corr-browser-smoke-users-direct");
    assertEquals("surface-user-admin-users", directUsers.surfaceId());
    assertEquals("list-search", directUsers.surfaceType());
    assertEquals("user_admin.users.v1", directUsers.data().get("surfaceContract"));
    assertEquals("corr-browser-smoke-users-direct", directUsers.correlationId());
    assertTrue(directUsers.toString().contains("surface-user-admin-users"));
    assertTrue(directUsers.toString().contains("action-user-admin-show-users"));
    assertTrue(directUsers.toString().contains("member@example.test"));
    assertTrue(directUsers.toString().contains("targetSurfaceId=surface-user-admin-user-detail"));
    assertTrue(directUsers.toString().contains("action-open-useradmin-invitation-create"));
    assertTrue(directUsers.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-user-admin-users")));
    assertBrowserSafe(directUsers);

    var directUserDetail = getSurface("surface-user-admin-user-detail", "corr-browser-smoke-detail-direct");
    assertEquals("surface-user-admin-user-detail", directUserDetail.surfaceId());
    assertEquals("show-inspection", directUserDetail.surfaceType());
    assertEquals("user_admin.user_detail.v1", directUserDetail.data().get("surfaceContract"));
    assertEquals("corr-browser-smoke-detail-direct", directUserDetail.correlationId());
    assertTrue(directUserDetail.toString().contains("recordLabel=Tenant Admin"));
    assertTrue(directUserDetail.toString().contains("membershipId=membership-admin"));
    assertTrue(directUserDetail.toString().contains("canMutateInline=false"));
    assertTrue(directUserDetail.toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertTrue(directUserDetail.toString().contains("trace-user-admin-detail"));
    assertTrue(directUserDetail.actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-membership-status-confirmation")));
    assertTrue(directUserDetail.actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-support-access-grant")));
    assertTrue(directUserDetail.actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-read-access-review")));
    assertBrowserSafe(directUserDetail);

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
    assertEquals("corr-browser-smoke-users", users.correlationId());
    assertNotNull(users.resultSurface());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
    assertEquals("list-search", users.resultSurface().surfaceType());
    assertEquals("user_admin.users.v1", users.resultSurface().data().get("surfaceContract"));
    assertTrue(users.resultSurface().toString().contains("action-user-admin-show-users"));
    assertTrue(users.resultSurface().toString().contains("status"));
    assertTrue(users.resultSurface().toString().contains("member@example.test"));
    assertTrue(users.resultSurface().toString().contains("targetSurfaceId=surface-user-admin-user-detail"));
    assertTrue(users.resultSurface().toString().contains("openActionId=action-display-user-detail"));
    assertTrue(users.resultSurface().toString().contains("trace-surface-user-admin-users"));
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
    assertEquals("user_admin.user_detail.v1", detail.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-browser-smoke-detail", detail.resultSurface().correlationId());
    assertTrue(detail.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-user-admin-detail")));
    assertTrue(detail.resultSurface().toString().contains("recordLabel=Member User"));
    assertTrue(detail.resultSurface().toString().contains("membershipId=membership-member"));
    assertTrue(detail.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(detail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertTrue(detail.resultSurface().toString().contains("access-review"));
    assertTrue(detail.resultSurface().toString().contains("supportAccess"));
    assertTrue(detail.resultSurface().toString().contains("trace-useradmin-status-action"));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-membership-status-confirmation")));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-support-access-grant")));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-support-access-revoke-confirmation")));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-identity-exception-review")));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-read-access-review")));
    assertFalse(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-change-member-roles")), "User detail must route role changes through the preview task surface, not inline mutation.");
    assertFalse(detail.resultSurface().toString().contains("recordLabel=Tenant Admin"));
    assertBrowserSafe(detail.resultSurface());

    var rolePreview = runAction(new CapabilityActionRequest(
        "action-useradmin-preview-role-change",
        "action-useradmin-preview-role-change",
        "USERADMIN_PREVIEW_ROLE_CHANGE",
        "USERADMIN_PREVIEW_ROLE_CHANGE",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "protected browser smoke role preview"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-browser-smoke-role-preview"));
    assertEquals("accepted", rolePreview.status());
    assertEquals("surface-user-admin-role-change-preview", rolePreview.resultSurface().surfaceId());
    assertEquals("decision-card", rolePreview.resultSurface().surfaceType());
    assertEquals("user_admin.role_change_preview.v1", rolePreview.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-browser-smoke-role-preview", rolePreview.resultSurface().correlationId());
    assertTrue(rolePreview.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-preview-role-change")));
    assertTrue(rolePreview.resultSurface().toString().contains("targetSummary"));
    assertTrue(rolePreview.resultSurface().toString().contains("roleChangeProposal"));
    assertTrue(rolePreview.resultSurface().toString().contains("capabilityDelta"));
    assertTrue(rolePreview.resultSurface().toString().contains("policyDecision"));
    assertTrue(rolePreview.resultSurface().toString().contains("decisionEvidence"));
    assertTrue(rolePreview.resultSurface().toString().contains("confirmationForm"));
    assertTrue(rolePreview.resultSurface().toString().contains("approvalRequired=true"));
    assertTrue(rolePreview.resultSurface().toString().contains("action-commit-user-admin-role-change"));
    assertTrue(rolePreview.resultSurface().toString().contains("raw-policy-redacted"));
    assertTrue(rolePreview.resultSurface().toString().contains("sibling-scope-facts-redacted"));
    assertBrowserSafe(rolePreview.resultSurface());

    var roleChanged = runAction(new CapabilityActionRequest(
        "action-commit-user-admin-role-change",
        "action-commit-user-admin-role-change",
        "USERADMIN_CHANGE_MEMBER_ROLES",
        "USERADMIN_CHANGE_MEMBER_ROLES",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "protected browser smoke role commit"),
        "idem-browser-smoke-role-change",
        SELECTED_CONTEXT_ID,
        rolePreview.resultSurface().surfaceId(),
        "corr-browser-smoke-role-commit"));
    assertEquals("accepted", roleChanged.status());
    assertEquals("surface-user-admin-user-detail", roleChanged.resultSurface().surfaceId());
    assertEquals("corr-browser-smoke-role-commit", roleChanged.correlationId());
    assertTrue(roleChanged.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-change-member-roles")));
    assertTrue(roleChanged.resultSurface().toString().contains("recordLabel=Member User"));
    assertTrue(roleChanged.resultSurface().toString().contains("Tenant Admin"));
    assertTrue(roleChanged.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(roleChanged.resultSurface().toString().contains("trace-useradmin-status-action"));
    assertBrowserSafe(roleChanged.resultSurface());

    var roleReplay = runAction(new CapabilityActionRequest(
        "action-commit-user-admin-role-change",
        "action-commit-user-admin-role-change",
        "USERADMIN_CHANGE_MEMBER_ROLES",
        "USERADMIN_CHANGE_MEMBER_ROLES",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "protected browser smoke role replay"),
        "idem-browser-smoke-role-change-replay",
        SELECTED_CONTEXT_ID,
        rolePreview.resultSurface().surfaceId(),
        "corr-browser-smoke-role-replay"));
    assertEquals("no-op", roleReplay.status());
    assertTrue(roleReplay.message().contains("already match"));
    assertTrue(roleReplay.resultSurface().toString().contains("Tenant Admin"));
    assertBrowserSafe(roleReplay.resultSurface());

    var selfRemovalPreview = runAction(new CapabilityActionRequest(
        "action-useradmin-preview-role-change",
        "action-useradmin-preview-role-change",
        "USERADMIN_PREVIEW_ROLE_CHANGE",
        "USERADMIN_PREVIEW_ROLE_CHANGE",
        Map.of("accountId", "admin@example.test", "membershipId", SELECTED_CONTEXT_ID, "roles", List.of("TENANT_EMPLOYEE"), "reason", "protected browser smoke self-removal denial"),
        null,
        SELECTED_CONTEXT_ID,
        directUserDetail.surfaceId(),
        "corr-browser-smoke-role-self-denied"));
    assertEquals("denied", selfRemovalPreview.status());
    assertEquals("surface-user-admin-role-change-preview", selfRemovalPreview.resultSurface().surfaceId());
    assertTrue(selfRemovalPreview.resultSurface().toString().contains("self-admin-role-removal-denied"));
    assertTrue(selfRemovalPreview.resultSurface().toString().contains("selfActionRisk=blocked"));
    assertTrue(selfRemovalPreview.resultSurface().toString().contains("disabledReason=self-admin-role-removal-denied"));
    assertBrowserSafe(selfRemovalPreview.resultSurface());

    var membershipTask = runAction(new CapabilityActionRequest(
        "action-open-useradmin-membership-status-confirmation",
        "action-open-useradmin-membership-status-confirmation",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "status", "removed"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-browser-smoke-detail-membership-task"));
    assertEquals("accepted", membershipTask.status());
    assertEquals("surface-user-admin-membership-status-confirmation", membershipTask.resultSurface().surfaceId());
    assertEquals("user_admin.membership_status_confirmation.v1", membershipTask.resultSurface().data().get("surfaceContract"));
    assertTrue(membershipTask.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertTrue(membershipTask.resultSurface().toString().contains("last-admin-denied"));
    assertTrue(membershipTask.resultSurface().toString().contains("noRoleMutation=true"));
    assertTrue(membershipTask.resultSurface().toString().contains("noSupportAccessMutation=true"));
    assertTrue(membershipTask.resultSurface().toString().contains("noInvitationMutation=true"));
    assertBrowserSafe(membershipTask.resultSurface());

    var membershipChanged = runAction(new CapabilityActionRequest(
        "action-confirm-user-admin-membership-status-change",
        "action-confirm-user-admin-membership-status-change",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "status", "suspended", "reason", "protected browser smoke pause"),
        "idem-browser-smoke-membership-status-suspend",
        SELECTED_CONTEXT_ID,
        membershipTask.resultSurface().surfaceId(),
        "corr-browser-smoke-membership-status-suspend"));
    assertEquals("accepted", membershipChanged.status());
    assertEquals("surface-user-admin-user-detail", membershipChanged.resultSurface().surfaceId());
    assertEquals("corr-browser-smoke-membership-status-suspend", membershipChanged.correlationId());
    assertTrue(membershipChanged.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-update-member-status")));
    assertTrue(membershipChanged.resultSurface().toString().contains("membershipStatus=suspended"));
    assertTrue(membershipChanged.resultSurface().toString().contains("role"));
    assertTrue(membershipChanged.resultSurface().toString().contains("supportAccess"));
    assertTrue(membershipChanged.resultSurface().toString().contains("canMutateInline=false"));
    assertBrowserSafe(membershipChanged.resultSurface());

    var membershipReplay = runAction(new CapabilityActionRequest(
        "action-confirm-user-admin-membership-status-change",
        "action-confirm-user-admin-membership-status-change",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "status", "suspended", "reason", "duplicate protected browser smoke pause"),
        "idem-browser-smoke-membership-status-replay",
        SELECTED_CONTEXT_ID,
        membershipTask.resultSurface().surfaceId(),
        "corr-browser-smoke-membership-status-replay"));
    assertEquals("no-op", membershipReplay.status());
    assertTrue(membershipReplay.message().contains("already matches"));
    assertTrue(membershipReplay.resultSurface().toString().contains("membershipStatus=suspended"));
    assertBrowserSafe(membershipReplay.resultSurface());

    var membershipReactivated = runAction(new CapabilityActionRequest(
        "action-confirm-user-admin-membership-status-change",
        "action-confirm-user-admin-membership-status-change",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "status", "active", "reason", "protected browser smoke restore"),
        "idem-browser-smoke-membership-status-reactivate",
        SELECTED_CONTEXT_ID,
        membershipTask.resultSurface().surfaceId(),
        "corr-browser-smoke-membership-status-reactivate"));
    assertEquals("accepted", membershipReactivated.status());
    assertEquals("surface-user-admin-user-detail", membershipReactivated.resultSurface().surfaceId());
    assertTrue(membershipReactivated.resultSurface().toString().contains("membershipStatus=active"));
    assertTrue(membershipReactivated.resultSurface().toString().contains("trace-useradmin-status-action"));
    assertBrowserSafe(membershipReactivated.resultSurface());

    var hiddenUserTask = runAction(new CapabilityActionRequest(
        "action-open-useradmin-membership-status-confirmation",
        "action-open-useradmin-membership-status-confirmation",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        "USERADMIN_UPDATE_MEMBER_STATUS",
        Map.of("accountId", "hidden@example.test", "membershipId", "membership-hidden"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-browser-smoke-detail-hidden-user-task"));
    assertEquals("denied", hiddenUserTask.status());
    assertTrue(hiddenUserTask.resultSurface().surfaceId().contains("denied"));
    assertEquals("system_message", hiddenUserTask.resultSurface().surfaceType());
    assertFalse(hiddenUserTask.resultSurface().toString().contains("hidden@example.test"));
    assertFalse(hiddenUserTask.resultSurface().toString().contains("membership-hidden"));
    assertBrowserSafe(hiddenUserTask.resultSurface());

    var task = runAction(new CapabilityActionRequest(
        "action-open-user-admin-invitation-create",
        "action-open-user-admin-invitation-create",
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
    assertTrue(task.resultSurface().data().containsKey("form"));
    assertTrue(task.resultSurface().data().containsKey("deliveryReadiness"));
    assertTrue(task.resultSurface().toString().contains("action-submit-user-admin-invitation"));
    assertTrue(task.resultSurface().toString().contains("noFakeSuccess=true"));
    assertTrue(task.resultSurface().toString().contains("expiry"));
    assertBrowserSafe(task.resultSurface());

    var invited = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-invitation",
        "action-submit-user-admin-invitation",
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
  void protectedWorkstreamApiExercisesUserAdminSupportAccessGrantRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-support-access-grant")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Support access grant surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-user-support-grant-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-submit-user-admin-support-access-grant",
            "action-submit-user-admin-support-access-grant",
            "USERADMIN_SUPPORT_ACCESS_GRANT",
            "USERADMIN_SUPPORT_ACCESS_GRANT",
            Map.of("accountId", "member@example.test", "membershipId", "membership-member", "purpose", "missing bearer", "expiryHours", "2"),
            "idem-user-support-grant-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-support-access-grant",
            "corr-user-support-grant-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Support access grant action must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-support-access-revoke-confirmation")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Support access revoke confirmation surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-user-support-revoke-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-confirm-user-admin-support-access-revoke",
            "action-confirm-user-admin-support-access-revoke",
            "USERADMIN_SUPPORT_ACCESS_REVOKE",
            "USERADMIN_SUPPORT_ACCESS_REVOKE",
            Map.of("accountId", "member@example.test", "membershipId", "membership-member", "reason", "missing bearer"),
            "idem-user-support-revoke-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-support-access-revoke-confirmation",
            "corr-user-support-revoke-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Support access revoke action must reject missing bearer tokens.");

    var dashboard = getSurface("surface-user-admin-dashboard", "corr-user-support-grant-dashboard");
    var users = runAction(new CapabilityActionRequest(
        "action-user-admin-show-users",
        "user-admin.show-users",
        "search-user-directory",
        "USERADMIN_LIST_MEMBERS",
        null,
        null,
        SELECTED_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-user-support-grant-users"));
    assertEquals("accepted", users.status());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());

    var detail = runAction(new CapabilityActionRequest(
        "action-display-user-detail",
        "action-display-user-detail",
        "USERADMIN_LIST_MEMBERS",
        "USERADMIN_LIST_MEMBERS",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member"),
        null,
        SELECTED_CONTEXT_ID,
        users.resultSurface().surfaceId(),
        "corr-user-support-grant-detail"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertTrue(detail.resultSurface().toString().contains("supportAccess=false"));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-support-access-grant")));
    assertBrowserSafe(detail.resultSurface());

    var directGrant = getSurface("surface-user-admin-support-access-grant", "corr-user-support-grant-direct");
    assertEquals("surface-user-admin-support-access-grant", directGrant.surfaceId());
    assertEquals("create-form", directGrant.surfaceType());
    assertEquals("user_admin.support_access_grant.v1", directGrant.data().get("surfaceContract"));
    assertEquals("corr-user-support-grant-direct", directGrant.correlationId());
    assertTrue(directGrant.toString().contains("grantRequestForm"));
    assertTrue(directGrant.toString().contains("policyContext"));
    assertTrue(directGrant.toString().contains("decisionEvidence"));
    assertTrue(directGrant.toString().contains("noDirectMutation=true"));
    assertTrue(directGrant.toString().contains("noFakeSuccess=true"));
    assertTrue(directGrant.actions().stream().anyMatch(action -> action.actionId().equals("action-submit-user-admin-support-access-grant")));
    assertTrue(directGrant.actions().stream().anyMatch(action -> action.actionId().equals("action-validate-user-admin-support-access-grant")));
    assertBrowserSafe(directGrant);

    var opened = runAction(new CapabilityActionRequest(
        "action-open-user-admin-support-access-grant",
        "action-open-user-admin-support-access-grant",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-user-support-grant-open"));
    assertEquals("accepted", opened.status());
    assertEquals("surface-user-admin-support-access-grant", opened.resultSurface().surfaceId());
    assertEquals("user_admin.support_access_grant.v1", opened.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-user-support-grant-open", opened.resultSurface().correlationId());
    assertTrue(opened.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-support-access-grant")));
    assertTrue(opened.resultSurface().toString().contains("recordLabel=Member User"));
    assertTrue(opened.resultSurface().toString().contains("membershipId=membership-member"));
    assertTrue(opened.resultSurface().toString().contains("branchReturnActionId=action-display-user-detail"));
    assertTrue(opened.resultSurface().toString().contains("expiryHoursOptions"));
    assertTrue(opened.resultSurface().toString().contains("support-provider-internals-redacted"));
    assertBrowserSafe(opened.resultSurface());

    var validation = runAction(new CapabilityActionRequest(
        "action-validate-user-admin-support-access-grant",
        "action-validate-user-admin-support-access-grant",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member"),
        null,
        SELECTED_CONTEXT_ID,
        opened.resultSurface().surfaceId(),
        "corr-user-support-grant-validation"));
    assertEquals("validation-error", validation.status());
    assertEquals("surface-user-admin-support-access-grant", validation.resultSurface().surfaceId());
    assertTrue(validation.resultSurface().toString().contains("Purpose is required"));
    assertTrue(validation.resultSurface().toString().contains("supportAccessSummary=not-active"));
    assertBrowserSafe(validation.resultSurface());

    var submitted = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-support-access-grant",
        "action-submit-user-admin-support-access-grant",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "purpose", "customer-requested-support", "expiryHours", "2"),
        "idem-user-support-grant-runtime",
        SELECTED_CONTEXT_ID,
        opened.resultSurface().surfaceId(),
        "corr-user-support-grant-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("corr-user-support-grant-submit", submitted.correlationId());
    assertEquals("surface-user-admin-user-detail", submitted.resultSurface().surfaceId());
    assertEquals("user_admin.user_detail.v1", submitted.resultSurface().data().get("surfaceContract"));
    assertTrue(submitted.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-support-access")));
    assertTrue(submitted.resultSurface().toString().contains("supportAccess=true"));
    assertTrue(submitted.resultSurface().toString().contains("Support access granted or extended"));
    assertTrue(submitted.resultSurface().toString().contains("canMutateInline=false"));
    assertBrowserSafe(submitted.resultSurface());

    var replayed = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-support-access-grant",
        "action-submit-user-admin-support-access-grant",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "purpose", "customer-requested-support", "expiryHours", "2"),
        "idem-user-support-grant-runtime-replay",
        SELECTED_CONTEXT_ID,
        opened.resultSurface().surfaceId(),
        "corr-user-support-grant-replay"));
    assertEquals("accepted", replayed.status());
    assertEquals("surface-user-admin-user-detail", replayed.resultSurface().surfaceId());
    assertTrue(replayed.resultSurface().toString().contains("supportAccess=true"));
    assertBrowserSafe(replayed.resultSurface());

    var revokeOpened = runAction(new CapabilityActionRequest(
        "action-open-user-admin-support-access-revoke-confirmation",
        "action-open-user-admin-support-access-revoke-confirmation",
        "USERADMIN_SUPPORT_ACCESS_REVOKE",
        "USERADMIN_SUPPORT_ACCESS_REVOKE",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member"),
        null,
        SELECTED_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-user-support-revoke-open"));
    assertEquals("accepted", revokeOpened.status());
    assertEquals("surface-user-admin-support-access-revoke-confirmation", revokeOpened.resultSurface().surfaceId());
    assertEquals("destructive-lifecycle-confirmation", revokeOpened.resultSurface().surfaceType());
    assertEquals("user_admin.support_access_revoke_confirmation.v1", revokeOpened.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-user-support-revoke-open", revokeOpened.resultSurface().correlationId());
    assertTrue(revokeOpened.resultSurface().toString().contains("trace-useradmin-support-access-revoke-surface"));
    assertTrue(revokeOpened.resultSurface().toString().contains("targetSummary"));
    assertTrue(revokeOpened.resultSurface().toString().contains("activeSupportGrant"));
    assertTrue(revokeOpened.resultSurface().toString().contains("currentSupportAccess"));
    assertTrue(revokeOpened.resultSurface().toString().contains("eligibility"));
    assertTrue(revokeOpened.resultSurface().toString().contains("confirmationForm"));
    assertTrue(revokeOpened.resultSurface().toString().contains("idempotencyKeyHint=client-generated"));
    assertTrue(revokeOpened.resultSurface().toString().contains("action-confirm-user-admin-support-access-revoke"));
    assertTrue(revokeOpened.resultSurface().toString().contains("noRoleMutation=true"));
    assertTrue(revokeOpened.resultSurface().toString().contains("noMembershipLifecycleMutation=true"));
    assertTrue(revokeOpened.resultSurface().toString().contains("noInvitationMutation=true"));
    assertTrue(revokeOpened.resultSurface().toString().contains("noIdentityProviderMutation=true"));
    assertTrue(revokeOpened.resultSurface().toString().contains("noDirectAccessReviewMutation=true"));
    assertTrue(revokeOpened.resultSurface().toString().contains("noFakeSuccess=true"));
    assertTrue(revokeOpened.resultSurface().toString().contains("noDirectMutation=true"));
    assertTrue(revokeOpened.resultSurface().toString().contains("support-provider-internals-redacted"));
    assertTrue(revokeOpened.resultSurface().toString().contains("raw-jwt-redacted"));
    assertTrue(revokeOpened.resultSurface().toString().contains("private-profile-redacted"));
    assertTrue(revokeOpened.resultSurface().toString().contains("hidden-grant-redacted"));
    assertTrue(revokeOpened.resultSurface().toString().contains("sibling-scope-redacted"));
    assertBrowserSafe(revokeOpened.resultSurface());

    var revoked = runAction(new CapabilityActionRequest(
        "action-confirm-user-admin-support-access-revoke",
        "action-confirm-user-admin-support-access-revoke",
        "USERADMIN_SUPPORT_ACCESS_REVOKE",
        "USERADMIN_SUPPORT_ACCESS_REVOKE",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "reason", "case complete"),
        "idem-user-support-revoke-runtime",
        SELECTED_CONTEXT_ID,
        revokeOpened.resultSurface().surfaceId(),
        "corr-user-support-revoke-submit"));
    assertEquals("accepted", revoked.status());
    assertEquals("surface-user-admin-user-detail", revoked.resultSurface().surfaceId());
    assertEquals("corr-user-support-revoke-submit", revoked.resultSurface().correlationId());
    assertTrue(revoked.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-support-access")));
    assertTrue(revoked.resultSurface().toString().contains("Support access revoked"));
    assertTrue(revoked.resultSurface().toString().contains("supportAccess=false"));
    assertTrue(revoked.resultSurface().toString().contains("canMutateInline=false"));
    assertFalse(revoked.resultSurface().toString().contains("providerSecret"));
    assertBrowserSafe(revoked.resultSurface());

    var revokeReplay = runAction(new CapabilityActionRequest(
        "action-confirm-user-admin-support-access-revoke",
        "action-confirm-user-admin-support-access-revoke",
        "USERADMIN_SUPPORT_ACCESS_REVOKE",
        "USERADMIN_SUPPORT_ACCESS_REVOKE",
        Map.of("accountId", "member@example.test", "membershipId", "membership-member", "reason", "case already complete"),
        "idem-user-support-revoke-runtime-replay",
        SELECTED_CONTEXT_ID,
        revokeOpened.resultSurface().surfaceId(),
        "corr-user-support-revoke-replay"));
    assertEquals("accepted", revokeReplay.status());
    assertEquals("surface-user-admin-user-detail", revokeReplay.resultSurface().surfaceId());
    assertTrue(revokeReplay.resultSurface().toString().contains("supportAccess=false"));
    assertBrowserSafe(revokeReplay.resultSurface());

    assertThrows(RuntimeException.class, () -> runActionAs(new CapabilityActionRequest(
        "action-open-user-admin-support-access-revoke-confirmation",
        "action-open-user-admin-support-access-revoke-confirmation",
        "USERADMIN_SUPPORT_ACCESS_REVOKE",
        "USERADMIN_SUPPORT_ACCESS_REVOKE",
        Map.of("accountId", "admin@example.test", "membershipId", SELECTED_CONTEXT_ID),
        null,
        "membership-member",
        submitted.resultSurface().surfaceId(),
        "corr-user-support-revoke-member-denied"), "workos-member", "member@example.test", "Member User", "membership-member"),
        "Regular members must not open support access revoke confirmation through the protected action API.");

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-open-user-admin-support-access-grant",
        "action-open-user-admin-support-access-grant",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        Map.of("accountId", "hidden@example.test", "membershipId", "membership-hidden"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-user-support-grant-hidden-open")),
        "Hidden support-access grant targets must be denied without a successful browser payload.");

    assertThrows(RuntimeException.class, () -> runActionAs(new CapabilityActionRequest(
        "action-open-user-admin-support-access-grant",
        "action-open-user-admin-support-access-grant",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        "USERADMIN_SUPPORT_ACCESS_GRANT",
        Map.of("accountId", "admin@example.test", "membershipId", SELECTED_CONTEXT_ID),
        null,
        "membership-member",
        detail.resultSurface().surfaceId(),
        "corr-user-support-grant-member-denied"), "workos-member", "member@example.test", "Member User", "membership-member"),
        "Regular members must not open support access grant through the protected action API.");
  }

  @Test
  void protectedWorkstreamApiExercisesUserAdminInvitationCreateRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-invitation-create")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Invitation create surface must reject missing bearer tokens.");

    var directCreate = getSurface("surface-user-admin-invitation-create", "corr-user-invite-create-direct");
    assertEquals("surface-user-admin-invitation-create", directCreate.surfaceId());
    assertEquals("create-form", directCreate.surfaceType());
    assertEquals("user_admin.invitation_create.v1", directCreate.data().get("surfaceContract"));
    assertEquals("corr-user-invite-create-direct", directCreate.correlationId());
    assertTrue(directCreate.toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertTrue(directCreate.toString().contains("scopeSummary"));
    assertTrue(directCreate.toString().contains("roleOptions"));
    assertTrue(directCreate.toString().contains("expiryOptions"));
    assertTrue(directCreate.toString().contains("deliveryReadiness"));
    assertTrue(directCreate.toString().contains("noFakeSuccess=true"));
    assertTrue(directCreate.actions().stream().anyMatch(action -> action.actionId().equals("action-submit-user-admin-invitation")));
    assertBrowserSafe(directCreate);

    var dashboard = getSurface("surface-user-admin-dashboard", "corr-user-invite-create-dashboard");
    var users = runAction(new CapabilityActionRequest(
        "action-user-admin-show-users",
        "user-admin.show-users",
        "search-user-directory",
        "USERADMIN_LIST_MEMBERS",
        null,
        null,
        SELECTED_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-user-invite-create-users"));
    assertEquals("accepted", users.status());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());

    var opened = runAction(new CapabilityActionRequest(
        "action-open-user-admin-invitation-create",
        "action-open-user-admin-invitation-create",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        Map.of("email", "draft.invitee@example.test", "displayName", "Draft Invitee", "roles", "TENANT_EMPLOYEE", "reason", "draft smoke"),
        null,
        SELECTED_CONTEXT_ID,
        users.resultSurface().surfaceId(),
        "corr-user-invite-create-open"));
    assertEquals("accepted", opened.status());
    assertEquals("surface-user-admin-invitation-create", opened.resultSurface().surfaceId());
    assertEquals("user_admin.invitation_create.v1", opened.resultSurface().data().get("surfaceContract"));
    assertTrue(opened.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-invitation-create")));
    assertTrue(opened.resultSurface().toString().contains("draft.invitee@example.test"));
    assertTrue(opened.resultSurface().toString().contains("TENANT_EMPLOYEE"));
    assertTrue(opened.resultSurface().toString().contains("action-submit-user-admin-invitation"));
    assertBrowserSafe(opened.resultSurface());

    var submitted = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-invitation",
        "action-submit-user-admin-invitation",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        Map.of("email", "runtime.invitee@example.test", "displayName", "Runtime Invitee", "roles", "TENANT_EMPLOYEE", "reason", "runtime invitation create smoke"),
        "idem-user-invite-create-runtime",
        SELECTED_CONTEXT_ID,
        opened.resultSurface().surfaceId(),
        "corr-user-invite-create-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("corr-user-invite-create-submit", submitted.correlationId());
    assertEquals("surface-user-admin-invitation-detail", submitted.resultSurface().surfaceId());
    assertEquals("user_admin.invitation_detail.v1", submitted.resultSurface().data().get("surfaceContract"));
    assertTrue(submitted.resultSurface().toString().contains("runtime.invitee@example.test"));
    assertTrue(submitted.resultSurface().toString().contains("deliveryState"));
    assertTrue(submitted.resultSurface().toString().contains("providerReadiness=ready_or_captured"));
    assertTrue(submitted.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-invitation")));
    assertFalse(submitted.resultSurface().toString().contains("invite-token"));
    assertFalse(submitted.resultSurface().toString().contains("tokenHash"));
    assertBrowserSafe(submitted.resultSurface());

    var replayed = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-invitation",
        "action-submit-user-admin-invitation",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        Map.of("email", "runtime.invitee@example.test", "displayName", "Runtime Invitee", "roles", "TENANT_EMPLOYEE", "reason", "runtime invitation replay"),
        "idem-user-invite-create-runtime",
        SELECTED_CONTEXT_ID,
        opened.resultSurface().surfaceId(),
        "corr-user-invite-create-replay"));
    assertEquals("accepted", replayed.status());
    assertEquals("surface-user-admin-invitation-detail", replayed.resultSurface().surfaceId());
    assertTrue(replayed.resultSurface().toString().contains("runtime.invitee@example.test"));
    assertBrowserSafe(replayed.resultSurface());

    var duplicateDifferentKey = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-invitation",
        "action-submit-user-admin-invitation",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        Map.of("email", "runtime.invitee@example.test", "displayName", "Runtime Invitee", "roles", "TENANT_EMPLOYEE", "reason", "runtime invitation duplicate"),
        "idem-user-invite-create-runtime-duplicate",
        SELECTED_CONTEXT_ID,
        opened.resultSurface().surfaceId(),
        "corr-user-invite-create-duplicate"));
    assertEquals("accepted", duplicateDifferentKey.status());
    assertEquals("surface-user-admin-invitation-detail", duplicateDifferentKey.resultSurface().surfaceId());
    assertTrue(duplicateDifferentKey.resultSurface().toString().contains("runtime.invitee@example.test"));
    assertFalse(duplicateDifferentKey.resultSurface().toString().contains("invite-token"));
    assertBrowserSafe(duplicateDifferentKey.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-invitation-create",
        "corr-user-invite-create-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        "membership-member"), "Regular members must not load the invitation create surface.");
  }

  @Test
  void protectedWorkstreamApiExercisesUserAdminInvitationDetailRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-invitation-detail")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Invitation detail surface must reject missing bearer tokens.");

    var create = getSurface("surface-user-admin-invitation-create", "corr-user-invite-detail-create");
    var submitted = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-invitation",
        "action-submit-user-admin-invitation",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        Map.of("email", "detail.invitee@example.test", "displayName", "Detail Invitee", "roles", "TENANT_EMPLOYEE", "reason", "runtime invitation detail smoke"),
        "idem-user-invite-detail-runtime",
        SELECTED_CONTEXT_ID,
        create.surfaceId(),
        "corr-user-invite-detail-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("surface-user-admin-invitation-detail", submitted.resultSurface().surfaceId());
    assertEquals("show-inspection", submitted.resultSurface().surfaceType());
    assertEquals("user_admin.invitation_detail.v1", submitted.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-user-invite-detail-submit", submitted.resultSurface().correlationId());
    assertTrue(submitted.resultSurface().toString().contains("detail.invitee@example.test"));
    assertTrue(submitted.resultSurface().toString().contains("invitationSummary"));
    assertTrue(submitted.resultSurface().toString().contains("deliveryStatus"));
    assertTrue(submitted.resultSurface().toString().contains("lifecycleStatus"));
    assertTrue(submitted.resultSurface().toString().contains("eligibility"));
    assertTrue(submitted.resultSurface().toString().contains("availableTaskActions"));
    assertTrue(submitted.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(submitted.resultSurface().toString().contains("action-open-useradmin-invitation-resend-confirmation"));
    assertTrue(submitted.resultSurface().toString().contains("action-open-useradmin-invitation-revoke-confirmation"));
    assertTrue(submitted.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertFalse(submitted.resultSurface().toString().contains("invite-token"));
    assertFalse(submitted.resultSurface().toString().contains("tokenHash"));
    assertFalse(submitted.resultSurface().toString().contains("providerMessageId"));
    assertBrowserSafe(submitted.resultSurface());

    var actionContext = (Map<?, ?>) submitted.resultSurface().data().get("actionContext");
    var invitationId = String.valueOf(actionContext.get("invitationId"));
    assertNotNull(invitationId);

    var directDetail = getSurface("surface-user-admin-invitation-detail", "corr-user-invite-detail-direct");
    assertEquals("surface-user-admin-invitation-detail", directDetail.surfaceId());
    assertEquals("user_admin.invitation_detail.v1", directDetail.data().get("surfaceContract"));
    assertEquals("corr-user-invite-detail-direct", directDetail.correlationId());
    assertTrue(directDetail.toString().contains("detail.invitee@example.test"));
    assertBrowserSafe(directDetail);

    var readDetail = runAction(new CapabilityActionRequest(
        "action-display-invitation-detail",
        "action-display-invitation-detail",
        "USERADMIN_LIST_INVITATIONS",
        "USERADMIN_LIST_INVITATIONS",
        Map.of("invitationId", invitationId),
        null,
        SELECTED_CONTEXT_ID,
        "surface-user-admin-users",
        "corr-user-invite-detail-read"));
    assertEquals("accepted", readDetail.status());
    assertEquals("surface-user-admin-invitation-detail", readDetail.resultSurface().surfaceId());
    assertEquals("corr-user-invite-detail-read", readDetail.resultSurface().correlationId());
    assertTrue(readDetail.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-user-admin-invitation")));
    assertBrowserSafe(readDetail.resultSurface());

    var resendConfirmation = runAction(new CapabilityActionRequest(
        "action-open-useradmin-invitation-resend-confirmation",
        "action-open-useradmin-invitation-resend-confirmation",
        "USERADMIN_RESEND_INVITATION",
        "USERADMIN_RESEND_INVITATION",
        Map.of("invitationId", invitationId),
        null,
        SELECTED_CONTEXT_ID,
        readDetail.resultSurface().surfaceId(),
        "corr-user-invite-detail-resend-confirm"));
    assertEquals("accepted", resendConfirmation.status());
    assertEquals("surface-user-admin-invitation-resend-confirmation", resendConfirmation.resultSurface().surfaceId());
    assertEquals("user_admin.invitation_resend_confirmation.v1", resendConfirmation.resultSurface().data().get("surfaceContract"));
    assertTrue(resendConfirmation.resultSurface().toString().contains("idempotencyKeyHint=client-generated"));
    assertTrue(resendConfirmation.resultSurface().toString().contains("deliveryState"));
    assertBrowserSafe(resendConfirmation.resultSurface());

    var resent = runAction(new CapabilityActionRequest(
        "action-useradmin-resend-invitation",
        "action-useradmin-resend-invitation",
        "USERADMIN_RESEND_INVITATION",
        "USERADMIN_RESEND_INVITATION",
        Map.of("invitationId", invitationId, "reason", "runtime resend smoke"),
        "idem-user-invite-detail-resend",
        SELECTED_CONTEXT_ID,
        resendConfirmation.resultSurface().surfaceId(),
        "corr-user-invite-detail-resend"));
    assertEquals("accepted", resent.status());
    assertEquals("surface-user-admin-invitation-detail", resent.resultSurface().surfaceId());
    assertTrue(resent.resultSurface().toString().contains("resendCount=1"));
    assertBrowserSafe(resent.resultSurface());

    var revokeConfirmation = runAction(new CapabilityActionRequest(
        "action-open-useradmin-invitation-revoke-confirmation",
        "action-open-useradmin-invitation-revoke-confirmation",
        "USERADMIN_REVOKE_INVITATION",
        "USERADMIN_REVOKE_INVITATION",
        Map.of("invitationId", invitationId),
        null,
        SELECTED_CONTEXT_ID,
        resent.resultSurface().surfaceId(),
        "corr-user-invite-detail-revoke-confirm"));
    assertEquals("accepted", revokeConfirmation.status());
    assertEquals("surface-user-admin-invitation-revoke-confirmation", revokeConfirmation.resultSurface().surfaceId());
    assertEquals("destructive-lifecycle-confirmation", revokeConfirmation.resultSurface().surfaceType());
    assertEquals("user_admin.invitation_revoke_confirmation.v1", revokeConfirmation.resultSurface().data().get("surfaceContract"));
    assertTrue(revokeConfirmation.resultSurface().toString().contains("reasonRequired=true"));
    assertBrowserSafe(revokeConfirmation.resultSurface());

    var revoked = runAction(new CapabilityActionRequest(
        "action-useradmin-revoke-invitation",
        "action-useradmin-revoke-invitation",
        "USERADMIN_REVOKE_INVITATION",
        "USERADMIN_REVOKE_INVITATION",
        Map.of("invitationId", invitationId, "reason", "runtime revoke smoke"),
        "idem-user-invite-detail-revoke",
        SELECTED_CONTEXT_ID,
        revokeConfirmation.resultSurface().surfaceId(),
        "corr-user-invite-detail-revoke"));
    assertEquals("accepted", revoked.status());
    assertEquals("surface-user-admin-invitation-detail", revoked.resultSurface().surfaceId());
    assertTrue(revoked.resultSurface().toString().contains("revoked"));
    assertTrue(revoked.resultSurface().toString().contains("revoke_unavailable"));
    assertBrowserSafe(revoked.resultSurface());

    var revokeReplay = runAction(new CapabilityActionRequest(
        "action-useradmin-revoke-invitation",
        "action-useradmin-revoke-invitation",
        "USERADMIN_REVOKE_INVITATION",
        "USERADMIN_REVOKE_INVITATION",
        Map.of("invitationId", invitationId, "reason", "runtime revoke replay"),
        "idem-user-invite-detail-revoke-replay",
        SELECTED_CONTEXT_ID,
        revoked.resultSurface().surfaceId(),
        "corr-user-invite-detail-revoke-replay"));
    assertEquals("no-op", revokeReplay.status());
    assertEquals("surface-user-admin-invitation-detail", revokeReplay.resultSurface().surfaceId());
    assertBrowserSafe(revokeReplay.resultSurface());

    var hidden = runAction(new CapabilityActionRequest(
        "action-display-invitation-detail",
        "action-display-invitation-detail",
        "USERADMIN_LIST_INVITATIONS",
        "USERADMIN_LIST_INVITATIONS",
        Map.of("invitationId", "invitation-hidden-cross-scope"),
        null,
        SELECTED_CONTEXT_ID,
        "surface-user-admin-users",
        "corr-user-invite-detail-hidden"));
    assertEquals("denied", hidden.status());
    assertEquals("surface-user-admin-system-message", hidden.resultSurface().surfaceId());
    assertFalse(hidden.resultSurface().toString().contains("invitation-hidden-cross-scope"));
    assertBrowserSafe(hidden.resultSurface());

    assertThrows(RuntimeException.class, () -> runActionAs(new CapabilityActionRequest(
        "action-display-invitation-detail",
        "action-display-invitation-detail",
        "USERADMIN_LIST_INVITATIONS",
        "USERADMIN_LIST_INVITATIONS",
        Map.of("invitationId", invitationId),
        null,
        "membership-member",
        "surface-user-admin-users",
        "corr-user-invite-detail-member-denied"), "workos-member", "member@example.test", "Member User", "membership-member"),
        "Regular members must not inspect invitation detail through the protected action API.");
  }

  @Test
  void protectedWorkstreamApiExercisesUserAdminInvitationResendConfirmationRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-invitation-resend-confirmation")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Invitation resend confirmation surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-user-invite-resend-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-useradmin-resend-invitation",
            "action-useradmin-resend-invitation",
            "USERADMIN_RESEND_INVITATION",
            "USERADMIN_RESEND_INVITATION",
            Map.of("invitationId", "hidden-without-bearer"),
            "idem-user-invite-resend-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-invitation-resend-confirmation",
            "corr-user-invite-resend-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Invitation resend action must reject missing bearer tokens.");

    var create = getSurface("surface-user-admin-invitation-create", "corr-user-invite-resend-create");
    var submitted = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-invitation",
        "action-submit-user-admin-invitation",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        Map.of("email", "resend.invitee@example.test", "displayName", "Resend Invitee", "roles", "TENANT_EMPLOYEE", "reason", "runtime invitation resend smoke"),
        "idem-user-invite-resend-create",
        SELECTED_CONTEXT_ID,
        create.surfaceId(),
        "corr-user-invite-resend-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("surface-user-admin-invitation-detail", submitted.resultSurface().surfaceId());
    assertBrowserSafe(submitted.resultSurface());

    var actionContext = (Map<?, ?>) submitted.resultSurface().data().get("actionContext");
    var invitationId = String.valueOf(actionContext.get("invitationId"));
    assertNotNull(invitationId);

    var directConfirmation = getSurface("surface-user-admin-invitation-resend-confirmation", "corr-user-invite-resend-direct");
    assertEquals("surface-user-admin-invitation-resend-confirmation", directConfirmation.surfaceId());
    assertEquals("lifecycle-confirmation", directConfirmation.surfaceType());
    assertEquals("user_admin.invitation_resend_confirmation.v1", directConfirmation.data().get("surfaceContract"));
    assertEquals("corr-user-invite-resend-direct", directConfirmation.correlationId());
    assertTrue(directConfirmation.toString().contains("resend.invitee@example.test"));
    assertTrue(directConfirmation.toString().contains("invitationSummary"));
    assertTrue(directConfirmation.toString().contains("resendEligibility"));
    assertTrue(directConfirmation.toString().contains("deliveryReadiness"));
    assertTrue(directConfirmation.toString().contains("confirmationForm"));
    assertTrue(directConfirmation.toString().contains("idempotencyKeyHint=client-generated"));
    assertTrue(directConfirmation.toString().contains("noFakeSuccess=true"));
    assertTrue(directConfirmation.toString().contains("noDirectMutation=true"));
    assertTrue(directConfirmation.toString().contains("trace-useradmin-invitation-resend-confirmation"));
    assertTrue(directConfirmation.actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-resend-invitation")));
    assertTrue(directConfirmation.actions().stream().anyMatch(action -> action.actionId().equals("action-display-invitation-detail")));
    assertBrowserSafe(directConfirmation);

    var openedConfirmation = runAction(new CapabilityActionRequest(
        "action-open-useradmin-invitation-resend-confirmation",
        "action-open-useradmin-invitation-resend-confirmation",
        "USERADMIN_RESEND_INVITATION",
        "USERADMIN_RESEND_INVITATION",
        Map.of("invitationId", invitationId),
        null,
        SELECTED_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-user-invite-resend-open"));
    assertEquals("accepted", openedConfirmation.status());
    assertEquals("surface-user-admin-invitation-resend-confirmation", openedConfirmation.resultSurface().surfaceId());
    assertEquals("corr-user-invite-resend-open", openedConfirmation.resultSurface().correlationId());
    assertTrue(openedConfirmation.resultSurface().toString().contains("resend.invitee@example.test"));
    assertTrue(openedConfirmation.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertBrowserSafe(openedConfirmation.resultSurface());

    var resent = runAction(new CapabilityActionRequest(
        "action-useradmin-resend-invitation",
        "action-useradmin-resend-invitation",
        "USERADMIN_RESEND_INVITATION",
        "USERADMIN_RESEND_INVITATION",
        Map.of("invitationId", invitationId, "reason", "runtime resend confirmation smoke"),
        "idem-user-invite-resend-confirmation",
        SELECTED_CONTEXT_ID,
        openedConfirmation.resultSurface().surfaceId(),
        "corr-user-invite-resend-confirm"));
    assertEquals("accepted", resent.status());
    assertEquals("surface-user-admin-invitation-detail", resent.resultSurface().surfaceId());
    assertEquals("corr-user-invite-resend-confirm", resent.resultSurface().correlationId());
    assertTrue(resent.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-invitation")));
    assertTrue(resent.resultSurface().toString().contains("resendCount=1"));
    assertTrue(resent.resultSurface().toString().contains("recoverySurfaceId=surface-user-admin-invitation-resend-confirmation"));
    assertFalse(resent.resultSurface().toString().contains("invite-token"));
    assertFalse(resent.resultSurface().toString().contains("tokenHash"));
    assertFalse(resent.resultSurface().toString().contains("providerMessageId"));
    assertBrowserSafe(resent.resultSurface());

    var replayed = runAction(new CapabilityActionRequest(
        "action-useradmin-resend-invitation",
        "action-useradmin-resend-invitation",
        "USERADMIN_RESEND_INVITATION",
        "USERADMIN_RESEND_INVITATION",
        Map.of("invitationId", invitationId, "reason", "runtime resend confirmation replay"),
        "idem-user-invite-resend-confirmation",
        SELECTED_CONTEXT_ID,
        openedConfirmation.resultSurface().surfaceId(),
        "corr-user-invite-resend-replay"));
    assertEquals("accepted", replayed.status());
    assertEquals("surface-user-admin-invitation-detail", replayed.resultSurface().surfaceId());
    assertTrue(replayed.resultSurface().toString().contains("resend.invitee@example.test"));
    assertBrowserSafe(replayed.resultSurface());

    var hiddenOpen = runAction(new CapabilityActionRequest(
        "action-open-useradmin-invitation-resend-confirmation",
        "action-open-useradmin-invitation-resend-confirmation",
        "USERADMIN_RESEND_INVITATION",
        "USERADMIN_RESEND_INVITATION",
        Map.of("invitationId", "invitation-hidden-cross-scope"),
        null,
        SELECTED_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-user-invite-resend-hidden-open"));
    assertEquals("denied", hiddenOpen.status());
    assertTrue(hiddenOpen.resultSurface().surfaceId().contains("denied"));
    assertFalse(hiddenOpen.resultSurface().toString().contains("invitation-hidden-cross-scope"));
    assertBrowserSafe(hiddenOpen.resultSurface());

    assertThrows(RuntimeException.class, () -> runActionAs(new CapabilityActionRequest(
        "action-open-useradmin-invitation-resend-confirmation",
        "action-open-useradmin-invitation-resend-confirmation",
        "USERADMIN_RESEND_INVITATION",
        "USERADMIN_RESEND_INVITATION",
        Map.of("invitationId", invitationId),
        null,
        "membership-member",
        submitted.resultSurface().surfaceId(),
        "corr-user-invite-resend-member-denied"), "workos-member", "member@example.test", "Member User", "membership-member"),
        "Regular members must not open invitation resend confirmation through the protected action API.");
  }

  @Test
  void protectedWorkstreamApiExercisesUserAdminInvitationRevokeConfirmationRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-invitation-revoke-confirmation")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Invitation revoke confirmation surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-user-invite-revoke-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-useradmin-revoke-invitation",
            "action-useradmin-revoke-invitation",
            "USERADMIN_REVOKE_INVITATION",
            "USERADMIN_REVOKE_INVITATION",
            Map.of("invitationId", "hidden-without-bearer"),
            "idem-user-invite-revoke-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-invitation-revoke-confirmation",
            "corr-user-invite-revoke-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Invitation revoke action must reject missing bearer tokens.");

    var create = getSurface("surface-user-admin-invitation-create", "corr-user-invite-revoke-create");
    var submitted = runAction(new CapabilityActionRequest(
        "action-submit-user-admin-invitation",
        "action-submit-user-admin-invitation",
        "USERADMIN_SEND_INVITATION",
        "USERADMIN_SEND_INVITATION",
        Map.of("email", "revoke.invitee@example.test", "displayName", "Revoke Invitee", "roles", "TENANT_EMPLOYEE", "reason", "runtime invitation revoke smoke"),
        "idem-user-invite-revoke-create",
        SELECTED_CONTEXT_ID,
        create.surfaceId(),
        "corr-user-invite-revoke-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("surface-user-admin-invitation-detail", submitted.resultSurface().surfaceId());
    assertBrowserSafe(submitted.resultSurface());

    var actionContext = (Map<?, ?>) submitted.resultSurface().data().get("actionContext");
    var invitationId = String.valueOf(actionContext.get("invitationId"));
    assertNotNull(invitationId);

    var directConfirmation = getSurface("surface-user-admin-invitation-revoke-confirmation", "corr-user-invite-revoke-direct");
    assertEquals("surface-user-admin-invitation-revoke-confirmation", directConfirmation.surfaceId());
    assertEquals("destructive-lifecycle-confirmation", directConfirmation.surfaceType());
    assertEquals("user_admin.invitation_revoke_confirmation.v1", directConfirmation.data().get("surfaceContract"));
    assertEquals("corr-user-invite-revoke-direct", directConfirmation.correlationId());
    assertTrue(directConfirmation.toString().contains("revoke.invitee@example.test"));
    assertTrue(directConfirmation.toString().contains("invitationSummary"));
    assertTrue(directConfirmation.toString().contains("revokeEligibility"));
    assertTrue(directConfirmation.toString().contains("consequenceSummary"));
    assertTrue(directConfirmation.toString().contains("confirmationForm"));
    assertTrue(directConfirmation.toString().contains("idempotencyKeyHint=client-generated"));
    assertTrue(directConfirmation.toString().contains("noFakeSuccess=true"));
    assertTrue(directConfirmation.toString().contains("noDirectMutation=true"));
    assertTrue(directConfirmation.toString().contains("trace-useradmin-invitation-revoke-confirmation"));
    assertTrue(directConfirmation.actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-revoke-invitation")));
    assertTrue(directConfirmation.actions().stream().anyMatch(action -> action.actionId().equals("action-display-invitation-detail")));
    assertBrowserSafe(directConfirmation);

    var openedConfirmation = runAction(new CapabilityActionRequest(
        "action-open-useradmin-invitation-revoke-confirmation",
        "action-open-useradmin-invitation-revoke-confirmation",
        "USERADMIN_REVOKE_INVITATION",
        "USERADMIN_REVOKE_INVITATION",
        Map.of("invitationId", invitationId),
        null,
        SELECTED_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-user-invite-revoke-open"));
    assertEquals("accepted", openedConfirmation.status());
    assertEquals("surface-user-admin-invitation-revoke-confirmation", openedConfirmation.resultSurface().surfaceId());
    assertEquals("corr-user-invite-revoke-open", openedConfirmation.resultSurface().correlationId());
    assertTrue(openedConfirmation.resultSurface().toString().contains("revoke.invitee@example.test"));
    assertTrue(openedConfirmation.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertBrowserSafe(openedConfirmation.resultSurface());

    var revoked = runAction(new CapabilityActionRequest(
        "action-useradmin-revoke-invitation",
        "action-useradmin-revoke-invitation",
        "USERADMIN_REVOKE_INVITATION",
        "USERADMIN_REVOKE_INVITATION",
        Map.of("invitationId", invitationId, "reason", "runtime revoke confirmation smoke"),
        "idem-user-invite-revoke-confirmation",
        SELECTED_CONTEXT_ID,
        openedConfirmation.resultSurface().surfaceId(),
        "corr-user-invite-revoke-confirm"));
    assertEquals("accepted", revoked.status());
    assertEquals("surface-user-admin-invitation-detail", revoked.resultSurface().surfaceId());
    assertEquals("corr-user-invite-revoke-confirm", revoked.resultSurface().correlationId());
    assertTrue(revoked.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-invitation")));
    assertTrue(revoked.resultSurface().toString().contains("revoke.invitee@example.test"));
    assertTrue(revoked.resultSurface().toString().contains("revoked"));
    assertTrue(revoked.resultSurface().toString().contains("revoke_unavailable"));
    assertFalse(revoked.resultSurface().toString().contains("invite-token"));
    assertFalse(revoked.resultSurface().toString().contains("tokenHash"));
    assertFalse(revoked.resultSurface().toString().contains("providerMessageId"));
    assertBrowserSafe(revoked.resultSurface());

    var replayed = runAction(new CapabilityActionRequest(
        "action-useradmin-revoke-invitation",
        "action-useradmin-revoke-invitation",
        "USERADMIN_REVOKE_INVITATION",
        "USERADMIN_REVOKE_INVITATION",
        Map.of("invitationId", invitationId, "reason", "runtime revoke confirmation replay"),
        "idem-user-invite-revoke-confirmation-replay",
        SELECTED_CONTEXT_ID,
        revoked.resultSurface().surfaceId(),
        "corr-user-invite-revoke-replay"));
    assertEquals("no-op", replayed.status());
    assertEquals("surface-user-admin-invitation-detail", replayed.resultSurface().surfaceId());
    assertTrue(replayed.resultSurface().toString().contains("revoke.invitee@example.test"));
    assertBrowserSafe(replayed.resultSurface());

    var hiddenOpen = runAction(new CapabilityActionRequest(
        "action-open-useradmin-invitation-revoke-confirmation",
        "action-open-useradmin-invitation-revoke-confirmation",
        "USERADMIN_REVOKE_INVITATION",
        "USERADMIN_REVOKE_INVITATION",
        Map.of("invitationId", "invitation-hidden-cross-scope"),
        null,
        SELECTED_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-user-invite-revoke-hidden-open"));
    assertEquals("denied", hiddenOpen.status());
    assertTrue(hiddenOpen.resultSurface().surfaceId().contains("denied"));
    assertFalse(hiddenOpen.resultSurface().toString().contains("invitation-hidden-cross-scope"));
    assertBrowserSafe(hiddenOpen.resultSurface());

    assertThrows(RuntimeException.class, () -> runActionAs(new CapabilityActionRequest(
        "action-open-useradmin-invitation-revoke-confirmation",
        "action-open-useradmin-invitation-revoke-confirmation",
        "USERADMIN_REVOKE_INVITATION",
        "USERADMIN_REVOKE_INVITATION",
        Map.of("invitationId", invitationId),
        null,
        "membership-member",
        submitted.resultSurface().surfaceId(),
        "corr-user-invite-revoke-member-denied"), "workos-member", "member@example.test", "Member User", "membership-member"),
        "Regular members must not open invitation revoke confirmation through the protected action API.");
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
    assertEquals("corr-saas-owner-admins-submit", submitted.correlationId());
    assertEquals("surface-user-admin-invitation-detail", submitted.resultSurface().surfaceId());
    assertTrue(submitted.resultSurface().toString().contains("browser-owner-admin@example.test"));
    assertTrue(submitted.resultSurface().toString().contains("Role"));
    assertTrue(submitted.resultSurface().toString().contains("trace-"));
    assertFalse(submitted.resultSurface().toString().contains("invite-token"));
    assertFalse(submitted.resultSurface().toString().contains("tokenHash"));
    assertBrowserSafe(submitted.resultSurface());

    var replayedSubmit = runActionAs(new CapabilityActionRequest(
        "action-submit-saas-owner-admin-invitation",
        "user-admin.submit-saas-owner-admin-invite",
        "manage-saas-owner-admins",
        "saas_owner.admin.invite",
        Map.of("email", "browser-owner-admin@example.test", "displayName", "Browser Owner Admin", "roles", "SAAS_OWNER_ADMIN", "reason", "browser smoke replay"),
        "idem-browser-owner-admin",
        "membership-owner",
        createForm.resultSurface().surfaceId(),
        "corr-saas-owner-admins-submit-replay"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertTrue(List.of("accepted", "blocked-runtime").contains(replayedSubmit.status()));
    assertEquals("corr-saas-owner-admins-submit-replay", replayedSubmit.correlationId());
    assertEquals(submitted.resultSurface().surfaceId(), replayedSubmit.resultSurface().surfaceId());
    assertTrue(replayedSubmit.resultSurface().toString().contains("browser-owner-admin@example.test"));
    assertBrowserSafe(replayedSubmit.resultSurface());

    var unsupportedRole = runActionAs(new CapabilityActionRequest(
        "action-submit-saas-owner-admin-invitation",
        "user-admin.submit-saas-owner-admin-invite",
        "manage-saas-owner-admins",
        "saas_owner.admin.invite",
        Map.of("email", "bad-role-owner-admin@example.test", "displayName", "Bad Role", "roles", "TENANT_ADMIN", "reason", "role escalation attempt"),
        "idem-browser-owner-admin-bad-role",
        "membership-owner",
        createForm.resultSurface().surfaceId(),
        "corr-saas-owner-admins-bad-role"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("validation-error", unsupportedRole.status());
    assertEquals("surface-user-admin-saas-owner-admin-invitation-create", unsupportedRole.resultSurface().surfaceId());
    assertTrue(unsupportedRole.message().contains("SAAS_OWNER_ADMIN"));
    assertFalse(unsupportedRole.resultSurface().toString().contains("bad-role-owner-admin@example.test"));
    assertBrowserSafe(unsupportedRole.resultSurface());

    var missingIdempotency = runActionAs(new CapabilityActionRequest(
        "action-submit-saas-owner-admin-invitation",
        "user-admin.submit-saas-owner-admin-invite",
        "manage-saas-owner-admins",
        "saas_owner.admin.invite",
        Map.of("email", "missing-idempotency-owner-admin@example.test", "displayName", "Missing Idempotency", "roles", "SAAS_OWNER_ADMIN", "reason", "missing idempotency"),
        null,
        "membership-owner",
        createForm.resultSurface().surfaceId(),
        "corr-saas-owner-admins-missing-idempotency"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("validation-error", missingIdempotency.status());
    assertTrue(missingIdempotency.message().contains("idempotency key"));
    assertEquals("corr-saas-owner-admins-missing-idempotency", missingIdempotency.correlationId());

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

  @Test
  void protectedWorkstreamApiExercisesUserAdminAccessReviewTaskRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-access-review-task")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Access-review task surface must reject missing bearer tokens.");

    var directStatus = getSurface("surface-user-admin-access-review-task", "corr-access-review-task-direct");
    assertEquals("surface-user-admin-access-review-task", directStatus.surfaceId());
    assertEquals("workflow-status", directStatus.surfaceType());
    assertEquals("user_admin.access_review_task.v1", directStatus.data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", directStatus.data().get("status"));
    assertEquals(true, directStatus.data().get("noFakeSuccess"));
    assertEquals(true, directStatus.data().get("noDirectMutation"));
    assertTrue(directStatus.toString().contains("modelToolDataPolicyUsage"));
    assertTrue(directStatus.toString().contains("surface-audit-trace-detail"));
    assertAccessReviewBrowserSafe(directStatus);

    var started = runAction(new CapabilityActionRequest(
        "action-useradmin-start-access-review",
        "action-useradmin-start-access-review",
        "user_admin.access_review.start",
        "user_admin.access_review.start",
        Map.of("scope", "tenant"),
        "idem-access-review-task-browser-smoke",
        SELECTED_CONTEXT_ID,
        "surface-user-admin-dashboard",
        "corr-access-review-task-start"));
    assertEquals("blocked-runtime", started.status());
    assertEquals("corr-access-review-task-start", started.correlationId());
    assertEquals("surface-user-admin-access-review-task", started.resultSurface().surfaceId());
    assertEquals("user_admin.access_review_task.v1", started.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", started.resultSurface().data().get("status"));
    assertEquals(true, started.resultSurface().data().get("noDirectMutation"));
    assertTrue(started.resultSurface().toString().contains("providerFailures"));
    assertTrue(started.resultSurface().toString().contains("modelToolDataPolicyUsage"));
    assertTrue(started.resultSurface().toString().contains("surface-audit-trace-detail"));
    assertTrue(started.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-access-review")));
    assertAccessReviewBrowserSafe(started.resultSurface());

    var taskId = String.valueOf(started.resultSurface().data().get("taskId"));
    assertFalse(taskId.isBlank());

    var read = runAction(new CapabilityActionRequest(
        "action-useradmin-read-access-review",
        "action-useradmin-read-access-review",
        "user_admin.access_review.read",
        "user_admin.access_review.read",
        Map.of("taskId", taskId),
        null,
        SELECTED_CONTEXT_ID,
        started.resultSurface().surfaceId(),
        "corr-access-review-task-read"));
    assertEquals("accepted", read.status());
    assertEquals(taskId, read.resultSurface().data().get("taskId"));
    assertEquals("blocked_provider_or_runtime", read.resultSurface().data().get("status"));
    assertTrue(read.resultSurface().toString().contains("providerFailures"));
    assertTrue(read.resultSurface().toString().contains("noDirectMutation=true"));
    assertAccessReviewBrowserSafe(read.resultSurface());

    var notCompletedAccept = runAction(new CapabilityActionRequest(
        "action-useradmin-accept-access-review-result",
        "action-useradmin-accept-access-review-result",
        "user_admin.access_review.accept_result",
        "user_admin.access_review.accept_result",
        Map.of("taskId", taskId, "reason", "browser smoke should not accept provider-blocked task"),
        "idem-access-review-task-accept-not-completed",
        SELECTED_CONTEXT_ID,
        read.resultSurface().surfaceId(),
        "corr-access-review-task-accept-not-completed"));
    assertEquals("denied", notCompletedAccept.status());
    assertEquals("surface-user-admin-system-message", notCompletedAccept.resultSurface().surfaceId());
    assertTrue(notCompletedAccept.resultSurface().surfaceType().contains("system"));
    assertAccessReviewBrowserSafe(notCompletedAccept.resultSurface());

    var cancelled = runAction(new CapabilityActionRequest(
        "action-useradmin-cancel-access-review",
        "action-useradmin-cancel-access-review",
        "user_admin.access_review.cancel",
        "user_admin.access_review.cancel",
        Map.of("taskId", taskId, "reason", "browser smoke cancel"),
        "idem-access-review-task-cancel",
        SELECTED_CONTEXT_ID,
        read.resultSurface().surfaceId(),
        "corr-access-review-task-cancel"));
    assertEquals("accepted", cancelled.status());
    assertEquals("cancelled", cancelled.resultSurface().data().get("status"));
    assertEquals(true, cancelled.resultSurface().data().get("noDirectMutation"));
    assertTrue(cancelled.message().contains("access state unchanged"));
    assertTrue(cancelled.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-access-review")));
    assertAccessReviewBrowserSafe(cancelled.resultSurface());

    assertThrows(RuntimeException.class, () -> runActionAs(new CapabilityActionRequest(
        "action-useradmin-read-access-review",
        "action-useradmin-read-access-review",
        "user_admin.access_review.read",
        "user_admin.access_review.read",
        Map.of("taskId", taskId),
        null,
        "membership-member",
        cancelled.resultSurface().surfaceId(),
        "corr-access-review-task-member-denied"), "workos-member", "member@example.test", "Member User", "membership-member"),
        "Regular members must not read tenant access-review task state through the protected action API.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-access-review-task-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-useradmin-read-access-review",
            "action-useradmin-read-access-review",
            "user_admin.access_review.read",
            "user_admin.access_review.read",
            Map.of("taskId", taskId),
            null,
            SELECTED_CONTEXT_ID,
            cancelled.resultSurface().surfaceId(),
            "corr-access-review-task-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Access-review task action path must reject missing bearer tokens.");
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

  private static void assertAccessReviewBrowserSafe(Object payload) {
    var text = String.valueOf(payload);
    assertFalse(text.contains("invite-token"));
    assertFalse(text.contains("tokenHash"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
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
