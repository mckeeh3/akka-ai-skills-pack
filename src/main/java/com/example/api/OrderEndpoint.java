package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.OrderEntity;
import com.example.domain.Order;
import com.example.domain.OrderValidator;
import java.util.List;

/**
 * HTTP endpoint for the order entity example.
 *
 * <p>This endpoint demonstrates the remaining event sourced entity endpoint patterns that were
 * previously shown by {@code EventSourcedEntityTemplateEndpoint}:
 *
 * <ul>
 *   <li>validating requests before invoking a flow-internal entity</li>
 *   <li>returning {@code HttpResponses.created(...)} on create</li>
 *   <li>exposing both normal and strongly consistent reads</li>
 *   <li>mapping replication filter commands to HTTP endpoints</li>
 * </ul>
 */
@HttpEndpoint("/orders")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class OrderEndpoint {

  public record CreateOrderRequest(String cartId, List<LineItemRequest> lineItems) {}

  public record LineItemRequest(String productId, String name, int quantity, boolean readyToShip) {}

  public record OrderLineItemResponse(
      String productId,
      String name,
      int quantity,
      boolean readyToShip) {}

  public record OrderResponse(
      String orderId,
      String cartId,
      List<OrderLineItemResponse> lineItems,
      boolean readyToShip,
      boolean created) {}

  public record RegionRequest(String region) {}

  public record StatusResponse(String orderId, String status) {}

  private final ComponentClient componentClient;

  public OrderEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/{orderId}")
  public OrderResponse getOrder(String orderId) {
    return toApi(
        componentClient
            .forEventSourcedEntity(orderId)
            .method(OrderEntity::getOrder)
            .invoke());
  }

  @Get("/{orderId}/consistent")
  public OrderResponse getOrderConsistent(String orderId) {
    return toApi(
        componentClient
            .forEventSourcedEntity(orderId)
            .method(OrderEntity::getOrderConsistent)
            .invoke());
  }

  @Post("/{orderId}")
  public HttpResponse createOrder(String orderId, CreateOrderRequest request) {
    var command =
        new Order.Command.CreateOrder(
            request.cartId(),
            request.lineItems().stream()
                .map(item -> new Order.LineItem(item.productId(), item.name(), item.quantity(), item.readyToShip()))
                .toList());
    var errors = OrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return HttpResponses.badRequest(String.join("; ", errors));
    }

    try {
      componentClient
          .forEventSourcedEntity(orderId)
          .method(OrderEntity::createOrder)
          .invoke(command);
      return HttpResponses.created(currentResponse(orderId));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Put("/{orderId}/line-items/{productId}/ready-to-ship")
  public HttpResponse lineItemReadyToShip(String orderId, String productId) {
    var command = new Order.Command.LineItemReadyToShip(productId);
    var errors = OrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return HttpResponses.badRequest(String.join("; ", errors));
    }

    try {
      componentClient
          .forEventSourcedEntity(orderId)
          .method(OrderEntity::lineItemReadyToShip)
          .invoke(command);
      return HttpResponses.ok(currentResponse(orderId));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Put("/{orderId}/replication/include")
  public HttpResponse includeRegion(String orderId, RegionRequest request) {
    var command = new Order.Command.IncludeRegion(request.region());
    var errors = OrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return HttpResponses.badRequest(String.join("; ", errors));
    }

    try {
      componentClient
          .forEventSourcedEntity(orderId)
          .method(OrderEntity::includeRegion)
          .invoke(command);
      return HttpResponses.ok(new StatusResponse(orderId, "included-region:" + request.region()));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Put("/{orderId}/replication/exclude")
  public HttpResponse excludeRegion(String orderId, RegionRequest request) {
    var command = new Order.Command.ExcludeRegion(request.region());
    var errors = OrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return HttpResponses.badRequest(String.join("; ", errors));
    }

    try {
      componentClient
          .forEventSourcedEntity(orderId)
          .method(OrderEntity::excludeRegion)
          .invoke(command);
      return HttpResponses.ok(new StatusResponse(orderId, "excluded-region:" + request.region()));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  private OrderResponse currentResponse(String orderId) {
    return toApi(
        componentClient
            .forEventSourcedEntity(orderId)
            .method(OrderEntity::getOrderConsistent)
            .invoke());
  }

  private static OrderResponse toApi(Order.State state) {
    return new OrderResponse(
        state.orderId(),
        state.cartId(),
        state.lineItems().stream()
            .map(
                item ->
                    new OrderLineItemResponse(
                        item.productId(), item.name(), item.quantity(), item.readyToShip()))
            .toList(),
        state.readyToShip(),
        state.created());
  }
}
