# Access: Governance/Policy

## Authorized roles

- `saas-owner-admin`: manages foundation policy catalog defaults, approves SaaS-wide policy versions, activates approved defaults, grants or revokes SaaS-scope exceptions, and authorizes SaaS-wide rollback decisions.
- `tenant-admin`: reads effective policies for their tenant, drafts tenant-scoped policy changes, approves/activates tenant-owned business-governance changes where policy allows, requests or reviews tenant exceptions, and initiates tenant rollback decisions.
- `policy-operator`: prepares drafts, runs simulations, assembles decision-card evidence, requests approvals, and executes approved activation/rollback/exception actions only when granted the matching capability in selected `AuthContext`.
- `auditor`: reads authorized policy versions, drafts, simulations, decisions, exceptions, rollback records, runtime-decision evidence, and trace links. Audit access alone does not allow mutation or approval.
- `support`: reads authorized tenant policy state and history for troubleshooting only when support access is active and backend-scoped.

## Scope rules

All reads, writes, agent turns, surface actions, confirmed chat plans, bounded agent tool calls, workflow steps, runtime internal checks, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer/account ids, membership status, role/capability grants, support-access state, and hard platform-security checks.

Policy scopes may include SaaS/default, tenant, customer, account, agent, workstream, governed tool/action, role, model/prompt/skill/reference policy, approval threshold, exception, and trace-visibility scope. Browser-provided scope values are untrusted hints; the backend resolves and authorizes the selected scope before returning or changing policy state.

Tenant admins and policy operators may not change SaaS-wide defaults unless the selected context and capability grant say so. SaaS owner admins must not silently overwrite tenant-owned approved versions or exceptions. Cross-tenant/customer/account access is denied or redacted even when a visible deep link is supplied.

## Approval authority

Human approval is required for authority expansion, approval-gate changes, exception grants, activation of behavior-shaping policy versions, rollback to a prior version, and trace visibility/retention policy changes. The reviewer must have the matching role/capability for the policy scope and cannot approve their own high-risk draft when separation-of-duty policy marks it as required.

The Governance/Policy functional agent may draft, summarize, and recommend. It cannot approve, activate, roll back, or grant exceptions. `human_chat_tool_plan` execution requires exact plan-snapshot confirmation plus backend reauthorization. `agent_tool_call` is read/simulation-assist only unless a future app-description explicitly grants a non-mutating tool.

## Denials

Disabled users, inactive memberships, missing selected context, missing capability, missing reviewer authority, separation-of-duty violation, cross-tenant/customer/account access, hidden scope targets, unsupported policy types/scopes, missing change reason, stale policy version, failed simulation precondition, unapproved activation, expired/invalid exception, and attempts to override hard platform security are denied server-side.

Denials produce safe `system_message` feedback where user-facing, result or partial-failure surfaces where applicable, and policy-denial traces without exposing protected data or confirming hidden target existence.

## Non-overridable controls

Governance/Policy cannot override tenant isolation, backend authorization, frontend secret boundaries, JWT/provider-key protection, raw prompt/model/provider payload protection, redaction boundaries, audit/trace integrity, platform integrity checks, or required human-governance gates for authority expansion.
