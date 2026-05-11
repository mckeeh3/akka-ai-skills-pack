---
name: ai-first-saas-runtime-orchestration
description: Design runtime execution, Akka orchestration, agent/task/tool lifecycles, retries, approval blocking, idempotency, and failure handling for ai-first SaaS systems.
---

# ai-first-saas-runtime-orchestration

Use this skill when a coding agent must specify or implement how ai-first SaaS work executes at runtime: goal activation, planning, agent delegation, task execution, tool calls, async workers, approvals, retries, pause/resume, long-running work, and failure recovery.

References:

- `docs/ai-first-saas-coding-agent-framework.md` is the canonical contract for goals, execution plans, agents, approvals, traces, policies, and human governance.
- `docs/skills-pack-tech-stack.md` defines the target Akka + React/Vite/TypeScript stack: Workflows, Event Sourced Entities, Key Value Entities, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, and Akka agents.

## Core rules

- Treat runtime execution as a durable process, not an ephemeral model call.
- A human objective must become a durable `Goal`, then an inspectable `ExecutionPlan`, then one or more `AgentRun` and `TaskRun` records.
- Use Akka Workflows for long-running orchestration, retries, compensation, pause/resume, approval gates, and multi-agent delegation.
- Use Event Sourced Entities for lifecycle-heavy objects where reconstruction, auditability, replay, and temporal reasoning matter.
- Use Key Value Entities only for simpler current-state records whose transitions are low-risk and still emit audit events when meaningful.
- Agents may plan, classify, recommend, summarize, and call tools, but platform workflows/entities enforce permissions, idempotency, side-effect boundaries, and approval gates.
- Every runtime transition that affects accountability should emit an audit event and update trace/read-model projections.

## Akka runtime mapping

| Runtime concern | Preferred stack component |
|---|---|
| Goal lifecycle and authoritative state | Event Sourced Entity, with Views for UI |
| Execution plan lifecycle | Event Sourced Entity, optionally created by an Akka agent |
| Long-running goal execution | Akka Workflow |
| Agent run/task run lifecycle | Event Sourced Entity or workflow-owned state plus audit events |
| Coordinator delegation | Workflow step invoking Akka agents and routing tasks |
| Tool calls and external side effects | Workflow steps with idempotency keys and durable `ToolInvocation` records |
| Approval blocking and resume | Workflow pause/signal/resume pattern plus `ApprovalRequest` entity |
| Async event reactions | Consumers responding to entity/workflow/topic events |
| Deadlines, reminders, retry schedules | Timed Actions and timers |
| Query/UI read state | Views/projections for command center, queues, traces, and digests |
| Browser/API ingress | HTTP endpoints, SSE/WebSocket for streaming progress where useful |
| Typed internal APIs | gRPC endpoints where typed service contracts are needed |
| Agent/tool exposure to AI clients | MCP endpoints when exposing application tools/resources/prompts |

## Runtime object checklist

Define these objects when designing orchestration:

```yaml
runtime_objects:
  goal:
    id: string
    state: draft | planned | active | paused | blocked | completed | failed | canceled
  execution_plan:
    id: string
    goal_id: string
    state: draft | awaiting_approval | approved | active | revised | completed | canceled
  agent_run:
    id: string
    goal_id: string
    execution_plan_id: string
    agent_id: string
    compiled_context_id: string
    state: pending | running | waiting_on_tool | waiting_on_approval | waiting_on_dependency | completed | failed | canceled
  task_run:
    id: string
    agent_run_id: string
    assigned_agent_id: string
    state: pending | ready | running | blocked | waiting_on_approval | retry_scheduled | completed | failed | canceled | skipped
  tool_invocation:
    id: string
    task_run_id: string
    tool_name: string
    idempotency_key: string
    side_effect_class: none | reversible | compensatable | irreversible
    state: requested | authorized | running | succeeded | failed | timed_out | compensated | canceled
  approval_gate:
    id: string
    blocking_object_id: string
    state: not_required | required | requested | approved | rejected | expired | escalated
```

## State machines

### Goal lifecycle

```text
draft
→ planned
→ active
→ paused
→ active
→ completed
```

Alternative terminal or exception paths:

```text
planned → canceled
active → blocked → active
active → failed
active → canceled
paused → canceled
```

Rules:

- `draft` goals may be edited without execution.
- `planned` goals have an execution plan but no active work unless policy allows auto-activation.
- `active` goals must have at least one workflow/run coordinating execution.
- `blocked` means work cannot proceed without dependency, approval, data, policy, permission, or human action.
- Terminal states must link to outcome, failure classification, or cancellation reason where applicable.

### Execution plan lifecycle

```text
draft
→ generated
→ awaiting_approval
→ approved
→ active
→ completed
```

Alternative paths:

```text
generated → revised → awaiting_approval
awaiting_approval → rejected → revised | canceled
active → revised → awaiting_approval | active
active → canceled
```

Rules:

- Plans must expose assigned agents, tools, data scopes, approval gates, risks, and expected side effects before activation.
- High-risk plans require explicit approval before `active`.
- Revisions after activation must preserve provenance and may require a new approval gate.

### Agent run lifecycle

```text
pending
→ context_compiled
→ authorized
→ running
→ completed
```

Blocking and failure paths:

```text
running → waiting_on_tool → running
running → waiting_on_approval → running
running → waiting_on_dependency → running
running → failed_retryable → retry_scheduled → running
running → failed_terminal
running → canceled
```

Rules:

- `context_compiled` snapshots prompt, skill, policy, rule, tool, data scope, and authority versions.
- `authorized` means platform permission checks passed; prompts alone do not authorize execution.
- Each run must create or attach to a `WorkTrace`.
- Retryable failures must preserve attempt count, error class, and previous side-effect status.

### Task run lifecycle

```text
pending
→ ready
→ running
→ completed
```

Blocking and alternate paths:

```text
ready → blocked
running → waiting_on_approval → ready
running → retry_scheduled → ready
running → failed
running → skipped
running → canceled
```

Rules:

- A task should be small enough to classify permissions, inputs, outputs, side effects, and retry behavior.
- Task dependencies should be explicit rather than implicit in agent text.
- Skipped tasks require a reason: policy, superseded plan, dependency failure, human cancellation, or no longer relevant.

### Tool invocation lifecycle

```text
requested
→ policy_checked
→ authorized
→ running
→ succeeded
```

Failure and compensation paths:

```text
policy_checked → denied
policy_checked → approval_required → approved → authorized
approval_required → rejected
running → timed_out → retry_scheduled | failed
running → failed → retry_scheduled | failed_terminal
succeeded → compensation_requested → compensated | compensation_failed
```

Rules:

- Tool invocations must have idempotency keys before calling external systems.
- Separate no-side-effect tools from reversible, compensatable, and irreversible side effects.
- Irreversible or sensitive side effects usually require stricter approval gates and richer audit events.
- Never retry a side-effecting call unless idempotency and previous outcome are known.

### Approval gate lifecycle

```text
not_required
→ required
→ requested
→ approved
→ resumed
```

Alternative paths:

```text
requested → rejected → revised | canceled | failed
requested → expired → escalated
requested → escalated → approved | rejected
```

Rules:

- The workflow must pause or block before the protected side effect, not after.
- Approval requests must cite the policy clause, confidence/risk/impact, evidence, alternatives, and requested action.
- A human decision resumes, revises, cancels, or escalates the blocked workflow.

## Coordinator delegation pattern

Use a coordinator workflow/agent pair for non-trivial systems:

1. HTTP/API/chat/UI command creates or updates a durable `Goal`.
2. Coordinator agent drafts an `ExecutionPlan` with tasks, owners, tools, data needs, approval gates, and risk assessment.
3. Workflow validates the plan against policy, permissions, maturity level, and required approvals.
4. Human approves or revises the plan when required.
5. Workflow dispatches task runs to specialist agents.
6. Specialist agents execute bounded tasks through platform-authorized tools.
7. Consumers update Views for command center, queues, traces, and digest surfaces.
8. Approval or exception events pause specific branches and surface decision cards.
9. Human decisions resume, revise, or terminate affected branches.
10. Workflow completes the goal, records outcomes, or classifies terminal failure.

## Async queues and workers

- Prefer durable workflow steps, entity commands, Consumers, topics/streams, and Timed Actions over in-memory background jobs.
- Use queues/topics for high-volume or decoupled work, but persist correlation IDs to `Goal`, `ExecutionPlan`, `AgentRun`, `TaskRun`, and `WorkTrace`.
- Build Views for pending work: ready tasks, blocked tasks, approval queue, retry queue, failed tasks, active goals, and stale runs.
- Use Timed Actions for reminders, deadlines, SLA escalations, delayed retries, periodic digests, and stale-run detection.
- Make workers idempotent: processing the same event twice must not duplicate external side effects or corrupt state.

## Idempotency and side-effect boundaries

For every command, workflow step, tool call, and external integration, specify:

```yaml
side_effect_contract:
  operation: string
  side_effect_class: none | local_state_only | reversible | compensatable | irreversible
  idempotency_key: string
  dedupe_scope: tenant | goal | task | external_system
  precondition_checks: string[]
  approval_required_before_execution: boolean
  compensation_action: string | null
  audit_events:
    - string
```

Guidance:

- Generate idempotency keys from stable operation identity, not random retry attempts.
- Persist `requested`, `authorized`, and `completed` states around external side effects.
- Check whether an external call already succeeded before retrying after timeout.
- Prefer a prepare/preview/approve/commit pattern for high-impact work.
- Put irreversible actions behind explicit approval gates unless policy clearly permits automation.

## Retries and partial failures

Classify failures before retrying:

| Failure class | Examples | Default handling |
|---|---|---|
| transient_infrastructure | network timeout, 503, rate limit | retry with backoff and jitter |
| external_dependency | upstream unavailable, stale API contract | retry then escalate if deadline/risk exceeded |
| validation | missing required field, schema mismatch | block for data repair or plan revision |
| policy_denied | permission, policy, threshold, approval missing | do not retry; request approval/revision/escalation |
| model_uncertainty | low confidence, conflicting evidence | request more evidence, human review, or specialist task |
| tool_error | deterministic tool failure | retry only if idempotent; otherwise escalate |
| side_effect_ambiguous | timeout after possible external commit | reconcile before retry; never blindly duplicate |
| human_rejected | approval rejected or counterproposed | revise/cancel; do not retry same action unchanged |
| security_privacy | tenant breach risk, injection, secret exposure | stop, contain, audit, escalate |
| terminal_business | objective impossible, constraint conflict | fail goal/task with explanation and alternatives |

Partial failure guidance:

- Isolate failures to the smallest task/branch possible.
- Continue independent branches only when policy and dependency rules allow.
- Surface material partial failures in the command center and trace.
- Record compensation status for already-completed side effects.
- Convert repeated retry exhaustion into a durable exception, not an infinite loop.

## Approval blocking and resume

Design workflows so approval gates are first-class:

```text
workflow reaches protected action
→ policy evaluator returns approval_required
→ ApprovalRequest entity created
→ decision card appears in queue
→ workflow pauses with correlation ID
→ human approves/rejects/counterproposes/requests evidence
→ workflow resumes, revises plan, spawns evidence task, or cancels branch
```

Implementation notes:

- Store the exact blocked action and pre-approval state.
- Expire or escalate stale approvals with Timed Actions.
- Rejecting an approval should be a business decision with audit impact, not a technical failure.
- Resuming must re-check policy and permissions if relevant state changed while paused.

## Long-running goal resume

A resumable goal must persist:

- current goal/plan/task states;
- workflow instance/correlation IDs;
- compiled agent context references used by active runs;
- pending approvals and exceptions;
- completed side effects and compensation status;
- retry schedules and attempt counts;
- external integration cursors/checkpoints;
- unresolved dependency markers;
- trace and audit event links.

On resume:

1. Rehydrate workflow/entity state.
2. Reconcile external side effects with persisted invocation records.
3. Re-check permissions, policy versions, and approval validity.
4. Continue only idempotent or explicitly resumable steps.
5. Emit a resume audit event with reason and actor.

## Runtime design output template

```yaml
runtime_orchestration_spec:
  product_or_capability: string
  maturity_level: prototype | mvp | production | regulated_high_risk
  entrypoints:
    - name: string
      type: http | grpc | mcp | consumer | timed_action | ui_event
      creates_or_updates: string[]
  workflows:
    - name: string
      purpose: string
      trigger: string
      state_machine: string
      steps:
        - name: string
          actor: workflow | akka_agent | consumer | timed_action | human | external_system
          input_objects: string[]
          output_objects: string[]
          side_effect_class: none | local_state_only | reversible | compensatable | irreversible
          idempotency_key_strategy: string
          approval_gate: string | null
          retry_policy: string
          failure_handling: string
  entities:
    - name: string
      akka_component: event_sourced_entity | key_value_entity
      commands: string[]
      events: string[]
      lifecycle_states: string[]
  agents:
    - agent_id: string
      runtime_role: coordinator | specialist | policy | exception | audit | other
      invoked_by: string
      tools_available: string[]
      authority_boundary: string
      emits: string[]
  async_reactions:
    - consumer_or_timer: string
      listens_to: string[]
      action: string
      idempotency_rule: string
  approval_gates:
    - name: string
      blocks: string
      policy_clause_ids: string[]
      resume_actions: string[]
  failure_matrix:
    - failure_class: string
      detection: string
      retry: string
      compensation: string | null
      escalation: string
      audit_events: string[]
  read_models:
    - view_name: string
      source_events: string[]
      serves_ui_surface: string
```

## Acceptance checks

Before considering runtime orchestration complete, verify:

- [ ] Goals, plans, agent runs, task runs, tool invocations, approvals, exceptions, and outcomes have explicit lifecycle states.
- [ ] Long-running work is modeled with Akka Workflows rather than in-memory control flow.
- [ ] Event Sourced Entities are used where lifecycle history, auditability, replay, or temporal reasoning matter.
- [ ] Every side-effecting operation has an idempotency strategy and side-effect classification.
- [ ] Approval gates block before protected actions and can resume/revise/cancel workflows.
- [ ] Failure handling distinguishes retryable, non-retryable, policy, human, security, model, and ambiguous side-effect failures.
- [ ] Consumers, Views, and Timed Actions keep UI read models, queues, deadlines, and digests current.
- [ ] Platform authorization and policy checks are enforced outside the model prompt.
- [ ] Runtime events feed audit traces and decision provenance.
