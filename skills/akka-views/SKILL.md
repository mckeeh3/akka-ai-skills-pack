---
name: akka-views
description: Orchestrate Akka Java SDK View work across source selection, table updater design, query design, and integration testing. Use when the task spans more than one part of view implementation.
---

# Akka Views

Use this as the top-level skill for Akka Java SDK view work.

## Goal

Generate or review view code that is:
- correct for Akka SDK 3.4+
- explicit about its source and update model
- queryable with low-ambiguity result mappings
- easy for AI agents to extend safely
- backed by integration tests

## Required reading before coding

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/reference/views/concepts/result-mapping.html.md`
- `akka-context/reference/views/syntax/order-by.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project view examples under `src/main/java/**/application/*View.java`
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../../../src/main/java/com/example/application/ShoppingCartAuditView.java`
- `../../../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`
- `../../../src/main/java/com/example/application/DraftCartLifecycleView.java`
- `../../../src/main/java/com/example/application/ReviewRequestsByStatusView.java`
- `../../../src/main/java/com/example/application/ShoppingCartTopicView.java`
- `../../../docs/service-to-service-views.md`
- `../../../src/test/java/com/example/application/ShoppingCartsByCheckedOutViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/ShoppingCartAuditViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartsByCheckedOutViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartLifecycleViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/ReviewRequestsByStatusViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/ShoppingCartTopicViewIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-view-from-event-sourced-entity`
  - views built from ESE events with `onEvent(...)`
- `akka-view-from-key-value-entity`
  - views built from KVE state changes with `onUpdate(...)`
- `akka-view-from-workflow`
  - views built from workflow state changes with `onUpdate(...)`
- `akka-view-from-topic`
  - views built from topic messages with `ce-subject` metadata
- `akka-view-from-service-stream`
  - views built from public streams of another Akka service
- `akka-view-query-patterns`
  - wrapper result records, aliases, pagination, and query shape
- `akka-view-streaming`
  - `QueryStreamEffect`, `queryStreamResult()`, and `streamUpdates = true`
- `akka-view-testing`
  - `TestKitSupport`, mocked incoming messages, and `Awaitility`

If the source is another Akka service via service-to-service eventing, use this top-level skill plus:
- `akka-view-from-service-stream`
- `../../../docs/service-to-service-views.md`
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`

## Default package layout

Use:
- `com.<org>.<app>.domain`
- `com.<org>.<app>.application`
- `com.<org>.<app>.api`

Rules:
- view classes belong in `application`
- view row and query response records may live as nested records in the view when they are view-specific
- domain records stay in `domain` when reused outside the view
- endpoints that expose views belong in `api`

## Core rules

1. A view extends `View` and has a stable `@Component(id = "...")`.
2. Source subscriptions live on a static inner `TableUpdater` class, not on the view class itself.
3. ESE-backed views use `onEvent(Event)`.
4. KVE-backed and Workflow-backed views use `onUpdate(State)`.
5. Query methods return `QueryEffect<T>` or `QueryStreamEffect<T>`.
6. Multi-row queries should usually return a wrapper record and use `SELECT * AS <field>`.
7. Use `rowState()` for incremental updates and `updateContext().eventSubject()` for the source id.
8. Use `effects().updateRow(...)`, `effects().deleteRow()`, or `effects().ignore()` explicitly.
9. View tests should publish incoming updates and query through `componentClient.forView()`.
10. Streaming queries use `QueryStreamEffect` with `queryStreamResult()`.
11. Remember eventual consistency: assert view results with `Awaitility`.
12. For every non-SSE query, each `ORDER BY` column must also be present in that query's `WHERE` conditions; otherwise Akka may reject the query with `AK-00101`.
13. For view queries exposed as SSE with `serverSentEventsForView(...)`, do **not** generate `ORDER BY`; SSE view events are delivered in created/event order.
14. Prefer separate query methods over optional-filter `OR` expressions so each access path has explicit indexed fields.

## Decision guide

Choose one of these modes before coding:

### 1. Direct state indexing view
Use when the source emits the full latest state and the view mainly adds alternate query access.

Typical sources:
- Key Value Entity
- Workflow

Repository examples:
- `DraftCartsByCheckedOutView`
- `ReviewRequestsByStatusView`

### 2. Event-driven projection view
Use when the source emits domain events and the view must rebuild row state incrementally.

Typical sources:
- Event Sourced Entity
- Topic

Repository examples:
- `ShoppingCartsByCheckedOutView`
- `ShoppingCartTopicView`

### 3. Query-shaping or streaming task
Use when the main problem is result mapping, aliases, pagination, streaming current results, or streaming updates.

Repository examples:
- `DraftCartsByCheckedOutView#getCartsPage`
- `DraftCartsByCheckedOutView#streamCarts`
- `ShoppingCartAuditView#streamByDeleted`

## Advanced semantics to remember

- Views are eventually consistent; do not assume entity writes are visible immediately.
- ESE, KVE, and Workflow-backed views have built-in exactly-once deduplication.
- KVE-backed views may skip intermediate state transitions and reflect only the latest guaranteed state.
- Topic-backed views rely on topic delivery and metadata such as `ce-subject`.
- For multi-region scenarios, `updateContext().hasLocalOrigin()`, `originRegion()`, and `selfRegion()` can drive origin-aware filtering.
- For service-to-service eventing sources, also use `docs/service-to-service-views.md` and `akka-context/sdk/consuming-producing.html.md`.
- `ORDER BY` is constrained by the View indexes inferred from the `WHERE` clause. If you need `ORDER BY lowestConsumablePercent, deviceId`, include conditions for both fields, such as `lowestConsumablePercent <= :maxPercent` and `deviceId >= :minDeviceId`.
- Do not use `ORDER BY` on live view queries forwarded to SSE; create a separate unsorted `streamUpdates = true` query for SSE and keep sorted/paginated queries separate.
- View schema/query changes must be treated carefully; incompatible changes may require a new `@Component(id = ...)` and a staged migration.

## Final review checklist

Before finishing, verify:
- `@Component(id = ...)` is present and stable
- the updater source annotation matches the source component type
- ESE/Topic uses `onEvent(...)`, KVE/Workflow uses `onUpdate(...)`
- query response records match the `SELECT` shape and aliases exactly
- multi-row queries use a wrapper field alias such as `AS carts`
- every non-SSE `ORDER BY` column appears in the same query's `WHERE` conditions
- SSE-backed view queries do not contain `ORDER BY`
- optional filters are split into separate query methods rather than `OR` branches
- delete behavior is explicit when needed
- snapshot handling is present when an ESE-backed view should start from snapshots
- tests publish incoming messages from the correct source type
- tests use `Awaitility` instead of assuming immediate view updates

## Response style

When answering coding tasks:
- name the files used or changed
- call out the source type of each view explicitly
- state whether the view stores direct state, a transformed row, or a paginated/query-specific projection
- mention streaming, delete handling, snapshot handling, or origin-aware filtering when those features are present
