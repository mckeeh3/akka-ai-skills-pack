# Realization: Frontend routes and surfaces for User Admin

Capability: `user-and-access-administration`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| User Admin functional-agent rail and workstream panel | `frontend/src/workstream/rail/**`, `frontend/src/workstream/shell/WorkstreamPanel.tsx` | Rail visibility does not grant backend authority. Default surface is `surface-user-admin-dashboard`. |
| Dashboard command center | `frontend/src/workstream/surfaces/DashboardSurface.tsx` | Renders `user_admin.dashboard.v1` attention cards, summary cards, recent activity, readiness, provider/model/outbox blockers, and safe action bars, including backend-authored Customer branch cards/actions only for Organization/Tenant Admin contexts. |
| Customer directory/detail/lifecycle and Customer Admin branch | `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`, `frontend/src/api/ApiClient.ts`, `frontend/src/api/HttpApiClient.ts`, `frontend/src/api/types.ts` | Renders Customer directory/detail/create/rename/suspend/reactivate and Customer Admin list/invite/detail/manage flows for `tenant.customer.*` and `tenant.customer_admin.*`. Browser actions such as `action-customer-list`, `action-customer-read`, `action-customer-create`, `action-customer-rename`, `action-customer-suspend`, `action-customer-reactivate`, `action-customer-admin-list`, `action-customer-admin-invite`, and `action-customer-admin-manage` submit to backend-authorized API/workstream contracts and never grant authority by route or client state. |
| Users directory/list-search | `frontend/src/workstream/surfaces/ListSearchSurface.tsx` | Renders `user_admin.users.v1` scoped rows, filters, pagination, table-to-card fallback, create/invite action when allowed, row action availability, backend-authored row target surface metadata, denial categories, and trace refs. Row/card activation opens the state-appropriate target surface such as user detail, invitation detail, role preview, access-review task, identity-exception review, or system message; list rendering must not mutate access inline. |
| User detail inspection | `frontend/src/workstream/surfaces/DetailEditSurface.tsx` or split show/inspection component | Renders `user_admin.user_detail.v1` as browser-safe inspection and task router with no raw provider data, tokens, or hidden memberships. |
| Invitation create/detail/resend/revoke | Form, workflow-status, and confirmation surface components | Renders `user_admin.invitation_create.v1`, `user_admin.invitation_detail.v1`, `user_admin.invitation_resend_confirmation.v1`, and `user_admin.invitation_revoke_confirmation.v1` with validation/no-op/conflict/outbox-blocked states and no raw tokens/secrets. |
| Membership lifecycle and role preview | Confirmation, `DecisionSurface.tsx`, `ActionFeedbackItem.tsx`, `SurfaceActionBar.tsx` | Renders `user_admin.membership_status_confirmation.v1` and `user_admin.role_change_preview.v1`; human approval/denial feedback remains backend-policy governed. |
| Support access task surfaces | Form/confirmation and decision surface components | Renders `user_admin.support_access_grant.v1` and `user_admin.support_access_revoke_confirmation.v1` with purpose, expiry, approval, idempotency, and trace states. |
| Access-review and identity-exception task surfaces | `WorkflowStatusSurface.tsx`, outcome/decision surface components | Renders `user_admin.access_review_task.v1` and `user_admin.identity_exception_review.v1`; worker output and identity recovery cannot directly mutate access outside deterministic User Admin capabilities. |
| Trace links and admin audit context | `frontend/src/workstream/stream/TraceLinkList.tsx`, `AuditTimelineSurface.tsx` | User Admin actions link to audit/work traces and reauthorize trace opens. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | DTOs and error mapping distinguish unauthorized, forbidden, validation, conflict, stale, provider/model/outbox blocked, and server errors. |

## Route/deep-link expectations

Routes and deep links reopen the User Admin functional agent, selected surface id, safe filter context, or typed target reference. They must not expose hidden resource ids, skip backend authorization, or define product meaning outside the workstream/surface contracts.

Deep links to Customer directory/detail/create/rename/lifecycle surfaces, Customer Admin list/invite/detail/manage surfaces, directory filters, user detail, invitation records/forms/confirmations, membership lifecycle confirmations, role previews, support-access forms/confirmations, access-review tasks, identity-exception reviews, decision cards, and audit evidence are reauthorized server-side. Hidden/not-found targets render `surface-user-admin-system-message` with safe recovery and no sibling-customer leakage.

## Validation evidence

- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-organization-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-user-admin-expertise.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`

## Gaps / caveats

- Legacy screen fixtures are not the primary runtime architecture unless later reconciliation reclassifies them.
- Surface rendering must use real backend API/realtime contracts for runtime completion; fixtures are only tests/examples.
