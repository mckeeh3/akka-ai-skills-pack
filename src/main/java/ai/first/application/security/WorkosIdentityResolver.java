package ai.first.application.security;

import akka.javasdk.JwtClaims;
import ai.first.domain.security.WorkosIdentity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Resolves a browser WorkOS/AuthKit identity without assuming access tokens contain email. */
public final class WorkosIdentityResolver {
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
  private static final ConcurrentMap<String, WorkosUserProfile> USER_CACHE = new ConcurrentHashMap<>();

  private WorkosIdentityResolver() {}

  public static WorkosIdentity fromClaims(JwtClaims claims) {
    var subject = claims.subject().or(() -> claims.getString("sub")).orElse(null);
    var email = firstPresent(claims.getString("email"), claims.getString("preferred_username"), claims.getString("username"));
    var name = firstPresent(claims.getString("name"), claims.getString("given_name"), claims.getString("nickname"));

    if ((email == null || email.isBlank()) && subject != null && !subject.isBlank()) {
      var profile = fetchWorkosUser(subject);
      if (profile.isPresent()) {
        email = profile.get().email();
        if (name == null || name.isBlank()) {
          name = profile.get().displayName();
        }
      }
    }

    return new WorkosIdentity(subject, email, name);
  }

  @SafeVarargs
  private static String firstPresent(Optional<String>... values) {
    for (var value : values) {
      if (value.isPresent() && !value.get().isBlank()) {
        return value.get();
      }
    }
    return null;
  }

  private static Optional<WorkosUserProfile> fetchWorkosUser(String userId) {
    var cached = USER_CACHE.get(userId);
    if (cached != null) {
      return Optional.of(cached);
    }

    var apiKey = config("WORKOS_API_KEY");
    if (apiKey == null) {
      return Optional.empty();
    }

    try {
      var baseUrl = Optional.ofNullable(config("WORKOS_API_BASE_URL")).orElse("https://api.workos.com");
      var encodedUserId = URLEncoder.encode(userId, StandardCharsets.UTF_8).replace("+", "%20");
      var request = HttpRequest.newBuilder()
          .uri(URI.create(stripTrailingSlash(baseUrl) + "/user_management/users/" + encodedUserId))
          .timeout(Duration.ofSeconds(5))
          .header("Authorization", "Bearer " + apiKey)
          .header("Accept", "application/json")
          .GET()
          .build();
      var response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        return Optional.empty();
      }
      JsonNode body = JSON.readTree(response.body());
      var email = text(body, "email");
      if (email == null || email.isBlank()) {
        return Optional.empty();
      }
      var profile = new WorkosUserProfile(email, displayName(text(body, "first_name"), text(body, "last_name"), email));
      USER_CACHE.put(userId, profile);
      return Optional.of(profile);
    } catch (Exception ignored) {
      return Optional.empty();
    }
  }

  private static String config(String name) {
    var value = System.getProperty(name);
    if (value == null || value.isBlank()) {
      value = System.getenv(name);
    }
    return value == null || value.isBlank() ? null : value.trim();
  }

  private static String stripTrailingSlash(String value) {
    while (value.endsWith("/")) {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }

  private static String text(JsonNode node, String field) {
    var value = node.get(field);
    return value == null || value.isNull() ? null : value.asText(null);
  }

  private static String displayName(String firstName, String lastName, String email) {
    var name = String.join(" ", firstName == null ? "" : firstName.trim(), lastName == null ? "" : lastName.trim()).trim();
    return name.isBlank() ? email : name;
  }

  static void clearCacheForTests() {
    USER_CACHE.clear();
  }

  private record WorkosUserProfile(String email, String displayName) {}
}
