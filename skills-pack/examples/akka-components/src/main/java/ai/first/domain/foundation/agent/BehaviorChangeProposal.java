package ai.first.domain.foundation.agent;

import java.time.Instant;
import java.util.List;

/** Governed-agent behavior change; draft/review/activation are deterministic and separate. */
public record BehaviorChangeProposal(
    String proposalId,
    String tenantId,
    String agentDefinitionId,
    String targetArtifactId,
    TargetArtifact targetArtifact,
    Status status,
    String requestedByAccountId,
    String rationale,
    String proposedContent,
    List<ToolPermissionBoundary.ToolGrant> proposedToolGrants,
    String riskClassification,
    String correlationId,
    Instant createdAt,
    Instant reviewedAt,
    String reviewedByAccountId,
    String reviewReason,
    Instant activatedAt,
    String activatedByAccountId,
    Instant rolledBackAt,
    String rolledBackByAccountId) {
  public BehaviorChangeProposal {
    proposedToolGrants = List.copyOf(proposedToolGrants == null ? List.of() : proposedToolGrants);
  }

  public enum TargetArtifact {
    PROMPT,
    SKILL,
    REFERENCE,
    SKILL_MANIFEST,
    REFERENCE_MANIFEST,
    MODEL_REF,
    TOOL_BOUNDARY
  }

  public enum Status {
    PROPOSED,
    IN_REVIEW,
    APPROVED,
    ACTIVATED,
    REJECTED,
    CANCELLED,
    DENIED,
    ROLLED_BACK
  }
}
