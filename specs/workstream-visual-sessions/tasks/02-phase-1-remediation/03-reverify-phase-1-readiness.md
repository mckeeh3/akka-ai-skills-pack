# Task Brief: Reverify Phase 1 Readiness

## Task

Reverify phase 1 after the request-anchor remediation and update documentation/queue notes so they accurately reflect the implemented behavior.

## Required reads

- `AGENTS.md`
- `specs/workstream-visual-sessions/pending-tasks.md`
- `docs/workstream-visual-sessions.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream-visual-session.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/main.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream-visual-session.contract.test.mjs`

## Expected outputs

- `docs/workstream-visual-sessions.md` readiness notes updated only if needed.
- Phase 1 acceptance checklist remains checked only if source and starter implementations actually keep request surfaces anchored while response surfaces append.
- Any remaining limitations are documented as explicit follow-up work.
- Queue status update and git commit.

## Required checks

- `cd frontend && npm run typecheck`
- `cd frontend && node --test src/workstream-visual-session.contract.test.mjs`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- `cd templates/ai-first-saas-starter/frontend && node --test src/workstream-visual-session.contract.test.mjs`
- Run full frontend/template tests where practical; if an unrelated failure remains, document it with file/path evidence.

## Constraints

- Do not claim phase 2 browser-local persistence or phase 3 backend persistence.
- Do not mark remediation complete based only on regex tests if implementation evidence still contradicts the UX objective.

## Completion

Mark `TASK-WVS-02-003` done after commit.
