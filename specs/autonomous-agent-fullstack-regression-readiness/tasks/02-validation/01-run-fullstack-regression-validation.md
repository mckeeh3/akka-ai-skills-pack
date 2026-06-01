# TASK-AAFR-02-001: Run fullstack regression validation

## Objective

Run fresh scaffold full backend and frontend validation after the regression fix.

## Required checks

- `git diff --check`
- fresh scaffold full backend `mvn test`
- frontend `npm ci`, `npm test`, `npm run typecheck`, `npm run build`
- focused scans for all four AutonomousAgent verticals

## Expected outputs

- validation artifact under this mini-project
- updated pending queue

## Commit message

`autonomous-agent-regression: run fullstack validation`
