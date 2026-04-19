package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.EventSourcedTemplate;
import org.junit.jupiter.api.Test;

/**
 * Integration tests that start the full Akka service and exercise both the entity and the HTTP
 * endpoint via {@code componentClient} and HTTP calls.
 *
 * <p>Run with {@code mvn verify}.
 */
class EventSourcedEntityTemplateIntegrationTest extends TestKitSupport {

  // -- Request/response records for HTTP tests ---------------------------
  // Mirrors the endpoint's public API records so the test does not depend on inner classes.

  record CreateRequest(String name) {}

  record RenameRequest(String name) {}

  record UpdateDetailsRequest(String description, int quantity) {}

  record EntityResponse(String entityId, String name, String description, int quantity) {}

  record StatusResponse(String entityId, String status) {}

  // -- Entity via ComponentClient ----------------------------------------

  @Test
  void createAndGetEntityViaComponentClient() {
    var entityId = "integration-cc-1";

    var createResult =
        await(
            componentClient
                .forEventSourcedEntity(entityId)
                .method(EventSourcedEntityTemplate::create)
                .invokeAsync(new EventSourcedTemplate.Command.Create("cc-entity")));

    assertEquals(Done.getInstance(), createResult);

    var state =
        await(
            componentClient
                .forEventSourcedEntity(entityId)
                .method(EventSourcedEntityTemplate::get)
                .invokeAsync());

    assertTrue(state.exists());
    assertEquals("cc-entity", state.name());
    assertEquals("", state.description());
    assertEquals(0, state.quantity());
  }

  @Test
  void fullLifecycleViaComponentClient() {
    var entityId = "integration-cc-2";

    // Create
    await(
        componentClient
            .forEventSourcedEntity(entityId)
            .method(EventSourcedEntityTemplate::create)
            .invokeAsync(new EventSourcedTemplate.Command.Create("lifecycle-test")));

    // Rename
    await(
        componentClient
            .forEventSourcedEntity(entityId)
            .method(EventSourcedEntityTemplate::rename)
            .invokeAsync(new EventSourcedTemplate.Command.Rename("renamed")));

    // Update details
    await(
        componentClient
            .forEventSourcedEntity(entityId)
            .method(EventSourcedEntityTemplate::updateDetails)
            .invokeAsync(
                new EventSourcedTemplate.Command.UpdateDetails("a description", 10)));

    // Verify state
    var state =
        await(
            componentClient
                .forEventSourcedEntity(entityId)
                .method(EventSourcedEntityTemplate::get)
                .invokeAsync());

    assertEquals("renamed", state.name());
    assertEquals("a description", state.description());
    assertEquals(10, state.quantity());

    // Delete
    await(
        componentClient
            .forEventSourcedEntity(entityId)
            .method(EventSourcedEntityTemplate::delete)
            .invokeAsync());
  }

  // -- Endpoint via HTTP -------------------------------------------------

  @Test
  void createAndGetViaEndpoint() {
    var entityId = "integration-http-1";

    var createResponse =
        await(
            httpClient
                .POST("/entity-template/" + entityId)
                .withRequestBody(new CreateRequest("http-entity"))
                .responseBodyAs(EntityResponse.class)
                .invokeAsync());

    assertTrue(createResponse.status().isSuccess());
    assertEquals("http-entity", createResponse.body().name());

    var getResponse =
        await(
            httpClient
                .GET("/entity-template/" + entityId)
                .responseBodyAs(EntityResponse.class)
                .invokeAsync());

    assertTrue(getResponse.status().isSuccess());
    var body = getResponse.body();
    assertEquals(entityId, body.entityId());
    assertEquals("http-entity", body.name());
    assertEquals("", body.description());
    assertEquals(0, body.quantity());
  }

  @Test
  void renameAndUpdateDetailsViaEndpoint() {
    var entityId = "integration-http-2";

    // Create
    await(
        httpClient
            .POST("/entity-template/" + entityId)
            .withRequestBody(new CreateRequest("original"))
            .responseBodyAs(EntityResponse.class)
            .invokeAsync());

    // Rename
    var renameResponse =
        await(
            httpClient
                .PUT("/entity-template/" + entityId + "/name")
                .withRequestBody(new RenameRequest("updated-name"))
                .responseBodyAs(EntityResponse.class)
                .invokeAsync());

    assertTrue(renameResponse.status().isSuccess());
    assertEquals("updated-name", renameResponse.body().name());

    // Update details
    var detailsResponse =
        await(
            httpClient
                .PUT("/entity-template/" + entityId + "/details")
                .withRequestBody(new UpdateDetailsRequest("new desc", 42))
                .responseBodyAs(EntityResponse.class)
                .invokeAsync());

    assertTrue(detailsResponse.status().isSuccess());
    assertEquals("new desc", detailsResponse.body().description());

    // Verify
    var getResponse =
        await(
            httpClient
                .GET("/entity-template/" + entityId)
                .responseBodyAs(EntityResponse.class)
                .invokeAsync());

    var body = getResponse.body();
    assertEquals("updated-name", body.name());
    assertEquals("new desc", body.description());
    assertEquals(42, body.quantity());
  }

  @Test
  void deleteViaEndpoint() {
    var entityId = "integration-http-3";

    // Create then delete
    await(
        httpClient
            .POST("/entity-template/" + entityId)
            .withRequestBody(new CreateRequest("to-delete"))
            .responseBodyAs(EntityResponse.class)
            .invokeAsync());

    var deleteResponse =
        await(
            httpClient
                .DELETE("/entity-template/" + entityId)
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(deleteResponse.status().isSuccess());
    assertEquals("deleted", deleteResponse.body().status());
  }

  @Test
  void createWithBlankNameReturnsBadRequest() {
    var entityId = "integration-http-4";

    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/entity-template/" + entityId)
                        .withRequestBody(new CreateRequest(""))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("name must not be blank"));
  }
}
