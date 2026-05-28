---
name: project-discussed-idea-to-pending-project
description: Project-only skill for this akka-ai-skills-pack source repo. Create a self-contained specs/<initiative>/ mini-project with supporting docs and a pending-tasks.md queue when an idea, feature, concept, migration, or remediation for this repository has already been discussed and is ready to implement one task per fresh harness session.
---

# Project Discussed Idea to Pending Project

This is a **project-only harness skill for the `akka-ai-skills-pack` source repository**.
It is not part of the installable skills pack and must not be registered in `pack/manifest.yaml` or documented as a downstream generated-app skill.

Use it when a repository-maintenance idea, feature, concept, migration, remediation, review finding, or follow-up initiative for this source repo has already been discussed enough to implement and should be captured as a durable mini-project under `specs/`.

The output is planning and queue materialization only. Do not implement the queued tasks in the same run unless the user explicitly asks to execute one task after the queue exists.

## Use this skill when

Use when the prompt sounds like:
- "we discussed this; create pending tasks"
- "turn this concept into a task queue"
- "make a specs mini-project for this repo"
- "create a pending-tasks directory for this work"
- "this is ready to implement; break it into fresh-session tasks"
- "capture our discussion as a plan and queue"

## Project scope guardrail

This repository develops the skills pack. The mini-project usually concerns source assets such as:
- `skills/`
- `docs/`
- `pack/`
- `templates/`
- `specs/`
- examples, tests, packaging, install scripts, or repository guidance

Do not treat the repo-local mini-project as an end-user Akka application plan unless the user explicitly says it is an example app, starter template, or generated-app reference asset in this repository.

Do not add this project-only skill to:
- `skills/`
- `skills/README.md`
- `pack/manifest.yaml`
- packaged `dist/` content

Those are installable-pack assets intended for other projects.

## Required reading

Read only the smallest useful context:
- `AGENTS.md`
- `skills/README.md`
- `docs/pending-task-queue.md` for queue mechanics
- `docs/pending-question-queue.md` when unresolved decisions may block tasks
- `docs/ai-first-saas-application-architecture.md` and `docs/capability-first-backend-architecture.md` only when the initiative affects generated-app doctrine, agent workstreams, governed capabilities, security, UI, agents, audit, or runtime completion semantics
- the conversation notes, issue, prompt, draft spec, review finding, or source files named by the user
- related existing `specs/*/README.md` and `specs/*/pending-tasks.md` as pattern references

Prefer source artifacts that capture the actual discussion. Do not reread the whole repository unless the initiative is a whole-pack migration or review.

## Output directory shape

Create a self-contained mini-project under:

```text
specs/<kebab-case-initiative>/
```

Recommended files:

```text
README.md
conversation-capture.md
pending-tasks.md
sprints/
  01-<sprint-name>-sprint.md
backlog/
  01-<sprint-name>-build-backlog.md
tasks/
  01-<sprint-name>/
    01-<task-name>.md
```

For very small initiatives, `sprints/`, `backlog/`, or `tasks/` may be omitted only when `pending-tasks.md` remains fully executable without them. For multi-session work, prefer keeping all three because future fresh contexts need supporting docs in addition to the queue.

## File purposes

### `README.md`

Capture:
- purpose and background;
- source discussion or trigger;
- scope and non-goals;
- affected repository areas;
- execution model: one task per fresh harness session;
- read order for future task sessions;
- sprint sequence;
- done state;
- open concerns or recommendations.

### `conversation-capture.md`

Summarize:
- user goals and decisions already made;
- accepted constraints;
- rejected alternatives or non-goals;
- risks;
- unresolved questions, if any.

Do not invent decisions. If an important decision is unresolved, create or reference a pending question and block affected tasks.

### `sprints/*.md`

Define implementation phases with objective, scope, source context, ordered work areas, acceptance criteria, and handoff notes.

### `backlog/*.md`

Translate each sprint into harness-sized work. Include goal, implementation notes, suggested harness task breakdown, dependencies, required checks, and acceptance criteria.

### `tasks/**/*.md`

Write fresh-session task briefs with exact objective, required reads, in-scope/out-of-scope work, expected outputs, skills, checks, done criteria, and commit message convention.

## `pending-tasks.md` contract

Use this queue shape inside the mini-project directory. Every generated queue must include a terminal verification work task after the planned implementation/review tasks. If verification finds gaps, append new bounded tasks before a new terminal verification task so the queue repeats the task-group loop until the mini-project is complete.

```md
# Pending Tasks: <Initiative Name>

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `<prefix>: <short task title>`.

## Tasks

### TASK-<PREFIX>-00-001: Create <initiative> planning scaffold

- status: done
- source: <discussion, request, issue, or review finding>
- task brief: specs/<initiative>/tasks/00-planning/00-create-<initiative>-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - <source discussion/spec files>
- skills:
  - none; repository planning task
- expected outputs:
  - specs/<initiative>/README.md
  - specs/<initiative>/conversation-capture.md
  - specs/<initiative>/pending-tasks.md
  - specs/<initiative>/sprints/*.md
  - specs/<initiative>/backlog/*.md
  - specs/<initiative>/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlogs, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `<prefix>: add <initiative> queue`
```

If the planning scaffold is created in the current session but not committed, mark the scaffold task `pending` or `in-progress`, not `done`. If the current session commits the scaffold, mark it `done` and record the commit message.

For implementation tasks, each queue entry must include status, source, task brief, dependencies, minimal reads, exact skills, bounded outputs, checks, done criteria, and notes for commit message/blockers/supersession.

Every initial queue must end with a verification task, for example:

```md
### TASK-<PREFIX>-99-001: Verify <initiative> completion

- status: pending
- source: mini-project verification loop
- task brief: specs/<initiative>/tasks/99-verification/01-verify-<initiative>-completion.md
- depends on:
  - <all planned implementation/review task ids>
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/<initiative>/README.md
  - specs/<initiative>/conversation-capture.md
  - specs/<initiative>/pending-tasks.md
  - specs/<initiative>/sprints/*.md
  - specs/<initiative>/backlog/*.md
  - specs/<initiative>/tasks/**/*.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/<initiative>/pending-tasks.md
  - completion summary, verification notes, or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - any checks needed to validate the mini-project done state
- done criteria:
  - task group/sprint goals have been compared against completed work
  - mini-project done state has been compared against completed work
  - unresolved questions/blockers have been reviewed
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
- notes:
  - commit message: `<prefix>: verify <initiative> completion`
```

## Verification loop

The task queue must implement a repeatable task-group loop:

1. plan bounded tasks;
2. execute queued work one task per fresh harness context;
3. run a verification work task at the end of the current task group;
4. verify both the current task group/sprint goals and the overall mini-project done state;
5. when gaps remain, append more bounded tasks and then append a new terminal verification task;
6. repeat until the mini-project is complete.

A verification task must:
- assess whether the current task group or sprint goals are complete;
- assess whether the overall mini-project done state in `README.md` has been achieved;
- compare completed work against sprint/backlog goals, task done criteria, conversation-capture decisions, unresolved questions, and blockers;
- run or require appropriate validation checks for the stated scope;
- append new bounded tasks when material gaps remain;
- append a new terminal verification task after any newly added work;
- record completion only when no material gaps remain.

Keep verification bounded to the mini-project's stated scope and done state. Do not turn the verification task into a whole-repository review unless the mini-project is explicitly a whole-repository review. If unrelated issues are discovered, record them as recommendations or propose a separate mini-project rather than silently expanding scope.

## Task sizing rules

Each task must be executable in one fresh harness context.

Good task boundaries:
- one doctrine/routing update plus focused consistency checks;
- one skill family update plus tests/search proof;
- one reference slice or example update;
- one backend component family plus direct tests;
- one frontend surface/component family plus typecheck/tests/build;
- one review/audit task that produces findings and follow-up queue edits.

Split or block tasks that are too broad, such as "fix all skills", "make starter production ready", "migrate all UI", or "implement the whole feature".

Generated-app/reference-runtime tasks in this repository must preserve the runtime completion doctrine: do not mark feature work complete unless the real local runtime/API/UI path works at the stated scope, or the task is explicitly non-runtime/docs-only.

## Status and commit rules

Future task sessions must:
1. mark exactly one selected task `in-progress` before implementation edits;
2. execute only that task;
3. run required checks or block with a clear reason;
4. mark `done` only when checks and done criteria pass;
5. create one focused git commit for completed work;
6. record the commit message or hash in task notes;
7. report the next runnable task.

Do not mark a task `done` without a commit unless the queue explicitly says the task is non-mutating review-only and the user accepts no commit. For this repository's normal mini-projects, prefer committing review outputs too.

## Planning workflow

1. Name the initiative with a stable kebab-case directory and short task prefix.
2. Capture the discussion and decisions in `conversation-capture.md`.
3. Define done state and non-goals in `README.md`.
4. Split work into ordered sprints only as much as needed.
5. Write backlog files with suggested harness task breakdowns.
6. Write task briefs for non-trivial queue items, including the terminal verification task brief.
7. Create `pending-tasks.md` with the scaffold task, implementation/review tasks, and a final verification task.
8. Validate that the first non-done task is runnable without guessing.
9. Validate that the final verification task can determine whether task-group and mini-project goals are complete, and can append follow-up tasks plus a new terminal verification task when needed.
10. Run `git diff --check`.
11. If committing the scaffold now, commit only the mini-project files and queue-status update.

## Final response

Report:
- created mini-project path;
- supporting docs created;
- number of tasks, including the terminal verification task, and first runnable task;
- whether the planning scaffold was committed;
- questions, concerns, or recommendations.

End with a fresh-context handoff prompt for the next task, for example:

```text
Use the project-discussed-idea-to-pending-project queue at specs/<initiative>/pending-tasks.md.
Execute only the next runnable task, load only its required reads and listed skills, update the queue, commit the task changes, and report the next runnable task.
```
