package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationDelivery;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationPreference;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
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
}
