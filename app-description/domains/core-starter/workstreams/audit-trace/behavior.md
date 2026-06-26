# Behavior: Audit/Trace

## Current-state behavior

Audit/Trace v1 provides a tenant-admin searchable activity log for immutable audit records answering **"who did what?"**.

The workstream records and exposes these trace families:

- human worker user-facing request/response traces;
- agent worker user-facing request/response traces;
- tool-call traces linked to their parent request/response;
- authorization denials, including denial reason and policy reference;
- tenant retention configuration changes.

Every trace is tenant-scoped. A trace may optionally link to a customer/account. System-level/no-customer traces are valid when no customer/account applies.

## Search behavior

Tenant admins can filter the activity log by:

- date/time range;
- worker type;
- actor/user/agent;
- action type;
- customer/account;
- status: success, failure, or denied.

Keyword search is limited to deterministic app-generated metadata and summary fields. Full request, response, and tool payloads are not indexed for keyword search in v1.

Default activity log rows show:

- time;
- worker type;
- actor/agent;
- action type;
- customer/account;
- status;
- deterministic summary;
- correlation/session id.

## Detail behavior

Tenant admins can open an authorized trace detail and view full payloads. Detail views must show the warning: **"Sensitive full payload — tenant admin access only."**

Human worker trace detail identifies the human by email, role, and org.

Agent worker trace detail identifies the agent by agent name, role/workstream, model used, prompt/skill/version used, session/conversation id, and requested-by human/user where applicable.

Tool-call detail includes tool name, tool purpose, input payload, output payload, authorization result, duration, status/error, and the linked parent request/response.

Denied actions show the denial reason and policy reference when the tenant admin is authorized to inspect that tenant-scoped trace.

## Retention behavior

Default audit trace retention is 90 days. Tenant admins can configure tenant retention from 30 to 365 days.

Audit trace records are immutable and are removed only by retention expiry. Retention configuration changes are logged like any other interaction and include old value, new value, tenant, actor, timestamp, status, and correlation id.

## Invariants

- Backend authorization is required for every search, detail read, tool-call detail read, and retention configuration change.
- Browser route visibility or frontend state never grants trace access.
- Tenant admins cannot edit or delete audit records.
- Retention expiry is the only v1 record-removal mechanism.
- Full payloads are visible only in authorized detail views, not in list rows or keyword search indexes.
- Search summaries are deterministic app-generated summaries only.

## Forbidden behavior

V1 must not:

- provide export or compliance bundles;
- provide investigation notes or acknowledgements;
- use agent-generated audit summaries for search indexing;
- allow keyword search across full payloads;
- expose tenant audit traces to non-tenant-admin users;
- reveal cross-tenant data, hidden trace existence, raw secrets, provider credentials, bearer/session tokens, or internal authorization state;
- allow agents to retrieve or reveal audit payloads through chat/tool authority.

## Failure and edge cases

- Invalid filters return validation errors without running a broad fallback query.
- Cross-tenant, missing-context, disabled-user, inactive-membership, and non-tenant-admin requests are denied server-side and traced.
- Hidden or expired trace references return safe `not_found_or_redacted` feedback without confirming protected record existence.
- Repeated identical retention-setting submissions with the same value are idempotent no-ops that still produce a diagnosable outcome trace.
- Retention values below 30 or above 365 days are rejected with validation feedback and trace evidence.
