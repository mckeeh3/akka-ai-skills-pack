---
name: akka-kve-unit-testing
description: Write unit tests for Akka Java SDK KeyValueEntity components using KeyValueEntityTestKit. Use for entity command tests, no-op behavior, delete behavior, and TTL assertions.
---

# Akka KVE Unit Testing

Use this skill for unit tests of key value entities.

## Required reading

Read these first if present:
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/test/java/com/example/application/DraftCartEntityTest.java`
- `../../../src/test/java/com/example/application/PurchaseOrderEntityTest.java`
- `../../../src/test/java/com/example/application/ExpiringDraftCartSessionEntityTest.java`

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
- `DraftCartEntityTest`
  - success
  - validation error
  - no-op
  - delete

### Downstream/internal entity tests
- `PurchaseOrderEntityTest`
  - create
  - idempotent no-op
  - strongly consistent read pattern
  - replication filter command shape

### TTL test
- `ExpiringDraftCartSessionEntityTest`
  - asserts `Optional.of(Duration.ofDays(30))` from `getExpireAfter()`

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
