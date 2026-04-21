---
name: akka-timed-action-testing
description: Test Akka timer-backed flows with TimedActionTestkit for unit behavior and TestKitSupport plus Awaitility for end-to-end timer execution. Use when validation of timed action behavior is the main concern.
---

# Akka Timed Action Testing

Use this skill when validating a timed action component or a full timer-backed flow.

## Read first

- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../src/test/java/com/example/application/TicketReservationTimedActionTest.java`
- `../../../src/test/java/com/example/application/TicketReservationEndpointIntegrationTest.java`
- `../../../src/main/java/com/example/application/TicketReservationTimedAction.java`
- `../../../src/main/java/com/example/api/TicketReservationEndpoint.java`
- `../../../src/test/java/com/example/application/ReminderJobTimedActionTest.java`
- `../../../src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`
- `../../../src/main/java/com/example/application/ReminderJobTimedAction.java`
- `../../../src/main/java/com/example/api/ReminderJobEndpoint.java`
- `../../../src/test/java/com/example/application/ApprovalDeadlineTimedActionTest.java`
- `../../../src/test/java/com/example/application/ApprovalDeadlineWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/ApprovalDeadlineWorkflowEndpointIntegrationTest.java`
- `../../../src/main/java/com/example/application/ApprovalDeadlineTimedAction.java`
- `../../../src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`

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

Repository examples:
- `TicketReservationEndpointIntegrationTest#reservationExpiresAfterTimerFires`
- `ReminderJobEndpointIntegrationTest#timedActionSelfSchedulesUntilMaxRemindersReached`

### 3. Delete-or-confirm integration test
Use `TestKitSupport` plus `Awaitility.during(...)` when you want to prove that confirmation prevented the later timer from changing state.

Repository examples:
- `TicketReservationEndpointIntegrationTest#confirmDeletesTimerAndKeepsReservationConfirmed`
- `ReminderJobEndpointIntegrationTest#completeStopsFutureSelfRescheduling`
- `ApprovalDeadlineWorkflowIntegrationTest#approveDeletesTimerAndWorkflowStaysApproved`

## Assertions to favor

- `result.isDone()` for unit tests
- eventual state assertions with `Awaitility.await().atMost(...).untilAsserted(...)`
- stable terminal state assertions after the timer window passes
- HTTP-level assertions through `httpClient` when the timer is scheduled by an endpoint

## Common mistakes

Avoid:
- testing timer-backed endpoints through `componentClient` instead of `httpClient`
- assuming timers fire instantly without eventual assertions
- asserting only timer registration while never verifying the later state change
- ignoring the obsolete-timer path after confirmation or cancellation
