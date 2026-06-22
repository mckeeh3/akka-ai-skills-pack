# Task: Implement User Admin surface routing proof

## Objective

Implement high-confidence deterministic User Admin routes, including Organization Create prefill for `create organization "Org 1"`.

## Required reads

- `AGENTS.md`
- `specs/workstream-surface-intent-routing/README.md`
- `specs/workstream-surface-intent-routing/conversation-capture.md`
- `specs/workstream-surface-intent-routing/sprints/01-router-user-admin-proof.md`
- `specs/workstream-surface-intent-routing/tasks/02-user-admin/01-implement-user-admin-surface-routing.md`
- router contract from previous task
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/surfaces/OrganizationAdminSurface.tsx`
- existing User Admin and Organization Admin tests

## Skills

- capability-first-backend
- akka-http-endpoint-component-client
- akka-web-ui-state-rendering

## Expected outputs

- Routes for:
  - `create organization "<name>"` → `surface-user-admin-organization-create` with prefilled organization name;
  - `show/open organizations` → Organization Directory;
  - `show users` → User Directory;
  - `invite user <email>` → invitation create surface with prefilled email where safe.
- Safe no-match/ambiguous fallback behavior.
- Tests for authorized and unauthorized selected contexts.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend tests for User Admin routing
- `mvn test` if shared workstream behavior changes materially

## Done criteria

- `create organization "Org 1"` opens a prefilled Organization Create surface and does not create an Organization.
- Missing capability or unavailable surface is handled as a safe denial/fallback without hidden target enumeration.
- User-facing workstream item copy says the user must review and submit the surface.
- Changes and queue update are committed.
