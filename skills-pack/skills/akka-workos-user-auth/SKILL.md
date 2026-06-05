---
name: akka-workos-user-auth
description: Implement WorkOS-backed user authentication for Akka-hosted web apps, including AuthKit frontend setup, JWT bearer calls to /api, Akka @JWT validation, /api/me account linking, and secret boundaries.
---

# Akka WorkOS User Authentication

Use this skill when a browser frontend authenticates users through WorkOS and calls Akka backend APIs with bearer JWTs. WorkOS/AuthKit is the supported user auth service for generated browser apps in this skills pack; do not introduce another user auth provider unless the pack has first been extended with provider-specific skills, docs, examples, and tests.

## Required reading

Read these first if present:
- `../docs/security-pattern-selection.md`
- `../docs/security-workos-auth-and-admin.md`
- `../docs/security-review-checklist.md`
- `../docs/frontend-with-akka-backend.md` — authentication, frontend API calls, JWT validation, authorization model, bootstrap, and security notes
- `../akka-http-endpoint-jwt/SKILL.md`
- `../akka-http-endpoint-request-context/SKILL.md`
- `../akka-web-ui-frontend-project/SKILL.md`
- project `frontend/package.json`
- project frontend auth shell/components
- existing `/api/me` endpoint and user/account components

## Use this skill when

- the user asks for login/sign-in/sign-out with WorkOS
- the frontend should use WorkOS AuthKit React
- browser APIs should send WorkOS access tokens as bearer tokens
- Akka endpoints should validate JWTs with `@JWT`
- `/api/me` should return the signed-in user's local Akka account/profile/roles
- first login should link a WorkOS identity to a local invited account

## Boundary with foundation and UI skills

This skill owns WorkOS/AuthKit authentication, bearer-token transport, Akka JWT validation, `/api/me` identity bootstrap/linking, and frontend/backend secret boundaries.

When a sprint, task, or user request says `user auth`, `sign-in`, `login`, or `authentication` is implemented, do not interpret that as only adding AuthKit widgets or a JWT annotation. At the stated scope, the feature is implemented only when a developer can run the Akka app locally, sign in or use the configured local/test token path, call `/api/me`, receive the browser-safe local authorization context, exercise at least one protected API/UI path, observe disabled/forbidden handling, and run the required auth, secret-boundary, and audit tests. If invitation linking, `/api/me`, local authorization, frontend bearer-token calls, protected route checks, or tests are deferred, label the output as a narrower auth seam or mark the task/sprint incomplete; do not call user auth fully implemented.

It does not replace:
- `akka-basic-user-admin` for local Account, Membership, Role/Capability, UserDirectoryView, support-access, admin audit, and backend authorization semantics;
- `akka-saas-invitation-onboarding` for complete invitation lifecycle, acceptance, expiry, resend/revoke, delivery status, and onboarding tests;
- `akka-resend-email-service` for production Resend delivery/outbox and captured local/dev/test email behavior;
- `akka-web-ui-apps` and companions for the workstream shell, functional-agent rail, structured surfaces, rendering, and frontend UX.

Frontend route guards, left-rail visibility, hidden buttons, and WorkOS claims are not sufficient authorization. Every protected backend route, stream, component action, and agent tool must re-check local Akka-owned authorization for the selected `AuthContext`.

## Runtime configuration

Set up and test user auth as one of the first verticals in every new AI-first SaaS project.

Backend-only runtime variables or deployment secrets:

```bash
ADMIN_USERS="jane@example.com:SAAS_OWNER_ADMIN:OWNER,joe@example.com:TENANT_ADMIN:tenant-123"
WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
WORKOS_API_BASE_URL="https://api.workos.com" # optional override for tests/proxies
WORKOS_JWT_ISSUER="configured-workos-issuer"
WORKOS_JWT_AUDIENCE="configured-workos-audience"
APP_PUBLIC_BASE_URL="http://localhost:9000"
RESEND_API_KEY="re_xxxxxxxxx" # required for production invite email delivery
INVITE_EMAIL_FROM="Your App <onboarding@example.com>" # or RESEND_FROM_EMAIL for shared sender config
INVITE_EMAIL_SUBJECT="Account access information"
```

Frontend build-time public variables:

```env
VITE_WORKOS_CLIENT_ID=client_your_workos_client_id
VITE_WORKOS_REDIRECT_URI=http://localhost:9000
```

New-project setup checklist:
1. configure the WorkOS/AuthKit app redirect/callback URI, usually `http://localhost:9000` locally;
2. put only `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI` in `frontend/.env.local`;
3. put `ADMIN_USERS`, `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, and `APP_PUBLIC_BASE_URL` in backend `.env` or deployment secrets;
4. put `RESEND_API_KEY` and `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` in backend secrets before production invite email delivery is considered ready;
5. run `/api/me`, protected API, invitation-linking, disabled-user, forbidden-role/scope, tenant/customer-isolation, and frontend secret-boundary tests before app-specific features;
6. run the Akka app locally and verify the intended sign-in/auth flow or documented local/test-token substitute reaches the authenticated shell and protected API path before marking the auth sprint complete.

If issuer/audience validation is required, store WorkOS issuer/audience values in backend-only deployment configuration, commonly `WORKOS_JWT_ISSUER` and `WORKOS_JWT_AUDIENCE`, and apply them in Akka JWT configuration/annotations. Never put `WORKOS_API_KEY`, Resend keys, bootstrap admin data, JWT key material, or service credentials in `frontend/.env*`.

Java environment-variable handling rules:
- load `ADMIN_USERS` through the app's single Akka `@Setup` `ServiceSetup.onStartup()` bootstrap path so initial admin state exists before protected routes depend on it; startup bootstrap must be idempotent because each service instance/restart may run it;
- use a shared env/config helper for backend variables; do not scatter raw `System.getenv(...)` calls;
- trim values and treat missing, empty, and blank values as unset;
- when a required variable is missing at startup/readiness validation or while blocking a required operation, log `error` and include the full env var name, e.g. `Required backend environment variable [WORKOS_API_KEY] is not set or is blank`;
- never log secret values;
- fail startup/readiness for required production variables instead of silently disabling auth or email delivery;
- keep optional variables explicit (`WORKOS_API_BASE_URL`, `RESEND_API_BASE_URL`) and do not log them as errors when absent.

## Security model

Separate concerns:

1. WorkOS authenticates the browser user and issues tokens.
2. Akka `@JWT` validates bearer tokens at API endpoints.
3. Akka-owned Account, Membership, Role, Permission/Capability, Tenant, Customer, and selected AuthContext state authorizes application actions.
4. Frontend navigation is only UX; it is not an authorization control.
5. `/api/me` is the browser-safe context bootstrap; every protected API still rechecks authorization server-side.
6. AdminAuditEvent/work traces record identity, authorization denials, support access, role changes, and consequential data/tool access.

## Frontend implementation rules

For React/AuthKit:

```tsx
<AuthKitProvider clientId={clientId} redirectUri={redirectUri}>
  <SecureShell />
</AuthKitProvider>
```

Use public Vite variables only:

```ts
const clientId = import.meta.env.VITE_WORKOS_CLIENT_ID ?? '';
const redirectUri = import.meta.env.VITE_WORKOS_REDIRECT_URI ?? window.location.origin;
```

Call Akka APIs with bearer tokens:

```ts
const token = await auth.getAccessToken();
await fetch('/api/me', { headers: { Authorization: `Bearer ${token}` } });
```

Rules:
- use relative `/api/...` URLs when Akka hosts the frontend
- never put `WORKOS_API_KEY`, email API keys, or other backend secrets in `frontend/.env*`
- treat `VITE_` values as public because they are embedded in the bundle

## Akka endpoint rules

For browser-callable protected APIs:

```java
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/me")
public class MeEndpoint extends AbstractHttpEndpoint {
}
```

Use `AbstractHttpEndpoint` plus a reusable backend identity resolver when reading claims. AuthKit access tokens may contain `sub` without `email`; generated code must not assume `claims.getString("email")` is present in every endpoint.

```java
var identity = WorkosIdentityResolver.fromClaims(requestContext().getJwtClaims());
```

The resolver must:
- read subject from `JwtClaims.subject()` or raw `sub`;
- read email from `email`, `preferred_username`, or `username` when present;
- read display name from `name`, `given_name`, or `nickname` when present;
- when subject exists but email is missing, call the WorkOS user-management API server-side with backend-only `WORKOS_API_KEY` and optional `WORKOS_API_BASE_URL`;
- cache successful local/core app profile lookups through Akka components;
- never expose or log `WORKOS_API_KEY`;
- return only resolved identity data, leaving local Akka account/membership/invitation checks authoritative.

Apply the shared resolver to every browser-protected endpoint that resolves the signed-in user, including `/api/me`, workstream bootstrap/action/event APIs, admin APIs, and invitation acceptance/admin APIs. Do not duplicate endpoint-local `new WorkosIdentity(claims.subject(), claims.getString("email"), ...)` construction.

Prefer issuer/audience claim validation when the WorkOS token contract is known:

```java
@JWT(
    validate = JWT.JwtMethodMode.BEARER_TOKEN,
    bearerTokenIssuers = "configured-workos-issuer",
    staticClaims = @JWT.StaticClaim(claim = "aud", values = "configured-workos-audience"))
```

## `/api/me` rules

`GET /api/me` should:
- require JWT
- resolve WorkOS identity through the shared backend resolver; use token email claims when available, otherwise fetch the WorkOS profile server-side using backend-only `WORKOS_API_KEY`
- find existing linked local account, or link an invited account only through a valid invitation, invite token/acceptance context, or explicit membership policy
- return a browser-facing `MeResponse`
- include account status, memberships, selected/default AuthContext, tenant/customer ids, roles/capabilities, profile, settings, and context-switch options needed for navigation
- reject disabled users or return a safe disabled state that prevents protected API use
- not expose internal user entity state or secrets

Typical local statuses:
- `INVITED`
- `ACTIVE`
- `DISABLED`

## First-login linking rules

When an invited user signs in:
1. validate JWT
2. extract stable WorkOS subject and resolve email via token claims or server-side WorkOS profile lookup
3. find invited local account by normalized email plus valid invitation, invite token policy, or acceptance context
4. reject expired, revoked/cancelled, delivery-failed-without-admin-override, or already-accepted-by-another-subject invitations
5. link WorkOS subject to local account idempotently
6. accept the invitation, activate account/membership if allowed, and record audit facts
7. return active `/api/me` response

Reject or return a pending state when:
- no matching valid invitation or membership policy exists and self-registration is disabled
- the local account is disabled
- email/domain does not match allowed policy
- WorkOS subject is already linked to another local account
- the invitation is expired, revoked/cancelled, or outside the target membership policy

## Testing rules

Add tests for:
- missing bearer token rejected
- startup bootstrap invokes the `@Setup` `ServiceSetup.onStartup()` path and idempotently loads configured `ADMIN_USERS` before `/api/me` or admin endpoints are called
- backend env validation reports missing or blank `ADMIN_USERS`, `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, `APP_PUBLIC_BASE_URL`, and production email variables with error logs that include the exact env var names and no secret values
- bearer token claims available through `requestContext().getJwtClaims()`
- token contains `sub` and `email`, and `/api/me` resolves without WorkOS profile lookup
- token contains `sub` but no `email`, mocked WorkOS user-management API returns email, and `/api/me` resolves/links to the configured local account
- token contains `sub` but no `email`, WorkOS profile lookup is unavailable/fails, and `missing-workos-claims` or equivalent denial remains safe
- WorkOS profile lookup returns an email that is not in `ADMIN_USERS` and has no valid invitation, and `no-local-account-or-invitation` remains forbidden
- secret-boundary coverage confirms `WORKOS_API_KEY` is not emitted to frontend bundles, API responses, logs, errors, or resolved identity DTOs
- `/api/me` returns linked active account, memberships, selected AuthContext, tenant/customer scopes, and browser-safe capabilities
- invited account first-login link and replayed invitation acceptance are idempotent
- expired, revoked/cancelled, missing, and cross-scope invitations cannot activate membership
- disabled account cannot access protected APIs even with a valid JWT
- tenant/customer scope mismatch is forbidden
- identity, denial, invite/link, invitation acceptance, delivery status, membership, and role changes create required audit events
- frontend API client attaches `Authorization: Bearer ...` if frontend tests exist

Integration tests may use unsigned `alg: none` JWTs locally, but production configuration must rely on real trusted JWT key/issuer setup.

## Anti-patterns

Avoid:
- trusting hidden frontend navigation for authorization
- storing mutable application roles only in frontend state
- relying solely on role claims when app roles are managed in Akka
- putting WorkOS API keys in frontend env files
- returning internal user/account entity records from `/api/me`
- using `/api/me` to create privileged local authorization state without a valid invitation or membership policy
- reading JWT claims without `@JWT`
- assuming AuthKit access tokens always contain an `email` claim
- self-registering unknown local users only because WorkOS profile lookup returned an email
