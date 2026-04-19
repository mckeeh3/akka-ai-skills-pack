package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PurchaseOrderReadyToShipBusinessLogicTest {

  @Test
  void decideReturnsNoStateChangeWhenLineItemIsAlreadyReady() {
    var state =
        new PurchaseOrder.State(
            "order-1",
            "cart-1",
            List.of(new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, true)),
            false,
            true);

    var decision =
        PurchaseOrderReadyToShipBusinessLogic.decide(
            state,
            new PurchaseOrder.Command.LineItemReadyToShip("sku-1"));

    assertEquals(Optional.empty(), decision.updatedState());
  }

  @Test
  void decideReturnsUpdatedStateWhenFinalLineItemBecomesReady() {
    var state =
        new PurchaseOrder.State(
            "order-1",
            "cart-1",
            List.of(
                new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, true),
                new PurchaseOrder.LineItem("sku-2", "Akka Socks", 1, false)),
            false,
            true);

    var decision =
        PurchaseOrderReadyToShipBusinessLogic.decide(
            state,
            new PurchaseOrder.Command.LineItemReadyToShip("sku-2"));

    assertEquals(
        Optional.of(
            new PurchaseOrder.State(
                "order-1",
                "cart-1",
                List.of(
                    new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, true),
                    new PurchaseOrder.LineItem("sku-2", "Akka Socks", 1, true)),
                true,
                true)),
        decision.updatedState());
  }
}
