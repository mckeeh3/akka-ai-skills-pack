# Sprint 01: Agent Admin Full-Core Map and First Implementation Group

## Objective

Define and queue the Agent Admin SMB full-core implementation path, then implement the first bounded source-edit group once source boundaries are known.

## Source context

The starter already contains governed managed-agent runtime foundations and prior workstream contracts. This sprint turns those foundations into an operator-facing Agent Admin workstream with deterministic lifecycle controls and governed AgentAdminAgent guidance.

## Ordered work areas

1. Define Agent Admin vertical slice contracts and implementation map.
2. Implement catalog/detail dashboard and governed config reads.
3. Implement prompt/skill/reference/manifest/tool-boundary visibility.
4. Implement behavior-change proposal/review/activation lifecycle at SMB scope.
5. Implement AgentAdminAgent request/response guidance.
6. Implement prompt-risk/behavior-review worker only if deterministic lifecycle foundations and task semantics justify it.
7. Validate runtime/API/UI behavior and verify mini-project readiness.

## Acceptance criteria

- Implementation tasks are bounded by actual source boundaries.
- Agent Admin surfaces are typed, trace-linked, authority-scoped, and visually polished.
- Deterministic lifecycle services own behavior changes.
- Model-backed guidance/worker behavior uses governed Akka runtime and provider fail-closed semantics.
- Targeted and broad starter validation pass or blockers are queued.
