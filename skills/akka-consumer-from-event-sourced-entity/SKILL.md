---
name: akka-consumer-from-event-sourced-entity
description: Implement Akka Java SDK Consumers that subscribe to EventSourcedEntity events, optionally start from snapshots, and trigger idempotent downstream effects.
---

# Akka Consumer from Event Sourced Entity

Use this skill when a Consumer reacts to persisted events from an Event Sourced Entity.

## Required reading

Read these first if present:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/component-and-service-calls.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `../../../src/main/java/com/example/application/ShoppingCartEventsToTopicConsumer.java`
- `../../../src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `../../../src/test/java/com/example/application/ShoppingCartCheckoutConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`

## Use this pattern when

- the downstream behavior depends on event facts, not just latest state
- the consumer should react differently to different event subtypes
- replay of historical events matters
- snapshots may help a new consumer catch up faster

## Core pattern

1. Annotate the class with `@Consume.FromEventSourcedEntity(MyEntity.class)`.
2. Accept the entity event type in `onEvent(...)`.
3. Use a `switch` to handle only the event subtypes you care about.
4. Return `effects().ignore()` for irrelevant event types.
5. Use `messageContext().eventSubject()` for the entity id.
6. If needed, read current entity state via `ComponentClient` before calling another component.
7. Keep downstream writes idempotent because delivery is at least once.

## Snapshot pattern

Add `@SnapshotHandler` only when catch-up from current state is more useful than replaying the full event history.

Rules:
- the snapshot parameter type must match the entity state type
- use the snapshot to initialize downstream state or emit a summary message
- keep snapshot logic compatible with later event processing

## Repository examples

- `ShoppingCartCheckoutConsumer`
  - filters to `CheckedOut`
  - reads current cart state
  - creates a downstream order entity
- `ShoppingCartEventsToTopicConsumer`
  - republishes every shopping-cart event to a broker topic
  - sets `ce-subject` metadata from the entity id
- `ShoppingCartPublicEventsConsumer`
  - transforms internal events into service-stream public events
  - uses `@Acl` because other services subscribe

## Gotchas

- deleting an event sourced entity does not itself emit a consumer callback unless you persist a final deletion event before delete
- do not mutate in-memory consumer fields to track progress
- do not assume one delivery per event
- do not place side effects inside entity `thenReply()` when a consumer is the right downstream boundary

## Review checklist

Before finishing, verify:
- `@Consume.FromEventSourcedEntity` points at the right entity
- `onEvent(...)` accepts the entity event type
- ignored event types are explicit
- `messageContext().eventSubject()` is used when the entity id is needed
- snapshot usage is intentional if `@SnapshotHandler` is present
- downstream component calls are idempotent
