package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.MediaTypes;
import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class LowLevelHttpEndpointIntegrationTest extends TestKitSupport {

  record HelloResponse(String greeting) {}

  record UploadSummaryResponse(String name, int bytes, String contentType) {}

  @Test
  void lowerLevelHttpResponseCanReturnJson() {
    var response =
        httpClient.GET("/low-level/hello/Ada/42").responseBodyAs(HelloResponse.class).invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("Hello Ada!", response.body().greeting());
  }

  @Test
  void lowerLevelHttpResponseCanReturnBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () -> httpClient.GET("/low-level/hello/Ada/200").responseBodyAs(String.class).invoke());

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
  }

  @Test
  void strictRequestBodyCanValidateContentType() {
    var jpeg = ContentTypes.create(MediaTypes.IMAGE_JPEG);

    var response =
        httpClient
            .POST("/low-level/images/avatar")
            .withRequestBody(jpeg, new byte[] {1, 2, 3, 4})
            .responseBodyAs(UploadSummaryResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("avatar", response.body().name());
    assertEquals(4, response.body().bytes());
    assertTrue(response.body().contentType().contains("image/jpeg"));
  }
}
