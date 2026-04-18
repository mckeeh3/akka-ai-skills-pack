package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.example.domain.EventSourcedTemplate;
import com.example.domain.EventSourcedTemplateCommandHandler;
import com.example.domain.EventSourcedTemplateEventHandler;
import java.util.List;

/**
 * Thin Akka wrapper around the domain handlers.
 *
 * <p>This example models a flow-internal entity:
 *
 * <ul>
 *   <li>the Akka entity id is authoritative</li>
 *   <li>commands are expected to be validated upstream</li>
 *   <li>duplicate or stale commands are treated as no-ops</li>
 *   <li>non-trivial business rules can live in separate domain classes</li>
 * </ul>
 *
 * <p>For edge-facing entry points, validate commands before invoking this entity. See
 * {@code EventSourcedTemplateValidator}. For an example of replay-safe business decision logic, see
 * {@code EventSourcedTemplateBusinessLogic}.
 */
@Component(id = "event-sourced-entity-template")
public class EventSourcedEntityTemplate
    extends EventSourcedEntity<EventSourcedTemplate.State, EventSourcedTemplate.Event> {

  private final String entityId;

  public EventSourcedEntityTemplate(EventSourcedEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public EventSourcedTemplate.State emptyState() {
    return EventSourcedTemplate.State.empty(entityId);
  }

  public ReadOnlyEffect<EventSourcedTemplate.State> get() {
    return effects().reply(currentState());
  }

  public Effect<Done> create(EventSourcedTemplate.Command.Create command) {
    return persistAndReply(
        EventSourcedTemplateCommandHandler.onCreate(currentState(), entityId, command));
  }

  public Effect<Done> rename(EventSourcedTemplate.Command.Rename command) {
    return persistAndReply(EventSourcedTemplateCommandHandler.onRename(currentState(), command));
  }

  public Effect<Done> updateDetails(EventSourcedTemplate.Command.UpdateDetails command) {
    return persistAndReply(
        EventSourcedTemplateCommandHandler.onUpdateDetails(currentState(), command));
  }

  public Effect<Done> delete() {
    var events = EventSourcedTemplateCommandHandler.onDelete(
        currentState(), new EventSourcedTemplate.Command.Delete());

    if (events.isEmpty()) {
      return effects().reply(Done.getInstance());
    }

    return effects().persistAll(events).deleteEntity().thenReply(__ -> Done.getInstance());
  }

  @Override
  public EventSourcedTemplate.State applyEvent(EventSourcedTemplate.Event event) {
    return EventSourcedTemplateEventHandler.apply(currentState(), event);
  }

  private Effect<Done> persistAndReply(List<EventSourcedTemplate.Event> events) {
    if (events.isEmpty()) {
      return effects().reply(Done.getInstance());
    }

    return effects().persistAll(events).thenReply(__ -> Done.getInstance());
  }
}
