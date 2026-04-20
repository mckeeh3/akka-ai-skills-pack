package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.ShoppingCart;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ShoppingCartsByCheckedOutViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withEventSourcedEntityIncomingMessages(ShoppingCartEntity.class);
  }

  @Test
  void shouldQueryCheckedOutShoppingCarts() {
    IncomingMessages shoppingCartEvents =
        testKit.getEventSourcedEntityIncomingMessages(ShoppingCartEntity.class);

    shoppingCartEvents.publish(
        new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("akka-tshirt", "Akka T-Shirt", 2)),
        "cart-view-1");
    shoppingCartEvents.publish(new ShoppingCart.Event.CheckedOut(), "cart-view-1");
    shoppingCartEvents.publish(
        new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("akka-socks", "Akka Socks", 1)),
        "cart-view-2");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(ShoppingCartsByCheckedOutView::getCarts)
                      .invoke(new ShoppingCartsByCheckedOutView.FindByCheckedOut(true));

              assertEquals(1, result.carts().size());
              assertEquals("cart-view-1", result.carts().getFirst().cartId());
              assertTrue(result.carts().getFirst().checkedOut());
              assertEquals(1, result.carts().getFirst().items().size());
            });
  }

  @Test
  void deleteEventRemovesShoppingCartFromView() {
    IncomingMessages shoppingCartEvents =
        testKit.getEventSourcedEntityIncomingMessages(ShoppingCartEntity.class);

    shoppingCartEvents.publish(
        new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("akka-tshirt", "Akka T-Shirt", 1)),
        "cart-view-delete");
    shoppingCartEvents.publish(new ShoppingCart.Event.CheckedOut(), "cart-view-delete");
    shoppingCartEvents.publish(new ShoppingCart.Event.Deleted(), "cart-view-delete");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(ShoppingCartsByCheckedOutView::getCarts)
                      .invoke(new ShoppingCartsByCheckedOutView.FindByCheckedOut(true));

              assertTrue(result.carts().isEmpty());
            });
  }
}
