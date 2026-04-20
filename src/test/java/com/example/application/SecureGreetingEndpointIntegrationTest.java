package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SecureGreetingEndpointIntegrationTest extends TestKitSupport {

  record AuthenticatedGreetingResponse(String issuer, String subject, String role, String message) {}

  @Test
  void bearerTokenClaimsAreAvailableThroughRequestContext() throws Exception {
    var response =
        httpClient
            .GET("/secure-greetings/me")
            .addHeader(
                "Authorization",
                "Bearer " + bearerTokenWith(Map.of("iss", "test-issuer", "sub", "alice", "role", "reader")))
            .responseBodyAs(AuthenticatedGreetingResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("test-issuer", response.body().issuer());
    assertEquals("alice", response.body().subject());
    assertEquals("reader", response.body().role());
    assertEquals("Hello alice!", response.body().message());
  }

  @Test
  void missingBearerTokenIsRejected() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                httpClient
                    .GET("/secure-greetings/me")
                    .responseBodyAs(String.class)
                    .invoke());

    assertTrue(error.getMessage().contains("401") || error.getMessage().contains("403"));
  }

  private String bearerTokenWith(Map<String, String> claims) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(claims));
    return header + "." + payload;
  }
}
