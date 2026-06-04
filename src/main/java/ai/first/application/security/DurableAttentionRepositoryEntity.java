package ai.first.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.foundation.attention.AttentionItem;
import java.util.List;
import java.util.Optional;

/** Durable Akka Key Value Entity backing the shared attention backbone. */
@Component(id = "starter-attention-backbone")
public class DurableAttentionRepositoryEntity extends KeyValueEntity<AttentionRepositoryState> {
  public static final String ENTITY_ID = "starter-attention-backbone";

  @Override
  public AttentionRepositoryState emptyState() {
    return AttentionRepositoryState.empty();
  }

  public Effect<AttentionItem> upsert(AttentionItem item) {
    return effects().updateState(currentState().save(item)).thenReply(() -> item);
  }

  public ReadOnlyEffect<Optional<AttentionItem>> find(FindQuery query) {
    return effects().reply(currentState().find(query.tenantId(), query.itemId()));
  }

  public ReadOnlyEffect<List<AttentionItem>> listTenant(ListTenantQuery query) {
    return effects().reply(currentState().listTenant(query.tenantId()));
  }

  public Effect<AttentionItem> save(AttentionItem item) {
    return effects().updateState(currentState().save(item)).thenReply(() -> item);
  }

  public record FindQuery(String tenantId, String itemId) {}
  public record ListTenantQuery(String tenantId) {}
}
