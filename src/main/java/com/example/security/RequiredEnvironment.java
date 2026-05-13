package com.example.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Startup validation for backend-only environment variables required by the authenticated seed app. */
public final class RequiredEnvironment {

  private static final List<String> REQUIRED_BACKEND_VARIABLES =
      List.of(
          "ADMIN_USERS",
          "WORKOS_API_KEY",
          "APP_PUBLIC_BASE_URL",
          "RESEND_API_KEY",
          "INVITE_EMAIL_FROM");

  private RequiredEnvironment() {}

  public static void validateOrThrow(Map<String, String> environment) {
    var missing = missingRequiredVariables(environment);
    if (!missing.isEmpty()) {
      throw new IllegalStateException(
          "Missing required backend environment variable(s): "
              + String.join(", ", missing)
              + ". Copy .env.example to .env, fill in the backend values, and source it before starting the app.");
    }
  }

  public static List<String> missingRequiredVariables(Map<String, String> environment) {
    var missing = new ArrayList<String>();
    for (var name : REQUIRED_BACKEND_VARIABLES) {
      var value = environment == null ? null : environment.get(name);
      if (value == null || value.isBlank()) {
        missing.add(name);
      }
    }
    return List.copyOf(missing);
  }
}
