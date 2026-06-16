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
