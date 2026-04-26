---
name: akka-revised-prd-reconciliation
description: Reconcile a revised or replacement PRD against existing app-description, specs, backlogs, and pending tasks; produce a controlled delta instead of blindly regenerating the task queue.
---

# Akka Revised PRD Reconciliation

Use this skill when a project already has app-description/spec/backlog/queue artifacts and the user provides a revised PRD, replacement requirements document, or substantial product specification update.

This skill protects long-running work from stale plans. It compares the revised requirements to the current maintained artifacts and updates them deliberately.

## Goal

Reconcile a revised PRD with the current project plan by identifying:
- added requirements
- changed requirements
- removed or de-scoped requirements
- clarified requirements
- conflicting requirements
- completed implementation work that now needs follow-up
- pending tasks that remain valid
- pending tasks that must be updated, blocked, deferred, or superseded
- new queue tasks to append

The output should preserve useful planning history and produce a trustworthy next implementation queue.

## Use this skill when

The task sounds like:
- "here is the revised PRD; update the plan"
- "reconcile this new PRD with the existing backlog"
- "the requirements changed; update specs and pending tasks"
- "compare this PRD version against our current specs"
- "do not restart from scratch; update the existing plan"

Use `akka-change-request-to-spec-update` for small feature requests, bug reports, or implementation discoveries.

Use `akka-prd-to-specs-backlog` when no existing planning artifacts exist or the user explicitly wants a clean initial planning package.

## Required reading

Read these first if present:
- `../README.md`
- `../../docs/pending-task-queue.md`
- `../../docs/solution-plan-to-implementation-queue.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../akka-change-request-to-spec-update/SKILL.md`
- `../app-description-change-impact/SKILL.md`
- target project `app-description/00-system/app-manifest.md` if present
- target project `app-description/10-capabilities/capabilities-index.md` if present
- target project `app-description/20-behavior/behavior-index.md` if present
- target project `specs/README.md` if present
- target project `specs/akka-solution-plan.md` if present
- target project `specs/pending-tasks.md` if present
- relevant `specs/slices/*.md`, `specs/backlog/*.md`, and `specs/tasks/**/*.md`

Read the revised PRD completely.

If an original PRD is available, read it only when needed for diff confidence. Prefer reconciling against the maintained app-description/specs because they are the current source of truth.

## Reconciliation workflow

### 1. Establish current baseline

Summarize the current baseline from maintained artifacts:
- app identity and goals
- capabilities
- major behavior flows
- chosen Akka components
- slice/backlog structure
- queue status counts
- completed work that may be affected

### 2. Extract revised PRD facts

From the revised PRD, extract:
- actors and roles
- capabilities and outcomes
- commands/write operations
- queries/views/reporting needs
- workflows and timers
- integrations
- security/auth requirements
- observability/operational requirements
- explicit non-goals/de-scopes
- acceptance and regression expectations

### 3. Produce a delta classification

Classify each meaningful difference as:
- added
- changed
- removed/de-scoped
- clarified
- conflict/open question
- implementation detail only

For each delta, identify likely affected:
- app-description layers
- `specs/akka-solution-plan.md`
- cross-cutting specs
- slice specs
- backlog files
- task briefs
- pending tasks
- generated outputs/components

### 4. Update app-description first when present

If `app-description/` exists, update authoritative layers before `specs/`:
1. capabilities
2. behavior
3. tests
4. auth/security
5. observability
6. traceability
7. readiness

If the revised PRD conflicts with current app-description meaning, do not silently choose. Ask the smallest clarifying question or mark affected tasks blocked.

### 5. Update specs and backlogs

Update existing files rather than replacing the whole tree:
- modify existing slice specs when a slice remains valid
- add new numbered slice specs for new capability areas
- mark de-scoped behavior in specs explicitly rather than deleting context when useful
- update matching backlog files
- create new backlog files for new slices
- update or create task briefs for oversized changed work

Preserve numbering. Add new numbers at the end unless a local insertion is clearly safer and does not force renumbering.

### 6. Reconcile `specs/pending-tasks.md`

Apply queue reconciliation rules:
- preserve IDs, order, and status history
- do not delete completed tasks
- do not reset `done` tasks to `pending`
- append new tasks for new work
- update pending tasks whose scope/read list/skills changed but still represent the same work
- mark obsolete non-done tasks as `superseded`
- add follow-up tasks for completed work that must change
- mark tasks `blocked` when the revised PRD creates unresolved decisions
- keep dependencies valid and not over-serialized

Status counts should be reported after reconciliation.

### 7. Decide whether broader replanning is required

Recommend one of:
- `localized reconciliation` — changes are bounded to known slices/tasks
- `broad reconciliation` — multiple slices/backlogs changed but architecture is intact
- `full replanning recommended` — foundational assumptions changed enough that patching is unsafe

Do not claim a localized update is safe without a clear dependency chain.

## Queue supersession rules

Use `superseded` when a pending/blocked/deferred task is no longer valid because of the revised PRD.

Example:

```md
- status: superseded
- notes:
  - superseded: revised PRD replaced this task with TASK-047 and changed approval behavior from single-step to escalation workflow
```

For completed tasks, keep `done`; append a new follow-up task instead:

```md
### TASK-047: Update approval workflow for revised escalation policy

- status: pending
- source: specs/backlog/03-approval-build-backlog.md
- depends on: [TASK-012]
...
```

## Required reconciliation report

Use this response shape:

```md
# Revised PRD Reconciliation

## Baseline read
- ...

## Revised PRD delta
### Added
- ...
### Changed
- ...
### Removed/de-scoped
- ...
### Clarifications
- ...
### Conflicts/open questions
- ...

## Artifact updates
- app-description:
- specs:
- backlogs:
- task briefs:
- pending tasks:

## Queue reconciliation
- preserved:
- updated:
- added:
- blocked:
- superseded:
- follow-up tasks for completed work:

## Replanning recommendation
- localized | broad | full replanning recommended
- rationale:

## Next runnable task
- ...
```

## Anti-patterns

Avoid:
- blindly regenerating all specs and losing task history
- deleting completed queue entries
- renumbering existing tasks
- letting old pending tasks survive when their source requirement changed
- treating the revised PRD as additive only
- skipping removal/de-scope analysis
- changing implementation code during reconciliation
- failing to surface conflicts between the revised PRD and current app-description

## Final review checklist

Before finishing, verify:
- the revised PRD was read completely
- current maintained artifacts were used as baseline
- deltas are categorized as added/changed/removed/clarified/conflict
- affected app-description/spec/backlog/task files were updated
- queue history was preserved
- obsolete non-done tasks were superseded, not deleted
- completed affected work has follow-up tasks
- queue status counts and next runnable task are reported
