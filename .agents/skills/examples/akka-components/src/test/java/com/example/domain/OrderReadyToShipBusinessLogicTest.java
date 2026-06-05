package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class OrderReadyToShipBusinessLogicTest {

  @Test
  void decideReturnsNoEventsWhenLineItemIsAlreadyReady() {
    var state =
        new Order.State(
            "order-1",
            "cart-1",
            List.of(new Order.LineItem("sku-1", "Akka T-Shirt", 2, true)),
            false,
            true);

    var decision =
        OrderReadyToShipBusinessLogic.decide(
            state,
            new Order.Command.LineItemReadyToShip("sku-1"));

    assertEquals(List.of(), decision.events());
  }

  @Test
  void decideReturnsTwoEventsWhenFinalLineItemBecomesReady() {
    var state =
        new Order.State(
            "order-1",
            "cart-1",
            List.of(
                new Order.LineItem("sku-1", "Akka T-Shirt", 2, true),
                new Order.LineItem("sku-2", "Akka Socks", 1, false)),
            false,
            true);

    var decision =
        OrderReadyToShipBusinessLogic.decide(
            state,
            new Order.Command.LineItemReadyToShip("sku-2"));

    assertEquals(
        List.of(
            new Order.Event.LineItemFlaggedReadyToShip("sku-2"),
            new Order.Event.OrderFlaggedReadyToShip()),
        decision.events());
  }
}
