# Attention Release Readiness Dogfood

## Purpose

Capture and complete release-readiness/dogfood validation for the attention-backed five-core starter workstream behavior after completion of:

- `specs/workstream-attention-backbone-v1/`
- `specs/workstream-attention-event-producers-v2/`

The user manually tested the app and reported significant improvement: left-rail workstream “things needing my attention” are working, and dashboards/surfaces are visibly improved. This mini-project records that evidence and defines the remaining bounded validation needed before treating the attention slice as release-ready at its claimed scope.

## Source observation

User dogfood evidence:

> “i tested the app and things have significantly improved. the left rail workstream things needing my attention are working. also, seeing improvements in the dashboards and surfaces.”

Interpretation:

- backend-derived left-rail attention appears usable in manual testing;
- dashboard/surface attention rendering appears improved;
- no immediate user-reported blocker in the core visible attention experience;
- remaining release-readiness work should verify edge cases, repeatability, security, docs, and handoff rather than start a larger event-backbone effort inside the v1/v2 release-readiness queue. Later bounded v3 event-backbone work is documented separately under `specs/workstream-event-backbone-v3/`.

## Scope

This mini-project validates the current starter/reference attention scope:

- fresh scaffold repeatability;
- backend/frontend automated checks;
- manual/dogfood evidence capture;
- five-core workstream attention behavior at implemented v1/v2 scope;
- denial/redaction, lifecycle resolution, and provider/fail-closed checks;
- docs/handoff accuracy.

## Non-goals

- Do not implement Workstream Event Backbone v3.
- Do not require all five core workstreams to be integrated with a future generic event backbone.
- Do not expand into full enterprise notification, digest, or SSE infrastructure.
- Do not treat manual positive evidence as a substitute for targeted repeatability/security checks.
- Do not mark unsupported future behavior as release-ready.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run its required checks or record a blocker, and make one focused commit.

## Sprint sequence

1. **Dogfood evidence and validation plan** — record observed manual evidence and define exact smoke/edge checks.
2. **Fresh scaffold and automated validation** — prove scaffold/build/test repeatability for attention flows.
3. **Manual/runtime smoke and security review** — verify five-core attention behavior, denial/redaction, lifecycle, and fail-closed paths.
4. **Release docs/handoff** — update or create concise release-readiness notes and future-work boundaries.
5. **Verification** — confirm no blockers remain or append bounded fixes.

## Done state

The mini-project is complete when:

- user dogfood evidence is captured in a durable artifact;
- a fresh scaffold validates backend and frontend attention checks;
- local/manual or documented smoke evidence covers left rail, My Account, dashboards/surfaces, producer-driven updates, lifecycle resolution, and denial/redaction at v1/v2 scope;
- provider/fail-closed and frontend-only-authority guardrails are checked;
- docs/handoff accurately distinguish release-ready v1/v2 attention behavior from later bounded v3 event-backbone work and future broader event/AutonomousAgent/notification initiatives;
- any blockers are either fixed in bounded tasks or explicitly recorded as release blockers.
