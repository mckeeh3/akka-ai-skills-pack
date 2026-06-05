package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.consumer.Consumer;
import com.example.domain.Order;
import com.example.domain.ShoppingCart;

/**
 * Consumer that reacts to shopping cart checkout events and creates a downstream order entity.
 *
 * <p>This example demonstrates an event-driven flow:
 *
 * <ul>
 *   <li>an edge-facing {@code ShoppingCartEntity} is called from an endpoint</li>
 *   <li>a checkout event is consumed here</li>
 *   <li>the consumer transforms shopping cart state into an {@code Order.Command.CreateOrder}</li>
 *   <li>the downstream {@code OrderEntity} is created with idempotent command handling</li>
 * </ul>
 */
@Component(id = "shopping-cart-checkout-consumer")
@Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
public class ShoppingCartCheckoutConsumer extends Consumer {

  private final ComponentClient componentClient;

  public ShoppingCartCheckoutConsumer(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect onEvent(ShoppingCart.Event event) {
    return switch (event) {
      case ShoppingCart.Event.CheckedOut checkedOut -> onCheckedOut();
      default -> effects().ignore();
    };
  }

  private Effect onCheckedOut() {
    var cartId = messageContext().eventSubject().orElseThrow();
    var cart =
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::getCart)
            .invoke();

    componentClient
        .forEventSourcedEntity(orderIdFromCartId(cartId))
        .method(OrderEntity::createOrder)
        .invoke(new Order.Command.CreateOrder(cartId, toOrderLineItems(cart)));

    return effects().done();
  }

  private static String orderIdFromCartId(String cartId) {
    return cartId;
  }

  private static java.util.List<Order.LineItem> toOrderLineItems(ShoppingCart.State cart) {
    return cart.items().stream()
        .map(item -> new Order.LineItem(item.productId(), item.name(), item.quantity(), false))
        .toList();
  }
}
