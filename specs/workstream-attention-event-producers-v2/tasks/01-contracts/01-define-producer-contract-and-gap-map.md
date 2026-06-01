# TASK-WAEP-01-001: Define producer contract and v1 gap map

## Objective

Define the v2 attention producer contract and map v1 derivations to concrete producer/update tasks.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-event-producers-v2/README.md`
- `specs/workstream-attention-event-producers-v2/conversation-capture.md`
- `specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md`
- `specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md`
- `specs/workstream-attention-event-producers-v2/tasks/01-contracts/01-define-producer-contract-and-gap-map.md`
- `specs/workstream-attention-backbone-v1/attention-backbone-v1-contract.md`
- `specs/workstream-attention-backbone-v1/pending-tasks.md`
- focused v1 backend/frontend attention files found with `rg -n "AttentionItem|AttentionService|attention\.list|attention\.open|railAttentionState|attentionSource" templates/ai-first-saas-starter`

## Skills

- none; starter/reference contract task

## In scope

- Create a focused v2 contract/gap-map artifact under `specs/workstream-attention-event-producers-v2/`.
- Define producer ids, source event/state identity, idempotency keys, upsert/resolve behavior, audit/trace fields, tenant/customer/AuthContext scope, and tests.
- Map v1 derivations to v2 producer candidates:
  - invitation delivery failure/resolution/expiry;
  - governance proposal approval state;
  - audit/provider failure evidence;
  - Agent Admin provider readiness;
  - worker/task blocked/provider-fail-closed states.
- Identify update delivery target: refresh, polling, SSE, or existing shell mechanism.

## Out of scope

- Java/TypeScript runtime implementation.
- Whole-repository event architecture redesign.

## Expected outputs

- `specs/workstream-attention-event-producers-v2/attention-event-producers-v2-contract.md` or equivalent.
- Updated `pending-tasks.md` status/notes.

## Required checks

- `git diff --check`
- focused `rg` proving the contract names producer ids, idempotency, upsert/resolve, timed checks, task-state attention, and update delivery.

## Done criteria

- Later implementation tasks can proceed without guessing producer identity, lifecycle behavior, or update delivery expectations.
- Contract preserves backend attention state as authoritative.
- Task changes and queue update are committed.

## Commit message

`attention-producers: define v2 contract`
