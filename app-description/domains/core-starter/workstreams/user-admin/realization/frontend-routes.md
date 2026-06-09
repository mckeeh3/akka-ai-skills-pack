# Realization: Frontend routes and surfaces for User Admin

Capability: `user-and-access-administration`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| User Admin functional-agent rail and workstream panel | `frontend/src/workstream/rail/**`, `frontend/src/workstream/shell/WorkstreamPanel.tsx` | Rail visibility does not grant backend authority. Default surface is `surface-user-admin-dashboard`. |
| Dashboard command center | `frontend/src/workstream/surfaces/DashboardSurface.tsx` | Renders `user_admin.dashboard.v1` attention cards, summary cards, recent activity, readiness, provider/model/outbox blockers, and safe action bars. |
| Member directory/list-search | `frontend/src/workstream/surfaces/ListSearchSurface.tsx` | Renders `user_admin.member_directory.v1` scoped rows, filters, pagination, table-to-card fallback, row action availability, denial categories, and trace refs. |
| Invitation panel and user account detail | `frontend/src/workstream/surfaces/DetailEditSurface.tsx`, `WorkflowStatusSurface.tsx` | Renders `user_admin.invitation_panel.v1` and `user_admin.user_account.v1` with validation/no-op/conflict/blocked states and no raw tokens/secrets. |
| Role preview, risky access decision cards, and action feedback | `DecisionSurface.tsx`, `ActionFeedbackItem.tsx`, `SurfaceActionBar.tsx` | Human approval/denial feedback remains backend-policy governed. |
| Access-review task progress/result | `WorkflowStatusSurface.tsx`, outcome/decision surface components | Renders durable worker status/result/review without direct mutation by worker output. |
| Trace links and admin audit context | `frontend/src/workstream/stream/TraceLinkList.tsx`, `AuditTimelineSurface.tsx` | User Admin actions link to audit/work traces and reauthorize trace opens. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | DTOs and error mapping distinguish unauthorized, forbidden, validation, conflict, stale, provider/model/outbox blocked, and server errors. |

## Route/deep-link expectations

Routes and deep links reopen the User Admin functional agent, selected surface id, safe filter context, or typed target reference. They must not expose hidden resource ids, skip backend authorization, or define product meaning outside the workstream/surface contracts.

Deep links to directory filters, user account detail, invitation records, access-review tasks, decision cards, and audit evidence are reauthorized server-side. Hidden/not-found targets render `surface-user-admin-system-message` with safe recovery.

## Validation evidence

- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-user-admin-expertise.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`

## Gaps / caveats

- Legacy screen fixtures are not the primary runtime architecture unless later reconciliation reclassifies them.
- Surface rendering must use real backend API/realtime contracts for runtime completion; fixtures are only tests/examples.
