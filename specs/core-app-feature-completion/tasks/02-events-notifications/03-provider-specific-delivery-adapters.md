# Task Brief: Provider-Specific Delivery Adapters

## Objective

Implement selected real production adapters for external notification channels after provider choices are resolved.

## Required reads

- `specs/core-app-feature-completion/pending-questions.md`
- `specs/core-app-feature-completion/tasks/02-events-notifications/02-notification-delivery-platform.md`
- provider documentation selected by Q-001
- `docs/capability-first-backend-architecture.md`

## In scope

- Real configured-provider adapter(s) selected by Q-001.
- Backend-only secret configuration, provider error mapping, retries/idempotency, delivery audit, redaction, and local smoke/integration validation.
- Tests proving missing config fails closed and successful configured delivery uses the real adapter.

## Blocker

This task must remain blocked until Q-001 selects provider scope. Do not implement fake production adapters.

## Checks

- `git diff --check`
- provider-specific tests/smoke from selected provider scope
- static asset/provider-secret scan
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Named production channels are backed by real provider adapters and validated, or the task is narrowed/blocked without claiming production delivery.
