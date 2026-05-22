# Sprint 03: Seed and Starter User Admin Expertise Example

## Objective

Make `user-admin-agent` the canonical proof that a workstream can be backed by real governed expertise: seeded prompt, skills, reference documents, manifest, tool boundary, trace expectations, and tests.

## Scope

Likely source files:

- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/**`
- `docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/05-managed-agent-foundation.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/test-index.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/prompt-and-skill-governance.md`
- `templates/ai-first-saas-starter/**` seed resources, backend fixtures, frontend fixtures, and contract tests where applicable
- `src/main/resources/agent-behavior-seeds/**` or reference examples if this repo's executable examples are the better canonical target

## Deliverables

- User Admin workstream expertise artifact with process knowledge, assigned skills, reference docs, callable capabilities, denials, and tests.
- Seed/starter resources demonstrate the default skill/reference bundle and manifest entries.
- Tests or fixture checks prove manifest display, authorized load, denied unassigned load, and trace visibility.

## Checks

- `git diff --check`
- Relevant existing unit/contract tests for touched starter/example assets, or documented docs-only limitation if no runtime code is touched.
