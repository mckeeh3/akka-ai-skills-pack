package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.domain.DraftCart;
import java.util.List;

/**
 * Key value view that indexes draft carts by checkout status.
 *
 * <p>This is the canonical repository example for the view pattern that consumes full state
 * updates from a {@code KeyValueEntity}. The view stores a compact summary row rather than the
 * complete draft cart state.
 */
@Component(id = "draft-carts-by-checked-out")
public class DraftCartsByCheckedOutView extends View {

  public record FindByCheckedOut(boolean checkedOut) {}

  public record FindPage(boolean checkedOut, int offset, int pageSize) {}

  public record DraftCartSummary(String cartId, int itemCount, boolean checkedOut) {}

  public record DraftCartSummaries(List<DraftCartSummary> carts) {}

  public record DraftCartPage(List<DraftCartSummary> carts) {}

  @Consume.FromKeyValueEntity(DraftCartEntity.class)
  public static class DraftCartsByCheckedOutUpdater extends TableUpdater<DraftCartSummary> {

    public Effect<DraftCartSummary> onUpdate(DraftCart.State state) {
      var cartId = updateContext().eventSubject().orElse("");
      return effects().updateRow(new DraftCartSummary(cartId, state.items().size(), state.checkedOut()));
    }
  }

  @Query(
      """
      SELECT * AS carts
      FROM draft_carts_by_checked_out
      WHERE checkedOut = :checkedOut
      ORDER BY cartId
      """)
  public QueryEffect<DraftCartSummaries> getCarts(FindByCheckedOut request) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS carts
      FROM draft_carts_by_checked_out
      WHERE checkedOut = :checkedOut
      ORDER BY cartId
      OFFSET :offset
      LIMIT :pageSize
      """)
  public QueryEffect<DraftCartPage> getCartsPage(FindPage request) {
    return queryResult();
  }

  @Query(
      value =
          """
          SELECT *
          FROM draft_carts_by_checked_out
          WHERE checkedOut = :checkedOut
          ORDER BY cartId
          """,
      streamUpdates = true)
  public QueryStreamEffect<DraftCartSummary> continuousCarts(FindByCheckedOut request) {
    return queryStreamResult();
  }
}
