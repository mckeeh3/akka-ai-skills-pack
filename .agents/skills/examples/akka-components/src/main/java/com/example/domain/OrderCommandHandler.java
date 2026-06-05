package com.example.domain;

import java.util.List;

/**
 * Command-to-event mapping for {@code OrderEntity}.
 *
 * <p>This class demonstrates a concrete multi-event command handler. A single command may emit zero,
 * one, or two events depending on the current state.
 */
public final class OrderCommandHandler {

  private OrderCommandHandler() {}

  public static List<Order.Event> onCommand(
      Order.State state,
      Order.Command.CreateOrder command) {
    if (state.exists()) {
      return List.of();
    }

    return List.of(new Order.Event.OrderCreated(state.orderId(), command.cartId(), command.lineItems()));
  }

  /**
   * Multi-event command example.
   *
   * <p>This command may emit:
   *
   * <ul>
   *   <li>0 events when the order does not exist, is already ready to ship, the line item is not
   *       found, or that line item is already ready to ship</li>
   *   <li>1 event when one line item becomes ready to ship but the whole order is not yet ready</li>
   *   <li>2 events when the line item becomes ready to ship and that transition makes the whole
   *       order ready to ship</li>
   * </ul>
   *
   * <p>This is a useful pattern for AI coding agents: inspect the current state first, decide the
   * minimal set of facts that changed, and persist exactly those facts as events.
   */
  public static List<Order.Event> onCommand(
      Order.State state,
      Order.Command.LineItemReadyToShip command) {
    return OrderReadyToShipBusinessLogic.decide(state, command).events();
  }
}
