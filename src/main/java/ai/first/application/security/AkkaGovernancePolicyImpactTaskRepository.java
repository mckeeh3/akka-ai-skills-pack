package ai.first.application.security;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.security.GovernancePolicyImpactTask;
import java.util.Optional;

/** Akka-backed Governance/Policy impact task lifecycle adapter for normal starter runtime paths. */
public final class AkkaGovernancePolicyImpactTaskRepository implements GovernancePolicyImpactTaskRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaGovernancePolicyImpactTaskRepository(ComponentClient componentClient) {
    this(componentClient, DurableGovernancePolicyImpactTaskRepositoryEntity.ENTITY_ID);
  }

  public AkkaGovernancePolicyImpactTaskRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<GovernancePolicyImpactTask> find(String impactTaskId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyImpactTaskRepositoryEntity::find).invoke(impactTaskId);
  }

  @Override
  public Optional<GovernancePolicyImpactTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyImpactTaskRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableGovernancePolicyImpactTaskRepositoryEntity.IdempotencyQuery(tenantId, accountId, idempotencyKey));
  }

  @Override
  public GovernancePolicyImpactTask save(GovernancePolicyImpactTask task) {
    return componentClient.forKeyValueEntity(entityId).method(DurableGovernancePolicyImpactTaskRepositoryEntity::save).invoke(task);
  }
}
