---
name: ai-first-saas-agent-team-design
description: Design bounded agent teams for ai-first SaaS products, including coordinator/specialist roles, system prompts, skills, tools, data scopes, authority boundaries, thresholds, escalation criteria, audit requirements, and permission matrices.
---

# ai-first-saas-agent-team-design

Use this skill when a coding agent must design the agent team for an ai-first SaaS product from a PRD, intake assessment, object model, maturity recommendation, or implementation plan.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical conceptual contract and vocabulary for ai-first SaaS, agent definitions, human roles, authority boundaries, and audit expectations.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack. Use it when mapping agents to Akka agents, workflows, tools, endpoints, and frontend surfaces.

## Core rule

Agents are bounded operational actors, not an omnipotent app brain. Design each agent with a clear purpose, system prompt, assigned skills, mechanically enforced tool permissions, data scopes, action authority, confidence/risk thresholds, escalation criteria, and audit requirements.

System prompts and skills instruct behavior. Platform permissions, authorization checks, data scopes, workflow gates, and approval gates enforce behavior. Never treat prompt text as a security boundary.

## Procedure

### 1. Identify delegated work and risk

From the source material, list the work agents will perform:

- planning goals into execution plans;
- reading, validating, enriching, or reconciling data;
- executing routine operations;
- drafting or sending communications;
- applying policy and thresholds;
- detecting exceptions or anomalies;
- preparing recommendations or decision cards;
- curating digests and insights;
- producing audit or accountability reports.

For each work item, capture impact, reversibility, data sensitivity, required evidence, external side effects, and whether it is suitable for `auto`, `review`, `approval`, or `escalate` disposition.

### 2. Choose one agent vs multiple agents

Use **one agent or a very small team** when all of these are true:

- prototype or low-risk MVP;
- narrow domain and few action types;
- no sensitive side effects or only mocked tools;
- humans review most outputs before execution;
- simple data scopes and little need for least-privilege separation.

Use **multiple specialized agents** when any of these are true:

- production, regulated, high-risk, or customer-impacting operations;
- different work requires different tools, data scopes, or permissions;
- one agent would need broad access to unrelated systems;
- auditability requires clear responsibility for separate steps;
- policy application, exception handling, communication, and execution need different review thresholds;
- the product needs safer prompt/skill evolution by role.

Avoid one omnipotent agent with all tools and all data unless the product is intentionally small and low risk.

### 3. Define the coordinator/orchestrator layer

Most ai-first SaaS products need a coordinator/orchestration responsibility. It may be an Akka Workflow, a coordinator agent, deterministic routing code, or a combination.

Coordinator responsibilities:

- convert durable goals into execution plans;
- select participating agents and assign tasks;
- route work according to policy, risk, confidence, and permissions;
- maintain plan state and pause/resume around approval gates;
- detect partial failures and retry or reassign work;
- consolidate progress into the command center and digest;
- ensure each agent run references the correct prompt, skill, policy, and permission versions;
- create or trigger audit events for meaningful transitions.

Coordinator non-goals:

- bypass specialist permission boundaries;
- perform every task personally;
- approve its own high-risk deviations;
- silently change active prompts, policies, skills, thresholds, or permissions.

### 4. Select specialist agent categories

Choose only the categories needed for the product and maturity level:

- `CoordinatorAgent`: plans, delegates, tracks progress, and orchestrates long-running goals.
- `DataAgent`: reads, validates, reconciles, extracts, enriches, and summarizes data.
- `OperationsAgent`: performs routine domain workflow steps under policy.
- `ExceptionAgent`: detects anomalies, ambiguity, blocked actions, and policy/authority boundary issues.
- `PolicyAgent`: evaluates clauses, prepares policy citations, and proposes policy/skill improvements for human approval.
- `CommunicationAgent`: drafts or sends customer/team/vendor communications within approval boundaries.
- `InsightAgent`: detects trends, risks, opportunities, and outcome patterns.
- `AuditAgent`: prepares trace summaries, provenance reports, and accountability views.
- `DomainSpecificAgent`: handles domain-specialized work not covered above.

For each category, decide whether it is an LLM agent, deterministic service, Akka Workflow step, Consumer, Timed Action, or hybrid component.

### 5. Write per-agent operating boundaries

For every agent, specify:

- purpose and responsibilities;
- non-goals and forbidden behaviors;
- system prompt text or prompt outline;
- assigned skills and versions or binding policy;
- tools allowed and forbidden tools;
- data scopes allowed and forbidden data;
- auto-allowed actions;
- actions requiring human approval;
- actions that must always escalate;
- confidence, risk, impact, and stakes thresholds;
- required evidence before recommendation/action;
- audit events and trace fields to produce;
- failure handling, retry, timeout, and handoff behavior.

### 6. Design authority and routing rules

Use disposition tags consistently:

- `auto`: agent may execute immediately within policy and permission.
- `review`: agent may proceed but surfaces work for human review.
- `approval`: agent must block until a human approves.
- `escalate`: agent cannot resolve within authority; route to an exception handler or higher-authority role.
- `fyi`: informational output only.

Routing should be driven by explicit policy, action type, data sensitivity, confidence, risk, impact, reversibility, and tenant/customer constraints. Scores are not decoration: they must drive or explain routing behavior.

### 7. Map platform enforcement

For each agent, identify the enforcement mechanism:

- API authorization and tenant checks for data access;
- tool registry allowlists and deny-by-default execution;
- workflow approval gates before side effects;
- policy evaluators before action execution;
- capability tokens or scoped service clients;
- read/write separation for sensitive data;
- audit events for allowed, denied, failed, and approval-gated attempts.

Prompts may say an agent should not call a tool, but the tool layer must make the forbidden call impossible or safely denied.

### 8. Produce roster and permission matrix

Finish with the required output templates below. Keep the roster concrete enough for implementation planning, testing, UI agent roster screens, and audit design.

## Required output format

```yaml
agent_team_design:
  product_name: string
  maturity_level: prototype | mvp | production | regulated_or_high_risk
  team_strategy:
    pattern: single_agent | coordinator_plus_specialists | deterministic_orchestrator_plus_agents | hybrid
    rationale: string
    why_not_more_agents: string
    why_not_fewer_agents: string

  coordinator_orchestration:
    component_type: akka_workflow | akka_agent | deterministic_service | mixed | not_needed
    responsibilities:
      - string
    state_owned:
      - goal | execution_plan | task_run | approval_gate | retry_state | digest_state | other
    approval_gate_behavior: string
    failure_handling: string
    audit_events:
      - string

  agents:
    - id: string
      name: string
      category: coordinator | data | operations | exception | policy | communication | insight | audit | domain_specific
      implementation_shape: akka_agent | workflow_step | consumer | timed_action | service | hybrid
      purpose: string
      responsibilities:
        - string
      non_goals:
        - string
      system_prompt:
        prompt_id: string
        version_id: string
        text_or_outline: string
      assigned_skills:
        - skill_id: string
          skill_version_id: string
          binding_mode: pinned | latest_approved
          purpose: string
      inputs:
        - string
      outputs:
        - string
      tools_allowed:
        - tool_id: string
          allowed_operations:
            - string
          enforcement: tool_registry | api_authz | scoped_client | workflow_gate | other
      tools_forbidden:
        - string
      data_scopes_allowed:
        - scope_id: string
          access: read | write | read_write
          tenant_boundary: string
      data_scopes_forbidden:
        - string
      actions_auto_allowed:
        - string
      actions_requiring_approval:
        - action: string
          approving_role: intent_author | supervisor | reviewer_approver | exception_handler | policy_owner_coach | auditor | outcome_owner
          required_evidence:
            - string
      actions_always_escalate:
        - string
      confidence_thresholds:
        auto_min: number
        review_min: number
        approval_below: number
        escalate_below: number
      risk_thresholds:
        auto_max: number
        review_max: number
        approval_above: number
        escalate_above: number
      impact_or_stakes_thresholds:
        auto_max: string
        approval_above: string
        escalate_above: string
      escalation_criteria:
        - string
      audit_requirements:
        - event_type: string
          when_emitted: string
          required_payload_fields:
            - string
      failure_handling:
        retries: string
        timeout: string
        fallback_agent_or_role: string
        compensation_or_rollback: string

  permission_matrix:
    - agent_id: string
      tool_or_action: string
      permission: allowed | denied | approval_required | escalate_only
      data_scope: string
      enforcing_component: string
      audit_event_on_attempt: string
      audit_event_on_denial: string
      notes: string

  handoff_matrix:
    - from_agent_id: string
      to_agent_id_or_human_role: string
      trigger: string
      payload_required:
        - string
      blocking: boolean
      audit_event: string

  shared_team_policies:
    - policy_or_clause_id: string
      applies_to_agents:
        - string
      routing_effect: auto | review | approval | escalate | fyi
      notes: string

  open_questions:
    - string
```

## Concrete agent spec template

Use this smaller template when specifying a single agent:

```yaml
agent_spec:
  id: string
  name: string
  category: coordinator | data | operations | exception | policy | communication | insight | audit | domain_specific
  purpose: string
  system_prompt:
    prompt_id: string
    version_id: string
    text: string
  assigned_skills:
    - skill_id: string
      skill_version_id: string
      binding_mode: pinned | latest_approved
  inputs:
    - string
  outputs:
    - string
  tools_allowed:
    - string
  data_scopes_allowed:
    - string
  actions_auto_allowed:
    - string
  actions_requiring_approval:
    - string
  confidence_thresholds:
    auto: number
    review: number
    escalate: number
  risk_thresholds:
    auto: number
    review: number
    escalate: number
  escalation_criteria:
    - string
  audit_requirements:
    - string
  failure_handling:
    - string
```

## Review checklist

Before finalizing an agent team design, verify:

- [ ] Every delegated work item has an accountable agent or deterministic component.
- [ ] The design justifies one agent vs multiple specialized agents.
- [ ] A coordinator/orchestration responsibility is defined or intentionally omitted.
- [ ] Every agent has a versioned system prompt separate from assigned skills.
- [ ] Every agent has bounded tools, data scopes, and authority.
- [ ] Platform enforcement exists for permissions, not just prompt instructions.
- [ ] Approval, review, escalation, and auto-routing rules are explicit.
- [ ] Confidence/risk/impact thresholds affect routing behavior.
- [ ] Human governance roles are named for approval and escalation paths.
- [ ] Audit events cover agent runs, tool attempts, data access, denials, approvals, exceptions, and handoffs.
- [ ] Failure handling covers retries, timeouts, partial failure, fallback, and rollback/compensation where relevant.
- [ ] The resulting roster can power an Agent Roster UI, permission matrix, tests, and audit traces.
