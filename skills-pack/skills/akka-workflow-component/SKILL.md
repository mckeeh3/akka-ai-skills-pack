---
name: akka-workflow-component
description: Implement Akka Java SDK Workflow classes using command handlers, step handlers, WorkflowSettings, and method-reference transitions. Use when writing the workflow component itself.
---

# Akka Workflow Component

Use this skill when the task is mainly about the workflow class itself.

## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Required reading

Read these first if present:
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/workflows.html.md`
- `../docs/timer-pattern-selection.md`

## Capability-first pattern

Before writing the class, name the governed workflow capability and preserve its contract in code: capability id, allowed callers, AuthContext and tenant/customer scope, command schema, idempotency key or workflow id strategy, side effects, approval/escalation rules, audit/work-trace fields, and selected exposure surfaces.

Workflow command handlers are capability entry/advance points. They should validate caller intent, store the authorization or approval basis needed by later steps, and avoid committing consequential side effects before the workflow reaches the authorized step.

## Core pattern

1. Annotate the class with `@Component(id = "...")`.
2. Extend `Workflow<State>`.
3. Put workflow configuration in `settings()`.
4. Start from a public command handler that updates state with capability id, actor/scope, trace/correlation ids, and `transitionTo(...)` the first step.
5. Keep steps private and return `StepEffect`.
6. Use step input records when a later step needs durable input.
7. Add a read-only command handler when callers need the current workflow state.

## Pattern references

- a domain-specific workflow
  - start command updates state and transitions to a withdraw step
  - step-specific recovery overrides the default strategy
  - state keeps stable command ids for replay-safe downstream calls
- a domain-specific approval workflow
  - start command transitions to a pause step
  - read handler exposes current approval state
- `ApprovalDeadlineWorkflow`
  - start command registers a timer from the workflow and then pauses
  - timer-triggered timeout is routed back into the workflow through a timed action

## Design guidance

Prefer workflow state that answers these questions directly:
- what governed capability execution does this workflow represent?
- what actor, AuthContext, tenant/customer scope, correlation id, and trace id started it?
- what approval, policy, or supervision decision currently authorizes progress?
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
- consequential steps either reauthorize or use a persisted valid approval/policy decision
- workflow state and tests make supervision, denial, idempotency, and audit/trace behavior explicit when relevant
