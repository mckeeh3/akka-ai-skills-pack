package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.DeleteHandler;
import akka.javasdk.annotations.Query;
import akka.javasdk.annotations.SnapshotHandler;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.domain.ShoppingCart;
import java.util.List;

/**
 * Event sourced view demonstrating snapshots, ignored events, custom delete handling, and
 * streaming query results.
 */
@Component(id = "shopping-cart-audit")
public class ShoppingCartAuditView extends View {

  public record FindByDeleted(boolean deleted) {}

  public record AuditRow(String cartId, int itemCount, boolean checkedOut, boolean deleted) {}

  public record AuditRows(List<AuditRow> carts) {}

  @Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
  public static class ShoppingCartAuditUpdater extends TableUpdater<AuditRow> {

    public Effect<AuditRow> onEvent(ShoppingCart.Event event) {
      var cartId = updateContext().eventSubject().orElse("");
      var current = rowState() == null ? new AuditRow(cartId, 0, false, false) : rowState();

      return switch (event) {
        case ShoppingCart.Event.ItemAdded ignored ->
            effects().updateRow(new AuditRow(cartId, current.itemCount() + 1, current.checkedOut(), false));
        case ShoppingCart.Event.ItemRemoved ignored ->
            effects().updateRow(new AuditRow(cartId, Math.max(0, current.itemCount() - 1), current.checkedOut(), false));
        case ShoppingCart.Event.CheckedOut ignored ->
            effects().updateRow(new AuditRow(cartId, current.itemCount(), true, false));
        case ShoppingCart.Event.Deleted ignored -> effects().ignore();
      };
    }

    @SnapshotHandler
    public Effect<AuditRow> onSnapshot(ShoppingCart.State snapshot) {
      return effects()
          .updateRow(
              new AuditRow(
                  snapshot.cartId(),
                  snapshot.items().size(),
                  snapshot.checkedOut(),
                  false));
    }

    @DeleteHandler
    public Effect<AuditRow> onDelete() {
      var row = rowState();
      return effects().updateRow(new AuditRow(row.cartId(), row.itemCount(), row.checkedOut(), true));
    }
  }

  @Query(
      """
      SELECT * AS carts
      FROM shopping_cart_audit
      WHERE deleted = :deleted
      ORDER BY cartId
      """)
  public QueryEffect<AuditRows> getByDeleted(FindByDeleted request) {
    return queryResult();
  }

  @Query(
      """
      SELECT *
      FROM shopping_cart_audit
      WHERE deleted = :deleted
      ORDER BY cartId
      """)
  public QueryStreamEffect<AuditRow> streamByDeleted(FindByDeleted request) {
    return queryStreamResult();
  }
}
