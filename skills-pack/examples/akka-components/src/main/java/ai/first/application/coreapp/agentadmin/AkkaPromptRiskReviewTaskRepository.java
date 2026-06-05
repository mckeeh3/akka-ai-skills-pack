package ai.first.application.coreapp.agentadmin;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import java.util.Optional;

/** Akka-backed Agent Admin prompt-risk task lifecycle adapter for normal starter runtime paths. */
public final class AkkaPromptRiskReviewTaskRepository implements PromptRiskReviewTaskRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaPromptRiskReviewTaskRepository(ComponentClient componentClient) {
    this(componentClient, DurablePromptRiskReviewTaskRepositoryEntity.ENTITY_ID);
  }

  public AkkaPromptRiskReviewTaskRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<PromptRiskReviewTask> find(String taskId) {
    return componentClient.forKeyValueEntity(entityId).method(DurablePromptRiskReviewTaskRepositoryEntity::find).invoke(taskId);
  }

  @Override
  public Optional<PromptRiskReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurablePromptRiskReviewTaskRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurablePromptRiskReviewTaskRepositoryEntity.IdempotencyQuery(tenantId, accountId, idempotencyKey));
  }

  @Override
  public PromptRiskReviewTask save(PromptRiskReviewTask task) {
    return componentClient.forKeyValueEntity(entityId).method(DurablePromptRiskReviewTaskRepositoryEntity::save).invoke(task);
  }
}
