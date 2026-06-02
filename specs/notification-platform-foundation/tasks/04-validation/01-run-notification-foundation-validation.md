# TASK-NPF-04-001: Run notification foundation validation

## Objective

Validate notification foundation behavior in a fresh scaffold.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- frontend tests/typecheck/build
- focused scans for backend-owned notifications and no hidden-workstream leakage

## Expected outputs

- validation artifact under this mini-project
- updated pending queue

## Commit message

`notification-foundation: validate`
