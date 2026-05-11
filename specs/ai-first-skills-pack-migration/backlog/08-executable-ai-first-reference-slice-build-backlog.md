# Sprint 8 Build Backlog: Executable AI-First Supplies Autopilot Reference Slice

## Purpose

Materialize the first executable AI-first reference slice from the DCA app-description example. The implementation should prove AI-first semantics in runnable Akka code and tests without building the whole DCA platform.

## Delivery goal

A future agent can run the Sprint 8 pending tasks to create a small vertical supplies autopilot example with durable decisions, policy-gated workflow behavior, deterministic agent/tool stubs, supervision views/APIs/UI, trace completeness, and outcome linkage.

## AI-first scope

- Objective: `GOAL-02` timely and policy-safe supply fulfillment.
- Delegated work: telemetry interpretation, depletion forecast, entitlement/inventory checks, recommendation drafting, policy explanation.
- Retained authority: high-cost, abnormal, ambiguous, offboarding, unmapped-contract, and exception decisions require human approval or suppression.
- Policies: stable `SUP-1.0` through `SUP-5.0` clause IDs, thresholds in test fixtures, policy invocation trace records.
- Decisions: supply decision cards include evidence, risk, confidence, impact, alternatives, policy trigger, trace link, and action state.
- Audit/trace: persist or project telemetry, tool/data access, forecast, policy invocation, recommendation, approval/rejection/suppression, shipment-prepared, stale escalation, and outcome-link events.
- UI: supplies command center, pending decisions, decision-card detail, auto-ship/suppression history, trace lookup.
- Outcomes: link every recommendation or side effect to a placeholder outcome record or outcome-ref field for later measurement.

## Recommended package layout additions

Use the existing example package style unless a future implementation task chooses a narrower subpackage:

```text
src/main/java/com/example/domain/supplies/
src/main/java/com/example/application/supplies/
src/main/java/com/example/api/supplies/
src/main/resources/web/supplies/ or frontend project path selected by web UI skills
src/test/java/com/example/application/supplies/
```

## Class-by-class target list

Indicative class names; implementation tasks may adjust names to existing repository conventions while preserving the responsibilities.

- Domain records:
  - `SupplyObjective`, `DeviceTelemetry`, `SupplyItem`, `SupplyEvidence`, `SupplyPolicyClauseRef`, `SupplyRecommendation`, `SupplyDecisionCard`, `SupplyDecisionAction`, `SupplyTraceEvent`, `SupplyOutcomeRef`.
- Write model:
  - `SupplyDecisionEntity` plus state/events/commands under the supplies domain package.
- Current-state support:
  - `SupplySnapshotEntity` only if the workflow needs a latest inventory/entitlement cache for deterministic tests.
- Workflow:
  - `SupplyAutopilotWorkflow` with auto-ship, approval-required pause/resume, suppression, stale decision, and idempotent/no-op paths.
- Agent/tool stubs:
  - `SupplyForecastAgent`, `SupplyForecastTools`, and deterministic test doubles or fixtures.
- Views:
  - `SupplyRiskView`, `PendingSupplyDecisionView`, `SupplyTraceView`.
- Async/timed support:
  - `SupplyDecisionTimedAction`, optional `SupplyTraceConsumer` if trace projection/fanout is not covered by direct entity/workflow views.
- Endpoints:
  - `SupplyAutopilotEndpoint`, `SupplyDecisionEndpoint`, `SupplyTraceEndpoint`.
- Web UI:
  - supplies command-center route and decision-card detail route with typed API client and clear loading/empty/error/action states.

## Endpoint/API target list

- `POST /api/supplies/telemetry` — test/reference telemetry intake that starts or signals the workflow.
- `GET /api/supplies/risks` — list supply risk rows for command center.
- `GET /api/supplies/decisions/pending` — list pending decision cards.
- `GET /api/supplies/decisions/{decisionId}` — decision-card detail.
- `POST /api/supplies/decisions/{decisionId}/approve` — approve with actor and rationale.
- `POST /api/supplies/decisions/{decisionId}/reject` — reject with actor and rationale.
- `POST /api/supplies/decisions/{decisionId}/suppress` — suppress shipment with reason/policy context.
- `GET /api/supplies/traces/{traceId}` — trace lookup for audit/debug UI.

## Write-model design decisions

- `SupplyDecisionEntity` is event sourced because recommendation, approval, suppression, shipment-prepared, stale decision, policy invocation, and outcome linkage are audit-grade facts.
- `SupplySnapshotEntity` is optional and key-value because inventory/entitlement fixtures are latest-state inputs, not the audit record.
- The workflow, not the agent, owns side-effect decisions. Agents or deterministic stubs may recommend/explain, but policy and workflow gates decide whether auto-ship, pause, suppress, or escalate occurs.
- Commands must be idempotent by decision/recommendation IDs and must no-op safely on duplicate approvals, stale timer callbacks, or repeated telemetry.

## Suggested harness task breakdown

### TASK-08-001: Implement supply domain and trace vocabulary

Output: pure domain records, validators, and unit tests for supplies objective, telemetry, evidence, policy refs, decision-card fields, trace event schema, and outcome refs.

### TASK-08-002: Implement SupplyDecision event-sourced write model

Output: `SupplyDecisionEntity`, state/events/commands, replay logic, command validation, and tests for recommendation, approval, rejection, suppression, shipment-prepared, stale decision, idempotency, and outcome linkage.

### TASK-08-003: Implement supplies workflow with deterministic agent/tool stubs

Output: `SupplyAutopilotWorkflow`, bounded forecast/policy/inventory stubs, auto-ship path, approval pause/resume path, suppression path, missing-evidence escalation, stale-decision scheduling hook, and workflow tests.

### TASK-08-004: Implement supplies views, trace fanout, and stale-decision timer

Output: risk, pending-decision, history/trace views; optional trace consumer; timed action for stale decision escalation; projection/timer/idempotency tests.

### TASK-08-005: Implement supplies HTTP APIs and endpoint tests

Output: telemetry test hook, decision action APIs, trace lookup API, API records/error mapping, and endpoint integration tests.

### TASK-08-006: Implement supplies command-center and decision-card web UI

Output: minimal React/Vite supplies command center and decision-card detail screens, typed client calls, action states, trace link rendering, accessibility/responsive basics, and frontend/build/route smoke tests.

### TASK-08-007: Add slice-level AI-first acceptance and trace/outcome tests

Output: end-to-end or integration tests covering auto-ship, approval-required, suppression, stale decision, retry/idempotency, trace completeness, and outcome linkage across the slice.

## Done criteria

- Every task remains executable in one fresh harness context.
- The first runnable slice demonstrates AI-first semantics, not only component mechanics.
- All implementation tasks preserve the DCA app-description boundary and do not expand into full lifecycle modules.
- Tests prove authority boundaries, policy gates, decision-card completeness, trace facts, idempotency, and outcome references.

## Explicit defer list

- Real integrations with DCA collectors, suppliers, ERP, billing, or service dispatch.
- Full policy governance center or autonomous policy updates.
- Full authentication/tenant administration beyond minimal role/actor fields needed by tests.
- MCP/gRPC exposure for the supplies slice.
- Broad lifecycle expansion beyond supplies.