# Frontend API Contracts

Canonical workstream UI calls protected backend APIs for data/actions; routes are not authorization.

## User Admin compatibility contracts

- `GET /api/admin/users/dashboard` returns `UserAdminDashboardPayload` with scoped dashboard counters, trace refs, and authorized actions.
- `GET /api/admin/users` returns scoped user/invitation rows.
- `GET /api/admin/users/{accountId}` returns `UserAdminUserAccountPayload` for an authorized account detail.
- `POST /api/admin/invitations` creates an invitation request without exposing raw invitation tokens/token hashes.
- `POST /api/admin/invitations/{invitationId}/resend` resends when authorized.
- `POST /api/admin/invitations/{invitationId}/revoke` revokes when authorized.

## SaaS Owner Admin user-management contracts

SaaS Owner Admins must be able to invite and manage other app-owner administrators from a SaaS Owner selected `AuthContext`. These contracts are protected by backend `saas_owner.admin.*` capabilities and never expose tenant/customer application data, raw provider ids, raw invitation tokens, or provider secrets.

- `GET /api/admin/saas-owner-admins?query=&status=&pageSize=&pageToken=` returns browser-safe SaaS Owner Admin memberships and app-owner invitations with redaction, trace refs, and correlation id.
- `POST /api/admin/saas-owner-admins/invitations` invites another SaaS Owner Admin from `{ email, displayName?, roles: ['SAAS_OWNER_ADMIN'], idempotencyKey, reason?, correlationId? }` and returns invitation/detail status or validation/duplicate/outbox-blocked/forbidden system message.
- `POST /api/admin/saas-owner-admins/invitations/{invitationId}/resend` and `/revoke` manage app-owner invitations when authorized.
- `PUT /api/admin/saas-owner-admins/{accountId}/roles` and `POST /api/admin/saas-owner-admins/{accountId}/suspend|reactivate|remove` manage app-owner admin memberships with last-owner-admin and self-action protections.

## SaaS Owner Organization Admin contracts

Browser-facing contracts use **Organization** terminology. Backend implementation maps `organizationId` to internal Tenant ids and enforces SaaS Owner `AuthContext` plus `saas_owner.tenant.read/manage` capabilities server-side.

- `GET /api/admin/organizations?query=&status=&pageSize=&pageToken=` returns `OrganizationListPayload` with browser-safe Organization summaries, filters, trace refs, correlation id, and no hidden counts or tenant/customer app data.
- `GET /api/admin/organizations/{organizationId}` returns `OrganizationDetailPayload` with safe lifecycle metadata, boundary notice, visible actions, recent redacted audit excerpts, trace refs, and no provider secrets, billing-derived authority, support-access internals, or tenant app data.
- `POST /api/admin/organizations` creates an Organization/Tenant boundary from `{ organizationName, idempotencyKey, reason?, correlationId? }` and returns a refreshed `OrganizationDetailPayload` or typed validation/duplicate/forbidden/no-op system message.
- `POST /api/admin/organizations/{organizationId}/rename` renames an Organization from `{ organizationName, idempotencyKey, reason?, correlationId? }` and returns refreshed detail/list state or typed validation/no-op/stale/forbidden system message.
- `POST /api/admin/organizations/{organizationId}/suspend` suspends an Organization from `{ reason, idempotencyKey, correlationId? }` and returns refreshed detail/list state, safe lifecycle warning, or typed no-op/forbidden system message.
- `POST /api/admin/organizations/{organizationId}/reactivate` reactivates an Organization from `{ idempotencyKey, reason?, correlationId? }` and returns refreshed detail/list state or typed no-op/forbidden system message.
- `GET /api/admin/organizations/{organizationId}/admins?query=&status=&pageSize=&pageToken=` lists Organization Admin users and invitations for the selected Organization/Tenant with browser-safe membership/invitation summaries, last-admin risk flags, trace refs, correlation id, and no tenant application data.
- `POST /api/admin/organizations/{organizationId}/admins/invitations` bootstraps/invites an Organization Admin from `{ email, displayName?, roles: ['TENANT_ADMIN'], idempotencyKey, reason?, correlationId? }` after the Organization exists and returns admin invitation/detail status or validation/duplicate/outbox-blocked/forbidden system message.
- `POST /api/admin/organizations/{organizationId}/admins/invitations/{invitationId}/resend` and `/revoke` manage Organization Admin invitations when authorized.
- `PUT /api/admin/organizations/{organizationId}/admins/{accountId}/roles` and `POST /api/admin/organizations/{organizationId}/admins/{accountId}/suspend|reactivate|remove` manage Organization Admin memberships with target-organization validation, `SAAS_OWNER_ADMIN` escalation denial, and last-organization-admin protection.

DTOs:

- `OrganizationSummary`: `{ organizationId, organizationName, status, updatedAt?, safeLifecycleSummary?, visibleTenantAdminCount?, traceRefs[] }`.
- `OrganizationDetailPayload`: `{ organization, safeBoundaryNotice, visibleActions[], recentAuditEvents[], traceRefs[], correlationId, redaction }`.
- `OrganizationListPayload`: `{ organizations[], filters, pageInfo, traceRefs[], correlationId, redaction }`.
- `OrganizationActionResult`: `{ status, organization?, systemMessage?, traceRefs[], correlationId }`.
- `AdminSubjectSummary`: `{ accountId?, invitationId?, displayName?, email?, scopeType, tenantId?, roles[], status, invitationStatus?, deliveryStatus?, lastAdminRisk?, visibleActions[], traceRefs[] }`.
- `SaasOwnerAdminListPayload`: `{ admins[], invitations[], filters, pageInfo, traceRefs[], correlationId, redaction }`.
- `OrganizationAdminListPayload`: `{ organization, admins[], invitations[], filters, pageInfo, safeBoundaryNotice, traceRefs[], correlationId, redaction }`.

## Workstream shell contracts

- `POST /api/workstream/bootstrap` loads `/api/me`-safe shell state and structured surfaces.
- `POST /api/workstream/actions` executes capability-backed surface actions with `X-Selected-Context-Id`.
