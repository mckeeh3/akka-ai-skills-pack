package ai.first.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;

class RequiredEnvironmentTest {

  @Test
  void reportsMissingRequiredBackendVariables() {
    var missing = RequiredEnvironment.missingRequiredVariables(Map.of("ADMIN_USERS", "admin@example.com:APP_ADMIN:ALL"));

    assertEquals(
        java.util.List.of(
            "WORKOS_API_KEY",
            "WORKOS_JWT_ISSUER",
            "WORKOS_JWT_AUDIENCE",
            "APP_PUBLIC_BASE_URL",
            "RESEND_API_KEY",
            "INVITE_EMAIL_FROM",
            "RESEND_FROM_EMAIL"),
        missing);
  }

  @Test
  void throwsWithActionableMessageWhenRequiredVariablesAreMissing() {
    var error = assertThrows(IllegalStateException.class, () -> RequiredEnvironment.validateOrThrow(Map.of()));

    assertEquals(
        "Missing required backend environment variable(s): ADMIN_USERS, WORKOS_API_KEY, WORKOS_JWT_ISSUER, WORKOS_JWT_AUDIENCE, APP_PUBLIC_BASE_URL, RESEND_API_KEY, INVITE_EMAIL_FROM, RESEND_FROM_EMAIL. Copy .env.example to .env, fill in the backend values, and source it before starting the app.",
        error.getMessage());
  }

  @Test
  void acceptsCompleteBackendEnvironment() {
    RequiredEnvironment.validateOrThrow(
        Map.of(
            "ADMIN_USERS", "admin@example.com:APP_ADMIN:ALL",
            "WORKOS_API_KEY", "sk_test_123",
            "WORKOS_JWT_ISSUER", "https://issuer.example.test",
            "WORKOS_JWT_AUDIENCE", "api://starter",
            "APP_PUBLIC_BASE_URL", "http://localhost:9000",
            "RESEND_API_KEY", "re_123",
            "INVITE_EMAIL_FROM", "no-reply@example.com"));
  }

  @Test
  void acceptsSharedResendFromEmailAsInviteSender() {
    RequiredEnvironment.validateOrThrow(
        Map.of(
            "ADMIN_USERS", "admin@example.com:APP_ADMIN:ALL",
            "WORKOS_API_KEY", "sk_test_123",
            "WORKOS_JWT_ISSUER", "https://issuer.example.test",
            "WORKOS_JWT_AUDIENCE", "api://starter",
            "APP_PUBLIC_BASE_URL", "http://localhost:9000",
            "RESEND_API_KEY", "re_123",
            "RESEND_FROM_EMAIL", "no-reply@example.com"));
  }
}
