# Audit / Trace Workstream PRD

## PRD identity

- **Workstream id:** `audit_trace`
- **Backing functional agent:** `functional_agent.audit_trace`
- **Domain:** `ai_first_saas_core_app`
- **Purpose:** investigate what happened, who/what authorized it, which evidence/policies/tools were used, and what outcomes resulted
- **Primary users:** auditors, tenant admins with audit capability, support operators with explicit support access, governance admins for behavior traces

## Invariants

```text
This workstream is backed by exactly one functional/context-area agent.
Surfaces are the only renderable workstream artifacts.
System messages are typed surfaces.
Every surface action, including read/query and surface-request actions, maps to a governed backend capability.
The workstream agent may request surfaces and guide users, but backend capabilities enforce authority.
```

Audit/trace read access is itself sensitive and must be scoped, redacted, and audited.

## User intents

The workstream agent must handle:

- `dashboard`, `show audit dashboard`
- `search audit`, `find denials for Alex`, `show role changes yesterday`
- `open trace`, `explain this trace`, `why was this action denied`
- `show prompt assembly trace`, `show skill load trace`, `show tool calls`
- `show data access for this user`, `show support access events`
- `export audit records`, `request export approval`
- `summarize suspicious activity`, `what changed before this outcome`
- help/how-to questions for audit investigation and trace interpretation

The agent summarizes and explains evidence but cannot hide, alter, or delete audit records.

## Required surfaces

| Surface id | Type | Purpose | Producing capability | Primary actions |
|---|---|---|---|---|
| `surface.audit_trace.dashboard.v1` | dashboard | audit health, recent denials, high-risk activity, trace volume, pending exports | `audit_trace.dashboard.view` | search audit, open denial queue, open exports |
| `surface.audit_trace.search.v1` | data_table/search | scoped audit/work-trace search | `audit_trace.records.search` | open record, filter, page, save query |
| `surface.audit_trace.record_detail.v1` | detail_card | one audit event with actor, target, action, decision, auth basis, redactions | `audit_trace.records.view` | open related trace, open actor/target, copy citation |
| `surface.audit_trace.timeline.v1` | audit_timeline | correlated sequence across audit/work/policy/tool/data events | `audit_trace.timeline.view` | open event, expand evidence, summarize |
| `surface.audit_trace.agent_work_trace.v1` | audit_timeline/detail | AgentWorkTrace with prompt assembly, tools, evidence, decisions, redactions | `audit_trace.agent_work.view` | open prompt assembly, open skill/reference loads, open tool call |
| `surface.audit_trace.prompt_skill_trace.v1` | audit_timeline/detail | PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace | `audit_trace.prompt_skill.view` | open document/version, open manifest, open boundary |
| `surface.audit_trace.data_access.v1` | data_table/timeline | sensitive data reads by actor/tool/workflow/support | `audit_trace.data_access.search` | open event, export request |
| `surface.audit_trace.export_request.v1` | form/workflow_status | request/export audit evidence with approval/redaction | `audit_trace.exports.form` | request export, check status, download approved export |
| `surface.audit_trace.system_message.v1` | system_message | denial, redaction, approval-required, export status, no results | capability-specific | retry, request approval, open trace |

## Surface style expectations

These surfaces inherit `ai-first-workstream-enterprise` from `../../../web-ui-style-guide.md`: calm enterprise workstream styling, named-theme tokens, neutral layered surfaces, blue/indigo AI accent, sparse semantic status colors, accessible focus states, strong numerical/table hierarchy, and prominent evidence, redaction, authorization, and trace cues. Style is a UI realization layer only; it must not change audit sensitivity, redaction, scoped capability mappings, approval policy, routes, export behavior, or trace immutability.

- Dashboard: render as an audit mission-control briefing with KPI cards for denials, high-risk activity, trace volume, pending exports, support-access reads, and redaction pressure; put suspicious-activity and export attention queues above routine volume summaries.
- Search and data-access surfaces: use dense enterprise search/table layouts with filter chips, saved-query controls, scoped tenant/customer context, redaction badges, actor/tool/workflow columns, monotonic timestamps, trace/correlation ids in monospace, and empty/no-results states that do not leak hidden records.
- Record detail and evidence panels: use layered detail cards that foreground actor, target, action, decision, authorization basis, policy clause/version, redaction status, and citation/copy affordances; link related traces without exposing unauthorized facts.
- Audit timelines and agent work traces: render chronological timelines with semantic icons, ordered events, prompt/skill/reference/tool/data-access segments, automation/review/escalation badges, correlation/causation ids, evidence expansion, and clear redacted/unavailable states.
- Prompt/skill/reference trace surfaces: use version-aware trace panels with document/version ids, manifest and boundary references, allowed/denied load outcomes, redaction notes, source links, and auditor-only detail affordances where authorized.
- Export request surfaces: render as approval-aware workflow forms/status cards showing requested scope, sensitivity, redaction plan, approvers, status, expiry/download eligibility, denial reasons, and immutable export audit links.
- System-message surfaces: use typed cards for denied access, redaction, approval required, export status, stale/reconnect, no-results, and trace-unavailable states with semantic icon/color plus text, recovery actions, and request-approval/open-trace affordances when authorized.

## Capability inventory and exposure channels

A capability is the governed backend contract. It may be exposed through one or more channels: surface action, browser API, workstream-agent tool, internal-agent tool, workflow step, timer, consumer, MCP tool, view, or internal method. Browser APIs and agent tools are exposure forms over the same capability; they do not redefine authorization, validation, redaction, idempotency, side effects, audit, approval, or denial behavior.

For this workstream, scoped read/evidence and trace/audit capabilities may be exposed as workstream-agent tools so the Audit/Trace agent can answer conversational requests such as “why was this action denied?” or “summarize role changes yesterday”. Export and broad sensitive reads require approval/redaction policy and must return traceable result surfaces or system-message surfaces.

| Capability id | Class | Purpose | Side effects |
|---|---|---|---|
| `audit_trace.dashboard.view` | trace/audit/read | dashboard summary | audit read trace |
| `audit_trace.records.search` | trace/audit/read | search audit events | sensitive-read audit |
| `audit_trace.records.view` | trace/audit/read | audit record detail | sensitive-read audit |
| `audit_trace.timeline.view` | trace/audit/read | correlated timeline | sensitive-read audit |
| `audit_trace.agent_work.view` | trace/audit/read | agent work trace detail | sensitive-read audit/redaction |
| `audit_trace.prompt_skill.view` | trace/audit/read | prompt assembly/skill/reference traces | sensitive-read audit/redaction |
| `audit_trace.data_access.search` | trace/audit/read | data-access event search | sensitive-read audit |
| `audit_trace.exports.form` | read/evidence | export form metadata | read trace |
| `audit_trace.exports.request` | workflow/approval | request audit export | export workflow, approval, audit |
| `audit_trace.exports.status` | read/evidence | export workflow status | read trace |
| `audit_trace.exports.download` | command/read | download approved export | download audit event |
| `audit_trace.saved_queries.manage` | command | save/update audit query | saved query state, audit |
| `audit_trace.summaries.generate` | proposal/read | generate investigation summary | AgentWorkTrace, no record mutation |

## Authorization and policy

- Audit access requires explicit audit capability and selected AuthContext.
- Support operators require active support-access grant and are always audited.
- Audit searches must be tenant/customer scoped and redacted by role.
- Cross-tenant search is denied without leaking existence.
- Prompt content, skill/reference text, model/provider metadata, PII, tokens, email invite tokens, and secrets are redacted unless explicitly authorized.
- Export requires approval for broad ranges, PII, cross-object evidence bundles, or support-access contexts.
- Audit records are append-only; mutation/deletion is not exposed here.

## Workstream-agent prompt requirements

`workstream-agent/prompt.md` must define the agent as the audit and trace investigation assistant. It must:

- help users construct scoped searches;
- explain audit events, authorization decisions, policy triggers, traces, and redactions;
- summarize timelines with citations to trace ids;
- avoid unsupported conclusions;
- refuse to reveal redacted facts or bypass access controls;
- request export approval when required;
- emit system-message surfaces for no results, redaction, denied access, stale/reconnect, and export status.

Runtime skills should cover audit search, event interpretation, trace timelines, agent trace interpretation, redaction rules, export requests, and investigation summaries.

## Akka realization candidates

- ESE: `AdminAuditEventEntity` or append-only audit writer pattern; `AuditExportRequestEntity`.
- Views: `AuditSearchView`, `AuditTimelineView`, `AgentWorkTraceView`, `PromptAssemblyTraceView`, `SkillLoadTraceView`, `DataAccessEventView`, `AuditExportView`.
- Workflow: audit export approval/preparation/download expiry.
- Timed Action: export expiry/retention reminders.
- Consumer: audit event enrichment/projection, trace correlation, retention marking.
- Agent: `AuditTraceAgent` with read-only trace tools and summary/proposal behavior.
- HTTP: `/api/audit-trace/**` surface payload/action endpoints.

## Tests

Required:

- dashboard/search/detail/timeline/agent trace/export surfaces render required states;
- audit search scoped by tenant/customer and role;
- support-access audit read requires active grant and emits support read event;
- forbidden cross-tenant search denied safely;
- prompt/skill/reference/tool traces redacted by role;
- export approval required for broad/sensitive export;
- export status/download idempotency and expiry;
- investigation summary includes trace citations and no hidden facts;
- no audit record mutation exposed;
- surface actions invoke backend capabilities;
- audit reads create audit/work traces.

## Not ready if

- audit UI reads raw logs without scoped backend capability;
- audit read access is unaudited;
- trace detail leaks prompt secrets, invite tokens, provider secrets, or cross-tenant data;
- export is direct download without approval/redaction policy;
- workstream agent invents unsupported conclusions or hides lack of evidence;
- tests do not cover redaction, support access, and cross-tenant denial.
