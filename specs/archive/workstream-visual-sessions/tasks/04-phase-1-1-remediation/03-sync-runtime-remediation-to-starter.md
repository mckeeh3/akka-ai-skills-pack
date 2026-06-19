# Task Brief: Sync Runtime Remediation to Starter Template

## Task

Sync the source frontend runtime remediation for background response focus and unseen-response indicators into the AI-first SaaS starter template.

## Required reads

- `AGENTS.md`
- `docs/workstream-visual-sessions.md`
- source files changed by `TASK-WVS-04-001` and `TASK-WVS-04-002`
- matching files under `templates/ai-first-saas-starter/frontend/src/**`
- starter template contract tests

## Expected outputs

- Starter template does not steal focus when a background response arrives.
- Starter template shows and clears left-rail unseen-response indicators at runtime.
- Starter template tests cover the corrected behavior with more than regex-only assertions where practical.

## Required checks

- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- `cd templates/ai-first-saas-starter/frontend && node --test src/workstream-visual-session.contract.test.mjs`
- `cd templates/ai-first-saas-starter/frontend && node --test src/workstream-shell.contract.test.mjs`
- `cd templates/ai-first-saas-starter/frontend && npm test`

## Constraints

- Preserve template placeholder syntax.
- Keep source and starter semantics aligned.
- Do not introduce Phase 2/3 persistence.

## Completion

Mark `TASK-WVS-04-003` done after commit.
