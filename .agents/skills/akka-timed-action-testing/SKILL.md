---
name: akka-timed-action-testing
description: Test Akka timer-backed flows with TimedActionTestkit for unit behavior and TestKitSupport plus Awaitility for end-to-end timer execution. Use when validation of timed action behavior is the main concern.
---

# Akka Timed Action Testing

Use this skill when validating a timed action component or a full timer-backed flow.

## Lifecycle and compile boundary

Testing work belongs to the build/compile phase unless the selected task is explicitly runtime verification or runtime-validation reconciliation. Use this skill to prove the component-specific mechanics and the declared worker/harness/actor-adapter/governed-tool/capability path; do not widen a component-testing task into unrelated planning, product repair, or runtime-validation failure triage. For feature-bearing generated SaaS work, passing component tests can support `manual-ready`; `runtime-ready` still requires the real local API/UI/agent path, provider/fail-closed evidence where relevant, and reconciliation of runtime-validation findings through `../docs/runtime-validation-reconciliation.md`.

## Capability-first test role

Timed action tests should verify scheduled capability behavior, not only timer mechanics. Cover scheduler authority, system/service principal, tenant/customer scope, approval/policy reference, idempotent duplicate execution, stale/forbidden no-op semantics, retry behavior, and audit/work-trace effects for protected or consequential scheduled work.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Read first

- `../docs/app-development-lifecycle.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/runtime-validation-reconciliation.md` when tests are part of a runtime-validation readiness claim or remediation loop
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

## Test modes

### 1. Timed action unit test
Use `TimedActionTestkit` when you want to validate the returned effect without booting the whole service flow.

Canonical shape:

```java
var testKit = TimedActionTestkit.of(() -> new MyTimedAction(componentClient));
var result = testKit.method(MyTimedAction::handle).invoke("id-1");
assertTrue(result.isDone());
```

Use this when:
- the timed action maps downstream terminal results to `done()`
- constructor dependencies can be stubbed cheaply
- you want fast feedback on effect behavior

### 2. End-to-end timer integration test
Use `TestKitSupport` plus `Awaitility` when you want to verify that a timer actually fires and changes state later.

For self-rescheduling handlers that call `timers()` inside the timed action, prefer end-to-end tests for the rescheduling path. `TimedActionTestkit` is still useful for terminal `done()` mappings such as not-found or already-completed outcomes.

Pattern references:
- target-project endpoint/component integration test for timer expiry
- target-project self-rescheduling integration test that runs until the modeled reminder/max-attempt boundary

### 3. Delete-or-confirm integration test
Use `TestKitSupport` plus `Awaitility.during(...)` when you want to prove that confirmation prevented the later timer from changing state.

Pattern references:
- target-project endpoint/component integration test for timer cancellation/confirmation
- target-project self-rescheduling cancellation test
- target-project workflow approval/deadline cancellation test

## Assertions to favor

- `result.isDone()` for successful, obsolete, stale, or expected denied timer executions that should not retry
- explicit failure/error assertions only when the capability expects retry on transient dependency failure
- eventual state assertions with `Awaitility.await().atMost(...).untilAsserted(...)`
- stable terminal state assertions after the timer window passes
- HTTP-level assertions through `httpClient` when the timer is scheduled by an endpoint
- audit/work-trace assertions for scheduled execution, denials/no-ops, approvals/timeouts, and consequential side effects when those records are part of the capability contract

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Common mistakes

Avoid:
- testing timer-backed endpoints through `componentClient` instead of `httpClient`
- assuming timers fire instantly without eventual assertions
- asserting only timer registration while never verifying the later state change
- ignoring the obsolete-timer path after confirmation or cancellation
- skipping duplicate timer delivery/retry tests for side-effecting scheduled capabilities
- treating missing tenant/customer scope, approval reference, or system authority as a happy path
