# TASK-WAEP-04-001: Wire backend-derived attention update delivery

## Objective

Expose backend-derived attention changes to the starter shell/rail/My Account/workstream surfaces through the smallest honest update mechanism supported by local patterns.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-event-producers-v2/README.md`
- `specs/workstream-attention-event-producers-v2/conversation-capture.md`
- `specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md`
- `specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md`
- `specs/workstream-attention-event-producers-v2/tasks/04-realtime/01-wire-attention-update-delivery.md`
- v2 producer contract from `TASK-WAEP-01-001`
- frontend/backend attention files from v1 and producer tasks
- starter endpoint/UI patterns for shell refresh, polling, SSE, or workstream actions

## Skills

- web UI/frontend skills as needed
- Akka HTTP endpoint/SSE skills only if adding backend streaming endpoints

## In scope

- Choose and implement one bounded update path:
  - explicit refresh endpoint/action;
  - polling/refresh after producer-affecting actions;
  - SSE/stream if already supported and small enough.
- Ensure rail/My Account/workstream attention summaries are refreshed from backend-derived data.
- Preserve transient `railAttentionState` for unseen responses only.
- Add frontend/backend contract tests for update delivery, empty/denied states, and no frontend-only authoritative counts.

## Out of scope

- Broad push notification infrastructure.
- Personal digest emails.
- Redesigning the workstream shell.

## Expected outputs

- Updated backend endpoints or action responses if needed.
- Updated frontend client/shell/surface refresh behavior.
- Tests.
- Updated queue status/notes.

## Required checks

- `git diff --check`
- targeted frontend tests/typecheck/build
- targeted scaffolded backend tests if backend endpoints/actions change
- focused `rg` proving actionable attention update delivery uses backend summaries rather than frontend-only state

## Done criteria

- Users can see updated backend-derived attention summaries/items after producer-affecting changes through the selected update path.
- Frontend-only state is not authoritative.
- Task changes and queue update are committed.

## Commit message

`attention-producers: wire update delivery`
