package ai.first.domain.foundation.email;

import ai.first.domain.foundation.notification.NotificationCategory;
import ai.first.domain.foundation.notification.NotificationRedactionLevel;
import ai.first.domain.foundation.notification.NotificationSourceRef;
import java.time.Instant;
import java.util.List;

/** Durable, redacted delivery intent/result for the governed notification email channel. */
public record EmailNotificationDelivery(
    String deliveryId,
    String tenantId,
    String customerId,
    String accountId,
    String normalizedRecipientEmail,
    String selectedContextId,
    NotificationCategory category,
    String sourceNotificationId,
    List<NotificationSourceRef> sourceRefs,
    List<String> traceRefs,
    String requiredCapabilityId,
    String owningWorkstreamId,
    String subject,
    String previewText,
    String bodyText,
    String surfaceRef,
    NotificationRedactionLevel redactionLevel,
    String dedupeKey,
    String deliveryAttemptId,
    String outboxId,
    String provider,
    String providerMessageId,
    EmailNotificationDeliveryStatus status,
    String safeErrorSummary,
    String correlationId,
    Instant createdAt,
    Instant updatedAt) {
  public EmailNotificationDelivery {
    sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
  }

  public EmailNotificationDelivery withResult(EmailNotificationDeliveryStatus nextStatus, String nextProvider, String nextProviderMessageId, String nextSafeErrorSummary, Instant now, String nextCorrelationId) {
    return new EmailNotificationDelivery(deliveryId, tenantId, customerId, accountId, normalizedRecipientEmail, selectedContextId, category, sourceNotificationId, sourceRefs, traceRefs, requiredCapabilityId, owningWorkstreamId, subject, previewText, bodyText, surfaceRef, redactionLevel, dedupeKey, deliveryAttemptId, outboxId, nextProvider, nextProviderMessageId, nextStatus, nextSafeErrorSummary, nextCorrelationId, createdAt, now);
  }
}
