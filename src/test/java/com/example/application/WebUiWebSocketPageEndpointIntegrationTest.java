package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class WebUiWebSocketPageEndpointIntegrationTest extends TestKitSupport {

  @Test
  void packagedWebSocketPageReferencesExpectedSocketRoute() {
    var response = httpClient.GET("/ui/websocket").responseBodyAs(String.class).invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains("Browser page using Akka WebSocket"));
    assertTrue(response.body().contains("/websockets/ping"));
    assertTrue(response.body().contains("/ui/websocket/app.js"));
  }

  @Test
  void packagedWebSocketScriptContainsBrowserSocketLogic() {
    var response = httpClient.GET("/ui/websocket/app.js").responseBodyAs(String.class).invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains("new WebSocket"));
    assertTrue(response.body().contains("socket.send(\"ping\")"));
  }
}
