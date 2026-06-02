# Notification Delivery Release Readiness Checklist

## Scope under review

This checklist validates the coherent release-readiness slice that combines the implemented backend-owned in-app notification foundation with the implemented Resend-backed email notification delivery channel.

## Checklist

### In-app notification center

- [ ] Backend-owned notification projection still produces `NotificationItem` records from authorized source state.
- [ ] My Account notification center is populated from backend-shaped data, not frontend-local notification state.
- [ ] Lifecycle actions list/get/mark-read/dismiss/archive/snooze update only notification channel state.
- [ ] Hidden workstream, tenant/customer, recipient, capability, and `not_found_or_redacted` guardrails remain enforced.
- [ ] UI surfaces render unread/visible counts, redacted states, empty states, preference summary, and action descriptors from backend data.

### Email delivery channel

- [ ] Email delivery is layered over authorized notification/source state and does not act as a source-of-truth notification system.
- [ ] Production delivery uses the Resend service boundary with backend-only provider secrets.
- [ ] Missing or blank `RESEND_API_KEY` or `RESEND_FROM_EMAIL` fails closed with actionable status/audit evidence.
- [ ] Local/dev/test delivery uses explicitly labelled captured outbox behavior rather than fake sent success.
- [ ] My Account email preference surfaces are backend-derived and scoped to implemented email notification preferences.

### Captured outbox

- [ ] Captured outbox records intended email delivery in local/dev/test mode without external provider calls.
- [ ] Captured delivery is labelled as captured, not sent.
- [ ] Captured outbox entries preserve recipient, category, dedupe key, rendered content summary, and audit/work-trace evidence needed for validation.

### Preferences and category allowlist

- [ ] Notification preferences are checked before in-app/email delivery decisions where applicable.
- [ ] Email preference-required denial is represented distinctly from provider failure.
- [ ] Category allowlist denial is represented distinctly from preference denial and provider failure.
- [ ] Preference/category state is tenant/customer and recipient scoped.

### Redaction and secret safety

- [ ] Redaction decisions from notification/source state are preserved for in-app and email surfaces.
- [ ] Email subject, preview, text, and HTML render only redacted-safe content.
- [ ] Tests or scans check for token/secret leakage and redaction markers.
- [ ] Redacted or unauthorized source state does not become visible through email, outbox, audit, or UI metadata.

### Idempotency

- [ ] In-app projection uses stable notification dedupe semantics for repeated source inputs.
- [ ] Email delivery lookup uses a stable dedupe key so duplicate projections do not create duplicate outbox messages.
- [ ] Duplicate handling leaves auditable evidence and does not resend through Resend or duplicate captured outbox records.

### Audit and trace evidence

- [ ] In-app projection/lifecycle actions emit or preserve governed tool/action evidence.
- [ ] Email channel records audit evidence for captured delivery, Resend/config failure, preference denial, allowlist denial, redaction-safe rendering, and duplicate handling.
- [ ] Audit/work traces avoid provider secrets and unauthorized source details.

### Documentation and handoff consistency

- [ ] Foundation handoff still states in-app scope only and does not imply email readiness by itself.
- [ ] Email channel handoff still states implemented email scope and Resend/captured-outbox/fail-closed guardrails.
- [ ] Release-readiness validation records fullstack backend/frontend evidence and any blockers.
- [ ] Future SMS, mobile push, Slack/Teams, webhook, marketing email, provider-selection, and analytics work remains explicitly out of scope.

## Required validation evidence for the next task

- [ ] `git diff --check`.
- [ ] Fresh scaffold backend tests covering notification projection, email delivery, captured outbox, preferences, category allowlist, redaction, idempotency, and audit evidence.
- [ ] Frontend tests, typecheck, and build for My Account notification and email preference surfaces.
- [ ] Focused scans for Resend configuration, captured outbox, backend-owned notification center, redaction, idempotency, audit terms, and future-channel boundary copy.
- [ ] Optional real Resend smoke only when explicitly configured and safe; otherwise record that it was skipped.
