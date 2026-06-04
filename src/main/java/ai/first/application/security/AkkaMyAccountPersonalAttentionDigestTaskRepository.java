package ai.first.application.security;

import ai.first.domain.foundation.identity.Account;
import akka.javasdk.client.ComponentClient;
import ai.first.domain.security.MyAccountPersonalAttentionDigestTask;
import java.util.Optional;

/** Akka-backed My Account personal attention digest task lifecycle adapter for normal starter runtime paths. */
public final class AkkaMyAccountPersonalAttentionDigestTaskRepository implements MyAccountPersonalAttentionDigestTaskRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaMyAccountPersonalAttentionDigestTaskRepository(ComponentClient componentClient) {
    this(componentClient, DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity.ENTITY_ID);
  }

  public AkkaMyAccountPersonalAttentionDigestTaskRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<MyAccountPersonalAttentionDigestTask> find(String digestTaskId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity::find).invoke(digestTaskId);
  }

  @Override
  public Optional<MyAccountPersonalAttentionDigestTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity.IdempotencyQuery(tenantId, accountId, idempotencyKey));
  }

  @Override
  public MyAccountPersonalAttentionDigestTask save(MyAccountPersonalAttentionDigestTask task) {
    return componentClient.forKeyValueEntity(entityId).method(DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity::save).invoke(task);
  }
}
