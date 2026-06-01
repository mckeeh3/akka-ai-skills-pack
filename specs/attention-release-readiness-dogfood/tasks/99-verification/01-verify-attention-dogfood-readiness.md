# TASK-ARD-99-001: Verify attention dogfood release readiness

## Objective

Verify that the dogfood/release-readiness mini-project reached its done state, or append bounded follow-up tasks plus a new terminal verification task.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/attention-release-readiness-dogfood/README.md`
- `specs/attention-release-readiness-dogfood/pending-tasks.md`
- all artifacts and task briefs under `specs/attention-release-readiness-dogfood/`

## Required checks

- `git diff --check`
- review recorded validation commands/results
- focused `rg` as needed for backend-derived attention and stale doc claims

## Done criteria

- Done state is assessed against captured evidence.
- If complete, queue records release-readiness completion.
- If incomplete, bounded follow-up tasks and a new terminal verification task are appended.
- Task changes and queue update are committed.

## Commit message

`attention-dogfood: verify release readiness`
