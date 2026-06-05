package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.NotificationPublisher;
import akka.javasdk.testkit.EventSourcedTestKit;
import com.example.domain.ShoppingCart;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShoppingCartEntityTest {

  private final List<ShoppingCart.Event> publishedNotifications = new ArrayList<>();

  private final NotificationPublisher<ShoppingCart.Event> stubPublisher =
      publishedNotifications::add;

  private EventSourcedTestKit<ShoppingCart.State, ShoppingCart.Event, ShoppingCartEntity> newTestKit(
      String entityId) {
    publishedNotifications.clear();
    return EventSourcedTestKit.of(entityId, ctx -> new ShoppingCartEntity(ctx, stubPublisher));
  }

  @Test
  void addItemPersistsSingleEventAndRepliesWithUpdatedCart() {
    var testKit = newTestKit("cart-1");

    var result =
        testKit
            .method(ShoppingCartEntity::addItem)
            .invoke(new ShoppingCart.Command.AddItem("akka-tshirt", "Akka T-Shirt", 2));

    assertEquals(
        new ShoppingCart.State(
            "cart-1",
            List.of(new ShoppingCart.LineItem("akka-tshirt", "Akka T-Shirt", 2)),
            false),
        result.getReply());
    assertEquals(
        new ShoppingCart.Event.ItemAdded(
            new ShoppingCart.LineItem("akka-tshirt", "Akka T-Shirt", 2)),
        result.getNextEventOfType(ShoppingCart.Event.ItemAdded.class));
    assertEquals(
        List.of(
            new ShoppingCart.Event.ItemAdded(
                new ShoppingCart.LineItem("akka-tshirt", "Akka T-Shirt", 2))),
        publishedNotifications);
  }

  @Test
  void addItemWithInvalidQuantityReturnsError() {
    var testKit = newTestKit("cart-1");

    var result =
        testKit
            .method(ShoppingCartEntity::addItem)
            .invoke(new ShoppingCart.Command.AddItem("akka-tshirt", "Akka T-Shirt", 0));

    assertTrue(result.isError());
    assertEquals("quantity must be greater than zero.", result.getError());
    assertFalse(result.didPersistEvents());
  }

  @Test
  void removeMissingItemIsANoOp() {
    var testKit = newTestKit("cart-1");

    var result =
        testKit
            .method(ShoppingCartEntity::removeItem)
            .invoke(new ShoppingCart.Command.RemoveItem("missing-product"));

    assertEquals(ShoppingCart.State.empty("cart-1"), result.getReply());
    assertFalse(result.didPersistEvents());
  }

  @Test
  void checkoutEmptyCartReturnsError() {
    var testKit = newTestKit("cart-1");

    var result =
        testKit.method(ShoppingCartEntity::checkout).invoke(new ShoppingCart.Command.Checkout());

    assertTrue(result.isError());
    assertEquals("Cannot checkout an empty cart.", result.getError());
    assertFalse(result.didPersistEvents());
  }

  @Test
  void checkoutPersistsEventAndReturnsCheckedOutCart() {
    var testKit = newTestKit("cart-1");

    testKit
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem("akka-socks", "Akka Socks", 3));

    var result =
        testKit.method(ShoppingCartEntity::checkout).invoke(new ShoppingCart.Command.Checkout());

    assertEquals(
        new ShoppingCart.State(
            "cart-1",
            List.of(new ShoppingCart.LineItem("akka-socks", "Akka Socks", 3)),
            true),
        result.getReply());
    assertEquals(
        new ShoppingCart.Event.CheckedOut(),
        result.getNextEventOfType(ShoppingCart.Event.CheckedOut.class));
  }

  @Test
  void deletePersistsFinalEventAndMarksEntityDeleted() {
    var testKit = newTestKit("cart-1");

    testKit
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem("akka-socks", "Akka Socks", 3));

    var result =
        testKit.method(ShoppingCartEntity::delete).invoke(new ShoppingCart.Command.Delete());

    assertEquals("deleted", result.getReply());
    assertEquals(
        new ShoppingCart.Event.Deleted(),
        result.getNextEventOfType(ShoppingCart.Event.Deleted.class));
    assertTrue(testKit.isDeleted());
    assertEquals(new ShoppingCart.Event.Deleted(), publishedNotifications.getLast());
  }
}
