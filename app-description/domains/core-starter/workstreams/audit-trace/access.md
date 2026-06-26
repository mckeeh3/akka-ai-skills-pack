# Access: Audit/Trace

## Authorized role

- `tenant-admin`: may search and view tenant-scoped audit trace records, open full-payload trace details, and configure the tenant's audit retention setting.

The tenant-admin activity-log scope does not grant audit trace access to customer admins, auditors, support operators, SaaS-owner admins, agents, or non-admin tenant members unless a future app-description change explicitly adds those roles.

## Authorized workers

- `workers/tenant-admin-human.md` is the primary human worker for browser `surface_action` and `api_call` adapters.
- `workers/audit-trace-functional-agent-worker.md` is the AI-backed workstream assistant for navigation and safe explanation only; it has no trace-evidence retrieval or mutation adapters in this scope.
- `workers/audit-trace-system-worker.md` is the deterministic backend/API/projection/retention worker for trace ingestion, scoped search/detail reads, retention update/expiry, idempotency, and trace emission.

## Scope rules

All reads, writes, surface actions, API calls, internal calls, consumer/timer reactions, and governed-tool invocations use backend-owned selected `AuthContext`, tenant id, active membership status, role/capability grants, and server-side authorization. Human browser surface availability does not grant agent authority; agent prompt text does not create trace-search, payload-read, retention-update, or chat-plan authority.

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
