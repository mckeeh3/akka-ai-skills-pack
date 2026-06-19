# TASK-FCBAD-02-004: Repair Customer list/search filter parity

## Objective

Make Customer directory filtering/search behavior match the app-description and typed frontend API contract.

## Source finding

- `runtime-audit/foundation-customer-boundary-runtime-drift-audit.md#fcb-rd-02-apiadmincustomers-ignores-querystatus-filters-even-though-the-contract-is-listsearch`

## Required reads

- `AGENTS.md`
- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-drift-audit.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/api/HttpApiClient.ts`
- `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`
- relevant tests
- this task brief

## Implementation scope

- Read `query` and `status` query parameters in `GET /api/admin/customers` and pass them to `TenantCustomerAdminService.listCustomers`.
- Propagate backend-authored safe filter input through the workstream Customer directory refresh path where currently feasible.
- Preserve no-hidden-count behavior and safe empty states.
- Update frontend contract only if needed to align names/status values.

## Required tests

- API list returns only matching Customer rows for query filter.
- API list returns only active/suspended rows for status filter.
- Workstream directory refresh preserves safe query/status filter input if supported by the selected action request.
- Filtered empty state does not leak hidden Customer counts.

## Required checks

- `mvn -Dtest=ai.first.application.coreapp.workstream.WorkstreamServiceTest,ai.first.application.coreapp.useradmin.AdminEndpointIntegrationTest test`
- `npm --prefix frontend test -- --run frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `git diff --check`

## Done criteria

- API and workstream list/search behavior match the active app-description.
- Frontend typed API remains aligned with backend route behavior.
- Tests prove query/status filtering and safe empty behavior.
- Queue status and notes are updated and committed with code/test changes.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: list/search parity repair; no new attention item.
- Surface graph: `surface-user-admin-customer-directory` list-search.
- Governed-tool id and exposure: `manage-customers` browser-tool/API/workstream read.
- Capability id: `tenant.customer.list`.
- AuthContext / roles / tenant scope: Organization/Tenant Admin selected context; tenant-scoped Customer filtering.
- Akka substrate: HTTP endpoint + WorkstreamService + frontend API contract/tests.
- Audit/work trace requirements: list/read audit as currently implemented; no hidden/sibling leakage.
- Local validation path: focused backend/frontend tests and `git diff --check`.
