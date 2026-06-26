# Workforce decomposition

## Status and scope

This is the canonical workforce-decomposition doctrine for generated secure AI-first SaaS app intake, app-description maintenance, solution decomposition, backlog generation, and implementation planning.

Use it with:

- `./app-worker-tool-model.md` for the canonical worker → harness → actor adapter → governed tool → capability → Akka implementation separation
- `./worker-artifact-contract.md` for reusable `workers/<worker>.md` current-intent artifact shape
- `./requirements-to-workstream-development-process.md`
- `./agent-workstream-application-architecture.md`
- `./workstream-contract.md`
- `./structured-surface-contracts.md`
- `./capability-first-backend-architecture.md`
- `./agent-component-selection-guide.md`

Workforce decomposition fills the gap between identifying workstreams and selecting surfaces, capabilities, agents, workflows, or Akka components. It answers the product question:

> Who or what does the work, with what authority, evidence, tools, supervision, handoffs, traces, and failure behavior?

## Core rule

Generated AI-first SaaS applications must identify the workforce before capability or component selection. The workforce includes human workers, user-facing functional-agent workers, internal specialist agent workers, durable autonomous/background agent workers, and deterministic system workers.

Do not treat agent workers as generic AI helpers. Each agent worker is a bounded worker with explicit responsibility, authority, evidence needs, execution harnesses, actor adapters, governed tools, capabilities, supervision, handoffs, and traces.

Default order:

```text
input / PRD / feature request / incremental change
→ secure SaaS foundation and AuthContext assumptions
→ affected workstream inventory
→ workforce decomposition: human workers + agent workers + system workers
→ responsibility, authority, supervision, and handoff map
→ per-workstream attention categories and role-specific dashboards
→ human surface graph and agent/internal worker graph
→ governed tools inside backend capabilities
→ selected actor adapters and exposure channels
→ Akka substrate and implementation tasks
```

A planning output is incomplete if it names a workstream, agent, capability, workflow, or surface without identifying the responsible worker or recording why no worker is needed for that slice.

## Worker taxonomy

| Worker type | Meaning | Typical realization |
|---|---|---|
| `human` | Authenticated person or organizational role responsible for doing, deciding, approving, supervising, or auditing work. | Role/AuthContext, workstream access, dashboards, surfaces, forms, approvals, trace visibility. |
| `functional-agent` | User-facing functional/context-area agent that owns exactly one role-authorized workstream. | Managed AgentDefinition, workstream shell, composer, structured surfaces, governed tools, Akka Agent when model-backed. |
| `internal-agent` | Bounded specialist agent invoked by a functional agent, workflow, timer, consumer, endpoint, or internal tool. Not a rail/workstream entry. | Akka Agent, governed prompt/skill/reference/tool boundary, structured response, traces. |
| `autonomous-agent` | Durable background model-driven worker with task lifecycle, progress, notifications, cancellation/failure, and acceptance/rejection semantics. | Akka AutonomousAgent tasks, workflow or capability wrappers, task/progress/result surfaces. |
| `evaluator-agent` | Specialist reviewer that judges quality, policy fit, risk, completeness, or outcome evidence. | Akka evaluator agent, structured EvaluationResult, workflow gates, decision cards. |
| `system` | Deterministic workflow, timer, consumer, service integration, projection, or policy engine participant. | Workflow, TimedAction, Consumer, View updater, endpoint/service code. |

Functional agents and human workers are both workstream participants. Internal/autonomous/evaluator agents and system workers usually support the workstream through capability-backed operations, events, task progress, or result surfaces.

## Worker identification workflow

### 1. Identify work to be done

For each affected workstream or domain outcome, list the concrete work items:

- monitor, inspect, search, decide, approve, create, update, revoke, retry, summarize, recommend, evaluate, escalate, notify, reconcile, archive, learn, or audit;
- what starts the work: user prompt, surface action, attention item, schedule, event, workflow step, support request, integration input, or deep link;
- what a successful outcome looks like;
- what can fail, become stale, require approval, or need human judgment.

### 2. Identify human workers

Name human workers in product language. Examples: Sales Rep, Sales Manager, Inventory Manager, Procurement Lead, Finance Approver, Support Agent, Tenant Admin, Auditor, SaaS Owner Support Operator.

For each human worker, record:

- organizational responsibility and non-responsibilities;
- selected AuthContext scope: organization/tenant/customer/member/role/capability basis;
- workstreams they can enter and default dashboard needs;
- surfaces they use to inspect, decide, approve, repair, or supervise;
- actions they can execute directly versus actions requiring approval/escalation;
- evidence and trace visibility;
- denied/hidden behavior and tenant-isolation expectations.

### 3. Identify agent workers

Name agent workers by the job they perform, not by a generic AI label. Prefer domain-semantic names such as `Reorder Recommendation Agent`, `Lead Scoring Agent`, `Policy Deviation Reviewer`, `Follow-up Drafting Agent`, `Trace Summarizer`, or `Stockout Monitor Agent`.

Classify each as:

- `functional-agent` when users directly enter the work area, see durable workstream history, request surfaces/actions, and supervise work;
- `internal-agent` when it performs one bounded reasoning job for another worker or component;
- `autonomous-agent` when it owns durable task lifecycle, progress, failure/cancellation, notifications, and acceptance/rejection;
- `evaluator-agent` when it reviews another worker's output or gates a decision.

For each agent worker, record:

- single responsibility and explicit non-responsibilities;
- worker owner/steward and supervising human role;
- allowed evidence/data and redaction profile;
- allowed governed tools, capability ids, actor adapters, and exposure channels, if any;
- authority level: recommend, draft, evaluate, propose, execute, approve, or only route;
- autonomy policy and approval gates;
- handoffs/escalations to humans, functional agents, workflows, or other agents;
- prompt/skill/reference/model/tool-boundary governance needs;
- trace requirements: prompt assembly, skill/reference loads, tool calls, data access, policy decisions, denials, outputs, and requested-by relationship;
- provider/configuration fail-closed behavior.

Human availability does not automatically grant agent availability. If a human can execute a surface action, an AI-backed worker may use the same governed-tool only when its tool boundary and policy explicitly allow that exposure.

### 4. Identify system workers

Record deterministic participants when they perform work without model reasoning:

- workflows that coordinate steps, approvals, retries, compensation, or waiting;
- timers that schedule reminders, expiry, rechecks, digests, or retention;
- consumers that react to entity/workflow/topic/service-stream events;
- views/projections that produce dashboards, attention summaries, lists, searches, or evidence;
- external integrations and service identities.

System workers still need authority basis at their invocation boundary, idempotency, provenance/correlation, audit/work traces, and failure behavior.

### 5. Build the responsibility and handoff map

For each unit of work, identify:

```text
primary worker
supporting workers
reviewer/approver
fallback or escalation worker
handoff artifact
handoff trigger
result surface or event
trace/audit record
```

Avoid responsibility gaps and unsafe overlap. If two workers can perform the same side effect, they must share one governed capability/governed-tool and declare separate actor adapters, approval behavior, and trace source.

## Worker contract

Use this minimum contract in app-description, specs, backlogs, and decomposition plans when the worker is relevant to implementation. For maintained current-intent files, use the fuller reusable Markdown shape in `./worker-artifact-contract.md` under `app-description/global/workers/<worker>.md` or `app-description/domains/<domain>/workstreams/<workstream>/workers/<worker>.md`.

```yaml
workerId:
displayName:
workerType: human | functional-agent | internal-agent | autonomous-agent | evaluator-agent | system
owningWorkstreamId:
responsibility:
nonResponsibilities:
supervisingHumanWorkerIds:
authContextScope:
authorityLevel: observe | recommend | draft | evaluate | propose | execute | approve | administer
allowedDecisions:
requiresApprovalWhen:
inputsAndEvidence:
producedOutputs:
surfacesUsed:
surfacesProduced:
capabilitiesUsed:
governedTools:
executionHarnesses:
actorAdapters:
exposureChannels:
handoffsTo:
escalatesTo:
auditAndTraceRequirements:
failureAndDenialBehavior:
runtimeReadiness:
```

Keep the contract proportional. A planning artifact may summarize low-risk deterministic system workers, but agent workers and consequential human workers need explicit authority, tool, trace, and handoff fields.

## Relationship to workstreams, surfaces, and capabilities

Workforce decomposition does not replace workstreams, surfaces, or capabilities. It makes them safer:

```text
worker → uses/produces surfaces
worker → invokes governed-tools through actor adapters
worker → owns or supports attention categories
worker → participates in workflows/tasks/events
worker → emits audit/work traces
worker → maps to Akka Agent, AutonomousAgent, Workflow, Consumer, Timer, View, Endpoint, or frontend surface as needed
```

Surfaces remain the human-backed worker harness and `surface_action` actor-adapter interface. Agent tools are `agent_tool_call` actor adapters for governed tools, not separate authority. Capabilities remain the product backend boundary. Akka components remain implementation substrates selected after responsibilities, governed-tool semantics, and capability semantics are clear.

## Readiness checklist

Before moving from description/planning to implementation, verify:

- every affected workstream has a worker roster or an explicit non-worker/system-only justification;
- every functional agent has exactly one owning workstream;
- every consequential surface action names the human worker(s) and capability/governed-tool behind it;
- every agent worker has a type, responsibility, non-responsibilities, authority level, supervising human, allowed tools/evidence, approval policy, traces, and failure behavior;
- every side-effecting AI-backed worker has explicit bounded autonomy or human approval;
- every durable/background agent worker has task lifecycle, progress/result surfaces, notification/attention behavior, cancellation/failure semantics, and runtime fail-closed behavior;
- every handoff/escalation target exists and produces a traceable artifact;
- no prompt, UI label, route, or hidden field is treated as authorization;
- tests can cover allowed, denied, stale/failure, tenant isolation, audit/trace, and handoff behavior.

## Anti-patterns

- Jumping from requirements directly to Akka components without naming workers.
- Creating one generic assistant for unrelated responsibilities with different authority, tools, or risk.
- Treating a human role's permission as automatic permission for an AI agent.
- Using a workflow, timer, or consumer as an untracked system worker with no provenance or audit.
- Making dashboards or surfaces without identifying which worker needs the work and what they can do next.
- Duplicating business operations separately for human clicks, confirmed chat plans, and AI tool calls instead of sharing one governed tool inside one capability with separate actor adapters.

See also [Requirements-to-workstream development process](requirements-to-workstream-development-process.md), [Agent workstream application architecture](agent-workstream-application-architecture.md), [Structured surface contracts](structured-surface-contracts.md), and [Capability-first backend architecture](capability-first-backend-architecture.md).
