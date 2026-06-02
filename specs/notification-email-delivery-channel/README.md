# Notification Email Delivery Channel

## Purpose

Create the next notification delivery slice after the in-app Notification Platform Foundation: a governed **email notification channel** using the existing Resend email service foundation.

Email delivery must be explicit, preference-controlled, scoped/redacted, audited, and fail-closed when Resend/provider configuration is missing. It must not be inferred from in-app notification readiness and must not become a broad marketing/analytics platform.

## Source context

Builds on:

- `specs/notification-platform-foundation/`
- `specs/notification-platform-foundation/notification-foundation-handoff.md`
- existing starter Resend/email/outbox foundation
- `skills/README.md` Resend/email onboarding guidance
- starter My Account, notification, invitation/email, audit, and frontend files

## Scope

- Define email notification channel contract.
- Use the supported **Resend** email service for production email delivery.
- Use local/dev/test captured outbox behavior for validation.
- Add opt-in/opt-out preferences and quiet-hours or safe deferral rules where scoped.
- Select a bounded category allowlist such as digest-ready, review-required, provider-blocked, and high-severity attention.
- Enqueue/send only redacted, browser-safe, recipient-authorized email content.
- Emit audit traces for enqueue/send/deny/fail.
- Add My Account email notification preferences surface.
- Add tests for preferences, redaction, idempotency, provider fail-closed, and outbox behavior.

## Non-goals

- Do not implement SMS, mobile push, Slack/Teams, webhooks, or marketing emails.
- Do not send email for hidden/redacted workstreams/items.
- Do not bypass notification preferences, AuthContext, tenant/customer visibility, or audit.
- Do not replace in-app notifications as the source projection.
- Do not silently fall back to fake production email success when Resend configuration is absent.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run checks or record blockers, and make one focused commit.

## Handoff and validation

- Contract: `notification-email-channel-contract.md`
- Validation evidence: `email-channel-validation.md`
- Current handoff: `notification-email-channel-handoff.md`

The validated starter/reference scope uses Resend for production email delivery and captured outbox behavior for local/dev/test. Missing Resend provider configuration fails closed instead of recording fake production success. My Account email preferences, category allowlist checks, redaction, idempotency, and audit evidence are part of the validated boundary. Future SMS/push/webhook channels require separate governed delivery-channel contracts.

## Done state

Complete when the starter/reference assets have:

- documented email notification channel contract;
- Resend-backed production email delivery path and captured local/dev/test outbox path;
- governed email preferences and category allowlist;
- scoped/redacted email content generation from notification source refs;
- idempotent enqueue/send/failure behavior;
- audit traces for email notification activity;
- My Account email notification preferences surface;
- tests and scaffold validation proving Resend/fail-closed/outbox/redaction/preference behavior.
