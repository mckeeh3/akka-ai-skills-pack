# WorkOS authentication, JWT-secured APIs, and basic user administration

Use this doc when an Akka service hosts a browser frontend and uses WorkOS for end-user authentication.

This is an agent-oriented security integration guide. It keeps identity-provider authentication separate from application authorization.

## Recommended full-stack shape

```text
public frontend routes
  /              -> frontend index.html
  /assets/**     -> built JS/CSS/assets

JWT-protected backend APIs
  /api/me
  /api/admin/users
  /api/tenants
  /api/tenants/{tenantId}/customers
```

Rules:
- static frontend assets may be public
- backend API routes require a bearer token
- the frontend sends the token in `Authorization: Bearer <token>`
- backend endpoints validate JWTs with Akka `@JWT`
- backend authorization decisions use Akka-owned user/account/role state, not hidden frontend navigation

## Frontend WorkOS integration

For React apps, wrap the browser app with WorkOS AuthKit:

```tsx
import { AuthKitProvider, useAuth } from '@workos-inc/authkit-react';

<AuthKitProvider clientId={clientId} redirectUri={redirectUri}>
  <SecureShell />
</AuthKitProvider>
```

Read public browser-side configuration from Vite variables:

```ts
const clientId = import.meta.env.VITE_WORKOS_CLIENT_ID ?? '';
const redirectUri = import.meta.env.VITE_WORKOS_REDIRECT_URI ?? window.location.origin;
```

Local `.env.local` example:

```env
VITE_WORKOS_CLIENT_ID=client_your_workos_client_id
VITE_WORKOS_REDIRECT_URI=http://localhost:9000
```

Only `VITE_` variables are embedded into the frontend bundle. Treat them as public.

Never put backend secrets in frontend env files:
- WorkOS API key
- email provider API keys
- invite sender credentials
- database or service credentials

## Calling Akka APIs from the frontend

After sign-in, get an access token and call same-origin APIs:

```ts
const token = await auth.getAccessToken();

const response = await fetch('/api/me', {
  headers: {
    Authorization: `Bearer ${token}`
  }
});
```

Prefer relative `/api/...` URLs when Akka hosts the frontend. This avoids production CORS complexity.

For Vite dev server mode, proxy `/api` to Akka and configure WorkOS allowed origins/redirects for both the Akka origin and the Vite dev origin.

## Akka JWT-protected endpoint pattern

Use public internet ACL plus bearer JWT validation for browser-callable protected APIs:

```java
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/me")
public class MeEndpoint extends AbstractHttpEndpoint {
  @Get
  public MeResponse me() {
    var claims = requestContext().getJwtClaims();
    var subject = claims.subject().orElseThrow();
    // Load or link the local Akka user by WorkOS subject/email.
    return ...;
  }
}
```

Use `bearerTokenIssuers` and `staticClaims` when the issuer/audience/claim contract is known:

```java
@JWT(
    validate = JWT.JwtMethodMode.BEARER_TOKEN,
    bearerTokenIssuers = "configured-workos-issuer",
    staticClaims = @JWT.StaticClaim(claim = "aud", values = "${WORKOS_AUDIENCE}"))
```

Production services must configure trusted JWT keys/issuer behavior according to Akka JWT configuration and the identity provider's token contract. Integration tests and local dev may use unsigned `alg: none` tokens, but production must not rely on that behavior.

## `/api/me` contract

Use `/api/me` as the frontend's local account bootstrap endpoint.

Responsibilities:
- validate JWT
- read WorkOS identity claims from `requestContext().getJwtClaims()`
- find or link the local Akka user account
- return browser-facing profile, status, role, and tenant/customer scope DTOs
- never return internal secrets or full domain state accidentally

Example response shape:

```json
{
  "userId": "user-123",
  "email": "jane@example.com",
  "displayName": "Jane Example",
  "status": "ACTIVE",
  "roles": ["APP_ADMIN"],
  "tenantIds": ["tenant-123"]
}
```

## Authorization model

WorkOS authenticates the user. Akka application state authorizes actions.

Typical roles:

```text
APP_ADMIN       global administration
TENANT_ADMIN    tenant-scoped administration
CUSTOMER_ADMIN  customer/user-scoped administration
USER            basic authenticated access
```

Rules:
- the frontend may hide or show navigation from `/api/me`
- every protected backend operation must re-check authorization server-side
- tenant/customer scope must be checked on backend queries and commands
- do not rely solely on JWT role claims for app authorization unless the app explicitly chooses that model
- prefer local Akka user/account/role state for mutable application roles

## Basic administration APIs

Common API families:

```text
GET  /api/me
GET  /api/admin/users
POST /api/admin/users/invite
POST /api/admin/users/{userId}/roles
POST /api/admin/users/{userId}/disable
GET  /api/tenants
POST /api/tenants
```

Authorization examples:
- `APP_ADMIN` may manage all users, tenants, and roles
- `TENANT_ADMIN` may manage users and customers within assigned tenant scope
- `CUSTOMER_ADMIN` may manage users within assigned customer scope
- `USER` may view/update own profile only

Backend checks should return clear `401` for unauthenticated requests and `403` for authenticated but forbidden requests unless the app intentionally hides resource existence.

## Startup admin bootstrap

Use backend-only environment variables for initial admin bootstrap, for example:

```bash
export ADMIN_USERS="jane@gmail.com:APP_ADMIN:ALL,joe@outlook.com:TENANT_ADMIN:tenant-123"
export WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
export APP_BASE_URL="http://localhost:9000"
```

Optional invite-email settings are also backend-only:

```bash
export RESEND_API_KEY="re_xxxxxxxxx"
export INVITE_EMAIL_FROM="Acme <onboarding@example.com>"
export INVITE_EMAIL_SUBJECT="Account access information"
```

Bootstrap behavior:
- parse configured initial admins at startup
- create invited local Akka user accounts idempotently
- optionally send invite emails
- when the user signs in through WorkOS, link WorkOS identity to the invited local account
- activate the local account after successful link

## Testing checklist

- [ ] frontend calls `/api/me` with `Authorization: Bearer ...`
- [ ] protected APIs reject missing bearer tokens
- [ ] endpoint tests inject bearer tokens with expected claims
- [ ] `/api/me` returns browser-facing DTOs only
- [ ] admin endpoints enforce role and tenant/customer scope on the backend
- [ ] frontend navigation is treated as UX only, not authorization
- [ ] backend secrets are not present in `frontend/.env*` or built assets
- [ ] startup bootstrap is idempotent
- [ ] invite/link/activate flow has success and failure tests
- [ ] admin actions are auditable when audit is in scope
