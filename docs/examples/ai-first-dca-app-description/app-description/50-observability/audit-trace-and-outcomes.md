# Audit Trace and Outcomes

## Purpose

This file defines audit-grade work traces, decision traces, policy/tool/data-access traces, and outcome-loop expectations for the AI-first DCA reference app. It complements:

- `logs-and-audit.md` for logs vs durable audit facts;
- `traces-and-correlation.md` for correlation continuity;
- `metrics.md` for operational and outcome measures;
- `health-and-alerts.md` for health, alerts, and diagnosis.

These are business audit facts, not just logs.

## Trace principles

- Create trace records during workflow, agent, policy, tool, approval, timer, consumer, integration, and side-effect execution.
- Link trace events across goal -> lifecycle workflow -> task -> agent/tool/data/policy -> recommendation -> decision/approval -> side effect -> outcome.
- Capture foundation security events for identity, membership, roles, invitations, support access, billing-boundary changes, protected reads, denials, and admin-agent activity.
- Keep recommendations, approvals, policy commits, support access, retention/export actions, and external side effects durable and auditable.
- Redact sensitive payloads while preserving enough summary, provenance, authorization, policy, and evidence data for investigation.
- Derived views support search and UI, but the source of audit truth should be append-only facts, event-sourced history, or durable trace records.

## Trace event types

| Event type | Producer | Required fields | Source of truth | Example consumers/views |
|---|---|---|---|---|
| `AdminAuditEventRecorded` | foundation routes/components/workflows | actor, AuthContext, tenant/customer, permission, action, target, reason, result, correlation id, redaction marker | event-sourced or append-only admin audit history | admin audit, access review, support-access timeline |
| `AuthenticationLinked` | WorkOS/AuthKit + `/api/me` | WorkOS subject link summary, account id, invitation/membership basis, tenant/customer context, result, denial reason where relevant | account/membership event history + audit fact | `/api/me` diagnostics, security review |
| `InvitationLifecycleRecorded` | InvitationWorkflow / email consumer / expiry timer | invitation id, target scope, delivery status, action, actor, expiry, idempotency key, result | invitation history + audit fact | invitation admin, delivery failure queue |
| `SupportAccessRecorded` | support-access workflow/routes | grant id, tenant, support actor, reason, expiry, use/revoke/expire action, approval link | support-access history + audit fact | support-access review, audit search |
| `LifecycleGateEvaluated` | lifecycle workflow | goal, workflow, customer/device/collector, gate, result, policy version, evidence links | event-sourced lifecycle history | blocked gate queue, lifecycle timeline |
| `AgentRecommendationRecorded` | workflow after agent call | agent id/version, prompt/skill/model version, input summary, recommendation id, confidence, risk, evidence links | event-sourced recommendation record or durable trace | activity stream, decision card detail |
| `PolicyInvoked` | policy check component, workflow, or agent tool boundary | policy document/version, clause ids, disposition, threshold, action scope, decision basis | append-only trace/event-sourced policy facts | policy invocation timeline, governance replay |
| `ToolOrDataAccessRecorded` | agent tool, integration, component client, support view, or audit query | actor/agent, resource, operation, data class, redaction status, purpose, authorization basis, correlation id | append-only trace facts | audit search, data-access review |
| `DecisionCardCreated` | workflow | recommendation, triggers, required role, evidence snapshot, deadline, safe default | event-sourced decision card | approval queue, digest |
| `HumanDecisionRecorded` | endpoint/workflow | actor, role, action, rationale, evidence snapshot, policy version, allowed next transition | event-sourced decision card/workflow | decision timeline, policy learning |
| `ExternalSideEffectAttempted` | workflow/consumer/integration | idempotency key, target system, command summary, authorization basis, result status | event-sourced side-effect record or durable integration event | operational audit, retry monitor |
| `OutcomeMeasured` | consumer/timed action/workflow | outcome metric, measurement source/window, observed value, data quality, linked decision/action | event-sourced outcome link or durable metric fact | outcome dashboard, policy impact report |
| `AuditOutcomeAccessed` | audit/outcome review UI/API/agent/timer | query/export/digest id, actor/support grant, scope, filters summary, redaction class, result | audit access trace | audit of audit access, export review |

## Foundation trace requirements (`CAP-00`)

Minimum foundation trace fields:

- account id, WorkOS-linked subject summary, selected `AuthContext`, membership id, role/capability checked, tenant/customer id, support grant id where applicable;
- invitation id, delivery status, expiry, acceptance context, target scope, requested roles/capabilities, resend/revoke/accept result;
- admin action target, reason, policy/approval link, decision-card id where applicable;
- billing-boundary object id and safe subscription/entitlement state for SaaS Owner operations;
- denial reason category without leaking unrelated tenant/customer resource existence;
- correlation id, idempotency key, actor/service/agent identity, and redaction marker.

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
| Support-access audit completeness | security/audit quality | support grant, support reads/actions, audit events | grant lifetime + review window | verifies no support bypass exists |
| Invitation delivery resolution | security/operations | invitation, delivery attempt, admin action | invite lifetime | keeps onboarding auditable and actionable |
| Policy change impact | learning/governance | policy proposal, simulation/replay, activation/rollback, outcomes | before/after policy version window | proves governance changes improve results safely |

## Feedback-to-learning loop

Human decisions may create learning artifacts, but not automatic authority expansion.

1. A reviewer approves, rejects, modifies, defers, escalates, or requests more evidence.
2. The decision is linked to later outcome facts and trace completeness checks.
3. A repeated pattern can become a `ReferenceExample`, `Precedent`, `PolicyProposal`, evaluator finding, or threshold-change proposal.
4. Material policy changes require simulation/replay and a human `PolicyCommit`.
5. Future recommendations cite the committed policy version or approved reference example.

## Privacy, retention, and access rules

- Trace payloads should include summaries and links rather than raw sensitive documents unless required by an approved audit/retention policy.
- Data-access trace events must classify customer, billing, contract, device, support, retention, and telemetry data.
- Audit and outcome views must enforce tenant, role, customer, support-access, and export/retention permissions.
- Retention/deletion actions must preserve required billing/audit records and record any redaction/anonymization policy decision.
- Outcome analytics preserve tenant/customer boundaries; cross-tenant SaaS Owner views are platform-safe summaries only unless support access grants a scoped investigation.

## Tests implied by this description

Future implementation should verify:

- trace emission for foundation success, denial, protected read, invitation delivery failure, support-access use, billing-boundary change, approval outcome, and admin-agent/tool activity;
- trace emission for auto-ship, approval-required, suppression, denied, retry/no-op, and integration-failure paths;
- policy clause ids and versions are present in consequential traces;
- human decisions capture actor, role, rationale, evidence snapshot, and workflow transition;
- outcome metrics link back to decisions/actions/policies and handle missing or delayed measurements;
- unauthorized roles cannot read restricted audit details, use support access, export data, or commit policy changes;
- audit/outcome access is itself audited.

## Akka substrate mapping

- Audit-grade trace, decision, policy, side-effect, support-access, invitation, and outcome facts -> Event Sourced Entities or append-only topic/consumer flows.
- Trace enrichment and outcome measurement from integrations -> Consumers.
- Search, command-center feeds, digest inputs, admin audit, access-review, and outcome dashboards -> Views.
- Scheduled outcome measurement, SLA checks, digest generation, expiry, retention/export, and replay windows -> Timed Actions.
- Decision and trace APIs -> HTTP endpoints; service integrations use gRPC or MCP only when justified by a concrete generated-app requirement.
