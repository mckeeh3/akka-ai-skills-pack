# Workstream Attention Event Producers v2

## Purpose

Extend the completed `workstream-attention-backbone-v1` starter/reference implementation so attention items are produced and maintained from real backend events, scheduled checks, workflow/task states, and optional notification delivery instead of only direct service derivations.

v1 established the shared backend-owned attention backbone: `AttentionItem` lifecycle, scoped workstream/My Account/rail reads, core workstream derivations, safe redaction, audit traces, and frontend rendering. v2 should make that backbone operationally useful as an event-driven attention substrate for generated AI-first SaaS apps.

## Source discussion

After v1 completion, the recommended next step was a follow-up mini-project for intentionally deferred areas:

1. event producers from backend state changes;
2. timed/projection updates for overdue or stale attention;
3. AutonomousAgent task attention for failed, stuck, completed-with-review-needed, or rejected-result tasks;
4. realtime/notification delivery of backend-derived attention summaries;
5. docs/package updates so guidance accurately distinguishes the implemented v1 backbone, v2 producers, and future event/notification work.

The user accepted creating this v2 mini-project.

## Scope

This mini-project concerns source-repository starter/reference assets and guidance, primarily:

- `templates/ai-first-saas-starter/backend/**`
- `templates/ai-first-saas-starter/frontend/**`
- `docs/**` and starter docs only where needed to describe v1/v2 status accurately
- `specs/workstream-attention-backbone-v1/**` as source context

## Non-goals

- Do not rebuild the v1 attention backbone.
- Do not replace workstream-specific domain queues such as invitation, governance proposal, or access-review queues; v2 should publish/derive attention from them.
- Do not implement broad enterprise notification infrastructure.
- Do not fake AutonomousAgent task success or model-backed worker behavior; task-attention integration may use blocked/provider-fail-closed task states where that is the honest starter runtime state.
- Do not make realtime/frontend updates authoritative; backend attention state remains authoritative.

## Execution model

Execute one task per fresh harness context. Each implementation task must update `pending-tasks.md`, run its required checks, and make one focused commit.

Future task sessions should read, in order:

1. `AGENTS.md`
2. `skills/README.md`
3. this `README.md`
4. `conversation-capture.md`
5. selected sprint, backlog, queue entry, and task brief
6. `specs/workstream-attention-backbone-v1/attention-backbone-v1-contract.md` and the smallest relevant v1 implementation files
7. only focused source files listed by the selected task

## Sprint sequence

1. **Producer contract and gap map** — define event/timer/task/realtime producer contracts and identify v1 extension points.
2. **Domain event producers** — wire invitation, governance, audit/provider, and agent-admin state changes to attention upsert/resolve behavior.
3. **Timed and worker/task attention** — add bounded timed/stale checks and AutonomousAgent/task-state attention contracts for starter-supported blocked/deferred worker states.
4. **Realtime/notification delivery** — expose backend-derived attention changes to shell/rail refresh or stream mechanisms without making frontend state authoritative.
5. **Docs and package guidance** — update starter/guidance docs to reflect v1 implemented and v2 producer semantics.
6. **Verification** — prove the mini-project done state or append follow-up tasks.

## Done state

The mini-project is complete when the starter/reference assets have:

- a documented v2 producer contract for attention events, upserts, resolutions, idempotency, source refs, and audit traces;
- bounded backend producers that create/update/resolve attention from real starter state changes rather than static fixtures;
- scheduled/timed attention checks for at least one bounded starter case where time/staleness matters;
- task/worker attention integration for starter-supported internal worker states without pretending model-backed AutonomousAgent work is complete when it is not;
- backend-derived notification/realtime or refresh behavior for updated rail/My Account/workstream attention summaries;
- tests for producer idempotency, resolution, tenant isolation, redaction, lifecycle, audit/trace, and frontend update behavior;
- docs/guidance updated so future agents know v1 exists and v2 handles producers/notifications; and
- no normal runtime path that treats frontend-only state, demo fixtures, or fake worker success as authoritative attention behavior.

Follow-on `workstream-event-backbone-v3` has since added a bounded governed event backbone over selected starter invitation/access-review lifecycle events and backend-derived projection-refresh hints. v2 remains the producer/update-delivery contract; v3 is the typed event envelope/source-ref and consumer layer above it. Broad generated-app event coverage, enterprise notifications/digests, and real AutonomousAgent durable task runtime integration remain future initiatives unless separately implemented and verified.

## Open concerns

- If the starter lacks a suitable event publication mechanism for a producer, a task may implement a narrow service-level producer first and record a follow-up recommendation for richer Akka Consumers.
- Realtime may be implemented as explicit refresh/polling if that is the honest existing starter shell contract; do not overbuild streaming unless local patterns already support it.
