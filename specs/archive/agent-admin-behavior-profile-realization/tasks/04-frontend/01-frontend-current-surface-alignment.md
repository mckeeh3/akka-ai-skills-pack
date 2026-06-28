# Task AABP-04-001: Align Agent Admin frontend surfaces and contracts

## Goal

Update Agent Admin frontend types, fixtures, renderers, and tests to match current behavior-profile proposal/review/activation intent.

## Required reads

- `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/frontend-routes.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- `frontend/AGENTS.md`
- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`
- Current Agent Admin frontend surfaces, fixtures, and API types.

## Skills

- `akka-web-ui-apps`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

## Vertical contract

- User-facing worker: SaaS admin human using structured surfaces.
- Actor adapter: frontend invokes backend-authored `surface_action` actions only; no frontend-only authorization or direct mutation.
- Surfaces: blank/dashboard/catalog/detail/doc editor/proposal review/profile version/assignment/runtime traces/system-message.
- Secret boundary: no provider secrets, raw model settings, hidden platform instructions, generated tool internals, or unapproved tool-boundary internals.

## Expected outputs

- Frontend contracts/types/fixtures for current Agent Admin surface inventory.
- Purpose-built or reconciled renderers for proposal review, version history, behavior profile, skill/generated-tool assignment, model config ref summary, runtime traces, and safe recovery states.
- Removed/replaced stale assertions for direct active save, permanent delete, whole-agent activation/deactivation, generated agent identity edits, and generic governance-console defaults.
- Tests for SaaS-admin-only visibility, proposal save/activation states, high-risk denial/routing, historical read-only views, restore proposal, assignment surfaces, and trace metadata.

## Done criteria

- Frontend tests/typecheck/build pass.
- UI remains backend-authoritative and browser-safe.
- Current Agent Admin surface inventory is covered without stale governance-console assumptions.
- Queue status is updated and changes are committed.

## Required checks

```bash
npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-surfaces.contract.test.mjs frontend/src/workstream-actions.contract.test.mjs
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

## Commit message

`Align Agent Admin frontend behavior profile surfaces`
