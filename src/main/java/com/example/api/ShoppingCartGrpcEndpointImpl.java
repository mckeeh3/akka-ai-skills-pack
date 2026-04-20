package com.example.api;

import akka.NotUsed;
import akka.grpc.GrpcServiceException;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.GrpcEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.stream.javadsl.Source;
import com.example.api.grpc.AddItemRequest;
import com.example.api.grpc.Cart;
import com.example.api.grpc.CartItem;
import com.example.api.grpc.CartStatusResponse;
import com.example.api.grpc.CartSummary;
import com.example.api.grpc.CheckedOutCartsRequest;
import com.example.api.grpc.CheckoutCartRequest;
import com.example.api.grpc.DeleteCartRequest;
import com.example.api.grpc.GetCartRequest;
import com.example.api.grpc.ShoppingCartGrpcEndpoint;
import com.example.application.ShoppingCartEntity;
import com.example.application.ShoppingCartsByCheckedOutView;
import com.example.domain.ShoppingCart;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import java.time.Instant;

/**
 * Focused gRPC endpoint example for protobuf request/response mapping, component calls, error
 * translation, and streamed responses.
 */
@GrpcEndpoint
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ShoppingCartGrpcEndpointImpl implements ShoppingCartGrpcEndpoint {

  private final ComponentClient componentClient;

  public ShoppingCartGrpcEndpointImpl(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Override
  public Cart getCart(GetCartRequest in) {
    requireCartId(in.getCartId());

    var cart = componentClient.forEventSourcedEntity(in.getCartId()).method(ShoppingCartEntity::getCart).invoke();
    return toApi(cart);
  }

  @Override
  public Cart addItem(AddItemRequest in) {
    requireCartId(in.getCartId());
    requireText(in.getProductId(), "product_id");
    requireText(in.getName(), "name");
    if (in.getQuantity() <= 0) {
      throw invalidArgument("quantity must be greater than zero");
    }

    try {
      var cart =
          componentClient
              .forEventSourcedEntity(in.getCartId())
              .method(ShoppingCartEntity::addItem)
              .invoke(new ShoppingCart.Command.AddItem(in.getProductId(), in.getName(), in.getQuantity()));
      return toApi(cart);
    } catch (CommandException error) {
      throw invalidArgument(error.getMessage());
    }
  }

  @Override
  public Cart checkoutCart(CheckoutCartRequest in) {
    requireCartId(in.getCartId());

    try {
      var cart =
          componentClient
              .forEventSourcedEntity(in.getCartId())
              .method(ShoppingCartEntity::checkout)
              .invoke(new ShoppingCart.Command.Checkout());
      return toApi(cart);
    } catch (CommandException error) {
      throw invalidArgument(error.getMessage());
    }
  }

  @Override
  public CartStatusResponse deleteCart(DeleteCartRequest in) {
    requireCartId(in.getCartId());

    try {
      var status =
          componentClient
              .forEventSourcedEntity(in.getCartId())
              .method(ShoppingCartEntity::delete)
              .invoke(new ShoppingCart.Command.Delete());

      return CartStatusResponse.newBuilder()
          .setStatus(status)
          .setDetail(StringValue.of("cart deleted or already empty"))
          .build();
    } catch (CommandException error) {
      throw invalidArgument(error.getMessage());
    }
  }

  @Override
  public Source<CartSummary, NotUsed> streamCheckedOutCarts(CheckedOutCartsRequest in) {
    return componentClient
        .forView()
        .stream(ShoppingCartsByCheckedOutView::streamCarts)
        .source(new ShoppingCartsByCheckedOutView.FindByCheckedOut(in.getCheckedOut()))
        .map(ShoppingCartGrpcEndpointImpl::toSummary);
  }

  private static Cart toApi(ShoppingCart.State cart) {
    var builder =
        Cart.newBuilder()
            .setCartId(cart.cartId())
            .setCheckedOut(cart.checkedOut())
            .setObservedAt(nowTimestamp())
            .addAllItems(cart.items().stream().map(ShoppingCartGrpcEndpointImpl::toApi).toList());

    var stateLabel = cart.checkedOut() ? "checked-out" : "open";
    builder.setStateLabel(StringValue.of(stateLabel));
    return builder.build();
  }

  private static CartItem toApi(ShoppingCart.LineItem item) {
    return CartItem.newBuilder()
        .setProductId(item.productId())
        .setName(item.name())
        .setQuantity(item.quantity())
        .build();
  }

  private static CartSummary toSummary(ShoppingCart.State cart) {
    return CartSummary.newBuilder()
        .setCartId(cart.cartId())
        .setItemCount(cart.items().size())
        .setCheckedOut(cart.checkedOut())
        .setObservedAt(nowTimestamp())
        .build();
  }

  private static void requireCartId(String cartId) {
    requireText(cartId, "cart_id");
  }

  private static void requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw invalidArgument(field + " must not be empty");
    }
  }

  private static GrpcServiceException invalidArgument(String description) {
    return new GrpcServiceException(Status.INVALID_ARGUMENT.augmentDescription(description));
  }

  private static Timestamp nowTimestamp() {
    var now = Instant.now();
    return Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build();
  }
}
