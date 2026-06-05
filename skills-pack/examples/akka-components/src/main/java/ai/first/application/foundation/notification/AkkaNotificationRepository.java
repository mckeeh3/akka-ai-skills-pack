package ai.first.application.foundation.notification;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.coreapp.myaccount.DigestExportRequest;
import ai.first.domain.foundation.email.EmailNotificationDelivery;
import ai.first.domain.foundation.email.EmailNotificationPreference;
import ai.first.domain.foundation.email.EmailOutboxMessage;
import ai.first.domain.foundation.notification.NotificationDeliveryAttempt;
import ai.first.domain.foundation.notification.NotificationExternalOutboxMessage;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationPreference;
import java.time.Instant;
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

  public NotificationDeliveryAttempt saveDeliveryAttempt(NotificationDeliveryAttempt attempt) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::saveDeliveryAttempt).invoke(attempt);
  }

  public Optional<NotificationDeliveryAttempt> findDeliveryAttempt(String tenantId, String attemptId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findDeliveryAttempt).invoke(new DurableNotificationRepositoryEntity.FindQuery(tenantId, attemptId));
  }

  public Optional<NotificationDeliveryAttempt> findDeliveryAttemptByDedupeKey(String tenantId, String dedupeKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findDeliveryAttemptByDedupeKey).invoke(new DurableNotificationRepositoryEntity.FindDedupeQuery(tenantId, dedupeKey));
  }

  public List<NotificationDeliveryAttempt> listDeliveryAttempts(String tenantId, String accountId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::listDeliveryAttempts).invoke(new DurableNotificationRepositoryEntity.ListPreferencesQuery(tenantId, accountId));
  }

  public NotificationExternalOutboxMessage saveExternalOutbox(NotificationExternalOutboxMessage message) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::saveExternalOutbox).invoke(message);
  }

  public List<NotificationExternalOutboxMessage> listExternalOutbox(String tenantId, String accountId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::listExternalOutbox).invoke(new DurableNotificationRepositoryEntity.ListPreferencesQuery(tenantId, accountId));
  }

  public DigestExportRequest saveDigestExportRequest(DigestExportRequest request) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::saveDigestExportRequest).invoke(request);
  }

  public Optional<DigestExportRequest> findDigestExportRequest(String tenantId, String requestId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findDigestExportRequest).invoke(new DurableNotificationRepositoryEntity.FindQuery(tenantId, requestId));
  }

  public Optional<DigestExportRequest> findDigestExportRequestByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::findDigestExportRequestByIdempotencyKey).invoke(new DurableNotificationRepositoryEntity.FindDigestExportDedupeQuery(tenantId, accountId, idempotencyKey));
  }

  public List<DigestExportRequest> listDigestExportRequests(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::listDigestExportRequests).invoke(new DurableNotificationRepositoryEntity.ListTenantQuery(tenantId));
  }

  public List<DigestExportRequest> listDueDigestExportRequests(String tenantId, Instant dueAt) {
    return componentClient.forKeyValueEntity(entityId).method(DurableNotificationRepositoryEntity::listDueDigestExportRequests).invoke(new DurableNotificationRepositoryEntity.ListDueDigestExportQuery(tenantId, dueAt));
  }
}
