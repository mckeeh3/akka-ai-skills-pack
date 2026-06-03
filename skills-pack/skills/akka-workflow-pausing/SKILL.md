---
name: akka-workflow-pausing
description: Implement Akka Java SDK Workflow pause/resume flows using thenPause() and guarded resume commands. Use when a workflow must wait for external approval or additional input.
---

# Akka Workflow Pausing

Use this skill when the workflow should stop processing until a later command resumes it.

## Required reading

Read these first if present:
- `../../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/workflows.html.md`
- `../../examples/akka-components/src/main/java/com/example/application/ApprovalWorkflow.java`
- `../../examples/akka-components/src/main/java/com/example/domain/ApprovalState.java`
- `../../examples/akka-components/src/test/java/com/example/application/ApprovalWorkflowIntegrationTest.java`

## Capability-first approval/supervision role

Use pause/resume when a capability must wait for human approval, additional evidence, external input, policy review, exception handling, or supervision before continuing. Store the waiting reason, required approver role/scope, evidence summary, risk/impact context, correlation id, trace id, and any policy citation in workflow state.

## Core pattern

1. Start the workflow and transition to a step that returns `stepEffects().thenPause()`.
2. Keep the workflow state in an explicit waiting status.
3. Add a public resume command such as `approve(...)` or `provideData(...)` with actor/AuthContext and decision input.
4. Guard the resume command so it only works in the waiting status and caller has the required role/scope for the capability.
5. Resume by recording the approval/supervision decision, emitting audit/work-trace details, and transitioning to the next step with any required input.

## Repository example

- `ApprovalWorkflow`
  - pauses in `waitForApprovalStep()`
  - resumes only from `WAITING_FOR_APPROVAL`
  - applies the approval decision in a dedicated step before ending

## Design note

Pause/resume workflows should reject resume commands when the workflow has not started, is already completed, is otherwise in the wrong status, or the caller lacks authority for the waiting capability. Keep those guards explicit in the command handler; do not rely on UI-hidden actions or prompt instructions.

## Review checklist

Before finishing, verify:
- the waiting step uses `thenPause()`
- workflow state exposes an explicit waiting status plus required approver/supervisor scope
- resume commands validate the current status and caller authority before transitioning
- approval, rejection, timeout, and escalation outcomes are auditable
- tests cover successful resume, invalid resume attempts, and denied/unauthorized resume attempts when auth is in scope
