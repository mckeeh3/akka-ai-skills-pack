# Workstream dashboards, attention, and event backbone — provenance notes

## Status

Superseded as the primary rule source by `docs/requirements-to-workstream-development-process.md`.

This document remains as provenance, design detail, open-question backlog, and source material for future focused docs. For normative intake, PRD, app-description, planning, backlog, and implementation-readiness guidance, use the canonical requirements-to-workstream process doc first.

Implementation status for starter/reference assets:

- v1 shared attention backbone is implemented at starter scope: backend-owned `AttentionItem` lifecycle, scoped workstream/My Account/rail projections, redaction, and audit/work traces.
- v2 bounded attention producers are implemented at starter scope: invitation delivery, governance approval, timed invitation checks, worker/task blocked or review-needed states, and backend-derived refresh/update delivery.
- v3 governed event backbone is implemented at bounded starter scope: typed `WorkstreamEventEnvelope`/`WorkstreamEventSourceRef`, Akka-backed event repository seam, event publication for starter invitation and access-review lifecycle states, idempotent event-to-attention consumer behavior, and backend-derived projection-refresh hints.
- This provenance document may still discuss broader conceptual event/message backbones, notifications, digests, and AutonomousAgent task streams. Treat those as future work unless a current starter contract or test proves the specific runtime path. In particular, broad real AutonomousAgent durable task runtime integration remains the recommended next initiative after v3.

Related current doctrine:
- `docs/requirements-to-workstream-development-process.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/workstream-ui-reference-architecture.md`
- `docs/agent-component-selection-guide.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-tasks/SKILL.md`
- `skills/akka-autonomous-agent-coordination/SKILL.md`
- `skills/akka-autonomous-agent-governance/SKILL.md`

## Core insight

A generated AI-first SaaS app can infer much of its initial product and implementation shape from this expansion chain:

```text
workstream
→ dashboard
→ surfaces
→ backend capabilities
→ request-based workstream agent turns
→ internal/background autonomous tasks
→ events/messages/notifications
→ attention items
→ audit/work traces
→ Akka component realization
```

This is significant because it lets the skills pack generate an initial, useful workstream implementation from a small amount of user input. The user should not need to enumerate every screen, endpoint, entity, workflow, event, agent, task, or table. Once a workstream exists, the pack can infer the dashboard, supporting surfaces, backend capabilities, participant agents, autonomous tasks, attention model, traces, and implementation tasks.

Akka `AutonomousAgent` materially strengthens this concept. It gives the pack a first-class Akka substrate for what this discussion previously called "backend agents": durable internal/background AI participants that own typed task lifecycles, snapshots, results, notifications, dependencies, cancellation/failure, and optional delegation/handoff/team/moderation coordination. Request-based Akka `Agent` remains the default for user-facing workstream turns; `AutonomousAgent` becomes the default candidate for durable model-driven background work that outlives a single turn.

## Dashboard meaning

A dashboard is a situational-awareness surface. It helps a user quickly understand what is happening, what needs attention, and what actions are available.

A workstream dashboard is not a generic data table or global BI page. It is a structured surface scoped primarily to one workstream.

A workstream dashboard should generally answer:

1. What is happening in this workstream?
2. What needs my attention?
3. What is blocked, overdue, risky, failed, paused, or waiting on a human?
4. Which agents, workflows, users, or systems are participating?
5. What decisions, approvals, exceptions, or policy issues are pending?
6. What changed recently?
7. What can this user do next, given their authority?

## Workstream scoping rule

For most workstreams, the dashboard focuses only on things directly related to that specific workstream.

The dashboard may be aware of or reference other workstreams when relevant, but it should not become a broad cross-application operations page by default.

Examples:
- A Customer Support dashboard can reference a Governance/Policy approval if a support action is blocked by policy.
- A Procurement dashboard can reference an Audit/Trace item if a purchase approval is under review.
- A User Admin dashboard can reference Agent Admin if a user-permission change affects an agent's tool boundary.

Cross-workstream awareness is allowed, but each normal workstream owns its own primary attention model.

## Special case: My Account dashboard

The My Account workstream dashboard is the main exception to the workstream-local dashboard rule.

Its main objective is to orient the current user across all workstreams available to them. It should show a high-level count of things needing the user's attention in each accessible workstream.

My Account acts as a personal attention inbox or command center:

```text
My Account
  Profile and settings shortcuts
  Personal queue
  Available workstreams:
    User Admin             3 needing attention
    Agent Admin            1 needing attention
    Audit/Trace            4 needing attention
    Governance/Policy      2 needing attention
    Customer Support       7 needing attention
```

Selecting a workstream status panel should open the target workstream dashboard or a focused attention surface through a governed surface-request action.

## Left rail attention indicator

The existing workstream attention indicator in the left rail can be extended into a persistent triage surface by showing the number of items needing attention for each workstream.

Conceptual rail:

```text
User Admin          3
Agent Admin         1
Audit/Trace         4
Governance/Policy   2
Customer Support    7
```

The count should mean:

> There are N items in this workstream that currently require this user's attention, given their identity, tenant/customer context, memberships, roles, permissions, workstream availability, and authority.

Design implications:

- Counts are user-specific, not just global workstream health.
- Counts reflect actionable attention, not every warning, event, or unread item.
- Counts should derive from governed backend state/projections, not frontend-only badge logic. The starter's implemented v1/v2 path uses backend attention projections and bounded producers for actionable attention; frontend-only rail badges remain transient presentation state.
- Clicking a rail item should open that workstream, ideally showing its default dashboard with attention items visible or prioritized.
- My Account provides the explanatory aggregate view; the left rail provides the compact persistent view.
- A count can be zero, hidden, or unavailable depending on authorization and context.

Initial contract sketch:

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

The compact summary can feed:
- left rail count badges;
- My Account aggregate dashboard;
- workstream switchers/status panels;
- digest or briefing surfaces.

Detailed attention items feed the individual workstream dashboard.

## Attention as a first-class concept

The phrase "things needing your attention" is a powerful product concept and should become a first-class architecture object.

An attention item is not merely a notification. It is a scoped, authorized, actionable signal that some human or participant may need to inspect, decide, approve, correct, retry, escalate, acknowledge, or learn from something.

Possible attention item categories:

- approval requested;
- decision needed;
- exception raised;
- policy conflict;
- work blocked;
- workflow paused;
- agent run failed;
- SLA or deadline risk;
- overdue human response;
- unresolved customer issue;
- security or authorization denial needing review;
- audit anomaly;
- governance proposal awaiting review;
- outcome metric drift.

Attention items should preserve:

- owning workstream;
- target user/role/group authority;
- tenant/customer scope;
- severity and reason;
- source event/message/trace ids;
- linked autonomous task id when applicable;
- linked surface or capability action;
- lifecycle state such as open, acknowledged, resolved, dismissed, or expired;
- audit/work trace linkage.

## Autonomous Agents and the backend-agent gap

Akka `AutonomousAgent` should become the default interpretation of many durable "backend agent" responsibilities in generated apps.

Use request-based Akka `Agent` for:
- user-facing workstream request/response turns;
- immediate or streamed `markdown_response` and structured surface generation;
- one bounded model step inside a workflow or endpoint;
- model work where the caller owns the turn and expects a direct result.

Use Akka `AutonomousAgent` for:
- internal/background model-driven work that outlives the initiating request;
- investigations, reviews, evaluations, summaries, monitoring/remediation, or specialist follow-up with typed results;
- task lifecycle, cancellation/failure, snapshots, notification streams, dependencies, or external completion;
- model-driven delegation, handoff, teams, or moderation.

Use Akka `Workflow` for:
- deterministic business process authority;
- explicit step order, retries, compensation, approval pauses, timeouts, and state transitions;
- launching or waiting for autonomous tasks when the business process owns orchestration.

Important guardrail: Autonomous Agents add durable task machinery; they do not grant authority. Every autonomous task start, assignment, query, result read, external complete/fail, suspend/resume/terminate, notification stream, delegated subtask, handoff, team/moderation action, and tool call must map to governed backend capabilities with tenant/customer scope, AuthContext, permission checks, model policy, tool boundaries, approval rules, idempotency, audit/work traces, and fail-closed provider/security configuration.

Terminology guardrail: Akka autonomous `AgentDefinition` is the SDK definition returned by `AutonomousAgent.definition()` or dynamic `AgentSetup`. It is not the skills pack's governed managed-agent `AgentDefinition` domain record for tenant-scoped lifecycle, model refs, prompt/skill/reference manifests, and tool boundaries. Qualify the term whenever both are in scope.

## Event/message backbone concept

A central event/message backbone can drive the app, provided it is typed, governed, tenant-aware, auditable, and capability-oriented.

It should not be a loose global message soup. It should be a governed workstream event/message backbone where human users and application participants produce and consume messages through authorized capabilities.

Participants include:

- human users;
- request-based functional/context-area agents backing user-facing workstream turns;
- Akka `AutonomousAgent` components for durable internal/background model-driven tasks;
- deterministic non-AI agents/workers executing well-defined business logic;
- workflows;
- consumers;
- timed actions;
- integrations and external systems.

Use this working distinction:

```text
Functional workstream agent
  = user-facing, left-rail, request/response Akka Agent

Internal/background AI participant
  = Akka AutonomousAgent when the work is durable, task-oriented, observable, cancellable, or coordinated

Workflow
  = deterministic business-process authority and orchestration

Event/message backbone
  = how humans, agents, workflows, consumers, timers, and integrations produce and consume commands, facts, tasks, notifications, attention, and traces
```

The bus should connect governed capabilities, not bypass them.

For example, an AI agent should not directly mutate arbitrary state because it saw an event. A request-based workstream agent should usually interpret the user's turn and invoke a governed capability. An autonomous agent should consume or be assigned a typed task, reason or propose/request an action, then invoke authorized tools/capabilities that validate authority, apply policy, change state, and emit durable facts/traces.

## Message families

The backbone should distinguish at least these families:

### Commands / intents

Requests to do something. These require authorization, validation, idempotency, and policy checks.

Examples:
- `CreateCustomerIssue`
- `AssignIssue`
- `RequestAgentInvestigation`
- `ApproveResolution`
- `PauseWorkstreamRun`

### Domain events

Facts that happened. These are durable and auditable, and they drive downstream projections/reactions.

Examples:
- `CustomerIssueCreated`
- `IssueAssigned`
- `AgentInvestigationCompleted`
- `ResolutionApproved`
- `SlaRiskDetected`

### Work items / tasks

Things a participant should act on.

Examples:
- support user should review a proposed response;
- AI agent should inspect account history;
- manager approval is required;
- customer follow-up is overdue.

### Autonomous agent tasks

Durable typed tasks owned by an Akka `AutonomousAgent`. These are the concrete runtime form for many internal/background AI participants.

Examples:
- `InvestigateCustomerIssueTask` with typed `InvestigationResult`;
- `ReviewAccessRiskTask` with typed `AccessRiskFindings`;
- `SummarizeAuditWindowTask` with typed `AuditSummary`;
- `EvaluatePolicyProposalTask` with typed `PolicyEvaluation`.

Autonomous tasks add lifecycle state (`PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESULT_REJECTED`, `COMPLETED`, `FAILED`, `CANCELLED`), typed snapshots/results, dependencies, external completion/failure, notification streams, and optional delegation/handoff/team/moderation semantics. They are not just messages; every task start/query/result/notification/lifecycle operation is a governed backend capability.

### Notifications

Progress signals from workflows, autonomous agents, task entities, consumers, timers, or integrations.

Autonomous agent notifications are especially useful for dashboard progress, logs, task-detail surfaces, and realtime UI updates. They should not be the business source of truth; task snapshots, workflow/entity state, or views remain authoritative for decisions and attention counts.

### Attention signals

User-facing attention records or projections derived from events, autonomous task state, work items, policies, deadlines, notifications, and traces.

Examples:
- left rail count: `Customer Support: 4`;
- dashboard card: `2 approvals pending`;
- My Account summary: `7 items need attention across 3 workstreams`.

Attention summaries should usually be projections. Detailed attention items may also need durable lifecycle state when acknowledgement, assignment, escalation, dismissal, or resolution matters.

Autonomous task states can directly drive attention:

```text
PENDING / ASSIGNED / IN_PROGRESS
  → progress surface, usually not attention unless waiting too long

RESULT_REJECTED / repeated struggle / dependency stuck
  → attention item for review, correction, or escalation

COMPLETED with recommendation, risk, exception, or approval need
  → result surface, decision card, or attention item

FAILED / CANCELLED
  → exception attention item and trace link
```

### Audit and work trace events

Governance records explaining who or what did what, why, under which authority, with which evidence, policy, tool boundary, prompt/skill/reference, and outcome.

Examples:
- human accepted an AI recommendation;
- agent used a tool;
- policy allowed or denied an action;
- prompt/skill/reference was loaded;
- workflow transition occurred;
- cross-workstream reference was created.

## Example: customer issue workflow

A customer issue workflow illustrates the backbone:

1. A customer issue is created.
2. A domain event records the issue creation.
3. A classifier agent or deterministic classifier categorizes severity and topic.
4. A workflow starts investigation and resolution tracking.
5. A support user comments, assigns priority, or requests agent investigation.
6. A governed capability starts a `CustomerIssueInvestigationAutonomousAgent` task and returns a task/progress surface.
7. The autonomous agent gathers evidence, calls authorized read/evidence tools, may delegate to specialist autonomous agents, and emits task/agent notifications.
8. A deterministic SLA rule checks deadline risk.
9. The autonomous task completes with a typed recommendation/result, or fails/gets stuck and emits an exception path.
10. A policy check determines whether a proposed response requires approval.
11. A work item or attention item is created for the responsible human.
12. The Customer Support dashboard updates with task progress/result/decision surfaces.
13. The left rail count updates for authorized users.
14. The My Account dashboard updates aggregate attention counts.
15. Audit/work traces record human actions, autonomous task lifecycle, agent reasoning/tool use, policy checks, denials, approvals, and workflow transitions.

## Akka realization fit

This concept maps naturally onto Akka:

- Event Sourced Entities own durable state and emit domain facts.
- Workflows coordinate deterministic long-running processes, approvals, retries, pauses, resumes, compensations, and process authority.
- Autonomous Agents own durable model-driven internal/background task loops with typed tasks, snapshots/results, dependencies, notification streams, failure/cancellation, and optional delegation/handoff/team/moderation.
- Request-based Agents perform bounded user-facing workstream turns, immediate or streamed responses, and one-shot classification, summarization, planning, recommendation, evaluation, or explanation through governed tools/capabilities.
- Consumers react to events and trigger follow-up capabilities, projections, traces, task starts, notifications, or integrations.
- Views project events, state, workflow progress, task snapshots/results, and attention lifecycle into dashboards, attention summaries, queues, searches, and reports.
- Timed Actions handle reminders, SLA checks, expiry, digest generation, periodic rechecks, and stale autonomous task escalation.
- HTTP/gRPC/MCP endpoints expose selected capabilities while preserving authorization and audit.
- React/Vite/TypeScript surfaces render dashboard, attention, task progress, task result, detail, decision, audit, workflow, and outcome contracts.

Selection rule:

| Need | Prefer |
|---|---|
| immediate user-facing workstream turn or streamed response | request-based Akka `Agent` |
| durable model-driven investigation/background task | Akka `AutonomousAgent` |
| deterministic long-running business process | Akka `Workflow` |
| deterministic process with one bounded LLM step | `Workflow` + request-based `Agent` |
| deterministic process that launches or waits on open-ended AI investigation | `Workflow` + `AutonomousAgent` |
| quick deterministic reaction to an event | `Consumer` or `TimedAction` |
| durable non-AI state transition | Entity or Workflow |

Autonomous Agents fill the previous "backend agent" gap, but they do not replace workstream functional agents or governed capability modeling.

## Surface-first workstream decomposition

This discussion suggests a surface-first decomposition path for the skills pack:

```text
1. Identify the workstream and its responsibility.
2. Define its default dashboard/attention surface.
3. Expand the dashboard into specific surfaces.
4. Expand surfaces into allowed actions and events.
5. Map each action/event to governed backend capabilities.
6. Identify request-based workstream agent turns, internal autonomous tasks, deterministic workers, workflows, consumers, and timers that participate.
7. Identify commands, domain events, autonomous task definitions/results, notifications, work items, attention items, and traces.
8. Select Akka components to realize the capability contracts, using `docs/agent-component-selection-guide.md` when choosing between request-based `Agent`, `AutonomousAgent`, `Workflow`, `Workflow + Agent`, and `Workflow + AutonomousAgent`.
9. Generate implementation and tests component by component.
```

This complements capability-first backend design. The dashboard/surface layer gives product shape; the capability layer turns that shape into enforceable backend contracts; Akka components realize those contracts.

## Inference opportunity for the skills pack

Given the five core workstreams, a skills-pack agent should be able to define and implement an initial baseline without detailed user input.

For each core workstream, the agent can infer:

- default dashboard purpose;
- attention categories;
- initial surfaces;
- user and agent intents;
- request-based workstream agent behavior;
- internal autonomous agents and deterministic workers;
- autonomous task definitions, typed result DTOs, task rules, dependencies, and notification needs;
- commands/events/work items/attention items;
- governed capabilities for task start/query/result/notification/lifecycle operations;
- audit/work trace requirements;
- views/projections over domain state, workflow state, task snapshots/results, notifications, and attention lifecycle;
- Akka component candidates;
- frontend shell and surface contracts;
- tests.

The generated baseline should be explicitly labeled as an initial inferred operating model. Users can then refine naturally:

- "Add a customer support workstream."
- "Show SLA risk on the dashboard."
- "Let the AI draft but not send customer replies."
- "Require manager approval before external responses."
- "Escalate unresolved cases after 24 hours."
- "Make the left rail badge count only items assigned to me."

The pack can route those changes into workstreams, dashboards, surfaces, capabilities, request-based workstream agent turns, autonomous agent tasks, workflows, events, notifications, attention projections, traces, and implementation tasks.

## Initial inferred core-workstream dashboard directions

These are tentative and should be refined.

### My Account

Primary goal: answer "what do I need to do next?" across accessible workstreams.

Likely dashboard content:
- profile/settings shortcuts;
- selected context and authority summary;
- personal queue;
- per-workstream attention counts;
- recent important activity;
- autonomous task status summaries where the current user is a requester, approver, assignee, or reviewer;
- sign-out/security/account state surfaces.

Potential autonomous tasks:
- personal briefing generation;
- cross-workstream attention digest;
- stale task/attention explanation summaries.

### User Admin

Primary goal: manage people, access, invitations, memberships, roles, and access review.

Likely dashboard content:
- pending invitations;
- users requiring review;
- disabled/pending accounts;
- role or membership changes needing approval;
- access review findings;
- recent admin audit items.

Potential autonomous tasks:
- access-review investigation;
- admin-risk batch review;
- role recommendation draft;
- invitation or onboarding issue analysis;
- support-access review summary.

### Agent Admin

Primary goal: manage agent definitions, prompts, skills, references, manifests, tool boundaries, lifecycle, behavior tests, and behavior-change approvals.

Likely dashboard content:
- agent definitions needing review;
- prompt/skill/reference proposals;
- tool-boundary changes;
- behavior test failures;
- model/provider configuration issues;
- autonomous task queues and failures for internal/background agents;
- recent agent work traces.

Potential autonomous tasks:
- behavior regression evaluation;
- prompt/skill/reference change review;
- tool-boundary impact simulation;
- model/provider readiness investigation;
- specialist review of failed autonomous tasks.

### Audit/Trace

Primary goal: investigate what happened, who/what did it, why, and under what authority.

Likely dashboard content:
- recent security/audit events;
- authorization denials;
- tool/data access traces;
- policy decisions;
- high-risk or anomalous traces;
- investigation shortcuts.

Potential autonomous tasks:
- audit-window summarization;
- anomaly investigation;
- trace correlation and explanation;
- failed-task postmortem draft;
- access-denial pattern analysis.

### Governance/Policy

Primary goal: manage policies, thresholds, approval rules, proposals, simulations, and governed behavior changes.

Likely dashboard content:
- policy proposals awaiting review;
- approval rule conflicts;
- simulations needing attention;
- exceptions/deviations;
- recent policy invocations;
- outcome feedback that may require policy updates.

Potential autonomous tasks:
- policy proposal evaluation;
- simulation/replay analysis;
- exception/deviation clustering;
- outcome-feedback review;
- approval-rule conflict investigation.

## Open design questions

1. Should attention items be a durable entity, a projection over domain/workflow/autonomous-task events, or both depending on lifecycle needs?
2. What is the canonical schema for `WorkstreamAttentionSummary` and detailed `AttentionItem`?
3. How should attention counts handle role-based visibility, group assignment, delegated authority, shared responsibility, and autonomous tasks requested by one user but assigned to another role?
4. Should the left rail show only counts, or also severity/color/state indicators?
5. How should cross-workstream references be represented without violating workstream-local dashboard focus?
6. Which events/messages are application-level backbone contracts versus domain-specific contracts?
7. How should deterministic non-AI agents be represented: internal agents, workers, consumers, timed actions, workflows, or capability executors?
8. What minimum event/message/autonomous-task backbone should the five core workstream starter include?
9. Which views/projections power left rail counts, My Account aggregate dashboard, and individual workstream dashboards?
10. How should realtime updates, stale markers, reconnect behavior, autonomous task notifications, and authoritative task snapshots affect attention counts and dashboard surfaces?
11. Which autonomous task lifecycle states should count as attention by default, and which should only render progress?
12. How should workflow-owned approval pauses differ from autonomous-agent external-input task dependencies?
13. What app-description layer should own autonomous task definitions, task result DTOs, task rules, and notification-to-surface mappings?

## Possible next steps

- Promote this WIP into canonical doctrine or split it into focused docs:
  - workstream dashboard doctrine;
  - attention model doctrine;
  - event/message/autonomous-task backbone doctrine;
  - dashboard-to-surface-to-capability inference flow.
- Update `docs/agent-workstream-application-architecture.md` with the dashboard scoping rule, My Account exception, and dashboard/task awareness guidance.
- Update `docs/structured-surface-contracts.md` with `dashboard`, `attention`, `autonomous_task_progress`, and `autonomous_task_result` surface contract guidance.
- Update `docs/workstream-ui-reference-architecture.md` with left rail attention count and autonomous task progress/result contract details.
- Add app-description guidance for dashboard/surface expansion, attention contracts, autonomous task definitions, notification mappings, and task result surfaces under `12-workstreams/`.
- Add skills or skill sections that teach agents to infer workstream dashboards, surfaces, request-based workstream agent turns, autonomous tasks, events/messages/notifications, capabilities, and tests from a workstream name/responsibility.
- Add a core-starter follow-up plan for the minimum autonomous-task backbone: task start/query/result/notification capabilities, dashboard progress surfaces, attention projection rules, and tests.
