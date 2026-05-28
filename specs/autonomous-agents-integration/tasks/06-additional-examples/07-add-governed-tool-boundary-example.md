# TASK-AUTO-06-007: Add governed Autonomous Agent tool-boundary example

## Objective

Implement the focused `src/` reference slice planned in `specs/autonomous-agents-integration/governed-tool-boundary-example-plan.md` to prove generated-app-style `ToolPermissionBoundary` enforcement around an Akka `AutonomousAgent` local tool facade.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `specs/autonomous-agents-integration/governed-tool-boundary-example-plan.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-governance/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `docs/agent-coverage-matrix.md`

## Skills

- `akka-autonomous-agents`
- `akka-autonomous-agent-governance`
- `akka-autonomous-agent-testing`

## In scope

- Add a small executable `src/main/java/com/example/...` Autonomous Agent reference slice for governed tool-boundary behavior.
- Use stable capability ids, tenant/customer-scoped request/result DTOs, a backend-owned tool registry/boundary representation, approval-required side-effect semantics, and safe trace records.
- Register a local `@FunctionTool` facade that enforces the active boundary before protected reads or side effects.
- Add integration tests that trigger the real Akka `AutonomousAgent` task path through `ComponentClient` or a narrow endpoint.
- Update `docs/agent-coverage-matrix.md` only for the exact coverage proven by the tests.

## Out of scope

- Do not build a full generated-app governed runtime, UI, persistent repository, or starter-template implementation.
- Do not implement MCP/component-tool/readSkill/readReferenceDoc boundary coverage unless the task is first split.
- Do not introduce deterministic/demo/model-less normal runtime behavior; `TestModelProvider` belongs only in tests.
- Do not let prompt text, task instructions, skill/reference text, or model-supplied tenant/customer ids grant authority.

## Expected outputs

- `src/main/java/com/example/.../GovernedRiskReviewAutonomousAgent.java` or equivalent
- `src/main/java/com/example/.../GovernedRiskReviewTasks.java` or equivalent
- `src/main/java/com/example/.../GovernedRiskReviewTools.java` or equivalent
- minimal domain/service/trace classes needed for the focused boundary example
- `src/test/java/com/example/.../GovernedRiskReviewAutonomousAgentIntegrationTest.java` or equivalent
- `docs/agent-coverage-matrix.md` update for the proven governed non-component facade/tool-boundary pattern

## Required checks

- `mvn test`
- `git diff --check`
- `rg -n "ToolPermissionBoundary|tenant|approval_required|trace|fail-closed|AutonomousAgent|FunctionTool" src/main/java src/test/java docs/agent-coverage-matrix.md`

## Done criteria

- Tests prove allowed read-only tool invocation with allowed trace and typed task completion.
- Tests prove ungranted tool denial with safe response and denial trace.
- Tests prove cross-tenant or wrong-customer denial without evidence leakage.
- Tests prove side-effecting follow-up returns `approval_required`, records an approval/proposal trace, and does not execute the side effect.
- Tests prove missing provider/security/boundary configuration fails closed with an actionable error or failed task.
- Coverage matrix reflects exactly the implemented/proven slice.
- Focused commit exists with message `autonomous-agents: add governed tool boundary example`.
