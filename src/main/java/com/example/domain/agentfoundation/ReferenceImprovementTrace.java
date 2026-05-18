package com.example.domain.agentfoundation;

/** Safe trace fact for closed-loop improvement proposal, evidence, decision, activation, and rollback. */
public record ReferenceImprovementTrace(
    String tenantId,
    String traceId,
    String improvementProposalId,
    String evaluationRunId,
    String sourceWorkTraceId,
    TraceEvent event,
    String safeSummary,
    String correlationId) {

  public enum TraceEvent {
    FINDING_NORMALIZED,
    PROPOSAL_CREATED,
    BEHAVIOR_EDIT_LINKED,
    REPLAY_SIMULATED,
    DECISION_RECORDED,
    ACTIVATED,
    MONITORING_RECORDED,
    ROLLED_BACK
  }
}
