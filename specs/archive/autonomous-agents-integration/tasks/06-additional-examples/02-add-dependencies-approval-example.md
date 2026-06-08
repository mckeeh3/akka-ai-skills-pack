# TASK-AUTO-06-002: Add Autonomous Agent dependencies and approval example

## Objective

Add a focused executable example showing task dependencies and external/human approval as an unassigned task that gates downstream autonomous work.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-coordination/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `akka-context/sdk/autonomous-agents/tasks.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/coordination.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`

## In scope

- Add minimal task definitions for draft/investigate, approval/external input, and publish/finalize.
- Demonstrate `dependsOn(taskId)` and externally completing/failing the approval task via `ComponentClient.forTask(...)`.
- Add tests for approved path and failed/cancelled dependency behavior.
- Update `docs/agent-coverage-matrix.md` if coverage status changes.

## Out of scope

- No real user approval UI.
- No production policy engine implementation.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "dependsOn|approval|complete\(|fail\(|CANCELLED|AutonomousAgent" src/main/java src/test/java docs/agent-coverage-matrix.md`

## Done criteria

- Test proves dependency-gated autonomous task behavior and external approval completion/failure.
- The example states when Workflow pause/resume would be preferred.
- Focused commit exists with message `autonomous-agents: add dependencies approval example`.
