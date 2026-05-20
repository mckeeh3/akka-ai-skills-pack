# Sprint 02: Planning and App-Description Alignment

## Objective

Align app-description maintenance and PRD/spec/backlog generation so they produce vertical workstream/surface/capability implementation plans instead of page-first or vague component-slice plans.

## Scope

Likely source files:

- `skills/app-descriptions/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md`
- `skills/app-description-capability-modeling/SKILL.md`
- `skills/app-description-ui/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`
- `docs/module-sprint-planning.md`
- `docs/pending-task-queue.md`

## Deliverables

- App-description guidance keeps application meaning in `12-workstreams/`.
- `55-ui/` is clarified as browser realization, not primary app decomposition.
- PRD/spec/backlog generation requires tasks to preserve functional agent, surface, surface action, capability id, auth, side effects, audit, tests, and Akka component mapping.
- Sprint review identifies remaining app-description/PRD planning drift and creates the next sprint tasks.

## Checks

- `git diff --check`
- Run a text audit for stale page-first or screen-first language in the touched skills/docs.
