# Audit/Trace Workstream v0 Capability Inventory

## Inventory rules

This inventory specializes the shared five-core v0 contract for the `Audit/Trace Agent` workstream. Every protected capability below requires backend authorization from the selected `AuthContext`, tenant/customer scoping, safe denial, redaction, and audit/work-trace records. Exposure channels are implementation choices; they do not grant authority.

## Capability summary

| Capability id | Class | Primary actors/callers | Akka substrate intent | Exposure |
|---|---|---|---|---|
| `audit.trace.dashboard.read` | Read/evidence | authorized human user, Audit/Trace Agent | View/query + deterministic service | browser API, workstream surface |
| `audit.trace.search` | Read/evidence | authorized human user, Audit/Trace Agent | View/query + deterministic service | browser API, workstream action, read-only agent tool |
| `audit.trace.detail.read` | Read/evidence | authorized human user, Audit/Trace Agent | View/query + deterministic redaction service | browser API, workstream action, read-only agent tool |
| `audit.trace.timeline.read` | Read/evidence | authorized human user, Audit/Trace Agent | View/query + deterministic timeline service | browser API, workstream action, read-only agent tool |
| `audit.trace.failureEvidence.read` | Read/evidence | authorized human user, Audit/Trace Agent | View/query + deterministic classifier/redaction service | browser API, workstream action, read-only agent tool |
| `audit.trace.explain` | Request/response explanation | authorized human user through Audit/Trace workstream | Request-based Akka `Agent` plus governed read-only tools | workstream composer/API |
| `audit.trace.investigationGuide.read` | Read/evidence/proposal | authorized human user, Audit/Trace Agent | deterministic recommendation service; optional request-based Agent wording | browser API, structured surface |
| `audit.trace.summaryTask.start` | Autonomous task candidate | audit reviewer/admin, future workflow | Optional Akka `AutonomousAgent` task start; not required unless implemented | future workstream action/API |
| `audit.trace.summaryTask.read` | Autonomous task evidence | initiating/authorized user, future workflow | Optional Akka `AutonomousAgent` task snapshot/result read | future workstream surface/API |
| `audit.trace.summaryTask.cancel` | Autonomous task command | initiating/authorized user with task authority | Optional Akka `AutonomousAgent` task cancellation | future workstream action/API |

## Capability details

### `audit.trace.dashboard.read`

- Purpose: return a scoped dashboard with recent trace volume, notable denials/failures, saved or recent filters, readiness/empty states, and links to search/timeline surfaces.
- Actors/callers: authorized human user; Audit/Trace Agent only when composing a permitted dashboard answer.
- AuthContext: authenticated account, selected tenant/customer context, active membership, `audit.trace.read` or equivalent role/capability.
- Inputs: selected context id, optional time range, optional workstream/category filter, correlation id shortcut, request correlation id.
- Outputs: dashboard DTO with counts, redacted event summaries, filter metadata, trace links/correlation ids, safe empty/forbidden/partial/error states.
- Data access: scoped trace views, audit event views, workstream request/response trace index, model/tool trace index; tenant/customer filters mandatory.
- Side effects: read access trace/audit event for protected dashboard read and denials; no domain state mutation.
- Idempotency: repeated reads return current scoped projection; request correlation id dedupes duplicate access trace noise where implemented.
- Policy/approval: read-only; no approval required; support/SaaS-owner context requires explicit support authority if introduced.
- Audit/trace: record caller, selected AuthContext, filters, result count bands, redaction count, denial/partial reasons, correlation id.
- Exposure channels: browser API; dashboard structured surface; not directly side-effecting agent tool by default.
- Tests: authorized success, empty state, invalid time range, missing selected context, missing capability, disabled user, cross-tenant filter rejection, redaction count, access trace emission.

### `audit.trace.search`

- Purpose: search scoped audit/work traces by time range, correlation id, actor, event kind, workstream, capability id, severity, and status.
- Actors/callers: authorized human user; Audit/Trace Agent via governed read-only tool when assigned and authorized.
- AuthContext: selected tenant/customer context, active membership, `audit.trace.read`; optional narrower capability for sensitive event kinds.
- Inputs: query object with filters, pagination cursor, sort, page size, request correlation id; validation caps broad/expensive queries.
- Outputs: paged result DTO with redacted rows, total/count bands, next cursor, partial/redaction metadata, safe denial/error shape.
- Data access: trace/audit views scoped by tenant/customer; no raw state dump; sensitive fields redacted before output.
- Side effects: protected read trace; denied query trace; no mutation.
- Idempotency: read-only and retry safe; same query/cursor returns projection-consistent results.
- Policy/approval: read-only; high-sensitivity categories may be omitted or summarized unless caller has narrower authority.
- Audit/trace: record query filter categories, page size band, result count band, redaction/sensitivity summary, caller, AuthContext, denial reason.
- Exposure channels: browser API, search surface action, read-only `@FunctionTool`/tool facade for `audit.trace.explain` when granted by `ToolPermissionBoundary`.
- Tests: success with filters, validation for page-size/time-range, forbidden missing capability, tenant isolation, redaction, pagination, duplicate retry, denied agent-tool access, access trace emission.

### `audit.trace.detail.read`

- Purpose: show a single authorized trace/audit event with redacted payload, authority basis, related entities, and links.
- Actors/callers: authorized human user; Audit/Trace Agent through governed read-only tool.
- AuthContext: selected tenant/customer context, active membership, `audit.trace.read`; sensitive event kinds require matching evidence permission.
- Inputs: trace id or event id, optional expected correlation id, request correlation id.
- Outputs: detail DTO with event kind, timestamp, actor, source, correlation ids, authorization basis, redacted input/output/evidence fragments, related links, redaction metadata, safe denial/not-found shape.
- Data access: single scoped trace/audit record plus related lightweight references; verify tenant/customer ownership before redaction/output.
- Side effects: protected detail-read trace and denial trace; no mutation.
- Idempotency: read-only and retry safe.
- Policy/approval: read-only; no approval; sensitive fields always redacted unless a later explicit export/legal capability exists.
- Audit/trace: record detail-read event id, caller, AuthContext, redaction level, denial/not-found reason.
- Exposure channels: browser API, detail surface action, read-only agent tool.
- Tests: authorized detail, not-found safe shape, wrong tenant denial, sensitive redaction, missing authority denial, disabled user, trace emission.

### `audit.trace.timeline.read`

- Purpose: assemble a correlation timeline for a request, workstream turn, capability call, model/tool invocation, denial, or future task.
- Actors/callers: authorized human user; Audit/Trace Agent through governed read-only tool.
- AuthContext: selected tenant/customer context, active membership, `audit.trace.read` plus event-specific evidence permissions where needed.
- Inputs: correlation id/request id/work item id, optional include categories, optional time window, request correlation id.
- Outputs: ordered timeline DTO with event nodes, relationships, redacted summaries, source type (`request_response`, `capability`, `deterministic_service`, `model`, `tool`, `policy`, `workflow`, `autonomous_task`), partial/redaction indicators, safe denial/error shape.
- Data access: scoped trace/audit/workstream/model/tool indexes; tenant/customer filters for every node.
- Side effects: timeline-read trace; no mutation.
- Idempotency: read-only and retry safe.
- Policy/approval: read-only; unavailable or unauthorized event nodes are redacted/omitted with partial indicators instead of leaking existence where needed.
- Audit/trace: record requested correlation id, node count band, omitted/redacted category summary, caller, AuthContext, denial reason.
- Exposure channels: browser API, timeline surface action, read-only agent tool.
- Tests: successful ordered timeline, partial redaction, cross-tenant correlation denial, unknown correlation empty/not-found, malformed id validation, source type labeling, trace emission.

### `audit.trace.failureEvidence.read`

- Purpose: return focused evidence for denials, provider blocks/failures, model failures, tool denials/failures, policy blocks, and capability validation failures.
- Actors/callers: authorized human user; Audit/Trace Agent through governed read-only tool.
- AuthContext: selected tenant/customer context, active membership, `audit.trace.read`; narrower capabilities for model/tool/provider internals if needed.
- Inputs: trace id, failure id, correlation id + event selector, optional failure category.
- Outputs: failure evidence DTO with category, safe reason, user-actionable next steps, relevant policy/capability/tool/model refs, redacted details, trace links, denial/blocked/error shape.
- Data access: scoped failure traces, tool invocation traces, provider/model invocation traces, authorization decisions, policy decision traces.
- Side effects: protected evidence-read trace; no mutation.
- Idempotency: read-only and retry safe.
- Policy/approval: read-only; never exposes provider secrets, raw credentials, hidden prompt text beyond authorized prompt metadata, or unauthorized tenant/customer evidence.
- Audit/trace: record failure category, caller, AuthContext, redaction level, denial reason.
- Exposure channels: browser API, failure evidence panel, read-only agent tool.
- Tests: denial evidence success, provider blocked success without secret leakage, tool denied success, validation failure evidence, tenant isolation, redaction, missing authority denial, trace emission.

### `audit.trace.explain`

- Purpose: answer a bounded user question about authorized trace evidence in the Audit/Trace workstream.
- Actors/callers: authorized human user through workstream composer/API; the functional Audit/Trace Agent runtime.
- AuthContext: selected tenant/customer context, active membership, `audit.trace.read` plus any evidence capability needed by tools; active governed managed-agent `AgentDefinition` for the Audit/Trace Agent.
- Inputs: user question, optional trace ids/correlation id/search filters, conversation/session id, request correlation id.
- Outputs: safe markdown or typed explanation with cited trace links/correlation ids, uncertainty/partial indicators, safe denial/blocked-provider/error surface.
- Data access: only through governed read-only capabilities/tools such as search/detail/timeline/failure evidence after deterministic redaction and `ToolPermissionBoundary` checks.
- Side effects: prompt assembly trace, skill/reference load traces, model invocation trace, tool invocation/denial traces, AgentWorkTrace, workstream request/response entry; no domain mutation.
- Idempotency: repeated identical user messages create separate workstream turns unless caller supplies an idempotency key; duplicate idempotency key returns existing turn/result where implemented.
- Policy/approval: explanation-only; cannot grant access, activate policy, change roles, modify prompts, export raw data, or perform side effects. Side-effecting next steps must be represented as governed actions outside this capability.
- Audit/trace: record AuthContext, active governed AgentDefinition, prompt version refs, compact skill/reference manifest refs, tool boundary id, tool calls/denials, provider blocked/failure, output trace links.
- Exposure channels: workstream composer/API; request-based Akka `Agent` invocation with `effects().tools(runtimeTools)` for authorized tools.
- Tests: successful explanation invokes concrete Akka Agent through governed runtime path, forbidden question with unauthorized evidence, denied tool boundary, missing provider fail-closed blocked surface, no deterministic/model-less normal fallback, safe markdown rendering contract, AgentWorkTrace emission.

### `audit.trace.investigationGuide.read`

- Purpose: provide safe next-step guidance from the current dashboard/search/detail/timeline context, such as refine filters, open allowed related workstream, ask an explanation, or request a future summary task.
- Actors/callers: authorized human user; Audit/Trace Agent.
- AuthContext: selected tenant/customer context, active membership, `audit.trace.read`; target action capabilities checked before action exposure.
- Inputs: current surface context, selected trace ids/correlation id, optional failure category, request correlation id.
- Outputs: guidance DTO with allowed actions, disabled/denied reasons, trace links, risk/impact notes, and safe system messages.
- Data access: scoped current surface data and capability visibility; no raw trace dump.
- Side effects: guidance-read trace; no mutation.
- Idempotency: read-only and retry safe.
- Policy/approval: does not perform consequential action; only advertises actions the backend confirms as visible/requestable.
- Audit/trace: record evaluated action ids, caller, AuthContext, denied/hidden action summary.
- Exposure channels: browser API, investigation guidance card, optional agent response support.
- Tests: allowed action list from backend capabilities, forbidden action omitted/disabled, missing context denial, trace emission, no frontend-only authorization.

### `audit.trace.summaryTask.start` (future optional AutonomousAgent capability)

- Purpose: start a durable audit-summary or anomaly-review task over an authorized bounded trace set when a request/response explanation is insufficient.
- Actors/callers: audit reviewer/admin with explicit task authority; future workflow; not enabled by default.
- AuthContext: selected tenant/customer context, active membership, `audit.trace.investigate` or explicit summary-task capability; active governed/task model policy.
- Inputs: task scope, filters/correlation ids, objective, time bounds, idempotency key, request correlation id.
- Outputs: task id, accepted/rejected/blocked status, progress surface link, safe denial/error shape.
- Data access: scoped/redacted trace evidence through governed tools; no cross-tenant/customer reads.
- Side effects: creates AutonomousAgent task, task trace, notification/progress events; may read evidence tools; no policy/role/prompt mutations.
- Idempotency: duplicate idempotency key returns existing task acceptance/result; retries do not create duplicate tasks.
- Policy/approval: may require human approval or stricter role when broad, high-sensitivity, or support-context scope is requested.
- Audit/trace: task start, acceptance/rejection, model iterations, tool calls/denials, progress snapshots, completion/failure/cancellation, notifications.
- Exposure channels: future workstream action/API; only implemented when backend/runtime/frontend task lifecycle exists.
- Tests: authorized start, validation of scope bounds, forbidden/tenant isolation, duplicate idempotency key, blocked provider/model config, tool-boundary denial, task trace emission.

### `audit.trace.summaryTask.read` (future optional AutonomousAgent capability)

- Purpose: read progress, snapshots, result, failure, or blocked state for a previously started audit-summary/anomaly-review task.
- Actors/callers: initiating user, authorized reviewer/admin, future workflow.
- AuthContext: selected tenant/customer context, active membership, task visibility authority, tenant/customer ownership.
- Inputs: task id, optional cursor/snapshot id, request correlation id.
- Outputs: task status DTO, progress snapshots, redacted result/evidence citations, notifications, safe denial/not-found/error shape.
- Data access: scoped task state, task trace, redacted evidence citations.
- Side effects: protected read trace; no mutation.
- Idempotency: read-only and retry safe.
- Policy/approval: results may be partial/redacted based on current authority; support contexts require explicit model.
- Audit/trace: task read, caller/AuthContext, redaction level, denial/not-found reason.
- Exposure channels: future task status/result surface/API.
- Tests: read own authorized task, forbidden other tenant/customer, result redaction, failed/blocked state, trace emission.

### `audit.trace.summaryTask.cancel` (future optional AutonomousAgent capability)

- Purpose: cancel or terminate an in-progress audit-summary/anomaly-review task within authorized scope.
- Actors/callers: initiating user with cancel authority, audit admin/reviewer, future governance workflow.
- AuthContext: selected tenant/customer context, active membership, task ownership/authority, `audit.trace.investigate` or cancel-specific capability.
- Inputs: task id, cancellation reason, idempotency key, request correlation id.
- Outputs: cancellation accepted/no-op/denied DTO with task status and trace link.
- Data access: scoped task state and task trace.
- Side effects: task cancellation/termination request, notification/progress event, audit/work trace.
- Idempotency: repeated cancel for already terminal task returns no-op terminal status; duplicate idempotency key returns prior outcome.
- Policy/approval: high-impact externally triggered cancellation may require stricter authority if introduced.
- Audit/trace: cancellation request, authority basis, prior/new task state, no-op reason, denial reason.
- Exposure channels: future task surface action/API.
- Tests: authorized cancel, no-op already complete/cancelled, forbidden wrong tenant/customer, missing authority denial, idempotency, trace emission.

## Cross-capability validation checklist

Implementation tasks should verify the applicable subset of these checks:

- success with authorized selected `AuthContext`;
- validation and safe error shapes;
- forbidden access for missing capability, missing selected context, disabled account, missing membership, and wrong tenant/customer;
- tenant/customer isolation on every query and trace detail;
- redaction of secrets, raw tokens, sensitive prompt/provider details, and unauthorized evidence;
- idempotent/retry-safe read behavior and command idempotency for future task operations;
- audit/work trace creation for reads, denials, data access, model/provider failures, tool calls/denials, and future task lifecycle events;
- `ToolPermissionBoundary` enforcement before any agent tool is registered or invoked;
- request-based Akka `Agent` invocation for `audit.trace.explain` with governed managed-agent runtime and provider fail-closed behavior;
- no fixture/mock/deterministic/model-less normal runtime substitute for model-backed explanation behavior;
- frontend rendering of loading, empty, forbidden, redacted, partial, blocked-provider, validation-error, and safe-denial states without exposing backend secrets.
