# Task Brief: Add Left Rail Unseen Response Indicator

## Task

Add a visual indicator in the left rail when a non-selected workstream receives an unseen response or other material background activity.

## Required reads

- `AGENTS.md`
- `docs/workstream-visual-sessions.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/workstream-ui-reference-architecture.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/rail/**`
- `frontend/src/workstream/types/agents.ts`
- `frontend/src/workstream/fixtures/agents.ts`
- `frontend/src/styles/components.css`
- `frontend/src/workstream-shell.contract.test.mjs`
- `frontend/src/workstream-visual-session.contract.test.mjs`

## Expected outputs

- In-memory rail attention/unseen state for workstreams that receive background responses.
- Left rail item renders an accessible unseen-response indicator, such as a badge/dot/count.
- Indicator clears when the user selects that workstream.
- Indicator distinguishes at least normal unseen response from stronger future states via extensible state shape, while keeping the first UI simple.
- Tests cover indicator rendering and clearing on selection.

## Required checks

- `cd frontend && npm run typecheck`
- `cd frontend && node --test src/workstream-shell.contract.test.mjs`
- `cd frontend && node --test src/workstream-visual-session.contract.test.mjs`

## Constraints

- Indicator is visual/session state, not authorization state.
- Do not persist unseen state to browser storage or backend in this task.
- Do not make left rail availability depend on frontend-only indicator state.

## Completion

Mark `TASK-WVS-03-003` done after commit.
