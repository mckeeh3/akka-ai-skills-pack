# Task AADE-04-003: Frontend edit, create/delete, trace flows, and stale governance cleanup

## Scope

Complete frontend Agent Admin interactive flows and remove/hide stale governance-console behavior from current Agent Admin UI/tests.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/sprints/04-frontend-sprint.md`
- `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/frontend-routes.md`
- frontend Agent Admin surfaces and contract tests

## Skills

- `akka-web-ui-forms-validation`
- `akka-web-ui-state-rendering`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

## Implementation guidance

Implement/render:

- edit-session surface with user instruction transcript, full proposed document, summary, advisory warnings/risks, Show diff, refinement input, Save, Cancel;
- create skill flow;
- delete skill confirmation with permanent warning and reference doc count/list;
- create reference doc flow;
- delete reference doc confirmation;
- runtime read traces with filters for agent, doc, time range;
- composer routing alignment for Agent Admin current surfaces;
- cleanup/hiding of stale Agent Admin governance UI, old prompt-risk flows, seed import, model refs, tool-boundary, activation/deactivation/rollback surfaces from current Agent Admin tests.

## Required checks

```bash
npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-actions.contract.test.mjs frontend/src/workstream-surface-intent-routing.contract.test.mjs
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

## Done criteria

- Frontend covers all remaining Agent Admin doc-editing interactive flows.
- Current tests no longer assert stale governance-console Agent Admin behavior.
- Save/Cancel/Delete/Restore action affordances route to protected backend action envelopes.
- Queue is updated and changes are committed.
