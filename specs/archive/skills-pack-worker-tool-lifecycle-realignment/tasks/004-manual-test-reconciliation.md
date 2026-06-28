# TASK-004: Define manual runtime test reconciliation doctrine

## Scope

Create canonical guidance for manual runtime testing as a first-class lifecycle phase that feeds findings back into app-description/spec/task/code reconciliation.

## Required reads

- `skills-pack/docs/app-development-lifecycle.md` if present
- `skills-pack/docs/app-worker-tool-model.md` if present
- `skills-pack/docs/intent-to-realization-flow.md`
- `skills-pack/references/generated-saas-runtime-completion.md`
- `skills-pack/skills/akka-runtime-feature-verification/SKILL.md`
- `skills-pack/skills/akka-manual-failure-reconciliation/SKILL.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md`

## Expected outputs

- New `skills-pack/docs/manual-test-reconciliation.md`.
- Focused routing updates to runtime/manual verification skills if needed.

## Done criteria

- Defines manual test session inputs/outputs.
- Defines worker-centric test scenarios: worker -> adapter -> tool -> capability -> runtime path.
- Defines finding categories: description gap, implementation gap, test gap, provider/config blocker, seed/demo-data gap, UX/state gap, expectation change.
- Preserves runtime completion doctrine and fail-closed provider expectations.
- Explains how manual findings return to interview/reconciliation before code patches when product intent is unclear.

## Required checks

- `git diff --check`
- Search proof that manual verification skills or docs reference the new doctrine.
