package com.example.domain.supplies;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/** Pure domain vocabulary for the AI-first supplies autopilot reference slice. */
public final class Supply {

  private Supply() {}

  public static final String GOAL_SUPPLY_FULFILLMENT = "GOAL-02";
  public static final Set<String> STABLE_POLICY_CLAUSE_IDS =
      Set.of("SUP-1.0", "SUP-2.0", "SUP-3.0", "SUP-4.0", "SUP-5.0");

  private static final Pattern TRACE_ID = Pattern.compile("trace-[A-Za-z0-9][A-Za-z0-9._:-]*");
  private static final Pattern CORRELATION_ID = Pattern.compile("corr-[A-Za-z0-9][A-Za-z0-9._:-]*");
  private static final Pattern IDEMPOTENCY_KEY = Pattern.compile("idem-[A-Za-z0-9][A-Za-z0-9._:-]*");

  public enum DeviceLifecycleStatus {
    ACTIVE,
    OFFBOARDING,
    UNMAPPED_CONTRACT,
    SUSPENDED
  }

  public enum EvidenceType {
    TELEMETRY,
    FORECAST,
    INVENTORY,
    ENTITLEMENT,
    POLICY,
    HUMAN_CONTEXT
  }

  public enum TraceEventType {
    TELEMETRY_RECEIVED,
    TOOL_INVOKED,
    POLICY_INVOKED,
    RECOMMENDATION_CREATED,
    DECISION_CARD_CREATED,
    APPROVAL_RECORDED,
    REJECTION_RECORDED,
    SHIPMENT_SUPPRESSED,
    SHIPMENT_PREPARED,
    STALE_DECISION_ESCALATED,
    OUTCOME_LINKED
  }

  public enum DecisionAction {
    AUTO_SHIP,
    REQUIRE_APPROVAL,
    SUPPRESS_SHIPMENT,
    ESCALATE_EXCEPTION,
    REJECTED,
    APPROVED
  }

  public record SupplyObjective(
      String goalId, String ownerRole, String objective, List<String> successCriteria) {
    public SupplyObjective {
      requireNonBlank(goalId, "goalId");
      requireNonBlank(ownerRole, "ownerRole");
      requireNonBlank(objective, "objective");
      successCriteria = List.copyOf(requireNonEmpty(successCriteria, "successCriteria"));
    }

    public static SupplyObjective goal02() {
      return new SupplyObjective(
          GOAL_SUPPLY_FULFILLMENT,
          "Operations Supervisor",
          "Keep monitored devices supplied on time without violating supply policy.",
          List.of("timely replenishment", "policy-safe shipments", "traceable outcomes"));
    }
  }

  public record DeviceTelemetry(
      String idempotencyKey,
      TraceRef trace,
      String customerId,
      String deviceId,
      Instant observedAt,
      int tonerPercent,
      int pagesSinceLastSupply,
      DeviceLifecycleStatus lifecycleStatus) {
    public DeviceTelemetry {
      requireIdempotencyKey(idempotencyKey);
      trace = Objects.requireNonNull(trace, "trace");
      requireNonBlank(customerId, "customerId");
      requireNonBlank(deviceId, "deviceId");
      observedAt = Objects.requireNonNull(observedAt, "observedAt");
      requirePercent(tonerPercent, "tonerPercent");
      requireNonNegative(pagesSinceLastSupply, "pagesSinceLastSupply");
      lifecycleStatus = Objects.requireNonNull(lifecycleStatus, "lifecycleStatus");
    }

    public boolean indicatesDepletionRisk(int thresholdPercent) {
      requirePercent(thresholdPercent, "thresholdPercent");
      return tonerPercent <= thresholdPercent;
    }
  }

  public record SupplyItem(String sku, String name, int quantity, long estimatedUnitCostCents) {
    public SupplyItem {
      requireNonBlank(sku, "sku");
      requireNonBlank(name, "name");
      requirePositive(quantity, "quantity");
      requireNonNegative(estimatedUnitCostCents, "estimatedUnitCostCents");
    }

    public long estimatedTotalCostCents() {
      return estimatedUnitCostCents * quantity;
    }
  }

  public record SupplyEvidence(
      EvidenceType type, String source, String summary, double confidence, TraceRef trace) {
    public SupplyEvidence {
      type = Objects.requireNonNull(type, "type");
      requireNonBlank(source, "source");
      requireNonBlank(summary, "summary");
      requireScore(confidence, "confidence");
      trace = Objects.requireNonNull(trace, "trace");
    }
  }

  public record SupplyPolicyClauseRef(String clauseId, String summary) {
    public SupplyPolicyClauseRef {
      requireStablePolicyClauseId(clauseId);
      requireNonBlank(summary, "summary");
    }
  }

  public record SupplyRecommendation(
      String recommendationId,
      String idempotencyKey,
      SupplyItem item,
      double depletionRisk,
      double confidence,
      long estimatedCostCents,
      List<SupplyEvidence> evidence,
      List<SupplyPolicyClauseRef> policyClauses,
      String rationale,
      List<String> alternatives) {
    public SupplyRecommendation {
      requireNonBlank(recommendationId, "recommendationId");
      requireIdempotencyKey(idempotencyKey);
      item = Objects.requireNonNull(item, "item");
      requireScore(depletionRisk, "depletionRisk");
      requireScore(confidence, "confidence");
      requireNonNegative(estimatedCostCents, "estimatedCostCents");
      evidence = List.copyOf(requireNonEmpty(evidence, "evidence"));
      policyClauses = List.copyOf(requireNonEmpty(policyClauses, "policyClauses"));
      requireNonBlank(rationale, "rationale");
      alternatives = List.copyOf(requireNonEmpty(alternatives, "alternatives"));
    }

    public boolean hasEvidence(EvidenceType type) {
      return evidence.stream().anyMatch(item -> item.type() == type);
    }

    public boolean hasRequiredDecisionEvidence() {
      return hasEvidence(EvidenceType.TELEMETRY)
          && hasEvidence(EvidenceType.FORECAST)
          && hasEvidence(EvidenceType.INVENTORY)
          && hasEvidence(EvidenceType.ENTITLEMENT);
    }
  }

  public record TraceRef(String traceId, String correlationId) {
    public TraceRef {
      requireTraceId(traceId);
      requireCorrelationId(correlationId);
    }
  }

  public record OutcomeRef(String outcomeId, String metricName) {
    public OutcomeRef {
      requireNonBlank(outcomeId, "outcomeId");
      requireNonBlank(metricName, "metricName");
    }
  }

  public record SupplyDecisionCard(
      String decisionId,
      SupplyObjective objective,
      SupplyRecommendation recommendation,
      DecisionAction proposedAction,
      String riskSummary,
      String impactSummary,
      TraceRef trace,
      OutcomeRef outcome) {
    public SupplyDecisionCard {
      requireNonBlank(decisionId, "decisionId");
      objective = Objects.requireNonNull(objective, "objective");
      recommendation = Objects.requireNonNull(recommendation, "recommendation");
      proposedAction = Objects.requireNonNull(proposedAction, "proposedAction");
      requireNonBlank(riskSummary, "riskSummary");
      requireNonBlank(impactSummary, "impactSummary");
      trace = Objects.requireNonNull(trace, "trace");
      outcome = Objects.requireNonNull(outcome, "outcome");
    }

    public boolean isCompleteForReview() {
      return recommendation.hasRequiredDecisionEvidence()
          && !recommendation.policyClauses().isEmpty()
          && !recommendation.alternatives().isEmpty()
          && !riskSummary.isBlank()
          && !impactSummary.isBlank();
    }
  }

  public record SupplyTraceEvent(
      String eventId,
      TraceEventType type,
      TraceRef trace,
      String idempotencyKey,
      Instant occurredAt,
      String actor,
      String summary,
      List<SupplyPolicyClauseRef> policyClauses,
      OutcomeRef outcome) {
    public SupplyTraceEvent {
      requireNonBlank(eventId, "eventId");
      type = Objects.requireNonNull(type, "type");
      trace = Objects.requireNonNull(trace, "trace");
      requireIdempotencyKey(idempotencyKey);
      occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
      requireNonBlank(actor, "actor");
      requireNonBlank(summary, "summary");
      policyClauses = List.copyOf(policyClauses == null ? List.of() : policyClauses);
    }

    public boolean linksOutcome() {
      return outcome != null;
    }
  }

  public static void requireStablePolicyClauseId(String clauseId) {
    requireNonBlank(clauseId, "clauseId");
    if (!STABLE_POLICY_CLAUSE_IDS.contains(clauseId)) {
      throw new IllegalArgumentException("policy clause id must be one of " + STABLE_POLICY_CLAUSE_IDS);
    }
  }

  public static void requireTraceId(String traceId) {
    requireNonBlank(traceId, "traceId");
    if (!TRACE_ID.matcher(traceId).matches()) {
      throw new IllegalArgumentException("traceId must start with trace-");
    }
  }

  public static void requireCorrelationId(String correlationId) {
    requireNonBlank(correlationId, "correlationId");
    if (!CORRELATION_ID.matcher(correlationId).matches()) {
      throw new IllegalArgumentException("correlationId must start with corr-");
    }
  }

  public static void requireIdempotencyKey(String idempotencyKey) {
    requireNonBlank(idempotencyKey, "idempotencyKey");
    if (!IDEMPOTENCY_KEY.matcher(idempotencyKey).matches()) {
      throw new IllegalArgumentException("idempotencyKey must start with idem-");
    }
  }

  public static void requireScore(double value, String field) {
    if (Double.isNaN(value) || value < 0.0 || value > 1.0) {
      throw new IllegalArgumentException(field + " must be between 0.0 and 1.0");
    }
  }

  public static void requirePercent(int value, String field) {
    if (value < 0 || value > 100) {
      throw new IllegalArgumentException(field + " must be between 0 and 100");
    }
  }

  public static void requirePositive(int value, String field) {
    if (value <= 0) {
      throw new IllegalArgumentException(field + " must be positive");
    }
  }

  public static void requireNonNegative(long value, String field) {
    if (value < 0) {
      throw new IllegalArgumentException(field + " must be non-negative");
    }
  }

  public static void requireNonBlank(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " is required");
    }
  }

  private static <T> List<T> requireNonEmpty(List<T> value, String field) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException(field + " is required");
    }
    if (value.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException(field + " cannot contain null values");
    }
    return value;
  }
}
