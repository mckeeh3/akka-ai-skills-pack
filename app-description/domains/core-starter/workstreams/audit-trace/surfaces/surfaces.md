# Surfaces: Audit/Trace

Audit/Trace v1 is a tenant-admin activity-log workstream. It owns browser surfaces for searching audit traces, viewing authorized trace detail/full payloads, and configuring tenant retention.

## Surface bindings

| Surface id | Type | Surface contract | Purpose |
|---|---|---|---|
| `surface-audit-trace-activity-log` | `list-search` | `audit.trace.activityLog.v1` | Searchable tenant-admin activity log answering "who did what?" |
| `surface-audit-trace-detail` | `detail` | `audit.trace.detail.v1` | Authorized trace detail with full request/response/tool payloads and sensitive-access warning. |
| `surface-audit-trace-retention-settings` | `settings-form` | `audit.trace.retentionSettings.v1` | Tenant-admin configuration for audit retention from 30 to 365 days. |
| `surface-audit-trace-system-message` | `system-message` | `audit.trace.systemMessage.v1` | Safe validation, forbidden, not-found/redacted, or idempotent-result feedback. |

## `surface-audit-trace-activity-log` contract details

- Surface role: list/search surface for tenant-scoped audit records.
- Owner: Audit/Trace workstream; exactly-one functional-agent binding is `audit-trace-agent`, but v1 search/detail actions are browser surface actions only.
- Actor: `tenant-admin` with selected backend-owned `AuthContext` and active membership.
- User goal: find the human worker, agent worker, or tool call that performed an action.
- Primary action: run or refine a backend-authorized search. Success returns rows for the selected tenant only.

### Activity log payload schema

Top-level browser-safe fields for `audit.trace.activityLog.v1`:

- `surfaceContract`, `surfaceId`, `generatedAt`, `selectedTenant`, `authContextSummary`, `query`, `filters`, `rows`, `pageInfo`, `availableActions`, `emptyState`, `validationErrors`, `redaction`, and `correlationId`.
- `filters`: date/time range, worker type, actor/user/agent, action type, customer/account, and status (`success`, `failure`, `denied`).
- `rows[]`: trace display handle, time, worker type, actor/agent display label, action type, customer/account label or `system/no-customer`, status, deterministic app-generated summary, correlation/session id, and available action ids.
- `redaction`: safe explanation of omitted data categories; rows never include full payloads, secrets, raw tokens, provider credentials, or hidden cross-tenant identifiers.

Keyword search applies only to deterministic metadata/summary fields and never to full payloads.

### Activity log action map

| User-facing interaction | Action id | Governed capability/tool | Result surface or outcome |
|---|---|---|---|
| Run, refresh, filter, sort, or page activity log | `action-audit-trace-search` | `audit.trace.search` / `search-audit-traces` | `surface-audit-trace-activity-log`, `validation-error`, or `forbidden` |
| Open trace detail from an authorized row | `action-audit-trace-detail` | `audit.trace.detail.read` / `read-trace-detail` | `surface-audit-trace-detail` or `not_found_or_redacted` |
| Open retention settings | `action-audit-trace-retention-settings-open` | `audit.trace.retention.read` / `read-audit-retention-setting` | `surface-audit-trace-retention-settings` |

### Activity log states, traces, UI, and tests

- States: loading, empty authorized result set, ready, submitting, validation-error, forbidden, partial-data/redacted, stale/reconnect, and failure.
- Every search, validation failure, denial, empty result, partial/redacted result, and detail-open attempt emits trace evidence with tenant, actor, selected filters, status, and correlation id.
- Rows and controls are keyboard-operable, have visible focus, use accessible table/list semantics, and do not rely on color alone for status.
- Surface-description sufficiency review: sufficient for v1 implementation without inventing payload fields, actions, auth/tenant behavior, trace links, tests, or component semantics.

## `surface-audit-trace-detail` contract details

- Surface role: detail view for one authorized tenant-scoped audit trace.
- Actor: `tenant-admin` with selected backend-owned `AuthContext` and active membership.
- User goal: inspect full payload evidence for a specific human request/response, agent request/response, tool call, denial, failure, or retention-setting change.
- Primary action: open or refresh detail; follow linked parent/child trace references when authorized.
- Sensitive warning: the detail view must show **"Sensitive full payload — tenant admin access only."** before or beside full payload sections.

### Detail payload schema

Top-level browser-safe fields for `audit.trace.detail.v1`:

- `surfaceContract`, `surfaceId`, `generatedAt`, `selectedTenant`, `authContextSummary`, `traceDisplayHandle`, `time`, `worker`, `action`, `status`, `customerAccount`, `correlationId`, `sessionConversationId`, `summary`, `sensitivePayloadWarning`, `requestPayload`, `responsePayload`, `toolCall`, `denial`, `relatedTraces`, `retentionStatus`, `availableActions`, `redaction`, and `recovery`.
- `worker.human`: email, role, and org when worker type is human.
- `worker.agent`: agent name, role/workstream, model used, prompt/skill/version used, session/conversation id, and requested-by human/user where applicable.
- `toolCall`: tool name, purpose, input payload, output payload, authorization result, duration, status/error, and linked parent request/response.
- `denial`: denial reason and policy reference when status is denied.
- `relatedTraces[]`: parent request/response link for tool calls and child tool-call links for parent requests/responses.

Internal-only metadata includes secrets, bearer/session tokens, provider credentials, hidden cross-tenant identifiers, raw storage ids, and backend implementation names unless a future diagnostic role explicitly authorizes them.

### Detail action map

| User-facing interaction | Action id | Governed capability/tool | Result surface or outcome |
|---|---|---|---|
| Open or refresh trace detail | `action-audit-trace-detail` | `audit.trace.detail.read` / `read-trace-detail` | `surface-audit-trace-detail`, `not_found_or_redacted`, or `forbidden` |
| Open linked tool-call detail or parent request/response | `action-audit-trace-tool-call-detail` | `audit.trace.tool_call.detail.read` / `read-trace-tool-call-detail` | `surface-audit-trace-detail` or `not_found_or_redacted` |
| Return to activity log with prior filters | `action-audit-trace-search` | `audit.trace.search` / `search-audit-traces` | `surface-audit-trace-activity-log` |

### Detail states, traces, UI, and tests

- States: loading, ready, forbidden, `not_found_or_redacted`, retention-expired, partial-data/redacted, stale/reconnect, and failure.
- Every detail read, linked trace open, denial, not-found/redacted result, and retention-expired result emits trace evidence.
- Full payload sections are visually subordinate to the sensitive warning and are not copied into list rows or keyword search.
- Surface-description sufficiency review: sufficient for v1 implementation without inventing payload fields, actions, auth/tenant behavior, trace links, tests, or component semantics.

## `surface-audit-trace-retention-settings` contract details

- Surface role: settings form for tenant audit retention.
- Actor: `tenant-admin` with selected backend-owned `AuthContext` and active membership.
- User goal: review and update the tenant audit-retention value.
- Primary action: submit a retention value from 30 to 365 days.

### Retention payload schema

Top-level browser-safe fields for `audit.trace.retentionSettings.v1`:

- `surfaceContract`, `surfaceId`, `generatedAt`, `selectedTenant`, `authContextSummary`, `currentRetentionDays`, `defaultRetentionDays`, `allowedMinDays`, `allowedMaxDays`, `pendingValue`, `validationErrors`, `lastChangedBy`, `lastChangedAt`, `availableActions`, and `correlationId`.

Default retention is 90 days. Allowed values are 30 through 365 days.

### Retention action map

| User-facing interaction | Action id | Governed capability/tool | Result surface or outcome |
|---|---|---|---|
| Open retention settings | `action-audit-trace-retention-settings-open` | `audit.trace.retention.read` / `read-audit-retention-setting` | `surface-audit-trace-retention-settings` |
| Save retention setting | `action-audit-trace-retention-settings-save` | `audit.trace.retention.update` / `update-audit-retention-setting` | `surface-audit-trace-retention-settings` or `surface-audit-trace-system-message` |

### Retention states, traces, UI, and tests

- States: loading, ready, submitting, validation-error, forbidden, no-op/idempotent, saved, stale/conflict, and failure.
- Saving a valid changed value emits an immutable audit trace recording old value, new value, actor email/role/org, tenant, timestamp, status, and correlation id.
- Submitting the same value is an idempotent no-op with a diagnosable outcome trace.
- Values below 30 or above 365 are rejected with validation feedback and no configuration change.
- Surface-description sufficiency review: sufficient for v1 implementation without inventing payload fields, actions, auth/tenant behavior, trace links, tests, or component semantics.

## Explicit v1 exclusions

The following previously discussed or candidate surfaces are out of scope for v1 and must not be generated as working features from this workstream description:

- export request or compliance bundle surfaces;
- investigation note surfaces;
- suspicious activity acknowledgement/review surfaces;
- AI-generated summary progress/review surfaces;
- full-payload keyword search surfaces.
