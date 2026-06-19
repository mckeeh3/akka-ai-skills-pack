# Access: Governance/Policy

## Authorized roles

- `policy-owner-approver`: may review, approve, activate, roll back, and request changes for policy proposals only within an authorized SaaS Owner/App Admin platform-governance context or tenant/organization selected context with explicit `governance_policy.*` capabilities.
- `agent-steward`: may draft, simulate, and review policy changes that affect managed-agent behavior only within the selected scope and explicit `governance_policy.*` / `agent_admin.*` capability overlap; cannot approve its own authority expansion unless policy separately allows it.
- `auditor`: may read authorized Governance/Policy evidence, policy outcome notes, and trace links; audit access alone does not allow proposal mutation, approval, activation, rollback, or simulation start.

## Scope rules

All reads, writes, surface actions, streams, agent turns, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer ids, membership status, role/capability grants, and approval policy.

## Denials

Disabled users, inactive memberships, missing selected context, cross-tenant/customer access, unsupported authority expansion, and hidden/unauthorized actions are denied server-side, produce safe `system-message` feedback where user-facing, and emit required traces without exposing protected data.
