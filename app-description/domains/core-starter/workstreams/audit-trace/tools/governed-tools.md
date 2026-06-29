# Tools: Audit/Trace

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Audit/Trace exposes read/investigation governed tools for audit/work trace search, read, correlation, denial investigation, support-access review, investigation summary, redacted export request, trace ingestion/projection, trace-gap detection, and runtime-validation evidence linking.

Canonical browser action aliases:

- `action-audit-trace-search-open` / `action-audit-trace-search` -> `search-audit-traces`
- `action-audit-work-trace-search` -> `search-work-traces`
- `action-audit-trace-detail` -> `read-audit-trace-detail`
- `action-audit-work-trace-detail` -> `read-work-trace-detail`
- `action-audit-trace-correlation-open` -> `lookup-trace-correlation`
- `action-audit-trace-denial-open` -> `investigate-denied-trace-access`
- `action-audit-trace-summary-request` -> `summarize-investigation-evidence`
- `action-audit-trace-export-request` -> `request-redacted-trace-export`
- `action-audit-trace-support-access-review-open` -> `review-support-access-traces`
- `action-audit-trace-runtime-validation-open` -> `lookup-trace-correlation`

## Tool authority boundaries

All tools require backend-owned selected `AuthContext`, tenant/support scope, active membership or service provenance, role/capability grants, redaction policy, and trace emission.

Human `surface_action`/`api_call` access does not grant model access. `human_chat_tool_plan` access requires explicit confirmation of the proposed read-only plan. `agent_tool_call` access requires ToolPermissionBoundary grants and model-safe result payloads. Export/support-access-sensitive paths require approval where policy says so.

## Adapter binding matrix

| Governed tool id | Capability id | Workers | Allowed actor adapters | Authority / side-effect boundary | Result surfaces / events |
|---|---|---|---|---|---|
| `search-audit-traces` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, Audit/Trace functional agent, system worker | `surface_action`, `api_call`, `human_chat_tool_plan`, bounded `agent_tool_call`, `internal_call` | Read-only scoped audit metadata/summary query; no full-payload keyword search. | `surface-audit-trace-search`, validation/forbidden system message, search trace. |
| `search-work-traces` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, Audit/Trace functional agent, system worker | `surface_action`, `api_call`, `human_chat_tool_plan`, bounded `agent_tool_call`, `internal_call` | Read-only scoped work-trace query. | Search surface, work-trace search trace. |
| `read-audit-trace-detail` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, Audit/Trace functional agent, system worker | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded redacted `agent_tool_call`, `internal_call` | Read-only authorized detail; sensitive fields require grant; secret-never-store material absent. | `surface-audit-trace-detail`, `not_found_or_redacted`, detail-view trace. |
| `read-work-trace-detail` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, Audit/Trace functional agent, system worker | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded redacted `agent_tool_call`, `internal_call` | Read-only authorized work trace detail. | Detail surface, work-trace read trace. |
| `lookup-trace-correlation` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, Audit/Trace functional agent, system worker | `surface_action`, `api_call`, `human_chat_tool_plan`, bounded `agent_tool_call`, `projection_update`, `internal_call` | Read-only correlation/timeline over authorized safe handles; trace gaps represented explicitly. | `surface-audit-trace-timeline`, trace-gap/system message, correlation trace. |
| `investigate-denied-trace-access` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, Audit/Trace functional agent, system worker | `surface_action`, `api_call`, `human_chat_tool_plan`, bounded `agent_tool_call`, `internal_call` | Read-only denial evidence and safe remediation; no hidden target enumeration. | `surface-audit-trace-denial-investigation`, denial-investigation trace. |
| `summarize-investigation-evidence` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, Audit/Trace functional agent, system worker | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `internal_call` | Generate summary from selected authorized evidence; no new facts beyond cited refs. | `surface-audit-trace-investigation-summary`, summary trace, partial-failure result. |
| `request-redacted-trace-export` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, system worker; agent routes only to surface | `surface_action`, `api_call`, `internal_call` | Request/prepare redacted export where policy permits; sensitive/raw export approval-gated or denied. | `surface-audit-trace-export-request`, approval-required/denied/redacted-result traces. |
| `review-support-access-traces` | `audit-and-trace-investigation` | tenant-admin human, SaaS support human, system worker | `surface_action`, `api_call`, `internal_call` | Read support-access grant/use/expiry/denial evidence inside authorized scope. | `surface-audit-trace-support-access-review`, support-access review trace. |
| trace ingestion/projection/retention/gap internals | `audit-and-trace-investigation` | system worker | `internal_call`, `consumer_reaction`, `projection_update`, `timer_invocation` | Append immutable trace facts, update projections, expire by retention, detect trace gaps. | Durable trace facts, projection updates, retention-expiry, trace-gap attention. |
| runtime-validation evidence link internals | `audit-and-trace-investigation` | system worker | `internal_call` | Link validation run/status/evidence refs into Audit/Trace projections. | Runtime-validation evidence trace and dashboard/timeline item. |

## Confirmation, approval, idempotency, and result rules

- Read-only `human_chat_tool_plan` executions require confirmation bound to the proposed plan, caller, scope, governed tool ids, redaction level, and correlation id.
- Bounded `agent_tool_call` executions return model-safe summaries and safe handles; missing tool-boundary grants fail closed and emit denial traces.
- Export requests are idempotent by actor/scope/redaction/export request key. Replays return current request/result state without duplicate artifact preparation.
- Repeated search/detail/correlation/denial reads are read-only and may emit read traces only.
- Partial failures produce `surface-audit-trace-system-message` or `surface-audit-trace-investigation-summary` with per-tool result refs.
- Denied tool calls are traced and return safe feedback without hidden target enumeration or protected-data leakage.

## Current explicit exclusions

Audit/Trace does not grant autonomous mutation, support-access self-approval, raw sensitive export by default, full-payload keyword search, trace deletion/editing, prompt-based authority expansion, or cross-tenant discovery.
