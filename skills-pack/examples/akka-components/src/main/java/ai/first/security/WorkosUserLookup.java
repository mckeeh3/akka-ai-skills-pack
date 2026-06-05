package ai.first.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Backend-only WorkOS API lookup used when the browser JWT lacks an email claim. */
public class WorkosUserLookup {

  private static final Logger logger = LoggerFactory.getLogger(WorkosUserLookup.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final HttpClient httpClient;

  public WorkosUserLookup() {
    this(HttpClient.newHttpClient());
  }

  WorkosUserLookup(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public WorkosUser userById(String workosUserId) {
    var normalizedUserId = workosUserId == null ? "" : workosUserId.trim();
    if (normalizedUserId.isBlank()) {
      return WorkosUser.empty("");
    }

    var apiKey = getenv("WORKOS_API_KEY");
    if (apiKey.isBlank()) {
      logger.error("Required backend environment variable [WORKOS_API_KEY] is not set or is blank; cannot look up WorkOS user [{}]", normalizedUserId);
      return WorkosUser.empty(normalizedUserId);
    }

    try {
      var baseUrl = getenv("WORKOS_API_BASE_URL");
      if (baseUrl.isBlank()) {
        baseUrl = "https://api.workos.com";
      }
      var encodedUserId = URLEncoder.encode(normalizedUserId, StandardCharsets.UTF_8);
      var request =
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/user_management/users/" + encodedUserId))
              .header("Authorization", "Bearer " + apiKey)
              .header("Accept", "application/json")
              .GET()
              .build();

      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        logger.warn(
            "WorkOS user lookup failed for [{}] with status [{}]: {}",
            normalizedUserId,
            response.statusCode(),
            response.body());
        return WorkosUser.empty(normalizedUserId);
      }

      JsonNode json = objectMapper.readTree(response.body());
      return new WorkosUser(
          json.path("id").asText(normalizedUserId),
          json.path("email").asText(""),
          json.path("email_verified").asBoolean(false),
          json.path("first_name").asText(""),
          json.path("last_name").asText(""));
    } catch (Exception ex) {
      logger.warn("WorkOS user lookup failed for [{}]", normalizedUserId, ex);
      return WorkosUser.empty(normalizedUserId);
    }
  }

  private String getenv(String name) {
    var value = System.getenv(name);
    return value == null ? "" : value.trim();
  }

  public record WorkosUser(String id, String email, boolean emailVerified, String firstName, String lastName) {
    public WorkosUser {
      id = id == null ? "" : id.trim();
      email = email == null ? "" : email.trim().toLowerCase();
      firstName = firstName == null ? "" : firstName.trim();
      lastName = lastName == null ? "" : lastName.trim();
    }

    public static WorkosUser empty(String id) {
      return new WorkosUser(id, "", false, "", "");
    }

    public String displayName() {
      return (firstName + " " + lastName).trim();
    }
  }
}
