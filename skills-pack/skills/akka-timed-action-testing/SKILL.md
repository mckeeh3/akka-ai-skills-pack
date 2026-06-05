---
name: akka-timed-action-testing
description: Test Akka timer-backed flows with TimedActionTestkit for unit behavior and TestKitSupport plus Awaitility for end-to-end timer execution. Use when validation of timed action behavior is the main concern.
---

# Akka Timed Action Testing

Use this skill when validating a timed action component or a full timer-backed flow.

## Capability-first test role

Timed action tests should verify scheduled capability behavior, not only timer mechanics. Cover scheduler authority, system/service principal, tenant/customer scope, approval/policy reference, idempotent duplicate execution, stale/forbidden no-op semantics, retry behavior, and audit/work-trace effects for protected or consequential scheduled work.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Read first

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

## Generated SaaS timer contract

For generated SaaS scheduled capabilities, require:
- scheduled capability id, scheduler authority basis, tenant/customer target scope, and system principal;
- small timer payloads containing stable ids/references only;
- idempotency/no-op strategy for duplicate, stale, obsolete, denied, or cross-tenant invocations;
- policy/approval reference, retry budget, and escalation behavior for consequential work;
- audit/work-trace records for scheduling, execution, denial/no-op, retry exhaustion, and side effects;
- tests for authorized execution, stale/no-op, forbidden/cross-tenant, retry/idempotency, audit/trace, and surface/realtime updates where exposed.


## Common mistakes

Avoid:
- testing timer-backed endpoints through `componentClient` instead of `httpClient`
- assuming timers fire instantly without eventual assertions
- asserting only timer registration while never verifying the later state change
- ignoring the obsolete-timer path after confirmation or cancellation
- skipping duplicate timer delivery/retry tests for side-effecting scheduled capabilities
- treating missing tenant/customer scope, approval reference, or system authority as a happy path
