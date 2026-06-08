# Notification Delivery Release Readiness

## Purpose

Validate the completed in-app notification foundation and Resend-backed email notification channel as one coherent notification delivery slice before adding more channels.

## Source context

Builds on:

- `specs/notification-platform-foundation/`
- `specs/notification-email-delivery-channel/`
- starter notification, My Account, Resend/email/outbox, audit, attention, and frontend files

## Scope

- Validate in-app notification projection and My Account notification center.
- Validate email channel through Resend boundary and captured outbox behavior.
- Validate preferences, category allowlist, redaction, idempotency, and audit traces.
- Run full scaffold backend/frontend checks.
- Run optional real Resend smoke only if safely configured.
- Update handoff docs if any status is stale.

## Non-goals

- Do not add SMS, push, webhooks, Slack/Teams, or analytics.
- Do not require real Resend credentials for ordinary provider-skip validation.
- Do not fake production email success when Resend config is absent.

## Done state

Complete when fresh scaffold validation proves in-app + email notification delivery works at the stated scope, or any remaining blockers are recorded as bounded follow-up tasks.
