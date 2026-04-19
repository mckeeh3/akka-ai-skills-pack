package com.example.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Shared domain model for the key value draft cart example.
 *
 * <p>This mirrors the event sourced shopping cart example but is tailored for key value entities:
 * the current state is replaced on each successful write.
 */
public final class DraftCart {

  private DraftCart() {}

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

  public sealed interface Notification {
    record ItemAdded(String cartId, String productId, String name, int quantity) implements Notification {}

    record ItemRemoved(String cartId, String productId) implements Notification {}

    record CheckedOut(String cartId) implements Notification {}

    record Deleted(String cartId) implements Notification {}
  }
}
