package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.DraftCartEntity;
import com.example.domain.DraftCart;
import java.util.List;

/**
 * HTTP endpoint for the edge-facing key value draft cart example.
 *
 * <p>The endpoint performs request-to-command mapping but leaves business validation to the entity.
 * Entity rejections are translated into HTTP 400 responses.
 */
@HttpEndpoint("/draft-carts")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class DraftCartEndpoint {

  public record AddItemRequest(String productId, String name, int quantity) {}

  public record CartItemResponse(String productId, String name, int quantity) {}

  public record CartResponse(String cartId, List<CartItemResponse> items, boolean checkedOut) {}

  public record CartNotificationResponse(String type, String productId, String name, int quantity) {}

  public record StatusResponse(String status) {}

  private final ComponentClient componentClient;

  public DraftCartEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/{cartId}")
  public CartResponse getCart(String cartId) {
    var cart = componentClient.forKeyValueEntity(cartId).method(DraftCartEntity::getCart).invoke();
    return toApi(cart);
  }

  @Post("/{cartId}/items")
  public HttpResponse addItem(String cartId, AddItemRequest request) {
    try {
      var cart =
          componentClient
              .forKeyValueEntity(cartId)
              .method(DraftCartEntity::addItem)
              .invoke(new DraftCart.Command.AddItem(request.productId(), request.name(), request.quantity()));
      return HttpResponses.ok(toApi(cart));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Delete("/{cartId}/items/{productId}")
  public HttpResponse removeItem(String cartId, String productId) {
    try {
      var cart =
          componentClient
              .forKeyValueEntity(cartId)
              .method(DraftCartEntity::removeItem)
              .invoke(new DraftCart.Command.RemoveItem(productId));
      return HttpResponses.ok(toApi(cart));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Post("/{cartId}/checkout")
  public HttpResponse checkout(String cartId) {
    try {
      var cart =
          componentClient
              .forKeyValueEntity(cartId)
              .method(DraftCartEntity::checkout)
              .invoke(new DraftCart.Command.Checkout());
      return HttpResponses.ok(toApi(cart));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Delete("/{cartId}")
  public HttpResponse deleteCart(String cartId) {
    try {
      var status =
          componentClient
              .forKeyValueEntity(cartId)
              .method(DraftCartEntity::delete)
              .invoke(new DraftCart.Command.Delete());
      return HttpResponses.ok(new StatusResponse(status));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{cartId}/notifications")
  public HttpResponse notifications(String cartId) {
    var source =
        componentClient
            .forKeyValueEntity(cartId)
            .notificationStream(DraftCartEntity::notifications)
            .source()
            .map(DraftCartEndpoint::toNotification);
    return HttpResponses.serverSentEvents(source);
  }

  private static CartResponse toApi(DraftCart.State cart) {
    return new CartResponse(
        cart.cartId(),
        cart.items().stream()
            .map(item -> new CartItemResponse(item.productId(), item.name(), item.quantity()))
            .toList(),
        cart.checkedOut());
  }

  private static CartNotificationResponse toNotification(DraftCart.Notification notification) {
    return switch (notification) {
      case DraftCart.Notification.ItemAdded added ->
          new CartNotificationResponse(
              "item-added", added.productId(), added.name(), added.quantity());
      case DraftCart.Notification.ItemRemoved removed ->
          new CartNotificationResponse("item-removed", removed.productId(), "", 0);
      case DraftCart.Notification.CheckedOut ignored ->
          new CartNotificationResponse("checked-out", "", "", 0);
      case DraftCart.Notification.Deleted ignored ->
          new CartNotificationResponse("deleted", "", "", 0);
    };
  }
}
