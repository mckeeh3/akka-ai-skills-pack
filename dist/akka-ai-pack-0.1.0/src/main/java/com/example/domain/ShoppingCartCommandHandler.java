package com.example.domain;

import java.util.Optional;

/**
 * Command-to-event mapping for {@code ShoppingCartEntity}.
 *
 * <p>This class assumes validation has already happened. It can still inspect the current state to
 * decide that some commands should be treated as no-ops, for example duplicate checkout requests or
 * attempts to remove an item that is not present.
 */
public final class ShoppingCartCommandHandler {

  private ShoppingCartCommandHandler() {}

  public static ShoppingCart.Event onCommand(
      ShoppingCart.State state,
      ShoppingCart.Command.AddItem command) {
    return new ShoppingCart.Event.ItemAdded(
        new ShoppingCart.LineItem(command.productId(), command.name(), command.quantity()));
  }

  public static Optional<ShoppingCart.Event> onCommand(
      ShoppingCart.State state,
      ShoppingCart.Command.RemoveItem command) {
    if (state.findItem(command.productId()).isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new ShoppingCart.Event.ItemRemoved(command.productId()));
  }

  public static Optional<ShoppingCart.Event> onCommand(
      ShoppingCart.State state,
      ShoppingCart.Command.Checkout command) {
    if (state.checkedOut()) {
      return Optional.empty();
    }

    return Optional.of(new ShoppingCart.Event.CheckedOut());
  }

  public static Optional<ShoppingCart.Event> onCommand(
      ShoppingCart.State state,
      ShoppingCart.Command.Delete command) {
    if (state.items().isEmpty() && !state.checkedOut()) {
      return Optional.empty();
    }

    return Optional.of(new ShoppingCart.Event.Deleted());
  }
}
