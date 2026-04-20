---
name: akka-ese-notifications
description: Add live notification streams to Akka Java SDK EventSourcedEntity components using NotificationPublisher and NotificationStream. Use when exposing entity updates to subscribers or HTTP SSE endpoints.
---

# Akka ESE Notifications

Use this skill when clients need live updates from an event sourced entity.

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartEntity.java`
- `../../../src/main/java/com/example/api/ShoppingCartEndpoint.java`
- `../../../src/test/java/com/example/application/ShoppingCartEntityTest.java`

## Core pattern

1. Inject `NotificationPublisher<Event>` into the entity.
2. Expose a `NotificationStream<Event>` method.
3. Publish only after successful persistence.
4. Map domain events to API notification records before exposing externally.

## Entity rules

- Notification message type is often the domain event type.
- Publish inside `thenReply(...)`, not before persist.
- Do not rely on notifications for business-critical correctness.
- Notifications are for live updates, not replay of history.

Repository example:
- `ShoppingCartEntity.notifications()`
- `ShoppingCartEntity.persistAndReply(...)`
- `ShoppingCartEntity.deleteAndReply(...)`

## Endpoint rules

When exposing notifications over HTTP:
- use `ComponentClient.notificationStream(...)`
- map domain events to API records
- return SSE via `HttpResponses.serverSentEvents(...)`
- do not leak internal event types directly outside the service unless explicitly intended

Repository example:
- `ShoppingCartEndpoint.notifications(...)`
- `ShoppingCartEndpoint.toNotification(...)`

## Testing guidance

At minimum, test that published messages happen after successful command handling.

Repository example:
- `ShoppingCartEntityTest`

This repository uses a stub `NotificationPublisher` to capture published events and assert them.

## Anti-patterns

Avoid:
- publishing before persist succeeds
- using notifications instead of durable business workflows
- exposing raw domain notifications directly as public API when an API shape is preferable
