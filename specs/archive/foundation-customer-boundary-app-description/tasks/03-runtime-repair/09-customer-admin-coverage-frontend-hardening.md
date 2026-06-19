# TASK-FCBAD-02-005: Harden Customer Admin backend and frontend coverage

## Objective

Add coverage for Customer lifecycle and Customer Admin branch behavior required by app-description after the high-risk backend/workstream repairs have landed.

## Source finding

- `runtime-audit/foundation-customer-boundary-runtime-drift-audit.md#fcb-rd-07-customer-admin-listmanage-api-behavior-is-under-tested`

## Required reads

- `AGENTS.md`
- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-drift-audit.md`
- `app-description/domains/core-starter/workstreams/user-admin/tests/coverage.md`
- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`
- this task brief

## Implementation scope

- Add or expand tests for Customer lifecycle and Customer Admin list/invite/role/status behavior.
- Add frontend contract tests proving backend-authored Customer target propagation, role-safe options, redaction, and no client-side authority for Customer Admin branch surfaces.
- Prefer tests over new runtime behavior unless a small uncovered bug is discovered; if a substantial new bug appears, update the queue rather than folding broad repairs into this coverage task.

## Required test coverage

- Customer lifecycle API: list/read/create/rename/suspend/reactivate, idempotency, safe denial, redaction.
- Customer Admin API: list/invite/role/status, role escalation denial, suspended Customer denial, target Customer proof.
- Workstream: Customer detail -> Customer Admin list/invite/detail/manage branch routing.
- Frontend: scoped admin renderer submits backend-authored `customerId`, presents role-safe options, and does not infer authority.

## Required checks

- `mvn -Dtest=ai.first.application.coreapp.workstream.WorkstreamServiceTest,ai.first.application.coreapp.useradmin.AdminEndpointIntegrationTest test`
- `npm --prefix frontend test -- --run frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `git diff --check`

## Done criteria

- Tests cover the app-description Customer boundary obligations touched by prior repair tasks.
- No material coverage gaps remain for Customer Admin branch target, role, suspended-state, redaction, and authority boundaries.
- Queue status and notes are updated and committed with test changes.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: test hardening; no runtime attention item.
- Surface graph: Customer lifecycle and Customer Admin branch surfaces.
- Governed-tool id and exposure: `manage-customers`, `manage-customer-admins`.
- Capability id: `tenant.customer.*`, `tenant.customer_admin.*`.
- AuthContext / roles / tenant scope: Organization/Tenant Admin and Customer Admin denial variants.
- Akka substrate: endpoint/workstream/frontend contract tests.
- Audit/work trace requirements: tests assert safe trace/redaction evidence where available.
- Local validation path: focused Maven and npm tests plus `git diff --check`.
