# Target Process Contract: Requirements to Workstreams

## Purpose

This contract defines the expected shape for future skills, docs, examples, planning artifacts, backlogs, and pending tasks that process broad generated-app input. It is a mini-project planning artifact, not yet canonical installed-pack doctrine.

Generated secure AI-first SaaS input must be processed through workstreams, attention, dashboards, structured surfaces, governed capabilities, selected Akka substrates, events/notifications, projections, and audit/work traces before implementation tasks are considered ready.

## Mandatory processing order

For any broad user input, PRD, feature request, revised requirement, app-description change, or planning prompt for a generated secure AI-first SaaS app, use this order by default:

```text
input / PRD / feature request
→ secure SaaS foundation and selected tenant/customer/AuthContext assumptions
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

Do not start from CRUD resources, pages, endpoint lists, Akka component families, database tables, or event streams when the input is broad enough to infer workstreams.

## Required output fields

Every downstream artifact that claims to decompose, plan, backlog, queue, or implement a generated SaaS feature should preserve the smallest relevant subset of these fields.

### 1. Workstream identity

- `workstreamId`
- display name and responsibility
- owner functional/context-area agent
- authorized actors, roles, memberships, and tenant/customer scope
- whether the workstream is core foundation or domain-specific

### 2. Attention model

- attention question answered by the workstream, usually `what needs my attention?`
- user/role-specific attention categories, such as approval, decision, exception, policy conflict, blocked work, overdue item, failed action, SLA risk, audit anomaly, or outcome drift
- target audience for each category
- severity and lifecycle expectations: open, acknowledged, resolved, dismissed, expired, escalated
- whether an item contributes to left rail and My Account counts

### 3. Dashboard contract

- default dashboard purpose and summary cards
- detailed attention item surfaces reachable from the dashboard
- cross-workstream references, if any, with the owning workstream preserved
- My Account aggregate behavior when the item affects the current user across workstreams
- left rail summary behavior: count, highest severity, unavailable/hidden/zero states

### 4. Structured surfaces and actions

- surface ids, types, payload summaries, empty/loading/error states, and accessibility expectations
- system-message surfaces when the request-based workstream Agent returns structured results
- actions available from each surface
- governed surface-request actions, such as `open_workstream`, `open_attention_item`, `request_approval`, `retry_failed_action`, `acknowledge`, `dismiss`, `escalate`, or `start_investigation`
- realtime or refresh behavior when events, notifications, task snapshots, or views change

### 5. Governed capabilities and APIs

- capability id and class: command, query, stream, tool, workflow action, task lifecycle action, projection read, or integration action
- callers and exposure surfaces: browser, workstream Agent tool, AutonomousAgent tool, Workflow step, Consumer, TimedAction, HTTP/gRPC/MCP endpoint, internal component call
- input/output schemas and idempotency key rules
- AuthContext, role/scope checks, tenant/customer isolation, backend authorization, approval gates, policy checks, and fail-closed behavior
- side effects, emitted events/messages, audit/work trace records, and tests

### 6. Akka substrate selection

Select the Akka substrate only after the capability contract is known.

- Use request-based Akka `Agent` for immediate user-facing workstream turns, streamed responses, bounded recommendations, explanations, and one-shot model steps where the caller owns the turn.
- Use Akka `AutonomousAgent` for durable internal/background model-driven tasks with typed lifecycle, snapshots/results, notifications, dependencies, external completion/failure, cancellation, delegation, handoff, team coordination, or moderation.
- Use Akka `Workflow` for deterministic process authority, ordered steps, retries, compensation, pauses, approvals, timeouts, and orchestration. A Workflow may launch or wait for an `AutonomousAgent` task when deterministic business authority owns the process.
- Use Entities for durable non-AI state, Views for projections/search/dashboard reads, Consumers for event reactions, TimedActions for reminders/SLA/stale checks, and endpoints for selected external exposure.

### 7. Request-based workstream Agent turns

For user-facing workstream messages, record:

- functional agent id and prompt intent
- allowed tools/capabilities and `ToolPermissionBoundary`
- expected response surface types, including `markdown_response` only when that is the selected surface, not as a substitute for the application model
- prompt/skill/reference trace requirements
- provider/model policy and fail-closed missing-configuration behavior

### 8. Internal/background worker candidates

For each durable worker candidate, record whether it is:

- an Akka `AutonomousAgent` task;
- a deterministic Workflow/Consumer/TimedAction/service step;
- a request-based Agent step inside a bounded process; or
- explicitly deferred.

For each `AutonomousAgent` candidate, preserve:

- task id/type and typed input/result DTOs
- lifecycle states and cancellation/failure behavior
- snapshots, notifications, and progress/result surfaces
- dependencies, external completion/failure, handoff, delegation, team, or moderation needs
- capabilities for start/query/result/read notification/suspend/resume/terminate operations
- linked attention categories and audit/work traces

### 9. Events, messages, notifications, and projections

Record:

- command/intention sources
- domain events and work items
- AutonomousAgent task events, snapshots, results, and notifications
- notification-to-surface behavior
- attention projection updates for workstream dashboards, My Account, and left rail
- authoritative source of truth for counts and lifecycle state
- stale/reconnect/retry behavior for realtime UI where relevant

Notifications are progress signals, not authority. Events and task state may feed projections, but governed capabilities remain the authority boundary for changes.

### 10. Audit/work traces and tests

Every artifact should identify tests and validation for:

- tenant/customer isolation and forbidden access
- backend authorization and role/scope denial
- approval/policy gate behavior
- attention count/projection correctness
- dashboard/surface action wiring to capabilities
- AutonomousAgent lifecycle/result/notification behavior when applicable
- request-based Agent tool boundary and governed runtime path when applicable
- audit/work trace linkage
- local runtime/API/UI smoke checks for named implemented features

## Anti-drift rules

- **No CRUD-first decomposition**: records and entities are implementation details discovered after workstream attention and capability modeling.
- **No page-first decomposition**: pages/routes are delivery surfaces, not the root product model.
- **No component-first decomposition**: Akka components are selected after capability contracts and participant roles are known.
- **No event-only backbone**: events/messages/notifications connect governed capabilities; they must not bypass authorization, policy, approval, or audit.
- **No chatbot-bolt-on default**: request-based Agents back functional workstream turns, but workstreams, dashboards, surfaces, actions, and capabilities remain first-class.
- **No capability-without-workstream shortcut for broad input**: capabilities are backend authority contracts discovered through workstream surfaces/actions unless the task is explicitly narrow infrastructure or component work.
- **No AutonomousAgent omission**: durable model-driven background work must be evaluated for Akka `AutonomousAgent`; request-based `Agent` remains the default only for immediate bounded user-facing turns.
- **No deterministic/demo/model-less runtime substitute**: normal generated-app runtime paths for auth, agents, tools, capabilities, traces, and provider calls must fail closed when configuration is missing.

## Implementation readiness rule

A planning output is ready to become implementation tasks only when each vertical slice can be traced as:

```text
workstream attention category
→ dashboard or surface state/action
→ governed capability/API
→ selected Akka substrate and participant
→ events/notifications/projections
→ audit/work trace
→ tests and local validation
```

If any link is missing, create bounded follow-up questions or tasks before claiming implementation readiness.
