package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Durable state for backend-owned in-app notification projection items and preferences. */
public record NotificationRepositoryState(Map<String, NotificationItem> itemsByKey, Map<String, NotificationPreference> preferencesByKey) {
  public NotificationRepositoryState {
    itemsByKey = Map.copyOf(itemsByKey == null ? Map.of() : itemsByKey);
    preferencesByKey = Map.copyOf(preferencesByKey == null ? Map.of() : preferencesByKey);
  }

  public static NotificationRepositoryState empty() {
    return new NotificationRepositoryState(Map.of(), Map.of());
  }

  public Optional<NotificationItem> find(String tenantId, String notificationId) {
    return Optional.ofNullable(itemsByKey.get(itemKey(tenantId, notificationId)));
  }

  public Optional<NotificationItem> findByDedupeKey(String tenantId, String dedupeKey) {
    return itemsByKey.values().stream().filter(item -> tenantId.equals(item.tenantId()) && dedupeKey.equals(item.dedupeKey())).findFirst();
  }

  public List<NotificationItem> listTenant(String tenantId) {
    return itemsByKey.values().stream().filter(item -> tenantId.equals(item.tenantId())).sorted(Comparator.comparing(NotificationItem::lastChangedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
  }

  public NotificationRepositoryState save(NotificationItem item) {
    var next = new LinkedHashMap<>(itemsByKey);
    next.put(itemKey(item.tenantId(), item.notificationId()), item);
    return new NotificationRepositoryState(next, preferencesByKey);
  }

  public Optional<NotificationPreference> findPreference(String tenantId, String preferenceId) {
    return Optional.ofNullable(preferencesByKey.get(prefKey(tenantId, preferenceId)));
  }

  public List<NotificationPreference> listPreferences(String tenantId, String accountId) {
    return preferencesByKey.values().stream().filter(pref -> tenantId.equals(pref.tenantId()) && accountId.equals(pref.accountId())).toList();
  }

  public NotificationRepositoryState savePreference(NotificationPreference preference) {
    var next = new LinkedHashMap<>(preferencesByKey);
    next.put(prefKey(preference.tenantId(), preference.preferenceId()), preference);
    return new NotificationRepositoryState(itemsByKey, next);
  }

  private static String itemKey(String tenantId, String notificationId) {
    return tenantId + ":" + notificationId;
  }

  private static String prefKey(String tenantId, String preferenceId) {
    return tenantId + ":" + preferenceId;
  }
}
