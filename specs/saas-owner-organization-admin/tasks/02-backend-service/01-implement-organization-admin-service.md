# Task: Implement backend Organization Admin service

## Objective

Add backend repository and service support for SaaS Owner Organization management while preserving internal Tenant isolation and audit semantics.

## Required reads

- `AGENTS.md`
- `specs/saas-owner-organization-admin/README.md`
- `specs/saas-owner-organization-admin/conversation-capture.md`
- `specs/saas-owner-organization-admin/backlog/01-saas-owner-organization-admin-build-backlog.md`
- `specs/saas-owner-organization-admin/tasks/02-backend-service/01-implement-organization-admin-service.md`
- app-description files changed by `TASK-SOOA-01-001`
- `src/main/java/ai/first/domain/foundation/identity/Tenant.java`
- `src/main/java/ai/first/domain/foundation/identity/IdentityRepositoryState.java`
- `src/main/java/ai/first/domain/foundation/identity/FoundationRole.java`
- `src/main/java/ai/first/application/foundation/identity/IdentityRepository.java`
- `src/main/java/ai/first/application/foundation/identity/DurableIdentityRepositoryEntity.java`
- `src/main/java/ai/first/application/foundation/identity/AkkaIdentityRepository.java`
- `src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java`
- `src/test/java/ai/first/application/foundation/identity/LocalDemoIdentityRepository.java`

## Skills

- `capability-first-backend`
- `akka-key-value-entities`
- `akka-kve-domain-modeling`
- `akka-kve-unit-testing`

## Expected outputs

- Tenant/Organization list support in identity repository state and adapters.
- `SaasOwnerOrganizationAdminService` or equivalent focused backend service.
- Focused service tests covering authorization, lifecycle, idempotency/no-op, audit, and safe denials.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend tests for identity repository and Organization Admin service
- broader `mvn test` if shared identity/domain behavior changes materially

## Done criteria

- SaaS Owner selected context with `saas_owner.tenant.read/manage` can list/read/create/rename/suspend/reactivate Organizations through backend service methods.
- Tenant Admin and Customer Admin are denied safely.
- Missing capability and hidden target denials are audited and browser-safe.
- Mutations require idempotency keys and handle no-op/replay safely.
- No tenant/customer app data, provider secrets, or billing-derived authority are exposed.
- Changes and queue update are committed.
