---
name: akka-workflow-component
description: Implement Akka Java SDK Workflow classes using command handlers, step handlers, WorkflowSettings, and method-reference transitions. Use when writing the workflow component itself.
---

# Akka Workflow Component

Use this skill when the task is mainly about the workflow class itself.

## Required reading

Read these first if present:
- `akka-context/sdk/workflows.html.md`
- `../../../docs/timer-pattern-selection.md`
- `../../../src/main/java/com/example/application/TransferWorkflow.java`
- `../../../src/main/java/com/example/application/ApprovalWorkflow.java`
- `../../../src/main/java/com/example/application/ApprovalDeadlineWorkflow.java`
- `../../../src/main/java/com/example/domain/TransferState.java`
- `../../../src/main/java/com/example/domain/ApprovalState.java`
- `../../../src/main/java/com/example/domain/ApprovalDeadlineState.java`

## Core pattern

1. Annotate the class with `@Component(id = "...")`.
2. Extend `Workflow<State>`.
3. Put workflow configuration in `settings()`.
4. Start from a public command handler that updates state and `transitionTo(...)` the first step.
5. Keep steps private and return `StepEffect`.
6. Use step input records when a later step needs durable input.
7. Add a read-only command handler when callers need the current workflow state.

## Repository examples

- `TransferWorkflow`
  - start command updates state and transitions to a withdraw step
  - step-specific recovery overrides the default strategy
  - state keeps stable command ids for replay-safe downstream calls
- `ApprovalWorkflow`
  - start command transitions to a pause step
  - read handler exposes current approval state
- `ApprovalDeadlineWorkflow`
  - start command registers a timer from the workflow and then pauses
  - timer-triggered timeout is routed back into the workflow through a timed action

## Design guidance

Prefer workflow state that answers these questions directly:
- what business request started the workflow?
- what step outcome has already happened?
- what data is required by the next step?
- what data is required if compensation becomes necessary?

Keep step names stable with `@StepName` when readability or migration clarity matters.

## Review checklist

Before finishing, verify:
- the workflow has at least one public command handler
- command handlers use `effects()`
- step handlers use `stepEffects()`
- transitions use method references
- any `ReadOnlyEffect` handler does not mutate state
- state and step-input records are immutable Java records
