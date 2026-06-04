package ai.first.application.security;

import ai.first.domain.security.DigestExportRequest;
import ai.first.domain.foundation.email.EmailNotificationDelivery;
import ai.first.domain.foundation.email.EmailNotificationPreference;
import ai.first.domain.foundation.email.EmailOutboxMessage;
import ai.first.domain.foundation.notification.NotificationDeliveryAttempt;
import ai.first.domain.foundation.notification.NotificationExternalOutboxMessage;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationPreference;
import java.time.Instant;
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
    Map<String, EmailOutboxMessage> emailOutboxByKey,
    Map<String, NotificationDeliveryAttempt> deliveryAttemptsByKey,
    Map<String, NotificationExternalOutboxMessage> externalOutboxByKey,
    Map<String, DigestExportRequest> digestExportRequestsByKey) {
  public NotificationRepositoryState {
    itemsByKey = Map.copyOf(itemsByKey == null ? Map.of() : itemsByKey);
    preferencesByKey = Map.copyOf(preferencesByKey == null ? Map.of() : preferencesByKey);
    emailPreferencesByKey = Map.copyOf(emailPreferencesByKey == null ? Map.of() : emailPreferencesByKey);
    emailDeliveriesByKey = Map.copyOf(emailDeliveriesByKey == null ? Map.of() : emailDeliveriesByKey);
    emailOutboxByKey = Map.copyOf(emailOutboxByKey == null ? Map.of() : emailOutboxByKey);
    deliveryAttemptsByKey = Map.copyOf(deliveryAttemptsByKey == null ? Map.of() : deliveryAttemptsByKey);
    externalOutboxByKey = Map.copyOf(externalOutboxByKey == null ? Map.of() : externalOutboxByKey);
    digestExportRequestsByKey = Map.copyOf(digestExportRequestsByKey == null ? Map.of() : digestExportRequestsByKey);
  }

  public static NotificationRepositoryState empty() {
    return new NotificationRepositoryState(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
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
    return new NotificationRepositoryState(next, preferencesByKey, emailPreferencesByKey, emailDeliveriesByKey, emailOutboxByKey, deliveryAttemptsByKey, externalOutboxByKey, digestExportRequestsByKey);
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
    return new NotificationRepositoryState(itemsByKey, next, emailPreferencesByKey, emailDeliveriesByKey, emailOutboxByKey, deliveryAttemptsByKey, externalOutboxByKey, digestExportRequestsByKey);
  }

  public List<EmailNotificationPreference> listEmailPreferences(String tenantId, String accountId) {
    return emailPreferencesByKey.values().stream().filter(pref -> tenantId.equals(pref.tenantId()) && accountId.equals(pref.accountId())).toList();
  }

  public NotificationRepositoryState saveEmailPreference(EmailNotificationPreference preference) {
    var next = new LinkedHashMap<>(emailPreferencesByKey);
    next.put(prefKey(preference.tenantId(), preference.preferenceId()), preference);
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, next, emailDeliveriesByKey, emailOutboxByKey, deliveryAttemptsByKey, externalOutboxByKey, digestExportRequestsByKey);
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
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, emailPreferencesByKey, next, emailOutboxByKey, deliveryAttemptsByKey, externalOutboxByKey, digestExportRequestsByKey);
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
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, emailPreferencesByKey, emailDeliveriesByKey, next, deliveryAttemptsByKey, externalOutboxByKey, digestExportRequestsByKey);
  }

  public Optional<NotificationDeliveryAttempt> findDeliveryAttempt(String tenantId, String attemptId) {
    return Optional.ofNullable(deliveryAttemptsByKey.get(itemKey(tenantId, attemptId)));
  }

  public Optional<NotificationDeliveryAttempt> findDeliveryAttemptByDedupeKey(String tenantId, String dedupeKey) {
    return deliveryAttemptsByKey.values().stream().filter(attempt -> tenantId.equals(attempt.tenantId()) && dedupeKey.equals(attempt.dedupeKey())).findFirst();
  }

  public List<NotificationDeliveryAttempt> listDeliveryAttempts(String tenantId, String accountId) {
    return deliveryAttemptsByKey.values().stream().filter(attempt -> tenantId.equals(attempt.tenantId()) && accountId.equals(attempt.accountId())).sorted(Comparator.comparing(NotificationDeliveryAttempt::updatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
  }

  public NotificationRepositoryState saveDeliveryAttempt(NotificationDeliveryAttempt attempt) {
    var next = new LinkedHashMap<>(deliveryAttemptsByKey);
    next.put(itemKey(attempt.tenantId(), attempt.attemptId()), attempt);
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, emailPreferencesByKey, emailDeliveriesByKey, emailOutboxByKey, next, externalOutboxByKey, digestExportRequestsByKey);
  }

  public List<NotificationExternalOutboxMessage> listExternalOutbox(String tenantId, String accountId) {
    return externalOutboxByKey.values().stream().filter(message -> tenantId.equals(message.tenantId()) && accountId.equals(message.accountId())).toList();
  }

  public NotificationRepositoryState saveExternalOutbox(NotificationExternalOutboxMessage message) {
    var next = new LinkedHashMap<>(externalOutboxByKey);
    next.put(itemKey(message.tenantId(), message.outboxId()), message);
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, emailPreferencesByKey, emailDeliveriesByKey, emailOutboxByKey, deliveryAttemptsByKey, next, digestExportRequestsByKey);
  }

  public Optional<DigestExportRequest> findDigestExportRequest(String tenantId, String requestId) {
    return Optional.ofNullable(digestExportRequestsByKey.get(itemKey(tenantId, requestId)));
  }

  public Optional<DigestExportRequest> findDigestExportRequestByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return digestExportRequestsByKey.values().stream().filter(request -> tenantId.equals(request.tenantId()) && accountId.equals(request.accountId()) && idempotencyKey.equals(request.idempotencyKey())).findFirst();
  }

  public List<DigestExportRequest> listDigestExportRequests(String tenantId) {
    return digestExportRequestsByKey.values().stream().filter(request -> tenantId.equals(request.tenantId())).sorted(Comparator.comparing(DigestExportRequest::updatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed()).toList();
  }

  public List<DigestExportRequest> listDueDigestExportRequests(String tenantId, Instant dueAt) {
    return digestExportRequestsByKey.values().stream().filter(request -> tenantId.equals(request.tenantId()) && request.requestType() == DigestExportRequest.RequestType.SCHEDULED_DIGEST && request.status() == DigestExportRequest.Status.SCHEDULED && request.scheduledFor() != null && !request.scheduledFor().isAfter(dueAt)).toList();
  }

  public NotificationRepositoryState saveDigestExportRequest(DigestExportRequest request) {
    var next = new LinkedHashMap<>(digestExportRequestsByKey);
    next.put(itemKey(request.tenantId(), request.requestId()), request);
    return new NotificationRepositoryState(itemsByKey, preferencesByKey, emailPreferencesByKey, emailDeliveriesByKey, emailOutboxByKey, deliveryAttemptsByKey, externalOutboxByKey, next);
  }

  private static String itemKey(String tenantId, String notificationId) {
    return tenantId + ":" + notificationId;
  }

  private static String prefKey(String tenantId, String preferenceId) {
    return tenantId + ":" + preferenceId;
  }
}
