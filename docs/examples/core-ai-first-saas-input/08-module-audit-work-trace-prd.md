# Module 7 PRD: Audit and Work Trace

## Status

Detailed PRD for the Audit and Work Trace module in the progressive core AI-first SaaS seed app.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `02-persistent-discussion-capture.md`
- `03-module-auth-app-access-prd.md`
- `03a-module-agent-workstream-runtime-bootstrap-prd.md`
- `04-module-user-admin-prd.md`
- `05-module-agent-definition-prd.md`
- `06-module-prompt-governance-prd.md`
- `07-module-skill-governance-prd.md`


## Workstream architecture alignment

This module PRD is interpreted under `10-canonical-core-app-prd.md` and `../../agent-workstream-application-architecture.md`. Any legacy references to pages, screens, navigation, or route inventory mean structured workstream surfaces, surface actions, and route/deep-link implementation details inside the agent workstream shell. They must not be used to generate a page-first admin console or chatbot-bolt-on app.

## 1. Module purpose

This module turns security audit events and agent-governance activity into a unified, tenant-scoped audit and work trace experience.

The module lets authorized users investigate what happened, who or what initiated it, which tenant/context was used, which permissions and governed artifacts were involved, which prompt/skill/model/tool versions shaped behavior, and why an action was allowed, denied, reviewed, or escalated.

This module does not introduce evaluator agents or closed-loop improvement. It creates the trace substrate that the Evaluation and Closed-Loop Improvement module will use for evaluation, replay, proposals, and outcome learning.

## 2. User-visible outcome

At completion, an authorized auditor, admin, or reviewer can:

1. open a unified Audit and Work Trace area;
2. search tenant-scoped security, admin, prompt, skill, agent-test, and tool-load activity;
3. open a trace detail timeline for a correlated activity;
4. inspect authorization basis, actor, tenant context, target resources, prompt/skill/model references, tool calls, data access summaries, decisions, and outcomes when available;
5. see redacted payloads according to permission and sensitivity rules;
6. export or copy safe trace summaries for incident review;
7. verify that unauthorized users cannot view audit/trace data and that cross-tenant traces never leak.

## 3. MVP boundaries

### In scope

- Unified tenant-scoped audit and work trace model.
- Trace ingestion from Modules 1-5 audit/activity sources.
- Correlation id strategy across API requests, entity commands, workflows, consumers, timers, prompt assembly, skill loads, and agent-test runs.
- Audit/trace search, filters, list, detail, and timeline UI.
- Redaction and trace-access policy basics.
- Trace event categories for identity/auth, admin actions, invitations, agent definitions, prompts, skills, tool loads, prompt assembly, data access summaries, and denials.
- Read models/views for trace search and timeline retrieval.
- Optional SSE/realtime trace stream for active test/demo flows if lightweight.
- Tests for trace emission, correlation, redaction, tenant isolation, access denial, and UI states.

### Out of scope for Module 6

- Evaluator agents and LLM-as-judge scoring.
- Automated improvement proposal generation.
- Replay/simulation of prompt or skill changes.
- Canary activation and rollback workflows.
- Full SIEM integration, long-term archive, legal hold, or enterprise compliance exports.
- Cross-tenant SaaS Owner investigation console unless explicitly accepted.
- Advanced anomaly detection or risk scoring.
- Production-scale observability surfaces beyond trace/audit UX.

## 4. Actors

| Actor | Description | Module 6 expectations |
|---|---|---|
| Tenant Admin | Admin with audit/trace capabilities. | Can inspect tenant audit and work traces. |
| Auditor / Reviewer | Read-only investigation user. | Can search and inspect trace details without mutating application state. |
| Agent Steward | Owner of agent definitions/prompts/skills. | Can inspect traces related to owned agents if permitted. |
| Tenant Member | Normal user. | Cannot access audit/work trace area by default. |
| Support Operator | Optional future support role. | Deferred unless support-access boundary is included. |
| Future Evaluator Runtime | Module 7 evaluator/replay path. | Will consume trace records for evaluation and improvement loops. |

## 5. Authorization and capability model

Required capabilities:

- `audit.read` — search and inspect security/admin audit events.
- `trace.read` — search and inspect work traces.
- `trace.sensitive.read` — view sensitive/redacted fields when explicitly allowed.
- `trace.export` — export or copy safe summaries.
- `trace.stream` — subscribe to realtime trace stream if included.
- `trace.retention.manage` — manage retention policy placeholder if included.

Recommended initial role mapping:

| Role | Module 6 capabilities |
|---|---|
| Tenant Admin | `audit.read`, `trace.read`, optional `trace.export`. |
| Auditor | `audit.read`, `trace.read`, `trace.export`; `trace.sensitive.read` only if explicitly granted. |
| Agent Steward | `trace.read` for owned agent traces if scoped access is implemented. |
| Member | No audit/trace capabilities by default. |

Rules:

- Backend checks are authoritative.
- Trace records are tenant-scoped and must not leak across tenants.
- Sensitive payload fields require explicit capability and redaction policy.
- Trace read access does not grant access to underlying domain records unless separately authorized.
- Exports must be safe and redacted by default.

## 6. Durable objects and state ownership

### AuditTraceEvent

Represents an append-only normalized event for audit and trace search.

Required fields:

- `traceEventId`
- `tenantId`
- optional `customerId`
- correlation id
- causation id / parent event id if available
- trace id / work id if available
- timestamp
- event category: identity, authorization, admin, invitation, agent, prompt, skill, tool, data-access, decision, workflow, timer, consumer, system
- event type
- severity: info, warning, risk, error
- actor type: human, agent, workflow, timer, consumer, system
- actor account id or agentDefinitionId when applicable
- target resource type/id
- action name
- authorization decision: allowed, denied, not-applicable
- authorization basis summary: membership, role, capability, policy, approval, or system rule
- prompt document/version references when applicable
- skill document/version references when applicable
- model configuration reference when applicable
- tool name/category when applicable
- data access summary when applicable
- safe metadata map
- redaction classification

State owner expectation: append-only event ingestion pattern. Event Sourced Entity, event log entity, or ingestion endpoint plus views are acceptable if events are immutable and queryable.

### WorkTrace

Represents a correlated unit of work that groups events into a timeline.

Required fields:

- `workTraceId`
- `tenantId`
- correlation id
- title/summary
- initiating actor
- work type: auth, admin, invitation, agent-test, prompt-governance, skill-governance, future-agent-execution
- status: active, completed, failed, denied, partial
- started at / completed at
- linked event ids
- linked resources
- outcome summary if available

State owner expectation: Key Value Entity or View-derived projection from AuditTraceEvents. If explicit status transitions are required, Event Sourced Entity may be selected.

### TraceRedactionPolicy

Represents basic redaction rules for trace display.

For Module 6 this may be static configuration plus tests.

Required concepts:

- public/safe fields;
- sensitive fields;
- secret fields never stored or displayed;
- permission needed to view sensitive details;
- default redacted export behavior.

### TraceBookmark / ReviewNote

Optional lightweight object for investigation notes.

If included, required fields:

- note id
- trace id/event id
- tenant id
- author account id
- note text
- created timestamp

This may be deferred if it distracts from core trace search/timeline.

## 7. Capabilities

### 7.1 Trace ingestion and normalization

Module 6 must normalize events from earlier modules into a shared trace schema.

Sources:

- Module 1 auth/context access and denials;
- Module 2 user admin, invitations, membership changes, and admin denials;
- Module 3 agent definition lifecycle;
- Module 4 prompt lifecycle, prompt assembly, and prompt test runs;
- Module 5 skill lifecycle, manifest changes, and `readSkill` loads/denials.

Required behavior:

- preserve original event type and source;
- assign tenant id and correlation id;
- link related events into a work trace where possible;
- classify sensitivity and redactable fields;
- avoid storing raw tokens, provider secrets, model secrets, invitation secret tokens, or full sensitive payloads unless an explicit policy allows.

### 7.2 Trace search/list

Authorized users can search trace events and work traces.

Filters:

- time range;
- event category/type;
- actor;
- target resource;
- authorization decision;
- severity;
- correlation id;
- agentDefinitionId;
- promptDocument/version;
- skillDocument/version;
- tool name/category;
- status.

List rows:

- timestamp;
- category/type;
- actor;
- target;
- decision/status;
- severity;
- short summary;
- correlation/work trace link.

### 7.3 Work trace timeline detail

Authorized users can open a timeline view for a correlated work trace.

Timeline entries should show:

- timestamp;
- actor;
- action/event type;
- target;
- allowed/denied status;
- prompt/skill/model/tool references when applicable;
- data access summary;
- safe metadata;
- links to related governed artifacts if authorized.

### 7.4 Event detail

Event detail should show:

- normalized event fields;
- original source/type;
- authorization basis;
- correlation/causation ids;
- redacted metadata;
- related resource links;
- safe copy/export action if permitted.

### 7.5 Redaction and sensitive detail display

Trace UI must default to safe redaction.

Rules:

- secret fields are never displayed;
- sensitive fields are masked unless user has `trace.sensitive.read`;
- exports default to redacted;
- full prompt/skill content should normally be linked to governed version surfaces rather than duplicated in traces;
- denied/cross-tenant access should not reveal resource existence.

### 7.6 Optional realtime trace stream

If included, authorized users can watch trace events for active prompt/skill test flows.

Rules:

- stream is tenant-scoped;
- stream requires `trace.stream`;
- events are redacted according to same policy as list/detail;
- stream failure degrades to refreshable list.

## 8. UI requirements

### 8.1 Workstream surfaces and route/deep-link inventory

Minimum routes:

- `/app/audit` unified audit/trace landing;
- `/app/audit/events` event search/list;
- `/app/audit/events/:traceEventId` event detail;
- `/app/audit/work-traces` work trace list;
- `/app/audit/work-traces/:workTraceId` timeline detail;
- optional `/app/audit/live` realtime trace stream;
- optional `/app/audit/redaction-policy` read-only redaction policy summary.

### 8.2 Audit landing

Landing surface should summarize:

- recent denied authorization events;
- recent admin changes;
- recent prompt/skill/agent governance changes;
- recent skill loads/denials;
- trace events by category;
- quick filters for common investigations.

### 8.3 Search/list UI

Required states:

- loading;
- empty;
- filtered empty;
- populated;
- forbidden;
- error.

Search UI must support stable, shareable filters if practical.

### 8.4 Timeline UI

Timeline must:

- show chronological order;
- group related events when helpful;
- visually distinguish allowed, denied, warning, and error events;
- not rely on color alone;
- show redaction badges;
- link to agent, prompt, skill, user, invitation, and membership details when authorized.

### 8.5 Export/copy UI

If export is included:

- default to redacted summary;
- show warning before copying sensitive fields;
- include correlation id, timestamp range, event summaries, and artifact references;
- exclude secrets and raw tokens.

### 8.6 Accessibility and responsive behavior

- Filters, timeline entries, and detail panels must be keyboard accessible.
- Status badges must include text labels.
- Timeline should remain readable on narrow screens.
- Long metadata values should wrap or collapse safely.

## 9. API requirements

Exact endpoint names may be adjusted during implementation planning, but the module must cover these contracts.

### Trace search and detail

- `GET /api/audit/events` — search normalized trace events.
- `GET /api/audit/events/{traceEventId}` — event detail.
- `GET /api/audit/work-traces` — search/list work traces.
- `GET /api/audit/work-traces/{workTraceId}` — work trace summary.
- `GET /api/audit/work-traces/{workTraceId}/timeline` — timeline events.
- `GET /api/audit/redaction-policy` — read-only effective redaction policy summary if included.

### Trace ingestion/internal APIs

- internal command/function to record `AuditTraceEvent` from components;
- internal command/function to link events to a `WorkTrace`;
- optional `POST /api/dev/trace-events` test-only ingestion endpoint if useful.

### Optional streaming/export

- `GET /api/audit/events/stream` — SSE stream for tenant-scoped trace events if included.
- `POST /api/audit/work-traces/{workTraceId}/export` — create redacted export/copy payload if included.

API rules:

- all read endpoints require AuthContext and relevant audit/trace capability;
- tenant ids are resolved from AuthContext;
- queries are tenant-scoped server-side;
- sensitive fields are redacted server-side;
- not-found/forbidden behavior must not leak cross-tenant resource existence.

## 10. Authorization rules

Required backend authorization checks:

- resolve active account and selected tenant AuthContext;
- require `audit.read` or `trace.read` for relevant endpoints;
- require `trace.sensitive.read` for sensitive fields;
- require `trace.export` for export/copy actions;
- require `trace.stream` for realtime stream;
- verify target trace/event belongs to selected tenant;
- apply artifact-specific access checks for linked prompt/skill content surfaces;
- audit denied trace access attempts.

## 11. Correlation and trace rules

Required correlation behavior:

- every protected request should have or create a correlation id;
- backend commands/events should carry correlation id when possible;
- workflow, consumer, timer, prompt assembly, skill-load, and test-console actions should preserve causation links;
- trace events should include source component and source event id when available;
- a WorkTrace should group events by correlation id or explicit work id.

Required trace completeness for MVP:

- auth/context denials include actor/provider subject when safe, route/action, and reason;
- admin actions include actor, target, previous/new state summary;
- prompt/skill actions include document/version/checksum references;
- prompt assembly includes active prompt version and checksum;
- skill loads include manifest and skill version;
- tool/data access summaries include category and target summary, not raw secrets.

## 12. Redaction, retention, and privacy requirements

Redaction requirements:

- never store or display raw provider tokens, session cookies, model API keys, Resend email service keys, invitation secret tokens, or passwords;
- redact sensitive prompt/test input and output by default unless user has explicit permission;
- store checksums and version references instead of duplicating full prompt/skill content in trace records;
- redact email addresses in exports if export policy requires it;
- classify each event metadata field as safe, sensitive, or secret.

Retention requirements for MVP:

- define a default retention placeholder or configuration value;
- do not implement destructive purge unless explicitly planned;
- make future retention management possible without changing event schema.

Privacy requirements:

- trace access itself is sensitive and denied attempts must be audited;
- trace UI must not become a backdoor to read data the user cannot otherwise access;
- support access and SaaS Owner cross-tenant trace access remain deferred unless explicitly accepted.

## 13. Acceptance scenarios

### Scenario 1: Auditor opens trace landing

Given an auditor has `audit.read` and `trace.read`, when they open `/app/audit`, then they see tenant-scoped summary cards and recent events.

### Scenario 2: Member is forbidden

Given a normal member lacks trace capabilities, when they open audit surfaces or call trace APIs, then access is forbidden and a denial event is emitted.

### Scenario 3: Auth denial appears in audit

Given a disabled user is denied by `/api/me`, when an auditor searches authorization denials, then the denial appears with safe actor, reason, route, timestamp, and correlation id.

### Scenario 4: Invitation lifecycle trace is linked

Given an invitation is created, email queued, resent, accepted, and membership created, when an auditor opens the work trace, then the timeline shows the linked lifecycle events in order.

### Scenario 5: Prompt activation trace shows version references

Given a prompt version is approved and activated, when the trace is opened, then it shows promptDocumentId, version, checksum, actor, previous active version, and activation event without exposing secrets.

### Scenario 6: Skill load trace shows manifest basis

Given an agent test loads a skill through `readSkill`, when the trace is opened, then it shows agentDefinitionId, manifest version, skill id/version, authorization allowed decision, and correlation id.

### Scenario 7: Unassigned skill denial is traceable

Given an agent attempts to load an unassigned skill, when the tool denies the call, then a `SKILL_LOAD_DENIED` event appears with denial reason and no skill content.

### Scenario 8: Cross-tenant trace access is denied

Given a Tenant A auditor requests a Tenant B trace id, when the API processes it, then the response is forbidden/not found according to policy, no Tenant B data leaks, and denial is audited.

### Scenario 9: Sensitive metadata is redacted

Given a trace event contains sensitive test input, when a user without `trace.sensitive.read` opens detail, then sensitive fields are masked while safe metadata remains visible.

### Scenario 10: Sensitive reader sees permitted fields

Given a user has `trace.sensitive.read`, when they open the same event, then permitted sensitive fields are visible, secret fields remain unavailable, and access is auditable if selected.

### Scenario 11: Export is redacted by default

Given an auditor exports a work trace, when the export is generated, then it includes safe summaries and references but excludes secrets and masks sensitive fields by default.

### Scenario 12: Realtime stream is tenant-scoped

Given optional realtime stream is included, when an auditor subscribes, then they receive only selected-tenant events with server-side redaction.

## 14. Test requirements

Minimum test coverage:

- Trace landing/list/detail UI states: loading, empty, populated, filtered empty, forbidden, error.
- Trace search returns only selected-tenant events.
- Work trace timeline groups correlated events.
- Trace event ingestion from representative Module 1-5 actions.
- Correlation id preserved across API command, workflow/consumer/timer where applicable, audit event, and trace view.
- Authorization denial for users lacking `audit.read`/`trace.read`.
- Cross-tenant trace id denial without data leakage.
- Redaction for sensitive fields without `trace.sensitive.read`.
- Sensitive-field access with `trace.sensitive.read` while secret fields remain hidden.
- Skill-load allowed and denied events include manifest/version metadata.
- Prompt assembly trace includes prompt version/checksum/model reference and excludes secrets.
- Export/copy output is redacted by default if export is included.
- Optional SSE stream tenant isolation and redaction if stream is included.
- Frontend bundle/static asset test verifies no provider/model/email/backend secrets are exposed.

## 15. Akka decomposition notes

This section is input for later `akka-prd-to-specs-backlog` and implementation planning. It is not the final design.

Likely Akka components:

- Event Sourced Entity or append-oriented component for immutable `AuditTraceEvent` ingestion.
- Key Value Entity or projection-derived state for `WorkTrace` summaries.
- Views for trace events by tenant/time/category/actor/target/correlation and work traces by tenant/status/type.
- Consumers to normalize existing module audit events into trace events.
- Consumers to maintain WorkTrace summaries/timelines.
- Optional SSE endpoint for live trace stream.
- HTTP endpoints for trace search, detail, timeline, redaction policy, and optional export.
- React/Vite/TypeScript UI for audit landing, event search, event detail, work trace list, timeline, and optional live stream.

Implementation guidance:

- Reuse AuthContext and authorization helper from Module 1.
- Reuse audit event sources introduced in Modules 2-5, but normalize them into a shared trace schema.
- Apply redaction server-side before returning trace payloads.
- Prefer storing artifact references/checksums over duplicating full prompt/skill bodies.
- Make trace ingestion easy for future agents, workflows, tools, consumers, and timers.
- Keep evaluator/replay/proposal logic deferred to Module 7.

## 16. Demo flow

A successful Module 6 demo should run as follows:

1. Sign in as Tenant Admin/Auditor.
2. Perform a few actions from earlier modules: invite user, activate prompt, assign skill, run skill-loading test.
3. Open Audit and Work Trace landing.
4. Filter for prompt governance events and open a prompt activation trace.
5. Filter for skill-load events and open a trace showing manifest and skill version basis.
6. Open an invitation work trace timeline and inspect lifecycle events.
7. Sign in as a normal member and confirm audit surfaces are forbidden.
8. Attempt cross-tenant trace access and confirm denial/no leakage.
9. Inspect redaction behavior with and without sensitive-read capability.
10. Run tests proving trace ingestion, correlation, tenant isolation, redaction, capability denial, and frontend secret boundary.

## 17. Explicit defers to later modules

Deferred to Module 7 Evaluation and Closed-Loop Improvement:

- evaluator agents;
- trace-based scoring;
- issue/failure classification;
- improvement proposals;
- replay/simulation;
- human approval workflows for improvements;
- canary activation and rollback;
- outcome links and learning loops.

Deferred to later hardening/modules:

- SIEM/export integrations;
- legal hold and enterprise retention management;
- support-access investigation console;
- SaaS Owner cross-tenant audit console;
- anomaly detection and risk scoring;
- full observability metrics surface.

## 18. Readiness checklist

Module 6 is ready for decomposition when the following are true:

- [ ] Module 1-5 audit/activity sources and correlation id assumptions are accepted.
- [ ] `AuditTraceEvent` and `WorkTrace` fields are accepted.
- [ ] Event categories, event types, and trace completeness rules are accepted.
- [ ] Redaction policy basics and sensitive-read capability are accepted.
- [ ] Trace access capabilities and role mapping are accepted.
- [ ] Workstream surface, route/deep-link, search/timeline/detail states are accepted.
- [ ] Optional stream/export scope is accepted or deferred.
- [ ] Tenant isolation, capability denial, redaction, correlation, trace ingestion, and frontend secret-boundary tests are accepted.
- [ ] Evaluator, replay, proposal, outcome, SIEM, and enterprise retention features are confirmed as not part of Module 6.
