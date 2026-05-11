# AI-First Coverage Map

## Purpose

This traceability map connects the AI-first DCA example's goals, capabilities, operating model, behavior, UI, traces, outcomes, and implementation slices. It helps future agents see whether the worked example is complete enough to guide planning.

## Goal to capability to first slice

| Goal | Capabilities | First represented in | Notes |
|---|---|---|---|
| `GOAL-01` Keep customer fleets operational | CAP-01 lifecycle orchestration, CAP-02 telemetry intelligence, CAP-04 service coordination | Slice 2 mission control, Slice 4 lifecycle expansion | Fleet health and service are planned after supplies. |
| `GOAL-02` Supplies fulfillment timely and policy-safe | CAP-02 telemetry, CAP-03 supplies autopilot, CAP-08 policy governance | Slice 1 supplies autopilot | First vertical proof of AI-first loop. |
| `GOAL-03` Meter and billing data reliable | CAP-05 meter and billing review | Slice 4 lifecycle expansion | Billing rules and evidence schema already defined as examples. |
| `GOAL-04` Onboarding readiness | CAP-06 onboarding and installation | Slice 4 lifecycle expansion | Gate and exception patterns established. |
| `GOAL-05` Offboarding safely | CAP-07 offboarding and retention | Slice 4 lifecycle expansion | Retention/deauthorization actions remain human-governed. |
| `GOAL-06` Improve owner decisions over time | CAP-08 policy governance, CAP-10 audit/outcome review | Slice 3 policy governance | Requires outcomes and policy commits. |

## Operating model to behavior

| Operating-model object | Behavior files | UI surfaces | Trace/outcome coverage |
|---|---|---|---|
| Bounded agent roster | `15-operating-model/agent-team-design.md` | Mission Control, Supplies Autopilot, Owner Brief | agent recommendation and activity trace events |
| Policies and approval gates | `15-operating-model/policies-and-approval-gates.md`, `20-behavior/rules/01-approval-and-fail-safe-rules.md` | Decision Card, Policy Center | policy invocation, proposal, simulation, commit, decision outcome |
| Decision cards and exceptions | `15-operating-model/decisions-exceptions-and-evidence.md`, `20-behavior/flows/02-lifecycle-and-exception-flows.md` | Approvals & Exceptions, Supply Decision Card | decision trace, human decision, evidence snapshot, outcome follow-up |
| Supplies workflow | `20-behavior/flows/01-supplies-autopilot-flow.md` | Supplies Command Center, Shipment Trace Drawer | supply recommendation to outcome trace |
| Lifecycle workflows | `20-behavior/state-models/01-lifecycle-foundation.md`, `20-behavior/flows/02-lifecycle-and-exception-flows.md` | Lifecycle Workbench, Mission Control | lifecycle gate and exception trace events |
| Outcomes and learning | `50-observability/audit-trace-and-outcomes.md` | Owner Brief, Audit & Outcomes, Policy Center | outcome metrics, feedback-to-learning loop |

## UI to backing objects

| UI surface | Backing objects | Implementation slice |
|---|---|---|
| Owner Brief | `Digest`, `Goal`, `DecisionCard`, `OutcomeMetric`, `WorkTrace` | Slice 2 |
| Mission Control | `Goal`, `LifecycleWorkflow`, `AgentActivity`, `ExceptionCase`, `PolicyInvocation` | Slice 2 and Slice 4 |
| Approvals & Exceptions | `DecisionCard`, `ApprovalRequest`, `EvidenceItem`, `PolicyClause` | Slice 1 then generalized in Slice 4 |
| Supplies Autopilot | `SupplyRecommendation`, `PreparedShipment`, `SuppressedShipment`, `DecisionCard`, `OutcomeLink` | Slice 1 |
| Lifecycle Workbench | `ExecutionPlan`, `LifecycleGate`, `ApprovalGate`, `PolicyInvocation` | Slice 4 |
| Policy Center | `PolicyDocument`, `PolicyProposal`, `SimulationResult`, `PolicyCommit`, `ReferenceExample` | Slice 3 |
| Audit & Outcomes | `WorkTrace`, `DecisionTrace`, `AuditEvent`, `OutcomeMetric`, `OutcomeLink` | Slice 1 foundation, expanded in all later slices |

## Implementation slices to skills

| Slice | AI-first companion skills | Akka substrate skills |
|---|---|---|
| Supplies autopilot foundation | `ai-first-saas-object-model`, `ai-first-saas-agent-team-design`, `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, `ai-first-saas-ui-surfaces`, `ai-first-saas-outcomes-metrics` | `akka-event-sourced-entities`, `akka-key-value-entities`, `akka-workflows`, `akka-agents`, `akka-views`, `akka-timed-actions`, `akka-consumers`, `akka-http-endpoints`, `akka-web-ui-apps` |
| Owner mission control and digest | `ai-first-saas-ui-surfaces`, `ai-first-saas-audit-trace`, `ai-first-saas-outcomes-metrics` | `akka-views`, `akka-consumers`, `akka-timed-actions`, `akka-agents`, `akka-http-endpoints`, `akka-web-ui-apps` |
| Policy governance and learning loop | `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, `ai-first-saas-outcomes-metrics` | `akka-event-sourced-entities`, `akka-workflows`, `akka-timed-actions`, `akka-agents`, `akka-views`, `akka-http-endpoints`, `akka-web-ui-apps` |
| Lifecycle expansion | `ai-first-saas-agent-team-design`, `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, `ai-first-saas-ui-surfaces`, `ai-first-saas-outcomes-metrics` | all substrate families selected per workflow: entities, workflows, agents, views, consumers, timed actions, endpoints, web UI |

## Remaining example limitations

This worked example is complete enough for future planning guidance, but it still intentionally avoids runnable code. A downstream project would still need to answer or define:

- exact tenant/account/user/role model;
- external DCA, ERP, billing, fulfillment, and service-ticket API contracts;
- numeric policy thresholds and escalation SLAs;
- frontend style guide and visual design tokens;
- concrete retention periods and redaction classes;
- model/provider choices and evaluation fixtures for agents.

These should become pending questions or cross-cutting specs during downstream realization rather than being guessed from this reference example.
