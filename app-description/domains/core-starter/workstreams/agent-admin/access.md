# Access: Agent Admin

## Authorized roles

Tenant/organization administrators and SaaS Owner platform administrators may access Agent Admin when the selected backend `AuthContext` grants explicit `agent_admin.*` capabilities for the relevant governance scope. Tenant/Organization Admin contexts govern tenant/organization-scoped managed agents. SaaS Owner/App Admin contexts govern platform-level managed agents and seed/default behavior only; they do not gain tenant/customer application-data access by implication. Customer Admins, Customer Users, Tenant Employees without `agent_admin.*`, and Auditors without Agent Admin trace visibility are not Agent Admin operators.

## Scope rules

All reads, writes, surface actions, streams, agent turns, and governed-tool invocations use backend-owned selected `AuthContext`, app-owner/tenant ids, membership status, role/capability grants, and approval policy. Agent Admin supports platform-level governance for SaaS Owner/App Admin contexts and tenant/organization-scoped governance for Tenant/Organization Admin contexts; it is not customer-scoped. Customer-scoped selected contexts must not read, propose, approve, activate, roll back, or test managed-agent prompts, skills, references, manifests, model refs, tool boundaries, seed material, or prompt-risk review tasks.

## Denials

Disabled users, inactive memberships, missing selected context, customer-scoped callers, cross-tenant or cross-platform-scope access, SaaS Owner attempts to read tenant/customer agent data without an authorized tenant context, Tenant Admin attempts to govern platform-level agents, missing `agent_admin.*` capability, unsupported authority expansion, and hidden/unauthorized actions are denied server-side, produce safe `system-message` feedback where user-facing, and emit required traces without exposing protected data.
