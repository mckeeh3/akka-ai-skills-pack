---
name: akka-timed-actions
description: Orchestrate Akka Java SDK timer and TimedAction work across scheduling, obsolete-timer handling, and testing. Use when the task spans more than one timer concern.
---

# Akka Timed Actions and Timers

Use this as the top-level skill for Akka Java SDK timer-backed flows.

## Goal

Generate or review timer code that is:
- correct for Akka SDK 3.4+
- explicit about where timers are scheduled and where they are handled
- safe under at-least-once execution and retries
- easy for AI agents to extend without loading unrelated component families

## Required reading before coding

Read these first if present:
- `akka-context/sdk/timed-actions.html.md`
- `akka-context/sdk/setup-and-dependency-injection.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../../../docs/timer-pattern-selection.md`
- existing timer examples under `src/main/java/**/TimedAction*.java`, `*TimedAction.java`, or timer-scheduling endpoints
- matching tests under `src/test/java/**`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/TicketReservationEntity.java`
- `../../../src/main/java/com/example/application/TicketReservationTimedAction.java`
- `../../../src/main/java/com/example/api/TicketReservationEndpoint.java`
- `../../../src/main/java/com/example/domain/TicketReservation.java`
- `../../../src/test/java/com/example/application/TicketReservationEntityTest.java`
- `../../../src/test/java/com/example/application/TicketReservationTimedActionTest.java`
- `../../../src/test/java/com/example/application/TicketReservationEndpointIntegrationTest.java`
- `../../../src/main/java/com/example/application/ReminderJobEntity.java`
- `../../../src/main/java/com/example/application/ReminderJobTimedAction.java`
- `../../../src/main/java/com/example/api/ReminderJobEndpoint.java`
- `../../../src/main/java/com/example/domain/ReminderJob.java`
- `../../../src/test/java/com/example/application/ReminderJobEntityTest.java`
- `../../../src/test/java/com/example/application/ReminderJobTimedActionTest.java`
- `../../../src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`
- `../../../src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`
- `../../../src/main/java/com/example/application/ApprovalDeadlineTimedAction.java`
- `../../../src/main/java/com/example/api/ApprovalDeadlineWorkflowEndpoint.java`
- `../../../src/main/java/com/example/domain/ApprovalDeadlineState.java`
- `../../../src/test/java/com/example/application/ApprovalDeadlineWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/ApprovalDeadlineTimedActionTest.java`
- `../../../src/test/java/com/example/application/ApprovalDeadlineWorkflowEndpointIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-timed-action-component`
  - writing the `TimedAction` class itself, constructor injection, and terminal-result handling
- `akka-timers-scheduling`
  - `TimerScheduler.createSingleTimer(...)`, timer naming, delete patterns, and placement of scheduling logic
- `akka-timed-action-testing`
  - `TimedActionTestkit`, endpoint integration tests, and eventual assertions for timer-triggered outcomes

If the timer flow is part of a broader component story, also load the relevant family:
- `akka-http-endpoint-component-client` when scheduling from an HTTP endpoint
- `akka-workflows` when scheduling from workflow commands or steps
- `akka-consumers` when scheduling from a consumer
- `akka-key-value-entities` or `akka-event-sourced-entities` for the stateful target component

## Core rules

1. A timed action extends `akka.javasdk.timedaction.TimedAction` and has `@Component(id = "...")`.
2. Timer-triggered methods should usually return `effects().done()` for business-terminal outcomes such as already-confirmed, already-expired, or not-found.
3. Only propagate failures when retrying the timer is actually desired.
4. Use stable component ids, method names, and payload types because scheduled calls persist those values.
5. Timers are cluster-unique by name, so choose names that encode the target id and purpose.
6. Schedule the timer before creating mutable state when an untracked resource would be worse than an obsolete timer.
7. Delete timers as housekeeping after successful completion, but also make the target flow safe if deletion does not happen.
8. Keep timed actions stateless; coordinate with entities, workflows, views, or endpoints through `ComponentClient`.

## Decision guide

### 1. Schedule a one-shot expiry or reminder
Use when an endpoint or workflow should schedule one future call.

Repository example:
- `TicketReservationEndpoint`

### 2. Implement the timer handler
Use when the timed action translates the scheduled call into a command on another component.

Repository examples:
- `TicketReservationTimedAction`
- `ReminderJobTimedAction`

### 3. Self-reschedule from inside the timed action
Use when each timer execution decides whether to schedule the next one.

Repository example:
- `ReminderJobTimedAction#sendReminder`

### 4. Schedule a timer from a workflow command
Use when a workflow start or resume command should register a timeout or reminder.

Repository example:
- `ApprovalDeadlineWorkflow#start`

### 5. Make the target command timer-safe
Use when the entity or workflow must treat stale timer executions as successful no-ops or explicit terminal replies.

Repository examples:
- `TicketReservationEntity#expire`
- `ReminderJobEntity#recordReminderSent`
- `ApprovalDeadlineWorkflow#markTimedOut`

## Final review checklist

Before finishing, verify:
- `TimerScheduler` is injected only in supported component types
- timer names are stable and unique
- scheduled method signature is compatible with future deployments
- timed action replies `done()` for obsolete timers
- unexpected failures are not swallowed unless intentionally converted to terminal success
- tests cover both timer firing and explicit confirmation or cancellation paths

## Response style

When answering coding tasks:
- name the scheduling component and the timed action component explicitly
- say what makes the timer idempotent or safe under retries
- call out the timer name strategy
- list the concrete example files used as references
