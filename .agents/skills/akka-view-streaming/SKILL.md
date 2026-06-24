---
name: akka-view-streaming
description: Design Akka Java SDK View query methods that stream results or stream live updates using QueryStreamEffect and queryStreamResult(). Use when streaming rather than collected query results is the main concern.
---

# Akka View Streaming

Use this skill when the main problem is streaming view query results.

Capability-first framing: a streaming View query is a live read/evidence capability surface. Define who may subscribe, the AuthContext and tenant/customer scope, reconnect/cancellation behavior, row redaction, audit/data-access trace expectations, and whether the stream is browser SSE, gRPC, MCP/resource-like, agent evidence, or internal.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `../docs/capability-first-backend-architecture.md`

## Two streaming modes

### 1. Stream current query result
Use `QueryStreamEffect<T>` plus `queryStreamResult()`.

Repository example:
- `AdminAuditView#streamByDeleted`

### 2. Stream current result and later updates
Use `@Query(..., streamUpdates = true)` plus `QueryStreamEffect<T>` and `queryStreamResult()`.

Repository examples:
- `UserDirectoryView#continuousRows`
- a domain-specific workstream log stream endpoint

## Core rules

1. Return `QueryStreamEffect<T>` for streaming queries.
2. Use `queryStreamResult()` in the method body.
3. Add `streamUpdates = true` only when the stream should stay open for later matching updates.
4. Keep row and query shapes stable so downstream SSE or gRPC endpoints can reuse them.
5. For any view query that will be exposed as SSE with `serverSentEventsForView(...)`, do **not** include `ORDER BY`; SSE view events are delivered in created/event order and ordered queries are not supported for that use.
6. If a non-SSE stream query uses `ORDER BY`, every ordered column must also appear in the same query's `WHERE` conditions.
7. Test non-updating streams by collecting the stream.
8. Test updating streams through SSE or another consumer that can observe later updates.
9. Protected streams must include tenant/customer scope filters in the query or wrapper and must enforce authorization before opening the stream.
10. Record data-access/audit traces where the capability contract requires auditable streaming evidence access.

## SSE-backed view stream pattern

Use a dedicated live-update query without `ORDER BY` for SSE endpoints:

```sql
SELECT *
FROM workstream_logs_by_checked_out
WHERE checkedOut = :checkedOut
```

Keep any sorted list or paginated query as a separate `QueryEffect` method. Do not reuse sorted `ORDER BY` queries for SSE.

Do not implement a claimed SSE view response by returning a finite `List` or `QueryEffect` from the View and wrapping it as a one-shot response. For view-backed SSE, the View must expose a `QueryStreamEffect<T>` method that calls `queryStreamResult()`; add `streamUpdates = true` when subscribers must receive later matching updates.

## Review checklist

Before finishing, verify:
- the method returns `QueryStreamEffect<T>`
- `streamUpdates = true` is present only for live-update streams
- the query filter matches the intended stream behavior
- view queries forwarded to SSE do not contain `ORDER BY`
- any non-SSE `ORDER BY` columns are also indexed by `WHERE` conditions
- endpoints forwarding the stream preserve offset/reconnect semantics when relevant
- no endpoint or adapter wraps a finite `List`/`QueryEffect` as a substitute for view-backed SSE
- protected stream exposure tests cover authorized subscription, forbidden/cross-scope denial, redacted rows, and audit/data-access trace behavior
