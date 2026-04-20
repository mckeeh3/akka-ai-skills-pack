package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class InternalStatusEndpointIntegrationTest extends TestKitSupport {

  record CallerResponse(String origin, String localService, boolean internet) {}

  record StatusResponse(String status) {}

  @Test
  void internetCallIsDeniedForInternalOnlyMethod() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () -> httpClient.GET("/internal-status/whoami").responseBodyAs(String.class).invoke());

    assertTrue(error.getMessage().contains("403") || error.getMessage().contains("404"));
  }

  @Test
  void methodLevelAclOverrideAllowsPublicRoute() {
    var response =
        httpClient.GET("/internal-status/public-ping").responseBodyAs(StatusResponse.class).invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("ok", response.body().status());
  }

  @Test
  void impersonatedServiceCanCallInternalOnlyMethod() {
    var response =
        httpClient
            .GET("/internal-status/whoami")
            .addHeader("impersonate-service", "caller-service")
            .responseBodyAs(CallerResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("service", response.body().origin());
    assertEquals("caller-service", response.body().localService());
    assertTrue(!response.body().internet());
  }
}
