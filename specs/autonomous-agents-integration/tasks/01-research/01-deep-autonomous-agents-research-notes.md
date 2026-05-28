# TASK-AUTO-01-001: Create deep Autonomous Agents research notes

## Objective

Read the official Akka Autonomous Agent documentation deeply and create local research notes that future docs, skills, and examples can use without rereading every official page.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/README.md`
- `specs/autonomous-agents-integration/conversation-capture.md`
- `specs/autonomous-agents-integration/sprints/01-autonomous-agents-first-pass-sprint.md`
- `specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md`
- `akka-context/sdk/autonomous-agents.html.md`
- `akka-context/sdk/autonomous-agents/defining.html.md`
- `akka-context/sdk/autonomous-agents/tasks.html.md`
- `akka-context/sdk/autonomous-agents/coordination.html.md`
- `akka-context/sdk/autonomous-agents/capabilities.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/notifications.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`
- `akka-context/sdk/use-cases/autonomous-agents.html.md`
- relevant request-based agent and workflow docs only for comparison.

## In scope

- Create `specs/autonomous-agents-integration/research-notes.md`.
- Capture exact API concepts: `AutonomousAgent`, `AgentDefinition`, `Task`, `TaskTemplate`, `TaskAcceptance`, task rules, `ComponentClient.forAutonomousAgent`, `forTask`, `runSingleTask`, `assignTasks`, task lifecycle operations, notifications, `TestModelProvider.AutonomousAgentTools`.
- Capture decision rules for `Agent` vs `AutonomousAgent` vs `Workflow`.
- Capture governance implications and risks for this pack.
- Identify sample/example patterns to implement locally.

## Out of scope

- Do not update installable skills or core doctrine yet.
- Do not add examples/tests yet.

## Expected outputs

- `specs/autonomous-agents-integration/research-notes.md`
- queue status update and focused commit.

## Required checks

- `git diff --check`
- `rg -n "AutonomousAgent|TaskAcceptance|TaskTemplate|forAutonomousAgent|AutonomousAgentTools|Agent vs AutonomousAgent|Workflow" specs/autonomous-agents-integration/research-notes.md`

## Done criteria

- Notes are accurate, source-linked by file path, and agent-optimized.
- Notes explicitly call out naming collision with governed managed-agent `AgentDefinition`.
- Commit message: `autonomous-agents: add research notes`.
