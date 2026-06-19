# Conversation Capture: SaaS Owner Organization Admin

## User intent

The user requested adding capability for SaaS owner admins to manage tenants/organizations, then agreed that the six-phase plan should be captured as one mini-project rather than one mini-project per phase.

## Reviewed existing requirements

- Product language convention: customer-facing surfaces use **Organization**; internal isolation/code uses **Tenant**.
- Default foundation maps one Organization to one Tenant boundary.
- `FoundationRole.SAAS_OWNER_ADMIN` already exists in backend role code with capabilities for tenant read/manage, user manage, audit read, and billing-boundary manage.
- Existing identity state includes `Tenant` and `Customer`, plus repository methods for save/find.
- User Admin current intent already states that app-owner/SaaS Owner support roles do not automatically gain tenant/customer user or app-data access.
- Mandatory foundation rules apply: backend authorization, tenant/customer isolation, audit/work traces, idempotency, provider secret boundary, and safe denial behavior.

## Agreed implementation shape

Use one mini-project, with phased bounded tasks inside a single queue:

1. update app-description/spec/frontend API contracts;
2. add backend repository/domain support for listing and lifecycle-safe tenant/organization state;
3. add SaaS Owner Organization Admin service;
4. add protected Admin API endpoints;
5. add frontend API client and UI surface;
6. run terminal verification and append follow-up tasks if gaps remain.

## Key design decisions

- The feature is a foundation app extension, not skills-pack maintenance.
- SaaS Owner Admin manages customer-facing Organizations while code enforces internal Tenant boundaries.
- Organization administration must not grant tenant application-data access or support access by implication.
- Billing metadata/billing-boundary behavior remains non-authoritative for app-data access; full billing implementation is out of scope.
- All mutations require idempotency keys and produce audit evidence.
- Denials must be safe and must not reveal hidden organization/user/application-data facts.
- The first UI should attach to User Admin/SaaS Owner surfaces unless later evidence justifies a separate platform-owner workstream.

## Initial recommended endpoint contract

- `GET /api/admin/organizations`
- `GET /api/admin/organizations/{organizationId}`
- `POST /api/admin/organizations`
- `POST /api/admin/organizations/{organizationId}/rename`
- `POST /api/admin/organizations/{organizationId}/suspend`
- `POST /api/admin/organizations/{organizationId}/reactivate`

Browser DTOs should use `organizationId`, `organizationName`, and `status`; backend internals may map to `tenantId`, `displayName`, and active/status fields.

## Risks and constraints

- Existing `Tenant` state is minimal. The backend task must decide whether to extend it or keep lifecycle metadata in DTO/audit for the first slice.
- Avoid client-side authorization. Frontend visibility is only a convenience; API/service authorization is authoritative.
- Avoid unrelated `skills-pack/**` modifications or commits.
