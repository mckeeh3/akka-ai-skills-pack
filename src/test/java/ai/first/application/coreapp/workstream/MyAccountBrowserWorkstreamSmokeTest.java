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

/** Scriptable hosted-UI/workstream smoke for the My Account dashboard surface runtime path. */
class MyAccountBrowserWorkstreamSmokeTest extends TestKitSupport {
  private static final String TENANT_ID = "tenant-starter";
  private static final String ADMIN_CONTEXT_ID = "membership-admin";
  private static final String MEMBER_CONTEXT_ID = "membership-member";

  @BeforeEach
  void seedMyAccountSmokeActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(TENANT_ID, "Starter Tenant", true));
    seedIdentity(repository, "admin@example.test", "Tenant Admin", ADMIN_CONTEXT_ID, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    seedIdentity(repository, "member@example.test", "Member User", MEMBER_CONTEXT_ID, List.of(FoundationRole.TENANT_EMPLOYEE));
  }

  @Test
  void hostedShellAndProtectedWorkstreamApiExerciseMyAccountDashboardRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load from Akka static resources.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertFalse(shell.body().contains("providerSecret"));
    assertFalse(shell.body().contains("Bearer "));

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-account-browser-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(ADMIN_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-my-account") && agent.availability().equals("visible")));
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
    assertEquals("not_found_or_redacted", denied.resultSurface().data().get("status"));
    assertFalse(denied.resultSurface().toString().contains("agent_admin.list_definitions"));
    assertTrue(denied.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-my-account-open")));
    assertBrowserSafe(denied.resultSurface());

    assertThrows(RuntimeException.class, () -> httpClient.GET("/api/workstream/surfaces/surface-my-account-dashboard").responseBodyAs(String.class).invoke());
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
    assertThrows(RuntimeException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-profile-browser-denied")
        .withRequestBody(unsupportedMutation)
        .responseBodyAs(String.class)
        .invoke());

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
    assertThrows(RuntimeException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-my-settings-browser-invalid")
        .withRequestBody(invalidTimezone)
        .responseBodyAs(String.class)
        .invoke());

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
