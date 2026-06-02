package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import {{JAVA_BASE_PACKAGE}}.domain.security.DigestExportRequest;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationDelivery;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationPreference;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationDeliveryAttempt;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationExternalOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import java.time.Instant;
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

  public Effect<EmailNotificationPreference> saveEmailPreference(EmailNotificationPreference preference) {
    return effects().updateState(currentState().saveEmailPreference(preference)).thenReply(() -> preference);
  }

  public ReadOnlyEffect<List<EmailNotificationPreference>> listEmailPreferences(ListPreferencesQuery query) {
    return effects().reply(currentState().listEmailPreferences(query.tenantId(), query.accountId()));
  }

  public Effect<EmailNotificationDelivery> saveEmailDelivery(EmailNotificationDelivery delivery) {
    return effects().updateState(currentState().saveEmailDelivery(delivery)).thenReply(() -> delivery);
  }

  public ReadOnlyEffect<Optional<EmailNotificationDelivery>> findEmailDelivery(FindQuery query) {
    return effects().reply(currentState().findEmailDelivery(query.tenantId(), query.notificationId()));
  }

  public ReadOnlyEffect<Optional<EmailNotificationDelivery>> findEmailDeliveryByDedupeKey(FindDedupeQuery query) {
    return effects().reply(currentState().findEmailDeliveryByDedupeKey(query.tenantId(), query.dedupeKey()));
  }

  public Effect<EmailOutboxMessage> saveEmailOutbox(EmailOutboxMessage message) {
    return effects().updateState(currentState().saveEmailOutbox(message)).thenReply(() -> message);
  }

  public ReadOnlyEffect<Optional<EmailOutboxMessage>> findEmailOutbox(FindQuery query) {
    return effects().reply(currentState().findEmailOutbox(query.tenantId(), query.notificationId()));
  }

  public ReadOnlyEffect<List<EmailOutboxMessage>> listEmailOutbox(ListTenantQuery query) {
    return effects().reply(currentState().listEmailOutbox(query.tenantId()));
  }

  public Effect<NotificationDeliveryAttempt> saveDeliveryAttempt(NotificationDeliveryAttempt attempt) {
    return effects().updateState(currentState().saveDeliveryAttempt(attempt)).thenReply(() -> attempt);
  }

  public ReadOnlyEffect<Optional<NotificationDeliveryAttempt>> findDeliveryAttempt(FindQuery query) {
    return effects().reply(currentState().findDeliveryAttempt(query.tenantId(), query.notificationId()));
  }

  public ReadOnlyEffect<Optional<NotificationDeliveryAttempt>> findDeliveryAttemptByDedupeKey(FindDedupeQuery query) {
    return effects().reply(currentState().findDeliveryAttemptByDedupeKey(query.tenantId(), query.dedupeKey()));
  }

  public ReadOnlyEffect<List<NotificationDeliveryAttempt>> listDeliveryAttempts(ListPreferencesQuery query) {
    return effects().reply(currentState().listDeliveryAttempts(query.tenantId(), query.accountId()));
  }

  public Effect<NotificationExternalOutboxMessage> saveExternalOutbox(NotificationExternalOutboxMessage message) {
    return effects().updateState(currentState().saveExternalOutbox(message)).thenReply(() -> message);
  }

  public ReadOnlyEffect<List<NotificationExternalOutboxMessage>> listExternalOutbox(ListPreferencesQuery query) {
    return effects().reply(currentState().listExternalOutbox(query.tenantId(), query.accountId()));
  }

  public Effect<DigestExportRequest> saveDigestExportRequest(DigestExportRequest request) {
    return effects().updateState(currentState().saveDigestExportRequest(request)).thenReply(() -> request);
  }

  public ReadOnlyEffect<Optional<DigestExportRequest>> findDigestExportRequest(FindQuery query) {
    return effects().reply(currentState().findDigestExportRequest(query.tenantId(), query.notificationId()));
  }

  public ReadOnlyEffect<Optional<DigestExportRequest>> findDigestExportRequestByIdempotencyKey(FindDigestExportDedupeQuery query) {
    return effects().reply(currentState().findDigestExportRequestByIdempotencyKey(query.tenantId(), query.accountId(), query.idempotencyKey()));
  }

  public ReadOnlyEffect<List<DigestExportRequest>> listDigestExportRequests(ListTenantQuery query) {
    return effects().reply(currentState().listDigestExportRequests(query.tenantId()));
  }

  public ReadOnlyEffect<List<DigestExportRequest>> listDueDigestExportRequests(ListDueDigestExportQuery query) {
    return effects().reply(currentState().listDueDigestExportRequests(query.tenantId(), query.dueAt()));
  }

  public record FindQuery(String tenantId, String notificationId) {}
  public record FindDedupeQuery(String tenantId, String dedupeKey) {}
  public record ListTenantQuery(String tenantId) {}
  public record FindPreferenceQuery(String tenantId, String preferenceId) {}
  public record ListPreferencesQuery(String tenantId, String accountId) {}
  public record FindDigestExportDedupeQuery(String tenantId, String accountId, String idempotencyKey) {}
  public record ListDueDigestExportQuery(String tenantId, Instant dueAt) {}
}
