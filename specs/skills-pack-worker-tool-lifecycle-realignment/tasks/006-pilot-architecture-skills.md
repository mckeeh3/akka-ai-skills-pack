# TASK-006: Pilot migrate architecture and workforce skills

## Scope

Apply the new lifecycle and worker/tool/capability doctrine to a representative architecture skill set before broad migration.

## Required reads

- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-component-graph.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/skills/ai-first-saas/SKILL.md`
- `skills-pack/skills/agent-workstream-apps/SKILL.md`
- `skills-pack/skills/ai-first-saas-worker-decomposition/SKILL.md`
- `skills-pack/skills/capability-first-backend/SKILL.md`
- `skills-pack/skills/core-saas-foundation/SKILL.md`

## Expected outputs

- Updated pilot architecture skills with compact metadata/classification and canonical doc references.
- Removed or shortened repeated doctrine where safe.

## Done criteria

- Pilot skills route through the three-phase lifecycle where relevant.
- Worker/tool/capability terminology is consistent.
- Broad skills remain routing-oriented and do not duplicate long canonical docs.
- Existing security, tenant, governance, runtime completion, and foundation constraints are preserved.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
