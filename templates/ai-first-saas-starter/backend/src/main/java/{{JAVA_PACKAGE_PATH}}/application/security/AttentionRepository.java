package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import java.util.List;
import java.util.Optional;

/** Durable port for the shared tenant/customer-scoped attention backbone. */
public interface AttentionRepository {
  AttentionItem upsert(AttentionItem item);
  Optional<AttentionItem> find(String tenantId, String itemId);
  List<AttentionItem> listTenant(String tenantId);
  AttentionItem save(AttentionItem item);
}
