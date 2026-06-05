package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.EventSourcedTestKit;
import com.example.domain.ExpiringShoppingCart;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ExpiringShoppingCartEntityTest {

  @Test
  void addItemPersistsEventAndSetsTtl() {
    var testKit = EventSourcedTestKit.of("cart-ttl-1", ExpiringShoppingCartEntity::new);

    var result = testKit.method(ExpiringShoppingCartEntity::addItem).invoke("sku-1");

    assertEquals(Done.getInstance(), result.getReply());
    assertEquals(
        new ExpiringShoppingCart.Event.ItemAdded("sku-1"),
        result.getNextEventOfType(ExpiringShoppingCart.Event.ItemAdded.class));
    assertEquals(Optional.of(Duration.ofDays(30)), result.getExpireAfter());
    assertEquals(new ExpiringShoppingCart.State("cart-ttl-1", List.of("sku-1")), testKit.getState());
  }

  @Test
  void addItemWithBlankProductIdReturnsErrorWithoutPersistingEvents() {
    var testKit = EventSourcedTestKit.of("cart-ttl-2", ExpiringShoppingCartEntity::new);

    var result = testKit.method(ExpiringShoppingCartEntity::addItem).invoke(" ");

    assertTrue(result.isError());
    assertEquals("productId must not be blank.", result.getError());
    assertFalse(result.didPersistEvents());
    assertEquals(Optional.empty(), result.getExpireAfter());
  }
}
