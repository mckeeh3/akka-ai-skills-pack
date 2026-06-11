---
name: akka-basic-user-admin
description: Implement complete foundation user and tenant administration for WorkOS-authenticated Akka web apps, including /api/me, invites, user directory/search, memberships, roles, access state, support access, admin audit, and backend authorization checks.
---

# Akka Basic User Administration

Use this skill for the local authorization and admin-management foundation after WorkOS/AuthKit authentication. It owns Account/Profile/Settings, Tenant creation/maintenance, Tenant Admin bootstrap and lifecycle, Tenant/Customer memberships, roles/capabilities, user directory/search, support access, admin audit, access review, disabled-user behavior, and backend authorization checks.

## Required reading

Read the smallest relevant set:

- `../docs/security-pattern-selection.md`
- `../docs/security-workos-auth-and-admin.md`
- `../docs/security-review-checklist.md`
- `../docs/core-saas-identity-tenancy-admin.md`
- `../akka-workos-user-auth/SKILL.md` for AuthKit/JWT and `/api/me` identity bootstrap
- `../akka-saas-invitation-onboarding/SKILL.md` for invitation lifecycle, workflow, email, expiry/reminder, and invite tests
- `../akka-resend-email-service/SKILL.md` for Resend delivery/outbox
- endpoint/entity/view/test skills matching the implementation slice

## Boundary

| Concern | Primary skill |
|---|---|
| WorkOS/AuthKit sign-in, JWT bearer transport, claim extraction, identity linking | `akka-workos-user-auth` |
| Invitation entity/workflow, acceptance, expiry, resend/revoke, delivery status | `akka-saas-invitation-onboarding` |
| Resend production email and captured local/dev/test outbox | `akka-resend-email-service` |
| Protected JSON route mechanics | `akka-http-endpoints`, `akka-http-endpoint-jwt`, `akka-http-endpoint-request-context` |
| User Admin workstream UI surfaces | `akka-web-ui-apps` plus focused UI skills |

Frontend navigation and `/api/me` expose browser-safe capability hints only. Every command, query, stream, workflow action, consumer side effect, timer action, and agent tool must revalidate local Account/Membership/Role/Capability state and selected `AuthContext` on the backend.

## Core model

Authentication proves who the caller is; local Akka state decides what they may do.

Required foundation concepts:

- Account linked to WorkOS subject, UserProfile, and UserSettings
- status: `INVITED`, `ACTIVE`, `DISABLED`
- Tenant organization records with status such as `DRAFT`, `ONBOARDING`, `ACTIVE`, `SUSPENDED`, or `CLOSED`
- Tenant Admin bootstrap through invitation before privileged first sign-in
- Tenant and Customer Membership records
- canonical roles: `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, `AUDITOR`; app-specific roles extend capabilities but do not replace foundation checks
- selected `AuthContext` for protected operations
- support-access grants with expiry and audit
- AdminAuditEvent metadata for tenant lifecycle, identity, invitation, membership, role, support-access, approval, and data-access changes
- TenantDirectoryView, TenantAdminView, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView when User Admin is in scope

Use Key Value Entity for simple current state. Use Event Sourced Entity when the state model itself needs audit-grade history; Tenant lifecycle and status changes are usually audit-grade enough to prefer Event Sourced Entity.

## Typical API shape

Browser-facing admin APIs usually include:

```text
GET    /api/me
GET    /api/tenants
POST   /api/tenants
GET    /api/tenants/{tenantId}
PATCH  /api/tenants/{tenantId}
POST   /api/tenants/{tenantId}/activate
POST   /api/tenants/{tenantId}/suspend
POST   /api/tenants/{tenantId}/close
GET    /api/tenants/{tenantId}/admins
POST   /api/tenants/{tenantId}/admins/invite
PUT    /api/tenants/{tenantId}/admins/{accountId}/roles
POST   /api/tenants/{tenantId}/admins/{accountId}/suspend
POST   /api/tenants/{tenantId}/admins/{accountId}/reactivate
DELETE /api/tenants/{tenantId}/admins/{accountId}
GET    /api/admin/users
GET    /api/admin/users/{userId}
POST   /api/admin/users/invite
GET    /api/admin/invitations
POST   /api/admin/invitations/{invitationId}/resend
POST   /api/admin/invitations/{invitationId}/revoke
PUT    /api/admin/users/{userId}/roles
POST   /api/admin/users/{userId}/memberships
POST   /api/admin/users/{userId}/disable
POST   /api/admin/users/{userId}/activate
POST   /api/admin/support-access/grants
POST   /api/admin/support-access/{grantId}/revoke
GET    /api/admin/audit
GET    /api/admin/access-review
```

All protected routes require JWT, request-context extraction, active local account, selected tenant/customer scope validation, capability checks, redacted DTOs, pagination for lists, and forbidden responses for out-of-scope data. Tenant APIs must distinguish SaaS Owner platform administration from Tenant-scoped administration: SaaS Owner Admins can create and maintain Tenant organizations and bootstrap Tenant Admins; Tenant Admins can manage admins/users only inside their selected Tenant context.

## Authorization rules

- reject disabled users even with a valid JWT
- require `SAAS_OWNER_ADMIN` for Tenant creation, Tenant status changes, and SaaS Owner bootstrap of Tenant Admins
- allow `TENANT_ADMIN` to maintain Tenant Admins and users only inside their selected Tenant context
- validate target tenant/customer scope on every action
- prevent privilege escalation, including tenant admins assigning SaaS-owner privileges, creating sibling tenants, or assigning out-of-scope roles
- preserve last-admin safeguards for tenant/customer/admin scopes
- make invite/resend/membership/role commands idempotent where possible
- separate support access from ordinary tenant membership and require expiry, reason, approver/actor, and audit
- never trust caller-supplied user ids, scope ids, route names, or hidden UI controls as authorization

## Completion standard

Do not call User Admin implemented unless the stated scope works through the running Akka/API/UI path. A complete foundation slice includes Tenant create/maintain where SaaS Owner administration is in scope, Tenant Admin bootstrap and lifecycle, scoped list/search, invite/resend/revoke/acceptance visibility, role/membership lifecycle, disabled-user denial, last-admin protection, scoped audit/search, access-review queues, required surfaces, backend authorization, tenant/customer isolation, and tests. If any part is deferred, name the slice narrowly or mark it incomplete.

## Tests and validation

Cover at least:

- `/api/me` bootstrap for active, invited, disabled, unknown, and multi-scope users
- SaaS Owner Tenant creation, Tenant profile/status maintenance, and TenantDirectoryView list/search
- SaaS Owner bootstrap invitation for initial Tenant Admin
- Tenant Admin management of admins/users only inside the selected Tenant context
- cross-tenant Tenant Admin attempts, Tenant Admin sibling-tenant creation, and Tenant Admin SaaS Owner role assignment denials
- tenant/customer isolation for every list/query/action
- role/capability escalation denials
- disabled-user denial after valid JWT
- invite, resend, revoke, accept, duplicate, expiry, and delivery failure paths via invitation/email skills
- membership/role idempotency and last-admin protection
- support-access grant/revoke/expiry and audit
- TenantDirectoryView, TenantAdminView, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView projections
- browser-safe DTO redaction and frontend secret boundaries

Use the smallest checks that prove the touched slice: focused unit tests, endpoint integration tests, view tests, frontend contract tests, plus `git diff --check`.
