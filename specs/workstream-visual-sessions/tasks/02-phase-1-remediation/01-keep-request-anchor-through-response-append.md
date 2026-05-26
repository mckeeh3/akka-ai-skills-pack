# Task Brief: Keep Request Anchor Through Response Append

## Task

Fix the phase 1 visual-session implementation so the user request surface remains the active scroll anchor while response surfaces append below it.

## Required reads

- `AGENTS.md`
- `specs/workstream-visual-sessions/README.md`
- `specs/workstream-visual-sessions/conversation-capture.md`
- `docs/workstream-visual-sessions.md`
- `docs/workstream-ui-reference-architecture.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/stream/WorkstreamStream.tsx`
- `frontend/src/workstream/visual-session/visualSessionState.ts`
- `frontend/src/workstream-visual-session.contract.test.mjs`

## Expected outputs

- Composer success path keeps the submitted/restored user request item as the request scroll target while agent/markdown response surfaces append below it.
- Composer error path preserves the original user request anchor and appends the safe error/system surface below it, unless the product intentionally defines error anchoring differently and documents/tests that choice.
- Surface-open request flow anchors the `surface-request-*` item, not the response surface id.
- Surface-action flow anchors the `surface-action-request-*` item and does not retarget to the result surface when the response arrives.
- Correlation ids for request and response items are aligned where turn grouping depends on correlation.
- Source frontend contract tests cover the corrected behavior.

## Required checks

- `cd frontend && npm run typecheck`
- `cd frontend && node --test src/workstream-visual-session.contract.test.mjs`
- Run broader `cd frontend && npm test` if the unrelated seed-manifest path issue has been resolved; otherwise record that unrelated failure in task notes.

## Constraints

- Preserve traditional chat order: older turns above, newer turns below.
- Do not introduce browser-local or backend persistence.
- Do not treat visual-session state as authorization state.
- Do not retarget the active request anchor to response surfaces while responses append.

## Completion

Mark `TASK-WVS-02-001` done after commit.
