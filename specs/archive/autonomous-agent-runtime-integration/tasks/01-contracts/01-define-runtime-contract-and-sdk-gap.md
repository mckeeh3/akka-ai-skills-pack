# TASK-AAI-01-001: Define runtime contract and SDK gap check

## Objective

Define the User Admin Access Review AutonomousAgent runtime contract and confirm the exact Akka SDK APIs/patterns needed for implementation.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agent-runtime-integration/README.md`
- `specs/autonomous-agent-runtime-integration/conversation-capture.md`
- `specs/autonomous-agent-runtime-integration/sprints/01-autonomous-agent-runtime-sprint.md`
- `specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md`
- `specs/autonomous-agent-runtime-integration/tasks/01-contracts/01-define-runtime-contract-and-sdk-gap.md`
- `specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md`
- official Akka AutonomousAgent docs/examples under `akka-context/sdk/` and/or local skills for autonomous agents
- current starter User Admin access-review, attention, event backbone, and agent runtime files

## Skills

- `akka-autonomous-agents`
- `akka-autonomous-agent-tasks` if available/relevant

## In scope

- Create `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`.
- Define task schema, result schema, lifecycle states, events, attention mappings, surfaces, capabilities, auth/scope, model/provider policy, tools, traces, idempotency, and tests.
- Confirm exact SDK implementation pattern or record a concrete blocker/gap.
- Update queue if implementation tasks need splitting/blocking.

## Out of scope

- Runtime implementation.
- Frontend surface implementation.

## Required checks

- `git diff --check`
- focused `rg` proving contract names AutonomousAgent, task lifecycle, provider fail-closed, v3 events, attention, surfaces, and no fake success guardrail

## Done criteria

- Implementation tasks can proceed without guessing SDK shape, task contract, auth, events, attention, or surfaces.
- Any SDK blocker is recorded with affected tasks updated.
- Task changes and queue update are committed.

## Commit message

`autonomous-agent: define access review contract`
