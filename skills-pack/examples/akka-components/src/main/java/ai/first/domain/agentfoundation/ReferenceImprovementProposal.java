package ai.first.domain.agentfoundation;

import java.util.List;

/** Governed proposal created from evaluation findings; activation is a later decision step. */
public record ReferenceImprovementProposal(
    String tenantId,
    String improvementProposalId,
    String targetAgentDefinitionId,
    List<String> evaluationRunIds,
    List<String> findingIds,
    String behaviorEditProposalId,
    String proposedChangeKind,
    ImprovementStatus status,
    String expectedOutcome,
    String rollbackBaselineId,
    List<String> replaySimulationIds,
    String correlationId) {

  public ReferenceImprovementProposal {
    evaluationRunIds = List.copyOf(evaluationRunIds);
    findingIds = List.copyOf(findingIds);
    replaySimulationIds = List.copyOf(replaySimulationIds);
  }

  public enum ImprovementStatus {
    DRAFT,
    IN_REVIEW,
    APPROVED,
    REJECTED,
    ACTIVATED,
    ROLLED_BACK,
    REQUEST_CHANGES
  }
}
