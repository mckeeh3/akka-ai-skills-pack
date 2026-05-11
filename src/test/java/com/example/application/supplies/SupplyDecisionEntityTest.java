package com.example.application.supplies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventSourcedTestKit;
import com.example.domain.supplies.Supply;
import com.example.domain.supplies.Supply.DecisionAction;
import com.example.domain.supplies.Supply.EvidenceType;
import com.example.domain.supplies.Supply.OutcomeRef;
import com.example.domain.supplies.Supply.SupplyDecisionCard;
import com.example.domain.supplies.Supply.SupplyEvidence;
import com.example.domain.supplies.Supply.SupplyItem;
import com.example.domain.supplies.Supply.SupplyObjective;
import com.example.domain.supplies.Supply.SupplyPolicyClauseRef;
import com.example.domain.supplies.Supply.SupplyRecommendation;
import com.example.domain.supplies.Supply.SupplyTraceEvent;
import com.example.domain.supplies.Supply.TraceEventType;
import com.example.domain.supplies.Supply.TraceRef;
import com.example.domain.supplies.SupplyDecision;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class SupplyDecisionEntityTest {

  private final TraceRef trace = new TraceRef("trace-decision-1", "corr-device-1");
  private final OutcomeRef outcome = new OutcomeRef("outcome-supply-1", "supply.fulfillment.timely");

  private EventSourcedTestKit<SupplyDecision.State, SupplyDecision.Event, SupplyDecisionEntity>
      newTestKit(String decisionId) {
    return EventSourcedTestKit.of(decisionId, SupplyDecisionEntity::new);
  }

  @Test
  void openRecommendationPersistsRecommendationAndApprovalRequiredEvents() {
    var testKit = newTestKit("decision-1");

    var result =
        testKit
            .method(SupplyDecisionEntity::openRecommendation)
            .invoke(
                new SupplyDecision.Command.OpenRecommendation(
                    "idem-open-1", completeDecisionCard(), traceEvent("trace-event-open-1", TraceEventType.RECOMMENDATION_CREATED, "idem-open-1")));

    assertEquals(SupplyDecision.Status.APPROVAL_REQUIRED, result.getReply().status());
    assertEquals(
        "decision-1",
        result.getNextEventOfType(SupplyDecision.Event.RecommendationOpened.class).card().decisionId());
    assertEquals(
        TraceEventType.RECOMMENDATION_CREATED,
        result.getNextEventOfType(SupplyDecision.Event.ApprovalRequired.class).traceEvent().type());
    assertEquals(2, result.getAllEvents().size());
    assertEquals(outcome, testKit.getState().outcomes().getFirst());
  }

  @Test
  void duplicateOpenRecommendationIsNoOp() {
    var testKit = newTestKit("decision-1");
    var command =
        new SupplyDecision.Command.OpenRecommendation(
            "idem-open-1", completeDecisionCard(), traceEvent("trace-event-open-1", TraceEventType.RECOMMENDATION_CREATED, "idem-open-1"));
    testKit.method(SupplyDecisionEntity::openRecommendation).invoke(command);

    var duplicate = testKit.method(SupplyDecisionEntity::openRecommendation).invoke(command);

    assertFalse(duplicate.didPersistEvents());
    assertEquals(SupplyDecision.Status.APPROVAL_REQUIRED, duplicate.getReply().status());
  }

  @Test
  void approvalRequiresActorRationaleAndPendingApproval() {
    var testKit = newTestKit("decision-1");
    openDecision(testKit);

    var result =
        testKit
            .method(SupplyDecisionEntity::approve)
            .invoke(
                new SupplyDecision.Command.Approve(
                    "idem-approve-1",
                    " ",
                    "approved to prevent downtime",
                    traceEvent("trace-event-approve-1", TraceEventType.APPROVAL_RECORDED, "idem-approve-1")));

    assertTrue(result.isError());
    assertTrue(result.getError().contains("actor is required"));
  }

  @Test
  void approveThenPrepareShipmentReconstructsTraceAndFinalState() {
    var testKit = newTestKit("decision-1");
    openDecision(testKit);

    var approved =
        testKit
            .method(SupplyDecisionEntity::approve)
            .invoke(
                new SupplyDecision.Command.Approve(
                    "idem-approve-1",
                    "ops-supervisor",
                    "policy evidence is sufficient",
                    traceEvent("trace-event-approve-1", TraceEventType.APPROVAL_RECORDED, "idem-approve-1")));
    var shipped =
        testKit
            .method(SupplyDecisionEntity::prepareShipment)
            .invoke(
                new SupplyDecision.Command.PrepareShipment(
                    "idem-ship-1",
                    "supply-autopilot-workflow",
                    "approved shipment prepared",
                    traceEvent("trace-event-ship-1", TraceEventType.SHIPMENT_PREPARED, "idem-ship-1")));

    assertEquals(SupplyDecision.Status.APPROVED, approved.getReply().status());
    assertEquals(SupplyDecision.Status.SHIPMENT_PREPARED, shipped.getReply().status());
    assertEquals(SupplyDecision.Status.EMPTY, SupplyDecision.State.empty("decision-1").status());
    assertEquals(SupplyDecision.Status.SHIPMENT_PREPARED, testKit.getState().status());
    assertEquals(4, testKit.getState().traceEvents().size());
  }

  @Test
  void staleDecisionEscalationPersistsAuditFactWithoutTerminalShipment() {
    var testKit = newTestKit("decision-1");
    openDecision(testKit);

    var result =
        testKit
            .method(SupplyDecisionEntity::escalateStale)
            .invoke(
                new SupplyDecision.Command.EscalateStale(
                    "idem-stale-1",
                    "supply-decision-timer",
                    "approval SLA elapsed",
                    traceEvent("trace-event-stale-1", TraceEventType.STALE_DECISION_ESCALATED, "idem-stale-1")));

    assertEquals(SupplyDecision.Status.STALE_ESCALATED, result.getReply().status());
    assertEquals(
        TraceEventType.STALE_DECISION_ESCALATED,
        result.getNextEventOfType(SupplyDecision.Event.StaleEscalated.class).traceEvent().type());
    assertFalse(testKit.getState().isTerminal());
  }

  @Test
  void suppressRequiresPolicyClauseCitation() {
    var testKit = newTestKit("decision-1");
    openDecision(testKit);

    var result =
        testKit
            .method(SupplyDecisionEntity::suppress)
            .invoke(
                new SupplyDecision.Command.Suppress(
                    "idem-suppress-1",
                    "ops-supervisor",
                    "offboarding customer",
                    traceEventWithoutPolicy("trace-event-suppress-1", TraceEventType.SHIPMENT_SUPPRESSED, "idem-suppress-1")));

    assertTrue(result.isError());
    assertTrue(result.getError().contains("must cite policy clauses"));
  }

  @Test
  void linkOutcomeIsDurableAndIdempotent() {
    var testKit = newTestKit("decision-1");
    openDecision(testKit);
    var linked =
        testKit
            .method(SupplyDecisionEntity::linkOutcome)
            .invoke(
                new SupplyDecision.Command.LinkOutcome(
                    "idem-outcome-1",
                    new OutcomeRef("outcome-supply-2", "supply.approval.latency"),
                    new SupplyTraceEvent(
                        "trace-event-outcome-1",
                        TraceEventType.OUTCOME_LINKED,
                        trace,
                        "idem-outcome-1",
                        Instant.parse("2026-05-11T10:04:00Z"),
                        "supply-autopilot-workflow",
                        "Linked approval latency outcome.",
                        policyRefs(),
                        new OutcomeRef("outcome-supply-2", "supply.approval.latency"))));

    var duplicate =
        testKit
            .method(SupplyDecisionEntity::linkOutcome)
            .invoke(
                new SupplyDecision.Command.LinkOutcome(
                    "idem-outcome-1",
                    new OutcomeRef("outcome-supply-2", "supply.approval.latency"),
                    new SupplyTraceEvent(
                        "trace-event-outcome-1",
                        TraceEventType.OUTCOME_LINKED,
                        trace,
                        "idem-outcome-1",
                        Instant.parse("2026-05-11T10:04:00Z"),
                        "supply-autopilot-workflow",
                        "Linked approval latency outcome.",
                        policyRefs(),
                        new OutcomeRef("outcome-supply-2", "supply.approval.latency"))));

    assertEquals(2, linked.getReply().outcomes().size());
    assertFalse(duplicate.didPersistEvents());
    assertEquals(2, duplicate.getReply().outcomes().size());
  }

  private void openDecision(
      EventSourcedTestKit<SupplyDecision.State, SupplyDecision.Event, SupplyDecisionEntity> testKit) {
    testKit
        .method(SupplyDecisionEntity::openRecommendation)
        .invoke(
            new SupplyDecision.Command.OpenRecommendation(
                "idem-open-1", completeDecisionCard(), traceEvent("trace-event-open-1", TraceEventType.RECOMMENDATION_CREATED, "idem-open-1")));
  }

  private SupplyDecisionCard completeDecisionCard() {
    var recommendation =
        new SupplyRecommendation(
            "rec-1",
            "idem-rec-1",
            new SupplyItem("toner-black", "Black toner", 1, 6900),
            0.91,
            0.88,
            6900,
            List.of(
                evidence(EvidenceType.TELEMETRY),
                evidence(EvidenceType.FORECAST),
                evidence(EvidenceType.INVENTORY),
                evidence(EvidenceType.ENTITLEMENT)),
            policyRefs(),
            "Toner depletion is forecast before the next service window.",
            List.of("wait for next telemetry sample", "route to manual review"));

    return new SupplyDecisionCard(
        "decision-1",
        SupplyObjective.goal02(),
        recommendation,
        DecisionAction.REQUIRE_APPROVAL,
        "medium risk: abnormal usage is not detected",
        "prevents device downtime with one toner shipment",
        trace,
        outcome);
  }

  private SupplyEvidence evidence(EvidenceType type) {
    return new SupplyEvidence(type, type.name().toLowerCase(), "evidence for " + type, 0.9, trace);
  }

  private SupplyTraceEvent traceEvent(String eventId, TraceEventType type, String idempotencyKey) {
    return new SupplyTraceEvent(
        eventId,
        type,
        trace,
        idempotencyKey,
        Instant.parse("2026-05-11T10:02:00Z"),
        "supply-autopilot-workflow",
        "Recorded " + type,
        policyRefs(),
        outcome);
  }

  private SupplyTraceEvent traceEventWithoutPolicy(String eventId, TraceEventType type, String idempotencyKey) {
    return new SupplyTraceEvent(
        eventId,
        type,
        trace,
        idempotencyKey,
        Instant.parse("2026-05-11T10:02:00Z"),
        "supply-autopilot-workflow",
        "Recorded " + type,
        List.of(),
        outcome);
  }

  private List<SupplyPolicyClauseRef> policyRefs() {
    return List.of(
        new SupplyPolicyClauseRef("SUP-1.0", "Auto-ship active contracts only."),
        new SupplyPolicyClauseRef("SUP-3.0", "High-cost replenishment requires review."));
  }
}
