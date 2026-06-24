# Task AADE-02-002: Runtime doc loading and read traces

## Scope

Revise runtime managed-agent document loading and loader tools to match Agent Admin doc-editing intent.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/sprints/02-agent-runtime-sprint.md`
- `specs/agent-admin-doc-editing-realization/backlog/02-agent-runtime-build-backlog.md`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- `app-description/domains/core-starter/workstreams/agent-admin/traces/work-traces.md`
- existing `AgentRuntimeLoaderTools`, `AgentRuntimeService`, `AgentRuntimeTrace*`, and tests

## Skills

- `akka-agent-component-tools`
- `akka-agent-work-trace`
- `akka-agent-testing`

## Implementation guidance

Implement runtime behavior:

- each agent request loads current prompt;
- prompt context includes skill names/descriptions for that agent;
- `readSkill` is available to all agents and returns selected skill content plus reference doc names/descriptions;
- `readReferenceDoc` is available to all agents and returns selected reference content;
- no cross-agent skill discovery path exists;
- runtime reads are traced with agent name, doc read, timestamp, request/session id, and user/customer context;
- Agent Admin can query trace metadata without showing full read content in trace rows.

## Required checks

```bash
mvn -Dtest='*AgentRuntimeToolResolver*Test,*AgentRuntimeTrace*Test,*AgentRuntimeService*Test,*WorkstreamRuntimeAgent*Test' test
git diff --check
```

Adjust test names to actual classes added/changed.

## Done criteria

- Runtime prompt/skill/reference loading matches app-description.
- Runtime traces are emitted and queryable for Agent Admin metadata surfaces.
- Tests prove no cross-agent skill discovery path and trace field coverage.
- Queue is updated and changes are committed.
