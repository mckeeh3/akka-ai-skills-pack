# Source Boundary Notes: User Admin Starter

## Discovery commands used

- `find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(UserAdmin|user-admin|user_admin|Invitation|invitation|Member|member|Auth|auth|Tenant|tenant|Capability|capabil|Audit|audit|Trace|trace|Workstream|workstream|Surface|surface|Endpoint|Controller|Resource|frontend|test|api)"`
- `rg -n "USERADMIN_|surface-user-admin|user-admin-dashboard|action-(create-invitation|resend-invitation|revoke-invitation|invite-user)|/api/admin|/api/workstream|InvitationService|UserAdminService|InvitationView|UserDirectoryView|ResendEmailService|trace-useradmin|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'`

## Backend source boundaries

Primary source paths for the first directory/invitation dashboard slice:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
  - Current workstream API adapter owns bootstrap/items/surfaces/actions/messages/events.
  - Current initial User Admin bootstrap is still v0 `markdown_response`; structured `dashboardSurface`, `listSurface`, and `detailSurface` exist behind capability actions rather than initial bootstrap.
  - Current User Admin surface ids include `surface-user-admin-dashboard`, `surface-user-admin-list`, and `surface-user-admin-detail-admin`; fixture/frontend contract uses `user-admin-dashboard`, `user-admin-user-list`, and `user-admin-user-account` in several places.
  - Current `runAction` handles `action-invite-user`, role preview/change, and fail-closed access-review start. It does not yet handle first-slice resend/revoke invitation actions as backend-authoritative capability actions.
  - Current capability constants include overview/list/member/role/access-review ids, but invitation send/resend/revoke constants and per-action ids are incomplete in the backend path.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationService.java`
  - Deterministic create/resend/revoke/expire/accept/list behavior exists with scope checks, idempotency for create, outbox enqueue, delivery status, and audit events.
  - Next backend source task should expose resend/revoke through workstream/API surfaces without moving state transitions into model-backed agent behavior.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminService.java`
  - Deterministic scoped user listing, audit read, role preview/change, suspend/disable, last-admin checks, and audit events exist.
  - First slice should use list/search and audit/trace shaping only; member status/role mutation expansion remains later.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserDirectoryView.java`
  - Projection seam over `UserAdminService` for workstream surfaces.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationView.java`
  - Projection seam over `InvitationService` for workstream surfaces.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/ResendEmailService.java`
  - Provider/outbox delivery adapter already supports local capture and production fail-closed configuration checks.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/workstream/WorkstreamEndpoint.java`
  - Same-origin `/api/workstream/bootstrap`, `/api/workstream/surfaces/{surfaceId}`, `/api/workstream/actions`, `/api/workstream/messages`, `/api/workstream/invitations/accept`, and SSE events paths.
  - This is the preferred runtime path for structured User Admin dashboard/list/action surfaces.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/admin/AdminEndpoint.java`
  - Concrete protected `/api/admin/users`, `/api/admin/invitations`, and `/api/admin/audit-events` paths exist.
  - Frontend `HttpApiClient` currently posts to `/api/admin/users/invitations`, so any page-route work must either align the client or stay on `/api/workstream/actions` for the first slice.
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java`
  - Shared starter service registry wires `WorkstreamService`, `InvitationService`, `UserAdminService`, `UserDirectoryView`, `InvitationView`, and model/runtime seams.

Primary backend test paths:

- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java`
  - Existing tests cover v0 bootstrap, action context/idempotency, invite action, role preview/change, access-review fail-closed, message runtime, and persisted surfaces.
  - Next source task should add or revise tests for backend-authoritative `user_admin.dashboard.v1`/invitation panel behavior, capability ids, trace ids, resend/revoke actions, tenant/capability denial, idempotency/no-op, and provider/outbox fail-closed evidence.
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationAndUserAdminServiceTest.java`
  - Existing tests cover invitation lifecycle, browser acceptance, duplicate/resend/revoke/expiry, cross-tenant and role escalation denials, user admin listing/last-admin, Resend local capture and production fail-closed/sent/failure paths.
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/AdminEndpointIntegrationTest.java`
  - Existing tests cover protected users, invitation API idempotency, and audit/denial paths.

## Frontend source boundaries

Primary source paths for the first directory/invitation dashboard slice:

- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
  - Contains rich User Admin fixture/demo actions and surfaces, including dashboard/list/detail/role/access-review examples.
  - Fixture ids currently mix `user-admin-dashboard`/`user-admin-user-list` with backend `surface-user-admin-*` ids; source tasks should align runtime contract ids or explicitly map them.
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SurfaceRenderer.tsx`
  - Shared structured surface renderer entry point.
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DashboardSurface.tsx`
  - Renders dashboard cards/sections/next steps; first slice should ensure User Admin dashboard attention/readiness groups render without fixture-only assumptions.
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/ListSearchSurface.tsx`
  - Renders directory/invitation rows; first slice should ensure member and invitation row fields, trace links, empty/forbidden/stale states, and responsive table-to-card behavior are represented.
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/WorkflowStatusSurface.tsx`
  - Existing workflow/outbox status renderer; can carry invitation action results and provider/outbox blocked/failed states.
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/CapabilityActionButton.tsx`
  - Shows idempotency/audit/trace action affordances.
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/capabilityActionState.ts`
  - Maps action results to surface updates/feedback.
- `templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts`
  - Runtime workstream client for `/api/workstream/...` with selected context and correlation headers.
- `templates/ai-first-saas-starter/frontend/src/api/HttpApiClient.ts`
  - Page-route client currently calls `/api/admin/users/invitations`, which does not match `AdminEndpoint`'s `/api/admin/invitations`.
- `templates/ai-first-saas-starter/frontend/src/screens/admin/AdminUsersPage.tsx`
  - Older page-style admin slice exists; first workstream slice should not regress it, but the source task should keep workstream shell as the primary User Admin path.

Primary frontend test paths:

- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-vertical.contract.test.mjs`
  - String-level contract checks for User Admin surfaces, capabilities, denials, actions, and backend message path.
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-expertise.contract.test.mjs`
  - Existing test references `trace-user-admin-dashboard` and related User Admin expertise surface behavior.
- `templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs`
  - Shared renderer contract coverage.
- `templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs`
  - Shared capability action affordance/result mapping coverage.
- `templates/ai-first-saas-starter/frontend/src/api.contract.test.mjs`
  - API client contract coverage; use if page-route `/api/admin` client paths are changed.

## Validation command boundaries

Use these targeted checks for source-edit tasks, adjusting only if changed paths require broader validation:

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=InvitationAndUserAdminServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-user-admin-expertise.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-actions.contract.test.mjs src/api.contract.test.mjs
rg -n "USERADMIN_(VIEW_OVERVIEW|LIST_INVITATIONS|SEND_INVITATION|RESEND_INVITATION|REVOKE_INVITATION|LIST_MEMBERS)|user_admin\.dashboard\.v1|user_admin\.invitation_panel\.v1|system_message|blocked_provider_or_runtime|trace-useradmin|/api/workstream/actions|/api/admin/invitations" templates/ai-first-saas-starter --glob '!**/node_modules/**'
tools/validate-ai-first-saas-starter-fullstack.sh
```

`tools/validate-ai-first-saas-starter-fullstack.sh` should be used for broad runtime/API/UI source changes or when the task claims local runtime path readiness for the implemented slice.
