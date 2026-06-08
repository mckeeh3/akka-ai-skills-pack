# Realization: Frontend routes and surfaces for User Admin

Capability: `user-and-access-administration`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| User Admin functional-agent rail and workstream panel | `frontend/src/workstream/rail/**`, `frontend/src/workstream/shell/WorkstreamPanel.tsx` | Rail visibility does not grant backend authority. |
| User/membership/invitation/support access list and detail surfaces | `frontend/src/workstream/surfaces/ListSearchSurface.tsx`, `DetailEditSurface.tsx`, `DashboardSurface.tsx` | Renders backend-scoped user directory and admin payloads. |
| Risky access decision cards and action feedback | `DecisionSurface.tsx`, `WorkflowStatusSurface.tsx`, `ActionFeedbackItem.tsx`, `SurfaceActionBar.tsx` | Human approval/denial feedback remains backend-policy governed. |
| Trace links and admin audit context | `frontend/src/workstream/stream/TraceLinkList.tsx`, `AuditTimelineSurface.tsx` | User Admin actions should link to audit/work traces. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | DTOs and error mapping must distinguish unauthorized/forbidden/validation/server errors. |

## Validation evidence

- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-user-admin-expertise.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`

## Gaps / caveats

- Legacy screen fixtures are not the primary runtime architecture unless later reconciliation reclassifies them.
