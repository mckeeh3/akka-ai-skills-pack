package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class WebUiSsePageEndpointIntegrationTest extends TestKitSupport {

  @Test
  void packagedSsePageReferencesExpectedStreamRoute() {
    var response = httpClient.GET("/ui/sse").responseBodyAs(String.class).invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains("Browser page using Akka SSE"));
    assertTrue(response.body().contains("/counter-stream/numbers"));
    assertTrue(response.body().contains("/ui/sse/app.js"));
  }

  @Test
  void packagedSseScriptContainsBrowserEventSourceLogic() {
    var response = httpClient.GET("/ui/sse/app.js").responseBodyAs(String.class).invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains("new EventSource(streamPath)"));
    assertTrue(response.body().contains("counter"));
  }
}
