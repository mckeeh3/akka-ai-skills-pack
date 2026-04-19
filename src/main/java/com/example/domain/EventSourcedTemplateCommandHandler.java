package com.example.domain;

import java.util.List;
import java.util.Objects;

/**
 * Command-to-event logic for {@link EventSourcedTemplate}.
 *
 * <p>
 * This handler models a flow-internal entity. Commands are assumed to be normalized and validated upstream. As a
 * result, stale or duplicate commands are usually handled as no-ops instead of business rejections.
 */
public final class EventSourcedTemplateCommandHandler {

  private EventSourcedTemplateCommandHandler() {
  }

  public static List<EventSourcedTemplate.Event> onCommand(
      EventSourcedTemplate.State state,
      String entityId,
      EventSourcedTemplate.Command.Create command) {

    if (state.exists()) {
      return List.of();
    }

    return List.of(new EventSourcedTemplate.Event.Created(entityId, command.name()));
  }

  public static List<EventSourcedTemplate.Event> onCommand(
      EventSourcedTemplate.State state,
      EventSourcedTemplate.Command.Rename command) {

    if (!state.exists() || Objects.equals(state.name(), command.name())) {
      return List.of();
    }

    return List.of(new EventSourcedTemplate.Event.Renamed(command.name()));
  }

  /**
   * Example of delegating non-trivial business decisions to a separate class.
   *
   * <p>
   * The business logic class can normalize inputs, apply rules, and decide which facts should be persisted. The event
   * handler must not repeat that logic during replay.
   */
  public static List<EventSourcedTemplate.Event> onCommand(
      EventSourcedTemplate.State state,
      EventSourcedTemplate.Command.UpdateDetails command) {

    if (!state.exists()) {
      return List.of();
    }

    return EventSourcedTemplateBusinessLogic.decideUpdateDetails(state, command).events();
  }

  public static List<EventSourcedTemplate.Event> onCommand(
      EventSourcedTemplate.State state,
      EventSourcedTemplate.Command.Delete command) {

    if (!state.exists()) {
      return List.of();
    }

    return List.of(new EventSourcedTemplate.Event.Deleted());
  }
}
