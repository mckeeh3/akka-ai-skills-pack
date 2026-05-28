# Sprint 01: Autonomous Agents First-Pass Migration

## Objective

Make Akka `AutonomousAgent` a first-class component in this skills pack for durable background/internal agent work while preserving request-based Akka `Agent` for user-facing workstream request/response turns.

## Scope

- Research official Autonomous Agent docs deeply enough to produce local agent-optimized guidance.
- Update core doctrine and routing to include Autonomous Agents in component selection.
- Add installable skills for Autonomous Agents.
- Update coverage tracking.
- Add initial executable examples/tests under `src/`.
- Add a verification task that appends follow-up tasks and repeats verification as needed.

## Source context

Official docs to consult across the sprint:

- `akka-context/sdk/autonomous-agents.html.md`
- `akka-context/sdk/autonomous-agents/defining.html.md`
- `akka-context/sdk/autonomous-agents/tasks.html.md`
- `akka-context/sdk/autonomous-agents/coordination.html.md`
- `akka-context/sdk/autonomous-agents/capabilities.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/notifications.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`
- `akka-context/sdk/use-cases/autonomous-agents.html.md`
- request-based `Agent` and `Workflow` docs only where needed for comparison.

## Ordered work areas

1. Research and source notes.
2. Doctrine/routing migration.
3. Skill family creation and manifest/routing updates.
4. Existing guidance consistency updates.
5. Initial examples/tests.
6. Verification loop and follow-up example planning.

## Acceptance criteria

- Local docs and skills give clear decision rules for `Agent` vs `AutonomousAgent` vs `Workflow`.
- Background/internal generated-app agents default to Autonomous Agents when durable task/process semantics fit.
- Request-based workstream agents remain the default for bounded user-facing turns.
- Governed runtime and capability-first rules explicitly apply to Autonomous Agents.
- At least one executable `src/` example/test slice exists before the first-pass sprint is considered complete.
- Queue tasks are committed one at a time.

## Handoff notes

Do not broaden a task during execution. If a task reveals more work, update `pending-tasks.md` with follow-up tasks and leave the new work for a fresh harness session.
