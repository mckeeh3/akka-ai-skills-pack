# Sprint 01: Attention Event Producers v2

## Objective

Extend the completed v1 attention backbone so starter attention is produced, updated, resolved, and delivered from real backend state changes, timed checks, task states, and shell update mechanisms.

## Scope

- Define v2 producer contracts for event/service/timer/task sources.
- Wire bounded starter domain producers to `AttentionService` upsert/resolve lifecycle.
- Add timed/stale checks for at least one starter attention category.
- Add task/worker attention integration for honest starter worker states.
- Add backend-derived rail/My Account/workstream attention update delivery.
- Update starter/docs guidance to reflect v1 implemented and v2 producer semantics.

## Source context

- `specs/workstream-attention-backbone-v1/README.md`
- `specs/workstream-attention-backbone-v1/attention-backbone-v1-contract.md`
- `specs/workstream-attention-backbone-v1/pending-tasks.md`
- v1 implementation files under `templates/ai-first-saas-starter/backend/**` and `frontend/**`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-workstream-application-architecture.md`

## Acceptance criteria

- Producer contract clearly states source event identity, idempotency key, upsert/resolve rules, source refs, and audit trace behavior.
- At least two existing starter state changes update/resolve attention items through the shared backbone.
- At least one timed/stale check updates attention through the shared backbone.
- Worker/task attention exposes blocked/provider-fail-closed or lifecycle states honestly, without fake success.
- Shell/rail/My Account/workstream attention can refresh or receive updates from backend-derived summaries.
- Tests prove idempotency, resolution, tenant isolation/redaction, audit/trace, and frontend update behavior.

## Handoff notes

If a richer Akka Consumer/TimedAction/SSE path is too large for one task, implement the smallest honest starter-local slice and record a follow-up recommendation rather than weakening completion claims.
