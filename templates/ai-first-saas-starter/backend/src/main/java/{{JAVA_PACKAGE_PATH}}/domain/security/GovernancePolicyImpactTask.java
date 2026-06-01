package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.time.Instant;
import java.util.List;

/** Durable Governance/Policy impact-analysis task projection. Worker output is advisory and never activates policy state. */
public record GovernancePolicyImpactTask(
    String impactTaskId,
    String autonomousAgentTaskId,
    String proposalId,
    String targetPolicyId,
    String tenantId,
    String customerId,
    String startedByAccountId,
    String startedByMembershipId,
    String idempotencyKey,
    Status status,
    int progressPercent,
    String summary,
    String blockerCode,
    String decision,
    String decisionReason,
    List<String> affectedCapabilityIds,
    List<String> affectedArtifactRefs,
    List<String> evidenceRefs,
    List<String> findingRefs,
    List<String> traceIds,
    Instant createdAt,
    Instant updatedAt) {
  public GovernancePolicyImpactTask {
    affectedCapabilityIds = List.copyOf(affectedCapabilityIds == null ? List.of() : affectedCapabilityIds);
    affectedArtifactRefs = List.copyOf(affectedArtifactRefs == null ? List.of() : affectedArtifactRefs);
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
    REJECTED_RESULT,
    REQUEST_CHANGES
  }

  public boolean terminal() {
    return status == Status.CANCELLED || status == Status.ACCEPTED || status == Status.REJECTED_RESULT || status == Status.REQUEST_CHANGES;
  }

  public boolean resultDecisionAllowed() {
    return status == Status.COMPLETED_REVIEW_REQUIRED;
  }

  public GovernancePolicyImpactTask withAutonomousAgentTaskId(String nextAutonomousAgentTaskId, Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new GovernancePolicyImpactTask(impactTaskId, nextAutonomousAgentTaskId, proposalId, targetPolicyId, tenantId, customerId, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, affectedCapabilityIds, affectedArtifactRefs, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }

  public GovernancePolicyImpactTask withStatus(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextTraceIds, Instant now) {
    return new GovernancePolicyImpactTask(impactTaskId, autonomousAgentTaskId, proposalId, targetPolicyId, tenantId, customerId, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, affectedCapabilityIds, affectedArtifactRefs, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }

  public GovernancePolicyImpactTask withWorkerUpdate(Status nextStatus, int nextProgressPercent, String nextSummary, String nextBlockerCode, List<String> nextEvidenceRefs, List<String> nextFindingRefs, List<String> nextTraceIds, Instant now) {
    return new GovernancePolicyImpactTask(impactTaskId, autonomousAgentTaskId, proposalId, targetPolicyId, tenantId, customerId, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, nextProgressPercent, nextSummary, nextBlockerCode, decision, decisionReason, affectedCapabilityIds, affectedArtifactRefs, nextEvidenceRefs, nextFindingRefs, nextTraceIds, createdAt, now);
  }

  public GovernancePolicyImpactTask withDecision(Status nextStatus, String nextDecision, String nextDecisionReason, List<String> nextTraceIds, Instant now) {
    return new GovernancePolicyImpactTask(impactTaskId, autonomousAgentTaskId, proposalId, targetPolicyId, tenantId, customerId, startedByAccountId, startedByMembershipId, idempotencyKey, nextStatus, progressPercent, summary, blockerCode, nextDecision, nextDecisionReason, affectedCapabilityIds, affectedArtifactRefs, evidenceRefs, findingRefs, nextTraceIds, createdAt, now);
  }
}
