# Task AADE-03-001: Workstream/API action wiring

## Scope

Expose Agent Admin doc-administration behavior through protected backend workstream/API actions and typed surface results. Do not implement frontend rendering in this task.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/sprints/03-api-workstream-sprint.md`
- `specs/agent-admin-doc-editing-realization/backlog/03-api-frontend-build-backlog.md`
- `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/surface-catalog.md`
- existing `WorkstreamEndpoint`, `AdminEndpoint`, workstream API tests

## Skills

- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`

## Implementation guidance

Wire backend actions for:

- blank Agent Admin workstream state;
- optional dashboard;
- agent list and filters;
- agent detail;
- prompt/skill/reference doc reads;
- edit session draft/revise/save/cancel;
- version history and adjacent diff;
- restore;
- create/delete skill;
- create/delete reference doc;
- runtime traces.

Ensure non-SaaS-admin callers are denied server-side. Ensure old prompt-risk/seed/tool-boundary/model-ref/activation Agent Admin actions are not exposed as current Agent Admin surface actions unless retained only as non-current internal/legacy code.

## Required checks

```bash
mvn -Dtest='*AdminEndpoint*Test,*Workstream*Endpoint*Test,*AgentAdmin*Test' test
git diff --check
```

Adjust test names to actual classes added/changed.

## Done criteria

- Backend returns typed surface/action DTOs aligned with current app-description.
- Protected actions enforce SaaS-admin-only authorization.
- Targeted endpoint/workstream tests pass.
- Queue is updated and changes are committed.
