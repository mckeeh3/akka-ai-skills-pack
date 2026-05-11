# Decisions, Exceptions, and Evidence

## Purpose

This file defines the decision-card and exception pattern for the agent-first DCA example.

The app must keep recommendations distinct from decisions. Agents can prepare or recommend; humans or explicit active policy decide when authority, risk, impact, confidence, or evidence requirements demand review.

## Decision card schema

```yaml
decision_card:
  id: string
  type: supply_shipment | service_dispatch | billing_exception | onboarding_gate | offboarding_gate | policy_change | customer_risk | replacement_review
  title: string
  status: pending | approved | rejected | modified | deferred | escalated | more_evidence_requested | expired
  goal_id: string
  workflow_id: string
  customer_id: string
  site_id: string | null
  device_id: string | null
  collector_id: string | null
  lifecycle_context: string
  recommended_action: string
  recommending_agent: string
  decision_authority: human_role | policy_clause | bounded_automation
  required_role: string | null
  policy_triggers:
    - policy_document_id: string
      policy_version: string
      clause_id: string
      reason: string
  evidence:
    - source: telemetry | meter_read | contract | service_history | inventory | customer_note | lifecycle_state | policy | trace
      summary: string
      observed_at: datetime
      strength: high | medium | low
      link: string | null
  confidence: high | medium | low | numeric
  risk: high | medium | low
  impact:
    cost: string | null
    sla: string | null
    customer_experience: string | null
    billing: string | null
    retention_or_privacy: string | null
  alternatives:
    - option: string
      expected_outcome: string
      reason_not_recommended: string
  known_gaps:
    - string
  allowed_actions:
    - approve
    - reject
    - modify
    - defer
    - escalate
    - request_more_evidence
    - update_policy_from_decision
  deadline: datetime | null
  trace_links:
    - string
  outcome_follow_up: string | null
```

## Allowed actions and state effects

| Action | State effect | Trace requirement |
|---|---|---|
| approve | Workflow resumes and performs the approved side effect or state transition. | Record approver, authority, evidence snapshot, policy version, and approved action. |
| reject | Workflow records rejection, suppresses side effect, and may select safe alternative. | Record reason, rejected recommendation, and any policy feedback. |
| modify | Workflow records human counterproposal and executes only modified approved scope. | Record original recommendation, modification, reason, and affected policy clauses. |
| defer | Workflow pauses until deadline, recheck, or requested event. | Record deferral reason, owner, deadline, and blocked work. |
| escalate | Workflow transfers to higher authority or exception handler. | Record original reviewer, target role, reason, and urgency. |
| request_more_evidence | Workflow asks agents/tools to gather specific missing evidence. | Record requested evidence, requester, and timeout. |
| update_policy_from_decision | Creates policy proposal or reference example; does not activate policy automatically. | Link decision, outcome, proposed clause/example, and required policy owner review. |

## Exception types

| Exception | Trigger | Human owner | Safe default |
|---|---|---|---|
| Missing lifecycle evidence | Required lifecycle gate fact is absent or stale. | Operations supervisor | Pause transition and create evidence request. |
| Contract mismatch | Device/service/supply/billing action cannot be mapped to active contract terms. | Contract/policy owner | Suppress billable/shipment/dispatch side effect. |
| Telemetry stale or conflicting | Collector/device telemetry is stale, missing, or contradicts other facts. | Operations supervisor or DCA owner | Recheck, keep current lifecycle state, and block billing-impacting action. |
| Abnormal consumption | Supply usage exceeds configured threshold or past pattern. | Supplies/inventory owner | Suppress auto-shipment until review. |
| High-cost action | Shipment, part order, dispatch, or replacement exceeds threshold. | Supplies owner, service manager, or dealer owner | Prepare recommendation only. |
| Billing anomaly | Missing read, spike, manual adjustment, or final read waiver. | Billing owner | Hold billing batch or affected line. |
| Sensitive customer action | Outreach, concession, churn risk, or dispute affects relationship. | Customer success manager or dealer owner | Draft only; require review. |
| Retention/deletion action | Archive, delete, anonymize, retention hold, or access revocation is material. | Data steward | Preserve required records and pause destructive action. |
| Policy authority change | Proposal changes thresholds, permissions, prompts, skills, or autonomy. | Dealer owner / policy owner | Proposal only; no activation. |

## Example decision cards

### Supply shipment review

```yaml
type: supply_shipment
title: "Review toner shipment for Acme Legal satellite office"
recommended_action: "Ship one black cartridge from local inventory today"
policy_triggers:
  - clause_id: "SUP-3.0"
    reason: "Usage is 2.3x baseline"
  - clause_id: "SUP-5.0"
    reason: "Customer-specific receiving reliability override is active"
evidence:
  - source: telemetry
    summary: "Toner at 8%; projected depletion in 2.7 days"
    strength: high
  - source: contract
    summary: "Auto-supply included for this device"
    strength: high
  - source: customer_note
    summary: "Satellite receiving has missed two prior deliveries"
    strength: medium
confidence: medium
risk: medium
alternatives:
  - option: "Hold shipment and call site contact"
    expected_outcome: "Avoids misdelivery but increases depletion risk"
    reason_not_recommended: "Depletion window is short"
allowed_actions: [approve, reject, modify, defer, request_more_evidence, update_policy_from_decision]
```

### Installation blocker review

```yaml
type: onboarding_gate
title: "Northwind cannot enter Operational state"
recommended_action: "Schedule follow-up technician visit and keep customer in Installation In Progress"
policy_triggers:
  - clause_id: "INST-2.0"
    reason: "Required DCA collector is installed but not reporting"
evidence:
  - source: telemetry
    summary: "Collector heartbeat stopped 38 minutes after install"
    strength: high
  - source: lifecycle_state
    summary: "3 of 5 planned devices discovered"
    strength: high
confidence: high
risk: medium
allowed_actions: [approve, modify, defer, request_more_evidence]
```

### Offboarding archive blocker

```yaml
type: offboarding_gate
title: "Initech archive blocked by DCA and final-read gaps"
recommended_action: "Create removal ticket, cancel pending shipment, and keep data in Deactivation Pending"
policy_triggers:
  - clause_id: "OFF-2.0"
    reason: "DCA collector still online"
  - clause_id: "BILL-3.0"
    reason: "Final meter reads missing for two leased devices"
evidence:
  - source: telemetry
    summary: "Collector last heartbeat 12 minutes ago"
    strength: high
  - source: meter_read
    summary: "Final reads absent for device assignments DA-17 and DA-18"
    strength: high
  - source: inventory
    summary: "One pending toner shipment exists"
    strength: high
risk: high
allowed_actions: [approve, modify, defer, escalate, request_more_evidence]
```

## Akka substrate mapping

- `DecisionCard`, `Recommendation`, `ApprovalDecision`, `ExceptionCase`, and `Precedent` should use event-sourced history when consequential.
- Approval/exception lifecycles should be workflows with pause/resume and deadline handling.
- Reviewer queues and risk-ranked lists should be views.
- Decision deadlines and stale-card escalation should use timed actions.
- Decision-card APIs and browser UI should route through HTTP endpoint and web UI skills.
- Trace enrichment after decisions should use consumers.
