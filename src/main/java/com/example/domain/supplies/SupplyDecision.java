package com.example.domain.supplies;

import akka.javasdk.annotations.TypeName;
import com.example.domain.supplies.Supply.DecisionAction;
import com.example.domain.supplies.Supply.OutcomeRef;
import com.example.domain.supplies.Supply.SupplyDecisionCard;
import com.example.domain.supplies.Supply.SupplyTraceEvent;
import com.example.domain.supplies.Supply.TraceEventType;
import java.util.List;
import java.util.Optional;

/** Event-sourced write model vocabulary for an audit-grade supplies decision. */
public final class SupplyDecision {

  private SupplyDecision() {}

  public enum Status {
    EMPTY,
    OPEN,
    APPROVAL_REQUIRED,
    APPROVED,
    REJECTED,
    SUPPRESSED,
    SHIPMENT_PREPARED,
    STALE_ESCALATED
  }

  public record State(
      String decisionId,
      Status status,
      SupplyDecisionCard card,
      List<SupplyTraceEvent> traceEvents,
      List<OutcomeRef> outcomes,
      List<String> processedIdempotencyKeys) {

    public State {
      traceEvents = List.copyOf(traceEvents == null ? List.of() : traceEvents);
      outcomes = List.copyOf(outcomes == null ? List.of() : outcomes);
      processedIdempotencyKeys =
          List.copyOf(processedIdempotencyKeys == null ? List.of() : processedIdempotencyKeys);
    }

    public static State empty(String decisionId) {
      return new State(decisionId, Status.EMPTY, null, List.of(), List.of(), List.of());
    }

    public boolean exists() {
      return status != Status.EMPTY;
    }

    public boolean isTerminal() {
      return status == Status.REJECTED
          || status == Status.SUPPRESSED
          || status == Status.SHIPMENT_PREPARED;
    }

    public boolean processed(String idempotencyKey) {
      return processedIdempotencyKeys.contains(idempotencyKey);
    }

    public Optional<OutcomeRef> outcome(String outcomeId) {
      return outcomes.stream().filter(outcome -> outcome.outcomeId().equals(outcomeId)).findFirst();
    }

    State withOpened(SupplyDecisionCard card, SupplyTraceEvent traceEvent, String idempotencyKey) {
      return new State(
          decisionId,
          Status.OPEN,
          card,
          append(traceEvents, traceEvent),
          appendOutcomeIfPresent(outcomes, traceEvent),
          append(processedIdempotencyKeys, idempotencyKey));
    }

    State withStatus(Status nextStatus, SupplyTraceEvent traceEvent, String idempotencyKey) {
      return new State(
          decisionId,
          nextStatus,
          card,
          append(traceEvents, traceEvent),
          appendOutcomeIfPresent(outcomes, traceEvent),
          append(processedIdempotencyKeys, idempotencyKey));
    }

    State withOutcomeLinked(OutcomeRef outcome, SupplyTraceEvent traceEvent, String idempotencyKey) {
      return new State(
          decisionId,
          status,
          card,
          append(traceEvents, traceEvent),
          outcomes.stream().anyMatch(existing -> existing.outcomeId().equals(outcome.outcomeId()))
              ? outcomes
              : append(outcomes, outcome),
          append(processedIdempotencyKeys, idempotencyKey));
    }
  }

  public sealed interface Command {
    record OpenRecommendation(
        String idempotencyKey, SupplyDecisionCard card, SupplyTraceEvent traceEvent)
        implements Command {}

    record RequireApproval(String idempotencyKey, SupplyTraceEvent traceEvent) implements Command {}

    record Approve(String idempotencyKey, String actor, String rationale, SupplyTraceEvent traceEvent)
        implements Command {}

    record Reject(String idempotencyKey, String actor, String rationale, SupplyTraceEvent traceEvent)
        implements Command {}

    record Suppress(String idempotencyKey, String actor, String rationale, SupplyTraceEvent traceEvent)
        implements Command {}

    record PrepareShipment(String idempotencyKey, String actor, String rationale, SupplyTraceEvent traceEvent)
        implements Command {}

    record EscalateStale(String idempotencyKey, String actor, String rationale, SupplyTraceEvent traceEvent)
        implements Command {}

    record LinkOutcome(String idempotencyKey, OutcomeRef outcome, SupplyTraceEvent traceEvent)
        implements Command {}
  }

  public sealed interface Event {
    @TypeName("supply-decision-recommendation-opened")
    record RecommendationOpened(SupplyDecisionCard card, SupplyTraceEvent traceEvent, String idempotencyKey)
        implements Event {}

    @TypeName("supply-decision-approval-required")
    record ApprovalRequired(SupplyTraceEvent traceEvent, String idempotencyKey) implements Event {}

    @TypeName("supply-decision-approved")
    record Approved(String actor, String rationale, SupplyTraceEvent traceEvent, String idempotencyKey)
        implements Event {}

    @TypeName("supply-decision-rejected")
    record Rejected(String actor, String rationale, SupplyTraceEvent traceEvent, String idempotencyKey)
        implements Event {}

    @TypeName("supply-decision-suppressed")
    record Suppressed(String actor, String rationale, SupplyTraceEvent traceEvent, String idempotencyKey)
        implements Event {}

    @TypeName("supply-decision-shipment-prepared")
    record ShipmentPrepared(String actor, String rationale, SupplyTraceEvent traceEvent, String idempotencyKey)
        implements Event {}

    @TypeName("supply-decision-stale-escalated")
    record StaleEscalated(String actor, String rationale, SupplyTraceEvent traceEvent, String idempotencyKey)
        implements Event {}

    @TypeName("supply-decision-outcome-linked")
    record OutcomeLinked(OutcomeRef outcome, SupplyTraceEvent traceEvent, String idempotencyKey)
        implements Event {}
  }

  private static <T> List<T> append(List<T> values, T value) {
    var copy = new java.util.ArrayList<>(values);
    copy.add(value);
    return List.copyOf(copy);
  }

  private static List<OutcomeRef> appendOutcomeIfPresent(
      List<OutcomeRef> outcomes, SupplyTraceEvent traceEvent) {
    if (traceEvent == null || traceEvent.outcome() == null) {
      return outcomes;
    }
    return outcomes.stream().anyMatch(existing -> existing.outcomeId().equals(traceEvent.outcome().outcomeId()))
        ? outcomes
        : append(outcomes, traceEvent.outcome());
  }
}
