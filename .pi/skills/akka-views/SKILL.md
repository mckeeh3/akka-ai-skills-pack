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
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- existing project view examples under `src/main/java/**/application/*View.java`
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/ShoppingCartsByCheckedOutView.java`
- `../../../src/main/java/com/example/application/DraftCartsByCheckedOutView.java`
- `../../../src/test/java/com/example/application/ShoppingCartsByCheckedOutViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartsByCheckedOutViewIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-view-from-event-sourced-entity`
  - views built from ESE events with `onEvent(...)`
- `akka-view-from-key-value-entity`
  - views built from KVE state changes with `onUpdate(...)`
- `akka-view-query-patterns`
  - wrapper result records, aliases, pagination, and query shape
- `akka-view-testing`
  - `TestKitSupport`, mocked incoming messages, and `Awaitility`

If the source is a Workflow or Topic, use this top-level skill plus `akka-context/sdk/views.html.md` and the reference docs directly until a focused local skill exists.

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
10. Remember eventual consistency: assert view results with `Awaitility`.

## Decision guide

Choose one of these modes before coding:

### 1. Direct state indexing view
Use when the source emits the full latest state and the view mainly adds alternate query access.

Typical sources:
- Key Value Entity
- Workflow

Repository example:
- `DraftCartsByCheckedOutView`

### 2. Event-driven projection view
Use when the source emits domain events and the view must rebuild row state incrementally.

Typical source:
- Event Sourced Entity

Repository example:
- `ShoppingCartsByCheckedOutView`

### 3. Query-shaping task
Use when the main problem is result mapping, aliases, pagination, or streaming.

Repository example:
- `DraftCartsByCheckedOutView#getCartsPage`

## Final review checklist

Before finishing, verify:
- `@Component(id = ...)` is present and stable
- the updater source annotation matches the source component type
- ESE uses `onEvent(...)`, KVE/Workflow uses `onUpdate(...)`
- query response records match the `SELECT` shape and aliases exactly
- multi-row queries use a wrapper field alias such as `AS carts`
- delete behavior is explicit when needed
- tests publish incoming messages from the correct source type
- tests use `Awaitility` instead of assuming immediate view updates

## Response style

When answering coding tasks:
- name the files used or changed
- call out the source type of each view explicitly
- state whether the view stores direct state, a transformed row, or a paginated/query-specific projection
