# TASK-005: Update routing map and skill metadata contract

## Scope

Update skills-pack routing and contracts so the three-phase lifecycle and worker/tool/capability model become visible entry points for future skill migrations.

## Required reads

- `skills-pack/skills/README.md`
- `skills-pack/docs/intent-compiler-skill-contracts.md`
- `skills-pack/docs/intent-to-realization-flow.md`
- `skills-pack/pack/manifest.yaml`
- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-component-graph.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md`

## Expected outputs

- Updated `skills-pack/skills/README.md` with lifecycle-first routing.
- Updated skill contract docs with standard classification fields.
- Conservative manifest metadata additions or a documented deferred plan if manifest shape changes are too risky for this task.

## Done criteria

- Defines standard classification fields: phase, kind, family, consumes, produces, routes-to.
- Explains where interview, build/compile, and manual-test skills fit.
- Explains worker -> adapter -> tool -> capability -> Akka implementation routing.
- Does not break existing public skill names.
- Avoids making manifest changes that require installer changes unless validation is updated in the same task.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
