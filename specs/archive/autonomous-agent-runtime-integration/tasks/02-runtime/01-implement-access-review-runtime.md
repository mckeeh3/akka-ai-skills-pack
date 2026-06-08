# TASK-AAI-02-001: Implement access review AutonomousAgent runtime

## Objective

Implement the backend runtime path for starting, querying, and managing User Admin Access Review AutonomousAgent tasks through governed capabilities.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- contract from TASK-AAI-01-001
- relevant Akka AutonomousAgent docs/skills confirmed by TASK-AAI-01-001
- starter User Admin access-review service, attention, event backbone, and agent runtime files

## Skills

- `akka-autonomous-agents`
- `akka-autonomous-agent-tasks` if relevant

## In scope

- Implement task start/query/lifecycle/result review capability paths.
- Integrate Akka `AutonomousAgent` runtime or block precisely if SDK support/config is insufficient.
- Enforce AuthContext, tenant/customer scope, capability checks, idempotency, and audit/work traces.
- Provider/model missing config must fail closed with actionable status.
- Add backend tests for auth, lifecycle, idempotency, fail-closed, and no model-less normal success.

## Out of scope

- Frontend surface rendering.
- Multiple worker types or teams.

## Required checks

- `git diff --check`
- scaffolded backend Maven tests for access-review AutonomousAgent runtime/fail-closed behavior
- focused `rg` proving no deterministic fake success path is wired as normal runtime

## Done criteria

- Backend runtime path exists or is explicitly blocked with queue updates.
- Tests prove governed lifecycle and fail-closed behavior.
- Task changes and queue update are committed.

## Commit message

`autonomous-agent: implement access review runtime`
