# TASK-AUTO-03-002: Update agent guidance and coverage matrix

## Objective

Update existing agent/workflow/governance/testing guidance and `docs/agent-coverage-matrix.md` to reflect Autonomous Agent coverage and remaining gaps.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/agent-coverage-matrix.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-agents/SKILL.md`
- `skills/akka-agent-component/SKILL.md`
- `skills/akka-agent-orchestration/SKILL.md`
- `skills/akka-agent-testing/SKILL.md`
- `skills/akka-workflows/SKILL.md` if present
- new `skills/akka-autonomous-*` files from TASK-AUTO-03-001

## In scope

- Add official Autonomous Agent topics to `docs/agent-coverage-matrix.md`.
- Cross-link existing request-based Agent skills to Autonomous Agent skills where task/process semantics differ.
- Cross-link workflow guidance to prefer `AutonomousAgent` when model-driven step selection is the point, and `Workflow` when order is deterministic.
- Record gaps honestly as ✅/◑/△ according to actual local examples/tests.

## Out of scope

- Do not add examples/tests in this task.
- Do not mark coverage complete unless executable examples/tests exist.

## Expected outputs

- Coverage matrix updates.
- Existing skill cross-link updates.
- Queue status update and focused commit.

## Required checks

- `git diff --check`
- `rg -n "Autonomous Agent|AutonomousAgent|autonomous-agents|request-based|Workflow" docs/agent-coverage-matrix.md skills`

## Done criteria

- Coverage matrix accurately shows new coverage and gaps.
- Existing guidance no longer implies request-based Agents are the only Akka agent component.
- Commit message: `autonomous-agents: update guidance coverage`.
