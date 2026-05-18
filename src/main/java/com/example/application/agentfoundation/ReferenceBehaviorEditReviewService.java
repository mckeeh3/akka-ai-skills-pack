package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceBehaviorEditDecision;
import com.example.domain.agentfoundation.ReferenceBehaviorEditProposal;
import com.example.domain.agentfoundation.ReferenceBehaviorEditTrace;

/**
 * Deterministic reference helper for behavior-edit proposal review.
 *
 * <p>The service records review facts only. It deliberately does not activate draft prompt, skill,
 * manifest, tool-boundary, or AgentDefinition changes; follow-on activation must use governed
 * artifact commands outside this reference slice.
 */
public final class ReferenceBehaviorEditReviewService {
  private final ReferenceTraceSink traceSink;

  public ReferenceBehaviorEditReviewService(ReferenceTraceSink traceSink) {
    this.traceSink = traceSink;
  }

  public ReferenceBehaviorEditDecision approve(
      ReferenceBehaviorEditProposal proposal, String reviewerAccountId, String reason) {
    if (proposal.authorityExpansionDetected() || proposal.decisionCardRequired()) {
      return escalate(
          proposal,
          reviewerAccountId,
          "Authority expansion requires decision-card escalation before approval: " + reason);
    }
    return review(
        proposal,
        reviewerAccountId,
        ReferenceBehaviorEditDecision.DecisionType.APPROVE,
        reason,
        ReferenceBehaviorEditTrace.TraceEvent.REVIEW_APPROVED);
  }

  public ReferenceBehaviorEditDecision reject(
      ReferenceBehaviorEditProposal proposal, String reviewerAccountId, String reason) {
    return review(
        proposal,
        reviewerAccountId,
        ReferenceBehaviorEditDecision.DecisionType.REJECT,
        reason,
        ReferenceBehaviorEditTrace.TraceEvent.REVIEW_REJECTED);
  }

  public ReferenceBehaviorEditDecision requestChanges(
      ReferenceBehaviorEditProposal proposal, String reviewerAccountId, String reason) {
    return review(
        proposal,
        reviewerAccountId,
        ReferenceBehaviorEditDecision.DecisionType.REQUEST_CHANGES,
        reason,
        ReferenceBehaviorEditTrace.TraceEvent.REVIEW_CHANGES_REQUESTED);
  }

  public ReferenceBehaviorEditDecision escalate(
      ReferenceBehaviorEditProposal proposal, String reviewerAccountId, String reason) {
    return review(
        proposal,
        reviewerAccountId,
        ReferenceBehaviorEditDecision.DecisionType.ESCALATE,
        reason,
        ReferenceBehaviorEditTrace.TraceEvent.ESCALATED);
  }

  private ReferenceBehaviorEditDecision review(
      ReferenceBehaviorEditProposal proposal,
      String reviewerAccountId,
      ReferenceBehaviorEditDecision.DecisionType decisionType,
      String reason,
      ReferenceBehaviorEditTrace.TraceEvent traceEvent) {
    var decision =
        new ReferenceBehaviorEditDecision(
            proposal.tenantId(),
            proposal.proposalId(),
            decisionId(decisionType, proposal.proposalId()),
            reviewerAccountId,
            decisionType,
            reason,
            proposal.correlationId());
    traceSink.recordBehaviorEdit(
        new ReferenceBehaviorEditTrace(
            proposal.tenantId(),
            traceId(traceEvent, proposal.proposalId()),
            proposal.requestId(),
            proposal.proposalId(),
            proposal.targetAgentDefinitionId(),
            traceEvent,
            proposal.risk(),
            reason,
            proposal.correlationId()));
    return decision;
  }

  private static String decisionId(
      ReferenceBehaviorEditDecision.DecisionType decisionType, String proposalId) {
    return "behavior-decision-" + decisionType.name().toLowerCase().replace('_', '-') + "-" + proposalId;
  }

  private static String traceId(ReferenceBehaviorEditTrace.TraceEvent traceEvent, String proposalId) {
    return "behavior-trace-" + traceEvent.name().toLowerCase().replace('_', '-') + "-" + proposalId;
  }
}
