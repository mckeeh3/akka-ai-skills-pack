# TASK-FCSR-04-001: Complete User Admin structured surfaces

## Objective

Close the User Admin full-core surface/action readiness gap for users, invitations, roles/memberships, access review, support access, and admin audit at the selected scope.

## Required reads

- full-core readiness gap contract from `TASK-FCSR-01-001`
- completed invitation/auth task notes
- `app-description/12-workstreams/surface-contracts/02-user-admin-dashboard.md`
- `app-description/12-workstreams/surface-contracts/03-user-admin-user-list.md`
- `app-description/12-workstreams/surface-contracts/04-user-admin-user-account.md`
- `app-description/70-traceability/surface-to-capability-map.md`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/surfaces/**`
- User Admin backend/frontend tests

## Skills

- `akka-basic-user-admin`
- `akka-web-ui-forms-validation`
- `akka-web-ui-state-rendering`
- `akka-http-endpoint-testing`

## In scope

- Fill missing User Admin structured payload/action coverage identified by the gap contract.
- Ensure actions are backend-authoritative, idempotent where needed, audited, and tenant-scoped.
- Render safe loading/empty/error/forbidden/stale states.
- Add/update backend and frontend tests.

## Out of scope

- Managed-agent governance surfaces beyond User Admin references.
- Domain-specific user administration.

## Expected outputs

- Updated backend/frontend User Admin paths and tests.
- Updated app-description/readiness notes as needed.

## Required checks

- `git diff --check`
- focused backend User Admin/workstream tests
- `npm --prefix frontend test -- --run` or targeted frontend tests
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build` if frontend production output changes

## Done criteria

- User Admin surfaces/actions meet the full-core scope in the gap contract through real backend/API/UI paths.
- Changes and queue update are committed.

## Commit message

`full-core-ready: complete user admin surfaces`
