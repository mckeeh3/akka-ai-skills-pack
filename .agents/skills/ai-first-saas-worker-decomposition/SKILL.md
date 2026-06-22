---
name: ai-first-saas-worker-decomposition
description: Identify human, functional-agent, internal-agent, autonomous-agent, evaluator-agent, and system workers before surfaces, capabilities, or Akka components are selected; use for workforce rosters, responsibility boundaries, authority, handoffs, supervision, and traces.
---

# AI-First SaaS Worker Decomposition

Use this skill after `ai-first-saas` and before detailed surface, capability, agent-team, or Akka component decomposition when a product requirement needs a clear human/agent/system division of labor.

This is a workforce-modeling and routing skill. It does not replace `agent-workstream-apps`, `ai-first-saas-agent-team-design`, `capability-first-backend`, `akka-autonomous-agents`, `akka-agents`, `akka-workflows`, or app-description skills. It produces the worker roster and responsibility map those skills should preserve.

## Required reading

Read first:

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/requirements-to-workstream-development-process.md`
- `../docs/workforce-decomposition.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/structured-surface-contracts.md`
- `../docs/capability-first-backend-architecture.md`
- `../ai-first-saas/SKILL.md`
- `../agent-workstream-apps/SKILL.md`

Then load focused companions only for the selected scope:

- `ai-first-saas-agent-team-design` for detailed agent team/coordinator/specialist design;
- `capability-first-backend` for worker-to-capability and governed-tool contracts;
- `akka-solution-decomposition` for component mapping;
- `app-description-functional-agent-modeling`, `app-description-surface-modeling`, and `app-description-capability-modeling` for current-intent graph updates.

## Use when

Use this skill for tasks that mention or imply:

- roles, personas, workers, staff, operators, reps, managers, reviewers, approvers, auditors, or agents;
- questions like “who does this work?”, “which agent should own this?”, or “what does the AI worker do?”;
- fuzzy human/AI responsibility split;
- comparable human and agent workers such as Sales Rep versus Sales Pipeline Agent, Inventory Manager versus Reorder Recommendation Agent;
- internal specialist agents, background monitors, durable AutonomousAgent workers, evaluator/reviewer agents, or system workers;
- handoffs, escalations, supervision, approval boundaries, or worker-specific traces.

## Do not use when

Do not expand into workforce decomposition when:

- the task is a narrow fixed component edit with settled worker/capability context;
- the request is repository/skills-pack maintenance unrelated to generated app architecture;
- the feature is purely static/public and has no protected work, AuthContext, worker, or capability semantics.

## Worker decomposition workflow

### 1. Identify workstreams and units of work

Start from affected workstreams or domain outcomes. For each unit of work, list the verbs and triggers: inspect, monitor, decide, approve, create, update, revoke, retry, summarize, recommend, evaluate, escalate, notify, reconcile, archive, learn, audit; started by prompt, surface action, attention item, schedule, event, workflow step, integration, or deep link.

### 2. Inventory human workers

Name human workers in domain language, such as Sales Rep, Sales Manager, Inventory Manager, Procurement Lead, Finance Approver, Support Agent, Tenant Admin, Auditor, or SaaS Owner Support Operator.

For each human worker, capture responsibility, selected AuthContext scope, workstream access, surfaces used, direct actions, approval/escalation duties, evidence visibility, trace visibility, and denial/hidden behavior.

### 3. Inventory agent workers

Name agent workers by their specific job. Avoid generic names like “AI Agent” or “Inventory Bot” when a narrower job exists.

Classify each agent worker:

- `functional-agent`: user-facing workstream owner in the rail/shell;
- `internal-agent`: bounded specialist invoked by a functional agent, workflow, timer, consumer, endpoint, or tool;
- `autonomous-agent`: durable background model-driven worker with task lifecycle;
- `evaluator-agent`: independent reviewer/judge for quality, risk, policy fit, completeness, or outcomes.

For each agent worker, define single responsibility, non-responsibilities, supervising human, authority level, allowed evidence/data, allowed capabilities/governed-tools, approval/autonomy policy, handoffs/escalations, prompt/skill/reference/model/tool governance, traces, and fail-closed behavior.

### 4. Inventory system workers

Record deterministic participants such as workflows, timers, consumers, projections/views, integrations, service identities, and policy engines. Capture trigger, authority basis, idempotency, provenance, audit, failure behavior, and result/attention effects.

### 5. Build the responsibility and handoff map

For each work unit, map:

```text
primary worker → supporting worker(s) → reviewer/approver → escalation/fallback → handoff artifact → result surface/event → trace/audit record
```

If human and AI workers can both perform or request the same operation, map them to one governed capability/governed-tool with separate actor adapters and trace sources.

### 6. Route downstream

- Functional-agent/workstream placement → `agent-workstream-apps` and `app-description-functional-agent-modeling`.
- Surface use/production → `ai-first-saas-ui-surfaces` and `app-description-surface-modeling`.
- Worker actions/tools → `capability-first-backend` and `app-description-capability-modeling`.
- Agent team shape → `ai-first-saas-agent-team-design`.
- Durable background worker → `akka-autonomous-agents` and `akka-autonomous-agent-tasks`.
- Request/reply/internal specialist → `akka-agents` and focused `akka-agent-*` skills.
- Deterministic coordination → `akka-workflows`, `akka-timed-actions`, `akka-consumers`, or `akka-views` as appropriate.

## Output expectations

Produce a compact worker decomposition with:

- affected workstreams and scope;
- worker roster grouped by human, functional-agent, internal-agent, autonomous-agent, evaluator-agent, and system workers;
- for each worker: responsibility, non-responsibilities, authority level, AuthContext/scope, supervising human or owning workstream, tools/capabilities, surfaces used/produced, handoffs/escalations, traces, and failure behavior;
- worker-to-work-unit responsibility matrix;
- human/AI shared-operation map showing one governed-tool with separate actor adapters where applicable;
- candidate attention categories and result surfaces each worker produces or consumes;
- downstream skill routing and open questions only where authority, supervision, evidence, tool access, or handoff semantics would otherwise be guessed.

## Review checklist

Before moving to capability or component design, verify:

- every affected workstream has a worker roster or an explicit non-worker/system-only justification;
- every functional agent owns exactly one workstream;
- every agent worker has a specific type, bounded responsibility, non-responsibilities, supervising human, and authority level;
- no agent worker inherits human authority implicitly;
- every side-effecting agent worker has bounded autonomy or approval requirements;
- every worker action maps to a capability/governed-tool candidate;
- every handoff/escalation produces a traceable artifact and target;
- tests can cover allowed, denied, stale/failure, tenant isolation, audit/trace, and handoff behavior.
