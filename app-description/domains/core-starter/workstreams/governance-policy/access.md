# Access: Governance/Policy

## Authorized roles

- `policy-owner-approver`: may draft, review, approve, activate, roll back, and request changes for policy proposals only within an authorized SaaS Owner/App Admin platform-governance context or tenant/organization selected context with explicit `governance_policy.*` capabilities. The same authorized person may draft and approve unless a stricter scope policy applies.
- `agent-steward`: may draft, simulate, and review policy changes that affect managed-agent behavior only within the selected scope and explicit `governance_policy.*` / `agent_admin.*` capability overlap; high-risk authority expansion still follows the high-risk approver rule.
- `auditor`: may read authorized Governance/Policy evidence, policy outcome notes, and trace links; audit access alone does not allow proposal mutation, approval, activation, rollback, or simulation start.

## Scope rules

All reads, writes, surface actions, streams, agent turns, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer ids, membership status, role/capability grants, and approval policy. High-risk policy changes require two approvers when multiple eligible approvers are available in the selected scope; role/capability expansion, support-access policy, agent tool-boundary, model/provider configuration, and audit export policy changes are high-risk by default.

## Denials

Disabled users, inactive memberships, missing selected context, cross-tenant/customer access, unsupported authority expansion, and hidden/unauthorized actions are denied server-side, produce safe `system-message` feedback where user-facing, and emit required traces without exposing protected data.
