---
name: akka-prd-to-specs-backlog
description: Turn a PRD or other high-level requirements artifact into a repo-ready planning package: master Akka solution plan, cross-cutting specs, numbered slice specs, numbered build backlogs, and execution-order docs under specs/.
---

# Akka PRD to Specs Backlog

Use this skill when the user does not just want an Akka component plan, but wants the plan materialized into the repository as a harness-friendly `specs/` tree.

This is a **project-specific planning skill** that builds on the ideas in `akka-solution-decomposition` and continues all the way to implementation-ready planning artifacts.

## Goal

Generate a consistent planning package from a PRD, requirements document, or high-level feature set that:
- produces a master Akka solution plan
- splits the plan into bounded vertical slice specs
- turns each slice into a build backlog suitable for one or more independent harness operations
- writes index files that make execution order and dependencies explicit
- keeps files small enough to support focused downstream coding sessions

## Use this skill when

The task sounds like one of these:
- "I have a PRD. How do I build this in Akka?"
- "Turn this requirements doc into an implementation plan"
- "Break this PRD into harness-friendly tasks"
- "Create specs and backlogs from this product doc"
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
- `../../specs/akka-solution-plan.md` if it already exists
- `../references/akka-entity-comparison.md`

If the user provided a path to a PRD or requirements file:
1. read that file completely
2. extract capabilities, actors, commands, queries, workflows, timers, integrations, security constraints, and UI needs
3. then generate the file set

If `specs/` already exists:
- preserve numbering consistency where possible
- update indexes rather than duplicating them
- keep names aligned with the existing slice/backlog naming pattern

## What this skill must produce

At minimum, create or update these files under `specs/`:

### Top-level
- `specs/akka-solution-plan.md`
- `specs/README.md`

### Cross-cutting specs
Create only the ones justified by the PRD, but prefer these when broadly applicable:
- `specs/cross-cutting/00-common-domain-and-conventions.md`
- `specs/cross-cutting/01-auth-tenancy-audit.md`
- `specs/cross-cutting/02-<integration-or-platform-concern>.md`

### Vertical slice specs
Create numbered files such as:
- `specs/slices/01-<slice-name>.md`
- `specs/slices/02-<slice-name>.md`
- `specs/slices/03-<slice-name>.md`

### Build backlogs
Create matching numbered files such as:
- `specs/backlog/README.md`
- `specs/backlog/01-<slice-name>-build-backlog.md`
- `specs/backlog/02-<slice-name>-build-backlog.md`
- `specs/backlog/03-<slice-name>-build-backlog.md`

## Output contract

This skill is complete only when a future harness run can:
- read a slice spec
- read the matching backlog
- implement a bounded piece of work without rereading the entire PRD

If the output is still too broad for that, the skill has not decomposed far enough.

## Standard repository shape

Prefer this structure:

```text
specs/
  README.md
  akka-solution-plan.md
  cross-cutting/
    00-common-domain-and-conventions.md
    01-auth-tenancy-audit.md
    ...
  slices/
    01-....md
    02-....md
    ...
  backlog/
    README.md
    01-....-build-backlog.md
    02-....-build-backlog.md
    ...
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

Separate concerns that should not be duplicated across slices, such as:
- ID and domain conventions
- tenancy and auth rules
- audit rules
- ERP integration model
- notification delivery model
- export/reporting conventions

Create one file per cross-cutting concern when it affects multiple slices.

### 3. Split into harness-friendly slices

Create vertical slices that are:
- independently meaningful to the business
- small enough for focused implementation
- ordered by dependency
- clear about what they intentionally exclude

A good slice usually contains:
- one capability family
- one main write-model cluster
- its read side
- its endpoints
- its tests

Avoid slices that are either:
- too broad: "build the whole platform"
- too tiny: "add one enum"

### 4. Turn each slice into a build backlog

For each slice, create a matching backlog file that includes:
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

### 5. Create execution-order docs

Update or create:
- `specs/README.md`
- `specs/backlog/README.md`

These must explain:
- read order
- slice/backlog numbering alignment
- dependencies between slices
- recommended harness execution style

## Sizing rules

### Slice spec sizing
A slice spec should usually be:
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

## Naming rules

Keep numbering aligned:
- `slices/01-foo.md` ↔ `backlog/01-foo-build-backlog.md`
- `slices/02-bar.md` ↔ `backlog/02-bar-build-backlog.md`

Use stable names:
- slice names should describe business capability
- backlog names should match slice names exactly plus `-build-backlog`
- endpoint names should be feature-family oriented
- entity/workflow/view names should be explicit about their Akka role

## Recommended slice pattern

Prefer an order like this when the PRD supports it:
1. foundational current-state visibility or core write model
2. operational reactions and notifications
3. orchestration-heavy business flow
4. service/human-ops flow
5. reporting/contracts/export layer

Adjust only if the domain clearly suggests another order.

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

## Anti-patterns

Avoid:
- stopping at a single master plan when the user asked for implementation-ready planning artifacts
- writing giant slice files that still require the whole PRD for context
- generating backlog files that are just restatements of the slice title
- mixing unrelated capabilities into one slice only because they are both "backend"
- skipping tests in planning docs
- numbering slices and backlogs inconsistently
- inventing new directory structures when `specs/` already has an established pattern

## Final review checklist

Before finishing, verify:
- the PRD has been fully read
- the solution plan exists
- slice specs exist and are dependency-ordered
- backlog files exist and align by number with slice specs
- cross-cutting concerns are not duplicated excessively across slices
- each backlog supports bounded implementation work
- execution-order docs point to the correct files
- naming is consistent across `specs/`, `slices/`, and `backlog/`

## Response style

When using this skill:
- briefly summarize the proposed slice structure first
- then create or update the files
- clearly list which files were added or changed
- keep planning explicit and repo-oriented
- do not jump straight into application code
