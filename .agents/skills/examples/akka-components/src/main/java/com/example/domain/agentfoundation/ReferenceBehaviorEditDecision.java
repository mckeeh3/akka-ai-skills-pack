package com.example.domain.agentfoundation;

/** Review decision for a behavior edit proposal; activation is intentionally out of scope. */
public record ReferenceBehaviorEditDecision(
    String tenantId,
    String proposalId,
    String decisionId,
    String reviewerAccountId,
    DecisionType decisionType,
    String reason,
    String correlationId) {

  public enum DecisionType {
    APPROVE,
    REJECT,
    REQUEST_CHANGES,
    ESCALATE
  }
}
