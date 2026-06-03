package com.example.domain;

import java.util.Optional;

/**
 * Example of business decision logic kept outside the key value entity.
 *
 * <p>This class decides whether a line item update should change the full order state and whether
 * that same state transition should also mark the whole order as ready to ship.
 */
public final class PurchaseOrderReadyToShipBusinessLogic {

  private PurchaseOrderReadyToShipBusinessLogic() {}

  public static ReadyToShipDecision decide(
      PurchaseOrder.State state,
      PurchaseOrder.Command.LineItemReadyToShip command) {
    if (!state.exists() || state.readyToShip()) {
      return ReadyToShipDecision.noOp();
    }

    var lineItem = state.findLineItem(command.productId());
    if (lineItem.isEmpty() || lineItem.get().readyToShip()) {
      return ReadyToShipDecision.noOp();
    }

    var updatedState = state.flagLineItemReadyToShip(command.productId());
    if (updatedState.allLineItemsReadyToShip()) {
      updatedState = updatedState.flagOrderReadyToShip();
    }

    return new ReadyToShipDecision(Optional.of(updatedState));
  }

  public record ReadyToShipDecision(Optional<PurchaseOrder.State> updatedState) {
    public static ReadyToShipDecision noOp() {
      return new ReadyToShipDecision(Optional.empty());
    }
  }
}
