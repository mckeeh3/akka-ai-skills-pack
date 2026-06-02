# Task Brief: Prevent Background Response Selection Steal

## Task

Prevent an async response for a non-selected workstream from automatically switching the visible workstream back to that response's workstream.

## Required reads

- `AGENTS.md`
- `docs/workstream-visual-sessions.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/shell/**`
- `frontend/src/workstream/rail/**`
- `frontend/src/workstream-visual-session.contract.test.mjs`
- `frontend/src/workstream-composer-message-api.contract.test.mjs`

## Expected outputs

- Composer response handling appends/updates the originating workstream's items and surfaces without stealing selection when the user has switched to another workstream.
- If the originating workstream is still selected, current behavior remains: response surfaces appear below the request and selected surface state may update as appropriate.
- If the originating workstream is not selected, selection remains unchanged and the response becomes background activity for the originating workstream.
- Contract tests cover submitting in Workstream A, switching to Workstream B, and receiving A's response without switching back to A.

## Required checks

- `cd frontend && npm run typecheck`
- `cd frontend && node --test src/workstream-visual-session.contract.test.mjs`
- `cd frontend && node --test src/workstream-composer-message-api.contract.test.mjs`

## Constraints

- Do not drop or hide the background response.
- Do not weaken request anchoring for the selected workstream.
- Do not introduce persistence beyond component-backed Phase 1/1.1 state.

## Completion

Mark `TASK-WVS-03-002` done after commit.
