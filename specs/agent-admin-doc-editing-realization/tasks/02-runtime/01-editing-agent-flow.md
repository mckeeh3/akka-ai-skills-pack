# Task AADE-02-001: Editing-agent draft/revise/save/cancel flow

## Scope

Implement the AI-assisted editing agent runtime flow for Agent Admin doc edits. Do not implement runtime `readSkill` / `readReferenceDoc` loading or frontend surfaces in this task.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/sprints/02-agent-runtime-sprint.md`
- `specs/agent-admin-doc-editing-realization/backlog/02-agent-runtime-build-backlog.md`
- `app-description/domains/core-starter/workstreams/agent-admin/agents/functional-agent.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- existing `WorkstreamRuntimeAgent`, model provider, and agent runtime tests

## Skills

- `akka-agent-component`
- `akka-agent-structured-responses`
- `akka-agent-testing`

## Implementation guidance

Implement the editing session path:

- target doc and base current version;
- full current doc and related same-agent context;
- free-form user request;
- optional clarifying question;
- proposed full Markdown content;
- summary of changes;
- advisory warnings/risks;
- iterative refinement with all user instructions retained;
- Save and Cancel outcomes delegated to backend doc service;
- audit data for user input, proposed output, Save/Cancel, timestamps, actor;
- fail-closed behavior when provider/runtime config is missing.

Tests may use a test model provider, but normal runtime must not silently return deterministic/model-less proposals.

## Required checks

```bash
mvn -Dtest='*AgentAdmin*Edit*,*WorkstreamRuntimeAgent*Test,*Agent*Runtime*Test' test
git diff --check
```

Adjust test names to actual classes added/changed.

## Done criteria

- Editing-agent flow can draft and revise proposed full document content.
- Save/Cancel semantics are wired to backend doc state.
- Provider/runtime unavailable behavior fails closed.
- Audit fields are captured or explicitly emitted to the trace/audit sink.
- Queue is updated and changes are committed.
