# Workstream Design/Implementation Alignment

## Purpose

Capture and execute a targeted alignment initiative for the runnable AI-first SaaS core app's workstream model. The current audit found that the workstream concept is strong and substantially implemented, but several design-to-code seams need cleanup before domain-specific expansion is easy and safe.

## Source discussion / trigger

The user asked whether workstreams need improvement at design and implementation levels, then asked for an audit. The audit concluded:

- design model is strong;
- implementation model is good and improving;
- design-to-implementation consistency needs targeted cleanup;
- full-core readiness remains incomplete by the repository's runtime completion doctrine.

## Scope

This mini-project targets bounded alignment work across:

- `app-description/12-workstreams/**`
- `app-description/10-capabilities/**`
- `app-description/55-ui/**`
- `app-description/60-generation/**`
- `app-description/70-traceability/**`
- `app-description/80-review/**`
- `src/main/java/ai/first/api/coreapp/workstream/**`
- `src/main/java/ai/first/application/coreapp/workstream/**`
- `src/main/java/ai/first/application/foundation/workstream/**`
- `frontend/src/workstream/**`
- targeted backend/frontend tests

## Non-goals

- Do not redesign the workstream architecture from scratch.
- Do not add domain-specific workstreams.
- Do not weaken tenant/customer scope, backend authorization, audit/work traces, provider fail-closed behavior, or frontend secret boundaries.
- Do not claim production/full-core readiness unless the real local Akka/API/UI runtime path proves it at the stated scope.
- Do not commit unrelated untracked `.agents/**` files.

## Execution model

Execute one task per fresh harness context. Each task must read this mini-project's queue, selected sprint, backlog, and task brief; mark exactly one task `in-progress`; complete only that task; run required checks; update the queue; and create one focused commit.

## Read order for future sessions

1. `AGENTS.md`
2. `specs/workstream-design-implementation-alignment/README.md`
3. `specs/workstream-design-implementation-alignment/conversation-capture.md`
4. `specs/workstream-design-implementation-alignment/pending-tasks.md`
5. selected sprint/backlog/task brief
6. task-specific app-description/source/test files

## Sprint sequence

1. Sprint 01: Canonical alignment contracts and traceability.
2. Sprint 02: Runtime and UI behavior alignment.
3. Sprint 99: Verification loop.

## Done state

The initiative is complete when:

- canonical workstream, functional-agent, surface, dashboard, capability, and governed-tool ids are mapped and used consistently or explicitly aliased;
- app-description surface/action maps name exact governed-tool ids for core workstream actions;
- default dashboard loading behavior is deterministic and tested through backend/API/frontend paths;
- common prompt-entered surface requests route through a backend-authoritative shell resolver with safe denial behavior;
- realtime/SSE semantics are either implemented as true live behavior at v1 scope or explicitly narrowed with matching docs/tests;
- readiness/review docs reflect the current runtime state and remaining full-core gaps;
- terminal verification confirms task-group and mini-project goals, or appends new bounded tasks plus a new verification task.

## Open concerns / recommendations

- Realtime behavior may need a design decision: true continuous SSE vs finite replay plus explicit refresh/stale semantics.
- Legacy `frontend/src/screens/**` page-style artifacts should be reviewed for compatibility/reference status; do not remove them without proving they are unused or intentionally superseded.
- `WorkstreamService` is large; this initiative may identify follow-up extraction tasks, but should not silently expand into a wholesale service refactor.
