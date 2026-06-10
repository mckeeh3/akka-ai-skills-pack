# Realization: API contracts for User Admin

Capability: `user-and-access-administration`.

## Browser/API evidence

| Capability / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `user_admin.view_overview` / `search-user-directory` | `browser-tool`, `agent-tool` read | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `UserDirectoryView.java`; frontend `HttpWorkstreamApiClient.ts` | Produce `surface-user-admin-dashboard` with scoped attention, readiness, recent activity, redaction, correlation, and trace refs. |
| `user_admin.list_members` / `user_admin.read_user_account` | `browser-tool`, `agent-tool` read | `AdminEndpoint.java`, `UserAdminService.java`, `UserDirectoryView.java`; identity repositories/views | Scoped search/list/detail with backend filters, pagination, no hidden counts, row/detail action availability, and safe denials. |
| `user_admin.invite_user/resend_invitation/revoke_invitation/acceptance_status.read` | `browser-tool`; agent prepares human-confirmed payloads only | `AdminEndpoint.java`, foundation invitation/email services/views | Idempotent invitation lifecycle, validation/no-op/conflict states, Resend/captured-outbox boundary, no raw tokens/secrets/email-only authorization. |
| `user_admin.update_member_status/preview_role_change/change_member_roles` | `browser-tool` with approval when risky | `AdminEndpoint.java`, `UserAdminService.java`, identity repositories | Last-admin/self-disable/role-escalation guardrails, decision-card routing, idempotency, audit events, cross-scope denials. |
| `user_admin.support_access.*` | `browser-tool` with expiry/purpose/approval | `AdminEndpoint.java`, identity/support-access service evidence | Expiring scoped support access, active support grant checks, traced grant/revoke/extend and forbidden access behavior. |
| `user_admin.ask_agent` | `agent-tool` through governed runtime | UserAdmin agent/evidence tool runtime where present | Governed prompt/skill/reference/tool assembly, model policy, scoped evidence, provider/model fail-closed system messages and traces. |
| `user_admin.access_review.*` / `run-access-review` | `agent-tool`, `internal-tool`, human decision actions | access review worker/service/autonomous agent classes | Durable task lifecycle, progress/result/review surfaces, provider/model readiness, no autonomous access mutation. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/WorkstreamApiClient.ts` | Typed action and surface payloads with correlation ids, idempotency, authorization errors, and trace links. |

## Surface result mapping

API responses should render or update these typed surfaces: `surface-user-admin-dashboard`, `surface-user-admin-users`, `surface-user-admin-invitation-detail`, `surface-user-admin-user-detail`, `surface-user-admin-role-change-preview`, `surface-user-admin-access-review-task`, `decision-card`, Audit/Trace surfaces, `markdown-response`, and `surface-user-admin-system-message`.

## Validation evidence

- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`

## Gaps / caveats

- External Resend smoke proves provider integration only when configured; normal runtime must fail closed without secrets.
- Model-backed User Admin guidance and access-review tasks must fail closed when provider/model policy is unavailable; canned/model-less normal success is not acceptable runtime behavior.
