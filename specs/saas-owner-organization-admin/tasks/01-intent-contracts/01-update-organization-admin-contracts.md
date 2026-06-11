# Task: Update Organization Admin intent and contracts

## Objective

Update app-description and API contract artifacts so SaaS Owner Organization Admin semantics are explicit before implementation.

## Required reads

- `AGENTS.md`
- `app-description/app.md`
- `app-description/global/roles/foundation-roles.md`
- `app-description/global/policies/foundation-security-and-governance.md`
- `app-description/55-ui/frontend-api-contracts.md`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `app-description/domains/core-starter/workstreams/user-admin/access.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `specs/saas-owner-organization-admin/README.md`
- `specs/saas-owner-organization-admin/conversation-capture.md`
- `specs/saas-owner-organization-admin/backlog/01-saas-owner-organization-admin-build-backlog.md`

## Skills

- `core-saas-foundation`
- `app-description-auth-security`
- `app-description-capability-modeling`
- `app-description-surface-modeling`
- `app-description-test-specification`

## Expected outputs

- App-description updates for SaaS Owner Admin Organization management.
- Frontend API contract updates for organization routes/DTOs.
- Updated pending queue if implementation tasks need scope adjustment.

## Required checks

- `git diff --check`
- focused `rg -n "SaaS Owner|Organization|organization|saas_owner|tenant.manage|support access|billing" app-description specs/saas-owner-organization-admin`

## Done criteria

- Organization-vs-Tenant terminology is explicit.
- SaaS Owner Organization list/read/create/rename/suspend/reactivate behavior is specified.
- Support-access and billing-boundary non-authority are specified.
- Backend authorization, audit/work trace, idempotency, no-op, denial, and frontend secret-boundary expectations are specified.
- Changes and queue update are committed.
