# TASK-WAEP-02-001: Wire domain service attention producers

## Objective

Wire bounded starter domain/service state changes to produce, update, and resolve attention items through the v1 shared attention backbone.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-event-producers-v2/README.md`
- `specs/workstream-attention-event-producers-v2/conversation-capture.md`
- `specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md`
- `specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md`
- `specs/workstream-attention-event-producers-v2/tasks/02-producers/01-wire-domain-service-producers.md`
- v2 producer contract from `TASK-WAEP-01-001`
- v1 attention contract and implementation files
- relevant starter services for invitation, governance, audit/trace, agent admin, and workstream surfaces

## Skills

- none unless a focused Akka component skill is needed by local implementation shape

## In scope

- Add producer/update calls for at least two concrete starter flows, prioritizing:
  - invitation delivery failure and later resolution/retry status;
  - governance proposal approval/rejection/activation or pending review;
  - provider readiness/failure evidence for Agent Admin or Audit/Trace.
- Ensure producer updates are idempotent by source id/idempotency key.
- Resolve or dismiss attention when the source condition clears.
- Preserve tenant/customer scope, capability visibility, source refs, and audit/trace behavior.
- Add backend tests for producer creation/update/resolution/idempotency/redaction.

## Out of scope

- Timed checks and worker/task attention unless trivial and already contract-defined for this task.
- Frontend update delivery.

## Expected outputs

- Updated starter backend services/producers.
- Backend tests.
- Updated queue status/notes.

## Required checks

- `git diff --check`
- scaffolded starter backend Maven tests covering producer behavior
- focused `rg` for producer ids and upsert/resolve paths

## Done criteria

- Real starter service state changes produce/update/resolve attention via the shared backbone.
- Producer behavior is idempotent and traced.
- Task changes and queue update are committed.

## Commit message

`attention-producers: wire domain producers`
