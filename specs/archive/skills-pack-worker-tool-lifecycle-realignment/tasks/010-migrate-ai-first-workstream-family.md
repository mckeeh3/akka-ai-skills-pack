# TASK-010: Migrate AI-first SaaS, workstream, worker, policy, and surface family

## Scope

Migrate AI-first SaaS, worker decomposition, workstream, decision, policy, audit, outcome, object, admin-agent, and UI-surface architecture skills/docs to the new model.

## Required reads

- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-component-graph.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/workforce-decomposition.md`
- `skills-pack/docs/agent-workstream-application-architecture.md`
- `skills-pack/docs/structured-surface-contracts.md`
- `skills-pack/skills/ai-first-saas*/SKILL.md`
- `skills-pack/skills/agent-workstream-apps/SKILL.md`
- `skills-pack/skills/core-saas-foundation/SKILL.md`
- `skills-pack/skills/capability-first-backend/SKILL.md`

## Expected outputs

- Updated AI-first/workstream family skills and any necessary shared docs.
- Consistent worker, harness, actor adapter, governed tool, and capability language.

## Done criteria

- Human/software/system worker decomposition is explicit and consistent.
- Surfaces are consistently human-worker harnesses.
- Workstream actions consistently map to governed tools/capabilities.
- AI-backed workers never inherit human authority implicitly.
- Existing foundation/security/governance requirements are preserved.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
