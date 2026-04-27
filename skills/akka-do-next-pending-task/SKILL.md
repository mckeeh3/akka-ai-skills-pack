---
name: akka-do-next-pending-task
description: Select and execute the next runnable task from specs/pending-tasks.md, preferably in a fresh context, updating the queue status when complete or blocked.
---

# Akka Do Next Pending Task

Use this skill when the user asks to continue implementation from the project's pending task queue.

Typical user prompts:
- "do the next pending task"
- "continue with the next task"
- "run the next item in specs/pending-tasks.md"
- "execute TASK-003"
- "/do-next-pending-task"

This is a queue-execution skill, not a broad planning skill.
It should execute **one task only**.

## Goal

Reliably perform one bounded follow-on task from:

```text
specs/pending-tasks.md
```

The skill must:
- select the next runnable `pending` task unless the user named a specific task
- keep the task scope bounded
- prefer fresh-context execution
- load only the task's required reads and listed skills
- generate or update the requested outputs
- run the task's required checks when possible
- update the queue status before finishing
- report any blocking pending question or the next runnable pending task

## Required reading

Read these first if present:
- `../README.md`
- `../../docs/pending-question-queue.md`
- `../../docs/pending-task-queue.md`
- `../../docs/intent-driven-usage-flow.md`
- `../../docs/solution-plan-to-implementation-queue.md`
- the target project's `specs/pending-questions.md` if it exists
- the target project's `specs/pending-tasks.md`

Then read only the selected task's `required reads` and the listed implementation skills.

Do not reread the entire PRD unless the selected task explicitly lists it as a required read or the task is blocked without it.

## Use this skill when

Use this skill when:
- `specs/pending-tasks.md` exists and the user asks to continue pending work
- a PRD/spec/backlog planning run has created queued implementation tasks
- the user names a specific task ID from the queue
- the next action should be one focused implementation run rather than new decomposition

Do **not** use this skill when:
- there is no pending-task queue yet and the user is still at the PRD/spec stage; use `akka-prd-to-specs-backlog` or `akka-solution-decomposition`
- the user asks to revise the planning structure rather than execute a task
- the user asks to implement a concrete component directly without relying on the queue; use the focused Stage 3 skills
- the task belongs to description-first app maintenance; use `app-descriptions` and companions

## Fresh-context rule

Each queue task is intended to be performed in a fresh harness context.

If this skill is invoked in a session that already contains substantial unrelated planning or coding context, and the harness cannot spawn an isolated fresh context, prefer to stop and give the user this handoff prompt:

```text
Use the Akka skills pack to do the next pending task from specs/pending-tasks.md.
Execute only that one task, load only its required reads and listed skills, update its status when finished, and report the next runnable pending task.
```

If the user explicitly asks to proceed in the current session anyway, continue only when the selected task is bounded and its required reads are clear.
Still execute only one queue item.

## Queue file contract

The canonical queue file is:

```text
specs/pending-tasks.md
```

Tasks should use this shape:

```md
### TASK-001: <short task title>

- status: pending
- source: specs/backlog/01-<slice>-build-backlog.md
- task brief: specs/tasks/01-<slice>/01-<task>.md
- depends on: []
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/01-<slice>-build-backlog.md
- skills:
  - <skill-name>
- expected outputs:
  - <output>
- required checks:
  - <check>
- done criteria:
  - <criterion>
- notes:
  - <optional note>
```

Status values:
- `pending`
- `in-progress`
- `blocked`
- `done`
- `deferred`
- `superseded`

## Task selection

If the user named a task ID:
1. find that task in `specs/pending-tasks.md`
2. verify it is not `done` unless the user explicitly asks to reopen or redo it
3. verify its dependencies are satisfied
4. execute only that task

If the user did not name a task ID:
1. read tasks in file order
2. ignore `done`, `blocked`, `deferred`, and `superseded` tasks
3. select the first `pending` task whose `depends on` list is empty or all dependencies are `done`
4. if no task is runnable, report why and do not code

If a task is `superseded`, do not execute it unless the user explicitly asks to inspect or replace it.

If `specs/pending-questions.md` exists, verify that the selected task is not blocked by unresolved `blocking` questions referenced in task notes, dependencies, source specs, or affected component areas. If it is blocked, mark or keep the task `blocked`, cite the question IDs, and recommend `akka-do-next-pending-question` instead of coding.

If a task is `in-progress` from a previous interrupted run:
- inspect notes and changed files if needed
- either continue it if it is clearly the intended active task, or ask the user whether to resume or reset it to `pending`

## Execution workflow

### 1. Identify selected task

Report the selected task ID and title before editing application files.

If no queue exists, say so and suggest one of:
- `akka-prd-to-specs-backlog` for PRD-to-planning materialization
- `akka-slice-spec-to-backlog` for slice-to-backlog creation
- direct Stage 3 skills for an already concrete coding request

### 2. Mark in progress

Before implementation edits, update the selected task in `specs/pending-tasks.md`:

```md
- status: in-progress
```

Do not mark multiple tasks in progress.

### 3. Load minimal context

Read only:
- the task's `required reads`
- the task's `task brief`, if present
- focused skills listed under `skills`
- source files directly needed to modify or test the expected outputs

Avoid broad context loading.

### 4. Execute the task

Implement exactly the selected task's scope.

Allowed:
- create or update files listed by the task
- add tests required by the task
- make small prerequisite edits that are directly necessary for compilation or test execution

Not allowed:
- starting the next queue item
- opportunistically implementing adjacent backlog items
- changing architecture beyond the task contract
- silently widening scope because more context is available

### 5. Run required checks

Run each listed `required checks` command or the closest available equivalent.

If a check cannot run, record:
- which check was not run
- why it was not run
- whether that blocks completion

### 6. Update queue status

When finished:

Mark `done` only if done criteria are satisfied:

```md
- status: done
```

Mark `blocked` if implementation cannot proceed safely:

```md
- status: blocked
```

Add a note with the exact blocker and needed user/project action.

If partially implemented but not complete, keep or set status to `blocked` rather than `done`.

### 7. Report result and next task

Final response shape:

```md
# Pending Task Result

## Executed task
- task:
- status:

## Changed outputs
- ...

## Checks
- passed:
- failed:
- not run:

## Blockers or notes
- ...

## Next pending task
- task:
- fresh-context prompt:
```

If no pending tasks remain, say so clearly.

## Blocking rules

Block instead of guessing when:
- required reads are missing
- the task has unsatisfied dependencies
- required architecture choices or blocking pending questions are unresolved
- the task conflicts with current code or specs
- a required external credential/service is unavailable and no mock/test substitute is specified
- the task cannot be completed without widening into another queue item

## Queue update discipline

When editing `specs/pending-tasks.md`:
- preserve task IDs and order
- change only the selected task's status/notes unless discovering dependency state inconsistencies
- do not renumber tasks
- do not delete completed tasks
- append new tasks only if the current task explicitly discovers required follow-on work outside its scope

## Relationship to planning skills

This skill consumes queues created by planning skills such as:
- `akka-prd-to-specs-backlog`
- `akka-slice-spec-to-backlog`
- `akka-backlog-item-to-task-brief`
- `akka-solution-decomposition` when its output has been materialized into `specs/pending-tasks.md`

It does not replace those planning skills.
It is the manual next-task execution entry point.

## Final review checklist

Before finishing, verify:
- exactly one task was selected
- the queue status was updated
- only the selected task's scope was implemented
- required reads and skills were loaded narrowly
- checks were run or explicitly reported as not run
- the next runnable pending task was identified, or the absence of one was reported
