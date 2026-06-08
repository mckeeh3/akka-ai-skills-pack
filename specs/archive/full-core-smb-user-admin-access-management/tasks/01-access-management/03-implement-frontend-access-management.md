# Task: Implement frontend User Admin access-management surfaces

## Objective

Implement workstream-native frontend surfaces/actions for member status and role/capability management, backed by the deterministic backend action path.

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

- Align User Admin frontend action ids/capability ids with backend runtime actions.
- Render member status, role/capability summaries, role-change preview evidence, capability deltas, affected workstreams, policy/approval hints, last-admin/self-disable denials, no-op/idempotency feedback, and trace links.
- Preserve typed `system_message`, forbidden, stale, validation-error, blocked_provider_or_runtime, loading, empty, and responsive states.
- Update frontend contract tests for runtime-backed access-management behavior.

## Out of scope

- Backend service implementation except small DTO/type alignment fixes if required.
- UserAdminAgent guidance.
- Access-review worker.
- Page-first CRUD admin console expansion.

## Expected outputs

- Updated starter frontend files under `templates/ai-first-saas-starter/frontend/src/`.
- Updated starter frontend tests.
- Root `frontend/` synchronization only if touched-source mirroring is required.
- Updated `specs/full-core-smb-user-admin-access-management/pending-tasks.md`.

## Required checks

```bash
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "USERADMIN_UPDATE_MEMBER_STATUS|USERADMIN_PREVIEW_ROLE_CHANGE|USERADMIN_CHANGE_MEMBER_ROLES|user_admin\.role_change_preview\.v1|user_admin\.member_directory\.v1|system_message|last-admin|self-disable|idempotency|trace-useradmin|blocked_provider_or_runtime|table-to-card" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- User Admin surfaces show status and role/capability access-management evidence attractively inside the workstream shell.
- Frontend actions remain advisory and submit governed capability requests through the workstream action path.
- Denials, no-ops, idempotency, and trace links are visible and tested.
- Task changes and queue update are committed.

## Commit message

`full-core-smb: implement frontend access management`
