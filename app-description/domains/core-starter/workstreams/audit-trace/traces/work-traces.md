# Traces: Audit/Trace

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required tenant-admin activity-log scope evidence

Audit/Trace tenant-admin activity-log scope uses durable audit trace records as product data, not merely operational logs. Trace records are immutable until retention expiry.

Required event families:

- `audit_trace.human_request_response_recorded`
- `audit_trace.agent_request_response_recorded`
- `audit_trace.tool_call_recorded`
- `audit_trace.action_denied`
- `audit_trace.search_performed`
- `audit_trace.detail_viewed`
- `audit_trace.retention_setting_viewed`
- `audit_trace.retention_setting_updated`
- `audit_trace.retention_setting_update_denied`
- `audit_trace.retention_expired`

## Minimum trace fields

Every consequential trace record includes:

- event id and time;
- tenant id/safe tenant label;
- optional customer/account id/safe label;
- worker type: human, agent, tool, or system;
- human identity where applicable: email, role, org;
- agent identity where applicable: agent name, role/workstream, model, prompt/skill/version, session/conversation id, requested-by user;
- action type;
- status: success, failure, or denied;
- deterministic app-generated summary;
- correlation id and session/conversation id where applicable;
- authorization basis summary;
- denial reason and policy reference for denied actions;
- retention classification and expiry time where known.

Request/response trace detail also retains full request and response payloads for tenant-admin detail viewing.

Tool-call trace detail additionally includes:

- tool name;
- tool purpose;
- input payload;
- output payload;
- authorization result;
- duration;
- status/error;
- linked parent request/response trace id or safe handle.

Retention configuration change traces include old value, new value, tenant, actor, timestamp, status, correlation id, validation/denial reason when applicable, and idempotency/no-op outcome when applicable.

## Redaction, visibility, and indexing

Tenant admins can view full payloads in authorized detail surfaces. The detail UI must display **"Sensitive full payload — tenant admin access only."**

Search rows and keyword indexes use deterministic metadata/summary fields only. Full payloads are not indexed for keyword search in the tenant-admin activity-log scope.

Trace views must not expose secrets, bearer/session tokens, provider credentials, hidden cross-tenant identifiers, frontend-secret material, or raw implementation internals.

## Correlation model

Tool-call traces link to their parent human or agent request/response. Agent traces include session/conversation id and requested-by user where applicable. Retention-setting changes correlate to the tenant admin settings action that produced them.

## Retention model

Default retention is 90 days. Tenant admins may configure 30 to 365 days. Records are removed only by retention expiry. Retention expiry itself should be diagnosable through retained operational evidence that does not reveal expired payloads beyond authorized retention status.
