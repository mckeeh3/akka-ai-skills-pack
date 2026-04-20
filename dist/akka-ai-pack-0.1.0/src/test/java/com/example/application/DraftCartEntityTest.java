package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.NotificationPublisher;
import akka.javasdk.testkit.KeyValueEntityTestKit;
import com.example.domain.DraftCart;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DraftCartEntityTest {

  private final List<DraftCart.Notification> publishedNotifications = new ArrayList<>();

  private final NotificationPublisher<DraftCart.Notification> stubPublisher =
      publishedNotifications::add;

  private KeyValueEntityTestKit<DraftCart.State, DraftCartEntity> newTestKit(String entityId) {
    publishedNotifications.clear();
    return KeyValueEntityTestKit.of(entityId, ctx -> new DraftCartEntity(ctx, stubPublisher));
  }

  @Test
  void addItemUpdatesStateAndPublishesNotification() {
    var testKit = newTestKit("cart-1");

    var result =
        testKit
            .method(DraftCartEntity::addItem)
            .invoke(new DraftCart.Command.AddItem("akka-tshirt", "Akka T-Shirt", 2));

    assertTrue(result.stateWasUpdated());
    assertEquals(
        new DraftCart.State(
            "cart-1",
            List.of(new DraftCart.LineItem("akka-tshirt", "Akka T-Shirt", 2)),
            false),
        result.getReply());
    assertEquals(
        List.of(
            new DraftCart.Notification.ItemAdded(
                "cart-1", "akka-tshirt", "Akka T-Shirt", 2)),
        publishedNotifications);
  }

  @Test
  void addItemWithInvalidQuantityReturnsError() {
    var testKit = newTestKit("cart-1");

    var result =
        testKit
            .method(DraftCartEntity::addItem)
            .invoke(new DraftCart.Command.AddItem("akka-tshirt", "Akka T-Shirt", 0));

    assertTrue(result.isError());
    assertEquals("quantity must be greater than zero.", result.getError());
    assertFalse(result.stateWasUpdated());
  }

  @Test
  void removeMissingItemIsANoOp() {
    var testKit = newTestKit("cart-1");

    var result =
        testKit
            .method(DraftCartEntity::removeItem)
            .invoke(new DraftCart.Command.RemoveItem("missing-product"));

    assertEquals(DraftCart.State.empty("cart-1"), result.getReply());
    assertFalse(result.stateWasUpdated());
  }

  @Test
  void checkoutEmptyCartReturnsError() {
    var testKit = newTestKit("cart-1");

    var result = testKit.method(DraftCartEntity::checkout).invoke(new DraftCart.Command.Checkout());

    assertTrue(result.isError());
    assertEquals("Cannot checkout an empty cart.", result.getError());
    assertFalse(result.stateWasUpdated());
  }

  @Test
  void deleteDeletesEntityAndPublishesNotification() {
    var testKit = newTestKit("cart-1");

    testKit
        .method(DraftCartEntity::addItem)
        .invoke(new DraftCart.Command.AddItem("akka-socks", "Akka Socks", 3));

    var result = testKit.method(DraftCartEntity::delete).invoke(new DraftCart.Command.Delete());

    assertEquals("deleted", result.getReply());
    assertTrue(result.stateWasDeleted());
    assertTrue(testKit.isDeleted());
    assertEquals(new DraftCart.Notification.Deleted("cart-1"), publishedNotifications.getLast());
  }
}
