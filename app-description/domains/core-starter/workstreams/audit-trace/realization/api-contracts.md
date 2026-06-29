# Realization: API contracts for Audit/Trace

Capability: `audit-and-trace-investigation`.

This file records the Audit/Trace current-intent API contract. It is not runtime proof.

## Browser/API contract obligations

| Tool / action | Exposure | Contract obligations |
|---|---|---|
| `search-audit-traces` / `action-audit-trace-search` | `surface_action` + protected `api_call`; confirmed `human_chat_tool_plan`; bounded `agent_tool_call` | Tenant/support-scoped audit trace search over deterministic metadata/summary fields with role-appropriate redaction and no full-payload keyword search. |
| `search-work-traces` / `action-audit-work-trace-search` | `surface_action` + protected `api_call`; confirmed `human_chat_tool_plan`; bounded `agent_tool_call` | Work-trace search with AgentDefinition/prompt/skill/reference/model/tool/policy refs where visible and model-safe output for agent adapters. |
| `read-audit-trace-detail` / `action-audit-trace-detail` | `surface_action` + protected `api_call`; confirmed `human_chat_tool_plan`; bounded redacted `agent_tool_call` | Authorized detail read with safe default summary, redaction state, sensitive-detail grant handling, support-access context, and no secret-never-store fields. |
| `read-work-trace-detail` / `action-audit-work-trace-detail` | `surface_action` + protected `api_call`; confirmed `human_chat_tool_plan`; bounded redacted `agent_tool_call` | Authorized work-trace detail including prompt/skill/reference/model/tool/data/policy/authorization refs where visible. |
| `lookup-trace-correlation` / `action-audit-trace-correlation-open` | protected `api_call`; confirmed `human_chat_tool_plan`; bounded `agent_tool_call`; projection/internal | Timeline/correlation lookup preserving causation links across source workstreams and emitting explicit trace-gap results. |
| `investigate-denied-trace-access` / `action-audit-trace-denial-open` | protected `api_call`; confirmed `human_chat_tool_plan`; bounded `agent_tool_call` | Denial investigation contract with actor adapter, AuthContext/support scope, governed tool/capability, policy ref, redaction, and safe remediation. |
| `summarize-investigation-evidence` / `action-audit-trace-summary-request` | protected `api_call`; confirmed `human_chat_tool_plan`; bounded `agent_tool_call` | Summary result from selected authorized evidence refs with redaction disclaimer, unknowns, partial-failure handling, and summary trace. |
| `request-redacted-trace-export` / `action-audit-trace-export-request` | protected `api_call`; surface only for request/approval/result | Idempotent redacted export request/result states; sensitive/raw export approval-required or denied unless policy grants it. |
| `review-support-access-traces` / `action-audit-trace-support-access-review-open` | protected `api_call` | Support-access grant/use/expiry/denial review scoped to tenant/support authority and visible to authorized reviewers. |
| runtime-validation evidence link internals | `internal_call` | Safe ingestion/linking of validation run status/evidence refs, workstream, scenario id, source-alignment impact, and trace-gap findings. |

## Validation evidence required before build completion

- Backend/API tests for tenant-admin success, support-access success, non-admin denial, disabled/inactive denial, expired support-access denial, tenant isolation, invalid filters, hidden/expired refs, and no hidden target enumeration.
- Backend/API tests that list/search/timeline outputs exclude full payloads and full-payload keyword search is unavailable.
- Backend/API tests that sensitive detail/export is redacted or approval-gated according to grants.
- Backend/API tests for confirmed chat plan requestedBy/confirmedBy, bounded agent tool allow/deny, model-safe output, and partial-failure result surfaces.
- Backend/API tests for support-access review, redacted export request idempotency, runtime-validation evidence links, and trace-gap diagnostics.

## Explicit API exclusions

Do not implement autonomous remediation, support-access self-approval, raw sensitive export by default, trace edit/delete, full-payload keyword search, or agent/chat authority expansion APIs as part of this current-intent slice.
