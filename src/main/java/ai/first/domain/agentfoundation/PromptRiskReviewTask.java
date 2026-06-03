package ai.first.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Durable Agent Admin prompt-risk review task. Worker output is advisory and never activates behavior artifacts. */
public record PromptRiskReviewTask(
    String taskId,
    String autonomousAgentTaskId,
    String tenantId,
    String customerId,
    String targetAgentDefinitionId,
    String proposalId,
    String startedByAccountId,
    String startedByMembershipId,
    String idempotencyKey,
    Status status,
    int progressPercent,
    String summary,
    String blockerCode,
    String decision,
    String decisionReason,
    List<BehaviorArtifactDelta> proposedDeltas,
    List<String> evidenceRefs,
    List<String> findingRefs,
    List<String> traceIds,
    Instant createdAt,
    Instant updatedAt) {
  public PromptRiskReviewTask {
    proposedDeltas = List.copyOf(proposedDeltas == null ? List.of() : proposedDeltas);
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    findingRefs = List.copyOf(findingRefs == null ? List.of() : findingRefs);
    traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
  }

  public enum Status {
    QUEUED,
    RUNNING,
    BLOCKED_PROVIDER_OR_RUNTIME,
    CANCELLED,
    COMPLETED_REVIEW_REQUIRED,
    ACCEPTED,
    REJECTED
  }

  public enum ArtifactKind {
    AGENT_DEFINITION,
    PROMPT_DOCUMENT,
    AGENT_SKILL_MANIFEST,
    SKILL_DOCUMENT,
    AGENT_REFERENCE_MANIFEST,
    REFERENCE_DOCUMENT,
    MODEL_CONFIG_REF,
    MODEL_POLICY,
    TOOL_PERMISSION_BOUNDARY
  }

  public record BehaviorArtifactDelta(
      ArtifactKind artifactKind,
      String artifactId,
      Integer fromVersion,
      Integer toVersion,
      String changeSummary,
      String redactedDiffRef,
      String checksumBefore,
      String checksumAfter) {}

  public boolean terminal() {
    return status == Status.CANCELLED || status == Status.ACCEPTED || status == Status.REJECTED;
  }

  public boolean resultDecisionAllowed() {
    return status == Status.COMPLETED_REVIEW_REQUIRED;
  }

  public PromptRiskReviewTask withAutonomousAgentTaskId(String nextAutonomousAgentTaskId, Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new PromptRiskReviewTask(taskId, nextAutonomousAgentTaskId, tenantId, customerId, targetAgentDefinitionId, proposalId, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, proposedDeltas, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }

  public PromptRiskReviewTask withStatus(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new PromptRiskReviewTask(taskId, autonomousAgentTaskId, tenantId, customerId, targetAgentDefinitionId, proposalId, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, proposedDeltas, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }

  public PromptRiskReviewTask withWorkerUpdate(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextEvidenceRefs, List<String> nextFindingRefs, List<String> nextTraceIds, Instant now) {
    return new PromptRiskReviewTask(taskId, autonomousAgentTaskId, tenantId, customerId, targetAgentDefinitionId, proposalId, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, proposedDeltas, nextEvidenceRefs, nextFindingRefs, nextTraceIds, createdAt, now);
  }

  public PromptRiskReviewTask withDecision(Status nextStatus, String nextDecision, String nextDecisionReason, List<String> nextTraceIds, Instant now) {
    return new PromptRiskReviewTask(taskId, autonomousAgentTaskId, tenantId, customerId, targetAgentDefinitionId, proposalId, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, progressPercent, summary, blockerCode, nextDecision, nextDecisionReason, proposedDeltas, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }
}
