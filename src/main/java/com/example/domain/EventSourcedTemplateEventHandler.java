package com.example.domain;

/**
 * Applies persisted events to the current state.
 *
 * <p>This class must stay replay-safe. It should not perform business decisions, validation, time
 * lookups, or external calls. Those belong in command handlers or domain business logic classes.
 */
public final class EventSourcedTemplateEventHandler {

  private EventSourcedTemplateEventHandler() {}

  public static EventSourcedTemplate.State apply(
      EventSourcedTemplate.State state,
      EventSourcedTemplate.Event event) {

    return switch (event) {
      case EventSourcedTemplate.Event.Created created ->
          new EventSourcedTemplate.State(
              created.entityId(),
              created.name(),
              state.description(),
              state.quantity(),
              true);
      case EventSourcedTemplate.Event.Renamed renamed ->
          new EventSourcedTemplate.State(
              state.entityId(),
              renamed.name(),
              state.description(),
              state.quantity(),
              state.created());
      case EventSourcedTemplate.Event.DescriptionUpdated updated ->
          new EventSourcedTemplate.State(
              state.entityId(),
              state.name(),
              updated.description(),
              state.quantity(),
              state.created());
      case EventSourcedTemplate.Event.QuantityUpdated updated ->
          new EventSourcedTemplate.State(
              state.entityId(),
              state.name(),
              state.description(),
              updated.quantity(),
              state.created());
      case EventSourcedTemplate.Event.Deleted deleted ->
          EventSourcedTemplate.State.empty(state.entityId());
    };
  }
}
