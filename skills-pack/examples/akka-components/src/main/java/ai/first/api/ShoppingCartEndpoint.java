package ai.first.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import ai.first.application.CartInspectorAgent;
import ai.first.application.ShoppingCartEntity;
import ai.first.domain.ShoppingCart;
import java.util.List;

/**
 * HTTP endpoint for the edge-facing shopping cart example.
 *
 * <p>The endpoint performs request-to-command mapping but leaves business validation to the entity.
 * Entity rejections are translated into HTTP 400 responses.
 */
@HttpEndpoint("/carts")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ShoppingCartEndpoint {

  public record AddItemRequest(String productId, String name, int quantity) {}

  public record CartItemResponse(String productId, String name, int quantity) {}

  public record CartResponse(String cartId, List<CartItemResponse> items, boolean checkedOut) {}

  public record CartSummaryItemResponse(String name, int quantity) {}

  public record CartSummaryResponse(
      String capabilityId,
      String cartId,
      List<CartSummaryItemResponse> items,
      int totalQuantity,
      boolean checkedOut) {}

  public record CartNotificationResponse(String type, String productId) {}

  public record StatusResponse(String status) {}

  private final ComponentClient componentClient;

  public ShoppingCartEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/{cartId}")
  public CartResponse getCart(String cartId) {
    var cart = componentClient.forEventSourcedEntity(cartId).method(ShoppingCartEntity::getCart).invoke();
    return toApi(cart);
  }

  /**
   * Browser/API exposure of the same read-only capability used by {@link CartInspectorAgent}.
   *
   * <p>Capability: {@code cart.inspect-summary}. The browser action and agent component tool both
   * call {@link ShoppingCartEntity#inspectCartSummary()} so the curated fields, read-only behavior,
   * and future AuthContext/scope/audit checks stay in one backend capability surface instead of
   * drifting across UI/API/tool implementations.
   */
  @Get("/{cartId}/summary")
  public CartSummaryResponse inspectSummary(String cartId) {
    var summary =
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::inspectCartSummary)
            .invoke();
    return toApi(summary);
  }

  @Post("/{cartId}/items")
  public HttpResponse addItem(String cartId, AddItemRequest request) {
    try {
      var cart =
          componentClient
              .forEventSourcedEntity(cartId)
              .method(ShoppingCartEntity::addItem)
              .invoke(new ShoppingCart.Command.AddItem(request.productId(), request.name(), request.quantity()));
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
              .forEventSourcedEntity(cartId)
              .method(ShoppingCartEntity::removeItem)
              .invoke(new ShoppingCart.Command.RemoveItem(productId));
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
              .forEventSourcedEntity(cartId)
              .method(ShoppingCartEntity::checkout)
              .invoke(new ShoppingCart.Command.Checkout());
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
              .forEventSourcedEntity(cartId)
              .method(ShoppingCartEntity::delete)
              .invoke(new ShoppingCart.Command.Delete());
      return HttpResponses.ok(new StatusResponse(status));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{cartId}/notifications")
  public HttpResponse notifications(String cartId) {
    var source =
        componentClient
            .forEventSourcedEntity(cartId)
            .notificationStream(ShoppingCartEntity::notifications)
            .source()
            .map(ShoppingCartEndpoint::toNotification);
    return HttpResponses.serverSentEvents(source);
  }

  private static CartResponse toApi(ShoppingCart.State cart) {
    return new CartResponse(
        cart.cartId(),
        cart.items().stream()
            .map(item -> new CartItemResponse(item.productId(), item.name(), item.quantity()))
            .toList(),
        cart.checkedOut());
  }

  private static CartSummaryResponse toApi(ShoppingCartEntity.CartSummary summary) {
    return new CartSummaryResponse(
        "cart.inspect-summary",
        summary.cartId(),
        summary.items().stream()
            .map(item -> new CartSummaryItemResponse(item.name(), item.quantity()))
            .toList(),
        summary.totalQuantity(),
        summary.checkedOut());
  }

  private static CartNotificationResponse toNotification(ShoppingCart.Event event) {
    return switch (event) {
      case ShoppingCart.Event.ItemAdded added ->
          new CartNotificationResponse("item-added", added.item().productId());
      case ShoppingCart.Event.ItemRemoved removed ->
          new CartNotificationResponse("item-removed", removed.productId());
      case ShoppingCart.Event.CheckedOut ignored ->
          new CartNotificationResponse("checked-out", "");
      case ShoppingCart.Event.Deleted ignored ->
          new CartNotificationResponse("deleted", "");
    };
  }
}
