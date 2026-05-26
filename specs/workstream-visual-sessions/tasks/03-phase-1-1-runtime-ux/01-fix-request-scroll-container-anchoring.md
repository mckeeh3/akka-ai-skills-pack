# Task Brief: Fix Request Scroll Container Anchoring

## Task

Fix the runtime UX bug where request surfaces are selected as anchors but do not scroll to the top of the visible workstream panel.

## Required reads

- `AGENTS.md`
- `docs/workstream-visual-sessions.md`
- `docs/workstream-ui-reference-architecture.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/stream/WorkstreamStream.tsx`
- `frontend/src/workstream/shell/WorkstreamPanel.tsx`
- `frontend/src/styles/layout.css`
- `frontend/src/styles/components.css`
- `frontend/src/workstream-visual-session.contract.test.mjs`

## Expected outputs

- The source frontend scrolls the actual workstream panel/stream container so the request surface is aligned at the top of the visible panel.
- The implementation accounts for fixed/sticky context/composer layout offsets where needed.
- Reduced-motion behavior remains respected.
- Contract tests cover direct container scroll behavior or explicit scroll-container targeting, not only `scrollIntoView` existence.

## Required checks

- `cd frontend && npm run typecheck`
- `cd frontend && node --test src/workstream-visual-session.contract.test.mjs`
- Run targeted related frontend tests for shell/layout/visual-session behavior.

## Constraints

- Preserve traditional chat ordering.
- Keep the request item/surface as the anchor, not the response surface.
- Do not introduce browser-local or backend persistence.

## Completion

Mark `TASK-WVS-03-001` done after commit.
