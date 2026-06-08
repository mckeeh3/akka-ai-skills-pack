# Realization: API contracts for Audit/Trace

Capability: `audit-and-trace-investigation`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `search-audit-traces` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `WorkstreamEndpoint.java`, `AdminAuditView.java`, `AgentRuntimeTraceView.java` | Scoped, redacted search with tenant/customer filters and data-access audit. |
| `read-trace-detail` | `browser-tool`, `agent-tool` | audit/agent/workstream trace services/views | Timeline/correlation detail must hide unauthorized cross-scope ids and secrets. |
| `request-redacted-export` | `browser-tool` with approval when required | `AdminEndpoint.java`, `AuditTraceService.java`, policy/governance service evidence | Export requests are policy-gated and traced; unredacted exports are not default behavior. |
| `draft-investigation-note` | `agent-tool` proposal | `AuditTraceSummaryService.java`, audit summary autonomous agent | Produces human-reviewable notes/summaries, not autonomous evidence deletion or policy bypass. |
| Realtime workstream/audit events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/WorkstreamRealtimeClient.ts` | Streams typed events with reconnect/stale-state handling and no secret payloads. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/audit/AdminAuditViewTest.java`
- `src/test/java/ai/first/application/coreapp/audit/AuditTraceSummaryServiceTest.java`
- `frontend/src/workstream-audit-trace-vertical.contract.test.mjs`
- `frontend/src/workstream-attention-update-delivery.contract.test.mjs`

## Gaps / caveats

- Future API feature work must add explicit forbidden/cross-tenant and redaction tests for any new trace query path.
