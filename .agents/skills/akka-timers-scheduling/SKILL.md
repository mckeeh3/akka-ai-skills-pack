---
name: akka-timers-scheduling
description: Schedule and delete Akka timers with TimerScheduler, including naming strategy, maxRetries, and placement in endpoints, workflows, consumers, or timed actions. Use when timer registration is the main concern.
---

# Akka Timer Scheduling

Use this skill when the main task is registering, replacing, or deleting timers.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Read first

- `akka-context/sdk/timed-actions.html.md`
- `akka-context/sdk/setup-and-dependency-injection.html.md`
- `../../examples/akka-components/src/main/java/com/example/api/TicketReservationEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/application/TicketReservationTimedAction.java`
- `../../examples/akka-components/src/test/java/com/example/application/TicketReservationEndpointIntegrationTest.java`
- `../../examples/akka-components/src/main/java/com/example/api/ReminderJobEndpoint.java`
- `../../examples/akka-components/src/main/java/com/example/application/ReminderJobTimedAction.java`
- `../../examples/akka-components/src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`
- `../../examples/akka-components/src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`
- `../../examples/akka-components/src/main/java/com/example/application/ApprovalDeadlineTimedAction.java`
- `../../examples/akka-components/src/test/java/com/example/application/ApprovalDeadlineWorkflowIntegrationTest.java`

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
        .method(TicketReservationTimedAction::expireReservation)
        .deferred(reservationId));

var state = componentClient
    .forKeyValueEntity(reservationId)
    .method(TicketReservationEntity::reserve)
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
- `TicketReservationEndpoint#createReservation`

### Schedule from a workflow command
Use when a workflow owns the timeout or reminder lifecycle.

Repository example:
- `ApprovalDeadlineWorkflow#start`

### Delete after successful completion
Use when the timer is no longer needed but the target command remains safe if the timer still fires.

Repository example:
- `TicketReservationEndpoint#confirm`

### Reschedule by reusing the same name
Use when the latest reminder or expiry time should replace the previous one.

Repository example:
- `ReminderJobTimedAction#sendReminder`

### Schedule from inside a timed action
Use when each execution decides whether more work remains.

Repository example:
- `ReminderJobTimedAction#sendReminder`

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
- unstable timer names
- timer names or payloads that omit required tenant/customer scope for protected work
- large payloads instead of compact identifiers and references
- scheduling methods whose signature may change soon
- relying only on delete instead of making the target command timer-safe
- scheduling consequential work without a capability id, system principal, approval/policy reference, and audit plan
- swallowing all failures inside the timed action when retries are actually needed
