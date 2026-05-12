# Identity and Trust

## Purpose

Define the authenticated seed-app trust model for the AI-first DCA reference app. This adapts the working `examples/poc-user-auth-onboarding/` pattern: WorkOS authenticates browser users; Akka-owned local account state authorizes application behavior.

## Identity provider

- Browser users sign in through WorkOS/AuthKit.
- The React/Vite frontend receives an access token from AuthKit.
- The frontend calls Akka APIs with `Authorization: Bearer <token>`.
- Akka API endpoints under `/api/...` validate bearer JWTs with `@JWT` before reading claims.
- Public frontend build assets may be served without JWT; protected API routes must require JWT.

## Local account authority

WorkOS proves the external identity. Local Akka state determines app authority.

Local account state should include:

- stable local user id;
- normalized email;
- optional display name;
- WorkOS subject when linked;
- account status: `INVITED`, `ACTIVE`, `DISABLED`;
- role assignments and tenant/customer scopes;
- audit metadata for privileged changes.

`/api/me` is the browser-facing local account bootstrap contract. It must:

- validate JWT;
- extract WorkOS subject and email claims;
- link a matching invited local account idempotently;
- activate the local account when policy allows;
- reject disabled accounts even when the JWT is valid;
- reject or return an explicit not-invited state for unknown identities when self-registration is disabled;
- return only browser-safe profile, role, status, scope, and UI capability-hint data.

Local account linking is idempotent: repeated `/api/me` calls with the same WorkOS subject/email must not duplicate users, widen scopes, or overwrite administrator-managed roles.

## Startup admin bootstrap

The seed app may bootstrap initial administrators from backend-only environment variables such as `ADMIN_USERS`.

Rules:

- bootstrap is idempotent;
- bootstrap secrets and API keys are never exposed in frontend `VITE_` variables or built assets;
- invalid bootstrap configuration must fail loudly or surface an operational error;
- repeated bootstrap may update only fields explicitly owned by bootstrap policy.

## Trust boundaries

- Frontend route guards and hidden navigation are UX only, never authorization.
- Backend endpoints must re-check local account status, roles, and scopes for every protected operation.
- JWT role claims are not the mutable application authorization source unless a future product decision explicitly changes this.
- AI agents, workflows, and tools do not inherit broad user authority implicitly; their effective permissions must be represented and audited.

## Audit and readiness implications

Identity and trust implementation must emit admin/security audit facts for bootstrap-created admins, invited users, first-login linking, activation, disable/enable, role or scope changes, and rejected privileged actions. These facts feed `../50-observability/audit-trace-and-outcomes.md` and make the seed slice testable without relying on application logs alone.

## PoC source guidance

Use these PoC ideas as implementation guidance, not as drop-in production policy:

- `MeEndpoint` as the local account bootstrap route.
- `AuthorizationService`-style centralization for auth context and role/scope checks.
- WorkOS lookup/linking boundary in a security package.
- React/AuthKit shell with same-origin `/api/...` bearer-token calls.
