package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuditTraceSummaryTask;
import java.util.Optional;

/** Akka-backed Audit/Trace summary task lifecycle adapter for normal starter runtime paths. */
public final class AkkaAuditTraceSummaryTaskRepository implements AuditTraceSummaryTaskRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaAuditTraceSummaryTaskRepository(ComponentClient componentClient) {
    this(componentClient, DurableAuditTraceSummaryTaskRepositoryEntity.ENTITY_ID);
  }

  public AkkaAuditTraceSummaryTaskRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<AuditTraceSummaryTask> find(String taskId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAuditTraceSummaryTaskRepositoryEntity::find).invoke(taskId);
  }

  @Override
  public Optional<AuditTraceSummaryTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAuditTraceSummaryTaskRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableAuditTraceSummaryTaskRepositoryEntity.IdempotencyQuery(tenantId, accountId, idempotencyKey));
  }

  @Override
  public AuditTraceSummaryTask save(AuditTraceSummaryTask task) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAuditTraceSummaryTaskRepositoryEntity::save).invoke(task);
  }
}
