# Task: Implement backend User Admin access-management actions

## Objective

Implement deterministic backend runtime support for SMB member status and role/capability management through the workstream action path.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-user-admin-access-management/README.md`
- `specs/full-core-smb-user-admin-access-management/conversation-capture.md`
- `specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md`
- `specs/full-core-smb-user-admin-access-management/sprints/01-user-admin-access-management-sprint.md`
- `specs/full-core-smb-user-admin-access-management/backlog/01-user-admin-access-management-backlog.md`
- `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`
- `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`

## In scope

- `UserAdminService` status transition methods/result records for disable/reactivate or suspend/reactivate membership.
- `WorkstreamService` capability/action constants and dispatcher entries for `USERADMIN_UPDATE_MEMBER_STATUS`.
- Role preview/change surface/result enrichment for capability deltas, affected workstreams, policy/approval hints, last-admin impact, no-op/idempotency, and trace ids.
- Backend tests covering authorization, tenant/customer scope, last-admin, self-disable, disabled-user behavior, no-op/idempotency, trace/audit, and role/capability deltas.

## Out of scope

- UserAdminAgent guidance.
- Access-review worker.
- Enterprise custom role builder.
- Frontend visual polish except contract strings needed for backend response validation.

## Expected outputs

- Updated backend source under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/...`.
- Updated backend tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/`.
- Updated `specs/full-core-smb-user-admin-access-management/pending-tasks.md`.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
rg -n "USERADMIN_UPDATE_MEMBER_STATUS|USERADMIN_PREVIEW_ROLE_CHANGE|USERADMIN_CHANGE_MEMBER_ROLES|user_admin\.role_change_preview\.v1|last-admin|self-disable|idempotency|trace-useradmin|system_message|blocked_provider_or_runtime" templates/ai-first-saas-starter/backend/src templates/ai-first-saas-starter/backend/src/test --glob '!**/target/**'
git diff --check
```

## Done criteria

- Backend runtime action path can disable/reactivate membership at SMB scope with deterministic authority.
- Role preview/change exposes bounded role/capability evidence without model-owned authority.
- Guardrails for last-admin, self-disable, disabled actor, tenant/customer scope, no-op/idempotency, and audit/trace are tested.
- Task changes and queue update are committed.

## Commit message

`full-core-smb: implement backend access management`
