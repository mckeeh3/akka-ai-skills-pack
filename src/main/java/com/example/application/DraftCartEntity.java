package com.example.application;

import akka.javasdk.NotificationPublisher;
import akka.javasdk.NotificationPublisher.NotificationStream;
import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.DraftCart;
import com.example.domain.DraftCartCommandHandler;
import com.example.domain.DraftCartValidator;

/**
 * Edge-facing key value entity that validates commands and returns error effects on failures.
 *
 * <p>This example is intended for entities called directly from endpoint components. Invalid
 * commands are treated as business rejections and return {@code effects().error(...)}.
 */
@Component(id = "draft-cart")
public class DraftCartEntity extends KeyValueEntity<DraftCart.State> {

  private final String cartId;
  private final NotificationPublisher<DraftCart.Notification> notificationPublisher;

  public DraftCartEntity(
      KeyValueEntityContext context,
      NotificationPublisher<DraftCart.Notification> notificationPublisher) {
    this.cartId = context.entityId();
    this.notificationPublisher = notificationPublisher;
  }

  @Override
  public DraftCart.State emptyState() {
    return DraftCart.State.empty(cartId);
  }

  public ReadOnlyEffect<DraftCart.State> getCart() {
    return effects().reply(currentState());
  }

  /**
   * Exposes a live notification stream of draft cart changes.
   *
   * <p>Clients subscribe via the {@code ComponentClient}. The stream emits only new messages after
   * the subscription starts and does not replay historical messages.
   */
  public NotificationStream<DraftCart.Notification> notifications() {
    return notificationPublisher.stream();
  }

  public Effect<DraftCart.State> addItem(DraftCart.Command.AddItem command) {
    var errors = DraftCartValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    var newState = DraftCartCommandHandler.onCommand(currentState(), command);
    return effects()
        .updateState(newState)
        .thenReply(
            () -> {
              notificationPublisher.publish(
                  new DraftCart.Notification.ItemAdded(
                      cartId,
                      command.productId(),
                      command.name(),
                      command.quantity()));
              return newState;
            });
  }

  public Effect<DraftCart.State> removeItem(DraftCart.Command.RemoveItem command) {
    var errors = DraftCartValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    var updatedState = DraftCartCommandHandler.onCommand(currentState(), command);
    if (updatedState.isEmpty()) {
      return effects().reply(currentState());
    }

    return effects()
        .updateState(updatedState.get())
        .thenReply(
            () -> {
              notificationPublisher.publish(
                  new DraftCart.Notification.ItemRemoved(cartId, command.productId()));
              return updatedState.get();
            });
  }

  public Effect<DraftCart.State> checkout(DraftCart.Command.Checkout command) {
    var errors = DraftCartValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    var updatedState = DraftCartCommandHandler.onCommand(currentState(), command);
    if (updatedState.isEmpty()) {
      return effects().reply(currentState());
    }

    return effects()
        .updateState(updatedState.get())
        .thenReply(
            () -> {
              notificationPublisher.publish(new DraftCart.Notification.CheckedOut(cartId));
              return updatedState.get();
            });
  }

  public Effect<String> delete(DraftCart.Command.Delete command) {
    var errors = DraftCartValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    if (!DraftCartCommandHandler.shouldDelete(currentState(), command)) {
      return effects().reply("deleted");
    }

    return effects()
        .deleteEntity()
        .thenReply(
            () -> {
              notificationPublisher.publish(new DraftCart.Notification.Deleted(cartId));
              return "deleted";
            });
  }
}
