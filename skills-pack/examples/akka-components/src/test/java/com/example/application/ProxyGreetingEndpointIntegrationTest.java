package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class ProxyGreetingEndpointIntegrationTest extends TestKitSupport {

  record DelegatedGreetingResponse(String message, String delegatedTo) {}

  @Test
  void endpointCanDelegateToAnotherHttpRoute() {
    var baseUrl = "http://" + testKit.getHost() + ":" + testKit.getPort();

    var response =
        httpClient
            .GET("/proxy-greetings/hello/Ada?language=es")
            .addHeader("X-Base-Url", baseUrl)
            .responseBodyAs(DelegatedGreetingResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("Hola Ada!", response.body().message());
    assertEquals(baseUrl, response.body().delegatedTo());
  }

  @Test
  void missingBaseUrlHeaderReturnsBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                httpClient
                    .GET("/proxy-greetings/hello/Ada")
                    .responseBodyAs(String.class)
                    .invoke());

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("X-Base-Url header is required"));
  }
}
