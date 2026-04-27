---
name: akka-view-streaming
description: Design Akka Java SDK View query methods that stream results or stream live updates using QueryStreamEffect and queryStreamResult(). Use when streaming rather than collected query results is the main concern.
---

# Akka View Streaming

Use this skill when the main problem is streaming view query results.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `../../../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`
- `../../../src/main/java/com/example/application/ShoppingCartAuditView.java`
- `../../../src/main/java/com/example/api/DraftCartViewStreamEndpoint.java`
- `../../../src/test/java/com/example/application/DraftCartViewStreamEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/ShoppingCartAuditViewIntegrationTest.java`

## Two streaming modes

### 1. Stream current query result
Use `QueryStreamEffect<T>` plus `queryStreamResult()`.

Repository example:
- `ShoppingCartAuditView#streamByDeleted`

### 2. Stream current result and later updates
Use `@Query(..., streamUpdates = true)` plus `QueryStreamEffect<T>` and `queryStreamResult()`.

Repository examples:
- `DraftCartsByCheckedOutView#continuousCarts`
- `DraftCartViewStreamEndpoint`

## Core rules

1. Return `QueryStreamEffect<T>` for streaming queries.
2. Use `queryStreamResult()` in the method body.
3. Add `streamUpdates = true` only when the stream should stay open for later matching updates.
4. Keep row and query shapes stable so downstream SSE or gRPC endpoints can reuse them.
5. If the stream query uses `ORDER BY`, every ordered column must also appear in the same query's `WHERE` conditions.
6. Test non-updating streams by collecting the stream.
7. Test updating streams through SSE or another consumer that can observe later updates.

## Review checklist

Before finishing, verify:
- the method returns `QueryStreamEffect<T>`
- `streamUpdates = true` is present only for live-update streams
- the query filter matches the intended stream behavior
- any `ORDER BY` columns are also indexed by `WHERE` conditions
- endpoints forwarding the stream preserve offset/reconnect semantics when relevant
