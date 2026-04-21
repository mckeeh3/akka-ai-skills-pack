---
name: akka-timers-scheduling
description: Schedule and delete Akka timers with TimerScheduler, including naming strategy, maxRetries, and placement in endpoints, workflows, consumers, or timed actions. Use when timer registration is the main concern.
---

# Akka Timer Scheduling

Use this skill when the main task is registering, replacing, or deleting timers.

## Read first

- `akka-context/sdk/timed-actions.html.md`
- `akka-context/sdk/setup-and-dependency-injection.html.md`
- `../../../src/main/java/com/example/api/TicketReservationEndpoint.java`
- `../../../src/main/java/com/example/application/TicketReservationTimedAction.java`
- `../../../src/test/java/com/example/application/TicketReservationEndpointIntegrationTest.java`
- `../../../src/main/java/com/example/api/ReminderJobEndpoint.java`
- `../../../src/main/java/com/example/application/ReminderJobTimedAction.java`
- `../../../src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`
- `../../../src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`
- `../../../src/main/java/com/example/application/ApprovalDeadlineTimedAction.java`
- `../../../src/test/java/com/example/application/ApprovalDeadlineWorkflowIntegrationTest.java`

## Where timers may be scheduled

`TimerScheduler` can be injected into:
- service setup
- HTTP endpoints
- consumers
- workflows
- timed actions

Do not try to inject it into entities or views.

## Core scheduling rules

1. Use `createSingleTimer(name, delay, deferredCall)` for default retry behavior.
2. Use `createSingleTimer(name, delay, maxRetries, deferredCall)` when retries must be bounded.
3. Build the deferred call through `componentClient.forTimedAction().method(...).deferred(...)` or another component client supported by the timer API.
4. Give each timer a deterministic purpose-specific name such as `order-expiration-<id>`.
5. Remember that scheduling a timer with the same name replaces the previous timer.
6. Delete timers as cleanup after the business process completes.
7. Still make the eventual target call safe if delete never happens.

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

## Common mistakes

Avoid:
- unstable timer names
- large payloads instead of compact identifiers
- scheduling methods whose signature may change soon
- relying only on delete instead of making the target command timer-safe
- swallowing all failures inside the timed action when retries are actually needed
