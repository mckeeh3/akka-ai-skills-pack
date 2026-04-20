package com.example.domain;

import akka.javasdk.annotations.TypeName;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Shared domain model for the edge-facing shopping cart example.
 *
 * <p>This follows an AI-friendly pattern:
 *
 * <ul>
 *   <li>state is a nested record</li>
 *   <li>commands are grouped under a sealed interface</li>
 *   <li>state mutation methods are pure and focused only on applying data changes</li>
 * </ul>
 *
 * <p>Validation is intentionally kept out of this file. See {@code ShoppingCartValidator}.
 */
public final class ShoppingCart {

  private ShoppingCart() {}

  public record State(String cartId, List<LineItem> items, boolean checkedOut) {

    public static State empty(String cartId) {
      return new State(cartId, List.of(), false);
    }

    public State addItem(LineItem item) {
      var updatedItem =
          findItem(item.productId())
              .map(existing -> existing.withQuantity(existing.quantity() + item.quantity()))
              .orElse(item);

      var updatedItems =
          Stream.concat(itemsWithout(item.productId()).stream(), Stream.of(updatedItem))
              .sorted(Comparator.comparing(LineItem::productId))
              .toList();

      return new State(cartId, updatedItems, checkedOut);
    }

    public State removeItem(String productId) {
      return new State(cartId, itemsWithout(productId), checkedOut);
    }

    public State checkout() {
      return new State(cartId, items, true);
    }

    public Optional<LineItem> findItem(String productId) {
      return items.stream().filter(item -> item.productId().equals(productId)).findFirst();
    }

    private List<LineItem> itemsWithout(String productId) {
      return items.stream().filter(item -> !item.productId().equals(productId)).toList();
    }
  }

  public record LineItem(String productId, String name, int quantity) {
    public LineItem withQuantity(int quantity) {
      return new LineItem(productId, name, quantity);
    }
  }

  public sealed interface Command {
    record AddItem(String productId, String name, int quantity) implements Command {}

    record RemoveItem(String productId) implements Command {}

    record Checkout() implements Command {}

    record Delete() implements Command {}
  }

  public sealed interface Event {
    @TypeName("shopping-cart-item-added")
    record ItemAdded(LineItem item) implements Event {}

    @TypeName("shopping-cart-item-removed")
    record ItemRemoved(String productId) implements Event {}

    @TypeName("shopping-cart-checked-out")
    record CheckedOut() implements Event {}

    @TypeName("shopping-cart-deleted")
    record Deleted() implements Event {}
  }
}
