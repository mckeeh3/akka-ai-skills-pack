# Task Brief: Governed Artifact Lifecycle History

## Objective

Add richer event-sourced lifecycle history for governed runtime artifacts: prompts, skills, references, manifests, and tool boundaries.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/01-durable-core-sprint.md`
- `templates/ai-first-saas-starter/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `skills/akka-event-sourced-entities/SKILL.md`

## In scope

- Lifecycle event contracts for draft, submit, review, approve, activate, deprecate, rollback, archive, seed-import, and denial/no-op transitions.
- Prompt/skill/reference version snapshot links and checksums.
- Manifest/tool-boundary lifecycle history and authority-expansion audit facts.
- Tests for tenant isolation, immutable snapshots, rollback, activation denial, and trace references.

## Out of scope

- Arbitrary tenant-managed Java class binding.
- Prompt/skill text granting backend authority.

## Checks

- `git diff --check`
- focused rendered-scaffold governed-agent lifecycle tests
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Governed artifact lifecycle history works through the rendered Akka runtime at the stated scope and is reflected in safe admin/audit DTOs.
