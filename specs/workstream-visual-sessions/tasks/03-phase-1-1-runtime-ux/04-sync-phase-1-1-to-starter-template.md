# Task Brief: Sync Phase 1.1 Runtime UX to Starter Template

## Task

Sync the Phase 1.1 runtime UX fixes from the source frontend into the AI-first SaaS starter template.

## Required reads

- `AGENTS.md`
- `docs/workstream-visual-sessions.md`
- source frontend files changed by `TASK-WVS-03-001` through `TASK-WVS-03-003`
- matching files under `templates/ai-first-saas-starter/frontend/src/**`
- starter template contract tests

## Expected outputs

- Starter template request surfaces scroll to the top of the actual visible workstream panel.
- Starter template does not switch workstreams when a background response arrives.
- Starter template left rail shows and clears unseen-response indicators.
- Starter template tests cover the synced behavior.

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

Mark `TASK-WVS-03-004` done after commit.
