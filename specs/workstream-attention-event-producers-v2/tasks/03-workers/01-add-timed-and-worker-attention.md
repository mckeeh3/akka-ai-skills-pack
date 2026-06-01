# TASK-WAEP-03-001: Add timed and worker/task attention producers

## Objective

Add bounded timed/stale checks and honest worker/task-state attention integration for the starter template.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-event-producers-v2/README.md`
- `specs/workstream-attention-event-producers-v2/conversation-capture.md`
- `specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md`
- `specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md`
- `specs/workstream-attention-event-producers-v2/tasks/03-workers/01-add-timed-and-worker-attention.md`
- v2 producer contract from `TASK-WAEP-01-001`
- v1 attention implementation files
- relevant starter timed action, invitation, audit summary worker, access-review worker, or provider-blocked state files

## Skills

- `akka-timed-actions` if implementing or changing an Akka Timed Action
- `akka-autonomous-agents` only if task-state integration touches actual AutonomousAgent APIs; do not fake model-backed completion

## In scope

- Add at least one bounded timed/stale attention producer, such as expiring invitations or stale blocked provider/runtime state.
- Add worker/task-state attention integration for starter-supported states such as blocked_provider_or_runtime, failed, cancelled, rejected, or waiting_for_human.
- Ensure task/worker attention links to progress/result/system-message surfaces and trace refs.
- Add tests for timed/stale producer idempotency, resolution, and honest blocked/fail-closed worker state handling.

## Out of scope

- Full AutonomousAgent task runtime if not already present.
- Personal digest generation.
- Frontend realtime delivery.

## Expected outputs

- Backend timed/stale and worker/task attention producer implementation.
- Tests.
- Updated queue status/notes.

## Required checks

- `git diff --check`
- scaffolded starter backend Maven tests covering timed/worker attention behavior
- focused `rg` proving blocked/provider-fail-closed states are represented honestly and no fake worker success path is introduced

## Done criteria

- At least one timed/stale condition updates attention through the shared backbone.
- Worker/task states create/update/resolve attention without claiming model-backed work is complete through deterministic substitutes.
- Task changes and queue update are committed.

## Commit message

`attention-producers: add timed worker attention`
