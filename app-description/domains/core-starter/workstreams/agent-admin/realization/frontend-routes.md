# Realization: Frontend routes and surfaces for Agent Admin

Capability: `managed-agent-governance`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| Agent Admin command center | `frontend/src/workstream/surfaces/DashboardSurface.tsx` (`AgentAdminCommandCenter`) | Default workstream surface is `surface-agent-admin-dashboard`; attention counters precede authorized task entry points and each card opens a governed result surface. |
| Managed agent catalog and inspection | `frontend/src/workstream/surfaces/ListSearchSurface.tsx` (`AgentAdminCatalogView`), `DetailEditSurface.tsx` (`AgentAdminInspectionDetail`) | Catalog rows are keyboard-operable collection selections; detail is read-only inspection with dedicated task surfaces for behavior/lifecycle changes. |
| Prompt/skill/reference/version diffs, behavior proposals, and lifecycle confirmations | `GovernanceDiffSurface.tsx`, `DecisionSurface.tsx`, `WorkflowStatusSurface.tsx`, `AgentAdminTaskSurface.tsx` | Human review, activation, deactivation, and rollback remain backend-policy governed and use separate confirmation surfaces. |
| Runtime trace explanation and loader/tool denials | `AuditTimelineSurface.tsx`, `TraceLinkList.tsx`, `SystemMessageSurface.tsx` | Trace links connect behavior/admin changes to runtime evidence. |
| Functional-agent rail and composer | `frontend/src/workstream/rail/**`, `frontend/src/workstream/composer/**` | Agent guidance cannot add authority beyond backend governed tools. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | Error mapping must distinguish unauthorized, forbidden, validation, and provider-fail-closed states. |

## Validation evidence

- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`

## Gaps / caveats

- Future UX changes must continue to hide provider secrets and represent denied loader/tool access as first-class recoverable states.
- Agent Admin dashboard rendering is attention-first: actionable attention queues precede general work-area summaries, and non-actionable metrics are omitted or represented as denied/unavailable states returned by the backend.
