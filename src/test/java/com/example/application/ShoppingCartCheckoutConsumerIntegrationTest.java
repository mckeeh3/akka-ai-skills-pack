package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.Order;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ShoppingCartCheckoutConsumerIntegrationTest extends TestKitSupport {

  record AddItemRequest(String productId, String name, int quantity) {}

  @Test
  void checkoutCreatesOrderThroughConsumer() {
    var cartId = "cart-consumer-1";

    var order = createCheckedOutCartAndAwaitOrder(cartId);

    assertTrue(order.exists());
    assertEquals(cartId, order.orderId());
    assertEquals(cartId, order.cartId());
    assertEquals(2, order.lineItems().size());
    assertTrue(order.lineItems().stream().noneMatch(Order.LineItem::readyToShip));
    assertFalse(order.readyToShip());
  }

  @Test
  void lineItemReadyToShipDemonstratesMultiEventBehaviorEndToEnd() {
    var orderId = "cart-consumer-2";

    createCheckedOutCartAndAwaitOrder(orderId);

    await(
        componentClient
            .forEventSourcedEntity(orderId)
            .method(OrderEntity::lineItemReadyToShip)
            .invokeAsync(new Order.Command.LineItemReadyToShip("sku-1")));

    var afterFirstItem =
        await(
            componentClient
                .forEventSourcedEntity(orderId)
                .method(OrderEntity::getOrder)
                .invokeAsync());

    assertFalse(afterFirstItem.readyToShip());
    assertEquals(1, afterFirstItem.lineItems().stream().filter(Order.LineItem::readyToShip).count());

    await(
        componentClient
            .forEventSourcedEntity(orderId)
            .method(OrderEntity::lineItemReadyToShip)
            .invokeAsync(new Order.Command.LineItemReadyToShip("sku-2")));

    var afterSecondItem =
        await(
            componentClient
                .forEventSourcedEntity(orderId)
                .method(OrderEntity::getOrder)
                .invokeAsync());

    assertTrue(afterSecondItem.readyToShip());
    assertEquals(2, afterSecondItem.lineItems().stream().filter(Order.LineItem::readyToShip).count());
  }

  private Order.State createCheckedOutCartAndAwaitOrder(String cartId) {
    await(
        httpClient
            .POST("/carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("sku-1", "Akka T-Shirt", 2))
            .responseBodyAs(String.class)
            .invokeAsync());

    await(
        httpClient
            .POST("/carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("sku-2", "Akka Socks", 1))
            .responseBodyAs(String.class)
            .invokeAsync());

    await(
        httpClient
            .POST("/carts/" + cartId + "/checkout")
            .responseBodyAs(String.class)
            .invokeAsync());

    return awaitOrderCreated(cartId, Duration.ofSeconds(10));
  }

  private Order.State awaitOrderCreated(String orderId, Duration timeout) {
    var deadline = System.nanoTime() + timeout.toNanos();

    while (System.nanoTime() < deadline) {
      var state =
          await(
              componentClient
                  .forEventSourcedEntity(orderId)
                  .method(OrderEntity::getOrder)
                  .invokeAsync());

      if (state.exists()) {
        return state;
      }

      sleep(100);
    }

    return await(
        componentClient
            .forEventSourcedEntity(orderId)
            .method(OrderEntity::getOrder)
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
