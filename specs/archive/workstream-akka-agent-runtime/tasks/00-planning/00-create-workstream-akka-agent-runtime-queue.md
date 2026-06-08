# TASK-WSAGENT-00-001: Create workstream Akka Agent runtime queue

## Objective

Create a self-contained migration queue that fixes the gap between the promised v0 real AI model workstream feature and the current starter implementation that does not use an Akka Agent component for normal workstream responses.

## Required reads

- AGENTS.md
- skills/README.md
- docs/agent-workstream-application-architecture.md
- specs/production-ready-five-core-v0/pending-tasks.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java

## Expected outputs

- specs/workstream-akka-agent-runtime/README.md
- specs/workstream-akka-agent-runtime/conversation-capture.md
- specs/workstream-akka-agent-runtime/pending-tasks.md
- specs/workstream-akka-agent-runtime/backlog/01-workstream-akka-agent-runtime-backlog.md
- specs/workstream-akka-agent-runtime/sprints/01-runtime-correction-sprint.md
- specs/workstream-akka-agent-runtime/tasks/**/*.md

## Required checks

- `git diff --check`
- `rg -n "TASK-WSAGENT|Akka Agent|workstream|real model|fake|deterministic" specs/workstream-akka-agent-runtime`

## Done criteria

- Queue exists with self-sufficient tasks for fresh harness sessions.
- Each task requires a focused git commit before being marked done.
- This planning task is committed with message `workstream-agent: add Akka Agent runtime queue`.
