package ai.first.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.agentfoundation.ReferenceBehaviorEditDecision;
import ai.first.domain.agentfoundation.ReferenceBehaviorEditTrace;
import ai.first.domain.agentfoundation.ReferencePromptVersion;
import ai.first.domain.agentfoundation.ReferenceSkillVersion;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReferenceBehaviorEditReviewServiceTest {

  @Test
  void approveRecordsReviewTraceButDoesNotMutateActiveRuntimeRecords() {
    var traceSink = new ReferenceTraceSink();
    var service = new ReferenceBehaviorEditReviewService(traceSink);
    var promptVersions = promptVersions();
    var skillVersions = skillVersions();
    var activePromptBefore = promptVersions.get(ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID);
    var activeSkillBefore = skillVersions.get("skill-version-active");

    var decision =
        service.approve(
            ReferenceAgentFoundationFixtures.safeWordingProposal(),
            "account-reviewer-1",
            "Approve wording-only proposal for later governed activation.");

    assertEquals(ReferenceBehaviorEditDecision.DecisionType.APPROVE, decision.decisionType());
    assertEquals(ReferenceAgentFoundationFixtures.BEHAVIOR_PROPOSAL_ID, decision.proposalId());
    assertEquals(1, traceSink.behaviorEditTraces().size());
    assertEquals(
        ReferenceBehaviorEditTrace.TraceEvent.REVIEW_APPROVED,
        traceSink.behaviorEditTraces().getFirst().event());
    assertTrue(traceSink.behaviorEditTraces().getFirst().safeSummary().contains("governed activation"));

    // Review approval does not directly mutate active runtime records in this reference slice.
    assertSame(activePromptBefore, promptVersions.get(ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID));
    assertSame(activeSkillBefore, skillVersions.get("skill-version-active"));
    assertEquals(ReferencePromptVersion.VersionStatus.ACTIVE, activePromptBefore.status());
    assertEquals(ReferenceSkillVersion.VersionStatus.ACTIVE, activeSkillBefore.status());
  }

  @Test
  void rejectPreservesProposalHistoryAndEmitsTrace() {
    var traceSink = new ReferenceTraceSink();
    var service = new ReferenceBehaviorEditReviewService(traceSink);
    var proposal = ReferenceAgentFoundationFixtures.safeWordingProposal();

    var decision = service.reject(proposal, "account-reviewer-1", "Rejected pending clearer wording.");

    assertEquals(ReferenceBehaviorEditDecision.DecisionType.REJECT, decision.decisionType());
    assertEquals(proposal.proposalId(), decision.proposalId());
    assertFalse(proposal.proposedDiffs().isEmpty());
    assertEquals(ReferenceAgentFoundationFixtures.safePromptDiff(), proposal.proposedDiffs().getFirst());
    assertEquals(1, traceSink.behaviorEditTraces().size());
    assertEquals(
        ReferenceBehaviorEditTrace.TraceEvent.REVIEW_REJECTED,
        traceSink.behaviorEditTraces().getFirst().event());
  }

  @Test
  void requestChangesPreservesProposalHistoryAndEmitsTrace() {
    var traceSink = new ReferenceTraceSink();
    var service = new ReferenceBehaviorEditReviewService(traceSink);
    var proposal = ReferenceAgentFoundationFixtures.safeWordingProposal();

    var decision =
        service.requestChanges(
            proposal, "account-reviewer-1", "Request changes: include an acceptance test suggestion.");

    assertEquals(ReferenceBehaviorEditDecision.DecisionType.REQUEST_CHANGES, decision.decisionType());
    assertEquals(proposal.proposalId(), decision.proposalId());
    assertEquals(1, proposal.proposedDiffs().size());
    assertEquals(ReferenceAgentFoundationFixtures.safePromptDiff(), proposal.proposedDiffs().getFirst());
    assertEquals(1, traceSink.behaviorEditTraces().size());
    assertEquals(
        ReferenceBehaviorEditTrace.TraceEvent.REVIEW_CHANGES_REQUESTED,
        traceSink.behaviorEditTraces().getFirst().event());
  }

  @Test
  void authorityExpansionApprovalIsEscalatedToDecisionCardRequiredReview() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);
    var service = new ReferenceBehaviorEditReviewService(traceSink);
    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.authorityExpansionRequest());

    var decision = service.approve(proposal, "account-reviewer-1", "Looks acceptable to me.");

    assertTrue(proposal.authorityExpansionDetected());
    assertTrue(proposal.decisionCardRequired());
    assertEquals(ReferenceBehaviorEditDecision.DecisionType.ESCALATE, decision.decisionType());
    assertTrue(decision.reason().contains("Authority expansion requires decision-card escalation"));
    assertEquals(
        ReferenceBehaviorEditTrace.TraceEvent.ESCALATED,
        traceSink.behaviorEditTraces().getLast().event());
  }

  @Test
  void explicitEscalationEmitsTraceForHighRiskProposal() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);
    var service = new ReferenceBehaviorEditReviewService(traceSink);
    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.toolBoundaryExpansionRequest());

    var decision = service.escalate(proposal, "account-reviewer-1", "Escalate tool expansion to decision card.");

    assertEquals(ReferenceBehaviorEditDecision.DecisionType.ESCALATE, decision.decisionType());
    assertEquals(proposal.proposalId(), decision.proposalId());
    assertEquals(
        ReferenceBehaviorEditTrace.TraceEvent.ESCALATED,
        traceSink.behaviorEditTraces().getLast().event());
    assertTrue(traceSink.behaviorEditTraces().getLast().safeSummary().contains("decision card"));
  }

  @Test
  void reviewOutcomeTraceFactsAreEmittedForApproveRejectRequestChangesAndEscalate() {
    var traceSink = new ReferenceTraceSink();
    var service = new ReferenceBehaviorEditReviewService(traceSink);
    var proposal = ReferenceAgentFoundationFixtures.safeWordingProposal();

    service.approve(proposal, "account-reviewer-1", "approve");
    service.reject(proposal, "account-reviewer-1", "reject");
    service.requestChanges(proposal, "account-reviewer-1", "request changes");
    service.escalate(proposal, "account-reviewer-1", "escalate");

    assertEquals(4, traceSink.behaviorEditTraces().size());
    assertTrue(
        traceSink.behaviorEditTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceBehaviorEditTrace.TraceEvent.REVIEW_APPROVED));
    assertTrue(
        traceSink.behaviorEditTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceBehaviorEditTrace.TraceEvent.REVIEW_REJECTED));
    assertTrue(
        traceSink.behaviorEditTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceBehaviorEditTrace.TraceEvent.REVIEW_CHANGES_REQUESTED));
    assertTrue(
        traceSink.behaviorEditTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceBehaviorEditTrace.TraceEvent.ESCALATED));
  }

  private static ReferenceAgentBehaviorEditor editor(ReferenceTraceSink traceSink) {
    return new ReferenceAgentBehaviorEditor(
        Map.of(
            ReferenceAgentFoundationFixtures.AGENT_ID,
            ReferenceAgentFoundationFixtures.activeAgent(),
            ReferenceAgentFoundationFixtures.DISABLED_AGENT_ID,
            ReferenceAgentFoundationFixtures.disabledAgent()),
        promptVersions(),
        Map.of(
            ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID,
            ReferenceAgentFoundationFixtures.activeAssignedSkillDocument(),
            ReferenceAgentFoundationFixtures.UNASSIGNED_SKILL_ID,
            ReferenceAgentFoundationFixtures.unassignedSkillDocument()),
        skillVersions(),
        Map.of(
            ReferenceAgentFoundationFixtures.SKILL_MANIFEST_ID,
            ReferenceAgentFoundationFixtures.activeManifest()),
        Map.of(
            ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
            ReferenceAgentFoundationFixtures.activeToolBoundary()),
        traceSink);
  }

  private static Map<String, ReferencePromptVersion> promptVersions() {
    return Map.of(
        ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID,
        ReferenceAgentFoundationFixtures.activePromptVersion());
  }

  private static Map<String, ReferenceSkillVersion> skillVersions() {
    return Map.of(
        "skill-version-active",
        ReferenceAgentFoundationFixtures.activeAssignedSkillVersion(),
        "skill-version-unassigned",
        ReferenceAgentFoundationFixtures.unassignedSkillVersion());
  }
}
