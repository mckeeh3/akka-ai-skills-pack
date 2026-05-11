package com.example.application.supplies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import com.example.api.supplies.SupplyAutopilotEndpoint.DecisionActionRequest;
import com.example.api.supplies.SupplyAutopilotEndpoint.DecisionDetailResponse;
import com.example.api.supplies.SupplyAutopilotEndpoint.TelemetryRequest;
import com.example.api.supplies.SupplyAutopilotEndpoint.TraceLookupResponse;
import com.example.api.supplies.SupplyAutopilotEndpoint.WorkflowResponse;
import com.example.domain.supplies.Supply;
import com.example.domain.supplies.Supply.DeviceLifecycleStatus;
import com.example.domain.supplies.Supply.EvidenceType;
import com.example.domain.supplies.Supply.TraceEventType;
import com.example.domain.supplies.SupplyDecision;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SupplySliceAcceptanceIntegrationTest extends TestKitSupport {

  @Test
  void autoShipmentPreservesAuthorityTraceOutcomeAndIdempotencyAcrossTheSlice() {
    var workflowId = "supply-accept-auto-1";
    var telemetry = telemetry(workflowId, DeviceLifecycleStatus.ACTIVE, 18, 4_000);

    var start = postTelemetry(telemetry).body();
    assertEquals(workflowId, start.workflowId());
    assertEquals("trace-accept-auto-1", start.traceId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var workflow = getWorkflow(workflowId);
              var decision = getDecision(workflowId);

              assertEquals(SupplyAutopilotWorkflow.Status.AUTO_SHIPMENT_PREPARED, workflow.status());
              assertTrue(workflow.shipmentPrepared());
              assertEquals(SupplyDecision.Status.SHIPMENT_PREPARED, decision.status());
              assertAiFirstDecisionComplete(decision);
              assertTraceHas(decision, TraceEventType.RECOMMENDATION_CREATED, TraceEventType.SHIPMENT_PREPARED);
              assertFalse(traceHas(decision, TraceEventType.APPROVAL_RECORDED));
            });

    var eventCount = getDecision(workflowId).traceEvents().size();
    var duplicate = postTelemetry(telemetry).body();
    assertEquals("AUTO_SHIPMENT_PREPARED", duplicate.status());
    assertTrue(duplicate.shipmentPrepared());
    assertEquals(eventCount, getDecision(workflowId).traceEvents().size());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var trace = getTrace("trace-accept-auto-1").body();
              assertEquals(workflowId, trace.decisionId());
              assertEquals("SHIPMENT_PREPARED", trace.status());
              assertEquals("corr-accept-auto-1", trace.correlationId());
              assertFalse(trace.outcomeId().isBlank());
              assertTrue(trace.events().stream().anyMatch(event -> event.type().equals("RECOMMENDATION_CREATED")));
              assertTrue(trace.events().stream().anyMatch(event -> event.type().equals("SHIPMENT_PREPARED")));
              assertTrue(trace.events().stream().allMatch(event -> !event.outcomeId().isBlank()));
            });
  }

  @Test
  void highCostDecisionRequiresHumanApprovalBeforeShipmentAndExposesDecisionCardEvidence() {
    var workflowId = "supply-accept-approval-1";
    postTelemetry(telemetry(workflowId, DeviceLifecycleStatus.ACTIVE, 12, 9_100));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var workflow = getWorkflow(workflowId);
              var decision = getDecision(workflowId);
              assertEquals(SupplyAutopilotWorkflow.Status.WAITING_FOR_APPROVAL, workflow.status());
              assertTrue(workflow.staleDecisionTimerRequested());
              assertFalse(workflow.shipmentPrepared());
              assertEquals(SupplyDecision.Status.APPROVAL_REQUIRED, decision.status());
              assertAiFirstDecisionComplete(decision);
              assertTrue(hasPolicy(decision, "SUP-3.0"));
              assertFalse(traceHas(decision, TraceEventType.SHIPMENT_PREPARED));
            });

    var detail = getDecisionDetail(workflowId).body();
    assertEquals("APPROVAL_REQUIRED", detail.status());
    assertDecisionCardResponseComplete(detail);

    approve(workflowId, "idem-approve-" + workflowId, "ops-supervisor", "approved to prevent downtime");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var workflow = getWorkflow(workflowId);
              var decision = getDecision(workflowId);
              assertEquals(SupplyAutopilotWorkflow.Status.APPROVED_SHIPMENT_PREPARED, workflow.status());
              assertTrue(workflow.shipmentPrepared());
              assertEquals(SupplyDecision.Status.SHIPMENT_PREPARED, decision.status());
              assertTraceHas(decision, TraceEventType.APPROVAL_RECORDED, TraceEventType.SHIPMENT_PREPARED);
              assertTrue(
                  decision.traceEvents().stream()
                      .filter(event -> event.type() == TraceEventType.APPROVAL_RECORDED)
                      .anyMatch(event -> event.actor().equals("ops-supervisor")));
            });
  }

  @Test
  void rejectionSuppressionAndMissingEvidenceTerminateSafelyWithoutShipmentSideEffects() {
    var rejectedId = "supply-accept-reject-1";
    postTelemetry(telemetry(rejectedId, DeviceLifecycleStatus.ACTIVE, 12, 9_100));
    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> assertEquals(SupplyDecision.Status.APPROVAL_REQUIRED, getDecision(rejectedId).status()));

    reject(rejectedId, "idem-reject-" + rejectedId, "ops-supervisor", "reject until replacement window");
    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var workflow = getWorkflow(rejectedId);
              var decision = getDecision(rejectedId);
              assertEquals(SupplyAutopilotWorkflow.Status.REJECTED, workflow.status());
              assertFalse(workflow.shipmentPrepared());
              assertEquals(SupplyDecision.Status.REJECTED, decision.status());
              assertTraceHas(decision, TraceEventType.REJECTION_RECORDED);
              assertFalse(traceHas(decision, TraceEventType.SHIPMENT_PREPARED));
            });

    var suppressedId = "supply-accept-suppress-1";
    postTelemetry(telemetry(suppressedId, DeviceLifecycleStatus.OFFBOARDING, 15, 4_000));
    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var workflow = getWorkflow(suppressedId);
              var decision = getDecision(suppressedId);
              assertEquals(SupplyAutopilotWorkflow.Status.SUPPRESSED, workflow.status());
              assertFalse(workflow.shipmentPrepared());
              assertEquals(SupplyDecision.Status.SUPPRESSED, decision.status());
              assertTrue(hasPolicy(decision, "SUP-4.0"));
              assertTraceHas(decision, TraceEventType.SHIPMENT_SUPPRESSED);
              assertFalse(traceHas(decision, TraceEventType.SHIPMENT_PREPARED));
            });

    var missingEvidenceId = "supply-accept-missing-1";
    postTelemetry(telemetry(missingEvidenceId, DeviceLifecycleStatus.UNMAPPED_CONTRACT, 14, 4_000));
    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var workflow = getWorkflow(missingEvidenceId);
              var decision = getDecision(missingEvidenceId);
              assertEquals(SupplyAutopilotWorkflow.Status.EVIDENCE_MISSING, workflow.status());
              assertFalse(workflow.shipmentPrepared());
              assertNotNull(workflow.decisionCard());
              assertFalse(workflow.decisionCard().recommendation().hasRequiredDecisionEvidence());
              assertEquals(SupplyDecision.Status.EMPTY, decision.status());
              assertTrue(workflow.decisionCard().recommendation().hasEvidence(EvidenceType.TELEMETRY));
              assertTrue(hasPolicy(workflow.decisionCard(), "SUP-4.0"));
            });
  }

  @Test
  void staleDecisionEscalationRecordsOneAuditFactAndNoDuplicateSideEffects() {
    var workflowId = "supply-accept-stale-1";
    postTelemetry(telemetry(workflowId, DeviceLifecycleStatus.ACTIVE, 12, 9_100));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> assertEquals(SupplyAutopilotWorkflow.Status.WAITING_FOR_APPROVAL, getWorkflow(workflowId).status()));

    var stale =
        new SupplyAutopilotWorkflow.EscalateStaleDecision(
            "idem-stale-" + workflowId, "supply-decision-timer", "approval SLA elapsed");
    componentClient.forWorkflow(workflowId).method(SupplyAutopilotWorkflow::escalateStale).invoke(stale);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var workflow = getWorkflow(workflowId);
              var decision = getDecision(workflowId);
              assertEquals(SupplyAutopilotWorkflow.Status.STALE_ESCALATED, workflow.status());
              assertFalse(workflow.shipmentPrepared());
              assertEquals(SupplyDecision.Status.STALE_ESCALATED, decision.status());
              assertTraceHas(decision, TraceEventType.STALE_DECISION_ESCALATED);
              assertFalse(traceHas(decision, TraceEventType.SHIPMENT_PREPARED));
            });

    var eventCount = getDecision(workflowId).traceEvents().size();
    var duplicate =
        componentClient.forWorkflow(workflowId).method(SupplyAutopilotWorkflow::escalateStale).invoke(stale);
    assertEquals(SupplyAutopilotWorkflow.Status.STALE_ESCALATED, duplicate.status());
    assertEquals(eventCount, getDecision(workflowId).traceEvents().size());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var trace = getTrace("trace-accept-stale-1").body();
              assertEquals("STALE_ESCALATED", trace.status());
              assertTrue(trace.events().stream().anyMatch(event -> event.type().equals("STALE_DECISION_ESCALATED")));
              assertTrue(trace.events().stream().noneMatch(event -> event.type().equals("SHIPMENT_PREPARED")));
            });
  }

  private akka.javasdk.http.StrictResponse<WorkflowResponse> postTelemetry(TelemetryRequest request) {
    return await(
        httpClient
            .POST("/api/supplies/telemetry")
            .withRequestBody(request)
            .responseBodyAs(WorkflowResponse.class)
            .invokeAsync());
  }

  private akka.javasdk.http.StrictResponse<DecisionDetailResponse> getDecisionDetail(String decisionId) {
    return await(
        httpClient
            .GET("/api/supplies/decisions/" + decisionId)
            .responseBodyAs(DecisionDetailResponse.class)
            .invokeAsync());
  }

  private akka.javasdk.http.StrictResponse<TraceLookupResponse> getTrace(String traceId) {
    return await(
        httpClient
            .GET("/api/supplies/traces/" + traceId)
            .responseBodyAs(TraceLookupResponse.class)
            .invokeAsync());
  }

  private void approve(String decisionId, String idempotencyKey, String actor, String rationale) {
    await(
        httpClient
            .POST("/api/supplies/decisions/" + decisionId + "/approve")
            .withRequestBody(new DecisionActionRequest(idempotencyKey, actor, rationale))
            .responseBodyAs(WorkflowResponse.class)
            .invokeAsync());
  }

  private void reject(String decisionId, String idempotencyKey, String actor, String rationale) {
    await(
        httpClient
            .POST("/api/supplies/decisions/" + decisionId + "/reject")
            .withRequestBody(new DecisionActionRequest(idempotencyKey, actor, rationale))
            .responseBodyAs(WorkflowResponse.class)
            .invokeAsync());
  }

  private SupplyAutopilotWorkflow.State getWorkflow(String workflowId) {
    return componentClient.forWorkflow(workflowId).method(SupplyAutopilotWorkflow::get).invoke();
  }

  private SupplyDecision.State getDecision(String decisionId) {
    return componentClient
        .forEventSourcedEntity(decisionId)
        .method(SupplyDecisionEntity::getDecision)
        .invoke();
  }

  private TelemetryRequest telemetry(
      String workflowId, DeviceLifecycleStatus lifecycleStatus, int tonerPercent, int pagesSinceLastSupply) {
    var suffix = workflowId.substring("supply-".length());
    return new TelemetryRequest(
        workflowId,
        "idem-telemetry-" + suffix,
        "trace-" + suffix,
        "corr-" + suffix,
        "customer-" + suffix,
        "device-" + suffix,
        Instant.parse("2026-05-11T10:00:00Z"),
        tonerPercent,
        pagesSinceLastSupply,
        lifecycleStatus.name());
  }

  private void assertAiFirstDecisionComplete(SupplyDecision.State decision) {
    assertNotNull(decision.card());
    assertEquals(Supply.GOAL_SUPPLY_FULFILLMENT, decision.card().objective().goalId());
    assertTrue(decision.card().isCompleteForReview());
    assertFalse(decision.card().trace().traceId().isBlank());
    assertFalse(decision.card().outcome().outcomeId().isBlank());
    assertTrue(decision.outcomes().stream().anyMatch(outcome -> outcome.outcomeId().equals(decision.card().outcome().outcomeId())));
    assertTrue(decision.card().recommendation().hasEvidence(EvidenceType.TELEMETRY));
    assertTrue(decision.card().recommendation().hasEvidence(EvidenceType.FORECAST));
    assertTrue(decision.card().recommendation().hasEvidence(EvidenceType.INVENTORY));
    assertTrue(decision.card().recommendation().hasEvidence(EvidenceType.ENTITLEMENT));
    assertTrue(decision.card().recommendation().policyClauses().stream().anyMatch(clause -> clause.clauseId().equals("SUP-5.0")));
    assertTrue(
        decision.card().recommendation().policyClauses().stream()
            .allMatch(clause -> Supply.STABLE_POLICY_CLAUSE_IDS.contains(clause.clauseId())));
    assertTrue(
        decision.traceEvents().stream()
            .allMatch(event -> event.trace().equals(decision.card().trace()) && event.linksOutcome()));
  }

  private void assertDecisionCardResponseComplete(DecisionDetailResponse detail) {
    assertEquals(Supply.GOAL_SUPPLY_FULFILLMENT, detail.objectiveId());
    assertFalse(detail.traceId().isBlank());
    assertFalse(detail.correlationId().isBlank());
    assertFalse(detail.outcomeId().isBlank());
    assertFalse(detail.riskSummary().isBlank());
    assertFalse(detail.impactSummary().isBlank());
    assertFalse(detail.alternatives().isEmpty());
    assertEquals(
        Set.of("TELEMETRY", "FORECAST", "INVENTORY", "ENTITLEMENT"),
        detail.evidence().stream().map(evidence -> evidence.type()).collect(java.util.stream.Collectors.toSet()));
    assertTrue(detail.policyClauses().stream().anyMatch(clause -> clause.clauseId().equals("SUP-5.0")));
    assertTrue(detail.traceEvents().stream().allMatch(event -> !event.outcomeId().isBlank()));
  }

  private void assertTraceHas(SupplyDecision.State decision, TraceEventType... types) {
    for (var type : types) {
      assertTrue(traceHas(decision, type), "missing trace event " + type);
    }
  }

  private boolean traceHas(SupplyDecision.State decision, TraceEventType type) {
    return decision.traceEvents().stream().anyMatch(event -> event.type() == type);
  }

  private boolean hasPolicy(SupplyDecision.State decision, String clauseId) {
    return decision.card() != null && hasPolicy(decision.card(), clauseId);
  }

  private boolean hasPolicy(Supply.SupplyDecisionCard card, String clauseId) {
    return card.recommendation().policyClauses().stream().anyMatch(clause -> clause.clauseId().equals(clauseId));
  }
}
