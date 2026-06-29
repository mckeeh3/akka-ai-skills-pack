# Global Traces: Foundation trace patterns

Reusable trace patterns for the core starter. Trace evidence is part of the current-intent graph and must link worker, execution harness, actor adapter, governed tool, capability, selected `AuthContext`, tenant/customer/account scope, result surface, tests, and runtime-validation proof.

## Trace pattern ids

- `admin-audit-event`: identity, membership, role, support access, invitation, policy, approval, data access, denial, retention, and future approved export events.
- `workstream-log-trace`: request, response, surface action, capability result, system message, attention update, result surface, realtime/reconnect state, and correlation links.
- `prompt-assembly-trace`: managed agent definition, prompt version, manifest, model config, compact expertise manifest, provider boundary, and fail-closed decisions.
- `skill-reference-load-trace`: authorized/denied skill and reference loads, redaction, manifest checks, lifecycle status, and tool-boundary checks.
- `agent-work-trace`: model call, tool call, data/evidence read, recommendation, human-request relationship, denial, failure, partial result, and final structured result.
- `policy-decision-trace`: effective policy source, proposal, reason, decision, approval, activation/reset/default/override, denial, runtime decision, and outcome link.
- `runtime-validation-evidence`: scenario setup, worker/adapter/tool/capability path, expected trace/result, pass/fail/block classification, and reconciliation destination.

## Adapter source mapping

| Actor adapter / path | Trace source | Required shared facts |
| --- | --- | --- |
| `surface_action` | `surface_action` in `workstream-log-trace` plus `admin-audit-event` when consequential | actor account, selected `AuthContext`, tenant/customer scope, surface/action ids, governed tool id, capability id, correlation id, idempotency key or hash where applicable, policy decision, result surface/system-message, no-op/replay/conflict/denial category. |
| `human_chat_tool_plan` | `human_chat_tool_plan` in `agent-work-trace` and `workstream-log-trace`; audit event for executed side effects | requestedBy human, proposing functional agent id, plan id/version, governed tool id, confirmation id, confirmedBy, selected `AuthContext`, backend authorization result, idempotency/transaction result, partial-failure/result surface, denial category. |
| `agent_tool_call` | `agent_tool_call` in `agent-work-trace` plus loader traces when needed | agent id, resolved behavior profile/version, model config alias, tool-boundary decision, governed tool id, capability id, selected `AuthContext` or service authority, requestedBy/supervisor when human-initiated, safe input/output summary, policy decision, provider/model/tool denial category. |
| `api_call` | `api_call` in `workstream-log-trace` or `admin-audit-event` | caller identity/service identity, selected context resolution, endpoint or API contract ref, governed tool id, capability id, scoped errors, redaction, result status, correlation id. |
| `workflow_step` | `workflow_step` in work trace/audit evidence | workflow id/step, stored authority basis, governed tool id, retry/compensation state, approval wait, idempotency/replay status, result/event, correlation/causation ids. |
| `timer_invocation` | `timer_invocation` in work trace/audit evidence | schedule/source, stored authority basis, affected tenant/customer scope, governed tool id, no-op/retry status, retention/expiry decision, result/event. |
| `consumer_reaction` | `consumer_reaction` in work trace/audit evidence | source event provenance, duplicate/retry handling, correlation propagation, governed tool/capability, allowed side effect, result/projection update, denial/failure. |
| `internal_call` | `internal_call` in work trace/audit evidence when protected/consequential | caller component/class, invariant/authorization basis, governed tool/capability, selected or stored context, transaction/no-op result, denial/failure. |
| `mcp_tool_call` | `mcp_tool_call` in audit/work trace evidence when future exposure is accepted | service ACL/JWT identity, allowed-tool filter, tenant/context scope, governed tool/capability, redaction, remote audit id, result/denial. No current core-starter MCP exposure is implemented by this refresh. |

## Required trace fields

Every protected trace should include, where applicable:

- trace id/reference safe for the consuming surface, correlation id, causation id, timestamp, environment/source, and workstream id;
- worker id/type, actor id/type, role/capability grant, selected `AuthContext`, Organization/Tenant/Customer/account scope, support-access posture, and customer/account target when relevant;
- governed tool id, capability id, actor adapter/source, input summary, output/result summary, result surface/system-message id, and side effects/events;
- policy decision, approval/confirmation facts, `requestedBy`, `confirmedBy`, confirmation id, idempotency/no-op/replay/conflict status, transaction boundary, retry/compensation status, and partial-failure facts;
- denial category and safe reason for unauthorized, forbidden, hidden, cross-scope, disabled, stale, provider/outbox/model blocked, tool-boundary denied, validation, conflict, and not-found-or-redacted outcomes;
- redaction decision and omitted/sensitive field categories without exposing secrets, raw JWTs, raw invitation tokens, provider secrets, raw prompts, raw model/provider payloads, hidden ids, or unauthorized document bodies.

## Retention and redaction

Retention is capability-scoped. The current Audit/Trace tenant-admin activity-log scope defaults to 90 days and is configurable by tenant admins from 30 to 365 days. Deferred export, investigation-note, summary, legal-hold, compliance bundle, or account-erasure scopes must define separate retention, approval, redaction, tests, and provider-boundary contracts before exposure.

Trace payloads must preserve diagnosis and audit value while respecting tenant/customer scope, redaction, support-access limits, frontend secret boundaries, and non-enumeration. Read/evidence surfaces may show only browser-safe trace refs or redacted summaries unless the local capability grants full-payload detail.

## Runtime-validation evidence convention

Each refreshed workstream later records scenario ids or explicit gaps that identify:

- role and selected `AuthContext` setup;
- surface/API/realtime path or non-UI trigger;
- worker id, execution harness, actor adapter, governed tool id, capability id, and expected Akka/API/frontend path;
- expected result surface/system-message, side effect/view/projection update, and trace ids/patterns;
- denial or negative case, tenant/customer isolation case, idempotency/replay case, confirmation/approval case when applicable, and provider/outbox/model fail-closed case when relevant;
- reconciliation destination for failures or gaps.

Description refresh work may be marked `described` only. It does not prove runtime readiness.
