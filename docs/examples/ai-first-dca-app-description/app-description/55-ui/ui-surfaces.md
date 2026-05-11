# AI-First UI Surfaces

## Purpose

This file defines supervision-oriented UI surfaces for the agent-first DCA reference app. The UI is not a CRUD dashboard by default; it is organized around objectives, active delegated work, policy boundaries, human decisions, audit traces, and outcomes.

## Navigation model

Primary navigation should expose the way humans operate the dealer business:

1. `Owner Brief` — catch up on material work and pending decisions.
2. `Mission Control` — supervise active customer, device, DCA, supply, service, billing, and offboarding objectives.
3. `Approvals & Exceptions` — decide policy-bound recommendations and blocked workflow gates.
4. `Supplies Autopilot` — review the first-slice supply automation queue, recommendations, and outcomes.
5. `Lifecycle Workbench` — inspect or launch onboarding, service, billing, and offboarding plans.
6. `Policy Center` — edit, simulate, and commit governed policies and examples.
7. `Audit & Outcomes` — investigate work traces, decision traces, and outcome links.

Record detail pages may exist, but they should be reached from objectives, traces, decisions, and queues rather than becoming the main product frame.

## Surface catalog

| Surface | Primary human roles | Temporal mode | Primary objects | Required backing views/APIs |
|---|---|---|---|---|
| Owner Brief | Dealer owner, Outcome owner, Supervisor | `catching_up` | `Digest`, `Goal`, `OutcomeMetric`, `DecisionCard`, `WorkTrace` | digest summary view; pending decisions by stakes; material events by time window; outcome deltas |
| Mission Control | Supervisor, Exception handler | `attending_now` | `Goal`, `LifecycleWorkflow`, `AgentActivity`, `ExceptionCase`, `PolicyInvocation` | active objectives; agent activity stream; blocked lifecycle gates; risk clusters; trace links |
| Approvals & Exceptions | Reviewer, Approver, Exception handler | `deciding_now` | `DecisionCard`, `ApprovalRequest`, `EvidenceItem`, `PolicyClause` | risk-ranked decision queue; decision detail; approve/reject/modify/defer/request-evidence actions |
| Supplies Autopilot | Supplies owner, Inventory owner, Supervisor | `attending_now` + `deciding_now` | `SupplyRecommendation`, `PreparedShipment`, `SuppressedShipment`, `DecisionCard`, `OutcomeLink` | supply risk queue; auto-ship history; abnormal/high-cost review cards; inventory/cost evidence |
| Lifecycle Workbench | Intent author, Supervisor | `delegating_now` | `Goal`, `ExecutionPlan`, `LifecycleGate`, `ApprovalGate` | plan creation/review; gate status; launch/pause/resume controls; policy version binding |
| Policy Center | Policy owner, Dealer owner, Auditor | `teaching_now` | `PolicyDocument`, `PolicyClause`, `PolicyProposal`, `SimulationResult`, `PolicyCommit` | versioned policy editor; proposals; replay/simulation results; commit/discard actions |
| Audit & Outcomes | Auditor, Policy owner, Outcome owner | `auditing_later` | `WorkTrace`, `DecisionTrace`, `AuditEvent`, `OutcomeMetric`, `OutcomeLink` | trace search; decision provenance; policy invocation timeline; outcome reports |

## First-slice screen set: Supplies Autopilot

The initial implementation slice should make these screens concrete before expanding to service, billing, onboarding, and offboarding.

### Supplies Command Center

Purpose: show whether routine supply automation is healthy and what needs human attention.

Required sections:

- objective banner for `GOAL-02` with active policy version;
- supply-risk metrics: devices below threshold, projected stockouts, auto-shipments prepared, suppressed shipments, pending approvals;
- agent activity stream with `Supplies Agent`, `DCA Monitoring Agent`, `Contract and Policy Agent`, and `Inventory Agent` events;
- pending decision cards ranked by depletion urgency, cost, customer impact, and policy risk;
- recent auto-shipments with trace links and idempotency keys;
- suppressed shipments with explicit reason and safe-default status.

### Supply Decision Card

Purpose: allow a human to decide abnormal, high-cost, constrained, offboarding, or ambiguous shipment recommendations.

Required sections:

- recommendation summary and proposed side effect;
- device/customer lifecycle context;
- policy triggers such as `SUP-2.0`, `SUP-3.0`, `SUP-4.0`, or `SUP-5.0`;
- evidence: telemetry, depletion forecast, contract entitlement, inventory/cost, customer preference, recent service/offboarding context;
- risk, confidence, impact, alternatives considered, and known gaps;
- actions: approve, reject, modify, defer, escalate, request more evidence, create policy proposal/reference example;
- trace and outcome follow-up links.

### Shipment Trace Drawer

Purpose: explain why a shipment was prepared, approved, suppressed, or blocked.

Required timeline events:

- telemetry refresh;
- depletion forecast;
- entitlement and lifecycle checks;
- inventory/supplier lookup;
- policy invocations;
- agent recommendation;
- human decision when applicable;
- supplier/order integration result;
- delivery/outcome follow-up.

## State and realtime behavior

- Decision queues should update through SSE or WebSocket when recommendations, approvals, evidence, or workflow states change.
- A decision card open in the browser must show stale-state warnings if new evidence or a conflicting human action arrives.
- Approve/reject/modify actions must use optimistic UI only after the backend accepts the command; otherwise show the workflow-safe error state and keep the card pending.
- Routine activity can be compressed in summaries, but each summary item must drill down to trace facts.

## Accessibility and responsiveness

- Approval actions require clear labels, keyboard access, focus management, and confirmation for irreversible or high-impact actions.
- Evidence, policy clause, risk, confidence, and impact fields must remain visible on narrow screens before action controls.
- Color is not the only risk indicator; include text labels and ordering.
- Long timelines and evidence lists should preserve semantic headings and progressive disclosure.

## Style guide placeholder

A downstream realized app should select or create `55-ui/style-guide.md` before frontend implementation. Until then, this reference only requires an objective-centered, policy-aware, traceable interface; it does not prescribe dark mode or a futuristic cockpit aesthetic.

## Akka and web UI routing

- Read models for queues, mission control, trace search, and outcomes -> `akka-views`.
- Decision and workflow actions -> `akka-http-endpoints` with component clients.
- Realtime queues and activity -> HTTP SSE or WebSocket plus `akka-web-ui-realtime`.
- Frontend project -> `akka-web-ui-apps`, `akka-web-ui-state-rendering`, `akka-web-ui-forms-validation`, `akka-web-ui-accessibility-responsive`, and `akka-web-ui-testing`.
