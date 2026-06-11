# Surface Contract: SaaS Owner Organization Administration

- surface-id: `saas-owner-organization-admin`
- type/version: directory-detail-workspace/v1
- owner functional agent: `user-admin-agent` (User Admin)
- reusable surfaces: Organization rows and status/admin actions may open `decision-card`, `audit-trace-explorer`, `user-admin-user-list`, and `system_message` result surfaces.

## Placement and graph role

This is the SaaS Owner Admin workspace for creating and maintaining customer-facing Organizations backed by internal Tenant isolation boundaries. It is part of the foundation User Admin workstream, not a business-domain extension. It makes Organization/Tenant lifecycle work explicit in the surface graph so generated apps do not treat tenant creation as an implicit setup script or billing-only side effect.

## Payload summary

Payload must include:

- selected SaaS Owner `AuthContext`, visible SaaS Owner capability ids, `correlationId`, trace ids, generated/stale markers;
- Organization/Tenant directory rows from `TenantDirectoryView` using platform-safe metadata only;
- create/update/status-change action descriptors with browser-tool id, governed-tool id, capability id, confirmation/approval requirements, denial categories, idempotency requirements, and audit trace requirements;
- bootstrap Organization Admin invitation status from `TenantAdminView` / `InvitationView` without raw invitation tokens;
- billing-safe subscription indicators when the core billing foundation is enabled, without organization application data;
- lifecycle risk indicators such as no active Organization Admin, failed bootstrap invitation, suspended/closing status, billing-risk link, and required access review.

## Compact payload schema

```ts
type SaaSOwnerOrganizationAdminData = {
  authContext: SurfaceAuthContext;
  directory: {
    rows: Array<{
      tenantId: string;
      organizationLabel: string;
      legalName?: string;
      status: 'DRAFT' | 'ONBOARDING' | 'ACTIVE' | 'SUSPENDED' | 'CLOSED';
      onboardingState: string;
      bootstrapAdminState: string;
      subscriptionSummary?: { planLabel?: string; status: string; paymentStatus?: string; billingRisk?: string };
      lifecycleRisks: Array<{ riskId: string; severity: string; label: string; traceId?: string }>;
      redactionMarkers: string[];
    }>;
    pageInfo: { cursor?: string; hasMore: boolean; resultCount: number };
  };
  selectedOrganization?: {
    tenantId: string;
    organizationProfile: { displayName: string; legalName?: string; slug?: string; billingContactSummary?: string };
    status: string;
    adminSummary: Array<{ accountId: string; email: string; displayName?: string; membershipStatus: string; invitationStatus?: string; lastAdminRisk?: boolean }>;
    recentAudit: Array<{ auditTraceId: string; label: string; occurredAt: string; redactionMarkers: string[] }>;
  };
  availableActions: Array<SurfaceActionDescriptor>;
};
```

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Refresh Organization directory | `tenant:list` / `tenant:view` | browser-tool, agent-tool | update `saas-owner-organization-admin` |
| Create Organization | `tenant:create` | browser-tool | `saas-owner-organization-admin`, `decision-card`, or `system_message` |
| Open Organization detail | `tenant:view` | browser-tool surface-request | `saas-owner-organization-admin[tenantId]` |
| Update platform-safe Organization profile | `tenant:update_profile` | browser-tool | updated detail or `system_message` |
| Activate, suspend, or close Organization | `tenant:update_status` | browser-tool, approval-gated when policy requires | `decision-card`, updated detail, or `system_message` |
| Invite/bootstrap Organization Admin | `tenant_admin:invite` | browser-tool, agent-assisted draft allowed | `saas-owner-organization-admin`, `user-admin-user-list[filter=tenant-admins]`, `decision-card`, or `system_message` |
| Open Organization Admin list | `tenant_admin:list` | browser-tool surface-request | `user-admin-user-list[filter=tenant-admins,tenantId]` |
| Open audit timeline | `admin.audit.read` / `audit.traces.view` | browser-tool, agent-tool | `audit-trace-explorer` |

## Action mapping

| actionId | browserToolId | governedToolId | capabilityId | exposure | resultSurfaceId | idempotency | traceRequired |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `user-admin.refresh-organizations` | `user-admin.organizations.refresh` | `tenant.list` | `secure-tenant-user-foundation` | browser-tool, agent-tool | `saas-owner-organization-admin` | read-only request correlation id | true |
| `user-admin.create-organization` | `user-admin.organizations.create` | `tenant.create` | `secure-tenant-user-foundation` | browser-tool | `saas-owner-organization-admin`, `decision-card`, or `system_message` | client-generated organization create request id | true |
| `user-admin.open-organization` | `user-admin.organizations.open` | `tenant.view` | `secure-tenant-user-foundation` | browser-tool, surface-request | `saas-owner-organization-admin` | tenant id + request correlation id | true |
| `user-admin.update-organization-profile` | `user-admin.organizations.update-profile` | `tenant.update_profile` | `secure-tenant-user-foundation` | browser-tool | `saas-owner-organization-admin` or `system_message` | profile update request id | true |
| `user-admin.change-organization-status` | `user-admin.organizations.change-status` | `tenant.update_status` | `secure-tenant-user-foundation` | browser-tool | `decision-card`, `saas-owner-organization-admin`, or `system_message` | status change request id | true |
| `user-admin.bootstrap-organization-admin` | `user-admin.organizations.bootstrap-admin` | `tenant_admin.invite` | `secure-tenant-user-foundation` | browser-tool, agent-tool | `saas-owner-organization-admin`, `user-admin-user-list`, `decision-card`, or `system_message` | invitation request id + normalized email + tenant id | true |
| `user-admin.open-organization-admins` | `user-admin.organizations.open-admins` | `tenant_admin.list` | `secure-tenant-user-foundation` | browser-tool, surface-request | `user-admin-user-list` | tenant id + filter fingerprint | true |
| `user-admin.open-organization-audit` | `user-admin.organizations.open-audit` | `audit.traces.view` | `governance-decisions-audit` | browser-tool, agent-tool | `audit-trace-explorer` | trace query fingerprint | true |

## UI states

- `loading`: keep existing rows visible with stale markers until refresh completes.
- `empty`: explain that no Organizations exist yet and show Create Organization only to authorized SaaS Owner Admins.
- `error`: show retry and safe diagnostic `correlationId`; do not synthesize Organization counts.
- `forbidden`: show a user-safe denial without revealing hidden Organization names, tenant ids, billing status, or admin identities.
- `stale`: mark directory, status, billing indicators, and bootstrap-admin counts independently when their projections age differently.
- `approval-required`: route high-impact status changes or unusual bootstrap/admin actions to `decision-card` before mutation.

## Auth/security

- Only `SAAS_OWNER_ADMIN` or an equivalent SaaS Owner capability may create Organizations, update platform-safe Organization/Tenant metadata, change Organization status, or bootstrap initial Organization Admins.
- SaaS Owner Organization Administration may read only platform-safe metadata, billing-safe subscription indicators, bootstrap admin/invitation status, and admin audit facts needed for platform operations.
- It must not read organization application data, customer service data, prompt/provider secrets, user personal settings, or tenant-owned business traces unless the actor is operating under a separate tenant-scoped support-access membership.
- Organization Admin (`TENANT_ADMIN`) cannot create sibling Organizations/Tenants, assign SaaS Owner roles, or invoke SaaS Owner Organization lifecycle actions.
- Organization status and subscription/billing status are separate concepts; billing-driven suspension/reactivation must pass through explicit policy/decision flow when required.
- Raw invitation tokens, private WorkOS/provider ids, payment-provider secrets, and unrelated tenant facts are never returned to the browser.

## Rendering and capability tests

- SaaS Owner Admin can list/search Organizations through `TenantDirectoryView`, create an Organization, update platform-safe metadata, change status, and invite/bootstrap initial Organization Admins.
- Unauthorized Organization Admin, Customer Admin, regular user, and Auditor variants hide or deny mutation actions without leaking hidden Organizations.
- Organization Admin bootstrap uses the mandatory Invitation lifecycle with delivery/outbox status, expiry/revoke behavior, no raw tokens, idempotency, and audit.
- Status changes are validated, policy/decision-gated where high impact, and auditable.
- Directory/detail rows are platform-safe and prove no organization application data is loaded.
- Cross-scope attempts, stale projection refreshes, duplicate create/invite requests, and billing/status mismatch cases produce safe `system_message` or `decision-card` outcomes with trace links.
