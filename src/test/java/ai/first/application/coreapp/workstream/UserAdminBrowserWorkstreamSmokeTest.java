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
import ai.first.application.foundation.invitation.AkkaInvitationRepository;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
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
import ai.first.domain.foundation.invitation.InvitationStatus;
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
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("user-admin-agent") && agent.availability().equals("visible")));
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
            "user_admin.list_members",
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
        "user_admin.list_members",
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
        "user_admin.list_members",
        "user_admin.list_members",
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
        "user_admin.preview_role_change",
        "user_admin.preview_role_change",
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
        "user_admin.change_member_roles",
        "user_admin.change_member_roles",
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
        "user_admin.change_member_roles",
        "user_admin.change_member_roles",
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
        "user_admin.preview_role_change",
        "user_admin.preview_role_change",
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
        "user_admin.update_member_status",
        "user_admin.update_member_status",
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
        "user_admin.update_member_status",
        "user_admin.update_member_status",
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
        "user_admin.update_member_status",
        "user_admin.update_member_status",
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
        "user_admin.update_member_status",
        "user_admin.update_member_status",
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
        "user_admin.update_member_status",
        "user_admin.update_member_status",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.acceptance_status.read",
        "user_admin.acceptance_status.read",
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
  void protectedWorkstreamApiExercisesUserAdminSystemMessageRuntimeCoverage() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load before browser-safe system-message API smoke.");
    assertFalse(shell.body().contains("providerSecret"));
    assertFalse(shell.body().contains("invite-token"));

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-system-message")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-useradmin-system-message-missing-bearer")
        .responseBodyAs(String.class)
        .invoke(), "Protected system-message surface must reject missing bearer tokens.");

    var recovery = getSurface("surface-user-admin-system-message", "corr-useradmin-system-message-direct-http");
    assertEquals("surface-user-admin-system-message", recovery.surfaceId());
    assertEquals("system-message", recovery.surfaceType());
    assertEquals("user_admin.system_message.v1", recovery.data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", recovery.data().get("status"));
    assertEquals("direct_recovery", recovery.data().get("reasonCode"));
    assertEquals(true, recovery.data().get("noEnumeration"));
    assertEquals(true, recovery.data().get("noFakeSuccess"));
    assertEquals(true, recovery.data().get("noDirectMutation"));
    assertEquals("corr-useradmin-system-message-direct-http", recovery.correlationId());
    assertTrue(recovery.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-system-message")));
    assertTrue(recovery.toString().contains("selectedAuthContext"));
    assertTrue(recovery.toString().contains("readinessSummary"));
    assertTrue(recovery.toString().contains("validationSummary"));
    assertTrue(recovery.toString().contains("rawJwt"));
    assertTrue(recovery.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-return-dashboard")));
    assertTrue(recovery.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-show-users")));
    assertFalse(recovery.toString().contains("hidden@example.test"));
    assertBrowserSafe(recovery);

    var hiddenNavigation = runAction(new CapabilityActionRequest(
        "action-display-invitation-detail",
        "action-display-invitation-detail",
        "user_admin.acceptance_status.read",
        "user_admin.acceptance_status.read",
        Map.of("invitationId", "invitation-hidden-cross-scope"),
        null,
        SELECTED_CONTEXT_ID,
        "surface-user-admin-users",
        "corr-useradmin-system-message-hidden-target"));
    assertEquals("denied", hiddenNavigation.status());
    assertEquals("surface-user-admin-system-message", hiddenNavigation.resultSurface().surfaceId());
    assertEquals("user_admin.system_message.v1", hiddenNavigation.resultSurface().data().get("surfaceContract"));
    assertEquals(true, hiddenNavigation.resultSurface().data().get("noEnumeration"));
    assertEquals(true, hiddenNavigation.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, hiddenNavigation.resultSurface().data().get("noDirectMutation"));
    assertTrue(hiddenNavigation.resultSurface().traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-system-message")));
    assertFalse(hiddenNavigation.resultSurface().toString().contains("invitation-hidden-cross-scope"));
    assertFalse(hiddenNavigation.resultSurface().toString().contains("hidden@example.test"));
    assertBrowserSafe(hiddenNavigation.resultSurface());

    var returned = runAction(new CapabilityActionRequest(
        "action-user-admin-return-dashboard",
        "user-admin.return-dashboard",
        "search-user-directory",
        "user_admin.view_overview",
        null,
        null,
        SELECTED_CONTEXT_ID,
        recovery.surfaceId(),
        "corr-useradmin-system-message-return-dashboard"));
    assertEquals("accepted", returned.status());
    assertEquals("surface-user-admin-tenant-dashboard", returned.resultSurface().surfaceId());
    assertEquals("user_admin.tenant_dashboard.v1", returned.resultSurface().data().get("surfaceContract"));
    assertBrowserSafe(returned.resultSurface());
  }

  @Test
  void protectedWorkstreamApiExercisesUserAdminIdentityExceptionReviewRuntimePath() throws Exception {
    var repository = new AkkaIdentityRepository(componentClient);
    seedIdentity(repository, "identity.case@example.test", "Identity Case", "membership-identity-case", List.of(FoundationRole.TENANT_EMPLOYEE));
    seedIdentity(repository, "identity.deny@example.test", "Identity Deny", "membership-identity-deny", List.of(FoundationRole.TENANT_EMPLOYEE));
    var beforeMembership = repository.findMembership("membership-identity-case").orElseThrow();
    var beforeAccount = repository.findAccountByEmail("identity.case@example.test").orElseThrow();

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-identity-exception-review")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Identity-exception review surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-identity-review-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-useradmin-request-identity-relink",
            "action-useradmin-request-identity-relink",
            "user_admin.identity_relink.request",
            "user_admin.identity_relink.request",
            Map.of("accountId", "identity.case@example.test", "reason", "missing bearer"),
            "idem-identity-review-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-identity-exception-review",
            "corr-identity-review-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Identity recovery action path must reject missing bearer tokens.");

    var direct = getSurface("surface-user-admin-identity-exception-review", "corr-identity-review-direct");
    assertEquals("surface-user-admin-identity-exception-review", direct.surfaceId());
    assertEquals("decision-card", direct.surfaceType());
    assertEquals("user_admin.identity_exception_review.v1", direct.data().get("surfaceContract"));
    assertEquals("request-required", direct.data().get("status"));
    assertEquals(true, direct.data().get("noDirectMutation"));
    assertTrue(direct.toString().contains("provider-boundary"));
    assertTrue(direct.toString().contains("raw-jwt-redacted"));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-request-identity-relink")));
    assertBrowserSafe(direct);

    var requested = runAction(new CapabilityActionRequest(
        "action-useradmin-request-identity-relink",
        "action-useradmin-request-identity-relink",
        "user_admin.identity_relink.request",
        "user_admin.identity_relink.request",
        Map.of("accountId", "identity.case@example.test", "membershipId", "membership-identity-case", "reason", "provider mismatch browser smoke"),
        "idem-identity-review-request",
        SELECTED_CONTEXT_ID,
        direct.surfaceId(),
        "corr-identity-review-request"));
    assertEquals("approval-required", requested.status());
    assertEquals("surface-user-admin-identity-exception-review", requested.resultSurface().surfaceId());
    assertEquals("needs-review", requested.resultSurface().data().get("lifecycleStatus"));
    assertEquals("corr-identity-review-request", requested.correlationId());
    assertTrue(requested.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-identity-relink")));
    assertTrue(requested.resultSurface().toString().contains("provider-boundary:redacted"));
    assertEquals(true, requested.resultSurface().data().get("noDirectMutation"));
    assertBrowserSafe(requested.resultSurface());

    var recoveryId = String.valueOf(requested.resultSurface().data().get("recoveryId"));
    assertFalse(recoveryId.isBlank());

    var read = runAction(new CapabilityActionRequest(
        "action-useradmin-read-identity-relink",
        "action-useradmin-read-identity-relink",
        "user_admin.identity_relink.review",
        "user_admin.identity_relink.review",
        Map.of("recoveryId", recoveryId),
        null,
        SELECTED_CONTEXT_ID,
        requested.resultSurface().surfaceId(),
        "corr-identity-review-read"));
    assertEquals("accepted", read.status());
    assertEquals(recoveryId, read.resultSurface().data().get("recoveryId"));
    assertEquals("needs-review", read.resultSurface().data().get("lifecycleStatus"));
    assertTrue(read.resultSurface().toString().contains("trace-useradmin-identity-relink"));
    assertBrowserSafe(read.resultSurface());

    var approved = runAction(new CapabilityActionRequest(
        "action-useradmin-approve-identity-relink",
        "action-useradmin-approve-identity-relink",
        "user_admin.identity_relink.approve",
        "user_admin.identity_relink.approve",
        Map.of("recoveryId", recoveryId, "reason", "reviewed identity evidence", "approvalRef", "approval-identity-review-smoke"),
        "idem-identity-review-approve",
        SELECTED_CONTEXT_ID,
        read.resultSurface().surfaceId(),
        "corr-identity-review-approve"));
    assertEquals("approved-for-recovery", approved.status());
    assertEquals("approved-for-recovery", approved.resultSurface().data().get("lifecycleStatus"));
    assertTrue(approved.resultSurface().toString().contains("completionAllowedAfterApproval=true"));
    assertBrowserSafe(approved.resultSurface());

    var replayApprove = runAction(new CapabilityActionRequest(
        "action-useradmin-approve-identity-relink",
        "action-useradmin-approve-identity-relink",
        "user_admin.identity_relink.approve",
        "user_admin.identity_relink.approve",
        Map.of("recoveryId", recoveryId, "reason", "idempotent replay", "approvalRef", "approval-identity-review-smoke"),
        "idem-identity-review-approve-replay",
        SELECTED_CONTEXT_ID,
        approved.resultSurface().surfaceId(),
        "corr-identity-review-approve-replay"));
    assertEquals("no-op", replayApprove.status());
    assertEquals("approved-for-recovery", replayApprove.resultSurface().data().get("lifecycleStatus"));
    assertBrowserSafe(replayApprove.resultSurface());

    var completed = runAction(new CapabilityActionRequest(
        "action-useradmin-complete-identity-relink",
        "action-useradmin-complete-identity-relink",
        "user_admin.identity_relink.complete",
        "user_admin.identity_relink.complete",
        Map.of("accountId", "identity.case@example.test", "approvalRef", "approval-identity-review-smoke"),
        "idem-identity-review-complete",
        SELECTED_CONTEXT_ID,
        approved.resultSurface().surfaceId(),
        "corr-identity-review-complete"));
    assertEquals("accepted", completed.status());
    assertEquals("completed", completed.resultSurface().data().get("lifecycleStatus"));
    assertTrue(completed.message().contains("provider-boundary redaction"));
    assertBrowserSafe(completed.resultSurface());

    var afterMembership = repository.findMembership("membership-identity-case").orElseThrow();
    var afterAccount = repository.findAccountByEmail("identity.case@example.test").orElseThrow();
    assertEquals(beforeMembership.roles(), afterMembership.roles(), "Identity recovery must not mutate roles.");
    assertEquals(beforeMembership.status(), afterMembership.status(), "Identity recovery must not mutate membership lifecycle.");
    assertEquals(beforeMembership.supportAccess(), afterMembership.supportAccess(), "Identity recovery must not mutate support access.");
    assertEquals(beforeAccount.status(), afterAccount.status(), "Identity recovery must not disable or reactivate the account.");

    var denyRequested = runAction(new CapabilityActionRequest(
        "action-useradmin-request-identity-relink",
        "action-useradmin-request-identity-relink",
        "user_admin.identity_relink.request",
        "user_admin.identity_relink.request",
        Map.of("accountId", "identity.deny@example.test", "membershipId", "membership-identity-deny", "reason", "provider mismatch deny smoke"),
        "idem-identity-review-deny-request",
        SELECTED_CONTEXT_ID,
        direct.surfaceId(),
        "corr-identity-review-deny-request"));
    var denied = runAction(new CapabilityActionRequest(
        "action-useradmin-deny-identity-relink",
        "action-useradmin-deny-identity-relink",
        "user_admin.identity_relink.deny",
        "user_admin.identity_relink.deny",
        Map.of("recoveryId", String.valueOf(denyRequested.resultSurface().data().get("recoveryId")), "reason", "not enough verified evidence"),
        "idem-identity-review-deny",
        SELECTED_CONTEXT_ID,
        denyRequested.resultSurface().surfaceId(),
        "corr-identity-review-deny"));
    assertEquals("denied", denied.status());
    assertEquals("denied", denied.resultSurface().data().get("lifecycleStatus"));
    assertTrue(denied.message().contains("not mutated"));
    assertBrowserSafe(denied.resultSurface());

    assertThrows(RuntimeException.class, () -> runActionAs(new CapabilityActionRequest(
        "action-useradmin-read-identity-relink",
        "action-useradmin-read-identity-relink",
        "user_admin.identity_relink.review",
        "user_admin.identity_relink.review",
        Map.of("recoveryId", recoveryId),
        null,
        "membership-member",
        completed.resultSurface().surfaceId(),
        "corr-identity-review-member-denied"), "workos-member", "member@example.test", "Member User", "membership-member"),
        "Regular members must not read tenant identity-exception recovery through the protected action API.");
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
            "user_admin.support_access.grant_revoke_extend",
            "user_admin.support_access.grant_revoke_extend",
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
            "user_admin.support_access.grant_revoke_extend",
            "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.list_members",
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
        "user_admin.list_members",
        "user_admin.list_members",
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
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
        Map.of("accountId", "admin@example.test", "membershipId", SELECTED_CONTEXT_ID),
        null,
        "membership-member",
        submitted.resultSurface().surfaceId(),
        "corr-user-support-revoke-member-denied"), "workos-member", "member@example.test", "Member User", "membership-member"),
        "Regular members must not open support access revoke confirmation through the protected action API.");

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-open-user-admin-support-access-grant",
        "action-open-user-admin-support-access-grant",
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
        Map.of("accountId", "hidden@example.test", "membershipId", "membership-hidden"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-user-support-grant-hidden-open")),
        "Hidden support-access grant targets must be denied without a successful browser payload.");

    assertThrows(RuntimeException.class, () -> runActionAs(new CapabilityActionRequest(
        "action-open-user-admin-support-access-grant",
        "action-open-user-admin-support-access-grant",
        "user_admin.support_access.grant_revoke_extend",
        "user_admin.support_access.grant_revoke_extend",
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
        "user_admin.list_members",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.acceptance_status.read",
        "user_admin.acceptance_status.read",
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
        "user_admin.resend_invitation",
        "user_admin.resend_invitation",
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
        "user_admin.resend_invitation",
        "user_admin.resend_invitation",
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
        "user_admin.revoke_invitation",
        "user_admin.revoke_invitation",
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
        "user_admin.revoke_invitation",
        "user_admin.revoke_invitation",
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
        "user_admin.revoke_invitation",
        "user_admin.revoke_invitation",
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
        "user_admin.acceptance_status.read",
        "user_admin.acceptance_status.read",
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
        "user_admin.acceptance_status.read",
        "user_admin.acceptance_status.read",
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
            "user_admin.resend_invitation",
            "user_admin.resend_invitation",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.resend_invitation",
        "user_admin.resend_invitation",
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
        "user_admin.resend_invitation",
        "user_admin.resend_invitation",
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
        "user_admin.resend_invitation",
        "user_admin.resend_invitation",
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
        "user_admin.resend_invitation",
        "user_admin.resend_invitation",
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
        "user_admin.resend_invitation",
        "user_admin.resend_invitation",
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
            "user_admin.revoke_invitation",
            "user_admin.revoke_invitation",
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
        "user_admin.invite_user",
        "user_admin.invite_user",
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
        "user_admin.revoke_invitation",
        "user_admin.revoke_invitation",
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
        "user_admin.revoke_invitation",
        "user_admin.revoke_invitation",
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
        "user_admin.revoke_invitation",
        "user_admin.revoke_invitation",
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
        "user_admin.revoke_invitation",
        "user_admin.revoke_invitation",
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
        "user_admin.revoke_invitation",
        "user_admin.revoke_invitation",
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
  void protectedWorkstreamApiExercisesUserAdminOrganizationDirectoryRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-directory")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .responseBodyAs(String.class)
        .invoke(), "Organization Directory surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-detail")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .responseBodyAs(String.class)
        .invoke(), "Organization Detail surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-create")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .responseBodyAs(String.class)
        .invoke(), "Organization Create surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-rename")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .responseBodyAs(String.class)
        .invoke(), "Organization Rename surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-directory-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-user-admin-show-organizations",
            "user-admin.show-organizations",
            "manage-organizations",
            "saas_owner.organization.list",
            Map.of("scope", "saas-owner"),
            null,
            "membership-owner",
            "surface-user-admin-saas-owner-dashboard",
            "corr-org-directory-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Organization Directory action path must reject missing bearer tokens.");

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "SaaS Owner"))
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-directory-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals("membership-owner", bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertBrowserSafe(bootstrap.body());

    var dashboard = getSurfaceAs("surface-user-admin-dashboard", "corr-org-directory-dashboard", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-saas-owner-dashboard", dashboard.surfaceId());
    assertEquals("user_admin.saas_owner_dashboard.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-show-organizations")));
    assertTrue(dashboard.toString().contains("manage-organizations"));
    assertTrue(dashboard.toString().contains("saas_owner.organization.list"));
    assertBrowserSafe(dashboard);

    var directory = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-organizations",
        "user-admin.show-organizations",
        "manage-organizations",
        "saas_owner.organization.list",
        Map.of("scope", "saas-owner"),
        null,
        "membership-owner",
        dashboard.surfaceId(),
        "corr-org-directory-open"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", directory.status());
    assertEquals("corr-org-directory-open", directory.correlationId());
    assertEquals("surface-user-admin-organization-directory", directory.resultSurface().surfaceId());
    assertEquals("list-search", directory.resultSurface().surfaceType());
    assertEquals("user_admin.organization_directory.v1", directory.resultSurface().data().get("surfaceContract"));
    assertEquals("saas_owner", directory.resultSurface().data().get("scopeType"));
    assertEquals("corr-org-directory-open", directory.resultSurface().correlationId());
    assertTrue(directory.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-admin")));
    assertTrue(directory.resultSurface().toString().contains("Starter Tenant"));
    assertTrue(directory.resultSurface().toString().contains("tenant-starter"));
    assertTrue(directory.resultSurface().toString().contains("targetSurfaceId=surface-user-admin-organization-detail"));
    assertTrue(directory.resultSurface().toString().contains("action-organization-read"));
    assertTrue(directory.resultSurface().toString().contains("action-open-organization-create"));
    assertTrue(directory.resultSurface().toString().contains("Tenant lifecycle boundary"));
    assertTrue(directory.resultSurface().toString().contains("tenant-app-data-redacted"));
    assertTrue(directory.resultSurface().toString().contains("provider-secrets-redacted"));
    assertBrowserSafe(directory.resultSurface());

    var directDirectory = getSurfaceAs("surface-user-admin-organization-directory", "corr-org-directory-direct", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-directory", directDirectory.surfaceId());
    assertEquals("user_admin.organization_directory.v1", directDirectory.data().get("surfaceContract"));
    assertEquals("corr-org-directory-direct", directDirectory.correlationId());
    assertTrue(directDirectory.toString().contains("action-organization-list"));
    assertTrue(directDirectory.toString().contains("trace-organization-list"));
    assertBrowserSafe(directDirectory);

    var createForm = getSurfaceAs("surface-user-admin-organization-create", "corr-org-create-direct", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-create", createForm.surfaceId());
    assertEquals("create-form", createForm.surfaceType());
    assertEquals("user_admin.organization_create.v1", createForm.data().get("surfaceContract"));
    assertTrue(createForm.toString().contains("action-submit-organization-create"));
    assertTrue(createForm.toString().contains("validationPolicy"));
    assertTrue(createForm.toString().contains("creationBoundary"));
    assertTrue(createForm.toString().contains("Organization Admin bootstrap remains a separate"));
    assertBrowserSafe(createForm);

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-create-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-submit-organization-create",
            "user-admin.submit-organization-create",
            "manage-organizations",
            "saas_owner.tenant.manage",
            Map.of("organizationName", "Bearerless Org", "reason", "missing bearer must fail"),
            "idem-org-create-missing-bearer",
            "membership-owner",
            createForm.surfaceId(),
            "corr-org-create-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Organization Create submit action must reject missing bearer tokens.");

    var invalidCreate = runActionAs(new CapabilityActionRequest(
        "action-submit-organization-create",
        "user-admin.submit-organization-create",
        "manage-organizations",
        "saas_owner.tenant.manage",
        Map.of("organizationName", "A", "reason", "short name should fail closed"),
        "idem-org-create-validation-short-name",
        "membership-owner",
        createForm.surfaceId(),
        "corr-org-create-validation-short-name"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("denied", invalidCreate.status());
    assertEquals("surface-user-admin-system-message", invalidCreate.resultSurface().surfaceId());
    assertTrue(invalidCreate.resultSurface().toString().contains("organization-name-too-short"));
    assertTrue(invalidCreate.resultSurface().surfaceType().contains("system"));
    assertBrowserSafe(invalidCreate.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-organization-create",
        "corr-org-create-tenant-direct-denied",
        "workos-admin",
        "admin@example.test",
        "Tenant Admin",
        SELECTED_CONTEXT_ID),
        "Tenant Admin selected contexts must not directly load the SaaS Owner Organization Create form.");

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-submit-organization-create",
        "user-admin.submit-organization-create",
        "manage-organizations",
        "saas_owner.tenant.manage",
        Map.of("organizationName", "Tenant Admin Forbidden Org", "reason", "tenant admin must not create organizations"),
        "idem-org-create-tenant-denied",
        SELECTED_CONTEXT_ID,
        createForm.surfaceId(),
        "corr-org-create-tenant-denied")),
        "Tenant Admin selected contexts must not submit SaaS Owner Organization creation.");

    var createdOrganization = runActionAs(new CapabilityActionRequest(
        "action-submit-organization-create",
        "user-admin.submit-organization-create",
        "manage-organizations",
        "saas_owner.tenant.manage",
        Map.of("organizationName", "Acme Launch Org", "reason", "protected browser smoke create"),
        "idem-org-create-smoke",
        "membership-owner",
        createForm.surfaceId(),
        "corr-org-create-submit"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", createdOrganization.status());
    assertEquals("surface-user-admin-organization-detail", createdOrganization.resultSurface().surfaceId());
    assertTrue(createdOrganization.message().contains("Organization created"));
    assertTrue(createdOrganization.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-create")));
    assertTrue(createdOrganization.resultSurface().toString().contains("Acme Launch Org"));
    assertTrue(createdOrganization.resultSurface().toString().contains("visibleActions=[read, rename, suspend]"));
    assertBrowserSafe(createdOrganization.resultSurface());

    var createReplay = runActionAs(new CapabilityActionRequest(
        "action-submit-organization-create",
        "user-admin.submit-organization-create",
        "manage-organizations",
        "saas_owner.tenant.manage",
        Map.of("organizationName", "Acme Launch Org", "reason", "protected browser smoke replay"),
        "idem-org-create-smoke",
        "membership-owner",
        createForm.surfaceId(),
        "corr-org-create-replay"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("no-op", createReplay.status());
    assertEquals("surface-user-admin-organization-detail", createReplay.resultSurface().surfaceId());
    assertTrue(createReplay.message().contains("replay"));
    assertBrowserSafe(createReplay.resultSurface());

    var duplicateOrganization = runActionAs(new CapabilityActionRequest(
        "action-submit-organization-create",
        "user-admin.submit-organization-create",
        "manage-organizations",
        "saas_owner.tenant.manage",
        Map.of("organizationName", "Acme Launch Org", "reason", "protected browser smoke duplicate visible name"),
        "idem-org-create-duplicate-visible-name",
        "membership-owner",
        createForm.surfaceId(),
        "corr-org-create-duplicate-visible-name"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("no-op", duplicateOrganization.status());
    assertEquals("surface-user-admin-organization-detail", duplicateOrganization.resultSurface().surfaceId());
    assertTrue(duplicateOrganization.message().contains("visible Organization already uses this name"));
    assertTrue(duplicateOrganization.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-create")));
    assertTrue(duplicateOrganization.resultSurface().toString().contains("Acme Launch Org"));
    assertBrowserSafe(duplicateOrganization.resultSurface());

    var filteredEmpty = runActionAs(new CapabilityActionRequest(
        "action-organization-list",
        "action-organization-list",
        "saas_owner.organization.list",
        "saas_owner.organization.list",
        Map.of("query", "no-such-organization", "status", "active"),
        null,
        "membership-owner",
        directory.resultSurface().surfaceId(),
        "corr-org-directory-filter-empty"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", filteredEmpty.status());
    assertEquals("surface-user-admin-organization-directory", filteredEmpty.resultSurface().surfaceId());
    assertTrue(filteredEmpty.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-list")));
    assertTrue(String.valueOf(filteredEmpty.resultSurface().data().get("pageInfo")).contains("visibleCount=0"));
    assertTrue(filteredEmpty.resultSurface().toString().contains("empty"));
    assertTrue(filteredEmpty.resultSurface().toString().contains("No Organizations are visible"));
    assertFalse(filteredEmpty.resultSurface().toString().contains("tenant-hidden"));
    assertBrowserSafe(filteredEmpty.resultSurface());

    var detail = runActionAs(new CapabilityActionRequest(
        "action-organization-read",
        "action-organization-read",
        "saas_owner.organization.read",
        "saas_owner.organization.read",
        Map.of("organizationId", TENANT_ID),
        null,
        "membership-owner",
        directory.resultSurface().surfaceId(),
        "corr-org-directory-read-detail"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", detail.status());
    assertEquals("surface-user-admin-organization-detail", detail.resultSurface().surfaceId());
    assertEquals("show-inspection", detail.resultSurface().surfaceType());
    assertEquals("user_admin.organization_detail.v1", detail.resultSurface().data().get("surfaceContract"));
    assertTrue(detail.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-read")));
    assertTrue(detail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-organizations"));
    assertTrue(detail.resultSurface().toString().contains("Back to organizations"));
    assertTrue(detail.resultSurface().toString().contains("action-user-admin-show-organization-admins"));
    assertTrue(detail.resultSurface().toString().contains("action-open-organization-admin-invitation-create"));
    assertTrue(detail.resultSurface().toString().contains("availableTaskActions"));
    assertTrue(detail.resultSurface().toString().contains("organizationDetail"));
    assertTrue(detail.resultSurface().toString().contains("visibleActions=[read, rename, suspend]"));
    assertTrue(detail.resultSurface().toString().contains("does not grant tenant/customer application-data access"));
    assertBrowserSafe(detail.resultSurface());

    var directDetail = getSurfaceAs("surface-user-admin-organization-detail", "corr-org-detail-direct", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-detail", directDetail.surfaceId());
    assertEquals("show-inspection", directDetail.surfaceType());
    assertEquals("user_admin.organization_detail.v1", directDetail.data().get("surfaceContract"));
    assertEquals("corr-org-detail-direct", directDetail.correlationId());
    assertTrue(directDetail.toString().contains("action-open-organization-rename"));
    assertTrue(directDetail.toString().contains("action-open-organization-suspend"));
    assertTrue(directDetail.toString().contains("action-open-organization-reactivate"));
    assertTrue(directDetail.toString().contains("action-user-admin-show-organization-admins"));
    assertTrue(directDetail.toString().contains("action-open-organization-admin-invitation-create"));
    assertTrue(directDetail.toString().contains("providerBlockedCount=0"));
    assertBrowserSafe(directDetail);

    var directRenameWithoutTarget = getSurfaceAs("surface-user-admin-organization-rename", "corr-org-rename-direct-missing-target", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-rename", directRenameWithoutTarget.surfaceId());
    assertEquals("edit-form", directRenameWithoutTarget.surfaceType());
    assertEquals("user_admin.organization_rename.v1", directRenameWithoutTarget.data().get("surfaceContract"));
    assertEquals("missing-target", directRenameWithoutTarget.data().get("formState"));
    assertFalse(directRenameWithoutTarget.toString().contains("tenant-hidden"));
    assertTrue(directRenameWithoutTarget.toString().contains("noFakeSuccess=true"));
    assertBrowserSafe(directRenameWithoutTarget);

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-organization-rename",
        "corr-org-rename-tenant-direct-denied",
        "workos-admin",
        "admin@example.test",
        "Tenant Admin",
        SELECTED_CONTEXT_ID),
        "Tenant Admin selected contexts must not directly load the SaaS Owner Organization Rename form.");

    var renameTask = runActionAs(new CapabilityActionRequest(
        "action-open-organization-rename",
        "action-open-organization-rename",
        "manage-organizations",
        "saas_owner.organization.rename",
        Map.of("organizationId", TENANT_ID),
        null,
        "membership-owner",
        detail.resultSurface().surfaceId(),
        "corr-org-detail-open-rename"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", renameTask.status());
    assertEquals("surface-user-admin-organization-rename", renameTask.resultSurface().surfaceId());
    assertEquals("edit-form", renameTask.resultSurface().surfaceType());
    assertEquals("user_admin.organization_rename.v1", renameTask.resultSurface().data().get("surfaceContract"));
    assertTrue(renameTask.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-organizations"));
    assertTrue(renameTask.resultSurface().toString().contains("organizationDetail"));
    assertTrue(renameTask.resultSurface().toString().contains("action-submit-organization-rename"));
    assertTrue(renameTask.resultSurface().toString().contains("currentOrganizationName=Starter Tenant"));
    assertBrowserSafe(renameTask.resultSurface());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-rename-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-submit-organization-rename",
            "user-admin.submit-organization-rename",
            "manage-organizations",
            "saas_owner.organization.rename",
            Map.of("organizationId", TENANT_ID, "organizationName", "Bearerless Rename", "reason", "missing bearer must fail"),
            "idem-org-rename-missing-bearer",
            "membership-owner",
            renameTask.resultSurface().surfaceId(),
            "corr-org-rename-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Organization Rename submit action must reject missing bearer tokens.");

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-submit-organization-rename",
        "user-admin.submit-organization-rename",
        "manage-organizations",
        "saas_owner.organization.rename",
        Map.of("organizationId", TENANT_ID, "organizationName", "Tenant Admin Forbidden Rename", "reason", "tenant admin must not rename organizations"),
        "idem-org-rename-tenant-denied",
        SELECTED_CONTEXT_ID,
        renameTask.resultSurface().surfaceId(),
        "corr-org-rename-tenant-denied")),
        "Tenant Admin selected contexts must not submit SaaS Owner Organization rename.");

    var renameDuplicate = runActionAs(new CapabilityActionRequest(
        "action-submit-organization-rename",
        "user-admin.submit-organization-rename",
        "manage-organizations",
        "saas_owner.organization.rename",
        Map.of("organizationId", TENANT_ID, "organizationName", "Acme Launch Org", "reason", "visible duplicate should fail closed"),
        "idem-org-detail-rename-duplicate",
        "membership-owner",
        renameTask.resultSurface().surfaceId(),
        "corr-org-detail-rename-duplicate"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("validation-error", renameDuplicate.status());
    assertEquals("surface-user-admin-organization-detail", renameDuplicate.resultSurface().surfaceId());
    assertTrue(renameDuplicate.message().contains("visible Organization already uses this name"));
    assertTrue(renameDuplicate.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-rename")));
    assertFalse(renameDuplicate.resultSurface().toString().contains("tenant-hidden"));
    assertBrowserSafe(renameDuplicate.resultSurface());

    var renameNoOp = runActionAs(new CapabilityActionRequest(
        "action-organization-rename",
        "action-organization-rename",
        "saas_owner.tenant.manage",
        "saas_owner.tenant.manage",
        Map.of("organizationId", TENANT_ID, "organizationName", "Starter Tenant", "reason", "browser smoke no-op rename"),
        "idem-org-detail-rename-noop",
        "membership-owner",
        renameTask.resultSurface().surfaceId(),
        "corr-org-detail-rename-noop"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("no-op", renameNoOp.status());
    assertEquals("surface-user-admin-organization-detail", renameNoOp.resultSurface().surfaceId());
    assertTrue(renameNoOp.message().contains("already matches"));
    assertTrue(renameNoOp.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-rename")));
    assertTrue(renameNoOp.resultSurface().toString().contains("status=active"));
    assertBrowserSafe(renameNoOp.resultSurface());

    var renamed = runActionAs(new CapabilityActionRequest(
        "action-submit-organization-rename",
        "user-admin.submit-organization-rename",
        "manage-organizations",
        "saas_owner.organization.rename",
        Map.of("organizationId", TENANT_ID, "organizationName", "Starter Tenant Renamed", "reason", "protected browser smoke rename"),
        "idem-org-detail-rename-accepted",
        "membership-owner",
        renameTask.resultSurface().surfaceId(),
        "corr-org-detail-rename-accepted"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", renamed.status());
    assertEquals("surface-user-admin-organization-detail", renamed.resultSurface().surfaceId());
    assertTrue(renamed.message().contains("display name updated"));
    assertTrue(renamed.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-rename")));
    assertTrue(renamed.resultSurface().toString().contains("Starter Tenant Renamed"));
    assertBrowserSafe(renamed.resultSurface());

    var suspendTask = runActionAs(new CapabilityActionRequest(
        "action-open-organization-suspend",
        "action-open-organization-suspend",
        "manage-organizations",
        "saas_owner.organization.suspend",
        Map.of("organizationId", TENANT_ID),
        null,
        "membership-owner",
        detail.resultSurface().surfaceId(),
        "corr-org-detail-open-suspend"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", suspendTask.status());
    assertEquals("surface-user-admin-organization-suspend-confirmation", suspendTask.resultSurface().surfaceId());
    assertEquals("destructive-lifecycle-confirmation", suspendTask.resultSurface().surfaceType());
    assertEquals("user_admin.organization_suspend_confirmation.v1", suspendTask.resultSurface().data().get("surfaceContract"));
    assertTrue(suspendTask.resultSurface().toString().contains("confirmationPhrase=SUSPEND"));
    assertTrue(suspendTask.resultSurface().toString().contains("saas_owner.organization.suspend"));
    assertTrue(suspendTask.resultSurface().toString().contains("visibleActions=[read, rename, suspend]"));
    assertBrowserSafe(suspendTask.resultSurface());

    var directSuspendWithoutTarget = getSurfaceAs("surface-user-admin-organization-suspend-confirmation", "corr-org-suspend-direct-missing-target", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-suspend-confirmation", directSuspendWithoutTarget.surfaceId());
    assertEquals("destructive-lifecycle-confirmation", directSuspendWithoutTarget.surfaceType());
    assertEquals("user_admin.organization_suspend_confirmation.v1", directSuspendWithoutTarget.data().get("surfaceContract"));
    assertEquals("missing-target", directSuspendWithoutTarget.data().get("formState"));
    assertTrue(directSuspendWithoutTarget.toString().contains("noFakeSuccess=true"));
    assertFalse(directSuspendWithoutTarget.toString().contains("tenant-hidden"));
    assertBrowserSafe(directSuspendWithoutTarget);

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-suspend-confirmation")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-suspend-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Organization suspend confirmation direct load must reject missing bearer tokens.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-organization-suspend-confirmation",
        "corr-org-suspend-tenant-direct-denied",
        "workos-admin",
        "admin@example.test",
        "Tenant Admin",
        SELECTED_CONTEXT_ID),
        "Tenant Admin selected contexts must not directly load the SaaS Owner Organization suspend confirmation.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-suspend-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-organization-suspend",
            "action-organization-suspend",
            "manage-organizations",
            "saas_owner.organization.suspend",
            Map.of("organizationId", TENANT_ID, "reason", "missing bearer must fail", "confirmationPhrase", "SUSPEND"),
            "idem-org-suspend-missing-bearer",
            "membership-owner",
            suspendTask.resultSurface().surfaceId(),
            "corr-org-suspend-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Organization suspend submit action must reject missing bearer tokens.");

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-organization-suspend",
        "action-organization-suspend",
        "manage-organizations",
        "saas_owner.organization.suspend",
        Map.of("organizationId", TENANT_ID, "reason", "tenant admin must not suspend Organizations", "confirmationPhrase", "SUSPEND"),
        "idem-org-suspend-tenant-denied",
        SELECTED_CONTEXT_ID,
        suspendTask.resultSurface().surfaceId(),
        "corr-org-suspend-tenant-denied")),
        "Tenant Admin selected contexts must not submit SaaS Owner Organization suspension.");

    var missingReason = runActionAs(new CapabilityActionRequest(
        "action-organization-suspend",
        "action-organization-suspend",
        "manage-organizations",
        "saas_owner.organization.suspend",
        Map.of("organizationId", TENANT_ID, "reason", " ", "confirmationPhrase", "SUSPEND"),
        "idem-org-suspend-missing-reason",
        "membership-owner",
        suspendTask.resultSurface().surfaceId(),
        "corr-org-suspend-missing-reason"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("validation-error", missingReason.status());
    assertEquals("surface-user-admin-system-message", missingReason.resultSurface().surfaceId());
    assertTrue(missingReason.resultSurface().toString().contains("reason-required"));
    assertFalse(missingReason.resultSurface().toString().contains("status=suspended"));
    assertBrowserSafe(missingReason.resultSurface());

    var missingConfirmation = runActionAs(new CapabilityActionRequest(
        "action-organization-suspend",
        "action-organization-suspend",
        "manage-organizations",
        "saas_owner.organization.suspend",
        Map.of("organizationId", TENANT_ID, "reason", "protected browser smoke missing confirmation", "confirmationPhrase", ""),
        "idem-org-suspend-missing-confirmation",
        "membership-owner",
        suspendTask.resultSurface().surfaceId(),
        "corr-org-suspend-missing-confirmation"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("validation-error", missingConfirmation.status());
    assertEquals("surface-user-admin-system-message", missingConfirmation.resultSurface().surfaceId());
    assertTrue(missingConfirmation.resultSurface().toString().contains("confirmation-phrase-required"));
    assertFalse(missingConfirmation.resultSurface().toString().contains("status=suspended"));
    assertBrowserSafe(missingConfirmation.resultSurface());

    var suspended = runActionAs(new CapabilityActionRequest(
        "action-organization-suspend",
        "action-organization-suspend",
        "manage-organizations",
        "saas_owner.organization.suspend",
        Map.of("organizationId", TENANT_ID, "reason", "protected browser smoke suspend", "confirmationPhrase", "SUSPEND"),
        "idem-org-detail-suspend",
        "membership-owner",
        suspendTask.resultSurface().surfaceId(),
        "corr-org-detail-suspend"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", suspended.status());
    assertEquals("surface-user-admin-organization-detail", suspended.resultSurface().surfaceId());
    assertTrue(suspended.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-suspend")));
    assertTrue(suspended.resultSurface().toString().contains("status=suspended"));
    assertTrue(suspended.resultSurface().toString().contains("visibleActions=[read, rename, reactivate]"));
    assertFalse(suspended.resultSurface().toString().contains("tenant application data"));
    assertBrowserSafe(suspended.resultSurface());

    var staleSuspend = runActionAs(new CapabilityActionRequest(
        "action-open-organization-suspend",
        "action-open-organization-suspend",
        "manage-organizations",
        "saas_owner.organization.suspend",
        Map.of("organizationId", TENANT_ID),
        null,
        "membership-owner",
        suspended.resultSurface().surfaceId(),
        "corr-org-detail-stale-suspend"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("denied", staleSuspend.status());
    assertTrue(staleSuspend.resultSurface().surfaceId().contains("denied"));
    assertTrue(staleSuspend.resultSurface().surfaceType().contains("system"));
    assertFalse(staleSuspend.resultSurface().toString().contains("missing-organization-never-seeded"));
    assertBrowserSafe(staleSuspend.resultSurface());

    var reactivateTask = runActionAs(new CapabilityActionRequest(
        "action-open-organization-reactivate",
        "action-open-organization-reactivate",
        "manage-organizations",
        "saas_owner.organization.reactivate",
        Map.of("organizationId", TENANT_ID),
        null,
        "membership-owner",
        suspended.resultSurface().surfaceId(),
        "corr-org-detail-open-reactivate"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", reactivateTask.status());
    assertEquals("surface-user-admin-organization-reactivate-confirmation", reactivateTask.resultSurface().surfaceId());
    assertEquals("lifecycle-confirmation", reactivateTask.resultSurface().surfaceType());
    assertEquals("user_admin.organization_reactivate_confirmation.v1", reactivateTask.resultSurface().data().get("surfaceContract"));
    assertTrue(reactivateTask.resultSurface().toString().contains("confirmationPhrase=REACTIVATE"));
    assertTrue(reactivateTask.resultSurface().toString().contains("saas_owner.organization.reactivate"));
    assertBrowserSafe(reactivateTask.resultSurface());

    var directReactivateWithoutTarget = getSurfaceAs("surface-user-admin-organization-reactivate-confirmation", "corr-org-reactivate-direct-missing-target", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-reactivate-confirmation", directReactivateWithoutTarget.surfaceId());
    assertEquals("lifecycle-confirmation", directReactivateWithoutTarget.surfaceType());
    assertEquals("user_admin.organization_reactivate_confirmation.v1", directReactivateWithoutTarget.data().get("surfaceContract"));
    assertEquals("missing-target", directReactivateWithoutTarget.data().get("formState"));
    assertTrue(directReactivateWithoutTarget.toString().contains("noFakeSuccess=true"));
    assertFalse(directReactivateWithoutTarget.toString().contains("tenant-hidden"));
    assertBrowserSafe(directReactivateWithoutTarget);

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-reactivate-confirmation")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-reactivate-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Organization reactivate confirmation direct load must reject missing bearer tokens.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-organization-reactivate-confirmation",
        "corr-org-reactivate-tenant-direct-denied",
        "workos-admin",
        "admin@example.test",
        "Tenant Admin",
        SELECTED_CONTEXT_ID),
        "Tenant Admin selected contexts must not directly load the SaaS Owner Organization reactivate confirmation.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-reactivate-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-organization-reactivate",
            "action-organization-reactivate",
            "manage-organizations",
            "saas_owner.organization.reactivate",
            Map.of("organizationId", TENANT_ID, "reason", "missing bearer must fail", "confirmationPhrase", "REACTIVATE"),
            "idem-org-reactivate-missing-bearer",
            "membership-owner",
            reactivateTask.resultSurface().surfaceId(),
            "corr-org-reactivate-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Organization reactivate submit action must reject missing bearer tokens.");

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-organization-reactivate",
        "action-organization-reactivate",
        "manage-organizations",
        "saas_owner.organization.reactivate",
        Map.of("organizationId", TENANT_ID, "reason", "tenant admin must not reactivate Organizations", "confirmationPhrase", "REACTIVATE"),
        "idem-org-reactivate-tenant-denied",
        SELECTED_CONTEXT_ID,
        reactivateTask.resultSurface().surfaceId(),
        "corr-org-reactivate-tenant-denied")),
        "Tenant Admin selected contexts must not submit SaaS Owner Organization reactivation.");

    var missingReactivateReason = runActionAs(new CapabilityActionRequest(
        "action-organization-reactivate",
        "action-organization-reactivate",
        "manage-organizations",
        "saas_owner.organization.reactivate",
        Map.of("organizationId", TENANT_ID, "reason", " ", "confirmationPhrase", "REACTIVATE"),
        "idem-org-reactivate-missing-reason",
        "membership-owner",
        reactivateTask.resultSurface().surfaceId(),
        "corr-org-reactivate-missing-reason"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("validation-error", missingReactivateReason.status());
    assertEquals("surface-user-admin-system-message", missingReactivateReason.resultSurface().surfaceId());
    assertTrue(missingReactivateReason.resultSurface().toString().contains("reason-required"));
    assertFalse(missingReactivateReason.resultSurface().toString().contains("status=active"));
    assertBrowserSafe(missingReactivateReason.resultSurface());

    var missingReactivateConfirmation = runActionAs(new CapabilityActionRequest(
        "action-organization-reactivate",
        "action-organization-reactivate",
        "manage-organizations",
        "saas_owner.organization.reactivate",
        Map.of("organizationId", TENANT_ID, "reason", "protected browser smoke missing confirmation", "confirmationPhrase", ""),
        "idem-org-reactivate-missing-confirmation",
        "membership-owner",
        reactivateTask.resultSurface().surfaceId(),
        "corr-org-reactivate-missing-confirmation"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("validation-error", missingReactivateConfirmation.status());
    assertEquals("surface-user-admin-system-message", missingReactivateConfirmation.resultSurface().surfaceId());
    assertTrue(missingReactivateConfirmation.resultSurface().toString().contains("confirmation-phrase-required"));
    assertFalse(missingReactivateConfirmation.resultSurface().toString().contains("status=active"));
    assertBrowserSafe(missingReactivateConfirmation.resultSurface());

    var reactivated = runActionAs(new CapabilityActionRequest(
        "action-organization-reactivate",
        "action-organization-reactivate",
        "manage-organizations",
        "saas_owner.organization.reactivate",
        Map.of("organizationId", TENANT_ID, "reason", "protected browser smoke reactivate", "confirmationPhrase", "REACTIVATE"),
        "idem-org-detail-reactivate",
        "membership-owner",
        reactivateTask.resultSurface().surfaceId(),
        "corr-org-detail-reactivate"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", reactivated.status());
    assertEquals("surface-user-admin-organization-detail", reactivated.resultSurface().surfaceId());
    assertTrue(reactivated.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-reactivate")));
    assertTrue(reactivated.resultSurface().toString().contains("status=active"));
    assertBrowserSafe(reactivated.resultSurface());

    var reactivateNoOp = runActionAs(new CapabilityActionRequest(
        "action-organization-reactivate",
        "action-organization-reactivate",
        "manage-organizations",
        "saas_owner.organization.reactivate",
        Map.of("organizationId", TENANT_ID, "reason", "protected browser smoke idempotent reactivate replay", "confirmationPhrase", "REACTIVATE"),
        "idem-org-detail-reactivate-noop",
        "membership-owner",
        reactivated.resultSurface().surfaceId(),
        "corr-org-detail-reactivate-noop"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("no-op", reactivateNoOp.status());
    assertEquals("surface-user-admin-organization-detail", reactivateNoOp.resultSurface().surfaceId());
    assertTrue(reactivateNoOp.message().contains("already active"));
    assertTrue(reactivateNoOp.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-reactivate")));
    assertTrue(reactivateNoOp.resultSurface().toString().contains("status=active"));
    assertBrowserSafe(reactivateNoOp.resultSurface());

    var staleReactivate = runActionAs(new CapabilityActionRequest(
        "action-open-organization-reactivate",
        "action-open-organization-reactivate",
        "manage-organizations",
        "saas_owner.organization.reactivate",
        Map.of("organizationId", TENANT_ID),
        null,
        "membership-owner",
        reactivated.resultSurface().surfaceId(),
        "corr-org-detail-stale-reactivate"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("denied", staleReactivate.status());
    assertTrue(staleReactivate.resultSurface().surfaceId().contains("denied"));
    assertTrue(staleReactivate.resultSurface().surfaceType().contains("system"));
    assertFalse(staleReactivate.resultSurface().toString().contains("missing-organization-never-seeded"));
    assertBrowserSafe(staleReactivate.resultSurface());

    var admins = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-organization-admins",
        "user-admin.show-organization-admins",
        "manage-organization-admins",
        "saas_owner.organization_admin.list",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID),
        null,
        "membership-owner",
        detail.resultSurface().surfaceId(),
        "corr-org-detail-admins"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", admins.status());
    assertEquals("surface-user-admin-organization-admins", admins.resultSurface().surfaceId());
    assertEquals(TENANT_ID, admins.resultSurface().data().get("organizationId"));
    assertTrue(admins.resultSurface().toString().contains("user_admin.organization_admins.v1"));
    assertTrue(admins.resultSurface().toString().contains("TENANT_ADMIN"));
    assertTrue(admins.resultSurface().toString().contains("adminSummary"));
    assertTrue(admins.resultSurface().toString().contains("targetScope"));
    assertTrue(admins.resultSurface().toString().contains("tenant-app-data-redacted"));
    assertBrowserSafe(admins.resultSurface());

    var adminInviteTask = runActionAs(new CapabilityActionRequest(
        "action-open-organization-admin-invitation-create",
        "user-admin.open-organization-admin-invite",
        "manage-organization-admins",
        "saas_owner.organization_admin.invite",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID),
        null,
        "membership-owner",
        detail.resultSurface().surfaceId(),
        "corr-org-detail-admin-invite"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", adminInviteTask.status());
    assertEquals("surface-user-admin-organization-admin-invitation-create", adminInviteTask.resultSurface().surfaceId());
    assertEquals("create-form", adminInviteTask.resultSurface().surfaceType());
    assertEquals("user_admin.organization_admin_invitation_create.v1", adminInviteTask.resultSurface().data().get("surfaceContract"));
    assertEquals(TENANT_ID, adminInviteTask.resultSurface().data().get("organizationId"));
    assertTrue(adminInviteTask.resultSurface().toString().contains("Provider/outbox failures return system-message without fake success"));
    assertTrue(adminInviteTask.resultSurface().toString().contains("provider-payload-redacted"));
    assertBrowserSafe(adminInviteTask.resultSurface());

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-user-admin-show-organizations",
        "user-admin.show-organizations",
        "manage-organizations",
        "saas_owner.organization.list",
        Map.of("scope", "saas-owner"),
        null,
        SELECTED_CONTEXT_ID,
        "surface-user-admin-dashboard",
        "corr-org-directory-tenant-denied")),
        "Tenant Admin selected contexts must not open the SaaS Owner Organization Directory through the protected action API.");

    var hiddenRead = runActionAs(new CapabilityActionRequest(
        "action-organization-read",
        "action-organization-read",
        "saas_owner.organization.read",
        "saas_owner.organization.read",
        Map.of("organizationId", "missing-organization-never-seeded"),
        null,
        "membership-owner",
        directory.resultSurface().surfaceId(),
        "corr-org-directory-hidden-read"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("denied", hiddenRead.status());
    assertEquals("surface-user-admin-system-message", hiddenRead.resultSurface().surfaceId());
    assertTrue(hiddenRead.resultSurface().surfaceType().contains("system"));
    assertFalse(hiddenRead.resultSurface().toString().contains("missing-organization-never-seeded"));
    assertBrowserSafe(hiddenRead.resultSurface());
  }

  @Test
  void protectedWorkstreamApiExercisesUserAdminOrganizationAdminsRuntimeTestCoverage() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-admins")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-admins-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Organization Admins surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-admins-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-user-admin-show-organization-admins",
            "user-admin.show-organization-admins",
            "manage-organization-admins",
            "saas_owner.organization_admin.list",
            Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID),
            null,
            "membership-owner",
            "surface-user-admin-organization-detail",
            "corr-org-admins-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Organization Admins action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-admin-detail")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-admin-detail-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Organization Admin detail surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-admin-detail-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-open-organization-admin-detail",
            "user-admin.open-organization-admin-detail",
            "manage-organization-admins",
            "saas_owner.organization_admin.list",
            Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID, "membershipId", "membership-admin", "accountId", "admin@example.test"),
            null,
            "membership-owner",
            "surface-user-admin-organization-admins",
            "corr-org-admin-detail-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Organization Admin detail action path must reject missing bearer tokens.");

    var directDetailMissingTarget = getSurfaceAs("surface-user-admin-organization-admin-detail", "corr-org-admin-detail-direct-missing-target", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-admin-detail", directDetailMissingTarget.surfaceId());
    assertEquals("show-inspection", directDetailMissingTarget.surfaceType());
    assertEquals("user_admin.organization_admin_detail.v1", directDetailMissingTarget.data().get("surfaceContract"));
    assertEquals("corr-org-admin-detail-direct-missing-target", directDetailMissingTarget.correlationId());
    assertTrue(directDetailMissingTarget.toString().contains("missing-target"));
    assertTrue(directDetailMissingTarget.toString().contains("Select an Organization Admin membership or invitation"));
    assertTrue(directDetailMissingTarget.toString().contains("trace-organization-admin-detail-missing-target"));
    assertTrue(directDetailMissingTarget.toString().contains("hidden-organization-admin-counts-redacted"));
    assertBrowserSafe(directDetailMissingTarget);

    var directMissingTarget = getSurfaceAs("surface-user-admin-organization-admins", "corr-org-admins-direct-missing-target", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-admins", directMissingTarget.surfaceId());
    assertEquals("list-search", directMissingTarget.surfaceType());
    assertEquals("user_admin.organization_admins.v1", directMissingTarget.data().get("surfaceContract"));
    assertEquals("corr-org-admins-direct-missing-target", directMissingTarget.correlationId());
    assertTrue(directMissingTarget.toString().contains("missing-target"));
    assertTrue(directMissingTarget.toString().contains("Select an Organization before listing Organization Admin users and invitations"));
    assertTrue(directMissingTarget.toString().contains("trace-organization-admins-missing-target"));
    assertTrue(directMissingTarget.toString().contains("hidden-organization-admin-counts-redacted"));
    assertBrowserSafe(directMissingTarget);

    var dashboard = getSurfaceAs("surface-user-admin-dashboard", "corr-org-admins-dashboard", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-saas-owner-dashboard", dashboard.surfaceId());
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-show-organizations")));
    assertBrowserSafe(dashboard);

    var directory = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-organizations",
        "user-admin.show-organizations",
        "manage-organizations",
        "saas_owner.organization.list",
        Map.of("scope", "saas-owner"),
        null,
        "membership-owner",
        dashboard.surfaceId(),
        "corr-org-admins-directory"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", directory.status());
    assertEquals("surface-user-admin-organization-directory", directory.resultSurface().surfaceId());
    assertTrue(directory.resultSurface().toString().contains("targetSurfaceId=surface-user-admin-organization-detail"));
    assertBrowserSafe(directory.resultSurface());

    var organizationDetail = runActionAs(new CapabilityActionRequest(
        "action-organization-read",
        "action-organization-read",
        "saas_owner.organization.read",
        "saas_owner.organization.read",
        Map.of("organizationId", TENANT_ID),
        null,
        "membership-owner",
        directory.resultSurface().surfaceId(),
        "corr-org-admins-detail"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", organizationDetail.status());
    assertEquals("surface-user-admin-organization-detail", organizationDetail.resultSurface().surfaceId());
    assertTrue(organizationDetail.resultSurface().toString().contains("action-user-admin-show-organization-admins"));
    assertBrowserSafe(organizationDetail.resultSurface());

    var admins = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-organization-admins",
        "user-admin.show-organization-admins",
        "manage-organization-admins",
        "saas_owner.organization_admin.list",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID),
        null,
        "membership-owner",
        organizationDetail.resultSurface().surfaceId(),
        "corr-org-admins-open"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", admins.status());
    assertEquals("corr-org-admins-open", admins.correlationId());
    assertEquals("surface-user-admin-organization-admins", admins.resultSurface().surfaceId());
    assertEquals("list-search", admins.resultSurface().surfaceType());
    assertEquals("user_admin.organization_admins.v1", admins.resultSurface().data().get("surfaceContract"));
    assertEquals(TENANT_ID, admins.resultSurface().data().get("organizationId"));
    assertEquals(TENANT_ID, admins.resultSurface().data().get("tenantId"));
    assertTrue(admins.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-admins")));
    assertTrue(admins.resultSurface().toString().contains("scopeType=TENANT"));
    assertTrue(admins.resultSurface().toString().contains("backend-authored-organization-detail"));
    assertTrue(admins.resultSurface().toString().contains("admin@example.test"));
    assertTrue(admins.resultSurface().toString().contains("TENANT_ADMIN"));
    assertFalse(admins.resultSurface().toString().contains("member@example.test"));
    assertTrue(admins.resultSurface().toString().contains("visibleCount=1"));
    assertTrue(admins.resultSurface().toString().contains("activeAdminCount=1"));
    assertTrue(admins.resultSurface().toString().contains("lastAdminRiskCount=1"));
    assertTrue(admins.resultSurface().toString().contains("providerBlockedCount=0"));
    assertTrue(admins.resultSurface().toString().contains("outboxBlockedCount=0"));
    assertTrue(admins.resultSurface().toString().contains("action-open-organization-admin-invitation-create"));
    assertTrue(admins.resultSurface().toString().contains("action-open-organization-admin-detail"));
    assertTrue(admins.resultSurface().toString().contains("surface-user-admin-organization-admin-detail"));
    assertTrue(admins.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-organizations"));
    assertTrue(admins.resultSurface().toString().contains("tenant-app-data-redacted"));
    assertTrue(admins.resultSurface().toString().contains("provider-payload-redacted"));
    assertBrowserSafe(admins.resultSurface());

    var adminDetail = runActionAs(new CapabilityActionRequest(
        "action-open-organization-admin-detail",
        "user-admin.open-organization-admin-detail",
        "manage-organization-admins",
        "saas_owner.organization_admin.list",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID, "membershipId", "membership-admin", "accountId", "admin@example.test"),
        null,
        "membership-owner",
        admins.resultSurface().surfaceId(),
        "corr-org-admins-detail-open"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", adminDetail.status());
    assertEquals("surface-user-admin-organization-admin-detail", adminDetail.resultSurface().surfaceId());
    assertEquals("show-inspection", adminDetail.resultSurface().surfaceType());
    assertEquals("user_admin.organization_admin_detail.v1", adminDetail.resultSurface().data().get("surfaceContract"));
    assertEquals("organization_admin_membership", adminDetail.resultSurface().data().get("recordKind"));
    assertEquals(TENANT_ID, adminDetail.resultSurface().data().get("organizationId"));
    assertTrue(adminDetail.resultSurface().toString().contains("adminTarget"));
    assertTrue(adminDetail.resultSurface().toString().contains("admin@example.test"));
    assertTrue(adminDetail.resultSurface().toString().contains("lastAdminRisk=true"));
    assertTrue(adminDetail.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(adminDetail.resultSurface().toString().contains("action-open-user-admin-role-change-preview"));
    assertTrue(adminDetail.resultSurface().toString().contains("action-open-user-admin-membership-status-confirmation"));
    assertTrue(adminDetail.resultSurface().toString().contains("action-user-admin-show-organization-admins"));
    assertTrue(adminDetail.resultSurface().toString().contains("action-organization-read"));
    assertTrue(adminDetail.resultSurface().toString().contains("trace-organization-admin-detail-membership"));
    assertTrue(adminDetail.resultSurface().toString().contains("hidden-organization-admin-counts-redacted"));
    assertTrue(adminDetail.resultSurface().toString().contains("tenant-app-data-redacted"));
    assertBrowserSafe(adminDetail.resultSurface());

    var hiddenAdminDetail = runActionAs(new CapabilityActionRequest(
        "action-open-organization-admin-detail",
        "user-admin.open-organization-admin-detail",
        "manage-organization-admins",
        "saas_owner.organization_admin.list",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID, "membershipId", "missing-membership-never-seeded", "accountId", "hidden-admin@example.test"),
        null,
        "membership-owner",
        admins.resultSurface().surfaceId(),
        "corr-org-admin-detail-hidden-target"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("denied", hiddenAdminDetail.status());
    assertEquals("surface-user-admin-system-message", hiddenAdminDetail.resultSurface().surfaceId());
    assertFalse(hiddenAdminDetail.resultSurface().toString().contains("missing-membership-never-seeded"));
    assertFalse(hiddenAdminDetail.resultSurface().toString().contains("hidden-admin@example.test"));
    assertBrowserSafe(hiddenAdminDetail.resultSurface());

    var hiddenAdmins = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-organization-admins",
        "user-admin.show-organization-admins",
        "manage-organization-admins",
        "saas_owner.organization_admin.list",
        Map.of("organizationId", "missing-organization-never-seeded", "tenantId", "missing-organization-never-seeded"),
        null,
        "membership-owner",
        organizationDetail.resultSurface().surfaceId(),
        "corr-org-admins-hidden"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("denied", hiddenAdmins.status());
    assertEquals("surface-user-admin-system-message", hiddenAdmins.resultSurface().surfaceId());
    assertFalse(hiddenAdmins.resultSurface().toString().contains("missing-organization-never-seeded"));
    assertBrowserSafe(hiddenAdmins.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurface("surface-user-admin-organization-admins", "corr-org-admins-tenant-denied-direct"),
        "Tenant Admin selected contexts must not direct-load the SaaS Owner Organization Admins surface.");
    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-user-admin-show-organization-admins",
        "user-admin.show-organization-admins",
        "manage-organization-admins",
        "saas_owner.organization_admin.list",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID),
        null,
        SELECTED_CONTEXT_ID,
        "surface-user-admin-organization-detail",
        "corr-org-admins-tenant-denied-action")),
        "Tenant Admin selected contexts must not open Organization Admins through the protected action API.");
  }

  @Test
  void protectedWorkstreamApiExercisesUserAdminOrganizationAdminInvitationCreateRuntimeTestCoverage() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-organization-admin-invitation-create")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-admin-invite-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Organization Admin invitation create surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", "membership-owner")
        .addHeader("X-Correlation-Id", "corr-org-admin-invite-missing-bearer-submit")
        .withRequestBody(new CapabilityActionRequest(
            "action-submit-organization-admin-invitation",
            "user-admin.invite-organization-admin",
            "manage-organization-admins",
            "saas_owner.organization_admin.invite",
            Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID, "email", "missing-bearer-org-admin@example.test", "roles", "TENANT_ADMIN"),
            "idem-org-admin-invite-missing-bearer",
            "membership-owner",
            "surface-user-admin-organization-admin-invitation-create",
            "corr-org-admin-invite-missing-bearer-submit"))
        .responseBodyAs(String.class)
        .invoke(), "Organization Admin invitation submit action must reject missing bearer tokens.");

    var directForm = getSurfaceAs("surface-user-admin-organization-admin-invitation-create", "corr-org-admin-invite-direct", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-organization-admin-invitation-create", directForm.surfaceId());
    assertEquals("create-form", directForm.surfaceType());
    assertEquals("user_admin.organization_admin_invitation_create.v1", directForm.data().get("surfaceContract"));
    assertEquals("corr-org-admin-invite-direct", directForm.correlationId());
    assertTrue(directForm.toString().contains("action-submit-organization-admin-invitation"));
    assertTrue(directForm.toString().contains("TENANT_ADMIN"));
    assertTrue(directForm.toString().contains("Provider/outbox failures return system-message without fake success"));
    assertTrue(directForm.toString().contains("provider-payload-redacted"));
    assertBrowserSafe(directForm);

    var dashboard = getSurfaceAs("surface-user-admin-dashboard", "corr-org-admin-invite-dashboard", "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("surface-user-admin-saas-owner-dashboard", dashboard.surfaceId());
    assertBrowserSafe(dashboard);

    var directory = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-organizations",
        "user-admin.show-organizations",
        "manage-organizations",
        "saas_owner.organization.list",
        Map.of("scope", "saas-owner"),
        null,
        "membership-owner",
        dashboard.surfaceId(),
        "corr-org-admin-invite-directory"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", directory.status());
    assertEquals("surface-user-admin-organization-directory", directory.resultSurface().surfaceId());
    assertBrowserSafe(directory.resultSurface());

    var organizationDetail = runActionAs(new CapabilityActionRequest(
        "action-organization-read",
        "action-organization-read",
        "saas_owner.organization.read",
        "saas_owner.organization.read",
        Map.of("organizationId", TENANT_ID),
        null,
        "membership-owner",
        directory.resultSurface().surfaceId(),
        "corr-org-admin-invite-detail"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", organizationDetail.status());
    assertEquals("surface-user-admin-organization-detail", organizationDetail.resultSurface().surfaceId());
    assertTrue(organizationDetail.resultSurface().toString().contains("action-open-organization-admin-invitation-create"));
    assertBrowserSafe(organizationDetail.resultSurface());

    var form = runActionAs(new CapabilityActionRequest(
        "action-open-organization-admin-invitation-create",
        "user-admin.open-organization-admin-invite",
        "manage-organization-admins",
        "saas_owner.organization_admin.invite",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID),
        null,
        "membership-owner",
        organizationDetail.resultSurface().surfaceId(),
        "corr-org-admin-invite-form"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", form.status());
    assertEquals("surface-user-admin-organization-admin-invitation-create", form.resultSurface().surfaceId());
    assertEquals(TENANT_ID, form.resultSurface().data().get("organizationId"));
    assertTrue(form.resultSurface().toString().contains("targetScope"));
    assertTrue(form.resultSurface().toString().contains("backend-authored-organization-detail"));
    assertBrowserSafe(form.resultSurface());

    var submitRequest = new CapabilityActionRequest(
        "action-submit-organization-admin-invitation",
        "user-admin.invite-organization-admin",
        "manage-organization-admins",
        "saas_owner.organization_admin.invite",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID, "email", "runtime-org-admin@example.test", "displayName", "Runtime Organization Admin", "roles", "TENANT_ADMIN", "reason", "protected runtime smoke"),
        "idem-org-admin-invite-runtime-smoke",
        "membership-owner",
        form.resultSurface().surfaceId(),
        "corr-org-admin-invite-submit");
    var submitted = runActionAs(submitRequest, "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", submitted.status(), submitted.message() + " " + submitted.resultSurface().data());
    assertEquals("corr-org-admin-invite-submit", submitted.correlationId());
    assertEquals("surface-user-admin-invitation-detail", submitted.resultSurface().surfaceId());
    assertEquals("organization-admin-invitation", submitted.resultSurface().data().get("recordKind"));
    assertEquals(TENANT_ID, submitted.resultSurface().data().get("organizationId"));
    assertTrue(submitted.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-admin-invitation")));
    assertTrue(submitted.resultSurface().toString().contains("runtime-org-admin@example.test"));
    assertTrue(submitted.resultSurface().toString().contains("scopeType=TENANT"));
    assertTrue(submitted.resultSurface().toString().contains("targetScope"));
    assertTrue(submitted.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-organization-admins"));
    assertTrue(submitted.resultSurface().toString().contains("provider-payload-redacted"));
    assertTrue(submitted.resultSurface().toString().contains("tenant-app-data-redacted"));
    assertBrowserSafe(submitted.resultSurface());

    var invitationDetail = runActionAs(new CapabilityActionRequest(
        "action-open-organization-admin-invitation-detail",
        "user-admin.open-organization-admin-invitation-detail",
        "manage-organization-admins",
        "saas_owner.organization_admin.list",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID, "invitationId", submitted.resultSurface().data().get("recordId")),
        null,
        "membership-owner",
        submitted.resultSurface().surfaceId(),
        "corr-org-admin-invite-detail-open"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", invitationDetail.status());
    assertEquals("surface-user-admin-organization-admin-detail", invitationDetail.resultSurface().surfaceId());
    assertEquals("user_admin.organization_admin_detail.v1", invitationDetail.resultSurface().data().get("surfaceContract"));
    assertEquals("organization_admin_invitation", invitationDetail.resultSurface().data().get("recordKind"));
    assertTrue(invitationDetail.resultSurface().toString().contains("runtime-org-admin@example.test"));
    assertTrue(invitationDetail.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(invitationDetail.resultSurface().toString().contains("action-open-user-admin-invitation-resend-confirmation"));
    assertTrue(invitationDetail.resultSurface().toString().contains("action-open-user-admin-invitation-revoke-confirmation"));
    assertTrue(invitationDetail.resultSurface().toString().contains("action-user-admin-show-organization-admins"));
    assertTrue(invitationDetail.resultSurface().toString().contains("providerBlockedCount=0"));
    assertTrue(invitationDetail.resultSurface().toString().contains("outboxBlockedCount=0"));
    assertTrue(invitationDetail.resultSurface().toString().contains("trace-organization-admin-detail-invitation"));
    assertTrue(invitationDetail.resultSurface().toString().contains("invitation-token-redacted"));
    assertTrue(invitationDetail.resultSurface().toString().contains("provider-payload-redacted"));
    assertBrowserSafe(invitationDetail.resultSurface());

    var repeatedSubmit = runActionAs(submitRequest, "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("accepted", repeatedSubmit.status());
    assertEquals(submitted.resultSurface().data().get("recordId"), repeatedSubmit.resultSurface().data().get("recordId"));
    assertBrowserSafe(repeatedSubmit.resultSurface());

    var invalidRole = runActionAs(new CapabilityActionRequest(
        "action-submit-organization-admin-invitation",
        "user-admin.invite-organization-admin",
        "manage-organization-admins",
        "saas_owner.organization_admin.invite",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID, "email", "invalid-role-org-admin@example.test", "displayName", "Invalid Role", "roles", "TENANT_EMPLOYEE", "reason", "invalid role smoke"),
        "idem-org-admin-invite-invalid-role",
        "membership-owner",
        form.resultSurface().surfaceId(),
        "corr-org-admin-invite-invalid-role"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("validation-error", invalidRole.status());
    assertEquals("surface-user-admin-organization-admin-invitation-create", invalidRole.resultSurface().surfaceId());
    assertTrue(invalidRole.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-organization-admin-invite-validation")));
    assertTrue(invalidRole.message().contains("TENANT_ADMIN"));
    assertBrowserSafe(invalidRole.resultSurface());

    var hiddenSubmit = runActionAs(new CapabilityActionRequest(
        "action-submit-organization-admin-invitation",
        "user-admin.invite-organization-admin",
        "manage-organization-admins",
        "saas_owner.organization_admin.invite",
        Map.of("organizationId", "missing-organization-never-seeded", "tenantId", "missing-organization-never-seeded", "email", "hidden-org-admin@example.test", "displayName", "Hidden Organization Admin", "roles", "TENANT_ADMIN", "reason", "hidden target smoke"),
        "idem-org-admin-invite-hidden",
        "membership-owner",
        form.resultSurface().surfaceId(),
        "corr-org-admin-invite-hidden"), "workos-owner", "owner@example.test", "SaaS Owner", "membership-owner");
    assertEquals("denied", hiddenSubmit.status());
    assertEquals("surface-user-admin-system-message", hiddenSubmit.resultSurface().surfaceId());
    assertFalse(hiddenSubmit.resultSurface().toString().contains("missing-organization-never-seeded"));
    assertBrowserSafe(hiddenSubmit.resultSurface());

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-open-organization-admin-invitation-create",
        "user-admin.open-organization-admin-invite",
        "manage-organization-admins",
        "saas_owner.organization_admin.invite",
        Map.of("organizationId", TENANT_ID, "tenantId", TENANT_ID),
        null,
        SELECTED_CONTEXT_ID,
        organizationDetail.resultSurface().surfaceId(),
        "corr-org-admin-invite-tenant-denied")),
        "Tenant Admin selected contexts must not open SaaS Owner Organization Admin invitation forms.");
  }

  @Test
  void protectedWorkstreamApiExercisesUserAdminCustomerDirectoryRuntimeTestCoverage() throws Exception {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant("tenant-hidden", "Hidden Tenant", true));
    repository.saveCustomer(new Customer(TENANT_ID, "cust-alpha", "Alpha Customer", true));
    repository.saveCustomer(new Customer(TENANT_ID, "cust-beta", "Beta Customer", false));
    repository.saveCustomer(new Customer("tenant-hidden", "cust-hidden", "Hidden Customer", true));
    repository.saveAccount(new Account("customer-admin@example.test", null, "customer-admin@example.test", "customer-admin@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile("customer-admin@example.test", "customer-admin@example.test", "Customer Admin", null, null, null));
    repository.saveSettings(new UserSettings("customer-admin@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership("membership-customer-admin", "customer-admin@example.test", ScopeType.CUSTOMER, TENANT_ID, "cust-alpha", List.of(FoundationRole.CUSTOMER_ADMIN), MembershipStatus.ACTIVE, false, null));

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-customer-directory")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-directory-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Customer Directory surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-directory-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-user-admin-show-customers",
            "user-admin.show-customers",
            "manage-customers",
            "tenant.customer.list",
            Map.of("query", "Alpha"),
            null,
            SELECTED_CONTEXT_ID,
            "surface-user-admin-dashboard",
            "corr-customer-directory-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer Directory action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-detail-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-customer-read",
            "user-admin.read-customer",
            "manage-customers",
            "tenant.customer.read",
            Map.of("customerId", "cust-alpha"),
            null,
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-directory",
            "corr-customer-detail-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer Detail read action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-admins-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-user-admin-show-customer-admins",
            "user-admin.show-customer-admins",
            "manage-customer-admins",
            "tenant.customer_admin.list",
            Map.of("customerId", "cust-alpha"),
            null,
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-detail",
            "corr-customer-admins-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer Admin list action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-customer-create")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-create-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Customer create surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-create-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-submit-customer-create",
            "user-admin.create-customer",
            "manage-customers",
            "tenant.customer.create",
            Map.of("customerName", "Bearerless Customer", "reason", "missing bearer must not create"),
            "idem-customer-create-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-create",
            "corr-customer-create-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer create submit action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-customer-rename")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-rename-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Customer rename surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-rename-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-submit-customer-rename",
            "user-admin.submit-customer-rename",
            "manage-customers",
            "tenant.customer.rename",
            Map.of("customerId", "cust-alpha", "customerName", "Bearerless Rename", "reason", "missing bearer must not rename"),
            "idem-customer-rename-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-rename",
            "corr-customer-rename-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer rename submit action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-customer-suspend-confirmation")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-suspend-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Customer suspend confirmation surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-suspend-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-customer-suspend",
            "user-admin.suspend-customer",
            "manage-customers",
            "tenant.customer.suspend",
            Map.of("customerId", "cust-alpha", "reason", "missing bearer must not suspend", "confirmation", "SUSPEND"),
            "idem-customer-suspend-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-suspend-confirmation",
            "corr-customer-suspend-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer suspend submit action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-customer-reactivate-confirmation")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-reactivate-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Customer reactivate confirmation surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-reactivate-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-customer-reactivate",
            "user-admin.reactivate-customer",
            "manage-customers",
            "tenant.customer.reactivate",
            Map.of("customerId", "cust-beta", "reason", "missing bearer must not reactivate", "confirmation", "REACTIVATE"),
            "idem-customer-reactivate-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-reactivate-confirmation",
            "corr-customer-reactivate-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer reactivate submit action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-user-admin-customer-admin-detail")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-admin-detail-missing-bearer-direct")
        .responseBodyAs(String.class)
        .invoke(), "Customer Admin detail surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-admin-detail-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-open-customer-admin-detail",
            "user-admin.open-customer-admin-detail",
            "manage-customer-admins",
            "tenant.customer_admin.list",
            Map.of("customerId", "cust-alpha", "membershipId", "membership-customer-admin"),
            null,
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-admins",
            "corr-customer-admin-detail-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer Admin membership detail action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-admin-invitation-detail-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-open-customer-admin-invitation-detail",
            "user-admin.open-customer-admin-invitation-detail",
            "manage-customer-admins",
            "tenant.customer_admin.list",
            Map.of("customerId", "cust-alpha", "invitationId", "missing-invitation"),
            null,
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-admins",
            "corr-customer-admin-invitation-detail-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer Admin invitation detail action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-admin-invite-open-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-open-customer-admin-invitation-create",
            "user-admin.open-customer-admin-invite",
            "manage-customer-admins",
            "tenant.customer_admin.invite",
            Map.of("customerId", "cust-alpha"),
            null,
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-detail",
            "corr-customer-admin-invite-open-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer Admin invite create-form action path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", SELECTED_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-customer-admin-invite-submit-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-customer-admin-invite",
            "user-admin.invite-customer-admin",
            "manage-customer-admins",
            "tenant.customer_admin.invite",
            Map.of("customerId", "cust-alpha", "email", "missing.bearer.customer.admin@example.test", "displayName", "Missing Bearer", "roles", "CUSTOMER_ADMIN"),
            "idem-customer-admin-invite-missing-bearer",
            SELECTED_CONTEXT_ID,
            "surface-user-admin-customer-admin-invitation-create",
            "corr-customer-admin-invite-submit-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Customer Admin invite submit action path must reject missing bearer tokens.");

    var direct = getSurface("surface-user-admin-customer-directory", "corr-customer-directory-direct");
    assertEquals("surface-user-admin-customer-directory", direct.surfaceId());
    assertEquals("list-search", direct.surfaceType());
    assertEquals("user_admin.customer_directory.v1", direct.data().get("surfaceContract"));
    assertEquals("corr-customer-directory-direct", direct.correlationId());
    assertTrue(direct.toString().contains("Alpha Customer"));
    assertTrue(direct.toString().contains("Beta Customer"));
    assertTrue(direct.toString().contains("targetSurfaceId=surface-user-admin-customer-detail"));
    assertTrue(direct.toString().contains("openActionId=action-customer-read"));
    assertTrue(direct.toString().contains("branchReturnActionId=action-user-admin-show-customers"));
    assertTrue(direct.toString().contains("sibling-customers-redacted"));
    assertTrue(direct.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-user-admin-customer-directory")));
    assertFalse(direct.toString().contains("Hidden Customer"));
    assertFalse(direct.toString().contains("tenant-hidden"));
    assertBrowserSafe(direct);

    var filtered = runAction(new CapabilityActionRequest(
        "action-user-admin-show-customers",
        "user-admin.show-customers",
        "manage-customers",
        "tenant.customer.list",
        Map.of("query", "Beta", "status", "suspended"),
        null,
        SELECTED_CONTEXT_ID,
        "surface-user-admin-dashboard",
        "corr-customer-directory-filtered"));
    assertEquals("accepted", filtered.status());
    assertEquals("surface-user-admin-customer-directory", filtered.resultSurface().surfaceId());
    assertEquals("user_admin.customer_directory.v1", filtered.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-customer-directory-filtered", filtered.resultSurface().correlationId());
    assertTrue(filtered.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-directory")));
    assertTrue(String.valueOf(filtered.resultSurface().data().get("pageInfo")).contains("visibleCount=1"));
    assertTrue(filtered.resultSurface().toString().contains("query=Beta"));
    assertTrue(filtered.resultSurface().toString().contains("status=suspended"));
    assertTrue(filtered.resultSurface().toString().contains("Beta Customer"));
    assertFalse(filtered.resultSurface().toString().contains("Alpha Customer"));
    assertFalse(filtered.resultSurface().toString().contains("Hidden Customer"));
    assertBrowserSafe(filtered.resultSurface());

    var detail = runAction(new CapabilityActionRequest(
        "action-customer-read",
        "user-admin.read-customer",
        "manage-customers",
        "tenant.customer.read",
        Map.of("customerId", "cust-alpha"),
        null,
        SELECTED_CONTEXT_ID,
        direct.surfaceId(),
        "corr-customer-directory-row-open"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-user-admin-customer-detail", detail.resultSurface().surfaceId());
    assertEquals("show-inspection", detail.resultSurface().surfaceType());
    assertEquals("user_admin.customer_detail.v1", detail.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-customer-directory-row-open", detail.resultSurface().correlationId());
    assertTrue(detail.resultSurface().toString().contains("Alpha Customer"));
    assertTrue(detail.resultSurface().toString().contains("customerDetail={"));
    assertTrue(detail.resultSurface().toString().contains("customerId=cust-alpha"));
    assertTrue(detail.resultSurface().toString().contains("customerName=Alpha Customer"));
    assertTrue(detail.resultSurface().toString().contains("status=active"));
    assertTrue(detail.resultSurface().toString().contains("Customer lifecycle inspection"));
    assertTrue(detail.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(detail.resultSurface().toString().contains("canOpenTaskSurfaces=true"));
    assertTrue(detail.resultSurface().toString().contains("visibleActions=[read, rename, suspend]"));
    assertTrue(detail.resultSurface().toString().contains("action-open-customer-rename"));
    assertTrue(detail.resultSurface().toString().contains("action-open-customer-suspend"));
    assertTrue(detail.resultSurface().toString().contains("action-user-admin-show-customer-admins"));
    assertTrue(detail.resultSurface().toString().contains("action-open-customer-admin-invitation-create"));
    assertTrue(detail.resultSurface().toString().contains("action-open-audit-trace"));
    assertTrue(detail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-customers"));
    assertTrue(detail.resultSurface().toString().contains("sibling-customers-redacted"));
    assertTrue(detail.resultSurface().toString().contains("tenant-app-data-redacted"));
    assertTrue(detail.resultSurface().toString().contains("provider-secrets-redacted"));
    assertTrue(detail.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-read")));
    assertTrue(detail.resultSurface().toString().contains("trace-customer-read"));
    assertBrowserSafe(detail.resultSurface());

    var createForm = runAction(new CapabilityActionRequest(
        "action-open-customer-create",
        "user-admin.open-customer-create",
        "manage-customers",
        "tenant.customer.create",
        null,
        null,
        SELECTED_CONTEXT_ID,
        direct.surfaceId(),
        "corr-customer-directory-create-open"));
    assertEquals("accepted", createForm.status());
    assertEquals("surface-user-admin-customer-create", createForm.resultSurface().surfaceId());
    assertEquals("create-form", createForm.resultSurface().surfaceType());
    assertEquals("user_admin.customer_create.v1", createForm.resultSurface().data().get("surfaceContract"));
    assertTrue(createForm.resultSurface().toString().contains("action-submit-customer-create"));
    assertFalse(createForm.resultSurface().toString().contains("action-customer-create"));
    assertTrue(createForm.resultSurface().toString().contains("validationPolicy"));
    assertTrue(createForm.resultSurface().toString().contains("creationBoundary"));
    assertTrue(createForm.resultSurface().toString().contains("idempotencyKeyHint=client-generated"));
    assertBrowserSafe(createForm.resultSurface());

    var directCreateForm = getSurface("surface-user-admin-customer-create", "corr-customer-create-direct-authorized");
    assertEquals("surface-user-admin-customer-create", directCreateForm.surfaceId());
    assertEquals("create-form", directCreateForm.surfaceType());
    assertEquals("user_admin.customer_create.v1", directCreateForm.data().get("surfaceContract"));
    assertEquals("corr-customer-create-direct-authorized", directCreateForm.correlationId());
    assertTrue(directCreateForm.toString().contains("tenant.customer.create"));
    assertTrue(directCreateForm.toString().contains("action-submit-customer-create"));
    assertTrue(directCreateForm.toString().contains("sibling-customers-redacted"));
    assertTrue(directCreateForm.toString().contains("tenant-app-data-redacted"));
    assertBrowserSafe(directCreateForm);

    var customerRowsBeforeCreate = repository.customerRows().size();
    var createdCustomer = runAction(new CapabilityActionRequest(
        "action-submit-customer-create",
        "user-admin.create-customer",
        "manage-customers",
        "tenant.customer.create",
        Map.of("customerName", "Browser Smoke Customer", "reason", "runtime test customer create"),
        "idem-customer-create-browser-smoke",
        SELECTED_CONTEXT_ID,
        createForm.resultSurface().surfaceId(),
        "corr-customer-create-submit"));
    assertEquals("accepted", createdCustomer.status());
    assertEquals("corr-customer-create-submit", createdCustomer.correlationId());
    assertEquals("surface-user-admin-customer-detail", createdCustomer.resultSurface().surfaceId());
    assertEquals("show-inspection", createdCustomer.resultSurface().surfaceType());
    assertEquals("user_admin.customer_detail.v1", createdCustomer.resultSurface().data().get("surfaceContract"));
    assertTrue(createdCustomer.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-create")));
    assertTrue(createdCustomer.resultSurface().toString().contains("Browser Smoke Customer"));
    assertTrue(createdCustomer.resultSurface().toString().contains("status=active"));
    assertTrue(createdCustomer.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(createdCustomer.resultSurface().toString().contains("action-open-customer-admin-invitation-create"));
    assertTrue(createdCustomer.resultSurface().toString().contains("sibling-customers-redacted"));
    assertTrue(createdCustomer.resultSurface().toString().contains("tenant-app-data-redacted"));
    var createdCustomerId = String.valueOf(createdCustomer.resultSurface().data().get("recordId"));
    assertFalse(createdCustomerId.isBlank());
    var savedCreatedCustomer = repository.customer(TENANT_ID, createdCustomerId).orElseThrow();
    assertEquals("Browser Smoke Customer", savedCreatedCustomer.displayName());
    assertTrue(savedCreatedCustomer.active());
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(createdCustomer.resultSurface());

    var replayedCustomerCreate = runAction(new CapabilityActionRequest(
        "action-submit-customer-create",
        "user-admin.create-customer",
        "manage-customers",
        "tenant.customer.create",
        Map.of("customerName", "Browser Smoke Customer", "reason", "runtime test customer create replay"),
        "idem-customer-create-browser-smoke",
        SELECTED_CONTEXT_ID,
        createForm.resultSurface().surfaceId(),
        "corr-customer-create-submit-replay"));
    assertEquals("no-op", replayedCustomerCreate.status());
    assertEquals("surface-user-admin-customer-detail", replayedCustomerCreate.resultSurface().surfaceId());
    assertEquals(createdCustomerId, replayedCustomerCreate.resultSurface().data().get("recordId"));
    assertTrue(replayedCustomerCreate.message().contains("replay"));
    assertTrue(replayedCustomerCreate.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-create")));
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(replayedCustomerCreate.resultSurface());

    var missingIdempotencyCreate = runAction(new CapabilityActionRequest(
        "action-submit-customer-create",
        "user-admin.create-customer",
        "manage-customers",
        "tenant.customer.create",
        Map.of("customerName", "Missing Idempotency Customer", "reason", "missing idempotency must fail closed"),
        null,
        SELECTED_CONTEXT_ID,
        createForm.resultSurface().surfaceId(),
        "corr-customer-create-missing-idempotency"));
    assertEquals("validation-error", missingIdempotencyCreate.status());
    assertEquals("surface-user-admin-system-message", missingIdempotencyCreate.resultSurface().surfaceId());
    assertTrue(missingIdempotencyCreate.resultSurface().toString().contains("idempotency-key-required"));
    assertTrue(missingIdempotencyCreate.resultSurface().toString().contains("noFakeSuccess=true"));
    assertFalse(missingIdempotencyCreate.resultSurface().toString().contains("Missing Idempotency Customer"));
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(missingIdempotencyCreate.resultSurface());

    var customerCreateDenied = runActionAs(new CapabilityActionRequest(
        "action-submit-customer-create",
        "user-admin.create-customer",
        "manage-customers",
        "tenant.customer.create",
        Map.of("customerName", "Customer Admin Unauthorized Customer", "reason", "customer admin must not create sibling customer"),
        "idem-customer-create-customer-admin-denied",
        "membership-customer-admin",
        createForm.resultSurface().surfaceId(),
        "corr-customer-create-customer-admin-denied"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerCreateDenied.status());
    assertEquals("surface-user-admin-system-message", customerCreateDenied.resultSurface().surfaceId());
    assertTrue(customerCreateDenied.resultSurface().toString().contains("scope-forbidden"));
    assertTrue(customerCreateDenied.resultSurface().toString().contains("noFakeSuccess=true"));
    assertFalse(customerCreateDenied.resultSurface().toString().contains("Customer Admin Unauthorized Customer"));
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(customerCreateDenied.resultSurface());

    var directRenameWithoutTarget = getSurface("surface-user-admin-customer-rename", "corr-customer-rename-direct-missing-target");
    assertEquals("surface-user-admin-customer-rename", directRenameWithoutTarget.surfaceId());
    assertEquals("edit-form", directRenameWithoutTarget.surfaceType());
    assertEquals("user_admin.customer_rename.v1", directRenameWithoutTarget.data().get("surfaceContract"));
    assertEquals("missing-target", directRenameWithoutTarget.data().get("formState"));
    assertTrue(directRenameWithoutTarget.toString().contains("missing-visible-customer"));
    assertTrue(directRenameWithoutTarget.toString().contains("action-submit-customer-rename"));
    assertFalse(directRenameWithoutTarget.toString().contains("Hidden Customer"));
    assertBrowserSafe(directRenameWithoutTarget);

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-customer-rename",
        "corr-customer-rename-customer-admin-denied-direct",
        "workos-customer-admin",
        "customer-admin@example.test",
        "Customer Admin",
        "membership-customer-admin"), "Customer Admin selected contexts must not direct-load Customer rename forms.");

    var renameForm = runAction(new CapabilityActionRequest(
        "action-open-customer-rename",
        "user-admin.open-customer-rename",
        "manage-customers",
        "tenant.customer.rename",
        Map.of("customerId", createdCustomerId),
        null,
        SELECTED_CONTEXT_ID,
        createdCustomer.resultSurface().surfaceId(),
        "corr-customer-rename-open"));
    assertEquals("accepted", renameForm.status());
    assertEquals("surface-user-admin-customer-rename", renameForm.resultSurface().surfaceId());
    assertEquals("edit-form", renameForm.resultSurface().surfaceType());
    assertEquals("user_admin.customer_rename.v1", renameForm.resultSurface().data().get("surfaceContract"));
    assertTrue(renameForm.resultSurface().toString().contains("action-submit-customer-rename"));
    assertFalse(renameForm.resultSurface().toString().contains("action-customer-rename"));
    assertTrue(renameForm.resultSurface().toString().contains("validationPolicy"));
    assertTrue(renameForm.resultSurface().toString().contains("changePreview"));
    assertTrue(renameForm.resultSurface().toString().contains("tenant.customer.rename"));
    assertTrue(renameForm.resultSurface().toString().contains("sibling-customers-redacted"));
    assertBrowserSafe(renameForm.resultSurface());

    var renamedCustomer = runAction(new CapabilityActionRequest(
        "action-submit-customer-rename",
        "user-admin.submit-customer-rename",
        "manage-customers",
        "tenant.customer.rename",
        Map.of("customerId", createdCustomerId, "customerName", "Browser Smoke Customer Renamed", "reason", "runtime test customer rename"),
        "idem-customer-rename-browser-smoke",
        SELECTED_CONTEXT_ID,
        renameForm.resultSurface().surfaceId(),
        "corr-customer-rename-submit"));
    assertEquals("accepted", renamedCustomer.status());
    assertEquals("surface-user-admin-customer-detail", renamedCustomer.resultSurface().surfaceId());
    assertEquals("show-inspection", renamedCustomer.resultSurface().surfaceType());
    assertEquals("user_admin.customer_detail.v1", renamedCustomer.resultSurface().data().get("surfaceContract"));
    assertTrue(renamedCustomer.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-rename")));
    assertTrue(renamedCustomer.resultSurface().toString().contains("Browser Smoke Customer Renamed"));
    assertTrue(renamedCustomer.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(renamedCustomer.resultSurface().toString().contains("sibling-customers-redacted"));
    assertEquals("Browser Smoke Customer Renamed", repository.customer(TENANT_ID, createdCustomerId).orElseThrow().displayName());
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(renamedCustomer.resultSurface());

    var replayedCustomerRename = runAction(new CapabilityActionRequest(
        "action-submit-customer-rename",
        "user-admin.submit-customer-rename",
        "manage-customers",
        "tenant.customer.rename",
        Map.of("customerId", createdCustomerId, "customerName", "Browser Smoke Customer Renamed", "reason", "runtime test customer rename replay"),
        "idem-customer-rename-browser-smoke-replay",
        SELECTED_CONTEXT_ID,
        renameForm.resultSurface().surfaceId(),
        "corr-customer-rename-submit-replay"));
    assertEquals("no-op", replayedCustomerRename.status());
    assertEquals("surface-user-admin-customer-detail", replayedCustomerRename.resultSurface().surfaceId());
    assertEquals(createdCustomerId, replayedCustomerRename.resultSurface().data().get("recordId"));
    assertTrue(replayedCustomerRename.message().contains("already matches"));
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(replayedCustomerRename.resultSurface());

    var missingIdempotencyRename = runAction(new CapabilityActionRequest(
        "action-submit-customer-rename",
        "user-admin.submit-customer-rename",
        "manage-customers",
        "tenant.customer.rename",
        Map.of("customerId", createdCustomerId, "customerName", "Rename Without Idempotency", "reason", "missing idempotency must fail closed"),
        null,
        SELECTED_CONTEXT_ID,
        renameForm.resultSurface().surfaceId(),
        "corr-customer-rename-missing-idempotency"));
    assertEquals("validation-error", missingIdempotencyRename.status());
    assertEquals("surface-user-admin-system-message", missingIdempotencyRename.resultSurface().surfaceId());
    assertTrue(missingIdempotencyRename.resultSurface().toString().contains("idempotency-key-required"));
    assertTrue(missingIdempotencyRename.resultSurface().toString().contains("noFakeSuccess=true"));
    assertFalse(missingIdempotencyRename.resultSurface().toString().contains("Rename Without Idempotency"));
    assertEquals("Browser Smoke Customer Renamed", repository.customer(TENANT_ID, createdCustomerId).orElseThrow().displayName());
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(missingIdempotencyRename.resultSurface());

    var hiddenCustomerRename = runAction(new CapabilityActionRequest(
        "action-submit-customer-rename",
        "user-admin.submit-customer-rename",
        "manage-customers",
        "tenant.customer.rename",
        Map.of("customerId", "cust-hidden", "customerName", "Hidden Customer Rename Attempt", "reason", "hidden customer must not enumerate"),
        "idem-customer-rename-hidden-denied",
        SELECTED_CONTEXT_ID,
        renameForm.resultSurface().surfaceId(),
        "corr-customer-rename-hidden-denied"));
    assertEquals("denied", hiddenCustomerRename.status());
    assertEquals("surface-user-admin-system-message", hiddenCustomerRename.resultSurface().surfaceId());
    assertTrue(hiddenCustomerRename.resultSurface().toString().contains("target-not-found-or-forbidden"));
    assertTrue(hiddenCustomerRename.resultSurface().toString().contains("noFakeSuccess=true"));
    assertFalse(hiddenCustomerRename.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(hiddenCustomerRename.resultSurface().toString().contains("tenant-hidden"));
    assertFalse(hiddenCustomerRename.resultSurface().toString().contains("Hidden Customer Rename Attempt"));
    assertEquals("Browser Smoke Customer Renamed", repository.customer(TENANT_ID, createdCustomerId).orElseThrow().displayName());
    assertBrowserSafe(hiddenCustomerRename.resultSurface());

    var customerAdminRenameDenied = runActionAs(new CapabilityActionRequest(
        "action-submit-customer-rename",
        "user-admin.submit-customer-rename",
        "manage-customers",
        "tenant.customer.rename",
        Map.of("customerId", createdCustomerId, "customerName", "Customer Admin Unauthorized Rename", "reason", "customer admin must not rename customers"),
        "idem-customer-rename-customer-admin-denied",
        "membership-customer-admin",
        renameForm.resultSurface().surfaceId(),
        "corr-customer-rename-customer-admin-denied"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerAdminRenameDenied.status());
    assertEquals("surface-user-admin-system-message", customerAdminRenameDenied.resultSurface().surfaceId());
    assertTrue(customerAdminRenameDenied.resultSurface().toString().contains("scope-forbidden"));
    assertTrue(customerAdminRenameDenied.resultSurface().toString().contains("noFakeSuccess=true"));
    assertFalse(customerAdminRenameDenied.resultSurface().toString().contains("Customer Admin Unauthorized Rename"));
    assertEquals("Browser Smoke Customer Renamed", repository.customer(TENANT_ID, createdCustomerId).orElseThrow().displayName());
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(customerAdminRenameDenied.resultSurface());

    var directSuspendWithoutTarget = getSurface("surface-user-admin-customer-suspend-confirmation", "corr-customer-suspend-direct-missing-target");
    assertEquals("surface-user-admin-customer-suspend-confirmation", directSuspendWithoutTarget.surfaceId());
    assertEquals("destructive-lifecycle-confirmation", directSuspendWithoutTarget.surfaceType());
    assertEquals("user_admin.customer_suspend_confirmation.v1", directSuspendWithoutTarget.data().get("surfaceContract"));
    assertEquals("missing-target", directSuspendWithoutTarget.data().get("formState"));
    assertTrue(directSuspendWithoutTarget.toString().contains("tenant.customer.suspend"));
    assertTrue(directSuspendWithoutTarget.toString().contains("confirmationPhrase=SUSPEND"));
    assertTrue(directSuspendWithoutTarget.toString().contains("noFakeSuccess=true"));
    assertFalse(directSuspendWithoutTarget.toString().contains("Hidden Customer"));
    assertBrowserSafe(directSuspendWithoutTarget);

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-customer-suspend-confirmation",
        "corr-customer-suspend-customer-admin-denied-direct",
        "workos-customer-admin",
        "customer-admin@example.test",
        "Customer Admin",
        "membership-customer-admin"), "Customer Admin selected contexts must not direct-load Customer suspend confirmation surfaces.");

    var suspendForm = runAction(new CapabilityActionRequest(
        "action-open-customer-suspend",
        "user-admin.open-customer-suspend",
        "manage-customers",
        "tenant.customer.suspend",
        Map.of("customerId", createdCustomerId),
        null,
        SELECTED_CONTEXT_ID,
        renamedCustomer.resultSurface().surfaceId(),
        "corr-customer-suspend-open"));
    assertEquals("accepted", suspendForm.status());
    assertEquals("surface-user-admin-customer-suspend-confirmation", suspendForm.resultSurface().surfaceId());
    assertEquals("destructive-lifecycle-confirmation", suspendForm.resultSurface().surfaceType());
    assertEquals("user_admin.customer_suspend_confirmation.v1", suspendForm.resultSurface().data().get("surfaceContract"));
    assertTrue(suspendForm.resultSurface().toString().contains("tenant.customer.suspend"));
    assertTrue(suspendForm.resultSurface().toString().contains("confirmationPhrase=SUSPEND"));
    assertTrue(suspendForm.resultSurface().toString().contains("Customer Admin memberships and invitations are not changed"));
    assertTrue(suspendForm.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-suspend")));
    assertBrowserSafe(suspendForm.resultSurface());

    var missingConfirmationSuspend = runAction(new CapabilityActionRequest(
        "action-customer-suspend",
        "user-admin.suspend-customer",
        "manage-customers",
        "tenant.customer.suspend",
        Map.of("customerId", createdCustomerId, "reason", "runtime test customer suspend missing confirmation", "confirmation", ""),
        "idem-customer-suspend-missing-confirmation",
        SELECTED_CONTEXT_ID,
        suspendForm.resultSurface().surfaceId(),
        "corr-customer-suspend-missing-confirmation"));
    assertEquals("validation-error", missingConfirmationSuspend.status());
    assertEquals("surface-user-admin-system-message", missingConfirmationSuspend.resultSurface().surfaceId());
    assertTrue(missingConfirmationSuspend.resultSurface().toString().contains("confirmation-required"));
    assertTrue(repository.customer(TENANT_ID, createdCustomerId).orElseThrow().active());
    assertBrowserSafe(missingConfirmationSuspend.resultSurface());

    var suspendedCustomer = runAction(new CapabilityActionRequest(
        "action-customer-suspend",
        "user-admin.suspend-customer",
        "manage-customers",
        "tenant.customer.suspend",
        Map.of("customerId", createdCustomerId, "reason", "runtime test customer suspend", "confirmation", "SUSPEND"),
        "idem-customer-suspend-browser-smoke",
        SELECTED_CONTEXT_ID,
        suspendForm.resultSurface().surfaceId(),
        "corr-customer-suspend-submit"));
    assertEquals("accepted", suspendedCustomer.status());
    assertEquals("surface-user-admin-customer-detail", suspendedCustomer.resultSurface().surfaceId());
    assertTrue(suspendedCustomer.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-suspend")));
    assertTrue(suspendedCustomer.resultSurface().toString().contains("status=suspended"));
    assertFalse(repository.customer(TENANT_ID, createdCustomerId).orElseThrow().active());
    assertEquals(customerRowsBeforeCreate + 1, repository.customerRows().size());
    assertBrowserSafe(suspendedCustomer.resultSurface());

    var replayedCustomerSuspend = runAction(new CapabilityActionRequest(
        "action-customer-suspend",
        "user-admin.suspend-customer",
        "manage-customers",
        "tenant.customer.suspend",
        Map.of("customerId", createdCustomerId, "reason", "runtime test customer suspend replay", "confirmation", "SUSPEND"),
        "idem-customer-suspend-browser-smoke-replay",
        SELECTED_CONTEXT_ID,
        suspendForm.resultSurface().surfaceId(),
        "corr-customer-suspend-submit-replay"));
    assertEquals("no-op", replayedCustomerSuspend.status());
    assertEquals("surface-user-admin-customer-detail", replayedCustomerSuspend.resultSurface().surfaceId());
    assertEquals(createdCustomerId, replayedCustomerSuspend.resultSurface().data().get("recordId"));
    assertTrue(replayedCustomerSuspend.message().contains("already suspended"));
    assertTrue(replayedCustomerSuspend.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-suspend")));
    assertFalse(repository.customer(TENANT_ID, createdCustomerId).orElseThrow().active());
    assertBrowserSafe(replayedCustomerSuspend.resultSurface());

    var directReactivateWithoutTarget = getSurface("surface-user-admin-customer-reactivate-confirmation", "corr-customer-reactivate-direct-missing-target");
    assertEquals("surface-user-admin-customer-reactivate-confirmation", directReactivateWithoutTarget.surfaceId());
    assertEquals("lifecycle-confirmation", directReactivateWithoutTarget.surfaceType());
    assertEquals("user_admin.customer_reactivate_confirmation.v1", directReactivateWithoutTarget.data().get("surfaceContract"));
    assertEquals("missing-target", directReactivateWithoutTarget.data().get("formState"));
    assertTrue(directReactivateWithoutTarget.toString().contains("tenant.customer.reactivate"));
    assertTrue(directReactivateWithoutTarget.toString().contains("reactivationEligibility"));
    assertTrue(directReactivateWithoutTarget.toString().contains("noFakeSuccess=true"));
    assertFalse(directReactivateWithoutTarget.toString().contains("Hidden Customer"));
    assertBrowserSafe(directReactivateWithoutTarget);

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-customer-reactivate-confirmation",
        "corr-customer-reactivate-customer-admin-denied-direct",
        "workos-customer-admin",
        "customer-admin@example.test",
        "Customer Admin",
        "membership-customer-admin"), "Customer Admin selected contexts must not direct-load Customer reactivate confirmation surfaces.");

    var reactivateForm = runAction(new CapabilityActionRequest(
        "action-open-customer-reactivate",
        "user-admin.open-customer-reactivate",
        "manage-customers",
        "tenant.customer.reactivate",
        Map.of("customerId", createdCustomerId),
        null,
        SELECTED_CONTEXT_ID,
        replayedCustomerSuspend.resultSurface().surfaceId(),
        "corr-customer-reactivate-open"));
    assertEquals("accepted", reactivateForm.status());
    assertEquals("surface-user-admin-customer-reactivate-confirmation", reactivateForm.resultSurface().surfaceId());
    assertEquals("lifecycle-confirmation", reactivateForm.resultSurface().surfaceType());
    assertEquals("user_admin.customer_reactivate_confirmation.v1", reactivateForm.resultSurface().data().get("surfaceContract"));
    assertTrue(reactivateForm.resultSurface().toString().contains("tenant.customer.reactivate"));
    assertTrue(reactivateForm.resultSurface().toString().contains("reactivationEligibility"));
    assertTrue(reactivateForm.resultSurface().toString().contains("confirmationPhrase=REACTIVATE"));
    assertTrue(reactivateForm.resultSurface().toString().contains("providerOrOutboxReadinessSummary=No external provider or outbox success is fabricated by Customer reactivation."));
    assertTrue(reactivateForm.resultSurface().toString().contains("Customer Admin memberships and invitations are not changed"));
    assertTrue(reactivateForm.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-reactivate")));
    assertBrowserSafe(reactivateForm.resultSurface());

    var reactivatedCustomer = runAction(new CapabilityActionRequest(
        "action-customer-reactivate",
        "user-admin.reactivate-customer",
        "manage-customers",
        "tenant.customer.reactivate",
        Map.of("customerId", createdCustomerId, "reason", "runtime test customer reactivate", "confirmation", "REACTIVATE"),
        "idem-customer-reactivate-browser-smoke",
        SELECTED_CONTEXT_ID,
        reactivateForm.resultSurface().surfaceId(),
        "corr-customer-reactivate-submit"));
    assertEquals("accepted", reactivatedCustomer.status());
    assertEquals("surface-user-admin-customer-detail", reactivatedCustomer.resultSurface().surfaceId());
    assertTrue(reactivatedCustomer.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-reactivate")));
    assertTrue(reactivatedCustomer.resultSurface().toString().contains("status=active"));
    assertTrue(repository.customer(TENANT_ID, createdCustomerId).orElseThrow().active());
    assertBrowserSafe(reactivatedCustomer.resultSurface());

    var replayedCustomerReactivate = runAction(new CapabilityActionRequest(
        "action-customer-reactivate",
        "user-admin.reactivate-customer",
        "manage-customers",
        "tenant.customer.reactivate",
        Map.of("customerId", createdCustomerId, "reason", "runtime test customer reactivate replay"),
        "idem-customer-reactivate-browser-smoke-replay",
        SELECTED_CONTEXT_ID,
        reactivateForm.resultSurface().surfaceId(),
        "corr-customer-reactivate-submit-replay"));
    assertEquals("no-op", replayedCustomerReactivate.status());
    assertEquals("surface-user-admin-customer-detail", replayedCustomerReactivate.resultSurface().surfaceId());
    assertTrue(replayedCustomerReactivate.message().contains("already active"));
    assertTrue(replayedCustomerReactivate.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-reactivate")));
    assertTrue(repository.customer(TENANT_ID, createdCustomerId).orElseThrow().active());
    assertBrowserSafe(replayedCustomerReactivate.resultSurface());

    var missingIdempotencyReactivate = runAction(new CapabilityActionRequest(
        "action-customer-reactivate",
        "user-admin.reactivate-customer",
        "manage-customers",
        "tenant.customer.reactivate",
        Map.of("customerId", createdCustomerId, "reason", "missing idempotency must fail closed", "confirmation", "REACTIVATE"),
        null,
        SELECTED_CONTEXT_ID,
        reactivateForm.resultSurface().surfaceId(),
        "corr-customer-reactivate-missing-idempotency"));
    assertEquals("validation-error", missingIdempotencyReactivate.status());
    assertEquals("surface-user-admin-system-message", missingIdempotencyReactivate.resultSurface().surfaceId());
    assertTrue(missingIdempotencyReactivate.resultSurface().toString().contains("idempotency-key-required"));
    assertTrue(missingIdempotencyReactivate.resultSurface().toString().contains("noFakeSuccess=true"));
    assertTrue(repository.customer(TENANT_ID, createdCustomerId).orElseThrow().active());
    assertBrowserSafe(missingIdempotencyReactivate.resultSurface());

    var hiddenCustomerReactivate = runAction(new CapabilityActionRequest(
        "action-customer-reactivate",
        "user-admin.reactivate-customer",
        "manage-customers",
        "tenant.customer.reactivate",
        Map.of("customerId", "cust-hidden", "reason", "hidden customer must not enumerate", "confirmation", "REACTIVATE"),
        "idem-customer-reactivate-hidden-denied",
        SELECTED_CONTEXT_ID,
        reactivateForm.resultSurface().surfaceId(),
        "corr-customer-reactivate-hidden-denied"));
    assertEquals("denied", hiddenCustomerReactivate.status());
    assertEquals("surface-user-admin-system-message", hiddenCustomerReactivate.resultSurface().surfaceId());
    assertTrue(hiddenCustomerReactivate.resultSurface().toString().contains("target-not-found-or-forbidden"));
    assertTrue(hiddenCustomerReactivate.resultSurface().toString().contains("noFakeSuccess=true"));
    assertFalse(hiddenCustomerReactivate.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(hiddenCustomerReactivate.resultSurface().toString().contains("tenant-hidden"));
    assertFalse(hiddenCustomerReactivate.resultSurface().toString().contains("hidden customer must not enumerate"));
    assertTrue(repository.customer("tenant-hidden", "cust-hidden").orElseThrow().active());
    assertBrowserSafe(hiddenCustomerReactivate.resultSurface());

    var customerAdminReactivateDenied = runActionAs(new CapabilityActionRequest(
        "action-customer-reactivate",
        "user-admin.reactivate-customer",
        "manage-customers",
        "tenant.customer.reactivate",
        Map.of("customerId", createdCustomerId, "reason", "customer admin must not reactivate customers", "confirmation", "REACTIVATE"),
        "idem-customer-reactivate-customer-admin-denied",
        "membership-customer-admin",
        reactivateForm.resultSurface().surfaceId(),
        "corr-customer-reactivate-customer-admin-denied"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerAdminReactivateDenied.status());
    assertEquals("surface-user-admin-system-message", customerAdminReactivateDenied.resultSurface().surfaceId());
    assertTrue(customerAdminReactivateDenied.resultSurface().toString().contains("scope-forbidden"));
    assertTrue(customerAdminReactivateDenied.resultSurface().toString().contains("noFakeSuccess=true"));
    assertFalse(customerAdminReactivateDenied.resultSurface().toString().contains("customer admin must not reactivate customers"));
    assertTrue(repository.customer(TENANT_ID, createdCustomerId).orElseThrow().active());
    assertBrowserSafe(customerAdminReactivateDenied.resultSurface());

    var hiddenCustomerSuspend = runAction(new CapabilityActionRequest(
        "action-customer-suspend",
        "user-admin.suspend-customer",
        "manage-customers",
        "tenant.customer.suspend",
        Map.of("customerId", "cust-hidden", "reason", "hidden customer must not enumerate", "confirmation", "SUSPEND"),
        "idem-customer-suspend-hidden-denied",
        SELECTED_CONTEXT_ID,
        suspendForm.resultSurface().surfaceId(),
        "corr-customer-suspend-hidden-denied"));
    assertEquals("denied", hiddenCustomerSuspend.status());
    assertEquals("surface-user-admin-system-message", hiddenCustomerSuspend.resultSurface().surfaceId());
    assertTrue(hiddenCustomerSuspend.resultSurface().toString().contains("target-not-found-or-forbidden"));
    assertTrue(hiddenCustomerSuspend.resultSurface().toString().contains("noFakeSuccess=true"));
    assertFalse(hiddenCustomerSuspend.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(hiddenCustomerSuspend.resultSurface().toString().contains("tenant-hidden"));
    assertFalse(hiddenCustomerSuspend.resultSurface().toString().contains("hidden customer must not enumerate"));
    assertTrue(repository.customer("tenant-hidden", "cust-hidden").orElseThrow().active());
    assertBrowserSafe(hiddenCustomerSuspend.resultSurface());

    var missingIdempotencySuspend = runAction(new CapabilityActionRequest(
        "action-customer-suspend",
        "user-admin.suspend-customer",
        "manage-customers",
        "tenant.customer.suspend",
        Map.of("customerId", "cust-alpha", "reason", "missing idempotency must fail closed", "confirmation", "SUSPEND"),
        null,
        SELECTED_CONTEXT_ID,
        suspendForm.resultSurface().surfaceId(),
        "corr-customer-suspend-missing-idempotency"));
    assertEquals("validation-error", missingIdempotencySuspend.status());
    assertEquals("surface-user-admin-system-message", missingIdempotencySuspend.resultSurface().surfaceId());
    assertTrue(missingIdempotencySuspend.resultSurface().toString().contains("idempotency-key-required"));
    assertTrue(missingIdempotencySuspend.resultSurface().toString().contains("noFakeSuccess=true"));
    assertTrue(repository.customer(TENANT_ID, "cust-alpha").orElseThrow().active());
    assertBrowserSafe(missingIdempotencySuspend.resultSurface());

    var customerAdminSuspendDenied = runActionAs(new CapabilityActionRequest(
        "action-customer-suspend",
        "user-admin.suspend-customer",
        "manage-customers",
        "tenant.customer.suspend",
        Map.of("customerId", "cust-alpha", "reason", "customer admin must not suspend customers", "confirmation", "SUSPEND"),
        "idem-customer-suspend-customer-admin-denied",
        "membership-customer-admin",
        suspendForm.resultSurface().surfaceId(),
        "corr-customer-suspend-customer-admin-denied"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerAdminSuspendDenied.status());
    assertEquals("surface-user-admin-system-message", customerAdminSuspendDenied.resultSurface().surfaceId());
    assertTrue(customerAdminSuspendDenied.resultSurface().toString().contains("scope-forbidden"));
    assertTrue(repository.customer(TENANT_ID, "cust-alpha").orElseThrow().active());
    assertBrowserSafe(customerAdminSuspendDenied.resultSurface());

    var directCustomerAdmins = getSurface("surface-user-admin-customer-admins", "corr-customer-admins-direct-no-target");
    assertEquals("surface-user-admin-customer-admins", directCustomerAdmins.surfaceId());
    assertEquals("list-search", directCustomerAdmins.surfaceType());
    assertEquals("user_admin.customer_admins.v1", directCustomerAdmins.data().get("surfaceContract"));
    assertTrue(directCustomerAdmins.toString().contains("require one selected Customer target"));
    assertTrue(directCustomerAdmins.toString().contains("visibleCount=0"));
    assertFalse(directCustomerAdmins.toString().contains("Alpha Customer"));
    assertFalse(directCustomerAdmins.toString().contains("customer-admin@example.test"));
    assertBrowserSafe(directCustomerAdmins);

    var directInviteForm = getSurface("surface-user-admin-customer-admin-invitation-create", "corr-customer-admin-invite-direct-no-target");
    assertEquals("surface-user-admin-customer-admin-invitation-create", directInviteForm.surfaceId());
    assertEquals("create-form", directInviteForm.surfaceType());
    assertEquals("user_admin.customer_admin_invitation_create.v1", directInviteForm.data().get("surfaceContract"));
    assertTrue(directInviteForm.toString().contains("Open this form from Customer detail"));
    assertTrue(directInviteForm.toString().contains("Provider/outbox failures return system-message without fake success"));
    assertTrue(directInviteForm.toString().contains("action-customer-admin-invite"));
    assertFalse(directInviteForm.toString().contains("Alpha Customer"));
    assertFalse(directInviteForm.toString().contains("customer-admin@example.test"));
    assertBrowserSafe(directInviteForm);

    var directCustomerAdminDetail = getSurface("surface-user-admin-customer-admin-detail", "corr-customer-admin-detail-direct-no-target");
    assertEquals("surface-user-admin-customer-admin-detail", directCustomerAdminDetail.surfaceId());
    assertEquals("show-inspection", directCustomerAdminDetail.surfaceType());
    assertEquals("user_admin.customer_admin_detail.v1", directCustomerAdminDetail.data().get("surfaceContract"));
    assertEquals("corr-customer-admin-detail-direct-no-target", directCustomerAdminDetail.correlationId());
    assertTrue(directCustomerAdminDetail.toString().contains("Customer Admin membership/invitation inspection"));
    assertTrue(directCustomerAdminDetail.toString().contains("canMutateInline=false"));
    assertTrue(directCustomerAdminDetail.toString().contains("action-user-admin-show-customer-admins"));
    assertFalse(directCustomerAdminDetail.toString().contains("Alpha Customer"));
    assertFalse(directCustomerAdminDetail.toString().contains("customer-admin@example.test"));
    assertBrowserSafe(directCustomerAdminDetail);

    var customerAdmins = runAction(new CapabilityActionRequest(
        "action-user-admin-show-customer-admins",
        "user-admin.show-customer-admins",
        "manage-customer-admins",
        "tenant.customer_admin.list",
        Map.of("customerId", "cust-alpha"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-customer-directory-admins-open"));
    assertEquals("accepted", customerAdmins.status());
    assertEquals("surface-user-admin-customer-admins", customerAdmins.resultSurface().surfaceId());
    assertEquals("list-search", customerAdmins.resultSurface().surfaceType());
    assertEquals("user_admin.customer_admins.v1", customerAdmins.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-customer-directory-admins-open", customerAdmins.resultSurface().correlationId());
    assertTrue(customerAdmins.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-admins")));
    assertTrue(customerAdmins.resultSurface().toString().contains("customerId=cust-alpha"));
    assertTrue(customerAdmins.resultSurface().toString().contains("Alpha Customer"));
    assertTrue(customerAdmins.resultSurface().toString().contains("targetScopeProof"));
    assertTrue(customerAdmins.resultSurface().toString().contains("scopeType=CUSTOMER"));
    assertTrue(customerAdmins.resultSurface().toString().contains("backend-authored-customer-detail"));
    assertTrue(customerAdmins.resultSurface().toString().contains("customer-admin@example.test"));
    assertTrue(customerAdmins.resultSurface().toString().contains("role=CUSTOMER_ADMIN"));
    assertTrue(customerAdmins.resultSurface().toString().contains("visibleCount=1"));
    assertTrue(customerAdmins.resultSurface().toString().contains("action-open-customer-admin-invitation-create"));
    assertTrue(customerAdmins.resultSurface().toString().contains("action-open-audit-trace"));
    assertTrue(customerAdmins.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-customers"));
    assertTrue(customerAdmins.resultSurface().toString().contains("sibling-customers-redacted"));
    assertTrue(customerAdmins.resultSurface().toString().contains("provider-payload-redacted"));
    assertFalse(customerAdmins.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(customerAdmins.resultSurface().toString().contains("tenant-hidden"));
    assertBrowserSafe(customerAdmins.resultSurface());

    var customerAdminMembershipDetail = runAction(new CapabilityActionRequest(
        "action-open-customer-admin-detail",
        "user-admin.open-customer-admin-detail",
        "manage-customer-admins",
        "tenant.customer_admin.list",
        Map.of("customerId", "cust-alpha", "membershipId", "membership-customer-admin"),
        null,
        SELECTED_CONTEXT_ID,
        customerAdmins.resultSurface().surfaceId(),
        "corr-customer-admin-membership-detail"));
    assertEquals("accepted", customerAdminMembershipDetail.status());
    assertEquals("corr-customer-admin-membership-detail", customerAdminMembershipDetail.correlationId());
    assertEquals("surface-user-admin-customer-admin-detail", customerAdminMembershipDetail.resultSurface().surfaceId());
    assertEquals("show-inspection", customerAdminMembershipDetail.resultSurface().surfaceType());
    assertEquals("user_admin.customer_admin_detail.v1", customerAdminMembershipDetail.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-customer-admin-membership-detail", customerAdminMembershipDetail.resultSurface().correlationId());
    assertTrue(customerAdminMembershipDetail.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-admin-detail")));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("recordKind=customer-admin-membership"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("Customer Admin"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("customer-admin@example.test"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("membershipId=membership-customer-admin"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("targetScopeProof"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("scopeType=CUSTOMER"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("inlineMutationAllowed=false"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("action-open-user-admin-role-change-preview"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("action-open-user-admin-membership-status-confirmation"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("action-user-admin-show-customer-admins"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("trace-customer-admin-detail"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("sibling-customers-redacted"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("tenant-app-data-redacted"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("provider-payload-redacted"));
    assertFalse(customerAdminMembershipDetail.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(customerAdminMembershipDetail.resultSurface().toString().contains("tenant-hidden"));
    assertBrowserSafe(customerAdminMembershipDetail.resultSurface());

    var inviteForm = runAction(new CapabilityActionRequest(
        "action-open-customer-admin-invitation-create",
        "user-admin.open-customer-admin-invite",
        "manage-customer-admins",
        "tenant.customer_admin.invite",
        Map.of("customerId", "cust-alpha"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-customer-directory-admin-invite-open"));
    assertEquals("accepted", inviteForm.status());
    assertEquals("surface-user-admin-customer-admin-invitation-create", inviteForm.resultSurface().surfaceId());
    assertEquals("user_admin.customer_admin_invitation_create.v1", inviteForm.resultSurface().data().get("surfaceContract"));
    assertTrue(inviteForm.resultSurface().toString().contains("CUSTOMER_ADMIN"));
    assertTrue(inviteForm.resultSurface().toString().contains("outboxReadiness=backend-derived"));
    assertTrue(inviteForm.resultSurface().toString().contains("provider-payload-redacted"));
    assertTrue(inviteForm.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-customers"));
    assertBrowserSafe(inviteForm.resultSurface());

    var customerAdminInvite = runAction(new CapabilityActionRequest(
        "action-customer-admin-invite",
        "user-admin.invite-customer-admin",
        "manage-customer-admins",
        "tenant.customer_admin.invite",
        Map.of("customerId", "cust-alpha", "email", "new.customer.admin@example.test", "displayName", "New Customer Admin", "roles", "CUSTOMER_ADMIN", "reason", "browser smoke customer admin bootstrap"),
        "idem-customer-admin-invite-browser-smoke",
        SELECTED_CONTEXT_ID,
        inviteForm.resultSurface().surfaceId(),
        "corr-customer-admin-invite-submit"));
    assertEquals("accepted", customerAdminInvite.status());
    assertEquals("corr-customer-admin-invite-submit", customerAdminInvite.correlationId());
    assertEquals("surface-user-admin-invitation-detail", customerAdminInvite.resultSurface().surfaceId());
    assertEquals("user_admin.invitation_detail.v1", customerAdminInvite.resultSurface().data().get("surfaceContract"));
    assertTrue(customerAdminInvite.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-admin-invite-created")));
    assertTrue(customerAdminInvite.resultSurface().toString().contains("recordKind=customer-admin-invitation"));
    assertTrue(customerAdminInvite.resultSurface().toString().contains("new.customer.admin@example.test"));
    assertTrue(customerAdminInvite.resultSurface().toString().contains("scopeType=CUSTOMER"));
    assertTrue(customerAdminInvite.resultSurface().toString().contains("customerId=cust-alpha"));
    assertTrue(customerAdminInvite.resultSurface().toString().contains("status=sent"));
    assertTrue(customerAdminInvite.resultSurface().toString().contains("invitation-token-redacted"));
    assertBrowserSafe(customerAdminInvite.resultSurface());

    var invitationRepository = new AkkaInvitationRepository(componentClient);
    var savedCustomerAdminInvite = invitationRepository.findByIdempotencyKey("idem-customer-admin-invite-browser-smoke").orElseThrow();
    assertEquals(ScopeType.CUSTOMER, savedCustomerAdminInvite.scopeType());
    assertEquals(TENANT_ID, savedCustomerAdminInvite.tenantId());
    assertEquals("cust-alpha", savedCustomerAdminInvite.customerId());
    assertEquals(List.of(FoundationRole.CUSTOMER_ADMIN), savedCustomerAdminInvite.requestedRoles());
    assertEquals(InvitationStatus.SENT, savedCustomerAdminInvite.status());
    assertTrue(List.of(EmailDeliveryStatus.SENT, EmailDeliveryStatus.CAPTURED).contains(savedCustomerAdminInvite.deliveryStatus()));
    assertEquals("corr-customer-admin-invite-submit", savedCustomerAdminInvite.correlationId());
    assertTrue(invitationRepository.queuedEmails().stream().anyMatch(message -> message.invitationId().equals(savedCustomerAdminInvite.invitationId()) && message.scopeType() == ScopeType.CUSTOMER && "cust-alpha".equals(message.customerId()) && message.correlationId().equals("corr-customer-admin-invite-submit")));

    var replayedCustomerAdminInvite = runAction(new CapabilityActionRequest(
        "action-customer-admin-invite",
        "user-admin.invite-customer-admin",
        "manage-customer-admins",
        "tenant.customer_admin.invite",
        Map.of("customerId", "cust-alpha", "email", "new.customer.admin@example.test", "displayName", "New Customer Admin", "roles", "CUSTOMER_ADMIN", "reason", "browser smoke customer admin bootstrap replay"),
        "idem-customer-admin-invite-browser-smoke",
        SELECTED_CONTEXT_ID,
        inviteForm.resultSurface().surfaceId(),
        "corr-customer-admin-invite-submit-replay"));
    assertEquals("accepted", replayedCustomerAdminInvite.status());
    assertEquals("surface-user-admin-invitation-detail", replayedCustomerAdminInvite.resultSurface().surfaceId());
    assertTrue(replayedCustomerAdminInvite.resultSurface().toString().contains(savedCustomerAdminInvite.invitationId()));
    assertEquals(1, invitationRepository.invitations().stream().filter(invitation -> "new.customer.admin@example.test".equals(invitation.normalizedEmail()) && ScopeType.CUSTOMER == invitation.scopeType() && "cust-alpha".equals(invitation.customerId())).count());
    assertBrowserSafe(replayedCustomerAdminInvite.resultSurface());

    var customerAdminInvitationDetail = runAction(new CapabilityActionRequest(
        "action-open-customer-admin-invitation-detail",
        "user-admin.open-customer-admin-invitation-detail",
        "manage-customer-admins",
        "tenant.customer_admin.list",
        Map.of("customerId", "cust-alpha", "invitationId", savedCustomerAdminInvite.invitationId()),
        null,
        SELECTED_CONTEXT_ID,
        customerAdmins.resultSurface().surfaceId(),
        "corr-customer-admin-invitation-detail"));
    assertEquals("accepted", customerAdminInvitationDetail.status());
    assertEquals("corr-customer-admin-invitation-detail", customerAdminInvitationDetail.correlationId());
    assertEquals("surface-user-admin-customer-admin-detail", customerAdminInvitationDetail.resultSurface().surfaceId());
    assertEquals("show-inspection", customerAdminInvitationDetail.resultSurface().surfaceType());
    assertEquals("user_admin.customer_admin_detail.v1", customerAdminInvitationDetail.resultSurface().data().get("surfaceContract"));
    assertTrue(customerAdminInvitationDetail.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-admin-detail")));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("recordKind=customer-admin-invitation"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains(savedCustomerAdminInvite.invitationId()));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("new.customer.admin@example.test"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("scopeType=CUSTOMER"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("deliveryReadiness"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("action-open-useradmin-invitation-resend-confirmation"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("action-open-useradmin-invitation-revoke-confirmation"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("invitation-token-redacted"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("provider-payload-redacted"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("sibling-customers-redacted"));
    assertFalse(customerAdminInvitationDetail.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(customerAdminInvitationDetail.resultSurface().toString().contains("tenant-hidden"));
    assertBrowserSafe(customerAdminInvitationDetail.resultSurface());

    var unsupportedCustomerAdminRole = runAction(new CapabilityActionRequest(
        "action-customer-admin-invite",
        "user-admin.invite-customer-admin",
        "manage-customer-admins",
        "tenant.customer_admin.invite",
        Map.of("customerId", "cust-alpha", "email", "customer.user.invalid@example.test", "displayName", "Customer User", "roles", "CUSTOMER_USER"),
        "idem-customer-admin-invite-invalid-role-browser-smoke",
        SELECTED_CONTEXT_ID,
        inviteForm.resultSurface().surfaceId(),
        "corr-customer-admin-invite-invalid-role"));
    assertEquals("validation-error", unsupportedCustomerAdminRole.status());
    assertEquals("surface-user-admin-customer-admin-invitation-create", unsupportedCustomerAdminRole.resultSurface().surfaceId());
    assertTrue(unsupportedCustomerAdminRole.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-customer-admin-invite-validation")));
    assertTrue(unsupportedCustomerAdminRole.resultSurface().toString().contains("CUSTOMER_ADMIN"));
    assertFalse(invitationRepository.invitations().stream().anyMatch(invitation -> "customer.user.invalid@example.test".equals(invitation.normalizedEmail())));
    assertBrowserSafe(unsupportedCustomerAdminRole.resultSurface());

    var hiddenCustomerAdminInvite = runActionAs(new CapabilityActionRequest(
        "action-customer-admin-invite",
        "user-admin.invite-customer-admin",
        "manage-customer-admins",
        "tenant.customer_admin.invite",
        Map.of("customerId", "cust-hidden", "email", "hidden.customer.admin@example.test", "displayName", "Hidden Customer Admin", "roles", "CUSTOMER_ADMIN"),
        "idem-customer-admin-invite-hidden-browser-smoke",
        SELECTED_CONTEXT_ID,
        inviteForm.resultSurface().surfaceId(),
        "corr-customer-admin-invite-hidden-submit"), "workos-admin", "admin@example.test", "Tenant Admin", SELECTED_CONTEXT_ID);
    assertEquals("denied", hiddenCustomerAdminInvite.status());
    assertEquals("surface-user-admin-system-message", hiddenCustomerAdminInvite.resultSurface().surfaceId());
    assertTrue(hiddenCustomerAdminInvite.resultSurface().toString().contains("target-not-found-or-forbidden"));
    assertFalse(hiddenCustomerAdminInvite.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(hiddenCustomerAdminInvite.resultSurface().toString().contains("hidden.customer.admin@example.test"));
    assertFalse(invitationRepository.invitations().stream().anyMatch(invitation -> "hidden.customer.admin@example.test".equals(invitation.normalizedEmail())));
    assertBrowserSafe(hiddenCustomerAdminInvite.resultSurface());

    var customerAdminInviteDenied = runActionAs(new CapabilityActionRequest(
        "action-customer-admin-invite",
        "user-admin.invite-customer-admin",
        "manage-customer-admins",
        "tenant.customer_admin.invite",
        Map.of("customerId", "cust-alpha", "email", "peer.customer.admin@example.test", "displayName", "Peer Customer Admin", "roles", "CUSTOMER_ADMIN"),
        "idem-customer-admin-invite-customer-admin-denied",
        "membership-customer-admin",
        inviteForm.resultSurface().surfaceId(),
        "corr-customer-admin-invite-customer-admin-denied"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerAdminInviteDenied.status());
    assertEquals("surface-user-admin-system-message", customerAdminInviteDenied.resultSurface().surfaceId());
    assertTrue(customerAdminInviteDenied.resultSurface().toString().contains("scope-forbidden"));
    assertFalse(customerAdminInviteDenied.resultSurface().toString().contains("Alpha Customer"));
    assertFalse(customerAdminInviteDenied.resultSurface().toString().contains("peer.customer.admin@example.test"));
    assertFalse(invitationRepository.invitations().stream().anyMatch(invitation -> "peer.customer.admin@example.test".equals(invitation.normalizedEmail())));
    assertBrowserSafe(customerAdminInviteDenied.resultSurface());

    var hiddenRead = runActionAs(new CapabilityActionRequest(
        "action-customer-read",
        "user-admin.read-customer",
        "manage-customers",
        "tenant.customer.read",
        Map.of("customerId", "cust-hidden"),
        null,
        SELECTED_CONTEXT_ID,
        direct.surfaceId(),
        "corr-customer-directory-hidden-read"), "workos-admin", "admin@example.test", "Tenant Admin", SELECTED_CONTEXT_ID);
    assertEquals("denied", hiddenRead.status());
    assertEquals("surface-user-admin-system-message", hiddenRead.resultSurface().surfaceId());
    assertTrue(hiddenRead.resultSurface().toString().contains("target-not-found-or-forbidden"));
    assertFalse(hiddenRead.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(hiddenRead.resultSurface().toString().contains("tenant-hidden"));
    assertBrowserSafe(hiddenRead.resultSurface());

    var hiddenCustomerAdmins = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-customer-admins",
        "user-admin.show-customer-admins",
        "manage-customer-admins",
        "tenant.customer_admin.list",
        Map.of("customerId", "cust-hidden"),
        null,
        SELECTED_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-customer-admins-hidden-read"), "workos-admin", "admin@example.test", "Tenant Admin", SELECTED_CONTEXT_ID);
    assertEquals("denied", hiddenCustomerAdmins.status());
    assertEquals("surface-user-admin-system-message", hiddenCustomerAdmins.resultSurface().surfaceId());
    assertTrue(hiddenCustomerAdmins.resultSurface().toString().contains("target-not-found-or-forbidden"));
    assertFalse(hiddenCustomerAdmins.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(hiddenCustomerAdmins.resultSurface().toString().contains("tenant-hidden"));
    assertFalse(hiddenCustomerAdmins.resultSurface().toString().contains("sibling.customer.admin@example.test"));
    assertBrowserSafe(hiddenCustomerAdmins.resultSurface());

    var hiddenCustomerAdminDetail = runActionAs(new CapabilityActionRequest(
        "action-open-customer-admin-detail",
        "user-admin.open-customer-admin-detail",
        "manage-customer-admins",
        "tenant.customer_admin.list",
        Map.of("customerId", "cust-hidden", "membershipId", "membership-customer-admin"),
        null,
        SELECTED_CONTEXT_ID,
        customerAdmins.resultSurface().surfaceId(),
        "corr-customer-admin-detail-hidden-read"), "workos-admin", "admin@example.test", "Tenant Admin", SELECTED_CONTEXT_ID);
    assertEquals("denied", hiddenCustomerAdminDetail.status());
    assertEquals("surface-user-admin-system-message", hiddenCustomerAdminDetail.resultSurface().surfaceId());
    assertTrue(hiddenCustomerAdminDetail.resultSurface().toString().contains("target-not-found-or-forbidden"));
    assertFalse(hiddenCustomerAdminDetail.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(hiddenCustomerAdminDetail.resultSurface().toString().contains("tenant-hidden"));
    assertFalse(hiddenCustomerAdminDetail.resultSurface().toString().contains("customer-admin@example.test"));
    assertBrowserSafe(hiddenCustomerAdminDetail.resultSurface());

    var customerAdminListDenied = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-customer-admins",
        "user-admin.show-customer-admins",
        "manage-customer-admins",
        "tenant.customer_admin.list",
        Map.of("customerId", "cust-alpha"),
        null,
        "membership-customer-admin",
        detail.resultSurface().surfaceId(),
        "corr-customer-admins-customer-admin-denied-action"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerAdminListDenied.status());
    assertEquals("surface-user-admin-system-message", customerAdminListDenied.resultSurface().surfaceId());
    assertTrue(customerAdminListDenied.resultSurface().toString().contains("scope-forbidden"));
    assertFalse(customerAdminListDenied.resultSurface().toString().contains("Alpha Customer"));
    assertFalse(customerAdminListDenied.resultSurface().toString().contains("Beta Customer"));
    assertFalse(customerAdminListDenied.resultSurface().toString().contains("Hidden Customer"));
    assertBrowserSafe(customerAdminListDenied.resultSurface());

    var customerAdminDetailDenied = runActionAs(new CapabilityActionRequest(
        "action-open-customer-admin-detail",
        "user-admin.open-customer-admin-detail",
        "manage-customer-admins",
        "tenant.customer_admin.list",
        Map.of("customerId", "cust-alpha", "membershipId", "membership-customer-admin"),
        null,
        "membership-customer-admin",
        customerAdmins.resultSurface().surfaceId(),
        "corr-customer-admin-detail-customer-admin-denied-action"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerAdminDetailDenied.status());
    assertEquals("surface-user-admin-system-message", customerAdminDetailDenied.resultSurface().surfaceId());
    assertTrue(customerAdminDetailDenied.resultSurface().toString().contains("scope-forbidden"));
    assertFalse(customerAdminDetailDenied.resultSurface().toString().contains("Alpha Customer"));
    assertFalse(customerAdminDetailDenied.resultSurface().toString().contains("customer-admin@example.test"));
    assertBrowserSafe(customerAdminDetailDenied.resultSurface());

    var customerAdminReadDenied = runActionAs(new CapabilityActionRequest(
        "action-customer-read",
        "user-admin.read-customer",
        "manage-customers",
        "tenant.customer.read",
        Map.of("customerId", "cust-alpha"),
        null,
        "membership-customer-admin",
        direct.surfaceId(),
        "corr-customer-detail-customer-admin-denied-action"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerAdminReadDenied.status());
    assertEquals("surface-user-admin-system-message", customerAdminReadDenied.resultSurface().surfaceId());
    assertTrue(customerAdminReadDenied.resultSurface().toString().contains("scope-forbidden"));
    assertFalse(customerAdminReadDenied.resultSurface().toString().contains("Beta Customer"));
    assertFalse(customerAdminReadDenied.resultSurface().toString().contains("Hidden Customer"));
    assertFalse(customerAdminReadDenied.resultSurface().toString().contains("tenant-hidden"));
    assertBrowserSafe(customerAdminReadDenied.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-user-admin-customer-directory",
        "corr-customer-directory-customer-admin-denied-direct",
        "workos-customer-admin",
        "customer-admin@example.test",
        "Customer Admin",
        "membership-customer-admin"),
        "Customer Admin selected contexts must not direct-load the Organization/Tenant Customer Directory.");

    var customerAdminDenied = runActionAs(new CapabilityActionRequest(
        "action-user-admin-show-customers",
        "user-admin.show-customers",
        "manage-customers",
        "tenant.customer.list",
        null,
        null,
        "membership-customer-admin",
        "surface-user-admin-dashboard",
        "corr-customer-directory-customer-admin-denied-action"), "workos-customer-admin", "customer-admin@example.test", "Customer Admin", "membership-customer-admin");
    assertEquals("denied", customerAdminDenied.status());
    assertEquals("surface-user-admin-system-message", customerAdminDenied.resultSurface().surfaceId());
    assertTrue(customerAdminDenied.resultSurface().toString().contains("scope-forbidden"));
    assertFalse(customerAdminDenied.resultSurface().toString().contains("Beta Customer"));
    assertFalse(customerAdminDenied.resultSurface().toString().contains("Hidden Customer"));
    assertBrowserSafe(customerAdminDenied.resultSurface());
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
