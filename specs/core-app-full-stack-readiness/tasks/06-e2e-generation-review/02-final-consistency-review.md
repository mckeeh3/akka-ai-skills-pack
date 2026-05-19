# TASK-CORE-06-002: Final consistency review

## Purpose

Review the migration outputs and source guidance for contradictions, stale terms, missing links, or remaining blockers to full-core readiness.

## Required reads

- `specs/core-app-full-stack-readiness/README.md`
- `specs/core-app-full-stack-readiness/pending-tasks.md`
- all files produced by completed tasks in this migration
- `skills/README.md`
- `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`
- `docs/examples/ai-first-saas-seed-app-description/README.md`

## Expected outputs

- `specs/core-app-full-stack-readiness/final-consistency-review.md`
- cleanup updates if contradictions are found

## Required checks

- No full-core guidance allows silent omission of User Admin or Agent Admin.
- Scope labels are consistent.
- Role names and auth boundaries are consistent.
- `git diff --check`

## Done criteria

- Final review records pass/fail and any follow-up tasks.
- Queue status and changes are committed.
