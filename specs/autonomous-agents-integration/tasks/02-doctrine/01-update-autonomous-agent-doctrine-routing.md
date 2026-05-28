# TASK-AUTO-02-001: Update doctrine and routing for Autonomous Agents

## Objective

Update core repository doctrine and routing so Akka Autonomous Agents are first-class for durable internal/background agent work, while request-based Akka Agents remain the default for user-facing workstream request/response turns.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/agent-coverage-matrix.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md`

## In scope

- Update doctrine/routing language in `docs/ai-first-saas-application-architecture.md`, `docs/capability-first-backend-architecture.md`, `docs/agent-workstream-application-architecture.md`, and `skills/README.md` as needed.
- Add or update a concise decision guide for `Agent` vs `AutonomousAgent` vs `Workflow`.
- Preserve mandatory governed runtime, authorization, tenant isolation, approval, model policy, and trace requirements.
- Explicitly address Akka autonomous `AgentDefinition` vs governed managed-agent `AgentDefinition` terminology.

## Out of scope

- Do not create the new skill family in this task.
- Do not add Java examples/tests in this task.

## Expected outputs

- Core docs/routing updates.
- Optional new focused doc, if cleaner than embedding the decision guide in existing docs.
- Queue status update and focused commit.

## Required checks

- `git diff --check`
- `rg -n "AutonomousAgent|Autonomous Agent|request-based Agent|Workflow|background|internal agent|AgentDefinition" docs skills/README.md`

## Done criteria

- Routing makes the new default clear for background/internal agents.
- Workstream request/response agents are not accidentally reclassified as Autonomous Agents.
- Commit message: `autonomous-agents: update doctrine routing`.
