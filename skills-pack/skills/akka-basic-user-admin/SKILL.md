---
name: akka-basic-user-admin
description: Implement complete foundation user and tenant administration for WorkOS-authenticated Akka web apps, including /api/me, invites, user directory/search, memberships, roles, access state, support access, admin audit, and backend authorization checks.
---

# Akka Basic User Administration

Use this skill when an Akka web app needs local user/account administration after browser authentication is handled by WorkOS/AuthKit, the supported user auth service for this skills pack.

## Required reading

Read these first if present:
- `../docs/security-pattern-selection.md`
- `../docs/security-workos-auth-and-admin.md`
- `../docs/security-review-checklist.md`
- `../akka-workos-user-auth/SKILL.md`
- `../akka-saas-invitation-onboarding/SKILL.md` for complete InvitationWorkflow, Resend email delivery/outbox, expiry/reminder, InvitationView, and invite lifecycle tests
- `../akka-resend-email-service/SKILL.md` for the shared Resend email service, local captured outbox, future feature emails, and agent email `@FunctionTool` exposure
- `../akka-http-endpoint-jwt/SKILL.md`
- `../akka-key-value-entities/SKILL.md` or `../akka-event-sourced-entities/SKILL.md` when implementing user/account state
- existing user/account/tenant entities, views, endpoints, and tests

## Use this skill when

- the app needs canonical foundation roles (`SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, `AUDITOR`) plus optional app-specific roles mapped to capabilities
- startup should bootstrap initial admin users from environment variables
- admins should invite users, resend/revoke invitations, view invitation status, list/search users, view user detail, assign/replace/remove roles, and manage memberships within their authority boundary
- complete email-invite onboarding must be implemented through `akka-saas-invitation-onboarding` rather than as a single invite endpoint
- `/api/me` should drive role-aware frontend navigation
- admin APIs must enforce role and tenant/customer scope on the backend
- users can be disabled, reactivated, linked to WorkOS identity, reset/relinked under policy, or scoped to tenants/customers
- generated SaaS apps need UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView in the first admin slice so admins do not need caller-supplied user IDs to find users or access problems

## Boundary with auth, invitation, email, and UI skills

Use this as the local authorization and admin-management foundation after WorkOS authentication is established. Keep adjacent concerns routed as follows:

| Concern | Primary skill |
|---|---|
| WorkOS/AuthKit sign-in, JWT bearer transport, and `/api/me` identity-claim handling | `akka-workos-user-auth` |
| Invitation entity/workflow, acceptance context, expiry, resend/revoke, delivery status, and lifecycle tests | `akka-saas-invitation-onboarding` |
| Resend production email delivery, captured local/dev/test outbox, reusable email capabilities, and agent email tools | `akka-resend-email-service` |
| User Admin functional-agent shell surfaces for Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, and Tenant/Customer Settings | `akka-web-ui-apps` plus focused web UI companions |
| Protected JSON endpoint route mechanics and JWT/request-context extraction | `akka-http-endpoints`, `akka-http-endpoint-jwt`, and `akka-http-endpoint-request-context` |

This skill must preserve backend-authoritative authorization: `/api/me` and frontend navigation expose browser-safe capability hints only; every command, query, stream, workflow action, consumer side effect, timer action, and agent tool still revalidates local Account/Membership/Role/Capability state and selected `AuthContext`.

When a sprint, task, or user request says `User Admin`, `user administration`, `membership management`, or `invitation onboarding` is implemented, the stated scope must work through the locally running Akka app rather than only as isolated entities or endpoints. For full-core generated SaaS, that means admin list/search, invite/resend/revoke/acceptance visibility, role/membership lifecycle, disabled-user denial, last-admin protection, scoped audit/search, required User Admin surfaces, backend authorization, tenant/customer isolation, and tests are present. If any required part is deferred, the sprint/task must be labeled as a narrower internal slice or marked blocked/incomplete; do not call User Admin fully implemented.

## Core model

Authentication proves who the caller is. Local Akka admin state decides what they may do.

Required SaaS foundation concepts:
- Account id linked to the WorkOS subject
- UserProfile for email/display attributes and UserSettings for preferences
- status: `INVITED`, `ACTIVE`, `DISABLED`
- Tenant and Customer scoped Membership records
- roles/capabilities using canonical foundation roles: `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, and `AUDITOR` or scoped auditor capability
- scopes: SaaS Owner, tenant ids, customer ids, support-access windows, or self-only
- selected AuthContext for each protected operation
- Invitation lifecycle with invite token or acceptance context, status, expiry, resend, revoke/cancel, acceptance, delivery status, delivery attempts, idempotency key, and audit trail
- AdminAuditEvent metadata for identity, invitation delivery/resend/revoke/expiry/acceptance, membership, role, support-access, billing-boundary, approval, and data-access changes
- UserDirectoryView for scoped user list/search and user detail entry points, filtering by tenant, customer, email/name, account status, role, membership status, identity link state, and last activity
- MembershipView for tenant/customer membership lifecycle lists, role summaries, membership status filters, account filters, support-access expiry, and last-admin checks
- InvitationView for invitation status, delivery status, delivery attempts, resend/revoke actions, expiry, target email, inviter, and delivery failure repair
- AdminAuditView for scoped admin audit/search by actor, target user, tenant, customer, role, membership status, invitation status, action type, policy, decision card, and time range
- AccessReviewQueueView for stale invites, dormant/high-risk access, support-access expiry, orphaned customer admin gaps, last-admin risk queues, risk level, due time, and agent-generated recommendations

Use Key Value Entity for simple current user/account state. Use Event Sourced Entity when an audit-grade history of role/status changes is required in the state model.

## API route shape

Typical browser-facing routes:

```text
GET    /api/me
GET    /api/admin/users
GET    /api/admin/users/{userId}
PATCH  /api/admin/users/{userId}/profile
POST   /api/admin/users/invite
POST   /api/admin/invitations/{invitationId}/resend
POST   /api/admin/invitations/{invitationId}/revoke
GET    /api/admin/invitations
PUT    /api/admin/users/{userId}/roles
DELETE /api/admin/users/{userId}/roles/{role}
POST   /api/admin/users/{userId}/memberships
POST   /api/admin/memberships/{membershipId}/suspend
POST   /api/admin/memberships/{membershipId}/reactivate
DELETE /api/admin/memberships/{membershipId}
POST   /api/admin/users/{userId}/disable
POST   /api/admin/users/{userId}/activate
POST   /api/admin/users/{userId}/identity/relink
POST   /api/admin/support-access/grants
POST   /api/admin/support-access/{grantId}/revoke
GET    /api/admin/audit
GET    /api/admin/access-review
GET    /api/tenants
POST   /api/tenants
```

All admin routes should require JWT, request-context extraction, active local membership, tenant/customer scope validation, capability checks, and server-side authorization. List/query endpoints must filter by authorized tenant/customer context, apply redaction before returning DTOs, paginate large result sets, and return forbidden rather than leaking cross-tenant/customer data.

## Authorization rules

Recommended foundation scopes and capabilities:
- `SAAS_OWNER_ADMIN`: manage SaaS Owner users, Tenants, initial Tenant Admin bootstrap, platform-safe metadata, and subscription/billing boundaries; no direct Tenant data access without support-access membership.
- `TENANT_ADMIN`: manage users, memberships, roles, Customer organizations, Customer Admins, support-access grants, and Tenant settings within assigned tenant ids.
- `CUSTOMER_ADMIN`: manage Customer users and memberships within assigned customer ids.
- `AUDITOR` or scoped audit capability: list/search admin audit and access-review records without mutation privileges.
- `TENANT_EMPLOYEE`: use Tenant-owned application functionality according to app-specific permissions.
- `CUSTOMER_USER`: use Customer-facing online services according to app-specific permissions.
- App-specific roles: extend these foundation capabilities with domain permissions; they do not replace canonical foundation roles, scope/status/membership checks, or support-access rules. Do not use `APP_ADMIN` as the preferred generic platform role; use `SAAS_OWNER_ADMIN` and map any legacy/app-specific admin alias explicitly.

Rules:
- check caller status is `ACTIVE`
- reject `DISABLED` users even with a valid JWT
- validate target tenant/customer scope on every admin action
- prevent privilege escalation, such as a Tenant Admin assigning `SAAS_OWNER_ADMIN` or another out-of-scope capability
- require idempotent behavior for invites, resends, membership changes, and repeated role assignments where possible
- enforce last-admin protection before removing an admin role, suspending/removing a membership, disabling an account, or revoking support access that would leave a Tenant or Customer without an active admin
- return `401` for missing/invalid authentication and `403` for authenticated forbidden actions unless resource-hiding policy says otherwise
- write AdminAuditEvent records for invites, resend invite, revoke invite, role/membership changes, disables/reactivations, identity reset/relink, support-access grants/revocations/expiry, forbidden attempts, access reviews, and consequential admin data access

## Startup admin bootstrap

Read backend-only environment variables such as:

```bash
ADMIN_USERS="jane@gmail.com:SAAS_OWNER_ADMIN:OWNER,joe@outlook.com:TENANT_ADMIN:tenant-123"
WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
WORKOS_API_BASE_URL="https://api.workos.com" # optional override for tests/proxies
WORKOS_JWT_ISSUER="configured-workos-issuer" # when env-backed JWT config is used
WORKOS_JWT_AUDIENCE="configured-workos-audience" # when env-backed JWT config is used
APP_PUBLIC_BASE_URL="http://localhost:9000"
```

Invite-email delivery settings are backend-only and mandatory for production readiness. Use Resend (resend.com), the supported production email service for this pack:

```bash
RESEND_API_KEY="re_xxxxxxxxx"
INVITE_EMAIL_FROM="Acme <onboarding@example.com>"
INVITE_EMAIL_SUBJECT="Account access information"
```

Local/dev/test may use an explicit safe delivery adapter that captures invite emails in an outbox without external delivery.

Bootstrap rules:
- implement admin bootstrap in the single Akka service startup hook: one class annotated `@Setup` that implements `akka.javasdk.ServiceSetup`, with idempotent `onStartup()` logic that loads `ADMIN_USERS` and seeds initial admin state before normal traffic depends on it
- do not rely on endpoint lazy initialization, frontend calls, static-only registries, or tests calling the seeder directly as the production bootstrap mechanism; `@Setup` is the required startup path, and repeated service-instance startup/rolling restarts must be safe
- validate required backend env/config early for real AuthKit/admin testing: `ADMIN_USERS`, `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, and `APP_PUBLIC_BASE_URL`; production invitation delivery also requires `RESEND_API_KEY` plus `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL`
- every Java env/config helper must trim values, treat missing/empty/blank as unset, log an `error` for each missing required variable with the full env var name (for example `Required backend environment variable [ADMIN_USERS] is not set or is blank`), never log secret values, and fail startup/readiness instead of silently granting access or disabling auth/email delivery
- parse entries defensively and accept canonical foundation roles first (`SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, `AUDITOR`), with app-specific role aliases only when explicitly mapped to capabilities
- normalize email addresses
- create missing invited/admin users idempotently from `ADMIN_USERS` inside `onStartup()`; never require the first `/api/me` or admin endpoint call to trigger this seed
- create Invitation records with expiry, delivery status, delivery attempts, and audit metadata
- send invite emails in production or capture them in the local/dev/test outbox adapter
- block production readiness when required Resend configuration is missing
- update only allowed bootstrap-managed fields on repeated startup
- never expose bootstrap secrets to frontend assets
- surface invalid bootstrap or Resend email service config clearly from `@Setup` startup or in operational status

Minimal startup hook shape:

```java
import akka.javasdk.ServiceSetup;
import akka.javasdk.annotations.Setup;

@Setup
public final class ServiceBootstrap implements ServiceSetup {
  private final AdminBootstrapService adminBootstrapService;

  public ServiceBootstrap(AdminBootstrapService adminBootstrapService) {
    this.adminBootstrapService = adminBootstrapService;
  }

  @Override
  public void onStartup() {
    adminBootstrapService.seedAdminUsersFromEnvironment(); // idempotent ADMIN_USERS load
  }
}
```

Akka permits only one `@Setup` `ServiceSetup` class per service. If the app also needs dependency-provider setup or other startup operations, compose them in the same setup class rather than adding a second one.

## Complete admin management baseline

Generated SaaS apps are not generation-ready if admin management only supports invite/disable/activate or requires admins to already know user IDs. The foundation must include:

- list/search users through `UserDirectoryView`, with filters for email/name, tenant/customer scope, account status, role, membership status, and last activity where available;
- view user detail with browser-safe account/profile, memberships, invitation history, role/capability summary, identity link status, support-access grants, and audit links;
- edit only allowed profile fields; profile changes never grant authorization;
- assign, replace, and remove roles within caller authority, with escalation prevention and last-admin protection;
- add, suspend, reactivate, and remove memberships across SaaS Owner, Tenant, and Customer scopes according to caller capabilities;
- disable and reactivate accounts while preserving audit history and rejecting disabled users on every protected route;
- reset/relink external identity subject only under explicit policy, with evidence, audit events, and safe handling for compromised or migrated identities;
- grant, revoke, and expire Tenant-created support-access memberships with reason, duration, visibility to Tenant Admins, and audit;
- maintain first-slice `MembershipView`, `InvitationView`, `AdminAuditView`, and `AccessReviewQueueView` so admins can find stale invites, delivery failures, dormant access, risky role combinations, support-access nearing expiry, agent-generated admin recommendations, and last-admin risks.

## Required admin read-model query paths

Generated SaaS foundations must expose backend-authorized admin query endpoints over the first-slice read models:

- `UserDirectoryView`: search by authorized tenant/customer scope, email/name, account status, role, membership status, identity link state, and last activity; return browser-safe user summaries and user-detail entry links only inside caller scope.
- `MembershipView`: filter by tenant, customer, account, role, membership status, support-access expiry, and last-admin risk; support access-review and role/membership lifecycle surfaces.
- `InvitationView`: filter by target email, tenant/customer scope, invitation status, delivery status, expiry/due time, inviter, delivery attempts, and resend/revoke eligibility.
- `AdminAuditView`: filter by actor, target user, tenant, customer, role, membership status, invitation status, action type, policy/decision-card link, risk when present, and time range.
- `AccessReviewQueueView`: filter by tenant/customer scope, risk, due/expiry time, review status, item type, target user, role, membership status, invitation status, delivery status, and agent recommendation source.

Backend authorization and redaction rules:
- constrain every query with the caller's AuthContext before invoking the view or before returning rows;
- reject or resource-hide cross-scope access consistently with the app security policy;
- redact WorkOS subjects, raw invitation tokens, provider message ids, support details, policy evidence, and tenant/customer data outside caller authority;
- do not use frontend filters as the security boundary;
- include pagination and stable supported Akka View query shapes for large admin/audit result sets.

Required User Admin functional-agent surfaces: Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, and Tenant/Customer Settings. Each surface must be capability-gated in the UI, rendered inside or deep-linked into the workstream shell, and mechanically authorized on the backend.

## Invite and first-login flow

For generated SaaS apps, load `akka-saas-invitation-onboarding` for the full implementation contract: Invitation entity or audit-grade record, `InvitationWorkflow`, Resend email delivery/outbox Consumer, expiry/reminder TimedAction, `InvitationView`, admin endpoints/UI, resend/revoke/expiry/acceptance semantics, duplicate handling, and lifecycle tests. Load `akka-resend-email-service` for the shared Resend service, other app email features, or agent `@FunctionTool` email tools.


1. Admin invites a user with email, roles, and scopes.
2. Backend creates an `INVITED` local user or membership plus an Invitation record.
3. Invitation stores invite token or acceptance context, status, expiry, delivery status, delivery attempts, idempotency key, and audit trail.
4. Backend sends invite email in production or captures it in the explicit local/dev/test outbox adapter.
5. Failed delivery is visible to authorized admins and creates an audit event.
6. Admin may resend idempotently or revoke/cancel before acceptance; expiry prevents later acceptance.
7. User signs in through WorkOS.
8. `/api/me` links WorkOS subject to invited local account only through a valid invitation or accepted membership policy.
9. Backend accepts the invitation, activates the account/membership, and returns current profile/roles/scopes.

Do not let an uninvited WorkOS identity become an admin unless self-registration and role policy explicitly allow it. Never silently self-register privileged users from WorkOS claims alone.

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
- `/api/me` returns active linked account, profile/settings, memberships, selected AuthContext, roles/scopes, tenant/customer ids, and browser-safe capabilities
- missing JWT rejected for admin APIs
- disabled user rejected despite valid JWT
- non-admin cannot list users
- tenant admin cannot manage another tenant or customer outside scope
- customer admin cannot manage tenant-wide users
- tenant/customer-scoped list queries cannot leak cross-scope rows and preserve cross-scope filtering guarantees
- tenant admin cannot grant `SAAS_OWNER_ADMIN` or other out-of-scope capabilities
- startup/readiness validation logs missing required backend env vars with exact env var names and no secret values
- production readiness blocks missing required Resend configuration
- local/dev/test invite adapter captures outbound messages in an outbox without external delivery
- invite send, resend, revoke/cancel, expiry, acceptance, replayed acceptance, duplicate email handling, delivery failure, and delivery-attempt audit behavior
- user directory list/search filters by authorized scope and does not require caller-supplied user IDs
- user detail and admin/audit/search rows return only browser-safe admin DTOs and preserve scope redaction
- role assignment, replacement, and remove roles enforce scope/capability limits and last-admin protection
- add, suspend membership, reactivate membership, and remove membership flows enforce tenant/customer boundaries
- account disable/reactivate rejects disabled users and preserves audit history
- reset identity/relink identity subject requires policy, emits audit facts, and rejects privilege escalation
- support-access grant/revoke/expiry is scoped, visible, audited, and time-limited
- repeated invite/role assignment/membership lifecycle action is idempotent or returns documented conflict
- paginated admin read-model queries preserve stable results and do not rely on unsupported optional-filter `OR` patterns
- AccessReviewQueueView flags stale invites, dormant admins, support-access expiry, risky memberships, last-admin risks, and due/expiry-time correctness
- audit events are emitted for admin changes, invitation lifecycle events, delivery failures, access reviews, agent-generated recommendations, and forbidden attempts; audit trace completeness is tested
- frontend receives enough `/api/me` data for navigation without leaking internals

## Anti-patterns

Avoid:
- making JWT role claims the only source of mutable app authorization when roles are app-managed
- trusting frontend route guards as authorization
- allowing first login to auto-create privileged users accidentally
- treating invite email delivery as omitted setup instead of mandatory production readiness behavior
- storing backend API keys in frontend env files
- returning full internal user entities to the browser
- omitting scope checks from list/query endpoints
- limiting initial admin management to invite/disable/activate without list/search, membership lifecycle, role removal, access review, support-access lifecycle, and admin audit/search
