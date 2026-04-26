---
name: akka-change-request-to-spec-update
description: Convert an iterative feature request, bug report, issue, or implementation discovery into focused updates to app-description/specs artifacts and queue follow-up tasks without redoing the whole PRD decomposition.
---

# Akka Change Request to Spec Update

Use this skill when an Akka project already has app-description and/or `specs/` planning artifacts and the user introduces an incremental change.

This is an **evolution skill**. It keeps the app meaning and implementation plan current before new code is written.

## Goal

Turn a change request into a controlled planning delta that:
- preserves the current app-description/spec structure
- updates only the affected authoritative description/spec artifacts
- adds or revises verification expectations
- identifies auth/security and observability impact
- updates affected slice/backlog/task-brief artifacts
- appends, blocks, defers, or supersedes pending queue tasks as needed
- avoids broad PRD redecomposition unless the change is foundational

## Use this skill when

The task sounds like:
- "add this feature to the existing plan"
- "update the specs for this new requirement"
- "we found a bug; add a regression and fix task"
- "this pending task exposed a missing requirement"
- "adjust the backlog for this behavior change"
- "incorporate this issue into the existing task queue"

Use `akka-revised-prd-reconciliation` instead when the input is a full revised PRD or replacement requirements document.

Use `akka-do-next-pending-task` instead when the user wants to execute an existing queue item without changing the plan.

## Required reading

Read these first if present:
- `../README.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/pending-task-queue.md`
- `../../docs/solution-plan-to-implementation-queue.md`
- `../app-descriptions/SKILL.md`
- `../app-description-input-normalization/SKILL.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-change-impact/SKILL.md`
- `../akka-backlog-to-pending-tasks/SKILL.md`
- target project `app-description/` indexes if present
- target project `specs/README.md` if present
- target project `specs/akka-solution-plan.md` if present
- target project `specs/pending-tasks.md` if present

Then read only the affected slice, backlog, task brief, and description files needed to apply the change.

## Change classification

Classify the input as one or more of:
- additive feature
- behavior revision
- bug/regression fix
- security/auth change
- observability/operations change
- integration contract change
- UI/API surface change
- implementation discovery
- de-scope/removal
- unclear change requiring clarification

Ask the smallest clarifying question only when necessary to avoid changing the wrong semantic layer or queue scope.

## Operating flow

### 1. Normalize and route the request

Summarize:
- change basis
- affected capabilities
- affected behavior
- affected tests
- likely affected Akka components
- whether this is local, cross-slice, or foundational

If the change is broad enough to invalidate the current architecture, stop and recommend `akka-revised-prd-reconciliation` or `akka-prd-to-specs-backlog` instead of patching locally.

### 2. Update authoritative meaning first

If `app-description/` exists, update it before implementation specs:
1. capabilities when scope/outcomes changed
2. behavior when app semantics changed
3. tests for acceptance, regression, negative, idempotency, security, or operational verification
4. auth/security when identity, authorization, trust, or sensitive data changed
5. observability when logs, metrics, traces, audit, health, or alerts changed
6. traceability and readiness as needed

Do not bury new semantics only in backlog text or generated code.

### 3. Update realization specs

Update the smallest relevant `specs/` artifacts:
- `specs/akka-solution-plan.md` only if architectural choices or global implementation order changed
- `specs/cross-cutting/*.md` for shared conventions/policies
- `specs/slices/*.md` for business slice meaning
- `specs/backlog/*-build-backlog.md` for implementation breakdown
- `specs/tasks/**/*.md` when one task brief must change or a new leaf task is needed

Preserve numbering and existing file names unless the user asks for a larger reorganization.

### 4. Reconcile the queue

Update `specs/pending-tasks.md` if present.

Rules:
- preserve task IDs and statuses
- do not delete completed tasks
- append new tasks for new work
- mark obsolete pending/deferred/blocked tasks as `superseded` when a later spec change replaces them
- leave completed tasks as `done`; add new follow-up tasks if completed work now needs changes
- update required reads and skills for affected pending tasks
- block tasks whose source spec is now ambiguous
- avoid renumbering

If there is no queue yet but follow-on implementation is needed, create it with `akka-backlog-to-pending-tasks` rules.

### 5. Report the plan delta

End with:
- changed authoritative artifacts
- changed planning artifacts
- queue changes
- new/blocked/superseded tasks
- next runnable pending task

## Queue status guidance

Use these statuses:
- `pending`
- `in-progress`
- `blocked`
- `done`
- `deferred`
- `superseded`

Use `superseded` only when a task should not be executed because a later change replaced its source requirement, design, or implementation path.

Recommended note:

```md
- notes:
  - superseded: replaced by <TASK-ID or spec path> due to <change request summary>
```

## Anti-patterns

Avoid:
- implementing code before updating changed app meaning/specs
- regenerating all specs from scratch for a local change
- deleting completed or obsolete queue entries
- renumbering task IDs
- leaving regression tests unspecified for a bug fix
- treating security/observability as optional after behavior changes
- silently widening the current implementation task

## Final review checklist

Before finishing, verify:
- the change was classified
- authoritative description/spec files were updated before queue edits
- test impact was handled
- security and observability impact were considered
- affected backlog/task files were updated or intentionally left unchanged
- queue IDs and statuses were preserved
- obsolete tasks were superseded rather than deleted
- the next runnable task is named

## Response style

Use this shape:

```md
# Change Request Spec Update

## Change classification
- ...

## Updated artifacts
- app-description: ...
- specs: ...
- queue: ...

## Impact and rationale
- ...

## Queue result
- added:
- updated:
- blocked:
- superseded:

## Next runnable task
- ...
```
