# TASK-AUTO-04-002: Add Autonomous Agent coordination executable example

## Objective

Add a focused executable local reference example and test for one Autonomous Agent coordination capability, preferably delegation because it best represents background/internal specialist work.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-autonomous-agent-coordination/SKILL.md`
- `skills/akka-autonomous-agent-tasks/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `akka-context/sdk/autonomous-agents/coordination.html.md`
- `akka-context/sdk/autonomous-agents/capabilities.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`
- single-agent example from TASK-AUTO-04-001.

## In scope

- Add a minimal coordinator + specialist worker example using `Delegation.to(...)` and typed task results.
- Add tests registering one `TestModelProvider` per autonomous agent class.
- Use `AutonomousAgentTools.delegateTo(...)` and `completeTask(...)` helpers.
- Assert final typed result via task snapshot.
- If handoff or human approval is clearly more valuable after research, document the choice in task notes and keep the implementation to one coordination capability.

## Out of scope

- Do not implement all coordination capabilities in one task.
- Do not add UI or starter migration in this task.

## Expected outputs

- New or updated Java example files under `src/main/java/com/example/...`.
- New or updated tests under `src/test/java/com/example/...`.
- Queue status update and focused commit.

## Required checks

- `mvn test`
- `git diff --check`
- `rg -n "Delegation|delegateTo|AutonomousAgent|TaskAcceptance|completeTask" src/main/java src/test/java docs/agent-coverage-matrix.md`

## Done criteria

- Test proves the coordinator delegates to a worker and synthesizes/returns a typed top-level result.
- Coverage matrix is updated if status changes.
- Commit message: `autonomous-agents: add coordination example`.
