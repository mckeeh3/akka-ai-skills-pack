# TASK-WEB3-04-001: Harden event-backed update delivery

## Objective

Ensure event-backed attention/projection updates are visible through existing backend-derived shell/rail/My Account/workstream update paths, and optionally add a bounded stream only if supported by local patterns.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- v3 README, conversation capture, sprint, backlog, queue entry, and this task brief
- v3 contract from TASK-WEB3-01-001
- implementation from TASK-WEB3-02-001 and TASK-WEB3-03-001
- existing frontend/backend attention update delivery files

## Skills

- web UI/frontend skills as needed
- endpoint/SSE skills only if adding a stream endpoint

## In scope

- Verify and adjust backend refresh/update delivery so event-backed projection changes appear in left rail, My Account, and dashboard surfaces.
- Add optional bounded event/attention stream only if small and honest.
- Add tests for update visibility, stale/refresh behavior, denied states, and frontend-only authority guardrails.

## Out of scope

- Enterprise push notification infrastructure.
- Notification center/digests.

## Required checks

- `git diff --check`
- frontend tests/typecheck/build if frontend changes are made
- scaffolded backend tests if backend endpoints/actions change
- focused `rg` proving backend-derived event-backed updates are used

## Done criteria

- Event-backed projection changes are visible through supported update paths.
- Frontend state is not authoritative.
- Task changes and queue update are committed.

## Commit message

`event-backbone: harden update delivery`
