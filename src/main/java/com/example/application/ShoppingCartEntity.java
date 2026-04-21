package com.example.application;

import akka.javasdk.NotificationPublisher;
import akka.javasdk.NotificationPublisher.NotificationStream;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.FunctionTool;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.example.domain.ShoppingCart;
import com.example.domain.ShoppingCartCommandHandler;
import com.example.domain.ShoppingCartEventHandler;
import com.example.domain.ShoppingCartValidator;
import java.util.List;
import java.util.function.Function;

/**
 * Edge-facing event sourced entity that validates commands and returns error effects on failures.
 *
 * <p>This example is intended for entities called directly from endpoint components. Invalid
 * commands are treated as business rejections and return {@code effects().error(...)}.
 */
@Component(id = "shopping-cart")
public class ShoppingCartEntity extends EventSourcedEntity<ShoppingCart.State, ShoppingCart.Event> {

  private final String cartId;
  private final NotificationPublisher<ShoppingCart.Event> notificationPublisher;

  public ShoppingCartEntity(
      EventSourcedEntityContext context,
      NotificationPublisher<ShoppingCart.Event> notificationPublisher) {
    this.cartId = context.entityId();
    this.notificationPublisher = notificationPublisher;
  }

  @Override
  public ShoppingCart.State emptyState() {
    return ShoppingCart.State.empty(cartId);
  }

  @FunctionTool(
      description =
          "Return the current shopping cart state for the given uniqueId cart id, including items, quantities, and checkout status.")
  public ReadOnlyEffect<ShoppingCart.State> getCart() {
    return effects().reply(currentState());
  }

  /**
   * Exposes a live notification stream of persisted shopping cart events.
   *
   * <p>Clients subscribe via the {@code ComponentClient}. The stream emits only new messages after
   * the subscription starts and does not replay historical events.
   */
  public NotificationStream<ShoppingCart.Event> notifications() {
    return notificationPublisher.stream();
  }

  public Effect<ShoppingCart.State> addItem(ShoppingCart.Command.AddItem command) {
    var errors = ShoppingCartValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return persistAndReply(List.of(ShoppingCartCommandHandler.onCommand(currentState(), command)), newState -> newState);
  }

  public Effect<ShoppingCart.State> removeItem(ShoppingCart.Command.RemoveItem command) {
    var errors = ShoppingCartValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return persistAndReply(
        ShoppingCartCommandHandler.onCommand(currentState(), command).stream().toList(),
        newState -> newState);
  }

  public Effect<ShoppingCart.State> checkout(ShoppingCart.Command.Checkout command) {
    var errors = ShoppingCartValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return persistAndReply(
        ShoppingCartCommandHandler.onCommand(currentState(), command).stream().toList(),
        newState -> newState);
  }

  public Effect<String> delete(ShoppingCart.Command.Delete command) {
    var errors = ShoppingCartValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return deleteAndReply(
        ShoppingCartCommandHandler.onCommand(currentState(), command).stream().toList(),
        __ -> "deleted");
  }

  @Override
  public ShoppingCart.State applyEvent(ShoppingCart.Event event) {
    return ShoppingCartEventHandler.apply(currentState(), event);
  }

  private <T> Effect<T> persistAndReply(
      List<ShoppingCart.Event> events,
      Function<ShoppingCart.State, T> replyMapper) {
    if (events.isEmpty()) {
      return effects().reply(replyMapper.apply(currentState()));
    }

    if (events.size() == 1) {
      return effects()
          .persist(events.get(0))
          .thenReply(
              newState -> {
                events.forEach(notificationPublisher::publish);
                return replyMapper.apply(newState);
              });
    }

    return effects()
        .persistAll(events)
        .thenReply(
            newState -> {
              events.forEach(notificationPublisher::publish);
              return replyMapper.apply(newState);
            });
  }

  private <T> Effect<T> deleteAndReply(
      List<ShoppingCart.Event> events,
      Function<ShoppingCart.State, T> replyMapper) {
    if (events.isEmpty()) {
      return effects().reply(replyMapper.apply(currentState()));
    }

    return effects()
        .persistAll(events)
        .deleteEntity()
        .thenReply(
            newState -> {
              events.forEach(notificationPublisher::publish);
              return replyMapper.apply(newState);
            });
  }
}
