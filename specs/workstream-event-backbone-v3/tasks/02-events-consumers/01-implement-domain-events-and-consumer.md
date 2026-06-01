# TASK-WEB3-02-001: Implement domain events and consumer projection path

## Objective

Add bounded starter domain event publication and at least one governed consumer/projection reaction path into attention/projection state.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- v3 README, conversation capture, sprint, backlog, queue entry, and this task brief
- v3 contract from TASK-WEB3-01-001
- relevant starter backend attention/producer/domain service files
- Akka consumer guidance if implementing an Akka Consumer

## Skills

- `akka-consumers` if adding an Akka Consumer
- focused testing skills as needed

## In scope

- Add event records/envelopes and publication from at least one existing concrete starter flow, preferably invitation delivery or governance proposal state.
- Add a consumer/projection path that reacts to event(s) and updates attention/projection state idempotently.
- Preserve auth/source/capability/trace refs and tenant/customer isolation.
- Add tests for publication, duplicate/retry/idempotency, projection result, and safe redaction.

## Out of scope

- All domain events across the app.
- Broad event streaming infrastructure.
- AutonomousAgent runtime hardening.

## Required checks

- `git diff --check`
- scaffolded starter backend Maven tests covering event/consumer/projection behavior
- focused `rg` for event envelope, consumer, idempotency, and source refs

## Done criteria

- At least one real event-backed path updates attention/projection state.
- Duplicate events are safe.
- Task changes and queue update are committed.

## Commit message

`event-backbone: implement domain consumer path`
