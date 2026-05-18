package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceAuthContext;
import com.example.domain.agentfoundation.ReferenceImprovementDecision;
import com.example.domain.agentfoundation.ReferenceImprovementProposal;
import com.example.domain.agentfoundation.ReferenceImprovementTrace;
import com.example.domain.agentfoundation.ReferenceReplaySimulation;
import java.util.List;

/**
 * Deterministic reference helper for closed-loop improvement review, activation, monitoring, and
 * rollback decisions.
 *
 * <p>The service keeps approval separate from activation, requires passing replay/simulation
 * evidence before approval and activation, records safe trace facts for every transition, and models
 * rollback as an explicit audited decision rather than an invisible state edit.
 */
public final class ReferenceImprovementDecisionService {
  private final ReferenceReplaySimulationService replaySimulationService;
  private final ReferenceTraceSink traceSink;

  public ReferenceImprovementDecisionService(
      ReferenceReplaySimulationService replaySimulationService, ReferenceTraceSink traceSink) {
    this.replaySimulationService = replaySimulationService;
    this.traceSink = traceSink;
  }

  public DecisionResult approve(
      ReferenceAuthContext authContext,
      ReferenceImprovementProposal proposal,
      List<ReferenceReplaySimulation> simulations,
      String reviewerAccountId,
      String sourceWorkTraceId) {
    requireTenant(authContext, proposal);
    var blockedReason = replaySimulationService.activationBlockedReason(proposal, simulations);
    if (blockedReason != null) {
      var decision =
          decision(
              proposal,
              reviewerAccountId,
              ReferenceImprovementDecision.DecisionType.REQUEST_CHANGES,
              true,
              "Approval requires passing replay/simulation evidence: " + blockedReason);
      recordDecisionTrace(proposal, decision, sourceWorkTraceId);
      return new DecisionResult(proposal, decision, blockedReason);
    }

    var updatedProposal = withStatus(proposal, ReferenceImprovementProposal.ImprovementStatus.APPROVED);
    var decision =
        decision(
            updatedProposal,
            reviewerAccountId,
            ReferenceImprovementDecision.DecisionType.APPROVE,
            true,
            "Approved after passing replay/simulation evidence; activation remains separate.");
    recordDecisionTrace(updatedProposal, decision, sourceWorkTraceId);
    return new DecisionResult(updatedProposal, decision, null);
  }

  public DecisionResult reject(
      ReferenceAuthContext authContext,
      ReferenceImprovementProposal proposal,
      String reviewerAccountId,
      String reason,
      String sourceWorkTraceId) {
    requireTenant(authContext, proposal);
    var updatedProposal = withStatus(proposal, ReferenceImprovementProposal.ImprovementStatus.REJECTED);
    var decision =
        decision(
            updatedProposal,
            reviewerAccountId,
            ReferenceImprovementDecision.DecisionType.REJECT,
            false,
            reason);
    recordDecisionTrace(updatedProposal, decision, sourceWorkTraceId);
    return new DecisionResult(updatedProposal, decision, "activation blocked: proposal was rejected");
  }

  public DecisionResult requestChanges(
      ReferenceAuthContext authContext,
      ReferenceImprovementProposal proposal,
      String reviewerAccountId,
      String reason,
      String sourceWorkTraceId) {
    requireTenant(authContext, proposal);
    var updatedProposal = withStatus(proposal, ReferenceImprovementProposal.ImprovementStatus.REQUEST_CHANGES);
    var decision =
        decision(
            updatedProposal,
            reviewerAccountId,
            ReferenceImprovementDecision.DecisionType.REQUEST_CHANGES,
            false,
            reason);
    recordDecisionTrace(updatedProposal, decision, sourceWorkTraceId);
    return new DecisionResult(updatedProposal, decision, "activation blocked: reviewer requested changes");
  }

  public DecisionResult activate(
      ReferenceAuthContext authContext,
      ReferenceImprovementProposal proposal,
      List<ReferenceReplaySimulation> simulations,
      String reviewerAccountId,
      String sourceWorkTraceId) {
    requireTenant(authContext, proposal);
    if (proposal.status() != ReferenceImprovementProposal.ImprovementStatus.APPROVED) {
      var blockedReason = "activation blocked: proposal is not approved";
      var decision =
          decision(
              proposal,
              reviewerAccountId,
              ReferenceImprovementDecision.DecisionType.REQUEST_CHANGES,
              true,
              blockedReason);
      recordDecisionTrace(proposal, decision, sourceWorkTraceId);
      return new DecisionResult(proposal, decision, blockedReason);
    }

    var blockedReason = replaySimulationService.activationBlockedReason(proposal, simulations);
    if (blockedReason != null) {
      var decision =
          decision(
              proposal,
              reviewerAccountId,
              ReferenceImprovementDecision.DecisionType.REQUEST_CHANGES,
              true,
              blockedReason);
      recordDecisionTrace(proposal, decision, sourceWorkTraceId);
      return new DecisionResult(proposal, decision, blockedReason);
    }

    var updatedProposal = withStatus(proposal, ReferenceImprovementProposal.ImprovementStatus.ACTIVATED);
    var decision =
        decision(
            updatedProposal,
            reviewerAccountId,
            ReferenceImprovementDecision.DecisionType.ACTIVATE,
            true,
            "Activated approved improvement with passing replay/simulation evidence.");
    recordDecisionTrace(updatedProposal, decision, sourceWorkTraceId);
    recordLifecycleTrace(
        updatedProposal,
        ReferenceImprovementTrace.TraceEvent.ACTIVATED,
        "Activated governed improvement proposal after approval and evidence checks.",
        sourceWorkTraceId);
    return new DecisionResult(updatedProposal, decision, null);
  }

  public DecisionResult monitor(
      ReferenceAuthContext authContext,
      ReferenceImprovementProposal proposal,
      String reviewerAccountId,
      String safeMonitoringSummary,
      String sourceWorkTraceId) {
    requireTenant(authContext, proposal);
    var decision =
        decision(
            proposal,
            reviewerAccountId,
            ReferenceImprovementDecision.DecisionType.MONITOR,
            false,
            safeMonitoringSummary);
    recordDecisionTrace(proposal, decision, sourceWorkTraceId);
    recordLifecycleTrace(
        proposal,
        ReferenceImprovementTrace.TraceEvent.MONITORING_RECORDED,
        safeMonitoringSummary,
        sourceWorkTraceId);
    return new DecisionResult(proposal, decision, null);
  }

  public DecisionResult rollback(
      ReferenceAuthContext authContext,
      ReferenceImprovementProposal proposal,
      String reviewerAccountId,
      String reason,
      String sourceWorkTraceId) {
    requireTenant(authContext, proposal);
    if (proposal.status() != ReferenceImprovementProposal.ImprovementStatus.ACTIVATED) {
      var blockedReason = "rollback blocked: proposal is not activated";
      var decision =
          decision(
              proposal,
              reviewerAccountId,
              ReferenceImprovementDecision.DecisionType.REQUEST_CHANGES,
              false,
              blockedReason);
      recordDecisionTrace(proposal, decision, sourceWorkTraceId);
      return new DecisionResult(proposal, decision, blockedReason);
    }

    var updatedProposal = withStatus(proposal, ReferenceImprovementProposal.ImprovementStatus.ROLLED_BACK);
    var decision =
        decision(
            updatedProposal,
            reviewerAccountId,
            ReferenceImprovementDecision.DecisionType.ROLLBACK,
            false,
            reason + " Rollback baseline: " + proposal.rollbackBaselineId() + ".");
    recordDecisionTrace(updatedProposal, decision, sourceWorkTraceId);
    recordLifecycleTrace(
        updatedProposal,
        ReferenceImprovementTrace.TraceEvent.ROLLED_BACK,
        "Rolled back governed improvement to explicit rollback baseline "
            + proposal.rollbackBaselineId()
            + ".",
        sourceWorkTraceId);
    return new DecisionResult(updatedProposal, decision, null);
  }

  private void requireTenant(ReferenceAuthContext authContext, ReferenceImprovementProposal proposal) {
    if (!authContext.tenantId().equals(proposal.tenantId())) {
      throw new IllegalArgumentException("cross-tenant improvement decision denied");
    }
  }

  private ReferenceImprovementDecision decision(
      ReferenceImprovementProposal proposal,
      String reviewerAccountId,
      ReferenceImprovementDecision.DecisionType decisionType,
      boolean replayEvidenceRequired,
      String reason) {
    return new ReferenceImprovementDecision(
        proposal.tenantId(),
        proposal.improvementProposalId(),
        "improvement-decision-"
            + decisionType.name().toLowerCase().replace('_', '-')
            + "-"
            + proposal.improvementProposalId(),
        reviewerAccountId,
        decisionType,
        replayEvidenceRequired,
        reason,
        proposal.correlationId());
  }

  private void recordDecisionTrace(
      ReferenceImprovementProposal proposal,
      ReferenceImprovementDecision decision,
      String sourceWorkTraceId) {
    recordLifecycleTrace(
        proposal,
        ReferenceImprovementTrace.TraceEvent.DECISION_RECORDED,
        "Recorded improvement decision "
            + decision.decisionType()
            + " for proposal status "
            + proposal.status()
            + ": "
            + decision.reason(),
        sourceWorkTraceId);
  }

  private void recordLifecycleTrace(
      ReferenceImprovementProposal proposal,
      ReferenceImprovementTrace.TraceEvent event,
      String safeSummary,
      String sourceWorkTraceId) {
    traceSink.recordImprovement(
        new ReferenceImprovementTrace(
            proposal.tenantId(),
            "improvement-trace-"
                + event.name().toLowerCase().replace('_', '-')
                + "-"
                + proposal.improvementProposalId()
                + "-"
                + (traceSink.improvementTraces().size() + 1),
            proposal.improvementProposalId(),
            proposal.evaluationRunIds().getFirst(),
            sourceWorkTraceId,
            event,
            safeSummary,
            proposal.correlationId()));
  }

  private static ReferenceImprovementProposal withStatus(
      ReferenceImprovementProposal proposal, ReferenceImprovementProposal.ImprovementStatus status) {
    return new ReferenceImprovementProposal(
        proposal.tenantId(),
        proposal.improvementProposalId(),
        proposal.targetAgentDefinitionId(),
        proposal.evaluationRunIds(),
        proposal.findingIds(),
        proposal.behaviorEditProposalId(),
        proposal.proposedChangeKind(),
        status,
        proposal.expectedOutcome(),
        proposal.rollbackBaselineId(),
        proposal.replaySimulationIds(),
        proposal.correlationId());
  }

  public record DecisionResult(
      ReferenceImprovementProposal proposal,
      ReferenceImprovementDecision decision,
      String blockedReason) {}
}
