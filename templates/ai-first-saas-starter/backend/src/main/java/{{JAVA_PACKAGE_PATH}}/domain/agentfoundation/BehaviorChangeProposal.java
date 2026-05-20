package {{JAVA_BASE_PACKAGE}}.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Draft governed-agent behavior change; approval activates the exact reviewed draft. */
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
    String reviewReason) {
  public BehaviorChangeProposal {
    proposedToolGrants = List.copyOf(proposedToolGrants == null ? List.of() : proposedToolGrants);
  }

  public enum TargetArtifact {
    PROMPT,
    SKILL,
    TOOL_BOUNDARY
  }

  public enum Status {
    PROPOSED,
    APPROVED,
    REJECTED,
    DENIED
  }
}
