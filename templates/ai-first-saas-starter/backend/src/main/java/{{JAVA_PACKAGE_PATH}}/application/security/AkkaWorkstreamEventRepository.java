package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkstreamEventEnvelope;
import java.util.List;
import java.util.Optional;

/** Akka-backed repository adapter for normal runtime governed workstream events. */
public final class AkkaWorkstreamEventRepository implements WorkstreamEventRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaWorkstreamEventRepository(ComponentClient componentClient) {
    this(componentClient, DurableWorkstreamEventRepositoryEntity.ENTITY_ID);
  }

  public AkkaWorkstreamEventRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  public WorkstreamEventEnvelope publish(WorkstreamEventEnvelope event) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamEventRepositoryEntity::publish).invoke(event);
  }

  public Optional<WorkstreamEventEnvelope> find(String tenantId, String eventId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamEventRepositoryEntity::find)
        .invoke(new DurableWorkstreamEventRepositoryEntity.FindQuery(tenantId, eventId));
  }

  public Optional<WorkstreamEventEnvelope> findByIdempotencyKey(String tenantId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamEventRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableWorkstreamEventRepositoryEntity.IdempotencyQuery(tenantId, idempotencyKey));
  }

  public List<WorkstreamEventEnvelope> listTenant(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamEventRepositoryEntity::listTenant)
        .invoke(new DurableWorkstreamEventRepositoryEntity.ListTenantQuery(tenantId));
  }
}
