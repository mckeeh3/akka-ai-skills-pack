# Workstream Event Backbone v3

## Purpose

Create the next architecture/runtime increment after the release-ready attention backbone v1 and attention producers v2 work.

v1 established a shared backend-owned attention backbone. v2 added bounded service/timer/task producers and backend-derived refresh delivery. v3 should generalize that into a governed workstream event backbone for generated AI-first SaaS starter apps: domain events, workflow lifecycle events, task/provider events, consumers, source refs, projections, and optional update streams that feed attention, traces, dashboards, and future digests without making frontend state authoritative.

## Sequencing decision

The user chose to start Workstream Event Backbone v3 next. Broader AutonomousAgent runtime integration remains very important, but is intentionally sequenced after v3 because the event backbone should provide the typed event/source/ref/projection substrate that real AutonomousAgent task lifecycle integration will feed into.

## Source context

This mini-project builds on:

- `specs/workstream-attention-backbone-v1/`
- `specs/workstream-attention-event-producers-v2/`
- `specs/attention-release-readiness-dogfood/`
- `docs/workstream-dashboard-attention-event-backbone-wip.md`
- current starter backend/frontend attention implementation under `templates/ai-first-saas-starter/`

## Scope

- Define a starter v3 workstream event envelope and source-ref contract.
- Add bounded event publication/consumer paths for existing starter state changes.
- Map events to attention updates, dashboard projections, trace refs, and future digest/notification hooks.
- Add workflow lifecycle event handling for current starter workflows/process-like paths where present.
- Add optional backend-derived update delivery improvements only if they remain bounded.
- Preserve security, tenant/customer scope, AuthContext, audit, idempotency, and runtime completion doctrine.

## Non-goals

- Do not implement broad AutonomousAgent runtime hardening in this mini-project; prepare the event substrate for it.
- Do not build enterprise-wide notification/digest infrastructure.
- Do not replace existing attention v1/v2 functionality.
- Do not introduce a loose global message soup that bypasses governed capabilities.
- Do not make frontend/SSE/push state authoritative.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run its checks or record blockers, and make one focused commit.

## Sprint sequence

1. **Event contract and gap map** — define event envelope/source refs, event families, idempotency, consumers, projections, and v2-to-v3 migration targets.
2. **Domain event publication and consumers** — add bounded event emission and consumer/projection paths for existing starter service state changes.
3. **Workflow/task/provider lifecycle events** — add lifecycle event handling for starter workflow/process/provider states without faking AutonomousAgent success.
4. **Update delivery and projection hardening** — connect event-backed projections to existing attention refresh surfaces and optional streams if bounded.
5. **Docs/handoff** — update doctrine/starter docs to distinguish v1 attention, v2 producers, v3 event backbone, and future AutonomousAgent runtime integration.
6. **Verification** — prove v3 done state or append bounded follow-ups.

## Current implementation status

Tasks 01–04 have implemented the bounded starter/reference v3 runtime path: the contract exists, the starter includes typed event envelope/source-ref records, an Akka-backed event repository seam, event publication for invitation delivery and access-review lifecycle states, idempotent event-to-attention consumer behavior, backend-derived projection refresh hints, and targeted backend/frontend tests. See `event-backbone-v3-handoff.md` for the docs handoff that distinguishes v1, v2, v3, and future AutonomousAgent runtime work.

## Done state

The mini-project is complete when the starter/reference assets have:

- a documented v3 workstream event envelope/source-ref contract;
- bounded backend event publication for selected existing starter state changes;
- at least one Akka Consumer or equivalent governed event reaction path that updates attention/projections/traces from events;
- workflow/process/provider lifecycle events represented honestly and linked to attention/dashboard surfaces where claimed;
- tests for event idempotency, tenant isolation, authorization/redaction, trace/source refs, consumer retry/duplicate behavior, and projection results;
- docs updated so future agents know v3 exists and that broader AutonomousAgent runtime integration comes next;
- no runtime path where events let agents/workers bypass governed capabilities, auth, policy, audit, or tool boundaries.

## Open recommendation

After v3 is complete and release-readiness is verified, create a follow-up mini-project for real AutonomousAgent runtime integration: durable task lifecycle, task notifications, progress/result surfaces, cancellation/failure attention, and model/provider fail-closed behavior through the governed runtime path.
