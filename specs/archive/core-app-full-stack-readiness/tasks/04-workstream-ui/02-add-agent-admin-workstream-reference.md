# TASK-CORE-04-002: Add Agent Admin workstream reference

## Purpose

Add or strengthen frontend reference fixtures/surfaces for Agent Admin: definitions, prompt governance, skill governance, manifests, tool boundaries, model refs, test console, proposals, and traces.

## Required reads

- `specs/core-app-full-stack-readiness/core-workstream-api-contracts.md` if present
- `docs/workstream-ui-reference-architecture.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/agent-catalog-and-detail.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/prompt-and-skill-governance.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/skill-manifests-and-tool-permissions.md`
- `frontend/src/workstream/**`

## Expected outputs

- frontend fixtures/components/tests or docs needed to make Agent Admin a canonical workstream reference

## Required checks

- Agent Admin surfaces include loading, empty, forbidden, validation, diff/review, approval-required, and trace-linked states.
- Run frontend checks/build if frontend changes.
- `git diff --check`

## Done criteria

- Agent Admin UI is concrete enough for future generated apps.
- Queue status and changes are committed.
