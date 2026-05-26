# Task Brief: Sync Request Anchor Fix to Starter Template

## Task

Apply the corrected phase 1 request-anchor behavior from the source frontend to the AI-first SaaS starter template.

## Required reads

- `AGENTS.md`
- `specs/workstream-visual-sessions/README.md`
- `docs/workstream-visual-sessions.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/stream/WorkstreamStream.tsx`
- `frontend/src/workstream/visual-session/visualSessionState.ts`
- `frontend/src/workstream-visual-session.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/main.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/stream/WorkstreamStream.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/visual-session/visualSessionState.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream-visual-session.contract.test.mjs`

## Expected outputs

- Starter template matches the source frontend behavior for request anchoring during response append.
- Starter template contract tests assert that composer, surface-open, and surface-action flows keep request items as anchors while response surfaces append below.
- Template placeholder syntax remains intact.

## Required checks

- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- `cd templates/ai-first-saas-starter/frontend && node --test src/workstream-visual-session.contract.test.mjs`
- `cd templates/ai-first-saas-starter/frontend && npm test`
- Optional but recommended: compare relevant source/template files and document intentional differences.

## Constraints

- Do not broaden scope to browser-local or backend persistence.
- Do not replace governed runtime behavior with fixture-only behavior.
- Keep source and starter semantics aligned.

## Completion

Mark `TASK-WVS-02-002` done after commit.
