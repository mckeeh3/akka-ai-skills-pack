# Backlog 03: API and frontend realization

## Design notes

The frontend should render the Agent Admin doc-editing workspace described in app-description, not the older governance console. Existing generic workstream surface components may be reused if they naturally fit, but ordinary SaaS admins should see plain document-editing language.

## Implementation areas

Backend/API:

- `src/main/java/ai/first/api/coreapp/admin/**`
- workstream endpoint/action mapping classes
- related integration tests

Frontend:

- `frontend/src/api/types.ts`
- `frontend/src/api/HttpWorkstreamApiClient.ts`
- `frontend/src/__tests__/fixtures/**`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/workstream-surface-intent-routing.contract.test.mjs`

## Task breakdown

### AADE-03-001 — Workstream/API action wiring

Expose protected workstream actions for all Agent Admin surfaces and tool contracts. Ensure non-SaaS-admin callers are denied and old governance/prompt-risk/seed/tool-boundary actions are not current Agent Admin surface actions.

### AADE-04-001 — Frontend contracts, API types, and fixtures

Update frontend type definitions, API client contracts, and fixtures to model Agent Admin doc-editing surfaces.

### AADE-04-002 — Frontend browsing/doc/version surfaces

Implement blank state, optional dashboard, agent list, agent detail, prompt doc, skill doc, reference doc, version history, adjacent diff, historical read-only state, and restore UI.

### AADE-04-003 — Frontend edit/create/delete/trace flows and stale governance cleanup

Implement edit-session, create/delete skill, create/delete reference doc, runtime trace surfaces, composer routing alignment, and cleanup or hide stale governance UI/tests.

## Validation

Frontend tasks should run targeted contract tests first, then `npm --prefix frontend run typecheck`, and build/test where feasible.
