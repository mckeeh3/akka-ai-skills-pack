# AI-First Coverage Map

## Purpose

This traceability map connects the AI-first DCA example's secure foundation, goals, capabilities, operating model, behavior, UI, traces, outcomes, and implementation slices. It helps future agents see whether the worked example is complete enough to guide planning.

Authoritative meaning lives in the earlier app-description layers. This file is a derived navigation aid.

## Foundation to DCA vertical capabilities

| Foundation concern | Capability source | Consumed by | Required downstream coverage |
|---|---|---|---|
| WorkOS/AuthKit authentication plus Akka-owned authorization | `CAP-00` secure tenant/user foundation | every DCA capability, browser API, workflow action, view query, agent tool, timer, consumer, and support-access action | `40-auth-security/`, `/api/me`, backend authorization checks, tenant/customer filters, forbidden-access tests, audit events |
| Tenant/Customer scope, Membership, Role, Permission/Capability, selected `AuthContext` | `CAP-00` | `CAP-01` through `CAP-10` | scoped commands/queries, support-access limits, role/capability denial tests, trace fields with actor/scope/permission checked |
| Invitation lifecycle, admin audit, support access, billing-safe SaaS Owner boundary | `CAP-00` | foundation admin UI plus DCA operational surfaces | invitation/admin/support-access/billing-boundary tests, `AdminAuditEvent`, Users/Invitations/Roles/Access Review/Support Access/Admin Audit UI surfaces |
| Foundation admin-assistant agents | `CAP-00` | admin decision cards and access review surfaces | scoped read/draft/recommend tools only, backend enforcement, audit/work trace, human approval for risky admin changes |

## Goal to capability to first slice

| Goal | Capabilities | First represented in | Notes |
|---|---|---|---|
| Secure SaaS access before domain automation | `CAP-00` secure tenant/user foundation | Seed foundation: authenticated full-stack app shell | Mandatory prerequisite for every DCA slice; not a DCA-specific automation feature. |
| `GOAL-01` Keep customer fleets operational | `CAP-01` lifecycle orchestration, `CAP-02` telemetry intelligence, `CAP-04` service coordination, `CAP-09` owner command center | Slice 2 mission control, Slice 4 lifecycle expansion | Fleet health and service are planned after supplies but rely on the foundation and lifecycle vocabulary. |
| `GOAL-02` Supplies fulfillment timely and policy-safe | `CAP-02` telemetry intelligence, `CAP-03` supplies autopilot, `CAP-08` policy governance, `CAP-10` audit/outcome review | Slice 1 supplies autopilot | First detailed vertical proof of the AI-first loop and current capability-first contract shape. |
| `GOAL-03` Meter and billing data reliable | `CAP-05` meter and billing review, `CAP-10` audit/outcome review | Slice 4 lifecycle expansion | Billing rules and evidence schema remain lightweight pending future detail. |
| `GOAL-04` Onboarding readiness | `CAP-06` onboarding and installation, `CAP-01` lifecycle orchestration | Slice 4 lifecycle expansion | Gate and exception patterns are established but detailed integration contracts are future work. |
| `GOAL-05` Offboarding safely | `CAP-07` offboarding and retention, `CAP-01` lifecycle orchestration, `CAP-10` audit/outcome review | Slice 4 lifecycle expansion | Retention, deauthorization, final-read, and supply-suppression actions remain human-governed. |
| `GOAL-06` Improve owner decisions over time | `CAP-08` policy governance, `CAP-09` owner command center, `CAP-10` audit/outcome review | Slice 3 policy governance | Requires outcomes, simulations/replay, policy proposals, and human policy commits. |

## Capability contract coverage

| Capability | Coverage status | Cross-layer navigation |
|---|---|---|
| `CAP-00` secure tenant/user foundation | detailed foundation contract | `10-capabilities/01-secure-tenant-user-foundation.md`; `40-auth-security/`; `50-observability/audit-trace-and-outcomes.md`; `55-ui/ui-surfaces.md`; seed foundation in `60-generation/implementation-slices.md`; detailed map in `70-traceability/capability-to-layer-map.md` |
| `CAP-01` lifecycle orchestration | lightweight routing contract | lifecycle state/flow behavior, approval/fail-safe rules, mission control/lifecycle UI, Slice 4 |
| `CAP-02` telemetry intelligence | lightweight routing contract | telemetry evidence feeding supplies, lifecycle exceptions, trace/outcome evidence, Slice 1 and Slice 4 |
| `CAP-03` supplies autopilot | detailed vertical contract | supplies flow, approval/fail-safe rules, all auth/security boundaries, trace/outcome requirements, Supplies Autopilot UI, Slice 1 |
| `CAP-04` service coordination | lightweight routing contract | lifecycle exception behavior, service/SLA decisions, mission control/lifecycle UI, Slice 4 |
| `CAP-05` meter billing review | lightweight routing contract | billing anomaly decisions, billing-boundary redaction, Audit & Outcomes, Slice 4 |
| `CAP-06` onboarding installation | lightweight routing contract | onboarding gates, lifecycle workbench, readiness/waiver traces, Slice 4 |
| `CAP-07` offboarding retention | lightweight routing contract | offboarding gates, retention/deauthorization, supply suppression, Audit & Outcomes, Slice 4 |
| `CAP-08` policy governance | lightweight routing contract | policy proposals, simulation/replay, human policy commits, Policy Center, Slice 3 |
| `CAP-09` owner command center | lightweight routing contract | owner brief, mission control, decision queues, material-event digests, Slice 2 |
| `CAP-10` audit outcome review | lightweight routing contract | audit search, outcome loops, retention/export, support-access audit, all slices |

## Operating model to behavior

| Operating-model object | Behavior files | UI surfaces | Trace/outcome coverage |
|---|---|---|---|
| Secure SaaS foundation authority | `CAP-00`; future foundation behavior refresh should add explicit tenant/user/invitation/access flows | Sign-in, context selection, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings | identity, membership, role/scope, support-access, billing-boundary, protected-read, denial, and admin-agent audit events |
| Bounded agent roster | `15-operating-model/agent-team-design.md` | Mission Control, Supplies Autopilot, Owner Brief, Access Review | agent recommendation, tool/data-access, prompt/skill/policy version, and activity trace events |
| Policies and approval gates | `15-operating-model/policies-and-approval-gates.md`, `20-behavior/rules/01-approval-and-fail-safe-rules.md` | Decision Card, Policy Center | policy invocation, proposal, simulation, commit, decision outcome |
| Decision cards and exceptions | `15-operating-model/decisions-exceptions-and-evidence.md`, `20-behavior/flows/02-lifecycle-and-exception-flows.md` | Approvals & Exceptions, Supply Decision Card | decision trace, human decision, evidence snapshot, outcome follow-up |
| Supplies workflow | `20-behavior/flows/01-supplies-autopilot-flow.md` | Supplies Command Center, Shipment Trace Drawer | supply recommendation to outcome trace |
| Lifecycle workflows | `20-behavior/state-models/01-lifecycle-foundation.md`, `20-behavior/flows/02-lifecycle-and-exception-flows.md` | Lifecycle Workbench, Mission Control | lifecycle gate and exception trace events |
| Outcomes and learning | `50-observability/audit-trace-and-outcomes.md` | Owner Brief, Audit & Outcomes, Policy Center | outcome metrics, feedback-to-learning loop |

## UI to backing objects

| UI surface | Backing objects | Capability links | Implementation slice |
|---|---|---|---|
| Foundation Admin | `Account`, `UserProfile`, `UserSettings`, `Membership`, `Role`, `Permission/Capability`, `Invitation`, `SupportAccessGrant`, `AdminAuditEvent` | `CAP-00` | Seed foundation |
| Owner Brief | `Digest`, `Goal`, `DecisionCard`, `OutcomeMetric`, `WorkTrace` | `CAP-09`, `CAP-10` | Slice 2 |
| Mission Control | `Goal`, `LifecycleWorkflow`, `AgentActivity`, `ExceptionCase`, `PolicyInvocation` | `CAP-01`, `CAP-02`, `CAP-04`, `CAP-09` | Slice 2 and Slice 4 |
| Approvals & Exceptions | `DecisionCard`, `ApprovalRequest`, `EvidenceItem`, `PolicyClause` | `CAP-03` first, then `CAP-01`, `CAP-04`, `CAP-05`, `CAP-06`, `CAP-07`, `CAP-08` | Slice 1 then generalized in Slice 4 |
| Supplies Autopilot | `SupplyRecommendation`, `PreparedShipment`, `SuppressedShipment`, `DecisionCard`, `OutcomeLink` | `CAP-03`, with evidence from `CAP-02` and outcomes via `CAP-10` | Slice 1 |
| Lifecycle Workbench | `ExecutionPlan`, `LifecycleGate`, `ApprovalGate`, lifecycle state records | `CAP-01`, `CAP-06`, `CAP-07`, plus service/billing links | Slice 4 |
| Policy Center | `PolicyDocument`, `PolicyProposal`, `SimulationResult`, `PolicyCommit`, `ReferenceExample` | `CAP-08` | Slice 3 |
| Audit & Outcomes | `WorkTrace`, `DecisionTrace`, `AuditEvent`, `OutcomeMetric`, `OutcomeLink` | `CAP-10` plus all consequential capabilities | Slice 1 foundation, expanded in all later slices |

## Implementation slices to skills

| Slice | AI-first / foundation skills | Akka substrate skills |
|---|---|---|
| Seed secure SaaS foundation | `core-saas-foundation`, `akka-saas-invitation-onboarding`, `ai-first-saas-admin-agents`, `ai-first-saas-audit-trace`, `app-description-auth-security`, `app-description-ui`, `app-description-test-specification` | `akka-key-value-entities`, `akka-event-sourced-entities`, `akka-workflows`, `akka-views`, `akka-timed-actions`, `akka-consumers`, `akka-http-endpoints`, `akka-web-ui-apps`, `akka-workos-user-auth`, `akka-basic-user-admin` |
| Supplies autopilot foundation | `ai-first-saas-object-model`, `ai-first-saas-agent-team-design`, `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, `ai-first-saas-ui-surfaces`, `ai-first-saas-outcomes-metrics` | `akka-event-sourced-entities`, `akka-key-value-entities`, `akka-workflows`, `akka-agents`, `akka-views`, `akka-timed-actions`, `akka-consumers`, `akka-http-endpoints`, `akka-web-ui-apps` |
| Owner mission control and digest | `ai-first-saas-ui-surfaces`, `ai-first-saas-audit-trace`, `ai-first-saas-outcomes-metrics` | `akka-views`, `akka-consumers`, `akka-timed-actions`, `akka-agents`, `akka-http-endpoints`, `akka-web-ui-apps` |
| Policy governance and learning loop | `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, `ai-first-saas-outcomes-metrics` | `akka-event-sourced-entities`, `akka-workflows`, `akka-timed-actions`, `akka-agents`, `akka-views`, `akka-http-endpoints`, `akka-web-ui-apps` |
| Lifecycle expansion | `ai-first-saas-agent-team-design`, `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, `ai-first-saas-ui-surfaces`, `ai-first-saas-outcomes-metrics` | all substrate families selected per workflow: entities, workflows, agents, views, consumers, timed actions, endpoints, web UI |

## Remaining example limitations

This worked example is complete enough for future planning guidance, but it still intentionally avoids runnable code. A downstream project would still need to answer or define:

- external DCA, ERP, billing, fulfillment, and service-ticket API contracts;
- detailed numeric policy thresholds and escalation SLAs beyond the illustrative supplies gates already recorded in `CAP-03`;
- concrete frontend API contracts for every UI surface;
- concrete test files beyond the current placeholders;
- concrete retention periods and redaction classes;
- model/provider choices and evaluation fixtures for agents.

These should become pending questions, test specs, integration specs, or cross-cutting specs during downstream realization rather than being guessed from this reference example.
