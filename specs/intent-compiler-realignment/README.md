# Intent Compiler Realignment

## Purpose

Realign the Akka AI skills pack's user-intent processing layer around the **intent compiler** model developed in discussion and now captured in the canonical `skills-pack/docs/intent-compiler.md` doc set.

The skills pack must consume, digest, capture, react to, and realize **Capturing Incremental Intent (CII)**. Incremental user intent should compile into two current-state outputs:

1. non-code intent artifacts such as app-description, requirements, specs, acceptance criteria, and task queues; and
2. generated functional app code with tests and runtime validation.

## Background

The existing skills and docs for app-description, intake, PRD conversion, workstream planning, and generation have gone through many significant iterations. They likely contain redundant, bloated, or superseded description-first concepts. This mini-project plans a controlled archive-and-rebuild effort instead of attempting to patch the old model in place.

## Scope

In scope:

- inventory current skills/docs related to user intent input;
- define active vs archived boundaries for pre-intent-compiler content;
- create a clean canonical intent-compiler doc set;
- create or revise the active intent-processing skills to use the new model;
- update references from skills and docs to the new canonical docs;
- keep focused Akka implementation skills active and only lightly realign their references if needed;
- validate install/reference integrity.

## Non-goals

- Do not archive the whole skills pack.
- Do not rewrite focused Akka implementation skills unless a reference/routing update is needed.
- Do not implement generated app runtime features in this mini-project.
- Do not preserve historical intent inside canonical app-description/spec guidance; use git history and archive notes for historical context.

## Affected repository areas

- `skills-pack/docs/**`
- `skills-pack/skills/**`
- `skills-pack/examples/**` when examples reference old intent structure
- `skills-pack/templates/**` when templates reference old intent structure
- `skills-pack/tools/**` only if validators/reference checks need path updates
- `specs/intent-compiler-realignment/**`

## Execution model

Execute one task per fresh harness context. Each task must read this README, `conversation-capture.md`, `pending-tasks.md`, its sprint/backlog/task brief, and only the listed source files. Update `pending-tasks.md`, run required checks, and commit each completed task.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills-pack/AGENTS.md`
3. `specs/intent-compiler-realignment/README.md`
4. `specs/intent-compiler-realignment/conversation-capture.md`
5. `specs/intent-compiler-realignment/pending-tasks.md`
6. selected sprint/backlog/task brief
7. canonical intent compiler docs under `skills-pack/docs/intent-compiler*.md`, `skills-pack/docs/current-intent-model.md`, `skills-pack/docs/incremental-intent-processing.md`, and `skills-pack/docs/intent-to-realization-flow.md`
8. task-specific skill/doc files

## Sprint sequence

1. Inventory and archive plan.
2. Canonical intent-compiler docs.
3. Active intent-processing skill replacement/realignment.
4. Reference migration, examples/templates consistency, and install validation.
5. Terminal verification loop.

## Done state

This mini-project is complete when:

- obsolete or superseded user-intent processing docs/skills are archived or clearly marked inactive;
- the active canonical docs describe the intent compiler, current intent model, app-description intent graph, incremental intent processing, workstream binding model, and intent-to-code realization;
- active intent-processing skills route user input through the new current-intent/workstream-binding structure;
- active skill/doc references no longer point to superseded docs except as archive/reference material;
- install/reference checks pass;
- a terminal verification task confirms no material gaps remain or appends bounded follow-up tasks.

## Open concerns

- The exact archive path and whether skills should be moved, disabled, or replaced in place must be decided by the inventory/archive task.
- Moving skill directories may require installer/manifest/reference updates; the archive plan must avoid breaking installed-pack validation.
- Some old docs may contain useful detail that should be salvaged into concise new canonical docs.
