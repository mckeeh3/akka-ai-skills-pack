package com.example.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Validation helper for the edge-facing shopping cart example.
 *
 * <p>This class is intentionally separate from {@code ShoppingCart.State} so that AI coding agents
 * have a clear place to put command validation for edge-facing entities that reject invalid input
 * with {@code effects().error(...)}.
 */
public final class ShoppingCartValidator {

  private ShoppingCartValidator() {}

  public static List<String> validate(
      ShoppingCart.State state,
      ShoppingCart.Command.AddItem command) {
    var errors = new ArrayList<String>();

    if (state.checkedOut()) {
      errors.add("Cart is already checked out.");
    }
    if (isBlank(command.productId())) {
      errors.add("productId must not be blank.");
    }
    if (isBlank(command.name())) {
      errors.add("name must not be blank.");
    }
    if (command.quantity() <= 0) {
      errors.add("quantity must be greater than zero.");
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(
      ShoppingCart.State state,
      ShoppingCart.Command.RemoveItem command) {
    var errors = new ArrayList<String>();

    if (state.checkedOut()) {
      errors.add("Cart is already checked out.");
    }
    if (isBlank(command.productId())) {
      errors.add("productId must not be blank.");
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(
      ShoppingCart.State state,
      ShoppingCart.Command.Checkout command) {
    var errors = new ArrayList<String>();

    if (state.items().isEmpty()) {
      errors.add("Cannot checkout an empty cart.");
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(
      ShoppingCart.State state,
      ShoppingCart.Command.Delete command) {
    return List.of();
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
