package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.EventSourcedTestKit;
import com.example.domain.Order;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderEntityTest {

  private EventSourcedTestKit<Order.State, Order.Event, OrderEntity> newTestKit(String entityId) {
    return EventSourcedTestKit.of(entityId, OrderEntity::new);
  }

  @Test
  void createOrderPersistsCreatedEvent() {
    var testKit = newTestKit("order-1");

    var result =
        testKit
            .method(OrderEntity::createOrder)
            .invoke(
                new Order.Command.CreateOrder(
                    "cart-1",
                    List.of(new Order.LineItem("sku-1", "Akka T-Shirt", 2, false))));

    assertEquals(Done.getInstance(), result.getReply());
    assertEquals(
        new Order.Event.OrderCreated(
            "order-1",
            "cart-1",
            List.of(new Order.LineItem("sku-1", "Akka T-Shirt", 2, false))),
        result.getNextEventOfType(Order.Event.OrderCreated.class));
  }

  @Test
  void lineItemReadyToShipCanPersistTwoEvents() {
    var testKit = newTestKit("order-1");

    testKit
        .method(OrderEntity::createOrder)
        .invoke(
            new Order.Command.CreateOrder(
                "cart-1",
                List.of(
                    new Order.LineItem("sku-1", "Akka T-Shirt", 2, true),
                    new Order.LineItem("sku-2", "Akka Socks", 1, false))));

    var result =
        testKit
            .method(OrderEntity::lineItemReadyToShip)
            .invoke(new Order.Command.LineItemReadyToShip("sku-2"));

    assertEquals(Done.getInstance(), result.getReply());
    assertEquals(
        new Order.Event.LineItemFlaggedReadyToShip("sku-2"),
        result.getNextEventOfType(Order.Event.LineItemFlaggedReadyToShip.class));
    assertEquals(
        new Order.Event.OrderFlaggedReadyToShip(),
        result.getNextEventOfType(Order.Event.OrderFlaggedReadyToShip.class));

    assertTrue(testKit.getState().readyToShip());
    assertTrue(testKit.getState().allLineItemsReadyToShip());
  }

  @Test
  void lineItemReadyToShipForMissingItemIsANoOp() {
    var testKit = newTestKit("order-1");

    testKit
        .method(OrderEntity::createOrder)
        .invoke(
            new Order.Command.CreateOrder(
                "cart-1",
                List.of(new Order.LineItem("sku-1", "Akka T-Shirt", 2, false))));

    var result =
        testKit
            .method(OrderEntity::lineItemReadyToShip)
            .invoke(new Order.Command.LineItemReadyToShip("missing-sku"));

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.didPersistEvents());
    assertFalse(testKit.getState().readyToShip());
  }

  @Test
  void getOrderConsistentReturnsCurrentState() {
    var testKit = newTestKit("order-1");

    testKit
        .method(OrderEntity::createOrder)
        .invoke(
            new Order.Command.CreateOrder(
                "cart-1",
                List.of(new Order.LineItem("sku-1", "Akka T-Shirt", 2, false))));

    var result = testKit.method(OrderEntity::getOrderConsistent).invoke();

    assertTrue(result.getReply().exists());
    assertEquals("cart-1", result.getReply().cartId());
  }

  @Test
  void includeRegionRepliesDoneWithoutPersistingEvents() {
    var testKit = newTestKit("order-1");

    var result =
        testKit
            .method(OrderEntity::includeRegion)
            .invoke(new Order.Command.IncludeRegion("aws-us-east-2"));

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.didPersistEvents());
  }
}
