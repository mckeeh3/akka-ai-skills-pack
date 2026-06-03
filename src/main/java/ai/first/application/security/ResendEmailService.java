package ai.first.application.security;

import ai.first.domain.security.EmailOutboxMessage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Resend-only production email seam with safe local/test captured delivery. */
public final class ResendEmailService {
  private static final Logger logger = LoggerFactory.getLogger(ResendEmailService.class);
  private static final String DEFAULT_RESEND_API_BASE_URL = "https://api.resend.com";

  private final Map<String, String> environment;
  private final EmailDeliveryPort resendAdapter;

  public ResendEmailService() {
    this(System.getenv(), new ResendHttpEmailDeliveryAdapter(new JavaNetHttpTransport()));
  }

  public ResendEmailService(Map<String, String> environment, EmailDeliveryPort resendAdapter) {
    this.environment = Map.copyOf(environment == null ? Map.of() : environment);
    this.resendAdapter = Objects.requireNonNull(resendAdapter, "resendAdapter");
  }

  public DeliveryResult deliver(EmailOutboxMessage message, DeliveryMode mode) {
    Objects.requireNonNull(message, "message");
    if (mode == DeliveryMode.PRODUCTION) {
      var config = ResendConfiguration.from(environment);
      if (!config.ready()) {
        logMissingRequired("RESEND_API_KEY", config.apiKey());
        if (config.fromEmail() == null || config.fromEmail().isBlank()) {
          logger.error("Required backend environment variable [INVITE_EMAIL_FROM] is not set or is blank");
          logger.error("Required backend environment variable [RESEND_FROM_EMAIL] is not set or is blank");
        }
        logger.error("Production email delivery is blocked because required Resend environment configuration is missing");
        return DeliveryResult.failed("resend-config-missing");
      }
      try {
        return resendAdapter.deliver(message, config);
      } catch (RuntimeException e) {
        return DeliveryResult.failed("resend-adapter-error");
      }
    }
    return DeliveryResult.captured("captured-" + message.outboxId());
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

  private static void logMissingRequired(String name, String value) {
    if (value == null || value.isBlank()) {
      logger.error("Required backend environment variable [{}] is not set or is blank", name);
    }
  }

  public interface EmailDeliveryPort {
    DeliveryResult deliver(EmailOutboxMessage message, ResendConfiguration config);
  }

  public record ResendConfiguration(String apiKey, String fromEmail, String inviteSubject, URI apiBaseUri) {
    static ResendConfiguration from(Map<String, String> environment) {
      var apiKey = trimToNull(environment.get("RESEND_API_KEY"));
      var from = trimToNull(Optional.ofNullable(environment.get("INVITE_EMAIL_FROM")).orElse(environment.get("RESEND_FROM_EMAIL")));
      var subject = Optional.ofNullable(trimToNull(environment.get("INVITE_EMAIL_SUBJECT"))).orElse("You're invited");
      var baseUrl = Optional.ofNullable(trimToNull(environment.get("RESEND_API_BASE_URL"))).orElse(DEFAULT_RESEND_API_BASE_URL);
      return new ResendConfiguration(apiKey, from, subject, URI.create(baseUrl));
    }

    boolean ready() {
      return apiKey != null && fromEmail != null;
    }
  }

  public static final class ResendHttpEmailDeliveryAdapter implements EmailDeliveryPort {
    private final HttpTransport transport;

    public ResendHttpEmailDeliveryAdapter(HttpTransport transport) {
      this.transport = Objects.requireNonNull(transport, "transport");
    }

    @Override
    public DeliveryResult deliver(EmailOutboxMessage message, ResendConfiguration config) {
      var request = buildRequest(message, config);
      try {
        var response = transport.send(request);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
          return DeliveryResult.sent(extractJsonString(response.body(), "id").orElse("resend-accepted-" + message.outboxId()));
        }
        return DeliveryResult.failed("resend-http-" + response.statusCode());
      } catch (IOException e) {
        return DeliveryResult.failed("resend-io-error");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return DeliveryResult.failed("resend-interrupted");
      }
    }

    public HttpRequest buildRequest(EmailOutboxMessage message, ResendConfiguration config) {
      var endpoint = config.apiBaseUri().resolve(config.apiBaseUri().getPath().endsWith("/") ? "emails" : config.apiBaseUri().getPath() + "/emails");
      var body = "{"
          + jsonField("from", config.fromEmail()) + ","
          + jsonField("to", message.normalizedRecipientEmail()) + ","
          + jsonField("subject", subject(message, config)) + ","
          + jsonField("text", textBody(message)) + ","
          + jsonField("headers", "X-Correlation-Id: " + message.correlationId())
          + "}";
      return HttpRequest.newBuilder(endpoint)
          .timeout(Duration.ofSeconds(10))
          .header("Authorization", "Bearer " + config.apiKey())
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(body))
          .build();
    }

    private static String subject(EmailOutboxMessage message, ResendConfiguration config) {
      return message.subject() == null || message.subject().isBlank() ? config.inviteSubject() : message.subject();
    }

    private static String textBody(EmailOutboxMessage message) {
      if (message.bodyText() != null && !message.bodyText().isBlank()) {
        return message.bodyText();
      }
      return "You have been invited. Open this invitation link to continue: " + message.inviteUrl()
          + "\n\nThis message was generated by the governed invitation onboarding flow.";
    }
  }

  public interface HttpTransport {
    HttpTransportResponse send(HttpRequest request) throws IOException, InterruptedException;
  }

  public record HttpTransportResponse(int statusCode, String body) {}

  private static final class JavaNetHttpTransport implements HttpTransport {
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    @Override
    public HttpTransportResponse send(HttpRequest request) throws IOException, InterruptedException {
      var response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return new HttpTransportResponse(response.statusCode(), response.body());
    }
  }

  public record DeliveryResult(boolean success, String providerMessageId, String safeErrorSummary, DeliveryKind kind) {
    static DeliveryResult sent(String providerMessageId) {
      return new DeliveryResult(true, providerMessageId, null, DeliveryKind.SENT);
    }

    static DeliveryResult captured(String providerMessageId) {
      return new DeliveryResult(true, providerMessageId, null, DeliveryKind.CAPTURED);
    }

    static DeliveryResult failed(String safeErrorSummary) {
      return new DeliveryResult(false, null, safeErrorSummary, DeliveryKind.FAILED);
    }
  }

  public enum DeliveryKind {
    SENT,
    CAPTURED,
    FAILED
  }

  public record CapturedEmail(String outboxId, String recipient, String inviteUrl, String correlationId) {}

  private static String trimToNull(String value) {
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }

  private static String jsonField(String name, String value) {
    return "\"" + jsonEscape(name) + "\":\"" + jsonEscape(value) + "\"";
  }

  private static String jsonEscape(String value) {
    return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
  }

  private static Optional<String> extractJsonString(String json, String fieldName) {
    if (json == null) return Optional.empty();
    var marker = "\"" + fieldName + "\"";
    var field = json.indexOf(marker);
    if (field < 0) return Optional.empty();
    var colon = json.indexOf(':', field + marker.length());
    if (colon < 0) return Optional.empty();
    var start = json.indexOf('"', colon + 1);
    if (start < 0) return Optional.empty();
    var end = json.indexOf('"', start + 1);
    if (end < 0) return Optional.empty();
    return Optional.of(json.substring(start + 1, end));
  }
}
