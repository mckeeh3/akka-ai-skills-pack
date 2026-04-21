---
name: akka-kve-notifications
description: Add live notification streams to Akka Java SDK KeyValueEntity components using NotificationPublisher and NotificationStream. Use when exposing entity updates to subscribers or HTTP SSE endpoints.
---

# Akka KVE Notifications

Use this skill when clients need live updates from a key value entity.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `../../../src/main/java/com/example/application/DraftCartEntity.java`
- `../../../src/main/java/com/example/api/DraftCartEndpoint.java`
- `../../../src/test/java/com/example/application/DraftCartEntityTest.java`

## Core pattern

1. Inject `NotificationPublisher<Notification>` into the entity.
2. Expose a `NotificationStream<Notification>` method.
3. Publish only after successful state persistence.
4. Map domain notifications to API notification records before exposing externally.

## Entity rules

- Notification message type can be a simple record or sealed interface.
- Publish inside `thenReply(...)`, not before `updateState` or `deleteEntity` succeeds.
- Do not rely on notifications for business-critical correctness.
- Notifications are for live updates, not historical replay.

Repository example:
- `DraftCartEntity.notifications()`
- `DraftCartEntity.addItem(...)`
- `DraftCartEntity.delete(...)`

## Endpoint rules

When exposing notifications over HTTP:
- use `ComponentClient.notificationStream(...)`
- map domain notifications to API records
- return SSE via `HttpResponses.serverSentEvents(...)`
- do not leak internal notification types directly outside the service unless explicitly intended

Repository example:
- `DraftCartEndpoint.notifications(...)`
- `DraftCartEndpoint.toNotification(...)`

## Testing guidance

At minimum, test that published messages happen after successful command handling.

Repository example:
- `DraftCartEntityTest`

This repository uses a stub `NotificationPublisher` to capture published messages and assert them.

## Anti-patterns

Avoid:
- publishing before state update succeeds
- using notifications instead of durable business workflows
- exposing raw internal notifications directly as public API when an API shape is preferable
