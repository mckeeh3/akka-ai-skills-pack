package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class OrderCommandHandlerTest {

  @Test
  void createOrderReturnsCreatedEvent() {
    var state = Order.State.empty("order-1");
    var command =
        new Order.Command.CreateOrder(
            "cart-1",
            List.of(new Order.LineItem("sku-1", "Akka T-Shirt", 2, false)));

    var events = OrderCommandHandler.onCommand(state, command);

    assertEquals(
        List.of(
            new Order.Event.OrderCreated(
                "order-1",
                "cart-1",
                List.of(new Order.LineItem("sku-1", "Akka T-Shirt", 2, false)))),
        events);
  }

  @Test
  void lineItemReadyToShipReturnsNoEventsWhenItemIsMissing() {
    var state =
        new Order.State(
            "order-1",
            "cart-1",
            List.of(new Order.LineItem("sku-1", "Akka T-Shirt", 2, false)),
            false,
            true);

    var events =
        OrderCommandHandler.onCommand(state, new Order.Command.LineItemReadyToShip("missing-sku"));

    assertEquals(List.of(), events);
  }

  @Test
  void lineItemReadyToShipReturnsOneEventWhenOtherItemsRemain() {
    var state =
        new Order.State(
            "order-1",
            "cart-1",
            List.of(
                new Order.LineItem("sku-1", "Akka T-Shirt", 2, false),
                new Order.LineItem("sku-2", "Akka Socks", 1, false)),
            false,
            true);

    var events =
        OrderCommandHandler.onCommand(state, new Order.Command.LineItemReadyToShip("sku-1"));

    assertEquals(
        List.of(new Order.Event.LineItemFlaggedReadyToShip("sku-1")),
        events);
  }

  @Test
  void lineItemReadyToShipReturnsTwoEventsWhenOrderBecomesReady() {
    var state =
        new Order.State(
            "order-1",
            "cart-1",
            List.of(
                new Order.LineItem("sku-1", "Akka T-Shirt", 2, true),
                new Order.LineItem("sku-2", "Akka Socks", 1, false)),
            false,
            true);

    var events =
        OrderCommandHandler.onCommand(state, new Order.Command.LineItemReadyToShip("sku-2"));

    assertEquals(
        List.of(
            new Order.Event.LineItemFlaggedReadyToShip("sku-2"),
            new Order.Event.OrderFlaggedReadyToShip()),
        events);
  }
}
