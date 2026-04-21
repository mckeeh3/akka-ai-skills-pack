---
name: akka-ese-application-entity
description: Implement core Akka Java SDK EventSourcedEntity classes in the application package, including command handlers, read handlers, persist/persistAll, and delete behavior. Use companion skills for TTL, notifications, and replication.
---

# Akka ESE Application Entity

Use this skill for the `application` package entity class itself.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/application/OrderEntity.java`

Load companion skills when needed:
- `akka-ese-ttl`
- `akka-ese-notifications`
- `akka-ese-replication`

## Entity skeleton

The entity should:
- extend `EventSourcedEntity<State, Event>`
- have `@Component(id = "...")`
- override `emptyState()`
- implement command handlers that return `Effect<T>` or `ReadOnlyEffect<T>`
- implement a pure `applyEvent(Event event)`

## Constructor rules

Use constructor injection for supported dependencies, for example:
- `EventSourcedEntityContext`
- `NotificationPublisher<Event>`

If `emptyState()` needs the entity id, store `context.entityId()` in a field.

## Command handler algorithm

For each command:
1. inspect `currentState()`
2. validate input
3. return `effects().error(...)` on invalid input when appropriate
4. delegate business decision logic to domain helpers
5. if no events are needed, reply without persisting
6. if one event is needed, use `persist(event)`
7. if many events are needed, use `persistAll(events)`
8. reply in `thenReply(...)`

## Read handler rules

Use:
- `ReadOnlyEffect<T>` for ordinary reads
- `Effect<T>` for strongly consistent reads in replicated deployments

Repository example:
- `OrderEntity.getOrderConsistent()`

## Delete pattern

When deleting an entity:
- persist a final domain event first
- call `.deleteEntity()`
- then reply
- do not persist more events afterward

Repository example:
- `ShoppingCartEntity.delete(...)`

## Feature-specific companion skills

For focused guidance, load:
- `akka-ese-ttl`
- `akka-ese-notifications`
- `akka-ese-replication`

## Anti-patterns

Never:
- put business logic directly in `applyEvent`
- mutate state directly in command handlers
- call external services from the entity
- publish notifications before persist succeeds
- skip `emptyState()` when a sensible default exists

## Review checklist

Before finishing, verify:
- entity extends `EventSourcedEntity<State, Event>`
- `@Component(id = ...)` exists
- `emptyState()` is sensible
- `applyEvent` is pure
- validation happens before persist
- no-op commands do not persist events
- delete behavior is explicit when needed
- TTL, notifications, and replication use the companion skills when included
