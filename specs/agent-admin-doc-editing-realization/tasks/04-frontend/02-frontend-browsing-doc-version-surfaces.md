# Task AADE-04-002: Frontend browsing, doc, and version surfaces

## Scope

Implement frontend rendering for Agent Admin browsing and read-oriented document/version surfaces. Do not implement edit-session Save/Cancel or create/delete flows in this task except navigational placeholders.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/sprints/04-frontend-sprint.md`
- `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`

## Skills

- `akka-web-ui-apps`
- `akka-web-ui-state-rendering`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

## Implementation guidance

Render:

- blank Agent Admin workstream state;
- optional dashboard with total-agent count and top five recently changed agents;
- filterable agent list;
- agent detail with editable name/purpose fields displayed as backend-authorized controls or disabled placeholders if save wiring is in another task;
- prompt doc view;
- skill doc view;
- skill reference doc view;
- version history list;
- historical read-only banner;
- adjacent version diff surface;
- restore action affordance wired to backend action if available or represented by action button data.

Ensure edit input appears only on current/latest version and historical versions are visibly read-only.

## Required checks

```bash
npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-surfaces.contract.test.mjs
npm --prefix frontend run typecheck
git diff --check
```

## Done criteria

- Read/browse/version surfaces render from fixture and API-backed payloads.
- Accessibility basics are preserved for list rows, version rows, and action buttons.
- Old governance-dashboard assumptions are removed from Agent Admin vertical tests.
- Queue is updated and changes are committed.
