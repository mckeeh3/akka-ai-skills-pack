package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.KeyValueEntityTestKit;
import com.example.domain.PurchaseOrder;
import java.util.List;
import org.junit.jupiter.api.Test;

class PurchaseOrderEntityTest {

  private KeyValueEntityTestKit<PurchaseOrder.State, PurchaseOrderEntity> newTestKit(String entityId) {
    return KeyValueEntityTestKit.of(entityId, PurchaseOrderEntity::new);
  }

  @Test
  void createOrderUpdatesState() {
    var testKit = newTestKit("order-1");

    var result =
        testKit
            .method(PurchaseOrderEntity::createOrder)
            .invoke(
                new PurchaseOrder.Command.CreateOrder(
                    "cart-1",
                    List.of(new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, false))));

    assertEquals(Done.getInstance(), result.getReply());
    assertTrue(result.stateWasUpdated());
    assertEquals(
        new PurchaseOrder.State(
            "order-1",
            "cart-1",
            List.of(new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, false)),
            false,
            true),
        testKit.getState());
  }

  @Test
  void lineItemReadyToShipCanUpdateOrderAndReadyFlag() {
    var testKit = newTestKit("order-1");

    testKit
        .method(PurchaseOrderEntity::createOrder)
        .invoke(
            new PurchaseOrder.Command.CreateOrder(
                "cart-1",
                List.of(
                    new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, true),
                    new PurchaseOrder.LineItem("sku-2", "Akka Socks", 1, false))));

    var result =
        testKit
            .method(PurchaseOrderEntity::lineItemReadyToShip)
            .invoke(new PurchaseOrder.Command.LineItemReadyToShip("sku-2"));

    assertEquals(Done.getInstance(), result.getReply());
    assertTrue(result.stateWasUpdated());
    assertTrue(testKit.getState().readyToShip());
    assertTrue(testKit.getState().allLineItemsReadyToShip());
  }

  @Test
  void lineItemReadyToShipForMissingItemIsANoOp() {
    var testKit = newTestKit("order-1");

    testKit
        .method(PurchaseOrderEntity::createOrder)
        .invoke(
            new PurchaseOrder.Command.CreateOrder(
                "cart-1",
                List.of(new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, false))));

    var result =
        testKit
            .method(PurchaseOrderEntity::lineItemReadyToShip)
            .invoke(new PurchaseOrder.Command.LineItemReadyToShip("missing-sku"));

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.stateWasUpdated());
    assertFalse(testKit.getState().readyToShip());
  }

  @Test
  void getOrderConsistentReturnsCurrentState() {
    var testKit = newTestKit("order-1");

    testKit
        .method(PurchaseOrderEntity::createOrder)
        .invoke(
            new PurchaseOrder.Command.CreateOrder(
                "cart-1",
                List.of(new PurchaseOrder.LineItem("sku-1", "Akka T-Shirt", 2, false))));

    var result = testKit.method(PurchaseOrderEntity::getOrderConsistent).invoke();

    assertTrue(result.getReply().exists());
    assertEquals("cart-1", result.getReply().cartId());
  }

  @Test
  void includeRegionRepliesDoneWithoutUpdatingState() {
    var testKit = newTestKit("order-1");

    var result =
        testKit
            .method(PurchaseOrderEntity::includeRegion)
            .invoke(new PurchaseOrder.Command.IncludeRegion("aws-us-east-2"));

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.stateWasUpdated());
  }
}
