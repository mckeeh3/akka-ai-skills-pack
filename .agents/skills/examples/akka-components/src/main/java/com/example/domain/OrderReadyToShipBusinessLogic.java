package com.example.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Example of business decision logic that is kept outside the event handler.
 *
 * <p>This class decides which facts should be persisted when a line item becomes ready to ship. It
 * inspects the current state first and returns the minimal set of events to persist.
 */
public final class OrderReadyToShipBusinessLogic {

  private OrderReadyToShipBusinessLogic() {}

  public static ReadyToShipDecision decide(
      Order.State state,
      Order.Command.LineItemReadyToShip command) {
    if (!state.exists() || state.readyToShip()) {
      return ReadyToShipDecision.noOp();
    }

    var lineItem = state.findLineItem(command.productId());
    if (lineItem.isEmpty() || lineItem.get().readyToShip()) {
      return ReadyToShipDecision.noOp();
    }

    var events = new ArrayList<Order.Event>();
    events.add(new Order.Event.LineItemFlaggedReadyToShip(command.productId()));

    if (state.allLineItemsReadyToShipAfterFlagging(command.productId())) {
      events.add(new Order.Event.OrderFlaggedReadyToShip());
    }

    return new ReadyToShipDecision(List.copyOf(events));
  }

  public record ReadyToShipDecision(List<Order.Event> events) {
    public static ReadyToShipDecision noOp() {
      return new ReadyToShipDecision(List.of());
    }
  }
}
