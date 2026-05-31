# Description and Decomposition Path Review

## Scope

Task: `TASK-05-002`

Reviewed description-first and direct decomposition paths for stale content after the capability-first backend migration:

- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`
- `docs/description-first-application-doctrine.md`
- `docs/prd-to-akka-flow.md`
- `docs/examples/purchase-request-solution-plan.md`
- `docs/examples/purchase-request-app-description/README.md`
- `docs/examples/purchase-request-app-description/app-description/10-capabilities/*`
- `skills/app-descriptions/SKILL.md`
- `skills/app-description-*/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`

## Findings

### Resolved in this task

1. `skills/app-description-intake-router/SKILL.md` still described AI-first detection before routing to "CRUD, behavior, or generation work".
   - Fix: changed this to route to capability, behavior, UI, or generation work so broad input does not imply a CRUD-first description path.

2. The now-removed app-description skill-plan backlog still summarized `app-description-capability-modeling` as broad capability names/goals/actors only.
   - Fix at the time: expanded the capability-modeling contract to include stable capability ids/classes, AuthContext/scope, schemas, idempotency, policy/approval, audit/work-trace obligations, exposure surfaces, and links to UI/readiness/traceability.

3. The purchase-request app-description mechanics example had a very thin capability layer that did not demonstrate the capability-first contract fields now required by the app-description path.
   - Fix: updated the capability index and capability file to show capability ids/classes, actors/callers, AuthContext/scope, validation, outputs/denials, side effects, idempotency, approval, audit/trace, selected surfaces, tests, and linked artifacts while preserving the example's conventional/reference-material status.

4. `docs/examples/purchase-request-solution-plan.md` used the older solution-plan shape and skipped the explicit secure foundation, capability-to-component mapping, and capability contract details required by `akka-solution-decomposition`.
   - Fix: rewrote the plan in the current capability-aware shape with AI-first classification, scoped AuthContext assumptions, capability summary, capability-to-component mapping, skill routing, implementation order, and tests that preserve capability semantics.

### No cleanup required in this task

- `docs/internal-app-description-architecture.md` and `docs/app-description-maintenance-flow.md` already make `10-capabilities/` the governed backend contract layer before downstream behavior, security, UI, observability, readiness, generation, and component choices.
- `skills/app-descriptions/SKILL.md` already routes broad description work through AI-first interpretation, core SaaS foundation, and capability modeling before behavior/tests/security/UI/readiness/generation.
- Focused app-description skills already preserve capability fields and linked layer impacts; no broad rewrite was needed.
- `skills/app-generate-app/SKILL.md` already blocks generation when secure foundation, capability, AuthContext, authorization, audit, approval, or operating-model semantics would otherwise be invented.
- `skills/akka-solution-decomposition/SKILL.md` already requires governed capabilities before Akka component selection and preserves capability ids, AuthContext/scope, schemas, side effects, idempotency, approval, audit/trace, exposure surfaces, and tests in the handoff.

## Checks performed

- Searched app-description/decomposition docs and skills for stale `CRUD`, component-first, endpoint-first, tool-first, generation-first, and thin capability-language patterns.
- Verified description-first routing now preserves capability semantics before behavior, auth/security, UI, observability, readiness, and generation.
- Verified direct Akka solution decomposition output shape preserves capability semantics before component selection and downstream implementation tasks.
- Verified the purchase-request examples remain labeled as conventional/reference mechanics rather than generated SaaS target architecture.

## Residual work

No new residual app-description/decomposition tasks are required from this review. Remaining Sprint 5 reviews should cover focused component skills, examples/tests, and duplicate/superseded migration content as already queued.
