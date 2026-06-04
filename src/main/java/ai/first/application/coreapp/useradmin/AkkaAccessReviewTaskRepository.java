package ai.first.application.coreapp.useradmin;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import java.util.Optional;

/** Akka-backed User Admin access-review task lifecycle adapter for normal starter runtime paths. */
public final class AkkaAccessReviewTaskRepository implements AccessReviewTaskRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaAccessReviewTaskRepository(ComponentClient componentClient) {
    this(componentClient, DurableAccessReviewTaskRepositoryEntity.ENTITY_ID);
  }

  public AkkaAccessReviewTaskRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<AccessReviewTask> find(String taskId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAccessReviewTaskRepositoryEntity::find).invoke(taskId);
  }

  @Override
  public Optional<AccessReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAccessReviewTaskRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableAccessReviewTaskRepositoryEntity.IdempotencyQuery(tenantId, accountId, idempotencyKey));
  }

  @Override
  public AccessReviewTask save(AccessReviewTask task) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAccessReviewTaskRepositoryEntity::save).invoke(task);
  }
}
