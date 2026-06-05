package com.example.domain.agentfoundation;

/** Safe trace fact for proposal, denial, review, and escalation in behavior-editing examples. */
public record ReferenceBehaviorEditTrace(
    String tenantId,
    String traceId,
    String requestId,
    String proposalId,
    String targetAgentDefinitionId,
    TraceEvent event,
    ReferenceBehaviorEditRisk risk,
    String safeSummary,
    String correlationId) {

  public enum TraceEvent {
    REQUEST_RECEIVED,
    PROPOSAL_CREATED,
    DENIED,
    REVIEW_APPROVED,
    REVIEW_REJECTED,
    REVIEW_CHANGES_REQUESTED,
    ESCALATED
  }
}
