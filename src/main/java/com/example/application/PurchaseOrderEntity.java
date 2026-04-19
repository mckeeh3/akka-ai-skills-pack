package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.EnableReplicationFilter;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import akka.javasdk.keyvalueentity.ReplicationFilter;
import com.example.domain.PurchaseOrder;
import com.example.domain.PurchaseOrderCommandHandler;
import com.example.domain.PurchaseOrderValidator;

/**
 * Key value entity that models order fulfillment after draft cart checkout.
 *
 * <p>This example is aimed at downstream or flow-internal use cases. Commands may be validated for
 * malformed input, while duplicate or stale commands are often treated as no-ops.
 */
@Component(id = "purchase-order")
@EnableReplicationFilter
public class PurchaseOrderEntity extends KeyValueEntity<PurchaseOrder.State> {

  private final String orderId;

  public PurchaseOrderEntity(KeyValueEntityContext context) {
    this.orderId = context.entityId();
  }

  @Override
  public PurchaseOrder.State emptyState() {
    return PurchaseOrder.State.empty(orderId);
  }

  public ReadOnlyEffect<PurchaseOrder.State> getOrder() {
    return effects().reply(currentState());
  }

  /**
   * Strongly consistent read pattern for replicated deployments.
   *
   * <p>Returning {@code Effect} instead of {@code ReadOnlyEffect} routes the request to the
   * primary region even though this command does not update state.
   */
  public Effect<PurchaseOrder.State> getOrderConsistent() {
    return effects().reply(currentState());
  }

  public Effect<Done> createOrder(PurchaseOrder.Command.CreateOrder command) {
    var errors = PurchaseOrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    var updatedState = PurchaseOrderCommandHandler.onCommand(currentState(), command);
    if (updatedState.isEmpty()) {
      return effects().reply(Done.getInstance());
    }

    return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
  }

  public Effect<Done> lineItemReadyToShip(PurchaseOrder.Command.LineItemReadyToShip command) {
    var errors = PurchaseOrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    var updatedState = PurchaseOrderCommandHandler.onCommand(currentState(), command);
    if (updatedState.isEmpty()) {
      return effects().reply(Done.getInstance());
    }

    return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
  }

  public Effect<Done> includeRegion(PurchaseOrder.Command.IncludeRegion command) {
    var errors = PurchaseOrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return effects()
        .updateReplicationFilter(ReplicationFilter.includeRegion(command.region()))
        .thenReply(Done.getInstance());
  }

  public Effect<Done> excludeRegion(PurchaseOrder.Command.ExcludeRegion command) {
    var errors = PurchaseOrderValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }

    return effects()
        .updateReplicationFilter(ReplicationFilter.excludeRegion(command.region()))
        .thenReply(Done.getInstance());
  }
}
