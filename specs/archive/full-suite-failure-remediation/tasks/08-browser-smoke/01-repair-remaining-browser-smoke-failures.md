# TASK-FSFR-08-001: Repair remaining browser smoke failures

## Purpose

Repair browser smoke failures not already fixed by prior cluster tasks.

## Required reads

- `AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- browser smoke test files named by inventory
- related API/frontend/workstream source files named by inventory

## Skills

- `akka-http-endpoint-testing`
- `akka-web-ui-testing`
- `akka-runtime-feature-verification`

## Expected outputs

- focused repairs for remaining browser smoke failures
- queue update

## Required checks

- `git diff --check`
- targeted browser smoke tests named by inventory
- frontend tests/typecheck if frontend paths change

## Done criteria

- Browser smoke tests reflect the intended protected API/UI runtime path.
- Failures are fixed or split into precise runtime blockers.
- Changes and queue update are committed.
