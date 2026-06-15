# Access: Agent Admin

## Authorized roles

Only tenant/organization administrator users may access Agent Admin. In the foundation role model this means a selected `TENANT_ADMIN` / `tenant-admin` AuthContext with explicit `agent_admin.*` capabilities. Customer Admins, Customer Users, Tenant Employees, Auditors, and SaaS Owner/Admin platform contexts are not Agent Admin operators unless a future backend policy grants a separate tenant/organization-admin selected context.

## Scope rules

All reads, writes, surface actions, streams, agent turns, and governed-tool invocations use backend-owned selected `AuthContext`, tenant ids, membership status, role/capability grants, and approval policy. Agent Admin is tenant/organization-scoped; it is not customer-scoped. Customer-scoped selected contexts must not read, propose, approve, activate, roll back, or test managed-agent prompts, skills, references, manifests, model refs, tool boundaries, seed material, or prompt-risk review tasks.

## Denials

Disabled users, inactive memberships, missing selected context, customer-scoped callers, cross-tenant access, missing `agent_admin.*` capability, unsupported authority expansion, and hidden/unauthorized actions are denied server-side, produce safe `system-message` feedback where user-facing, and emit required traces without exposing protected data.
