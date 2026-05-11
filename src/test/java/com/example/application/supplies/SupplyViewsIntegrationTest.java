package com.example.application.supplies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.supplies.Supply.DeviceLifecycleStatus;
import com.example.domain.supplies.Supply.TraceEventType;
import com.example.domain.supplies.SupplyDecision;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SupplyViewsIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withWorkflowIncomingMessages(SupplyAutopilotWorkflow.class)
        .withEventSourcedEntityIncomingMessages(SupplyDecisionEntity.class);
  }

  @Test
  void workflowStatePopulatesRiskSupervisionQueue() {
    IncomingMessages workflowUpdates = testKit.getWorkflowIncomingMessages(SupplyAutopilotWorkflow.class);
    var card = SupplyFixtures.decisionCard("view-risk-1", DeviceLifecycleStatus.ACTIVE, 12, 9_100);

    workflowUpdates.publish(
        SupplyFixtures.workflowState("view-risk-1", SupplyAutopilotWorkflow.Status.WAITING_FOR_APPROVAL, card),
        "view-risk-1");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(SupplyRiskView::getByStatus)
                      .invoke(new SupplyRiskView.FindByStatus("WAITING_FOR_APPROVAL", ""));

              assertEquals(1, result.risks().size());
              var row = result.risks().getFirst();
              assertEquals("view-risk-1", row.workflowId());
              assertEquals("device-view-risk-1", row.deviceId());
              assertEquals("REQUIRE_APPROVAL", row.proposedAction());
              assertTrue(row.staleDecisionTimerRequested());
              assertFalse(row.shipmentPrepared());
              assertEquals("trace-supply-view-risk-1", row.traceId());
            });
  }

  @Test
  void decisionEventsPopulatePendingDecisionAndTraceLookupViews() {
    IncomingMessages decisionEvents = testKit.getEventSourcedEntityIncomingMessages(SupplyDecisionEntity.class);
    var card = SupplyFixtures.decisionCard("view-decision-1", DeviceLifecycleStatus.ACTIVE, 12, 9_100);
    var openTrace =
        SupplyFixtures.traceEvent(
            "open-view-decision-1", TraceEventType.RECOMMENDATION_CREATED, "idem-open-view-decision-1", card);
    var staleTrace =
        SupplyFixtures.traceEvent(
            "stale-view-decision-1", TraceEventType.STALE_DECISION_ESCALATED, "idem-stale-view-decision-1", card);

    decisionEvents.publish(
        new SupplyDecision.Event.RecommendationOpened(card, openTrace, "idem-open-view-decision-1"),
        "view-decision-1");
    decisionEvents.publish(
        new SupplyDecision.Event.ApprovalRequired(openTrace, "idem-open-view-decision-1"),
        "view-decision-1");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var pending =
                  componentClient
                      .forView()
                      .method(PendingSupplyDecisionView::getByStatus)
                      .invoke(new PendingSupplyDecisionView.FindByStatus("APPROVAL_REQUIRED", ""));

              assertEquals(1, pending.decisions().size());
              var row = pending.decisions().getFirst();
              assertEquals("view-decision-1", row.decisionId());
              assertEquals("REQUIRE_APPROVAL", row.proposedAction());
              assertEquals(4, row.evidenceCount());
              assertTrue(row.policyClauseCount() >= 3);
              assertEquals("trace-supply-view-decision-1", row.traceId());
            });

    decisionEvents.publish(
        new SupplyDecision.Event.StaleEscalated(
            "supply-decision-timer", "approval SLA elapsed", staleTrace, "idem-stale-view-decision-1"),
        "view-decision-1");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var traces =
                  componentClient
                      .forView()
                      .method(SupplyTraceView::getByTraceId)
                      .invoke(new SupplyTraceView.FindByTraceId("trace-supply-view-decision-1"));

              assertEquals(1, traces.traces().size());
              var trace = traces.traces().getFirst();
              assertEquals("view-decision-1", trace.decisionId());
              assertEquals("STALE_ESCALATED", trace.status());
              assertEquals("corr-supply-view-decision-1", trace.correlationId());
              assertEquals(2, trace.events().size());
              assertTrue(
                  trace.events().stream()
                      .anyMatch(event -> event.type().equals("STALE_DECISION_ESCALATED")));
            });
  }
}
