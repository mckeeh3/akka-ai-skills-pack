# Task AABP-01-004: Implement behavior-profile version and assignment seams

## Goal

Introduce tenant-scoped behavior profile versions for model config reference, prompt version, skill assignment, and generated tool assignment changes.

## Required reads

- `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- `app-description/domains/core-starter/data-state/managed-agent-behavior-state.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- Existing foundation agent behavior repository/runtime loader code.

## Skills

- `akka-agent-behavior-profiles`
- `akka-agent-model-governance`
- `akka-agent-skill-governance`
- `akka-agent-tool-boundaries`

## Vertical contract

- Worker: SaaS admin human, `surface_action` adapter.
- Governed tools/capabilities: behavior-profile read/change, model config reference change, skill assign/unassign, generated tool assign/unassign.
- Scope: SaaS Owner/Admin reserved `saas-app-owner` tenant plus tenant-specific override model; global fallback is read-only unless explicitly scoped.
- Trace: profile version id, previous/new assignment summaries, model alias, generated tool ids, tool-boundary refs, actor, correlation id.

## Expected outputs

- Behavior profile records or service seams with simple integer profile versions, resolved scope/provenance, active prompt version, assigned skill ids, assigned generated tool ids, model config ref, and tool-boundary ref.
- Activation of assignment/model/profile proposals creates new profile version and does not mutate skill doc versions or generated tool code.
- Catalog/detail DTOs can show resolved profile scope, model alias, skill/tool assignment summaries, profile history entries.
- Tests for clone-from-global on first tenant-scoped change, idempotent assignment changes, generated tool assignment no code mutation, and authorization denials.

## Done criteria

- Behavior-profile version seams are durable or clearly component-backed enough for runtime loader task.
- Agent detail can inspect profile summary without exposing provider secrets/tool-boundary internals.
- Queue status is updated and changes are committed.

## Required checks

```bash
mvn -Dtest='*AgentAdmin*Service*Test,*AgentRuntimeService*Test,*Agent*Profile*Test,*ToolBoundary*Test' test
git diff --check
```

## Commit message

`Implement Agent Admin behavior profile versions`
