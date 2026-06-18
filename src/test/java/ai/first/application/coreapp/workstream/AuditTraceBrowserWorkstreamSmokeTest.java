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

/** Akka-hosted browser/API smoke coverage for the Audit/Trace dashboard runtime path. */
class AuditTraceBrowserWorkstreamSmokeTest extends TestKitSupport {
  private static final String TENANT_ID = "tenant-audit-smoke";
  private static final String CUSTOMER_ID = "customer-audit-smoke";
  private static final String AUDITOR_CONTEXT_ID = "membership-audit-auditor";
  private static final String MEMBER_CONTEXT_ID = "membership-audit-member";
  private static final String CUSTOMER_CONTEXT_ID = "membership-audit-customer";
  private static final String DISABLED_CONTEXT_ID = "membership-audit-disabled";

  @BeforeEach
  void seedAuditTraceSmokeActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(TENANT_ID, "Audit Smoke Tenant", true));
    repository.saveCustomer(new Customer(TENANT_ID, CUSTOMER_ID, "Audit Smoke Customer", true));
    seedIdentity(repository, "auditor@example.test", "Audit Reviewer", AUDITOR_CONTEXT_ID, AccountStatus.ACTIVE, ScopeType.TENANT, null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    seedIdentity(repository, "member-audit@example.test", "Member User", MEMBER_CONTEXT_ID, AccountStatus.ACTIVE, ScopeType.TENANT, null, List.of(FoundationRole.TENANT_EMPLOYEE));
    seedIdentity(repository, "customer-audit@example.test", "Customer Admin", CUSTOMER_CONTEXT_ID, AccountStatus.ACTIVE, ScopeType.CUSTOMER, CUSTOMER_ID, List.of(FoundationRole.CUSTOMER_ADMIN));
    seedIdentity(repository, "disabled-audit@example.test", "Disabled Auditor", DISABLED_CONTEXT_ID, AccountStatus.DISABLED, ScopeType.TENANT, null, List.of(FoundationRole.AUDITOR));
  }

  @Test
  @SuppressWarnings("unchecked")
  void hostedShellAndProtectedWorkstreamApiExerciseAuditTraceDashboardRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load from Akka static resources.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertBrowserSafe(shell.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-dashboard")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace dashboard must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-dashboard-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-audit-trace-dashboard",
            "action-audit-trace-dashboard",
            "audit.trace.dashboard.read",
            "audit.trace.dashboard.read",
            null,
            null,
            AUDITOR_CONTEXT_ID,
            "surface-audit-trace-dashboard",
            "corr-audit-dashboard-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace dashboard action path must reject missing bearer tokens.");

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-audit-auditor", "auditor@example.test", "Audit Reviewer"))
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-dashboard-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(AUDITOR_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-audit-trace") && agent.availability().equals("visible")));
    assertBrowserSafe(bootstrap.body());

    var dashboard = getSurface("surface-audit-trace-dashboard", "corr-audit-dashboard-direct");
    assertEquals("surface-audit-trace-dashboard", dashboard.surfaceId());
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("audit.trace.dashboard.v1", dashboard.data().get("surfaceContract"));
    assertEquals("corr-audit-dashboard-direct", dashboard.correlationId());
    assertTrue(dashboard.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-audit-dashboard")));
    assertTrue(dashboard.toString().contains("needs-attention"));
    assertTrue(dashboard.toString().contains("things-i-can-do"));
    assertTrue(dashboard.toString().contains("audit.trace.dashboard.read"));
    assertTrue(dashboard.toString().contains("selectedContextId=" + AUDITOR_CONTEXT_ID));
    assertTrue(dashboard.toString().contains("fails closed until provider/runtime/tool-boundary configuration is present"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-search") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-search")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-failure-evidence") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-failure-evidence")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-task-start") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-summary-progress")));
    assertBrowserSafe(dashboard);

    var refreshed = runAction(new CapabilityActionRequest(
        "action-audit-trace-dashboard",
        "action-audit-trace-dashboard",
        "audit.trace.dashboard.read",
        "audit.trace.dashboard.read",
        null,
        null,
        AUDITOR_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-audit-dashboard-refresh"));
    assertEquals("accepted", refreshed.status());
    assertEquals("surface-audit-trace-dashboard", refreshed.resultSurface().surfaceId());
    assertEquals("corr-audit-dashboard-refresh", refreshed.correlationId());
    assertBrowserSafe(refreshed.resultSurface());

    var search = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "AUTH_CONTEXT", "pageSize", 10),
        null,
        AUDITOR_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-audit-dashboard-search"));
    assertEquals("accepted", search.status());
    assertEquals("surface-audit-trace-search", search.resultSurface().surfaceId());
    assertEquals("audit.trace.search.v1", search.resultSurface().data().get("surfaceContract"));
    assertTrue(search.resultSurface().toString().contains("AUTH_CONTEXT_RESOLVE"));
    assertBrowserSafe(search.resultSurface());

    var rows = (List<Map<String, Object>>) search.resultSurface().data().get("rows");
    assertFalse(rows.isEmpty(), "Audit/Trace search should expose at least the protected AuthContext trace row.");
    var traceId = String.valueOf(rows.get(0).get("traceId"));
    var detail = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", traceId),
        null,
        AUDITOR_CONTEXT_ID,
        search.resultSurface().surfaceId(),
        "corr-audit-dashboard-detail"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-audit-trace-detail", detail.resultSurface().surfaceId());
    assertEquals("audit.trace.detail.v1", detail.resultSurface().data().get("surfaceContract"));
    assertEquals(traceId, detail.resultSurface().data().get("traceId"));
    assertTrue(detail.resultSurface().toString().contains("redactedEvidence"));
    assertBrowserSafe(detail.resultSurface());

    var hiddenDetail = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", "trace-other-tenant-secret"),
        null,
        AUDITOR_CONTEXT_ID,
        search.resultSurface().surfaceId(),
        "corr-audit-dashboard-hidden-detail"));
    assertEquals("accepted", hiddenDetail.status());
    assertEquals("not_found_or_redacted", hiddenDetail.resultSurface().data().get("decision"));
    assertBrowserSafe(hiddenDetail.resultSurface());

    var timeline = runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("correlationId", "corr-audit-dashboard-search"),
        null,
        AUDITOR_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-audit-dashboard-timeline"));
    assertEquals("accepted", timeline.status());
    assertEquals("surface-audit-trace-timeline", timeline.resultSurface().surfaceId());
    assertEquals("audit.trace.timeline.v1", timeline.resultSurface().data().get("surfaceContract"));
    assertTrue(timeline.resultSurface().toString().contains("auth-context"));
    assertBrowserSafe(timeline.resultSurface());

    var failureEvidence = runAction(new CapabilityActionRequest(
        "action-audit-trace-failure-evidence",
        "action-audit-trace-failure-evidence",
        "audit.trace.failureEvidence.read",
        "audit.trace.failureEvidence.read",
        Map.of("failureCategory", "provider_blocked"),
        null,
        AUDITOR_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-audit-dashboard-failure-evidence"));
    assertEquals("accepted", failureEvidence.status());
    assertEquals("surface-audit-trace-failure-evidence", failureEvidence.resultSurface().surfaceId());
    assertEquals("audit.trace.failureEvidence.v1", failureEvidence.resultSurface().data().get("surfaceContract"));
    assertTrue(failureEvidence.resultSurface().toString().contains("[REDACTED]"));
    assertBrowserSafe(failureEvidence.resultSurface());

    var summaryStart = runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-task-start",
        "action-audit-trace-summary-task-start",
        "audit.trace.summaryTask.start",
        "audit.trace.summary_task.start",
        Map.of("window", "recent"),
        "idem-audit-dashboard-summary-start",
        AUDITOR_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-audit-dashboard-summary-start"));
    assertEquals("blocked_provider_or_runtime", summaryStart.status());
    assertEquals("surface-audit-trace-summary-progress", summaryStart.resultSurface().surfaceId());
    assertEquals("audit.trace.summaryProgress.v1", summaryStart.resultSurface().data().get("surfaceContract"));
    assertEquals(true, summaryStart.resultSurface().data().get("noDirectMutation"));
    assertTrue(summaryStart.resultSurface().toString().contains("no deterministic or model-less successful worker result"));
    assertBrowserSafe(summaryStart.resultSurface());
  }

  @Test
  void protectedAuditTraceDashboardDeniesUnauthorizedAndDisabledContextsSafelyWhileScopingCustomers() throws Exception {
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-dashboard",
        "corr-audit-dashboard-member-denied",
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Audit/Trace dashboard state.");

    var customerDashboard = getSurfaceAs(
        "surface-audit-trace-dashboard",
        "corr-audit-dashboard-customer-scoped",
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID);
    assertEquals("surface-audit-trace-dashboard", customerDashboard.surfaceId());
    assertTrue(customerDashboard.toString().contains("customerId=" + CUSTOMER_ID));
    assertBrowserSafe(customerDashboard);

    assertThrows(RuntimeException.class, () -> runActionAs(
        new CapabilityActionRequest(
            "action-audit-trace-search",
            "action-audit-trace-search",
            "audit.trace.search",
            "audit.trace.search",
            Map.of("customerId", "customer-other"),
            null,
            CUSTOMER_CONTEXT_ID,
            "surface-audit-trace-dashboard",
            "corr-audit-dashboard-customer-cross-scope-denied"),
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped Audit/Trace searches must not enumerate a different customer scope.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-dashboard",
        "corr-audit-dashboard-disabled-denied",
        "workos-audit-disabled",
        "disabled-audit@example.test",
        "Disabled Auditor",
        DISABLED_CONTEXT_ID), "Disabled accounts must not resolve an Audit/Trace AuthContext.");
  }

  private SurfaceEnvelope getSurface(String surfaceId, String correlationId) throws Exception {
    return getSurfaceAs(surfaceId, correlationId, "workos-audit-auditor", "auditor@example.test", "Audit Reviewer", AUDITOR_CONTEXT_ID);
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
    return runActionAs(request, "workos-audit-auditor", "auditor@example.test", "Audit Reviewer", AUDITOR_CONTEXT_ID);
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
    assertNotNull(response.body().traceIds());
    return response.body();
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, AccountStatus status, ScopeType scopeType, String customerId, List<FoundationRole> roles) {
    repository.saveAccount(new Account(email, null, email, email, status, "UNLINKED"));
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
    assertFalse(text.contains("sk_live_"));
    assertFalse(text.contains("sk_test_"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("rawJwt=ey"));
    assertFalse(text.contains("rawProviderCredential=sk_"));
    assertFalse(text.contains("hiddenPromptText=secret"));
    assertFalse(text.contains("rawToolPayload={"));
    assertFalse(text.contains("providerSecret=secret"));
    assertFalse(text.contains("api_key="));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }
}
