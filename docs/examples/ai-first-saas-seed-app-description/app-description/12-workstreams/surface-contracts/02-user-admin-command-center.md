# Surface Contract: User Admin Command Center

- surface-id: `user-admin-command-center`
- type/version: dashboard+table+forms/v1
- functional agents: User Admin
- payload schema:
  - invitation queue, user directory rows, membership rows, access-review items, support-access grants, admin audit excerpts, agent recommendations
- allowed actions:
  - create/resend/revoke invitation → `secure-tenant-user-foundation`
  - assign/replace/remove membership role → `secure-tenant-user-foundation`
  - suspend/reactivate user or membership → `secure-tenant-user-foundation`
  - approve/reject risky access decision → `governance-decisions-audit`
- states:
  - loading tables, empty tenant, failed invite delivery, expiring support access, last-admin risk, forbidden role action, stale queue stream
- auth/security:
  - every row and action is tenant/customer scoped; support access requires explicit grant and audit.
- rendering tests:
  - invitation lifecycle controls, denied cross-tenant role change, last-admin warning, audit link rendering, mobile table-to-card fallback.
