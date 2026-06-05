package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.example.domain.ExpiringShoppingCart;
import java.time.Duration;

/**
 * Focused TTL example for event sourced entities.
 *
 * <p>Each successful persist attaches a 30 day time-to-live. If no further events are persisted
 * before that duration elapses, Akka automatically deletes the entity.
 */
@Component(id = "expiring-shopping-cart")
public class ExpiringShoppingCartEntity
    extends EventSourcedEntity<ExpiringShoppingCart.State, ExpiringShoppingCart.Event> {

  static final Duration TTL = Duration.ofDays(30);

  private final String cartId;

  public ExpiringShoppingCartEntity(EventSourcedEntityContext context) {
    this.cartId = context.entityId();
  }

  @Override
  public ExpiringShoppingCart.State emptyState() {
    return ExpiringShoppingCart.State.empty(cartId);
  }

  public ReadOnlyEffect<ExpiringShoppingCart.State> getCart() {
    return effects().reply(currentState());
  }

  public Effect<Done> addItem(String productId) {
    if (productId == null || productId.isBlank()) {
      return effects().error("productId must not be blank.");
    }

    return effects()
        .persist(new ExpiringShoppingCart.Event.ItemAdded(productId))
        .expireAfter(TTL)
        .thenReply(__ -> Done.getInstance());
  }

  @Override
  public ExpiringShoppingCart.State applyEvent(ExpiringShoppingCart.Event event) {
    return switch (event) {
      case ExpiringShoppingCart.Event.ItemAdded added -> currentState().addItem(added.productId());
    };
  }
}
