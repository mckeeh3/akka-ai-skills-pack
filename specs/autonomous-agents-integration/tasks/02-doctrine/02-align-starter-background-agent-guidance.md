# TASK-AUTO-02-002: Align starter and generated-app background-agent guidance

## Objective

Update starter/generated-app guidance so internal/background agent work defaults to Akka Autonomous Agents where durable task/process semantics fit, while preserving request-based workstream agents for user-facing request/response turns.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `templates/ai-first-saas-starter/README.md`
- `docs/minimum-ai-first-saas-app.md`
- `docs/core-ai-first-saas-foundation.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/ai-first-saas-application-architecture.md`
- `specs/autonomous-agents-integration/research-notes.md`
- new Autonomous Agent skills from TASK-AUTO-03-001.

## In scope

- Update starter/template documentation and generated-app guidance where it discusses internal/background agents, admin agents, evaluation/replay loops, monitoring/remediation, digest processors, or specialist agents.
- Clarify that workstream functional agents use request-based `Agent` for bounded user-facing turns unless the workstream action launches a durable task.
- Preserve governed runtime, model policy, seed behavior, tool-boundary, trace, authorization, and fail-closed requirements.

## Out of scope

- Do not perform a broad starter implementation migration unless the touched files already contain a bounded internal/background agent example suitable for direct replacement.
- Do not change user-facing workstream runtime from request-based `Agent` to `AutonomousAgent` by default.

## Expected outputs

- Starter/generated-app guidance updates.
- Queue status update and focused commit.

## Required checks

- `git diff --check`
- `rg -n "AutonomousAgent|Autonomous Agent|background|internal agent|workstream|request-based" templates/ai-first-saas-starter docs skills/README.md`

## Done criteria

- Starter guidance teaches the new default for internal/background agents.
- Workstream request/response guidance remains intact and explicit.
- Commit message: `autonomous-agents: align starter guidance`.
