package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationDelivery;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationPreference;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Durable state for backend-owned in-app notification projection items and preferences. */
public record NotificationRepositoryState(
    Map<String, NotificationItem> itemsByKey,
    Map<String, NotificationPreference> preferencesByKey,
    Map<String, EmailNotificationPreference> emailPreferencesByKey,
    Map<String, EmailNotificationDelivery> emailDeliveriesByKey,
    Map<String, EmailOutboxMessage> emailOutboxByKey) {
  public NotificationRepositoryState {
    itemsByKey = Map.copyOf(itemsByKey == null ? Map.of() : itemsByKey);
    preferencesByKey = Map.copyOf(preferencesByKey == null ? Map.of() : preferencesByKey);
    emailPreferencesByKey = Map.copyOf(emailPreferencesByKey == null ? Map.of() : emailPreferencesByKey);
    emailDeliveriesByKey = Map.copyOf(emailDeliveriesByKey == null ? Map.of() : emailDeliveriesByKey);
    emailOutboxByKey = Map.copyOf(emailOutboxByKey == null ? Map.of() : emailOutboxByKey);
  }

  public static NotificationRepositoryState empty() {
    return new NotificationRepositoryState(Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
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
    return new NotificationRepositoryState(next, preferencesByKey, emailPreferencesByKey, emailDeliveriesByKey, emailOutboxByKey);
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
    return new NotificationRepositoryState(itemsByKey, next, emailPreferencesByKey, emailDeliveriesByKey, emailOutboxByKey);
  }

  public List<EmailNotificationPreference> listEmailPreferences(String tenantId, String accountId) {
    return emailPreferencesByKey.values().stream().filter(pref -> tenantId.equals(pref.tenantId()) && accountId.equals(pref.accountId())).toList();
  }

  public NotificationRepositoryState saveEmailPreference(EmailNotificationPreference preference) {
    var next = new LinkedHashMap<>(emailPreferencesByKey);
    next.put(prefKey(preference.tenantId(), preference.preferenceId()), preference);
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, next, emailDeliveriesByKey, emailOutboxByKey);
  }

  public Optional<EmailNotificationDelivery> findEmailDelivery(String tenantId, String deliveryId) {
    return Optional.ofNullable(emailDeliveriesByKey.get(itemKey(tenantId, deliveryId)));
  }

  public Optional<EmailNotificationDelivery> findEmailDeliveryByDedupeKey(String tenantId, String dedupeKey) {
    return emailDeliveriesByKey.values().stream().filter(delivery -> tenantId.equals(delivery.tenantId()) && dedupeKey.equals(delivery.dedupeKey())).findFirst();
  }

  public NotificationRepositoryState saveEmailDelivery(EmailNotificationDelivery delivery) {
    var next = new LinkedHashMap<>(emailDeliveriesByKey);
    next.put(itemKey(delivery.tenantId(), delivery.deliveryId()), delivery);
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, emailPreferencesByKey, next, emailOutboxByKey);
  }

  public Optional<EmailOutboxMessage> findEmailOutbox(String tenantId, String outboxId) {
    return Optional.ofNullable(emailOutboxByKey.get(itemKey(tenantId, outboxId)));
  }

  public List<EmailOutboxMessage> listEmailOutbox(String tenantId) {
    return emailOutboxByKey.values().stream().filter(message -> tenantId.equals(message.tenantId())).toList();
  }

  public NotificationRepositoryState saveEmailOutbox(EmailOutboxMessage message) {
    var next = new LinkedHashMap<>(emailOutboxByKey);
    next.put(itemKey(message.tenantId(), message.outboxId()), message);
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, emailPreferencesByKey, emailDeliveriesByKey, next);
  }

  private static String itemKey(String tenantId, String notificationId) {
    return tenantId + ":" + notificationId;
  }

  private static String prefKey(String tenantId, String preferenceId) {
    return tenantId + ":" + preferenceId;
  }
}
