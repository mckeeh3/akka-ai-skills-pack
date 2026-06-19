# User Admin capability and action normalization

User Admin app-description uses product-facing capability ids as the authoritative names for current surfaces, actions, tests, and traces. Older foundation aliases and shorthand action ids are retired and must not be introduced into new runtime payloads, fixtures, generated clients, or tests.

## Canonical capability families

| Canonical family | Retired names / internal mappings | Notes |
|---|---|---|
| `user_admin.view_overview` | retired: `USERADMIN_VIEW_OVERVIEW`, `secure-tenant-user-foundation` | Dashboard overview/read projection only. Stale retired ids are rejected or translated internally without being emitted. |
| `user_admin.list_members` | retired: `USERADMIN_LIST_MEMBERS`; internal mapping: `tenant.user.read` | Scoped User branch list/search/read. |
| `user_admin.read_user_account` | retired: `USERADMIN_LIST_MEMBERS`; internal mapping: `tenant.user.read` | User detail inspection/task-router surfaces. |
| `user_admin.invite_user` | retired: `USERADMIN_SEND_INVITATION`; internal mapping: `tenant.invitation.manage` | Dedicated invitation create surfaces. |
| `user_admin.resend_invitation` | retired: `USERADMIN_RESEND_INVITATION`; internal mapping: `tenant.invitation.manage` | Invitation resend confirmation. |
| `user_admin.revoke_invitation` | retired: `USERADMIN_REVOKE_INVITATION`; internal mapping: `tenant.invitation.manage` | Invitation revoke confirmation. |
| `user_admin.update_member_status` | retired: `USERADMIN_UPDATE_MEMBER_STATUS`; internal mapping: `tenant.user.manage` | Membership/account lifecycle confirmation. |
| `user_admin.preview_role_change` | retired: `USERADMIN_PREVIEW_ROLE_CHANGE`; internal mapping: `tenant.role.manage` | Role/capability preview decision surface. |
| `user_admin.change_member_roles` | retired: `USERADMIN_CHANGE_MEMBER_ROLES`; internal mapping: `tenant.role.manage` | Role mutation after preview/approval. |
| `saas_owner.organization.*` | internal mapping: `saas_owner.tenant.read`, `saas_owner.tenant.manage` | Browser/API language is Organization; backend isolation object is Tenant. Internal mappings are not emitted as browser/API capability ids. |
| `tenant.customer.*` | internal mapping: `tenant.customer.manage` | Tenant Admin Customer lifecycle; concrete `list/read/create/rename/suspend/archive/reactivate` capabilities are emitted and enforced. |
| `tenant.customer_admin.*` | internal mapping: `tenant.customer.manage` | Customer Admin bootstrap/management; concrete `list/invite/manage` capabilities are emitted and enforced. |

## Canonical Customer action ids

Current product surface contracts use these Customer branch action ids as authoritative for new work: `action-user-admin-show-customers`, `action-customer-read`, `action-open-customer-create`, `action-submit-customer-create`, `action-open-customer-rename`, `action-submit-customer-rename`, `action-open-customer-suspend`, `action-customer-suspend`, `action-open-customer-reactivate`, `action-customer-reactivate`, `action-user-admin-show-customer-admins`, `action-open-customer-admin-invitation-create`, and `action-customer-admin-invite`.

Retired action ids are not accepted as current product actions: `action-customer-create`, `action-customer-rename`, `action-open-customer-suspend-confirmation`, `action-open-customer-reactivate-confirmation`, `action-customer-list`, `action-customer-admin-list`, and `action-customer-admin-manage`. Stale clients submitting them receive safe unsupported-action/system-message results and trace evidence; implementations must emit the canonical action ids above.

## Enforcement rule

Frontend visibility is advisory. Backend actions must check selected `AuthContext`, canonical capability ids, target tenant/customer ownership, idempotency, approval policy, audit/work trace emission, and redaction before returning a surface. Retired capability or action ids must not be translated into successful product behavior at the browser/API boundary; stale clients receive safe unsupported-action or system-message results unless a backend-only internal mapping is explicitly listed above.

## Cleanup rule

New app-description and implementation work must use canonical lowercase product ids. Uppercase `USERADMIN_*` and broad `secure-tenant-user-foundation` strings are retired and should not be introduced for new product meaning.

## Unsafe-assumption rejection

- Do not infer Customer CRM, customer-success, sales, support-case, billing, entitlement, procurement, customer-intelligence, or timer-reminder behavior from the foundation `Customer` boundary, old endpoint names, archived specs, or test fixtures.
- Do not infer Organization/Tenant application-data authority from SaaS Owner, Organization Admin, or Customer Admin management surfaces; admin surfaces manage foundation identity/access boundaries only.
- Do not infer provider/outbox/model success from seeded tests or local fixtures. Invitation delivery, agent guidance, access review, and identity recovery must use the governed runtime path or fail closed with trace evidence.
