---
name: akka-ese-unit-testing
description: Write unit tests for Akka Java SDK EventSourcedEntity components using EventSourcedTestKit. Use for entity command tests, no-op behavior, multi-event behavior, delete behavior, and TTL assertions.
---

# Akka ESE Unit Testing

Use this skill for unit tests of event sourced entities.

## Lifecycle and compile boundary

Testing work belongs to the build/compile phase unless the selected task is explicitly runtime verification or runtime-validation reconciliation. Use this skill to prove the component-specific mechanics and the declared worker/harness/actor-adapter/governed-tool/capability path; do not widen a component-testing task into unrelated planning, product repair, or runtime-validation failure triage. For feature-bearing generated SaaS work, passing component tests can support `manual-ready`; `runtime-ready` still requires the real local API/UI/agent path, provider/fail-closed evidence where relevant, and reconciliation of runtime-validation findings through `../docs/runtime-validation-reconciliation.md`.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `../docs/app-development-lifecycle.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/runtime-validation-reconciliation.md` when tests are part of a runtime-validation readiness claim or remediation loop
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../docs/akka-entity-testing-shared-patterns.md`

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
- `AgentDefinitionEntityTest`
  - success
  - validation error
  - no-op
  - delete

### Multi-event entity tests
- target-project ESE tests for commands that persist multiple events
  - one command persisting two events
  - no-op for missing item
  - strongly consistent read pattern
- current curated governed-document tests: `../examples/akka-components/src/test/java/ai/first/application/foundation/agent/GovernedDocumentEntityTest.java`

### TTL test
- target-project TTL tests for entities that configure `expireAfter(...)`
  - assert the expected `Optional<Duration>` from `getExpireAfter()`
- the current curated tree does not include a dedicated expiring entity fixture

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

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
