# TASK-FSFR-01-001: Reproduce and classify current full-suite failures

## Purpose

Create the authoritative current failure inventory before repairing clusters.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/conversation-capture.md`
- `specs/full-suite-failure-remediation/sprints/01-baseline-reproduction.md`
- `specs/full-suite-failure-remediation/backlog/01-full-suite-failure-remediation-build-backlog.md`
- `specs/workstream-chat-tool-catalog-expansion/verification-notes.md`

## Skills

- `akka-runtime-feature-verification`
- `akka-manual-failure-reconciliation`
- `akka-pending-task-queue-maintenance`

## Expected outputs

- `specs/full-suite-failure-remediation/failure-inventory.md`
- queue update

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run` or recorded failure output
- `npm --prefix frontend run typecheck` or recorded failure output
- `mvn test` or targeted class/suite commands if a full run is too expensive, with exact rationale

## Done criteria

- Inventory lists exact current failures/errors, commands run, affected files/tests, suspected root cause, and task mapping.
- Any differences from the prior verification notes are explained.
- Changes and queue update are committed.
