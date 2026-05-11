---
name: ai-first-saas-outcomes-metrics
description: Define outcomes, metrics, attribution, event links, CQRS read models, analytics views, UI-facing metrics, and validation tests for ai-first SaaS systems.
---

# ai-first-saas-outcomes-metrics

Use this skill when a coding agent must design how an ai-first SaaS product measures whether delegated agent work produced business value, safety, quality, or operational improvement.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for goals, decisions, traces, audit events, and outcome links.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack: Event Sourced Entities, Key Value Entities, Workflows, Consumers, Views/projections, analytics read models, HTTP APIs, and UI-facing metrics.

## Core principle

Outcomes are durable evidence about what happened after agent work, not vanity counters. They must link back to the goals, decisions, agents, policies, prompts, skills, traces, and external conditions that plausibly influenced them. Attribution should be useful but humble: most outcomes are multi-causal and uncertain.

Do not present outcome attribution as proof unless the product has a valid experiment, control group, or deterministic causal relationship. Prefer `contribution`, `correlation`, `unknown`, or `needs_review` when causal certainty is unavailable.

## What counts as an outcome

An outcome is an observed or reported result that helps answer whether an ai-first workflow succeeded.

Examples:

- business result: revenue recovered, ticket resolved, claim approved correctly, incident prevented;
- operational result: cycle time reduced, backlog cleared, SLA met, escalations avoided;
- quality result: error rate, rework rate, customer satisfaction, human override rate;
- safety/governance result: policy violations, approval bypass attempts blocked, audit completeness;
- learning result: decision precedent effectiveness, policy change impact, replay regression improvement.

Distinguish outcomes from:

- activity metrics: actions taken, messages sent, records processed;
- model metrics: token count, latency, confidence scores;
- UI metrics: page views or clicks;
- intermediate signals: risk score, recommendation, pending approval.

Activity and model metrics can be leading indicators, but they are not outcomes unless tied to an objective.

## Leading vs lagging indicators

For each goal, define both leading and lagging measures.

- **Leading indicators** appear quickly and predict future success. Examples: first response time, number of correctly auto-classified items, approval queue age, evidence completeness, policy violation rate.
- **Lagging indicators** prove later business impact. Examples: renewal saved, payment collected, incident cost avoided, customer satisfaction after resolution, compliance finding avoided.

Use leading indicators for real-time supervision and lagging indicators for outcome review, calibration, and governance changes.

## Human-entered vs automatically observed outcomes

Capture outcome source explicitly.

- `automatic`: derived from system events, external APIs, workflow state, or domain records.
- `human_entered`: entered by an outcome owner, reviewer, auditor, or operator.
- `imported`: synced from analytics, warehouse, CRM, ticketing, billing, monitoring, or compliance systems.
- `inferred`: derived by an agent or classifier and requiring confidence, evidence, and validation status.

Human-entered outcomes should record the human actor, rationale, evidence, and whether the value is final or provisional. Inferred outcomes should not silently become authoritative.

## Delayed outcome handling

Many outcomes arrive after a decision or agent action. Design for delayed linking.

Required behaviors:

1. Create `OutcomePending` records when the expected result is not yet knowable.
2. Store expected observation windows, e.g. `24h`, `7d`, `quarter_end`.
3. Use Akka Timed Actions or timers to check delayed outcomes.
4. Use Consumers to react when external or domain events provide result evidence.
5. Allow late-arriving outcomes to update metrics without rewriting historical audit events.
6. Preserve both initial disposition and final observed outcome.

## Attribution model

Attribute outcomes to the artifacts that influenced them, but record uncertainty.

Potential attribution targets:

- `Goal` and `Objective` pursued;
- `ExecutionPlan` and workflow run;
- `Decision`, `ApprovalRequest`, `Exception`, or human override;
- `Agent` and agent run;
- system prompt version;
- skill version and relevant skill rule IDs;
- policy document/version and stable clause IDs;
- tool invocation or data source;
- human reviewer or approver;
- external factors such as market condition, customer behavior, outage, vendor delay, seasonality, or manual work outside the system.

Attribution modes:

- `direct`: outcome deterministically follows the action, e.g. payment was sent after approval;
- `contributing`: artifact plausibly contributed but was not sole cause;
- `correlated`: related in time or segment, causal link unproven;
- `counterfactual_estimate`: estimated against baseline, replay, or experiment;
- `external`: mostly caused by outside factors;
- `unknown`: insufficient evidence.

Always include an `attribution_confidence` and notes describing limitations.

## Outcome metric schema

Use this template when designing outcome objects and metric catalogs.

```yaml
outcome_metric:
  id: string
  name: string
  description: string
  metric_type: business | operational | quality | safety | governance | learning
  objective_ids: string[]
  owner_role: intent_author | supervisor | reviewer | policy_owner | auditor | outcome_owner
  unit: string
  direction: increase_is_good | decrease_is_good | target_range | boolean_success
  leading_or_lagging: leading | lagging
  source_type: automatic | human_entered | imported | inferred
  source_system: string | null
  observation_window: string
  calculation: string
  baseline:
    value: number | string | null
    period: string | null
  target:
    value: number | string
    period: string
  segmentation:
    tenant_id: boolean
    agent_id: boolean
    workflow_type: boolean
    policy_version: boolean
    risk_band: boolean
    custom_dimensions: string[]
  attribution_targets:
    - target_type: goal | execution_plan | workflow_run | decision | agent | prompt_version | skill_version | policy_clause | tool_invocation | human_actor | external_factor
      target_id: string
      attribution_mode: direct | contributing | correlated | counterfactual_estimate | external | unknown
      attribution_confidence: number
      notes: string
  trace_links:
    work_trace_ids: string[]
    audit_event_ids: string[]
    decision_ids: string[]
  validation:
    status: provisional | validated | disputed | superseded
    validated_by: string | null
    validation_method: event_derived | human_review | reconciliation | experiment | audit
  privacy:
    contains_sensitive_data: boolean
    redaction_rule: string | null
    retention_period: string
```

## Outcome event and link schema

Emit meaningful events when outcomes are expected, observed, validated, disputed, or linked.

```yaml
outcome_event:
  event_name: OutcomeExpected | OutcomeObserved | OutcomeLinked | OutcomeValidated | OutcomeDisputed | OutcomeSuperseded
  outcome_id: string
  metric_id: string
  value: number | string | boolean
  observed_at: datetime
  source_type: automatic | human_entered | imported | inferred
  source_ref: string | null
  related_goal_id: string | null
  related_trace_id: string | null
  attribution_summary: string
  validation_status: provisional | validated | disputed | superseded
```

```yaml
outcome_link:
  id: string
  outcome_id: string
  linked_object_type: goal | execution_plan | workflow_run | task_run | agent_run | decision | approval_request | exception | audit_event | work_trace | policy_clause | skill_version | prompt_version | tool_invocation | external_factor
  linked_object_id: string
  relationship: caused | contributed_to | measured | blocked | mitigated | correlated_with | contradicted | unknown
  confidence: number
  evidence: string[]
  limitation_notes: string
```

## Akka stack mapping

Map outcome capabilities to the target stack as follows.

- **Event Sourced Entities**: use for goals, decisions, policy versions, and outcome records when history, provenance, disputes, or corrections matter.
- **Key Value Entities**: use for simple metric definitions, current dashboards, or low-risk metric configuration.
- **Workflows**: coordinate delayed outcome observation, validation, reconciliation, dispute handling, and post-decision follow-up.
- **Consumers**: subscribe to domain, workflow, tool, audit, and external events to derive outcomes and create `OutcomeObserved` or `OutcomeLinked` events.
- **Timed Actions/timers**: schedule delayed checks, SLA windows, stale-outcome reminders, and periodic metric refresh.
- **Views/projections**: build CQRS read models for metric dashboards, goal outcome summaries, agent performance trends, policy impact views, and audit-linked outcome drilldowns.
- **HTTP/gRPC APIs**: expose metric catalogs, outcome records, dashboards, filters, and validation actions.
- **React/Vite/TypeScript UI**: render outcome dashboards, goal progress panels, agent/team scorecards, decision outcome panels, policy impact views, and trace-linked drilldowns.

## UI-facing metrics views

Provide read models for these surfaces:

- **Goal outcome view**: progress against objective, leading indicators, lagging outcomes, pending observations.
- **Command center metrics**: active risk, SLA pressure, approval queue impact, exceptions, near-term leading indicators.
- **Decision outcome panel**: what happened after an approval/rejection/counterproposal, including delayed status.
- **Agent performance view**: outcome trends by agent, action class, risk band, escalation rate, and human override rate.
- **Policy impact view**: outcomes before/after policy, threshold, prompt, or skill changes, with replay/simulation context.
- **Audit drilldown**: link from any metric to trace, events, evidence, policy clauses, and decisions behind the number.

Every aggregate metric should allow drilldown to representative or complete underlying events subject to privacy and retention rules.

## Attribution limits and uncertainty rules

- Never imply an agent caused an outcome solely because it acted before the outcome.
- Separate measured fact from interpretation.
- Show baselines, denominators, time windows, and segment filters.
- Mark small samples and incomplete observation windows.
- Preserve external-factor notes.
- For policy/prompt/skill comparisons, distinguish observed production outcomes from replay or simulation estimates.
- Use experiments, holdouts, or matched baselines before claiming causal improvement.

## Outcome validation tests

Create tests for:

- outcome events are emitted when expected and observed;
- delayed timers create follow-up checks without duplicate outcomes;
- Consumers correctly derive metrics from domain and audit events;
- `OutcomeLink` records connect outcomes to goals, decisions, traces, agents, policy clauses, prompts, and skills;
- disputed or corrected outcomes preserve history rather than overwriting audit facts;
- UI read models show baselines, windows, denominators, attribution mode, and uncertainty;
- privacy rules redact sensitive metric dimensions;
- replay/simulation results are labeled as estimates, not production outcomes;
- human-entered and inferred outcomes require validation metadata;
- aggregate metrics can drill down to traceable evidence.

## Output format for coding agents

When applying this skill, produce:

```yaml
outcomes_metrics_design:
  product_area: string
  objectives:
    - objective_id: string
      success_definition: string
      leading_metrics: string[]
      lagging_metrics: string[]
  metric_catalog:
    - outcome_metric: {}
  outcome_events:
    - name: string
      trigger: string
      payload_fields: string[]
      producer: entity | workflow | consumer | timed_action | external_import
  attribution_plan:
    targets: string[]
    attribution_modes_used: string[]
    uncertainty_notes: string[]
    external_factors: string[]
  akka_mapping:
    entities: string[]
    workflows: string[]
    consumers: string[]
    timed_actions: string[]
    views: string[]
    apis: string[]
  ui_views:
    - name: string
      audience_role: string
      metrics_shown: string[]
      drilldowns: string[]
  validation_tests:
    - name: string
      scenario: string
      expected_events: string[]
      expected_read_model: string
      expected_uncertainty_label: string
```
