package com.example.security;

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

public class WorkosUserLookup {
  private static final Logger logger = LoggerFactory.getLogger(WorkosUserLookup.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final HttpClient httpClient = HttpClient.newHttpClient();

  public WorkosUser userById(String workosUserId) {
    var apiKey = getenv("WORKOS_API_KEY");
    if (apiKey.isBlank()) {
      logger.warn("WORKOS_API_KEY is not set; cannot look up WorkOS user [{}]", workosUserId);
      return WorkosUser.empty(workosUserId);
    }

    try {
      var baseUrl = getenv("WORKOS_API_BASE_URL");
      if (baseUrl.isBlank()) baseUrl = "https://api.workos.com";

      var encodedUserId = URLEncoder.encode(workosUserId, StandardCharsets.UTF_8);
      var request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/user_management/users/" + encodedUserId))
        .header("Authorization", "Bearer " + apiKey)
        .header("Accept", "application/json")
        .GET()
        .build();

      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        logger.warn("WorkOS user lookup failed for [{}] with status [{}]: {}", workosUserId, response.statusCode(), response.body());
        return WorkosUser.empty(workosUserId);
      }

      JsonNode json = objectMapper.readTree(response.body());
      return new WorkosUser(
        json.path("id").asText(workosUserId),
        json.path("email").asText(""),
        json.path("email_verified").asBoolean(false),
        json.path("first_name").asText(""),
        json.path("last_name").asText("")
      );
    } catch (Exception e) {
      logger.warn("WorkOS user lookup failed for [{}]", workosUserId, e);
      return WorkosUser.empty(workosUserId);
    }
  }

  private String getenv(String name) {
    var value = System.getenv(name);
    return value == null ? "" : value.trim();
  }

  public record WorkosUser(String id, String email, boolean emailVerified, String firstName, String lastName) {
    public static WorkosUser empty(String id) {
      return new WorkosUser(id, "", false, "", "");
    }
  }
}
