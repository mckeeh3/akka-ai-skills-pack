package com.example.domain;

import java.util.ArrayList;
import java.util.List;

/** Validation helper for the order entity example. */
public final class OrderValidator {

  private OrderValidator() {}

  public static List<String> validate(Order.Command.CreateOrder command) {
    var errors = new ArrayList<String>();

    if (isBlank(command.cartId())) {
      errors.add("cartId must not be blank");
    }
    if (command.lineItems() == null || command.lineItems().isEmpty()) {
      errors.add("lineItems must not be empty");
      return List.copyOf(errors);
    }

    for (var item : command.lineItems()) {
      if (isBlank(item.productId())) {
        errors.add("lineItem productId must not be blank");
      }
      if (isBlank(item.name())) {
        errors.add("lineItem name must not be blank");
      }
      if (item.quantity() <= 0) {
        errors.add("lineItem quantity must be greater than zero");
      }
    }

    return List.copyOf(errors);
  }

  public static List<String> validate(Order.Command.LineItemReadyToShip command) {
    if (isBlank(command.productId())) {
      return List.of("productId must not be blank");
    }

    return List.of();
  }

  public static List<String> validate(Order.Command.IncludeRegion command) {
    if (isBlank(command.region())) {
      return List.of("region must not be blank");
    }

    return List.of();
  }

  public static List<String> validate(Order.Command.ExcludeRegion command) {
    if (isBlank(command.region())) {
      return List.of("region must not be blank");
    }

    return List.of();
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
