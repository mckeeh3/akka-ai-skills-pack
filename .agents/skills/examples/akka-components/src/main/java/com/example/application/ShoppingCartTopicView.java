package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.domain.ShoppingCart;
import java.util.List;

/**
 * Topic-backed view that consumes shopping-cart topic messages with ce-subject metadata.
 */
@Component(id = "shopping-cart-topic-view")
public class ShoppingCartTopicView extends View {

  public record FindByStatus(String status) {}

  public record CartEventSummary(
      String cartId,
      String status,
      int eventCount,
      boolean localOrigin,
      String originRegion) {}

  public record CartEventSummaries(List<CartEventSummary> entries) {}

  @Consume.FromTopic("shopping-cart-events")
  public static class ShoppingCartTopicUpdater extends TableUpdater<CartEventSummary> {

    public Effect<CartEventSummary> onEvent(ShoppingCart.Event event) {
      var cartId = updateContext().eventSubject().orElse("");
      var current =
          rowState() == null
              ? new CartEventSummary(
                  cartId,
                  "EMPTY",
                  0,
                  updateContext().hasLocalOrigin(),
                  updateContext().originRegion().orElse(updateContext().selfRegion()))
              : rowState();

      return switch (event) {
        case ShoppingCart.Event.ItemRemoved ignored -> effects().ignore();
        case ShoppingCart.Event.Deleted ignored -> effects().deleteRow();
        case ShoppingCart.Event.CheckedOut ignored ->
            effects()
                .updateRow(
                    new CartEventSummary(
                        cartId,
                        "CHECKED_OUT",
                        current.eventCount(),
                        updateContext().hasLocalOrigin(),
                        updateContext().originRegion().orElse(updateContext().selfRegion())));
        case ShoppingCart.Event.ItemAdded ignored ->
            effects()
                .updateRow(
                    new CartEventSummary(
                        cartId,
                        "ACTIVE",
                        current.eventCount() + 1,
                        updateContext().hasLocalOrigin(),
                        updateContext().originRegion().orElse(updateContext().selfRegion())));
      };
    }
  }

  @Query(
      """
      SELECT * AS entries
      FROM shopping_cart_topic_view
      WHERE status = :status
      ORDER BY cartId
      """)
  public QueryEffect<CartEventSummaries> getByStatus(FindByStatus request) {
    return queryResult();
  }
}
