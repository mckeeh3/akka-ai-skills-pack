package ai.first.domain.coreapp.audit;

import java.time.Instant;
import java.util.List;

/** Durable Audit/Trace summary AutonomousAgent task projection. Advisory only; never mutates traces, policy, users, authorization, or provider config. */
public record AuditTraceSummaryTask(
    String taskId,
    String autonomousAgentTaskId,
    String tenantId,
    String customerId,
    String selectedAuthContextId,
    String startedByAccountId,
    String startedByMembershipId,
    String idempotencyKey,
    Instant windowStart,
    Instant windowEnd,
    List<String> evidenceCategories,
    Status status,
    int progressPercent,
    String summary,
    String blockerCode,
    String decision,
    String decisionReason,
    List<String> evidenceRefs,
    List<String> findingRefs,
    List<String> traceIds,
    Instant createdAt,
    Instant updatedAt) {
  public AuditTraceSummaryTask {
    evidenceCategories = List.copyOf(evidenceCategories == null ? List.of() : evidenceCategories);
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    findingRefs = List.copyOf(findingRefs == null ? List.of() : findingRefs);
    traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
  }

  public enum Status {
    QUEUED,
    RUNNING,
    BLOCKED_PROVIDER_OR_RUNTIME,
    FAILED,
    CANCELLED,
    COMPLETED_REVIEW_REQUIRED,
    ACCEPTED,
    REJECTED
  }

  public boolean terminal() {
    return status == Status.CANCELLED || status == Status.ACCEPTED || status == Status.REJECTED;
  }

  public boolean resultDecisionAllowed() {
    return status == Status.COMPLETED_REVIEW_REQUIRED;
  }

  public AuditTraceSummaryTask withAutonomousAgentTaskId(String nextAutonomousAgentTaskId, Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new AuditTraceSummaryTask(taskId, nextAutonomousAgentTaskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, startedByMembershipId, idempotencyKey, windowStart, windowEnd, evidenceCategories, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }

  public AuditTraceSummaryTask withStatus(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new AuditTraceSummaryTask(taskId, autonomousAgentTaskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, startedByMembershipId, idempotencyKey, windowStart, windowEnd, evidenceCategories, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }

  public AuditTraceSummaryTask withWorkerUpdate(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextEvidenceRefs, List<String> nextFindingRefs, List<String> nextTraceIds, Instant now) {
    return new AuditTraceSummaryTask(taskId, autonomousAgentTaskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, startedByMembershipId, idempotencyKey, windowStart, windowEnd, evidenceCategories, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, nextEvidenceRefs, nextFindingRefs, nextTraceIds, createdAt, now);
  }

  public AuditTraceSummaryTask withDecision(Status nextStatus, String nextDecision, String nextDecisionReason, List<String> nextTraceIds, Instant now) {
    return new AuditTraceSummaryTask(taskId, autonomousAgentTaskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, startedByMembershipId, idempotencyKey, windowStart, windowEnd, evidenceCategories, nextStatus, progressPercent, summary, blockerCode, nextDecision, nextDecisionReason, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }
}
