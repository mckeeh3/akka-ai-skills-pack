# Task: Add protected Organization Admin API

## Objective

Expose SaaS Owner Organization management through protected Admin API endpoints using Organization-facing DTOs and backend Tenant authorization.

## Required reads

- `AGENTS.md`
- `specs/saas-owner-organization-admin/README.md`
- `specs/saas-owner-organization-admin/backlog/01-saas-owner-organization-admin-build-backlog.md`
- `specs/saas-owner-organization-admin/tasks/03-api/01-add-organization-admin-api.md`
- app-description/API contracts updated by `TASK-SOOA-01-001`
- backend service implemented by `TASK-SOOA-02-001`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/main/java/ai/first/application/foundation/identity/StarterSecurityComponents.java`
- existing AdminEndpoint/UserAdmin tests under `src/test/java/ai/first/application/coreapp/useradmin/`

## Skills

- `akka-http-endpoint-jwt`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`

## Expected outputs

- Protected routes for organization list/detail/create/rename/suspend/reactivate.
- Browser-safe Organization DTOs.
- API/endpoint tests for success and denial cases.
- Queue update.

## Required checks

- `git diff --check`
- targeted endpoint/API tests
- `mvn test` if shared endpoint/security paths change materially

## Done criteria

- Endpoints require JWT/authenticated selected context and backend SaaS Owner authorization.
- Browser DTOs use Organization terminology and do not leak application data, provider secrets, hidden counts, or raw authority internals.
- Forbidden/not-found behavior is safe for unsupported roles and hidden targets.
- Mutating endpoints require idempotency keys and return auditable trace/correlation references.
- Changes and queue update are committed.
