package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationDelivery;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationPreference;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import java.util.List;
import java.util.Optional;

/** Akka-backed repository adapter for normal runtime in-app notification state. */
public final class AkkaNotificationRepository implements NotificationRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaNotificationRepository(ComponentClient componentClient) {
    this(componentClient, DurableNotificationRepositoryEntity.ENTITY_ID);
  }

  public AkkaNotificationRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  public NotificationItem upsert(NotificationItem item) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::upsert).invoke(item);
  }

  public NotificationItem save(NotificationItem item) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::save).invoke(item);
  }

  public Optional<NotificationItem> find(String tenantId, String notificationId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::find).invoke(new DurableNotificationRepositoryEntity.FindQuery(tenantId, notificationId));
  }

  public Optional<NotificationItem> findByDedupeKey(String tenantId, String dedupeKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findByDedupeKey).invoke(new DurableNotificationRepositoryEntity.FindDedupeQuery(tenantId, dedupeKey));
  }

  public List<NotificationItem> listTenant(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::listTenant).invoke(new DurableNotificationRepositoryEntity.ListTenantQuery(tenantId));
  }

  public NotificationPreference savePreference(NotificationPreference preference) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::savePreference).invoke(preference);
  }

  public Optional<NotificationPreference> findPreference(String tenantId, String preferenceId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findPreference).invoke(new DurableNotificationRepositoryEntity.FindPreferenceQuery(tenantId, preferenceId));
  }

  public List<NotificationPreference> listPreferences(String tenantId, String accountId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::listPreferences).invoke(new DurableNotificationRepositoryEntity.ListPreferencesQuery(tenantId, accountId));
  }

  public EmailNotificationPreference saveEmailPreference(EmailNotificationPreference preference) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::saveEmailPreference).invoke(preference);
  }

  public List<EmailNotificationPreference> listEmailPreferences(String tenantId, String accountId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::listEmailPreferences).invoke(new DurableNotificationRepositoryEntity.ListPreferencesQuery(tenantId, accountId));
  }

  public EmailNotificationDelivery saveEmailDelivery(EmailNotificationDelivery delivery) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::saveEmailDelivery).invoke(delivery);
  }

  public Optional<EmailNotificationDelivery> findEmailDelivery(String tenantId, String deliveryId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findEmailDelivery).invoke(new DurableNotificationRepositoryEntity.FindQuery(tenantId, deliveryId));
  }

  public Optional<EmailNotificationDelivery> findEmailDeliveryByDedupeKey(String tenantId, String dedupeKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findEmailDeliveryByDedupeKey).invoke(new DurableNotificationRepositoryEntity.FindDedupeQuery(tenantId, dedupeKey));
  }

  public EmailOutboxMessage saveEmailOutbox(EmailOutboxMessage message) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::saveEmailOutbox).invoke(message);
  }

  public Optional<EmailOutboxMessage> findEmailOutbox(String tenantId, String outboxId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findEmailOutbox).invoke(new DurableNotificationRepositoryEntity.FindQuery(tenantId, outboxId));
  }

  public List<EmailOutboxMessage> listEmailOutbox(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::listEmailOutbox).invoke(new DurableNotificationRepositoryEntity.ListTenantQuery(tenantId));
  }
}
