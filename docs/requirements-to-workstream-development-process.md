# Requirements-to-workstream development process

## Status

Canonical process doctrine for generated secure AI-first SaaS app intake, PRD processing, app-description maintenance, decomposition, backlog generation, and implementation planning.

Use this doc when broad product input must become a workstream-centered application model before Akka components are selected. It promotes the requirements-to-workstream process from `docs/workstream-dashboard-attention-event-backbone-wip.md`; keep that WIP as provenance and idea backlog, not as the primary rule source.

Related doctrine:
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-component-selection-guide.md`
- `docs/workstream-ui-reference-architecture.md`

## Core rule

For generated secure AI-first SaaS apps, process broad requirements through workstreams before pages, CRUD resources, endpoint lists, database tables, event streams, or Akka component families.

Default order:

```text
input / PRD / feature request
→ secure SaaS foundation and tenant/customer/AuthContext assumptions
→ workstream inventory
→ per-workstream attention breakdown: "what needs my attention?"
→ default dashboard and attention summary contract
→ structured surfaces, surface states, and surface actions
→ governed capability/API contracts
→ selected Akka substrate per capability
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
→ dashboard or surface state/action
→ governed capability/API
→ selected Akka substrate and participant
→ events/notifications/projections
→ audit/work trace
→ tests and local validation
```

If a link is missing, create bounded follow-up questions or tasks instead of claiming readiness.

## Required fields to preserve

Artifacts may keep only the smallest subset relevant to their scope, but decomposition, app-description, specs, backlog, queue, and implementation tasks must not erase these semantics.

### Workstream identity

Record:
- `workstreamId`, display name, and responsibility;
- owner functional/context-area agent;
- authorized actors, roles, memberships, and tenant/customer scope;
- core-foundation vs domain-specific classification.

### Attention model

Each workstream must answer the product question `what needs my attention?` for its authorized users.

Record:
- attention categories such as approval, decision, exception, policy conflict, blocked work, overdue item, failed action, SLA risk, audit anomaly, or outcome drift;
- target audience for each category;
- severity and lifecycle expectations: open, acknowledged, resolved, dismissed, expired, escalated;
- whether the item contributes to left rail and My Account counts.

Attention is not merely notification. It is a scoped, authorized, actionable signal that a human or participant may need to inspect, decide, approve, correct, retry, escalate, acknowledge, or learn from something.

### Dashboard contract

A workstream dashboard is a situational-awareness surface scoped primarily to one workstream. It should answer:

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

Counts must derive from governed backend state/projections, not frontend-only badge logic.

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

### Structured surfaces and actions

Record:
- surface ids, types, payload summaries, empty/loading/error/forbidden/stale states, and accessibility expectations;
- system-message surfaces when the request-based workstream Agent returns structured results;
- actions available from each surface;
- governed surface-request actions such as `open_workstream`, `open_attention_item`, `request_approval`, `retry_failed_action`, `acknowledge`, `dismiss`, `escalate`, or `start_investigation`;
- realtime or refresh behavior when events, notifications, task snapshots, or views change.

Buttons, links, cards, rail items, and agent suggestions that perform protected work map to governed capabilities. They are not ad hoc frontend jumps.

### Governed capabilities and APIs

For each surface action, agent tool, workflow step, API, timer, consumer reaction, projection read, or internal operation, record:
- capability id and class: command, query, stream, tool, workflow action, task lifecycle action, projection read, or integration action;
- callers and exposure surfaces: browser, workstream Agent tool, AutonomousAgent tool, Workflow step, Consumer, TimedAction, HTTP/gRPC/MCP endpoint, internal component call;
- input/output schemas and idempotency key rules;
- AuthContext, role/scope checks, tenant/customer isolation, backend authorization, approval gates, policy checks, and fail-closed behavior;
- side effects, emitted events/messages, audit/work trace records, and tests.

Capabilities are the backend authority boundary. Events, notifications, frontend gating, prompt text, and tool descriptions must not bypass them.

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
