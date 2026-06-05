package ai.first.application;

import java.time.Instant;
import java.util.List;

/** Browser-safe durable projection for a bounded Audit/Trace summary AutonomousAgent task. */
public record AuditTraceSummaryTaskProjection(
    String summaryTaskId,
    String autonomousAgentTaskId,
    String tenantId,
    String customerId,
    String selectedAuthContextId,
    String startedByAccountId,
    String idempotencyKey,
    Status status,
    int progressPercent,
    String progressSummary,
    String blockerCode,
    AuditTraceSummaryResult result,
    List<String> evidenceRefs,
    List<String> findingRefs,
    List<String> traceIds,
    String humanDisposition,
    String humanDispositionReason,
    Instant updatedAt) {
  public AuditTraceSummaryTaskProjection {
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    findingRefs = List.copyOf(findingRefs == null ? List.of() : findingRefs);
    traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
  }

  public enum Status {
    QUEUED,
    RUNNING,
    BLOCKED_PROVIDER_OR_RUNTIME,
    FAILED,
    COMPLETED_REVIEW_REQUIRED,
    CANCELLED,
    ACCEPTED,
    REJECTED_RESULT
  }

  public AuditTraceSummaryTaskProjection withRuntimeStart(String taskId, Status nextStatus, int progress, String summary, String blocker, List<String> traces, Instant now) {
    return new AuditTraceSummaryTaskProjection(summaryTaskId, taskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, idempotencyKey, nextStatus, progress, summary, blocker, result, evidenceRefs, findingRefs, traces, humanDisposition, humanDispositionReason, now);
  }
}
