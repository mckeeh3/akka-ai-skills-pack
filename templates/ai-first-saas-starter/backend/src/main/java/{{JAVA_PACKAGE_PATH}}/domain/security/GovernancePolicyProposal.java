package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Deterministic Governance/Policy proposal record; model output and frontend state never grant authority. */
public record GovernancePolicyProposal(
    String proposalId,
    String tenantId,
    String customerId,
    String createdByAccountId,
    Status status,
    String targetPolicyId,
    String title,
    String rationale,
    String proposedContent,
    String riskClassification,
    List<String> affectedCapabilityIds,
    List<String> affectedArtifactRefs,
    String requiredApprovalCapabilityId,
    String rollbackRequirement,
    String idempotencyKey,
    String createdCorrelationId,
    String submittedCorrelationId,
    String decision,
    String decisionRationale,
    String decisionCorrelationId,
    String activationCorrelationId,
    String rollbackReference,
    String rollbackCorrelationId,
    Instant createdAt,
    Instant updatedAt) {
  public enum Status {
    DRAFT,
    IN_REVIEW,
    APPROVED,
    REJECTED,
    ACTIVATED,
    ROLLED_BACK,
    BLOCKED
  }

  public GovernancePolicyProposal {
    Objects.requireNonNull(proposalId);
    Objects.requireNonNull(tenantId);
    Objects.requireNonNull(createdByAccountId);
    Objects.requireNonNull(status);
    Objects.requireNonNull(targetPolicyId);
    Objects.requireNonNull(title);
    Objects.requireNonNull(rationale);
    Objects.requireNonNull(riskClassification);
    affectedCapabilityIds = List.copyOf(affectedCapabilityIds == null ? List.of() : affectedCapabilityIds);
    affectedArtifactRefs = List.copyOf(affectedArtifactRefs == null ? List.of() : affectedArtifactRefs);
    Objects.requireNonNull(requiredApprovalCapabilityId);
    Objects.requireNonNull(rollbackRequirement);
    Objects.requireNonNull(createdAt);
    Objects.requireNonNull(updatedAt);
  }

  public GovernancePolicyProposal submitted(String correlationId, Instant now) {
    if (status == Status.IN_REVIEW) return this;
    if (status != Status.DRAFT) return blocked(correlationId, now);
    return copy(Status.IN_REVIEW, submittedCorrelationId == null ? correlationId : submittedCorrelationId, decision, decisionRationale, decisionCorrelationId, activationCorrelationId, rollbackReference, rollbackCorrelationId, now);
  }

  public GovernancePolicyProposal approved(String rationale, String correlationId, Instant now) {
    if (status == Status.APPROVED) return this;
    if (status != Status.IN_REVIEW) return blocked(correlationId, now);
    return copy(Status.APPROVED, submittedCorrelationId, "approve", rationale, correlationId, activationCorrelationId, rollbackReference, rollbackCorrelationId, now);
  }

  public GovernancePolicyProposal rejected(String rationale, String correlationId, Instant now) {
    if (status == Status.REJECTED) return this;
    if (status != Status.IN_REVIEW) return blocked(correlationId, now);
    return copy(Status.REJECTED, submittedCorrelationId, "reject", rationale, correlationId, activationCorrelationId, rollbackReference, rollbackCorrelationId, now);
  }

  public GovernancePolicyProposal activated(String rollbackReference, String correlationId, Instant now) {
    if (status == Status.ACTIVATED) return this;
    if (status != Status.APPROVED || rollbackReference == null || rollbackReference.isBlank()) return blocked(correlationId, now);
    return copy(Status.ACTIVATED, submittedCorrelationId, decision, decisionRationale, decisionCorrelationId, correlationId, rollbackReference, rollbackCorrelationId, now);
  }

  public GovernancePolicyProposal rolledBack(String correlationId, Instant now) {
    if (status == Status.ROLLED_BACK) return this;
    if (status != Status.ACTIVATED || rollbackReference == null || rollbackReference.isBlank()) return blocked(correlationId, now);
    return copy(Status.ROLLED_BACK, submittedCorrelationId, decision, decisionRationale, decisionCorrelationId, activationCorrelationId, rollbackReference, correlationId, now);
  }

  public GovernancePolicyProposal blocked(String correlationId, Instant now) {
    return copy(Status.BLOCKED, submittedCorrelationId, decision, decisionRationale, decisionCorrelationId, activationCorrelationId, rollbackReference, rollbackCorrelationId == null ? correlationId : rollbackCorrelationId, now);
  }

  private GovernancePolicyProposal copy(Status nextStatus, String nextSubmittedCorrelationId, String nextDecision, String nextDecisionRationale, String nextDecisionCorrelationId, String nextActivationCorrelationId, String nextRollbackReference, String nextRollbackCorrelationId, Instant now) {
    return new GovernancePolicyProposal(proposalId, tenantId, customerId, createdByAccountId, nextStatus, targetPolicyId, title, rationale, proposedContent, riskClassification, affectedCapabilityIds, affectedArtifactRefs, requiredApprovalCapabilityId, rollbackRequirement, idempotencyKey, createdCorrelationId, nextSubmittedCorrelationId, nextDecision, nextDecisionRationale, nextDecisionCorrelationId, nextActivationCorrelationId, nextRollbackReference, nextRollbackCorrelationId, createdAt, now);
  }
}
