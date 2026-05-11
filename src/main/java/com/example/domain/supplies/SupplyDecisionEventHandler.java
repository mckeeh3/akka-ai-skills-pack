package com.example.domain.supplies;

import com.example.domain.supplies.SupplyDecision.Event;
import com.example.domain.supplies.SupplyDecision.State;
import com.example.domain.supplies.SupplyDecision.Status;

/** Pure replay logic for supplies decision events. */
public final class SupplyDecisionEventHandler {

  private SupplyDecisionEventHandler() {}

  public static State apply(State state, Event event) {
    return switch (event) {
      case Event.RecommendationOpened opened ->
          state.withOpened(opened.card(), opened.traceEvent(), opened.idempotencyKey());
      case Event.ApprovalRequired approvalRequired ->
          state.withStatus(
              Status.APPROVAL_REQUIRED,
              approvalRequired.traceEvent(),
              approvalRequired.idempotencyKey());
      case Event.Approved approved ->
          state.withStatus(Status.APPROVED, approved.traceEvent(), approved.idempotencyKey());
      case Event.Rejected rejected ->
          state.withStatus(Status.REJECTED, rejected.traceEvent(), rejected.idempotencyKey());
      case Event.Suppressed suppressed ->
          state.withStatus(Status.SUPPRESSED, suppressed.traceEvent(), suppressed.idempotencyKey());
      case Event.ShipmentPrepared shipmentPrepared ->
          state.withStatus(
              Status.SHIPMENT_PREPARED,
              shipmentPrepared.traceEvent(),
              shipmentPrepared.idempotencyKey());
      case Event.StaleEscalated staleEscalated ->
          state.withStatus(
              Status.STALE_ESCALATED,
              staleEscalated.traceEvent(),
              staleEscalated.idempotencyKey());
      case Event.OutcomeLinked outcomeLinked ->
          state.withOutcomeLinked(
              outcomeLinked.outcome(), outcomeLinked.traceEvent(), outcomeLinked.idempotencyKey());
    };
  }
}
