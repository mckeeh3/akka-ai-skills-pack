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

  public ReadOnlyEffect<ShoppingCart.State> getCart() {
    return effects().reply(currentState());
  }

  /**
   * Read-only evidence capability exposed as an agent component tool.
   *
   * <p>Capability: {@code cart.inspect-summary}. The output is a curated, agent-safe summary of
   * cart contents and checkout state. It intentionally avoids exposing event history or component
   * internals; production SaaS variants would also enforce AuthContext, tenant scope, data-access
   * audit, and field redaction before returning protected cart evidence.
   */
  @FunctionTool(
      description =
          "Read-only capability cart.inspect-summary. Return a curated, agent-safe cart "
              + "summary for the given uniqueId cart id, including line item names, quantities, "
              + "total quantity, and checkout status. This tool does not change cart state.")
  public ReadOnlyEffect<CartSummary> inspectCartSummary() {
    return effects().reply(CartSummary.from(currentState()));
  }

  public record CartSummary(
      String cartId, List<ItemSummary> items, int totalQuantity, boolean checkedOut) {
    static CartSummary from(ShoppingCart.State state) {
      var summaries =
          state.items().stream()
              .map(item -> new ItemSummary(item.name(), item.quantity()))
              .toList();
      var totalQuantity = summaries.stream().mapToInt(ItemSummary::quantity).sum();
      return new CartSummary(state.cartId(), summaries, totalQuantity, state.checkedOut());
    }
  }

  public record ItemSummary(String name, int quantity) {}

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
