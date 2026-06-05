package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.List;
import org.junit.jupiter.api.Test;

class FrontendReferenceWebUiIntegrationTest extends TestKitSupport {

  record RequestRow(String id, String title, String requester, String status, int amount) {}

  record DashboardResponse(
      List<RequestRow> requests, List<String> allowedStatuses, String streamPath, String submitPath) {}

  record SubmitRequest(String title, String requester, int amount) {}

  record SubmitResponse(RequestRow request, String message) {}

  @Test
  void servesReferenceFrontendShellAndModuleAssets() {
    var page = httpClient.GET("/ui/frontend-reference").responseBodyAs(String.class).invoke();

    assertTrue(page.status().isSuccess());
    assertTrue(page.body().contains("Purchase request dashboard"));
    assertTrue(page.body().contains("type=\"module\" src=\"/ui/frontend-reference/app.js\""));
    assertTrue(page.body().contains("data-api-path=\"/api/frontend-reference/dashboard\""));
    assertTrue(page.body().contains("aria-describedby=\"title-error\""));

    var css = httpClient.GET("/ui/frontend-reference/app.css").responseBodyAs(String.class).invoke();
    assertTrue(css.status().isSuccess());
    assertTrue(css.body().contains(".skip-link"));
    assertTrue(css.body().contains(":focus-visible"));

    var appJs = httpClient.GET("/ui/frontend-reference/app.js").responseBodyAs(String.class).invoke();
    assertTrue(appJs.status().isSuccess());
    assertTrue(appJs.body().contains("from \"./api.js\""));
    assertTrue(appJs.body().contains("DOMContentLoaded"));

    var apiJs = httpClient.GET("/ui/frontend-reference/api.js").responseBodyAs(String.class).invoke();
    assertTrue(apiJs.status().isSuccess());
    assertTrue(apiJs.body().contains("fetch(apiPath"));
  }

  @Test
  void dashboardApiReturnsBrowserFacingData() {
    var response =
        httpClient
            .GET("/api/frontend-reference/dashboard")
            .responseBodyAs(DashboardResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals(3, response.body().requests().size());
    assertTrue(response.body().allowedStatuses().contains("Pending"));
    assertEquals("/api/frontend-reference/requests", response.body().submitPath());
  }

  @Test
  void submitApiCoversSuccessAndValidationFailure() {
    var response =
        httpClient
            .POST("/api/frontend-reference/requests")
            .withRequestBody(new SubmitRequest("New monitor", "Ada", 700))
            .responseBodyAs(SubmitResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("REQ-DRAFT", response.body().request().id());
    assertEquals("Pending", response.body().request().status());

    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                httpClient
                    .POST("/api/frontend-reference/requests")
                    .withRequestBody(new SubmitRequest(" ", "Ada", 700))
                    .responseBodyAs(String.class)
                    .invoke());

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("title is required"));
  }
}
