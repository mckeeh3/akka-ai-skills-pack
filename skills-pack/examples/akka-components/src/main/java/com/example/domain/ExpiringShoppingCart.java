package com.example.domain;

import akka.javasdk.annotations.TypeName;
import java.util.List;
import java.util.stream.Stream;

/**
 * Focused domain model for the TTL example.
 *
 * <p>This example is intentionally small so it can be used in documentation and by AI coding
 * assistants when generating event sourced entities that use {@code expireAfter(...)}.
 */
public final class ExpiringShoppingCart {

  private ExpiringShoppingCart() {}

  public record State(String cartId, List<String> productIds) {

    public static State empty(String cartId) {
      return new State(cartId, List.of());
    }

    public State addItem(String productId) {
      return new State(cartId, Stream.concat(productIds.stream(), Stream.of(productId)).toList());
    }
  }

  public sealed interface Event {
    @TypeName("expiring-shopping-cart-item-added")
    record ItemAdded(String productId) implements Event {}
  }
}
