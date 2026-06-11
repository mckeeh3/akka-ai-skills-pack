# Task: Wire Organization Admin UI to runtime Admin API

## Objective

Make the production browser Organization Admin surface execute list/read/create/rename/suspend/reactivate through the real protected `/api/admin/organizations` API path instead of only fixture workstream action results.

## Required reads

- `AGENTS.md`
- `specs/saas-owner-organization-admin/README.md`
- `specs/saas-owner-organization-admin/verification.md`
- `specs/saas-owner-organization-admin/tasks/05-ui-runtime/01-wire-organization-admin-ui-to-runtime-api.md`
- `app-description/55-ui/frontend-api-contracts.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- `frontend/src/api/ApiClient.ts`
- `frontend/src/api/HttpApiClient.ts`
- `frontend/src/workstream/surfaces/OrganizationAdminSurface.tsx`
- `frontend/src/workstream/**`

## Skills

- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-testing`

## Expected outputs

- Production Organization Admin UI/workstream action handling that calls the typed `AdminClient` methods for Organization list/read/create/rename/suspend/reactivate.
- Safe rendering of API success, validation, no-op, forbidden, hidden/not-found, stale/conflict, and failure states.
- Focused frontend tests or contract tests proving the production path is not fixture-only.
- A cheap automated or documented local Akka-hosted UI/API smoke path for the Organization Admin surface.
- Queue update.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build`
- Targeted backend/API tests if endpoint contracts change

## Done criteria

- Browser Organization Admin actions invoke the real protected Admin API/client path in production mode.
- Fixture-only workstream action behavior remains test/demo-scoped and is not counted as normal runtime completion.
- Selected context/JWT handling, backend authorization, browser-safe redaction, idempotency keys, validation/no-op/denial states, and trace/correlation display are preserved.
- Local runtime or documented smoke evidence demonstrates the intended UI-to-API path at the selected scope.
- Changes and queue update are committed.
