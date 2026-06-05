package com.example.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.domain.agentfoundation.ReferenceImprovementTrace;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReferenceReplaySimulationServiceTest {

  @Test
  void attachesPassingReplaySimulationEvidenceToImprovementProposal() {
    var traceSink = new ReferenceTraceSink();
    var service = passingService(traceSink);
    var proposal = ReferenceAgentFoundationFixtures.safeImprovementProposal();

    var evidence =
        service.simulate(
            ReferenceAgentFoundationFixtures.authContext(),
            proposal,
            "candidate-prompt-version-draft-safe-wording",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertFalse(evidence.activationBlocked());
    assertEquals(proposal.improvementProposalId(), evidence.replaySimulation().improvementProposalId());
    assertTrue(evidence.replaySimulation().passed());
    assertEquals(58, evidence.replaySimulation().baselineScore());
    assertEquals(91, evidence.replaySimulation().candidateScore());
    assertTrue(evidence.replaySimulation().safeSummary().contains("passed replay/simulation"));
    assertTrue(evidence.proposalWithEvidence().replaySimulationIds().contains(evidence.replaySimulation().simulationId()));
    assertTrue(evidence.proposalWithEvidence().expectedOutcome().contains("Replay/simulation evidence attached"));
  }

  @Test
  void recordsReplaySimulationTraceFactWithSafeEvidenceSummary() {
    var traceSink = new ReferenceTraceSink();
    var service = passingService(traceSink);

    var evidence =
        service.simulate(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.safeImprovementProposal(),
            "candidate-prompt-version-draft-safe-wording",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.REPLAY_SIMULATED));
    var trace = traceSink.improvementTraces().getFirst();
    assertEquals(evidence.proposalWithEvidence().improvementProposalId(), trace.improvementProposalId());
    assertEquals(ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID, trace.sourceWorkTraceId());
    assertTrue(trace.safeSummary().contains("replay/simulation evidence"));
    assertTrue(trace.safeSummary().contains("activation blocked=false"));
  }

  @Test
  void activationBlockedWhenReplaySimulationEvidenceIsMissing() {
    var traceSink = new ReferenceTraceSink();
    var service = passingService(traceSink);
    var proposal = ReferenceAgentFoundationFixtures.safeImprovementProposal();

    assertTrue(service.activationBlocked(proposal, List.of()));
    assertEquals(
        "activation blocked: missing replay/simulation evidence",
        service.activationBlockedReason(proposal, List.of()));
  }

  @Test
  void activationBlockedWhenReplaySimulationEvidenceIsFailing() {
    var traceSink = new ReferenceTraceSink();
    var service = failingService(traceSink);

    var evidence =
        service.simulate(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.safeImprovementProposal(),
            "candidate-prompt-version-draft-regression",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertTrue(evidence.activationBlocked());
    assertFalse(evidence.replaySimulation().passed());
    assertTrue(evidence.replaySimulation().riskNotes().stream().anyMatch(note -> note.contains("Activation remains blocked")));
    assertEquals(
        "activation blocked: replay/simulation evidence is failing",
        service.activationBlockedReason(evidence.proposalWithEvidence(), List.of(evidence.replaySimulation())));
  }

  @Test
  void activationAllowedWhenPassingReplayEvidenceIsAttached() {
    var traceSink = new ReferenceTraceSink();
    var service = passingService(traceSink);

    var evidence =
        service.simulate(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.safeImprovementProposal(),
            "candidate-prompt-version-draft-safe-wording",
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID);

    assertNull(
        service.activationBlockedReason(
            evidence.proposalWithEvidence(), List.of(evidence.replaySimulation())));
  }

  @Test
  void deniesCrossTenantReplaySimulation() {
    var traceSink = new ReferenceTraceSink();
    var service = passingService(traceSink);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            service.simulate(
                ReferenceAgentFoundationFixtures.crossTenantAuthContext(),
                ReferenceAgentFoundationFixtures.safeImprovementProposal(),
                "candidate-prompt-version-draft-safe-wording",
                ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID));
  }

  private static ReferenceReplaySimulationService passingService(ReferenceTraceSink traceSink) {
    return new ReferenceReplaySimulationService(
        Map.of(
            ReferenceAgentFoundationFixtures.ROLLBACK_BASELINE_ID,
            58,
            "candidate-prompt-version-draft-safe-wording",
            91),
        traceSink);
  }

  private static ReferenceReplaySimulationService failingService(ReferenceTraceSink traceSink) {
    return new ReferenceReplaySimulationService(
        Map.of(
            ReferenceAgentFoundationFixtures.ROLLBACK_BASELINE_ID,
            58,
            "candidate-prompt-version-draft-regression",
            61),
        traceSink);
  }
}
