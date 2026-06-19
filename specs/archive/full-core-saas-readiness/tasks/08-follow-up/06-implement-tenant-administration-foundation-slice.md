# TASK-FCSR-08-006: Implement tenant administration foundation slice

## Objective

Implement the first runnable Tenant Administration foundation slice so SaaS Owner Admins can create and maintain Tenant organizations and bootstrap Tenant Admins, while Tenant Admins remain constrained to their own Tenant context.

This task exists because the skills-pack tenant-management guidance is now explicit, but the runnable core app still needs an implementation-sized contract before code changes. Complete only this backend/API/workstream slice; do not claim full tenant-management UI completion if browser surface work is deferred.

## Required reads

- `AGENTS.md`
- `skills-pack/docs/core-saas-identity-tenancy-admin.md`
- `skills-pack/skills/akka-basic-user-admin/SKILL.md`
- `specs/full-core-saas-readiness/full-core-readiness-gap-contract.md`
- `specs/full-core-saas-readiness/pending-tasks.md`
- `specs/full-core-saas-readiness/tasks/08-follow-up/06-implement-tenant-administration-foundation-slice.md`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
- `app-description/domains/core-starter/workstreams/user-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/user-admin/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/user-admin/tests/coverage.md`
- `src/main/java/ai/first/domain/foundation/identity/**`
- `src/main/java/ai/first/application/foundation/identity/**`
- `src/main/java/ai/first/application/foundation/invitation/**`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- relevant User Admin, identity, invitation, and endpoint tests under `src/test/java/ai/first/**`

## Skills

- `core-saas-foundation`
- `akka-basic-user-admin`
- `akka-saas-invitation-onboarding`
- `akka-http-endpoints`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-component-client`
- `akka-view-query-patterns` if adding a view/read-model class
- `akka-http-endpoint-testing`

## Implementation scope

### Capability and app-description alignment

Update only the affected User Admin/current-intent artifacts needed to make Tenant Administration authoritative:

- Add or refine governed-tool/capability entries for:
  - `tenant:create`
  - `tenant:view`
  - `tenant:list`
  - `tenant:update_profile`
  - `tenant:update_status`
  - `tenant_admin:invite`
  - `tenant_admin:list`
  - `tenant_admin:replace_roles`
  - `tenant_admin:suspend_membership`
  - `tenant_admin:reactivate_membership`
  - `tenant_admin:remove_membership`
  - `tenant_admin:last_admin_protection_check`
- Bind these to the User Admin functional-agent/workstream surfaces as backend-authoritative actions.
- Record that frontend surface rendering may be a follow-up if this task only exposes backend/workstream payloads.

### Domain and state contract

Use existing identity-domain records where compatible. Do not create duplicate Tenant or Membership concepts if `ai.first.domain.foundation.identity.Tenant`, `Membership`, `FoundationRole`, and `ScopeType` already satisfy the need.

Required Tenant state fields for this slice:

- `tenantId`
- `displayName`
- optional `legalName` or equivalent billing-safe label if existing model supports it
- `status`: at least `DRAFT`/`ONBOARDING`, `ACTIVE`, `SUSPENDED`, `CLOSED` or an equivalent existing enum/state representation
- created/updated/status-change timestamps or audit metadata if existing repositories support them

Allowed status transitions for this slice:

```text
DRAFT or ONBOARDING -> ACTIVE
ACTIVE -> SUSPENDED
SUSPENDED -> ACTIVE
ACTIVE or SUSPENDED -> CLOSED
CLOSED -> no further mutation except read/audit
```

If the existing model uses simpler active/inactive state, preserve compatibility but document the reduced transition fidelity in the validation artifact.

### Backend service/API behavior

Implement or extend backend services/endpoints so the normal runtime path supports:

- SaaS Owner Admin creates a Tenant with idempotency by client request id or normalized tenant key where available.
- SaaS Owner Admin lists/searches Tenants through platform-safe metadata only.
- SaaS Owner Admin views one Tenant without Tenant application data.
- SaaS Owner Admin updates billing-safe Tenant profile metadata.
- SaaS Owner Admin activates/suspends/closes a Tenant using allowed transitions.
- SaaS Owner Admin invites/bootstrap one or more Tenant Admins through the existing mandatory Invitation lifecycle.
- Tenant Admin lists and maintains Tenant Admin memberships only inside their selected Tenant context.
- Tenant Admin attempts to create sibling Tenants, assign SaaS Owner roles, or manage another Tenant are forbidden and audited.
- Last active Tenant Admin cannot be suspended, removed, disabled, or role-changed away from `TENANT_ADMIN`.

Preferred browser API shape if endpoint work is in scope:

```text
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
```

If the current app exposes User Admin primarily through `WorkstreamEndpoint`/`WorkstreamService`, implement the same capability behavior there first and add direct `/api/tenants` endpoints only if that matches the current architecture.

### Authorization matrix

Enforce this matrix in backend code and tests:

| Capability | SaaS Owner Admin | Tenant Admin | Auditor |
|---|---:|---:|---:|
| create/list/view Tenant platform metadata | yes | own Tenant view only | read-only within granted scope |
| update Tenant billing-safe profile metadata | yes | own Tenant allowed settings only if app-description permits | no |
| activate/suspend/close Tenant | yes | no | no |
| invite/bootstrap initial Tenant Admin | yes | no for sibling/bootstrap; yes only for own Tenant admin invitation after Tenant exists | no |
| list Tenant Admins | yes platform-safe metadata; Tenant data redacted | own Tenant only | read-only within granted scope |
| replace/suspend/reactivate/remove Tenant Admin membership | yes within platform policy; Tenant Admin only within own Tenant | own Tenant only | no |
| assign `SAAS_OWNER_ADMIN` | SaaS Owner Admin only through SaaS Owner-scope user admin path, not Tenant Admin path | no | no |

All protected operations must reject disabled users even with a valid JWT, validate selected `AuthContext`, and scope by local Membership/Role/Capability state rather than frontend state or JWT role claims alone.

### Audit and redaction

Emit or preserve `AdminAuditEvent` facts for:

- Tenant create/update/status change;
- Tenant Admin invitation/resend/revoke/acceptance where invoked;
- Tenant Admin role/membership changes;
- forbidden cross-tenant or privilege-escalation attempts;
- last-admin protection denials.

Never expose raw invitation tokens, WorkOS/Resend secrets, private provider ids, unrelated Tenant data, or Tenant application data in Tenant Administration DTOs.

## Out of scope

- Full browser UX polish for Tenant Administration tables/forms if backend/workstream payloads are the selected first slice.
- SaaS Owner access to Tenant application data.
- Billing provider integration, invoices, subscription workflows, or service entitlement enforcement beyond billing-safe Tenant metadata fields.
- Customer organization administration unless needed to keep existing tests compiling.
- Managed-agent recommendation/risk-scoring changes beyond preserving existing User Admin agent boundaries.
- Replacing WorkOS/AuthKit or Resend foundations.

## Expected outputs

- Updated app-description User Admin capability/surface/tool/test files for Tenant Administration.
- Backend domain/service/endpoint/workstream changes for Tenant create/list/view/update/status and Tenant Admin bootstrap/maintenance at the selected slice.
- Focused backend tests for authorization, tenant isolation, idempotency, invitation integration, last-admin protection, audit, and redaction.
- A validation artifact, for example `specs/full-core-saas-readiness/tenant-administration-foundation-validation.md`, recording implemented scope, checks, and deferred UI/billing work.
- Updated `specs/full-core-saas-readiness/pending-tasks.md` with status and notes.

## Required checks

Run the smallest checks that prove this slice, normally:

```bash
git diff --check
mvn test -Dtest=<focused identity/user-admin/tenant-admin tests>
```

Also run when relevant:

```bash
mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

Run full `mvn test` if shared identity repository, invitation lifecycle, or authorization code changes broadly.

## Done criteria

- SaaS Owner Admin can create/list/view/update/status-change Tenants through the implemented backend/API/workstream path.
- SaaS Owner Admin can bootstrap Tenant Admins through the existing Invitation lifecycle without raw token/secret exposure.
- Tenant Admin can manage Tenant Admin memberships only inside the selected Tenant context.
- Cross-tenant Tenant Admin attempts, sibling Tenant creation attempts, and SaaS Owner role assignment attempts are forbidden and audited.
- Last active Tenant Admin protection is enforced.
- TenantDirectory/TenantAdmin read behavior is scoped and browser-safe, whether implemented as explicit Views, repository queries, or workstream DTO projections in this slice.
- Validation artifact records any deferred frontend table/form polish, direct endpoint work, billing, or customer-admin follow-up.
- Queue status is updated and the task changes are committed with the commit message below.

## Commit message

`full-core-ready: implement tenant admin foundation`
