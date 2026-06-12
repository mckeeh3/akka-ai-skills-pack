# User Admin Surface Navigation Tree Mini-Project

## Purpose

Make the User Admin workstream surfaces conform to an explicit tree-navigation model where the dashboard is the trunk, User Directory and Organization Directory are first-level branches, and all descendant task/detail surfaces route back to their owning directory.

This mini-project covers app-facing root application assets: `app-description/`, backend workstream/API payloads, frontend workstream surfaces, tests, and validation notes.

## Current intent

The User Admin dashboard must be an action-router trunk:

- it opens `surface-user-admin-users` for scoped users/memberships/invitations/access items;
- it opens `surface-user-admin-organization-directory` for SaaS Owner/App Admin contexts with `saas_owner.organization.list` or equivalent backend capability;
- it never exposes hidden counts, hidden organizations, hidden users, or unauthorized branch actions through frontend-only logic.

The first-level directory surfaces are the branch roots:

- User branch root: `surface-user-admin-users`.
- Organization branch root: `surface-user-admin-organization-directory`.

Every descendant surface must include an explicit, backend-authorized navigation action to return to its branch root:

- User branch descendants: **Show users** / **Back to users** -> `surface-user-admin-users`.
- Organization branch descendants: **Show organizations** / **Back to organizations** -> `surface-user-admin-organization-directory`.

Directory row/card activation remains lifecycle-aware and backend-authored. Descendant surfaces perform one job each and should not become combined CRUD panels.

## Done state

This mini-project is complete when:

1. existing User Admin and Organization Admin app-description, backend, frontend, and test surfaces have been surveyed and classified;
2. `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md` and related realization/traceability docs describe the dashboard-trunk/directory-branch tree, branch return actions, auth, payloads, traces, states, and tests;
3. backend/workstream surface payloads include explicit navigation metadata/actions for dashboard -> directories, directory -> descendants, and descendant -> directory returns;
4. frontend surfaces implement the tree navigation and all required branch descendant surfaces at the stated scope;
5. obsolete/conflicting surfaces are removed, deprecated, or routed safely;
6. tests prove dashboard-to-directory, directory-to-descendant, descendant-back-to-directory, auth/forbidden, stale/reconnect, audit/trace, and no frontend secret behavior;
7. final verification compares implementation against this README and app-description and appends follow-up tasks plus a new terminal verification task if material gaps remain.

## Non-goals

- Redesigning the full identity/admin foundation beyond navigation-tree and missing surface realization.
- Granting SaaS Owner users tenant app-data access without support/scoped authorization.
- Replacing backend authorization with frontend route or button visibility.
- Counting fixture-only or mocked normal runtime behavior as complete.

## Primary source artifacts

- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/realization/traceability.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream/types/surfaces.ts`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-organization-admin-vertical.contract.test.mjs`

## Task execution rules

Use `specs/user-admin-surface-navigation-tree/pending-tasks.md`. Execute one task per fresh harness context, update task status before implementation edits, run the task's checks, and commit each completed task with the queue update.
