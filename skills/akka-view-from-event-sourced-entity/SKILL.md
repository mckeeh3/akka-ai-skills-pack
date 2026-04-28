---
name: akka-view-from-event-sourced-entity
description: Implement Akka Java SDK Views that consume EventSourcedEntity events using TableUpdater.onEvent(...), rowState(), and deleteRow(). Use when building projections from persisted events.
---

# Akka View from Event Sourced Entity

Use this skill when the source of the view is an `EventSourcedEntity`.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/event-sourced-entities.html.md`
- `../../../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../../../src/main/java/com/example/application/ShoppingCartAuditView.java`
- `../../../src/test/java/com/example/application/ShoppingCartsByCheckedOutViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/ShoppingCartAuditViewIntegrationTest.java`

## Source-specific rules

1. Subscribe with `@Consume.FromEventSourcedEntity(EntityClass.class)`.
2. Implement `onEvent(Event event)` in the `TableUpdater`.
3. Handle the sealed event supertype when possible.
4. Use `rowState()` to evolve the current row incrementally.
5. Use `updateContext().eventSubject()` to recover the entity id for first-row creation.
6. Model entity deletion explicitly in `onEvent(...)` when the delete is represented as a domain event.
7. If the entity delete signal itself should be customized, add `@DeleteHandler`.
8. If snapshots are relevant, add `@SnapshotHandler` with the entity state type.

## Recommended implementation pattern

Use one of these two patterns:

### 1. Reuse pure domain event application logic
Best when the domain already has a replay-safe event handler.

Pattern:
- initialize an empty row when `rowState()` is `null`
- delegate state evolution to a pure helper
- map a delete event to `effects().deleteRow()`

Repository examples:
- `ShoppingCartsByCheckedOutView`
- `ShoppingCartAuditView`
  - demonstrates `@SnapshotHandler`
  - demonstrates `effects().ignore()` for an event not projected directly
  - demonstrates custom `@DeleteHandler`

### 2. Maintain a smaller projection row
Best when the query only needs a subset of fields or a transformed shape.

Pattern:
- store a dedicated row record in the view
- update only the fields needed for the queries
- avoid pulling in unnecessary domain structure

## Anti-patterns

Never:
- use `onUpdate(State)` for an ESE-backed view
- put `@Consume` on the `View` class instead of the `TableUpdater`
- ignore delete events when the entity emits an explicit delete event that should remove the row
- duplicate business decision logic that belongs in the entity command path

## Review checklist

Before finishing, verify:
- the updater uses `@Consume.FromEventSourcedEntity(...)`
- `onEvent(...)` handles all relevant event subtypes
- first-row creation handles missing `rowState()` safely
- delete events or delete callbacks remove or explicitly retain rows as intended
- snapshot handling uses the entity state type when present
- queries index by a field other than entity id when that is the purpose of the view
- non-SSE `ORDER BY` columns also appear in the same query's `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
