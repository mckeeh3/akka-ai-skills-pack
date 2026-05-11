package com.example.application.supplies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SupplyAutopilotEndpointIntegrationTest extends TestKitSupport {

  record TelemetryRequest(
      String workflowId,
      String idempotencyKey,
      String traceId,
      String correlationId,
      String customerId,
      String deviceId,
      Instant observedAt,
      int tonerPercent,
      int pagesSinceLastSupply,
      String lifecycleStatus) {}

  record DecisionActionRequest(String idempotencyKey, String actor, String rationale) {}

  record WorkflowResponse(
      String workflowId,
      String decisionId,
      String status,
      boolean staleDecisionTimerRequested,
      boolean shipmentPrepared,
      String message,
      String traceId,
      String outcomeId) {}

  record DecisionRowsResponse(List<DecisionRow> decisions) {}

  record DecisionRow(
      String decisionId,
      String status,
      String proposedAction,
      String riskSummary,
      String impactSummary,
      double confidence,
      long estimatedCostCents,
      int evidenceCount,
      int policyClauseCount,
      String traceId,
      String outcomeId) {}

  record RiskRowsResponse(List<RiskRow> risks) {}

  record RiskRow(
      String workflowId,
      String decisionId,
      String customerId,
      String deviceId,
      String status,
      int tonerPercent,
      double depletionRisk,
      String proposedAction,
      boolean staleDecisionTimerRequested,
      boolean shipmentPrepared,
      String traceId,
      String outcomeId) {}

  record DecisionDetailResponse(
      String decisionId,
      String status,
      String proposedAction,
      String objectiveId,
      String recommendedSku,
      long estimatedCostCents,
      double depletionRisk,
      double confidence,
      String riskSummary,
      String impactSummary,
      List<EvidenceResponse> evidence,
      List<PolicyClauseResponse> policyClauses,
      List<String> alternatives,
      String traceId,
      String correlationId,
      String outcomeId,
      List<TraceEventResponse> traceEvents) {}

  record EvidenceResponse(String type, String source, String summary, double confidence, String traceId) {}

  record PolicyClauseResponse(String clauseId, String summary) {}

  record TraceLookupResponse(
      String decisionId,
      String status,
      String traceId,
      String correlationId,
      String outcomeId,
      List<TraceEventResponse> events) {}

  record TraceEventResponse(
      String eventId,
      String type,
      String idempotencyKey,
      Instant occurredAt,
      String actor,
      String summary,
      int policyClauseCount,
      String outcomeId) {}

  @Test
  void telemetryPendingDecisionDetailAndApprovalFlowUseDurableWorkflowAndDecisionGates() {
    var workflowId = "supply-http-approval-1";

    var start = postTelemetry(telemetry(workflowId, 12, 9_100, "ACTIVE"));
    assertTrue(start.status().isSuccess());
    assertEquals(workflowId, start.body().workflowId());
    assertEquals("trace-supply-http-approval-1", start.body().traceId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var pending = getPendingDecisions();
              assertTrue(pending.status().isSuccess());
              assertTrue(pending.body().decisions().stream().anyMatch(row -> row.decisionId().equals(workflowId)));

              var row =
                  pending.body().decisions().stream()
                      .filter(candidate -> candidate.decisionId().equals(workflowId))
                      .findFirst()
                      .orElseThrow();
              assertEquals("APPROVAL_REQUIRED", row.status());
              assertEquals("REQUIRE_APPROVAL", row.proposedAction());
              assertEquals(4, row.evidenceCount());
              assertTrue(row.policyClauseCount() >= 3);
              assertEquals("trace-supply-http-approval-1", row.traceId());
              assertFalse(row.outcomeId().isBlank());
            });

    var detail = getDecision(workflowId).body();
    assertEquals("APPROVAL_REQUIRED", detail.status());
    assertEquals("GOAL-02", detail.objectiveId());
    assertEquals(4, detail.evidence().size());
    assertTrue(detail.evidence().stream().anyMatch(evidence -> evidence.type().equals("TELEMETRY")));
    assertTrue(detail.policyClauses().stream().anyMatch(clause -> clause.clauseId().equals("SUP-2.0")));
    assertFalse(detail.alternatives().isEmpty());
    assertEquals("trace-supply-http-approval-1", detail.traceId());

    var approve =
        await(
            httpClient
                .POST("/api/supplies/decisions/" + workflowId + "/approve")
                .addHeader("X-Actor", "ops-supervisor")
                .withRequestBody(new DecisionActionRequest("idem-approve-" + workflowId, "", "approved with evidence"))
                .responseBodyAs(WorkflowResponse.class)
                .invokeAsync());

    assertTrue(approve.status().isSuccess());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var approved = getDecision(workflowId).body();
              assertEquals("SHIPMENT_PREPARED", approved.status());
              assertTrue(approved.traceEvents().stream().anyMatch(event -> event.type().equals("APPROVAL_RECORDED")));
              assertTrue(approved.traceEvents().stream().anyMatch(event -> event.type().equals("SHIPMENT_PREPARED")));
              assertTrue(approved.traceEvents().stream().anyMatch(event -> event.actor().equals("ops-supervisor")));
            });
  }

  @Test
  void traceLookupExposesDecisionCardTraceAndOutcomeContextForAutoShipment() {
    var workflowId = "supply-http-auto-1";
    postTelemetry(telemetry(workflowId, 18, 4_000, "ACTIVE"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var trace =
                  await(
                      httpClient
                          .GET("/api/supplies/traces/trace-supply-http-auto-1")
                          .responseBodyAs(TraceLookupResponse.class)
                          .invokeAsync());

              assertTrue(trace.status().isSuccess());
              assertEquals(workflowId, trace.body().decisionId());
              assertEquals("SHIPMENT_PREPARED", trace.body().status());
              assertEquals("corr-supply-http-auto-1", trace.body().correlationId());
              assertFalse(trace.body().outcomeId().isBlank());
              assertTrue(trace.body().events().stream().anyMatch(event -> event.type().equals("RECOMMENDATION_CREATED")));
              assertTrue(trace.body().events().stream().anyMatch(event -> event.type().equals("SHIPMENT_PREPARED")));
              assertTrue(trace.body().events().stream().anyMatch(event -> event.policyClauseCount() > 0));
            });
  }

  @Test
  void riskListEndpointReadsStatusQueryParameter() {
    var workflowId = "supply-http-risk-1";
    postTelemetry(telemetry(workflowId, 12, 9_100, "ACTIVE"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var risks =
                  await(
                      httpClient
                          .GET("/api/supplies/risks?status=WAITING_FOR_APPROVAL")
                          .responseBodyAs(RiskRowsResponse.class)
                          .invokeAsync());

              assertTrue(risks.status().isSuccess());
              var row =
                  risks.body().risks().stream()
                      .filter(candidate -> candidate.workflowId().equals(workflowId))
                      .findFirst()
                      .orElseThrow();
              assertEquals("device-http-risk-1", row.deviceId());
              assertEquals("REQUIRE_APPROVAL", row.proposedAction());
              assertTrue(row.staleDecisionTimerRequested());
              assertFalse(row.shipmentPrepared());
            });
  }

  @Test
  void telemetryValidationFailureBecomesBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/api/supplies/telemetry")
                        .withRequestBody(telemetry("supply-http-invalid-1", 120, 4_000, "ACTIVE"))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("tonerPercent must be between 0 and 100"));
  }

  @Test
  void missingDecisionAndStaleActionFailuresMapToHttpErrors() {
    var missing =
        assertThrows(
            RuntimeException.class,
            () ->
                await(
                    httpClient
                        .GET("/api/supplies/decisions/missing-supply-decision")
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(missing.getMessage().contains("HTTP status 404 Not Found"));
    assertTrue(missing.getMessage().contains("decision not found"));

    var workflowId = "supply-http-stale-action-1";
    postTelemetry(telemetry(workflowId, 18, 4_000, "ACTIVE"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> assertEquals("SHIPMENT_PREPARED", getDecision(workflowId).body().status()));

    var staleAction =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/api/supplies/decisions/" + workflowId + "/approve")
                        .withRequestBody(
                            new DecisionActionRequest(
                                "idem-approve-" + workflowId, "ops-supervisor", "too late"))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(staleAction.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(staleAction.getMessage().contains("decision is not waiting for approval"));
  }

  private akka.javasdk.http.StrictResponse<WorkflowResponse> postTelemetry(TelemetryRequest request) {
    return await(
        httpClient
            .POST("/api/supplies/telemetry")
            .withRequestBody(request)
            .responseBodyAs(WorkflowResponse.class)
            .invokeAsync());
  }

  private akka.javasdk.http.StrictResponse<DecisionRowsResponse> getPendingDecisions() {
    return await(
        httpClient
            .GET("/api/supplies/decisions/pending")
            .responseBodyAs(DecisionRowsResponse.class)
            .invokeAsync());
  }

  private akka.javasdk.http.StrictResponse<DecisionDetailResponse> getDecision(String decisionId) {
    return await(
        httpClient
            .GET("/api/supplies/decisions/" + decisionId)
            .responseBodyAs(DecisionDetailResponse.class)
            .invokeAsync());
  }

  private TelemetryRequest telemetry(
      String workflowId, int tonerPercent, int pagesSinceLastSupply, String lifecycleStatus) {
    var suffix = workflowId.substring("supply-".length());
    return new TelemetryRequest(
        workflowId,
        "idem-telemetry-" + suffix,
        "trace-supply-" + suffix,
        "corr-supply-" + suffix,
        "customer-" + suffix,
        "device-" + suffix,
        Instant.parse("2026-05-11T10:00:00Z"),
        tonerPercent,
        pagesSinceLastSupply,
        lifecycleStatus);
  }
}
