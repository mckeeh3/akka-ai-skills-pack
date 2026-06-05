package com.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Backend-only Resend invitation email sender for admin-created local accounts. */
public class InvitationEmailSender {

  private static final Logger logger = LoggerFactory.getLogger(InvitationEmailSender.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final HttpClient httpClient;

  public InvitationEmailSender() {
    this(HttpClient.newHttpClient());
  }

  InvitationEmailSender(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public DeliveryResult sendInvitation(String email, String displayName) {
    var recipient = normalize(email);
    if (recipient.isBlank()) {
      return DeliveryResult.failed("missing-recipient");
    }

    var apiKey = getenv("RESEND_API_KEY");
    var from = getenv("INVITE_EMAIL_FROM");
    if (from.isBlank()) {
      from = getenv("RESEND_FROM_EMAIL");
    }
    var appUrl = getenv("APP_PUBLIC_BASE_URL");
    if (apiKey.isBlank() || from.isBlank() || appUrl.isBlank()) {
      logMissingRequired("RESEND_API_KEY", apiKey);
      if (from.isBlank()) {
        logMissingRequired("INVITE_EMAIL_FROM", from);
        logMissingRequired("RESEND_FROM_EMAIL", from);
      }
      logMissingRequired("APP_PUBLIC_BASE_URL", appUrl);
      logger.error(
          "Invitation email cannot be sent for [{}]; required Resend onboarding email configuration is missing",
          recipient);
      return DeliveryResult.failed("email-configuration-missing");
    }

    try {
      var name = displayName == null || displayName.isBlank() ? recipient : displayName.trim();
      var subject = getenv("INVITE_EMAIL_SUBJECT");
      if (subject.isBlank()) {
        subject = "You're invited";
      }
      var payload =
          Map.of(
              "from", from,
              "to", List.of(recipient),
              "subject", subject,
              "text", "Hello " + name + ",\n\nYou've been invited. Sign in here: " + appUrl + "\n");
      var request =
          HttpRequest.newBuilder()
              .uri(URI.create(resendEmailUrl()))
              .timeout(Duration.ofSeconds(10))
              .header("Authorization", "Bearer " + apiKey)
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
              .build();
      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        return DeliveryResult.sent();
      }
      logger.warn("Resend invitation email failed for [{}] with status [{}]: {}", recipient, response.statusCode(), response.body());
      return DeliveryResult.failed("resend-status-" + response.statusCode());
    } catch (Exception ex) {
      logger.warn("Invitation email delivery failed for [{}]", recipient, ex);
      return DeliveryResult.failed("delivery-error");
    }
  }

  private void logMissingRequired(String name, String value) {
    if (value == null || value.isBlank()) {
      logger.error("Required backend environment variable [{}] is not set or is blank", name);
    }
  }

  private String resendEmailUrl() {
    var baseUrl = getenv("RESEND_API_BASE_URL");
    if (baseUrl.isBlank()) {
      return "https://api.resend.com/emails";
    }
    return baseUrl.endsWith("/emails") ? baseUrl : baseUrl.replaceAll("/+$", "") + "/emails";
  }

  private String getenv(String name) {
    var value = System.getenv(name);
    return value == null ? "" : value.trim();
  }

  private static String normalize(String email) {
    return email == null ? "" : email.trim().toLowerCase();
  }

  public record DeliveryResult(String status, String reason) {
    static DeliveryResult sent() {
      return new DeliveryResult("SENT", "");
    }

    static DeliveryResult failed(String reason) {
      return new DeliveryResult("FAILED", reason);
    }
  }
}
