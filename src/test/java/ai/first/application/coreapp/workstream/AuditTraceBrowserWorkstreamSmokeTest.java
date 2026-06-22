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
import ai.first.application.foundation.audit.AuditTraceService;
import ai.first.application.foundation.identity.AkkaIdentityRepository;
import ai.first.application.coreapp.audit.AkkaAuditTraceSummaryTaskRepository;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
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
import java.time.Instant;
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
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("audit-trace-agent") && agent.availability().equals("visible")));
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

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-timeline")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace timeline must reject missing bearer tokens.");

    var directTimeline = getSurface("surface-audit-trace-timeline", "corr-audit-timeline-direct");
    assertEquals("surface-audit-trace-timeline", directTimeline.surfaceId());
    assertEquals("audit-timeline", directTimeline.surfaceType());
    assertEquals("audit.trace.timeline.v1", directTimeline.data().get("surfaceContract"));
    assertTrue(directTimeline.data().containsKey("selectedScope"));
    assertTrue(directTimeline.data().containsKey("authorizationBasis"));
    assertTrue(directTimeline.data().containsKey("correlationSummary"));
    assertTrue(((List<Map<String, Object>>) directTimeline.data().get("events")).stream().anyMatch(event -> "auth-context".equals(event.get("eventId"))));
    assertTrue(directTimeline.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-export-request")));
    assertTrue(directTimeline.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-append-investigation-note") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-note")));
    assertTrue(directTimeline.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-search") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-search")));
    assertBrowserSafe(directTimeline);

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
    assertTrue(((List<Map<String, Object>>) timeline.resultSurface().data().get("events")).stream().anyMatch(event -> String.valueOf(event.get("availableEventActionIds")).contains("action-audit-trace-detail")));
    assertTrue(String.valueOf(timeline.resultSurface().data().get("redaction")).contains("non-enumerating"));
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
        "start-audit-summary-task",
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
  @SuppressWarnings("unchecked")
  void protectedAuditTraceSummaryProgressCoversStartReadFailClosedDenialsAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-summary-progress")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace summary progress must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-summary-progress-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-audit-trace-summary-task-start",
            "action-audit-trace-summary-task-start",
            "start-audit-summary-task",
            "audit.trace.summary_task.start",
            Map.of("window", "recent"),
            "idem-audit-summary-missing-bearer",
            AUDITOR_CONTEXT_ID,
            "surface-audit-trace-summary-progress",
            "corr-audit-summary-progress-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace summary start must reject missing bearer tokens.");

    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess());
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertBrowserSafe(shell.body());

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-audit-auditor", "auditor@example.test", "Audit Reviewer"))
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-summary-progress-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(AUDITOR_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertBrowserSafe(bootstrap.body());

    var direct = getSurface("surface-audit-trace-summary-progress", "corr-audit-summary-progress-direct");
    assertEquals("surface-audit-trace-summary-progress", direct.surfaceId());
    assertEquals("workflow-status", direct.surfaceType());
    assertEquals("audit.trace.summaryProgress.v1", direct.data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", direct.data().get("status"));
    assertEquals("blocked-provider-or-runtime", direct.data().get("readiness"));
    assertEquals(true, direct.data().get("noDirectMutation"));
    assertEquals(true, direct.data().get("noFakeSuccess"));
    assertTrue(String.valueOf(direct.data().get("providerRuntime")).contains("ToolPermissionBoundary"));
    assertTrue(String.valueOf(direct.data().get("providerRuntime")).contains("auditTraceSummaryEvidence.read"));
    assertTrue(String.valueOf(direct.data().get("allowedActions")).contains("action-audit-trace-summary-task-start"));
    assertFalse(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-task-read")), "Direct refresh without a retained task must not expose a fake progress read action.");
    assertBrowserSafe(direct);

    var dashboard = getSurface("surface-audit-trace-dashboard", "corr-audit-summary-progress-source-dashboard");
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-task-start") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-summary-progress")));
    assertBrowserSafe(dashboard);

    CapabilityActionResult start;
    try {
      start = runAction(new CapabilityActionRequest(
          "action-audit-trace-summary-task-start",
          "action-audit-trace-summary-task-start",
          "start-audit-summary-task",
          "audit.trace.summary_task.start",
          Map.of("window", "recent"),
          "idem-audit-summary-progress-runtime-smoke",
          AUDITOR_CONTEXT_ID,
          dashboard.surfaceId(),
          "corr-audit-summary-progress-start"));
    } catch (RuntimeException failure) {
      throw new AssertionError("summary start failed from dashboard source " + dashboard.surfaceId() + " with actions " + dashboard.actions(), failure);
    }
    assertEquals("blocked_provider_or_runtime", start.status());
    assertEquals("surface-audit-trace-summary-progress", start.resultSurface().surfaceId());
    assertEquals("audit.trace.summaryProgress.v1", start.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", start.resultSurface().data().get("status"));
    assertEquals("blocked-provider-or-runtime", start.resultSurface().data().get("readiness"));
    assertTrue(start.traceIds().stream().anyMatch(trace -> trace.contains("summary") || trace.contains("autonomous_task")));
    assertTrue(start.resultSurface().traceIds().stream().anyMatch(trace -> trace.contains("summary") || trace.contains("autonomous_task")));
    assertEquals("corr-audit-summary-progress-start", start.correlationId());
    assertEquals(true, start.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, start.resultSurface().data().get("noFakeSuccess"));
    assertTrue(String.valueOf(start.resultSurface().data().get("providerRuntime")).contains("no deterministic or model-less successful worker result"));
    assertTrue(String.valueOf(start.resultSurface().data().get("blockers")).contains("blocked_provider_or_runtime"));
    assertTrue(String.valueOf(start.resultSurface().data().get("sourceScope")).contains("provider_readiness"));
    assertTrue(String.valueOf(start.resultSurface().data().get("authorizationBasis")).contains("audit.trace.summary_task.start"));
    assertTrue(String.valueOf(start.resultSurface().data().get("authorizationBasis")).contains("audit.trace.summary_task.read"));
    assertTrue(String.valueOf(start.resultSurface().data().get("redactionSummary")).contains("Raw JWTs"));
    assertTrue(start.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-task-read") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-summary-progress")));
    assertTrue(start.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-failure-evidence") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-failure-evidence")));
    assertBrowserSafe(start.resultSurface());

    assertNotNull(start.resultSurface().data().get("summaryTaskId"));
    var summaryTaskId = String.valueOf(start.resultSurface().data().get("summaryTaskId"));
    assertFalse(summaryTaskId.isBlank());
    var retainedTask = new AkkaAuditTraceSummaryTaskRepository(componentClient).find(summaryTaskId);
    assertTrue(retainedTask.isPresent(), "Started summary task must be immediately retained in the Akka-backed repository for protected progress reads.");
    var read = runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-task-read",
        "action-audit-trace-summary-task-read",
        "audit.trace.summary_task.read",
        "audit.trace.summary_task.read",
        Map.of("summaryTaskId", summaryTaskId),
        null,
        AUDITOR_CONTEXT_ID,
        start.resultSurface().surfaceId(),
        "corr-audit-summary-progress-read"));
    assertEquals("blocked_provider_or_runtime", read.status());
    assertEquals("surface-audit-trace-summary-progress", read.resultSurface().surfaceId());
    assertEquals(summaryTaskId, read.resultSurface().data().get("summaryTaskId"));
    assertEquals("corr-audit-summary-progress-read", read.correlationId());
    assertEquals(true, read.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, read.resultSurface().data().get("noFakeSuccess"));
    assertTrue(read.resultSurface().traceIds().stream().anyMatch(trace -> trace.contains("summary") || trace.contains("autonomous_task")));
    assertTrue(String.valueOf(read.resultSurface().data().get("providerRuntime")).contains("no deterministic or model-less successful worker result"));
    assertTrue(String.valueOf(read.resultSurface().data().get("authorizationBasis")).contains("audit.trace.summary_task.read"));
    assertBrowserSafe(read.resultSurface());

    assertThrows(RuntimeException.class, () -> runActionAs(
        new CapabilityActionRequest(
            "action-audit-trace-summary-task-start",
            "action-audit-trace-summary-task-start",
            "start-audit-summary-task",
            "audit.trace.summary_task.start",
            Map.of("window", "recent"),
            "idem-audit-summary-member-denied",
            MEMBER_CONTEXT_ID,
            dashboard.surfaceId(),
            "corr-audit-summary-progress-member-denied"),
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not start Audit/Trace summary worker tasks.");

    assertThrows(RuntimeException.class, () -> runActionAs(
        new CapabilityActionRequest(
            "action-audit-trace-summary-task-read",
            "action-audit-trace-summary-task-read",
            "audit.trace.summary_task.read",
            "audit.trace.summary_task.read",
            Map.of("summaryTaskId", summaryTaskId),
            null,
            CUSTOMER_CONTEXT_ID,
            dashboard.surfaceId(),
            "corr-audit-summary-progress-customer-denied"),
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped actors must not enumerate tenant-scoped summary worker tasks.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedAuditTraceSummaryReviewCoversReviewAcceptRejectDenialsAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-summary-review")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace summary review must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-summary-review-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-audit-trace-summary-review",
            "action-audit-trace-summary-review",
            "audit.trace.summary_task.review",
            "audit.trace.summary_task.review",
            Map.of("summaryTaskId", "audit-summary-review-missing-bearer"),
            null,
            AUDITOR_CONTEXT_ID,
            "surface-audit-trace-summary-progress",
            "corr-audit-summary-review-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace summary review action path must reject missing bearer tokens.");

    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess());
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertBrowserSafe(shell.body());

    var direct = getSurface("surface-audit-trace-summary-review", "corr-audit-summary-review-direct-not-ready");
    assertEquals("surface-audit-trace-summary-review", direct.surfaceId());
    assertEquals("decision-card", direct.surfaceType());
    assertEquals("audit.trace.summaryReview.v1", direct.data().get("surfaceContract"));
    assertEquals("review_not_ready", direct.data().get("status"));
    assertEquals("review-not-ready", direct.data().get("readiness"));
    assertEquals(true, direct.data().get("noDirectMutation"));
    assertEquals(true, direct.data().get("noFakeSuccess"));
    assertTrue(String.valueOf(direct.data().get("advisorySummary")).contains("no cached, fixture, provider-bypassed, or model-less summary is accepted"));
    assertTrue(String.valueOf(direct.data().get("disabledActions")).contains("review_not_ready"));
    assertFalse(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-accept")), "Direct not-ready review must not expose accept as a fake-success action.");
    assertBrowserSafe(direct);

    var repository = new AkkaAuditTraceSummaryTaskRepository(componentClient);
    var reviewableTask = completedSummaryTask("audit-summary-review-runtime-smoke", "idem-audit-summary-review-runtime-smoke", "trace-audit-summary-review-model-backed");
    repository.save(reviewableTask);

    var review = runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-review",
        "action-audit-trace-summary-review",
        "audit.trace.summary_task.review",
        "audit.trace.summary_task.review",
        Map.of("summaryTaskId", reviewableTask.taskId()),
        null,
        AUDITOR_CONTEXT_ID,
        "surface-audit-trace-summary-progress",
        "corr-audit-summary-review-open"));
    assertEquals("ready_for_review", review.status());
    assertEquals("surface-audit-trace-summary-review", review.resultSurface().surfaceId());
    assertEquals("audit.trace.summaryReview.v1", review.resultSurface().data().get("surfaceContract"));
    assertEquals("ready_for_review", review.resultSurface().data().get("status"));
    assertEquals("ready_for_review", review.resultSurface().data().get("readiness"));
    assertEquals(reviewableTask.taskId(), review.resultSurface().data().get("summaryTaskId"));
    assertTrue(String.valueOf(review.resultSurface().data().get("reviewState")).contains("unreviewed"));
    assertTrue(String.valueOf(review.resultSurface().data().get("advisorySummary")).contains("redacted advisory summary"));
    assertTrue(String.valueOf(review.resultSurface().data().get("evidenceSummary")).contains("auditTraceSummaryEvidence.read"));
    assertTrue(String.valueOf(review.resultSurface().data().get("omissions")).contains("rawPrompt"));
    assertTrue(String.valueOf(review.resultSurface().data().get("qualityNotes")).contains("modelBackedRequired=true"));
    assertTrue(String.valueOf(review.resultSurface().data().get("decisionForm")).contains("noSourceMutation=true"));
    assertEquals(true, review.resultSurface().data().get("noDirectMutation"));
    assertEquals(false, review.resultSurface().data().get("noFakeSuccess"));
    assertFalse(review.resultSurface().traceIds().isEmpty());
    assertTrue(review.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-accept") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-summary-review")));
    assertTrue(review.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-reject") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-summary-review")));
    assertTrue(review.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-detail") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-detail")));
    assertTrue(review.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-timeline") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-timeline")));
    assertTrue(review.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-failure-evidence") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-failure-evidence")));
    assertTrue(review.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-investigation-guide") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-guide")));
    assertTrue(review.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-export-request")));
    assertTrue(review.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-append-investigation-note") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-note")));
    assertBrowserSafe(review.resultSurface());

    var accepted = runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-accept",
        "action-audit-trace-summary-accept",
        "audit.trace.summary_task.accept",
        "audit.trace.summary_task.accept",
        Map.of("summaryTaskId", reviewableTask.taskId(), "reason", "Retain the redacted advisory summary as review evidence only; no source mutation."),
        "idem-audit-summary-review-accept",
        AUDITOR_CONTEXT_ID,
        review.resultSurface().surfaceId(),
        "corr-audit-summary-review-accept"));
    assertEquals("accepted", accepted.status());
    assertEquals("accepted", accepted.resultSurface().data().get("status"));
    assertTrue(String.valueOf(accepted.resultSurface().data().get("reviewState")).contains("accepted"));
    assertTrue(String.valueOf(accepted.resultSurface().data().get("reviewState")).contains("source mutation"));
    assertTrue(String.valueOf(accepted.message()).contains("advisory review evidence only"));
    assertFalse(accepted.resultSurface().traceIds().isEmpty());
    assertFalse(accepted.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-accept")), "Accepted review must not expose another consequential accept action.");
    assertBrowserSafe(accepted.resultSurface());

    var acceptedReplay = runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-accept",
        "action-audit-trace-summary-accept",
        "audit.trace.summary_task.accept",
        "audit.trace.summary_task.accept",
        Map.of("summaryTaskId", reviewableTask.taskId(), "reason", "Idempotent replay should keep review evidence only."),
        "idem-audit-summary-review-accept-replay",
        AUDITOR_CONTEXT_ID,
        accepted.resultSurface().surfaceId(),
        "corr-audit-summary-review-accept-replay"));
    assertEquals("accepted", acceptedReplay.status());
    assertEquals(accepted.resultSurface().data().get("summaryTaskId"), acceptedReplay.resultSurface().data().get("summaryTaskId"));
    assertBrowserSafe(acceptedReplay.resultSurface());

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-reject",
        "action-audit-trace-summary-reject",
        "audit.trace.summary_task.reject",
        "audit.trace.summary_task.reject",
        Map.of("summaryTaskId", reviewableTask.taskId(), "reason", "Conflicting decision must fail closed."),
        "idem-audit-summary-review-conflict",
        AUDITOR_CONTEXT_ID,
        accepted.resultSurface().surfaceId(),
        "corr-audit-summary-review-conflict")), "Conflicting accept/reject decisions must fail closed instead of mutating retained review evidence.");

    var rejectableTask = completedSummaryTask("audit-summary-review-runtime-reject", "idem-audit-summary-review-runtime-reject", "trace-audit-summary-review-model-backed-reject");
    repository.save(rejectableTask);
    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-reject",
        "action-audit-trace-summary-reject",
        "audit.trace.summary_task.reject",
        "audit.trace.summary_task.reject",
        Map.of("summaryTaskId", rejectableTask.taskId(), "reason", ""),
        "idem-audit-summary-review-reject-validation",
        AUDITOR_CONTEXT_ID,
        "surface-audit-trace-summary-review",
        "corr-audit-summary-review-reject-validation")), "Rejecting a summary without a safe reason must return backend validation failure.");

    var rejected = runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-reject",
        "action-audit-trace-summary-reject",
        "audit.trace.summary_task.reject",
        "audit.trace.summary_task.reject",
        Map.of("summaryTaskId", rejectableTask.taskId(), "reason", "Reject because the advisory summary omitted required retained evidence categories; source evidence remains unchanged."),
        "idem-audit-summary-review-reject",
        AUDITOR_CONTEXT_ID,
        "surface-audit-trace-summary-review",
        "corr-audit-summary-review-reject"));
    assertEquals("rejected", rejected.status());
    assertEquals("rejected", rejected.resultSurface().data().get("status"));
    assertTrue(String.valueOf(rejected.resultSurface().data().get("reviewState")).contains("rejected"));
    assertTrue(String.valueOf(rejected.message()).contains("advisory review evidence only"));
    assertFalse(rejected.resultSurface().traceIds().isEmpty());
    assertBrowserSafe(rejected.resultSurface());

    assertThrows(RuntimeException.class, () -> runActionAs(
        new CapabilityActionRequest(
            "action-audit-trace-summary-review",
            "action-audit-trace-summary-review",
            "audit.trace.summary_task.review",
            "audit.trace.summary_task.review",
            Map.of("summaryTaskId", rejectableTask.taskId()),
            null,
            MEMBER_CONTEXT_ID,
            "surface-audit-trace-summary-progress",
            "corr-audit-summary-review-member-denied"),
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not review Audit/Trace summaries.");

    assertThrows(RuntimeException.class, () -> runActionAs(
        new CapabilityActionRequest(
            "action-audit-trace-summary-review",
            "action-audit-trace-summary-review",
            "audit.trace.summary_task.review",
            "audit.trace.summary_task.review",
            Map.of("summaryTaskId", rejectableTask.taskId()),
            null,
            CUSTOMER_CONTEXT_ID,
            "surface-audit-trace-summary-progress",
            "corr-audit-summary-review-customer-denied"),
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped actors must not enumerate tenant-scoped Audit/Trace summaries.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedAuditTraceSearchCoversFiltersValidationRowActionsExportDenialsAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-search")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace search must reject missing bearer tokens.");

    var defaultSearch = getSurface("surface-audit-trace-search", "corr-audit-search-direct");
    assertEquals("surface-audit-trace-search", defaultSearch.surfaceId());
    assertEquals("list-search", defaultSearch.surfaceType());
    assertEquals("audit.trace.search.v1", defaultSearch.data().get("surfaceContract"));
    assertEquals("corr-audit-search-direct", defaultSearch.correlationId());
    assertTrue(defaultSearch.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-audit-search")));
    assertTrue(defaultSearch.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-search") && action.capabilityId().equals("audit.trace.search")));
    assertTrue(defaultSearch.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-detail") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-detail")));
    assertTrue(defaultSearch.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-export-request")));
    assertTrue(defaultSearch.toString().contains("selectedContextId=" + AUDITOR_CONTEXT_ID));
    assertTrue(defaultSearch.toString().contains("non-enumerating"));
    assertBrowserSafe(defaultSearch);

    var directRows = (List<Map<String, Object>>) defaultSearch.data().get("rows");
    assertFalse(directRows.isEmpty(), "Direct protected search should expose authorized trace rows only.");
    var directPageInfo = (Map<String, Object>) defaultSearch.data().get("pageInfo");
    assertEquals(10, directPageInfo.get("pageSize"));
    assertEquals(directRows.size(), directPageInfo.get("totalKnownCount"));

    var filteredSearch = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "AUTH_CONTEXT_RESOLVE", "pageSize", "1"),
        null,
        AUDITOR_CONTEXT_ID,
        defaultSearch.surfaceId(),
        "corr-audit-search-filtered"));
    assertEquals("accepted", filteredSearch.status());
    assertEquals("surface-audit-trace-search", filteredSearch.resultSurface().surfaceId());
    assertEquals("audit.trace.search.v1", filteredSearch.resultSurface().data().get("surfaceContract"));
    var filteredRows = (List<Map<String, Object>>) filteredSearch.resultSurface().data().get("rows");
    assertEquals(1, filteredRows.size(), "Browser string page size must be parsed server-side and cap rows.");
    assertTrue(String.valueOf(filteredRows.get(0).get("eventKind")).contains("AUTH_CONTEXT_RESOLVE"));
    assertTrue(((List<String>) filteredRows.get(0).get("availableRowActionIds")).containsAll(List.of("action-audit-trace-detail", "action-audit-trace-timeline", "action-audit-trace-failure-evidence", "action-audit-trace-investigation-guide")));
    assertTrue(filteredSearch.resultSurface().toString().contains("corr-audit-search-filtered"));
    assertBrowserSafe(filteredSearch.resultSurface());

    var emptySearch = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "definitely-no-authorized-row", "pageSize", 5),
        null,
        AUDITOR_CONTEXT_ID,
        defaultSearch.surfaceId(),
        "corr-audit-search-empty"));
    assertEquals("accepted", emptySearch.status());
    assertEquals(List.of(), emptySearch.resultSurface().data().get("rows"));
    var emptyState = (Map<String, Object>) emptySearch.resultSurface().data().get("emptyState");
    assertEquals("empty", emptyState.get("status"));
    assertTrue(String.valueOf(emptyState.get("recovery")).contains("Clear filters"));
    assertBrowserSafe(emptySearch.resultSurface());

    var invalidSearch = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("pageSize", 0),
        null,
        AUDITOR_CONTEXT_ID,
        defaultSearch.surfaceId(),
        "corr-audit-search-invalid-page"));
    assertEquals("validation-error", invalidSearch.status());
    assertEquals("surface-audit-trace-validation-error", invalidSearch.resultSurface().surfaceId());
    assertEquals("pageSize", invalidSearch.resultSurface().data().get("field"));
    assertTrue(invalidSearch.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-audit-validation")));
    assertBrowserSafe(invalidSearch.resultSurface());

    var traceId = String.valueOf(filteredRows.get(0).get("traceId"));
    var rowDetail = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", traceId),
        null,
        AUDITOR_CONTEXT_ID,
        filteredSearch.resultSurface().surfaceId(),
        "corr-audit-search-row-detail"));
    assertEquals("accepted", rowDetail.status());
    assertEquals("surface-audit-trace-detail", rowDetail.resultSurface().surfaceId());
    assertEquals(traceId, rowDetail.resultSurface().data().get("traceId"));
    assertBrowserSafe(rowDetail.resultSurface());

    var rowTimeline = runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("correlationId", filteredRows.get(0).get("correlationId")),
        null,
        AUDITOR_CONTEXT_ID,
        filteredSearch.resultSurface().surfaceId(),
        "corr-audit-search-row-timeline"));
    assertEquals("accepted", rowTimeline.status());
    assertEquals("surface-audit-trace-timeline", rowTimeline.resultSurface().surfaceId());
    assertEquals("audit.trace.timeline.v1", rowTimeline.resultSurface().data().get("surfaceContract"));
    assertBrowserSafe(rowTimeline.resultSurface());

    var guide = runAction(new CapabilityActionRequest(
        "action-audit-trace-investigation-guide",
        "action-audit-trace-investigation-guide",
        "audit.trace.investigationGuide.read",
        "audit.trace.investigationGuide.read",
        Map.of("traceId", traceId),
        null,
        AUDITOR_CONTEXT_ID,
        filteredSearch.resultSurface().surfaceId(),
        "corr-audit-search-guide"));
    assertEquals("accepted", guide.status());
    assertEquals("surface-audit-trace-investigation-guide", guide.resultSurface().surfaceId());
    assertTrue(guide.resultSurface().toString().contains("Continue only with backend-authorized"));
    assertBrowserSafe(guide.resultSurface());

    var export = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "jsonl-redacted", "reason", "Runtime smoke export request for visible search rows only."),
        "idem-audit-search-export",
        AUDITOR_CONTEXT_ID,
        filteredSearch.resultSurface().surfaceId(),
        "corr-audit-search-export"));
    assertEquals("accepted", export.status());
    assertEquals("surface-audit-trace-export-request", export.resultSurface().surfaceId());
    assertEquals("approval_required", export.resultSurface().data().get("status"));
    assertTrue(export.resultSurface().toString().contains("Unredacted export is not a default browser action"));
    assertBrowserSafe(export.resultSurface());

    var exportWithoutIdempotency = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("reason", "Missing idempotency should be safely rejected."),
        null,
        AUDITOR_CONTEXT_ID,
        filteredSearch.resultSurface().surfaceId(),
        "corr-audit-search-export-missing-idempotency"));
    assertEquals("validation-error", exportWithoutIdempotency.status());
    assertNotNull(exportWithoutIdempotency.traceIds());
    assertBrowserSafe(exportWithoutIdempotency);

    var returnToDashboard = runAction(new CapabilityActionRequest(
        "action-audit-trace-dashboard",
        "action-audit-trace-dashboard",
        "audit.trace.dashboard.read",
        "audit.trace.dashboard.read",
        null,
        null,
        AUDITOR_CONTEXT_ID,
        filteredSearch.resultSurface().surfaceId(),
        "corr-audit-search-dashboard-return"));
    assertEquals("accepted", returnToDashboard.status());
    assertEquals("surface-audit-trace-dashboard", returnToDashboard.resultSurface().surfaceId());
    assertBrowserSafe(returnToDashboard.resultSurface());

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("tenantId", "tenant-other", "filter", "recent"),
        null,
        AUDITOR_CONTEXT_ID,
        defaultSearch.surfaceId(),
        "corr-audit-search-cross-tenant-denied")), "Cross-tenant Audit/Trace search must fail closed without row/count enumeration.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedAuditTraceExportRequestCoversDirectRefreshPolicyDenialsIdempotencyAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-export-request")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace export-request surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-export-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-audit-trace-request-redacted-export",
            "action-audit-trace-request-redacted-export",
            "audit.trace.export.request",
            "audit.trace.export.request",
            Map.of("format", "jsonl-redacted", "reason", "Missing bearer export request must be rejected."),
            "idem-audit-export-missing-bearer",
            AUDITOR_CONTEXT_ID,
            "surface-audit-trace-search",
            "corr-audit-export-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace export-request action path must reject missing bearer tokens.");

    var direct = getSurface("surface-audit-trace-export-request", "corr-audit-export-direct");
    assertEquals("surface-audit-trace-export-request", direct.surfaceId());
    assertEquals("audit.trace.exportRequest.v1", direct.data().get("surfaceContract"));
    assertEquals("approval_required", direct.data().get("status"));
    assertEquals("jsonl-redacted", direct.data().get("requestedFormat"));
    assertEquals("corr-audit-export-direct", direct.correlationId());
    assertTrue(direct.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-audit-export")));
    assertTrue(direct.data().containsKey("exportRequest"));
    assertTrue(direct.data().containsKey("exportScope"));
    assertTrue(direct.data().containsKey("authorizationBasis"));
    assertTrue(direct.data().containsKey("policyDecision"));
    assertTrue(direct.data().containsKey("bundleMetadata"));
    assertTrue(direct.data().containsKey("approval"));
    var directDecision = (Map<String, Object>) direct.data().get("policyDecision");
    assertEquals("approval_required", directDecision.get("classification"));
    assertEquals(true, directDecision.get("approvalRequired"));
    var directAuthorization = (Map<String, Object>) direct.data().get("authorizationBasis");
    assertEquals(AUDITOR_CONTEXT_ID, directAuthorization.get("selectedContextId"));
    assertTrue(String.valueOf(directAuthorization.get("visibleCapabilityIds")).contains("audit.trace.export.request"));
    assertTrue(String.valueOf(direct.data().get("disabledActions")).contains("unredacted_export_forbidden"));
    assertTrue(String.valueOf(direct.data().get("delivery")).contains("No raw browser download URL"));
    assertEquals(true, direct.data().get("noDirectMutation"));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export") && action.capabilityId().equals("audit.trace.export.request") && action.idempotency().required()));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-detail") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-detail")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-dashboard") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-dashboard")));
    assertBrowserSafe(direct);

    var submitted = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "jsonl-redacted", "reason", "Dedicated runtime test export request for authorized redacted evidence."),
        "idem-audit-export-dedicated",
        AUDITOR_CONTEXT_ID,
        direct.surfaceId(),
        "corr-audit-export-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("surface-audit-trace-export-request", submitted.resultSurface().surfaceId());
    assertEquals("approval_required", submitted.resultSurface().data().get("status"));
    assertEquals("audit.trace.exportRequest.v1", submitted.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-audit-export-submit", submitted.correlationId());
    assertTrue(submitted.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-audit-export")));
    assertTrue(submitted.resultSurface().toString().contains("approval_required"));
    assertTrue(submitted.resultSurface().toString().contains("No raw browser download URL"));
    assertBrowserSafe(submitted.resultSurface());

    var retry = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "jsonl-redacted", "reason", "Dedicated runtime test export request retry for the same scoped evidence."),
        "idem-audit-export-dedicated",
        AUDITOR_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-audit-export-retry"));
    assertEquals("accepted", retry.status());
    assertEquals("surface-audit-trace-export-request", retry.resultSurface().surfaceId());
    assertEquals(submitted.resultSurface().data().get("exportId"), retry.resultSurface().data().get("exportId"), "Same idempotency key must keep the backend export decision handle stable.");
    assertEquals("approval_required", retry.resultSurface().data().get("status"));
    assertBrowserSafe(retry.resultSurface());

    var missingIdempotency = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "jsonl-redacted", "reason", "Missing idempotency must be rejected."),
        null,
        AUDITOR_CONTEXT_ID,
        direct.surfaceId(),
        "corr-audit-export-missing-idempotency"));
    assertEquals("validation-error", missingIdempotency.status());
    assertNotNull(missingIdempotency.traceIds());
    assertBrowserSafe(missingIdempotency);

    var unredacted = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "csv-unredacted", "reason", "Attempt unredacted export should be denied by policy."),
        "idem-audit-export-unredacted-denied",
        AUDITOR_CONTEXT_ID,
        direct.surfaceId(),
        "corr-audit-export-unredacted-denied"));
    assertEquals("accepted", unredacted.status());
    assertEquals("denied", unredacted.resultSurface().data().get("status"));
    var unredactedDecision = (Map<String, Object>) unredacted.resultSurface().data().get("policyDecision");
    assertEquals("unredacted_export_forbidden", unredactedDecision.get("classification"));
    assertEquals(false, unredactedDecision.get("approvalRequired"));
    assertTrue(unredacted.resultSurface().toString().contains("Unredacted browser export is forbidden"));
    assertBrowserSafe(unredacted.resultSurface());

    var customerScoped = getSurfaceAs(
        "surface-audit-trace-export-request",
        "corr-audit-export-customer-scoped",
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID);
    assertEquals("surface-audit-trace-export-request", customerScoped.surfaceId());
    var customerAuthorization = (Map<String, Object>) customerScoped.data().get("authorizationBasis");
    assertEquals(true, customerAuthorization.get("customerScopeRestricted"));
    assertTrue(customerScoped.toString().contains("scopeKind=customer"));
    assertBrowserSafe(customerScoped);

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("tenantId", "tenant-other", "format", "jsonl-redacted", "reason", "Cross-tenant export must be denied."),
        "idem-audit-export-cross-tenant-denied",
        AUDITOR_CONTEXT_ID,
        direct.surfaceId(),
        "corr-audit-export-cross-tenant-denied")), "Cross-tenant export requests must fail closed without hidden evidence enumeration.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-export-request",
        "corr-audit-export-member-denied",
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not open Audit/Trace export-request decisions.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-export-request",
        "corr-audit-export-disabled-denied",
        "workos-audit-disabled",
        "disabled-audit@example.test",
        "Disabled Auditor",
        DISABLED_CONTEXT_ID), "Disabled accounts must not resolve an Audit/Trace export-request AuthContext.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedAuditTraceDetailCoversDirectRefreshFollowUpActionsDenialsAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-detail")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace detail must reject missing bearer tokens.");

    var detail = getSurface("surface-audit-trace-detail", "corr-audit-detail-direct");
    assertEquals("surface-audit-trace-detail", detail.surfaceId());
    assertEquals("detail-edit", detail.surfaceType());
    assertEquals("audit.trace.detail.v1", detail.data().get("surfaceContract"));
    assertTrue(String.valueOf(detail.data().get("traceId")).startsWith("trace-auth-context-"));
    assertEquals("AUTH_CONTEXT_RESOLVE", detail.data().get("eventKind"));
    assertEquals("audit.trace.read", detail.data().get("authorizationBasis"));
    assertEquals("allowed", detail.data().get("decision"));
    assertTrue(detail.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-audit-detail")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-timeline") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-timeline")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-failure-evidence") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-failure-evidence")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-investigation-guide") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-guide")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-export-request")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-append-investigation-note") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-note")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-search") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-search")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-dashboard") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-dashboard")));
    assertTrue(detail.toString().contains("redactedEvidence"));
    assertTrue(detail.toString().contains("rawProviderCredential"));
    assertBrowserSafe(detail);

    var traceId = String.valueOf(detail.data().get("traceId"));
    var detailAction = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", traceId),
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-action-refresh"));
    assertEquals("accepted", detailAction.status());
    assertEquals("surface-audit-trace-detail", detailAction.resultSurface().surfaceId());
    assertEquals(traceId, detailAction.resultSurface().data().get("traceId"));
    assertBrowserSafe(detailAction.resultSurface());

    var hiddenDetail = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", "trace-other-tenant-secret"),
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-hidden"));
    assertEquals("accepted", hiddenDetail.status());
    assertEquals("not_found_or_redacted", hiddenDetail.resultSurface().data().get("decision"));
    var hiddenRedaction = (Map<String, Object>) hiddenDetail.resultSurface().data().get("redactionMetadata");
    assertEquals(true, hiddenRedaction.get("nonEnumerating"));
    assertBrowserSafe(hiddenDetail.resultSurface());

    var invalidDetail = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", "x".repeat(161)),
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-invalid"));
    assertEquals("validation-error", invalidDetail.status());
    assertEquals("surface-audit-trace-validation-error", invalidDetail.resultSurface().surfaceId());
    assertEquals("traceId", invalidDetail.resultSurface().data().get("field"));
    assertTrue(invalidDetail.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-validation")));
    assertBrowserSafe(invalidDetail.resultSurface());

    var correlationIds = (List<String>) detail.data().get("correlationIds");
    var timeline = runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("correlationId", correlationIds.get(0)),
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-timeline"));
    assertEquals("accepted", timeline.status());
    assertEquals("surface-audit-trace-timeline", timeline.resultSurface().surfaceId());
    assertTrue(timeline.resultSurface().toString().contains("Unauthorized tenant/customer evidence is omitted"));
    assertBrowserSafe(timeline.resultSurface());

    var failureEvidence = runAction(new CapabilityActionRequest(
        "action-audit-trace-failure-evidence",
        "action-audit-trace-failure-evidence",
        "audit.trace.failureEvidence.read",
        "audit.trace.failureEvidence.read",
        Map.of("failureCategory", "AUTH_CONTEXT_RESOLVE"),
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-failure"));
    assertEquals("accepted", failureEvidence.status());
    assertEquals("surface-audit-trace-failure-evidence", failureEvidence.resultSurface().surfaceId());
    assertTrue(failureEvidence.resultSurface().toString().contains("[REDACTED]"));
    assertBrowserSafe(failureEvidence.resultSurface());

    var guide = runAction(new CapabilityActionRequest(
        "action-audit-trace-investigation-guide",
        "action-audit-trace-investigation-guide",
        "audit.trace.investigationGuide.read",
        "audit.trace.investigationGuide.read",
        Map.of("traceId", traceId),
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-guide"));
    assertEquals("accepted", guide.status());
    assertEquals("surface-audit-trace-investigation-guide", guide.resultSurface().surfaceId());
    assertTrue(guide.resultSurface().toString().contains("Continue only with backend-authorized"));
    assertBrowserSafe(guide.resultSurface());

    var export = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "jsonl-redacted", "reason", "Detail runtime smoke export for a visible trace only."),
        "idem-audit-detail-export",
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-export"));
    assertEquals("accepted", export.status());
    assertEquals("surface-audit-trace-export-request", export.resultSurface().surfaceId());
    assertEquals("approval_required", export.resultSurface().data().get("status"));
    assertBrowserSafe(export.resultSurface());

    var note = runAction(new CapabilityActionRequest(
        "action-audit-trace-append-investigation-note",
        "action-audit-trace-append-investigation-note",
        "audit.trace.investigation_note.append",
        "audit.trace.investigation_note.append",
        Map.of("traceId", traceId, "note", "Investigated provider api_key=secret without exposing it."),
        "idem-audit-detail-note",
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-note"));
    assertEquals("recorded", note.status());
    assertEquals("surface-audit-trace-investigation-note", note.resultSurface().surfaceId());
    assertEquals("recorded", note.resultSurface().data().get("status"));
    assertTrue(note.resultSurface().toString().contains("do not mutate source traces"));
    assertBrowserSafe(note.resultSurface());

    var searchReturn = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", traceId, "pageSize", 5),
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-search-return"));
    assertEquals("accepted", searchReturn.status());
    assertEquals("surface-audit-trace-search", searchReturn.resultSurface().surfaceId());
    assertBrowserSafe(searchReturn.resultSurface());

    var dashboardReturn = runAction(new CapabilityActionRequest(
        "action-audit-trace-dashboard",
        "action-audit-trace-dashboard",
        "audit.trace.dashboard.read",
        "audit.trace.dashboard.read",
        null,
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-dashboard-return"));
    assertEquals("accepted", dashboardReturn.status());
    assertEquals("surface-audit-trace-dashboard", dashboardReturn.resultSurface().surfaceId());
    assertBrowserSafe(dashboardReturn.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-detail",
        "corr-audit-detail-member-denied",
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Audit/Trace detail evidence.");

    assertThrows(RuntimeException.class, () -> runActionAs(
        new CapabilityActionRequest(
            "action-audit-trace-detail",
            "action-audit-trace-detail",
            "audit.trace.detail.read",
            "audit.trace.detail.read",
            Map.of("traceId", traceId),
            null,
            MEMBER_CONTEXT_ID,
            detail.surfaceId(),
            "corr-audit-detail-member-action-denied"),
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not invoke the Audit/Trace detail action.");

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("tenantId", "tenant-other", "traceId", traceId),
        null,
        AUDITOR_CONTEXT_ID,
        detail.surfaceId(),
        "corr-audit-detail-cross-tenant-denied")), "Cross-tenant Audit/Trace detail reads must fail closed without hidden evidence enumeration.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-detail",
        "corr-audit-detail-disabled-denied",
        "workos-audit-disabled",
        "disabled-audit@example.test",
        "Disabled Auditor",
        DISABLED_CONTEXT_ID), "Disabled accounts must not resolve an Audit/Trace detail AuthContext.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedAuditTraceTimelineCoversDirectRefreshFollowUpActionsDenialsAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-timeline")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace timeline must reject missing bearer tokens.");

    var searchSeed = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "AUTH_CONTEXT_RESOLVE", "pageSize", 2),
        null,
        AUDITOR_CONTEXT_ID,
        "surface-audit-trace-dashboard",
        "corr-audit-timeline-seed-search"));
    assertEquals("accepted", searchSeed.status());
    assertBrowserSafe(searchSeed.resultSurface());

    var timeline = runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("correlationId", "corr-audit-timeline-seed-search"),
        null,
        AUDITOR_CONTEXT_ID,
        searchSeed.resultSurface().surfaceId(),
        "corr-audit-timeline-action"));
    assertEquals("accepted", timeline.status());
    assertEquals("surface-audit-trace-timeline", timeline.resultSurface().surfaceId());
    assertEquals("audit-timeline", timeline.resultSurface().surfaceType());
    assertEquals("audit.trace.timeline.v1", timeline.resultSurface().data().get("surfaceContract"));
    assertEquals("corr-audit-timeline-seed-search", timeline.resultSurface().data().get("correlationId"));
    assertTrue(timeline.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-timeline")));
    assertTrue(timeline.resultSurface().data().containsKey("selectedScope"));
    assertTrue(timeline.resultSurface().data().containsKey("authorizationBasis"));
    assertTrue(timeline.resultSurface().data().containsKey("correlationSummary"));
    assertTrue(timeline.resultSurface().data().containsKey("omittedCategories"));
    assertTrue(String.valueOf(timeline.resultSurface().data().get("redaction")).contains("non-enumerating"));
    assertTrue(String.valueOf(timeline.resultSurface().data().get("redactionSummary")).contains("not_found_or_redacted"));
    assertTrue(timeline.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-detail") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-detail")));
    assertTrue(timeline.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-failure-evidence") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-failure-evidence")));
    assertTrue(timeline.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-investigation-guide") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-guide")));
    assertTrue(timeline.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-export-request")));
    assertTrue(timeline.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-append-investigation-note") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-note")));
    assertTrue(timeline.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-search") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-search")));
    assertTrue(timeline.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-dashboard") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-dashboard")));
    var events = (List<Map<String, Object>>) timeline.resultSurface().data().get("events");
    assertTrue(events.stream().anyMatch(event -> "auth-context".equals(event.get("eventId"))));
    assertTrue(events.stream().anyMatch(event -> String.valueOf(event.get("availableEventActionIds")).contains("action-audit-trace-detail")));
    var eventTraceId = events.stream()
        .map(event -> String.valueOf(event.get("traceId")))
        .findFirst()
        .orElseThrow();
    assertBrowserSafe(timeline.resultSurface());

    var emptyTimeline = runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("correlationId", "corr-audit-timeline-empty-authorized"),
        null,
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-empty"));
    assertEquals("accepted", emptyTimeline.status());
    var emptyState = (Map<String, Object>) emptyTimeline.resultSurface().data().get("emptyState");
    assertEquals("empty", emptyState.get("status"));
    assertTrue(String.valueOf(emptyState.get("recovery")).contains("hidden evidence is not enumerated"));
    assertBrowserSafe(emptyTimeline.resultSurface());

    var invalidTimeline = runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("correlationId", "x".repeat(129)),
        null,
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-invalid"));
    assertEquals("validation-error", invalidTimeline.status());
    assertEquals("surface-audit-trace-validation-error", invalidTimeline.resultSurface().surfaceId());
    assertEquals("correlationId", invalidTimeline.resultSurface().data().get("field"));
    assertTrue(invalidTimeline.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-validation")));
    assertBrowserSafe(invalidTimeline.resultSurface());

    var detail = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", eventTraceId),
        null,
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-event-detail"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-audit-trace-detail", detail.resultSurface().surfaceId());
    assertEquals(eventTraceId, detail.resultSurface().data().get("traceId"));
    assertBrowserSafe(detail.resultSurface());

    var failureEvidence = runAction(new CapabilityActionRequest(
        "action-audit-trace-failure-evidence",
        "action-audit-trace-failure-evidence",
        "audit.trace.failureEvidence.read",
        "audit.trace.failureEvidence.read",
        Map.of("failureCategory", "AUTH_CONTEXT_RESOLVE"),
        null,
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-failure"));
    assertEquals("accepted", failureEvidence.status());
    assertEquals("surface-audit-trace-failure-evidence", failureEvidence.resultSurface().surfaceId());
    assertTrue(failureEvidence.resultSurface().toString().contains("[REDACTED]"));
    assertBrowserSafe(failureEvidence.resultSurface());

    var guide = runAction(new CapabilityActionRequest(
        "action-audit-trace-investigation-guide",
        "action-audit-trace-investigation-guide",
        "audit.trace.investigationGuide.read",
        "audit.trace.investigationGuide.read",
        Map.of("correlationId", "corr-audit-timeline-seed-search"),
        null,
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-guide"));
    assertEquals("accepted", guide.status());
    assertEquals("surface-audit-trace-investigation-guide", guide.resultSurface().surfaceId());
    assertTrue(guide.resultSurface().toString().contains("Continue only with backend-authorized"));
    assertBrowserSafe(guide.resultSurface());

    var export = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "jsonl-redacted", "reason", "Timeline runtime smoke export for visible ordered events only."),
        "idem-audit-timeline-export",
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-export"));
    assertEquals("accepted", export.status());
    assertEquals("surface-audit-trace-export-request", export.resultSurface().surfaceId());
    assertEquals("approval_required", export.resultSurface().data().get("status"));
    assertTrue(export.resultSurface().toString().contains("Unredacted export is not a default browser action"));
    assertBrowserSafe(export.resultSurface());

    var note = runAction(new CapabilityActionRequest(
        "action-audit-trace-append-investigation-note",
        "action-audit-trace-append-investigation-note",
        "audit.trace.investigation_note.append",
        "audit.trace.investigation_note.append",
        Map.of("traceId", eventTraceId, "note", "Timeline note confirms raw api_key=secret stays redacted."),
        "idem-audit-timeline-note",
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-note"));
    assertEquals("recorded", note.status());
    assertEquals("surface-audit-trace-investigation-note", note.resultSurface().surfaceId());
    assertEquals("recorded", note.resultSurface().data().get("status"));
    assertTrue(note.resultSurface().toString().contains("do not mutate source traces"));
    assertBrowserSafe(note.resultSurface());

    var searchReturn = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "corr-audit-timeline-seed-search", "pageSize", 5),
        null,
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-search-return"));
    assertEquals("accepted", searchReturn.status());
    assertEquals("surface-audit-trace-search", searchReturn.resultSurface().surfaceId());
    assertBrowserSafe(searchReturn.resultSurface());

    var dashboardReturn = runAction(new CapabilityActionRequest(
        "action-audit-trace-dashboard",
        "action-audit-trace-dashboard",
        "audit.trace.dashboard.read",
        "audit.trace.dashboard.read",
        null,
        null,
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-dashboard-return"));
    assertEquals("accepted", dashboardReturn.status());
    assertEquals("surface-audit-trace-dashboard", dashboardReturn.resultSurface().surfaceId());
    assertBrowserSafe(dashboardReturn.resultSurface());

    var customerTimeline = getSurfaceAs(
        "surface-audit-trace-timeline",
        "corr-audit-timeline-customer-scoped",
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID);
    assertEquals("surface-audit-trace-timeline", customerTimeline.surfaceId());
    assertTrue(customerTimeline.toString().contains("customerScopeRestricted=true"));
    assertBrowserSafe(customerTimeline);

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("tenantId", "tenant-other", "correlationId", "corr-audit-timeline-seed-search"),
        null,
        AUDITOR_CONTEXT_ID,
        timeline.resultSurface().surfaceId(),
        "corr-audit-timeline-cross-tenant-denied")), "Cross-tenant Audit/Trace timelines must fail closed without hidden event enumeration.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-timeline",
        "corr-audit-timeline-member-denied",
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Audit/Trace timeline evidence.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-timeline",
        "corr-audit-timeline-disabled-denied",
        "workos-audit-disabled",
        "disabled-audit@example.test",
        "Disabled Auditor",
        DISABLED_CONTEXT_ID), "Disabled accounts must not resolve an Audit/Trace timeline AuthContext.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedAuditTraceFailureEvidenceCoversDirectRefreshFollowUpDenialsFailClosedAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-failure-evidence")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace failure evidence must reject missing bearer tokens.");

    var direct = getSurface("surface-audit-trace-failure-evidence", "corr-audit-failure-direct");
    assertEquals("surface-audit-trace-failure-evidence", direct.surfaceId());
    assertEquals("detail-edit", direct.surfaceType());
    assertEquals("audit.trace.failureEvidence.v1", direct.data().get("surfaceContract"));
    assertEquals("corr-audit-failure-direct", direct.correlationId());
    assertTrue(direct.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-failure")));
    assertTrue(direct.data().containsKey("selectedScope"));
    assertTrue(direct.data().containsKey("authorizationBasis"));
    assertTrue(direct.data().containsKey("failureClassification"));
    assertTrue(direct.data().containsKey("evidence"));
    assertTrue(String.valueOf(direct.data().get("recovery")).contains("fail closed"));
    assertTrue(String.valueOf(direct.data().get("redaction")).contains("non-enumerating"));
    assertTrue(String.valueOf(direct.data().get("redactedDetails")).contains("[REDACTED]"));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-detail") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-detail")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-timeline") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-timeline")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-investigation-guide") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-guide")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-export-request")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-append-investigation-note") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-note")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-search") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-search")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-dashboard") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-dashboard")));
    assertBrowserSafe(direct);

    var seedSearch = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "AUTH_CONTEXT_RESOLVE", "pageSize", 2),
        null,
        AUDITOR_CONTEXT_ID,
        direct.surfaceId(),
        "corr-audit-failure-provider"));
    assertEquals("accepted", seedSearch.status());
    assertEquals("surface-audit-trace-search", seedSearch.resultSurface().surfaceId());
    assertBrowserSafe(seedSearch.resultSurface());

    var failure = runAction(new CapabilityActionRequest(
        "action-audit-trace-failure-evidence",
        "action-audit-trace-failure-evidence",
        "audit.trace.failureEvidence.read",
        "audit.trace.failureEvidence.read",
        Map.of("failureCategory", "AUTH_CONTEXT_RESOLVE"),
        null,
        AUDITOR_CONTEXT_ID,
        direct.surfaceId(),
        "corr-audit-failure-provider"));
    assertEquals("accepted", failure.status());
    assertEquals("surface-audit-trace-failure-evidence", failure.resultSurface().surfaceId());
    assertEquals("audit.trace.failureEvidence.v1", failure.resultSurface().data().get("surfaceContract"));
    assertEquals("AUTH_CONTEXT_RESOLVE", failure.resultSurface().data().get("category"));
    assertEquals("corr-audit-failure-provider", failure.resultSurface().data().get("correlationId"));
    assertTrue(failure.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-failure")));
    assertTrue(String.valueOf(failure.resultSurface().data().get("evidence")).contains("fail-closed when provider/runtime/tool-boundary configuration is unavailable"));
    assertTrue(String.valueOf(failure.resultSurface().data().get("evidence")).contains("no source records are mutated"));
    assertTrue(String.valueOf(failure.resultSurface().data().get("policyRefs")).contains("audit.trace.failureEvidence.read"));
    assertBrowserSafe(failure.resultSurface());

    var relatedEvents = (List<Map<String, Object>>) failure.resultSurface().data().get("relatedEvents");
    assertFalse(relatedEvents.isEmpty(), "Failure evidence should expose redacted related events for the selected authorized failure category.");
    var relatedTraceId = String.valueOf(relatedEvents.get(0).get("traceId"));

    var invalidFailure = runAction(new CapabilityActionRequest(
        "action-audit-trace-failure-evidence",
        "action-audit-trace-failure-evidence",
        "audit.trace.failureEvidence.read",
        "audit.trace.failureEvidence.read",
        Map.of("failureCategory", "x".repeat(121)),
        null,
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-invalid"));
    assertEquals("validation-error", invalidFailure.status());
    assertEquals("surface-audit-trace-validation-error", invalidFailure.resultSurface().surfaceId());
    assertEquals("failureCategory", invalidFailure.resultSurface().data().get("field"));
    assertTrue(invalidFailure.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-validation")));
    assertBrowserSafe(invalidFailure.resultSurface());

    var detail = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", relatedTraceId),
        null,
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-detail"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-audit-trace-detail", detail.resultSurface().surfaceId());
    assertEquals(relatedTraceId, detail.resultSurface().data().get("traceId"));
    assertBrowserSafe(detail.resultSurface());

    var timeline = runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("correlationId", "corr-audit-failure-provider"),
        null,
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-timeline"));
    assertEquals("accepted", timeline.status());
    assertEquals("surface-audit-trace-timeline", timeline.resultSurface().surfaceId());
    assertEquals("audit.trace.timeline.v1", timeline.resultSurface().data().get("surfaceContract"));
    assertTrue(timeline.resultSurface().toString().contains("Unauthorized tenant/customer evidence is omitted"));
    assertBrowserSafe(timeline.resultSurface());

    var guide = runAction(new CapabilityActionRequest(
        "action-audit-trace-investigation-guide",
        "action-audit-trace-investigation-guide",
        "audit.trace.investigationGuide.read",
        "audit.trace.investigationGuide.read",
        Map.of("traceId", relatedTraceId),
        null,
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-guide"));
    assertEquals("accepted", guide.status());
    assertEquals("surface-audit-trace-investigation-guide", guide.resultSurface().surfaceId());
    assertTrue(guide.resultSurface().toString().contains("Continue only with backend-authorized"));
    assertBrowserSafe(guide.resultSurface());

    var export = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "jsonl-redacted", "reason", "Failure evidence smoke export for visible redacted failure rows only."),
        "idem-audit-failure-export",
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-export"));
    assertEquals("accepted", export.status());
    assertEquals("surface-audit-trace-export-request", export.resultSurface().surfaceId());
    assertEquals("approval_required", export.resultSurface().data().get("status"));
    assertTrue(export.resultSurface().toString().contains("Unredacted export is not a default browser action"));
    assertBrowserSafe(export.resultSurface());

    var note = runAction(new CapabilityActionRequest(
        "action-audit-trace-append-investigation-note",
        "action-audit-trace-append-investigation-note",
        "audit.trace.investigation_note.append",
        "audit.trace.investigation_note.append",
        Map.of("traceId", relatedTraceId, "note", "Failure evidence note confirms api_key=secret stays redacted."),
        "idem-audit-failure-note",
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-note"));
    assertEquals("recorded", note.status());
    assertEquals("surface-audit-trace-investigation-note", note.resultSurface().surfaceId());
    assertEquals("recorded", note.resultSurface().data().get("status"));
    assertTrue(note.resultSurface().toString().contains("do not mutate source traces"));
    assertBrowserSafe(note.resultSurface());

    var searchReturn = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "AUTH_CONTEXT_RESOLVE", "pageSize", 5),
        null,
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-search-return"));
    assertEquals("accepted", searchReturn.status());
    assertEquals("surface-audit-trace-search", searchReturn.resultSurface().surfaceId());
    assertBrowserSafe(searchReturn.resultSurface());

    var dashboardReturn = runAction(new CapabilityActionRequest(
        "action-audit-trace-dashboard",
        "action-audit-trace-dashboard",
        "audit.trace.dashboard.read",
        "audit.trace.dashboard.read",
        null,
        null,
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-dashboard-return"));
    assertEquals("accepted", dashboardReturn.status());
    assertEquals("surface-audit-trace-dashboard", dashboardReturn.resultSurface().surfaceId());
    assertBrowserSafe(dashboardReturn.resultSurface());

    var customerFailure = getSurfaceAs(
        "surface-audit-trace-failure-evidence",
        "corr-audit-failure-customer-scoped",
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID);
    assertEquals("surface-audit-trace-failure-evidence", customerFailure.surfaceId());
    assertTrue(customerFailure.toString().contains("customerScopeRestricted=true"));
    assertBrowserSafe(customerFailure);

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-audit-trace-failure-evidence",
        "action-audit-trace-failure-evidence",
        "audit.trace.failureEvidence.read",
        "audit.trace.failureEvidence.read",
        Map.of("tenantId", "tenant-other", "failureCategory", "provider_blocked"),
        null,
        AUDITOR_CONTEXT_ID,
        failure.resultSurface().surfaceId(),
        "corr-audit-failure-cross-tenant-denied")), "Cross-tenant Audit/Trace failure evidence must fail closed without hidden failure enumeration.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-failure-evidence",
        "corr-audit-failure-member-denied",
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Audit/Trace failure evidence.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-failure-evidence",
        "corr-audit-failure-disabled-denied",
        "workos-audit-disabled",
        "disabled-audit@example.test",
        "Disabled Auditor",
        DISABLED_CONTEXT_ID), "Disabled accounts must not resolve an Audit/Trace failure-evidence AuthContext.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedAuditTraceInvestigationGuideCoversDirectRefreshFollowUpsDenialsFailClosedAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-investigation-guide")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace investigation guide must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-guide-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-audit-trace-investigation-guide",
            "action-audit-trace-investigation-guide",
            "audit.trace.investigationGuide.read",
            "audit.trace.investigationGuide.read",
            Map.of("correlationId", "corr-audit-guide-missing-bearer-action"),
            null,
            AUDITOR_CONTEXT_ID,
            "surface-audit-trace-dashboard",
            "corr-audit-guide-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace investigation-guide action path must reject missing bearer tokens.");

    var direct = getSurface("surface-audit-trace-investigation-guide", "corr-audit-guide-direct");
    assertEquals("surface-audit-trace-investigation-guide", direct.surfaceId());
    assertEquals("decision", direct.surfaceType());
    assertEquals("audit.trace.investigationGuide.v1", direct.data().get("surfaceContract"));
    assertEquals("corr-audit-guide-direct", direct.correlationId());
    assertEquals("corr-audit-guide-direct", direct.data().get("correlationId"));
    assertEquals(true, direct.data().get("noDirectMutation"));
    assertTrue(direct.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-guide")));
    assertTrue(direct.data().containsKey("selectedScope"));
    assertTrue(direct.data().containsKey("authorizationBasis"));
    assertTrue(direct.data().containsKey("riskSummary"));
    assertTrue(direct.data().containsKey("recommendedPath"));
    assertTrue(direct.data().containsKey("allowedActions"));
    assertTrue(direct.data().containsKey("disabledActions"));
    assertTrue(direct.data().containsKey("evidenceSummary"));
    assertTrue(String.valueOf(direct.data().get("recommendation")).contains("advisory and cannot expand authority"));
    assertTrue(String.valueOf(direct.data().get("riskSummary")).contains("fail-closed when provider/runtime/tool-boundary configuration is unavailable"));
    assertTrue(String.valueOf(direct.data().get("redaction")).contains("non-enumerating"));
    assertTrue(String.valueOf(direct.data().get("disabledActions")).contains("authority_expansion_forbidden"));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-detail") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-detail")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-timeline") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-timeline")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-failure-evidence") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-failure-evidence")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-export-request")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-append-investigation-note") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-investigation-note")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-search") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-search")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-dashboard") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-dashboard")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-task-start") && action.resultSurface().updateSurfaceId().equals("surface-audit-trace-summary-progress")));
    assertBrowserSafe(direct);

    var seedSearch = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "AUTH_CONTEXT_RESOLVE", "pageSize", 2),
        null,
        AUDITOR_CONTEXT_ID,
        direct.surfaceId(),
        "corr-audit-guide-seed"));
    assertEquals("accepted", seedSearch.status());
    assertEquals("surface-audit-trace-search", seedSearch.resultSurface().surfaceId());
    var seedRows = (List<Map<String, Object>>) seedSearch.resultSurface().data().get("rows");
    assertFalse(seedRows.isEmpty(), "Investigation guide smoke needs an authorized trace from the protected search path.");
    var traceId = String.valueOf(seedRows.get(0).get("traceId"));
    assertBrowserSafe(seedSearch.resultSurface());

    var guide = runAction(new CapabilityActionRequest(
        "action-audit-trace-investigation-guide",
        "action-audit-trace-investigation-guide",
        "audit.trace.investigationGuide.read",
        "audit.trace.investigationGuide.read",
        Map.of("traceId", traceId, "correlationId", "corr-audit-guide-seed", "sourceSurfaceId", seedSearch.resultSurface().surfaceId()),
        null,
        AUDITOR_CONTEXT_ID,
        seedSearch.resultSurface().surfaceId(),
        "corr-audit-guide-seed"));
    assertEquals("accepted", guide.status());
    assertEquals("surface-audit-trace-investigation-guide", guide.resultSurface().surfaceId());
    assertEquals("audit.trace.investigationGuide.v1", guide.resultSurface().data().get("surfaceContract"));
    assertEquals("ready", guide.resultSurface().data().get("readiness"));
    assertEquals("corr-audit-guide-seed", guide.resultSurface().data().get("correlationId"));
    assertTrue(String.valueOf(guide.resultSurface().data().get("investigationContext")).contains("trace_detail"));
    assertTrue(String.valueOf(guide.resultSurface().data().get("evidenceSummary")).contains(traceId));
    assertTrue(String.valueOf(guide.resultSurface().data().get("recommendedPath")).contains("step-record-or-export-only-when-governed"));
    assertTrue(String.valueOf(guide.resultSurface().data().get("allowedActions")).contains("audit.trace.investigation_note.append"));
    assertTrue(String.valueOf(guide.resultSurface().data().get("disabledActions")).contains("export_forbidden"));
    assertTrue(guide.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-guide")));
    assertBrowserSafe(guide.resultSurface());

    var invalidGuide = runAction(new CapabilityActionRequest(
        "action-audit-trace-investigation-guide",
        "action-audit-trace-investigation-guide",
        "audit.trace.investigationGuide.read",
        "audit.trace.investigationGuide.read",
        Map.of("correlationId", "x".repeat(129)),
        null,
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-invalid"));
    assertEquals("validation-error", invalidGuide.status());
    assertEquals("surface-audit-trace-validation-error", invalidGuide.resultSurface().surfaceId());
    assertEquals("correlationId", invalidGuide.resultSurface().data().get("field"));
    assertTrue(invalidGuide.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-validation")));
    assertBrowserSafe(invalidGuide.resultSurface());

    var detail = runAction(new CapabilityActionRequest(
        "action-audit-trace-detail",
        "action-audit-trace-detail",
        "audit.trace.detail.read",
        "audit.trace.detail.read",
        Map.of("traceId", traceId),
        null,
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-detail"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-audit-trace-detail", detail.resultSurface().surfaceId());
    assertEquals(traceId, detail.resultSurface().data().get("traceId"));
    assertBrowserSafe(detail.resultSurface());

    var timeline = runAction(new CapabilityActionRequest(
        "action-audit-trace-timeline",
        "action-audit-trace-timeline",
        "audit.trace.timeline.read",
        "audit.trace.timeline.read",
        Map.of("correlationId", "corr-audit-guide-seed"),
        null,
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-timeline"));
    assertEquals("accepted", timeline.status());
    assertEquals("surface-audit-trace-timeline", timeline.resultSurface().surfaceId());
    assertTrue(timeline.resultSurface().toString().contains("Unauthorized tenant/customer evidence is omitted"));
    assertBrowserSafe(timeline.resultSurface());

    var failureEvidence = runAction(new CapabilityActionRequest(
        "action-audit-trace-failure-evidence",
        "action-audit-trace-failure-evidence",
        "audit.trace.failureEvidence.read",
        "audit.trace.failureEvidence.read",
        Map.of("failureCategory", "AUTH_CONTEXT_RESOLVE"),
        null,
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-failure"));
    assertEquals("accepted", failureEvidence.status());
    assertEquals("surface-audit-trace-failure-evidence", failureEvidence.resultSurface().surfaceId());
    assertTrue(failureEvidence.resultSurface().toString().contains("[REDACTED]"));
    assertBrowserSafe(failureEvidence.resultSurface());

    var export = runAction(new CapabilityActionRequest(
        "action-audit-trace-request-redacted-export",
        "action-audit-trace-request-redacted-export",
        "audit.trace.export.request",
        "audit.trace.export.request",
        Map.of("format", "jsonl-redacted", "reason", "Guide smoke export stays redacted and excludes raw provider payloads."),
        "idem-audit-guide-export",
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-export"));
    assertEquals("accepted", export.status());
    assertEquals("surface-audit-trace-export-request", export.resultSurface().surfaceId());
    assertEquals("approval_required", export.resultSurface().data().get("status"));
    assertTrue(export.resultSurface().toString().contains("Unredacted export is not a default browser action"));
    assertBrowserSafe(export.resultSurface());

    var note = runAction(new CapabilityActionRequest(
        "action-audit-trace-append-investigation-note",
        "action-audit-trace-append-investigation-note",
        "audit.trace.investigation_note.append",
        "audit.trace.investigation_note.append",
        Map.of("traceId", traceId, "note", "Guide note confirms api_key=secret stays redacted."),
        "idem-audit-guide-note",
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-note"));
    assertEquals("recorded", note.status());
    assertEquals("surface-audit-trace-investigation-note", note.resultSurface().surfaceId());
    assertTrue(note.resultSurface().toString().contains("do not mutate source traces"));
    assertBrowserSafe(note.resultSurface());

    var searchReturn = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "AUTH_CONTEXT_RESOLVE", "pageSize", 5),
        null,
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-search-return"));
    assertEquals("accepted", searchReturn.status());
    assertEquals("surface-audit-trace-search", searchReturn.resultSurface().surfaceId());
    assertBrowserSafe(searchReturn.resultSurface());

    var dashboardReturn = runAction(new CapabilityActionRequest(
        "action-audit-trace-dashboard",
        "action-audit-trace-dashboard",
        "audit.trace.dashboard.read",
        "audit.trace.dashboard.read",
        null,
        null,
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-dashboard-return"));
    assertEquals("accepted", dashboardReturn.status());
    assertEquals("surface-audit-trace-dashboard", dashboardReturn.resultSurface().surfaceId());
    assertBrowserSafe(dashboardReturn.resultSurface());

    var summaryStart = runAction(new CapabilityActionRequest(
        "action-audit-trace-summary-task-start",
        "action-audit-trace-summary-task-start",
        "start-audit-summary-task",
        "audit.trace.summary_task.start",
        Map.of("window", "guide-visible-evidence"),
        "idem-audit-guide-summary-start",
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-summary-start"));
    assertEquals("blocked_provider_or_runtime", summaryStart.status());
    assertEquals("surface-audit-trace-summary-progress", summaryStart.resultSurface().surfaceId());
    assertTrue(summaryStart.resultSurface().toString().contains("no deterministic or model-less successful worker result"));
    assertBrowserSafe(summaryStart.resultSurface());

    var customerGuide = getSurfaceAs(
        "surface-audit-trace-investigation-guide",
        "corr-audit-guide-customer-scoped",
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID);
    assertEquals("surface-audit-trace-investigation-guide", customerGuide.surfaceId());
    assertTrue(customerGuide.toString().contains("customerScopeRestricted=true"));
    assertBrowserSafe(customerGuide);

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-audit-trace-investigation-guide",
        "action-audit-trace-investigation-guide",
        "audit.trace.investigationGuide.read",
        "audit.trace.investigationGuide.read",
        Map.of("tenantId", "tenant-other", "correlationId", "corr-audit-guide-cross-tenant-denied"),
        null,
        AUDITOR_CONTEXT_ID,
        guide.resultSurface().surfaceId(),
        "corr-audit-guide-cross-tenant-denied")), "Cross-tenant Audit/Trace investigation guides must fail closed without hidden context enumeration.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-investigation-guide",
        "corr-audit-guide-member-denied",
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Audit/Trace investigation guidance.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-investigation-guide",
        "corr-audit-guide-disabled-denied",
        "workos-audit-disabled",
        "disabled-audit@example.test",
        "Disabled Auditor",
        DISABLED_CONTEXT_ID), "Disabled accounts must not resolve an Audit/Trace investigation-guide AuthContext.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedAuditTraceInvestigationNoteCoversDirectRefreshAppendValidationDenialsAndSecretBoundaries() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-audit-trace-investigation-note")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace investigation-note result must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-note-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-audit-trace-append-investigation-note",
            "action-audit-trace-append-investigation-note",
            "audit.trace.investigation_note.append",
            "audit.trace.investigation_note.append",
            Map.of("traceId", "trace-redacted", "note", "missing bearer should not append"),
            "idem-audit-note-missing-bearer",
            AUDITOR_CONTEXT_ID,
            "surface-audit-trace-detail",
            "corr-audit-note-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Audit/Trace investigation-note action must reject missing bearer tokens.");

    var directRefresh = getSurface("surface-audit-trace-investigation-note", "corr-audit-note-direct-refresh");
    assertEquals("surface-audit-trace-investigation-note", directRefresh.surfaceId());
    assertEquals("system-message", directRefresh.surfaceType());
    assertEquals("audit.trace.investigationNote.v1", directRefresh.data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", directRefresh.data().get("status"));
    assertEquals("corr-audit-note-direct-refresh", directRefresh.correlationId());
    assertTrue(directRefresh.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-audit-note")));
    assertTrue(String.valueOf(directRefresh.data().get("noteResult")).contains("not_found_or_redacted"));
    assertTrue(String.valueOf(directRefresh.data().get("disabledActions")).contains("validation_required"));
    assertTrue(String.valueOf(directRefresh.data().get("targetEvidence")).contains("Source traces, policy, authorization, retained evidence"));
    assertBrowserSafe(directRefresh);

    var search = runAction(new CapabilityActionRequest(
        "action-audit-trace-search",
        "action-audit-trace-search",
        "audit.trace.search",
        "audit.trace.search",
        Map.of("filter", "AUTH_CONTEXT", "pageSize", 5),
        null,
        AUDITOR_CONTEXT_ID,
        "surface-audit-trace-dashboard",
        "corr-audit-note-search-target"));
    assertEquals("accepted", search.status());
    var rows = (List<Map<String, Object>>) search.resultSurface().data().get("rows");
    assertFalse(rows.isEmpty(), "Investigation note smoke needs an authorized Audit/Trace row as target evidence.");
    var traceId = String.valueOf(rows.get(0).get("traceId"));

    var validation = runAction(new CapabilityActionRequest(
        "action-audit-trace-append-investigation-note",
        "action-audit-trace-append-investigation-note",
        "audit.trace.investigation_note.append",
        "audit.trace.investigation_note.append",
        Map.of("traceId", traceId, "note", ""),
        "idem-audit-note-validation",
        AUDITOR_CONTEXT_ID,
        search.resultSurface().surfaceId(),
        "corr-audit-note-validation"));
    assertEquals("validation-error", validation.status());
    assertEquals("surface-audit-trace-investigation-note", validation.resultSurface().surfaceId());
    assertEquals("validation-error", validation.resultSurface().data().get("status"));
    assertTrue(String.valueOf(validation.resultSurface().data().get("validationErrors")).contains("note"));
    assertTrue(String.valueOf(validation.resultSurface().data().get("disabledActions")).contains("note_body_rejected"));
    assertBrowserSafe(validation.resultSurface());

    var idempotencyKey = "idem-audit-note-runtime-smoke";
    var expectedItemId = "item-audit-trace-note-" + AuditTraceService.stableSuffix(idempotencyKey);
    var note = runAction(new CapabilityActionRequest(
        "action-audit-trace-append-investigation-note",
        "action-audit-trace-append-investigation-note",
        "audit.trace.investigation_note.append",
        "audit.trace.investigation_note.append",
        Map.of("traceId", traceId, "note", "Runtime smoke note references api_key=secret and bearer hidden-token for redaction."),
        idempotencyKey,
        AUDITOR_CONTEXT_ID,
        search.resultSurface().surfaceId(),
        "corr-audit-note-recorded"));
    assertEquals("recorded", note.status());
    assertEquals("surface-audit-trace-investigation-note", note.resultSurface().surfaceId());
    assertEquals("audit.trace.investigationNote.v1", note.resultSurface().data().get("surfaceContract"));
    assertEquals("recorded", note.resultSurface().data().get("status"));
    assertEquals("ready", note.resultSurface().data().get("readiness"));
    assertTrue(note.traceIds().stream().anyMatch(trace -> trace.contains("trace-audit-note")));
    assertTrue(String.valueOf(note.resultSurface().data().get("noteResult")).contains("client-generated-key-present-redacted"));
    assertTrue(String.valueOf(note.resultSurface().data().get("annotation")).contains("[REDACTED]"));
    assertTrue(String.valueOf(note.resultSurface().data().get("allowedActions")).contains("action-audit-trace-detail"));
    assertTrue(String.valueOf(note.resultSurface().data().get("allowedActions")).contains("action-audit-trace-dashboard"));
    assertEquals(true, note.resultSurface().data().get("noDirectMutation"));
    assertTrue(String.valueOf(note.resultSurface().data().get("redaction")).contains("Raw note bodies before sanitization"));
    assertBrowserSafe(note.resultSurface());

    var replay = runAction(new CapabilityActionRequest(
        "action-audit-trace-append-investigation-note",
        "action-audit-trace-append-investigation-note",
        "audit.trace.investigation_note.append",
        "audit.trace.investigation_note.append",
        Map.of("traceId", traceId, "note", "Runtime smoke note references api_key=secret and bearer hidden-token for redaction."),
        idempotencyKey,
        AUDITOR_CONTEXT_ID,
        search.resultSurface().surfaceId(),
        "corr-audit-note-recorded-replay"));
    assertEquals("recorded", replay.status());
    assertEquals(note.resultSurface().data().get("noteSummary"), replay.resultSurface().data().get("noteSummary"));
    assertBrowserSafe(replay.resultSurface());

    var items = httpClient
        .GET("/api/workstream/items?functionalAgentId=audit-trace-agent")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-audit-auditor", "auditor@example.test", "Audit Reviewer"))
        .addHeader("X-Selected-Context-Id", AUDITOR_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-audit-note-items")
        .responseBodyAs(String.class)
        .invoke();
    assertTrue(items.status().isSuccess());
    assertTrue(items.body().contains(expectedItemId), "Recorded investigation note should create a stable, idempotent workstream item.");
    assertEquals(1, countOccurrences(items.body(), expectedItemId), "Idempotent replay must not duplicate the durable note workstream item.");
    assertBrowserSafe(items.body());

    assertThrows(RuntimeException.class, () -> runActionAs(
        new CapabilityActionRequest(
            "action-audit-trace-append-investigation-note",
            "action-audit-trace-append-investigation-note",
            "audit.trace.investigation_note.append",
            "audit.trace.investigation_note.append",
            Map.of("traceId", traceId, "note", "member should not append"),
            "idem-audit-note-member-denied",
            MEMBER_CONTEXT_ID,
            search.resultSurface().surfaceId(),
            "corr-audit-note-member-denied"),
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not append Audit/Trace investigation notes.");

    assertThrows(RuntimeException.class, () -> runActionAs(
        new CapabilityActionRequest(
            "action-audit-trace-append-investigation-note",
            "action-audit-trace-append-investigation-note",
            "audit.trace.investigation_note.append",
            "audit.trace.investigation_note.append",
            Map.of("traceId", traceId, "note", "cross-customer should not append", "customerId", "customer-other"),
            "idem-audit-note-cross-customer-denied",
            CUSTOMER_CONTEXT_ID,
            search.resultSurface().surfaceId(),
            "corr-audit-note-cross-customer-denied"),
        "workos-audit-customer",
        "customer-audit@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped Audit/Trace notes must not enumerate or annotate a different customer scope.");
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

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-audit-trace-timeline",
        "corr-audit-timeline-member-denied",
        "workos-audit-member",
        "member-audit@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Audit/Trace timeline evidence.");

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

  private AuditTraceSummaryTask completedSummaryTask(String taskId, String idempotencyKey, String traceId) {
    var createdAt = Instant.parse("2026-05-25T10:15:30Z");
    return new AuditTraceSummaryTask(
        taskId,
        "akka-task-" + taskId,
        TENANT_ID,
        null,
        AUDITOR_CONTEXT_ID,
        "auditor@example.test",
        AUDITOR_CONTEXT_ID,
        idempotencyKey,
        Instant.parse("2026-05-20T00:00:00Z"),
        Instant.parse("2026-05-25T00:00:00Z"),
        List.of("admin_audit", "authorization_denial", "provider_readiness", "agent_work"),
        AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Model-backed redacted advisory summary completed through the governed AuditTraceSummaryAutonomousAgent; human review decides whether to retain it as evidence only.",
        null,
        null,
        null,
        List.of("auditTraceSummaryEvidence.read", "auditTraceEvidence.read", "readSkill:audit-trace-summary-review", "readReferenceDoc:audit-trace-summary-review"),
        List.of("audit_trace_summary_finding:provider_readiness", "audit_trace_summary_finding:authorization_denial"),
        List.of(traceId, "trace-audit-summary-review-correlation"),
        createdAt,
        createdAt);
  }

  private String bearerToken(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }

  private static int countOccurrences(String haystack, String needle) {
    var count = 0;
    var fromIndex = 0;
    while ((fromIndex = haystack.indexOf(needle, fromIndex)) >= 0) {
      count++;
      fromIndex += needle.length();
    }
    return count;
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
