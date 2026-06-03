package com.example.domain;

import java.util.Optional;

/**
 * Command-to-state mapping for {@code PurchaseOrderEntity}.
 *
 * <p>This class demonstrates a downstream/internal key value entity where duplicate or stale
 * commands are often treated as no-ops.
 *
 * <p>AI-agent note: this is the key value counterpart to an event-sourced command handler. Instead
 * of returning events to persist, it returns the next state snapshot or {@code Optional.empty()}.
 */
public final class PurchaseOrderCommandHandler {

  private PurchaseOrderCommandHandler() {}

  public static Optional<PurchaseOrder.State> onCommand(
      PurchaseOrder.State state,
      PurchaseOrder.Command.CreateOrder command) {
    if (state.exists()) {
      return Optional.empty();
    }

    return Optional.of(state.create(command.cartId(), command.lineItems()));
  }

  public static Optional<PurchaseOrder.State> onCommand(
      PurchaseOrder.State state,
      PurchaseOrder.Command.LineItemReadyToShip command) {
    return PurchaseOrderReadyToShipBusinessLogic.decide(state, command).updatedState();
  }
}
