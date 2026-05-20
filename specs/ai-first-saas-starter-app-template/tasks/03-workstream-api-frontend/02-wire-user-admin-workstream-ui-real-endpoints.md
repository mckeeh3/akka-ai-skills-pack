# TASK-STARTER-03-002: Wire User Admin workstream UI to real endpoints

## Purpose

Make the canonical User Admin workstream vertical operate against starter backend APIs in the production path.

## Required reads

- `specs/ai-first-saas-starter-app-template/starter-workstream-api-contracts.md`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/api/**`
- `frontend/src/workstream/**`
- `skills/akka-web-ui-apps/SKILL.md`
- `skills/akka-web-ui-frontend-project/SKILL.md`
- `skills/akka-web-ui-testing/SKILL.md`

## Expected outputs

- Real frontend client wiring for User Admin dashboard/list/detail/edit.
- Backend endpoint tests and frontend tests covering User Admin real API behavior.

## Done criteria

- Fixture clients remain available for tests/dev fixtures but are not the production path.
- User Admin actions preserve backend-authoritative auth, idempotency, audit, validation, and denial semantics.
- Required frontend/backend checks pass, queue status is updated, and changes are committed.
