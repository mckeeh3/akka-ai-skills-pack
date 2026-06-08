# Realization: Frontend routes and surfaces for Agent Admin

Capability: `managed-agent-governance`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| Managed agent catalog and detail | `frontend/src/workstream/surfaces/ListSearchSurface.tsx`, `DetailEditSurface.tsx`, `DashboardSurface.tsx` | Uses typed backend payloads, not internal Java state. |
| Prompt/skill/reference/version diffs and behavior proposals | `GovernanceDiffSurface.tsx`, `DecisionSurface.tsx`, `WorkflowStatusSurface.tsx` | Human review and activation remain backend-policy governed. |
| Runtime trace explanation and loader/tool denials | `AuditTimelineSurface.tsx`, `TraceLinkList.tsx`, `SystemMessageSurface.tsx` | Trace links connect behavior/admin changes to runtime evidence. |
| Functional-agent rail and composer | `frontend/src/workstream/rail/**`, `frontend/src/workstream/composer/**` | Agent guidance cannot add authority beyond backend governed tools. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | Error mapping must distinguish unauthorized, forbidden, validation, and provider-fail-closed states. |

## Validation evidence

- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`

## Gaps / caveats

- Future UX changes must continue to hide provider secrets and represent denied loader/tool access as first-class recoverable states.
