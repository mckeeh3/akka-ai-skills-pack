# TASK-FC5-04-001: Implement User Admin full-core vertical

## Objective

Implement User Admin full-core surfaces and capabilities for user directory, invitations, memberships, roles/scopes, access review, and admin audit visibility.

## Required reads

- `specs/full-core-five-workstreams/full-core-contract-matrix.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- relevant starter security/admin backend and frontend files

## Expected outputs

- User Admin dashboard, user table/search, user detail, invitation form/list, membership/role actions, access-review surfaces.
- Governed capabilities/governed-tools for list/search/detail, invite/resend/revoke, role updates, disable/enable, access review, and audit reads.
- Backend authorization, tenant isolation, idempotency, Resend/outbox boundary where applicable, audit/work traces.
- UserAdminAgent expertise/tool-boundary updates for explaining/invoking allowed capabilities.
- Frontend rendering/action tests and backend tests.

## Checks

- `mvn test`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- local smoke path for list/invite/access review
- `git diff --check`

## Done criteria

User Admin supports real protected administration workflows through typed surfaces/actions and governed backend capabilities.
