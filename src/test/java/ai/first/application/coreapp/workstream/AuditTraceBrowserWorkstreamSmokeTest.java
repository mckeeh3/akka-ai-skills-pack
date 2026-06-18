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
