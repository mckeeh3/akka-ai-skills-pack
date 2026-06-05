---
name: akka-view-from-event-sourced-entity
description: Implement Akka Java SDK Views that consume EventSourcedEntity events using TableUpdater.onEvent(...), rowState(), and deleteRow(). Use when building projections from persisted events.
---

# Akka View from Event Sourced Entity

Use this skill when the source of the view is an `EventSourcedEntity`.

Capability-first framing: use ESE-backed Views for read/evidence capabilities that need audit-grade event history projected into scoped, redacted query rows such as decision evidence, policy history, approval queues, trace search, or lifecycle reports. Keep the entity as the authority; the View is the query/evidence surface.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/event-sourced-entities.html.md`
- `../examples/akka-components/src/main/java/ai/first/application/ShoppingCartsByCheckedOutView.java`
- `../examples/akka-components/src/main/java/ai/first/application/ShoppingCartAuditView.java`
- `../examples/akka-components/src/test/java/ai/first/application/ShoppingCartsByCheckedOutViewIntegrationTest.java`
- `../examples/akka-components/src/test/java/ai/first/application/ShoppingCartAuditViewIntegrationTest.java`
- `../docs/capability-first-backend-architecture.md`

## Source-specific rules

1. Subscribe with `@Consume.FromEventSourcedEntity(EntityClass.class)`.
2. Implement `onEvent(Event event)` in the `TableUpdater`.
3. Handle the sealed event supertype when possible.
4. Use `rowState()` to evolve the current row incrementally.
5. Use `updateContext().eventSubject()` to recover the entity id for first-row creation.
6. Model entity deletion explicitly in `onEvent(...)` when the delete is represented as a domain event.
7. If the entity delete signal itself should be customized, add `@DeleteHandler`.
8. If snapshots are relevant, add `@SnapshotHandler` with the entity state type.
9. Preserve capability scope fields, correlation ids, audit references, and redaction-ready fields in the projected row when the read/evidence capability requires them.

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

## Generated SaaS view contract

For generated SaaS read/evidence capabilities, require:
- tenant/customer scoped row keys and query filters aligned with the selected `AuthContext`;
- redacted DTO rows for the chosen UI/API/MCP/agent-tool consumers, not raw state dumps;
- stable surface payload or evidence-bundle mapping when used by structured surfaces;
- data-access audit/work-trace requirements at the query or endpoint boundary;
- tests for authorized query, forbidden/cross-tenant query, redaction, projection update/delete behavior, and surface/API/tool consumers where exposed.


## Review checklist

Before finishing, verify:
- the updater uses `@Consume.FromEventSourcedEntity(...)`
- `onEvent(...)` handles all relevant event subtypes
- first-row creation handles missing `rowState()` safely
- delete events or delete callbacks remove or explicitly retain rows as intended
- snapshot handling uses the entity state type when present
- queries index by a field other than entity id when that is the purpose of the view
- protected capability queries include tenant/customer scope and expose only redacted evidence fields appropriate for the caller
- non-SSE `ORDER BY` columns also appear in the same query's `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
