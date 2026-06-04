package ai.first.application.security;

import ai.first.domain.foundation.attention.AttentionItem;
import java.util.List;
import java.util.Optional;

/** Durable port for the shared tenant/customer-scoped attention backbone. */
public interface AttentionRepository {
  AttentionItem upsert(AttentionItem item);
  Optional<AttentionItem> find(String tenantId, String itemId);
  List<AttentionItem> listTenant(String tenantId);
  AttentionItem save(AttentionItem item);
}
