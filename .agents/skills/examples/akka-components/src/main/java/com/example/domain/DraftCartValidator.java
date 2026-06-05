package com.example.domain;

import java.util.ArrayList;
import java.util.List;

/** Validation helper for the edge-facing key value draft cart example. */
public final class DraftCartValidator {

  private DraftCartValidator() {}

  public static List<String> validate(DraftCart.State state, DraftCart.Command.AddItem command) {
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

  public static List<String> validate(DraftCart.State state, DraftCart.Command.RemoveItem command) {
    var errors = new ArrayList<String>();

    if (state.checkedOut()) {
      errors.add("Cart is already checked out.");
    }
    if (isBlank(command.productId())) {
      errors.add("productId must not be blank.");
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(DraftCart.State state, DraftCart.Command.Checkout command) {
    var errors = new ArrayList<String>();

    if (state.items().isEmpty()) {
      errors.add("Cannot checkout an empty cart.");
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(DraftCart.State state, DraftCart.Command.Delete command) {
    return List.of();
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
