# TASK-AUTO-06-001: Add Autonomous Agent TaskRule retry example

## Objective

Add a focused executable example showing `TaskRule` rejection, model retry, and eventual typed completion through the real Akka Autonomous Agent task path.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-tasks/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `akka-context/sdk/autonomous-agents/tasks.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`

## In scope

- Add a minimal task/result record with a rule such as required sources/evidence.
- Add an `AutonomousAgent` accepting that task.
- Add an endpoint or component-client driven integration test that scripts first rejected output and then accepted output.
- Update `docs/agent-coverage-matrix.md` if coverage status changes.

## Out of scope

- No generated-app governance runtime implementation.
- No broad example refactor.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "TaskRule|RESULT_REJECTED|AutonomousAgentTools|completeTask" src/main/java src/test/java docs/agent-coverage-matrix.md`

## Done criteria

- Test proves task-rule rejection and retry through Akka Autonomous Agent infrastructure.
- No normal-runtime fake is introduced.
- Focused commit exists with message `autonomous-agents: add task rule example`.
