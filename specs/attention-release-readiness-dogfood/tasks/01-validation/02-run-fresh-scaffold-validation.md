# TASK-ARD-01-002: Run fresh scaffold automated validation

## Objective

Validate that a fresh scaffold of the starter passes targeted backend/frontend attention checks.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/attention-release-readiness-dogfood/README.md`
- smoke checklist from TASK-ARD-01-001
- `specs/attention-release-readiness-dogfood/tasks/01-validation/02-run-fresh-scaffold-validation.md`

## In scope

- Scaffold a fresh starter into a temp directory.
- Run targeted backend Maven tests for attention backbone/producers/workstreams.
- Run frontend tests, typecheck, and build.
- Record results in a validation artifact.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- frontend `npm test`, `npm run typecheck`, and `npm run build` or targeted equivalents justified in notes

## Done criteria

- Fresh scaffold repeatability is proven or blockers are recorded.
- Validation artifact and queue update are committed.

## Commit message

`attention-dogfood: run scaffold validation`
