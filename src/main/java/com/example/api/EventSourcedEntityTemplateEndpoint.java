package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.EventSourcedEntityTemplate;
import com.example.domain.EventSourcedTemplate;
import com.example.domain.EventSourcedTemplateValidator;
import java.util.List;

/**
 * HTTP endpoint that translates public API requests into entity commands.
 *
 * <p>This layer is responsible for:
 *
 * <ul>
 *   <li>Validating incoming requests before they reach the entity</li>
 *   <li>Mapping public API records to internal domain commands</li>
 *   <li>Mapping internal domain state to public API responses</li>
 *   <li>Exposing the notification stream as Server-Sent Events</li>
 * </ul>
 */
@HttpEndpoint("/entity-template")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class EventSourcedEntityTemplateEndpoint {

  private final ComponentClient componentClient;

  public EventSourcedEntityTemplateEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  // -- Public API records ------------------------------------------------

  /** Public response returned to callers — decoupled from internal domain state. */
  public record EntityResponse(String entityId, String name, String description, int quantity) {}

  public record NotificationResponse(String type, String value) {}

  public record StatusResponse(String entityId, String status) {}

  public record RegionRequest(String region) {}

  public record CreateRequest(String name) {}

  public record RenameRequest(String name) {}

  public record UpdateDetailsRequest(String description, int quantity) {}

  // -- Endpoints ---------------------------------------------------------

  @Get("/{entityId}")
  public EntityResponse get(String entityId) {
    return toApiResponse(
        componentClient
            .forEventSourcedEntity(entityId)
            .method(EventSourcedEntityTemplate::get)
            .invoke());
  }

  @Get("/{entityId}/consistent")
  public EntityResponse getConsistent(String entityId) {
    return currentResponse(entityId);
  }

  @Post("/{entityId}")
  public HttpResponse create(String entityId, CreateRequest request) {
    var command = new EventSourcedTemplate.Command.Create(request.name());
    var errors = EventSourcedTemplateValidator.validate(command);
    if (!errors.isEmpty()) {
      return badRequest(errors);
    }

    componentClient
        .forEventSourcedEntity(entityId)
        .method(EventSourcedEntityTemplate::create)
        .invoke(command);

    return HttpResponses.created(currentResponse(entityId));
  }

  @Put("/{entityId}/name")
  public HttpResponse rename(String entityId, RenameRequest request) {
    var command = new EventSourcedTemplate.Command.Rename(request.name());
    var errors = EventSourcedTemplateValidator.validate(command);
    if (!errors.isEmpty()) {
      return badRequest(errors);
    }

    componentClient
        .forEventSourcedEntity(entityId)
        .method(EventSourcedEntityTemplate::rename)
        .invoke(command);

    return HttpResponses.ok(currentResponse(entityId));
  }

  @Put("/{entityId}/details")
  public HttpResponse updateDetails(String entityId, UpdateDetailsRequest request) {
    var command =
        new EventSourcedTemplate.Command.UpdateDetails(request.description(), request.quantity());
    var errors = EventSourcedTemplateValidator.validate(command);
    if (!errors.isEmpty()) {
      return badRequest(errors);
    }

    componentClient
        .forEventSourcedEntity(entityId)
        .method(EventSourcedEntityTemplate::updateDetails)
        .invoke(command);

    return HttpResponses.ok(currentResponse(entityId));
  }

  @Put("/{entityId}/replication/include")
  public HttpResponse includeRegion(String entityId, RegionRequest request) {
    var command = new EventSourcedTemplate.Command.IncludeRegion(request.region());
    var errors = EventSourcedTemplateValidator.validate(command);
    if (!errors.isEmpty()) {
      return badRequest(errors);
    }

    componentClient
        .forEventSourcedEntity(entityId)
        .method(EventSourcedEntityTemplate::includeRegion)
        .invoke(command);

    return HttpResponses.ok(new StatusResponse(entityId, "included-region:" + request.region()));
  }

  @Put("/{entityId}/replication/exclude")
  public HttpResponse excludeRegion(String entityId, RegionRequest request) {
    var command = new EventSourcedTemplate.Command.ExcludeRegion(request.region());
    var errors = EventSourcedTemplateValidator.validate(command);
    if (!errors.isEmpty()) {
      return badRequest(errors);
    }

    componentClient
        .forEventSourcedEntity(entityId)
        .method(EventSourcedEntityTemplate::excludeRegion)
        .invoke(command);

    return HttpResponses.ok(new StatusResponse(entityId, "excluded-region:" + request.region()));
  }

  @Delete("/{entityId}")
  public HttpResponse delete(String entityId) {
    componentClient
        .forEventSourcedEntity(entityId)
        .method(EventSourcedEntityTemplate::delete)
        .invoke();

    return HttpResponses.ok(new StatusResponse(entityId, "deleted"));
  }

  /**
   * Server-Sent Events stream of entity notifications.
   *
   * <p>Emits events in real time after the client subscribes. Does not replay historical events. If
   * the stream detects missing messages it will fail, allowing clients to reconnect.
   */
  @Get("/{entityId}/notifications")
  public HttpResponse notifications(String entityId) {
    var source =
        componentClient
            .forEventSourcedEntity(entityId)
            .notificationStream(EventSourcedEntityTemplate::notifications)
            .source()
            .map(EventSourcedEntityTemplateEndpoint::toNotificationPayload);
    return HttpResponses.serverSentEvents(source);
  }

  // -- Mapping helpers ---------------------------------------------------

  private static EntityResponse toApiResponse(EventSourcedTemplate.State state) {
    return new EntityResponse(
        state.entityId(), state.name(), state.description(), state.quantity());
  }

  private EntityResponse currentResponse(String entityId) {
    return toApiResponse(
        componentClient
            .forEventSourcedEntity(entityId)
            .method(EventSourcedEntityTemplate::getConsistent)
            .invoke());
  }

  private static NotificationResponse toNotificationPayload(EventSourcedTemplate.Event event) {
    return switch (event) {
      case EventSourcedTemplate.Event.Created e ->
          new NotificationResponse("created", e.name());
      case EventSourcedTemplate.Event.Renamed e ->
          new NotificationResponse("renamed", e.name());
      case EventSourcedTemplate.Event.DescriptionUpdated e ->
          new NotificationResponse("description-updated", e.description());
      case EventSourcedTemplate.Event.QuantityUpdated e ->
          new NotificationResponse("quantity-updated", String.valueOf(e.quantity()));
      case EventSourcedTemplate.Event.Deleted e -> new NotificationResponse("deleted", "");
    };
  }

  private static HttpResponse badRequest(List<String> errors) {
    return HttpResponses.badRequest(String.join("; ", errors));
  }
}
