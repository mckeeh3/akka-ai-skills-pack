# Timer pattern selection

Tiny agent-oriented reference for choosing between three Akka timer patterns.

Primary official semantics:
- `akka-context/sdk/timed-actions.html.md`
- `akka-context/sdk/workflows.html.md`

Local executable examples:
- `src/main/java/com/example/application/ApprovalWorkflow.java`
- `src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`
- `src/main/java/com/example/application/ApprovalDeadlineTimedAction.java`
- `src/main/java/com/example/application/ReminderJobTimedAction.java`
- `src/main/java/com/example/application/TicketReservationTimedAction.java`
- `src/test/java/com/example/application/ApprovalDeadlineWorkflowIntegrationTest.java`
- `src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`
- `src/test/java/com/example/application/TicketReservationEndpointIntegrationTest.java`

## Quick choice

Use:
- **workflow pause timeout** when a workflow is already paused and the timeout only exists to resume or fail that pause
- **workflow-triggered timer** when the workflow should explicitly own a timer lifecycle outside the built-in pause-timeout mechanism
- **self-rescheduling timed action** when each execution decides whether to schedule the next run

## 1. Workflow pause timeout

Use when:
- the workflow is waiting in `thenPause(...)`
- timeout behavior is part of that paused step
- you want the timeout to be modeled directly in the workflow definition
- there is a single pause window rather than an independent timer lifecycle

Prefer this when the timeout is really “resume or timeout this pause”.

Official pattern:
- `akka-context/sdk/workflows.html.md` section on pausing workflows

Repository reference:
- `src/main/java/com/example/application/ApprovalWorkflow.java`

Mental model:
- pause step owns the wait
- timeout is attached to the pause
- workflow remains the only moving part

## 2. Workflow-triggered timer

Use when:
- a workflow command should register a timer explicitly
- the timer should survive independently of a single pause step
- timeout work should come back through a timed action and a normal workflow command
- you want explicit timer naming, deletion, and compatibility management

Prefer this when the workflow owns a timer as a first-class integration concern.

Repository reference:
- `src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`
- `src/main/java/com/example/application/ApprovalDeadlineTimedAction.java`
- `src/test/java/com/example/application/ApprovalDeadlineWorkflowIntegrationTest.java`

Mental model:
- workflow schedules timer
- timed action receives timer call
- timed action calls workflow timeout command
- workflow handles stale timer outcomes safely

## 3. Self-rescheduling timed action

Use when:
- repeated work should continue until runtime data says stop
- each execution decides whether another timer is needed
- the schedule is naturally “do work, inspect result, maybe schedule next run”
- the loop does not need workflow step orchestration

Prefer this when the timer itself is the loop controller.

Repository reference:
- `src/main/java/com/example/application/ReminderJobTimedAction.java`
- `src/main/java/com/example/application/ReminderJobEntity.java`
- `src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`

Mental model:
- timed action does one unit of work
- timed action checks result
- timed action schedules next run with `timers()` if needed

## Comparison table

| Pattern | Best for | Main owner | Next run decided by | Repository example |
|---|---|---|---|---|
| Workflow pause timeout | timeout of a paused workflow wait | workflow pause step | workflow pause settings | `ApprovalWorkflow` |
| Workflow-triggered timer | explicit workflow-owned timeout/reminder | workflow + timed action | workflow command that creates/deletes timer | `ApprovalDeadlineWorkflow` |
| Self-rescheduling timed action | repeated polling/reminders/maintenance loops | timed action | timed action handler | `ReminderJobTimedAction` |

## Decision rules

Choose **workflow pause timeout** if:
- there is already a paused step
- the timeout only exists for that pause
- you do not need an independently named timer

Choose **workflow-triggered timer** if:
- the workflow should explicitly create/delete the timer
- the timer should call back through a timed action
- you want timer lifecycle visible outside pause semantics

Choose **self-rescheduling timed action** if:
- no workflow is needed
- repetition count or continuation decision is produced by each run
- the handler itself should drive the next schedule

## Anti-pattern hints

Avoid using a workflow-triggered timer when a simple pause timeout is enough.

Avoid using a self-rescheduling timed action when you really need durable multi-step workflow state and explicit business transitions.

Avoid making timer-triggered commands fail for known terminal cases like already approved, already timed out, not found, or already completed. Those should usually be mapped to successful terminal replies so the timer can be dropped.
