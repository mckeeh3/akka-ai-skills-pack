# Realization: Frontend routes and surfaces for Governance/Policy

Capability: `governance-policy-lifecycle`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| Policy proposal queue and dashboard | `frontend/src/workstream/surfaces/ListSearchSurface.tsx`, `DashboardSurface.tsx` | Backend-scoped proposal rows and statuses drive display. |
| Policy diff/simulation/impact evidence | `GovernanceDiffSurface.tsx`, `MarkdownResponseSurface.tsx`, `DetailEditSurface.tsx` | Renders evidence and affected-capability summaries without hidden commits. |
| Approval/activation/rollback decisions | `DecisionSurface.tsx`, `WorkflowStatusSurface.tsx`, `OutcomeSurface.tsx`, `SurfaceActionBar.tsx` | Human decisions map to backend policy actions and trace links. |
| Audit and policy decision trace links | `AuditTimelineSurface.tsx`, `TraceLinkList.tsx` | Policy events must be investigable from the workstream. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | DTOs and errors must preserve policy denial and validation states. |

## Validation evidence

- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`

## Gaps / caveats

- Legacy `frontend/src/screens/governance/GovernancePoliciesPage.tsx` is reference/fixture evidence, not the primary workstream shell architecture.
