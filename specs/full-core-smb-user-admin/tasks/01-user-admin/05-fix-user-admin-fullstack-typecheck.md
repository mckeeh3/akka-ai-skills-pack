# Task: Fix User Admin fullstack typecheck blockers

## Objective

Fix the TypeScript issues surfaced by fullstack validation so the User Admin invitation/dashboard foundation can satisfy the starter runtime/API/UI validation gate.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-user-admin/README.md`
- `specs/full-core-smb-user-admin/conversation-capture.md`
- `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`
- `specs/full-core-smb-user-admin/source-boundary-notes.md`
- `specs/full-core-smb-user-admin/tasks/99-verification/01-verify-user-admin-readiness.md`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/actions.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DashboardSurface.tsx`

## Skills

- none; focused repository source-edit task

## In scope

- Resolve the `surface-request` `SurfaceActionIntent`/`SurfaceAction` type mismatch in the starter frontend without weakening governed action semantics.
- Add any missing typed field needed by existing fixture action metadata, such as shell-request metadata, only if it is already represented by runtime/fixture contracts.
- Add an explicit return type to `DashboardSurface`'s recursive `renderSurfaceValue` helper or otherwise make the helper typecheck without changing rendered semantics.
- Keep User Admin workstream surfaces backend-authoritative and avoid converting runtime behavior into fixture-only success.

## Out of scope

- New User Admin product behavior beyond fixing validation blockers.
- Member disable/reactivate, role mutation expansion, UserAdminAgent normal-runtime changes, or access-review worker implementation.
- Broad visual redesign.

## Expected outputs

- Updated frontend type definitions and/or fixture code under `templates/ai-first-saas-starter/frontend/src/workstream/`.
- Updated `specs/full-core-smb-user-admin/pending-tasks.md` marking this task done when checks pass.

## Required checks

- `git diff --check`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-user-admin-expertise.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-actions.contract.test.mjs src/api.contract.test.mjs`
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- The fullstack validation no longer fails on the `surface-request` action intent or `DashboardSurface` implicit recursive return type.
- Existing User Admin surface/action contract tests still pass.
- Task changes and queue update are committed.

## Commit message

`full-core-smb: fix user admin fullstack typecheck`
