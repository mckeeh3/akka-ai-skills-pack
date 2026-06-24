# TASK-FSFR-99-003: Verify Full Suite Failure Remediation completion after My Account frontend contract follow-up

## Purpose

Re-run terminal verification after `TASK-FSFR-11-001` resolves the residual My Account frontend contract failure found by `TASK-FSFR-99-002`.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/conversation-capture.md`
- `specs/full-suite-failure-remediation/pending-tasks.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- `specs/full-suite-failure-remediation/verification-notes.md`
- this task brief
- completed task notes and changed files since `TASK-FSFR-99-002`

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-pending-task-queue-maintenance`

## Expected outputs

- Updated `specs/full-suite-failure-remediation/verification-notes.md` with final command evidence.
- Queue update marking verification done only when README done state is achieved.
- Additional bounded follow-up tasks plus another terminal verification task if material failures remain.
- Commit for verification notes and queue updates.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `mvn test`

## Done criteria

- Verification notes compare completed work against every README done-state bullet and every `failure-inventory.md` item, including residual My Account failures recorded by `TASK-FSFR-99-001` and `TASK-FSFR-99-002`.
- Full frontend tests and typecheck pass.
- `mvn test` passes, or any remaining failures are explicitly queued as bounded blockers with a new terminal verification task.
- Changes and queue update are committed.
