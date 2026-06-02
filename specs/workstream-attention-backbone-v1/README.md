# Workstream Attention Backbone v1

## Status

Completed at the starter/reference scope. The AI-first SaaS starter now includes a shared backend-owned attention backbone with `AttentionItem` lifecycle state, scoped workstream/My Account/rail reads, safe redaction, audit/work traces, and frontend rendering from backend-derived summaries. This README preserves the original planning rationale and done-state contract; do not use the historical source discussion below to claim the backbone is still missing.

Follow-on `workstream-attention-event-producers-v2` work adds bounded event/service/timer/task producers and backend-derived update delivery. Later `workstream-event-backbone-v3` work adds a bounded governed event backbone with typed event envelopes/source refs, starter invitation/access-review lifecycle publication, idempotent event-to-attention consumption, and backend-derived projection-refresh hints. Broad generated-app event coverage, full notification infrastructure, digest/enterprise notification flows, and broad AutonomousAgent runtime task integration remain future work unless a later task implements and validates them.

## Purpose

Create a self-contained implementation queue for the shared workstream attention backbone discussed in this session.

At planning time, the repository already had doctrine and partial starter behavior for “what needs my attention?” through dashboard `attentionItems`, My Account personal attention, and left-rail indicators. The then-missing feature was a first-class backend-owned attention backbone: one shared, tenant-aware, workstream-scoped store/projection layer that powers workstream dashboards, My Account aggregate attention, and left-rail summaries.

## Source discussion

The user asked whether the internal bus/queue of “things that need my attention” had been implemented. The original review found these pre-v1 gaps, which are now historical for the completed v1 scope:

- `templates/ai-first-saas-starter/backend/.../MyAccountService.java` returned hard-coded/deterministically capability-derived personal attention.
- `WorkstreamService.java` included dashboard `attentionItems`, but they were local to each surface and not backed by a shared attention queue/projection.
- frontend rail attention state tracked unseen/background responses through Akka components only.
- no durable `AttentionItem`, `AttentionRepository`, attention projection, internal event bus, or shared attention queue implementation existed.

The accepted direction is:

```text
one shared attention backbone/store
→ attention items have owningWorkstreamId and source links
→ workstream-local detail projections
→ My Account and left-rail aggregate projections
```

## Scope

This mini-project plans implementation in the source repository's starter/reference assets, primarily:

- `templates/ai-first-saas-starter/backend/**`
- `templates/ai-first-saas-starter/frontend/**`
- starter contract tests and docs/spec artifacts as needed
- local doctrine references only where needed to keep routing and completion claims accurate

## Non-goals

- Do not build a separate production app in this repository.
- Do not replace all workstream-specific queues such as invitation queues or governance proposal queues.
- Do not implement the full future event/autonomous-agent ecosystem in v1.
- Do not make frontend-only badges count as implemented attention state.
- Do not claim completion unless the starter runtime/API/UI path uses backend-derived attention data at the stated scope.

## Execution model

Execute one task per fresh harness context. Each implementation task must update `pending-tasks.md`, run its required checks, and make one focused commit.

Future task sessions should read, in order:

1. `AGENTS.md`
2. `skills/README.md`
3. this `README.md`
4. `conversation-capture.md`
5. selected sprint, backlog, queue entry, and task brief
6. only the focused implementation skills and source files listed by the task

## Sprint sequence

1. **Contracts and implementation plan** — define the v1 attention item contract, projections, capability ids, API shapes, and starter wiring target.
2. **Backend backbone** — implement shared attention domain/service/repository and backend tests in the starter template.
3. **Workstream integration** — replace hard-coded/dashboard-local attention with the shared service for My Account, workstream dashboards, and initial producers.
4. **Frontend/API integration** — wire summaries/items into shell rail and My Account/workstream surfaces with tests.
5. **Verification** — prove the mini-project done state or append follow-up tasks.

## Done state

The mini-project is complete when the starter template has a v1 shared attention backbone with:

- first-class `AttentionItem` lifecycle state and scoped read contracts;
- one shared backend-owned source for attention items/summaries;
- workstream-scoped reads for dashboard attention;
- My Account aggregate personal attention reads from the backbone, not hard-coded capability-only lists;
- left-rail summary data available from backend-derived attention counts;
- initial producers/derivations for the five core starter workstreams where currently represented by hard-coded or local dashboard attention;
- tenant/customer/AuthContext/capability filtering and safe redaction for hidden workstreams;
- audit/work-trace emission for protected reads and lifecycle changes;
- backend and frontend contract/runtime-oriented tests; and
- no deterministic/demo/mock/model-less substitute used to claim normal runtime attention behavior.

## Open concerns

- v1 may use a pragmatic shared repository/service over Akka components if that is the smallest safe starter slice, but any runtime completion claim must be explicit about the scope and should prefer durable Akka-backed state where feasible.
- AutonomousAgent task notifications and advanced event consumers are future extensions unless a task explicitly adds a bounded slice.
