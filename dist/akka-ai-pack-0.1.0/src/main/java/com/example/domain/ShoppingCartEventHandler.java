package com.example.domain;

/** Pure replay-safe event application for the shopping cart example. */
public final class ShoppingCartEventHandler {

  private ShoppingCartEventHandler() {}

  public static ShoppingCart.State apply(ShoppingCart.State state, ShoppingCart.Event event) {
    return switch (event) {
      case ShoppingCart.Event.ItemAdded added -> state.addItem(added.item());
      case ShoppingCart.Event.ItemRemoved removed -> state.removeItem(removed.productId());
      case ShoppingCart.Event.CheckedOut ignored -> state.checkout();
      case ShoppingCart.Event.Deleted ignored -> ShoppingCart.State.empty(state.cartId());
    };
  }
}
