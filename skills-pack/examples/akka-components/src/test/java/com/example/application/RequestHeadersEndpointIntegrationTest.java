package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class RequestHeadersEndpointIntegrationTest extends TestKitSupport {

  record HeaderSummaryResponse(
      String requestId,
      String tenant,
      boolean internetPrincipal,
      int headerCount) {}

  @Test
  void requestHeadersAreAvailableThroughRequestContext() {
    var response =
        httpClient
            .GET("/request-headers/echo")
            .addHeader("X-Request-Id", "req-123")
            .addHeader("X-Tenant", "acme")
            .responseBodyAs(HeaderSummaryResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("req-123", response.body().requestId());
    assertEquals("acme", response.body().tenant());
    assertTrue(response.body().internetPrincipal());
    assertTrue(response.body().headerCount() >= 2);
  }

  @Test
  void missingRequiredHeaderReturnsBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                httpClient
                    .GET("/request-headers/echo")
                    .responseBodyAs(String.class)
                    .invoke());

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("X-Request-Id header is required"));
  }
}
