# Notification Delivery Release Readiness Handoff

## Status

The notification delivery release-readiness mini-project has validated the coherent starter/reference slice that combines the implemented backend-owned **in-app notification foundation** with the implemented **Resend-backed email notification delivery channel**.

Validated scope is limited to in-app notification projection/lifecycle/preferences/My Account surfaces plus governed email delivery over authorized notification/source state. The combined slice is release-ready at this stated scope based on scaffolded fullstack validation. It is not a source-of-truth attention/workstream system, not a marketing email system, and not a general notification provider-selection or analytics platform.

## Implemented and validated channels

### In-app notification channel

Implemented and validated in-app behavior includes:

- backend-owned `NotificationItem` projection from authorized attention, workstream event/projection refresh, personal digest, and worker/task source state;
- lifecycle actions for list, get, mark-read, dismiss, archive, and snooze that mutate only notification channel state;
- tenant/customer/AuthContext, recipient, capability, hidden-workstream, and `not_found_or_redacted` guardrails;
- backend-derived My Account notification center data, unread/visible counts, redacted and empty states, preference summary, and action descriptors;
- stable in-app dedupe semantics and audit/governed-action evidence.

### Email notification channel

Implemented and validated email behavior includes:

- email delivery layered over authorized notification/source state rather than acting as an independent source-of-truth notification system;
- production delivery through the Resend email service boundary with backend-only provider secrets;
- explicit local/dev/test `captured_outbox` behavior that records intended delivery as captured, not sent;
- fail-closed handling for missing or blank `RESEND_API_KEY` or `RESEND_FROM_EMAIL`, with actionable status/audit evidence such as `resend-config-missing`;
- preference-required and category-allowlist denials represented separately from provider/config failure;
- redaction-safe subject, preview, text, and HTML rendering;
- stable dedupe-key lookup so repeated projections do not resend through Resend or duplicate captured outbox messages;
- audit evidence for captured delivery, Resend/config failure, preference denial, category denial, redaction-safe rendering, and duplicate handling;
- backend-derived My Account email preference surfaces scoped to implemented email notification preferences.

## Validation evidence

See `validation/notification-delivery-fullstack-validation.md`.

Release-readiness validation passed:

- `git diff --check` passed before validation artifact edits;
- rendered scaffold fullstack validation passed for `Notification Delivery Readiness Starter`;
- scaffolded backend Maven tests passed: 239 tests run, 0 failures, 0 errors, 1 skipped;
- notification-specific backend tests passed, including `NotificationServiceTest`, `EmailNotificationServiceTest`, and `DurableNotificationRepositoryEntityTest`;
- frontend `npm install`, tests, typecheck, and build passed (`132` tests passed);
- built static secret scan passed;
- focused scans found Resend configuration/fail-closed handling, captured outbox, backend-owned notification center evidence, redaction, dedupe/idempotency, audit evidence, and future-channel boundary copy.

Optional real Resend smoke was not run because no explicit safe Resend provider-smoke configuration was provided. This does not change the provider-skip validation result: production delivery remains fail-closed unless Resend is configured, and local/dev/test captured delivery must remain explicitly labelled as captured rather than sent.

## Required operating guardrails

- In-app notification readiness and email notification readiness are related but distinct; do not infer either channel's readiness from the other alone.
- Notification lifecycle actions must not resolve source attention, mutate worker tasks, alter digest state, or change policy/governance source state unless a separate governed source capability is invoked and authorized.
- Email delivery must preserve AuthContext, recipient, preferences, category allowlist, redaction proof, stable dedupe keys, and audit/work traces.
- Production email mode must use Resend and must not silently fall back to captured outbox or fake success when `RESEND_API_KEY` or `RESEND_FROM_EMAIL` is missing or blank.
- Local/dev/test captured outbox mode must be labelled captured, not sent, and must not imply real provider delivery.
- Provider secrets and unauthorized source details must not appear in frontend assets, outbox metadata, audit evidence, rendered email content, or handoff documentation.

## Future channel boundary

The following remain future work and are not implemented by this release-readiness slice:

- SMS;
- mobile push;
- Slack/Teams;
- webhooks;
- marketing email;
- provider-selection orchestration;
- delivery analytics.

Future SMS, mobile push, Slack/Teams, webhook, marketing email, provider-selection, or analytics work must be introduced as separate governed delivery-channel contracts with their own preferences, provider configuration, fail-closed behavior, redaction rules, idempotency keys, audit traces, frontend surfaces, tests, and handoffs. They must not be hidden toggles or implied extensions of the in-app notification center or email preference surface.

## Release-readiness conclusion

No release-readiness blockers were found for the implemented in-app + email notification delivery slice. The next step is terminal mini-project verification to confirm the checklist, validation artifact, handoff, and queue state are mutually consistent.
