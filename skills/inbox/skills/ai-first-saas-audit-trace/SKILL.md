---
name: ai-first-saas-audit-trace
description: Specify audit events, work traces, decision provenance, event granularity, retention, redaction, and Akka substrate choices for ai-first SaaS accountability.
---

# ai-first-saas-audit-trace

Use this skill when a coding agent must design or implement accountability substrate, audit trails, work traces, decision provenance, tool/data access records, policy invocation records, rollback records, or trace/audit read models for an ai-first SaaS product.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for ai-first SaaS objects, substrate requirements, decision provenance, and audit expectations.
- `docs/skills-pack-tech-stack.md` describes the target Akka + React/Vite/TypeScript implementation stack and the distinction between Event Sourced Entities, Key Value Entities, Workflows, Consumers, Views, endpoints, and logs.

## Core rules

- Audit data must be generated as part of execution, not reconstructed only from application logs or raw model transcripts.
- Every meaningful agent action must be linked to a durable goal, execution plan, task/run, agent definition version, compiled context, policy/skill/prompt versions, authority check, and outcome where applicable.
- Store structured rationale, evidence, alternatives, policy citations, scoring, and disposition. Do not require storage of hidden model chain-of-thought.
- Audit events are business/accountability facts. Application logs are operational diagnostics. Raw transcripts are optional evidence artifacts, not the audit system of record.
- Event granularity should support accountability, replay, human review, and compliance without recording noisy implementation steps as first-class audit events.
- Retention, redaction, export, deletion, and tenant isolation requirements must be designed before production rollout.

## Accountability substrate concepts

### Audit event

A durable, queryable fact that records a meaningful business, governance, authorization, agent, data, tool, policy, decision, or outcome occurrence.

Examples: `GoalCreated`, `ExecutionPlanApproved`, `PolicyClauseInvoked`, `ToolInvocationCompleted`, `ApprovalGranted`, `ExceptionRaised`, `OutcomeRecorded`.

### Work trace

A trace is the linked sequence of audit events, evidence, tool invocations, data accesses, policy invocations, decisions, and outcomes for a goal, plan, task, recommendation, or final action.

A trace answers:

- What happened?
- Which agent or human did it?
- Under what authority?
- What evidence, data, tools, prompts, skills, and policies were used?
- What changed as a result?
- Was the action approved, reversible, and successful?

### Decision provenance

The structured context that explains a recommendation or action: inputs, evidence, agent ID, prompt/skill/policy versions, clauses invoked, alternatives considered, scores, chosen disposition, human decision, and later outcome.

### Application log

A diagnostic record for operators and developers, such as stack traces, latency, memory, retries, HTTP failures, or debug messages. Logs may help investigate incidents, but they are not a substitute for audit events.

### Raw model transcript

A prompt/response artifact or provider-level interaction. Store only when needed by policy, debugging, evaluation, or compliance; redact sensitive data. Transcripts do not replace structured audit events because they are difficult to query, may expose sensitive data, and may include irrelevant model text.

## Akka substrate selection

Use `docs/skills-pack-tech-stack.md` to choose the smallest substrate that satisfies accountability requirements.

### Full event sourcing / Akka Event Sourced Entities

Use Event Sourced Entities when:

- state transitions must be reconstructable from event history;
- temporal reasoning, replay, or forensic investigation matters;
- the object has meaningful lifecycle transitions;
- decisions, approvals, policy changes, permissions, or regulated actions are involved;
- you must answer exactly how current state was reached.

Good candidates:

- `Goal`, `ExecutionPlan`, `AgentRun`, `TaskRun`, `ApprovalRequest`, `Decision`, `Exception`, `PolicyDocument`, `PolicyCommit`, `AgentDefinition`, `CompiledAgentContext`, high-stakes domain entities.

### Key Value Entities with audit events

Use Key Value Entities when:

- current state is primary;
- lifecycle history is low-risk or reconstructability is not required;
- audit completeness can be satisfied by emitting separate audit events for meaningful changes;
- full event sourcing would overbuild the maturity level.

Good candidates:

- preference records, UI settings, low-risk cached summaries, simple current-state projections, editable drafts before activation.

### Akka Workflows and workflow events

Use Workflows when:

- work is long-running, multi-step, retryable, pausable, resumable, or blocked on human approval;
- compensation, rollback, deadlines, timers, or partial failure handling is required;
- task and agent runs must be coordinated across components.

Workflow events should record lifecycle transitions such as `WorkflowStarted`, `ApprovalGateReached`, `WorkflowPausedForApproval`, `WorkflowResumed`, `StepRetried`, `CompensationStarted`, and `WorkflowCompleted`.

### Views/projections

Use Views for CQRS read models and UI-facing trace queries:

- audit timelines by goal, plan, task, decision, agent, policy, tenant, user, or time window;
- pending approval and exception queues;
- policy invocation analytics;
- tool/data access reports;
- digest and command-center activity streams;
- outcome attribution views.

Views are derived read models, not the authoritative audit source.

### Consumers

Use Consumers to react to entity, workflow, service stream, or topic events:

- fan out audit events to projections;
- compute material/routine classifications;
- trigger notifications and digests;
- update outcome and analytics read models;
- enforce secondary monitoring or compliance checks.

### Application logs

Use logs for developer/operator diagnostics only. A trace or audit surface must not depend on grepping logs to explain a user-visible decision.

## Procedure

### 1. Identify auditable objects and lifecycles

List domain and ai-first objects that require accountability. Include at minimum, when present:

- `Goal`, `Objective`, `ExecutionPlan`, `AgentRun`, `TaskRun`;
- `AgentDefinition`, `AgentSystemPrompt`, `AgentSkill`, `CompiledAgentContext`;
- `PolicyDocument`, `PolicyClause`, `PolicyInvocation`;
- `ToolInvocation`, `DataAccessEvent`, `EvidenceItem`;
- `Recommendation`, `Decision`, `ApprovalRequest`, `Exception`, `Escalation`;
- `OutcomeMetric`, `OutcomeLink`, `RollbackRecord`.

For each object, define lifecycle states and which transitions must create audit events.

### 2. Choose event granularity

Create audit events for meaningful facts, not every internal function call.

A meaningful event usually changes one of these:

- authority or permission status;
- user, agent, or workflow state;
- business/domain state;
- policy, prompt, skill, threshold, guardrail, or approval-gate version;
- evidence set, recommendation, decision, or exception status;
- data access, tool invocation, external side effect, or customer-visible action;
- outcome, rollback, redaction, export, or retention status.

Avoid first-class audit events for:

- token streaming chunks;
- minor UI rendering state;
- repeated polling;
- internal cache hits;
- debug-only messages;
- verbose model scratchpad text.

If a noisy event may matter operationally, keep it in logs or summarized trace details unless it affects accountability.

### 3. Define event schemas

Each audit event should include these base fields unless a maturity decision explicitly scopes them down:

```yaml
audit_event_base:
  id: string
  tenant_id: string
  event_type: string
  occurred_at: datetime
  recorded_at: datetime
  actor:
    actor_type: human | agent | system | external_service
    actor_id: string
    display_name: string | null
  subject:
    object_type: string
    object_id: string
  trace_id: string
  goal_id: string | null
  execution_plan_id: string | null
  task_run_id: string | null
  agent_run_id: string | null
  causation_id: string | null
  correlation_id: string | null
  disposition: auto | review | approval | escalate | fyi | null
  authority:
    permission_id: string | null
    approval_gate_id: string | null
    policy_document_id: string | null
    policy_version_id: string | null
    policy_clause_ids: string[]
  context_versions:
    agent_definition_id: string | null
    agent_definition_version_id: string | null
    system_prompt_version_id: string | null
    skill_version_ids: string[]
    compiled_context_id: string | null
  summary: string
  payload: object
  sensitivity:
    classification: public | internal | confidential | restricted
    contains_pii: boolean
    redaction_status: none | redacted | pending | exempt
  retention:
    policy_id: string
    delete_after: datetime | null
  integrity:
    hash: string | null
    previous_hash: string | null
```

### 4. Capture specialized trace records

Design these records as structured, linkable artifacts. They may be stored as events, child records, or both depending on the Akka substrate choice.

#### Tool invocation

```yaml
tool_invocation_record:
  id: string
  trace_id: string
  agent_run_id: string
  tool_name: string
  tool_version: string | null
  permission_checked: string
  input_summary: string
  input_artifact_ids: string[]
  output_summary: string | null
  output_artifact_ids: string[]
  status: requested | allowed | denied | started | succeeded | failed | timed_out | cancelled
  external_side_effect: boolean
  idempotency_key: string | null
  started_at: datetime | null
  completed_at: datetime | null
  error_class: string | null
```

#### Data access

```yaml
data_access_event:
  id: string
  trace_id: string
  actor_type: human | agent | system
  actor_id: string
  data_scope_id: string
  resource_type: string
  resource_id: string | null
  query_summary: string
  fields_accessed: string[]
  access_purpose: string
  permission_checked: string
  policy_clause_ids: string[]
  contains_pii: boolean
  redaction_applied: boolean
  occurred_at: datetime
```

#### Policy invocation

```yaml
policy_invocation_record:
  id: string
  trace_id: string
  policy_document_id: string
  policy_version_id: string
  clause_ids: string[]
  inputs_summary: string
  evaluation_result: allowed | denied | requires_review | requires_approval | escalate | not_applicable
  explanation: string
  threshold_values: object
  created_approval_request_id: string | null
```

#### Decision provenance

```yaml
decision_provenance_record:
  decision_id: string
  trace_id: string
  recommendation_id: string | null
  agent_id: string
  compiled_context_id: string
  evidence_item_ids: string[]
  alternatives_considered:
    - option: string
      reason_rejected: string
      projected_outcome: string | null
  confidence: number
  risk: number
  impact: number
  stakes_estimate: string | null
  policy_invocation_ids: string[]
  human_decision_event_id: string | null
  outcome_link_ids: string[]
```

#### Rollback/reversibility

```yaml
rollback_record:
  id: string
  trace_id: string
  action_event_id: string
  reversibility: reversible | partially_reversible | irreversible | unknown
  rollback_method: string | null
  rollback_deadline: datetime | null
  rollback_status: not_needed | available | requested | completed | failed | unavailable
  compensation_workflow_id: string | null
  residual_risk: string | null
```

### 5. Link outcomes back to decisions and context

For every material action or decision, specify whether and how outcomes will be linked back to:

- goal and success criteria;
- recommendation and decision;
- agent definition and prompt version;
- skill version and policy version;
- tool invocation or external action;
- human approval, rejection, or override;
- delayed or uncertain outcome windows.

### 6. Define retention, redaction, and privacy rules

For every event category and artifact type, define:

- retention duration and legal/compliance rationale;
- whether PII, secrets, protected health/financial data, customer content, or regulated data may appear;
- redaction at write time vs read time;
- who can view unredacted events;
- export/delete behavior;
- immutable audit requirements that may override deletion;
- tenant-isolation enforcement;
- whether raw transcripts are stored, summarized, encrypted, redacted, or disabled.

### 7. Design read models and UI access

Create Views/projections for:

- trace timeline by `trace_id`;
- audit history by object;
- tool invocation history by agent, tool, tenant, and status;
- data access report by actor, data scope, field, and purpose;
- policy invocation analytics by clause, agent, decision type, and outcome;
- approval and exception provenance;
- rollback/reversibility status;
- outcome attribution summaries.

Every goal, plan, decision card, exception, policy change, and material action should link to its audit/work trace.

## Required event catalog categories

At minimum, evaluate whether the product needs events in these categories:

- goal/objective lifecycle;
- execution plan lifecycle;
- workflow and task-run lifecycle;
- agent run lifecycle;
- compiled context generated/used;
- prompt, skill, rule, policy, threshold, guardrail, permission, and approval-gate version changes;
- permission checks and denials;
- policy invocations;
- tool invocation requested/allowed/denied/started/completed/failed;
- data access;
- evidence attached/changed;
- recommendation created;
- approval requested/granted/rejected/expired;
- decision made/overridden/counterproposed;
- exception raised/escalated/resolved;
- external side effect attempted/completed/failed;
- rollback or compensation requested/completed/failed;
- outcome observed/linked/validated;
- retention, redaction, export, or deletion actions;
- security or tenant-boundary violations.

## Event catalog output format

Use this template when producing a spec, architecture plan, data model, or implementation backlog.

```yaml
audit_trace_design:
  substrate_summary:
    event_sourced_entities:
      - object: string
        reason: string
        key_events: string[]
    key_value_entities_with_audit:
      - object: string
        reason: string
        audit_events: string[]
    workflows:
      - workflow: string
        reason: string
        lifecycle_events: string[]
    views:
      - view: string
        query_purpose: string
        source_events: string[]
    consumers:
      - consumer: string
        trigger_events: string[]
        side_effects: string[]

  event_catalog:
    - event_type: string
      category: goal | plan | workflow | agent_run | task_run | context | policy | permission | tool | data_access | evidence | recommendation | approval | decision | exception | side_effect | rollback | outcome | retention | security
      description: string
      meaningful_when: string
      source_component: event_sourced_entity | key_value_entity | workflow | consumer | endpoint | agent | external_service
      actor_types: [human, agent, system, external_service]
      subject_objects: string[]
      required_fields: string[]
      policy_or_permission_fields: string[]
      links_to: string[]
      disposition_required: boolean
      retention_policy: string
      pii_risk: none | low | medium | high
      redaction_rule: string
      view_projection_targets: string[]
      test_cases: string[]

  trace_models:
    - trace_type: goal_trace | plan_trace | task_trace | decision_trace | policy_trace | tool_trace | data_access_trace | outcome_trace
      primary_key: string
      included_event_types: string[]
      required_artifacts: string[]
      ui_entry_points: string[]

  retention_and_redaction:
    - artifact_type: string
      retention: string
      redaction: string
      unredacted_access_roles: string[]
      export_delete_behavior: string

  open_questions:
    - string
```

## Event sourcing decision checklist

Use full event sourcing when most answers are `yes`:

- Must auditors reconstruct how the current state was reached?
- Are changes governed by human approvals, policy clauses, or regulated requirements?
- Do historical transitions affect future behavior, trust, replay, or outcomes?
- Is rollback, compensation, or temporal debugging important?
- Would losing intermediate transitions undermine accountability?
- Does the object coordinate agent actions or external side effects?

Audit-log-backed current state may be acceptable when most answers are `no`:

- the object is low-risk and easily overwritten;
- only current state matters to users;
- lifecycle transitions do not influence policy, approvals, or outcomes;
- audit requirements can be met by recording create/update/delete events separately;
- replay does not depend on reconstructing this object's internal event stream.

## Acceptance checks

A design using this skill is complete only if:

- meaningful events are cataloged with schemas, source components, retention, redaction, and projections;
- audit events are clearly separated from application logs and raw model transcripts;
- each material agent action links to prompt, skill, policy, data, tool, authority, decision, and outcome context where applicable;
- Akka substrate choices are justified rather than defaulting every object to CRUD;
- Workflows emit pause/resume/approval/retry/failure events for long-running work;
- Views support audit and work-trace UI surfaces without becoming the source of truth;
- sensitive data, transcript storage, retention, deletion, and redaction policies are explicit;
- tests can verify audit event creation for approvals, denials, tool calls, data access, policy invocations, decisions, outcomes, and rollback records.
