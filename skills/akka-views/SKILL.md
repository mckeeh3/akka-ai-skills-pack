---
name: akka-views
description: Orchestrate Akka Java SDK View work across source selection, table updater design, query design, and integration testing. Use when the task spans more than one part of view implementation.
---

# Akka Views

Use this as the top-level skill for Akka Java SDK view work when a View is already selected as a curated read/evidence surface for one or more backend capabilities.

For broad product, PRD, feature, dashboard, reporting, or search requests, route through `capability-first-backend` and `akka-solution-decomposition` before implementing views. Do not start from raw projections when capability purpose, caller scope, redaction, tenant/customer filters, data-access audit, and exposure surfaces are still unclear.

## Goal

Generate or review view code that is:
- correct for Akka SDK 3.4+
- explicit about its source and update model
- queryable with low-ambiguity result mappings
- easy for AI agents to extend safely
- backed by integration tests

## AI-first and capability-first substrate role

In AI-first SaaS work, use Views for supervision and accountability read models: command centers, active goal/plan lists, agent activity feeds, approval and exception queues, decision-card indexes, policy catalogs, audit search, digest inputs, outcome dashboards, and first-slice admin read models such as UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView.

In capability-first backend design, a View usually realizes a curated read/evidence capability: scoped lists, dashboards, queues, search results, audit lookups, decision evidence, and agent-safe context retrieval. Define the capability contract before coding the View: allowed actors/callers, AuthContext, tenant/customer filters, row redaction, query parameters, pagination/streaming behavior, denial shape, data-access audit expectations, and tests. Do not expose a raw projection just because the source component has data; expose only the query shape the UI, API, workflow, or agent tool is authorized to use.

Views are derived projections, not the source of authority. Keep consequential decisions, policies, traces, approvals, and outcomes in entities, workflows, topics, or authoritative integrations, then project them into views for review, filtering, streaming, and reporting. Generated SaaS view rows and queries must carry tenant/customer scope fields where data is scoped, and endpoints exposing views must filter by authorized AuthContext rather than by frontend state.

Typical AI-first view sources:
- Event Sourced Entities for audit-grade goals, decisions, policies, traces, and outcomes
- Key Value Entities for current-state objects and operational summaries
- Workflows for running plans, approval waits, failures, compensations, and completion status
- Topics or service streams for external signals, trace enrichment, and cross-service reporting

## Required reading before coding

Read these first if present:
- `akka-context/sdk/views.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/reference/views/concepts/result-mapping.html.md`
- `akka-context/reference/views/syntax/order-by.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../docs/capability-first-backend-architecture.md`
- existing project view examples under `src/main/java/**/application/*View.java`
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../../src/main/java/com/example/application/ShoppingCartAuditView.java`
- `../../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`
- `../../src/main/java/com/example/application/DraftCartLifecycleView.java`
- `../../src/main/java/com/example/application/ReviewRequestsByStatusView.java`
- `../../src/main/java/com/example/application/SupervisedExportEvidenceView.java` — capability-first scoped evidence view
- `../../src/main/java/com/example/application/ShoppingCartTopicView.java`
- `../../docs/service-to-service-views.md`
- `../../src/test/java/com/example/application/ShoppingCartsByCheckedOutViewIntegrationTest.java`
- `../../src/test/java/com/example/application/ShoppingCartAuditViewIntegrationTest.java`
- `../../src/test/java/com/example/application/DraftCartsByCheckedOutViewIntegrationTest.java`
- `../../src/test/java/com/example/application/DraftCartLifecycleViewIntegrationTest.java`
- `../../src/test/java/com/example/application/ReviewRequestsByStatusViewIntegrationTest.java`
- `../../src/test/java/com/example/application/SupervisedExportEvidenceViewIntegrationTest.java`
- `../../src/test/java/com/example/application/ShoppingCartTopicViewIntegrationTest.java`

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
- `../../docs/service-to-service-views.md`
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
15. Treat every protected View query as a named read/evidence capability with explicit callers, AuthContext, scope filters, output redaction, audit/data-access expectations, and exposure surfaces.
16. Include tenantId/customerId conditions in protected view queries and stream queries; never expose unscoped lists for tenant/customer data.
17. Design forbidden-access behavior for view endpoints, tool/resource wrappers, and tests: a caller outside scope receives `403`/permission denial or resource-hidden behavior, not rows filtered only in the browser or prompt.
18. For generated SaaS admin views, define first-slice query paths for actor, target user, tenant, customer, role, membership status, invitation status, delivery status, action type, risk, due/expiry time, and time range where relevant.
19. Apply backend authorization and redaction before returning view rows; tests must cover query authorization, cross-scope filtering, redaction, pagination, stale invite/access-review queue correctness, data-access audit/trace completeness, and agent-safe evidence shape when exposed to tools.

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
- Views are read/evidence capability surfaces, not authority boundaries. Enforce authorization at the endpoint/tool/resource/caller boundary before querying and keep scope fields in the view row so the backend can filter mechanically.
- ESE, KVE, and Workflow-backed views have built-in exactly-once deduplication.
- KVE-backed views may skip intermediate state transitions and reflect only the latest guaranteed state.
- Topic-backed views rely on topic delivery and metadata such as `ce-subject`.
- For multi-region scenarios, `updateContext().hasLocalOrigin()`, `originRegion()`, and `selfRegion()` can drive origin-aware filtering.
- For service-to-service eventing sources, also use `docs/service-to-service-views.md` and `akka-context/sdk/consuming-producing.html.md`.
- `ORDER BY` is constrained by the View indexes inferred from the `WHERE` clause. If you need `ORDER BY lowestConsumablePercent, deviceId`, include conditions for both fields, such as `lowestConsumablePercent <= :maxPercent` and `deviceId >= :minDeviceId`.
- Do not use `ORDER BY` on live view queries forwarded to SSE; create a separate unsorted `streamUpdates = true` query for SSE and keep sorted/paginated queries separate.
- View schema/query changes must be treated carefully; incompatible changes may require a new `@Component(id = ...)` and a staged migration.
- Tenant/customer scope columns are part of the security contract for scoped SaaS views; omit them only for deliberately platform-public or aggregate data approved by the foundation spec.
- Admin/audit/access-review views are first-slice foundation requirements in generated SaaS apps, not optional reporting polish. Model AdminAuditView, MembershipView, InvitationView, UserDirectoryView, and AccessReviewQueueView with explicit access paths rather than one broad optional-filter query.

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
- scoped views include tenant/customer filters in queries and endpoint/tool/resource callers enforce AuthContext before querying or streaming
- admin read models include required filters for actor, target user, tenant, customer, role, membership status, invitation status, delivery status, action type, risk, due/expiry time, and time range where relevant
- tests cover no cross-tenant/customer leakage, cross-scope filtering, redaction, pagination, stale invite/access-review queue correctness, audit trace completeness, and forbidden access for protected view APIs

## Response style

When answering coding tasks:
- name the files used or changed
- call out the source type of each view explicitly
- state whether the view stores direct state, a transformed row, or a paginated/query-specific projection
- mention streaming, delete handling, snapshot handling, or origin-aware filtering when those features are present
