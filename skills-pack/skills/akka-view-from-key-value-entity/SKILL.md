---
name: akka-view-from-key-value-entity
description: Implement Akka Java SDK Views that consume KeyValueEntity state changes using TableUpdater.onUpdate(...). Use when indexing current state snapshots by alternate query fields.
---

# Akka View from Key Value Entity

Use this skill when the source of the view is a `KeyValueEntity`.

Capability-first framing: use KVE-backed Views for current-state read/evidence capabilities such as directories, settings/search lists, operational summaries, and dashboards where the latest state is sufficient. Keep command authority in the entity/caller; the View provides scoped and redacted query access.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/key-value-entities.html.md`
- `../docs/capability-first-backend-architecture.md`

## Source-specific rules

1. Subscribe with `@Consume.FromKeyValueEntity(EntityClass.class)`.
2. Implement `onUpdate(State state)` in the `TableUpdater`.
3. Treat the incoming value as the latest state snapshot, not an incremental event.
4. Derive a view row directly from the current state.
5. Use `updateContext().eventSubject()` for the source entity id.
6. Add `@DeleteHandler` only when you need custom delete behavior.
7. Remember that intermediate state transitions may be skipped; only the latest state is guaranteed.
8. Include tenant/customer scope, status, ownership, and redaction-ready fields needed by the capability's authorized query paths.

## Recommended implementation pattern

Prefer a transformed summary row when the view only needs a subset of fields.

Pattern:
- define a compact row record inside the view
- compute that row from the latest state in `onUpdate(...)`
- query using wrapper records such as `List<Row> rows`

Repository examples:
- `UserDirectoryView`
- `AdminAuditView`
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

## Generated SaaS view contract

For generated SaaS read/evidence capabilities, require:
- tenant/customer scoped row keys and query filters aligned with the selected `AuthContext`;
- redacted DTO rows for the chosen UI/API/MCP/agent-tool consumers, not raw state dumps;
- stable surface payload or evidence-bundle mapping when used by structured surfaces;
- data-access audit/work-trace requirements at the query or endpoint boundary;
- tests for authorized query, forbidden/cross-tenant query, redaction, projection update/delete behavior, and surface/API/tool consumers where exposed.


## Review checklist

Before finishing, verify:
- the updater uses `@Consume.FromKeyValueEntity(...)`
- `onUpdate(...)` maps from the latest full state to the row type
- the row type contains only fields needed by the queries
- protected query rows carry scope fields and omit/redact fields not approved for the selected exposure surface
- query wrappers and aliases match exactly
- non-SSE `ORDER BY` columns also appear in the same query's `WHERE` conditions
- view queries exposed as SSE do not include `ORDER BY`
- tests simulate updates with key value incoming messages
- delete handlers are present only when custom delete behavior is required
