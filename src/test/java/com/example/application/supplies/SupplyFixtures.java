package com.example.application.supplies;

import com.example.domain.supplies.Supply.DeviceLifecycleStatus;
import com.example.domain.supplies.Supply.DeviceTelemetry;
import com.example.domain.supplies.Supply.SupplyDecisionCard;
import com.example.domain.supplies.Supply.SupplyTraceEvent;
import com.example.domain.supplies.Supply.TraceEventType;
import com.example.domain.supplies.Supply.TraceRef;
import java.time.Instant;
import java.util.List;

final class SupplyFixtures {

  private SupplyFixtures() {}

  static DeviceTelemetry telemetry(
      String suffix, DeviceLifecycleStatus lifecycleStatus, int tonerPercent, int pagesSinceLastSupply) {
    return new DeviceTelemetry(
        "idem-telemetry-" + suffix,
        new TraceRef("trace-supply-" + suffix, "corr-supply-" + suffix),
        "customer-" + suffix,
        "device-" + suffix,
        Instant.parse("2026-05-11T10:00:00Z"),
        tonerPercent,
        pagesSinceLastSupply,
        lifecycleStatus);
  }

  static SupplyDecisionCard decisionCard(
      String decisionId, DeviceLifecycleStatus lifecycleStatus, int tonerPercent, int pagesSinceLastSupply) {
    return new SupplyForecastAgent()
        .recommend(telemetry(decisionId, lifecycleStatus, tonerPercent, pagesSinceLastSupply), decisionId)
        .decisionCard();
  }

  static SupplyTraceEvent traceEvent(
      String suffix, TraceEventType type, String idempotencyKey, SupplyDecisionCard card) {
    return new SupplyTraceEvent(
        "trace-event-" + suffix,
        type,
        card.trace(),
        idempotencyKey,
        Instant.parse("2026-05-11T10:01:00Z"),
        actorFor(type),
        type.name().toLowerCase().replace('_', ' '),
        card.recommendation().policyClauses(),
        card.outcome());
  }

  static SupplyAutopilotWorkflow.State workflowState(
      String workflowId, SupplyAutopilotWorkflow.Status status, SupplyDecisionCard card) {
    return new SupplyAutopilotWorkflow.State(
        workflowId,
        workflowId,
        telemetry(workflowId, DeviceLifecycleStatus.ACTIVE, 12, 9_100),
        card,
        status,
        status == SupplyAutopilotWorkflow.Status.WAITING_FOR_APPROVAL,
        status == SupplyAutopilotWorkflow.Status.AUTO_SHIPMENT_PREPARED
            || status == SupplyAutopilotWorkflow.Status.APPROVED_SHIPMENT_PREPARED,
        status.name(),
        List.of("idem-telemetry-" + workflowId));
  }

  private static String actorFor(TraceEventType type) {
    return switch (type) {
      case RECOMMENDATION_CREATED -> "supply-forecast-agent";
      case APPROVAL_RECORDED, REJECTION_RECORDED -> "ops-supervisor";
      case STALE_DECISION_ESCALATED -> "supply-decision-timer";
      default -> "supply-autopilot-workflow";
    };
  }
}
