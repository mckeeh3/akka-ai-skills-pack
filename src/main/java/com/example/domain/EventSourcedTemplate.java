package com.example.domain;

import akka.javasdk.annotations.TypeName;

/**
 * Shared domain model for the event sourced entity template.
 *
 * <p>This template is intentionally simple and AI-friendly:
 *
 * <ul>
 *   <li>The Akka entity id is the only source of truth for identity.</li>
 *   <li>Commands do not carry an id, which avoids id mismatches.</li>
 *   <li>State exposes {@code exists()} instead of relying on "empty string" checks.</li>
 *   <li>One command can emit multiple events to demonstrate event-sourced flows.</li>
 * </ul>
 */
public final class EventSourcedTemplate {

  private EventSourcedTemplate() {}

  public record State(String entityId, String name, String description, int quantity, boolean created) {

    public static State empty(String entityId) {
      return new State(entityId, "", "", 0, false);
    }

    public boolean exists() {
      return created;
    }
  }

  public sealed interface Command {
    record Create(String name) implements Command {}

    record Rename(String name) implements Command {}

    record UpdateDetails(String description, int quantity) implements Command {}

    record Delete() implements Command {}
  }

  public sealed interface Event {
    @TypeName("event-sourced-template-created")
    record Created(String entityId, String name) implements Event {}

    @TypeName("event-sourced-template-renamed")
    record Renamed(String name) implements Event {}

    @TypeName("event-sourced-template-description-updated")
    record DescriptionUpdated(String description) implements Event {}

    @TypeName("event-sourced-template-quantity-updated")
    record QuantityUpdated(int quantity) implements Event {}

    @TypeName("event-sourced-template-deleted")
    record Deleted() implements Event {}
  }
}
