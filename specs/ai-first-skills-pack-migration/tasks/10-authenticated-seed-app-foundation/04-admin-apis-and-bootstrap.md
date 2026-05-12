# TASK-10-004: Implement admin APIs and bootstrap lifecycle

## Purpose

Implement the user, tenant, customer, role, status, bootstrap, and audit administration surface for the authenticated DCA seed app.

## Required reads

- `AGENTS.md`
- `docs/security-workos-auth-and-admin.md`
- `specs/ai-first-skills-pack-migration/sprints/10-authenticated-seed-app-foundation-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/10-authenticated-seed-app-foundation-build-backlog.md`
- security domain/components from `TASK-10-002`
- `/api/me` and authorization helper from `TASK-10-003`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/api/AdminUsersEndpoint.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/api/TenantAdminEndpoint.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/api/CustomerAdminEndpoint.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/application/AdminUserBootstrap.java`

## Scope

- Add admin APIs for listing/inviting users, assigning roles/scopes, disabling/activating users, and managing tenants/customers.
- Add idempotent startup admin bootstrap from backend-only environment variables.
- Add audit entries for privileged operations.
- Add tests for APP_ADMIN, tenant/customer scoped admins, privilege-escalation denial, cross-scope denial, disabled-user denial, and audit creation.

## Non-goals

- No frontend admin screens beyond API needs.
- No production email delivery unless implemented behind a stub boundary.
- No impersonation unless already explicitly accepted.

## Skills

- `akka-basic-user-admin`
- `akka-workos-user-auth`
- `akka-http-endpoints`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`

## Expected outputs

- Admin endpoint classes and request/response records.
- Bootstrap component/service.
- Authorization and audit integration tests.

## Required checks

- Run focused admin endpoint tests.
- Verify tenant/customer scope is checked on every list and mutation operation.

## Done criteria

- Seed app administrators can safely manage local accounts and scopes with auditable backend enforcement.
