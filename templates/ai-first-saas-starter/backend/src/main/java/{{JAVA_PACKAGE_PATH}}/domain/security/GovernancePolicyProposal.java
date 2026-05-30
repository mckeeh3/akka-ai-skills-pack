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
    if (status != Status.DRAFT) return new GovernancePolicyProposal(proposalId, tenantId, customerId, createdByAccountId, Status.BLOCKED, targetPolicyId, title, rationale, proposedContent, riskClassification, affectedCapabilityIds, affectedArtifactRefs, requiredApprovalCapabilityId, rollbackRequirement, idempotencyKey, createdCorrelationId, correlationId, createdAt, now);
    return new GovernancePolicyProposal(proposalId, tenantId, customerId, createdByAccountId, Status.IN_REVIEW, targetPolicyId, title, rationale, proposedContent, riskClassification, affectedCapabilityIds, affectedArtifactRefs, requiredApprovalCapabilityId, rollbackRequirement, idempotencyKey, createdCorrelationId, correlationId, createdAt, now);
  }
}
