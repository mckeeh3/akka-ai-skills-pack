---
name: ai-first-saas-worked-example
description: Use a compact end-to-end customer support operations example as a reference for applying the ai-first SaaS framework without recreating the full framework.
---

# ai-first-saas-worked-example

Use this skill when a coding agent needs a concrete reference example before applying the ai-first SaaS framework to a new product, backlog, object model, UI spec, or test plan.

References:

- `docs/ai-first-saas-coding-agent-framework.md` defines the canonical vocabulary and required ai-first loop.
- `docs/ai-first-saas-ui-patterns.md` defines screen-composition patterns.
- `docs/skills-pack-tech-stack.md` defines the Akka + React/Vite/TypeScript target stack.

This is a reference slice, not a second framework. Adapt the structure to the user's domain instead of copying names blindly.

## Worked example: customer support operations copilot

### Product objective

Reduce time-to-resolution for enterprise support tickets while keeping refunds, service credits, sensitive-data exposure, and policy exceptions under human governance.

### Human roles

- **Support Director / Intent Author:** defines resolution goals, customer tiers, escalation rules, and success criteria.
- **Support Supervisor:** monitors active ticket goals, agent throughput, exception queues, and SLA risk.
- **Reviewer / Approver:** approves refunds, service credits, policy deviations, and high-risk customer communications.
- **Policy Owner / Coach:** updates response, refund, escalation, and evidence policies from decisions.
- **Auditor:** reviews traces for who authorized actions, which data was accessed, and why an agent recommended an outcome.
- **Outcome Owner:** tracks resolution time, reopen rate, customer satisfaction, and avoided escalations.

### Domain entities

```yaml
domain_entities:
  - name: CustomerAccount
    purpose: company, plan, tier, contractual support entitlements, account health
  - name: SupportTicket
    purpose: customer issue, severity, SLA deadline, conversation thread, current owner
  - name: ProductIncident
    purpose: known outage/bug linked to affected customers and status updates
  - name: RefundOrCreditRequest
    purpose: proposed monetary remediation requiring policy checks and approval gates
  - name: KnowledgeArticle
    purpose: approved support guidance and troubleshooting instructions
```

### Agent team

```yaml
agents:
  - id: support-coordinator
    category: coordinator
    purpose: Convert ticket-resolution objectives into execution plans, assign specialists, and route decisions.
    tools_allowed: [ticket_read, ticket_update_draft, agent_task_dispatch]
    approval_required_actions: [activate_high_severity_plan, close_escalated_ticket]

  - id: ticket-triage-agent
    category: operations
    purpose: Classify incoming tickets, severity, sentiment, SLA risk, and likely product area.
    tools_allowed: [ticket_read, account_read, incident_read]
    auto_actions: [set_initial_category, link_known_incident, mark_sla_risk]

  - id: resolution-agent
    category: domain_specific
    purpose: Draft troubleshooting steps, collect evidence, and recommend next actions.
    tools_allowed: [ticket_read, knowledge_search, diagnostic_query]
    approval_required_actions: [send_customer_message_for_enterprise_sev1]

  - id: remediation-agent
    category: operations
    purpose: Evaluate refund or service-credit eligibility and prepare approval requests.
    tools_allowed: [contract_read, billing_read, refund_quote]
    approval_required_actions: [issue_refund, issue_service_credit]

  - id: policy-coach-agent
    category: policy
    purpose: Convert repeated human decisions into proposed policy clauses or reference examples.
    tools_allowed: [decision_read, policy_draft_create, replay_request]
    approval_required_actions: [activate_policy_change]
```

### One durable goal

```yaml
goal:
  id: goal-ent-support-week-2026-05-11
  title: Resolve this week's enterprise severity-2 tickets inside SLA
  owner_role: Support Supervisor
  success_criteria:
    - 90% of enterprise severity-2 tickets receive a qualified response within 2 business hours.
    - 80% are resolved or have a customer-approved workaround within 24 hours.
    - No refund, credit, or contractual admission is sent without approval.
  constraints:
    - Follow Support Response Policy v3.2.
    - Redact API keys, secrets, and personal data from model-visible summaries.
    - Escalate tickets with legal threat, churn risk above 0.7, or credit value above $1,000.
```

### One execution plan

```yaml
execution_plan:
  id: plan-ent-support-week-001
  goal_id: goal-ent-support-week-2026-05-11
  policy_versions: [support-response-policy:v3.2, refund-credit-policy:v2.1]
  steps:
    - Triage all open enterprise severity-2 tickets and link known incidents.
    - Rank tickets by SLA risk, account tier, customer sentiment, and impact.
    - Draft resolution paths using approved knowledge articles and diagnostics.
    - Request human approval for high-risk communications, refunds, credits, or policy deviations.
    - Send approved responses or update tickets with next-step recommendations.
    - Record outcomes and reopen/reply signals for calibration.
  approval_gates:
    - customer_message_enterprise_sev1_or_legal_risk
    - refund_or_credit_above_1000_usd
    - deviation_from_approved_workaround
```

### One policy

```yaml
policy_document:
  id: support-response-policy
  version_id: v3.2
  clauses:
    - id: SRP-4.1
      type: escalation
      text: Tickets mentioning legal action, security breach, or executive sponsor dissatisfaction must be escalated to a human reviewer before external response.
    - id: SRP-5.3
      type: approval_threshold
      text: Enterprise severity-1 or severity-2 customer messages may be auto-drafted, but must be approved when confidence is below 0.82 or impact is high.
    - id: SRP-7.2
      type: data_tool_boundary
      text: Agents may summarize logs but must not expose secrets, credentials, or raw personal data in customer-facing drafts.
```

### One decision card

```yaml
decision_card:
  id: dc-ticket-8842-credit
  title: Approve $1,500 service credit for ACME outage impact?
  decision_type: approval
  context_summary: ACME had 3 hours of degraded API performance tied to incident INC-77; contract allows discretionary credit above $1,000 only with supervisor approval.
  agent_id: remediation-agent
  agent_recommendation: Approve a $1,500 service credit and send the approved incident-response template.
  confidence: { value: 0.86, label: high }
  risk: { value: 0.62, label: medium }
  impact: { value: 0.78, label: high }
  policy_trigger:
    policy_document_id: refund-credit-policy
    policy_version_id: v2.1
    clause_id: RCP-2.4
    explanation: Credits above $1,000 require human approval.
  evidence:
    - source: incident INC-77
      summary: API error rate exceeded SLO for ACME region from 09:10 to 12:15 UTC.
      strength: high
    - source: contract ACME-2025
      summary: Enterprise plan permits discretionary credits after material SLA breach.
      strength: medium
  alternatives_considered:
    - option: Offer apology without credit
      reason_rejected: Likely insufficient for high-value account with documented SLA breach.
    - option: Offer $500 credit
      reason_rejected: Below internal precedent for comparable outage duration.
  suggested_actions: [approve, reject, counter, request_more_evidence, escalate]
  learning_option:
    apply_as: precedent
    proposed_policy_change: null
```

### One audit trace

```yaml
work_trace:
  id: trace-ticket-8842
  goal_id: goal-ent-support-week-2026-05-11
  execution_plan_id: plan-ent-support-week-001
  ticket_id: TICKET-8842
  timeline:
    - event: ticket_triaged
      agent_id: ticket-triage-agent
      details: Enterprise severity-2, high sentiment risk, linked to INC-77.
    - event: data_accessed
      agent_id: remediation-agent
      details: Read contract summary, billing tier, incident duration, prior credits.
    - event: policy_invoked
      policy: refund-credit-policy:v2.1#RCP-2.4
    - event: approval_requested
      decision_card_id: dc-ticket-8842-credit
    - event: human_decision_made
      actor_role: Reviewer / Approver
      decision: approved_as_precedent
    - event: customer_message_sent
      authorization: approval dc-ticket-8842-credit
  outcome_link: outcome-ticket-8842-resolution
  rollback_status: credit_reversal_possible_until_invoice_close
```

### One replay scenario

```yaml
replay_scenario:
  id: replay-refund-threshold-v2-2
  proposed_change: Lower automatic review threshold for service credits from $1,000 to $750 for enterprise accounts.
  historical_fixture_window: last_90_days_enterprise_credit_decisions
  side_effect_mode: no_side_effects_mocked_tools
  expected_output:
    changed_decisions: count and IDs that would newly require review
    risk_delta: additional supervisor queue load and avoided unauthorized-credit risk
    outcome_uncertainty: replay cannot prove customer satisfaction impact without delayed outcomes
  approval_requirement: Policy Owner must approve before activation.
```

### One backlog slice

```yaml
backlog_slice:
  name: Enterprise support credit approval slice
  user_story: As a support supervisor, I can review agent-recommended service credits with evidence, policy triggers, and an audit trace before any credit is issued.
  backend:
    - Akka Event Sourced Entity: ApprovalRequest with requested/approved/rejected/countered states.
    - Akka Workflow: pause remediation execution until approval, then resume or compensate.
    - Akka View: pending approvals ranked by stakes and SLA risk.
    - Consumer: append audit events for data access, policy invocation, approval request, and human decision.
  agents:
    - remediation-agent produces structured DecisionCard output and cites stable policy clauses.
  frontend:
    - React DecisionCard screen with evidence, alternatives, risk/confidence/impact, and approve/reject/counter actions.
    - Link from the card to WorkTraceTimeline.
  tests:
    - Credit above $1,000 cannot execute without human approval.
    - Decision card must cite refund-credit-policy:v2.1#RCP-2.4.
    - Approval creates audit event and resumes workflow exactly once.
```

## How to use this example

When adapting this example to another domain:

1. Keep the ai-first shape: durable goal, execution plan, bounded agents, policy clauses, decision card, trace, replay, and backlog slice.
2. Replace domain entities and policies with the user's real operational domain.
3. Preserve human approval for high-stakes side effects.
4. Preserve stable policy clause IDs and trace links.
5. Keep examples concise enough to guide implementation without becoming the product spec.
