package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationDelivery;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationPreference;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationDeliveryAttempt;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationExternalOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Test-source Akka component test notification repository. */
final class LocalDemoNotificationRepository implements NotificationRepository {
  private final Map<String, NotificationItem> items = new LinkedHashMap<>();
  private final Map<String, NotificationPreference> preferences = new LinkedHashMap<>();
  private final Map<String, EmailNotificationPreference> emailPreferences = new LinkedHashMap<>();
  private final Map<String, EmailNotificationDelivery> emailDeliveries = new LinkedHashMap<>();
  private final Map<String, EmailOutboxMessage> emailOutbox = new LinkedHashMap<>();
  private final Map<String, NotificationDeliveryAttempt> deliveryAttempts = new LinkedHashMap<>();
  private final Map<String, NotificationExternalOutboxMessage> externalOutbox = new LinkedHashMap<>();

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

  public EmailNotificationPreference saveEmailPreference(EmailNotificationPreference preference) {
    emailPreferences.put(key(preference.tenantId(), preference.preferenceId()), preference);
    return preference;
  }

  public List<EmailNotificationPreference> listEmailPreferences(String tenantId, String accountId) {
    return emailPreferences.values().stream().filter(pref -> tenantId.equals(pref.tenantId()) && accountId.equals(pref.accountId())).toList();
  }

  public EmailNotificationDelivery saveEmailDelivery(EmailNotificationDelivery delivery) {
    emailDeliveries.put(key(delivery.tenantId(), delivery.deliveryId()), delivery);
    return delivery;
  }

  public Optional<EmailNotificationDelivery> findEmailDelivery(String tenantId, String deliveryId) {
    return Optional.ofNullable(emailDeliveries.get(key(tenantId, deliveryId)));
  }

  public Optional<EmailNotificationDelivery> findEmailDeliveryByDedupeKey(String tenantId, String dedupeKey) {
    return emailDeliveries.values().stream().filter(delivery -> tenantId.equals(delivery.tenantId()) && dedupeKey.equals(delivery.dedupeKey())).findFirst();
  }

  public EmailOutboxMessage saveEmailOutbox(EmailOutboxMessage message) {
    emailOutbox.put(key(message.tenantId(), message.outboxId()), message);
    return message;
  }

  public Optional<EmailOutboxMessage> findEmailOutbox(String tenantId, String outboxId) {
    return Optional.ofNullable(emailOutbox.get(key(tenantId, outboxId)));
  }

  public List<EmailOutboxMessage> listEmailOutbox(String tenantId) {
    return emailOutbox.values().stream().filter(message -> tenantId.equals(message.tenantId())).toList();
  }

  public NotificationDeliveryAttempt saveDeliveryAttempt(NotificationDeliveryAttempt attempt) {
    deliveryAttempts.put(key(attempt.tenantId(), attempt.attemptId()), attempt);
    return attempt;
  }

  public Optional<NotificationDeliveryAttempt> findDeliveryAttempt(String tenantId, String attemptId) {
    return Optional.ofNullable(deliveryAttempts.get(key(tenantId, attemptId)));
  }

  public Optional<NotificationDeliveryAttempt> findDeliveryAttemptByDedupeKey(String tenantId, String dedupeKey) {
    return deliveryAttempts.values().stream().filter(attempt -> tenantId.equals(attempt.tenantId()) && dedupeKey.equals(attempt.dedupeKey())).findFirst();
  }

  public List<NotificationDeliveryAttempt> listDeliveryAttempts(String tenantId, String accountId) {
    return deliveryAttempts.values().stream().filter(attempt -> tenantId.equals(attempt.tenantId()) && accountId.equals(attempt.accountId())).sorted(Comparator.comparing(NotificationDeliveryAttempt::updatedAt).reversed()).toList();
  }

  public NotificationExternalOutboxMessage saveExternalOutbox(NotificationExternalOutboxMessage message) {
    externalOutbox.put(key(message.tenantId(), message.outboxId()), message);
    return message;
  }

  public List<NotificationExternalOutboxMessage> listExternalOutbox(String tenantId, String accountId) {
    return externalOutbox.values().stream().filter(message -> tenantId.equals(message.tenantId()) && accountId.equals(message.accountId())).toList();
  }

  private String key(String tenantId, String id) {
    return tenantId + ":" + id;
  }
}
