package ai.first.application.agentfoundation;

import ai.first.domain.agentfoundation.ReferenceAuthContext;
import ai.first.domain.agentfoundation.ReferenceImprovementProposal;
import ai.first.domain.agentfoundation.ReferenceImprovementTrace;
import ai.first.domain.agentfoundation.ReferenceReplaySimulation;
import java.util.List;
import java.util.Map;

/**
 * Deterministic reference helper for closed-loop improvement replay/simulation evidence.
 *
 * <p>The service compares the current rollback baseline with the proposed candidate behavior using
 * fixture scores. It attaches replay evidence to a new proposal value, records safe trace facts, and
 * keeps activation blocked unless passing replay evidence is present.
 */
public final class ReferenceReplaySimulationService {
  private final Map<String, Integer> deterministicScores;
  private final ReferenceTraceSink traceSink;

  public ReferenceReplaySimulationService(
      Map<String, Integer> deterministicScores, ReferenceTraceSink traceSink) {
    this.deterministicScores = Map.copyOf(deterministicScores);
    this.traceSink = traceSink;
  }

  public SimulationEvidence simulate(
      ReferenceAuthContext authContext,
      ReferenceImprovementProposal proposal,
      String candidateReferenceId,
      String sourceWorkTraceId) {
    if (!authContext.tenantId().equals(proposal.tenantId())) {
      throw new IllegalArgumentException("cross-tenant replay/simulation denied");
    }

    int baselineScore = deterministicScores.getOrDefault(proposal.rollbackBaselineId(), 0);
    int candidateScore = deterministicScores.getOrDefault(candidateReferenceId, 0);
    boolean passed = candidateScore > baselineScore && candidateScore >= 80;
    var riskNotes = riskNotes(passed, baselineScore, candidateScore);
    var simulationId = "replay-simulation-" + proposal.improvementProposalId();
    var simulation =
        new ReferenceReplaySimulation(
            proposal.tenantId(),
            simulationId,
            proposal.improvementProposalId(),
            proposal.rollbackBaselineId(),
            candidateReferenceId,
            passed,
            baselineScore,
            candidateScore,
            riskNotes,
            safeSummary(passed, baselineScore, candidateScore),
            proposal.correlationId());

    traceSink.recordImprovement(
        new ReferenceImprovementTrace(
            proposal.tenantId(),
            "improvement-trace-replay-simulated-" + simulationId,
            proposal.improvementProposalId(),
            proposal.evaluationRunIds().getFirst(),
            sourceWorkTraceId,
            ReferenceImprovementTrace.TraceEvent.REPLAY_SIMULATED,
            "Recorded replay/simulation evidence "
                + simulationId
                + "; activation blocked="
                + activationBlocked(proposalWithSimulation(proposal, simulation), List.of(simulation))
                + ".",
            proposal.correlationId()));

    var updatedProposal = proposalWithSimulation(proposal, simulation);
    return new SimulationEvidence(
        updatedProposal, simulation, activationBlocked(updatedProposal, List.of(simulation)));
  }

  public boolean activationBlocked(
      ReferenceImprovementProposal proposal, List<ReferenceReplaySimulation> simulations) {
    return activationBlockedReason(proposal, simulations) != null;
  }

  public String activationBlockedReason(
      ReferenceImprovementProposal proposal, List<ReferenceReplaySimulation> simulations) {
    var attachedSimulations =
        simulations.stream()
            .filter(simulation -> simulation.tenantId().equals(proposal.tenantId()))
            .filter(simulation -> simulation.improvementProposalId().equals(proposal.improvementProposalId()))
            .filter(simulation -> proposal.replaySimulationIds().contains(simulation.simulationId()))
            .toList();
    if (attachedSimulations.isEmpty()) {
      return "activation blocked: missing replay/simulation evidence";
    }
    if (attachedSimulations.stream().noneMatch(ReferenceReplaySimulation::passed)) {
      return "activation blocked: replay/simulation evidence is failing";
    }
    return null;
  }

  private static ReferenceImprovementProposal proposalWithSimulation(
      ReferenceImprovementProposal proposal, ReferenceReplaySimulation simulation) {
    var simulationIds =
        proposal.replaySimulationIds().contains(simulation.simulationId())
            ? proposal.replaySimulationIds()
            : concat(proposal.replaySimulationIds(), simulation.simulationId());
    return new ReferenceImprovementProposal(
        proposal.tenantId(),
        proposal.improvementProposalId(),
        proposal.targetAgentDefinitionId(),
        proposal.evaluationRunIds(),
        proposal.findingIds(),
        proposal.behaviorEditProposalId(),
        proposal.proposedChangeKind(),
        proposal.status(),
        proposal.expectedOutcome() + " Replay/simulation evidence attached: " + simulation.safeSummary(),
        proposal.rollbackBaselineId(),
        simulationIds,
        proposal.correlationId());
  }

  private static List<String> concat(List<String> existing, String next) {
    var updated = new java.util.ArrayList<>(existing);
    updated.add(next);
    return List.copyOf(updated);
  }

  private static List<String> riskNotes(boolean passed, int baselineScore, int candidateScore) {
    if (passed) {
      return List.of(
          "Candidate improved score from " + baselineScore + " to " + candidateScore + ".",
          "No deterministic replay risk detected; authority still requires separate approval.");
    }
    return List.of(
        "Candidate score " + candidateScore + " did not satisfy replay threshold.",
        "Activation remains blocked until passing evidence exists.");
  }

  private static String safeSummary(boolean passed, int baselineScore, int candidateScore) {
    return passed
        ? "Candidate behavior passed replay/simulation against current behavior."
        : "Candidate behavior failed replay/simulation against current behavior.";
  }

  public record SimulationEvidence(
      ReferenceImprovementProposal proposalWithEvidence,
      ReferenceReplaySimulation replaySimulation,
      boolean activationBlocked) {}
}
