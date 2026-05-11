package com.example.application.supplies;

import com.example.domain.supplies.Supply;
import com.example.domain.supplies.Supply.DecisionAction;
import com.example.domain.supplies.Supply.DeviceLifecycleStatus;
import com.example.domain.supplies.Supply.DeviceTelemetry;
import com.example.domain.supplies.Supply.EvidenceType;
import com.example.domain.supplies.Supply.OutcomeRef;
import com.example.domain.supplies.Supply.SupplyDecisionCard;
import com.example.domain.supplies.Supply.SupplyEvidence;
import com.example.domain.supplies.Supply.SupplyItem;
import com.example.domain.supplies.Supply.SupplyObjective;
import com.example.domain.supplies.Supply.SupplyPolicyClauseRef;
import com.example.domain.supplies.Supply.SupplyRecommendation;
import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic specialist-agent stub for supply recommendations.
 *
 * <p>This reference stub only recommends and explains. It has no method that prepares shipments,
 * approves decisions, or changes policy; those authority transitions belong to
 * {@link SupplyAutopilotWorkflow} and {@link SupplyDecisionEntity}.
 */
public class SupplyForecastAgent {

  private static final long NORMAL_UNIT_COST_CENTS = 6_900;
  private static final long HIGH_COST_UNIT_COST_CENTS = 18_500;
  private static final int LOW_TONER_THRESHOLD = 25;

  private final SupplyForecastTools tools;

  public SupplyForecastAgent() {
    this(new SupplyForecastTools());
  }

  SupplyForecastAgent(SupplyForecastTools tools) {
    this.tools = tools;
  }

  public RecommendationDraft recommend(DeviceTelemetry telemetry, String decisionId) {
    var trace = telemetry.trace();
    var risk = tools.forecastDepletionRisk(telemetry.tonerPercent(), telemetry.pagesSinceLastSupply());
    var entitlement = tools.entitlementEvidence(telemetry.customerId(), telemetry.lifecycleStatus());
    var unitCost = telemetry.pagesSinceLastSupply() > 8_000 ? HIGH_COST_UNIT_COST_CENTS : NORMAL_UNIT_COST_CENTS;
    var item = new SupplyItem("toner-black", "Black toner", 1, unitCost);
    var inventory = tools.inventoryEvidence(item.sku());

    var evidence = new ArrayList<SupplyEvidence>();
    evidence.add(
        new SupplyEvidence(
            EvidenceType.TELEMETRY,
            "device-telemetry",
            "Toner is " + telemetry.tonerPercent() + "% after " + telemetry.pagesSinceLastSupply() + " pages.",
            0.97,
            trace));
    evidence.add(
        new SupplyEvidence(
            EvidenceType.FORECAST,
            "supply-forecast-agent",
            "Deterministic depletion risk is " + String.format("%.2f", risk) + ".",
            risk >= 0.45 ? 0.9 : 0.72,
            trace));
    evidence.add(
        new SupplyEvidence(
            EvidenceType.INVENTORY, inventory.source(), inventory.summary(), inventory.confidence(), trace));

    if (telemetry.lifecycleStatus() != DeviceLifecycleStatus.UNMAPPED_CONTRACT) {
      evidence.add(
          new SupplyEvidence(
              EvidenceType.ENTITLEMENT, entitlement.source(), entitlement.summary(), entitlement.confidence(), trace));
    }

    var clauses = policyClausesFor(telemetry, unitCost);
    var action = actionFor(telemetry, risk, unitCost, evidence);
    var recommendation =
        new SupplyRecommendation(
            "rec-" + decisionId,
            "idem-rec-" + decisionId,
            item,
            risk,
            confidenceFor(action, evidence),
            item.estimatedTotalCostCents(),
            evidence,
            clauses,
            rationaleFor(action, telemetry, risk),
            alternativesFor(action));

    var card =
        new SupplyDecisionCard(
            decisionId,
            SupplyObjective.goal02(),
            recommendation,
            action,
            riskSummaryFor(action, telemetry, risk),
            impactSummaryFor(action, item),
            trace,
            new OutcomeRef("outcome-" + decisionId, "supply.fulfillment.timely"));

    return new RecommendationDraft(card, action, !recommendation.hasRequiredDecisionEvidence());
  }

  private List<SupplyPolicyClauseRef> policyClausesFor(DeviceTelemetry telemetry, long unitCost) {
    var refs = new ArrayList<SupplyPolicyClauseRef>();
    refs.add(new SupplyPolicyClauseRef("SUP-1.0", "Auto-ship only for active supply contracts."));
    refs.add(new SupplyPolicyClauseRef("SUP-2.0", "Telemetry and forecast evidence are required."));
    if (unitCost >= HIGH_COST_UNIT_COST_CENTS) {
      refs.add(new SupplyPolicyClauseRef("SUP-3.0", "High-cost replenishment requires review."));
    }
    if (telemetry.lifecycleStatus() != DeviceLifecycleStatus.ACTIVE) {
      refs.add(new SupplyPolicyClauseRef("SUP-4.0", "Lifecycle exceptions block autonomous shipment."));
    }
    refs.add(new SupplyPolicyClauseRef("SUP-5.0", "Every recommendation must carry a trace and outcome link."));
    return List.copyOf(refs);
  }

  private DecisionAction actionFor(
      DeviceTelemetry telemetry, double risk, long unitCost, List<SupplyEvidence> evidence) {
    if (telemetry.lifecycleStatus() == DeviceLifecycleStatus.OFFBOARDING
        || telemetry.lifecycleStatus() == DeviceLifecycleStatus.SUSPENDED) {
      return DecisionAction.SUPPRESS_SHIPMENT;
    }
    if (telemetry.lifecycleStatus() == DeviceLifecycleStatus.UNMAPPED_CONTRACT
        || evidence.stream().noneMatch(item -> item.type() == EvidenceType.ENTITLEMENT)) {
      return DecisionAction.ESCALATE_EXCEPTION;
    }
    if (unitCost >= HIGH_COST_UNIT_COST_CENTS || telemetry.tonerPercent() <= 5 || risk >= 0.92) {
      return DecisionAction.REQUIRE_APPROVAL;
    }
    if (telemetry.indicatesDepletionRisk(LOW_TONER_THRESHOLD)) {
      return DecisionAction.AUTO_SHIP;
    }
    return DecisionAction.ESCALATE_EXCEPTION;
  }

  private double confidenceFor(DecisionAction action, List<SupplyEvidence> evidence) {
    if (evidence.stream().noneMatch(item -> item.type() == EvidenceType.ENTITLEMENT)) {
      return 0.48;
    }
    return action == DecisionAction.AUTO_SHIP ? 0.91 : 0.86;
  }

  private String rationaleFor(DecisionAction action, DeviceTelemetry telemetry, double risk) {
    return switch (action) {
      case AUTO_SHIP -> "Active contract and in-stock supply with depletion risk " + String.format("%.2f", risk) + ".";
      case REQUIRE_APPROVAL -> "Policy threshold requires human review before replenishment.";
      case SUPPRESS_SHIPMENT -> "Lifecycle status " + telemetry.lifecycleStatus() + " blocks autonomous shipment.";
      case ESCALATE_EXCEPTION -> "Evidence or contract mapping is insufficient for autonomous shipment.";
      default -> "Recommendation is advisory only.";
    };
  }

  private List<String> alternativesFor(DecisionAction action) {
    return switch (action) {
      case AUTO_SHIP -> List.of("wait for next telemetry sample", "route to manual review");
      case REQUIRE_APPROVAL -> List.of("approve one shipment", "reject until additional evidence arrives");
      case SUPPRESS_SHIPMENT -> List.of("keep shipment suppressed", "reopen after lifecycle status changes");
      case ESCALATE_EXCEPTION -> List.of("map the customer contract", "ask operations for entitlement evidence");
      default -> List.of("no-op", "manual review");
    };
  }

  private String riskSummaryFor(DecisionAction action, DeviceTelemetry telemetry, double risk) {
    return action == DecisionAction.AUTO_SHIP
        ? "low policy risk: active contract and normal-cost replenishment"
        : "policy review risk: " + telemetry.lifecycleStatus() + " with depletion risk " + String.format("%.2f", risk);
  }

  private String impactSummaryFor(DecisionAction action, SupplyItem item) {
    return action == DecisionAction.SUPPRESS_SHIPMENT
        ? "prevents unauthorized shipment while preserving trace evidence"
        : "prevents device downtime with " + item.quantity() + " " + item.name() + " shipment";
  }

  public record RecommendationDraft(
      SupplyDecisionCard decisionCard, DecisionAction recommendedAction, boolean missingRequiredEvidence) {}
}
