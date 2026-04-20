package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ShoppingCartValidatorTest {

  @Test
  void validateAddItemCommand() {
    var errors =
        ShoppingCartValidator.validate(
            ShoppingCart.State.empty("cart-1"),
            new ShoppingCart.Command.AddItem("", "", 0));

    assertEquals(
        List.of(
            "productId must not be blank.",
            "name must not be blank.",
            "quantity must be greater than zero."),
        errors);
  }

  @Test
  void validateRemoveItemCommand() {
    var errors =
        ShoppingCartValidator.validate(
            ShoppingCart.State.empty("cart-1"),
            new ShoppingCart.Command.RemoveItem(""));

    assertEquals(List.of("productId must not be blank."), errors);
  }

  @Test
  void validateCheckoutCommand() {
    var errors =
        ShoppingCartValidator.validate(
            ShoppingCart.State.empty("cart-1"),
            new ShoppingCart.Command.Checkout());

    assertEquals(List.of("Cannot checkout an empty cart."), errors);
  }
}
