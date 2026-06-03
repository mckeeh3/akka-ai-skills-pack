package ai.first.domain.security;

import java.time.Instant;
import java.util.Map;

/** Captured local/test outbox row for provider-neutral external notification delivery. */
public record NotificationExternalOutboxMessage(
    String outboxId,
    String tenantId,
    String customerId,
    String accountId,
    NotificationChannel channel,
    String destinationSummary,
    String title,
    String previewText,
    Map<String, String> metadata,
    String correlationId,
    Instant createdAt) {
  public NotificationExternalOutboxMessage {
    metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
  }
}
