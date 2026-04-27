---
name: akka-view-from-key-value-entity
description: Implement Akka Java SDK Views that consume KeyValueEntity state changes using TableUpdater.onUpdate(...). Use when indexing current state snapshots by alternate query fields.
---

# Akka View from Key Value Entity

Use this skill when the source of the view is a `KeyValueEntity`.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/key-value-entities.html.md`
- `../../../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`
- `../../../src/main/java/com/example/application/DraftCartLifecycleView.java`
- `../../../src/test/java/com/example/application/DraftCartsByCheckedOutViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartLifecycleViewIntegrationTest.java`

## Source-specific rules

1. Subscribe with `@Consume.FromKeyValueEntity(EntityClass.class)`.
2. Implement `onUpdate(State state)` in the `TableUpdater`.
3. Treat the incoming value as the latest state snapshot, not an incremental event.
4. Derive a view row directly from the current state.
5. Use `updateContext().eventSubject()` for the source entity id.
6. Add `@DeleteHandler` only when you need custom delete behavior.
7. Remember that intermediate state transitions may be skipped; only the latest state is guaranteed.

## Recommended implementation pattern

Prefer a transformed summary row when the view only needs a subset of fields.

Pattern:
- define a compact row record inside the view
- compute that row from the latest state in `onUpdate(...)`
- query using wrapper records such as `List<Row> carts`

Repository examples:
- `DraftCartsByCheckedOutView`
- `DraftCartLifecycleView`
  - demonstrates `@DeleteHandler` for logical deletion

## Delete guidance

Default behavior:
- when the entity is deleted, the matching view row is deleted automatically

Add `@DeleteHandler` only when you need one of these:
- logical deletion instead of physical deletion
- cleanup that depends on current `rowState()`
- a deliberately explicit pattern example

## Anti-patterns

Never:
- use `onEvent(Event)` for a KVE-backed view
- assume every intermediate state write will be observed by the view
- return `QueryEffect<List<Row>>` for multi-row results; use a wrapper record instead
- put Akka effect logic in the domain state type

## Review checklist

Before finishing, verify:
- the updater uses `@Consume.FromKeyValueEntity(...)`
- `onUpdate(...)` maps from the latest full state to the row type
- the row type contains only fields needed by the queries
- query wrappers and aliases match exactly
- `ORDER BY` columns also appear in the same query's `WHERE` conditions
- tests simulate updates with key value incoming messages
- delete handlers are present only when custom delete behavior is required
