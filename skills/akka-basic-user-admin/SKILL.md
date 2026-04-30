---
name: akka-basic-user-admin
description: Implement basic user and tenant administration for WorkOS-authenticated Akka web apps, including admin roles, /api/me, user invite/bootstrap, role assignment, disabling users, and backend authorization checks.
---

# Akka Basic User Administration

Use this skill when an Akka web app needs local user/account administration after authentication is handled by WorkOS or another identity provider.

## Required reading

Read these first if present:
- `../../../docs/security-pattern-selection.md`
- `../../../docs/security-workos-auth-and-admin.md`
- `../../../docs/security-review-checklist.md`
- `../akka-workos-user-auth/SKILL.md`
- `../akka-http-endpoint-jwt/SKILL.md`
- `../akka-key-value-entities/SKILL.md` or `../akka-event-sourced-entities/SKILL.md` when implementing user/account state
- existing user/account/tenant entities, views, endpoints, and tests

## Use this skill when

- the app needs `APP_ADMIN`, `TENANT_ADMIN`, `CUSTOMER_ADMIN`, or `USER` roles
- startup should bootstrap initial admin users from environment variables
- admins should invite users or assign roles
- `/api/me` should drive role-aware frontend navigation
- admin APIs must enforce role and tenant/customer scope on the backend
- users can be disabled, activated, linked to WorkOS identity, or scoped to tenants/customers

## Core model

Authentication proves who the caller is. Local Akka admin state decides what they may do.

Recommended concepts:
- local user id
- email and display name
- external identity provider subject, e.g. WorkOS subject
- status: `INVITED`, `ACTIVE`, `DISABLED`
- roles: `APP_ADMIN`, `TENANT_ADMIN`, `CUSTOMER_ADMIN`, `USER`
- scopes: all tenants, tenant ids, customer ids, or self-only
- audit metadata for admin changes when audit is in scope

Use Key Value Entity for simple current user/account state. Use Event Sourced Entity when an audit-grade history of role/status changes is required in the state model.

## API route shape

Typical browser-facing routes:

```text
GET  /api/me
GET  /api/admin/users
POST /api/admin/users/invite
POST /api/admin/users/{userId}/roles
POST /api/admin/users/{userId}/disable
POST /api/admin/users/{userId}/activate
GET  /api/tenants
POST /api/tenants
```

All admin routes should require JWT and server-side authorization checks.

## Authorization rules

Recommended defaults:
- `APP_ADMIN`: manage all users, tenants, customers, and roles
- `TENANT_ADMIN`: manage users/customers within assigned tenant ids
- `CUSTOMER_ADMIN`: manage users within assigned customer ids
- `USER`: read/update own basic profile only

Rules:
- check caller status is `ACTIVE`
- reject `DISABLED` users even with a valid JWT
- validate target tenant/customer scope on every admin action
- prevent privilege escalation, such as a tenant admin assigning `APP_ADMIN`
- require idempotent behavior for invites and repeated role assignments where possible
- return `401` for missing/invalid authentication and `403` for authenticated forbidden actions unless resource-hiding policy says otherwise

## Startup admin bootstrap

Read backend-only environment variables such as:

```bash
ADMIN_USERS="jane@gmail.com:APP_ADMIN:ALL,joe@outlook.com:TENANT_ADMIN:tenant-123"
APP_BASE_URL="http://localhost:9000"
WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
```

Optional invite-email settings:

```bash
RESEND_API_KEY="re_xxxxxxxxx"
INVITE_EMAIL_FROM="Acme <onboarding@example.com>"
INVITE_EMAIL_SUBJECT="Account access information"
```

Bootstrap rules:
- parse entries defensively
- normalize email addresses
- create missing invited/admin users idempotently
- update only allowed bootstrap-managed fields on repeated startup
- never expose bootstrap secrets to frontend assets
- surface invalid bootstrap config clearly at startup or in operational status

## Invite and first-login flow

1. Admin invites a user with email, roles, and scopes.
2. Backend creates an `INVITED` local user.
3. Backend optionally sends invite email.
4. User signs in through WorkOS.
5. `/api/me` links WorkOS subject to invited local account.
6. Backend activates the account and returns current profile/roles/scopes.

Do not let an uninvited WorkOS identity become an admin unless self-registration and role policy explicitly allow it.

## Endpoint implementation guidance

In endpoint methods:
- validate JWT with `@JWT`
- read identity claims via `requestContext().getJwtClaims()`
- load caller local user/account
- call an authorization helper or component method with caller + target scope
- return endpoint-facing DTOs only
- map validation and forbidden outcomes to explicit HTTP responses

Keep business authorization out of frontend code. The frontend may use `/api/me` to choose navigation, but endpoints must enforce every permission again.

## Testing rules

Add tests for:
- bootstrap creates configured admins idempotently
- `/api/me` returns active linked user roles/scopes
- missing JWT rejected for admin APIs
- disabled user rejected despite valid JWT
- non-admin cannot list users
- tenant admin cannot manage another tenant
- tenant admin cannot grant `APP_ADMIN`
- repeated invite/role assignment is idempotent or returns documented conflict
- frontend receives enough `/api/me` data for navigation without leaking internals

## Anti-patterns

Avoid:
- making JWT role claims the only source of mutable app authorization when roles are app-managed
- trusting frontend route guards as authorization
- allowing first login to auto-create privileged users accidentally
- storing backend API keys in frontend env files
- returning full internal user entities to the browser
- omitting scope checks from list/query endpoints
