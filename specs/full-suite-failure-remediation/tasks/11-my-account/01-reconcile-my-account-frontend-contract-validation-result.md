# TASK-FSFR-11-001: Reconcile My Account frontend contract with validation-result backend evidence

## Purpose

Repair the residual frontend contract failure found by `TASK-FSFR-99-002` after `TASK-FSFR-10-001` reconciled the My Account service-level direct-action validation tests with the accepted protected-workstream `validation-error` result-surface contract.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/conversation-capture.md`
- `specs/full-suite-failure-remediation/pending-tasks.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- `specs/full-suite-failure-remediation/verification-notes.md`
- this task brief
- `frontend/src/workstream-my-account-vertical.contract.test.mjs`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-pending-task-queue-maintenance`

## Expected outputs

- Focused frontend contract/current-intent repair for My Account validation-result backend evidence.
- Queue update.

## Required checks

- `git diff --check`
- `cd frontend && node --test src/workstream-my-account-vertical.contract.test.mjs`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- Targeted My Account backend check if backend evidence changes.

## Done criteria

- My Account frontend contract aligns with the accepted protected-workstream validation-result contract from `TASK-FSFR-10-001`.
- Unsupported self-service fields and invalid settings remain fail-closed without requiring browser-visible thrown authorization exceptions.
- Frontend suite is clean and no provider secret or unsafe backend evidence is exposed.
- Changes and queue update are committed.
