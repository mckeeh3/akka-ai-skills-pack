# TASK-NDRR-01-002: Run fullstack notification delivery validation

## Objective

Run fresh scaffold fullstack validation for notification delivery.

## Required checks

- `git diff --check`
- fresh scaffold backend tests covering notification projection/email/outbox/preferences/redaction/audit
- frontend tests/typecheck/build
- optional real Resend smoke only if explicitly configured and safe
- focused scans for Resend, captured outbox, backend-owned notification center, redaction, idempotency, audit, and future channel boundary

## Expected outputs

- validation artifact
- updated pending queue

## Commit message

`notification-readiness: run fullstack validation`
