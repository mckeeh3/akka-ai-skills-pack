package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.time.Instant;
import java.util.Map;

/** Durable email delivery intent; production sends through Resend, tests/local capture it. */
public record EmailOutboxMessage(
    String outboxId,
    String messageType,
    String invitationId,
    String deliveryAttemptId,
    ScopeType scopeType,
    String tenantId,
    String customerId,
    String normalizedRecipientEmail,
    String inviteUrl,
    Map<String, String> templateVariables,
    String correlationId,
    Instant createdAt) {
  public EmailOutboxMessage {
    templateVariables = Map.copyOf(templateVariables == null ? Map.of() : templateVariables);
  }
}
