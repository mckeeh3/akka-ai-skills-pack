package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Test-source in-memory notification repository. */
final class LocalDemoNotificationRepository implements NotificationRepository {
  private final Map<String, NotificationItem> items = new LinkedHashMap<>();
  private final Map<String, NotificationPreference> preferences = new LinkedHashMap<>();

  public NotificationItem upsert(NotificationItem item) {
    items.put(key(item.tenantId(), item.notificationId()), item);
    return item;
  }

  public NotificationItem save(NotificationItem item) {
    return upsert(item);
  }

  public Optional<NotificationItem> find(String tenantId, String notificationId) {
    return Optional.ofNullable(items.get(key(tenantId, notificationId)));
  }

  public Optional<NotificationItem> findByDedupeKey(String tenantId, String dedupeKey) {
    return items.values().stream().filter(item -> tenantId.equals(item.tenantId()) && dedupeKey.equals(item.dedupeKey())).findFirst();
  }

  public List<NotificationItem> listTenant(String tenantId) {
    return items.values().stream().filter(item -> tenantId.equals(item.tenantId())).sorted(Comparator.comparing(NotificationItem::lastChangedAt).reversed()).toList();
  }

  public NotificationPreference savePreference(NotificationPreference preference) {
    preferences.put(key(preference.tenantId(), preference.preferenceId()), preference);
    return preference;
  }

  public Optional<NotificationPreference> findPreference(String tenantId, String preferenceId) {
    return Optional.ofNullable(preferences.get(key(tenantId, preferenceId)));
  }

  public List<NotificationPreference> listPreferences(String tenantId, String accountId) {
    return preferences.values().stream().filter(pref -> tenantId.equals(pref.tenantId()) && accountId.equals(pref.accountId())).toList();
  }

  private String key(String tenantId, String id) {
    return tenantId + ":" + id;
  }
}
