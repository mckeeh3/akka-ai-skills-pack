# Audit Trace and Outcomes

## Purpose

This file defines audit, work-trace, decision-trace, and outcome-loop examples for the AI-first DCA reference app. These are business audit facts, not just logs.

## Trace principles

- Create trace records during workflow, agent, policy, tool, approval, and side-effect execution.
- Link trace events across goal -> lifecycle workflow -> task -> agent/tool/data/policy -> recommendation -> decision/approval -> side effect -> outcome.
- Keep recommendations, approvals, policy commits, and external side effects durable and auditable.
- Redact sensitive payloads while preserving enough summary, provenance, and authorization data for investigation.
- Derived views support search and UI, but the source of audit truth should be append-only facts or event-sourced history.

## Trace event types

| Event type | Producer | Required fields | Source of truth | Example consumers/views |
|---|---|---|---|---|
| `LifecycleGateEvaluated` | lifecycle workflow | goal, workflow, customer/device/collector, gate, result, policy version, evidence links | event-sourced lifecycle history | blocked gate queue, lifecycle timeline |
| `AgentRecommendationRecorded` | workflow after agent call | agent id/version, prompt/skill version, input summary, recommendation id, confidence, risk, evidence links | event-sourced recommendation record | activity stream, decision card detail |
| `PolicyInvoked` | policy check component or workflow | policy document/version, clause ids, disposition, threshold, action scope, decision basis | append-only trace/event-sourced policy facts | policy invocation timeline, governance replay |
| `ToolOrDataAccessRecorded` | agent tool, integration, or component client | actor/agent, resource, operation, data class, redaction status, purpose, correlation id | append-only trace facts | audit search, data-access review |
| `DecisionCardCreated` | workflow | recommendation, triggers, required role, evidence snapshot, deadline, safe default | event-sourced decision card | approval queue, digest |
| `HumanDecisionRecorded` | endpoint/workflow | actor, role, action, rationale, evidence snapshot, policy version, allowed next transition | event-sourced decision card/workflow | decision timeline, policy learning |
| `ExternalSideEffectAttempted` | workflow/consumer/integration | idempotency key, target system, command summary, authorization basis, result status | event-sourced side-effect record or durable integration event | operational audit, retry monitor |
| `OutcomeMeasured` | consumer/timed action/workflow | outcome metric, measurement source/window, observed value, data quality, linked decision/action | event-sourced outcome link or durable metric fact | outcome dashboard, policy impact report |

## First-slice trace: supply recommendation to outcome

```text
TelemetryReceived
-> LifecycleGateEvaluated
-> AgentRecommendationRecorded
-> PolicyInvoked(SUP-1.0, SUP-3.0, SUP-4.0)
-> ToolOrDataAccessRecorded(inventory lookup)
-> DecisionCardCreated or AutoShipmentAuthorized
-> HumanDecisionRecorded when review is required
-> ExternalSideEffectAttempted(supplier/order export)
-> OutcomeMeasured(delivered before depletion, cost, exception result)
```

Minimum supply trace fields:

- tenant/dealer, customer, site, device, assignment, supply item;
- goal id `GOAL-02` and supplies workflow id;
- telemetry observation time and freshness;
- depletion forecast and confidence;
- contract entitlement and lifecycle state;
- policy document/version and clause ids;
- inventory/cost/supplier evidence;
- recommendation id and recommending agents;
- decision card id and reviewer action when applicable;
- fulfillment id/idempotency key or suppression reason;
- delivery/outcome measurement window.

## Outcome metrics

| Metric | Category | Linked objects | Measurement source/window | Why it matters |
|---|---|---|---|---|
| Stockout avoided | business value | supply recommendation, shipment, device, customer | delivery confirmation before projected depletion | validates timely delegated work |
| Shipment accuracy | quality/safety | recommendation, policy invocations, contract entitlement | shipment result + human correction window | detects bad forecasts or entitlement mistakes |
| Auto-ship exception rate | safety/risk | policy clauses, decision cards, suppressed shipments | weekly by policy version/customer segment | reveals overly broad or overly strict autonomy |
| Abnormal consumption review outcome | learning/governance | decision card, human action, future consumption | review decision + 30-day follow-up | turns repeat patterns into examples or policy proposals |
| Cost leakage prevented | business value/risk | high-cost decision card, alternatives, shipment outcome | approved/rejected high-cost recommendations | measures value of approval gates |
| Offboarding shipment suppression | safety/risk | lifecycle state, `SUP-2.0`, suppressed shipment | offboarding period | proves fail-safe lifecycle gating |
| Decision turnaround | timeliness/workload | decision card, reviewer role, deadline | created-to-action duration | manages supervisor burden and SLA risk |
| Trace completeness | audit quality | all consequential events | automated trace validation | verifies explainability requirements |

## Feedback-to-learning loop

Human decisions may create learning artifacts, but not automatic authority expansion.

1. A reviewer approves, rejects, modifies, or requests more evidence.
2. The decision is linked to later outcome facts.
3. A repeated pattern can become a `ReferenceExample`, `Precedent`, `PolicyProposal`, or threshold-change proposal.
4. Material policy changes require simulation/replay and a human `PolicyCommit`.
5. Future recommendations cite the committed policy version or approved reference example.

## Privacy, retention, and access rules

- Trace payloads should include summaries and links rather than raw sensitive documents unless required for audit.
- Data-access trace events must classify customer, billing, contract, device, and retention-sensitive data.
- Audit and outcome views must enforce tenant, role, and customer scope.
- Retention/deletion actions must preserve required billing/audit records and record any redaction/anonymization policy decision.

## Tests implied by this description

Future implementation should verify:

- trace emission for auto-ship, approval-required, suppression, denied, retry, and integration-failure paths;
- policy clause ids and versions are present in consequential traces;
- human decisions capture actor, role, rationale, evidence snapshot, and workflow transition;
- outcome metrics link back to decisions/actions/policies and handle missing or delayed measurements;
- unauthorized roles cannot read restricted audit details or commit policy changes.

## Akka substrate mapping

- Audit-grade trace, decision, policy, side-effect, and outcome facts -> Event Sourced Entities or append-only topic/consumer flows.
- Trace enrichment and outcome measurement from integrations -> Consumers.
- Search, command-center feeds, digest inputs, and outcome dashboards -> Views.
- Scheduled outcome measurement, SLA checks, digest generation, and replay windows -> Timed Actions.
- Decision and trace APIs -> HTTP endpoints; optional service integrations may use gRPC or MCP only when justified.
