package com.example.domain.agentfoundation;

import java.util.List;

/** Replay/simulation evidence comparing current behavior with a proposed improvement. */
public record ReferenceReplaySimulation(
    String tenantId,
    String simulationId,
    String improvementProposalId,
    String baselineReferenceId,
    String candidateReferenceId,
    boolean passed,
    int baselineScore,
    int candidateScore,
    List<String> riskNotes,
    String safeSummary,
    String correlationId) {

  public ReferenceReplaySimulation {
    riskNotes = List.copyOf(riskNotes);
  }
}
