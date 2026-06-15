# Realization: API contracts for User Admin

Capability: `user-and-access-administration`.

## Browser/API evidence

| Capability / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `user_admin.view_overview` / `search-user-directory` | `browser-tool`, `agent-tool` read | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `UserDirectoryView.java`; frontend `HttpWorkstreamApiClient.ts` | Produce `surface-user-admin-dashboard` with scoped attention, readiness, recent activity, redaction, correlation, trace refs, and SaaS Owner branch actions only when authorized. |
| `saas_owner.admin.*` / `manage-saas-owner-admins` | `browser-tool`; agent prepares human-confirmed payloads only | Admin API/workstream actions for SaaS Owner Admin directory, invitation, role/status lifecycle | SaaS Owner Admin list/invite/manage surfaces; app-owner selected context, last-owner-admin/self-action guardrails, idempotency, invitation/outbox delivery, safe denials, and audit events. |
| `saas_owner.organization.*` / `manage-organizations` | `browser-tool`; agent prepares human-confirmed payloads only | `AdminEndpoint.java`, `SaasOwnerOrganizationAdminService.java`, identity repositories | Organization directory/detail/create/rename/suspend/reactivate surfaces; internal Tenant mapping, idempotency, no tenant app-data/support/billing authority, safe denials, and audit events. |
| `saas_owner.organization_admin.*` / `manage-organization-admins` | `browser-tool`; agent prepares human-confirmed payloads only | Admin API/workstream actions plus invitation/user-admin services scoped to selected Organization/Tenant | Organization Admin list/invite/detail/manage surfaces; target Organization exists, `TENANT_ADMIN` role validation, no `SAAS_OWNER_ADMIN` escalation, last-organization-admin guardrails, idempotency, invitation/outbox delivery, safe denials, and audit events. |
| `user_admin.list_members` / `user_admin.read_user_account` | `browser-tool`, `agent-tool` read | `AdminEndpoint.java`, `UserAdminService.java`, `UserDirectoryView.java`; identity repositories/views | Scoped search/list/detail with backend filters, pagination, no hidden counts, visible create/invite action when allowed, backend-authored row target surface metadata for state-aware selection, detail task entry points, and safe denials. |
| `user_admin.invite_user/resend_invitation/revoke_invitation/acceptance_status.read` | `browser-tool`; agent prepares human-confirmed payloads only | `AdminEndpoint.java`, foundation invitation/email services/views | Dedicated invitation create, detail, resend-confirmation, and revoke-confirmation surfaces; idempotent lifecycle, validation/no-op/conflict states, Resend/captured-outbox boundary, no raw tokens/secrets/email-only authorization. |
| `user_admin.update_member_status/preview_role_change/change_member_roles` | `browser-tool` with approval when risky | `AdminEndpoint.java`, `UserAdminService.java`, identity repositories | Dedicated membership lifecycle confirmation and role-preview surfaces; last-admin/self-disable/role-escalation guardrails, decision-card routing, idempotency, audit events, cross-scope denials. |
| `user_admin.support_access.*` | `browser-tool` with expiry/purpose/approval | `AdminEndpoint.java`, identity/support-access service evidence | Dedicated support-access grant/extend and revoke-confirmation surfaces; expiring scoped support access, active support grant checks, traced grant/revoke/extend and forbidden access behavior. |
| `user_admin.ask_agent` | `agent-tool` through governed runtime | UserAdmin agent/evidence tool runtime where present | Governed prompt/skill/reference/tool assembly, model policy, scoped evidence, provider/model fail-closed system messages and traces. |
| `user_admin.access_review.*` / `run-access-review` | `agent-tool`, `internal-tool`, human decision actions | access review worker/service/autonomous agent classes | Durable task lifecycle, progress/result/review surfaces, provider/model readiness, no autonomous access mutation. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/WorkstreamApiClient.ts` | Typed action and surface payloads with correlation ids, idempotency, authorization errors, and trace links. |

## Surface result mapping

API responses should render or update these typed User Admin surfaces: `surface-user-admin-dashboard`, `surface-user-admin-saas-owner-admins`, `surface-user-admin-saas-owner-admin-invitation-create`, `surface-user-admin-organization-directory`, `surface-user-admin-organization-detail`, `surface-user-admin-organization-create`, `surface-user-admin-organization-rename`, `surface-user-admin-organization-suspend-confirmation`, `surface-user-admin-organization-reactivate-confirmation`, `surface-user-admin-organization-admins`, `surface-user-admin-organization-admin-invitation-create`, `surface-user-admin-organization-admin-detail`, `surface-user-admin-users`, `surface-user-admin-user-detail`, `surface-user-admin-invitation-create`, `surface-user-admin-invitation-detail`, `surface-user-admin-invitation-resend-confirmation`, `surface-user-admin-invitation-revoke-confirmation`, `surface-user-admin-membership-status-confirmation`, `surface-user-admin-role-change-preview`, `surface-user-admin-support-access-grant`, `surface-user-admin-support-access-revoke-confirmation`, `surface-user-admin-access-review-task`, `surface-user-admin-identity-exception-review`, `decision-card`, Audit/Trace surfaces, `markdown-response`, and `surface-user-admin-system-message`.

## Validation evidence

- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`

## Gaps / caveats

- External Resend smoke proves provider integration only when configured; normal runtime must fail closed without secrets.
- Model-backed User Admin guidance and access-review tasks must fail closed when provider/model policy is unavailable; canned/model-less normal success is not acceptable runtime behavior.
