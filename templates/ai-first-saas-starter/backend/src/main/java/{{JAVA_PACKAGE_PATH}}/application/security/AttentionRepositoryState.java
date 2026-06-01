package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Durable state for the starter shared attention backbone. */
public record AttentionRepositoryState(Map<String, AttentionItem> itemsByKey) {
  public AttentionRepositoryState {
    itemsByKey = Map.copyOf(itemsByKey == null ? Map.of() : itemsByKey);
  }

  public static AttentionRepositoryState empty() {
    return new AttentionRepositoryState(Map.of());
  }

  public Optional<AttentionItem> find(String tenantId, String itemId) {
    return Optional.ofNullable(itemsByKey.get(key(tenantId, itemId)));
  }

  public List<AttentionItem> listTenant(String tenantId) {
    return itemsByKey.values().stream()
        .filter(item -> tenantId.equals(item.tenantId()))
        .sorted(java.util.Comparator.comparing(AttentionItem::lastChangedAt).reversed())
        .toList();
  }

  public AttentionRepositoryState save(AttentionItem item) {
    var next = new LinkedHashMap<>(itemsByKey);
    next.put(key(item.tenantId(), item.itemId()), item);
    return new AttentionRepositoryState(next);
  }

  private static String key(String tenantId, String itemId) {
    return tenantId + ":" + itemId;
  }
}
