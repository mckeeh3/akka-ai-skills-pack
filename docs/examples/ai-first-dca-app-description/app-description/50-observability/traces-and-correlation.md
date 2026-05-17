# Traces and Correlation

## Purpose

Define the trace continuity required to explain DCA delegated work from authenticated request or trusted event through workflow, agent/tool use, policy checks, decisions, side effects, and outcomes.

## Correlation model

Every generated slice should preserve these identifiers where applicable:

- request/correlation id and causation id;
- selected `AuthContext`, actor account id or service identity, tenant id, and customer id;
- capability id and exposure surface;
- goal/objective id, workflow id, task id, decision-card id, recommendation id, policy document/version, and policy clause ids;
- agent id, prompt/skill/model version, tool id, data resource id, integration target, and evidence snapshot id;
- idempotency key, natural dedupe key, retry attempt, and no-op/replay marker;
- outcome link id and measurement window when the trace leads to later outcome review.

UI errors and support dialogs should expose only safe request/correlation ids, never internal secrets or unrelated resource identifiers.

## End-to-end trace paths

### Foundation path (`CAP-00`)

```text
Browser request / provider callback
-> WorkOS authentication seam
-> local account + membership resolution
-> selected AuthContext
-> backend authorization check
-> component command/query/workflow/timer/consumer/tool action
-> AdminAuditEvent / work trace
-> scoped admin/audit view
```

Trace continuity must make it possible to answer who authenticated, which local membership authorized the action, what scope was selected, which permission/capability was checked, why a denial occurred, and which audit record proves it.

### Supplies path (`CAP-03`)

```text
TelemetryReceived or RefreshRequested
-> EvaluateSupplyNeed
-> LifecycleGateEvaluated
-> PolicyInvoked(SUP/OFF clauses)
-> ToolOrDataAccessRecorded(entitlement/inventory/lifecycle evidence)
-> AgentRecommendationRecorded where used
-> DecisionCardCreated or AutoShipmentAuthorized or SuppressionRecorded
-> HumanDecisionRecorded when review is required
-> ExternalSideEffectAttempted(fulfillment/inventory)
-> OutcomeMeasured(delivery, stockout, cost, exception)
```

Trace continuity must show the telemetry snapshot, evidence freshness, policy clauses, recommendation, reviewer action or autonomous authority basis, idempotency key, integration result, and outcome link.

## Durable trace event contract

Consequential trace events should carry:

- event id/time, tenant/customer scope, actor/service/agent/component;
- authorization basis and checked permission/capability;
- capability id, workflow/recommendation/decision/order/resource ids;
- policy/guardrail references and evidence snapshot links;
- input/output summary with redaction status;
- decision, approval, exception, side-effect, retry/no-op, and outcome links;
- retention and access classification.

## Derived trace views

Derived views may power command-center feeds, decision queues, audit search, outcome dashboards, and digests, but they are not the audit source of truth. Future realization should derive them from append-only facts, event-sourced histories, or durable trace records.

## Failure diagnosability

For stuck or failed work, traces must answer:

- which tenant/customer/device/workflow is affected;
- where the flow stopped and whether it is retrying, waiting for approval, suppressed, denied, or terminal;
- which policy, evidence, integration, timer, consumer, or agent/tool call caused the state;
- who can act next and which UI/API surface exposes the action;
- whether previous retries were idempotent no-ops or changed durable state.
