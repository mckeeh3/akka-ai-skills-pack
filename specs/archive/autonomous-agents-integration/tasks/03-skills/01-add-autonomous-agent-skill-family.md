# TASK-AUTO-03-001: Add Autonomous Agent skill family

## Objective

Add installable skills that teach future harness sessions how to implement, test, and govern Akka Autonomous Agents.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `pack/manifest.yaml`
- `specs/autonomous-agents-integration/research-notes.md`
- `docs/agent-coverage-matrix.md`
- `skills/akka-agents/SKILL.md`
- `skills/akka-agent-component/SKILL.md`
- `skills/akka-agent-testing/SKILL.md`
- `skills/akka-agent-orchestration/SKILL.md`
- `skills/akka-agent-tools/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- official autonomous-agent docs listed in the research task as needed for API confirmation.

## In scope

Create focused skills such as:

- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-tasks/SKILL.md`
- `skills/akka-autonomous-agent-coordination/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `skills/akka-autonomous-agent-governance/SKILL.md`

Update `skills/README.md` and `pack/manifest.yaml` so the skills are discoverable/exported if that is the repository convention.

## Out of scope

- Do not add Java examples/tests in this task.
- Do not rewrite existing request-based agent skills beyond minimal cross-links.

## Expected outputs

- New skill files.
- Routing/manifest updates.
- Queue status update and focused commit.

## Required checks

- `git diff --check`
- `rg -n "akka-autonomous-agent|AutonomousAgent|TaskAcceptance|forAutonomousAgent|AutonomousAgentTools" skills pack/manifest.yaml skills/README.md`

## Done criteria

- Skills are low-token, action-oriented, and route to official docs only when needed.
- Skills clearly state when not to use Autonomous Agents.
- Commit message: `autonomous-agents: add skill family`.
