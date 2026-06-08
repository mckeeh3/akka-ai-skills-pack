# TASK-FCSR-04-001: Complete User Admin structured surfaces

## Objective

Close the User Admin full-core surface/action readiness gap for users, invitations, roles/memberships, access review, support access, and admin audit at the selected scope.

## Required reads

- full-core readiness gap contract from `TASK-FCSR-01-001`
- completed invitation/auth task notes
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/realization/traceability.md` and workstream `realization/` files
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
