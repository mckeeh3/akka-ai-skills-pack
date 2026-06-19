# Realization: Frontend routes and surfaces for User Admin

Capability: `user-and-access-administration`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| User Admin functional-agent rail and workstream panel | `frontend/src/workstream/rail/**`, `frontend/src/workstream/shell/WorkstreamPanel.tsx` | Rail visibility does not grant backend authority. Default surface is `surface-user-admin-dashboard`. |
| Dashboard command center | `frontend/src/workstream/surfaces/DashboardSurface.tsx` | Renders `user_admin.dashboard.v1` attention cards, summary cards, recent activity, readiness, provider/model/outbox blockers, and safe action bars, including backend-authored Customer branch cards/actions only for Organization/Tenant Admin contexts. |
| Customer directory/detail/lifecycle and Customer Admin branch | `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`, `frontend/src/api/ApiClient.ts`, `frontend/src/api/HttpApiClient.ts`, `frontend/src/api/types.ts` | Renders Customer directory/detail/create/rename/suspend/reactivate and Customer Admin list/invite/detail/manage flows for `tenant.customer.*` and `tenant.customer_admin.*`. Browser submissions use backend-authored canonical action ids: `action-user-admin-show-customers`, `action-customer-read`, `action-open-customer-create`, `action-submit-customer-create`, `action-open-customer-rename`, `action-submit-customer-rename`, `action-open-customer-suspend`, `action-customer-suspend`, `action-open-customer-reactivate`, `action-customer-reactivate`, `action-user-admin-show-customer-admins`, `action-open-customer-admin-invitation-create`, and `action-customer-admin-invite`. Route or client state never grants authority, and retired shorthand or compatibility names are not active frontend actions. |
| Users directory/list-search | `frontend/src/workstream/surfaces/ListSearchSurface.tsx` | Renders `user_admin.users.v1` scoped rows, filters, pagination, table-to-card fallback, create/invite action when allowed, row action availability, backend-authored row target surface metadata, denial categories, and trace refs. Row/card activation opens the state-appropriate target surface such as user detail, invitation detail, role preview, access-review task, identity-exception review, or system message; list rendering must not mutate access inline. |
| User detail inspection | `frontend/src/workstream/surfaces/UserAdminUserDetailSurface.tsx` backed by shared detail primitives | Renders `user_admin.user_detail.v1` as browser-safe inspection and task router with no raw provider data, tokens, or hidden memberships. Generic `DetailEditSurface.tsx` may provide primitives but is not the owning component. |
| Invitation create/detail/resend/revoke | `UserAdminInvitationCreateSurface.tsx`, `UserAdminInvitationDetailSurface.tsx`, `UserAdminInvitationResendConfirmationSurface.tsx`, `UserAdminInvitationRevokeConfirmationSurface.tsx` | Renders `user_admin.invitation_create.v1`, `user_admin.invitation_detail.v1`, `user_admin.invitation_resend_confirmation.v1`, and `user_admin.invitation_revoke_confirmation.v1` with validation/no-op/conflict/outbox-blocked states and no raw tokens/secrets. |
| Membership lifecycle and role preview | `UserAdminMembershipStatusConfirmationSurface.tsx`, `UserAdminRoleChangePreviewSurface.tsx`, plus shared `DecisionSurface.tsx`, `ActionFeedbackItem.tsx`, `SurfaceActionBar.tsx` primitives | Renders `user_admin.membership_status_confirmation.v1` and `user_admin.role_change_preview.v1`; human approval/denial feedback remains backend-policy governed. |
| Support access task surfaces | `UserAdminSupportAccessGrantSurface.tsx` and `UserAdminSupportAccessRevokeConfirmationSurface.tsx` | Renders `user_admin.support_access_grant.v1` and `user_admin.support_access_revoke_confirmation.v1` with purpose, expiry, approval, idempotency, and trace states. |
| Access-review and identity-exception task surfaces | `UserAdminAccessReviewTaskSurface.tsx` and `UserAdminIdentityExceptionReviewSurface.tsx` backed by shared `WorkflowStatusSurface.tsx`/decision primitives | Renders `user_admin.access_review_task.v1` and `user_admin.identity_exception_review.v1`; worker output and identity recovery cannot directly mutate access outside deterministic User Admin capabilities. |
| Trace links and admin audit context | `frontend/src/workstream/stream/TraceLinkList.tsx`, `AuditTimelineSurface.tsx` | User Admin actions link to audit/work traces and reauthorize trace opens. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | DTOs and error mapping distinguish unauthorized, forbidden, validation, conflict, stale, provider/model/outbox blocked, and server errors. |

## Route/deep-link expectations

Canonical route pattern: `/workstreams/user-admin?surface=<surfaceId>&ref=<opaqueSafeTargetRef>&mode=<surfaceMode>&filter=<serverFilterRef>`. `surface` is a declared surface id; `ref` and `filter` are backend-issued opaque references or short-lived signed handles, not raw account, tenant, customer, invitation, task, provider, or trace ids; `mode` is limited to declared surface modes. Route parsing posts a `surface_reopen` shell request through `/api/workstream/shell-requests` with `{ workstreamId: 'user-admin', surfaceId, targetRef?, surfaceMode?, serverFilterRef?, correlationId }` and renders only the returned authorized surface/system-message.

Deep links to Customer directory/detail/create/rename/lifecycle surfaces, Customer Admin list/invite/detail/manage surfaces, directory filters, user detail, invitation records/forms/confirmations, membership lifecycle confirmations, role previews, support-access forms/confirmations, access-review tasks, identity-exception reviews, decision cards, and audit evidence are reauthorized server-side through that shell request. Hidden/not-found targets render `surface-user-admin-system-message` with safe recovery and no sibling-customer leakage. Compatibility `/api/admin/**` URLs may be used for protected JSON/API smoke evidence but are not browser deep-link authority.

## Validation evidence

- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-organization-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-user-admin-expertise.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`

## Gaps / caveats

- Removed screen modules are not runtime architecture, fixture evidence, alternate paths, or future reconciliation targets.
- Surface rendering must use real backend API/realtime contracts for runtime completion; fixtures are only tests/examples.
