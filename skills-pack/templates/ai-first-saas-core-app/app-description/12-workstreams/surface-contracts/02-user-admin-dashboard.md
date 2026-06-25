# Surface Contract: User Admin Dashboard

- surface-id: `user-admin-dashboard`
- type/version: dashboard/attention/v1
- owner functional agent: `user-admin-agent` (User Admin)
- reusable surfaces: attention cards open `user-admin-user-list`, `user-admin-user-account`, `decision-card`, `audit-trace-explorer`, and `system_message` result surfaces.

## Placement and graph role

This dashboard is the User Admin human surface graph trunk. It answers what is happening in user and Organization administration, what needs this actor's attention, which Organization bootstrap/invitation/access-review/support-access work is blocked or risky, and what actions are authorized next.


## User-visible/internal metadata boundary

Default rendering must use SaaS product language and show only information the current actor needs to decide, act, recover, or understand the business outcome. Internal ids, raw trace/event/correlation data, governed-tool/capability ids, backend component names, prompt/provider/model details, and policy implementation references are implementation metadata. Expose them only in authorized admin, support, auditor, or developer drilldowns, and keep them visually subordinate to user-meaningful labels.

## Payload summary

Payload must include:

- selected `AuthContext`, visible admin capability ids, `correlationId`, trace ids, generated/stale markers;
- attention summary by category: Organization onboarding/bootstrap-admin risk, pending invitation, failed delivery, expired invitation, disabled account, access-review risk, last-admin risk, support-access review, role-escalation proposal, admin audit anomaly;
- summary cards and queue counts from backend views/projections, not frontend-only badges;
- recent material changes with audit links;
- action descriptors with browser-tool id, governed-tool id, capability id, confirmation/approval requirements, denial categories, and idempotency requirements.

## Compact payload schema

```ts
type UserAdminDashboardData = {
  authContext: SurfaceAuthContext;
  attentionByCategory: Array<{ categoryId: string; count: number; highestSeverity: string; stale?: boolean }>;
  summaryCards: Array<{ cardId: string; label: string; count: number; queueSurfaceId?: string; traceIds: string[] }>;
  recentChanges: Array<{ changeId: string; label: string; occurredAt: string; auditTraceId: string; redactionMarkers: string[] }>;
  riskFlags: Array<{ flagId: string; severity: string; relatedSurfaceId?: string; requiresDecision?: boolean }>;
};
```

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Refresh dashboard | `admin.users.dashboard.read` | browser-tool, agent-tool | update `user-admin-dashboard` |
| Open SaaS Owner Organization Administration | `tenant:list` / `tenant:create` / `tenant:update_status` (internal tenant-boundary capability hints) | browser-tool surface-request | `saas-owner-organization-admin` |
| Open invitation queue | `admin.users.search` | browser-tool surface-request | `user-admin-user-list[filter=invitations]` |
| Open access review queue | `admin.access_review.read` | browser-tool surface-request | `user-admin-user-list[filter=access_review]` or `decision-card` |
| Start access-risk investigation | `admin.access_review.investigate` | browser-tool, internal-tool | autonomous task progress/result surface or `system_message` SaaS Foundation App fallback |
| Open audit evidence | `admin.audit.read` / `audit.traces.view` | browser-tool, agent-tool | `audit-trace-explorer` |
| Draft invitation | `admin.invitations.draft` | browser-tool, agent-tool | invite form/decision card or `system_message` SaaS Foundation App fallback |

## Action mapping

| actionId | browserToolId | governedToolId | capabilityId | exposure | resultSurfaceId | idempotency | traceRequired |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `user-admin.refresh-dashboard` | `user-admin.dashboard.refresh` | `useradmin.dashboard.read` | `secure-tenant-user-foundation` | browser-tool, agent-tool | `user-admin-dashboard` | read-only request correlation id | true |
| `user-admin.open-organization-admin` | `user-admin.organizations.open-admin` | `tenant.list` | `secure-tenant-user-foundation` | browser-tool, surface-request | `saas-owner-organization-admin` | dashboard queue id or selected SaaS Owner context | true |
| `user-admin.open-invitation-queue` | `user-admin.invitations.open-queue` | `useradmin.users.search` | `secure-tenant-user-foundation` | browser-tool, surface-request | `user-admin-user-list` | dashboard queue id | true |
| `user-admin.open-access-review-queue` | `user-admin.access-review.open-queue` | `useradmin.access_review.read` | `secure-tenant-user-foundation` | browser-tool, surface-request | `user-admin-user-list` or `decision-card` | dashboard queue id | true |
| `user-admin.start-access-risk-investigation` | `user-admin.access-risk.investigate` | `useradmin.access_review.investigate` | `secure-tenant-user-foundation` | browser-tool, internal-tool | deferred `task-progress-surface` or `system_message` | client-generated investigation request id | true |
| `user-admin.open-audit-evidence` | `user-admin.audit.open` | `audit.traces.view` | `governance-decisions-audit` | browser-tool, agent-tool | `audit-trace-explorer` | trace id | true |
| `user-admin.draft-invitation` | `user-admin.invitation.draft` | `useradmin.invitation.draft` | `secure-tenant-user-foundation` | browser-tool, agent-tool | deferred `invitation-draft-form`, `decision-card`, or `system_message` | client-generated draft id | true |



Action mappings must preserve the shared tool-use contract: `governedToolId`, actor adapter/source (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/timer/consumer/MCP/internal), `confirmationRequired`, `approvalPolicy`, idempotency key, transaction boundary, result/partial-failure behavior, `traceSource`, and `traceRequired`. If this surface exposes only the browser-tool adapter, state `surface_action` and keep any chat/agent adapter in the workstream tool catalog instead of duplicating business semantics.

## UI states

- `loading`: preserve current dashboard cards and show refreshing state.
- `empty`: explicitly state no user-admin attention items for the selected context.
- `error`: show retry, safe category, and readable support/reference label; raw `correlationId` appears only in authorized diagnostic detail, with no counts from failed partial reads.
- `forbidden`: show selected context and denial category without queue counts or identity leakage.
- `stale`: left rail and My Account counts remain marked stale until projection refresh succeeds.

## Auth/security

- Tenant/customer scope is enforced by backend views before counts and queue links are produced.
- SaaS Owner Admin Organization/Tenant lifecycle entrypoints are explicit and limited to platform-safe metadata.
- SaaS Owner support-access and Customer Admin boundaries are explicit.
- Risky actions require decision-card/approval flow when policy says so.
- Frontend visibility does not authorize dashboard reads or actions.

## Rendering and capability tests

- Role variants produce correct counts, cards, redactions, actions, and forbidden states, including SaaS Owner Admin Organization Administration entrypoints.
- Attention count feeds User Admin rail badge and My Account aggregate from backend projection.
- Queue cards open filtered list surfaces through shell request routing, not page-first navigation.
- Risky actions produce decision cards or approval-needed system messages.
- Payload access, denial, action invocation, and projection refresh produce audit/work traces.
