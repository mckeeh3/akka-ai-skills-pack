# Sprint 8: Executable AI-First Supplies Autopilot Reference Slice

## Sprint goal

Create the first runnable AI-first reference implementation slice derived from the DCA app-description example: a supplies autopilot foundation that demonstrates durable goals, bounded agent/tool behavior, policy-gated recommendations, human approval/exception paths, trace completeness, supervision APIs/UI, and outcome linkage on Akka.

This sprint is a future implementation sprint. It must be executed as bounded pending tasks; Sprint 7 only planned it.

## Source references

- `docs/ai-first-saas-application-architecture.md`
- `docs/ai-first-examples-and-tests-gap-list.md`
- `docs/examples/ai-first-dca-app-description/README.md`
- `docs/examples/ai-first-dca-app-description/app-description/60-generation/implementation-slices.md`
- `docs/examples/ai-first-dca-app-description/app-description/15-operating-model/README.md`
- `docs/examples/ai-first-dca-app-description/app-description/50-observability/audit-trace-and-outcomes.md`

## Selected slice

Slice 1 from the DCA implementation-slices reference: **Supplies autopilot foundation**.

Why this is first:

- it is small enough to implement as one vertical reference path;
- it exercises core AI-first semantics: durable objective, bounded recommendation, policy gate, decision card, approval/suppression, trace, outcome;
- it uses existing Akka substrate families without requiring the entire DCA platform;
- it can produce concrete tests that future agents can imitate.

## AI-first operating-model increment

Implemented scope:

- durable objective: `GOAL-02` supply fulfillment is timely and policy-safe;
- delegated work: collect/normalize telemetry, evaluate depletion risk, prepare recommendation, explain policy outcome;
- retained human authority: approve high-cost, abnormal, ambiguous, offboarding, or policy-bound shipments;
- policy gates: `SUP-1.0` through `SUP-5.0` as stable policy-clause identifiers in test data and traces;
- decision surfaces: supply decision cards with evidence, risk, confidence, impact, alternatives, policy triggers, and trace links;
- audit/work trace: telemetry, forecast/tool, inventory/tool, policy invocation, recommendation, approval/rejection/suppression, shipment-prepared, and outcome-linked events;
- outcome loop: shipment prepared, shipment suppressed, approval latency, stale decision, and later outcome placeholder linked by IDs.

Explicitly deferred:

- real DCA, ERP, billing, or supplier integrations;
- broad lifecycle onboarding/service/billing/offboarding modules;
- production WorkOS/auth setup beyond simple role/authority modeling required by the reference;
- autonomous policy changes or policy governance center implementation;
- full brand/UI theme beyond a minimal reference UI surface if no style decision is already recorded.

## Akka component plan

| Component family | Reference component | Purpose |
|---|---|---|
| Domain records | `Supply*`, `DeviceTelemetry`, `PolicyClauseRef`, `TraceRef`, `OutcomeRef` records | Shared vocabulary for goal, telemetry, evidence, recommendation, decision, trace, and outcome. |
| Event Sourced Entity | `SupplyDecisionEntity` | Audit-grade write model for recommendation, decision-card state, approval/rejection/suppression, shipment-prepared, stale/expired decision, and outcome linkage. |
| Key Value Entity | `SupplySnapshotEntity` | Latest inventory/entitlement snapshot cache when useful for deterministic test doubles; no audit-grade history. |
| Workflow | `SupplyAutopilotWorkflow` | Orchestrates telemetry intake through recommendation, policy gate, auto-ship or pause-for-approval, suppression, stale-decision escalation, idempotent resume, and no-op handling. |
| Agents / tools | deterministic `SupplyForecastAgent` and tool stubs | Structured recommendation/explanation behavior with bounded authority; deterministic test substitutes are acceptable for reference stability. |
| Views | `SupplyRiskView`, `PendingSupplyDecisionView`, `SupplyTraceView` | Command-center queues for supply risk, pending decision cards, auto-ship/suppression history, and trace lookup. |
| Timed Actions | `SupplyDecisionTimedAction` | Schedules stale decision escalation or expiry recheck. |
| Consumers | `SupplyTraceConsumer` | Enriches or fans out material trace facts from decision/workflow updates when needed by views/tests. |
| HTTP endpoints | `SupplyAutopilotEndpoint`, `SupplyDecisionEndpoint`, `SupplyTraceEndpoint` | Telemetry test hook, review/action APIs, and trace lookup APIs for UI/smoke tests. |
| Web UI | Supplies command center + decision-card screen | Minimal React/Vite reference surface for pending decisions, evidence, policy triggers, actions, and trace links. |
| Tests | unit, integration, endpoint, frontend smoke | Verify AI-first semantics and Akka mechanics together. |

## Delivery order

1. Domain and trace vocabulary.
2. Event-sourced decision write model.
3. Workflow, deterministic agent/tool stubs, policy-gated paths, and timed stale-decision behavior.
4. Views, trace fanout, and HTTP APIs.
5. Minimal supplies command-center and decision-card UI.
6. Slice-level integration, trace-completeness, idempotency, and outcome-linkage tests.

## Acceptance behavior

The sprint is done when the reference implementation demonstrates all of these flows with tests:

```text
fresh telemetry + active contract + normal forecast -> auto shipment prepared with trace
abnormal usage or high cost -> decision card with evidence and policy clauses
customer offboarding or unmapped contract -> shipment suppressed or escalated safely
approved/rejected decision -> workflow resumes or remains safe and outcome is measurable
stale decision timer -> escalation recorded without duplicate side effects
replayed/idempotent command -> no duplicate shipment and trace remains complete
```

## Required tests

- domain validation and event replay tests for `SupplyDecisionEntity`;
- workflow integration tests for auto-ship, approval-required, suppression, stale decision, retry/idempotency, and missing evidence;
- agent/tool boundary tests proving deterministic stubs cannot perform autonomous shipment outside workflow policy gates;
- view projection tests for pending decisions, risk queue, history, and trace lookup;
- endpoint tests for telemetry hook, decision actions, and trace lookup;
- frontend build/smoke tests for command-center and decision-card states if UI implementation is included;
- slice-level trace/outcome tests proving policy, tool/data access, decision, approval/suppression, side-effect, and outcome IDs are present.

## Done criteria

- All Sprint 8 pending tasks are `done` or explicitly deferred/superseded with rationale.
- Each task has focused code/tests and a task-local commit.
- The reference slice remains clearly a skills-pack executable example, not this repository's business application.
- No task expands into full DCA lifecycle implementation.