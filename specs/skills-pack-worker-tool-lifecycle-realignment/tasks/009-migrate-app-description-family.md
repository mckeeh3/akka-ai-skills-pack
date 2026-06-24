# TASK-009: Migrate app-description skill family

## Scope

Migrate the remaining `app-description-*`, `app-descriptions`, and `app-generate-app` skills after the pilot proves the doctrine.

## Required reads

- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-component-graph.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/intent-compiler-skill-contracts.md`
- All `skills-pack/skills/app-description-*/SKILL.md`
- `skills-pack/skills/app-descriptions/SKILL.md`
- `skills-pack/skills/app-generate-app/SKILL.md`

## Expected outputs

- Updated app-description family skills.
- Consistent phase/kind/family metadata or equivalent standardized header.
- Reduced duplicate doctrine where safe.

## Done criteria

- The family consistently treats app-description as the living graph.
- Workers and tools are first-class graph implications.
- Build/compile handoff language points to the compile contract.
- Security, observability, UI, test, and readiness skills preserve existing responsibilities.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
