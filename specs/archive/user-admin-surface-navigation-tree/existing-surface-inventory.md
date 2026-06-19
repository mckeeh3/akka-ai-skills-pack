# Existing Surface Inventory: User Admin Navigation Tree

## Scope

Task: `TASK-UASNT-01-001`.

This is a docs-only survey of the existing User Admin / `user-admin-agent` app-description, frontend, backend/workstream, and contract-test artifacts against the dashboard-trunk/directory-branch design.

## Survey evidence

Commands/searches used while surveying:

```bash
find frontend/src/workstream/surfaces src/main/java/ai/first/application/coreapp/useradmin -type f | sort
rg -n "surface-user-admin|user_admin\.|action-(display|open|organization|useradmin|invite)|Show users|Back to users|Show organizations|Back to organizations|system-message|Organization Admin|Tenant Admin|Customer Admin|SaaS Owner|organization" \
  app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md \
  frontend/src/workstream/surfaces \
  frontend/src/workstream/types/surfaces.ts \
  frontend/src/workstream-user-admin-vertical.contract.test.mjs \
  frontend/src/workstream-organization-admin-vertical.contract.test.mjs \
  src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java \
  src/main/java/ai/first/application/coreapp/useradmin
rg -n "surface-user-admin-(dashboard|users|user-detail|invitation-create|invitation-detail|invitation-resend-confirmation|invitation-revoke-confirmation|membership-status-confirmation|role-change-preview|support-access-grant|support-access-revoke-confirmation|access-review-task|identity-exception-review|organization-directory|organization-detail|organization-create|organization-rename|organization-suspend-confirmation|organization-reactivate-confirmation|system-message)|action-display-organization-admin|action-organization-list|Back to Organization directory|Show organizations|Back to organizations|Show users" \
  frontend/src/__tests__/fixtures/workstream/surfaces.ts \
  frontend/src/workstream/surfaces/*.tsx \
  src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java \
  app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md \
  frontend/src/workstream-user-admin-vertical.contract.test.mjs \
  frontend/src/workstream-organization-admin-vertical.contract.test.mjs
```

Key files reviewed:

- App description: `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`.
- Frontend renderer/types: `frontend/src/workstream/surfaces/*.tsx`, `frontend/src/workstream/types/surfaces.ts`.
- Frontend fixtures/tests: `frontend/src/__tests__/fixtures/workstream/surfaces.ts`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `frontend/src/workstream-organization-admin-vertical.contract.test.mjs`.
- Backend/workstream: `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/application/coreapp/useradmin/*.java`.

## High-level findings

- App-description already names the full desired surface graph and canonical collection-object progression, including `surface-user-admin-organization-directory` and its descendants.
- Backend/workstream already exposes live surfaces for dashboard variants, user list/detail, invitation detail, role preview, access review, and all Organization Admin surfaces. It does not yet expose separate live surfaces for several user-branch task/confirmation nodes that the app-description lists as required.
- Frontend has dedicated Organization Admin rendering, generic User Admin list/detail rendering, system-message rendering, and dashboard action routing. The Organization detail surface has a backend action returning to the directory but labels it `Back to Organization directory`, not the requested `Show organizations` / `Back to organizations` wording.
- Frontend user detail and invitation detail expose a `Show users` return action through `action-display-user-list`.
- Contract tests assert many existing User Admin/Organization Admin ids, capabilities, states, and protected API/client paths, but they do not yet prove the complete dashboard -> directory -> descendant -> branch-root tree for both branches.

## Dashboard trunk

| Expected surface/artifact | Existing evidence | Classification | Notes for next tasks |
|---|---|---|---|
| `surface-user-admin-dashboard` trunk | App-description inventory; backend dynamic aliases at `WorkstreamService.java` `dynamicSurface`; frontend fixture has canonical id; dashboard renderer has User Admin command center. | revise | Backend uses role-specific dashboard ids (`surface-user-admin-saas-owner-dashboard`, tenant/customer variants) while app-description also names canonical trunk. Preserve variants but app-description/implementation tasks should clarify canonical trunk vs variants and explicit branch actions. |
| Dashboard -> User Directory action | Backend `displayListAction()` label `Show users`, result `surface-user-admin-users`; dashboard cards/actions target `surface-user-admin-users`; frontend fixtures and tests assert `action-display-user-list`. | usable as-is with focused test additions | Already backend-authored; later tasks should make branch metadata explicit rather than relying on generic action lookup only. |
| Dashboard -> Organization Directory action | Backend `displayOrganizationAdminAction()` label `Open Organization Admin`, result `surface-user-admin-organization-directory`, capability `saas_owner.tenant.read`; frontend fixtures/tests assert action and Organization Admin protected path. | revise | App-description says `saas_owner.organization.list`; code uses `saas_owner.tenant.read`. Next app-description/backend tasks should normalize alias/capability language and assert omission/denial for non-SaaS Owner contexts. |
| Unauthorized Organization branch omission/denial | Backend `SaasOwnerOrganizationAdminService.requireSaasOwner()` and `requireRead()` deny non-SaaS Owner/missing capability; frontend Organization state copy mentions Tenant Admin/Customer Admin cannot gain SaaS Owner authority. | usable as-is with stronger traversal tests | Dashboard should omit unauthorized Organization action; direct/deep link should return safe system/forbidden behavior. Current evidence supports backend denial but full workstream traversal tests are still needed. |

## User branch

| Expected surface/artifact | Existing evidence | Classification | Notes for next tasks |
|---|---|---|---|
| `surface-user-admin-users` branch root | App-description list/search contract; backend `listSurface()` emits list-search with `user_admin.users.v1`; frontend `ListSearchSurface.tsx` has `UserAdminUsersView`; tests assert id. | revise | Usable branch root exists. It currently provides inline invite form behavior instead of a separate `surface-user-admin-invitation-create` surface. Rows route by frontend user/invitation kind, not a full backend-authored per-row `targetSurfaceId`. |
| `surface-user-admin-user-detail` | App-description show-inspection contract; backend `detailSurface()` emits `surface-user-admin-user-detail`; frontend `DetailEditSurface.tsx` renders User Admin detail; tests assert id. | revise | Existing surface combines inspection with status/role forms. The tree requires inspection/task routing with consequential work in dedicated task/confirmation surfaces. `Show users` return action exists. |
| `surface-user-admin-invitation-create` | App-description required create surface only. Frontend list has inline invite form; backend `inviteAction()` creates invitation and returns invitation detail. | missing/new | Needs dedicated create-form surface or explicit accepted first-slice fallback in app-description/backend/frontend. |
| `surface-user-admin-invitation-detail` | App-description; backend `invitationDetailSurface()`; frontend user detail renderer handles invitation record kind; fixtures/tests include action result paths. | revise | Existing detail exists and has `Show users` return. Resend/revoke currently execute command actions directly from detail instead of opening dedicated confirmation surfaces. |
| `surface-user-admin-invitation-resend-confirmation` | App-description required; backend action result currently returns invitation detail; no dynamic surface case found. | missing/new | Needs separate lifecycle confirmation surface and frontend renderer state. |
| `surface-user-admin-invitation-revoke-confirmation` | App-description required; backend action result currently returns invitation detail; no dynamic surface case found. | missing/new | Needs separate destructive lifecycle confirmation surface and frontend renderer state. |
| `surface-user-admin-membership-status-confirmation` | App-description required; backend status actions return user detail; frontend detail has inline status select/save. | missing/new | Existing behavior should be revised into a dedicated confirmation surface. |
| `surface-user-admin-role-change-preview` | App-description; backend `roleChangePreviewSurface()`; frontend detail overview handles `user_admin.role_change_preview.v1`; tests assert id/capability delta. | revise | Existing surface exists but is rendered through `detail-edit`; verify branch return action is included consistently. |
| `surface-user-admin-support-access-grant` | App-description required; backend support grant/extend action returns user detail; no dynamic surface case found. | missing/new | Needs create/extend form surface or explicit deferral. |
| `surface-user-admin-support-access-revoke-confirmation` | App-description required; backend revoke action returns user detail; no dynamic surface case found. | missing/new | Needs destructive confirmation surface or explicit deferral. |
| `surface-user-admin-access-review-task` | App-description; backend `accessReviewBlockedSurface()` and action results emit workflow-status; frontend has `WorkflowStatusSurface`; tests assert access-review actions. | revise | Surface exists; branch-return metadata/action to users should be made explicit if access review is a user-branch descendant. |
| `surface-user-admin-identity-exception-review` | App-description required; no backend dynamic surface case, frontend fixture, or specific renderer path found. | missing/new | Needs implementation or explicit deferral. |
| User branch return action | Backend `displayListAction()` label `Show users`, result `surface-user-admin-users`; frontend `DetailEditSurface.tsx` renders `Show users` button when action exists. | usable as-is for existing detail/invitation; extend to missing descendants | Good foundation; missing descendant surfaces must include this action. |

## Organization branch

| Expected surface/artifact | Existing evidence | Classification | Notes for next tasks |
|---|---|---|---|
| `surface-user-admin-organization-directory` branch root | App-description; backend `organizationDirectorySurface()`; frontend `OrganizationAdminSurface.tsx`; fixtures/tests assert id and contract. | revise | Exists. Backend initial directory surface starts empty and uses refresh/list action through protected API/client path; traversal tests should prove dashboard-to-directory with data. |
| `surface-user-admin-organization-detail` | App-description; backend `organizationDetailSurface()` and `action-organization-read`; frontend detail renderer. | revise | Exists. Return action exists as `action-organization-list`, but label is `Back to Organization directory` in frontend and `Refresh Organizations` in backend, not the requested `Show organizations` / `Back to organizations`. |
| `surface-user-admin-organization-create` | App-description; backend open/create surfaces; frontend create form; tests assert typed clients and forms. | revise | Exists. The create form does not render a visible branch-return control to Organization Directory. Add/verify branch-return metadata/actions. |
| `surface-user-admin-organization-rename` | App-description; backend open/rename surfaces; frontend rename form. | revise | Exists. Needs branch-return action/control to Organization Directory. |
| `surface-user-admin-organization-suspend-confirmation` | App-description; backend open/suspend surface; frontend lifecycle confirmation. | revise | Exists. Needs branch-return action/control to Organization Directory. |
| `surface-user-admin-organization-reactivate-confirmation` | App-description; backend open/reactivate surface; frontend lifecycle confirmation. | revise | Exists. Needs branch-return action/control to Organization Directory. |
| Organization branch return action | Backend `organizationListAction()` returns `surface-user-admin-organization-directory`; frontend detail has `Back to Organization directory`. | revise | Mechanism exists. Normalize action labels to `Show organizations` / `Back to organizations`, include on all Organization descendants, and test. |

## System-message and safe denial surface

| Expected surface/artifact | Existing evidence | Classification | Notes for next tasks |
|---|---|---|---|
| `surface-user-admin-system-message` | App-description defines required system message. Frontend has generic `SystemMessageSurface.tsx`; backend shell denial uses `shellSystemMessageSurface(...)` but no surveyed dynamic case for the specific User Admin system-message id. | revise | Safe generic system messages exist. Next tasks should align specific User Admin system-message id/contract for stale/deep-link/forbidden branch navigation results. |
| Forbidden/stale/no-op states | App-description defines states. Frontend types include `forbidden`, `stale`, `no-op`, `not_found_or_redacted`, etc. Organization renderer copies denial message; backend org service throws 403/404 and audits denials. | usable as-is with focused test additions | Needs tree-specific tests for direct unauthorized org branch attempts and stale/deep-link safe denial. |

## Obsolete or conflicting artifacts

- Legacy User Admin fixture alias is preserved in `frontend/src/__tests__/fixtures/workstream/surfaces.ts` for stale-screen quarantine tests; keep until replacement tests prove the canonical ids.
- `surface-user-admin-role-capability-matrix` and `user-admin-role-capability-matrix` appear in existing tests/fixtures as older broad role/capability surface concepts. Treat as revise/deprecate unless later app-description work intentionally retains them as non-branch support surfaces.
- Existing user detail/list implementation mixes create/status/role mutation controls into list/detail surfaces. This conflicts with the split task-surface model and should be revised, not deleted blindly.

## Task-blocking gaps or ordering repair

No queue ordering repair is required. The next task, `TASK-UASNT-02-001`, can revise the app-description using this inventory.

Material gaps to carry forward:

1. Normalize dashboard trunk ids/role-specific variants and Organization capability naming (`saas_owner.organization.*` vs current `saas_owner.tenant.*`).
2. Add explicit branch-return action contract to every descendant surface, including label expectations.
3. Implement or explicitly defer missing user-branch task/confirmation surfaces.
4. Add focused backend/frontend tests for dashboard -> directory -> descendant -> branch-root traversal and unauthorized Organization branch behavior.

## Next-task handoff

Use this inventory as the source of truth for `TASK-UASNT-02-001`: revise the app-description tree while preserving backend-authoritative authorization, selected AuthContext boundaries, audit/work trace expectations, and frontend-safe payload rules.
