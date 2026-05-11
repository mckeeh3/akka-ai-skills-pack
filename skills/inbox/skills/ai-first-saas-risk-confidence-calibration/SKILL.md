---
name: ai-first-saas-risk-confidence-calibration
description: Design score definitions, thresholds, trust levels, routing behavior, calibration datasets, monitoring, and tests for confidence, risk, impact, stakes, and trust in ai-first SaaS systems.
---

# ai-first-saas-risk-confidence-calibration

Use this skill when a coding agent must define how an ai-first SaaS product represents confidence, risk, impact, stakes, trust, and threshold-driven routing for autonomous agent work.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for ai-first SaaS objects, decision cards, disposition tags, approval gates, audit traces, and trust calibration.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack for durable score state, workflows, views, consumers, timed monitoring, APIs, and UI surfaces.

## Core principle

Confidence, risk, impact, stakes, and trust scores are operational controls, not decorative badges. Every displayed score must either:

1. drive a routing, approval, review, escalation, ranking, or automation decision; or
2. explain why a routing decision already occurred.

Do not add opaque AI confidence chips that humans cannot interpret or act on. A score must have a definition, source, range, calibration method, owner, threshold, freshness, and audit trail.

## Concepts and distinctions

- **Confidence**: estimated probability that a recommendation, classification, extraction, or planned action is correct for the stated task.
- **Risk**: likelihood and severity of harm, policy violation, operational failure, security issue, customer impact, or incorrect action.
- **Impact**: expected magnitude of consequences if the action succeeds or fails.
- **Stakes / value-at-risk**: business, customer, legal, financial, safety, or reputation exposure that determines priority and approval rigor.
- **Trust level**: a governed authority tier for an agent, action class, domain, or workflow based on observed performance, calibration, policy compliance, and human oversight history.
- **Disposition**: the routing outcome selected at decision time: `auto`, `review`, `approval`, `escalate`, or `fyi`.

Keep these separate. High confidence does not imply low risk. Low risk does not imply high trust. High impact may require approval even when confidence is high.

## Score ranges and labels

Use normalized numeric scores plus human-readable labels. Preserve raw model/provider scores separately when available.

Recommended normalized ranges:

```yaml
score_scale:
  confidence:
    range: 0.0_to_1.0
    labels:
      low: "0.00-0.59"
      medium: "0.60-0.84"
      high: "0.85-0.94"
      very_high: "0.95-1.00"
  risk:
    range: 0_to_100
    labels:
      low: "0-24"
      medium: "25-59"
      high: "60-84"
      critical: "85-100"
  impact:
    range: 0_to_100
    labels:
      negligible: "0-9"
      low: "10-29"
      medium: "30-59"
      high: "60-84"
      severe: "85-100"
  stakes_value_at_risk:
    range: domain_specific_currency_or_rank
    labels:
      trivial: "no material consequence"
      routine: "bounded operational consequence"
      material: "meaningful customer/business consequence"
      major: "large financial, legal, security, or reputation exposure"
      existential: "business-critical or safety-critical exposure"
```

Adjust ranges per domain, but document the mapping. If labels are shown in the UI, users must be able to inspect the numeric value and definition behind the label.

## Score source types

Every score must record how it was produced.

- `model_generated`: produced by an LLM or model. Requires prompt/version, model/version, evidence, and calibration caveats.
- `deterministic`: calculated by code from explicit rules, e.g. invoice amount, clause match, policy threshold, customer tier.
- `heuristic`: computed from transparent weighted rules that may need tuning.
- `human_assigned`: entered or overridden by an authorized human.
- `ensemble`: combined from multiple sources; must expose component scores and combination logic.

Prefer deterministic and transparent heuristic inputs for approval gates. Use model-generated scores as signals, not sole authorization for high-stakes side effects.

## Calibration dataset requirements

Define calibration datasets for each high-value agent/action class.

Minimum dataset fields:

```yaml
calibration_dataset:
  id: string
  name: string
  domain: string
  action_class: string
  owner_role: policy_owner | auditor | outcome_owner
  examples:
    - example_id: string
      input_snapshot_id: string
      agent_id: string
      action_class: string
      predicted_confidence: number
      predicted_risk: number
      predicted_impact: number
      predicted_disposition: auto | review | approval | escalate | fyi
      ground_truth_outcome: correct | incorrect | partially_correct | harmful | unknown
      human_decision_id: string | null
      outcome_metric_ids: string[]
      policy_clause_ids: string[]
      segment_tags: string[]
  sampling_strategy: random | stratified | high_risk_only | human_reviewed | regression_suite
  minimum_size: number
  refresh_cadence: string
  known_biases_or_gaps: string[]
```

Include successes, failures, edge cases, human overrides, near misses, policy violations, and representative routine work. Do not calibrate only on easy or approved cases.

## Threshold design

Thresholds must be scoped by agent, action class, domain segment, policy, and stakes tier. Avoid one global confidence threshold for the entire product.

```yaml
threshold_policy:
  id: string
  name: string
  applies_to:
    agent_ids: string[]
    action_classes: string[]
    domain_segments: string[]
    policy_clause_ids: string[]
    trust_level_ids: string[]
  score_inputs:
    confidence_score_id: string
    risk_score_id: string
    impact_score_id: string
    stakes_estimate_id: string
    trust_level_id: string
  routes:
    auto:
      min_confidence: number
      max_risk: number
      max_impact: number
      max_stakes_label: trivial | routine | material | major | existential
      required_trust_level: string
      extra_conditions: string[]
    review:
      min_confidence: number | null
      max_risk: number | null
      conditions: string[]
    approval:
      conditions: string[]
      required_approver_roles: string[]
      blocking: true
    escalate:
      conditions: string[]
      escalation_roles: string[]
      response_sla: string
    fyi:
      conditions: string[]
  fallback_route: review | approval | escalate
  owner_role: policy_owner
  version_id: string
  effective_at: datetime
  audit_event_names: string[]
```

If score inputs are missing, stale, out-of-distribution, or contradictory, route to `review`, `approval`, or `escalate`; never silently auto-complete.

## Routing rules

Use scores to select the disposition at decision time.

Typical routing logic:

- `auto`: high calibrated confidence, low risk, low or bounded impact, stakes below auto limit, matching policy, sufficient agent trust, reversible or low-cost side effect.
- `review`: moderate uncertainty or material information useful to humans, but no blocking approval required.
- `approval`: side effect is blocked until authorized because impact, stakes, policy, customer commitment, money movement, data exposure, or authority boundary requires human approval.
- `escalate`: risk is high/critical, policy is ambiguous or violated, confidence is too low for safe recommendation, required data is missing, permission is denied, or incident/security/compliance concerns exist.
- `fyi`: low-risk informational event retained for trace and digest.

Persist the selected disposition, evaluated threshold version, score values, and explanation in the audit trace.

## Stakes and value-at-risk ranking

Pending queues and digests should be ranked by stakes, not chronology alone.

Estimate stakes from domain-specific factors such as:

- customer tier, contract value, invoice amount, account risk, legal deadline, SLA breach risk;
- number of people affected, safety/security exposure, privacy sensitivity;
- reversibility, cost of delay, precedent-setting effect;
- brand/reputation exposure or compliance severity.

Use a transparent `stakes_estimate` object:

```yaml
stakes_estimate:
  id: string
  object_type: goal | task | approval_request | exception | decision | action
  object_id: string
  value_at_risk:
    amount: number | null
    currency: string | null
    rank: trivial | routine | material | major | existential
  factors:
    - name: string
      value: string
      weight: number | null
      source: deterministic | heuristic | model_generated | human_assigned
  reversibility: reversible | partially_reversible | irreversible | unknown
  deadline: datetime | null
  explanation: string
```

## Trust level definition

Trust is a governed operating tier, not a feeling. Define trust per agent and action class.

```yaml
trust_level:
  id: string
  agent_id: string
  action_class: string
  level: probationary | limited | standard | elevated | suspended
  allowed_dispositions: auto | review | approval | escalate | fyi[]
  auto_limits:
    max_risk: number
    max_impact: number
    max_stakes_rank: trivial | routine | material | major | existential
  evidence:
    calibration_dataset_ids: string[]
    recent_accuracy: number
    override_rate: number
    harmful_error_rate: number
    policy_violation_rate: number
    sample_size: number
    observation_window: string
  promotion_criteria: string[]
  demotion_criteria: string[]
  owner_role: policy_owner
  last_reviewed_at: datetime
```

Trust levels should update through governed policy or threshold changes, not automatic silent self-promotion by the agent.

## Score object template

Use this for durable score records and decision-card inputs.

```yaml
calibrated_score:
  id: string
  score_type: confidence | risk | impact | stakes | trust
  normalized_value: number
  label: low | medium | high | very_high | critical | negligible | severe | trivial | routine | material | major | existential | probationary | limited | standard | elevated | suspended
  source_type: model_generated | deterministic | heuristic | human_assigned | ensemble
  source_refs:
    model_id: string | null
    prompt_version_id: string | null
    heuristic_version_id: string | null
    policy_clause_ids: string[]
    calibration_dataset_id: string | null
  object_ref:
    type: recommendation | approval_request | task | tool_invocation | decision | agent_run
    id: string
  generated_at: datetime
  expires_at: datetime | null
  calibration:
    calibrated: boolean
    method: reliability_curve | confusion_matrix | backtest | human_review | none
    expected_error_rate: number | null
    population_segment: string | null
    out_of_distribution: boolean
  explanation:
    summary: string
    top_factors: string[]
    limitations: string[]
  audit_event_id: string
```

## Akka + frontend implementation mapping

Backend design:

- Use Event Sourced Entities for threshold policies, trust-level changes, and score-affecting decisions where history and replay matter.
- Use Key Value Entities for current score snapshots only when full history is already captured by audit events.
- Use Workflows to pause/resume approval gates based on evaluated disposition.
- Use Consumers to update score views when decisions, outcomes, overrides, policy violations, or calibration examples arrive.
- Use Views for pending queues ranked by stakes, per-agent trust dashboards, score history, threshold audit, and calibration monitoring.
- Use Timed Actions for periodic calibration checks, stale-score detection, threshold review reminders, and trust-level review cadences.
- Expose HTTP/gRPC APIs for score details, threshold evaluation, queue ranking, and calibration reports.

Frontend design:

- Show confidence/risk/impact/stakes on decision cards with plain-language explanations and threshold reasons.
- Show why an item was routed to auto/review/approval/escalate/fyi.
- Provide drill-down from a badge to source, calibration, evidence, threshold policy, and audit trace.
- Avoid unexplained red/yellow/green chips without definitions.
- In command centers and digests, rank pending decisions by stakes and deadline, while preserving filters by agent, goal, policy, and risk.

## Testing and monitoring

Create tests for both score quality and routing behavior.

Required tests:

- threshold boundary tests for each agent/action class;
- missing/stale/out-of-distribution score fallback tests;
- approval gate tests proving high-stakes or high-risk actions cannot auto-execute;
- calibration backtests comparing predicted confidence/risk with observed outcomes;
- human override and harmful-error rate monitoring;
- queue ranking tests using stakes/value-at-risk;
- UI tests proving decision cards explain score meaning and routing reason;
- replay regression tests for proposed threshold, trust-level, prompt, skill, or policy changes.

Monitoring signals:

- auto-action error rate;
- approval override rate;
- escalation false-positive and false-negative rates;
- model confidence calibration error by segment;
- policy violation rate by agent/action class;
- stale score count;
- trust-level promotion/demotion history;
- number of human decisions caused by unclear score explanations.

## Output format

When applying this skill, produce this artifact:

```yaml
risk_confidence_calibration_spec:
  product_or_workflow: string
  score_definitions:
    - score_type: confidence | risk | impact | stakes | trust
      purpose: string
      range: string
      labels: string[]
      source_types: string[]
      owner_role: string
  action_classes:
    - name: string
      agents: string[]
      risks: string[]
      side_effects: string[]
      reversibility: reversible | partially_reversible | irreversible | unknown
  threshold_policies:
    - threshold_policy_id: string
      applies_to: string
      auto_conditions: string[]
      review_conditions: string[]
      approval_conditions: string[]
      escalate_conditions: string[]
      fallback_route: review | approval | escalate
  trust_levels:
    - agent_id: string
      action_class: string
      level: probationary | limited | standard | elevated | suspended
      allowed_dispositions: string[]
      promotion_criteria: string[]
      demotion_criteria: string[]
  calibration_datasets:
    - name: string
      examples_needed: string[]
      ground_truth_source: string
      refresh_cadence: string
      known_gaps: string[]
  routing_examples:
    - scenario: string
      scores: string
      selected_disposition: auto | review | approval | escalate | fyi
      explanation: string
      audit_events: string[]
  ui_requirements:
    - surface: decision_card | command_center | async_digest | audit_trace | governance_center
      score_displays: string[]
      drill_downs: string[]
      user_actions: string[]
  tests:
    - name: string
      type: threshold | approval_gate | calibration | replay | ui | monitoring
      expected_result: string
```

## Acceptance checklist

- [ ] Confidence, risk, impact, stakes, and trust are defined separately.
- [ ] Score ranges, labels, source types, and owners are documented.
- [ ] Thresholds are scoped by agent/action class/domain/stakes, not only global.
- [ ] Scores drive or explain disposition routing.
- [ ] High-stakes and high-risk actions cannot auto-execute solely because model confidence is high.
- [ ] Missing, stale, or out-of-distribution scores have safe fallback routing.
- [ ] Calibration datasets include failures, overrides, and edge cases.
- [ ] Trust levels are governed and auditable.
- [ ] Decision cards and queues show score explanations and threshold reasons.
- [ ] Tests verify threshold behavior, approval gates, calibration quality, and UI clarity.
