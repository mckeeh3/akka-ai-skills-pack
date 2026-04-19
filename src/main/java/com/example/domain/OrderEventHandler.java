package com.example.domain;

/** Pure replay-safe event application for the order entity example. */
public final class OrderEventHandler {

  private OrderEventHandler() {}

  public static Order.State apply(Order.State state, Order.Event event) {
    return switch (event) {
      case Order.Event.OrderCreated created ->
          state.create(created.cartId(), created.lineItems());
      case Order.Event.LineItemFlaggedReadyToShip flagged ->
          state.flagLineItemReadyToShip(flagged.productId());
      case Order.Event.OrderFlaggedReadyToShip ignored -> state.flagOrderReadyToShip();
    };
  }
}
