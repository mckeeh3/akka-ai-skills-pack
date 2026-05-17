# Sprint 2 Build Backlog: Secure SaaS Foundation Alignment

## Purpose

Make the current mandatory secure SaaS foundation explicit inside the DCA app description.

## Suggested harness task breakdown

### 1. Add secure tenant/user foundation capability

- task ID: `TASK-02-001`
- outputs: add `10-capabilities/01-secure-tenant-user-foundation.md` and update the capability index.

### 2. Align auth/security layer with foundation capability

- task ID: `TASK-02-002`
- outputs: update `40-auth-security/` so identity, authorization, agent permissions, data protection, and boundaries match current secure SaaS doctrine.

### 3. Add invitation, admin, support-access, and billing-boundary details

- task ID: `TASK-02-003`
- outputs: strengthen invitation lifecycle, admin audit, support access, subscription/billing boundary, and generation-slice notes.

## Done criteria

- DCA-specific automation sits on top of explicit Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, Invitation, AuthContext, `/api/me`, AdminAuditEvent, support-access, billing-boundary, and tenant-isolation semantics.
- No frontend-only or prompt-only security control is implied.
