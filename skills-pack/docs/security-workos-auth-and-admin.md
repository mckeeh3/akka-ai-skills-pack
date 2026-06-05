# WorkOS authentication, JWT-secured APIs, and basic user administration

Use this doc when an Akka service hosts a browser frontend. WorkOS/AuthKit is the supported user authentication service for generated browser apps in this skills pack.

This is an agent-oriented security integration guide. It keeps WorkOS authentication separate from Akka-owned application authorization. Do not substitute a different user auth service unless the skills pack has been explicitly extended with matching skills, docs, examples, and tests.

## Required runtime configuration

Keep backend secrets in backend runtime environment variables or deployment secrets. Keep frontend settings in Vite `VITE_` variables only.

Backend runtime variables:

```bash
export ADMIN_USERS="jane@example.com:SAAS_OWNER_ADMIN:OWNER,joe@example.com:TENANT_ADMIN:tenant-123"
export WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
export WORKOS_API_BASE_URL="https://api.workos.com" # optional override for tests/proxies
export WORKOS_JWT_ISSUER="configured-workos-issuer" # when env-backed JWT config is used
export WORKOS_JWT_AUDIENCE="configured-workos-audience" # when env-backed JWT config is used
export APP_PUBLIC_BASE_URL="http://localhost:9000"
export RESEND_API_KEY="re_xxxxxxxxx"
export INVITE_EMAIL_FROM="Your App <onboarding@example.com>"
export INVITE_EMAIL_SUBJECT="Account access information"
```

Frontend build-time variables:

```env
VITE_WORKOS_CLIENT_ID=client_your_workos_client_id
VITE_WORKOS_REDIRECT_URI=http://localhost:9000
```

When issuer/audience checking is part of the endpoint contract, record the WorkOS JWT issuer and audience in backend-only deployment configuration, commonly `WORKOS_JWT_ISSUER` and `WORKOS_JWT_AUDIENCE`, and use those values in Akka JWT configuration/annotations. Never put `WORKOS_API_KEY`, Resend keys, invite sender secrets, bootstrap admin configuration, JWT key material, or service credentials in `frontend/.env*`.

For a new AI-first SaaS project, make user auth one of the first implemented and tested verticals. Minimum local setup for real AuthKit testing is:

1. configure the WorkOS/AuthKit app with the local redirect URI, usually `http://localhost:9000`;
2. set browser-public `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI` in `frontend/.env.local` only;
3. set backend-only `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, `APP_PUBLIC_BASE_URL`, and `ADMIN_USERS` in the backend `.env` or deployment secret store;
4. set production email variables `RESEND_API_KEY` and `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` before testing invitation email delivery; local/dev/test may use the captured outbox adapter instead;
5. run `/api/me`, protected API, disabled-user, forbidden-role, tenant/customer-scope, invitation-linking, and frontend secret-boundary tests before app-specific features are called ready.

## Required environment variable handling in Java

Every generated Java class that reads backend environment variables must use one small validation/helper path rather than scattered `System.getenv(...)` calls. The helper must:

- trim values and treat missing, empty, and blank values as unset;
- log an `error` for each missing required variable at the point the app validates startup readiness or blocks a required operation;
- include the full environment variable name in each log message, for example `Required backend environment variable [WORKOS_API_KEY] is not set or is blank`;
- never log secret values;
- fail startup/readiness for required production variables instead of silently falling back to insecure auth or disabled email delivery;
- distinguish optional variables such as `WORKOS_API_BASE_URL` and `RESEND_API_BASE_URL` from required variables.

Required variables for full local AuthKit/user-admin testing are `ADMIN_USERS`, `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, and `APP_PUBLIC_BASE_URL`. Required variables for production invitation email delivery are `RESEND_API_KEY` plus `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL`. Frontend `VITE_` values should be validated by frontend configuration/UI tests, not by backend Java secret loaders.

## Recommended full-stack shape

```text
public frontend routes
  /              -> frontend index.html
  /assets/**     -> built JS/CSS/assets

JWT-protected backend APIs
  /api/me
  /api/admin/users
  /api/admin/invitations
  /api/admin/memberships
  /api/admin/access-review
  /api/admin/audit
  /api/admin/support-access
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
- Resend API keys and email service secrets
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

AuthKit access tokens may not include `email`. Generated backends must not construct local identity independently in each endpoint from `claims.getString("email")`. Use one backend resolver for all browser-protected APIs:

```java
var identity = WorkosIdentityResolver.fromClaims(requestContext().getJwtClaims());
```

The resolver should read subject from `subject()`/`sub`, email from `email`, `preferred_username`, or `username`, display name from `name`, `given_name`, or `nickname`, and when subject is present but email is missing fetch the WorkOS user-management profile server-side with backend-only `WORKOS_API_KEY` and optional `WORKOS_API_BASE_URL`. Cache successful local/starter lookups through Akka components. Never expose or log `WORKOS_API_KEY`.

Use `bearerTokenIssuers` and `staticClaims` when the WorkOS issuer/audience/claim contract is known:

```java
@JWT(
    validate = JWT.JwtMethodMode.BEARER_TOKEN,
    bearerTokenIssuers = "configured-workos-issuer",
    staticClaims = @JWT.StaticClaim(claim = "aud", values = "configured-workos-audience"))
```

Production services must configure trusted JWT keys/issuer behavior according to Akka JWT configuration and the WorkOS token contract. Integration tests and local dev may use unsigned `alg: none` tokens, but production must not rely on that behavior.

## `/api/me` contract

Use `/api/me` as the frontend's local account bootstrap endpoint.

Responsibilities:
- validate JWT
- resolve WorkOS identity from `requestContext().getJwtClaims()` through the shared backend resolver; use token email claims when present, otherwise call the WorkOS user-management API server-side
- find an existing linked local account or link an invited account only through a valid invitation, invite token/acceptance context, or explicit membership policy
- return browser-facing profile, status, role, and tenant/customer scope DTOs
- never return internal secrets or full domain state accidentally

The WorkOS profile lookup is identity resolution only. Local Akka account, membership, invitation, and configured `ADMIN_USERS` state remains the authorization source of truth. `/api/me` must not silently self-register privileged users from WorkOS claims or WorkOS profile data alone. If no valid invitation or accepted self-registration policy exists, return a safe pending/forbidden outcome rather than creating admin access.

Example response shape:

```json
{
  "userId": "user-123",
  "email": "jane@example.com",
  "displayName": "Jane Example",
  "status": "ACTIVE",
  "roles": ["TENANT_ADMIN"],
  "tenantIds": ["tenant-123"],
  "capabilities": ["users:list", "memberships:manage", "invitations:resend"]
}
```

## Authorization model

WorkOS authenticates the user. Akka application state authorizes actions.

Foundation roles and scoped capabilities:

```text
SAAS_OWNER_ADMIN  platform/SaaS Owner administration without direct Tenant data access
TENANT_ADMIN      tenant-scoped user, customer, support-access, and settings administration
TENANT_EMPLOYEE   tenant-scoped application use according to app capabilities
CUSTOMER_ADMIN    customer-scoped user administration and service supervision
CUSTOMER_USER     customer-facing application use
AUDITOR           scoped admin audit/search and access-review visibility without mutation
```

App-specific roles map to additional permissions/capabilities and do not replace foundation scope, membership status, or role checks. Use `SAAS_OWNER_ADMIN`, not `APP_ADMIN`, as the preferred generic SaaS Owner/platform administration role; any `APP_ADMIN` usage should be an app-specific alias with an explicit mapping to foundation capabilities.

Rules:
- the frontend may hide or show navigation from `/api/me`
- every protected backend operation must re-check authorization server-side
- tenant/customer scope must be checked on backend queries and commands
- do not rely solely on JWT role claims for app authorization unless the app explicitly chooses that model
- prefer local Akka user/account/role state for mutable application roles

## Basic administration APIs

Common API families:

```text
GET    /api/me
GET    /api/admin/users
GET    /api/admin/users/{userId}
PATCH  /api/admin/users/{userId}/profile
POST   /api/admin/users/invite
GET    /api/admin/invitations
POST   /api/admin/invitations/{invitationId}/resend
POST   /api/admin/invitations/{invitationId}/revoke
PUT    /api/admin/users/{userId}/roles
DELETE /api/admin/users/{userId}/roles/{role}
POST   /api/admin/users/{userId}/memberships
POST   /api/admin/memberships/{membershipId}/suspend
POST   /api/admin/memberships/{membershipId}/reactivate
DELETE /api/admin/memberships/{membershipId}
POST   /api/admin/users/{userId}/disable
POST   /api/admin/users/{userId}/activate
POST   /api/admin/users/{userId}/identity/relink
GET    /api/admin/access-review
GET    /api/admin/audit
POST   /api/admin/support-access/grants
POST   /api/admin/support-access/{grantId}/revoke
GET    /api/tenants
POST   /api/tenants
```

Authorization examples:
- `SAAS_OWNER_ADMIN` may manage SaaS Owner users, Tenants, Tenant Admin bootstrap, and platform-safe metadata; it must not read Tenant application data without a Tenant-scoped support-access membership
- `TENANT_ADMIN` may manage users, memberships, customers, support access, and Tenant settings within assigned tenant scope
- `CUSTOMER_ADMIN` may manage users and memberships within assigned customer scope
- `AUDITOR` may search admin audit and access-review views within assigned scope without mutation privileges
- basic users may view/update their own profile and settings only

Backend checks should return clear `401` for unauthenticated requests and `403` for authenticated but forbidden requests unless the app intentionally hides resource existence. Role removal, membership suspension/removal, account disable, identity relink, and support-access changes must enforce last-admin protection where the target scope requires an active admin.

## Admin read models and UI readiness

Initial generated apps must include operational admin discovery, not only write endpoints. Required scoped views are:
- `UserDirectoryView` for list/search users and view user detail entry points;
- `MembershipView` for membership lifecycle, role/status filters, support-access expiry, and last-admin risk;
- `InvitationView` for invitation status, delivery status, resend invite/revoke invite visibility, expiry, and failed delivery;
- `AdminAuditView` for actor/target/action/scope/time search;
- `AccessReviewQueueView` for stale invitations, dormant access, risky memberships, support-access review, and last-admin risks where applicable.

Required browser admin surfaces are Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, and Tenant/Customer Settings. A description/spec is not generation-ready if admin management is limited to invite/disable/activate or if list/search and membership lifecycle behavior are omitted.

## Startup admin bootstrap

Use backend-only environment variables for initial admin bootstrap, for example:

```bash
export ADMIN_USERS="jane@gmail.com:SAAS_OWNER_ADMIN:OWNER,joe@outlook.com:TENANT_ADMIN:tenant-123"
export WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
export WORKOS_API_BASE_URL="https://api.workos.com"
export WORKOS_JWT_ISSUER="configured-workos-issuer"
export WORKOS_JWT_AUDIENCE="configured-workos-audience"
export APP_PUBLIC_BASE_URL="http://localhost:9000"
```

Invite-email settings are backend-only and mandatory for production readiness. Use Resend (resend.com), the supported production email service for this pack:

```bash
export RESEND_API_KEY="re_xxxxxxxxx"
export INVITE_EMAIL_FROM="Your App <onboarding@example.com>"
export INVITE_EMAIL_SUBJECT="Account access information"
```

Local/dev/test environments may replace external delivery with an explicit safe adapter that captures emails in an outbox for inspection without sending externally. Production startup/readiness must fail or report not-ready when required Resend configuration is missing.

Bootstrap behavior:
- implement startup bootstrap in the service's single Akka `@Setup` class implementing `akka.javasdk.ServiceSetup`; `onStartup()` must load `ADMIN_USERS` before `/api/me` or admin endpoints depend on local admin state
- keep bootstrap idempotent because Akka invokes `onStartup()` for each service instance and restart; do not rely on endpoint lazy initialization or tests calling the bootstrap helper directly as the production startup path
- parse configured initial admins at startup using canonical foundation roles (`SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, `AUDITOR`) plus explicitly mapped app-specific roles when needed
- create invited local Akka user accounts idempotently
- create Invitation records with invite token or acceptance context, status, expiry, delivery status, delivery attempts, and audit metadata
- send invite emails in production or capture them in the local/dev/test outbox adapter
- surface delivery failures to authorized admins and record AdminAuditEvent facts
- support idempotent resend and revoke/cancel before acceptance
- when the user signs in through WorkOS, link WorkOS identity to the invited local account only through a valid invitation or membership policy
- activate the local account after successful link and invitation acceptance

## Testing checklist

- [ ] frontend calls `/api/me` with `Authorization: Bearer ...`
- [ ] protected APIs reject missing bearer tokens
- [ ] endpoint tests inject bearer tokens with expected claims
- [ ] `/api/me` returns browser-facing DTOs only
- [ ] admin endpoints enforce role and tenant/customer scope on the backend
- [ ] UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView list/search only authorized scoped rows and redact protected fields
- [ ] admin operations cover invite, resend invite, revoke invite, list/search, view user detail, edit allowed profile fields, role assignment/replacement/removal, membership lifecycle, disable/reactivate, reset/relink identity subject under policy, support-access grant/revoke/expiry, and last-admin protection
- [ ] frontend navigation is treated as UX only, not authorization
- [ ] backend secrets are not present in `frontend/.env*` or built assets
- [ ] startup bootstrap is implemented through the single Akka `@Setup` `ServiceSetup.onStartup()` path and is idempotent
- [ ] startup/readiness validation logs each missing required backend environment variable as an error and includes the full env var name, without logging secret values
- [ ] production readiness fails when required Resend invite email delivery configuration is missing
- [ ] local/dev/test invite email adapter captures messages in an outbox without external delivery
- [ ] invite/link/activate flow has send, resend, revoke/cancel, expiry, delivery failure, acceptance, idempotency, and audit tests
- [ ] admin actions emit required AdminAuditEvent records for identity, membership, role, support-access, data-access, access-review, and forbidden attempts
