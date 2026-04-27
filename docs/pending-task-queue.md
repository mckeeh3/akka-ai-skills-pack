# Pending task queue

Use this contract when PRD/spec planning creates follow-on implementation work that should be executed reliably across separate harness sessions.

Purpose:
- persist follow-on work after decomposition, backlog creation, task-brief creation, and resolution or deferral of blocking questions
- make the next runnable task obvious
- keep each implementation run bounded to one task
- support fresh-context execution for each task
- record whether tasks are pending, blocked, deferred, done, or superseded

## Canonical location

In a target application project, use:

```text
specs/pending-tasks.md
```

This file belongs in the target project workspace, not inside the installed `.agents/` pack.

If a project already has an equivalent issue tracker or task queue, the harness may map this contract onto that system, but the markdown file is the default portable representation.

## Queue rules

1. Execute one queue task per harness run.
2. Prefer a fresh context session for every task.
3. Do not combine adjacent tasks just because their files are nearby.
4. Select the first `pending` task whose dependencies are `done` or empty.
5. Mark a task `done` only after its required checks pass or are explicitly reported as not runnable.
6. Mark a task `blocked` when required decisions, pending questions, inputs, dependencies, or build/runtime preconditions are missing.
7. Mark a task `deferred` only when the user or plan explicitly chooses to postpone it.
8. Mark a task `superseded` when a later app-description/spec/PRD change replaces the task and it should not be executed.
9. Keep the queue stable: append new tasks or update statuses; do not renumber existing task IDs casually.
10. When a task is complete, blocked, or superseded, report the next runnable pending task if one exists.
11. At the end of ordinary harness responses, remind the user when runnable pending tasks remain, without automatically starting them.

## Status values

Use these exact status values:

- `pending` — ready or potentially ready to execute when dependencies are satisfied
- `in-progress` — currently being executed in this harness run
- `blocked` — cannot proceed without a decision, dependency, missing input, or failed prerequisite
- `done` — completed and validated as far as the task requires
- `deferred` — intentionally postponed and not eligible for automatic next-task selection
- `superseded` — replaced by a later requirement, spec, backlog, or task and not eligible for execution

## Task ID format

Use stable, sortable task IDs:

```text
TASK-001
TASK-002
TASK-003
```

For large multi-slice plans, a slice-prefixed ID is also acceptable:

```text
TASK-01-001
TASK-01-002
TASK-02-001
```

Choose one format per project and keep it consistent.

## Required queue shape

Use this structure:

```md
# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.

## Tasks

### TASK-001: <short task title>

- status: pending
- source: specs/backlog/01-<slice>-build-backlog.md
- task brief: specs/tasks/01-<slice>/01-<task>.md
- depends on: []
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/01-<slice>-build-backlog.md
  - specs/tasks/01-<slice>/01-<task>.md
- skills:
  - <skill-name>
- expected outputs:
  - <file, package, component, or test family>
- required checks:
  - <test command or verification step>
- done criteria:
  - <observable completion criterion>
- notes:
  - <optional assumptions, constraints, provenance, or links>
  - source requirement ids: <optional stable requirement IDs when available>
  - source capability ids: <optional app-description capability IDs when available>
  - supersedes: <optional task IDs this task replaces>
  - superseded by: <optional replacement task ID when this task is superseded>
```

If no separate task brief exists, omit `task brief:` or set it to `none` and make the `source` backlog item specific enough to execute.

## Selection algorithm

To choose the next task:

1. Read `specs/pending-tasks.md`.
2. Ignore tasks with status `done`, `blocked`, `deferred`, or `superseded`.
3. For each remaining `pending` task in file order, inspect `depends on`.
4. Select the first task whose dependencies are empty or all refer to tasks with status `done`.
5. If no task is runnable, report the earliest blocked dependency chain instead of coding.

## End-of-response reminder

When `specs/pending-tasks.md` exists and runnable pending tasks remain, end responses with a short reminder unless the response is only a trivial clarification or the user asked not to receive reminders.

Use this shape:

```md
Pending tasks remain.

Next runnable task:
- <TASK-ID>: <title>

To continue reliably, start a fresh context and ask:
"Use akka-do-next-pending-task to execute the next pending task from specs/pending-tasks.md."
```

Do not automatically execute the next task unless the user asks to continue implementation.

## Fresh-context handoff prompt

When the next task should be run in a new context, use a prompt like:

```text
Use the Akka skills pack to do the next pending task from specs/pending-tasks.md.
Execute only that one task, load only its required reads and listed skills, update its status when finished, and report the next runnable pending task.
```

For a specific task:

```text
Use the Akka skills pack to execute TASK-001 from specs/pending-tasks.md in a fresh context.
Do not work on any other queue item. Update the queue before finishing.
```

## Updating status

Before coding, update the selected task to:

```md
- status: in-progress
```

After coding and validation, update to:

```md
- status: done
```

If blocked, update to:

```md
- status: blocked
- notes:
  - blocked: <reason and exact user decision/input needed>
```

If replaced by later requirements or specs, update to:

```md
- status: superseded
- notes:
  - superseded: <replacement task/spec and why the old task should not run>
```

Preserve useful prior notes when adding blocked, superseded, or completion notes.

## Relationship to pending questions

`specs/pending-questions.md` is the durable clarification queue for unresolved decisions.
`specs/pending-tasks.md` is the durable implementation queue.

Do not create or execute runnable implementation tasks for work blocked by unresolved `blocking` questions unless those questions have been explicitly deferred with an accepted default or limitation.

When a task is blocked by a question, record the question ID in `notes`, for example:

```md
- notes:
  - blocked by Q-003: approval deadline behavior is unresolved
```

If a task discovers an unresolved design decision during execution, block the task and add or update `specs/pending-questions.md` instead of guessing.

## Relationship to other planning artifacts

- `specs/akka-solution-plan.md` defines the overall architecture and implementation order.
- `specs/slices/*.md` define bounded business slices.
- `specs/backlog/*-build-backlog.md` define implementation-ready slice work.
- `specs/tasks/**/*.md` optionally narrow oversized backlog items.
- `specs/pending-tasks.md` is the executable queue index across those artifacts.

## Related skills and docs

- `../skills/akka-pending-question-generation/SKILL.md`
- `../skills/akka-do-next-pending-question/SKILL.md`
- `../skills/akka-pending-question-queue-maintenance/SKILL.md`
- `../skills/akka-do-next-pending-task/SKILL.md`
- `../skills/akka-pending-task-queue-maintenance/SKILL.md`
- `../skills/akka-change-request-to-spec-update/SKILL.md`
- `../skills/akka-revised-prd-reconciliation/SKILL.md`
- `../skills/akka-backlog-to-pending-tasks/SKILL.md`
- `../skills/akka-prd-to-specs-backlog/SKILL.md`
- `../skills/akka-slice-spec-to-backlog/SKILL.md`
- `../skills/akka-backlog-item-to-task-brief/SKILL.md`
- `examples/purchase-request-pending-tasks.md`
- `solution-plan-to-implementation-queue.md`
- `intent-driven-usage-flow.md`
