# TASK-008: Pilot migrate implementation and verification skills

## Scope

Apply the new compile contract to representative implementation and runtime verification skills.

## Required reads

- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/manual-test-reconciliation.md`
- `skills-pack/skills/akka-solution-decomposition/SKILL.md`
- `skills-pack/skills/akka-agent-tools/SKILL.md`
- `skills-pack/skills/akka-http-endpoint-component-client/SKILL.md`
- `skills-pack/skills/akka-web-ui-apps/SKILL.md`
- `skills-pack/skills/akka-runtime-feature-verification/SKILL.md`
- `skills-pack/skills/akka-manual-failure-reconciliation/SKILL.md`

## Expected outputs

- Updated representative implementation/verification skills.
- Compile/checklist language that preserves worker -> adapter -> tool -> capability -> Akka path.

## Done criteria

- Implementation skills require or inherit a compile contract before coding.
- Tool skills clearly separate governed tools from Akka components and endpoints.
- Web UI skill treats surfaces as human-worker harnesses.
- Runtime/manual verification skills test worker/adapter/tool/capability paths and reconcile findings.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
