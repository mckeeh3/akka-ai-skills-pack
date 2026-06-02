# TASK-NEDC-02-001: Implement Resend email notification channel

## Objective

Implement backend email notification delivery using the existing Resend email service foundation and captured outbox behavior.

## In scope

- Email channel service over notification projection items.
- Resend production delivery integration through existing email service boundary.
- Captured local/dev/test outbox behavior.
- Preference/category allowlist checks.
- Redacted email content generation.
- Idempotent enqueue/send/fail handling and audit traces.
- Backend tests.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- focused scans for Resend, captured outbox, provider fail-closed, redaction, preferences, and idempotency

## Commit message

`notification-email: implement resend channel`
