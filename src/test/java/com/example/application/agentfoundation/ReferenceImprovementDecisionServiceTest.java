package com.example.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.domain.agentfoundation.ReferenceImprovementDecision;
import com.example.domain.agentfoundation.ReferenceImprovementProposal;
import com.example.domain.agentfoundation.ReferenceImprovementTrace;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReferenceImprovementDecisionServiceTest {

  @Test
  void approvalRequiresPassingReplayEvidence() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);

    var result =
        service.approve(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.safeImprovementProposal(),
            List.of(),
            "account-reviewer-1",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.DRAFT, result.proposal().status());
    assertEquals(ReferenceImprovementDecision.DecisionType.REQUEST_CHANGES, result.decision().decisionType());
    assertTrue(result.blockedReason().contains("missing replay/simulation evidence"));
    assertDecisionTraceRecorded(traceSink);
  }

  @Test
  void approveRecordsDecisionButDoesNotActivate() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);
    var evidence = passingEvidence(traceSink);

    var result =
        service.approve(
            ReferenceAgentFoundationFixtures.authContext(),
            evidence.proposalWithEvidence(),
            List.of(evidence.replaySimulation()),
            "account-reviewer-1",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.APPROVED, result.proposal().status());
    assertEquals(ReferenceImprovementDecision.DecisionType.APPROVE, result.decision().decisionType());
    assertNull(result.blockedReason());
    assertTrue(result.decision().replayEvidenceRequired());
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.DECISION_RECORDED));
    assertTrue(
        traceSink.improvementTraces().stream()
            .noneMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.ACTIVATED));
  }

  @Test
  void activationRequiresApproval() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);
    var evidence = passingEvidence(traceSink);

    var result =
        service.activate(
            ReferenceAgentFoundationFixtures.authContext(),
            evidence.proposalWithEvidence(),
            List.of(evidence.replaySimulation()),
            "account-reviewer-1",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.DRAFT, result.proposal().status());
    assertEquals("activation blocked: proposal is not approved", result.blockedReason());
    assertEquals(ReferenceImprovementDecision.DecisionType.REQUEST_CHANGES, result.decision().decisionType());
    assertDecisionTraceRecorded(traceSink);
  }

  @Test
  void activationRequiresReplayEvidenceEvenAfterApproval() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);
    var approved =
        new ReferenceImprovementProposal(
            ReferenceAgentFoundationFixtures.TENANT_ID,
            ReferenceAgentFoundationFixtures.IMPROVEMENT_PROPOSAL_ID,
            ReferenceAgentFoundationFixtures.AGENT_ID,
            List.of(ReferenceAgentFoundationFixtures.EVALUATION_RUN_ID),
            List.of(ReferenceAgentFoundationFixtures.EVALUATION_FINDING_ID),
            ReferenceAgentFoundationFixtures.BEHAVIOR_PROPOSAL_ID,
            "prompt_wording_change",
            ReferenceImprovementProposal.ImprovementStatus.APPROVED,
            "Approved copy without attached replay evidence should still be blocked.",
            ReferenceAgentFoundationFixtures.ROLLBACK_BASELINE_ID,
            List.of(),
            "corr-improvement-rainy-day");

    var result =
        service.activate(
            ReferenceAgentFoundationFixtures.authContext(),
            approved,
            List.of(),
            "account-reviewer-1",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.APPROVED, result.proposal().status());
    assertTrue(result.blockedReason().contains("missing replay/simulation evidence"));
    assertDecisionTraceRecorded(traceSink);
  }

  @Test
  void activateApprovedProposalRecordsActivationTrace() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);
    var evidence = passingEvidence(traceSink);
    var approved =
        service
            .approve(
                ReferenceAgentFoundationFixtures.authContext(),
                evidence.proposalWithEvidence(),
                List.of(evidence.replaySimulation()),
                "account-reviewer-1",
                ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID)
            .proposal();

    var activated =
        service.activate(
            ReferenceAgentFoundationFixtures.authContext(),
            approved,
            List.of(evidence.replaySimulation()),
            "account-reviewer-1",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.ACTIVATED, activated.proposal().status());
    assertEquals(ReferenceImprovementDecision.DecisionType.ACTIVATE, activated.decision().decisionType());
    assertNull(activated.blockedReason());
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.ACTIVATED));
  }

  @Test
  void rejectedAndRequestChangesProposalsDoNotActivate() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);
    var evidence = passingEvidence(traceSink);

    var rejected =
        service.reject(
            ReferenceAgentFoundationFixtures.authContext(),
            evidence.proposalWithEvidence(),
            "account-reviewer-1",
            "Rejected because proposed copy does not match tenant tone guidance.",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);
    var requestedChanges =
        service.requestChanges(
            ReferenceAgentFoundationFixtures.authContext(),
            evidence.proposalWithEvidence(),
            "account-reviewer-1",
            "Request shorter example coverage before activation.",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.REJECTED, rejected.proposal().status());
    assertEquals(ReferenceImprovementProposal.ImprovementStatus.REQUEST_CHANGES, requestedChanges.proposal().status());
    assertTrue(rejected.blockedReason().contains("rejected"));
    assertTrue(requestedChanges.blockedReason().contains("requested changes"));

    var rejectedActivation =
        service.activate(
            ReferenceAgentFoundationFixtures.authContext(),
            rejected.proposal(),
            List.of(evidence.replaySimulation()),
            "account-reviewer-1",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);
    var requestedChangesActivation =
        service.activate(
            ReferenceAgentFoundationFixtures.authContext(),
            requestedChanges.proposal(),
            List.of(evidence.replaySimulation()),
            "account-reviewer-1",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals("activation blocked: proposal is not approved", rejectedActivation.blockedReason());
    assertEquals("activation blocked: proposal is not approved", requestedChangesActivation.blockedReason());
  }

  @Test
  void monitorRecordsTraceWithoutChangingStatus() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);
    var evidence = passingEvidence(traceSink);

    var result =
        service.monitor(
            ReferenceAgentFoundationFixtures.authContext(),
            evidence.proposalWithEvidence(),
            "account-reviewer-1",
            "Monitoring shows no regression in first deterministic observation window.",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals(evidence.proposalWithEvidence().status(), result.proposal().status());
    assertEquals(ReferenceImprovementDecision.DecisionType.MONITOR, result.decision().decisionType());
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.MONITORING_RECORDED));
  }

  @Test
  void rollbackIsExplicitAndRequiresActivatedProposal() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);
    var evidence = passingEvidence(traceSink);

    var blockedRollback =
        service.rollback(
            ReferenceAgentFoundationFixtures.authContext(),
            evidence.proposalWithEvidence(),
            "account-reviewer-1",
            "Attempted rollback before activation.",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals("rollback blocked: proposal is not activated", blockedRollback.blockedReason());

    var approved =
        service
            .approve(
                ReferenceAgentFoundationFixtures.authContext(),
                evidence.proposalWithEvidence(),
                List.of(evidence.replaySimulation()),
                "account-reviewer-1",
                ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID)
            .proposal();
    var activated =
        service
            .activate(
                ReferenceAgentFoundationFixtures.authContext(),
                approved,
                List.of(evidence.replaySimulation()),
                "account-reviewer-1",
                ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID)
            .proposal();

    var rollback =
        service.rollback(
            ReferenceAgentFoundationFixtures.authContext(),
            activated,
            "account-reviewer-1",
            "Outcome monitoring detected regression.",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.ROLLED_BACK, rollback.proposal().status());
    assertEquals(ReferenceImprovementDecision.DecisionType.ROLLBACK, rollback.decision().decisionType());
    assertTrue(rollback.decision().reason().contains(ReferenceAgentFoundationFixtures.ROLLBACK_BASELINE_ID));
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.ROLLED_BACK));
  }

  @Test
  void deniesCrossTenantImprovementDecision() {
    var traceSink = new ReferenceTraceSink();
    var service = decisionService(traceSink);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            service.reject(
                ReferenceAgentFoundationFixtures.crossTenantAuthContext(),
                ReferenceAgentFoundationFixtures.safeImprovementProposal(),
                "account-reviewer-1",
                "Cross tenant rejection should be denied.",
                ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID));
  }

  private static ReferenceImprovementDecisionService decisionService(ReferenceTraceSink traceSink) {
    return new ReferenceImprovementDecisionService(replayService(traceSink), traceSink);
  }

  private static ReferenceReplaySimulationService replayService(ReferenceTraceSink traceSink) {
    return new ReferenceReplaySimulationService(
        Map.of(
            ReferenceAgentFoundationFixtures.ROLLBACK_BASELINE_ID,
            58,
            "candidate-prompt-version-draft-safe-wording",
            91),
        traceSink);
  }

  private static ReferenceReplaySimulationService.SimulationEvidence passingEvidence(
      ReferenceTraceSink traceSink) {
    return replayService(traceSink)
        .simulate(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.safeImprovementProposal(),
            "candidate-prompt-version-draft-safe-wording",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);
  }

  private static void assertDecisionTraceRecorded(ReferenceTraceSink traceSink) {
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.DECISION_RECORDED));
  }
}
