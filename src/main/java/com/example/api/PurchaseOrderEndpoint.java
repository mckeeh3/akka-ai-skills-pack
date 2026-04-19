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
import com.example.application.PurchaseOrderEntity;
import com.example.domain.PurchaseOrder;
import com.example.domain.PurchaseOrderValidator;
import java.util.List;

/**
 * HTTP endpoint for the key value purchase order example.
 *
 * <p>This endpoint demonstrates key key-value entity endpoint patterns:
 *
 * <ul>
 *   <li>validating requests before invoking a flow-internal entity</li>
 *   <li>returning {@code HttpResponses.created(...)} on create</li>
 *   <li>exposing both normal and strongly consistent reads</li>
 *   <li>mapping replication filter commands to HTTP endpoints</li>
 * </ul>
 */
@HttpEndpoint("/purchase-orders")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class PurchaseOrderEndpoint {

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

  public PurchaseOrderEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/{orderId}")
  public OrderResponse getOrder(String orderId) {
    return toApi(
        componentClient
            .forKeyValueEntity(orderId)
            .method(PurchaseOrderEntity::getOrder)
            .invoke());
  }

  @Get("/{orderId}/consistent")
  public OrderResponse getOrderConsistent(String orderId) {
    return toApi(
        componentClient
            .forKeyValueEntity(orderId)
            .method(PurchaseOrderEntity::getOrderConsistent)
            .invoke());
  }

  @Post("/{orderId}")
  public HttpResponse createOrder(String orderId, CreateOrderRequest request) {
    var command =
        new PurchaseOrder.Command.CreateOrder(
            request.cartId(),
            request.lineItems().stream()
                .map(item -> new PurchaseOrder.LineItem(item.productId(), item.name(), item.quantity(), item.readyToShip()))
                .toList());
    var errors = PurchaseOrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return HttpResponses.badRequest(String.join("; ", errors));
    }

    try {
      componentClient.forKeyValueEntity(orderId).method(PurchaseOrderEntity::createOrder).invoke(command);
      return HttpResponses.created(currentResponse(orderId));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Put("/{orderId}/line-items/{productId}/ready-to-ship")
  public HttpResponse lineItemReadyToShip(String orderId, String productId) {
    var command = new PurchaseOrder.Command.LineItemReadyToShip(productId);
    var errors = PurchaseOrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return HttpResponses.badRequest(String.join("; ", errors));
    }

    try {
      componentClient
          .forKeyValueEntity(orderId)
          .method(PurchaseOrderEntity::lineItemReadyToShip)
          .invoke(command);
      return HttpResponses.ok(currentResponse(orderId));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Put("/{orderId}/replication/include")
  public HttpResponse includeRegion(String orderId, RegionRequest request) {
    var command = new PurchaseOrder.Command.IncludeRegion(request.region());
    var errors = PurchaseOrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return HttpResponses.badRequest(String.join("; ", errors));
    }

    try {
      componentClient
          .forKeyValueEntity(orderId)
          .method(PurchaseOrderEntity::includeRegion)
          .invoke(command);
      return HttpResponses.ok(new StatusResponse(orderId, "included-region:" + request.region()));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Put("/{orderId}/replication/exclude")
  public HttpResponse excludeRegion(String orderId, RegionRequest request) {
    var command = new PurchaseOrder.Command.ExcludeRegion(request.region());
    var errors = PurchaseOrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return HttpResponses.badRequest(String.join("; ", errors));
    }

    try {
      componentClient
          .forKeyValueEntity(orderId)
          .method(PurchaseOrderEntity::excludeRegion)
          .invoke(command);
      return HttpResponses.ok(new StatusResponse(orderId, "excluded-region:" + request.region()));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  private OrderResponse currentResponse(String orderId) {
    return toApi(
        componentClient
            .forKeyValueEntity(orderId)
            .method(PurchaseOrderEntity::getOrderConsistent)
            .invoke());
  }

  private static OrderResponse toApi(PurchaseOrder.State state) {
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
