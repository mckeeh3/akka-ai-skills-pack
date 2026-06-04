package ai.first.domain.foundation.notification;

import java.time.Instant;

/** Backend-owned bounded in-app notification preference. */
public record NotificationPreference(
    String preferenceId,
    String tenantId,
    String customerId,
    String accountId,
    NotificationChannel channel,
    NotificationCategory category,
    boolean enabled,
    NotificationPriority minimumPriority,
    Instant muteUntil,
    boolean includeReadInCenter,
    Instant updatedAt,
    String updatedBy,
    String correlationId) {}
