# Requirements-to-workstream development process

## Status

Canonical process doctrine for generated secure AI-first SaaS app intake, PRD processing, app-description maintenance, decomposition, backlog generation, and implementation planning.

Use this doc when broad product input must become a workstream-centered application model before Akka components are selected. It is the operative source for workstream-centered intake, planning, backlog, and implementation guidance.

Related doctrine:
- `./ai-first-saas-application-architecture.md`
- `./agent-workstream-application-architecture.md`
- `./workstream-contract.md`
- `./workstream-manifest-schema.md`
- `./minimum-implementable-workstream-slice.md`
- `./workstream-attention-contracts.md`
- `./structured-surface-contracts.md`
- `./capability-first-backend-architecture.md`
- `./agent-component-selection-guide.md`
- `./workstream-ui-reference-architecture.md`

## Core rule

For generated secure AI-first SaaS apps, process broad requirements and incremental changes through workstreams before pages, CRUD resources, endpoint lists, database tables, event streams, or Akka component families. For an existing app, reconcile the input against the existing workstream graph: affected workstreams, role-specific dashboards, attention items, surface graph nodes/edges, governed-tools, internal workstream agent graph, workstream expertise, Akka substrate, UI/API, tests, and pending tasks.

Default order:

```text
input / PRD / feature request / incremental change
→ secure SaaS foundation and tenant/customer/AuthContext assumptions
→ affected workstream inventory
→ per-workstream attention breakdown: "what needs my attention?"
→ role-specific dashboard surfaces and attention summary contracts
→ human surface graph: dashboard trunk, surface nodes, surface-action edges
→ internal workstream agent graph: virtual dashboard agent, worker agents, delegations, escalations
→ governed-tools inside capability files and surface/action maps
→ governed capability/API contracts and selected exposure channels
→ selected Akka substrate per capability/governed-tool
→ request-based workstream Agent turns for immediate user-facing messages
→ internal/background worker candidates, usually AutonomousAgent tasks when durable lifecycle fits
→ events/messages/notifications
→ My Account, left rail, and workstream-local attention projections
→ audit/work traces
→ implementation tasks and local validation
```

A planning output is implementation-ready only when each vertical slice can be traced as:

```text
workstream attention category
→ role-specific dashboard state
→ surface graph node/action edge
→ governed capability/API and governed-tool
→ exposure channel: browser-tool, agent-tool, internal-tool, workflow/timer/consumer/MCP exposure, or API
→ selected Akka substrate and participant
→ events/notifications/projections
→ audit/work trace
→ tests and local validation
```

If a link is missing, create bounded follow-up questions or tasks instead of claiming readiness.

## Planning artifact output contract

Direct solution plans and PRD-to-spec/backlog outputs must make the graph model visible, not implicit. A generated SaaS planning artifact should include, at minimum:

1. scope label and secure SaaS foundation assumptions;
2. one-workstream vs multi-workstream decision, with split/merge rationale and shared cross-workstream concerns;
3. affected workstreams and owner functional agents;
4. per-role dashboard/attention contracts, including My Account and left rail effects;
5. human surface graph nodes, action edges, result surfaces, edge effects, and trace links;
6. internal workstream agent graph, including virtual dashboard agent, worker agents, delegations, governed-tools, stop/escalation rules, and result/proposal surfaces;
7. workstream expertise plan: prompt intent, skill/reference families, compact manifests, tool boundaries, authorized loaders, denials, governance owner, default-content governance expectations, user-help examples, and tests;
8. capability and governed-tool inventory with AuthContext, tenant/customer scope, schemas, side effects, idempotency, policy/approval, audit/work traces, qualified exposure channels, and tests;
9. Akka substrate mapping only after the capability/governed-tool contract is clear;
10. implementation order by vertical workstream/attention/dashboard/surface-edge/governed-tool/capability increments;
11. pending questions or pending tasks that preserve the same context.

Large PRDs must not skip the one-workstream vs multi-workstream decision. Incremental inputs must reconcile against existing workstream graph nodes, surface edges, governed-tools, expertise bundles, specs, backlogs, queues, and tests instead of regenerating a disconnected plan.

## Required fields to preserve

Artifacts may keep only the smallest subset relevant to their scope, but decomposition, app-description, specs, backlog, queue, and implementation tasks must not erase these semantics.

### Workstream identity

Use `./workstream-contract.md` as the compact field contract and `./workstream-manifest-schema.md` as the machine-readable index for app-description trees. For one harness-sized implementation task, use `./minimum-implementable-workstream-slice.md` instead of copying the full contract. Record:
- `workstreamId`, display name, and responsibility;
- whether this is the workstream definition/type or a runtime workstream instance/thread/log;
- owner functional/context-area agent, exactly one per workstream definition;
- authorized actors, roles, memberships, and tenant/customer scope;
- workstream icon metadata;
- core-foundation vs domain-specific classification;
- readiness level: `identified`, `described`, `surface-ready`, `capability-ready`, `expertise-ready`, `runtime-ready`, or `production-ready`.

### Attention model

Use `./workstream-attention-contracts.md` for the concrete `AttentionItem`, `WorkstreamAttentionSummary`, producer, idempotency, lifecycle, aggregation, and test contracts. Each workstream must answer the product question `what needs my attention?` for its authorized users.

Record:
- attention categories such as approval, decision, exception, policy conflict, blocked work, overdue item, failed action, SLA risk, audit anomaly, or outcome drift;
- target audience for each category;
- severity and lifecycle expectations: open, acknowledged, resolved, dismissed, expired, escalated;
- whether the item contributes to left rail and My Account counts.

Attention is not merely notification. It is a scoped, authorized, actionable signal that a human or participant may need to inspect, decide, approve, correct, retry, escalate, acknowledge, or learn from something.

### Role-specific dashboard contract

A workstream dashboard is a role-specific situational-awareness surface scoped primarily to one workstream and AuthContext. It is not a generic analytics dashboard; its primary objective is to show what requires this actor's attention and what work can or should be done next. The same workstream may have different dashboard variants for Tenant Admin, Customer Admin, Auditor, SaaS Owner support, or other authorized roles. It should answer:

1. What is happening in this workstream?
2. What needs my attention?
3. What is blocked, overdue, risky, failed, paused, or waiting on a human?
4. Which agents, workflows, users, systems, or autonomous tasks are participating?
5. What decisions, approvals, exceptions, or policy issues are pending?
6. What changed recently?
7. What can this user do next, given their authority?

Record:
- default dashboard purpose and summary cards;
- detailed attention item surfaces reachable from the dashboard;
- cross-workstream references, with the owning workstream preserved;
- My Account aggregate behavior when an item affects the current user across workstreams;
- left rail summary behavior: count, highest severity, hidden/unavailable/zero states.

### My Account and left rail attention

My Account is the main exception to workstream-local dashboard scope. It acts as the current user's aggregate attention inbox across accessible workstreams.

The left rail may show compact workstream attention indicators. The count means:

> There are N items in this workstream that currently require this user's attention, given their identity, tenant/customer context, memberships, roles, permissions, workstream availability, and authority.

Counts must derive from governed backend state/projections, not frontend-only badge logic. In the SaaS Foundation App, this is implemented by the shared backend-owned attention backbone and bounded attention producers: backend state owns actionable `AttentionItem` lifecycle, while workstream dashboards, My Account, and left rail summaries read authorized backend projections. Frontend-only unseen-response badges are transient presentation state, not authoritative attention.

Planning and implementation artifacts should distinguish current SaaS Foundation App coverage from future work:

- attention backbone: shared backend-owned attention items, scoped reads, redaction, lifecycle operations, and traces;
- bounded attention producers: bounded service/timer/task producers with stable producer ids, idempotency, upsert/resolve behavior, and backend-derived refresh/update delivery;
- typed workstream event backbone: typed `WorkstreamEventEnvelope`/source refs, bounded SaaS Foundation App invitation and access-review lifecycle event publication, idempotent event-to-attention consumption, and backend-derived projection-refresh hints;
- future extensions: broader generated-app event coverage, enterprise notification preferences, digests, and real AutonomousAgent durable task notification/lifecycle streams over the governed runtime path.

Initial summary shape:

```ts
type WorkstreamAttentionSummary = {
  workstreamId: string;
  displayName: string;
  attentionCount: number;
  highestSeverity?: "info" | "warning" | "urgent" | "blocked";
  categories?: {
    decisions?: number;
    approvals?: number;
    exceptions?: number;
    blockedRuns?: number;
    policyIssues?: number;
    overdueItems?: number;
    failedActions?: number;
  };
  lastChangedAt?: string;
};
```

### Surface graph

Record the human work tree for each workstream:
- the role-specific dashboard surface is the trunk;
- surface ids, types, payload summaries, empty/loading/error/forbidden/stale states, and accessibility expectations are graph nodes;
- system-message surfaces are graph nodes when the request-based workstream Agent or backend returns structured feedback;
- actions available from each surface are graph edges;
- governed surface-request actions such as `open_workstream`, `open_attention_item`, `request_approval`, `retry_failed_action`, `acknowledge`, `dismiss`, `escalate`, or `start_investigation`;
- edge effects: show another surface, invoke a browser-tool, create a system-message surface, update dashboard attention, start internal-agent work, open traces, or route to approval/decision surfaces;
- realtime or refresh behavior when events, notifications, task snapshots, or views change.

Buttons, links, cards, rail items, and agent suggestions that perform protected work map to governed capabilities and qualified governed-tools. They are not ad hoc frontend jumps.

### Governed capabilities, governed-tools, and APIs

Capabilities remain product-level abilities or groupings. A governed-tool is an executable semantic operation inside a capability boundary and surface/action map. Use qualified terms where ambiguity matters:
- `governed-tool`: semantic executable operation with actor/caller rules, AuthContext, schemas, side effects, idempotency, approval/policy, audit/work trace, and implementation mapping;
- `browser-tool`: governed-tool exposed to humans through protected surface actions and browser APIs;
- `agent-tool`: governed-tool exposed to request-based workstream Agents or internal/AutonomousAgent workers through Akka `@FunctionTool`, MCP, component tools, or equivalent;
- `internal-tool`: governed-tool used by workflows, timers, consumers, projections, or internal services without direct browser exposure.

For each surface action, agent-tool, workflow step, API, timer, consumer reaction, projection read, or internal operation, record:
- capability id and governed-tool id/class: command, query, stream, workflow action, task lifecycle action, projection read, or integration action;
- callers and exposure surfaces: browser-tool, workstream Agent agent-tool, AutonomousAgent agent-tool, Workflow step, Consumer, TimedAction, HTTP/gRPC/MCP endpoint, internal component call;
- input/output schemas and idempotency key rules;
- AuthContext, role/scope checks, tenant/customer isolation, backend authorization, approval gates, policy checks, and fail-closed behavior;
- side effects, emitted events/messages, audit/work trace records, and tests.

Capabilities and their governed-tools are the backend authority boundary. Events, notifications, frontend gating, prompt text, and tool descriptions must not bypass them.

### Internal workstream agent graph

Each workstream should also have an internal virtual dashboard agent view that asks what requires agent attention, what can be handled by internal workers, what should be delegated, and what must be escalated to humans. Record:
- virtual dashboard agent responsibility and attention sources;
- internal worker agents, usually modeled as AutonomousAgent task candidates when durable lifecycle fits;
- delegation edges, inputs, outputs, stop conditions, escalation rules, and result/proposal surfaces;
- governed-tools available to each internal worker and its `ToolPermissionBoundary`;
- human handoff, approval, denial, and trace behavior;
- how worker results update the role-specific dashboard, surface graph, My Account, left rail, events/projections, and audit/work traces.

Internal agent graph design does not grant authority. Every worker action still maps to governed capabilities/governed-tools with AuthContext or service authority, tenant/customer scope, approval/policy checks, idempotency, and audit.

## Akka substrate selection

Select Akka components only after the capability contract and participant roles are clear.

| Need | Prefer |
|---|---|
| immediate user-facing workstream turn, streamed response, bounded explanation, or one-shot model step where the caller owns the turn | request-based Akka `Agent` |
| durable internal/background model-driven investigation, review, evaluation, summary, monitoring/remediation, specialist follow-up, coordination, handoff, team, or moderation work | Akka `AutonomousAgent` |
| deterministic business process authority, ordered steps, retries, compensation, approval pauses, timeouts, and orchestration | Akka `Workflow` |
| deterministic process with one bounded model step | `Workflow` + request-based `Agent` |
| deterministic process that launches or waits on open-ended AI investigation | `Workflow` + `AutonomousAgent` |
| durable non-AI state transition | Entity or Workflow |
| curated dashboard/search/report/evidence read | View |
| event reaction, trace enrichment, publication, integration, or follow-up capability trigger | Consumer |
| reminders, SLA checks, expiry, periodic rechecks, stale task escalation, digest generation | Timed Action |
| selected browser/service/LLM-client boundary | HTTP/gRPC/MCP endpoint |

Request-based Akka `Agent` remains the default for immediate user-facing workstream turns. Akka `AutonomousAgent` is the default candidate for durable model-driven internal/background worker tasks when typed lifecycle, snapshots/results, notifications, dependencies, external completion/failure, cancellation, delegation, handoff, team coordination, or moderation fit. Akka `Workflow` owns deterministic process authority and may launch or wait for an `AutonomousAgent` task.

Autonomous Agents add durable task machinery; they do not grant authority. Every autonomous task start, assignment, query, result read, external complete/fail, suspend/resume/terminate, notification stream, delegated subtask, handoff, team/moderation action, and tool call must map to governed backend capabilities with tenant/customer scope, AuthContext, permission checks, model policy, tool boundaries, approval rules, idempotency, audit/work traces, and fail-closed provider/security configuration.

Terminology guardrail: Akka autonomous `AgentDefinition` is the SDK definition returned by `AutonomousAgent.definition()` or dynamic `AgentSetup`. It is not the skills pack's governed managed-agent `AgentDefinition` domain record for tenant-scoped lifecycle, model refs, prompt/skill/reference manifests, and tool boundaries. Qualify the term whenever both are in scope.

## Events, messages, notifications, and projections

Use a governed workstream event/message backbone. It connects authorized capabilities; it is not a loose global message bus.

Distinguish these families:
- commands/intents requiring authorization, validation, idempotency, policy, and audit;
- domain events recording durable facts;
- work items for humans, workflows, deterministic workers, or agents;
- AutonomousAgent task events, snapshots, results, dependencies, and notifications;
- notifications as progress signals, not authority or source of truth;
- attention signals/projections for dashboards, My Account, and left rail;
- audit/work trace events explaining who or what acted, under which authority, with which evidence, policy, tool boundary, prompt/skill/reference, and outcome.

Autonomous task states can drive surfaces and attention:
- `PENDING`, `ASSIGNED`, `IN_PROGRESS` → progress surfaces by default; attention only when waiting too long or blocked;
- `RESULT_REJECTED`, repeated struggle, dependency stuck → attention item for correction/escalation;
- `COMPLETED` with recommendation, risk, exception, or approval need → result surface, decision card, or attention item;
- `FAILED` or `CANCELLED` → exception attention item and trace link.

Views or equivalent projections should be the authoritative read model for workstream dashboards, My Account aggregate attention, left rail summaries, queues, searches, and reports.

## Anti-drift rules

- No CRUD-first decomposition for broad input: records/entities are implementation details discovered after workstream attention and capability modeling.
- No page-first decomposition: pages/routes are delivery surfaces and deep links, not the root product model.
- No component-first decomposition: Akka components are selected after capability contracts and participant roles are known.
- No event-only backbone: events/messages/notifications connect governed capabilities; they must not bypass authorization, policy, approval, or audit.
- No chatbot-bolt-on default: request-based Agents back functional workstream turns, but workstreams, dashboards, surfaces, actions, and capabilities remain first-class.
- No capability-without-workstream shortcut for broad generated-SaaS input: capabilities are backend authority contracts discovered through workstream surfaces/actions unless the task is explicitly narrow infrastructure or component work.
- No AutonomousAgent omission: durable model-driven background work must be evaluated for Akka `AutonomousAgent`; request-based `Agent` remains the default only for immediate bounded user-facing turns.
- No deterministic/demo/model-less runtime substitute: normal generated-app runtime paths for auth, agents, tools, capabilities, traces, and provider calls must fail closed when configuration is missing.

## Task and validation expectations

Backlogs and pending tasks generated from requirements must preserve:
- workstream id and attention category;
- dashboard/surface state or action;
- governed capability id, auth/scope, schema, side effects, idempotency, approval/policy, and audit;
- selected Akka substrate and exposure channel;
- request-based Agent or AutonomousAgent semantics where applicable;
- events/notifications/projections;
- tests and local runtime/API/UI validation.

Required verification themes:
- tenant/customer isolation and forbidden access;
- backend authorization and role/scope denial;
- approval/policy gate behavior;
- attention count/projection correctness;
- dashboard/surface action wiring to capabilities;
- AutonomousAgent lifecycle/result/notification behavior when applicable;
- request-based Agent tool boundary and governed runtime path when applicable;
- audit/work trace linkage;
- local runtime/API/UI smoke checks for named implemented features.
