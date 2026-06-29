# Capability: Audit and trace investigation

## Purpose

Let authorized tenant administrators and explicitly scoped SaaS support operators answer **"who did what, why was it allowed or denied, and how does this correlate across workstreams?"** by searching, inspecting, correlating, summarizing, and reviewing immutable audit/work trace evidence under backend authorization and redaction policy.

Audit/Trace is read/investigation first. It covers audit events, agent work traces, governed-tool invocations, authorization denials, support-access review, provider/config fail-closed evidence, runtime-validation evidence links, trace-gap diagnostics, and redacted export request handling where policy allows. It is not a trace editing/deletion surface, support-access self-approval path, raw sensitive export grant, full-payload keyword search engine, autonomous remediation engine, or prompt-based authority expansion path.

## Actors and scope

- `tenant-admin` / Organization admin: may search and inspect tenant-scoped audit/work traces, denials, agent/tool/policy evidence, support-access events involving the tenant, runtime-validation evidence for the tenant, and redacted export request state when policy grants it.
- `saas-support` / SaaS owner support operator: may investigate only under active support-access or platform support scope recorded in `AuthContext`; support views are redacted by default and all support-access use is reviewable.
- `audit-trace-agent`: may assist authorized users with read-only search, detail, correlation, denial investigation, support-access review explanation, runtime-validation evidence interpretation, and evidence-cited summaries through confirmed `human_chat_tool_plan` or bounded `agent_tool_call` adapters. It cannot widen scope, approve support access, approve exports, mutate traces, or reveal hidden targets.
- `audit-trace-system-worker`: records immutable trace facts, builds projections/correlations, enforces retention/redaction/support-access policy, handles runtime-validation evidence links, detects trace gaps, and executes authorized API/internal/export bookkeeping.

All calls require backend-owned selected `AuthContext`, active membership or service provenance, tenant/support scope, role/capability grants, redaction policy evaluation, and audit/work trace emission. Route visibility, row visibility, support status, prompt text, chat context, or model output never grants trace authority.

## Governed tools and exposure

Canonical capability id: `audit-and-trace-investigation`.

- `search-audit-traces` (`surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `internal_call` read): search tenant/support-scoped audit trace metadata and safe summaries; no full-payload keyword search.
- `search-work-traces` (`surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `internal_call` read): search work traces with agent/prompt/skill/reference/model/tool/policy refs where visible.
- `read-audit-trace-detail` (`surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded redacted `agent_tool_call`, `internal_call` read): open authorized trace detail with progressive disclosure, redaction state, sensitive-detail grant handling, and secret-never-store exclusion.
- `read-work-trace-detail` (`surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded redacted `agent_tool_call`, `internal_call` read): open authorized work-trace detail.
- `lookup-trace-correlation` (`surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `projection_update`, `internal_call` read): build correlation/timeline views and represent trace gaps explicitly.
- `investigate-denied-trace-access` (`surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `internal_call` read): explain authorized denial evidence without hidden target enumeration.
- `summarize-investigation-evidence` (`surface_action`, `api_call`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `internal_call` read/result): summarize selected authorized evidence with refs, redaction disclaimer, unresolved unknowns, and partial-failure reporting.
- `request-redacted-trace-export` (`surface_action`, `api_call`, `internal_call` request/workflow): request or prepare a redacted export where policy permits; sensitive/raw export is approval-gated or denied unless an explicit policy and capability grant exist.
- `review-support-access-traces` (`surface_action`, `api_call`, `internal_call` read): inspect support-access grant/use/expiry/denial evidence inside authorized scope.
- Trace ingestion/projection/retention/gap internals (`internal_call`, `consumer_reaction`, `projection_update`, `timer_invocation`): append immutable trace facts, update projections, expire by retention, and emit trace-gap findings.
- Runtime-validation evidence link internals (`internal_call`): link validation run status/evidence refs into Audit/Trace without exposing secrets.

Human `surface_action` and protected `api_call` access does not grant model access. `human_chat_tool_plan` access requires explicit confirmation of the proposed read-only plan. `agent_tool_call` access requires exact `ToolPermissionBoundary` grants and model-safe result payloads. Export/support-access-sensitive paths require separate approval where policy says so.

## Authorization, redaction, and denials

Unauthorized callers, disabled users, inactive memberships, missing selected context, non-authorized roles, expired support access, missing capability grants, cross-tenant/customer filters, hidden/expired trace references, unsupported export attempts, unconfirmed chat plans, missing agent tool-boundary grants, and unsupported internal callers are denied server-side.

Denied requests return safe feedback and emit durable audit/work trace evidence without protected-data leakage, hidden counts, hidden ids, or internal policy implementation details. Authorized denial investigation may show safe denial reason, policy reference, actor-adapter source, selected `AuthContext`/support scope summary, redaction class, correlation id, and remediation path when visible.

Search rows and keyword indexes use deterministic metadata and safe summary fields only. Full request/response/tool payload text, raw prompt/model output, provider credentials, bearer/session tokens, invite secret tokens, backend secrets, frontend-secret material, and hidden cross-tenant identifiers are never indexed or exposed to unauthorized readers.

## Search, detail, correlation, and summary behavior

Search/filter supports date/time range, tenant and optional customer/account within authorized scope, actor/worker/workstream, event category/action, governed tool/capability, policy/agent/prompt/skill/reference/model refs where visible, actor adapter/source, status, and safe correlation/work-trace handle.

Trace/work-trace detail opens authorized evidence with safe summary by default, role-gated evidence where allowed, sensitive payload warning/grant handling, support-access context, and related trace links. Hidden, expired, unsupported, or cross-scope references return `not_found_or_redacted` or forbidden/approval-required results without confirming protected existence.

Correlation/timeline views preserve causation across `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow, consumer, API, projection, internal, support-access, export, policy/decision, and runtime-validation events. Missing, delayed, malformed, or uncorrelated source events produce explicit trace-gap evidence rather than fabricated timelines.

Investigation summaries are result surfaces, not the source of truth. They must cite authorized evidence refs, scope/redaction disclaimer, relevant denials, trace gaps, support/export refs, and unresolved unknowns without inventing missing evidence or inferring hidden facts.

## Retention, export, and support-access behavior

Default audit/work trace retention is 90 days unless a governed tenant policy records another supported value. Trace records are immutable until retention expiry; retention expiry is diagnosable without exposing expired payloads. Trace visibility and retention policy changes remain subordinate to hard platform controls and Governance/Policy/Audit/Trace gates.

Redacted export requests are idempotent by actor/scope/redaction/export request key and return approval-required, queued/preparing, redacted-result, denied, expired, or failed state. Sensitive/raw payload export is not a default tenant-admin capability and requires explicit approval policy plus capability grant.

Support-access grant, use, expiry, revocation, denied-use, and scope changes are first-class trace events. Support operators cannot approve their own support access or export requests.

## Outcomes

In scope:

- role-scoped Audit/Trace dashboard, search, detail, timeline/correlation, denial investigation, support-access review, investigation summary, export request/result, and system-message surfaces;
- tenant/support scoped audit and work trace search/detail/correlation with redaction and no hidden enumeration;
- confirmed read-only chat plans and bounded model-safe agent-tool reads where explicitly granted;
- durable trace evidence for reads, denials, support-access, export, summaries, trace gaps, runtime-validation evidence links, and retention expiry;
- explicit source-alignment/runtime-validation evidence links without treating validation artifacts as runtime proof unless recorded.

Out of scope:

- autonomous remediation;
- support-access self-approval;
- raw sensitive export by default;
- trace edit/delete;
- full-payload keyword search;
- prompt-based authority expansion;
- cross-tenant discovery.

## Linked graph nodes

- Workstream: `../workstreams/audit-trace/workstream.md`
- Access: `../workstreams/audit-trace/access.md`
- Behavior: `../workstreams/audit-trace/behavior.md`
- Workers: `../workstreams/audit-trace/workers/`
- Agent binding: `../workstreams/audit-trace/agents/functional-agent.md`
- Tools: `../workstreams/audit-trace/tools/governed-tools.md`
- Surfaces: `../workstreams/audit-trace/surfaces/surfaces.md`
- Policies: `../workstreams/audit-trace/policies/policy-bindings.md`
- Traces: `../workstreams/audit-trace/traces/work-traces.md`
- Tests: `../workstreams/audit-trace/tests/coverage.md`
- Realization/source alignment: `../workstreams/audit-trace/realization/source-alignment.md`
