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
import ai.first.domain.foundation.identity.Customer;
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

/** Scriptable Akka-hosted UI/API smoke for the Agent Admin command center. */
class AgentAdminBrowserWorkstreamSmokeTest extends TestKitSupport {
  private static final String TENANT_ID = "tenant-starter";
  private static final String CUSTOMER_ID = "customer-agent-smoke";
  private static final String ADMIN_CONTEXT_ID = "membership-agent-admin";
  private static final String MEMBER_CONTEXT_ID = "membership-agent-member";
  private static final String CUSTOMER_CONTEXT_ID = "membership-agent-customer";

  @BeforeEach
  void seedAgentAdminSmokeActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(TENANT_ID, "Starter Tenant", true));
    repository.saveCustomer(new Customer(TENANT_ID, CUSTOMER_ID, "Customer Agent Smoke", true));
    seedIdentity(repository, "admin@example.test", "Tenant Admin", ADMIN_CONTEXT_ID, ScopeType.TENANT, null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    seedIdentity(repository, "member@example.test", "Member User", MEMBER_CONTEXT_ID, ScopeType.TENANT, null, List.of(FoundationRole.TENANT_EMPLOYEE));
    seedIdentity(repository, "customer@example.test", "Customer Admin", CUSTOMER_CONTEXT_ID, ScopeType.CUSTOMER, CUSTOMER_ID, List.of(FoundationRole.CUSTOMER_ADMIN));
  }

  @Test
  void hostedShellAndProtectedWorkstreamApiExerciseAgentAdminDashboardRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load from Akka static resources.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertBrowserSafe(shell.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-admin-dashboard")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin dashboard must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-agent-admin-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-agent-admin-refresh-dashboard",
            "action-agent-admin-refresh-dashboard",
            "agent_admin.list_definitions",
            "agent_admin.list_definitions",
            null,
            null,
            ADMIN_CONTEXT_ID,
            "surface-agent-admin-dashboard",
            "corr-agent-admin-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin dashboard action path must reject missing bearer tokens.");

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-agent-admin-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(ADMIN_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-admin-agent") && agent.availability().equals("visible")));
    assertBrowserSafe(bootstrap.body());

    var dashboard = getSurface("surface-agent-admin-dashboard", "corr-agent-admin-dashboard");
    assertEquals("surface-agent-admin-dashboard", dashboard.surfaceId());
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("agent_admin.dashboard.v1", dashboard.data().get("surfaceContract"));
    assertEquals("corr-agent-admin-dashboard", dashboard.correlationId());
    assertTrue(dashboard.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-admin-dashboard")));
    assertTrue(dashboard.toString().contains("surfaceSummary"));
    assertTrue(dashboard.toString().contains("scopeSummary"));
    assertTrue(dashboard.toString().contains("attentionSections"));
    assertTrue(dashboard.toString().contains("authorizedActions"));
    assertTrue(dashboard.toString().contains("providerModelStatus"));
    assertTrue(dashboard.toString().contains("blocked_provider_or_runtime"));
    assertTrue(dashboard.toString().contains("noFakeSuccess=true"));
    assertTrue(dashboard.toString().contains("trace-agent-admin-dashboard"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-catalog") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-catalog")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-prompt-risk-review") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-prompt-risk-review")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-model-refs") && action.resultSurface().updateSurfaceId().equals("surface-agent-model-refs")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));
    assertBrowserSafe(dashboard);

    var refreshed = runAction(new CapabilityActionRequest(
        "action-agent-admin-refresh-dashboard",
        "action-agent-admin-refresh-dashboard",
        "agent_admin.list_definitions",
        "agent_admin.list_definitions",
        null,
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-refresh"));
    assertEquals("accepted", refreshed.status());
    assertEquals("surface-agent-admin-dashboard", refreshed.resultSurface().surfaceId());
    assertEquals("corr-agent-admin-refresh", refreshed.correlationId());
    assertBrowserSafe(refreshed.resultSurface());

    var catalog = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-catalog",
        "action-agent-admin-open-catalog",
        "agent_admin.list_definitions",
        "agent_admin.list_definitions",
        null,
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-catalog"));
    assertEquals("accepted", catalog.status());
    assertEquals("surface-agent-admin-catalog", catalog.resultSurface().surfaceId());
    assertEquals("list-search", catalog.resultSurface().surfaceType());
    assertEquals("agent_admin.catalog.v1", catalog.resultSurface().data().get("surfaceContract"));
    assertTrue(catalog.resultSurface().toString().contains("action-open-agent-detail"));
    assertTrue(catalog.resultSurface().toString().contains("trace-agent-admin-catalog"));
    assertBrowserSafe(catalog.resultSurface());

    var modelRefs = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-model-refs",
        "action-agent-admin-open-model-refs",
        "agent_admin.get_model_ref",
        "agent_admin.get_model_ref",
        null,
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-model-refs"));
    assertEquals("accepted", modelRefs.status());
    assertEquals("surface-agent-model-refs", modelRefs.resultSurface().surfaceId());
    assertEquals("agent_admin.model_ref.v1", modelRefs.resultSurface().data().get("surfaceContract"));
    assertTrue(modelRefs.resultSurface().toString().contains("providerCredential=[REDACTED]"));
    assertTrue(modelRefs.resultSurface().toString().contains("secretVisibility=redacted"));
    assertTrue(modelRefs.resultSurface().toString().contains("traceLinks"));
    assertBrowserSafe(modelRefs.resultSurface());

    var promptRisk = runAction(new CapabilityActionRequest(
        "action-agentadmin-start-prompt-risk-review",
        "action-agentadmin-start-prompt-risk-review",
        "agent_admin.prompt_risk_review.start",
        "agent_admin.prompt_risk_review.start",
        Map.of("agentDefinitionId", "agent-admin-agent", "proposalId", "proposal-agent-admin-prompt-001"),
        "idem-agent-admin-prompt-risk-smoke",
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-prompt-risk"));
    assertEquals("blocked_provider_or_runtime", promptRisk.status());
    assertEquals("surface-agent-admin-prompt-risk-review", promptRisk.resultSurface().surfaceId());
    assertEquals("agent_admin.prompt_risk_review_task.v1", promptRisk.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", promptRisk.resultSurface().data().get("status"));
    assertTrue(promptRisk.resultSurface().toString().contains("activationBlockedUntilHumanDecision=true"));
    assertTrue(promptRisk.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-admin-prompt-risk")));
    assertBrowserSafe(promptRisk.resultSurface());

    var trace = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-trace",
        "action-agent-admin-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-admin-dashboard"),
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-trace"));
    assertEquals("accepted", trace.status());
    assertEquals("surface-agent-admin-trace", trace.resultSurface().surfaceId());
    assertEquals("audit-timeline", trace.resultSurface().surfaceType());
    assertTrue(trace.resultSurface().toString().contains("events"));
    assertTrue(trace.resultSurface().toString().contains("PromptAssemblyTrace"));
    assertTrue(trace.resultSurface().toString().contains("AgentWorkTrace"));
    assertBrowserSafe(trace.resultSurface());
  }

  @Test
  void protectedAgentAdminDashboardDeniesUnauthorizedAndCustomerScopedContextsSafely() {
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-admin-dashboard",
        "corr-agent-admin-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin dashboard state.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-admin-dashboard",
        "corr-agent-admin-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not imply Agent Admin authority or expose tenant governance counts.");
  }

  private SurfaceEnvelope getSurface(String surfaceId, String correlationId) throws Exception {
    return getSurfaceAs(surfaceId, correlationId, "workos-admin", "admin@example.test", "Tenant Admin", ADMIN_CONTEXT_ID);
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
    var response = httpClient
        .POST("/api/workstream/actions")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", request.correlationId())
        .withRequestBody(request)
        .responseBodyAs(CapabilityActionResult.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    return response.body();
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, ScopeType scopeType, String customerId, List<FoundationRole> roles) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership(membershipId, email, scopeType, TENANT_ID, customerId, roles, MembershipStatus.ACTIVE, false, null));
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
    assertFalse(text.contains("sk_live_"));
    assertFalse(text.contains("sk_test_"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }
}
