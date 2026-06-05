package ai.first.domain.agentfoundation;

/** Human or policy decision over an improvement proposal. */
public record ReferenceImprovementDecision(
    String tenantId,
    String improvementProposalId,
    String decisionId,
    String reviewerAccountId,
    DecisionType decisionType,
    boolean replayEvidenceRequired,
    String reason,
    String correlationId) {

  public enum DecisionType {
    APPROVE,
    REJECT,
    REQUEST_CHANGES,
    ACTIVATE,
    MONITOR,
    ROLLBACK
  }
}
