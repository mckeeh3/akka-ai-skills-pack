---
name: akka-workos-user-auth
description: Implement WorkOS-backed user authentication for Akka-hosted web apps, including AuthKit frontend setup, JWT bearer calls to /api, Akka @JWT validation, /api/me account linking, and secret boundaries.
---

# Akka WorkOS User Authentication

Use this skill when a browser frontend authenticates users through WorkOS and calls Akka backend APIs with bearer JWTs.

## Required reading

Read these first if present:
- `../../../docs/security-pattern-selection.md`
- `../../../docs/security-workos-auth-and-admin.md`
- `../../../docs/security-review-checklist.md`
- `../../../frontend-with-akka-backend.md` — authentication, frontend API calls, JWT validation, authorization model, bootstrap, and security notes
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

## Security model

Separate concerns:

1. WorkOS authenticates the browser user and issues tokens.
2. Akka `@JWT` validates bearer tokens at API endpoints.
3. Akka-owned user/account/role state authorizes application actions.
4. Frontend navigation is only UX; it is not an authorization control.

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

Use `AbstractHttpEndpoint` when reading claims:

```java
var claims = requestContext().getJwtClaims();
var subject = claims.subject().orElseThrow();
var email = claims.getString("email").orElse(null);
```

Prefer issuer/audience claim validation when the token contract is known:

```java
@JWT(
    validate = JWT.JwtMethodMode.BEARER_TOKEN,
    bearerTokenIssuers = "configured-workos-issuer",
    staticClaims = @JWT.StaticClaim(claim = "aud", values = "${WORKOS_AUDIENCE}"))
```

## `/api/me` rules

`GET /api/me` should:
- require JWT
- read WorkOS identity claims
- find existing linked local account, or link an invited account when policy allows
- return a browser-facing `MeResponse`
- include status, roles, and allowed scopes needed for navigation
- not expose internal user entity state or secrets

Typical local statuses:
- `INVITED`
- `ACTIVE`
- `DISABLED`

## First-login linking rules

When an invited user signs in:
1. validate JWT
2. extract stable WorkOS subject and email claims
3. find invited local account by normalized email or invite token policy
4. link WorkOS subject to local account idempotently
5. activate account if allowed
6. return active `/api/me` response

Reject or return a pending state when:
- no matching invite exists and self-registration is disabled
- the local account is disabled
- email/domain does not match allowed policy
- WorkOS subject is already linked to another local account

## Testing rules

Add tests for:
- missing bearer token rejected
- bearer token claims available through `requestContext().getJwtClaims()`
- `/api/me` returns linked active account
- invited account first-login link is idempotent
- disabled account cannot access app shell/API
- frontend API client attaches `Authorization: Bearer ...` if frontend tests exist

Integration tests may use unsigned `alg: none` JWTs locally, but production configuration must rely on real trusted JWT key/issuer setup.

## Anti-patterns

Avoid:
- trusting hidden frontend navigation for authorization
- storing mutable application roles only in frontend state
- relying solely on role claims when app roles are managed in Akka
- putting WorkOS API keys in frontend env files
- returning internal user/account entity records from `/api/me`
- reading JWT claims without `@JWT`
