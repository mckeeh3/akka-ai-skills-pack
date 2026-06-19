# Realization: Frontend routes and surfaces for Audit/Trace

Capability: `audit-and-trace-investigation`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| Audit search/list and investigation dashboard | `frontend/src/workstream/surfaces/ListSearchSurface.tsx`, `DashboardSurface.tsx` | Query/filter UI must rely on backend-scoped results. |
| Trace timeline/detail | `AuditTimelineSurface.tsx`, `DetailEditSurface.tsx`, `TraceLinkList.tsx` | Timelines render redacted, correlation-rich evidence. |
| Export/approval/denial surfaces | `DecisionSurface.tsx`, `WorkflowStatusSurface.tsx`, `SystemMessageSurface.tsx`, `OutcomeSurface.tsx` | Sensitive export requests surface policy gates and denials. |
| Realtime/stale-state behavior | `frontend/src/workstream/realtime/useWorkstreamRealtime.ts`, `workstreamEvents.ts` | Event streams update workstream state and must recover from disconnects. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `HttpWorkstreamRealtimeClient.ts`, `types.ts` | Normalizes unauthorized/forbidden/not-found/server stream errors. |

## Validation evidence

- `frontend/src/workstream-audit-trace-vertical.contract.test.mjs`
- `frontend/src/workstream-attention-update-delivery.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`

## Gaps / caveats

- Audit/Trace frontend realization must stay in `frontend/src/workstream/**`; removed screen modules are not reference or fallback architecture.
