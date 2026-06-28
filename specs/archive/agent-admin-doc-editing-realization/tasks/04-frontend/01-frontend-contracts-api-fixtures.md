# Task AADE-04-001: Frontend contracts, API types, and fixtures

## Scope

Update frontend type definitions, API client contracts, fixtures, and contract tests to represent the new Agent Admin doc-editing surfaces. Do not implement full UI rendering in this task except minimal compile-safe placeholders as needed.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/sprints/04-frontend-sprint.md`
- `specs/agent-admin-doc-editing-realization/backlog/03-api-frontend-build-backlog.md`
- `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/frontend-routes.md`
- existing `frontend/src/api/types.ts`
- existing `frontend/src/__tests__/fixtures/workstream/surfaces.ts`
- existing Agent Admin frontend contract tests

## Skills

- `akka-web-ui-api-client`
- `akka-web-ui-testing`

## Implementation guidance

Update/add frontend contracts for all current Agent Admin surfaces:

- blank;
- dashboard;
- agent list;
- agent detail;
- prompt doc;
- skill doc;
- reference doc;
- edit session;
- version history;
- version diff;
- create/delete skill;
- create/delete reference doc;
- runtime traces;
- system message.

Remove or supersede fixture/test assumptions for stale governance/prompt-risk/seed/tool-boundary/activation surfaces.

## Required checks

```bash
npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-surfaces.contract.test.mjs
npm --prefix frontend run typecheck
git diff --check
```

Adjust exact test invocation if the frontend test runner requires a different pattern.

## Done criteria

- Frontend types and fixtures compile with current Agent Admin surface inventory.
- Contract tests assert current app-description semantics at the data-shape level.
- No backend code changes are attempted unless needed to keep generated types/API imports consistent.
- Queue is updated and changes are committed.
