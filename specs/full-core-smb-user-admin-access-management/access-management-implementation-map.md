# Access Management Implementation Map

## Discovery commands used

```bash
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(UserAdmin|user-admin|user_admin|Invitation|invitation|Member|member|Role|role|Capability|capabil|Status|status|Audit|audit|Trace|trace|Workstream|workstream|Surface|surface|Endpoint|Controller|frontend|test|api)"
rg -n "disable|reactivate|suspend|deactivate|last-admin|last admin|self-disable|self admin|role|capability|preview|change_member_roles|update_member_status|USERADMIN_|user_admin\.(update_member_status|preview_role_change|change_member_roles)|trace-useradmin|system_message|runtime validation" templates/ai-first-saas-starter --glob '!**/node_modules/**'
rg -n "userAdminCapabilities|USERADMIN_UPDATE_MEMBER_STATUS|action-useradmin-update-member-status|userAdminRoleCapabilityMatrixSurface|last-admin-risk|self-disable|disable|reactivate|role-change|capability delta|affected workstreams" templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts templates/ai-first-saas-starter/frontend/src/workstream/surfaces templates/ai-first-saas-starter/frontend/src/workstream/types -S
```

## Current source state

The starter already contains a partial deterministic access-management foundation:

- `UserAdminService` implements scoped member listing, role preview/change, `suspendMembership`, `disableAccount`, last-admin checks, role escalation checks, and audit events.
- `WorkstreamService` exposes role preview/change actions through `/api/workstream/actions` and structured User Admin surfaces.
- Frontend fixtures already name `USERADMIN_UPDATE_MEMBER_STATUS`, disable/reactivate-style fixture actions, role/capability matrix fixture material, denials, idempotency, trace, and blocked-runtime access-review states.

However, the runtime workstream path is not yet complete for the access-management slice:

- `WorkstreamService` has no `USERADMIN_UPDATE_MEMBER_STATUS` constant or backend-authoritative disable/reactivate action dispatcher.
- `UserAdminService.suspendMembership` has no no-op/idempotent result shape and no self-disable guardrail; `disableAccount` appears to look up an account with `findAccountByEmail(accountId)` and has no reactivation counterpart.
- Role preview/change returns a detail surface, but the surface does not yet carry capability deltas, affected workstreams, policy/approval hints, last-admin impact, or no-op/idempotency evidence as `user_admin.role_change_preview.v1`.
- The frontend fixture/action layer has richer status/role action vocabulary than the backend runtime path. Runtime-aligned action ids and result surfaces need to be made explicit so UI controls do not imply frontend authority.

## Backend implementation boundary

Primary files:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserDirectoryView.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/workstream/WorkstreamEndpoint.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationAndUserAdminServiceTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java`

Implementation scope:

1. Add backend capability/action constants and actions for `USERADMIN_UPDATE_MEMBER_STATUS` with runtime-aligned action ids:
   - `action-useradmin-disable-member` or alias existing fixture `action-suspend-membership` only if contracts/tests are updated consistently;
   - `action-useradmin-reactivate-member` or alias existing fixture `action-reactivate-membership`;
   - optionally keep `action-disable-account`/`action-reactivate-account` as fixture-only unless implemented end-to-end.
2. Extend `UserAdminService` with deterministic status transition result records:
   - validate selected `AuthContext`, target tenant/customer, active actor, target membership, capability, last-admin, self-disable, disabled-user behavior;
   - return no-op/idempotent results for repeated disable/reactivate requests;
   - emit audit events for allowed, denied, validation_error, and no-op outcomes;
   - do not let AI output or frontend action state grant authority.
3. Extend role preview/change result shaping:
   - preserve existing deterministic mutation path;
   - include capability delta, affected workstreams, policy/approval hints, last-admin impact, trace id, and no-op status in a `user_admin.role_change_preview.v1` surface/result payload;
   - keep SMB role set bounded to `FoundationRole` values already in the starter.
4. Extend User Admin directory/detail surfaces:
   - show membership status, account status where available, role/capability summary, last-admin risk, self-action denial hints, trace links, and safe `system_message`/denial text;
   - preserve invitation dashboard behavior from the previous slice.

Backend non-goals for this mini-project:

- no enterprise role builder;
- no UserAdminAgent request/response guidance implementation;
- no access-review worker implementation;
- no model-owned authorization/idempotency/mutation.

## Frontend implementation boundary

Primary files:

- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DetailEditSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/ListSearchSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DashboardSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DecisionSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/CapabilityActionButton.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/capabilityActionState.ts`
- `templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs`

Implementation scope:

1. Align frontend User Admin action ids and capabilities with backend runtime actions for member disable/reactivate and role preview/change.
2. Render member status and role/capability evidence in workstream-native surfaces:
   - status badges for active/suspended/disabled/pending;
   - capability delta and affected workstreams for role previews;
   - last-admin and self-disable denials as typed/safe states;
   - idempotency/no-op and trace links near action results.
3. Keep controls advisory: disabled buttons and hidden controls are convenience only; text must state backend denials are authoritative.
4. Preserve `system_message`, forbidden, stale, validation-error, blocked_runtime/provider, loading, empty, and responsive table-to-card states.
5. Synchronize root `frontend/` only if the repository convention or touched source mirror requires it; current discovered primary runtime target is the starter frontend.

## Validation boundary

Use targeted validation first:

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "USERADMIN_UPDATE_MEMBER_STATUS|USERADMIN_PREVIEW_ROLE_CHANGE|USERADMIN_CHANGE_MEMBER_ROLES|user_admin\.role_change_preview\.v1|user_admin\.member_directory\.v1|system_message|last-admin|self-disable|idempotency|trace-useradmin|blocked_provider_or_runtime|runtime validation" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

Run `tools/validate-ai-first-saas-starter-fullstack.sh` when a task claims broad local runtime/API/UI readiness for the implemented access-management slice or when backend/frontend behavior changes are broad enough to require generated-starter validation.

## Appended implementation tasks

- `TASK-FCSMB-UAM-01-002`: implement backend member status and role/capability runtime actions.
- `TASK-FCSMB-UAM-01-003`: implement frontend workstream surfaces/actions for member status and role/capability evidence.
- `TASK-FCSMB-UAM-01-004`: run integrated access-management validation and close or append blockers.

These tasks keep access management deterministic and SMB-scoped while preparing safe evidence for later UserAdminAgent guidance and access-review worker work.
