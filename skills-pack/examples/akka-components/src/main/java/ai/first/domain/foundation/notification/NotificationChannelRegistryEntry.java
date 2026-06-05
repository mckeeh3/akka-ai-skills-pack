package ai.first.domain.foundation.notification;

/** Browser-safe registry row for one notification delivery channel. */
public record NotificationChannelRegistryEntry(
    NotificationChannel channel,
    NotificationChannelStatus status,
    String providerKind,
    boolean productionConfigured,
    boolean localTestOutboxAvailable,
    String deliveryCapabilityId,
    String preferenceCapabilityId,
    String statusReason) {}
