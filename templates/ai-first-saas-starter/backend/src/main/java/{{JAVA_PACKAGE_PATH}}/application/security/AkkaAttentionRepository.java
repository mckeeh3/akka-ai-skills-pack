package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import java.util.List;
import java.util.Optional;

/** Akka-backed repository adapter for normal runtime attention state. */
public final class AkkaAttentionRepository implements AttentionRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaAttentionRepository(ComponentClient componentClient) {
    this(componentClient, DurableAttentionRepositoryEntity.ENTITY_ID);
  }

  public AkkaAttentionRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  public AttentionItem upsert(AttentionItem item) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAttentionRepositoryEntity::upsert).invoke(item);
  }

  public Optional<AttentionItem> find(String tenantId, String itemId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAttentionRepositoryEntity::find)
        .invoke(new DurableAttentionRepositoryEntity.FindQuery(tenantId, itemId));
  }

  public List<AttentionItem> listTenant(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAttentionRepositoryEntity::listTenant)
        .invoke(new DurableAttentionRepositoryEntity.ListTenantQuery(tenantId));
  }

  public AttentionItem save(AttentionItem item) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAttentionRepositoryEntity::save).invoke(item);
  }
}
