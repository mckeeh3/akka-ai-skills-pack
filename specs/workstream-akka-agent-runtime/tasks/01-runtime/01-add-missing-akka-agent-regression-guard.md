# TASK-WSAGENT-01-001: Add regression guard for missing Akka workstream Agent

## Objective

Make the current implementation gap impossible to miss: the starter template must fail a source/contract test if no workstream runtime component extends `akka.javasdk.agent.Agent` or if the normal workstream message path can bypass that component.

## Required reads

- AGENTS.md
- skills/README.md
- docs/agent-workstream-application-architecture.md
- docs/agent-coverage-matrix.md
- skills/akka-agents/SKILL.md
- skills/akka-agent-component/SKILL.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
- templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java

## Expected outputs

- Add or update a starter/template regression test that asserts a concrete workstream runtime class imports `akka.javasdk.agent.Agent` and extends `Agent`.
- Add or update a regression test that fails if `WorkstreamService.submitMessage(...)` or the message endpoint can produce successful normal runtime markdown without the Akka Agent runtime invoker seam.
- Keep fake providers/test adapters clearly named and scoped to tests only.
- Do not implement the production Akka Agent in this task unless required for the guard to compile; if so, keep it minimal and leave wiring to the next task.

## Required checks

- `mvn test`
- `git diff --check`
- `rg -n "extends Agent|akka.javasdk.agent.Agent|Workstream.*Agent|fake|test adapter|submitMessage" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Done criteria

- Tests fail on the pre-fix shape where the starter has no Akka Agent-backed workstream runtime.
- Tests still allow isolated unit-test fakes only when explicitly named as test adapters.
- Task status is updated in `specs/workstream-akka-agent-runtime/pending-tasks.md`.
- A focused git commit exists with message `workstream-agent: guard Akka Agent runtime`.
