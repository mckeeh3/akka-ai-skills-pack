# Surface Contract: User Admin Dashboard

- surface-id: `user-admin-dashboard`
- type/version: dashboard/attention/v1
- owner functional agent: `user-admin-agent` (User Admin)
- reusable surfaces: attention cards open `user-admin-user-list`, `user-admin-user-account`, `decision-card`, `audit-trace-explorer`, and `system_message` result surfaces.

## Placement and graph role

This dashboard is the User Admin human surface graph trunk. It answers what is happening in user administration, what needs this actor's attention, which invitation/access-review/support-access work is blocked or risky, and what actions are authorized next.

## Payload summary

Payload must include:

- selected `AuthContext`, visible admin capability ids, `correlationId`, trace ids, generated/stale markers;
- attention summary by category: pending invitation, failed delivery, expired invitation, disabled account, access-review risk, last-admin risk, support-access review, role-escalation proposal, admin audit anomaly;
- summary cards and queue counts from backend views/projections, not frontend-only badges;
- recent material changes with audit links;
- action descriptors with browser-tool id, governed-tool id, capability id, confirmation/approval requirements, denial categories, and idempotency requirements.

## Allowed actions

| Action | Capability hint | Qualified exposure | Result surface |
|---|---|---|---|
| Refresh dashboard | `admin.users.dashboard.read` | browser-tool, agent-tool | update `user-admin-dashboard` |
| Open invitation queue | `admin.users.search` | browser-tool surface-request | `user-admin-user-list[filter=invitations]` |
| Open access review queue | `admin.access_review.read` | browser-tool surface-request | `user-admin-user-list[filter=access_review]` or `decision-card` |
| Start access-risk investigation | `admin.access_review.investigate` | browser-tool, internal-tool | autonomous task progress/result surface or `system_message` starter fallback |
| Open audit evidence | `admin.audit.read` / `audit.traces.view` | browser-tool, agent-tool | `audit-trace-explorer` |
| Draft invitation | `admin.invitations.draft` | browser-tool, agent-tool | invite form/decision card or `system_message` starter fallback |

## UI states

- `loading`: preserve current dashboard cards and show refreshing state.
- `empty`: explicitly state no user-admin attention items for the selected context.
- `error`: show retry, safe category, and `correlationId`; no counts from failed partial reads.
- `forbidden`: show selected context and denial category without queue counts or identity leakage.
- `stale`: left rail and My Account counts remain marked stale until projection refresh succeeds.

## Auth/security

- Tenant/customer scope is enforced by backend views before counts and queue links are produced.
- SaaS Owner support-access and Customer Admin boundaries are explicit.
- Risky actions require decision-card/approval flow when policy says so.
- Frontend visibility does not authorize dashboard reads or actions.

## Rendering and capability tests

- Role variants produce correct counts, cards, redactions, actions, and forbidden states.
- Attention count feeds User Admin rail badge and My Account aggregate from backend projection.
- Queue cards open filtered list surfaces through shell request routing, not page-first navigation.
- Risky actions produce decision cards or approval-needed system messages.
- Payload access, denial, action invocation, and projection refresh produce audit/work traces.
