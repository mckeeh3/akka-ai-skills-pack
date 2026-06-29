# Behavior: Audit/Trace

## Current-intent behavior

Audit/Trace provides authorized investigation over immutable audit and work trace evidence. It answers:

- who/what acted;
- under which tenant/customer/AuthContext;
- through which execution harness and actor adapter (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`, `consumer_reaction`, `projection_update`, `internal_call`, or `timer_invocation`);
- which governed tool, capability, policy, approval, prompt/skill/reference/model, or runtime-validation evidence was involved;
- whether the action was allowed, denied, redacted, approved, failed, or partially failed;
- how events correlate across workstreams and outcomes.

## Trace families

The workstream records and exposes these trace families where authorized:

- human surface-action and API request/response traces;
- confirmed human chat tool-plan proposal, confirmation, per-tool execution, result, denial, and partial-failure traces;
- agent work traces, including AgentDefinition, prompt/skill/reference/model/tool/data/policy usage and denials;
- governed-tool invocation traces and parent/child tool-call links;
- policy/decision/approval/support-access traces;
- authorization denials and trace-read denials;
- provider/config fail-closed and runtime-loader denial summaries;
- runtime-validation evidence links and source-alignment validation runs;
- projection/consumer/internal/timer traces, retention expiry, and trace-gap findings.

## Search behavior

Authorized users can search and filter by:

- date/time range;
- tenant and optional customer/account within authorized scope;
- actor type and actor display label;
- worker/workstream;
- event category and action type;
- governed tool, capability, policy, agent, prompt/skill/reference/model version where visible;
- actor adapter/source;
- status: success, failure, denied, approval-required, redacted, partial-failure, or trace-gap;
- correlation/session/work trace id or safe display handle.

Keyword search applies only to deterministic metadata and safe summary fields. It never indexes full request/response/tool payload text, secrets, provider credentials, bearer/session tokens, hidden cross-tenant identifiers, or frontend-secret material.

Search is read-only and idempotent. Repeated searches may emit read traces but never mutate trace records.

## Detail and timeline behavior

Trace detail opens an authorized trace with progressive disclosure:

- default view: safe summary, actor/workstream/action/status, authorization basis summary, redaction state, correlation links, and visible result surface;
- role-gated evidence: request/response/tool/policy/prompt/skill/reference/model summaries and safe payload excerpts;
- sensitive detail: only when `trace.sensitive.read` is granted; still excludes secret-never-store material.

Timeline/correlation surfaces group related events by correlation/work trace id and show causation/parent-child links across surface actions, chat plans, agent tool calls, workflow/internal/consumer/API events, decisions, approvals, support-access events, and runtime-validation evidence.

Hidden, expired, unsupported, or cross-scope trace references return `not_found_or_redacted` without confirming protected existence.

## Denial investigation behavior

Denial investigation surfaces explain authorized denial evidence: actor, selected AuthContext, actor adapter, governed tool/capability, authorization basis summary, policy reference, redaction status, and safe remediation path. They do not reveal hidden target ids, hidden counts, cross-tenant facts, raw policy internals, secrets, or unsupported records.

Denied trace reads are themselves traced with a safe denial category and correlation id.

## Investigation summary behavior

An authorized tenant admin or scoped support operator may request an investigation summary from selected visible traces. The functional agent may prepare a read-only summary through confirmed `human_chat_tool_plan` or bounded `agent_tool_call` using only authorized/redacted evidence.

Summaries must include evidence refs, scope/redaction disclaimer, relevant denials, trace gaps, correlation chain, and unresolved unknowns. They must not invent missing evidence, infer hidden cross-tenant facts, or reveal payloads beyond the user's grants.

## Export behavior

Redacted exports are allowed only through governed export request surfaces and policy grants. Export requests are idempotent by request/scope/correlation key and return one of: approval-required, queued/preparing, redacted-result, denied, expired, or failed. Sensitive/raw payload export is not available unless an explicit approval policy and capability grant are active.

The agent cannot approve exports or support access. Support operators cannot approve their own support access or export requests.

## Support-access review behavior

Support-access grant, use, expiry, revocation, denied-use, and scope changes are first-class trace events. Authorized tenant admins and SaaS owners can review who accessed what, why, when, under which approval/grant, and which trace reads/exports resulted.

## Runtime-validation evidence behavior

Runtime-validation runs from refreshed workstreams link into Audit/Trace as evidence when they validate trace emission, denials, redaction, correlation, source-alignment, runtime-loader/provider fail-closed behavior, export/support-access gates, or frontend secret boundaries. Audit/Trace shows run status, scope, workstream, evidence refs, trace gaps, and source-alignment impact without treating validation artifacts as runtime proof unless explicitly recorded in source-alignment.

## Invariants

- Backend authorization is required for every search, detail read, timeline/correlation lookup, denial investigation, summary, export request, support-access review, runtime-validation evidence read, and internal/system operation.
- Browser route visibility, row visibility, model text, chat context, prompt instructions, or support status never grants authority.
- Trace records are immutable until retention expiry.
- Every read, denial, support-access use, export request, summary generation, and trace-gap finding emits trace evidence.
- Redacted/default result surfaces are preferred; sensitive detail requires explicit grants.
- Cross-tenant discovery and hidden target enumeration are forbidden.

## Failure and edge cases

- Invalid filters return validation errors without running broad fallback queries.
- Missing context, disabled users, inactive memberships, expired support-access grants, missing tool-boundary grants, unconfirmed chat plans, unsupported export requests, and cross-tenant access are denied and traced.
- Repeated read-only actions are idempotent and may create read evidence only.
- Repeated export requests with the same idempotency key return the existing request/result state without duplicate export preparation.
- Projection lag or missing source events produce `trace-gap` surfaces and diagnostics rather than silently fabricating timelines.
