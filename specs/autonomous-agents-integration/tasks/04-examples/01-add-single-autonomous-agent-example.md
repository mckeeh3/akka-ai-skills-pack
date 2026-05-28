# TASK-AUTO-04-001: Add single Autonomous Agent executable example

## Objective

Add a minimal executable local reference example and test for one Akka `AutonomousAgent` processing one typed task.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-tasks/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `akka-context/sdk/autonomous-agents.html.md`
- `akka-context/sdk/autonomous-agents/defining.html.md`
- `akka-context/sdk/autonomous-agents/tasks.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`
- existing `src/main/java/com/example/application/*Agent*.java` and tests for repository style.

## In scope

- Add a small `src/main/java/com/example/application/...` Autonomous Agent example with:
  - `@Component` description;
  - class extending `AutonomousAgent`;
  - task definition with typed result;
  - endpoint or service trigger using `ComponentClient.forAutonomousAgent(...).runSingleTask(...)` if consistent with examples.
- Add a test using `TestModelProvider` and `AutonomousAgentTools.completeTask(...)`.
- Use Awaitility/task snapshot polling for async completion.

## Out of scope

- Do not add multi-agent coordination in this task.
- Do not migrate starter template internals in this task.

## Expected outputs

- New or updated Java example files under `src/main/java/com/example/...`.
- New or updated tests under `src/test/java/com/example/...`.
- Queue status update and focused commit.

## Required checks

- `mvn test`
- `git diff --check`
- `rg -n "extends AutonomousAgent|TaskAcceptance|forAutonomousAgent|AutonomousAgentTools|completeTask" src/main/java src/test/java`

## Done criteria

- The example compiles and test proves typed task completion through the Akka Autonomous Agent path.
- No deterministic fake is wired as normal runtime behavior.
- Commit message: `autonomous-agents: add single agent example`.
