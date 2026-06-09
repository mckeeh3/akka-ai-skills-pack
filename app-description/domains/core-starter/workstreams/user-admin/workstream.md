# Workstream: User Admin

## Purpose

Give authorized administrators an AI-first access operations command center for scoped users, memberships, invitations, roles/capabilities, support access, access-review work, identity exceptions, and admin audit evidence. The workstream helps admins answer:

1. **Who needs access administration attention in this selected tenant/customer context?**
2. **Which invitations, memberships, roles, support grants, identity links, or review items can I safely act on?**
3. **What policy, last-admin, approval, support-access, and tenant/customer-scope boundaries govern each action?**
4. **What evidence and audit/work traces explain a recommendation, denial, no-op, or completed change?**

User Admin is not a generic CRUD console. It is a role-authorized functional-agent workstream whose structured surfaces are backed by capability `user-and-access-administration`, selected `AuthContext`, backend authorization, idempotency, approval policy, and durable traces.

## Functional agent

Owns `user-admin-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs and surface graphs, not page sessions. Internal access-review worker/agent tasks may support this workstream, but they do not become left-rail functional agents.

## Capability binding

Primary capability: `../../capabilities/user-and-access-administration.md`.

Capability families represented by this workstream include scoped directory reads, invitation lifecycle, membership status changes, role/capability preview and mutation, support-access lifecycle, access-review task lifecycle, identity relink review, admin audit/evidence reads, and User Admin agent guidance.

## Attention model

Backend-owned attention includes pending/stale/expired invitations, invitation delivery failures, risky role or support-access changes, last-admin risks, dormant or disabled-user review items, identity link/relink exceptions, stale access-review findings, autonomous access-review task results needing human review, recent denied admin actions, provider/outbox/model fail-closed blockers, and approval-required decisions.

Attention counts feed the User Admin rail tile and User Admin dashboard. Personal User Admin attention that is assigned to or directly requires action from the signed-in human may also aggregate into My Account. Hidden tenants/customers, hidden users, hidden counts, raw provider state, and cross-scope evidence are never surfaced through attention.

## Surface graph

Default trunk surface: `surface-user-admin-dashboard`.

Primary graph branches:

- `surface-user-admin-member-directory` for scoped search/list and queue-driven filters.
- `surface-user-admin-invitation-panel` for invite/resend/revoke and delivery/acceptance status.
- `surface-user-admin-user-account` for scoped user/membership/support/review/audit detail.
- `surface-user-admin-role-change-preview` for capability delta, last-admin, policy, and approval preview.
- `surface-user-admin-access-review-task` for autonomous access-review progress/result/human decision.
- reusable `decision-card`, `audit-timeline`, `workflow-status`, `markdown-response`, and `system-message` patterns for risky actions, evidence, blocked provider/model states, and safe denials.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation, scoped authorization tests, idempotency/no-op tests, frontend surface rendering checks, audit/work-trace proof, and provider/model/outbox fail-closed proof where applicable.
