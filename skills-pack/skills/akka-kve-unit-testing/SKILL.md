---
name: akka-kve-unit-testing
description: Write unit tests for Akka Java SDK KeyValueEntity components using KeyValueEntityTestKit. Use for entity command tests, no-op behavior, delete behavior, and TTL assertions.
---

# Akka KVE Unit Testing

Use this skill for unit tests of key value entities.

## Generated SaaS input contract

For generated full-stack AI-first SaaS implementation work, apply `../references/generated-saas-input-contract.md` before coding. If the selected task lacks the required workstream/capability/AuthContext/surface/trace/test contract and does not explicitly defer it, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

## Test kit rules

Use `KeyValueEntityTestKit`.

Preferred pattern:
- create a fresh test kit per test
- give explicit entity ids
- invoke the entity method under test with `testKit.method(...).invoke(...)`
- assert reply, error, update/delete flags, TTL, and resulting state as needed

## What to test

For each reusable entity example, cover at least:
1. successful command path
2. validation error path
3. no-op or idempotent path when applicable
4. delete behavior when supported
5. TTL behavior when supported

## Useful assertions

Use these `KeyValueEntityResult` capabilities:
- `getReply()`
- `isError()`
- `getError()`
- `stateWasUpdated()`
- `stateWasDeleted()`
- `getExpireAfter()`

Use these `KeyValueEntityTestKit` capabilities:
- `getState()`
- `isDeleted()` when relevant

## Repository examples

### Standard entity tests
- `DurableIdentityRepositoryEntityTest`
  - success
  - validation error
  - no-op
  - delete

### Downstream/internal entity tests
- a domain-specific key value entity test
  - create
  - idempotent no-op
  - strongly consistent read pattern
  - replication filter command shape

### TTL test
- `DurableNotificationRepositoryEntityTest`
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
- asserting only replies while ignoring state update/delete flags
- testing integration behavior with unit test APIs
- skipping no-op behavior when the entity claims idempotence

## Review checklist

Before finishing, verify:
- each test has a fresh test kit
- explicit entity ids are used
- update/delete flags are asserted where important
- no-op behavior checks `stateWasUpdated()` is false
- TTL tests check `getExpireAfter()`
- delete tests check deleted state
