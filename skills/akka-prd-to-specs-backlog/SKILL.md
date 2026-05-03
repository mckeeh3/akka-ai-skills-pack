---
name: akka-prd-to-specs-backlog
description: Turn a PRD or other high-level requirements artifact into a repo-ready planning package; master Akka solution plan, cross-cutting specs, module/sprint specs for large inputs or slice specs for smaller inputs, numbered build backlogs, and execution-order docs under specs/.
---

# Akka PRD to Specs Backlog

Use this skill when the user does not just want an Akka component plan, but wants the plan materialized into the repository as a harness-friendly `specs/` tree.

This is a **project-specific planning skill** that builds on the ideas in `akka-solution-decomposition` and continues all the way to implementation-ready planning artifacts.

## Goal

Generate a consistent planning package from a PRD, requirements document, or high-level feature set that:
- produces a master Akka solution plan
- for large inputs, splits the plan into module-oriented vertical sprint specs
- for smaller inputs, splits the plan into bounded vertical slice specs
- turns each sprint or slice into a build backlog suitable for one or more independent harness operations
- creates or updates `specs/pending-questions.md` when unresolved decisions should be answered before safe task generation
- creates or updates `specs/pending-tasks.md` as the durable execution queue when follow-on implementation work is sufficiently unblocked
- optionally materializes leaf task briefs when a backlog item is still too large for a single focused harness run
- writes index files that make execution order and dependencies explicit
- keeps files small enough to support focused downstream coding sessions

## Use this skill when

The task sounds like one of these:
- "I have a PRD. How do I build this in Akka?"
- "Turn this requirements doc into an implementation plan"
- "Break this PRD into harness-friendly tasks"
- "Create specs and backlogs from this product doc"
- "Write a master plan plus module/sprint specs and backlog files"
- "Write a master plan plus slice specs and backlog files"

Do **not** use this skill when the user already has a settled slice/backlog and wants code directly. In that case, use the focused Stage 3 implementation skills.

## Relationship to other skills

This skill sits above normal decomposition.

Use it as:
1. high-level repo planning entry point
2. repository materialization step
3. handoff generator for downstream coding work

It should reuse the reasoning shape of:
- `../akka-solution-decomposition/SKILL.md`

Then continue into repo file generation.

## Required reading

Read these first if present:
- `../README.md`
- `../akka-solution-decomposition/SKILL.md`
- `../../specs/README.md`
- `../../specs/backlog/README.md`
- `../../specs/tasks/README.md`
- `../../specs/pending-questions.md` if it already exists
- `../../specs/pending-tasks.md` if it already exists
- `../../docs/pending-question-queue.md`
- `../../docs/pending-task-queue.md`
- `../../docs/module-sprint-planning.md` when the input is large, multi-module, or includes backend plus frontend delivery
- `../../docs/web-ui-style-guide.md` when browser UI is in scope
- `../../specs/akka-solution-plan.md` if it already exists
- `../references/akka-entity-comparison.md`

If the user provided a path to a PRD or requirements file:
1. read that file completely
2. extract capabilities, actors, commands, queries, workflows, timers, integrations, security constraints, and UI needs
3. then generate the file set

If `specs/` already exists:
- preserve numbering consistency where possible
- update indexes rather than duplicating them
- keep names aligned with the existing module/sprint/backlog or slice/backlog naming pattern

## What this skill must produce

At minimum, create or update these files under `specs/`:

### Top-level
- `specs/akka-solution-plan.md`
- `specs/README.md`
- `specs/pending-questions.md` when blocking or important unresolved decisions exist
- `specs/pending-tasks.md` when follow-on implementation work is sufficiently unblocked

### Cross-cutting specs
Create only the ones justified by the PRD, but prefer these when broadly applicable:
- `specs/cross-cutting/00-common-domain-and-conventions.md`
- `specs/cross-cutting/01-auth-tenancy-audit.md`
- `specs/cross-cutting/02-ui-style-guide.md` when a browser UI is in scope and style is selected
- `specs/cross-cutting/03-<integration-or-platform-concern>.md`

### Module and sprint specs for larger PRDs
When the input is large, multi-module, or includes meaningful backend plus frontend delivery, prefer:
- `specs/modules/01-<module-name>.md`
- `specs/modules/02-<module-name>.md`
- `specs/sprints/01-<sprint-name>-sprint.md`
- `specs/sprints/02-<sprint-name>-sprint.md`

Module specs define durable boundaries. Sprint specs define ordered vertical full-stack delivery increments. See `../../docs/module-sprint-planning.md`.

### Vertical slice specs for smaller plans
For smaller plans, or when preserving an existing project shape, create numbered files such as:
- `specs/slices/01-<slice-name>.md`
- `specs/slices/02-<slice-name>.md`
- `specs/slices/03-<slice-name>.md`

Do not create both `specs/slices/` and `specs/sprints/` for new planning output unless project history requires it.

### Build backlogs
Create matching numbered files such as:
- `specs/backlog/README.md`
- `specs/backlog/01-<sprint-or-slice-name>-build-backlog.md`
- `specs/backlog/02-<sprint-or-slice-name>-build-backlog.md`
- `specs/backlog/03-<sprint-or-slice-name>-build-backlog.md`

### Optional leaf task briefs
Create these only when a backlog item would still be too large or too ambiguous for one focused harness run:
- `specs/tasks/README.md`
- `specs/tasks/01-<sprint-or-slice-name>/01-<task-name>.md`
- `specs/tasks/01-<sprint-or-slice-name>/02-<task-name>.md`

## Output contract

This skill is complete only when a future harness run can:
- read `specs/pending-tasks.md`
- select the next runnable task
- read the relevant module and sprint spec, or the relevant slice spec for smaller plans
- read the matching backlog
- implement a bounded piece of work without rereading the entire PRD

If the backlog is still too broad for that, either:
- tighten the backlog's harness task breakdown, or
- create leaf task briefs under `specs/tasks/`

If the output is still too broad for that, the skill has not decomposed far enough.

## Standard repository shape

Prefer this structure, choosing either `modules/` + `sprints/` for large plans or `slices/` for smaller/existing slice-based plans:

```text
specs/
  README.md
  akka-solution-plan.md
  pending-questions.md  # when unresolved decisions should be queued
  pending-tasks.md      # when implementation work is sufficiently unblocked
  cross-cutting/
    00-common-domain-and-conventions.md
    01-auth-tenancy-audit.md
    ...
  modules/             # use with sprints for large multi-module PRDs
    01-....md
    02-....md
  sprints/             # use with modules for vertical full-stack module sprints
    01-....-sprint.md
    02-....-sprint.md
  slices/              # alternative to sprints for smaller plans or existing slice-based projects
    01-....md
    02-....md
    ...
  backlog/
    README.md
    01-....-build-backlog.md
    02-....-build-backlog.md
    ...
  tasks/               # optional leaf layer for extra-large sprints or slices
    README.md
    01-<sprint-or-slice-name>/
      01-<task-name>.md
      02-<task-name>.md
```

## Decomposition workflow

### 1. Produce the master solution plan

Start with the same architecture reasoning as `akka-solution-decomposition`.

The master plan must include:
1. Inputs
2. Capability summary
3. Chosen components
4. Why each component exists
5. Skill routing
6. Open questions and assumptions
7. Recommended implementation order
8. Required tests

Write that to:
- `specs/akka-solution-plan.md`

### 2. Identify cross-cutting concerns

Separate concerns that should not be duplicated across modules, sprints, or slices, such as:
- ID and domain conventions
- tenancy and auth rules
- audit rules
- ERP integration model
- notification delivery model
- web UI style guide/theme, design tokens, mode policy, and brand adaptation when browser UI is in scope
- export/reporting conventions

Create one file per cross-cutting concern when it affects multiple modules, sprints, or slices.

### 3. Split into module-oriented sprints or harness-friendly slices

For large PRDs, prefer module-oriented vertical sprint planning:
1. identify durable app modules and write `specs/modules/NN-<module>.md` files
2. define ordered vertical delivery sprints and write `specs/sprints/NN-<sprint>-sprint.md` files
3. make each sprint testable through its backend and frontend surface when UI is in scope
4. keep cross-cutting foundation work explicit rather than duplicating it in every module

A good module spec contains boundaries, owned capabilities, actors, state ownership, UI area, integrations, and related sprints.

A good sprint spec contains:
- one module or tightly related module increment
- backend scope: entities, workflows, views, consumers, timers, endpoints
- frontend scope: screens, forms, navigation, API client calls, realtime behavior
- acceptance behavior and module-level tests
- pending questions and explicit defer list
- done criteria that include full-stack smoke/integration validation when applicable

For smaller plans, create vertical slices that are:
- independently meaningful to the business
- small enough for focused implementation
- ordered by dependency
- clear about what they intentionally exclude

Avoid increments that are either:
- too broad: "build the whole platform"
- too tiny: "add one enum"
- layer-only for large PRDs: "all entities" or "all UI"

### 4. Turn each sprint or slice into a build backlog

For each sprint or slice, create a matching backlog file that includes:
- purpose
- delivery goal
- package layout additions if needed
- class-by-class file list
- endpoint list
- write-model decisions
- workflow/view/consumer/timer design notes as relevant
- test plan by class/family
- implementation order
- suggested harness task breakdown
- done criteria
- explicit defer list

The suggested harness task breakdown is the default leaf layer.
Each task item should be phrased as a bounded independent implementation prompt. For module sprints, include a final module-level full-stack smoke/integration task when backend plus frontend or multiple backend surfaces must work together.

### 5. Materialize optional leaf task briefs when needed

Create physical task files under `specs/tasks/` only when at least one backlog task item is still too large, too ambiguous, or too cross-cutting for a single focused harness run.

A good task brief should contain:
- purpose
- required reads
- exact scope
- explicit non-goals
- Akka components involved
- exact skills to load
- expected outputs
- required tests
- done criteria

### 6. Create the pending question queue when needed

Create or update:
- `specs/pending-questions.md`

Use `../../docs/pending-question-queue.md` as the queue contract.

Create this queue when unresolved decisions would otherwise make the solution plan, module specs, sprint specs, slice specs, backlogs, or task queue speculative. Questions should be one-at-a-time, dependency-aware, and tied to concrete design impact.

The queue must:
- use stable question IDs such as `Q-001`, `Q-002`, `Q-003`
- preserve existing question IDs and statuses when updating an existing queue
- distinguish `answered` from `resolved`
- mark questions `blocking` only for the work they actually block
- include why each question matters and what artifacts or decisions it affects
- avoid dumping a large interrogation list into the chat response

If browser UI is in scope and no selected style guide exists in `specs/cross-cutting/*ui-style-guide*.md`, `app-description/55-ui/style-guide.md`, or equivalent UI spec, create a `category: ui` style-selection question with the default theme options from `../../docs/web-ui-style-guide.md`. This blocks only web UI implementation/generation tasks.

If unresolved `blocking` questions affect planned implementation work, either:
- stop before creating blocked implementation tasks and recommend `akka-do-next-pending-question`, or
- create tasks only for unblocked work and document blocked areas.

### 7. Create the pending task queue

Create or update:
- `specs/pending-tasks.md`

Use `../../docs/pending-task-queue.md` as the queue contract.

The queue must:
- contain one task for each bounded, unblocked item in each backlog's `Suggested harness task breakdown`
- block or omit work still gated by unresolved `blocking` questions in `specs/pending-questions.md`
- use stable task IDs such as `TASK-001`, `TASK-002`, `TASK-003`
- preserve existing task IDs and statuses when updating an existing queue
- mark obsolete non-done tasks as `superseded` instead of deleting them when requirements have replaced them
- set new tasks to `status: pending` unless a blocking question requires `status: blocked`
- represent dependencies with `depends on: [...]`
- include the smallest `required reads` needed for the task
- include the exact implementation `skills` to load
- include expected outputs, required checks, and done criteria
- point to a task brief when one exists, or use `task brief: none`

The queue is the durable follow-on execution index. A user should be able to start a fresh harness session and ask to run `akka-do-next-pending-task` without rereading the whole PRD.

### 8. Create execution-order docs

Update or create:
- `specs/README.md`
- `specs/backlog/README.md`

These must explain:
- read order
- sprint/backlog or slice/backlog numbering alignment
- dependencies between modules, sprints, or slices
- recommended harness execution style
- how to resolve design blockers with `specs/pending-questions.md` and `akka-do-next-pending-question`
- how to continue implementation with `specs/pending-tasks.md` and `akka-do-next-pending-task`

## Sizing rules

### Module and sprint spec sizing
A module spec should usually be stable and concise: module boundary, ownership, actors, state, UI area, integrations, and related sprints.

A sprint spec should usually be:
- 500 to 1500 words
- one bounded full-stack delivery increment
- understandable without the full PRD open beside it
- testable at module level before moving to the next sprint

### Slice spec sizing
For smaller plans, a slice spec should usually be:
- 500 to 1500 words
- one bounded capability area
- understandable without the full PRD open beside it

### Backlog sizing
A backlog file should be detailed enough to support several small harness runs, but not so large that it becomes a second PRD.

### Harness-operation sizing
Within a backlog, prefer work items like:
- one shared domain package
- one entity or workflow
- one consumer/timed action
- one view family
- one endpoint family
- one test family

If a work item still spans multiple unrelated component families or too many files, split it again into a task brief rather than handing it directly to code generation.

## Naming rules

Keep numbering aligned:
- `sprints/01-foo-sprint.md` ↔ `backlog/01-foo-build-backlog.md`
- `sprints/02-bar-sprint.md` ↔ `backlog/02-bar-build-backlog.md`
- or, for smaller slice-based plans, `slices/01-foo.md` ↔ `backlog/01-foo-build-backlog.md`

Use stable names:
- module names should describe durable app areas
- sprint or slice names should describe business delivery capability
- backlog names should match sprint or slice names exactly plus `-build-backlog`, dropping the `-sprint` suffix when present
- endpoint names should be feature-family oriented
- entity/workflow/view names should be explicit about their Akka role

## Recommended module sprint pattern

Prefer vertical module sprint order like this when the PRD supports it:
1. foundation: common domain, auth/security, style guide, project/test harness setup
2. first business module core: write model, endpoints, minimal UI, full-stack smoke test
3. next module increment: workflow/read side/UI flow, full-stack smoke test
4. operational reactions: consumers, timers, notifications, realtime UI if needed
5. reporting/admin/export modules

Adjust when domain dependencies clearly suggest another order. Avoid layer-only sprints for large PRDs.

## Recommended slice pattern

For smaller slice-based plans, prefer an order like this when the PRD supports it:
1. foundational current-state visibility or core write model
2. operational reactions and notifications
3. orchestration-heavy business flow
4. service/human-ops flow
5. reporting/contracts/export layer

Adjust only if the domain clearly suggests another order.

## Required content for each module spec

Each `specs/modules/*.md` file should contain:
- Module boundary and purpose
- Owned capabilities
- Actors and authorization boundary
- Domain objects and state ownership
- Backend components likely owned by the module
- Frontend screens/navigation areas owned by the module
- Integrations and events in/out
- Cross-cutting specs referenced
- Out of scope
- Related sprints/backlogs

## Required content for each sprint spec

Each `specs/sprints/*.md` file should contain:
- Sprint goal
- Parent module or modules
- Dependencies and prerequisite questions
- Backend scope
- Frontend scope when UI is in scope
- Acceptance behavior
- Pending questions affecting the sprint
- Implementation task groups
- Module-level full-stack test plan
- Done criteria
- Explicit defer list

## Required content for each slice spec

Each `specs/slices/*.md` file should contain:
- Scope
- Business goal
- Akka components involved
- Domain shape or business objects
- Commands and write operations
- Views and queries
- Endpoint/API scope
- Invariants
- Integrations
- Required tests
- Out of scope
- Handoff

## Required content for each backlog file

Each `specs/backlog/*.md` file should contain:
- Purpose
- Delivery goal
- Recommended package layout additions
- Class-by-class file list
- Concrete endpoint list
- Write-model design decisions
- View/workflow/consumer/timer design as relevant
- Test plan by file/class
- Implementation order
- Suggested harness task breakdown
- Done criteria
- Explicit defer list

## Required content for the pending question queue

`specs/pending-questions.md` must follow `../../docs/pending-question-queue.md` and include, for each question:
- question ID and title
- status
- priority
- category
- dependencies
- blocked decisions/artifacts/work areas
- source/provenance
- focused question text
- why it matters
- options when useful
- default if deferred
- answer, decision, decision impact, reconciled-into, and notes fields

Create this queue only when open decisions are meaningful enough to affect planning or implementation. Do not use it as a cosmetic preference checklist.

## Required content for the pending task queue

`specs/pending-tasks.md` must follow `../../docs/pending-task-queue.md` and include, for each task:
- task ID and title
- status
- source backlog path
- task brief path or `none`
- dependencies
- required reads
- skills
- expected outputs
- required checks
- done criteria
- notes when useful

Create queue tasks from backlog `Suggested harness task breakdown` items, not from every class name. A queue task should be one focused harness implementation run.

## Anti-patterns

Avoid:
- stopping at a single master plan when the user asked for implementation-ready planning artifacts
- writing giant module, sprint, or slice files that still require the whole PRD for context
- generating backlog files that are just restatements of the sprint or slice title
- mixing unrelated capabilities into one sprint/slice only because they are both "backend"
- creating layer-only large-PRD sprints such as "all entities" or "all frontend" instead of vertical module increments
- skipping tests in planning docs
- numbering sprints/slices and backlogs inconsistently
- omitting `specs/pending-questions.md` when unresolved blocking decisions would otherwise force guesses
- creating `specs/pending-questions.md` as a tedious cosmetic questionnaire instead of a design-impact queue
- omitting `specs/pending-tasks.md` when follow-on implementation work exists and is sufficiently unblocked
- generating queue tasks that are too broad for one fresh harness context
- inventing new directory structures when `specs/` already has an established pattern

## Final review checklist

Before finishing, verify:
- the PRD has been fully read
- the solution plan exists
- module/sprint specs exist for large PRDs, or slice specs exist for smaller plans, and they are dependency-ordered
- backlog files exist and align by number with sprint or slice specs
- `specs/pending-questions.md` exists when unresolved decisions block safe task generation
- unresolved `blocking` questions do not silently become implementation assumptions
- `specs/pending-tasks.md` exists when follow-on implementation work remains and is sufficiently unblocked
- pending tasks map to backlog task-breakdown items
- pending tasks include required reads, skills, expected outputs, checks, and done criteria
- cross-cutting concerns are not duplicated excessively across modules, sprints, or slices
- browser UI work has a selected style-guide spec or a pending/deferred style-selection question before UI tasks are created
- each backlog supports bounded implementation work
- optional task briefs exist when backlog items are still too broad
- execution-order docs point to the correct files
- naming is consistent across `specs/`, `modules/`, `sprints/` or `slices/`, `backlog/`, optional `tasks/`, and `pending-tasks.md`

## Response style

When using this skill:
- briefly summarize the proposed module/sprint structure for large PRDs, or slice structure for smaller plans, first
- then create or update the files
- clearly list which files were added or changed
- include `specs/pending-questions.md` in the changed-file summary when unresolved decisions were queued
- include `specs/pending-tasks.md` in the changed-file summary when follow-on implementation tasks exist
- name the next pending question when blocking decisions remain
- name the first runnable pending task and recommend a fresh context with `akka-do-next-pending-task` when tasks are ready
- keep planning explicit and repo-oriented
- do not jump straight into application code
