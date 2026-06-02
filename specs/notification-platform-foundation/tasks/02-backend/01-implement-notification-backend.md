# TASK-NPF-02-001: Implement notification backend foundation

## Objective

Implement backend notification projection, lifecycle operations, and preferences for the starter.

## In scope

- Notification item model/projection from selected v3 events/attention/digest states.
- Governed capabilities for list, mark-read, archive/dismiss, and preference update where scoped.
- Tenant/customer/AuthContext redaction and idempotency.
- Backend tests.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests
- focused scans for notification projection, lifecycle, preferences, and redaction

## Commit message

`notification-foundation: implement backend`
