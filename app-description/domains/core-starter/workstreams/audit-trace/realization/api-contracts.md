# Realization: API contracts for Audit/Trace

Capability: `audit-and-trace-investigation`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `read-audit-trace-dashboard` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `WorkstreamEndpoint.java`, audit projections | Scoped command-center counters, readiness, and attention entry points with no hidden counts. |
| `search-audit-traces` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `WorkstreamEndpoint.java`, `AdminAuditView.java`, `AgentRuntimeTraceView.java` | Scoped, redacted search with tenant/customer filters and data-access audit. |
| `read-trace-detail` | `browser-tool`, `agent-tool` | audit/agent/workstream trace services/views | Redacted trace detail must hide unauthorized cross-scope ids and secrets. |
| `read-trace-timeline` | `browser-tool`, `agent-tool` | audit/agent/workstream trace services/views | Correlation timeline omits unauthorized event categories and reauthorizes every open. |
| `read-trace-failure-evidence` | `browser-tool`, `agent-tool` | audit/provider/model/tool-boundary trace services | Redacted denial/provider/tool/model/runtime blocker evidence with safe recovery. |
| `read-investigation-guide` | `browser-tool`, `agent-tool` | audit guidance service | Advisory next steps only; cannot approve, retry, mutate, or expand authority. |
| `request-redacted-export` | `browser-tool` with approval when required | `AdminEndpoint.java`, `AuditTraceService.java`, policy/governance service evidence | Export requests are policy-gated and traced; unredacted exports are not default behavior. |
| `draft-investigation-note` | `browser-tool`, `agent-tool` proposal | `AuditTraceSummaryService.java`, audit summary autonomous agent | Idempotent human-reviewable notes/summaries, not autonomous evidence deletion or policy bypass. |
| Summary task tools (`start-audit-summary-task`, `read-audit-summary-task`, `review-audit-summary-task`, `accept-audit-summary-task`, `reject-audit-summary-task`) | `browser-tool`, `agent-tool` read/prepare, `internal-tool` worker start | `AuditTraceSummaryService.java`, audit summary autonomous agent/task state | Real model-backed redacted advisory summary lifecycle with provider/runtime/tool-boundary fail-closed behavior, no model-less acceptable summary, human accept/reject evidence only, idempotency, and trace evidence. |
| Realtime workstream/audit events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/WorkstreamRealtimeClient.ts` | Streams typed events with reconnect/stale-state handling and no secret payloads. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/audit/AdminAuditViewTest.java`
- `src/test/java/ai/first/application/coreapp/audit/AuditTraceSummaryServiceTest.java`
- `frontend/src/workstream-audit-trace-vertical.contract.test.mjs`
- `frontend/src/workstream-attention-update-delivery.contract.test.mjs`

## Gaps / caveats

- Future API feature work must add explicit forbidden/cross-tenant and redaction tests for any new trace query path.
