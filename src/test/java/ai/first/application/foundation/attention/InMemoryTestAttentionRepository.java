package ai.first.application.foundation.attention;

import ai.first.domain.foundation.attention.AttentionItem;
import java.util.List;
import java.util.Optional;

/** Unit-test/local adapter for shared attention backbone tests; production binds AkkaAttentionRepository. */
public final class InMemoryTestAttentionRepository implements AttentionRepository {
  private AttentionRepositoryState state = AttentionRepositoryState.empty();

  public synchronized AttentionItem upsert(AttentionItem item) {
    state = state.save(item);
    return item;
  }

  public synchronized Optional<AttentionItem> find(String tenantId, String itemId) {
    return state.find(tenantId, itemId);
  }

  public synchronized List<AttentionItem> listTenant(String tenantId) {
    return state.listTenant(tenantId);
  }

  public synchronized AttentionItem save(AttentionItem item) {
    state = state.save(item);
    return item;
  }
}
