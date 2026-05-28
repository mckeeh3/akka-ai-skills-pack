# TASK-AUTO-06-004: Add Autonomous Agent handoff triage example

## Objective

Add a focused executable example showing handoff from a triage Autonomous Agent to a specialist Autonomous Agent for the same task type.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-coordination/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `akka-context/sdk/autonomous-agents/coordination.html.md`
- `akka-context/sdk/autonomous-agents/capabilities.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`

## In scope

- Add triage and specialist `AutonomousAgent` classes.
- Use `TaskAcceptance.of(TASK).canHandoffTo(Specialist.class)`.
- Add an endpoint or component-client driven integration test that scripts `AutonomousAgentTools.handoffTo(...)` and specialist completion.
- Update `docs/agent-coverage-matrix.md` if coverage status changes.

## Out of scope

- No complex routing matrix or production escalation policy.
- No replacement of request-based user-facing workstream turns.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "canHandoffTo|handoffTo|handoff|AutonomousAgentTools|TaskAcceptance" src/main/java src/test/java docs/agent-coverage-matrix.md`

## Done criteria

- Test proves handoff ownership transfer and typed completion by the specialist path.
- The example notes approval/authority constraints for higher-authority handoff.
- Focused commit exists with message `autonomous-agents: add handoff example`.
