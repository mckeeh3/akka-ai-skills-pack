# Task Brief: Fix Runtime Unseen Response Indicator

## Task

Fix the runtime bug where no left-rail unseen-response indicator appears when a non-selected workstream receives a response.

## Context

Phase 1.1 added rail attention types and badge markup, but runtime testing shows no visible indicator. This likely shares the same root as focus steal: the app does not reliably classify the response as background activity. It may also require ensuring the rail receives updated `railAttentionByAgentId` and that the badge is visible in both expanded and collapsed rail states.

## Required reads

- `AGENTS.md`
- `docs/workstream-visual-sessions.md`
- `docs/workstream-ui-reference-architecture.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/rail/FunctionalAgentRail.tsx`
- `frontend/src/workstream/rail/FunctionalAgentRailItem.tsx`
- `frontend/src/workstream/rail/railState.ts`
- `frontend/src/workstream/types/agents.ts`
- `frontend/src/styles/components.css`
- `frontend/src/workstream-shell.contract.test.mjs`
- `frontend/src/workstream-visual-session.contract.test.mjs`

## Expected outputs

- Background composer success/error responses increment in-memory unseen state for the originating workstream.
- Left rail renders a visible and accessible badge/dot/count for that workstream.
- Indicator is visible in expanded and collapsed rail modes.
- Indicator clears when the user selects the workstream.
- Behavior-oriented tests verify badge state appears after a simulated background response and clears on selection.

## Required checks

- `cd frontend && npm run typecheck`
- `cd frontend && node --test src/workstream-shell.contract.test.mjs`
- `cd frontend && node --test src/workstream-visual-session.contract.test.mjs`

## Constraints

- Indicator remains visual/session state only; it is not authorization.
- Do not persist indicator state to browser storage or backend.
- Do not hide or replace existing backend-provided `attention` counts.

## Completion

Mark `TASK-WVS-04-002` done after commit.
