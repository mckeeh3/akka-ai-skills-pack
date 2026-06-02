# TASK-NEDC-04-001: Run email channel validation

## Objective

Validate notification email delivery behavior in a fresh scaffold.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- frontend tests/typecheck/build
- local/dev captured outbox checks
- Resend-provider fail-closed check when configuration absent
- real Resend smoke only if explicitly configured and safe

## Expected outputs

- validation artifact under this mini-project
- updated pending queue

## Commit message

`notification-email: validate channel`
