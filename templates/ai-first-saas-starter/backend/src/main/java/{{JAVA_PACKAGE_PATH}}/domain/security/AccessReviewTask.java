package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.time.Instant;
import java.util.List;

/** Durable User Admin access-review task state. Worker output is advisory and never mutates access directly. */
public record AccessReviewTask(
    String taskId,
    String tenantId,
    String customerId,
    ScopeType scopeType,
    String startedByAccountId,
    String startedByMembershipId,
    String idempotencyKey,
    Status status,
    int progressPercent,
    String summary,
    String blockerCode,
    String decision,
    String decisionReason,
    List<String> evidenceRefs,
    List<String> recommendationRefs,
    List<String> traceIds,
    Instant createdAt,
    Instant updatedAt) {

  public enum Status {
    QUEUED,
    RUNNING,
    BLOCKED_PROVIDER_OR_RUNTIME,
    CANCELLED,
    COMPLETED,
    ACCEPTED,
    REJECTED
  }

  public boolean terminal() {
    return status == Status.CANCELLED || status == Status.ACCEPTED || status == Status.REJECTED;
  }

  public boolean resultDecisionAllowed() {
    return status == Status.COMPLETED;
  }

  public AccessReviewTask withStatus(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new AccessReviewTask(taskId, tenantId, customerId, scopeType, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, evidenceRefs, recommendationRefs, nextTraceIds, createdAt, now);
  }

  public AccessReviewTask withDecision(Status nextStatus, String nextDecision, String nextDecisionReason, List<String> nextTraceIds, Instant now) {
    return new AccessReviewTask(taskId, tenantId, customerId, scopeType, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, progressPercent, summary, blockerCode, nextDecision, nextDecisionReason, evidenceRefs, recommendationRefs, nextTraceIds, createdAt, now);
  }
}
