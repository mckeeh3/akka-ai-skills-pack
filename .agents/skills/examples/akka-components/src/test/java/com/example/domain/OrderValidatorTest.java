package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class OrderValidatorTest {

  @Test
  void validateCreateOrderCommand() {
    var errors =
        OrderValidator.validate(
            new Order.Command.CreateOrder(
                "",
                List.of(new Order.LineItem("", "", 0, false))));

    assertEquals(
        List.of(
            "cartId must not be blank",
            "lineItem productId must not be blank",
            "lineItem name must not be blank",
            "lineItem quantity must be greater than zero"),
        errors);
  }

  @Test
  void validateLineItemReadyToShipCommand() {
    var errors = OrderValidator.validate(new Order.Command.LineItemReadyToShip(" "));

    assertEquals(List.of("productId must not be blank"), errors);
  }

  @Test
  void validateIncludeRegionCommand() {
    var errors = OrderValidator.validate(new Order.Command.IncludeRegion(" "));

    assertEquals(List.of("region must not be blank"), errors);
  }
}
