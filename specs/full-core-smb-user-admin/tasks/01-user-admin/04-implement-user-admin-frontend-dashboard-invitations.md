# Task: Implement User Admin frontend dashboard and invitation surface foundation

## Objective

Implement the frontend/workstream-shell half of the first User Admin directory and invitation dashboard foundation after the backend runtime contract exists.

This task should make the starter UI render backend-derived User Admin dashboard, member/invitation list, invitation action feedback, trace links, and safe system-message states through the workstream shell. It must not replace runtime behavior with fixture-only/demo data when the backend path is available.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-user-admin/README.md`
- `specs/full-core-smb-user-admin/conversation-capture.md`
- `specs/full-core-smb-user-admin/sprints/01-user-admin-vertical-contract-sprint.md`
- `specs/full-core-smb-user-admin/backlog/01-user-admin-vertical-contract-backlog.md`
- `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`
- `specs/full-core-smb-user-admin/source-boundary-notes.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- `templates/ai-first-saas-starter/frontend/src/api/WorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/api/ApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/api/HttpApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/api/types.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SurfaceRenderer.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DashboardSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/ListSearchSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/WorkflowStatusSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/CapabilityActionButton.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/capabilityActionState.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-expertise.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/api.contract.test.mjs`

## Skills

- none; focused repository source-edit task

## In scope

- Render backend-provided `user_admin.dashboard.v1`/User Admin dashboard data with readiness, attention cards, authorized next actions, recent activity/trace links, first-run and empty states.
- Render member/invitation list rows with scoped user/invitation fields, delivery/expiry/acceptance status, redaction state, trace references, and responsive table-to-card behavior.
- Render invitation create/resend/revoke action feedback using capability action metadata, idempotency/audit/trace affordances, and workflow/system-message surfaces for no-op, validation, forbidden, stale, outbox failure, and provider/outbox blocked states.
- Align fixture contract ids and runtime ids where the backend source task established a canonical mapping.
- Preserve workstream shell as the primary User Admin path through `HttpWorkstreamApiClient`; do not make page-first admin CRUD the only implemented path.
- Update frontend contract tests and any API client contract tests required by touched paths.

## Out of scope

- Backend source changes, except tiny DTO/type string updates required by already-implemented backend contract changes.
- Member disable/reactivate, role-change UI expansion beyond preserving existing display/action affordances.
- UserAdminAgent normal runtime UI beyond preserving request/response composer behavior.
- Durable access-review worker progress UI beyond existing fail-closed/blocked states.

## Expected outputs

- Updated frontend workstream sources/tests under `templates/ai-first-saas-starter/frontend/src/`.
- Optional synchronized fixture updates under `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/`.
- Updated `specs/full-core-smb-user-admin/pending-tasks.md` marking this task done when complete.

## Required checks

- `git diff --check`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-user-admin-expertise.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-actions.contract.test.mjs src/api.contract.test.mjs`
- `cd templates/ai-first-saas-starter/frontend && npm run build`
- `rg -n "USERADMIN_(VIEW_OVERVIEW|LIST_INVITATIONS|SEND_INVITATION|RESEND_INVITATION|REVOKE_INVITATION|LIST_MEMBERS)|user_admin\.dashboard\.v1|user_admin\.invitation_panel\.v1|system_message|blocked_provider_or_runtime|trace-useradmin|/api/workstream/actions|/api/admin/invitations" templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/backend/src/main/java --glob '!**/node_modules/**'`
- `tools/validate-ai-first-saas-starter-fullstack.sh` if this task changes runtime bootstrap/action contracts or claims local API/UI runtime readiness for the full first slice

## Done criteria

- User Admin workstream UI renders backend-derived dashboard/list/invitation action states at the implemented scope.
- UI action visibility remains advisory; backend-denied actions surface safe denial/system-message states with trace/correlation ids.
- Frontend fixtures do not imply access-review worker progress or model-backed UserAdminAgent success when backend/runtime/provider is missing.
- Required checks pass, or any skipped broad fullstack check is justified in the queue notes with a narrower passing validation set.
- Task changes and queue update are committed.

## Commit message

`full-core-smb: implement user admin frontend invitation foundation`
