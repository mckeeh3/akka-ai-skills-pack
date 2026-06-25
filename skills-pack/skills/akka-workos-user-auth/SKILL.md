---
name: akka-workos-user-auth
description: Implement WorkOS-backed user authentication for Akka-hosted web apps, including AuthKit frontend setup, JWT bearer calls to /api, Akka @JWT validation, /api/me account linking, and secret boundaries.
---

# Akka WorkOS User Auth

Use this skill for WorkOS/AuthKit browser authentication and JWT-bearing API calls into an Akka-hosted web app. Local authorization, memberships, roles, invitations, support access, and admin audit are owned by `akka-basic-user-admin` and related foundation skills.

## Required reading

- `../docs/security-pattern-selection.md`
- `../docs/security-workos-auth-and-admin.md`
- `../docs/security-review-checklist.md`
- `../docs/core-saas-identity-tenancy-admin.md`
- `../akka-basic-user-admin/SKILL.md`
- `../akka-http-endpoint-jwt/SKILL.md`
- `../akka-web-ui-api-client/SKILL.md` when browser fetch behavior is in scope

## Auth boundary

WorkOS proves identity. The Akka app owns authorization.

Implement:

- AuthKit/browser sign-in and sign-out flow
- frontend token acquisition and `Authorization: Bearer <jwt>` calls to `/api/...`
- Akka endpoint JWT validation with `@JWT` / request context claims
- `/api/me` bootstrap that links WorkOS subject/email to local Account/Profile/Membership state
- browser-safe response containing identity, selected/available scopes, capability hints, and UI bootstrap data
- fail-closed behavior for missing WorkOS/AuthKit or JWT verifier configuration, missing/invalid JWT, unknown local account, disabled account, missing membership, or unsupported selected scope

Do not put WorkOS secrets in frontend code, built static assets, `.env.local` committed files, logs, DTOs, or trace payloads. Only `VITE_` public config may be embedded in frontend builds. Missing backend WorkOS/JWT configuration must not fall back to anonymous, fixture, unsigned-token, or frontend-only authorization; protected API calls should return safe setup/denial responses with actionable server-side diagnostics.

## Typical route shape

```text
GET /api/me
POST /api/auth/logout or frontend/AuthKit sign-out route when needed
GET/POST /api/... protected app routes
```

Public static assets may be unauthenticated. Protected data/actions/streams require JWT and local authorization.

## `/api/me` contract

Return only browser-safe data:

- account id, email/display name/avatar if safe
- account status and safe recovery state
- selected organization/customer/support-access context, backed by tenant/customer isolation keys where applicable
- available contexts and role/capability hints
- visible workstreams/functional agents if UI bootstrap owns that concern
- trace/correlation ids for denied or recovery states when useful

Do not return provider tokens, raw JWTs, secrets, hidden roles, cross-tenant memberships, or unredacted admin facts.

## Implementation checklist

- Configure WorkOS/AuthKit in frontend using environment boundaries.
- Add typed API client behavior that attaches bearer tokens and maps 401/403/recovery errors.
- Secure backend endpoints with JWT validation and request-context extraction.
- Link or look up local accounts by stable WorkOS subject; use email only as a secondary/verified attribute under policy.
- Reject disabled users and users without authorized local membership.
- Emit admin/security audit events for first link, failed link, denied disabled access, scope selection, and suspicious mismatch where required.
- Keep local/dev/test auth fixtures explicit and test-only.

## Tests

Cover:

- missing WorkOS/AuthKit or JWT verifier configuration fails closed for protected API calls
- no token, invalid token, expired token, malformed claims
- valid WorkOS identity with active local account
- unknown identity recovery/denial path
- disabled local account denial despite valid JWT
- selected organization/customer scope allowed and denied cases, including tenant/customer enforcement keys where applicable
- `/api/me` redaction and frontend-safe DTO shape
- frontend API client attaches bearer token and handles 401/403/recovery states
- committed/static assets do not contain WorkOS secrets

Completion requires the real local Akka/API/UI path for protected routes. Mocked JWTs and AuthKit test doubles are acceptable only inside tests.
