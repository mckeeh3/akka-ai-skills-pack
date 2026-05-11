---
name: ai-first-saas-object-model
description: Convert ai-first SaaS requirements into an ERD-style domain and substrate object model with relationships, lifecycles, minimum implementation fields, and ambiguity-resolving prompts for goals, plans, agents, approvals, decisions, exceptions, traces, policies, audit, and outcomes.
---

# ai-first-saas-object-model

Use this skill when a coding agent must turn PRDs, intake notes, product specs, or feature ideas into an implementable object model for an ai-first SaaS product.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical conceptual contract and vocabulary.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript architecture when object model choices affect implementation planning.

## Core rule

Model both the product domain and the agentic substrate. Domain entities represent the customer's business objects. AI-first substrate entities represent delegated work, authority, decisions, policy, traces, audit, learning, and outcomes. Do not hide agent activity in logs, chat transcripts, or generic JSON blobs when the product needs durable supervision, approvals, audit, replay, or learning.

## Procedure

1. **Extract domain entities.** Identify customer/business objects, ownership, lifecycle, and key relationships.
2. **Extract delegated work.** Name what agents plan, decide, execute, review, escalate, or learn from.
3. **Add substrate entities.** Add the minimum ai-first objects needed for durable goals, plans, agent runs, approvals, decisions, exceptions, policies, traces, audit, and outcomes.
4. **Resolve ambiguous relationships.** Explicitly answer the relationship prompts below instead of leaving associations implicit.
5. **Define lifecycles.** For each core object, define states, allowed transitions, transition triggers, required actor, and audit event.
6. **Choose persistence shape.** Mark objects as event-sourced, current-state/KV, view/projection, workflow state, or log-derived only. Audit-critical lifecycle objects usually deserve event history.
7. **Produce an ERD-style output.** Include entities, fields, relationships, cardinalities, lifecycle notes, and open modeling questions.

## Entity categories

### Domain entities

Examples: `Customer`, `Account`, `Invoice`, `Case`, `Ticket`, `Candidate`, `Incident`, `Contract`, `Document`, `Vendor`, `Order`.

For each domain entity, capture:

- purpose and owner;
- source of truth;
- tenant boundary;
- key fields;
- lifecycle states;
- agent-readable evidence fields;
- human-editable fields;
- relationships to goals, plans, decisions, traces, and outcomes.

### AI-first substrate entities

Use these unless a maturity assessment explicitly allows a smaller subset.

- `Goal`: durable human intent and desired outcome.
- `Objective`: measurable target within a goal.
- `ExecutionPlan`: agent-generated plan for achieving a goal.
- `Agent`: bounded operational actor definition.
- `AgentRun`: one execution instance of an agent under a compiled context.
- `TaskRun`: one delegated unit of work within a plan or workflow.
- `ApprovalRequest`: blocking or non-blocking request for human authorization.
- `Decision`: human or system judgment with structured rationale and outcome.
- `Exception`: anomaly, blocked condition, uncertainty, or authority boundary breach.
- `Escalation`: routed handoff to a human role or higher-authority process.
- `PolicyDocument`: versioned governance document.
- `PolicyClause`: stable addressable policy unit.
- `PolicyInvocation`: record that a clause/rule was evaluated or applied.
- `WorkTrace`: queryable provenance for a goal, plan, run, task, or decision.
- `ToolInvocation`: attempted or completed tool call with inputs/outputs metadata.
- `DataAccessEvent`: record of data read/written by an agent or tool.
- `AuditEvent`: append-oriented meaningful event created as work progresses.
- `OutcomeMetric`: definition and observation of business result.
- `OutcomeLink`: attribution link from outcomes to goals, plans, decisions, agents, policies, prompts, or external factors.

## Minimum fields by core entity

Use these as implementation-planning minimums; add domain-specific fields as needed.

```yaml
minimum_fields:
  Goal:
    - id
    - tenant_id
    - title
    - objective_ids
    - owner_user_id
    - status: draft | proposed | active | paused | completed | canceled | failed
    - success_criteria
    - constraints
    - priority
    - risk_level
    - created_at
    - updated_at
  Objective:
    - id
    - goal_id
    - description
    - target_metric
    - target_value
    - due_at
    - status
  ExecutionPlan:
    - id
    - goal_id
    - version
    - status: draft | proposed | approved | active | paused | completed | superseded | canceled | failed
    - plan_steps
    - assigned_agent_ids
    - required_tool_ids
    - required_data_scopes
    - approval_gate_ids
    - risk_assessment
    - created_by_agent_id
    - approved_by_user_id
  Agent:
    - id
    - name
    - category
    - purpose
    - system_prompt_version_id
    - assigned_skill_version_ids
    - tools_allowed
    - data_scopes_allowed
    - auto_action_permissions
    - approval_required_actions
    - escalation_criteria
    - status
  AgentRun:
    - id
    - agent_id
    - goal_id
    - execution_plan_id
    - task_run_id
    - compiled_context_id
    - status: queued | running | waiting_for_tool | waiting_for_approval | completed | failed | canceled
    - started_at
    - ended_at
  TaskRun:
    - id
    - execution_plan_id
    - parent_task_run_id
    - assigned_agent_id
    - status: pending | running | blocked | waiting_for_approval | completed | failed | skipped
    - input_refs
    - output_refs
    - disposition: auto | review | approval | escalate | fyi
  ApprovalRequest:
    - id
    - goal_id
    - execution_plan_id
    - task_run_id
    - requested_by_agent_run_id
    - decision_id
    - status: requested | assigned | approved | rejected | countered | expired | canceled
    - requested_action
    - policy_clause_ids
    - risk_score
    - confidence_score
    - due_at
  Decision:
    - id
    - decision_type
    - actor_type: human | agent | system
    - actor_id
    - approval_request_id
    - selected_action
    - rationale_summary
    - alternatives_considered
    - evidence_item_ids
    - policy_clause_ids
    - creates_precedent
    - outcome_link_ids
  Exception:
    - id
    - goal_id
    - execution_plan_id
    - task_run_id
    - agent_run_id
    - type
    - severity
    - status: open | triaged | escalated | resolved | dismissed
    - blocked_action
    - boundary_or_policy_trigger
    - escalation_id
  PolicyDocument:
    - id
    - name
    - version_id
    - status: draft | approved | active | deprecated | archived
    - owner_role
    - effective_at
  PolicyClause:
    - id
    - policy_document_id
    - stable_clause_id
    - version_id
    - type: structured_rule | prose_guidance | threshold | permission | escalation | example | data_tool_boundary
    - text
    - executable_hint
  WorkTrace:
    - id
    - goal_id
    - execution_plan_id
    - task_run_id
    - agent_run_id
    - decision_id
    - summary
    - evidence_item_ids
    - tool_invocation_ids
    - data_access_event_ids
    - policy_invocation_ids
    - audit_event_ids
  AuditEvent:
    - id
    - tenant_id
    - event_type
    - subject_type
    - subject_id
    - actor_type
    - actor_id
    - occurred_at
    - payload
    - trace_id
  OutcomeMetric:
    - id
    - name
    - definition
    - measurement_method
    - window
    - owner_role
  OutcomeLink:
    - id
    - outcome_metric_id
    - observed_value
    - observed_at
    - linked_subject_type
    - linked_subject_id
    - attribution_strength: strong | medium | weak | unknown
    - attribution_notes
```

## Relationship guidance and ambiguity resolution

Answer these prompts in every object model.

### Goal to execution plans

- Can a `Goal` have many plan versions or concurrent plans?
- Which plan is active, approved, superseded, or canceled?
- Does plan activation require human approval?
- Are outcomes linked to the goal, the active plan, or both?

Recommended relationship: `Goal 1 -> many ExecutionPlan`; at most one active plan unless the product explicitly supports parallel strategies.

### Execution plan to agent runs and tasks

- Is the plan a sequence, graph, checklist, workflow, or agent-generated natural-language plan?
- Which `TaskRun`s are deterministic workflow steps versus LLM agent work?
- Can multiple agents work on one task, or should each task have one accountable agent?
- What happens when a task is retried or reassigned?

Recommended relationships: `ExecutionPlan 1 -> many TaskRun`; `TaskRun 0..many -> AgentRun`; `AgentRun many -> 1 Agent`.

### Approval request to decision

- Does every approval request produce exactly one terminal decision?
- Are counterproposals modeled as decisions, new approval requests, or plan revisions?
- Can approvals expire or be canceled without a decision?

Recommended relationship: `ApprovalRequest 0..1 -> Decision` for terminal human judgment; status can expire/cancel with audit events even without a decision.

### Exception to escalation

- Is the exception a detected condition, while escalation is the routing/handoff?
- Can one exception produce multiple escalations over time?
- Does resolving an escalation also resolve the exception?

Recommended relationship: `Exception 0..many -> Escalation`; exception remains the durable problem record, escalation records routing attempts and ownership transfers.

### Decision to precedent, learned rule, or policy proposal

- Is this decision one-time only, a reusable precedent, a training example, or a proposed policy change?
- Who approves learned behavior before activation?
- Does the decision create a new `PolicyProposal`, `ReferenceExample`, or `LearnedRule`?

Recommended relationship: `Decision 0..many -> Precedent | ReferenceExample | LearnedRule | PolicyProposal`; activation requires a human-approved governance commit.

### Trace to tool invocations and data access

- Which tool calls and data reads/writes must be shown in the trace?
- Are failed, denied, and skipped tool calls recorded?
- What data access details require redaction in UI while remaining auditable?

Recommended relationships: `WorkTrace 1 -> many ToolInvocation`; `WorkTrace 1 -> many DataAccessEvent`; `ToolInvocation 0..many -> DataAccessEvent` when tools read/write data internally.

## Lifecycle/state-machine prompts

For each core entity, define:

```yaml
state_machine_prompt:
  entity: string
  states: string[]
  initial_state: string
  terminal_states: string[]
  transitions:
    - from: string
      to: string
      trigger: string
      actor: human | agent | system | workflow | timer
      authorization_required: string
      emits_audit_event: string
      side_effects: string[]
  invariants:
    - string
```

Minimum lifecycle checks:

- `Goal`: draft/proposed/active/paused/completed/canceled/failed; cannot be active without accepted objective and owner.
- `ExecutionPlan`: draft/proposed/approved/active/paused/completed/superseded/canceled/failed; cannot activate without required approvals and permissions.
- `AgentRun`: queued/running/waiting/completed/failed/canceled; must reference the compiled context used.
- `TaskRun`: pending/running/blocked/waiting_for_approval/completed/failed/skipped; retries and reassignment must preserve prior attempts.
- `ApprovalRequest`: requested/assigned/approved/rejected/countered/expired/canceled; terminal decision must emit an audit event.
- `Decision`: proposed/made/applied/superseded/reversed; must preserve structured rationale and evidence references.
- `Exception`: open/triaged/escalated/resolved/dismissed; resolution must state who/what resolved it and why.
- `PolicyDocument` and `PolicyClause`: draft/approved/active/deprecated/archived; active policy changes require governed commit provenance.
- `WorkTrace`: open/finalized/redacted/exported; finalized trace should be immutable except for governed redaction annotations.
- `OutcomeMetric`: defined/active/observed/retired; observations should link to responsible artifacts with attribution limits.

## Stack mapping prompts

When turning this object model into implementation tasks, answer:

- Which lifecycle objects need Akka Event Sourced Entities because state history and auditability matter?
- Which simple reference/config objects can be Akka Key Value Entities initially?
- Which long-running processes need Akka Workflows: goal execution, approval pause/resume, exception escalation, policy commit, replay, or outcome collection?
- Which Views are required for command center activity, approval queues, trace lookup, policy history, agent roster, and outcome metrics?
- Which Consumers create projections, notifications, digests, outcome links, or policy-invocation analytics from events?
- Which Timed Actions handle approval deadlines, retries, reminders, scheduled digests, or delayed outcome observation?
- Which HTTP/gRPC/MCP endpoints expose object commands, query APIs, streaming updates, or agent tools?

## Required ERD-style output format

```yaml
agent_first_saas_object_model:
  product_or_feature: string
  modeling_assumptions:
    - string

  domain_entities:
    - name: string
      purpose: string
      source_of_truth: string
      tenant_boundary: string
      key_fields:
        - name: string
          type: string
          required: boolean
          notes: string
      states: string[]
      relationships:
        - target: string
          cardinality: one_to_one | one_to_many | many_to_one | many_to_many
          relationship_name: string
          notes: string

  substrate_entities:
    - name: Goal | Objective | ExecutionPlan | Agent | AgentRun | TaskRun | ApprovalRequest | Decision | Exception | Escalation | PolicyDocument | PolicyClause | PolicyInvocation | WorkTrace | ToolInvocation | DataAccessEvent | AuditEvent | OutcomeMetric | OutcomeLink | string
      purpose: string
      persistence: event_sourced_entity | key_value_entity | workflow_state | view_projection | append_only_audit_event | external_reference
      minimum_fields:
        - name: string
          type: string
          required: boolean
          notes: string
      relationships:
        - target: string
          cardinality: one_to_one | one_to_many | many_to_one | many_to_many
          relationship_name: string
          owning_side: string
          notes: string
      lifecycle:
        states: string[]
        initial_state: string
        terminal_states: string[]
        key_transitions:
          - from: string
            to: string
            trigger: string
            actor: human | agent | system | workflow | timer
            audit_event: string
      invariants:
        - string

  relationship_decisions:
    goal_to_execution_plans: string
    execution_plan_to_agent_runs_and_tasks: string
    approval_request_to_decision: string
    exception_to_escalation: string
    decision_to_precedent_or_policy_change: string
    trace_to_tool_invocations_and_data_access: string

  event_and_trace_requirements:
    audit_events:
      - event_type: string
        subject: string
        required_payload_fields: string[]
        emitted_by_transition: string
    work_traces:
      - trace_type: string
        linked_entities: string[]
        required_sections: string[]

  stack_implications:
    event_sourced_entities:
      - entity: string
        reason: string
    key_value_entities:
      - entity: string
        reason: string
    workflows:
      - workflow: string
        managed_entities: string[]
        pause_resume_points: string[]
    views:
      - view: string
        source_events_or_entities: string[]
        consumers: string[]

  open_questions:
    - question: string
      why_it_matters: string
      likely_options: string[]
```

## Quality checklist

- [ ] Domain entities and ai-first substrate entities are separated but connected.
- [ ] `Goal`, `Objective`, `ExecutionPlan`, `Agent`, `ApprovalRequest`, `Decision`, `Exception`, `PolicyDocument`, `PolicyClause`, `WorkTrace`, `AuditEvent`, and `OutcomeMetric` are included or explicitly deferred.
- [ ] Cardinalities are explicit; especially goal-plan, plan-task-run, approval-decision, exception-escalation, decision-learning, and trace-tool/data relationships.
- [ ] Core entities have lifecycles and transition audit events.
- [ ] Minimum implementation fields are listed with IDs, tenant boundaries, statuses, timestamps, owners, and version references where relevant.
- [ ] Policies cite stable clause IDs and decisions preserve structured rationale, alternatives, evidence, and outcomes.
- [ ] Traces link to tool invocations and data access events; audit events are not replaced by raw logs or transcripts.
- [ ] Stack implications identify Akka entities, workflows, views, consumers, timed actions, and endpoints where needed.
