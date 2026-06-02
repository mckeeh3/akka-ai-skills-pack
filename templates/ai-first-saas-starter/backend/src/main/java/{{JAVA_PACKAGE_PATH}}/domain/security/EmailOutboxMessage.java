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
    String subject,
    String bodyText,
    String bodyHtml,
    Map<String, String> templateVariables,
    String correlationId,
    Instant createdAt) {
  public EmailOutboxMessage(
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
    this(outboxId, messageType, invitationId, deliveryAttemptId, scopeType, tenantId, customerId, normalizedRecipientEmail, inviteUrl, null, null, null, templateVariables, correlationId, createdAt);
  }

  public EmailOutboxMessage {
    templateVariables = Map.copyOf(templateVariables == null ? Map.of() : templateVariables);
  }
}
