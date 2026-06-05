package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.PurchaseOrder;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class DraftCartCheckoutConsumerIntegrationTest extends TestKitSupport {

  record AddItemRequest(String productId, String name, int quantity) {}

  @Test
  void checkoutCreatesPurchaseOrderThroughConsumer() {
    var cartId = "draft-cart-consumer-1";

    var order = createCheckedOutCartAndAwaitOrder(cartId);

    assertTrue(order.exists());
    assertEquals(cartId, order.orderId());
    assertEquals(cartId, order.cartId());
    assertEquals(2, order.lineItems().size());
    assertTrue(order.lineItems().stream().noneMatch(PurchaseOrder.LineItem::readyToShip));
    assertFalse(order.readyToShip());
  }

  @Test
  void lineItemReadyToShipDemonstratesSingleUpdateBehaviorEndToEnd() {
    var orderId = "draft-cart-consumer-2";

    createCheckedOutCartAndAwaitOrder(orderId);

    await(
        componentClient
            .forKeyValueEntity(orderId)
            .method(PurchaseOrderEntity::lineItemReadyToShip)
            .invokeAsync(new PurchaseOrder.Command.LineItemReadyToShip("sku-1")));

    var afterFirstItem =
        await(
            componentClient
                .forKeyValueEntity(orderId)
                .method(PurchaseOrderEntity::getOrder)
                .invokeAsync());

    assertFalse(afterFirstItem.readyToShip());
    assertEquals(1, afterFirstItem.lineItems().stream().filter(PurchaseOrder.LineItem::readyToShip).count());

    await(
        componentClient
            .forKeyValueEntity(orderId)
            .method(PurchaseOrderEntity::lineItemReadyToShip)
            .invokeAsync(new PurchaseOrder.Command.LineItemReadyToShip("sku-2")));

    var afterSecondItem =
        await(
            componentClient
                .forKeyValueEntity(orderId)
                .method(PurchaseOrderEntity::getOrder)
                .invokeAsync());

    assertTrue(afterSecondItem.readyToShip());
    assertEquals(2, afterSecondItem.lineItems().stream().filter(PurchaseOrder.LineItem::readyToShip).count());
  }

  private PurchaseOrder.State createCheckedOutCartAndAwaitOrder(String cartId) {
    await(
        httpClient
            .POST("/draft-carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("sku-1", "Akka T-Shirt", 2))
            .responseBodyAs(String.class)
            .invokeAsync());

    await(
        httpClient
            .POST("/draft-carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("sku-2", "Akka Socks", 1))
            .responseBodyAs(String.class)
            .invokeAsync());

    await(
        httpClient
            .POST("/draft-carts/" + cartId + "/checkout")
            .responseBodyAs(String.class)
            .invokeAsync());

    return awaitOrderCreated(cartId, Duration.ofSeconds(10));
  }

  private PurchaseOrder.State awaitOrderCreated(String orderId, Duration timeout) {
    var deadline = System.nanoTime() + timeout.toNanos();

    while (System.nanoTime() < deadline) {
      var state =
          await(
              componentClient
                  .forKeyValueEntity(orderId)
                  .method(PurchaseOrderEntity::getOrder)
                  .invokeAsync());

      if (state.exists()) {
        return state;
      }

      sleep(100);
    }

    return await(
        componentClient
            .forKeyValueEntity(orderId)
            .method(PurchaseOrderEntity::getOrder)
            .invokeAsync());
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException error) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(error);
    }
  }
}
