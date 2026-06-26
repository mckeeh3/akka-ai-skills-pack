# Access: Audit/Trace

## Authorized role

- `tenant-admin`: may search and view tenant-scoped audit trace records, open full-payload trace details, and configure the tenant's audit retention setting.

The tenant-admin activity-log scope does not grant audit trace access to customer admins, auditors, support operators, SaaS-owner admins, agents, or non-admin tenant members unless a future app-description change explicitly adds those roles.

## Scope rules

All reads, writes, surface actions, and governed-tool invocations use backend-owned selected `AuthContext`, tenant id, active membership status, role/capability grants, and server-side authorization.

Every trace is tenant-scoped. Tenant admins can inspect only traces for their selected tenant/Organization. A trace may optionally carry customer/account scope. Customer/account filters never permit cross-tenant discovery.

System-level/no-customer traces are visible only when they belong to the selected tenant and the caller is an authorized tenant admin.

## Sensitive data rules

Tenant admins may view full request, response, and tool payloads in authorized detail views. The UI must show the warning: **"Sensitive full payload — tenant admin access only."**

Full payloads are not included in list rows, deterministic search summaries, or keyword search indexes. Browser payloads must never expose secrets, bearer/session tokens, provider credentials, hidden cross-tenant identifiers, or frontend-secret material.

## Denials

Disabled users, inactive memberships, missing selected context, non-tenant-admin roles, cross-tenant/customer access, hidden trace references, and unsupported actions are denied server-side.

Denied user-facing requests return safe feedback and emit audit trace evidence without exposing protected data, hidden counts, hidden ids, or internal policy implementation details. Denial traces that are visible to an authorized tenant admin include denial reason and policy reference.

## Retention administration

Only tenant admins can configure tenant audit retention. Allowed values are 30 through 365 days. The default is 90 days. Configuration changes are themselves audit-traced and cannot alter historical trace contents.
