package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import java.util.List;
import java.util.Optional;

/** Durable Akka Key Value Entity backing in-app notification items and preferences. */
@Component(id = "starter-notification-foundation")
public class DurableNotificationRepositoryEntity extends KeyValueEntity<NotificationRepositoryState> {
  public static final String ENTITY_ID = "starter-notification-foundation";

  @Override
  public NotificationRepositoryState emptyState() {
    return NotificationRepositoryState.empty();
  }

  public Effect<NotificationItem> upsert(NotificationItem item) {
    return effects().updateState(currentState().save(item)).thenReply(() -> item);
  }

  public Effect<NotificationItem> save(NotificationItem item) {
    return effects().updateState(currentState().save(item)).thenReply(() -> item);
  }

  public ReadOnlyEffect<Optional<NotificationItem>> find(FindQuery query) {
    return effects().reply(currentState().find(query.tenantId(), query.notificationId()));
  }

  public ReadOnlyEffect<Optional<NotificationItem>> findByDedupeKey(FindDedupeQuery query) {
    return effects().reply(currentState().findByDedupeKey(query.tenantId(), query.dedupeKey()));
  }

  public ReadOnlyEffect<List<NotificationItem>> listTenant(ListTenantQuery query) {
    return effects().reply(currentState().listTenant(query.tenantId()));
  }

  public Effect<NotificationPreference> savePreference(NotificationPreference preference) {
    return effects().updateState(currentState().savePreference(preference)).thenReply(() -> preference);
  }

  public ReadOnlyEffect<Optional<NotificationPreference>> findPreference(FindPreferenceQuery query) {
    return effects().reply(currentState().findPreference(query.tenantId(), query.preferenceId()));
  }

  public ReadOnlyEffect<List<NotificationPreference>> listPreferences(ListPreferencesQuery query) {
    return effects().reply(currentState().listPreferences(query.tenantId(), query.accountId()));
  }

  public record FindQuery(String tenantId, String notificationId) {}
  public record FindDedupeQuery(String tenantId, String dedupeKey) {}
  public record ListTenantQuery(String tenantId) {}
  public record FindPreferenceQuery(String tenantId, String preferenceId) {}
  public record ListPreferencesQuery(String tenantId, String accountId) {}
}
