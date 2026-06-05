package ai.first.application.foundation.governance;

import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.governance.GovernancePolicyProposal;
import ai.first.domain.foundation.governance.GovernancePolicySimulationResult;
import java.util.List;
import java.util.Optional;

/** Tenant-scoped deterministic storage boundary for Governance/Policy proposal lifecycle records. */
public interface GovernancePolicyRepository {
  Optional<GovernancePolicyProposal> findProposal(String tenantId, String customerId, String proposalId);

  Optional<GovernancePolicyProposal> findByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey);

  GovernancePolicyProposal saveProposal(GovernancePolicyProposal proposal);

  List<GovernancePolicyProposal> listProposals(String tenantId, String customerId);

  Optional<GovernancePolicySimulationResult> findSimulation(String tenantId, String customerId, String simulationId);

  Optional<GovernancePolicySimulationResult> findSimulationByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey);

  GovernancePolicySimulationResult saveSimulation(GovernancePolicySimulationResult simulation);

  List<GovernancePolicySimulationResult> listSimulations(String tenantId, String customerId, String proposalId);
}
