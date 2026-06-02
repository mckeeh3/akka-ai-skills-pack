package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.time.Instant;
import java.util.List;

/** Backend-owned in-app notification projection. Not source-of-truth for attention, tasks, or events. */
public record NotificationItem(
    String notificationId,
    String tenantId,
    String customerId,
    String accountId,
    String selectedContextId,
    NotificationChannel channel,
    String title,
    String summary,
    NotificationCategory category,
    NotificationPriority priority,
    NotificationLifecycleStatus status,
    List<NotificationSourceRef> sourceRefs,
    NotificationSurfaceRef surfaceRef,
    String requiredCapabilityId,
    String owningWorkstreamId,
    String origin,
    NotificationRedactionLevel redactionLevel,
    String dedupeKey,
    String correlationId,
    List<String> traceRefs,
    Instant createdAt,
    Instant updatedAt,
    Instant lastChangedAt,
    Instant readAt,
    Instant dismissedAt,
    Instant archivedAt,
    Instant snoozedUntil,
    Instant expiresAt) {
  public NotificationItem {
    channel = channel == null ? NotificationChannel.IN_APP : channel;
    status = status == null ? NotificationLifecycleStatus.UNREAD : status;
    sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
    redactionLevel = redactionLevel == null ? NotificationRedactionLevel.FULL : redactionLevel;
  }

  public NotificationItem updateFromProjection(String nextTitle, String nextSummary, NotificationPriority nextPriority, List<NotificationSourceRef> nextSourceRefs, List<String> nextTraceRefs, Instant now, String nextCorrelationId) {
    return new NotificationItem(notificationId, tenantId, customerId, accountId, selectedContextId, channel, nextTitle, nextSummary, category, nextPriority, status, nextSourceRefs, surfaceRef, requiredCapabilityId, owningWorkstreamId, origin, redactionLevel, dedupeKey, nextCorrelationId, nextTraceRefs, createdAt, now, now, readAt, dismissedAt, archivedAt, snoozedUntil, expiresAt);
  }

  public NotificationItem markRead(Instant now, String nextCorrelationId) {
    if (status == NotificationLifecycleStatus.READ) return this;
    return new NotificationItem(notificationId, tenantId, customerId, accountId, selectedContextId, channel, title, summary, category, priority, NotificationLifecycleStatus.READ, sourceRefs, surfaceRef, requiredCapabilityId, owningWorkstreamId, origin, redactionLevel, dedupeKey, nextCorrelationId, traceRefs, createdAt, now, now, now, dismissedAt, archivedAt, snoozedUntil, expiresAt);
  }

  public NotificationItem dismiss(Instant now, String nextCorrelationId) {
    if (status == NotificationLifecycleStatus.DISMISSED) return this;
    return new NotificationItem(notificationId, tenantId, customerId, accountId, selectedContextId, channel, title, summary, category, priority, NotificationLifecycleStatus.DISMISSED, sourceRefs, surfaceRef, requiredCapabilityId, owningWorkstreamId, origin, redactionLevel, dedupeKey, nextCorrelationId, traceRefs, createdAt, now, now, readAt, now, archivedAt, snoozedUntil, expiresAt);
  }

  public NotificationItem archive(Instant now, String nextCorrelationId) {
    if (status == NotificationLifecycleStatus.ARCHIVED) return this;
    return new NotificationItem(notificationId, tenantId, customerId, accountId, selectedContextId, channel, title, summary, category, priority, NotificationLifecycleStatus.ARCHIVED, sourceRefs, surfaceRef, requiredCapabilityId, owningWorkstreamId, origin, redactionLevel, dedupeKey, nextCorrelationId, traceRefs, createdAt, now, now, readAt, dismissedAt, now, snoozedUntil, expiresAt);
  }

  public NotificationItem snooze(Instant until, Instant now, String nextCorrelationId) {
    return new NotificationItem(notificationId, tenantId, customerId, accountId, selectedContextId, channel, title, summary, category, priority, NotificationLifecycleStatus.SNOOZED, sourceRefs, surfaceRef, requiredCapabilityId, owningWorkstreamId, origin, redactionLevel, dedupeKey, nextCorrelationId, traceRefs, createdAt, now, now, readAt, dismissedAt, archivedAt, until, expiresAt);
  }

  public boolean activeForCenter(Instant now, boolean includeRead) {
    if (status == NotificationLifecycleStatus.DISMISSED || status == NotificationLifecycleStatus.ARCHIVED || status == NotificationLifecycleStatus.EXPIRED) return false;
    if (status == NotificationLifecycleStatus.READ && !includeRead) return false;
    return status != NotificationLifecycleStatus.SNOOZED || snoozedUntil == null || !snoozedUntil.isAfter(now);
  }
}
