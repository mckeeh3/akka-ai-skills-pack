---
name: akka-backlog-to-pending-tasks
description: Create or repair specs/pending-tasks.md from existing specs/backlog/*-build-backlog.md files when backlogs already exist but the durable pending-task queue is missing, stale, or incomplete.
---

# Akka Backlog to Pending Tasks

Use this skill when a project already has planning artifacts under `specs/`, especially `specs/backlog/*.md`, but does not yet have a usable durable queue at:

```text
specs/pending-tasks.md
```

This is a queue repair/materialization skill. It does not redo PRD decomposition and it does not implement application code.

## Goal

Create or repair `specs/pending-tasks.md` so future harness runs can execute one focused task at a time with `akka-do-next-pending-task`.

The skill must:
- read existing solution, slice, backlog, and task-brief artifacts
- derive queue tasks from backlog `Suggested harness task breakdown` sections
- preserve existing queue task IDs and statuses when a queue already exists
- create stable task IDs for missing queue entries
- add dependencies based on backlog order, explicit prerequisite notes, and component dependencies
- include required reads, skills, expected outputs, required checks, done criteria, and notes
- avoid implementation code changes

## Use this skill when

Use this skill when the user says things like:
- "create pending-tasks.md from the existing backlog"
- "repair the pending task queue"
- "the backlogs exist but there is no specs/pending-tasks.md"
- "regenerate the pending task queue"
- "sync pending-tasks.md with specs/backlog"

Do **not** use this skill when:
- the user is still at PRD level and no slice/backlog planning exists; use `akka-prd-to-specs-backlog`
- the user wants a single backlog item narrowed into a task brief; use `akka-backlog-item-to-task-brief`
- the user wants to execute a pending task; use `akka-do-next-pending-task`
- the user asks for application code directly; use the focused implementation skills

## Required reading

Read these first if present:
- `../README.md`
- `../../docs/pending-question-queue.md`
- `../../docs/pending-task-queue.md`
- `../../docs/solution-plan-to-implementation-queue.md`
- `../../docs/web-ui-style-guide.md` when materializing browser UI tasks
- `../../specs/README.md`
- `../../specs/akka-solution-plan.md`
- `../../specs/pending-questions.md` if it exists
- `../../specs/pending-tasks.md` if it already exists
- all relevant `../../specs/backlog/*-build-backlog.md` files
- `../../specs/tasks/README.md` if present
- relevant `../../specs/tasks/**/*.md` task briefs if present
- relevant `../../specs/slices/*.md` files when needed to resolve dependencies or reads

Do not reread the original PRD unless the existing backlogs are too ambiguous to create queue tasks.

## Output

Create or update:

```text
specs/pending-tasks.md
```

Use the contract in `../../docs/pending-task-queue.md`.

## Queue derivation rules

### Source of tasks

Derive queue tasks from each backlog file's `Suggested harness task breakdown` section.

Do not create one queue task per class name unless the backlog explicitly frames each class as a separate harness-sized task.

### Question gate

If `specs/pending-questions.md` exists, inspect unresolved `blocking` questions before materializing tasks.

Rules:
- do not create runnable tasks for work blocked by unresolved `blocking` questions
- if a backlog item is entirely blocked, create a `blocked` task only when useful for visibility and note the blocking question IDs
- if only part of a backlog is blocked, create tasks for unblocked work and leave blocked work out or blocked with explicit question references
- if a question is `answered` but not `resolved`, reconcile it or leave affected tasks blocked
- do not silently choose defaults unless the question is `deferred` with an accepted default or limitation
- if browser UI tasks exist and no selected style guide or pending style question exists, add/update `specs/pending-questions.md` with the style-selection question from `../../docs/web-ui-style-guide.md` and block only the affected UI tasks

### Task sizing

Each queue task should be executable in one fresh harness context.

Good queue tasks usually map to:
- one shared domain package
- one entity or workflow plus direct tests
- one view family plus tests
- one consumer or timed action plus tests
- one endpoint family plus tests
- one focused task brief under `specs/tasks/`

If a backlog task item is too broad:
- prefer creating multiple smaller queue tasks when the split is obvious from the backlog
- otherwise create one `blocked` queue task with a note that `akka-backlog-item-to-task-brief` should split it first

### Existing queue preservation

If `specs/pending-tasks.md` already exists:
- preserve task IDs
- preserve statuses unless the user asks to reset them
- preserve useful notes
- do not delete completed tasks
- mark obsolete non-done tasks as `superseded` when the matching backlog work was replaced
- append missing tasks or update stale task metadata carefully
- do not renumber tasks just to improve aesthetics

When a backlog item appears to match an existing queue task, update that existing entry rather than creating a duplicate.

### Dependency rules

Set dependencies conservatively:
- foundational domain/config tasks usually have no dependencies
- entity/workflow tasks depend on required domain tasks
- views depend on their source component tasks
- endpoint tasks depend on components they call
- end-to-end tests depend on the components under test
- later slice tasks depend only on earlier tasks that are genuinely required

Avoid over-serializing independent work.

### Required reads

Each task should list the smallest useful reads, usually:
- `specs/akka-solution-plan.md`
- the source backlog file
- the matching task brief when one exists
- relevant cross-cutting spec files, including `*ui-style-guide*.md` for browser UI tasks
- relevant slice spec only when needed

Do not list the original PRD by default.

### Skills

List exact skills required for the task's component family.

Examples:
- entity task: `akka-event-sourced-entities`, `akka-ese-application-entity`, `akka-ese-unit-testing`
- workflow task: `akka-workflows`, `akka-workflow-component`, `akka-workflow-testing`
- view task: `akka-views`, `akka-view-from-event-sourced-entity`, `akka-view-testing`
- endpoint task: `akka-http-endpoints`, `akka-http-endpoint-component-client`, `akka-http-endpoint-testing`
- consumer task: `akka-consumers`, `akka-consumer-from-topic`, `akka-consumer-testing`
- timed action task: `akka-timed-actions`, `akka-timed-action-component`, `akka-timed-action-testing`

## Required queue entry shape

Each task must look like:

```md
### TASK-001: <short title>

- status: pending
- source: specs/backlog/01-<slice>-build-backlog.md
- task brief: none
- depends on: []
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/01-<slice>-build-backlog.md
- skills:
  - <skill-name>
- expected outputs:
  - <bounded output>
- required checks:
  - mvn test
- done criteria:
  - <specific stopping criterion>
- notes:
  - <optional note>
```

If a task is not ready because it needs a task brief first, use:

```md
- status: blocked
- notes:
  - blocked: split this backlog item with akka-backlog-item-to-task-brief before implementation
```

## Final review checklist

Before finishing, verify:
- `specs/pending-tasks.md` exists
- each runnable backlog task item has a queue entry
- no obvious duplicate queue entries were created
- obsolete non-done queue entries were superseded rather than deleted
- existing task IDs and statuses were preserved where possible
- unresolved blocking questions are reflected as blocked/omitted task work, not hidden assumptions
- dependencies are neither missing nor over-serialized
- required reads are minimal and sufficient
- skills match the component family
- required checks and done criteria are concrete
- no application code was changed

## Response style

When using this skill:
- summarize the backlog files used as input
- report whether the queue was created or repaired
- name any blocking pending questions that prevented task materialization
- name the first runnable pending task when one exists
- recommend continuing with `akka-do-next-pending-question` if questions block the queue, or a fresh context with `akka-do-next-pending-task` when tasks are ready
- do not implement code
