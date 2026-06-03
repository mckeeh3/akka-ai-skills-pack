package ai.first.application.security;

import ai.first.domain.security.GovernancePolicyProposal;
import ai.first.domain.security.GovernancePolicySimulationResult;
import java.util.List;
import java.util.Optional;

/** Fail-closed governance policy port until an Akka-backed policy repository is bound. */
public final class FailClosedGovernancePolicyRepository implements GovernancePolicyRepository {
  private IllegalStateException unavailable() {
    return FailClosedFoundationRuntime.unavailable("GovernancePolicyRepository");
  }

  @Override public Optional<GovernancePolicyProposal> findProposal(String tenantId, String customerId, String proposalId) { throw unavailable(); }
  @Override public Optional<GovernancePolicyProposal> findByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) { throw unavailable(); }
  @Override public GovernancePolicyProposal saveProposal(GovernancePolicyProposal proposal) { throw unavailable(); }
  @Override public List<GovernancePolicyProposal> listProposals(String tenantId, String customerId) { throw unavailable(); }
  @Override public Optional<GovernancePolicySimulationResult> findSimulation(String tenantId, String customerId, String simulationId) { throw unavailable(); }
  @Override public Optional<GovernancePolicySimulationResult> findSimulationByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) { throw unavailable(); }
  @Override public GovernancePolicySimulationResult saveSimulation(GovernancePolicySimulationResult simulation) { throw unavailable(); }
  @Override public List<GovernancePolicySimulationResult> listSimulations(String tenantId, String customerId, String proposalId) { throw unavailable(); }
}
