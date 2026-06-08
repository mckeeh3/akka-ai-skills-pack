# TASK-FC5-03-001: Implement My Account full-core vertical

## Objective

Implement My Account full-core surfaces for profile, settings, selected context, safe capability basis, and personal/cross-workstream attention.

## Required reads

- `specs/full-core-five-workstreams/full-core-contract-matrix.md`
- `docs/agent-workstream-application-architecture.md` My Account guidance
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- starter backend/frontend My Account and `/api/me` related files

## Expected outputs

- My Account dashboard/profile/settings/context/attention surface contracts and implementation.
- Backend capabilities for profile/settings/context reads and updates with authorization, validation, idempotency where needed, and audit.
- Surface actions for open profile, open settings, update settings, select context, and open attention/workstream targets.
- Frontend rendering and tests for ready, forbidden, validation, no-op, stale/error, and trace-link states.

## Checks

- `mvn test`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- local smoke path documented or run for My Account profile/settings/context actions
- `git diff --check`

## Done criteria

My Account is a real aggregate workstream surface path, not just markdown guidance.
