package com.example.domain.supplies;

import com.example.domain.supplies.Supply.DecisionAction;
import com.example.domain.supplies.SupplyDecision.Command;
import com.example.domain.supplies.SupplyDecision.Event;
import com.example.domain.supplies.SupplyDecision.State;
import java.util.List;

/** Pure command-to-event decisions for the supplies decision write model. */
public final class SupplyDecisionCommandHandler {

  private SupplyDecisionCommandHandler() {}

  public static List<Event> onCommand(State state, Command.OpenRecommendation command) {
    if (state.processed(command.idempotencyKey())) {
      return List.of();
    }
    var opened =
        new Event.RecommendationOpened(command.card(), command.traceEvent(), command.idempotencyKey());
    if (command.card().proposedAction() == DecisionAction.REQUIRE_APPROVAL) {
      return List.of(
          opened,
          new Event.ApprovalRequired(command.traceEvent(), command.idempotencyKey()));
    }
    return List.of(opened);
  }

  public static List<Event> onCommand(State state, Command.RequireApproval command) {
    if (state.processed(command.idempotencyKey()) || state.status() == SupplyDecision.Status.APPROVAL_REQUIRED) {
      return List.of();
    }
    return List.of(new Event.ApprovalRequired(command.traceEvent(), command.idempotencyKey()));
  }

  public static List<Event> onCommand(State state, Command.Approve command) {
    if (state.processed(command.idempotencyKey()) || state.status() == SupplyDecision.Status.APPROVED) {
      return List.of();
    }
    return List.of(
        new Event.Approved(
            command.actor(), command.rationale(), command.traceEvent(), command.idempotencyKey()));
  }

  public static List<Event> onCommand(State state, Command.Reject command) {
    if (state.processed(command.idempotencyKey()) || state.status() == SupplyDecision.Status.REJECTED) {
      return List.of();
    }
    return List.of(
        new Event.Rejected(
            command.actor(), command.rationale(), command.traceEvent(), command.idempotencyKey()));
  }

  public static List<Event> onCommand(State state, Command.Suppress command) {
    if (state.processed(command.idempotencyKey()) || state.status() == SupplyDecision.Status.SUPPRESSED) {
      return List.of();
    }
    return List.of(
        new Event.Suppressed(
            command.actor(), command.rationale(), command.traceEvent(), command.idempotencyKey()));
  }

  public static List<Event> onCommand(State state, Command.PrepareShipment command) {
    if (state.processed(command.idempotencyKey())
        || state.status() == SupplyDecision.Status.SHIPMENT_PREPARED) {
      return List.of();
    }
    return List.of(
        new Event.ShipmentPrepared(
            command.actor(), command.rationale(), command.traceEvent(), command.idempotencyKey()));
  }

  public static List<Event> onCommand(State state, Command.EscalateStale command) {
    if (state.processed(command.idempotencyKey())
        || state.status() == SupplyDecision.Status.STALE_ESCALATED) {
      return List.of();
    }
    return List.of(
        new Event.StaleEscalated(
            command.actor(), command.rationale(), command.traceEvent(), command.idempotencyKey()));
  }

  public static List<Event> onCommand(State state, Command.LinkOutcome command) {
    if (state.processed(command.idempotencyKey()) || state.outcome(command.outcome().outcomeId()).isPresent()) {
      return List.of();
    }
    return List.of(
        new Event.OutcomeLinked(command.outcome(), command.traceEvent(), command.idempotencyKey()));
  }
}
