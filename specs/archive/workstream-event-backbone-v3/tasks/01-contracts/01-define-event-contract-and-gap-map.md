# TASK-WEB3-01-001: Define event contract and gap map

## Objective

Define the v3 workstream event envelope/source-ref contract and map v2 producer paths to bounded event-backed implementation targets.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-event-backbone-v3/README.md`
- `specs/workstream-event-backbone-v3/conversation-capture.md`
- `specs/workstream-event-backbone-v3/sprints/01-workstream-event-backbone-v3-sprint.md`
- `specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md`
- `specs/workstream-event-backbone-v3/tasks/01-contracts/01-define-event-contract-and-gap-map.md`
- `specs/workstream-attention-event-producers-v2/attention-event-producers-v2-contract.md`
- `docs/workstream-dashboard-attention-event-backbone-wip.md`
- focused starter attention/producer files found with `rg -n "AttentionProducer|AttentionService|attention\.producer|upsertAttention|resolveAttention" templates/ai-first-saas-starter/backend/src/main/java`

## In scope

- Create `specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md`.
- Define event envelope fields: event id, event type, tenant/customer/AuthContext scope, actor/caller, source refs, capability/governed-tool refs, correlation/idempotency keys, trace refs, payload class, and redaction hints.
- Define event families: domain, workflow/process, task/worker, provider/config, attention lifecycle, audit/work trace.
- Define publication and consumer/projection rules.
- Map v2 producers to v3 event-backed candidates.
- Identify exact bounded implementation tasks and any blockers.

## Required checks

- `git diff --check`
- focused `rg` proving contract names event envelope, source refs, idempotency, consumers/projections, workflow lifecycle, provider events, and AutonomousAgent follow-up sequencing

## Done criteria

- Implementation tasks can proceed without guessing event fields, boundaries, or consumer behavior.
- Contract explicitly prevents bypassing governed capabilities/auth/audit.
- Task changes and queue update are committed.

## Commit message

`event-backbone: define v3 contract`
