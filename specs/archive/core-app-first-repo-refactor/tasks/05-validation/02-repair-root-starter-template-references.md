# Task Brief: Repair Root Starter-Template References

## Objective

Remove stale full-app starter-template path assumptions from root app source/tests after terminal verification found remaining `templates/ai-first-saas-starter/**` references outside specs.

## Required reads

- `AGENTS.md`
- `specs/core-app-first-repo-refactor/README.md`
- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- `specs/core-app-first-repo-refactor/verification-notes-2026-06-03.md`
- `specs/core-app-first-repo-refactor/tasks/05-validation/02-repair-root-starter-template-references.md`
- root files found by:
  - `rg -n --hidden --glob '!target/**' --glob '!frontend/node_modules/**' --glob '!node_modules/**' --glob '!skills-pack/dist/**' --glob '!specs/**' --glob '!.git/**' 'templates/ai-first-saas-starter|scaffold-ai-first-saas-starter|resources/templates/ai-first-saas-starter|starter template|full-app template|scaffold-first' .`

## In scope

- Update root frontend contract tests to read canonical root app paths instead of `templates/ai-first-saas-starter/**` fallbacks.
- Update root source/test comments that still describe the core app as a starter template.
- Preserve behavior and test intent; this is a path/name cleanup task, not a feature rewrite.
- Update `specs/core-app-first-repo-refactor/pending-tasks.md` with task status and notes.

## Out of scope

- Broad `specs/**` stale-reference repair; `TASK-LAYOUT-05-002` owns that.
- Skills-pack guidance changes unless a root source/test search exposes an immediate broken reference.
- Renaming public classes or endpoints unless required by tests; prefer comments/test helpers only.

## Expected outputs

- Updated root frontend test/source references as needed.
- Updated root Java comments as needed.
- Updated `specs/core-app-first-repo-refactor/pending-tasks.md`.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- focused backend tests if Java source/test files are changed, or `mvn test` if practical
- search proof that the stale template/scaffold patterns no longer match outside `specs/**`, `.agents/**`, generated/build output, node modules, and `.git/**`

## Done criteria

- Root app source/tests no longer point to `templates/ai-first-saas-starter/**` or call the root app a starter template.
- Required checks pass.
- Changes and queue update are committed with message `layout: repair root starter references`.
