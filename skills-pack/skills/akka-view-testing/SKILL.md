---
name: akka-view-testing
description: Write Akka Java SDK View integration tests using TestKitSupport, mocked incoming messages, componentClient.forView(), and Awaitility. Use for verifying projections and eventual-consistency query results.
---

# Akka View Testing

Use this skill for view integration tests.

For capability-first read/evidence capabilities, test the View projection and the protected exposure path that uses it. Direct View tests verify projection/query mechanics; endpoint, tool, MCP resource, or workflow tests verify AuthContext, tenant/customer scope, redaction, denial shape, and audit/data-access trace obligations.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/views.html.md`
- `../docs/capability-first-backend-architecture.md`

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
6. at least one invocation of every query method, including sorted, paginated, and SSE-backed stream queries, so unsupported query shapes such as invalid non-SSE `ORDER BY` indexes or `ORDER BY` on SSE view queries fail during tests
7. tenant/customer scope filtering for protected read capabilities, including no leakage for wrong tenant/customer ids
8. caller-safe redaction for rows exposed to browsers, APIs, MCP resources, or agent tools
9. forbidden/permission-denied behavior and audit/data-access trace creation in the protected wrapper that exposes the View

## Repository examples

### Current curated view references
- `../examples/akka-components/src/main/java/ai/first/application/coreapp/useradmin/UserDirectoryView.java`
  - user directory projection/query shape
  - pair with target-project view integration tests for mocked updates and eventual consistency
- `../examples/akka-components/src/main/java/ai/first/application/foundation/audit/AdminAuditView.java`
  - audit query/read-model shape
  - pair with target-project tests for stream/delete/query behavior

The current curated tree does not include standalone `*ViewIntegrationTest` classes. Treat event-sourced, key-value, and workflow view test bullets above as target-project test shapes, not repository class names.

### Topic view test
- target-project topic view integration test; the current curated tree does not include a standalone topic-view test class
  - publishes topic messages with `ce-subject` metadata
  - verifies ignored and deleted message behavior

## Anti-patterns

Avoid:
- asserting view results immediately after publishing source updates
- querying the source entity instead of the view under test
- using endpoint integration patterns when the goal is direct view testing
- forgetting explicit entity ids on published source updates
- treating direct View projection tests as sufficient for protected capability exposure without separate auth/scope/redaction/audit tests

## Review checklist

Before finishing, verify:
- `TestKit.Settings` is configured for the correct source type
- source updates are published through the matching incoming messages API
- topic tests include `ce-subject` metadata when needed
- assertions run inside `Awaitility.await()`
- queries use `componentClient.forView()`
- every View query method is invoked at least once, especially queries with `ORDER BY`, `OFFSET`, `LIMIT`, or SSE-backed streaming
- SSE-backed view stream queries contain no `ORDER BY`
- tests assert transformed row fields, not only result size
- protected read capability tests cover authorized, forbidden, cross-tenant/customer, redacted, and audited access through the selected exposure surface
