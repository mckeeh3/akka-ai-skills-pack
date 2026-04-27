---
name: akka-view-testing
description: Write Akka Java SDK View integration tests using TestKitSupport, mocked incoming messages, componentClient.forView(), and Awaitility. Use for verifying projections and eventual-consistency query results.
---

# Akka View Testing

Use this skill for view integration tests.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `../../../src/test/java/com/example/application/ShoppingCartsByCheckedOutViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/DraftCartsByCheckedOutViewIntegrationTest.java`

## Test kit rules

Use `TestKitSupport` and configure mocked incoming messages for the source component type.

Patterns:
- Event sourced source: `withEventSourcedEntityIncomingMessages(SourceEntity.class)`
- Key value source: `withKeyValueEntityIncomingMessages(SourceEntity.class)`
- Workflow source: `withWorkflowIncomingMessages(WorkflowClass.class)`
- Topic source: `withTopicIncomingMessages("topic-name")`

Then:
1. get the source-specific incoming messages handle from `testKit`
2. publish source updates with explicit entity ids
3. query the view through `componentClient.forView()`
4. wrap assertions in `Awaitility.await()` because view updates are eventually consistent

## What to test

For each reusable view example, cover at least:
1. source updates populate the view
2. the query returns the expected rows
3. transformed fields are mapped correctly
4. delete or move-out-of-query behavior when relevant
5. paginated result mapping when the view exposes a paginated query
6. at least one invocation of every query method, including sorted and paginated queries, so unsupported query shapes such as invalid `ORDER BY` indexes fail during tests

## Repository examples

### Event sourced view tests
- `ShoppingCartsByCheckedOutViewIntegrationTest`
  - publishes shopping cart events
  - queries checked-out carts
  - verifies delete event removes the row
- `ShoppingCartAuditViewIntegrationTest`
  - verifies logical delete event handling
  - verifies `QueryStreamEffect` collection for current rows

### Key value view tests
- `DraftCartsByCheckedOutViewIntegrationTest`
  - publishes draft cart state snapshots
  - queries checked-out carts
  - verifies paginated response mapping
- `DraftCartLifecycleViewIntegrationTest`
  - verifies logical delete via `@DeleteHandler`

### Workflow view test
- `ReviewRequestsByStatusViewIntegrationTest`
  - publishes workflow state snapshots
  - queries workflow-derived rows

### Topic view test
- `ShoppingCartTopicViewIntegrationTest`
  - publishes topic messages with `ce-subject` metadata
  - verifies ignored and deleted message behavior

## Anti-patterns

Avoid:
- asserting view results immediately after publishing source updates
- querying the source entity instead of the view under test
- using endpoint integration patterns when the goal is direct view testing
- forgetting explicit entity ids on published source updates

## Review checklist

Before finishing, verify:
- `TestKit.Settings` is configured for the correct source type
- source updates are published through the matching incoming messages API
- topic tests include `ce-subject` metadata when needed
- assertions run inside `Awaitility.await()`
- queries use `componentClient.forView()`
- every View query method is invoked at least once, especially queries with `ORDER BY`, `OFFSET`, or `LIMIT`
- tests assert transformed row fields, not only result size
