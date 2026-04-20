package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class GreetingEndpointIntegrationTest extends TestKitSupport {

  record ComposeGreetingRequest(String name) {}

  record GreetingResponse(String message, String language, boolean shouted) {}

  @Test
  void helloUsesQueryParametersFromRequestContext() {
    var response =
        await(
            httpClient
                .GET("/greetings/hello/Akka?language=sv&style=shout")
                .responseBodyAs(GreetingResponse.class)
                .invokeAsync());

    assertTrue(response.status().isSuccess());
    assertEquals("HEJ AKKA!", response.body().message());
    assertEquals("sv", response.body().language());
    assertTrue(response.body().shouted());
  }

  @Test
  void composeUsesPathParameterAndJsonBody() {
    var response =
        await(
            httpClient
                .POST("/greetings/compose/es")
                .withRequestBody(new ComposeGreetingRequest("Ada"))
                .responseBodyAs(GreetingResponse.class)
                .invokeAsync());

    assertTrue(response.status().isSuccess());
    assertEquals("Hola Ada!", response.body().message());
    assertEquals("es", response.body().language());
    assertTrue(!response.body().shouted());
  }

  @Test
  void patchCanUpdateGreetingStyle() {
    var response =
        httpClient
            .PATCH("/greetings/hello/Akka/shout")
            .responseBodyAs(GreetingResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("HELLO AKKA!", response.body().message());
    assertTrue(response.body().shouted());
  }

  @Test
  void blankNameReturnsBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/greetings/compose/en")
                        .withRequestBody(new ComposeGreetingRequest(" "))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("name must not be blank"));
  }
}
