package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import java.util.List;

/** Resend-only production email seam with safe local/test captured delivery. */
public final class ResendEmailService {
  public DeliveryResult deliver(EmailOutboxMessage message, DeliveryMode mode) {
    if (mode == DeliveryMode.PRODUCTION) {
      var apiKey = System.getenv("RESEND_API_KEY");
      var from = System.getenv().getOrDefault("INVITE_EMAIL_FROM", System.getenv("RESEND_FROM_EMAIL"));
      if (apiKey == null || apiKey.isBlank() || from == null || from.isBlank()) {
        return new DeliveryResult(false, null, "resend-config-missing");
      }
      // Generated apps replace this seam with the real Resend SDK/HTTP client adapter.
      return new DeliveryResult(true, "resend-stub-" + message.outboxId(), null);
    }
    return new DeliveryResult(true, "captured-" + message.outboxId(), null);
  }

  public List<CapturedEmail> captureAll(InvitationRepository repository) {
    return repository.queuedEmails().stream()
        .map(message -> new CapturedEmail(message.outboxId(), message.normalizedRecipientEmail(), message.inviteUrl(), message.correlationId()))
        .toList();
  }

  public enum DeliveryMode {
    PRODUCTION,
    LOCAL_OR_TEST
  }

  public record DeliveryResult(boolean success, String providerMessageId, String safeErrorSummary) {}

  public record CapturedEmail(String outboxId, String recipient, String inviteUrl, String correlationId) {}
}
