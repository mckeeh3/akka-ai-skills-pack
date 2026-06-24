# TASK-007: Pilot migrate app-description and surface/capability skills

## Scope

Apply the new doctrine to representative app-description compiler skills.

## Required reads

- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-component-graph.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/skills/app-descriptions/SKILL.md`
- `skills-pack/skills/app-description-input-normalization/SKILL.md`
- `skills-pack/skills/app-description-intake-router/SKILL.md`
- `skills-pack/skills/app-description-surface-modeling/SKILL.md`
- `skills-pack/skills/app-description-capability-modeling/SKILL.md`
- `skills-pack/skills/app-description-functional-agent-modeling/SKILL.md`

## Expected outputs

- Updated representative app-description skills with lifecycle/worker/tool/capability routing.
- Explicit handling of app-description worker and tool graph nodes in relevant skills.

## Done criteria

- Interview-phase skills capture worker/tool/capability implications without prematurely coding.
- Surface modeling treats surfaces as human-worker harnesses and actions as actor adapters for governed tools.
- Capability modeling treats governed tools as semantic operation exposures, not duplicated component methods.
- Functional-agent modeling distinguishes human and AI worker authority.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
