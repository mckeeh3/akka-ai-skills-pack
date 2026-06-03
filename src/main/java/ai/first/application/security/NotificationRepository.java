package ai.first.application.security;

import ai.first.domain.security.EmailNotificationDelivery;
import ai.first.domain.security.EmailNotificationPreference;
import ai.first.domain.security.EmailOutboxMessage;
import ai.first.domain.security.DigestExportRequest;
import ai.first.domain.security.NotificationDeliveryAttempt;
import ai.first.domain.security.NotificationExternalOutboxMessage;
import ai.first.domain.security.NotificationItem;
import ai.first.domain.security.NotificationPreference;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Durable port for backend-owned in-app notification projection state and preferences. */
public interface NotificationRepository {
  NotificationItem upsert(NotificationItem item);
  NotificationItem save(NotificationItem item);
  Optional<NotificationItem> find(String tenantId, String notificationId);
  Optional<NotificationItem> findByDedupeKey(String tenantId, String dedupeKey);
  List<NotificationItem> listTenant(String tenantId);
  NotificationPreference savePreference(NotificationPreference preference);
  Optional<NotificationPreference> findPreference(String tenantId, String preferenceId);
  List<NotificationPreference> listPreferences(String tenantId, String accountId);
  EmailNotificationPreference saveEmailPreference(EmailNotificationPreference preference);
  List<EmailNotificationPreference> listEmailPreferences(String tenantId, String accountId);
  EmailNotificationDelivery saveEmailDelivery(EmailNotificationDelivery delivery);
  Optional<EmailNotificationDelivery> findEmailDelivery(String tenantId, String deliveryId);
  Optional<EmailNotificationDelivery> findEmailDeliveryByDedupeKey(String tenantId, String dedupeKey);
  EmailOutboxMessage saveEmailOutbox(EmailOutboxMessage message);
  Optional<EmailOutboxMessage> findEmailOutbox(String tenantId, String outboxId);
  List<EmailOutboxMessage> listEmailOutbox(String tenantId);
  NotificationDeliveryAttempt saveDeliveryAttempt(NotificationDeliveryAttempt attempt);
  Optional<NotificationDeliveryAttempt> findDeliveryAttempt(String tenantId, String attemptId);
  Optional<NotificationDeliveryAttempt> findDeliveryAttemptByDedupeKey(String tenantId, String dedupeKey);
  List<NotificationDeliveryAttempt> listDeliveryAttempts(String tenantId, String accountId);
  NotificationExternalOutboxMessage saveExternalOutbox(NotificationExternalOutboxMessage message);
  List<NotificationExternalOutboxMessage> listExternalOutbox(String tenantId, String accountId);
  DigestExportRequest saveDigestExportRequest(DigestExportRequest request);
  Optional<DigestExportRequest> findDigestExportRequest(String tenantId, String requestId);
  Optional<DigestExportRequest> findDigestExportRequestByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);
  List<DigestExportRequest> listDigestExportRequests(String tenantId);
  List<DigestExportRequest> listDueDigestExportRequests(String tenantId, Instant dueAt);
}
