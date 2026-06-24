# TASK-011: Migrate agent and governed-agent skill family

## Scope

Migrate Akka agent, autonomous agent, and governed-agent skills to the worker/tool/capability model.

## Required reads

- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/governed-agent-substrate.md`
- `skills-pack/skills/akka-agents/SKILL.md`
- `skills-pack/skills/akka-agent-*/SKILL.md`
- `skills-pack/skills/akka-autonomous-agent*/SKILL.md`
- `skills-pack/skills/akka-autonomous-agents/SKILL.md`

## Expected outputs

- Updated agent/governance skills.
- Clear separation of software workers, agent runtimes, agent tool adapters, governed tools, and backend capabilities.

## Done criteria

- Agent skills treat Akka Agent/AutonomousAgent as worker harnesses, not as business operations themselves.
- Tool-boundary, prompt, skill, reference, model, and work-trace governance uses the canonical actor-adapter/tool terminology.
- Agent tool exposure requires explicit governed tool inclusion and policy; human availability is not inherited.
- Existing fail-closed provider/config and trace doctrine is preserved.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
