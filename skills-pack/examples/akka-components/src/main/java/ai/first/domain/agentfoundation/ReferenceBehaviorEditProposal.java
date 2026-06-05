package ai.first.domain.agentfoundation;

import java.util.List;
import java.util.Set;

/** Structured proposal produced by an AgentBehaviorEditorAgent-style reference helper. */
public record ReferenceBehaviorEditProposal(
    String tenantId,
    String proposalId,
    String requestId,
    String targetAgentDefinitionId,
    List<ReferenceProposedDocumentDiff> proposedDiffs,
    ReferenceBehaviorEditRisk risk,
    boolean authorityExpansionDetected,
    Set<String> expansionTypes,
    boolean decisionCardRequired,
    String rationale,
    String recommendedNextAction,
    String correlationId) {

  public ReferenceBehaviorEditProposal {
    proposedDiffs = List.copyOf(proposedDiffs);
    expansionTypes = Set.copyOf(expansionTypes);
  }
}
