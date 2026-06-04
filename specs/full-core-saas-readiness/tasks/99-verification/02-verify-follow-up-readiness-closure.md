# TASK-FCSR-99-002: Verify follow-up full-core readiness closure

## Objective

Verify closure or accepted blockers for the follow-up tasks appended by `TASK-FCSR-99-001`.

## Required reads

- `AGENTS.md`
- `specs/full-core-saas-readiness/pending-tasks.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- follow-up task artifacts and validation evidence
- `app-description/00-system/readiness-status.md`
- `app-description/80-review/latest-readiness-summary.md`

## Skills

- none; repository verification task

## In scope

- Verify provider, billing/timer, and validation-tool statuses against evidence.
- Update readiness docs and queue.
- Append any remaining bounded follow-up tasks if material gaps remain.

## Out of scope

- Implementing provider, billing, timer, or tooling changes during verification.

## Expected outputs

- Final follow-up verification artifact and queue update.

## Required checks

- `git diff --check`
- Checks required by completed follow-up tasks.

## Done criteria

- Follow-up statuses are verified against evidence or precise blockers.
- Changes and queue update are committed.
