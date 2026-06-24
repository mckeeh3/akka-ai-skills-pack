---
name: akka-workflow-pausing
description: Implement Akka Java SDK Workflow pause/resume flows using thenPause() and guarded resume commands. Use when a workflow must wait for external approval or additional input.
---

# Akka Workflow Pausing

Use this skill when the workflow should stop processing until a later command resumes it.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Required reading

Read these first if present:
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/workflows.html.md`

## Capability-first approval/supervision role

Use pause/resume when a capability must wait for human approval, additional evidence, external input, policy review, exception handling, or supervision before continuing. Store the waiting reason, required approver role/scope, evidence summary, risk/impact context, correlation id, trace id, and any policy citation in workflow state.

## Core pattern

1. Start the workflow and transition to a step that returns `stepEffects().thenPause()`.
2. Keep the workflow state in an explicit waiting status.
3. Add a public resume command such as `approve(...)` or `provideData(...)` with actor/AuthContext and decision input.
4. Guard the resume command so it only works in the waiting status and caller has the required role/scope for the capability.
5. Resume by recording the approval/supervision decision, emitting audit/work-trace details, and transitioning to the next step with any required input.

## Pattern reference

- a domain-specific approval workflow
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
