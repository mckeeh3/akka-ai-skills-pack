---
name: core-saas-foundation
description: Apply the mandatory secure SaaS foundation for every new AI-first SaaS app, PRD, spec, backlog, app-description, decomposition, and generation flow before app-specific domain work.
---

# Core SaaS Foundation

Use this skill for every new project, app, PRD, spec, backlog, app-description bootstrap, solution decomposition, and generation flow handled by this pack unless the user explicitly asks for repository-maintenance-only work or non-SaaS reference material.

This is a mandatory foundation skill. It does not replace `ai-first-saas`, app-description skills, Akka decomposition, or component implementation skills. It supplies the secure SaaS baseline those paths must include before app-specific CRM/domain features are treated as generation-ready.

## Required reading

Read these first when using this skill:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/core-ai-first-saas-foundation.md`
- `../../docs/core-saas-identity-tenancy-admin.md`
- `../../docs/core-saas-owner-tenant-billing.md`
- `../../docs/ai-first-saas-application-architecture.md`

Then load only the focused downstream skills needed for the selected path:
- `ai-first-saas` for operating-model interpretation
- `app-description-auth-security` for description-first security semantics
- `akka-workos-user-auth` for WorkOS/JWT browser authentication
- `akka-basic-user-admin` for local account, membership, role, invite, and admin flows
- `akka-saas-invitation-onboarding` for complete mandatory email-invite onboarding with InvitationWorkflow, outbox/email delivery, timers, InvitationView, and admin lifecycle tests

## Mandatory baseline objects

Every generated SaaS application must model these foundation concepts before app-specific features:

- SaaS Owner — platform operator that manages Tenants, Tenant Admin bootstrap, subscription/billing state, and platform-safe metadata only.
- Tenant — subscribing SaaS user organization that owns tenant application data and serves Customers.
- Customer — organization served by a Tenant; all Customer data remains inside the Tenant boundary.
- Account — local Akka-owned authorization record linked to a WorkOS-authenticated human identity.
- UserProfile — human-facing display/profile attributes; never grants authorization.
- UserSettings — user preferences such as `uiMode`; never overrides authorization, policy, or audit.
- Membership — scoped Account relationship to SaaS Owner, Tenant, or Customer with status and roles.
- Role — named permission bundle within a scope, such as `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, or `CUSTOMER_USER`.
- Permission/Capability — mechanically enforced action grants used by endpoints, component commands, queries, tools, workflows, consumers, timers, and UI capability display.
- Invitation — mandatory auditable email-invite onboarding state for Tenant, Customer, and user activation flows, including invite token or acceptance context, status, expiry, resend, revoke/cancel, acceptance, delivery status, delivery attempts, idempotency key, and audit trail.
- AuthContext — selected signed-in operating context: account, membership, roles/capabilities, tenantId/customerId when applicable, and actor metadata.
- UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView — mandatory scoped admin read models so admins can discover, search, review, and repair access without already knowing target user ids.
- AdminAuditEvent — durable audit record for identity, membership, role, policy, support-access, billing, data access, approval, and consequential AI/tool activity.
- Support-access membership — Tenant-created, time-limited, auditable Tenant-scoped access for SaaS Owner personnel when a Tenant requests help; never a global super-admin bypass.
- Subscription/billing boundary — SaaS Owner to Tenant subscription, plan, billing account, and entitlement state that excludes Tenant application data.

## Mandatory baseline behavior

All broad planning and generation paths must include:

- WorkOS authentication seam for browser human sign-in and JWT-bearing API calls unless the target explicitly selects another provider.
- Akka-owned local authorization state for Accounts, Memberships, Roles, Permissions/Capabilities, Tenant, Customer, support access, and billing-safe platform records.
- `/api/me` returning browser-safe account, profile, settings, active memberships, selected AuthContext, roles/capabilities, and context-switch data; first-login linking must require a valid invitation or accepted membership policy and must not silently self-register privileged users.
- Backend authorization service used by every protected HTTP/gRPC/MCP route, component command, view query, stream, workflow action, agent tool, consumer side effect, and timer action.
- Tenant/customer-scoped commands and queries that include `tenantId` and `customerId` where required and reject cross-scope access mechanically.
- AdminAuditEvent creation for identity changes, invitations, invitation email delivery attempts/failures, resend/revoke/expiry/acceptance, membership/role changes, support access, policy checks, approval outcomes, billing actions, data access, and consequential AI/tool activity.
- Complete admin management within each caller's authority boundary: invite, resend invite, revoke invite, view invitation status, list users, search/filter users, view user detail, edit allowed profile fields, assign/replace/remove roles, add/suspend/reactivate/remove memberships, disable/reactivate account, reset/relink identity subject under policy, grant/revoke/expire support-access, and enforce last-admin protection.
- Scoped admin capabilities for SaaS Owner Admin, Tenant Admin, Customer Admin, Auditor, and app-specific admin roles; app-specific admin roles extend the foundation capability model rather than bypassing it.
- Complete email-invite onboarding is mandatory: production readiness requires configured email delivery or an accepted provider decision; local/dev/test environments may use an explicit safe adapter that captures emails in an outbox without external delivery. Missing production email provider configuration blocks readiness.
- Tenant-isolation tests, forbidden-access tests, disabled-user tests, role/scope-denial tests, admin list/search authorization tests, audit tests, `/api/me` tests, last-admin protection tests, support-access lifecycle tests, and security-review checks.

## First-slice implementation order

For every new SaaS app, implement or specify the secure foundation before app-specific CRM/domain features:

1. Common identity/tenancy types: IDs, scope enums, roles, permissions/capabilities, AuthContext, audit metadata.
2. WorkOS/JWT authentication seam and request-context extraction.
3. Account, UserProfile, and UserSettings state plus base profile/settings APIs.
4. Tenant and Customer organization state with Tenant/Customer boundaries.
5. Membership, Role, Permission/Capability, complete Invitation lifecycle, support-access, and context-selection flows.
6. Email delivery/outbox adapter for invitation send/resend, delivery status, delivery attempts, failed-delivery visibility for admins, and auditable delivery failures.
7. `/api/me` endpoint and browser-safe capability model that links invited accounts only through a valid invitation/acceptance context or explicit membership policy.
8. Central backend authorization service and mandatory checks for routes, commands, queries, streams, tools, workflow actions, consumers, and timers.
9. SaaS Owner to Tenant subscription/billing boundary, plan/subscription/entitlement records, and billing-safe admin APIs where needed.
10. AdminAuditEvent write path plus UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView for scoped list/search, audit search, and access-review queues.
11. Foundation UI shell: sign-in, context selection, profile/settings, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, and capability-gated actions when a browser UI is in scope.
12. Security baseline tests: tenant-isolation, forbidden access, disabled user, role/scope denial, `/api/me`, invite delivery/resend/revoke/expiry/acceptance, user and membership list/search, membership lifecycle, last-admin protection, audit, support-access expiry/revocation, billing-boundary, and frontend secret-boundary tests.
13. Security review before implementing app-specific CRM/domain slices.

Do not let uncertainty about provider-specific details block modeling the mandatory local authorization, tenancy, AuthContext, and audit contracts. If WorkOS setup values are unknown, queue a provider-specific question while preserving the local boundary model.

## Route-specific requirements

### App-description paths

Bootstrap and maintain secure SaaS foundation files in capabilities, behavior, tests, auth/security, observability, and UI layers. Missing Account/Profile/Settings/Membership/Tenant/Customer/admin/audit semantics must make readiness `not-ready` or block generation. Admin semantics are incomplete unless they include list/search without caller-supplied user ids, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView where applicable, membership lifecycle operations, role replacement/removal, support-access lifecycle, last-admin protection, and admin UI surfaces beyond invite/disable/activate.

### Akka solution decomposition

Every solution plan must include a `Core secure SaaS foundation` section before app-specific capabilities. Skill routing must include `core-saas-foundation`, `akka-workos-user-auth`, `akka-basic-user-admin`, `akka-saas-invitation-onboarding`, endpoint JWT/request-context skills, and the entity/workflow/view/timer/consumer/test skills needed to realize the foundation.

### PRD/spec/backlog planning

Every app PRD must create `specs/cross-cutting/01-auth-tenancy-audit.md` and a first foundation sprint or slice unless the task is explicitly non-SaaS reference material. Pending tasks must start with foundation work before app-specific CRM/domain tasks.

### Generation

Generation must stop or mark the description not-ready when the foundation is missing. Do not invent access semantics during code generation; add description/spec gaps instead.

## Output checklist

Before handing off to downstream implementation, verify:
- SaaS Owner, Tenant, Customer, Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, complete Invitation lifecycle, AuthContext, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AdminAuditEvent, support-access, and subscription/billing boundary are present or explicitly deferred only for non-SaaS reference work.
- Invitation email delivery is configured for production readiness, or local/dev/test uses an explicit captured outbox adapter; delivery failures are visible to admins and auditable; focused implementation routes through `akka-saas-invitation-onboarding` for InvitationWorkflow, email delivery/outbox Consumer, expiry/reminder TimedAction, InvitationView, admin endpoints/UI, and lifecycle tests.
- Admin users can discover and manage users within their authority boundary using list/search, view user detail, role assignment/replacement/removal, membership add/suspend/reactivate/remove, account disable/reactivate, reset/relink identity subject under policy, support-access grant/revoke/expiry, and last-admin protection.
- `/api/me` and context selection are specified for browser apps.
- Backend authorization checks are required for every protected route, component command, query, stream, tool, workflow action, consumer side effect, and timer action.
- Tenant/customer-scoped commands and queries enforce isolation mechanically.
- Tenant-isolation and security baseline tests are first-slice work, not polish.
- App-specific domain implementation starts only after the secure SaaS foundation contract exists.
