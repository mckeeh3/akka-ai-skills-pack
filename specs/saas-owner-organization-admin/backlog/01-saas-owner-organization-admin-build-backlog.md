# SaaS Owner Organization Admin Build Backlog

## Capability summary

Add `saas_owner.organization_admin` semantics backed by existing `saas_owner.tenant.read` and `saas_owner.tenant.manage` capabilities. Customer-facing UI and DTOs say Organization; backend isolation remains Tenant.

## Capability inventory

| Capability id | Type | Responsibility | Surface/API exposure |
| --- | --- | --- | --- |
| `saas_owner.organization.list` | protected read | List/search platform-safe Organizations visible to SaaS Owner Admin. | Organization list; `GET /api/admin/organizations`. |
| `saas_owner.organization.read` | protected read | Read one Organization detail without tenant app data. | Organization detail; `GET /api/admin/organizations/{organizationId}`. |
| `saas_owner.organization.create` | command | Create active Organization/Tenant with idempotency and audit. | Create form; `POST /api/admin/organizations`. |
| `saas_owner.organization.rename` | command | Rename Organization display label with validation, no-op, idempotency, audit. | Detail action; `POST /api/admin/organizations/{id}/rename`. |
| `saas_owner.organization.suspend` | command | Suspend Organization/Tenant boundary without leaking app data; audit and safe warnings. | Detail action; `POST /api/admin/organizations/{id}/suspend`. |
| `saas_owner.organization.reactivate` | command | Reactivate suspended Organization/Tenant with idempotency and audit. | Detail action; `POST /api/admin/organizations/{id}/reactivate`. |

The implementation may map these to existing backend capability strings `saas_owner.tenant.read` and `saas_owner.tenant.manage` to avoid premature role-model churn, but app-description and API contracts should keep Organization language.

## Backend design notes

- Extend `IdentityRepository` and durable/local repository adapters with tenant listing.
- Add service under `src/main/java/ai/first/application/coreapp/useradmin/` or a small `foundation/identity` application service if reuse demands it.
- Service must require selected context `ScopeType.SAAS_OWNER` and appropriate SaaS Owner capability.
- Mutations require idempotency key and reason where consequential.
- Audit events should use action names such as `ORGANIZATION_CREATE`, `ORGANIZATION_RENAME`, `ORGANIZATION_SUSPEND`, `ORGANIZATION_REACTIVATE`, and denial/no-op equivalents.
- Do not expose tenant application data, customer records, provider ids/secrets, or billing state as authority.

## API design notes

Proposed routes under protected admin endpoint:

- `GET /api/admin/organizations?query=&status=&pageSize=&pageToken=`
- `GET /api/admin/organizations/{organizationId}`
- `POST /api/admin/organizations`
- `POST /api/admin/organizations/{organizationId}/rename`
- `POST /api/admin/organizations/{organizationId}/suspend`
- `POST /api/admin/organizations/{organizationId}/reactivate`

DTOs should use browser-safe Organization names:

```text
OrganizationSummary { organizationId, organizationName, status, visibleTenantAdminCount?, traceIds, updatedAt? }
OrganizationDetail { organization, safeBoundaryNotice, visibleActions, recentAuditEvents, traceIds, correlationId }
```

## Frontend design notes

- Prefer adding an Organization Admin section/surface under User Admin for the first slice.
- Show SaaS Owner Admin scope and a boundary notice: organization management does not grant app-data access.
- Actions should round-trip through API/workstream capability paths and show server result states.
- Hide actions only as UX convenience; direct calls must still be backend-authorized.

## Test requirements

- SaaS Owner can list/read/create/rename/suspend/reactivate Organizations.
- Tenant Admin and Customer Admin are forbidden safely.
- Missing SaaS Owner capability is forbidden safely.
- Duplicate create/replay and no-op rename/status transitions are idempotent/no-op and audited.
- Suspended/reactivated Organization state is visible in safe DTOs.
- API does not expose tenant app data, raw provider details, hidden counts, or secrets.
- Frontend renders authorized and forbidden states with Organization terminology.

## Suggested task breakdown

1. Contract/app-description update.
2. Backend repository and service implementation.
3. Protected Admin API endpoint implementation.
4. Frontend/API client and UI surface implementation.
5. Terminal verification and queue repair.
