package com.example.domain.supplies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.domain.supplies.Supply.DecisionAction;
import com.example.domain.supplies.Supply.DeviceLifecycleStatus;
import com.example.domain.supplies.Supply.EvidenceType;
import com.example.domain.supplies.Supply.SupplyDecisionCard;
import com.example.domain.supplies.Supply.SupplyEvidence;
import com.example.domain.supplies.Supply.SupplyItem;
import com.example.domain.supplies.Supply.SupplyObjective;
import com.example.domain.supplies.Supply.SupplyPolicyClauseRef;
import com.example.domain.supplies.Supply.SupplyRecommendation;
import com.example.domain.supplies.Supply.SupplyTraceEvent;
import com.example.domain.supplies.Supply.TraceEventType;
import com.example.domain.supplies.Supply.TraceRef;
import com.example.domain.supplies.Supply.OutcomeRef;
import com.example.domain.supplies.Supply.DeviceTelemetry;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class SupplyTest {

  private final TraceRef trace = new TraceRef("trace-supply-1", "corr-device-1");
  private final OutcomeRef outcome = new OutcomeRef("outcome-supply-1", "supply.fulfillment.timely");

  @Test
  void createsValidTelemetryAndDetectsDepletionRisk() {
    var telemetry =
        new DeviceTelemetry(
            "idem-telemetry-1",
            trace,
            "customer-1",
            "device-1",
            Instant.parse("2026-05-11T10:00:00Z"),
            12,
            3800,
            DeviceLifecycleStatus.ACTIVE);

    assertTrue(telemetry.indicatesDepletionRisk(20));
    assertFalse(telemetry.indicatesDepletionRisk(10));
  }

  @Test
  void validatesStablePolicyClauseIds() {
    var ref = new SupplyPolicyClauseRef("SUP-3.0", "High-cost replenishment requires review.");

    assertEquals("SUP-3.0", ref.clauseId());
    assertThrows(
        IllegalArgumentException.class,
        () -> new SupplyPolicyClauseRef("SUP-9.0", "Unknown future policy."));
  }

  @Test
  void validatesTraceCorrelationAndIdempotencyKeyFormats() {
    assertThrows(IllegalArgumentException.class, () -> new TraceRef("bad", "corr-device-1"));
    assertThrows(IllegalArgumentException.class, () -> new TraceRef("trace-ok", "bad"));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new DeviceTelemetry(
                "request-1",
                trace,
                "customer-1",
                "device-1",
                Instant.parse("2026-05-11T10:00:00Z"),
                10,
                100,
                DeviceLifecycleStatus.ACTIVE));
  }

  @Test
  void validatesRiskConfidenceAndPercentBounds() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SupplyEvidence(
                EvidenceType.FORECAST, "forecast-agent", "invalid confidence", 1.2, trace));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new DeviceTelemetry(
                "idem-telemetry-2",
                trace,
                "customer-1",
                "device-1",
                Instant.parse("2026-05-11T10:00:00Z"),
                101,
                100,
                DeviceLifecycleStatus.ACTIVE));
  }

  @Test
  void decisionCardCompletenessRequiresCoreEvidencePolicyAlternativesTraceAndOutcome() {
    var card = completeDecisionCard();

    assertTrue(card.isCompleteForReview());
    assertEquals(Supply.GOAL_SUPPLY_FULFILLMENT, card.objective().goalId());
    assertEquals("trace-supply-1", card.trace().traceId());
    assertEquals("outcome-supply-1", card.outcome().outcomeId());
  }

  @Test
  void decisionCardCompletenessFailsWhenEvidenceIsMissing() {
    var recommendation =
        new SupplyRecommendation(
            "rec-1",
            "idem-rec-1",
            new SupplyItem("toner-black", "Black toner", 1, 6900),
            0.91,
            0.88,
            6900,
            List.of(evidence(EvidenceType.TELEMETRY), evidence(EvidenceType.FORECAST)),
            List.of(new SupplyPolicyClauseRef("SUP-1.0", "Auto-ship active contracts only.")),
            "Toner depletion is forecast soon.",
            List.of("wait for next telemetry sample"));

    var card =
        new SupplyDecisionCard(
            "decision-1",
            SupplyObjective.goal02(),
            recommendation,
            DecisionAction.REQUIRE_APPROVAL,
            "medium risk",
            "prevents device downtime",
            trace,
            outcome);

    assertFalse(card.isCompleteForReview());
  }

  @Test
  void traceEventCanLinkPolicyAndOutcomeFacts() {
    var event =
        new SupplyTraceEvent(
            "event-1",
            TraceEventType.POLICY_INVOKED,
            trace,
            "idem-trace-1",
            Instant.parse("2026-05-11T10:01:00Z"),
            "supply-autopilot-workflow",
            "Applied supply auto-ship policy.",
            List.of(new SupplyPolicyClauseRef("SUP-1.0", "Auto-ship active contracts only.")),
            outcome);

    assertTrue(event.linksOutcome());
    assertEquals("corr-device-1", event.trace().correlationId());
    assertEquals("SUP-1.0", event.policyClauses().getFirst().clauseId());
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
            List.of(
                new SupplyPolicyClauseRef("SUP-1.0", "Auto-ship active contracts only."),
                new SupplyPolicyClauseRef("SUP-2.0", "Suppress offboarding customers.")),
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
}
