# TASK-FCSR-08-002: Run live Resend invite-email provider smoke

## Objective

Validate live Resend invitation delivery through the backend-only provider boundary.

## Blocker

Do not start until backend-only Resend API key and verified sender/domain configuration are supplied.

## Required reads

- `AGENTS.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `specs/full-core-saas-readiness/invitation-onboarding-validation.md`
- `src/main/java/ai/first/application/foundation/invitation/**`
- `src/main/java/ai/first/application/foundation/email/**`

## Skills

- `akka-resend-email-service`
- `akka-saas-invitation-onboarding`

## Expected outputs

- Live Resend delivery smoke evidence or precise blocker refresh.
- Queue/readiness updates.

## Required checks

- `git diff --check`
- Focused invitation/email tests plus the live-provider smoke command/runbook.

## Done criteria

- A real invitation email is delivered through Resend without exposing raw tokens or provider secrets in browser-safe state.
- Changes and queue update are committed.
