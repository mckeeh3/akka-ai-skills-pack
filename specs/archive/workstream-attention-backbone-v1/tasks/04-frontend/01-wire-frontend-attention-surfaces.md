# TASK-WAB-04-001: Wire frontend attention surfaces to backend-derived data

## Objective

Update the starter frontend/API contracts so left rail, My Account, and dashboard attention render backend-derived attention summaries/items while keeping transient unseen-response indicators separate.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-backbone-v1/README.md`
- `specs/workstream-attention-backbone-v1/conversation-capture.md`
- `specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md`
- `specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md`
- `specs/workstream-attention-backbone-v1/tasks/04-frontend/01-wire-frontend-attention-surfaces.md`
- contract artifact from `TASK-WAB-01-001`
- backend API/contracts from `TASK-WAB-02-001` and `TASK-WAB-03-001`
- `templates/ai-first-saas-starter/frontend/src/workstream/**`
- existing frontend contract tests matching `*attention*`, `*workstream*`, and `*my-account*`

## Skills

- web UI/frontend skills only if needed after reading local frontend patterns

## In scope

- Add/update TypeScript types for backend attention item and summary payloads.
- Wire functional-agent summaries or shell API state so left-rail attention counts/severity come from backend-derived summary data.
- Render My Account personal attention items with source workstream, severity, status, action, and trace affordances.
- Render workstream dashboard attention items consistently from surface payloads backed by shared attention.
- Preserve frontend `railAttentionState` only for transient unseen/background response badges; tests must not confuse it with backend actionable attention.
- Update fixtures/contract tests to represent backend-derived attention.

## Out of scope

- New visual redesign beyond the minimal necessary rendering/state wiring.
- Realtime streaming unless already present and trivial to preserve.

## Expected outputs

- Updated frontend types/components/fixtures/tests.
- Updated queue status/notes.

## Required checks

- `git diff --check`
- targeted frontend contract/type/build tests from `templates/ai-first-saas-starter/frontend`
- focused `rg` proving actionable attention counts are not sourced only from `railAttentionState`

## Done criteria

- Left rail and My Account attention UI consume backend-derived data at v1 scope.
- Transient unseen-response badges remain visually/functionally separate.
- Frontend tests cover rendering, empty/denied states, and trace/action metadata.
- Task changes and queue update are committed.

## Commit message

`attention-backbone: wire frontend surfaces`
