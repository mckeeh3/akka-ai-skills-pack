# Notification Foundation Handoff

## Status

The notification platform foundation mini-project has implemented and validated the first governed **in-app notification foundation** for the AI-first SaaS starter/reference template.

Validated scope is limited to backend-owned in-app notification projection, lifecycle, preferences, and the My Account notification center surface. The foundation is a user-facing projection/channel over authorized attention, workstream event, and personal digest source state; it is not the source of truth for those domains.

## Implemented in-app foundation

Implemented starter/reference assets include:

- backend notification domain contracts for `NotificationItem`, `NotificationProjectionInput`, `NotificationPreference`, lifecycle status, channel, category, redaction, source refs, and My Account center data;
- backend-owned projection and lifecycle service with governed tool ids for list, get, mark-read, dismiss, archive, snooze, preference update, and internal projection from source inputs;
- durable Akka-backed notification repository seam for normal runtime state when `ComponentClient` is available;
- projection inputs from authorized attention, workstream event/projection refresh, personal attention digest, and worker/task states;
- tenant/customer/AuthContext, recipient, capability, and hidden-workstream redaction guardrails;
- My Account backend surface data for `surface-my-account-notification-center` with backend-derived unread/visible counts and in-app-only channel metadata;
- React workstream surface rendering for backend-shaped notification center data, action descriptors, redacted/empty states, and preference summary;
- backend and frontend tests covering notification projection/lifecycle/preferences/redaction and My Account surface contracts.

## Validation evidence

See `validation/notification-foundation-validation.md`.

Rendered scaffold validation passed:

- `git diff --check` passed;
- scaffolded `mvn test` passed: `Tests run: 235, Failures: 0, Errors: 0, Skipped: 1`;
- notification-specific backend tests passed, including `NotificationServiceTest` and `DurableNotificationRepositoryEntityTest`;
- frontend `npm install`, tests, typecheck, and build passed;
- focused scans found backend-owned notification capabilities, deterministic `notification:in_app` dedupe keys, safe `not_found_or_redacted` denials, backend-derived notification center counts, and in-app-only/future email-push boundary copy.

## Boundaries and future work

Email, SMS, mobile push, webhook, Slack/Teams, external delivery, subscription fan-out, delivery analytics, and broad enterprise notification-platform features are **future governed delivery-channel work**. They are not implemented by this mini-project.

Future email/push work may reuse source refs, preferences, category semantics, and redaction decisions, but must be added as a separate delivery-channel layer with explicit opt-in, quiet-hours, provider configuration, fail-closed behavior, audit traces, and tests. Do not infer email/push readiness from the implemented in-app notification foundation.

Notification lifecycle actions affect only notification channel state. They must not resolve source attention, mutate worker tasks, alter digest state, or change policy/governance source state unless a separate governed source capability is invoked and authorized.
