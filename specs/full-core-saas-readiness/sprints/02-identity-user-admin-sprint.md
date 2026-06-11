# Sprint 02: Identity, Invitation, and User Admin Foundation

## Objective

Close core SaaS identity and User Admin readiness gaps through protected backend capabilities, workstream surfaces, and local runtime validation.

## Scope

- WorkOS/AuthKit validation and fail-closed behavior.
- `/api/me` and selected AuthContext readiness proof.
- Invitation lifecycle with Resend production boundary and local/dev/test captured outbox.
- Invitation expiry/reminder lifecycle where feasible.
- User Admin users/invitations/roles/memberships/access-review/support-access/admin-audit surfaces and actions.
- Tenant Administration for SaaS Owner Tenant create/maintain and Tenant Admin bootstrap/maintenance, with Tenant Admins constrained to their own Tenant context.

## Acceptance criteria

- Missing provider secrets fail closed with actionable system-message/API behavior.
- Invitation lifecycle and user admin actions are tenant-scoped, idempotent, audited, and tested.
- User Admin structured surfaces use backend-derived data and protected capability actions.
- SaaS Owner Tenant Administration is separated from Tenant-scoped admin operations, with cross-tenant and privilege-escalation denials tested.
- Frontend tests/typecheck/build pass for changed UI surfaces.
