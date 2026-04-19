package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PurchaseOrderCommandHandlerTest {

  @Test
  void createOrderReturnsUpdatedState() {
    var state = PurchaseOrder.State.empty("order-1");
    var command =
        new PurchaseOrder.Command.CreateOrder(
            "cart-1",
            List.of(new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, false)));

    var updatedState = PurchaseOrderCommandHandler.onCommand(state, command);

    assertEquals(
        Optional.of(
            new PurchaseOrder.State(
                "order-1",
                "cart-1",
                List.of(new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, false)),
                false,
                true)),
        updatedState);
  }

  @Test
  void lineItemReadyToShipReturnsEmptyWhenItemIsMissing() {
    var state =
        new PurchaseOrder.State(
            "order-1",
            "cart-1",
            List.of(new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, false)),
            false,
            true);

    var updatedState =
        PurchaseOrderCommandHandler.onCommand(
            state,
            new PurchaseOrder.Command.LineItemReadyToShip("missing-sku"));

    assertEquals(Optional.empty(), updatedState);
  }

  @Test
  void lineItemReadyToShipReturnsUpdatedStateWhenOrderBecomesReady() {
    var state =
        new PurchaseOrder.State(
            "order-1",
            "cart-1",
            List.of(
                new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, true),
                new PurchaseOrder.LineItem("sku-2", "Akka Socks", 1, false)),
            false,
            true);

    var updatedState =
        PurchaseOrderCommandHandler.onCommand(
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
        updatedState);
  }
}
