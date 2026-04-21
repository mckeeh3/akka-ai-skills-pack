package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class StaticContentEndpointIntegrationTest extends TestKitSupport {

  @Test
  void indexServesPackagedHtml() {
    var response = httpClient.GET("/static-content/").responseBodyAs(String.class).invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains("HTTP Endpoint Example"));
    assertTrue(response.body().contains("/static-content/app.css"));
  }

  @Test
  void cssServesPackagedStylesheet() {
    var response = httpClient.GET("/static-content/app.css").responseBodyAs(String.class).invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains(".panel"));
    assertTrue(response.body().contains("background: #f6f8fb"));
  }

  @Test
  void bundledStaticResourceSubtreeServesFilesAndDirectoryIndexes() {
    var textResponse =
        httpClient
            .GET("/static-content/bundle/http-endpoint/help.txt")
            .responseBodyAs(String.class)
            .invoke();

    assertTrue(textResponse.status().isSuccess());
    assertTrue(textResponse.body().contains("staticResource(request, prefix)"));

    var indexResponse =
        httpClient
            .GET("/static-content/bundle/http-endpoint/guide/")
            .responseBodyAs(String.class)
            .invoke();

    assertTrue(indexResponse.status().isSuccess());
    assertTrue(indexResponse.body().contains("Bundled Guide"));
  }

  @Test
  void openApiYamlCanBeServedAsStaticResource() {
    var response = httpClient.GET("/static-content/openapi.yaml").responseBodyAs(String.class).invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains("openapi: 3.1.0"));
    assertTrue(response.body().contains("/greetings/hello/{name}"));
  }
}
