package ai.first.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Startup validation for backend-only environment variables required by the authenticated starter app. */
public final class RequiredEnvironment {

  private static final Logger logger = LoggerFactory.getLogger(RequiredEnvironment.class);

  private static final List<String> REQUIRED_BACKEND_VARIABLES =
      List.of(
          "ADMIN_USERS",
          "WORKOS_API_KEY",
          "WORKOS_JWT_ISSUER",
          "WORKOS_JWT_AUDIENCE",
          "APP_PUBLIC_BASE_URL",
          "RESEND_API_KEY");

  private RequiredEnvironment() {}

  public static void validateOrThrow(Map<String, String> environment) {
    var missing = missingRequiredVariables(environment);
    if (!missing.isEmpty()) {
      for (var name : missing) {
        logger.error("Required backend environment variable [{}] is not set or is blank", name);
      }
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
    var inviteFrom = environment == null ? null : environment.get("INVITE_EMAIL_FROM");
    var resendFrom = environment == null ? null : environment.get("RESEND_FROM_EMAIL");
    if ((inviteFrom == null || inviteFrom.isBlank()) && (resendFrom == null || resendFrom.isBlank())) {
      missing.add("INVITE_EMAIL_FROM");
      missing.add("RESEND_FROM_EMAIL");
    }
    return List.copyOf(missing);
  }
}
