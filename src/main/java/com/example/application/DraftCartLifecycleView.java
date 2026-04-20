package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.DeleteHandler;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.domain.DraftCart;
import java.util.List;

/**
 * Key value view demonstrating custom delete handling with a logical delete flag.
 */
@Component(id = "draft-cart-lifecycle")
public class DraftCartLifecycleView extends View {

  public record FindByDeleted(boolean deleted) {}

  public record LifecycleRow(String cartId, int itemCount, boolean deleted) {}

  public record LifecycleRows(List<LifecycleRow> rows) {}

  @Consume.FromKeyValueEntity(DraftCartEntity.class)
  public static class DraftCartLifecycleUpdater extends TableUpdater<LifecycleRow> {

    public Effect<LifecycleRow> onUpdate(DraftCart.State state) {
      var cartId = updateContext().eventSubject().orElse("");
      return effects().updateRow(new LifecycleRow(cartId, state.items().size(), false));
    }

    @DeleteHandler
    public Effect<LifecycleRow> onDelete() {
      var row = rowState();
      return effects().updateRow(new LifecycleRow(row.cartId(), row.itemCount(), true));
    }
  }

  @Query(
      """
      SELECT * AS rows
      FROM draft_cart_lifecycle
      WHERE deleted = :deleted
      ORDER BY cartId
      """)
  public QueryEffect<LifecycleRows> getByDeleted(FindByDeleted request) {
    return queryResult();
  }
}
