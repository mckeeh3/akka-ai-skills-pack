# Task Brief: Core Projections and Views

## Objective

Complete broader starter projections/views for invitations, user directory, admin audit, and governed-agent runtime/catalog state.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/01-durable-core-sprint.md`
- `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`
- `specs/full-core-smb-agent-admin/agent-admin-implementation-map.md`
- `specs/full-core-smb-audit-trace/audit-trace-implementation-map.md`
- `skills/akka-views/SKILL.md`
- `skills/akka-view-testing/SKILL.md`
- `skills/akka-http-endpoints/SKILL.md`

## In scope

- `InvitationView`, `UserDirectoryView`, `AdminAuditView`, governed-agent catalog/runtime projections, and extended foundation projection contracts where currently partial.
- Tenant/customer scope, redaction, stable ordering, pagination, and denial/non-leak behavior.
- Workstream/API payload integration without trusting caller-supplied ids.

## Checks

- `git diff --check`
- focused rendered-scaffold view and endpoint tests
- frontend tests/typecheck/build when DTOs or surfaces change
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Core projections are queryable through backend-authorized APIs/surfaces and covered by tenant-isolation, redaction, and ordering tests.
