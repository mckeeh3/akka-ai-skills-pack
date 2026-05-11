package com.example.domain.supplies;

import com.example.domain.supplies.Supply.TraceEventType;
import com.example.domain.supplies.SupplyDecision.Command;
import com.example.domain.supplies.SupplyDecision.State;
import com.example.domain.supplies.SupplyDecision.Status;
import java.util.ArrayList;
import java.util.List;

/** Deterministic validation for authority-sensitive supply decision commands. */
public final class SupplyDecisionValidator {

  private SupplyDecisionValidator() {}

  public static List<String> validate(State state, Command.OpenRecommendation command) {
    var errors = new ArrayList<String>();
    validateIdempotency(command.idempotencyKey(), errors);
    if (state.exists() && !state.processed(command.idempotencyKey())) {
      errors.add("decision is already open");
    }
    if (command.card() == null) {
      errors.add("decision card is required");
    } else {
      if (!state.decisionId().equals(command.card().decisionId())) {
        errors.add("decision card id must match entity id");
      }
      if (!command.card().isCompleteForReview()) {
        errors.add("decision card must include required evidence, policy clauses, alternatives, risk, and impact");
      }
    }
    validateTrace(command.traceEvent(), TraceEventType.RECOMMENDATION_CREATED, command.idempotencyKey(), errors);
    return errors;
  }

  public static List<String> validate(State state, Command.RequireApproval command) {
    var errors = mutableCommandErrors(state, command.idempotencyKey(), false);
    if (errors.isEmpty() && state.status() != Status.OPEN) {
      errors.add("approval can only be required for an open decision");
    }
    validateTrace(command.traceEvent(), TraceEventType.DECISION_CARD_CREATED, command.idempotencyKey(), errors);
    return errors;
  }

  public static List<String> validate(State state, Command.Approve command) {
    var errors = authorityCommandErrors(state, command.idempotencyKey(), command.actor(), command.rationale());
    if (errors.isEmpty() && state.status() != Status.APPROVAL_REQUIRED) {
      errors.add("approval requires a pending approval decision");
    }
    validateTrace(command.traceEvent(), TraceEventType.APPROVAL_RECORDED, command.idempotencyKey(), errors);
    return errors;
  }

  public static List<String> validate(State state, Command.Reject command) {
    var errors = authorityCommandErrors(state, command.idempotencyKey(), command.actor(), command.rationale());
    if (errors.isEmpty() && state.status() != Status.APPROVAL_REQUIRED) {
      errors.add("rejection requires a pending approval decision");
    }
    validateTrace(command.traceEvent(), TraceEventType.REJECTION_RECORDED, command.idempotencyKey(), errors);
    return errors;
  }

  public static List<String> validate(State state, Command.Suppress command) {
    var errors = authorityCommandErrors(state, command.idempotencyKey(), command.actor(), command.rationale());
    if (errors.isEmpty() && state.isTerminal()) {
      errors.add("terminal decisions cannot be suppressed");
    }
    validateTrace(command.traceEvent(), TraceEventType.SHIPMENT_SUPPRESSED, command.idempotencyKey(), errors);
    requirePolicyTrace(command.traceEvent(), errors);
    return errors;
  }

  public static List<String> validate(State state, Command.PrepareShipment command) {
    var errors = authorityCommandErrors(state, command.idempotencyKey(), command.actor(), command.rationale());
    if (errors.isEmpty() && !(state.status() == Status.OPEN || state.status() == Status.APPROVED)) {
      errors.add("shipment can only be prepared from open or approved decisions");
    }
    validateTrace(command.traceEvent(), TraceEventType.SHIPMENT_PREPARED, command.idempotencyKey(), errors);
    return errors;
  }

  public static List<String> validate(State state, Command.EscalateStale command) {
    var errors = authorityCommandErrors(state, command.idempotencyKey(), command.actor(), command.rationale());
    if (errors.isEmpty() && state.status() != Status.APPROVAL_REQUIRED) {
      errors.add("only pending approval decisions can be escalated as stale");
    }
    validateTrace(command.traceEvent(), TraceEventType.STALE_DECISION_ESCALATED, command.idempotencyKey(), errors);
    return errors;
  }

  public static List<String> validate(State state, Command.LinkOutcome command) {
    var errors = mutableCommandErrors(state, command.idempotencyKey(), true);
    if (command.outcome() == null) {
      errors.add("outcome is required");
    }
    validateTrace(command.traceEvent(), TraceEventType.OUTCOME_LINKED, command.idempotencyKey(), errors);
    if (command.traceEvent() != null && command.traceEvent().outcome() == null) {
      errors.add("outcome trace event must link an outcome");
    }
    return errors;
  }

  private static List<String> authorityCommandErrors(
      State state, String idempotencyKey, String actor, String rationale) {
    var errors = mutableCommandErrors(state, idempotencyKey, false);
    try {
      Supply.requireNonBlank(actor, "actor");
    } catch (IllegalArgumentException ex) {
      errors.add(ex.getMessage());
    }
    try {
      Supply.requireNonBlank(rationale, "rationale");
    } catch (IllegalArgumentException ex) {
      errors.add(ex.getMessage());
    }
    return errors;
  }

  private static List<String> mutableCommandErrors(
      State state, String idempotencyKey, boolean allowTerminal) {
    var errors = new ArrayList<String>();
    validateIdempotency(idempotencyKey, errors);
    if (!state.exists()) {
      errors.add("decision is not open");
    } else if (!allowTerminal && state.isTerminal()) {
      errors.add("decision is terminal");
    }
    return errors;
  }

  private static void validateIdempotency(String idempotencyKey, List<String> errors) {
    try {
      Supply.requireIdempotencyKey(idempotencyKey);
    } catch (IllegalArgumentException ex) {
      errors.add(ex.getMessage());
    }
  }

  private static void validateTrace(
      Supply.SupplyTraceEvent traceEvent,
      TraceEventType expectedType,
      String idempotencyKey,
      List<String> errors) {
    if (traceEvent == null) {
      errors.add("trace event is required");
      return;
    }
    if (traceEvent.type() != expectedType) {
      errors.add("trace event type must be " + expectedType);
    }
    if (!traceEvent.idempotencyKey().equals(idempotencyKey)) {
      errors.add("trace idempotency key must match command idempotency key");
    }
  }

  private static void requirePolicyTrace(Supply.SupplyTraceEvent traceEvent, List<String> errors) {
    if (traceEvent != null && traceEvent.policyClauses().isEmpty()) {
      errors.add("authority-sensitive suppression must cite policy clauses");
    }
  }
}
