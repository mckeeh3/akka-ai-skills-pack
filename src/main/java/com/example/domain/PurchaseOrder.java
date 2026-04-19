package com.example.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Shared domain model for the downstream key value purchase order example.
 *
 * <p>This example mirrors the event sourced order example but updates whole state snapshots rather
 * than emitting events.
 */
public final class PurchaseOrder {

  private PurchaseOrder() {}

  public record State(
      String orderId,
      String cartId,
      List<LineItem> lineItems,
      boolean readyToShip,
      boolean created) {

    public static State empty(String orderId) {
      return new State(orderId, "", List.of(), false, false);
    }

    public boolean exists() {
      return created;
    }

    public State create(String cartId, List<LineItem> lineItems) {
      return new State(orderId, cartId, sortLineItems(lineItems), false, true);
    }

    public State flagLineItemReadyToShip(String productId) {
      return new State(
          orderId,
          cartId,
          sortLineItems(
              lineItems.stream()
                  .map(item -> item.productId().equals(productId) ? item.flagReadyToShip() : item)
                  .toList()),
          readyToShip,
          created);
    }

    public State flagOrderReadyToShip() {
      return new State(orderId, cartId, lineItems, true, created);
    }

    public Optional<LineItem> findLineItem(String productId) {
      return lineItems.stream().filter(item -> item.productId().equals(productId)).findFirst();
    }

    public boolean allLineItemsReadyToShip() {
      return !lineItems.isEmpty() && lineItems.stream().allMatch(LineItem::readyToShip);
    }

    public boolean allLineItemsReadyToShipAfterFlagging(String productId) {
      return !lineItems.isEmpty()
          && lineItems.stream()
              .allMatch(item -> item.readyToShip() || item.productId().equals(productId));
    }

    private static List<LineItem> sortLineItems(List<LineItem> lineItems) {
      return lineItems.stream().sorted(Comparator.comparing(LineItem::productId)).toList();
    }
  }

  public record LineItem(String productId, String name, int quantity, boolean readyToShip) {
    public LineItem flagReadyToShip() {
      return new LineItem(productId, name, quantity, true);
    }
  }

  public sealed interface Command {
    record CreateOrder(String cartId, List<LineItem> lineItems) implements Command {}

    record LineItemReadyToShip(String productId) implements Command {}

    record IncludeRegion(String region) implements Command {}

    record ExcludeRegion(String region) implements Command {}
  }
}
