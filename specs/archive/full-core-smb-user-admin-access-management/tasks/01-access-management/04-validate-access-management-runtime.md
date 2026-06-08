# Task: Validate User Admin access-management runtime slice

## Objective

Run integrated backend/frontend validation for the implemented User Admin access-management slice and append bounded blocker tasks if validation reveals gaps.

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

- Run targeted backend and frontend checks for the implemented access-management slice.
- Run or explicitly justify deferring `tools/validate-ai-first-saas-starter-fullstack.sh`.
- Inspect runtime/API/UI contract evidence for deterministic authority, denials, no-op/idempotency, traces, and frontend advisory-only behavior.
- Update queue with bounded blockers before verification if checks fail or runtime gaps remain.

## Out of scope

- Implementing broad new features beyond bounded fixes needed to make validation runnable.
- UserAdminAgent guidance.
- Access-review worker.

## Expected outputs

- Updated `specs/full-core-smb-user-admin-access-management/pending-tasks.md` with validation notes or follow-up tasks.
- Optional validation notes if useful for verification handoff.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "USERADMIN_UPDATE_MEMBER_STATUS|USERADMIN_PREVIEW_ROLE_CHANGE|USERADMIN_CHANGE_MEMBER_ROLES|user_admin\.role_change_preview\.v1|system_message|last-admin|self-disable|idempotency|trace-useradmin|blocked_provider_or_runtime|runtime validation" templates/ai-first-saas-starter specs/full-core-smb-user-admin-access-management --glob '!**/node_modules/**' --glob '!**/target/**'
tools/validate-ai-first-saas-starter-fullstack.sh
git diff --check
```

If fullstack validation cannot run in the local environment, record the exact blocker and append a bounded task if the blocker is in-scope.

## Done criteria

- Targeted backend/frontend checks pass or bounded blocker tasks are appended.
- Fullstack validation passes or an explicit in-scope blocker is queued.
- Queue is ready for terminal verification.
- Task changes and queue update are committed.

## Commit message

`full-core-smb: validate access management runtime`
