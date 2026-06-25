---
name: akka-kve-unit-testing
description: Write unit tests for Akka Java SDK KeyValueEntity components using KeyValueEntityTestKit. Use for entity command tests, no-op behavior, delete behavior, and TTL assertions.
---

# Akka KVE Unit Testing

Use this skill for unit tests of key value entities.

## Lifecycle and compile boundary

Testing work belongs to the build/compile phase unless the selected task is explicitly runtime verification or manual-test reconciliation. Use this skill to prove the component-specific mechanics and the declared worker/harness/actor-adapter/governed-tool/capability path; do not widen a component-testing task into unrelated planning, product repair, or manual-failure triage. For feature-bearing generated SaaS work, passing component tests can support `manual-ready`; `runtime-ready` still requires the real local API/UI/agent path, provider/fail-closed evidence where relevant, and reconciliation of manual findings through `../docs/manual-test-reconciliation.md`.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `../docs/app-development-lifecycle.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/manual-test-reconciliation.md` when tests are part of a manual/runtime readiness claim or remediation loop
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../docs/akka-entity-testing-shared-patterns.md`

## Test kit rules

Use `KeyValueEntityTestKit`.

Preferred pattern:
- create a fresh test kit per test
- give explicit entity ids
- invoke the entity method under test with `testKit.method(...).invoke(...)`
- assert reply, error, update/delete flags, TTL, and resulting state as needed

## What to test

For each target entity, cover at least:
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

### Repository example tests
- `DurableIdentityRepositoryEntityTest`
  - success-path current-state persistence
  - tenant-filtered read behavior
- `DurableNotificationRepositoryEntityTest`
  - tenant-scoped notification, preference, outbox, and digest repository behavior

### Target-project pattern tests
- entity-specific validation errors
- idempotent no-op commands
- delete behavior when supported by the target entity
- strongly consistent read and replication filter command shape when the target uses KVE replication
- TTL behavior when the target command attaches `expireAfter(...)`, including the expected `getExpireAfter()` value

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

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
