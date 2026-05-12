# TASK-10-002: Implement local account, tenant, customer, role, and audit domain

## Purpose

Create the reusable backend security domain for the authenticated DCA seed app.

## Required reads

- `AGENTS.md`
- `docs/security-workos-auth-and-admin.md`
- `specs/ai-first-skills-pack-migration/sprints/10-authenticated-seed-app-foundation-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/10-authenticated-seed-app-foundation-build-backlog.md`
- `specs/ai-first-skills-pack-migration/tasks/10-authenticated-seed-app-foundation/01-update-dca-auth-security-description.md`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/domain/Role.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/domain/RoleAssignment.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/domain/UserAccount.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/application/UserAccountEntity.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/application/AdminAuditEntryEntity.java`

## Scope

- Add local account, role assignment, tenant, customer, status, bootstrap, and audit domain records.
- Add Akka entities for local accounts, tenants/customers, and audit entries using KVE/ESE based on audit needs.
- Add validation, idempotent state transitions, and unit tests.

## Non-goals

- No HTTP endpoints.
- No WorkOS token validation.
- No frontend implementation.

## Skills

- `akka-key-value-entities`
- `akka-kve-domain-modeling`
- `akka-kve-application-entity`
- `akka-kve-unit-testing`
- `akka-event-sourced-entities` if audit-grade history is implemented with ESE

## Expected outputs

- Security/account domain records.
- Local account, tenant/customer, and audit application components.
- Focused unit tests.

## Required checks

- Run focused security domain/entity tests.
- Verify repeated bootstrap/invite/role updates are idempotent or return documented conflicts.

## Done criteria

- Local Akka state can authorize future APIs independently from frontend state or JWT role claims.
