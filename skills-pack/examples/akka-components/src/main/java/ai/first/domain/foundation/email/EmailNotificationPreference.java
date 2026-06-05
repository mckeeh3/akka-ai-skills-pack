package ai.first.domain.foundation.email;

import ai.first.domain.foundation.notification.NotificationCategory;
import ai.first.domain.foundation.notification.NotificationPriority;
import java.time.Instant;

/** Backend-owned email notification preference; email is separate from in-app notification preferences. */
public record EmailNotificationPreference(
    String preferenceId,
    String tenantId,
    String customerId,
    String accountId,
    NotificationCategory category,
    boolean enabled,
    NotificationPriority minimumPriority,
    Instant muteUntil,
    Instant updatedAt,
    String updatedBy,
    String correlationId) {}
