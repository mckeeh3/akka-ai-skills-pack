# Realization: Frontend routes and surfaces for Governance/Policy

Capability: `governance-policy-lifecycle`.

## Frontend evidence

| Surface / route concern | Candidate frontend evidence | Notes |
|---|---|---|
| Policy dashboard and all-policy inventory | `frontend/src/workstream/surfaces/DashboardSurface.tsx`, `ListSearchSurface.tsx` | Shows all visible policies, search/filter, overridden counts, and shortcuts. |
| Effective-policy detail | detail/show inspection surface components | Renders SaaS default, tenant override, effective value, winning scope, and decision explanation. |
| Simple policy edit | settings/detail-edit surface components, `SurfaceActionBar.tsx` | Supports boolean/counter default and override edits, reset-to-default, required reason, validation, and idempotency. |
| Policy history and runtime outcome links | timeline/history, `AuditTimelineSurface.tsx`, `TraceLinkList.tsx` | Shows direct changes plus practical runtime outcome traces without protected-data leakage. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | DTOs and errors must preserve default/override/effective values, denials, validation states, and trace refs. |

## Validation evidence

- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`

## Gaps / caveats

- Governance/Policy frontend realization must stay in `frontend/src/workstream/**`; removed screen modules are not reference or fallback architecture.
- Existing UI tests/surfaces may still reflect older proposal/diff/simulation intent and must be reconciled before claiming current alignment.
