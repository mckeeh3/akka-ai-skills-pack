# Task Brief: Fix Runtime Background Response Focus Steal

## Task

Fix the runtime bug where a response for a previously selected workstream still switches the visible workstream back after the user has moved to another workstream.

## Context

Phase 1.1 added a conditional around response-time `updateSelection`, but runtime behavior still steals focus. The likely implementation gap is that background-response detection depends on render/effect-updated selected-agent state rather than a synchronously updated selection ref/source of truth. Static regex tests were not sufficient to catch the real behavior.

## Required reads

- `AGENTS.md`
- `docs/workstream-visual-sessions.md`
- `specs/workstream-visual-sessions/pending-tasks.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/shell/WorkstreamShell.tsx`
- `frontend/src/workstream/rail/**`
- `frontend/src/workstream-visual-session.contract.test.mjs`
- `frontend/src/workstream-composer-message-api.contract.test.mjs`

## Expected outputs

- Selection source of truth used by async composer/action response handlers updates synchronously when the user selects a workstream.
- Composer success/error responses for a non-current workstream append/update that workstream without calling `updateSelection` or otherwise changing the visible workstream.
- Surface-action async responses also do not steal selection if the user switches workstreams before the action response resolves.
- Tests include behavior-oriented assertions strong enough to fail if async response handling can call selection update for a non-current workstream. Prefer extracted pure helpers or state-machine tests over regex-only assertions where practical.

## Required checks

- `cd frontend && npm run typecheck`
- `cd frontend && node --test src/workstream-visual-session.contract.test.mjs`
- `cd frontend && node --test src/workstream-composer-message-api.contract.test.mjs`

## Constraints

- Do not drop the background response.
- Do not weaken selected-workstream request anchoring.
- Do not introduce browser-local or backend persistence.
- Preserve deep-link selection behavior for explicit user navigation.

## Completion

Mark `TASK-WVS-04-001` done after commit.
