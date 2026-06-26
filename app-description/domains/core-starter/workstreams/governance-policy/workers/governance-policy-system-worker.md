# Governance/Policy system worker

workerId: governance-policy-system-worker
workerType: system
reasoningEngine: deterministic
scope: local-workstream
owningDomain: core-starter
owningWorkstream: governance-policy
runtimeReadiness: compile-ready

## Purpose

The Governance/Policy system worker deterministically resolves policy catalog/default/override state, computes effective policy decisions, records history, and emits policy-decision/admin-audit/workstream trace evidence.

## Responsibility

- Owns/does:
  - Resolve selected `AuthContext`, supported policy ids/types/scopes, and role/capability authorization for policy reads and writes.
  - Apply SaaS defaults, tenant overrides, more-specific precedence, reset-to-default, stale/version checks, and idempotent replay.
  - Deny hard-platform-security override attempts and hidden/cross-tenant/customer targets.
  - Emit history, policy-decision traces, workstream logs, and safe result surfaces/events.
- Does not own/do:
  - Make model-driven policy judgments, commit autonomous policy changes, notify by default, run simulations, or implement complex rule languages.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: `../behavior.md`, `../tools/governed-tools.md`, `../policies/policy-bindings.md`
  - type: deterministic-instruction
  - version/governance state: current app-description
  - summary: enforce simple policy catalog semantics, backend authorization, idempotency, redaction, and trace obligations.
- Skills:
  - deterministic policy precedence, idempotent command handling, redaction, history/trace emission, safe denial mapping.
- Tools:
  - Executes governed tools through `api_call`, `surface_action`, `human_chat_tool_plan` confirmed execution, `agent_tool_call` reads, and `internal_call` runtime checks as declared in `../tools/governed-tools.md`.
- Policies/rubrics/examples:
  - backend-authorization-default-deny, tenant-customer-isolation, redaction/export governance, idempotent-write safety, non-overridable platform security.
- Evidence profile:
  - allowed: scoped internal policy state needed to compute authorized result DTOs and traces.
  - forbidden/redacted: returning raw secrets, raw prompt/model/provider payloads, hidden target details, or unredacted trace payloads to unauthorized surfaces.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: not applicable.
  - workstream assistant / functional agent may interpret human text into tool plans: not applicable.
  - consequential tools require confirmation: enforced for `human_chat_tool_plan`; surface confirmation follows action policy.

## Authority and scope

- authorityLevel: execute deterministic reads/writes/checks only under caller authority.
- AuthContext scope: authenticated account/member/service identity plus backend-selected SaaS-owner/defaults or tenant/customer/account context.
- Allowed decisions: authorize/deny, validate, compute effective values, write defaults/overrides/resets, record history/traces, return typed results.
- Requires approval when: caller adapter is `human_chat_tool_plan`, a surface requires confirmation, or future policy marks an action as approval-gated.
- Denied/hidden behavior: return safe validation/forbidden/not-found-or-redacted outcomes without hidden enumeration.
- Retained human authority: humans retain accountability for policy commits; functional agent retains no execute authority.

## Supervision and handoffs

- Supervising human workers: `governance-policy-human-operators` through result/history/trace surfaces.
- Supports: human operators and functional-agent read/proposal paths.
- Handoffs to: Audit/Trace views for authorized trace investigation; Workstream shell for typed result/denial surfaces.
- Escalates to: safe support/admin guidance when authorization/configuration is missing.
- Fallback worker or process: deny closed and emit trace on missing context, unsupported policy, stale data, or config gap.

## Inputs, evidence, and outputs

- Inputs/triggers: API calls, surface action submissions, confirmed chat-plan execution, agent read-tool calls, runtime internal policy checks.
- Evidence allowed: internal policy catalog/default/override/history state and caller AuthContext for computation.
- Evidence forbidden: raw secrets/JWTs/provider keys, raw prompt/model/tool payloads, hidden cross-scope facts in user-visible output.
- Outputs produced: typed policy surfaces/results, history entries, runtime decision results, admin-audit/workstream/policy-decision traces, denial/`system_message` results.
- Result/progress/failure surfaces: dashboard/list/detail/edit/history surfaces and `system_message` results.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Protected workstream/admin APIs | api_call | browser/service API | api_call | Resolves selected context and returns typed surface/result DTOs. |
| Structured surface action handler | surface_action | browser workstream shell | surface_action | Human-submitted actions. |
| Confirmed chat-plan executor | human_chat_tool_plan | backend plan execution | human_chat_tool_plan | Executes only exact confirmed snapshots with reauthorization. |
| Agent read-tool handler | agent_tool_call | governed runtime tool | agent_tool_call | Read-only tools for the functional agent. |
| Runtime policy evaluator | internal_call | internal service/component | internal_call | Computes effective decisions for protected runtime behavior. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| governance.policy.list | governance-policy-lifecycle | surface_action, agent_tool_call, api_call, internal_call | observe | none | read-only scoped query |
| governance.policy.read_effective | governance-policy-lifecycle | surface_action, agent_tool_call, api_call, internal_call | observe/evaluate | none | read-only scoped query or runtime check |
| governance.policy.set_default | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call | administer defaults | reason plus caller confirmation policy | one default update per idempotency key |
| governance.policy.set_override | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call | execute tenant override | reason plus caller confirmation policy | one override update per idempotency key |
| governance.policy.reset_override | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call | execute reset | reason plus caller confirmation policy | one reset per idempotency key |
| governance.policy.read_history | governance-policy-lifecycle | surface_action, agent_tool_call, api_call | observe | none | read-only scoped query |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: all state access is scoped by backend authorization.
- Redaction and sensitive data: result DTOs expose only frontend-safe fields for the caller role/context.
- Tool-boundary or role/capability constraints: adapter-specific authority is checked independently.
- Provider/configuration preconditions for model-backed workers: read/write deterministic paths do not depend on model availability; agent-related calls fail closed when model governance is missing.
- Idempotency/replay/stale handling: write idempotency prevents duplicate state/history/traces; stale versions return conflict surfaces.
- Failure behavior: failed, denied, stale, unsupported, and hard-platform-security outcomes produce traceable safe results.
- Denial behavior: deny closed with safe error code and no hidden target enumeration.

## Audit and work traces

Record worker id/type, adapter/trace source, caller identity, selected AuthContext, policy id/scope/value type, effective-source and winning-scope explanation, old/new/effective values for writes, reason, idempotency/correlation refs, authorization/redaction decisions, denial/failure codes, and result surface/workstream item.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: deterministic reads/writes/effective checks/history in `../tests/coverage.md`.
  - denied/forbidden path: hard-platform override, unsupported scope/type, missing reason/context/capability.
  - tenant isolation: cross-tenant/customer hidden targets denied without enumeration.
  - idempotency/replay/stale behavior: replay returns same result; stale version conflicts.
  - approval/confirmation behavior: confirmed chat plans reauthorize and execute exact snapshots only.
  - trace/audit evidence: policy history and runtime decision traces are emitted and redacted.
- Manual runtime scenario:
  - caller adapter → deterministic governed tool handler → governance-policy-lifecycle → typed surface/result → history and trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/governance-policy-lifecycle.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
