# TASK-AUTO-06-006: Plan governed Autonomous Agent tool-boundary example

## Objective

Define the smallest future executable/generated-app-style reference slice for `ToolPermissionBoundary`, tenant isolation, approval gates, and traces around Autonomous Agent tools without faking a normal runtime.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-governance/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `docs/agent-coverage-matrix.md`

## In scope

- Write a focused implementation task brief or mini-plan for a governance-heavy Autonomous Agent reference slice.
- Specify capability ids, tenant/customer scope, tool grants/denials, approval-before-side-effect, trace expectations, and fail-closed missing-config tests.
- Decide whether to implement as `src/` reference code or starter-template work, and append one implementation task if warranted.

## Out of scope

- Do not implement the governance-heavy slice in this task.
- Do not create deterministic/demo/model-less normal runtime behavior.

## Checks

- `git diff --check`
- `rg -n "ToolPermissionBoundary|tenant|approval|trace|fail-closed|AutonomousAgent" specs/autonomous-agents-integration docs/agent-coverage-matrix.md`

## Done criteria

- Future governance-heavy Autonomous Agent example work is executable without guessing, or explicitly deferred with rationale.
- Focused commit exists with message `autonomous-agents: plan tool boundary example`.
