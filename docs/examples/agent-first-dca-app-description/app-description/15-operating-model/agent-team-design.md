# Agent Team Design

## Purpose

This file defines the bounded agent team for the agent-first DCA example. It is app-description reference material, not runnable Akka code.

## Team shape

Use a human-supervised coordinator-plus-specialists shape.

- Durable lifecycle workflows coordinate long-running customer, device, collector, supply, service, billing, and offboarding progress.
- Specialist agents perform narrow interpretation, forecasting, recommendation, review, or summary work.
- Humans retain authority for high-impact, ambiguous, policy-changing, customer-sensitive, billing-impacting, or retention/deletion decisions.

Akka substrate mapping:

- multi-step lifecycle progress and approval pauses -> workflows;
- bounded model calls and structured recommendations -> agents;
- consequential goals, recommendations, decisions, approvals, policy commits, and traces -> event sourced entities;
- command-center and queue projections -> views;
- deadlines, rechecks, reminders, and digests -> timed actions;
- trace fanout and notifications -> consumers.

## Agent roster

| Agent | Single responsibility | Autonomous when | Escalates when | Primary records emitted |
|---|---|---|---|---|
| Owner Briefing Agent | Summarize activity, risk, outcomes, and waiting decisions for the dealer owner. | Summarizing already-recorded events and ranking decision cards by configured policy. | A summary would imply policy change, expand authority, or hide high-risk unresolved work. | `BriefingSummary`, `DigestItem`, `TraceLink` |
| Onboarding Agent | Create and maintain onboarding plans from contract, customer, site, device, and DCA requirements. | Required data is present and tasks are within standard onboarding policy. | Contract terms, access windows, substitutions, baselines, or site requirements are missing or unusual. | `OnboardingPlan`, `LifecycleGateCheck`, `ApprovalRequest` |
| Install Coordinator Agent | Prepare installation tickets and validate completion evidence. | Technician assignment, checklist creation, and follow-up scheduling fit policy. | Device substitution, second visit, customer access exception, or manual completion evidence requires approval. | `InstallTask`, `InstallValidation`, `ExceptionCase` |
| DCA Monitoring Agent | Validate collector health, telemetry freshness, and discovery consistency. | Flagging stale telemetry, rechecking collectors, and opening low-risk configuration tasks. | Collector removal/deauthorization, missing device discovery, contract mismatch, or prolonged outage affects service/billing. | `TelemetryHealthCheck`, `CollectorException`, `DataAccessEvent` |
| Fleet Health Agent | Interpret faults, repeated issues, SLA risk, and replacement candidates. | Preparing low-risk service recommendations and routine ticket drafts. | Emergency dispatch, replacement recommendation, high-value customer impact, or repeated unresolved fault. | `ServiceRecommendation`, `SlaRiskSignal`, `DecisionCard` |
| Supplies Agent | Forecast consumable depletion and prepare eligible shipments. | Device is active, contract-covered, stock is available, cost is below threshold, and no abnormal consumption/offboarding flag exists. | Shipment is high cost, abnormal, outside entitlement, customer-specific override applies, inventory is constrained, or customer is offboarding. | `SupplyRecommendation`, `PreparedShipment`, `SuppressedShipment`, `DecisionCard` |
| Meter and Billing Agent | Convert meter telemetry into billing-ready events and billing exceptions. | Reads are fresh, monotonic, mapped to contract terms, and within anomaly thresholds. | Missing reads, usage spike, manual adjustment, final read waiver, or billing batch approval is needed. | `MeterValidation`, `BillingEventDraft`, `BillingException` |
| Contract and Policy Agent | Apply contract/policy clauses and draft governed policy improvements. | Citing active policies, classifying clauses, and drafting proposals without activation. | Contract mismatch, authority expansion, threshold change, or policy activation is required. | `PolicyInvocation`, `PolicyProposal`, `SimulationRequest` |
| Customer Success Agent | Detect churn, renewal, expansion, and sensitive relationship signals. | Preparing context and suggested outreach for standard low-risk cases. | Customer communication is sensitive, churn risk is high, concession is requested, or renewal/upgrade terms are material. | `CustomerRiskSignal`, `OpportunityRecommendation`, `OutreachDraft` |
| Inventory Agent | Check supply/part readiness and recommend replenishment. | Reserving stock or drafting purchase recommendations within stock/cost policy. | Supplier substitution, overstock risk, constrained inventory allocation, or purchase above threshold. | `InventoryCheck`, `PurchaseRecommendation`, `StockException` |
| Offboarding Agent | Create offboarding plans and ensure removal, final billing, deauthorization, and retention gates complete safely. | Creating checklists, pausing automation, and drafting removal tasks under known contract/ownership facts. | Device ownership, final reads, DCA deauthorization, retention, deletion/anonymization, or customer dispute is uncertain. | `OffboardingPlan`, `ArchiveGateCheck`, `RetentionDecisionRequest` |

## Coordinator responsibilities

Lifecycle workflows act as the durable coordinator. They must:

1. load the current goal, lifecycle state, policy version, and relevant customer/device/collector facts;
2. call only the specialist agents needed for the current step;
3. record each recommendation separately from each decision;
4. pause for human approval when policy, risk, confidence, impact, or missing evidence requires it;
5. resume from explicit human decisions or safe recheck events;
6. emit trace records for agent calls, tool/data access, policy invocations, decisions, and outcomes.

## Specialist contract template

Each specialist agent must have:

- one responsibility and explicit non-responsibilities;
- structured output when the result changes durable state, creates a decision card, or appears in a review surface;
- allowed data and tool scopes;
- confidence, risk, and impact fields when the output can trigger action;
- escalation conditions tied to policy clauses or missing evidence;
- trace emission for prompt version, policy version, data sources, tools, and output.

## Non-responsibilities shared by all agents

Agents must not:

- activate new policy, prompt, skill, threshold, permission, or authority without governed commit;
- ship supplies, dispatch service, approve billing, remove devices, deauthorize collectors, or delete/anonymize data when required evidence is missing;
- treat a recommendation as a final decision unless an active policy explicitly grants that autonomy;
- hide uncertainty from the human decision surface;
- bypass role, tenant, customer, or tool permissions.
