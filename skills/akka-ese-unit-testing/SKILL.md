---
name: akka-ese-unit-testing
description: Write unit tests for Akka Java SDK EventSourcedEntity components using EventSourcedTestKit. Use for entity command tests, no-op behavior, multi-event behavior, delete behavior, and TTL assertions.
---

# Akka ESE Unit Testing

Use this skill for unit tests of event sourced entities.

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
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../src/test/java/com/example/application/ShoppingCartEntityTest.java`
- `../../src/test/java/com/example/application/OrderEntityTest.java`
- `../../src/test/java/com/example/application/ExpiringShoppingCartEntityTest.java`

## Test kit rules

Use `EventSourcedTestKit`.

Preferred pattern:
- create a fresh test kit per test
- give explicit entity ids
- invoke the entity method under test with `testKit.method(...).invoke(...)`
- assert reply, error, events, and resulting state as needed

## What to test

For each reusable entity example, cover at least:
1. successful command path
2. validation error path
3. no-op or idempotent path when applicable
4. multi-event path when supported
5. delete behavior when supported
6. TTL behavior when supported

## Useful assertions

Use these `EventSourcedResult` capabilities:
- `getReply()`
- `isError()`
- `getError()`
- `didPersistEvents()`
- `getNextEventOfType(...)`
- `getAllEvents()`
- `getExpireAfter()`

Use these `EventSourcedTestKit` capabilities:
- `getState()`
- `getAllEvents()`
- `isDeleted()` when relevant

## Repository examples

### Standard entity tests
- `ShoppingCartEntityTest`
  - success
  - validation error
  - no-op
  - delete

### Multi-event entity tests
- `OrderEntityTest`
  - one command persisting two events
  - no-op for missing item
  - strongly consistent read pattern

### TTL test
- `ExpiringShoppingCartEntityTest`
  - asserts `Optional.of(Duration.ofDays(30))` from `getExpireAfter()`

## Generated SaaS test set

For generated SaaS entity work, derive tests from the capability contract, not only from entity mechanics:
- authorized success with tenant/customer scoped identifiers or state;
- validation failure and safe reply shape;
- no-op/idempotent duplicate command behavior;
- forbidden or cross-tenant attempts when the entity method is directly exposed or called by endpoints/tools/workflows;
- audit/work-trace expectations for consequential commands, denials, and data access;
- exposure parity for HTTP/gRPC/MCP/tool/surface flows when the entity backs those surfaces.


## Anti-patterns

Avoid:
- reusing one test kit across unrelated test methods
- asserting only replies while ignoring persisted events
- testing integration behavior with unit test APIs
- skipping no-op behavior when the entity claims idempotence

## Review checklist

Before finishing, verify:
- each test has a fresh test kit
- explicit entity ids are used
- persisted events are asserted where important
- no-op behavior checks `didPersistEvents()`
- TTL tests check `getExpireAfter()`
- delete tests check final event and deleted state
