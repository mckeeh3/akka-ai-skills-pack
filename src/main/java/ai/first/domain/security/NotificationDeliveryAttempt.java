package ai.first.domain.security;

import java.time.Instant;
import java.util.List;

/** Redacted provider-neutral delivery attempt for external notification channels. */
public record NotificationDeliveryAttempt(
    String attemptId,
    String tenantId,
    String customerId,
    String accountId,
    NotificationChannel channel,
    NotificationCategory category,
    String sourceNotificationId,
    List<NotificationSourceRef> sourceRefs,
    List<String> traceRefs,
    String requiredCapabilityId,
    String owningWorkstreamId,
    String destinationSummary,
    String providerKind,
    NotificationDeliveryAttemptStatus status,
    String safeErrorSummary,
    String dedupeKey,
    String outboxId,
    String correlationId,
    Instant createdAt,
    Instant updatedAt) {
  public NotificationDeliveryAttempt {
    sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
  }
}
