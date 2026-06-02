# Task Brief: Marketplace Prompts and Tenant-Managed Tool Binding Foundation

## Objective

Add safe governance foundations for marketplace prompts and tenant-managed tool binding without allowing tenant-managed text or arbitrary class names to grant backend authority.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/04-enterprise-extensions-sprint.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `skills/ai-first-saas-policy-governance/SKILL.md`

## In scope

- Marketplace prompt catalog/proposal/import lifecycle, provenance/checksum/versioning, approval gates, tenant customization rules, and audit.
- Tenant-managed tool-binding requests that select only backend-owned stable tool ids from an approved registry.
- Tests for authority expansion denial, malicious prompt/skill text, unapproved tools, tenant isolation, and trace creation.

## Out of scope

- Arbitrary tenant-supplied Java classes or unreviewed executable code.
- Marketplace publication/distribution service outside the starter.

## Checks

- `git diff --check`
- focused backend governance/tool-boundary tests
- frontend tests/typecheck/build if surfaces change
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Marketplace/tool-binding foundations are governed, auditable, and cannot bypass backend `ToolPermissionBoundary` enforcement.
