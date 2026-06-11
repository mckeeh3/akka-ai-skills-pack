# Security pattern selection

Use this doc when an Akka service needs authentication, authorization, route exposure boundaries, or basic administration. For browser user authentication, this skills pack currently supports WorkOS/AuthKit as the provider-specific path.

## Choose the smallest matching skill

| Need | Use |
| --- | --- |
| Description-first security requirements | `app-description-auth-security` |
| WorkOS/AuthKit browser sign-in and JWT-secured `/api/...` calls | `akka-workos-user-auth` |
| Local users, roles, invites, admin bootstrap, `/api/me`, disabling users | `akka-basic-user-admin` |
| HTTP bearer token validation and claim access | `akka-http-endpoint-jwt` |
| HTTP request context, headers, principals, claims | `akka-http-endpoint-request-context` |
| Internal-only HTTP surfaces | `akka-http-endpoint-acl-internal` |
| gRPC JWT security | `akka-grpc-endpoint-jwt` |
| MCP request context/JWT security | `akka-mcp-endpoint-request-context` |

## Capability-first security rule

Before choosing an endpoint, admin, WorkOS, or JWT pattern, identify the protected backend capability being exposed: capability id, allowed actors/callers, selected `AuthContext`, tenant/customer scope, required role/permission/capability, inputs/outputs, side effects, idempotency, policy/approval rule, audit/work-trace requirement, exposure surfaces, and tests.

Browser routes, HTTP/gRPC/MCP methods, workflow steps, timers, consumers, and agent tools are exposure/execution surfaces. They must preserve the same backend authorization, tenant isolation, approval, idempotency, audit, and denial semantics for the shared capability.

## Common web app security shape

```text
public static frontend
  /, /assets/**

JWT-protected APIs
  /api/me
  /api/admin/...
```

Flow:
1. WorkOS authenticates the browser user.
2. Frontend obtains an access token.
3. Frontend calls Akka with `Authorization: Bearer <token>`.
4. Akka endpoint uses `@JWT` to validate the token.
5. Endpoint reads claims through `requestContext().getJwtClaims()`.
6. Endpoint loads local Akka user/account, memberships, selected context, and capability state.
7. Backend builds or verifies `AuthContext`.
8. Backend enforces active status, tenant/customer scope, role/permission/capability grants, approval gates, and side-effect rules server-side.
9. Backend records required audit/work-trace facts for protected data access, denials, approvals, and consequential side effects.

## Authentication vs authorization

Authentication answers: who is this caller?
- WorkOS/AuthKit for generated browser user sign-in
- WorkOS JWT issuer/audience/subject/email claims
- Akka `@JWT` validation

Authorization answers: what may this caller do?
- local Akka user status
- canonical foundation roles: `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, and `AUDITOR`
- app-specific roles mapped to capabilities; do not use `APP_ADMIN` as the preferred generic platform role unless it is explicitly documented as an app alias for `SAAS_OWNER_ADMIN`
- organization/customer/self scopes in product language, backed by tenant/customer enforcement scopes
- business operation-specific rules

Do not let frontend navigation, JWT presence, email domain, route names, hidden fields, prompts, or tool descriptions authorize admin operations.

## Basic administration defaults

Use these defaults unless product requirements say otherwise:
- `GET /api/me` returns current local profile, status, roles, and scopes
- `SAAS_OWNER_ADMIN` can manage SaaS Owner users, Organizations, Organization Admin bootstrap, and platform-safe metadata without direct organization application-data access
- `TENANT_ADMIN` is the internal role id for customer-facing Organization Admins; it can manage users/customers only in assigned Organizations/Tenants
- `TENANT_EMPLOYEE` is the internal role id for customer-facing organization members/employees; it can use organization-owned app features according to mapped capabilities
- `CUSTOMER_ADMIN` can manage users only in assigned customers
- `CUSTOMER_USER` can access allowed customer-facing features only
- `AUDITOR` can read scoped audit/search and access-review surfaces without mutation
- `DISABLED` users are rejected even with valid JWTs
- startup admin bootstrap is idempotent, backend-only, and limited to initial `SAAS_OWNER_ADMIN` account/membership state; Organization Admins require a created Organization/Tenant and invitation flow

## Secret boundary

Frontend-public:
- `VITE_WORKOS_CLIENT_ID`
- `VITE_WORKOS_REDIRECT_URI`
- other `VITE_` variables intentionally embedded in the bundle

Backend-only runtime/deployment variables:
- `WORKOS_API_KEY`
- `WORKOS_API_BASE_URL` optional override for tests/proxies
- `WORKOS_JWT_ISSUER` and `WORKOS_JWT_AUDIENCE` when env-backed endpoint validation requires them
- `ADMIN_USERS`
- `APP_PUBLIC_BASE_URL`
- `RESEND_API_KEY`
- `RESEND_FROM_EMAIL` or feature-specific senders such as `INVITE_EMAIL_FROM`
- `INVITE_EMAIL_SUBJECT`
- JWT key material or deployment secrets

For new project auth setup, require `ADMIN_USERS`, `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, and `APP_PUBLIC_BASE_URL` for real local AuthKit/user-admin testing. Production invitation email delivery additionally requires `RESEND_API_KEY` and `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL`. Generated Java config helpers must trim values, treat missing/empty/blank as unset, log an `error` for each missing required backend variable with the full env var name, and never log secret values. `ADMIN_USERS` must be loaded by the single Akka `@Setup` `ServiceSetup.onStartup()` startup hook, not by endpoint lazy initialization. In production/default mode, `ADMIN_USERS` entries must create only SaaS Owner scoped `SAAS_OWNER_ADMIN` bootstrap users; tenant/customer-scoped roles are invalid until their Organization/Tenant or Customer scope exists and should be created through invitation/admin flows.

## Testing minimum

- missing token rejected
- valid token claims available in request context
- wrong issuer/audience/claim rejected when configured
- `/api/me` returns only browser-facing DTO with selected context and browser-safe capabilities
- role and tenant/customer scope checks enforced server-side
- disabled user rejected
- forbidden access, cross-tenant/customer access, and role/scope denial behavior verified
- admin bootstrap runs through `@Setup` `ServiceSetup.onStartup()`, is idempotent, creates only initial `SAAS_OWNER_ADMIN` state, rejects premature tenant/customer role entries, and does not create a permanent bypass
- missing required backend env vars produce startup/readiness error logs containing exact env var names and no secret values
- complete invitation lifecycle tests cover send/capture, resend, revoke/cancel, expiry, acceptance, delivery failure, idempotency, and no raw-token leakage
- support-access lifecycle tests cover grant, expiry, revoke, visibility, audit, and no SaaS Owner super-admin bypass
- audit/work-trace creation verified for protected data access, denials, approvals, and consequential side effects
- approval-gated consequential actions cannot commit without approval unless a documented bounded autonomy policy allows it
- frontend built assets do not contain backend secrets
