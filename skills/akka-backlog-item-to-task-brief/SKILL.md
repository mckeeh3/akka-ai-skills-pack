---
name: akka-backlog-item-to-task-brief
description: Turn one specific item from a specs/backlog/*-build-backlog.md file into a small physical specs/tasks/... task brief that is ready for one focused harness implementation run.
---

# Akka Backlog Item to Task Brief

Use this skill when the repository already has a slice spec and build backlog, but one backlog item is still too large, too cross-cutting, or too ambiguous to hand directly to code generation.

This is the **leaf planning skill** below `akka-prd-to-specs-backlog` and `akka-slice-spec-to-backlog`.

## Goal

Create or update one small task brief under `specs/tasks/` that:
- maps to exactly one slice and one backlog item
- narrows scope to one focused implementation run
- names the smallest required reads
- makes non-goals explicit
- lists the exact Akka components and skills to load
- lists expected code/test outputs
- gives done criteria clear enough for a harness to stop at the right boundary
- creates or updates the corresponding queue entry in `specs/pending-tasks.md`

## Use this skill when

The task sounds like one of these:
- "Turn backlog item 3 into a task brief"
- "This backlog item is still too big; split it again"
- "Write a focused implementation brief for slice 02 notifications"
- "Create a task file under specs/tasks for this backlog item"
- "Narrow this backlog into one harness-sized coding task"

Do **not** use this skill when:
- the user is still at PRD level and needs slices or backlogs
- the backlog item is already small and unambiguous enough to code directly
- the user wants implementation code now rather than one more planning cut

## Relationship to other skills

This skill assumes the broader planning layers already exist:
- `specs/akka-solution-plan.md`
- one or more `specs/cross-cutting/*.md` files
- one target `specs/slices/*.md` file
- one matching `specs/backlog/*-build-backlog.md` file

This skill should normally be used after:
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../akka-slice-spec-to-backlog/SKILL.md`

## Required reading

Read these first if present:
- `../README.md`
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../akka-slice-spec-to-backlog/SKILL.md`
- `../../specs/README.md`
- `../../specs/backlog/README.md`
- `../../specs/tasks/README.md`
- `../../specs/pending-tasks.md` if it already exists
- `../../docs/pending-task-queue.md`
- `../../specs/templates/implementation-task-template.md`
- `../../specs/akka-solution-plan.md`
- the target slice spec under `../../specs/slices/`
- the target backlog file under `../../specs/backlog/`
- any cross-cutting spec files referenced by that slice or backlog item

If a matching task brief already exists:
- read it first
- refine it instead of duplicating it
- preserve numbering and naming consistency within the slice task directory

## What this skill must produce

For one specific backlog item, produce one matching task brief such as:
- source backlog: `specs/backlog/01-fleet-visibility-build-backlog.md`
- target task brief: `specs/tasks/01-fleet-visibility-mvp/01-shared-foundations-and-tenant-settings.md`

Also create or update the matching task entry in:
- `specs/pending-tasks.md`

The brief should normally correspond to:
- one item from `Suggested harness task breakdown`
- or one smaller child task split from an oversized backlog item

## Required task brief content

Each task brief must include:
1. Purpose
2. Reads
3. Scope
4. Non-goals
5. Akka components involved
6. Skills to load
7. Expected outputs
8. Required tests
9. Done criteria

The matching `specs/pending-tasks.md` entry must include the task brief path, required reads, skills, expected outputs, required checks, and done criteria from the brief.

## Mapping rules

### Backlog item to task brief mapping
- `specs/backlog/01-foo-build-backlog.md` -> `specs/tasks/01-foo/...`
- preserve the slice number and stem in the task directory name
- use stable, short task filenames that describe the bounded work

Examples:
- `specs/tasks/01-fleet-visibility-mvp/01-shared-foundations-and-tenant-settings.md`
- `specs/tasks/01-fleet-visibility-mvp/02-customer-and-site-kves.md`
- `specs/tasks/03-consumables-ordering/03-order-workflow-core.md`

### Scope preservation
The task brief must not silently widen the backlog item.

Allowed:
- narrowing one backlog item into a smaller implementation contract
- making file outputs explicit
- clarifying exact tests and stopping conditions
- splitting one oversized backlog item into two or more task briefs when necessary

Not allowed:
- pulling in adjacent backlog items just because they are related
- expanding the task into later slices
- turning one focused task into a mini-backlog

## Sizing rules

A good task brief is usually one of these:
- one shared domain/common package task
- one entity or workflow plus its immediate domain records
- one consumer or timed action plus its tests
- one view family plus its tests
- one endpoint family plus its tests
- one tightly bounded integration adapter task

A task brief is too large if it still spans:
- multiple unrelated component families
- too many files for one focused run
- both broad domain modeling and multiple downstream delivery layers at once
- unresolved architecture questions

If that happens, split again before coding.
Only add queue entries that are small enough for one focused harness run; if the work remains too broad, create multiple task briefs or mark the corresponding queue item `blocked` with the exact split needed.

## Naming rules

Keep numbering aligned inside each slice task directory:
- `specs/tasks/01-foo/01-...md`
- `specs/tasks/01-foo/02-...md`

Use names that describe the implementation boundary, not just the business area.

Prefer names like:
- `shared-foundations-and-tenant-settings`
- `alert-entity-and-lifecycle-tests`
- `order-workflow-core`
- `report-endpoints-and-export-tests`

Avoid names like:
- `misc`
- `part-2`
- `backend-work`

## Anti-patterns

Avoid:
- rewriting the whole backlog item without narrowing it
- omitting non-goals
- omitting tests
- listing broad outputs like "implement service management"
- leaving the skill list too vague for the next run
- creating task briefs for every backlog item even when the backlog item is already small enough

## Final review checklist

Before finishing, verify:
- the task brief points to the correct slice and backlog
- the scope is smaller than the backlog item, not larger
- the reads are the minimum needed
- non-goals are explicit
- outputs are specific files or a tightly bounded file family
- required tests are named clearly
- the listed skills match the task's component type
- done criteria define a clear stopping point
- `specs/pending-tasks.md` has a matching entry or updated existing entry for this task brief
- existing queue task IDs and statuses are preserved

## Example invocation patterns

Use prompts like:
- "Read `specs/backlog/01-fleet-visibility-build-backlog.md` and turn task item 1 into a task brief under `specs/tasks/01-fleet-visibility-mvp/`."
- "The `Ingestion endpoint` item in `specs/backlog/01-fleet-visibility-build-backlog.md` is still too large. Split it into one focused task brief."
- "Create a `specs/tasks/...` brief for the `order workflow core` work in `specs/backlog/03-consumables-ordering-build-backlog.md`."
- "Refine the existing task brief for `shared-foundations-and-tenant-settings` so it is implementable in one harness run."

A good response should:
- identify the source backlog item
- show the target `specs/tasks/...` path
- preserve slice scope
- tighten the task until one focused implementation run is realistic

## Response style

When using this skill:
- name the source backlog file and target backlog item first
- summarize how you are narrowing it
- then write the task brief file
- update `specs/pending-tasks.md` for the narrowed task
- clearly report which files were added or updated
- do not jump into implementation code
