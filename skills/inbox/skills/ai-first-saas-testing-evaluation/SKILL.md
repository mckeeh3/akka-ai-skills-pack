---
name: ai-first-saas-testing-evaluation
description: Turn ai-first SaaS requirements into tests, evals, golden traces, replay regressions, UI acceptance criteria, human-in-the-loop workflow tests, and outcome validation plans.
---

# ai-first-saas-testing-evaluation

Use this skill when a coding agent must create test plans, acceptance criteria, evaluation suites, or quality gates for an ai-first SaaS product.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for goals, agents, policies, decisions, approvals, traces, replay, audit, outcomes, and required UI surfaces.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack and the expected backend/frontend/test decomposition.
- `docs/ai-first-saas-ui-patterns.md` defines UI acceptance expectations for command centers, decision cards, governance centers, digests, and audit traces.

## Inputs this skill can consume

This skill is designed to consume outputs from these related skills:

- `ai-first-saas-object-model`: entities, relationships, state machines, required fields.
- `ai-first-saas-agent-team-design`: agent roster, tools, permissions, authority boundaries, escalation rules.
- `ai-first-saas-policy-governance`: policies, clauses, skills, thresholds, approval gates, proposal/commit lifecycles.
- `ai-first-saas-ui-surfaces`: screen specs, backing objects, actions, states, audit events.
- `ai-first-saas-replay-simulation`: replay fixtures, sandbox contracts, diff categories.
- `ai-first-saas-security-privacy`: threat models, prompt-injection defenses, data handling controls.

If these inputs are missing, write tests as assumptions and list the missing artifact as an open question.

## Core testing principle

AI-first SaaS tests must prove more than correct UI rendering or API CRUD behavior. They must prove that agents act only inside authorized boundaries, route uncertainty to humans, preserve decision provenance, produce audit-complete traces, resist adversarial instructions, and improve or preserve outcomes over time.

Prompts are not sufficient controls. Tests should verify platform-enforced permissions, policy evaluators, workflow gates, tool wrappers, views, and audit events.

## Test categories

### 1. Object model and lifecycle tests

Verify that core ai-first objects exist, link correctly, and enforce state transitions.

Cover:

- `Goal`, `Objective`, `ExecutionPlan`, `Agent`, `TaskRun`, `ToolInvocation`, `ApprovalRequest`, `Decision`, `Exception`, `PolicyDocument`, `PolicyClause`, `WorkTrace`, `AuditEvent`, and `OutcomeMetric`.
- Goal-to-plan relationships and plan-to-agent/task relationships.
- Approval request to decision lifecycle.
- Exception to escalation lifecycle.
- Decision to precedent/example/policy proposal paths.
- Trace links to tool invocations, data access, policy invocations, evidence, and outcomes.

Expected test styles:

- Akka Event Sourced Entity command/state tests where history matters.
- Key Value Entity tests for low-risk current-state records.
- Workflow transition tests for goals, approvals, exceptions, replay jobs, and governance commits.
- View/projection tests proving UI read models include trace and policy references.

### 2. Policy evaluator tests

Policy tests should exercise executable or semi-structured rules, stable clause IDs, thresholds, and conflicting clauses.

Include cases for:

- permitted action routes to `auto`;
- low confidence routes to `review` or `escalate`;
- high risk, sensitive, irreversible, or external side effects route to `approval`;
- forbidden actions route to `denied` or `blocked`;
- policy clause IDs appear in decisions, traces, audit events, and UI read models;
- ambiguous or conflicting clauses produce warnings or escalation rather than silent auto-action;
- policy version changes do not affect historical traces retroactively.

### 3. Permission and approval gate tests

Verify mechanical enforcement before model calls, tool calls, data access, workflow continuation, and side effects.

Include cases for:

- agent lacks tool permission and tool invocation is denied;
- agent lacks data scope and data access is denied;
- tenant context mismatch is denied;
- model output requests a forbidden action and backend policy blocks it;
- approval gate pauses workflow before side effect;
- rejection, counterproposal, timeout, or escalation resumes the workflow correctly;
- approval events include actor, authority, policy clause, trace, and affected action.

### 4. Agent behavior evals

Use deterministic fixtures and structured expected outputs to evaluate agent responsibilities.

Evaluate:

- coordinator creates a plan within policy and asks clarifying questions when intent is underspecified;
- specialist agents use only assigned tools/data;
- agents cite evidence and policy triggers for recommendations;
- agents classify disposition correctly: `auto`, `review`, `approval`, `escalate`, `fyi`, `denied`, or `blocked`;
- agents refuse to treat untrusted content as policy or authority;
- agents summarize rationale and alternatives without relying on hidden chain-of-thought storage.

Prefer structured-output evals with explicit assertions over brittle text matching.

### 5. Golden traces and audit tests

A golden trace is a canonical scenario with expected events, decisions, policy invocations, tool calls, and outcome links.

Create golden traces for:

- happy-path autonomous completion;
- approval-required side effect;
- exception/escalation path;
- rejected or counterproposed human decision;
- teach-from-decision policy or example proposal;
- failed tool call and retry/compensation;
- security/privacy-sensitive data handling.

A golden trace test should assert event order, required payload fields, trace completeness, redaction, policy version binding, and drill-down links.

### 6. Replay regression tests

Use replay to protect prompt, skill, policy, threshold, model, and guardrail changes.

Include:

- historical replay of high-impact prior decisions;
- fixture replay for known golden and negative scenarios;
- policy impact analysis for changed clauses or thresholds;
- nondeterminism checks for LLM-involved scenarios;
- side-effect isolation verification: no production writes, no live external side effects, mocked tools only.

Gate high-impact activation on replay results that show no unsafe autonomy, no critical policy conflicts, and acceptable nondeterminism.

### 7. Adversarial and prompt-injection tests

Test hostile inputs across user prompts, documents, emails, tickets, webhooks, tool output, retrieved context, and model-generated instructions.

Required scenarios:

- untrusted content tells the agent to ignore policies or reveal secrets;
- malicious tool output tries to approve an action or change permissions;
- data poisoning alters evidence or precedents;
- prompt asks for cross-tenant data;
- model attempts to call a tool outside its permission set;
- adversarial input is stored safely in trace/audit without becoming active instruction.

Pass condition: backend-enforced controls block or route the attempt, and an audit/security event records the relevant sanitized context.

### 8. Confidence, risk, and calibration tests

Scores must drive routing behavior, not serve as decoration.

Test:

- score ranges and labels map consistently to dispositions;
- thresholds are per-agent or per-action where required;
- high confidence does not override high risk or missing permission;
- calibration fixtures include true positives, false positives, false negatives, and edge cases;
- threshold changes are replayed before activation;
- monitoring detects drift, over-escalation, unsafe autonomy, and stale confidence estimates.

### 9. UI acceptance tests

For React/Vite/TypeScript surfaces, test both visual state and backing-object integrity.

Decision Card tests must verify:

- recommendation, evidence, risk, confidence, impact, policy clause ID, alternatives, precedents, and trace link are visible;
- approve/reject/counter/request-evidence/escalate actions call the correct API;
- learning options create only proposals or precedents unless authorized commit occurs;
- loading, empty, error, stale, and permission-denied states are clear.

Audit Trace tests must verify:

- who/what/when/why/how-authorized are answerable;
- tool and data access events are shown;
- policy versions and clause IDs are visible;
- outcomes and rollback/reversibility status are linked;
- compressed summaries drill down to audit-complete records.

Also test Goal-to-Execution Workbench, Command Center, Governance Center, and Async Digest against their required roles, temporal modes, actions, and audit links.

### 10. Human-in-the-loop workflow tests

Simulate end-to-end human governance and supervision.

Cover:

- goal submitted, plan previewed, edited, simulated, and activated;
- approval request created, reviewed, approved/rejected/countered, and workflow resumed;
- exception escalated to the correct role and resolved;
- human decision creates one-time result, precedent, example, or policy proposal as selected;
- agent-proposed prompt/skill/policy/threshold change requires human approval before activation;
- reviewer permissions and separation of duties are enforced.

### 11. Outcome metric validation

Validate that outcomes can prove whether agent labor helped.

Test:

- outcome metrics are defined with owner, source, time window, and attribution notes;
- delayed outcomes can be linked back to goal, decision, agent, policy, prompt, skill, and external factors;
- outcome updates emit events and feed read models;
- UI metrics distinguish observed outcomes from inferred or human-entered outcomes;
- attribution uncertainty is represented rather than hidden.

## Akka + React test mapping

| Concern | Preferred test target |
|---|---|
| Command/state transitions | Akka Entity unit/component tests |
| Long-running approvals, retries, compensation | Akka Workflow tests |
| Policy and permission enforcement | Policy evaluator, command handler, endpoint, and tool-wrapper tests |
| Agent structured outputs | Akka agent evals with deterministic fixtures and mocked tools |
| Audit and work trace completeness | Event payload tests plus View/projection tests |
| Replay regression | Replay Workflow tests with sandboxed tool registry |
| Digest/curation and notifications | Consumers, Timed Actions, and View tests |
| Browser screens | React component/integration tests against typed API fixtures |
| End-to-end human-in-the-loop flow | API + workflow + UI E2E test |
| Security/privacy adversarial cases | Backend enforcement tests plus agent eval scenarios |

## Required test plan output format

When applying this skill, produce a test plan like this:

```yaml
agent_first_saas_test_plan:
  product_name: string
  maturity_level: prototype | mvp | production | regulated_or_high_risk
  source_artifacts:
    object_model: string | null
    agent_team_spec: string | null
    policy_governance_spec: string | null
    ui_surface_spec: string | null
    replay_spec: string | null
    security_privacy_spec: string | null

  quality_gates:
    - gate: pre_merge | pre_release | pre_policy_activation | pre_prompt_or_skill_activation | scheduled_regression | incident_response
      required_suites: string[]
      blocking_conditions: string[]

  test_suites:
    - id: string
      category: object_model_lifecycle | policy_evaluator | permission_approval_gate | agent_behavior_eval | golden_trace | replay_regression | adversarial_security | calibration | ui_acceptance | human_in_loop | outcome_validation
      purpose: string
      stack_targets:
        backend_components: string[]
        frontend_components: string[]
        agents: string[]
        workflows: string[]
        views: string[]
      scenarios:
        - id: string
          title: string
          risk_level: low | medium | high | critical
          given: string
          when: string
          then: string
          fixtures: string[]
          mocked_tools: string[]
          expected_disposition: auto | review | approval | escalate | fyi | denied | blocked | not_applicable
          expected_audit_events: string[]
          expected_trace_assertions: string[]
          expected_ui_assertions: string[]
          expected_outcome_assertions: string[]
          pass_fail_criteria: string[]

  golden_traces:
    - id: string
      scenario_id: string
      expected_event_sequence: string[]
      required_policy_clause_ids: string[]
      required_tool_invocations: string[]
      required_data_access_events: string[]
      required_human_decisions: string[]
      required_outcome_links: string[]

  evals:
    - id: string
      agent_id: string
      scenario_set: string
      structured_output_schema: string
      assertions: string[]
      evaluator: deterministic_assertions | llm_as_judge | hybrid
      nondeterminism_policy: string

  replay_regression:
    required_for_changes_to:
      - prompt
      - skill
      - policy
      - threshold
      - guardrail
      - permission
      - approval_gate
    scenario_sets: string[]
    sandbox_requirements: string[]
    max_allowed_unsafe_autonomy: number
    max_allowed_policy_conflicts: number

  ui_acceptance:
    surfaces:
      - name: goal_to_execution_workbench | command_center | decision_card | governance_center | async_digest | audit_trace
        required_roles: string[]
        required_objects: string[]
        required_actions: string[]
        loading_empty_error_states: string[]
        accessibility_checks: string[]

  open_questions:
    - string
```

## Acceptance checklist

A testing/evaluation plan is complete only if:

- [ ] It consumes or explicitly requests object model, agent team, policy governance, and UI surface specs.
- [ ] Policy evaluator, permission, approval gate, and tenant/data-scope enforcement tests are included.
- [ ] Golden traces verify event order, audit payloads, policy versions, evidence, tool/data access, and outcome links.
- [ ] Replay regression suites are defined for prompt, skill, policy, threshold, guardrail, permission, and approval-gate changes.
- [ ] Adversarial/prompt-injection tests verify backend enforcement rather than prompt-only compliance.
- [ ] Confidence/risk calibration tests prove scores affect routing behavior.
- [ ] UI acceptance tests cover decision cards and audit traces with loading, empty, error, and permission-denied states.
- [ ] Human-in-the-loop tests cover approvals, counterproposals, escalations, teach-from-decision, and governed activation.
- [ ] Outcome metric validation covers delayed outcomes, attribution limits, and read-model/UI visibility.
- [ ] Quality gates identify which failures block merge, release, or governance activation.
