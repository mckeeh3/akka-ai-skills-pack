# Capability: Audit and trace investigation

## Purpose

Let tenant admins answer **"who did what?"** by searching and inspecting immutable, tenant-scoped audit trace records for human workers, agent workers, and tool calls.

This v1 capability is an activity-log and trace-detail capability. It is not an export, investigation-notes, acknowledgement, or AI-generated summary capability.

## Actors and scope

- `tenant-admin`: may search and view audit trace records for their selected tenant/Organization and configure that tenant's audit-retention setting.
- Human workers: produce user-facing request/response audit traces and are identified by email, role, and org.
- Agent workers: produce user-facing request/response audit traces and tool-call traces. Agent identity includes agent name, role/workstream, model, prompt/skill/version, session/conversation id, and requested-by human/user when applicable.

All capability calls require selected backend-owned `AuthContext`, active membership, tenant scope, and tenant-admin authorization. Each trace is tenant-scoped. A trace may optionally link to a customer/account; system-level/no-customer traces are allowed.

## Governed tools and exposure

- `search-audit-traces` (`browser-tool` read): searches deterministic metadata/summary fields and applies tenant-admin filters.
- `read-trace-detail` (`browser-tool` read): opens one authorized trace detail, including full request/response/tool payloads and sensitive-payload warning.
- `read-trace-tool-call-detail` (`browser-tool` read): opens tool-call evidence linked to a parent request/response trace.
- `update-audit-retention-setting` (`browser-tool` mutation): updates tenant audit retention between 30 and 365 days and emits its own audit trace.

No v1 agent-tool authority is granted for this capability. Agents may produce audited activity, but they may not use this v1 capability to search or reveal traces. Browser visibility never grants backend authorization.

## Authorization and denials

Unauthorized callers, disabled users, inactive memberships, non-tenant-admin roles, missing selected context, cross-tenant access, and hidden trace references are denied server-side. Denied actions return safe feedback without protected-data leakage or hidden target enumeration and emit denial trace evidence.

Denied trace records shown to tenant admins include the denial reason and policy reference when the tenant admin is authorized to view that trace.

## Search and detail behavior

Search supports these required filters:

- date/time range;
- worker type;
- actor/user/agent;
- action type;
- customer/account;
- status: success, failure, or denied.

Keyword search applies only to deterministic app-generated metadata and summary fields. Full payloads are not indexed for keyword search in v1.

Trace detail exposes full payloads to tenant admins only and displays the warning: **"Sensitive full payload — tenant admin access only."**

## Retention behavior

Default retention is 90 days. Tenant admins may configure retention from 30 to 365 days for their tenant. Audit trace records are immutable and are removed only by retention expiry. Retention configuration changes are traced like any other interaction, including old value, new value, tenant, admin actor, timestamp, status, and correlation id.

## Outcomes

In scope for v1:

- searchable activity log;
- trace detail with full payloads for tenant admins;
- human request/response traces;
- agent request/response traces;
- tool-call traces linked to parent requests/responses;
- denial reason and policy evidence;
- configurable tenant retention from 30 to 365 days;
- immutable records until retention expiry.

Out of scope for v1:

- export or compliance bundles;
- investigation notes;
- suspicious-activity acknowledgement workflows;
- agent-generated audit summaries;
- full-payload keyword search;
- customer-admin, auditor, support-operator, or SaaS-owner trace access beyond tenant-admin v1 access.

## Linked graph nodes

- Workstream: `../workstreams/audit-trace/workstream.md`
- Access: `../workstreams/audit-trace/access.md`
- Behavior: `../workstreams/audit-trace/behavior.md`
- Surfaces: `../workstreams/audit-trace/surfaces/surfaces.md`
- Tools: `../workstreams/audit-trace/tools/governed-tools.md`
- Traces: `../workstreams/audit-trace/traces/work-traces.md`
- Tests: `../workstreams/audit-trace/tests/coverage.md`
