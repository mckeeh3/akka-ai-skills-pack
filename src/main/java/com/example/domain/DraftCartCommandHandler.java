package com.example.domain;

import java.util.Optional;

/**
 * Command-to-state mapping for {@code DraftCartEntity}.
 *
 * <p>This class assumes validation has already happened. It can still inspect the current state to
 * decide that some commands should be treated as no-ops.
 */
public final class DraftCartCommandHandler {

  private DraftCartCommandHandler() {}

  public static DraftCart.State onCommand(DraftCart.State state, DraftCart.Command.AddItem command) {
    return state.addItem(new DraftCart.LineItem(command.productId(), command.name(), command.quantity()));
  }

  public static Optional<DraftCart.State> onCommand(
      DraftCart.State state,
      DraftCart.Command.RemoveItem command) {
    if (state.findItem(command.productId()).isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(state.removeItem(command.productId()));
  }

  public static Optional<DraftCart.State> onCommand(
      DraftCart.State state,
      DraftCart.Command.Checkout command) {
    if (state.checkedOut()) {
      return Optional.empty();
    }

    return Optional.of(state.checkout());
  }

  public static boolean shouldDelete(DraftCart.State state, DraftCart.Command.Delete command) {
    return !state.items().isEmpty() || state.checkedOut();
  }
}
