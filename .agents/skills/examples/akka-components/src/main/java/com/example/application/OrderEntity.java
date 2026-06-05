package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.EnableReplicationFilter;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import akka.javasdk.eventsourcedentity.ReplicationFilter;
import com.example.domain.Order;
import com.example.domain.OrderCommandHandler;
import com.example.domain.OrderEventHandler;
import com.example.domain.OrderValidator;
import java.util.List;

/**
 * Event sourced entity that models order fulfillment after shopping cart checkout.
 *
 * <p>This example is aimed at downstream or flow-internal use cases. Commands may be validated for
 * malformed input, while duplicate or stale commands are often treated as no-ops.
 */
@Component(id = "order")
@EnableReplicationFilter
public class OrderEntity extends EventSourcedEntity<Order.State, Order.Event> {

  private final String orderId;

  public OrderEntity(EventSourcedEntityContext context) {
    this.orderId = context.entityId();
  }

  @Override
  public Order.State emptyState() {
    return Order.State.empty(orderId);
  }

  public ReadOnlyEffect<Order.State> getOrder() {
    return effects().reply(currentState());
  }

  /**
   * Strongly consistent read pattern for replicated deployments.
   *
   * <p>Returning {@code Effect} instead of {@code ReadOnlyEffect} routes the request to the
   * primary region even though this command does not persist events.
   */
  public Effect<Order.State> getOrderConsistent() {
    return effects().reply(currentState());
  }

  public Effect<Done> createOrder(Order.Command.CreateOrder command) {
    var errors = OrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return persistAndReply(OrderCommandHandler.onCommand(currentState(), command));
  }

  public Effect<Done> lineItemReadyToShip(Order.Command.LineItemReadyToShip command) {
    var errors = OrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return persistAndReply(OrderCommandHandler.onCommand(currentState(), command));
  }

  public Effect<Done> includeRegion(Order.Command.IncludeRegion command) {
    var errors = OrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return effects()
        .updateReplicationFilter(ReplicationFilter.includeRegion(command.region()))
        .thenReply(__ -> Done.getInstance());
  }

  public Effect<Done> excludeRegion(Order.Command.ExcludeRegion command) {
    var errors = OrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return effects()
        .updateReplicationFilter(ReplicationFilter.excludeRegion(command.region()))
        .thenReply(__ -> Done.getInstance());
  }

  @Override
  public Order.State applyEvent(Order.Event event) {
    return OrderEventHandler.apply(currentState(), event);
  }

  private Effect<Done> persistAndReply(List<Order.Event> events) {
    if (events.isEmpty()) {
      return effects().reply(Done.getInstance());
    }

    if (events.size() == 1) {
      return effects().persist(events.get(0)).thenReply(__ -> Done.getInstance());
    }

    return effects().persistAll(events).thenReply(__ -> Done.getInstance());
  }
}
