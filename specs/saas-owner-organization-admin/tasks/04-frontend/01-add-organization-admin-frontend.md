# Task: Add frontend Organization Admin surface

## Objective

Add a browser-visible Organization Admin surface/API client path for authorized SaaS Owner Admins, without creating frontend-only authorization.

## Required reads

- `AGENTS.md`
- `specs/saas-owner-organization-admin/README.md`
- `specs/saas-owner-organization-admin/backlog/01-saas-owner-organization-admin-build-backlog.md`
- `specs/saas-owner-organization-admin/tasks/04-frontend/01-add-organization-admin-frontend.md`
- app-description surface/API contracts updated by `TASK-SOOA-01-001`
- API DTOs/endpoints implemented by `TASK-SOOA-03-001`
- `frontend/src/api/**`
- `frontend/src/workstream/**`
- existing User Admin frontend contract tests

## Skills

- `akka-web-ui-apps`
- `akka-web-ui-api-client`
- `akka-web-ui-forms-validation`
- `akka-web-ui-state-rendering`
- `akka-web-ui-accessibility-responsive`

## Expected outputs

- Typed frontend API client methods/DTOs for Organization Admin.
- Organization list/detail/create/rename/suspend/reactivate UI under the User Admin/SaaS Owner workstream area.
- Frontend tests for authorized/forbidden states and safe rendering.
- Queue update.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build` if production UI output changes

## Done criteria

- SaaS Owner Admin sees Organization management surfaces/actions when authorized.
- Tenant Admin/Customer Admin unsupported contexts render safe unavailable/forbidden states and cannot gain authority from UI state.
- UI copy consistently says Organization and explains the app-data/support-access/billing boundary.
- Loading, empty, validation-error, no-op, forbidden, stale/conflict, success, and failure states are represented where relevant.
- No secrets, hidden cross-tenant facts, or raw provider details are exposed.
- Changes and queue update are committed.
