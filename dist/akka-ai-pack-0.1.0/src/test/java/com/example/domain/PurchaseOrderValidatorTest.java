package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PurchaseOrderValidatorTest {

  @Test
  void validateCreateOrderCommand() {
    var errors =
        PurchaseOrderValidator.validate(
            new PurchaseOrder.Command.CreateOrder(
                "",
                List.of(new PurchaseOrder.LineItem("", "", 0, false))));

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
    var errors = PurchaseOrderValidator.validate(new PurchaseOrder.Command.LineItemReadyToShip(" "));

    assertEquals(List.of("productId must not be blank"), errors);
  }

  @Test
  void validateIncludeRegionCommand() {
    var errors = PurchaseOrderValidator.validate(new PurchaseOrder.Command.IncludeRegion(" "));

    assertEquals(List.of("region must not be blank"), errors);
  }
}
