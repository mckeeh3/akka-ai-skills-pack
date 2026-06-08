# Notification Email Channel Handoff

## Status

The notification email delivery channel mini-project has implemented and validated the first governed **email notification channel** for the AI-first SaaS starter/reference template.

Validated scope is limited to backend-owned notification email delivery over authorized notification/source state, plus My Account email preferences. The channel is not a source-of-truth notification system, not a marketing email system, and not a general provider-selection platform.

## Implemented email channel

Implemented starter/reference assets include:

- backend email notification contract and delivery service over authorized notification projection inputs;
- production **Resend** delivery through the reusable Resend email service boundary;
- explicit local/dev/test **captured outbox** behavior that records intended email without external delivery;
- provider fail-closed handling when required Resend configuration, such as `RESEND_API_KEY` or `RESEND_FROM_EMAIL`, is absent or blank;
- preference and category allowlist checks before enqueue or send;
- backend-rendered redacted subject, preview, text, and HTML content with token/secret leakage guarded;
- idempotent delivery lookup by stable dedupe key so duplicate projections do not create duplicate outbox messages;
- audit evidence for captured delivery, Resend/config failure, preference denial, allowlist denial, and duplicate handling;
- My Account email notification preference surfaces backed by backend-derived channel/category data.

## Validation evidence

See `email-channel-validation.md`.

Rendered scaffold validation passed:

- `git diff --check` passed;
- fullstack starter validation passed for `Email Channel Validation Starter`;
- scaffolded backend Maven tests passed: 239 tests run, 0 failures, 0 errors, 1 skipped;
- `EmailNotificationServiceTest` covered captured outbox, Resend configuration fail-closed behavior, preference-required denial, category allowlist denial, redaction, and idempotency;
- frontend tests, typecheck, and build passed;
- focused scans found `captured_outbox`, `EMAIL_NOTIFICATION_DELIVERY_CAPTURED`, `resend-config-missing`, `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, preference/category denial terms, redaction markers, dedupe keys, and future channel boundary copy;
- real Resend smoke was not run because no explicit safe Resend smoke configuration was provided.

## Operating boundaries

Email delivery remains a governed delivery channel layered over authorized notification/source state:

```text
authorized notification/source state
→ email eligibility check with AuthContext, recipient, preferences, category allowlist, and redaction proof
→ durable delivery/outbox intent
→ captured outbox for local/dev/test OR Resend for production
→ audit/work trace and redacted status
```

Do not infer email readiness from in-app notification readiness alone. The email path must preserve preferences, redaction, idempotency, audit, provider fail-closed behavior, and backend-only provider secrets.

Production mode must use Resend and must not silently fall back to captured outbox or fake success when provider configuration is missing. Local/dev/test mode must label captured delivery as captured, not sent.

## Future SMS/push/webhook boundary

SMS, mobile push, Slack/Teams, webhooks, marketing email, delivery analytics, and provider selection remain out of scope.

Future SMS/push/webhook channels must be separate governed delivery-channel contracts with their own preferences, provider configuration, provider fail-closed behavior, redaction rules, idempotency keys, audit traces, frontend surfaces, and tests. Do not add them as hidden toggles or implied extensions of the email preference surface.
