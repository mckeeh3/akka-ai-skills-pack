---
name: ai-first-saas-decision-cards
description: Specify decision cards, approval requests, exceptions, deviation review, evidence requirements, learning options, and audit events for ai-first SaaS products.
---

# ai-first-saas-decision-cards

Use this skill when a coding agent must design or implement approval flows, exception handling, deviation review, human decision UI, decision data models, audit events, or acceptance criteria for ai-first SaaS products.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for ai-first SaaS objects, human roles, policy semantics, decision provenance, and audit expectations.
- `docs/ai-first-saas-ui-patterns.md` provides Decision Card / Deviation Review screen composition guidance.
- `docs/skills-pack-tech-stack.md` describes the target Akka + React/Vite/TypeScript implementation stack.

## Core rules

- A high-stakes agent recommendation must become a durable `DecisionCard`, not only a chat message, notification, or log line.
- Approval and exception flows must cite the policy, guardrail, authority boundary, risk threshold, or confidence threshold that triggered review.
- Policy triggers must cite stable policy clause IDs and policy version IDs.
- Store structured rationale, evidence, reasoning factors, alternatives considered, and selected disposition. Do not require storage or display of hidden model chain-of-thought.
- Human decisions must create audit events and may optionally create precedents, reference examples, policy proposals, skill-rule proposals, or threshold proposals.
- Approval controls must be mechanically enforced by workflows and authorization checks; prompts are not an approval boundary.

## When to create a decision card

Create a `DecisionCard` when any condition applies:

- an agent requests permission for a side effect outside its auto-authority;
- an approval gate blocks progress;
- an action violates or may violate a policy clause;
- risk, impact, stakes, uncertainty, or low confidence exceeds review thresholds;
- required evidence is incomplete or conflicting;
- a human override, counterproposal, exception resolution, or escalation is needed;
- the decision may become a precedent, policy update, or learning example;
- a regulated, sensitive, irreversible, or customer-visible action needs explicit authorization.

Do not create decision cards for routine low-risk activity that can be safely tagged `auto` or compressed into audit-complete traces.

## Procedure

### 1. Classify the decision

Identify:

- decision type: `approval`, `rejection`, `counterproposal`, `policy_deviation`, `exception`, `escalation`, `override`, `learning_review`;
- triggering condition and source object;
- involved human role: Reviewer / Approver, Exception Handler, Policy Owner / Coach, Auditor, or Supervisor;
- blocked or proposed action;
- whether the action is reversible;
- due time, SLA, or expiration behavior;
- expected outcome metric or downstream effect.

### 2. Resolve authority and policy trigger

For each card, specify:

- agent ID and agent definition version;
- action class requested;
- permission or approval gate checked;
- active policy document ID and version ID;
- stable policy clause IDs, guardrail IDs, threshold IDs, or skill-rule IDs invoked;
- confidence/risk/impact/stakes values that caused routing;
- why this is not eligible for autonomous execution.

If no stable clause ID exists, create a backlog item to add addressable policy clauses before implementation is considered complete.

### 3. Define required evidence

List evidence by type and minimum quality requirements. Common evidence includes:

- source records or domain artifacts;
- data freshness and provenance;
- tool outputs and validation results;
- policy excerpts and clause citations;
- agent observations and structured summaries;
- similar precedents and their outcomes;
- alternatives considered;
- reversibility or rollback information;
- missing, stale, or contradictory evidence warnings.

Evidence must be inspectable and linkable. Avoid evidence fields that contain only generic prose such as `the AI thinks this is fine`.

### 4. Specify human actions and consequences

For each available action, define state transition, side effects, required comment fields, learning options, and audit events.

Common actions:

- `approve`: authorize the proposed action and resume blocked workflow.
- `approve_with_conditions`: authorize with explicit constraints or modifications.
- `reject`: deny the proposed action and stop or reroute work.
- `counterproposal`: replace the recommendation with human-authored direction.
- `ask_agent_to_revise`: return to agent with requested changes.
- `request_more_evidence`: pause until required evidence is added.
- `escalate`: route to another role or authority level.
- `override_policy_once`: permit a one-time exception without changing future behavior.
- `mark_as_precedent`: store decision and outcome as a precedent.
- `propose_policy_update`: create a policy proposal for separate governance approval.
- `propose_example_or_rule`: create a reference example or skill-rule proposal.

### 5. Design learning options safely

A decision can teach the system, but it must not silently mutate active behavior. Model learning as one of:

- `one_time`: applies only to this case.
- `precedent`: creates a prior decision reference for future cards.
- `positive_example`: adds a proposed example of desired behavior.
- `negative_example`: adds a proposed example of behavior to avoid.
- `policy_proposal`: drafts a clause or threshold change for human approval.
- `skill_rule_proposal`: drafts a skill rule change for human approval.

Policy, skill, prompt, guardrail, threshold, approval-gate, and permission changes require explicit human authorization and commit events before activation.

## Decision card output template

Use this template when producing a product spec, data model, API contract, UI spec, or implementation plan.

```yaml
decision_card:
  id: string
  title: string
  decision_type: approval | rejection | counterproposal | policy_deviation | exception | escalation | override | learning_review
  status: draft | pending_review | awaiting_evidence | escalated | approved | approved_with_conditions | rejected | countered | withdrawn | expired | resolved
  priority: low | medium | high | critical
  source:
    goal_id: string | null
    execution_plan_id: string | null
    task_id: string | null
    agent_run_id: string | null
    workflow_id: string | null
    trace_id: string
  requester:
    agent_id: string
    agent_definition_version_id: string
    system_prompt_version_id: string
    skill_version_ids: string[]
  reviewer:
    required_role: string
    assigned_user_id: string | null
    due_time: datetime | null
  context_summary: string
  proposed_action:
    action_id: string
    description: string
    action_class: string
    side_effect_level: none | internal | customer_visible | financial | legal | irreversible
    reversibility: reversible | compensatable | irreversible | unknown
  agent_recommendation:
    recommendation: string
    disposition_requested: approve | reject | counter | revise | escalate
    structured_rationale:
      factors_for: string[]
      factors_against: string[]
      uncertainty_notes: string[]
  scores:
    confidence:
      value: number
      label: high | medium | low
      source: model | deterministic | heuristic | mixed
    risk:
      value: number
      label: high | medium | low
      source: model | deterministic | heuristic | mixed
    impact:
      value: number
      label: high | medium | low
      value_at_risk: string | null
    stakes_estimate: string
  trigger:
    trigger_type: policy_clause | approval_gate | permission_boundary | confidence_threshold | risk_threshold | evidence_gap | exception
    policy_document_id: string | null
    policy_version_id: string | null
    clause_ids: string[]
    guardrail_ids: string[]
    threshold_ids: string[]
    explanation: string
  deviation_statement:
    requested_action: string
    allowed_boundary: string
    gap: string
  required_evidence:
    - evidence_type: string
      requirement: string
      satisfied: boolean
      missing_reason: string | null
  evidence:
    - id: string
      source_type: record | document | tool_output | data_access | policy | precedent | human_note | external_system
      source_ref: string
      summary: string
      strength: high | medium | low
      freshness: current | stale | unknown
      link: string | null
  alternatives_considered:
    - option: string
      projected_outcome: string
      risk_delta: string
      reason_not_recommended: string
  precedents:
    - decision_id: string
      summary: string
      outcome: string
      similarity_reason: string
  available_actions:
    - action: approve | approve_with_conditions | reject | counterproposal | ask_agent_to_revise | request_more_evidence | escalate | override_policy_once
      requires_comment: boolean
      creates_side_effect: boolean
      next_status: string
  learning_options:
    allowed: boolean
    selected_mode: none | one_time | precedent | positive_example | negative_example | policy_proposal | skill_rule_proposal
    proposed_change_summary: string | null
    requires_separate_governance_approval: boolean
  audit:
    created_at: datetime
    created_by_agent_id: string
    audit_event_ids: string[]
    trace_link: string
```

## Approval and decision state transitions

Model approval and decision state explicitly. A prototype may collapse states visually, but the backend should preserve meaningful transitions.

```text
draft
→ pending_review
→ approved
→ resolved
```

```text
pending_review
→ awaiting_evidence
→ pending_review
```

```text
pending_review
→ escalated
→ pending_review | approved | rejected | countered
```

```text
pending_review
→ approved_with_conditions
→ resolved
```

```text
pending_review
→ rejected | countered | withdrawn | expired
```

Recommended status meanings:

- `draft`: card assembled but not yet visible to reviewers.
- `pending_review`: assigned or queued for human action.
- `awaiting_evidence`: blocked until additional evidence is collected.
- `escalated`: routed to a different authority or role.
- `approved`: proposed action authorized as-is.
- `approved_with_conditions`: action authorized with explicit constraints.
- `rejected`: proposed action denied.
- `countered`: human supplied a replacement direction.
- `withdrawn`: originating agent or workflow no longer needs the decision.
- `expired`: deadline passed; fallback policy applies.
- `resolved`: workflow consumed the human decision and recorded outcome linkage.

## Approval request lifecycle

Use `ApprovalRequest` for the workflow-blocking authorization object and `DecisionCard` for the review surface/object that presents context.

```yaml
approval_request:
  id: string
  decision_card_id: string
  status: requested | queued | in_review | awaiting_evidence | escalated | approved | rejected | expired | cancelled | consumed
  requested_action: string
  requester_agent_id: string
  required_approver_role: string
  policy_trigger_refs: string[]
  blocks_workflow_id: string
  expires_at: datetime | null
  result_decision_id: string | null
```

Lifecycle:

```text
requested → queued → in_review → approved → consumed
requested → queued → in_review → rejected
in_review → awaiting_evidence → in_review
in_review → escalated → in_review | approved | rejected
queued | in_review → expired
requested | queued | in_review → cancelled
```

The workflow must pause before the protected side effect and resume only after a valid approval result is consumed.

## Exception lifecycle

Use `Exception` when the system cannot proceed safely because of ambiguity, missing data, policy conflict, tool failure, authorization failure, or unexpected state.

```yaml
exception:
  id: string
  type: data_gap | policy_conflict | permission_denied | tool_failure | low_confidence | high_risk | human_escalation | unexpected_state
  status: detected | triaged | decision_requested | mitigated | resolved | dismissed | failed
  related_decision_card_id: string | null
  related_trace_id: string
  resolution_summary: string | null
```

Common flow:

```text
detected → triaged → decision_requested → resolved
triaged → mitigated → resolved
triaged → dismissed
triaged → failed
```

## Audit events

At minimum, specify events for:

- `decision_card_created`
- `decision_card_presented`
- `approval_requested`
- `approval_assigned`
- `evidence_added`
- `evidence_requested`
- `decision_escalated`
- `human_decision_recorded`
- `approval_consumed_by_workflow`
- `agent_action_authorized`
- `agent_action_rejected`
- `policy_override_used`
- `precedent_created`
- `reference_example_proposed`
- `policy_update_proposed_from_decision`
- `skill_rule_proposed_from_decision`
- `decision_outcome_linked`

Each audit event should include actor, timestamp, tenant, source object IDs, policy/version references, trace ID, and before/after status where applicable.

## Implementation mapping for the target stack

Backend Akka mapping:

- Use Event Sourced Entities for `DecisionCard`, `ApprovalRequest`, or high-risk `Exception` objects when temporal history and auditability are central.
- Use Workflows to pause protected side effects, wait for approvals, handle expiration/escalation, and resume or compensate after a human decision.
- Use Views for reviewer queues ranked by stakes, status, SLA, role, policy area, agent, goal, or domain object.
- Use Consumers to create notifications, digest entries, precedent candidates, and audit projections from decision events.
- Use Timed Actions for approval deadlines, reminder notifications, expiration, and stale evidence checks.
- Use Akka agents for recommendation drafting, evidence summarization, precedent retrieval, and policy-change proposal drafting, but keep final authorization in workflow/entity commands.

Frontend React/Vite/TypeScript mapping:

- Implement a focused `DecisionCard` screen/component with evidence, policy trigger, alternatives, precedent, scores, action panel, learning options, and audit link.
- Implement queue items that show title, priority, due time, risk, confidence, impact, policy trigger, and blocked workflow.
- Use typed command APIs for actions; do not let UI-only state imply approval.
- Stream or refresh evidence-gathering progress when the card is awaiting evidence.

## Acceptance checklist

Before considering a decision-card design complete, verify:

- [ ] The card has a durable ID, status, source objects, and trace link.
- [ ] The card cites stable policy clause IDs or records why policy is missing.
- [ ] Recommendation, evidence, risk, confidence, impact, and stakes are visible.
- [ ] Alternatives considered are structured and persisted.
- [ ] Human actions have explicit state transitions and side effects.
- [ ] Approval gates block side effects until the workflow consumes authorization.
- [ ] Structured rationale is stored without requiring hidden chain-of-thought.
- [ ] Learning options cannot activate prompt, skill, policy, threshold, guardrail, approval-gate, or permission changes without human governance approval.
- [ ] Every human decision creates audit events and can link to outcomes.
- [ ] The card links to full work trace, tool invocations, data access events, and policy invocations.
