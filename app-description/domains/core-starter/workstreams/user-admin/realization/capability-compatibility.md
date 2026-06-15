# User Admin capability compatibility mapping

User Admin app-description uses product-facing capability ids as the authoritative names for new surfaces, actions, tests, and traces. Runtime may still accept older foundation aliases while the starter app preserves backward-compatible fixtures and generated clients.

## Canonical capability families

| Canonical family | Runtime aliases accepted during compatibility window | Notes |
|---|---|---|
| `user_admin.view_overview` | `USERADMIN_VIEW_OVERVIEW`, `secure-tenant-user-foundation` | Dashboard overview/read projection only. |
| `user_admin.list_members` | `USERADMIN_LIST_MEMBERS`, `tenant.user.read` | Scoped User branch list/search/read. |
| `user_admin.read_user_account` | `USERADMIN_LIST_MEMBERS`, `tenant.user.read` | User detail inspection/task-router surfaces. |
| `user_admin.invite_user` | `USERADMIN_SEND_INVITATION`, `tenant.invitation.manage` | Dedicated invitation create surfaces. |
| `user_admin.resend_invitation` | `USERADMIN_RESEND_INVITATION`, `tenant.invitation.manage` | Invitation resend confirmation. |
| `user_admin.revoke_invitation` | `USERADMIN_REVOKE_INVITATION`, `tenant.invitation.manage` | Invitation revoke confirmation. |
| `user_admin.update_member_status` | `USERADMIN_UPDATE_MEMBER_STATUS`, `tenant.user.manage` | Membership/account lifecycle confirmation. |
| `user_admin.preview_role_change` | `USERADMIN_PREVIEW_ROLE_CHANGE`, `tenant.role.manage` | Role/capability preview decision surface. |
| `user_admin.change_member_roles` | `USERADMIN_CHANGE_MEMBER_ROLES`, `tenant.role.manage` | Role mutation after preview/approval. |
| `saas_owner.organization.*` | `saas_owner.tenant.read`, `saas_owner.tenant.manage` | Browser/API language is Organization; backend isolation object is Tenant. |
| `tenant.customer.*` | `tenant.customer.manage` | Tenant Admin Customer lifecycle; concrete `list/read/create/rename/suspend/reactivate` capabilities are now emitted and enforced. |
| `tenant.customer_admin.*` | `tenant.customer.manage` | Customer Admin bootstrap/management; concrete `list/invite/manage` capabilities are now emitted and enforced. |

## Enforcement rule

Frontend visibility is advisory. Backend actions must check selected `AuthContext`, canonical capability ids or documented aliases, target tenant/customer ownership, idempotency, approval policy, audit/work trace emission, and redaction before returning a surface.

## Cleanup rule

New app-description and implementation work should prefer canonical lowercase product ids. Uppercase `USERADMIN_*` and broad `secure-tenant-user-foundation` strings are compatibility aliases only and should not be introduced for new product meaning.
