# TASK-NEDC-01-001: Define email notification channel contract

## Objective

Define email notification channel contract, preferences, category allowlist, Resend/outbox boundary, redaction, idempotency, audit, and My Account preference surface semantics.

## Required reads

- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- `specs/notification-platform-foundation/notification-foundation-contract.md`
- `specs/notification-platform-foundation/notification-foundation-handoff.md`
- starter Resend/email/outbox service files and docs
- `skills/README.md` Resend/email foundation guidance

## Expected outputs

- `specs/notification-email-delivery-channel/notification-email-channel-contract.md`
- updated pending queue

## Required checks

- `git diff --check`
- focused `rg` proving contract covers Resend, captured outbox, preferences, category allowlist, redaction, idempotency, audit, fail-closed, and no fake production email success

## Commit message

`notification-email: define contract`
