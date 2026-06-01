# TASK-ARD-02-001: Run manual/runtime edge review

## Objective

Run or document a manual/runtime smoke review of attention behavior and edge cases at the current claimed scope.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/attention-release-readiness-dogfood/README.md`
- smoke checklist from TASK-ARD-01-001
- validation artifact from TASK-ARD-01-002
- `specs/attention-release-readiness-dogfood/tasks/02-review/01-run-manual-edge-review.md`

## In scope

- Verify or document evidence for:
  - left-rail backend-derived attention counts;
  - My Account aggregate attention;
  - core dashboard/surface attention;
  - producer-driven updates;
  - lifecycle resolution behavior;
  - hidden/denied workstream redaction;
  - provider/fail-closed states;
  - no frontend-only authority.
- Record any release blockers as new queue tasks if found.

## Required checks

- `git diff --check`
- manual/runtime notes or clear blocked reason

## Done criteria

- Manual/edge review evidence is captured.
- Any blockers are converted to bounded tasks or explicitly marked non-blocking future work.
- Task changes and queue update are committed.

## Commit message

`attention-dogfood: review runtime edges`
