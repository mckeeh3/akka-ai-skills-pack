---
name: akka-workflow-pausing
description: Implement Akka Java SDK Workflow pause/resume flows using thenPause() and guarded resume commands. Use when a workflow must wait for external approval or additional input.
---

# Akka Workflow Pausing

Use this skill when the workflow should stop processing until a later command resumes it.

## Required reading

Read these first if present:
- `akka-context/sdk/workflows.html.md`
- `../../../src/main/java/com/example/application/ApprovalWorkflow.java`
- `../../../src/main/java/com/example/domain/ApprovalState.java`
- `../../../src/test/java/com/example/application/ApprovalWorkflowIntegrationTest.java`

## Core pattern

1. Start the workflow and transition to a step that returns `stepEffects().thenPause()`.
2. Keep the workflow state in an explicit waiting status.
3. Add a public resume command such as `approve(...)` or `provideData(...)`.
4. Guard the resume command so it only works in the waiting status.
5. Resume by transitioning to the next step and passing any required input.

## Repository example

- `ApprovalWorkflow`
  - pauses in `waitForApprovalStep()`
  - resumes only from `WAITING_FOR_APPROVAL`
  - applies the approval decision in a dedicated step before ending

## Design note

Pause/resume workflows should reject resume commands when the workflow has not started, is already completed, or is otherwise in the wrong status. Keep those guards explicit in the command handler.

## Review checklist

Before finishing, verify:
- the waiting step uses `thenPause()`
- workflow state exposes an explicit waiting status
- resume commands validate the current status before transitioning
- tests cover both successful resume and invalid resume attempts
