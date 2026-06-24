# TASK-FSFR-99-001: Verify Full Suite Failure Remediation completion

## Purpose

Verify that the normal frontend/backend full-suite checks are clean, or append bounded follow-up tasks and a new terminal verification task.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/conversation-capture.md`
- `specs/full-suite-failure-remediation/pending-tasks.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- completed task notes and changed files

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-pending-task-queue-maintenance`

## Expected outputs

- `specs/full-suite-failure-remediation/verification-notes.md`
- queue update marking verification done only when README done state is achieved
- new bounded follow-up tasks plus a new terminal verification task if material failures remain
- commit for verification notes and queue updates

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `mvn test`

## Done criteria

- Verification notes compare completed work against every README done-state bullet and `failure-inventory.md` item.
- Full frontend tests and typecheck pass.
- `mvn test` passes, or remaining failures are explicitly queued as bounded blockers with a new terminal verification task.
- Changes and queue update are committed.
