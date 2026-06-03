package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.domain.ShoppingCart;
import com.example.domain.ShoppingCartEventHandler;
import java.util.List;

/**
 * Event sourced view that indexes shopping carts by checkout status.
 *
 * <p>This is the canonical repository example for the view pattern that consumes events from an
 * {@code EventSourcedEntity}. The table row reuses the replay-safe shopping cart state and evolves
 * through the same pure event handler used by the entity.
 */
@Component(id = "shopping-carts-by-checked-out")
public class ShoppingCartsByCheckedOutView extends View {

  public record FindByCheckedOut(boolean checkedOut) {}

  public record ShoppingCartSummaries(List<ShoppingCart.State> carts) {}

  @Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
  public static class ShoppingCartsByCheckedOutUpdater extends TableUpdater<ShoppingCart.State> {

    public Effect<ShoppingCart.State> onEvent(ShoppingCart.Event event) {
      return switch (event) {
        case ShoppingCart.Event.Deleted ignored -> effects().deleteRow();
        default -> {
          var cartId = updateContext().eventSubject().orElse("");
          var currentRow = rowState() == null ? ShoppingCart.State.empty(cartId) : rowState();
          yield effects().updateRow(ShoppingCartEventHandler.apply(currentRow, event));
        }
      };
    }
  }

  @Query(
      """
      SELECT * AS carts
      FROM shopping_carts_by_checked_out
      WHERE checkedOut = :checkedOut
      ORDER BY cartId
      """)
  public QueryEffect<ShoppingCartSummaries> getCarts(FindByCheckedOut request) {
    return queryResult();
  }

  @Query(
      """
      SELECT *
      FROM shopping_carts_by_checked_out
      WHERE checkedOut = :checkedOut
      ORDER BY cartId
      """)
  public QueryStreamEffect<ShoppingCart.State> streamCarts(FindByCheckedOut request) {
    return queryStreamResult();
  }
}
