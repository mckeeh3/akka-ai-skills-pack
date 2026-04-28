---
name: akka-slice-spec-to-backlog
description: Turn one existing specs/slices/*.md file into a matching specs/backlog/*-build-backlog.md file with package layout, class list, endpoint list, design notes, tests, implementation order, and harness-sized task breakdown.
---

# Akka Slice Spec to Backlog

Use this skill when the repository already has a slice spec and the next task is to convert that slice into a concrete build backlog for implementation.

This is a narrower follow-on planning skill than `akka-prd-to-specs-backlog`.

## Goal

Create or update a single backlog file that is tightly aligned with one existing slice spec and is detailed enough to drive focused coding sessions.
The backlog should end in bounded harness-sized task items, not just class lists and prose.
Also create or update matching entries in `specs/pending-tasks.md` so follow-on implementation can proceed one task per fresh context.

## Use this skill when

The task sounds like one of these:
- "Turn this slice spec into an implementation backlog"
- "Write the build backlog for slice 03"
- "Expand this slice into class-level tasks"
- "Generate a harness-friendly backlog from this spec"

Do **not** use this skill when the user is still at the PRD stage and needs the overall slice structure. Use `akka-prd-to-specs-backlog` for that.

## Relationship to other skills

This skill assumes these already exist or are mostly settled:
- `specs/akka-solution-plan.md`
- one or more `specs/cross-cutting/*.md` files
- a target `specs/slices/*.md` file

This skill should be used after:
- `../akka-prd-to-specs-backlog/SKILL.md`
- or after manual slice creation

## Required reading

Read these first if present:
- `../README.md`
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../../specs/README.md`
- `../../specs/backlog/README.md`
- `../../specs/tasks/README.md`
- `../../specs/pending-tasks.md` if it already exists
- `../../docs/pending-task-queue.md`
- `../../specs/templates/build-backlog-template.md`
- `../../specs/templates/implementation-task-template.md`
- the target slice spec file under `../../specs/slices/`
- any cross-cutting spec files referenced or obviously relevant to the slice
- `../../specs/cross-cutting/*ui-style-guide*.md`, `../../app-description/55-ui/style-guide.md`, or equivalent style artifact when the slice includes browser UI work
- `../../specs/akka-solution-plan.md`

If a matching backlog file already exists:
- read it first
- update it rather than duplicating it
- preserve numbering and naming consistency

## What this skill must produce

For one target slice file such as:
- `specs/slices/03-consumables-ordering.md`

produce the matching backlog file:
- `specs/backlog/03-consumables-ordering-build-backlog.md`

Also create or update:
- `specs/pending-tasks.md`

## Required backlog content

The backlog file must include:
1. Purpose
2. Delivery goal
3. Recommended package layout additions
4. Class-by-class file list
5. Concrete endpoint list
6. Write-model design decisions
7. View/workflow/consumer/timer/integration design as relevant
8. Web UI style-guide dependency and selected theme when the slice includes browser UI work
9. Test plan by file/class
10. Implementation order
11. Suggested harness task breakdown
12. Done criteria
13. Explicit defer list

The suggested harness task breakdown is the default leaf layer for implementation.
Each item should be small enough to become one focused implementation prompt without reopening the full PRD.

For each bounded item in the suggested harness task breakdown, add or update a corresponding task in `specs/pending-tasks.md` using `../../docs/pending-task-queue.md`.
If a bounded item implements browser UI and style is unresolved, do not make it runnable; add/update a `specs/pending-questions.md` style-selection question using `../../docs/web-ui-style-guide.md` and mark only the affected UI task as blocked or defer it with an explicitly accepted default.
Preserve existing task IDs and statuses when updating an existing queue.

## Mapping rules

### Slice to backlog name mapping
- `specs/slices/01-foo.md` -> `specs/backlog/01-foo-build-backlog.md`
- `specs/slices/02-bar.md` -> `specs/backlog/02-bar-build-backlog.md`

### Scope preservation
The backlog must not silently widen the slice.

Allowed:
- adding implementation detail
- adding class names
- adding tests
- clarifying dependencies

Not allowed:
- pulling in adjacent product areas unless the slice already implies them
- turning one slice into a whole-program backlog

### Detail level
The backlog should be detailed enough for several small harness runs, but not so detailed that it becomes source code.
If one task item still spans multiple unrelated component families or too many files, call that out and recommend a further task-brief decomposition before coding.
Do not add an oversized item to `specs/pending-tasks.md` as if it were ready; either split it into smaller pending tasks or mark the queue item `blocked` with a note that a task brief is required.

## Sizing rules

A good backlog usually supports independent tasks such as:
- one domain package or config model
- one entity or workflow
- one consumer or timed action
- one view family
- one endpoint family
- one test family

## Anti-patterns

Avoid:
- restating the slice spec without implementation detail
- omitting tests or done criteria
- inventing classes unrelated to the slice's purpose
- widening scope to future slices
- breaking slice/backlog numbering consistency

## Final review checklist

Before finishing, verify:
- the backlog filename matches the slice filename by number and stem
- the backlog references the right prerequisite specs
- the class list fits the slice scope
- the endpoint list fits the slice scope
- UI tasks include the selected style guide in required reads, or are blocked by the style-selection question
- the tests cover entity/workflow/view/endpoint behavior as applicable
- the harness task breakdown is composed of bounded operations
- any oversized task item is explicitly marked for further decomposition before coding
- `specs/pending-tasks.md` has matching queue entries for runnable harness tasks
- existing queue task IDs and statuses are preserved
- the defer list is explicit

## Response style

When using this skill:
- name the target slice first
- summarize the backlog sections you will create or update
- then write the backlog file
- clearly report which backlog file was added or updated
- clearly report whether `specs/pending-tasks.md` was added or updated
- name the first runnable pending task for the slice when one exists
- do not jump into implementation code
