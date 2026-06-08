# TASK-FCSR-08-001: Run live WorkOS/AuthKit provider smoke

## Objective

Validate production-like WorkOS/AuthKit authentication against a real configured provider boundary.

## Blocker

Do not start until backend-only WorkOS issuer/audience/provider values and a real AuthKit app/client are supplied.

## Required reads

- `AGENTS.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `specs/full-core-saas-readiness/auth-runtime-boundary-validation.md`
- `app-description/app.md` and `app-description/domains/core-starter/realization/traceability.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `frontend/src/main.tsx`
- `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`
- `src/main/java/ai/first/application/foundation/identity/**`

## Skills

- `akka-workos-user-auth`
- `akka-http-endpoint-jwt`
- `akka-web-ui-testing`

## Expected outputs

- Live-provider smoke evidence or precise blocker refresh.
- Queue/readiness updates.

## Required checks

- `git diff --check`
- Focused backend/frontend auth checks plus the live-provider smoke command/runbook.

## Done criteria

- Real issuer/audience/AuthKit app smoke validates JWT-bearing `/api/me` and protected workstream APIs without frontend secrets.
- Changes and queue update are committed.
