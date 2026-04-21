package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.List;
import org.junit.jupiter.api.Test;

class WebUiDataEndpointIntegrationTest extends TestKitSupport {

  record WebUiSummaryResponse(String title, String message, List<String> capabilities, String apiPath) {}

  @Test
  void summaryRouteReturnsBrowserConsumableJson() {
    var response =
        httpClient
            .GET("/api/web-ui/summary")
            .responseBodyAs(WebUiSummaryResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("Co-hosted Akka web UI", response.body().title());
    assertEquals("/api/web-ui/summary", response.body().apiPath());
    assertTrue(response.body().message().contains("/api route"));
    assertTrue(response.body().capabilities().contains("Framework-free browser code authored in TypeScript"));
  }
}
