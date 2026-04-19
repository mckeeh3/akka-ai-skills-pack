package com.example.application;

import akka.Done;
import akka.javasdk.NotificationPublisher;
import akka.javasdk.NotificationPublisher.NotificationStream;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.EnableReplicationFilter;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import akka.javasdk.eventsourcedentity.ReplicationFilter;
import com.example.domain.EventSourcedTemplate;
import com.example.domain.EventSourcedTemplateCommandHandler;
import com.example.domain.EventSourcedTemplateEventHandler;
import java.util.List;
import java.util.function.Function;

/**
 * Thin Akka wrapper around the domain handlers.
 *
 * <p>
 * This example models a flow-internal entity:
 *
 * <ul>
 * <li>the Akka entity id is authoritative</li>
 * <li>commands are expected to be validated upstream</li>
 * <li>duplicate or stale commands are treated as no-ops</li>
 * <li>non-trivial business rules can live in separate domain classes</li>
 * </ul>
 *
 * <p>
 * For edge-facing entry points, validate commands before invoking this entity. See
 * {@code EventSourcedTemplateValidator}. For an example of replay-safe business decision logic, see
 * {@code EventSourcedTemplateBusinessLogic}. For an edge-facing entity that validates inside the entity and returns
 * {@code effects().error(...)} on failures, see {@code ShoppingCartEntity}.
 */
@Component(id = "event-sourced-entity-template")
@EnableReplicationFilter
public class EventSourcedEntityTemplate
    extends EventSourcedEntity<EventSourcedTemplate.State, EventSourcedTemplate.Event> {

  private final String entityId;
  private final NotificationPublisher<EventSourcedTemplate.Event> notificationPublisher;

  public EventSourcedEntityTemplate(
      EventSourcedEntityContext context,
      NotificationPublisher<EventSourcedTemplate.Event> notificationPublisher) {
    this.entityId = context.entityId();
    this.notificationPublisher = notificationPublisher;
  }

  @Override
  public EventSourcedTemplate.State emptyState() {
    return EventSourcedTemplate.State.empty(entityId);
  }

  public ReadOnlyEffect<EventSourcedTemplate.State> get() {
    return effects().reply(currentState());
  }

  /**
   * Strongly consistent read pattern.
   *
   * <p>
   * Even though this method does not persist events, returning {@code Effect} instead of {@code ReadOnlyEffect} allows
   * replicated deployments to route the request to the primary region.
   */
  public Effect<EventSourcedTemplate.State> getConsistent() {
    return effects().reply(currentState());
  }

  /**
   * Exposes a live notification stream of persisted events.
   *
   * <p>
   * Clients subscribe via the {@code ComponentClient} and receive events in real time after connecting. The stream does
   * not replay historical events.
   */
  public NotificationStream<EventSourcedTemplate.Event> notifications() {
    return notificationPublisher.stream();
  }

  public Effect<Done> create(EventSourcedTemplate.Command.Create command) {
    return persistAndReply(
        EventSourcedTemplateCommandHandler.onCommand(currentState(), entityId, command));
  }

  public Effect<Done> rename(EventSourcedTemplate.Command.Rename command) {
    return persistAndReply(EventSourcedTemplateCommandHandler.onCommand(currentState(), command));
  }

  public Effect<Done> updateDetails(EventSourcedTemplate.Command.UpdateDetails command) {
    return persistAndReply(
        EventSourcedTemplateCommandHandler.onCommand(currentState(), command));
  }

  public Effect<Done> includeRegion(EventSourcedTemplate.Command.IncludeRegion command) {
    return effects()
        .updateReplicationFilter(ReplicationFilter.includeRegion(command.region()))
        .thenReply(__ -> Done.getInstance());
  }

  public Effect<Done> excludeRegion(EventSourcedTemplate.Command.ExcludeRegion command) {
    return effects()
        .updateReplicationFilter(ReplicationFilter.excludeRegion(command.region()))
        .thenReply(__ -> Done.getInstance());
  }

  public Effect<Done> delete() {
    var events = EventSourcedTemplateCommandHandler.onCommand(currentState(), new EventSourcedTemplate.Command.Delete());

    if (events.isEmpty()) {
      return effects().reply(Done.getInstance());
    }

    return effects()
        .persistAll(events)
        .deleteEntity()
        .thenReply(
            __ -> {
              events.forEach(notificationPublisher::publish);
              return Done.getInstance();
            });
  }

  @Override
  public EventSourcedTemplate.State applyEvent(EventSourcedTemplate.Event event) {
    return EventSourcedTemplateEventHandler.apply(currentState(), event);
  }

  private Effect<Done> persistAndReply(List<EventSourcedTemplate.Event> events) {
    return persistAndReply(events, __ -> Done.getInstance());
  }

  private <T> Effect<T> persistAndReply(
      List<EventSourcedTemplate.Event> events,
      Function<EventSourcedTemplate.State, T> replyMapper) {
    if (events.isEmpty()) {
      return effects().reply(replyMapper.apply(currentState()));
    }

    if (events.size() == 1) {
      return effects()
          .persist(events.get(0))
          .thenReply(
              newState -> {
                events.forEach(notificationPublisher::publish);
                return replyMapper.apply(newState);
              });
    }

    return effects()
        .persistAll(events)
        .thenReply(
            newState -> {
              events.forEach(notificationPublisher::publish);
              return replyMapper.apply(newState);
            });
  }
}
