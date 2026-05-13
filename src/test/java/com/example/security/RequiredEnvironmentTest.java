package com.example.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;

class RequiredEnvironmentTest {

  @Test
  void reportsMissingRequiredBackendVariables() {
    var missing = RequiredEnvironment.missingRequiredVariables(Map.of("ADMIN_USERS", "admin@example.com:APP_ADMIN:ALL"));

    assertEquals(
        java.util.List.of("WORKOS_API_KEY", "APP_PUBLIC_BASE_URL", "RESEND_API_KEY", "INVITE_EMAIL_FROM"),
        missing);
  }

  @Test
  void throwsWithActionableMessageWhenRequiredVariablesAreMissing() {
    var error = assertThrows(IllegalStateException.class, () -> RequiredEnvironment.validateOrThrow(Map.of()));

    assertEquals(
        "Missing required backend environment variable(s): ADMIN_USERS, WORKOS_API_KEY, APP_PUBLIC_BASE_URL, RESEND_API_KEY, INVITE_EMAIL_FROM. Copy .env.example to .env, fill in the backend values, and source it before starting the app.",
        error.getMessage());
  }

  @Test
  void acceptsCompleteBackendEnvironment() {
    RequiredEnvironment.validateOrThrow(
        Map.of(
            "ADMIN_USERS", "admin@example.com:APP_ADMIN:ALL",
            "WORKOS_API_KEY", "sk_test_123",
            "APP_PUBLIC_BASE_URL", "http://localhost:9000",
            "RESEND_API_KEY", "re_123",
            "INVITE_EMAIL_FROM", "no-reply@example.com"));
  }
}
