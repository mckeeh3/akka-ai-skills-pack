# AutonomousAgent Runtime Integration

## Purpose

Create the follow-up mini-project after Workstream Event Backbone v3 to make durable internal/background agent work a first-class runtime participant in the starter workstream architecture.

The first concrete vertical is **User Admin Access Review AutonomousAgent** because the starter already has access-review attention/task-state groundwork, blocked/provider-fail-closed surfaces, and v3 event backbone source refs. This mini-project should replace placeholder/blocked worker semantics with a real bounded Akka `AutonomousAgent` runtime path where feasible, without using deterministic fake success as normal runtime behavior.

## Source context

This project builds on:

- `specs/workstream-attention-backbone-v1/`
- `specs/workstream-attention-event-producers-v2/`
- `specs/attention-release-readiness-dogfood/`
- `specs/workstream-event-backbone-v3/`
- current starter backend/frontend under `templates/ai-first-saas-starter/`

## Scope

- Design the first durable internal/background agent vertical: User Admin Access Review.
- Implement real Akka `AutonomousAgent` task lifecycle where supported by local SDK patterns.
- Expose governed capabilities for task start/query/lifecycle/result review.
- Emit task lifecycle/progress/result/failure events into the v3 workstream event backbone.
- Convert task states into attention items through the existing attention backbone/producers.
- Render progress/result/decision surfaces in User Admin and My Account/rail attention paths.
- Preserve provider/model fail-closed behavior with actionable errors when configuration is missing.
- Add tests proving no deterministic/model-less normal runtime substitute is used to mark model-backed work complete.

## Non-goals

- Do not implement every future AutonomousAgent worker.
- Do not create a generic team/delegation framework in this first slice.
- Do not bypass governed runtime agent foundation, AuthContext, capabilities, policy, audit, or tool boundaries.
- Do not count test fixtures or deterministic fakes as normal runtime success.
- Do not weaken v1/v2/v3 attention/event semantics.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run required checks or record blockers, and make one focused commit.

## Sprint sequence

1. **Runtime contract and SDK gap check** — define User Admin Access Review AutonomousAgent contract and confirm Akka SDK patterns.
2. **Backend runtime integration** — implement governed task start/query/lifecycle and AutonomousAgent setup/invocation path.
3. **Events, attention, and surfaces** — wire task lifecycle to v3 events, attention, and structured surfaces.
4. **Runtime validation** — prove provider fail-closed and model-backed path behavior through scaffolded local validation.
5. **Docs/handoff** — document current AutonomousAgent integration status and future workers.
6. **Verification** — confirm done state or append bounded follow-ups.

## Done state

The mini-project is complete when the starter/reference assets have:

- a documented User Admin Access Review AutonomousAgent runtime contract;
- real governed task start/query/lifecycle capability paths;
- Akka `AutonomousAgent` integration or an explicitly documented blocker if local SDK/runtime support is insufficient;
- provider/model fail-closed behavior for missing configuration;
- task lifecycle/progress/result/failure events emitted into the v3 event backbone;
- attention items derived from task states without frontend-only or fake success shortcuts;
- structured progress/result/decision surfaces visible through User Admin/My Account paths;
- tests for authorization, tenant isolation, lifecycle, idempotency, event/attention linkage, provider fail-closed, and no model-less normal success;
- docs updated to distinguish this first AutonomousAgent vertical from future broader runtime/team/delegation work.
