package ai.first.application.security;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.foundation.governance.GovernancePolicyProposal;
import ai.first.domain.foundation.governance.GovernancePolicySimulationResult;
import java.util.List;
import java.util.Optional;

/** Akka-backed Governance/Policy proposal lifecycle adapter for normal starter runtime paths. */
public final class AkkaGovernancePolicyRepository implements GovernancePolicyRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaGovernancePolicyRepository(ComponentClient componentClient) {
    this(componentClient, DurableGovernancePolicyRepositoryEntity.ENTITY_ID);
  }

  public AkkaGovernancePolicyRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<GovernancePolicyProposal> findProposal(String tenantId, String customerId, String proposalId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyRepositoryEntity::findProposal)
        .invoke(new DurableGovernancePolicyRepositoryEntity.ProposalQuery(tenantId, customerId, proposalId));
  }

  @Override
  public Optional<GovernancePolicyProposal> findByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableGovernancePolicyRepositoryEntity.IdempotencyQuery(tenantId, customerId, accountId, idempotencyKey));
  }

  @Override
  public GovernancePolicyProposal saveProposal(GovernancePolicyProposal proposal) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyRepositoryEntity::saveProposal).invoke(proposal);
  }

  @Override
  public List<GovernancePolicyProposal> listProposals(String tenantId, String customerId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyRepositoryEntity::listProposals)
        .invoke(new DurableGovernancePolicyRepositoryEntity.ListQuery(tenantId, customerId));
  }

  @Override
  public Optional<GovernancePolicySimulationResult> findSimulation(String tenantId, String customerId, String simulationId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyRepositoryEntity::findSimulation)
        .invoke(new DurableGovernancePolicyRepositoryEntity.SimulationQuery(tenantId, customerId, simulationId));
  }

  @Override
  public Optional<GovernancePolicySimulationResult> findSimulationByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyRepositoryEntity::findSimulationByIdempotencyKey)
        .invoke(new DurableGovernancePolicyRepositoryEntity.IdempotencyQuery(tenantId, customerId, accountId, idempotencyKey));
  }

  @Override
  public GovernancePolicySimulationResult saveSimulation(GovernancePolicySimulationResult simulation) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyRepositoryEntity::saveSimulation).invoke(simulation);
  }

  @Override
  public List<GovernancePolicySimulationResult> listSimulations(String tenantId, String customerId, String proposalId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyRepositoryEntity::listSimulations)
        .invoke(new DurableGovernancePolicyRepositoryEntity.SimulationListQuery(tenantId, customerId, proposalId));
  }
}
