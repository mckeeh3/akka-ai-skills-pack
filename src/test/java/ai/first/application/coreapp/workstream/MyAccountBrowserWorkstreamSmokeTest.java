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
import ai.first.application.coreapp.myaccount.AkkaMyAccountPersonalAttentionDigestTaskRepository;
import ai.first.application.foundation.identity.AkkaIdentityRepository;
import ai.first.application.foundation.notification.AkkaNotificationRepository;
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
import ai.first.domain.foundation.notification.NotificationCategory;
import ai.first.domain.foundation.notification.NotificationChannel;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationLifecycleStatus;
import ai.first.domain.foundation.notification.NotificationPriority;
import ai.first.domain.foundation.notification.NotificationRedactionLevel;
import ai.first.domain.foundation.notification.NotificationSourceRef;
import ai.first.domain.foundation.notification.NotificationSurfaceRef;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Scriptable hosted-UI/workstream smoke for the My Account dashboard surface runtime path. */
class MyAccountBrowserWorkstreamSmokeTest extends TestKitSupport {
  private static final String TENANT_ID = "tenant-starter";
  private static final String CUSTOMER_ID = "customer-starter";
  private static final String ADMIN_CONTEXT_ID = "membership-admin";
  private static final String ADMIN_CUSTOMER_CONTEXT_ID = "membership-admin-customer";
  private static final String MEMBER_CONTEXT_ID = "membership-member";

  @BeforeEach
  void seedMyAccountSmokeActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(TENANT_ID, "Starter Tenant", true));
    repository.saveCustomer(new Customer(TENANT_ID, CUSTOMER_ID, "Starter Customer", true));
    seedIdentity(repository, "admin@example.test", "Tenant Admin", ADMIN_CONTEXT_ID, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    repository.saveMembership(new Membership(ADMIN_CUSTOMER_CONTEXT_ID, "admin@example.test", ScopeType.CUSTOMER, TENANT_ID, CUSTOMER_ID, List.of(FoundationRole.CUSTOMER_ADMIN), MembershipStatus.ACTIVE, false, null));
    seedIdentity(repository, "member@example.test", "Member User", MEMBER_CONTEXT_ID, List.of(FoundationRole.TENANT_EMPLOYEE));
  }

  @Test
  void hostedShellAndProtectedWorkstreamApiExerciseMyAccountDashboardRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load from Akka static resources.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertFalse(shell.body().contains("providerSecret"));
    assertFalse(shell.body().contains("Bearer "));

    var authCallbackShell = httpClient.GET("/callback").responseBodyAs(String.class).invoke();
    assertTrue(authCallbackShell.status().isSuccess(), "WorkOS/AuthKit redirect callback must return the SPA shell, not a 404.");
    assertTrue(authCallbackShell.body().contains("<div id=\"root\"></div>"));

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-account-browser-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(ADMIN_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("my-account-agent") && agent.availability().equals("visible")));
    assertEquals("surface-my-account-dashboard", bootstrap.body().surfaces().get(0).surfaceId());
    assertBrowserSafe(bootstrap.body());

    var dashboard = getSurface("surface-my-account-dashboard", ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin", "corr-my-account-browser-dashboard");
    assertEquals("surface-my-account-dashboard", dashboard.surfaceId());
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("my_account.personal_command_center.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.toString().contains("attentionCounters"));
    assertTrue(dashboard.toString().contains("controlPanels"));
    assertTrue(dashboard.toString().contains("my_account.view_summary"));
    assertTrue(dashboard.toString().contains("my_account.view_context"));
    assertTrue(dashboard.toString().contains("my_account.open_authorized_workstream"));
    assertTrue(dashboard.toString().contains("traceRefs"));
    assertTrue(dashboard.toString().contains("corr-my-account-browser-dashboard"));
    assertTrue(dashboard.toString().contains("blocked_provider_or_runtime"), "Dashboard must expose provider fail-closed digest state without faking success.");
    assertFalse(dashboard.toString().contains("providerSecret"));
    assertFalse(dashboard.toString().contains("RESEND_API_KEY"));
    assertBrowserSafe(dashboard);

    var digest = runAction(new CapabilityActionRequest(
        "action-start-my-account-personal-attention-digest",
        "action-start-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.start",
        "my_account.personal_attention_digest.start",
        Map.of("evidenceScope", "attention"),
        "idem-my-account-browser-digest",
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-my-account-browser-digest"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("blocked_provider_or_runtime", digest.status());
    assertNotNull(digest.resultSurface());
    assertEquals("surface-my-account-personal-attention-digest-blocked", digest.resultSurface().surfaceId());
    assertEquals("my_account.personal_attention_digest.blocked.v1", digest.resultSurface().data().get("surfaceContract"));
    assertEquals(true, digest.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, digest.resultSurface().data().get("noDirectMutation"));
    assertTrue(digest.resultSurface().toString().contains("trace"));
    assertFalse(digest.resultSurface().toString().contains("test-fake-model"));
    assertBrowserSafe(digest.resultSurface());

    var openedAgentAdmin = runAction(new CapabilityActionRequest(
        "action-open-agent-admin",
        "action-open-agent-admin",
        "my_account.open_authorized_workstream",
        "my_account.open_authorized_workstream",
        Map.of(),
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-my-account-browser-open-agent-admin"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", openedAgentAdmin.status());
    assertEquals("surface-agent-admin-dashboard", openedAgentAdmin.resultSurface().surfaceId());
    assertEquals("agent-admin-agent", openedAgentAdmin.resultSurface().ownerFunctionalAgentId());
    assertTrue(openedAgentAdmin.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-open")));
    assertBrowserSafe(openedAgentAdmin.resultSurface());

    var memberDashboard = getSurface("surface-my-account-dashboard", MEMBER_CONTEXT_ID, "member@example.test", "Member User", "corr-my-account-browser-member-dashboard");
    var denied = runAction(new CapabilityActionRequest(
        "action-open-agent-admin",
        "action-open-agent-admin",
        "my_account.open_authorized_workstream",
        "my_account.open_authorized_workstream",
        null,
        null,
        MEMBER_CONTEXT_ID,
        memberDashboard.surfaceId(),
        "corr-my-account-browser-denied-agent-admin"), MEMBER_CONTEXT_ID, "member@example.test", "Member User");
    assertEquals("denied", denied.status());
    assertEquals("system_message", denied.resultSurface().surfaceType());
    assertEquals("surface-my-account-open-denied", denied.resultSurface().surfaceId());
    assertEquals("my_account.open_denied.v1", denied.resultSurface().data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", denied.resultSurface().data().get("status"));
    assertEquals("not_found_or_redacted", denied.resultSurface().data().get("decision"));
    assertEquals(true, denied.resultSurface().data().get("noEnumeration"));
    assertTrue(denied.resultSurface().data().toString().contains("recoveryStepDetails"));
    assertTrue(denied.resultSurface().data().toString().contains("availableActions"));
    assertFalse(denied.resultSurface().toString().contains("agent_admin.list_definitions"));
    assertFalse(denied.resultSurface().toString().contains("Agent Admin"));
    assertTrue(denied.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-open")));
    assertBrowserSafe(denied.resultSurface());

    var directDenied = getSurface("surface-my-account-open-denied", MEMBER_CONTEXT_ID, "member@example.test", "Member User", "corr-my-account-browser-open-denied-direct");
    assertEquals("my_account.open_denied.v1", directDenied.data().get("surfaceContract"));
    assertEquals("not_available_in_selected_context", directDenied.data().get("safeReasonCode"));
    assertEquals(true, directDenied.data().get("noEnumeration"));
    assertTrue(directDenied.data().toString().contains("action-show-my-account-dashboard"));
    assertTrue(directDenied.data().toString().contains("corr-my-account-browser-open-denied-direct"));
    assertBrowserSafe(directDenied);

    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-account-dashboard").responseBodyAs(String.class).invoke());
  }

  @Test
  void protectedWorkstreamApiExercisesMyAccountOpenDeniedRuntimePath() throws Exception {
    var memberDashboard = getSurface("surface-my-account-dashboard", MEMBER_CONTEXT_ID, "member@example.test", "Member User", "corr-open-denied-member-dashboard");
    assertEquals("surface-my-account-dashboard", memberDashboard.surfaceId());

    var denied = runAction(new CapabilityActionRequest(
        "action-open-agent-admin",
        "action-open-agent-admin",
        "my_account.open_authorized_workstream",
        "my_account.open_authorized_workstream",
        Map.of("requestedWorkstreamId", "agent-admin"),
        "idem-open-denied-agent-admin",
        MEMBER_CONTEXT_ID,
        memberDashboard.surfaceId(),
        "corr-open-denied-action"), MEMBER_CONTEXT_ID, "member@example.test", "Member User");
    assertEquals("denied", denied.status());
    assertEquals("corr-open-denied-action", denied.correlationId());
    assertTrue(denied.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-open")));
    assertNotNull(denied.resultSurface());
    assertEquals("surface-my-account-open-denied", denied.resultSurface().surfaceId());
    assertEquals("system_message", denied.resultSurface().surfaceType());
    assertEquals("my_account.open_denied.v1", denied.resultSurface().data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", denied.resultSurface().data().get("status"));
    assertEquals("not_found_or_redacted", denied.resultSurface().data().get("decision"));
    assertEquals(true, denied.resultSurface().data().get("noEnumeration"));
    assertTrue(denied.resultSurface().data().toString().contains("action-show-my-account-dashboard"));
    assertTrue(denied.resultSurface().data().toString().contains("action-show-my-context"));
    assertTrue(denied.resultSurface().data().toString().contains("request-access-guidance"));
    assertTrue(denied.resultSurface().data().toString().contains("corr-open-denied-action"));
    assertOpenDeniedBrowserSafe(denied.resultSurface());

    var directDenied = getSurface("surface-my-account-open-denied", MEMBER_CONTEXT_ID, "member@example.test", "Member User", "corr-open-denied-direct");
    assertEquals("surface-my-account-open-denied", directDenied.surfaceId());
    assertEquals("system_message", directDenied.surfaceType());
    assertEquals("my_account.open_denied.v1", directDenied.data().get("surfaceContract"));
    assertEquals("not_available_in_selected_context", directDenied.data().get("safeReasonCode"));
    assertEquals(true, directDenied.data().get("noEnumeration"));
    assertTrue(directDenied.data().toString().contains("action-show-my-account-dashboard"));
    assertTrue(directDenied.data().toString().contains("action-show-my-context"));
    assertTrue(directDenied.data().toString().contains("corr-open-denied-direct"));
    assertOpenDeniedBrowserSafe(directDenied);

    assertThrows(RuntimeException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-my-account-open-denied")
        .addHeader("X-Selected-Context-Id", MEMBER_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-open-denied-missing-bearer")
        .responseBodyAs(String.class)
        .invoke());
  }

  @Test
  void protectedWorkstreamApiExercisesMyProfileRuntimePathAndDenials() throws Exception {
    var profile = getSurface("surface-my-profile", ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin", "corr-my-profile-browser-read");
    assertEquals("surface-my-profile", profile.surfaceId());
    assertEquals("detail-edit", profile.surfaceType());
    assertEquals("my_account.profile.self_service.v1", profile.data().get("surfaceContract"));
    assertTrue(profile.toString().contains("profileSummary"));
    assertTrue(profile.toString().contains("providerBoundarySummary"));
    assertTrue(profile.toString().contains("fields"));
    assertTrue(profile.toString().contains("permissionState"));
    assertTrue(profile.toString().contains("my_account.update_profile_settings"));
    assertTrue(profile.toString().contains("core.profile.update"));
    assertTrue(profile.toString().contains("traceRefs"));
    assertTrue(profile.toString().contains("corr-my-profile-browser-read"));
    assertFalse(profile.toString().contains("role editor"));
    assertProfileSurfaceBrowserSafe(profile);

    var update = runAction(new CapabilityActionRequest(
        "action-update-my-profile",
        "action-update-my-profile",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        Map.of("displayName", "Updated Browser Admin"),
        "idem-my-profile-browser-update",
        ADMIN_CONTEXT_ID,
        profile.surfaceId(),
        "corr-my-profile-browser-update"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", update.status());
    assertEquals("surface-my-profile", update.resultSurface().surfaceId());
    assertEquals("my_account.profile.self_service.v1", update.resultSurface().data().get("surfaceContract"));
    assertTrue(update.resultSurface().toString().contains("Updated Browser Admin"));
    assertTrue(update.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-profile-settings")));
    assertProfileSurfaceBrowserSafe(update.resultSurface());

    var postUpdateBootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-profile-browser-bootstrap-after-update")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(postUpdateBootstrap.status().isSuccess());
    assertEquals("Updated Browser Admin", postUpdateBootstrap.body().me().profile().displayName());
    assertBrowserSafe(postUpdateBootstrap.body());

    var repeatSamePayload = runAction(new CapabilityActionRequest(
        "action-update-my-profile",
        "action-update-my-profile",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        Map.of("displayName", "Updated Browser Admin"),
        "idem-my-profile-browser-update",
        ADMIN_CONTEXT_ID,
        profile.surfaceId(),
        "corr-my-profile-browser-repeat"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertTrue(List.of("accepted", "no-op").contains(repeatSamePayload.status()));
    assertTrue(repeatSamePayload.resultSurface().toString().contains("Updated Browser Admin"));
    assertFalse(repeatSamePayload.resultSurface().toString().contains("Ignored Browser Duplicate"));
    assertProfileSurfaceBrowserSafe(repeatSamePayload.resultSurface());

    var noOp = runAction(new CapabilityActionRequest(
        "action-update-my-profile",
        "action-update-my-profile",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        Map.of("displayName", "Updated Browser Admin"),
        "idem-my-profile-browser-noop",
        ADMIN_CONTEXT_ID,
        profile.surfaceId(),
        "corr-my-profile-browser-noop"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("no-op", noOp.status());
    assertEquals("surface-my-profile", noOp.resultSurface().surfaceId());
    assertTrue(noOp.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-profile-settings")));
    assertProfileSurfaceBrowserSafe(noOp.resultSurface());

    var unsupportedMutation = new CapabilityActionRequest(
        "action-update-my-profile",
        "action-update-my-profile",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        Map.of("roleIds", List.of("tenant-admin")),
        "idem-my-profile-browser-denied",
        ADMIN_CONTEXT_ID,
        profile.surfaceId(),
        "corr-my-profile-browser-denied");
    var unsupportedResult = runAction(unsupportedMutation, ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("validation-error", unsupportedResult.status());
    assertEquals("surface-my-profile", unsupportedResult.resultSurface().surfaceId());
    assertTrue(unsupportedResult.resultSurface().data().toString().contains("MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD".toLowerCase(Locale.ROOT)) || unsupportedResult.resultSurface().data().toString().contains("unsupported"));

    var afterDenied = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-profile-browser-after-denied")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(afterDenied.status().isSuccess());
    assertEquals("Updated Browser Admin", afterDenied.body().me().profile().displayName(), "Unsupported self-service fields must be denied before profile mutation.");
    assertBrowserSafe(afterDenied.body());

    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-profile").responseBodyAs(String.class).invoke());
  }

  @Test
  void protectedWorkstreamApiExercisesMyContextRuntimePathAndSelection() throws Exception {
    var context = getSurface("surface-my-context", ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin", "corr-my-context-browser-read");
    assertEquals("surface-my-context", context.surfaceId());
    assertEquals("detail-edit", context.surfaceType());
    assertEquals("my_account.context_authority.v1", context.data().get("surfaceContract"));
    assertTrue(context.toString().contains("selectedContext"));
    assertTrue(context.toString().contains("availableContexts"));
    assertTrue(context.toString().contains(ADMIN_CUSTOMER_CONTEXT_ID));
    assertTrue(context.toString().contains("visibleCapabilitySummary"));
    assertTrue(context.toString().contains("supportAccess"));
    assertTrue(context.toString().contains("not_found_or_redacted"));
    assertTrue(context.toString().contains("core.access.context.select"));
    assertTrue(context.toString().contains("traceRefs"));
    assertTrue(context.toString().contains("corr-my-context-browser-read"));
    assertContextSurfaceBrowserSafe(context);

    var selected = runAction(new CapabilityActionRequest(
        "action-select-my-context",
        "action-select-my-context",
        "core.access.context.select",
        "core.access.context.select",
        Map.of("selectedContextId", ADMIN_CUSTOMER_CONTEXT_ID),
        null,
        ADMIN_CUSTOMER_CONTEXT_ID,
        context.surfaceId(),
        "corr-my-context-browser-select"), ADMIN_CUSTOMER_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("no-op", selected.status());
    assertEquals("surface-my-context", selected.resultSurface().surfaceId());
    assertEquals(ADMIN_CUSTOMER_CONTEXT_ID, selected.resultSurface().authContext().get("selectedContextId"));
    assertTrue(selected.resultSurface().toString().contains(CUSTOMER_ID));
    assertTrue(selected.resultSurface().toString().contains("staleImpact"));
    assertContextSurfaceBrowserSafe(selected.resultSurface());

    var noOpSelection = runAction(new CapabilityActionRequest(
        "action-select-my-context",
        "action-select-my-context",
        "core.access.context.select",
        "core.access.context.select",
        Map.of("selectedContextId", ADMIN_CUSTOMER_CONTEXT_ID),
        null,
        ADMIN_CUSTOMER_CONTEXT_ID,
        selected.resultSurface().surfaceId(),
        "corr-my-context-browser-select-noop"), ADMIN_CUSTOMER_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("no-op", noOpSelection.status());
    assertEquals("surface-my-context", noOpSelection.resultSurface().surfaceId());
    assertEquals(ADMIN_CUSTOMER_CONTEXT_ID, noOpSelection.resultSurface().authContext().get("selectedContextId"));
    assertTrue(noOpSelection.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-context-select")));
    assertContextSurfaceBrowserSafe(noOpSelection.resultSurface());

    var switchedBootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CUSTOMER_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-context-browser-bootstrap-after-select")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(switchedBootstrap.status().isSuccess());
    assertEquals(ADMIN_CUSTOMER_CONTEXT_ID, switchedBootstrap.body().me().selectedAuthContext().selectedContextId());
    assertEquals(CUSTOMER_ID, switchedBootstrap.body().me().selectedAuthContext().customerId());
    assertBrowserSafe(switchedBootstrap.body());

    var hiddenContextSelection = new CapabilityActionRequest(
        "action-select-my-context",
        "action-select-my-context",
        "core.access.context.select",
        "core.access.context.select",
        Map.of("selectedContextId", "membership-hidden-cross-tenant"),
        null,
        ADMIN_CONTEXT_ID,
        context.surfaceId(),
        "corr-my-context-browser-hidden-action-denied");
    var hiddenContextResult = runAction(hiddenContextSelection, ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("denied", hiddenContextResult.status());
    assertEquals("surface-my-context", hiddenContextResult.resultSurface().surfaceId());
    assertThrows(RuntimeException.class, () -> getSurface("surface-my-context", "membership-hidden-cross-tenant", "admin@example.test", "Tenant Admin", "corr-my-context-hidden-denied"));
    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-context").responseBodyAs(String.class).invoke());
  }

  @Test
  void protectedWorkstreamApiExercisesMyAccountNotificationCenterRuntimePath() throws Exception {
    seedNotification("notif-read-browser", "Digest blocked", "Personal digest is waiting for provider readiness.", NotificationCategory.PROVIDER_READINESS, NotificationPriority.WARNING, "corr-notification-read-seed");
    seedNotification("notif-dismiss-browser", "Review available", "A governance review is visible in this context.", NotificationCategory.POLICY_OR_GOVERNANCE, NotificationPriority.INFO, "corr-notification-dismiss-seed");
    seedNotification("notif-archive-browser", "Audit evidence ready", "Audit trace evidence is available.", NotificationCategory.AUDIT_OR_SECURITY, NotificationPriority.URGENT, "corr-notification-archive-seed");
    seedNotification("notif-snooze-browser", "Agent work paused", "Agent work needs later review.", NotificationCategory.ATTENTION_REQUIRED, NotificationPriority.INFO, "corr-notification-snooze-seed");

    var center = getSurface("surface-my-account-notification-center", ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin", "corr-my-notifications-browser-read");
    assertEquals("surface-my-account-notification-center", center.surfaceId());
    assertEquals("notification-center", center.surfaceType());
    assertEquals("my_account.notification_center.v1", center.data().get("surfaceContract"));
    assertEquals("in_app", center.data().get("channel"));
    assertTrue(center.toString().contains("notif-read-browser"));
    assertTrue(center.toString().contains("notification.mark_read"));
    assertTrue(center.toString().contains("notification.dismiss"));
    assertTrue(center.toString().contains("notification.archive"));
    assertTrue(center.toString().contains("notification.snooze"));
    assertTrue(center.toString().contains("notification.update_preferences"));
    assertTrue(center.toString().contains("traceRefs"));
    assertTrue(center.toString().contains("corr-my-notifications-browser-read"));
    assertNotificationCenterBrowserSafe(center);

    var markedRead = runNotificationAction("action-notification-mark-read", "notif-read-browser", Map.of("notificationId", "notif-read-browser"), "corr-my-notifications-browser-mark-read");
    assertEquals("full", markedRead.status());
    assertEquals("surface-my-account-notification-center", markedRead.resultSurface().surfaceId());
    assertTrue(markedRead.message().contains("source attention/task/event state unchanged"));
    assertFalse(markedRead.resultSurface().toString().contains("notif-read-browser"), "Read notifications are hidden unless preferences include read items.");
    assertNotificationCenterBrowserSafe(markedRead.resultSurface());

    var markReadAgain = runNotificationAction("action-notification-mark-read", "notif-read-browser", Map.of("notificationId", "notif-read-browser"), "corr-my-notifications-browser-mark-read-again");
    assertEquals("full", markReadAgain.status(), "Repeated lifecycle operation remains authorized and no source state is resolved.");
    assertFalse(markReadAgain.resultSurface().toString().contains("notif-read-browser"));
    assertNotificationCenterBrowserSafe(markReadAgain.resultSurface());

    var dismissed = runNotificationAction("action-notification-dismiss", "notif-dismiss-browser", Map.of("notificationId", "notif-dismiss-browser"), "corr-my-notifications-browser-dismiss");
    assertEquals("full", dismissed.status());
    assertTrue(dismissed.message().contains("source state unchanged"));
    assertFalse(dismissed.resultSurface().toString().contains("notif-dismiss-browser"));
    assertNotificationCenterBrowserSafe(dismissed.resultSurface());

    var archived = runNotificationAction("action-notification-archive", "notif-archive-browser", Map.of("notificationId", "notif-archive-browser"), "corr-my-notifications-browser-archive");
    assertEquals("full", archived.status());
    assertTrue(archived.message().contains("source state unchanged"));
    assertFalse(archived.resultSurface().toString().contains("notif-archive-browser"));
    assertNotificationCenterBrowserSafe(archived.resultSurface());

    var snoozed = runNotificationAction("action-notification-snooze", "notif-snooze-browser", Map.of("notificationId", "notif-snooze-browser"), "corr-my-notifications-browser-snooze");
    assertEquals("full", snoozed.status());
    assertTrue(snoozed.message().contains("source state unchanged"));
    assertFalse(snoozed.resultSurface().toString().contains("notif-snooze-browser"), "Future-snoozed notifications are hidden from active center until due.");
    assertNotificationCenterBrowserSafe(snoozed.resultSurface());

    var preferences = runNotificationAction("action-notification-update-preferences", null, Map.of("enabled", false, "includeReadInCenter", true), "corr-my-notifications-browser-preferences");
    assertEquals("accepted", preferences.status());
    assertEquals("surface-my-account-notification-center", preferences.resultSurface().surfaceId());
    assertTrue(preferences.message().contains("email delivery remains a separate governed channel"));
    assertTrue(preferences.resultSurface().toString().contains("preferencesSummary"));
    assertNotificationCenterBrowserSafe(preferences.resultSurface());

    var memberDenied = new CapabilityActionRequest(
        "action-notification-mark-read",
        "action-notification-mark-read",
        "notification.mark_read",
        "notification.mark_read",
        Map.of("notificationId", "notif-read-browser"),
        null,
        MEMBER_CONTEXT_ID,
        "surface-my-account-notification-center",
        "corr-my-notifications-browser-member-denied");
    var memberDeniedResult = runAction(memberDenied, MEMBER_CONTEXT_ID, "member@example.test", "Member User");
    assertEquals("denied", memberDeniedResult.status());
    assertNotNull(memberDeniedResult.resultSurface());
    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-account-notification-center").responseBodyAs(String.class).invoke());
  }

  @Test
  void protectedWorkstreamApiExercisesMyAccountPersonalAttentionDigestProgressRuntimePath() throws Exception {
    var notStarted = getSurface("surface-my-account-personal-attention-digest-progress", ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin", "corr-my-digest-progress-browser-not-started");
    assertEquals("surface-my-account-personal-attention-digest-progress", notStarted.surfaceId());
    assertEquals("workflow-status", notStarted.surfaceType());
    assertEquals("my_account.personal_attention_digest.progress.v1", notStarted.data().get("surfaceContract"));
    assertEquals("not_started", notStarted.data().get("status"));
    assertTrue(notStarted.toString().contains("action-start-my-account-personal-attention-digest"));
    assertTrue(notStarted.toString().contains("noDirectMutation"));
    assertTrue(notStarted.toString().contains("trace-my-account-personal-attention-digest-not-started"));
    assertPersonalAttentionDigestProgressBrowserSafe(notStarted);

    var started = runAction(new CapabilityActionRequest(
        "action-start-my-account-personal-attention-digest",
        "action-start-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.start",
        "my_account.personal_attention_digest.start",
        Map.of("evidenceScope", "attention"),
        "idem-my-digest-progress-browser-start",
        ADMIN_CONTEXT_ID,
        notStarted.surfaceId(),
        "corr-my-digest-progress-browser-start"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("blocked_provider_or_runtime", started.status());
    assertEquals("surface-my-account-personal-attention-digest-blocked", started.resultSurface().surfaceId());
    assertEquals("my_account.personal_attention_digest.blocked.v1", started.resultSurface().data().get("surfaceContract"));
    assertEquals(true, started.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, started.resultSurface().data().get("noDirectMutation"));
    assertTrue(started.resultSurface().toString().contains("blocked_provider_or_runtime"));
    assertTrue(started.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-personal-attention-digest")));
    assertPersonalAttentionDigestProgressBrowserSafe(started.resultSurface());
    var digestTaskId = String.valueOf(started.resultSurface().data().get("digestTaskId"));
    assertFalse(digestTaskId.isBlank());

    var readBlocked = runAction(new CapabilityActionRequest(
        "action-read-my-account-personal-attention-digest",
        "action-read-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.read",
        "my_account.personal_attention_digest.read",
        Map.of("digestTaskId", digestTaskId),
        null,
        ADMIN_CONTEXT_ID,
        started.resultSurface().surfaceId(),
        "corr-my-digest-progress-browser-read-blocked"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", readBlocked.status());
    assertEquals("surface-my-account-personal-attention-digest-blocked", readBlocked.resultSurface().surfaceId());
    assertTrue(readBlocked.resultSurface().toString().contains("noFakeSuccess"));
    assertPersonalAttentionDigestProgressBrowserSafe(readBlocked.resultSurface());

    var cancelled = runAction(new CapabilityActionRequest(
        "action-cancel-my-account-personal-attention-digest",
        "action-cancel-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.cancel",
        "my_account.personal_attention_digest.cancel",
        Map.of("digestTaskId", digestTaskId, "reason", "browser smoke cancellation"),
        null,
        ADMIN_CONTEXT_ID,
        started.resultSurface().surfaceId(),
        "corr-my-digest-progress-browser-cancel"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", cancelled.status());
    assertEquals("surface-my-account-personal-attention-digest-progress", cancelled.resultSurface().surfaceId());
    assertEquals("my_account.personal_attention_digest.progress.v1", cancelled.resultSurface().data().get("surfaceContract"));
    assertEquals("cancelled", cancelled.resultSurface().data().get("status"));
    assertTrue(cancelled.message().contains("source attention unchanged"));
    assertTrue(cancelled.resultSurface().toString().contains("browser smoke cancellation"));
    assertPersonalAttentionDigestProgressBrowserSafe(cancelled.resultSurface());

    var cancelAgain = runAction(new CapabilityActionRequest(
        "action-cancel-my-account-personal-attention-digest",
        "action-cancel-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.cancel",
        "my_account.personal_attention_digest.cancel",
        Map.of("digestTaskId", digestTaskId, "reason", "repeat cancellation"),
        null,
        ADMIN_CONTEXT_ID,
        cancelled.resultSurface().surfaceId(),
        "corr-my-digest-progress-browser-cancel-again"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", cancelAgain.status());
    assertEquals("cancelled", cancelAgain.resultSurface().data().get("status"));
    assertFalse(cancelAgain.resultSurface().toString().contains("repeat cancellation"), "Terminal repeated cancel must be idempotent and preserve the original traceable state.");
    assertPersonalAttentionDigestProgressBrowserSafe(cancelAgain.resultSurface());

    var memberDeniedRead = new CapabilityActionRequest(
        "action-read-my-account-personal-attention-digest",
        "action-read-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.read",
        "my_account.personal_attention_digest.read",
        Map.of("digestTaskId", digestTaskId),
        null,
        MEMBER_CONTEXT_ID,
        "surface-my-account-personal-attention-digest-progress",
        "corr-my-digest-progress-browser-member-denied");
    var memberDeniedReadResult = runAction(memberDeniedRead, MEMBER_CONTEXT_ID, "member@example.test", "Member User");
    assertEquals("denied", memberDeniedReadResult.status());
    assertNotNull(memberDeniedReadResult.resultSurface());
    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-account-personal-attention-digest-progress").responseBodyAs(String.class).invoke());
  }

  @Test
  void protectedWorkstreamApiExercisesMyAccountPersonalAttentionDigestBlockedRuntimePath() throws Exception {
    var directBlocked = getSurface("surface-my-account-personal-attention-digest-blocked", ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin", "corr-my-digest-blocked-browser-direct");
    assertEquals("surface-my-account-personal-attention-digest-blocked", directBlocked.surfaceId());
    assertEquals("system_message", directBlocked.surfaceType());
    assertEquals("my_account.personal_attention_digest.blocked.v1", directBlocked.data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", directBlocked.data().get("status"));
    assertEquals("provider_runtime_readiness", directBlocked.data().get("blockerCategory"));
    assertEquals(true, directBlocked.data().get("noFakeSuccess"));
    assertEquals(true, directBlocked.data().get("noDirectMutation"));
    assertTrue(directBlocked.toString().contains("retry_requires_admin_readiness"));
    assertTrue(directBlocked.toString().contains("recoverySteps"));
    assertTrue(directBlocked.toString().contains("providerReadiness"));
    assertTrue(directBlocked.toString().contains("runtimeReadiness"));
    assertTrue(directBlocked.toString().contains("No authorized digest task is selected."));
    assertTrue(directBlocked.toString().contains("corr-my-digest-blocked-browser-direct"));
    assertPersonalAttentionDigestBlockedBrowserSafe(directBlocked);

    var started = runAction(new CapabilityActionRequest(
        "action-start-my-account-personal-attention-digest",
        "action-start-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.start",
        "my_account.personal_attention_digest.start",
        Map.of("evidenceScope", "attention"),
        "idem-my-digest-blocked-browser-start",
        ADMIN_CONTEXT_ID,
        directBlocked.surfaceId(),
        "corr-my-digest-blocked-browser-start"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("blocked_provider_or_runtime", started.status());
    assertEquals("surface-my-account-personal-attention-digest-blocked", started.resultSurface().surfaceId());
    assertEquals("my_account.personal_attention_digest.blocked.v1", started.resultSurface().data().get("surfaceContract"));
    assertEquals(true, started.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, started.resultSurface().data().get("noDirectMutation"));
    assertTrue(started.resultSurface().toString().contains("visibleAttentionCount"));
    assertTrue(started.resultSurface().toString().contains("noSourceAttentionMutated"));
    assertTrue(started.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-personal-attention-digest")));
    assertPersonalAttentionDigestBlockedBrowserSafe(started.resultSurface());
    var digestTaskId = String.valueOf(started.resultSurface().data().get("digestTaskId"));
    assertFalse(digestTaskId.isBlank());

    var readBlocked = runAction(new CapabilityActionRequest(
        "action-read-my-account-personal-attention-digest",
        "action-read-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.read",
        "my_account.personal_attention_digest.read",
        Map.of("digestTaskId", digestTaskId),
        null,
        ADMIN_CONTEXT_ID,
        started.resultSurface().surfaceId(),
        "corr-my-digest-blocked-browser-read"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", readBlocked.status());
    assertEquals("surface-my-account-personal-attention-digest-blocked", readBlocked.resultSurface().surfaceId());
    assertTrue(readBlocked.resultSurface().toString().contains("action-read-my-account-personal-attention-digest"));
    assertTrue(readBlocked.resultSurface().toString().contains("action-show-my-account-dashboard"));
    assertPersonalAttentionDigestBlockedBrowserSafe(readBlocked.resultSurface());

    var memberDeniedRead = new CapabilityActionRequest(
        "action-read-my-account-personal-attention-digest",
        "action-read-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.read",
        "my_account.personal_attention_digest.read",
        Map.of("digestTaskId", digestTaskId),
        null,
        MEMBER_CONTEXT_ID,
        "surface-my-account-personal-attention-digest-blocked",
        "corr-my-digest-blocked-browser-member-denied");
    var memberDeniedReadResult = runAction(memberDeniedRead, MEMBER_CONTEXT_ID, "member@example.test", "Member User");
    assertEquals("denied", memberDeniedReadResult.status());
    assertNotNull(memberDeniedReadResult.resultSurface());
    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-account-personal-attention-digest-blocked").responseBodyAs(String.class).invoke());
  }

  @Test
  void protectedWorkstreamApiExercisesMyAccountPersonalAttentionDigestResultRuntimePath() throws Exception {
    var emptyResult = getSurface("surface-my-account-personal-attention-digest-result", ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin", "corr-my-digest-result-browser-empty");
    assertEquals("surface-my-account-personal-attention-digest-result", emptyResult.surfaceId());
    assertEquals("outcome-panel", emptyResult.surfaceType());
    assertEquals("my_account.personal_attention_digest.result.v1", emptyResult.data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", emptyResult.data().get("decisionState"));
    assertTrue(emptyResult.toString().contains("no completed backend task selected"));
    assertPersonalAttentionDigestResultBrowserSafe(emptyResult);

    var reviewTaskId = seedCompletedDigestTask("digest-result-review", MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, null, null);
    var result = runAction(new CapabilityActionRequest(
        "action-read-my-account-personal-attention-digest",
        "action-read-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.read",
        "my_account.personal_attention_digest.read",
        Map.of("digestTaskId", reviewTaskId),
        null,
        ADMIN_CONTEXT_ID,
        emptyResult.surfaceId(),
        "corr-my-digest-result-browser-read"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", result.status());
    assertEquals("surface-my-account-personal-attention-digest-result", result.resultSurface().surfaceId());
    assertEquals("outcome-panel", result.resultSurface().surfaceType());
    assertEquals("my_account.personal_attention_digest.result.v1", result.resultSurface().data().get("surfaceContract"));
    assertEquals("completed_review_required", result.resultSurface().data().get("decisionState"));
    assertTrue(result.resultSurface().toString().contains("recommendations"));
    assertTrue(result.resultSurface().toString().contains("materialEvents"));
    assertTrue(result.resultSurface().toString().contains("pendingDecisions"));
    assertTrue(result.resultSurface().toString().contains("omissions"));
    assertTrue(result.resultSurface().toString().contains("advisoryOnly"));
    assertTrue(result.resultSurface().toString().contains("noDirectMutation"));
    assertTrue(result.resultSurface().toString().contains("action-accept-my-account-personal-attention-digest"));
    assertTrue(result.resultSurface().toString().contains("action-reject-my-account-personal-attention-digest"));
    assertTrue(result.resultSurface().toString().contains("corr-my-digest-result-browser-read"));
    assertTrue(result.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-personal-attention-digest")));
    assertPersonalAttentionDigestResultBrowserSafe(result.resultSurface());

    var accepted = runAction(new CapabilityActionRequest(
        "action-accept-my-account-personal-attention-digest",
        "action-accept-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.accept_result",
        "my_account.personal_attention_digest.accept_result",
        Map.of("digestTaskId", reviewTaskId, "reason", "browser smoke accepted advisory review"),
        null,
        ADMIN_CONTEXT_ID,
        result.resultSurface().surfaceId(),
        "corr-my-digest-result-browser-accept"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", accepted.status());
    assertEquals("surface-my-account-personal-attention-digest-result", accepted.resultSurface().surfaceId());
    assertEquals("accepted", accepted.resultSurface().data().get("decisionState"));
    assertTrue(accepted.message().contains("source attention lifecycle unchanged"));
    assertTrue(accepted.resultSurface().toString().contains("browser smoke accepted advisory review"));
    assertTrue(accepted.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-personal-attention-digest-accepted")));
    assertPersonalAttentionDigestResultBrowserSafe(accepted.resultSurface());

    var acceptAgain = runAction(new CapabilityActionRequest(
        "action-accept-my-account-personal-attention-digest",
        "action-accept-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.accept_result",
        "my_account.personal_attention_digest.accept_result",
        Map.of("digestTaskId", reviewTaskId, "reason", "browser smoke duplicate accept"),
        null,
        ADMIN_CONTEXT_ID,
        accepted.resultSurface().surfaceId(),
        "corr-my-digest-result-browser-accept-again"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", acceptAgain.status());
    assertEquals("accepted", acceptAgain.resultSurface().data().get("decisionState"));
    assertFalse(acceptAgain.resultSurface().toString().contains("browser smoke duplicate accept"), "Repeated accept must be idempotent and preserve the original advisory decision reason.");
    assertPersonalAttentionDigestResultBrowserSafe(acceptAgain.resultSurface());

    var conflictingReject = new CapabilityActionRequest(
        "action-reject-my-account-personal-attention-digest",
        "action-reject-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.reject_result",
        "my_account.personal_attention_digest.reject_result",
        Map.of("digestTaskId", reviewTaskId, "reason", "conflicting terminal decision"),
        null,
        ADMIN_CONTEXT_ID,
        accepted.resultSurface().surfaceId(),
        "corr-my-digest-result-browser-conflict");
    var conflictingRejectResult = runAction(conflictingReject, ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("denied", conflictingRejectResult.status());
    assertNotNull(conflictingRejectResult.resultSurface());

    var rejectTaskId = seedCompletedDigestTask("digest-result-reject", MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, null, null);
    var missingReasonReject = new CapabilityActionRequest(
        "action-reject-my-account-personal-attention-digest",
        "action-reject-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.reject_result",
        "my_account.personal_attention_digest.reject_result",
        Map.of("digestTaskId", rejectTaskId, "reason", ""),
        null,
        ADMIN_CONTEXT_ID,
        result.resultSurface().surfaceId(),
        "corr-my-digest-result-browser-reject-missing-reason");
    var missingReasonRejectResult = runAction(missingReasonReject, ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("validation-error", missingReasonRejectResult.status());
    assertNotNull(missingReasonRejectResult.resultSurface());

    var rejected = runAction(new CapabilityActionRequest(
        "action-reject-my-account-personal-attention-digest",
        "action-reject-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.reject_result",
        "my_account.personal_attention_digest.reject_result",
        Map.of("digestTaskId", rejectTaskId, "reason", "browser smoke requested better evidence"),
        null,
        ADMIN_CONTEXT_ID,
        result.resultSurface().surfaceId(),
        "corr-my-digest-result-browser-reject"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", rejected.status());
    assertEquals("surface-my-account-personal-attention-digest-result", rejected.resultSurface().surfaceId());
    assertEquals("rejected", rejected.resultSurface().data().get("decisionState"));
    assertTrue(rejected.message().contains("source attention lifecycle unchanged"));
    assertTrue(rejected.resultSurface().toString().contains("browser smoke requested better evidence"));
    assertPersonalAttentionDigestResultBrowserSafe(rejected.resultSurface());

    var memberDeniedRead = new CapabilityActionRequest(
        "action-read-my-account-personal-attention-digest",
        "action-read-my-account-personal-attention-digest",
        "my_account.personal_attention_digest.read",
        "my_account.personal_attention_digest.read",
        Map.of("digestTaskId", reviewTaskId),
        null,
        MEMBER_CONTEXT_ID,
        "surface-my-account-personal-attention-digest-result",
        "corr-my-digest-result-browser-member-denied");
    var memberDeniedReadResult = runAction(memberDeniedRead, MEMBER_CONTEXT_ID, "member@example.test", "Member User");
    assertEquals("denied", memberDeniedReadResult.status());
    assertNotNull(memberDeniedReadResult.resultSurface());
    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-account-personal-attention-digest-result").responseBodyAs(String.class).invoke());
  }

  @Test
  void protectedWorkstreamApiExercisesMySettingsRuntimePath() throws Exception {
    var settings = getSurface("surface-my-settings", ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin", "corr-my-settings-browser-read");
    assertEquals("surface-my-settings", settings.surfaceId());
    assertEquals("detail-edit", settings.surfaceType());
    assertEquals("my_account.preferences.self_service.v1", settings.data().get("surfaceContract"));
    assertTrue(settings.toString().contains("settingsSummary"));
    assertTrue(settings.toString().contains("preferredThemeId"));
    assertTrue(settings.toString().contains("availableThemes"));
    assertTrue(settings.toString().contains("locale"));
    assertTrue(settings.toString().contains("timeZone"));
    assertTrue(settings.toString().contains("notification.list_my_account_center"));
    assertTrue(settings.toString().contains("traceRefs"));
    assertTrue(settings.toString().contains("corr-my-settings-browser-read"));
    assertFalse(settings.toString().contains("system mode"));
    assertSettingsSurfaceBrowserSafe(settings);

    var update = runAction(new CapabilityActionRequest(
        "action-update-my-settings",
        "action-update-my-settings",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        Map.of("preferredThemeId", "midnight-dark", "locale", "en-GB", "timeZone", "Europe/London"),
        "idem-my-settings-browser-update",
        ADMIN_CONTEXT_ID,
        settings.surfaceId(),
        "corr-my-settings-browser-update"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", update.status());
    assertEquals("surface-my-settings", update.resultSurface().surfaceId());
    assertTrue(update.resultSurface().toString().contains("midnight-dark"));
    assertTrue(update.resultSurface().toString().contains("en-GB"));
    assertTrue(update.resultSurface().toString().contains("Europe/London"));
    assertTrue(update.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-profile-settings")));
    assertSettingsSurfaceBrowserSafe(update.resultSurface());

    var openNotifications = runAction(new CapabilityActionRequest(
        "action-show-my-account-notification-center",
        "action-show-my-account-notification-center",
        "notification.list_my_account_center",
        "notification.list_my_account_center",
        Map.of(),
        null,
        ADMIN_CONTEXT_ID,
        update.resultSurface().surfaceId(),
        "corr-my-settings-browser-open-notifications"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("accepted", openNotifications.status());
    assertEquals("surface-my-account-notification-center", openNotifications.resultSurface().surfaceId());
    assertEquals("my_account.notification_center.v1", openNotifications.resultSurface().data().get("surfaceContract"));
    assertBrowserSafe(openNotifications.resultSurface());

    var noOp = runAction(new CapabilityActionRequest(
        "action-update-my-settings",
        "action-update-my-settings",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        Map.of("preferredThemeId", "midnight-dark", "locale", "en-GB", "timeZone", "Europe/London"),
        "idem-my-settings-browser-noop",
        ADMIN_CONTEXT_ID,
        settings.surfaceId(),
        "corr-my-settings-browser-noop"), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("no-op", noOp.status());
    assertEquals("surface-my-settings", noOp.resultSurface().surfaceId());
    assertTrue(noOp.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-profile-settings")));
    assertSettingsSurfaceBrowserSafe(noOp.resultSurface());

    var postUpdateBootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-settings-browser-bootstrap-after-update")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(postUpdateBootstrap.status().isSuccess());
    assertEquals("midnight-dark", postUpdateBootstrap.body().me().settings().preferredThemeId());
    assertEquals("en-GB", postUpdateBootstrap.body().me().settings().locale());
    assertEquals("Europe/London", postUpdateBootstrap.body().me().settings().timeZone());
    assertBrowserSafe(postUpdateBootstrap.body());

    var unsupportedProviderMutation = new CapabilityActionRequest(
        "action-update-my-settings",
        "action-update-my-settings",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        Map.of("providerSecret", "sk-settings-browser-secret"),
        "idem-my-settings-browser-unsupported",
        ADMIN_CONTEXT_ID,
        settings.surfaceId(),
        "corr-my-settings-browser-unsupported");
    var unsupportedSettings = runAction(unsupportedProviderMutation, ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("validation-error", unsupportedSettings.status());
    assertEquals("surface-my-settings", unsupportedSettings.resultSurface().surfaceId());

    var invalidTimezone = new CapabilityActionRequest(
        "action-update-my-settings",
        "action-update-my-settings",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        Map.of("timeZone", "Hidden/Provider"),
        "idem-my-settings-browser-invalid",
        ADMIN_CONTEXT_ID,
        settings.surfaceId(),
        "corr-my-settings-browser-invalid");
    var invalidTimezoneResult = runAction(invalidTimezone, ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
    assertEquals("validation-error", invalidTimezoneResult.status());
    assertEquals("surface-my-settings", invalidTimezoneResult.resultSurface().surfaceId());

    var afterDenied = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-settings-browser-after-denied")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(afterDenied.status().isSuccess());
    assertEquals("Europe/London", afterDenied.body().me().settings().timeZone(), "Invalid settings values must be denied before mutation.");
    assertBrowserSafe(afterDenied.body());

    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-settings").responseBodyAs(String.class).invoke());
  }

  private SurfaceEnvelope getSurface(String surfaceId, String selectedContextId, String email, String name, String correlationId) throws Exception {
    var response = httpClient
        .GET("/api/workstream/surfaces/" + surfaceId)
        .addHeader("Authorization", "Bearer " + bearerToken(subjectFor(email), email, name))
        .addHeader("X-Selected-Context-Id", selectedContextId)
        .addHeader("X-Correlation-Id", correlationId)
        .responseBodyAs(SurfaceEnvelope.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    return response.body();
  }

  private CapabilityActionResult runNotificationAction(String actionId, String notificationId, Map<String, Object> input, String correlationId) throws Exception {
    var capabilityId = switch (actionId) {
      case "action-notification-mark-read" -> "notification.mark_read";
      case "action-notification-dismiss" -> "notification.dismiss";
      case "action-notification-archive" -> "notification.archive";
      case "action-notification-snooze" -> "notification.snooze";
      case "action-notification-update-preferences" -> "notification.update_preferences";
      default -> actionId;
    };
    return runAction(new CapabilityActionRequest(
        actionId,
        actionId,
        capabilityId,
        capabilityId,
        input,
        notificationId == null ? null : "idem-" + actionId + "-" + notificationId,
        ADMIN_CONTEXT_ID,
        "surface-my-account-notification-center",
        correlationId), ADMIN_CONTEXT_ID, "admin@example.test", "Tenant Admin");
  }

  private CapabilityActionResult runAction(CapabilityActionRequest request, String selectedContextId, String email, String name) throws Exception {
    var response = httpClient
        .POST("/api/workstream/actions")
        .addHeader("Authorization", "Bearer " + bearerToken(subjectFor(email), email, name))
        .addHeader("X-Selected-Context-Id", selectedContextId)
        .addHeader("X-Correlation-Id", request.correlationId())
        .withRequestBody(request)
        .responseBodyAs(CapabilityActionResult.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    return response.body();
  }

  private String seedCompletedDigestTask(String suffix, MyAccountPersonalAttentionDigestTask.Status status, String decision, String decisionReason) {
    var now = Instant.parse("2026-05-25T10:15:30Z");
    var task = new MyAccountPersonalAttentionDigestTask(
        "my-account-" + suffix,
        "akka-task-my-account-" + suffix,
        TENANT_ID,
        null,
        ADMIN_CONTEXT_ID,
        "admin@example.test",
        ADMIN_CONTEXT_ID,
        "idem-my-account-" + suffix,
        2,
        status,
        100,
        "Model-backed redacted personal attention digest ready for advisory review; source attention remains authoritative and unchanged.",
        null,
        decision,
        decisionReason,
        List.of("capability:attention.list_my_account_items", "readSkill:my-account-personal-attention-digest", "readReferenceDoc:my-account-personal-attention-digest", "attention_item:authorized-browser-evidence"),
        List.of("personal_attention_digest_section:authorized-browser-evidence"),
        List.of("trace-my-account-personal-attention-digest-result-" + suffix),
        now,
        now);
    return new AkkaMyAccountPersonalAttentionDigestTaskRepository(componentClient).save(task).digestTaskId();
  }

  private void seedNotification(String notificationId, String title, String summary, NotificationCategory category, NotificationPriority priority, String correlationId) {
    var now = Instant.parse("2026-05-24T10:15:30Z");
    new AkkaNotificationRepository(componentClient).save(new NotificationItem(
        notificationId,
        TENANT_ID,
        null,
        "admin@example.test",
        ADMIN_CONTEXT_ID,
        NotificationChannel.IN_APP,
        title,
        summary,
        category,
        priority,
        NotificationLifecycleStatus.UNREAD,
        List.of(new NotificationSourceRef("workstream", notificationId + "-source", "Authorized source", "my_account.view_summary", "trace-" + notificationId, correlationId)),
        new NotificationSurfaceRef("my-account-agent", "surface-my-account-dashboard", "dashboard", notificationId + "-source", "browser-tool-open-source", "my_account.view_summary"),
        "my_account.view_summary",
        "my-account",
        "workstream-event",
        NotificationRedactionLevel.FULL,
        "dedupe-" + notificationId,
        correlationId,
        List.of("trace-" + notificationId),
        now,
        now,
        now,
        null,
        null,
        null,
        null,
        null));
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, List<FoundationRole> roles) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership(membershipId, email, ScopeType.TENANT, TENANT_ID, null, roles, MembershipStatus.ACTIVE, false, null));
  }

  private String subjectFor(String email) {
    return "workos-" + email.substring(0, email.indexOf('@'));
  }

  private String bearerToken(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }

  private static void assertOpenDeniedBrowserSafe(SurfaceEnvelope payload) {
    var text = String.valueOf(payload);
    assertTrue(text.contains("my_account.open_denied.v1"));
    assertTrue(text.contains("noEnumeration"));
    assertTrue(text.contains("recoveryStepDetails"));
    assertTrue(text.contains("availableActions"));
    assertTrue(text.contains("traceRefs"));
    assertFalse(text.contains("agent_admin.list_definitions"));
    assertFalse(text.contains("Agent Admin"));
    assertFalse(text.contains("missingRoles"));
    assertFalse(text.contains("missingCapabilities"));
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-member"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }

  private static void assertProfileSurfaceBrowserSafe(SurfaceEnvelope payload) {
    var text = String.valueOf(payload);
    assertTrue(text.contains("omittedFieldKeys"));
    assertTrue(text.contains("providerSecret"));
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }

  private static void assertContextSurfaceBrowserSafe(SurfaceEnvelope payload) {
    var text = String.valueOf(payload);
    assertTrue(text.contains("omittedFieldKeys"));
    assertTrue(text.contains("providerSecret"));
    assertTrue(text.contains("hiddenContexts"));
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("hidden-cross-tenant-name"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }

  private static void assertNotificationCenterBrowserSafe(SurfaceEnvelope payload) {
    var text = String.valueOf(payload);
    assertTrue(text.contains("my_account.notification_center.v1"));
    assertTrue(text.contains("in_app"));
    assertFalse(text.contains("email channel controls"));
    assertFalse(text.contains("deliveryAttempt"));
    assertFalse(text.contains("outboxId"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
    assertFalse(text.contains("hiddenCategories=["));
    assertFalse(text.contains("slack"));
    assertFalse(text.contains("webhook destination"));
  }

  private static void assertPersonalAttentionDigestBlockedBrowserSafe(SurfaceEnvelope payload) {
    var text = String.valueOf(payload);
    assertTrue(text.contains("my_account.personal_attention_digest.blocked.v1"));
    assertTrue(text.contains("blocked_provider_or_runtime"));
    assertTrue(text.contains("noFakeSuccess"));
    assertTrue(text.contains("noDirectMutation"));
    assertTrue(text.contains("providerReadiness"));
    assertTrue(text.contains("runtimeReadiness"));
    assertTrue(text.contains("recoverySteps"));
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
    assertFalse(text.contains("fixture success payload"));
    assertFalse(text.contains("fake digest result"));
    assertFalse(text.contains("hidden workstream name"));
    assertFalse(text.contains("raw tool payload"));
  }

  private static void assertPersonalAttentionDigestProgressBrowserSafe(SurfaceEnvelope payload) {
    var text = String.valueOf(payload);
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
    assertFalse(text.contains("fixture success payload"));
    assertFalse(text.contains("fake digest result"));
    assertFalse(text.contains("hidden workstream name"));
    assertFalse(text.contains("raw tool payload"));
  }

  private static void assertPersonalAttentionDigestResultBrowserSafe(SurfaceEnvelope payload) {
    var text = String.valueOf(payload);
    assertTrue(text.contains("my_account.personal_attention_digest.result.v1"));
    assertTrue(text.contains("advisoryOnly"));
    assertTrue(text.contains("noDirectMutation"));
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
    assertFalse(text.contains("fixture success payload"));
    assertFalse(text.contains("fake digest result"));
    assertFalse(text.contains("hidden workstream name"));
    assertFalse(text.contains("raw tool payload"));
    assertFalse(text.contains("source attention completed"));
  }

  private static void assertSettingsSurfaceBrowserSafe(SurfaceEnvelope payload) {
    var text = String.valueOf(payload);
    assertTrue(text.contains("omittedFieldKeys"));
    assertTrue(text.contains("providerSecret"));
    assertTrue(text.contains("hiddenCategories"));
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
    assertFalse(text.contains("arbitraryCss:"));
  }

  private static void assertBrowserSafe(Object payload) {
    var text = String.valueOf(payload);
    assertFalse(text.contains("invite-token"));
    assertFalse(text.contains("tokenHash"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("RESEND_API_KEY"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("workos-admin"));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }
}
