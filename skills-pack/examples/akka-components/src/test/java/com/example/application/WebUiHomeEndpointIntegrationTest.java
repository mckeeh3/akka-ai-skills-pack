package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class WebUiHomeEndpointIntegrationTest extends TestKitSupport {

  @Test
  void packagedPageReferencesUiAndApiRoutes() {
    var response = httpClient.GET("/ui").responseBodyAs(String.class).invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains("Akka HTTP endpoint web UI example"));
    assertTrue(response.body().contains("/ui/app.css"));
    assertTrue(response.body().contains("/ui/app.js"));
    assertTrue(response.body().contains("/api/web-ui/summary"));
  }

  @Test
  void packagedCssAndJavaScriptAssetsAreServed() {
    var cssResponse = httpClient.GET("/ui/app.css").responseBodyAs(String.class).invoke();
    assertTrue(cssResponse.status().isSuccess());
    assertTrue(cssResponse.body().contains(".app-shell"));
    assertTrue(cssResponse.body().contains("background: linear-gradient"));

    var jsResponse = httpClient.GET("/ui/app.js").responseBodyAs(String.class).invoke();
    assertTrue(jsResponse.status().isSuccess());
    assertTrue(jsResponse.body().contains("fetch(apiPath"));
    assertTrue(jsResponse.body().contains("DOMContentLoaded"));
  }
}
