package ai.first.domain.coreapp.myaccount;

import ai.first.domain.foundation.identity.Account;
import java.time.Instant;
import java.util.List;

/** Durable My Account personal attention digest AutonomousAgent task projection. Advisory only; never mutates source attention. */
public record MyAccountPersonalAttentionDigestTask(
    String digestTaskId,
    String autonomousAgentTaskId,
    String tenantId,
    String customerId,
    String selectedAuthContextId,
    String startedByAccountId,
    String startedByMembershipId,
    String idempotencyKey,
    int authorizedAttentionCount,
    Status status,
    int progressPercent,
    String summary,
    String blockerCode,
    String decision,
    String decisionReason,
    List<String> evidenceRefs,
    List<String> sectionRefs,
    List<String> traceIds,
    Instant createdAt,
    Instant updatedAt) {
  public MyAccountPersonalAttentionDigestTask {
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    sectionRefs = List.copyOf(sectionRefs == null ? List.of() : sectionRefs);
    traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
  }

  public enum Status {
    QUEUED,
    RUNNING,
    BLOCKED_PROVIDER_OR_RUNTIME,
    FAILED,
    CANCELLED,
    COMPLETED_EMPTY,
    COMPLETED_REVIEW_REQUIRED,
    ACCEPTED,
    REJECTED
  }

  public boolean terminal() {
    return status == Status.CANCELLED || status == Status.ACCEPTED || status == Status.REJECTED;
  }

  public boolean resultDecisionAllowed() {
    return status == Status.COMPLETED_REVIEW_REQUIRED || status == Status.COMPLETED_EMPTY;
  }

  public MyAccountPersonalAttentionDigestTask withAutonomousAgentTaskId(String nextAutonomousAgentTaskId, Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new MyAccountPersonalAttentionDigestTask(digestTaskId, nextAutonomousAgentTaskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, startedByMembershipId, idempotencyKey, authorizedAttentionCount, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, evidenceRefs, sectionRefs, nextTraceIds, createdAt, now);
  }

  public MyAccountPersonalAttentionDigestTask withStatus(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new MyAccountPersonalAttentionDigestTask(digestTaskId, autonomousAgentTaskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, startedByMembershipId, idempotencyKey, authorizedAttentionCount, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, evidenceRefs, sectionRefs, nextTraceIds, createdAt, now);
  }

  public MyAccountPersonalAttentionDigestTask withWorkerUpdate(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, int nextAuthorizedAttentionCount, List<String> nextEvidenceRefs, List<String> nextSectionRefs, List<String> nextTraceIds, Instant now) {
    return new MyAccountPersonalAttentionDigestTask(digestTaskId, autonomousAgentTaskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, startedByMembershipId, idempotencyKey, nextAuthorizedAttentionCount, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, nextEvidenceRefs, nextSectionRefs, nextTraceIds, createdAt, now);
  }

  public MyAccountPersonalAttentionDigestTask withDecision(Status nextStatus, String nextDecision, String nextDecisionReason, List<String> nextTraceIds, Instant now) {
    return new MyAccountPersonalAttentionDigestTask(digestTaskId, autonomousAgentTaskId, tenantId, customerId, selectedAuthContextId, startedByAccountId, startedByMembershipId, idempotencyKey, authorizedAttentionCount, nextStatus, progressPercent, summary, blockerCode, nextDecision, nextDecisionReason, evidenceRefs, sectionRefs, nextTraceIds, createdAt, now);
  }
}
