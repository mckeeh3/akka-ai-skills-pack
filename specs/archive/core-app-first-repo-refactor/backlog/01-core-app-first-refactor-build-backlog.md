# Backlog: Core App First Repository Refactor

## Goal

Refactor the repository so the root is a normal runnable Akka Java + frontend core app and the skills-pack source is isolated under `skills-pack/`.

## Suggested harness task breakdown

1. Record target repository architecture and path map.
2. Inventory existing assets and classify migration actions.
3. Promote/reconcile core app backend and frontend into root paths.
4. Dissolve the full-app template and remove package-placeholder rendering assumptions.
5. Move skills-pack assets under `skills-pack/`.
6. Move focused Akka examples under `skills-pack/examples/akka-components/`.
7. Update root app docs, extension guidance, and domain-extension boundaries.
8. Update skills-pack docs, installer/packaging, and validation tooling.
9. Run terminal verification and append follow-up work if gaps remain.

## Dependencies

- Planning/inventory must happen before broad moves.
- Core app promotion should happen before deleting the full-app template.
- Skills-pack path moves should happen before final docs/tooling updates.
- Terminal verification depends on all implementation tasks.

## Required checks by phase

- `git diff --check` for every task.
- Root backend tests after core app source changes.
- Frontend typecheck/test/build after frontend moves.
- Search checks for stale `templates/ai-first-saas-starter` canonical references.
- Install/package validation after skills-pack path moves.

## Acceptance criteria

The backlog is complete when the mini-project README done state is satisfied and terminal verification records no material unqueued gaps.
