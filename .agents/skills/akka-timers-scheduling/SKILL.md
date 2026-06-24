---
name: akka-timers-scheduling
description: Schedule and delete Akka timers with TimerScheduler, including naming strategy, maxRetries, and placement in endpoints, workflows, consumers, or timed actions. Use when timer registration is the main concern.
---

# Akka Timer Scheduling

Use this skill when the main task is registering, replacing, or deleting timers.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Read first

- `akka-context/sdk/timed-actions.html.md`
- `akka-context/sdk/setup-and-dependency-injection.html.md`

## Where timers may be scheduled

`TimerScheduler` can be injected into:
- service setup
- HTTP endpoints
- consumers
- workflows
- timed actions

Do not try to inject it into entities or views.

## Capability-first scheduling rules

1. Schedule timers only for a named scheduled capability with an explicit authority basis: human request, workflow state, system policy, or previously approved decision.
2. Use `createSingleTimer(name, delay, deferredCall)` for default retry behavior.
3. Use `createSingleTimer(name, delay, maxRetries, deferredCall)` when retries must be bounded.
4. Build the deferred call through `componentClient.forTimedAction().method(...).deferred(...)` or another component client supported by the timer API.
5. Give each timer a deterministic purpose-specific name such as `order-expiration-<tenantId>-<id>` when tenant/customer scope matters.
6. Keep payloads compact but include or reload target id, tenant/customer scope, capability id, correlation id, and authorization/approval/audit reference required by the target.
7. Remember that scheduling a timer with the same name replaces the previous timer; ensure replacement is authorized and audited when it changes consequential behavior.
8. Delete timers as cleanup after the business process completes.
9. Still make the eventual target call safe if delete never happens.
10. Document whether stale/denied timer executions are terminal no-ops or retryable failures.

## Canonical endpoint pattern

```java
var reservationId = UUID.randomUUID().toString();
timerScheduler.createSingleTimer(
    timerName(reservationId),
    Duration.ofMinutes(15),
    5,
    componentClient
        .forTimedAction()
        .method(AttentionProducerService::expireReservation)
        .deferred(reservationId));

var state = componentClient
    .forKeyValueEntity(reservationId)
    .method(DurableAttentionRepositoryEntity::reserve)
    .invoke(command);
```

Then on success of the manual completion path:

```java
timerScheduler.delete(timerName(reservationId));
```

## Placement guidance

### Schedule before state creation
Use when an untracked business object is worse than an obsolete timer.

Repository example:
- `WorkstreamEndpoint#createReservation`

### Schedule from a workflow command
Use when a workflow owns the timeout or reminder lifecycle.

Pattern reference:
- `ApprovalDeadlineWorkflow#start`

### Delete after successful completion
Use when the timer is no longer needed but the target command remains safe if the timer still fires.

Repository example:
- `WorkstreamEndpoint#confirm`

### Reschedule by reusing the same name
Use when the latest reminder or expiry time should replace the previous one.

Pattern reference:
- `AttentionRefreshTimedAction#sendReminder`

### Schedule from inside a timed action
Use when each execution decides whether more work remains.

Pattern reference:
- `AttentionRefreshTimedAction#sendReminder`

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Common mistakes

Avoid:
- unstable timer names
- timer names or payloads that omit required tenant/customer scope for protected work
- large payloads instead of compact identifiers and references
- scheduling methods whose signature may change soon
- relying only on delete instead of making the target command timer-safe
- scheduling consequential work without a capability id, system principal, approval/policy reference, and audit plan
- swallowing all failures inside the timed action when retries are actually needed
