package com.example.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Example of complex business logic used by a command handler.
 *
 * <p>This class is the right place for domain decisions that would be unsafe to repeat during event
 * replay. It computes the new facts first, then the command handler persists those facts as events.
 * Event handlers should only apply the event data to state.
 */
public final class EventSourcedTemplateBusinessLogic {

  private EventSourcedTemplateBusinessLogic() {}

  public static UpdateDetailsDecision decideUpdateDetails(
      EventSourcedTemplate.State state,
      EventSourcedTemplate.Command.UpdateDetails command) {

    if (!state.exists()) {
      return UpdateDetailsDecision.noOp();
    }

    var normalizedDescription = normalizeDescription(command.description());
    var normalizedQuantity = normalizeQuantity(command.quantity());

    var events = new ArrayList<EventSourcedTemplate.Event>();

    if (!Objects.equals(state.description(), normalizedDescription)) {
      events.add(new EventSourcedTemplate.Event.DescriptionUpdated(normalizedDescription));
    }

    if (state.quantity() != normalizedQuantity) {
      events.add(new EventSourcedTemplate.Event.QuantityUpdated(normalizedQuantity));
    }

    return new UpdateDetailsDecision(List.copyOf(events));
  }

  static String normalizeDescription(String description) {
    if (description == null) {
      return "";
    }

    return description.trim().replaceAll("\\s+", " ");
  }

  static int normalizeQuantity(int quantity) {
    return Math.max(0, quantity);
  }

  public record UpdateDetailsDecision(List<EventSourcedTemplate.Event> events) {
    public static UpdateDetailsDecision noOp() {
      return new UpdateDetailsDecision(List.of());
    }
  }
}
