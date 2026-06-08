# Auth and User Admin Reference Gap Inventory

## Purpose

Inventory the gap between the current repository reference assets and the full-core User Admin/onboarding target from `full-core-realization-map.md` and `docs/core-saas-identity-tenancy-admin.md`.

This is an inventory-only task. It does not rewrite production/reference code.

## Current reference baseline

The current Java security reference under `src/main/java/com/example/**/security` is a useful **DCA seed / Module 1-ish** authentication and admin sketch, not a full-core SaaS User Admin reference.

Implemented or partially represented today:

- WorkOS/JWT-protected endpoints using `@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)`:
  - `src/main/java/com/example/api/security/MeEndpoint.java`
  - `src/main/java/com/example/api/security/AdminUsersEndpoint.java`
  - `src/main/java/com/example/api/security/TenantAdminEndpoint.java`
  - `src/main/java/com/example/api/security/CustomerAdminEndpoint.java`
- Akka-owned local authorization state:
  - `LocalAccountEntity` for local account current state.
  - `TenantDirectoryEntity` for tenant/customer current state.
  - `AdminAuditEntryEntity` for immutable single-record audit facts.
- Basic local auth helper:
  - `src/main/java/com/example/security/AuthorizationService.java`
  - `src/main/java/com/example/security/WorkosClaimExtractor.java`
- Backend-only environment validation for WorkOS/Resend/admin bootstrap:
  - `src/main/java/com/example/security/RequiredEnvironment.java`
- Direct Resend send helper used by `AdminUsersEndpoint`:
  - `src/main/java/com/example/security/InvitationEmailSender.java`
- Frontend workstream fixture coverage for a User Admin functional agent and structured surfaces:
  - `frontend/src/workstream/fixtures/agents.ts`
  - `frontend/src/workstream/fixtures/surfaces.ts`
  - `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- Integration tests demonstrate bootstrap, `/api/me`, invite-as-account-create, scope denial, disabled-user denial, audit entries, frontend secret boundary, and static shell hosting.

## Gap severity legend

- **P0**: blocks full-core readiness and should be specified or implemented before claiming full-core support.
- **P1**: required for a complete User Admin reference slice but can follow P0 architecture/specification work.
- **P2**: hardening/detail work after core behavior exists.

## Ordered gaps

### 1. P0 — Reference is app-specific DCA roles, not canonical foundation roles

Evidence:
- `SecurityRole` uses `APP_ADMIN`, `DEALER_OWNER`, `OPERATIONS_SUPERVISOR`, `POLICY_OWNER`, `AUDITOR`, `CUSTOMER_ADMIN`, `USER`.
- `RoleAssignment` documents `APP_ADMIN` as a DCA app-specific alias, but runtime authorization still uses `isAppAdmin()` as the platform admin gate.

Full-core target:
- Canonical roles must start with `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, and `AUDITOR`.
- App-specific roles must map to capabilities inside SaaS Owner/Tenant/Customer scopes without replacing foundation role/status/scope checks.

Follow-up ordering:
1. Specify the canonical role/capability model in `user-admin-reference-slice.md`.
2. Later code tasks should add foundation roles/capabilities and retain DCA roles only as explicit reference aliases where needed.

### 2. P0 — `/api/me` lacks full AuthContext, membership, profile, settings, and context-selection contract

Evidence:
- `MeEndpoint.MeResponse` returns `userId`, email/displayName/status, role names, simple scopes, and capability strings.
- There is no selected `AuthContext` object, membership ids/statuses, available contexts, `UserSettings`, light/dark `uiMode`, customer/tenant names, support access state, or functional-agent availability payload in the Java endpoint.
- First-login linking happens implicitly in `AuthorizationService.requireAuthenticated` when a local invited account exists by subject/email; it is not tied to a durable Invitation acceptance context.

Full-core target:
- `/api/me` must return browser-safe account, profile, settings, active memberships, selected/default AuthContext, available contexts, roles/capabilities, tenant/customer ids, and context-switch options.
- Disabled/no-access/context-invalid states must be explicit and safe for the frontend shell.
- First login must require a valid invitation, invite token/acceptance context, or explicit membership policy.

Follow-up ordering:
1. Specify `/api/me`, profile/settings, and context-selection DTOs in `user-admin-reference-slice.md`.
2. Implement endpoint and tests after Invitation and Membership contracts are settled.

### 3. P0 — Complete Invitation lifecycle is missing

Evidence:
- `AdminUsersEndpoint.invite` directly calls `LocalAccountEntity.invite` and `InvitationEmailSender.sendInvitation`.
- There is no `Invitation` entity/record, `InvitationWorkflow`, acceptance context/token hash, invitation status, expiry, resend, revoke, acceptance, delivery-attempt lifecycle, or duplicate-invite policy beyond `LocalAccount` idempotency.
- No `InvitationView` exists.

Full-core target:
- Invitation lifecycle must include create, delivery, resend, revoke/cancel, expire, accept/first-login link, duplicate handling, idempotency keys, delivery status, delivery attempts, audit facts, and scoped admin views.
- Raw invitation tokens must not appear in views/logs; first-login activation must validate invitation policy.

Follow-up ordering:
1. Next task should specify `invitation-onboarding-reference-slice.md` with entity/workflow/consumer/timer/view/API/test contracts.
2. Later implementation tasks should add the Akka reference components.

### 4. P0 — Resend/outbox email foundation is incomplete

Evidence:
- `InvitationEmailSender` performs synchronous direct HTTP calls to Resend from the admin endpoint path.
- Missing config returns a failed delivery result during invite handling, while `RequiredEnvironment` blocks only non-test startup.
- There is no reusable email outbox model, local/dev/test captured outbox adapter, delivery consumer, delivery attempt state, provider message id storage, or reusable email service for future app emails/agent tools.

Full-core target:
- Production delivery uses Resend through the supported reusable email service.
- Local/dev/test uses explicit captured outbox behavior.
- Delivery work should be durable/idempotent and visible to authorized admins, with failure audit and repair/resend flows.

Follow-up ordering:
1. Include email outbox/consumer and captured outbox in the invitation slice spec.
2. Later code task should replace direct endpoint sending with durable outbox delivery.

### 5. P0 — Membership lifecycle is collapsed into account roles

Evidence:
- `LocalAccount.State` stores `List<RoleAssignment> roles` directly on the account.
- There is no first-class `Membership` model with `membershipId`, `scopeType`, status (`INVITED`, `ACTIVE`, `SUSPENDED`, `REMOVED`), support-access expiry, audit metadata, or lifecycle commands.
- No add/suspend/reactivate/remove membership endpoints exist.

Full-core target:
- Memberships are scoped authorization grants separate from profile/settings.
- Admins must add, suspend, reactivate, remove memberships; assign/replace/remove roles within scope; enforce last-admin protection; and preserve audit.

Follow-up ordering:
1. Specify Membership current state/history choice and command semantics in `user-admin-reference-slice.md`.
2. Implement membership component, views, endpoints, and tests after invitation spec.

### 6. P0 — Admin discovery views are absent in Java reference

Evidence:
- `AdminUsersEndpoint.listUsers` requires caller-supplied `userIds` query parameter and filters loaded accounts through Akka components.
- There are no Akka Views for `UserDirectoryView`, `MembershipView`, `InvitationView`, `AdminAuditView`, or `AccessReviewQueueView`.
- `AdminAuditEntryEntity` stores individual immutable records but no queryable audit view.

Full-core target:
- Admins must discover users/invitations/memberships/audit/access-review items without knowing internal ids.
- Query endpoints must be scoped by backend AuthContext, paginated, redacted, and backed by supported Akka View query shapes.

Follow-up ordering:
1. Specify view schemas and query paths in invitation and user-admin slice specs.
2. Implement read models before full User Admin UI/API integration.

### 7. P0 — Support access lifecycle is not modeled

Evidence:
- No support-access grant entity/fields/endpoints exist in Java reference.
- Frontend fixtures include `support.access.read` and support-access rows, but there is no backend contract behind them.

Full-core target:
- Tenant-created support access must be time-limited, scoped, visible to Tenant Admins, auditable, revocable, and eligible for access review/expiry reminders.
- SaaS Owner users do not gain Tenant data access without an explicit Tenant-scoped support membership.

Follow-up ordering:
1. Include support-access membership semantics and access-review rows in `user-admin-reference-slice.md`.
2. Implement with membership lifecycle and timed access-review work.

### 8. P0 — Last-admin and risky-admin decision protections are missing

Evidence:
- `AuthorizationService.requireCanManageAccountRoles` checks broad scope but does not enforce last-admin protection.
- `LocalAccountEntity.replaceRoles`, `disable`, and `reactivate` have no knowledge of tenant/customer admin counts.
- Frontend fixture text mentions last-admin denial, but backend has no corresponding policy or tests.

Full-core target:
- Removing/suspending/disabling the last Tenant Admin or Customer Admin must be rejected or routed through an approved replacement/decision policy.
- Risky admin actions should create decision-card-ready facts with evidence, risk, confidence, alternatives, and audit links.

Follow-up ordering:
1. Specify last-admin query/read-model dependency and command rejection semantics in `user-admin-reference-slice.md`.
2. Implement enforcement once MembershipView/UserDirectoryView exists.

### 9. P1 — Tenant/Customer model is current-state only and lacks SaaS Owner boundary semantics

Evidence:
- `TenantDirectoryEntity` manages tenants/customers, but there is no SaaS Owner organization/account context, subscription/billing boundary, tenant bootstrap invitation flow, customer admin bootstrap flow, or tenant-created support access rule.
- `APP_ADMIN` can administer platform and is treated as all-tenant access through `RoleAssignment.isAppAdmin()`.

Full-core target:
- SaaS Owner Admin manages platform-safe metadata, Tenant creation, initial Tenant Admin bootstrap, and billing/subscription boundary.
- SaaS Owner has no direct Tenant application data access without Tenant-created support-access membership.

Follow-up ordering:
1. Capture SaaS Owner/Tenant/Customer admin boundaries in the user-admin slice.
2. Add code after canonical roles and membership scopes are in place.

### 10. P1 — Profile and settings APIs are incomplete

Evidence:
- `UserProfile` contains only `displayName` and `email`.
- There is no `UserSettings` model/entity, no `uiMode`, and no `GET/PATCH /api/me/profile` or `GET/PATCH /api/me/settings` endpoints.
- `AdminUsersEndpoint` exposes only invite, role replacement, disable, and activate; it does not support safe profile field editing.

Full-core target:
- Profile/settings are separate from authorization state.
- Baseline profile fields and `uiMode: LIGHT | DARK` settings are browser-safe and editable according to scope rules.

Follow-up ordering:
1. Specify profile/settings current-state components and DTOs in `user-admin-reference-slice.md`.
2. Implement before UI profile/preferences fixtures are wired to backend contracts.

### 11. P1 — Admin API surface is partial

Evidence:
- Existing admin routes cover list-by-known-ids, get-by-id/email, invite, replace roles, disable, activate, tenant upsert/get/list-by-known-ids, customer upsert/get.
- Missing baseline routes include invitation list/detail/resend/revoke/audit, user detail through directory, profile patch, role remove, membership add/suspend/reactivate/remove, identity relink/reset, support-access grant/revoke, admin audit search, access review, Tenant/Customer settings.

Full-core target:
- Full User Admin APIs must expose scoped capability-backed endpoints for all required surfaces and views.

Follow-up ordering:
1. Specify route contracts in invitation and user-admin slice docs.
2. Implement endpoints only after backing components/views exist.

### 12. P1 — Admin audit model is too narrow for full-core traceability

Evidence:
- `AdminAuditEntry.AdminAuditAction` has a small action set and details map.
- Audit records do not carry policy references, decision-card links, risk/evidence fields, correlation ids, data-access class, support-access fields, delivery attempt ids, or denied-action facts in a structured way.
- No searchable `AdminAuditView` exists.

Full-core target:
- Audit must answer who changed what, in which scope, when, why, under which policy, and with which decision/trace link.
- Denied actions, invite lifecycle, support access, role/membership changes, identity link/relink, access review, and agent-generated recommendations require durable audit facts.

Follow-up ordering:
1. Include audit event shape and view query paths in the user-admin slice.
2. Align later with Audit/Trace core module to avoid two incompatible audit models.

### 13. P1 — User Admin frontend fixtures are ahead of backend contracts

Evidence:
- Workstream fixtures model User Admin dashboard/list/detail, invitation queue, support access, access review, admin audit excerpts, action-to-capability mapping, trace links, and last-admin denial.
- Java backend currently exposes only partial `/api/admin/users`, `/api/tenants`, and `/api/tenants/{tenantId}/customers` endpoints and does not provide workstream surface payloads.
- `frontend/src/screens/admin/AdminUsersPage.tsx` still uses a legacy screen/page form shape with role ids like `tenant-admin`, while Java reference roles are DCA enum names like `APP_ADMIN` and `OPERATIONS_SUPERVISOR`.

Full-core target:
- User Admin UI surfaces should be backed by realistic full-core API contracts and typed DTOs: Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings.
- Backend remains authoritative for all capability actions.

Follow-up ordering:
1. After backend slice specs, align frontend API contracts in the later workstream task.
2. Avoid wiring UI to partial DCA endpoints as if they were full-core contracts.

### 14. P1 — Test matrix is partial and reference-specific

Evidence:
- Existing tests cover important seed behavior: JWT-protected APIs, bootstrap idempotency, `/api/me`, account link/activate, role denial, disabled denial, basic audit, frontend asset secret boundary.
- Missing full-core tests include invitation resend/revoke/expire/acceptance, delivery failure visibility, captured outbox, duplicate invite handling, membership lifecycle, role remove/replace with last-admin protection, support access grant/expiry/revoke, admin views scoped queries, tenant/customer isolation across views, profile/settings, identity relink/reset, and audit completeness for denied actions.

Full-core target:
- The User Admin reference must include unit/integration/security tests across every command/query/view/workflow/timer/consumer and frontend secret boundary.

Follow-up ordering:
1. Include required tests in invitation and user-admin slice specs.
2. Final acceptance matrix should consolidate these into the full-core test checklist.

### 15. P2 — WorkOS JWT configuration and lookup are useful but not full production contract

Evidence:
- Endpoints validate bearer JWT presence, but annotations do not yet include issuer/audience constraints.
- `WorkosUserLookup` may be used when email is absent, but first-login security remains tied to local account existence rather than an invitation acceptance policy.

Full-core target:
- Production guidance should use configured WorkOS issuer/audience validation where known.
- Backend-only WorkOS/Resend secrets must remain outside frontend bundles.

Follow-up ordering:
1. Keep current JWT seam as reference starter.
2. Harden JWT issuer/audience configuration during implementation tasks after the slice contracts are specified.

## Recommended follow-up task order

1. **Specify invitation onboarding reference slice** (`TASK-CORE-02-002`): Invitation entity/record, `InvitationWorkflow`, Resend/outbox consumer, expiry/reminder timer, `InvitationView`, admin invitation APIs, acceptance/linking rules, tests.
2. **Specify full user administration reference slice** (`TASK-CORE-02-003`): canonical roles/capabilities, Account/Profile/Settings/Membership model, `/api/me`, context selection, directory/membership/audit/access-review views, admin APIs, support access, last-admin protection, tests.
3. **Implement invitation foundation reference**: durable invitation lifecycle plus outbox and tests.
4. **Implement membership/user-admin foundation reference**: membership lifecycle, canonical roles, profile/settings, `/api/me`, views, admin APIs, support access, last-admin protection, audit.
5. **Align User Admin workstream API contracts**: connect frontend fixtures/surfaces to realistic backend DTOs and capability-action contracts.
6. **Fold into full-core acceptance/security matrix**: ensure WorkOS, `/api/me`, invitation lifecycle, outbox, membership lifecycle, support access, admin views, User Admin UI, and tests are covered as one checklist.

## Non-gaps / preserve as useful reference material

- The existing code correctly demonstrates the key principle that WorkOS authenticates but Akka-owned local state authorizes.
- The `@JWT` endpoint pattern, request-context claim extraction, disabled-user denial, local account link check, and frontend secret-boundary tests are useful foundations to preserve and generalize.
- The current frontend workstream User Admin fixture is a useful target contract, but it must be reconciled with backend full-core API contracts before being treated as generated-app-ready.
