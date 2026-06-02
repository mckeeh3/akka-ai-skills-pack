# Notification Email Channel Validation

Task: `TASK-NEDC-04-001`
Date: 2026-06-02

## Scope

Validated the rendered AI-first SaaS starter scaffold for the notification email delivery channel. The validation focused on Resend production boundary behavior, local/dev/test captured outbox behavior, preferences/category allowlist enforcement, redaction, idempotency, and My Account/workstream email preference surfaces.

## Checks run

```bash
git diff --check
tools/validate-ai-first-saas-starter-fullstack.sh \
  --app-name "Email Channel Validation Starter" \
  --app-slug "email-channel-validation-starter" \
  --base-package "ai.emailvalidation" \
  --maven-group-id "ai.emailvalidation"
rg -n "EmailNotificationServiceTest|captured_outbox|resend-config-missing|preference-required|category-not-allowlisted|\\[redacted\\]|dedupeKey|EMAIL_NOTIFICATION_DELIVERY_CAPTURED|RESEND_API_KEY|RESEND_FROM_EMAIL|future SMS/push/webhook|SMS, mobile push, Slack/Teams, and webhooks" \
  templates/ai-first-saas-starter/backend/src \
  templates/ai-first-saas-starter/frontend/src \
  specs/notification-email-delivery-channel \
  --glob '!**/node_modules/**'
```

## Results

- `git diff --check`: passed before edits.
- Scaffold generation: passed; rendered scaffold target was `/tmp/ai-first-saas-starter-fullstack.id6nI5`.
- Scaffolded backend Maven tests: passed with `BUILD SUCCESS`; 239 tests run, 0 failures, 0 errors, 1 skipped.
- Email-specific backend test evidence:
  - `EmailNotificationServiceTest` ran 4 tests with 0 failures/errors.
  - Local/test delivery records `captured_outbox` and `EMAIL_NOTIFICATION_DELIVERY_CAPTURED` audit evidence.
  - Missing production Resend configuration returns failed status with `resend-config-missing` instead of fake success.
  - Preference-required and category-not-allowlisted paths return `NOT_ELIGIBLE`.
  - Duplicate projection returns the existing delivery and preserves one outbox item.
  - Email body redacts `token=secret` to `[redacted]`.
- Frontend install/tests/typecheck/build: passed.
  - Frontend tests: 132 tests, 0 failures.
  - `npm run typecheck`: passed.
  - `npm run build`: passed and produced Akka static resources.
- Built static secret scan: passed; no backend secret markers were found in built static assets.
- Local/dev captured outbox check: covered by scaffolded backend tests and focused `rg` evidence for `captured_outbox`.
- Resend fail-closed check with configuration absent: covered by scaffolded backend tests and focused `rg` evidence for `resend-config-missing`, `RESEND_API_KEY`, and `RESEND_FROM_EMAIL` handling.
- Real Resend smoke: not run; no explicit safe Resend smoke configuration was provided for this task.
- Optional real model provider smoke in the fullstack validator passed; this was not an email-provider smoke.

## Blockers

No blockers found for this validation task.

## Notes for docs task

The next docs task should summarize the current email channel as validated for the scaffolded starter scope and should preserve the boundary that future SMS, mobile push, Slack/Teams, and webhooks require separate governed delivery-channel contracts.
