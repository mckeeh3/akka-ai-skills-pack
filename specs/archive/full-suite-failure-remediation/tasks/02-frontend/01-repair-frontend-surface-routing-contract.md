# TASK-FSFR-02-001: Repair frontend surface intent routing contract failure

## Purpose

Fix the pre-existing frontend contract failure around destructive and approval-gated asks staying on safe fallback.

## Required reads

- `AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- relevant frontend contract test named by the inventory
- `frontend/src/workstream/**` files named by the inventory
- `specs/workstream-surface-intent-routing/verification-notes.md`

## Skills

- `akka-web-ui-testing`
- `akka-web-ui-state-rendering`

## Expected outputs

- frontend code/test/current-intent repair as appropriate
- queue update

## Required checks

- `git diff --check`
- targeted frontend contract test
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Destructive/approval-gated prompts do not auto-execute or route unsafely.
- The frontend test suite no longer has this failure.
- Changes and queue update are committed.
