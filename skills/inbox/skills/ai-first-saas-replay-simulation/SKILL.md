---
name: ai-first-saas-replay-simulation
description: Design replay and no-side-effect simulation for prompt, skill, policy, threshold, and guardrail changes in ai-first SaaS systems.
---

# ai-first-saas-replay-simulation

Use this skill when a coding agent must specify or implement replay, dry runs, impact previews, regression scenarios, or simulation infrastructure for changes to prompts, skills, policies, guardrails, approval gates, permissions, or thresholds.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for replay, simulation, governance, decision provenance, traces, and versioned prompts/skills/policies.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack: Event Sourced Entities, Workflows, Consumers, Views, endpoints, and Akka agents.

## Core rules

- Replay is an accountability and governance tool, not a substitute for production execution.
- Replay evaluates historical or fixture-backed work against proposed prompt, skill, policy, rule, guardrail, approval-gate, permission, or threshold versions.
- Simulation must run in a no-side-effect sandbox. It may read fixtures and call mocked tools, but it must not send emails, charge cards, mutate customer records, call irreversible integrations, or emit production business events.
- Replay results are advisory unless the system proves deterministic equivalence for the specific decision path. Treat LLM-involved results as impact estimates with confidence, not authoritative truth.
- Every replay must state exactly which historical inputs, context versions, policies, tools, model configuration, and fixture responses were used.
- Replay should compare dispositions and decisions: `auto`, `review`, `approval`, `escalate`, `fyi`, `denied`, and `blocked`.
- High-impact changes should not be activated until replay/simulation results are reviewed and accepted by an authorized human.

## When to use replay vs simulation

| Mode | Use when | Input source | Expected output |
|---|---|---|---|
| Historical replay | Preview how proposed changes affect past decisions or work traces | Stored decisions, traces, evidence, tool/data snapshots, context versions | Diff against actual historical result |
| Fixture replay | Test known regression or golden scenarios | Curated deterministic fixtures | Pass/fail plus structured diffs |
| Prospective simulation | Preview a draft goal, plan, or policy before live execution | Draft objects and mocked/preview data | Predicted actions, approvals, risks, and blocked steps |
| Policy impact analysis | Estimate changed routing from threshold/approval/policy edits | Prior policy invocations and decision cards | Counts by changed disposition and risk bucket |
| Agent behavior eval | Compare prompt/skill/rule versions | Scenario set with mocked tools and expected behaviors | Behavioral diffs and evaluator judgments |

## Akka implementation mapping

| Replay concern | Preferred stack component |
|---|---|
| Replay job lifecycle and authoritative result | Event Sourced Entity or Workflow-owned durable state |
| Long-running replay batch | Akka Workflow with pause/cancel/retry support |
| Scenario set or replay fixture | Event Sourced Entity for governed fixtures; Key Value Entity for low-risk drafts |
| Mock tool response registry | Key Value Entity with versioned fixture records and audit events |
| Historical input and trace lookup | Views/projections over audit events and work traces |
| Batch fan-out and result aggregation | Workflow steps plus Consumers |
| Scheduled regression suite | Timed Actions |
| UI impact preview | Views for replay summaries, changed decisions, risk buckets, and drill-down diffs |
| Browser/API access | HTTP endpoints; SSE/WebSocket for streaming replay progress where useful |
| Agent evaluation or LLM-as-judge | Akka agents using deterministic fixtures and structured outputs |

## Replayable input requirements

A decision, task, or goal can be replayed only if enough provenance was stored. Require:

```yaml
replayable_input:
  source_object_type: goal | execution_plan | task_run | recommendation | decision | approval_request | policy_invocation | fixture
  source_object_id: string
  tenant_id: string
  original_trace_id: string | null
  captured_at: datetime
  input_artifacts:
    evidence_item_ids: string[]
    data_snapshot_ids: string[]
    user_instruction_snapshot: string | null
    domain_state_snapshot_ids: string[]
  original_context:
    agent_definition_id: string
    agent_definition_version_id: string
    system_prompt_version_id: string
    skill_version_ids: string[]
    policy_version_ids: string[]
    threshold_version_ids: string[]
    compiled_context_id: string
    model_provider: string | null
    model_name: string | null
    model_parameters: object
  original_result:
    disposition: auto | review | approval | escalate | fyi | denied | blocked
    recommendation: string | null
    decision_id: string | null
    action_taken: string | null
    outcome_link_ids: string[]
```

If a historical item lacks required provenance, mark it `not_replayable` and explain the missing artifacts instead of inventing context.

## Deterministic fixtures and mocked tools

### Fixture rules

- Freeze the input object, evidence, relevant domain state, policy clauses, prompt/skill/rule versions, thresholds, and expected output assertions.
- Prefer compact fixture data over full production records; preserve stable IDs and fields that affect policy or agent behavior.
- Redact sensitive data unless the scenario explicitly tests sensitive-data handling.
- Version fixtures so future replay results can cite the exact fixture used.
- Include negative fixtures for decisions that must remain blocked, escalated, or denied.

### Mock tool rules

- Replace all external integrations with mocked responses or preview-only adapters.
- Mock both success and failure cases, including timeouts, stale data, denied permissions, ambiguous side effects, and malicious/untrusted tool output.
- Persist mock response versions and bind each replay job to them.
- Never allow a replay job to call a side-effecting production tool. Enforce this in code, not by prompt instruction.

```yaml
mock_tool_response:
  id: string
  tool_name: string
  tool_version: string | null
  fixture_version_id: string
  input_matcher: object
  response_payload: object
  response_status: succeeded | failed | timed_out | denied
  latency_ms: number | null
  side_effect_simulated: none | reversible | compensatable | irreversible
  created_by: string
  approved_for_regression: boolean
```

## No-side-effect isolation contract

Every replay/simulation design must include this contract:

```yaml
replay_sandbox_contract:
  environment: replay | simulation | regression
  production_write_access: disabled
  external_side_effect_access: disabled
  allowed_reads:
    - historical_snapshots
    - redacted_trace_artifacts
    - approved_fixtures
    - mock_tool_responses
  allowed_writes:
    - replay_job_state
    - replay_result
    - simulation_result
    - evaluation_metric
    - audit_event_for_replay_activity
  blocked_operations:
    - production_domain_mutation
    - external_customer_communication
    - payment_or_financial_transaction
    - permission_or_policy_activation
    - live_tool_side_effect
  enforcement_mechanism: separate_tool_registry | sandbox_credentials | workflow_guard | policy_evaluator | network_isolation
  verification_tests: string[]
```

Do not rely on the model being told to avoid side effects. Use separate tool registries, sandbox credentials, network isolation, policy checks, and workflow guards.

## Replay procedure

1. **Define the change under review.** Identify proposed prompt, skill, policy, clause, rule, guardrail, permission, approval gate, or threshold versions.
2. **Select scenarios.** Choose historical decisions, policy invocations, work traces, golden fixtures, edge cases, and regression cases that cover the changed behavior.
3. **Validate replayability.** Confirm required provenance, snapshots, context versions, and mock tool responses exist.
4. **Create a replay job.** Bind the job to exact source scenarios, proposed versions, model configuration, mock registry, and sandbox contract.
5. **Run without side effects.** Execute through replay workflows/agents using mocked tools and production writes disabled.
6. **Capture structured results.** Persist recommendation, disposition, policy triggers, evidence use, scores, blocked actions, and evaluator outputs.
7. **Diff against baseline.** Compare original vs replayed disposition, recommendation, approval requirement, policy invocation, risk/confidence scores, side-effect intent, and expected outcome.
8. **Classify changes.** Separate intended improvements from regressions, uncertain differences, newly blocked work, newly auto-approved work, and missing-data failures.
9. **Summarize impact.** Produce counts and examples by risk level, agent, policy clause, customer/domain segment, and decision type.
10. **Require governance action.** Human reviewers approve activation, request revision, add fixtures, or reject the proposed change.

## Nondeterminism handling

LLM-based replay is often nondeterministic. Design for this explicitly:

- Store model provider, model name, model version if available, parameters, tool registry version, and compiled context.
- Use deterministic temperature/settings where supported for regression suites.
- Run multiple trials for high-risk or unstable scenarios and report variance.
- Treat semantic equivalence separately from exact text equality.
- Use structured output schemas and evaluator checks for expected behavior.
- Flag unstable scenarios when repeated runs change disposition, policy trigger, or action recommendation.
- Do not present a single LLM replay as proof that a policy change is safe.

```yaml
nondeterminism_report:
  scenario_id: string
  trial_count: number
  stable_disposition: boolean
  disposition_distribution:
    auto: number
    review: number
    approval: number
    escalate: number
    denied: number
    blocked: number
  recommendation_variance_summary: string
  policy_trigger_variance: string[]
  requires_human_review: boolean
```

## Diff categories

Classify each replay result with one primary diff category:

- `unchanged`: materially same recommendation and disposition.
- `intended_change`: matches the purpose of the proposed update.
- `safer_routing`: moves from auto/review to approval/escalate/denied for valid reasons.
- `over_blocking`: unnecessarily moves routine work to review/approval/escalate/denied.
- `unsafe_autonomy`: moves human-reviewed or blocked work to auto without sufficient policy support.
- `policy_conflict`: proposed rules produce contradictory clauses or thresholds.
- `evidence_gap`: replay cannot decide because required evidence is absent or stale.
- `tool_fixture_gap`: missing or inadequate mocked tool response.
- `nondeterministic`: repeated runs materially disagree.
- `not_replayable`: required provenance or snapshots are missing.

## Replay result output template

Use this template when producing a replay/simulation spec or result.

```yaml
replay_simulation_result:
  id: string
  mode: historical_replay | fixture_replay | prospective_simulation | policy_impact_analysis | agent_behavior_eval
  status: queued | running | completed | completed_with_warnings | failed | canceled
  proposed_change:
    change_type: prompt | skill | policy | clause | rule | guardrail | permission | approval_gate | threshold | agent_definition | model_config
    object_id: string
    from_version_id: string | null
    to_version_id: string
    summary: string
  sandbox_contract_id: string
  scenario_set:
    scenario_set_id: string
    scenario_count: number
    source: historical | fixture | mixed | draft
  execution_context:
    model_provider: string | null
    model_name: string | null
    model_parameters: object
    mock_tool_registry_version_id: string
    deterministic_settings_used: boolean
    trial_count_per_scenario: number
  summary:
    unchanged_count: number
    intended_change_count: number
    safer_routing_count: number
    over_blocking_count: number
    unsafe_autonomy_count: number
    policy_conflict_count: number
    evidence_gap_count: number
    tool_fixture_gap_count: number
    nondeterministic_count: number
    not_replayable_count: number
  scenario_results:
    - scenario_id: string
      source_object_id: string | null
      original_trace_id: string | null
      baseline:
        disposition: auto | review | approval | escalate | fyi | denied | blocked | unknown
        recommendation_summary: string | null
        policy_clause_ids: string[]
        confidence: number | null
        risk: number | null
        action_taken: string | null
      replayed:
        disposition: auto | review | approval | escalate | fyi | denied | blocked
        recommendation_summary: string | null
        policy_clause_ids: string[]
        confidence: number | null
        risk: number | null
        intended_action: string | null
      diff:
        category: unchanged | intended_change | safer_routing | over_blocking | unsafe_autonomy | policy_conflict | evidence_gap | tool_fixture_gap | nondeterministic | not_replayable
        explanation: string
        materiality: low | medium | high | critical
        requires_human_review: boolean
      side_effect_isolation:
        production_writes_attempted: boolean
        external_side_effects_attempted: boolean
        blocked_operation_count: number
      artifacts:
        replay_trace_id: string
        evaluator_report_id: string | null
        mock_tool_invocation_ids: string[]
  recommendation:
    activation_readiness: ready | ready_with_cautions | revise_before_activation | reject_change
    required_human_actions: string[]
    suggested_fixture_additions: string[]
    rollback_plan_required: boolean
  created_at: datetime
  completed_at: datetime | null
```

## Regression scenario set template

```yaml
replay_regression_suite:
  id: string
  name: string
  owner_role: policy_owner | auditor | product | engineering | compliance
  purpose: string
  required_before_changes:
    - prompt
    - skill
    - policy
    - threshold
  scenarios:
    - id: string
      title: string
      source: historical_trace | synthetic_fixture | incident | compliance_case | golden_path
      risk_level: low | medium | high | critical
      decision_type: string
      required_behavior: string
      forbidden_behavior: string
      expected_disposition: auto | review | approval | escalate | denied | blocked
      fixture_version_id: string
      mock_tool_response_ids: string[]
      assertions:
        - string
  pass_fail_policy:
    max_unsafe_autonomy: number
    max_policy_conflicts: number
    max_nondeterministic_high_risk: number
    human_review_required_for: string[]
```

## UI and governance expectations

Replay/simulation results should appear in the Agent Skills / Policy / Governance / Learning Center and link to audit/work traces. The UI should show:

- proposed change summary and diff;
- scenario coverage and known gaps;
- changed disposition counts;
- highest-risk changed decisions;
- unsafe autonomy and over-blocking examples;
- nondeterminism warnings;
- side-effect isolation verification;
- drill-down from each scenario to baseline trace, replay trace, policy clauses, evidence, mocked tool calls, and evaluator outputs;
- actions: approve activation, request revision, add fixture, rerun, reject, or require broader review.

Human approval is required before activating high-impact changes that replay evaluated. Replay jobs themselves should create audit events recording who ran them, what versions were tested, and what result informed the governance decision.

## Acceptance checks

A replay/simulation design is complete only if:

- [ ] Historical replay, fixture replay, and prospective simulation modes are distinguished.
- [ ] Replayable inputs include prompt, skill, policy, threshold, compiled context, evidence, data snapshots, and original result metadata.
- [ ] All external tools are mocked or preview-only, and production writes are mechanically blocked.
- [ ] Side-effect isolation is enforced by code/infrastructure, not prompt text.
- [ ] Replay results include structured diffs and changed disposition counts.
- [ ] Nondeterminism is measured or explicitly scoped out for deterministic-only scenarios.
- [ ] Results are labeled advisory unless deterministic equivalence is proven.
- [ ] Regression suites include positive, negative, edge, incident, and high-risk scenarios.
- [ ] Governance UI supports human review before activating high-impact changes.
- [ ] Replay activity emits audit events and links to source traces, proposed versions, mocked tool calls, and approval decisions.
