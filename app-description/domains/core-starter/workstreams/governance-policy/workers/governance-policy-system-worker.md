# Governance/Policy system worker

workerId: governance-policy-system-worker
workerType: system
reasoningEngine: deterministic
scope: local-workstream
owningDomain: core-starter
owningWorkstream: governance-policy
runtimeReadiness: compile-ready

## Purpose

The Governance/Policy system worker deterministically validates policy lifecycle commands, resolves effective policy, executes authorized draft/simulation/decision/activation/rollback/exception operations, publishes runtime policy state, and emits policy-decision/admin-audit/workstream trace evidence.

## Responsibility

- Owns/does:
  - Resolve selected `AuthContext`, supported policy ids/categories/types/scopes, role/capability authorization, separation-of-duty, and hard platform-security constraints.
  - Execute idempotent draft creation, simulation jobs, approval request state, decision recording, activation, rollback, exception state changes, history reads, and runtime effective-policy checks.
  - Enforce approval gates before activation, rollback, exception grant, authority expansion, trace visibility changes, and behavior-shaping managed-agent policy changes.
  - Emit history, policy-decision traces, workstream logs, workflow events, result/partial-failure/system-message surfaces, and safe denials.
- Does not own/do:
  - Make model-driven policy judgments, commit autonomous policy changes, bypass human approval, silently overwrite tenant authority, or implement complex rule languages.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: `../behavior.md`, `../tools/governed-tools.md`, `../policies/policy-bindings.md`
  - type: deterministic-instruction
  - version/governance state: current app-description
  - summary: enforce policy lifecycle semantics, backend authorization, human approvals, idempotency, transaction boundaries, redaction, and trace obligations.
- Skills:
  - deterministic policy validation, simulation orchestration, approval/exception/rollback workflow state, policy-version transaction handling, redaction, history/trace emission, safe denial mapping.
- Tools:
  - Executes governed tools through `api_call`, `surface_action`, `human_chat_tool_plan` confirmed execution, `agent_tool_call` read/simulation-assist, `workflow_step`, and `internal_call` runtime checks as declared in `../tools/governed-tools.md`.
- Policies/rubrics/examples:
  - backend-authorization-default-deny, tenant-customer-isolation, governed-agent-authority, decision-card evidence requirements, idempotent-write safety, non-overridable platform security, audit/history retention.
- Evidence profile:
  - allowed: scoped internal policy state, simulation evidence, decision records, exceptions, rollback targets, and caller AuthContext for computation.
  - forbidden/redacted: returning raw secrets, raw prompt/model/provider payloads, hidden target details, or unredacted trace payloads to unauthorized surfaces.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: not applicable.
  - workstream assistant / functional agent may interpret human text into tool plans: not applicable.
  - consequential tools require confirmation: enforced for `human_chat_tool_plan`; activation/rollback/exception additionally require decision-card approval state.

## Authority and scope

- authorityLevel: execute deterministic reads/writes/checks only under caller authority and recorded policy state.
- AuthContext scope: authenticated account/member/service identity plus backend-selected SaaS/defaults or tenant/customer/account context.
- Allowed decisions: authorize/deny, validate, simulate, record decision, activate approved version, roll back approved target, grant/deny/revoke exception, compute effective runtime decisions, record history/traces, return typed results.
- Requires approval when: activation, rollback, exception grant, authority expansion, approval-gate changes, trace visibility/retention changes, or behavior-shaping managed-agent policy changes occur.
- Denied/hidden behavior: return safe validation/forbidden/not-found-or-redacted outcomes without hidden enumeration.
- Retained human authority: humans retain accountability for policy decisions and confirmations; functional agent retains no execute authority.

## Supervision and handoffs

- Supervising human workers: `governance-policy-human-operators` through result/history/trace/decision surfaces.
- Supports: human operators and functional-agent read/proposal paths.
- Handoffs to: Audit/Trace views for authorized trace investigation; Workstream shell for typed result/denial surfaces; downstream workstreams through policy-decision traces.
- Escalates to: safe support/admin guidance when authorization/configuration/evidence is missing.
- Fallback worker or process: deny closed and emit trace on missing context, unsupported policy, stale data, failed simulation precondition, or config gap.

## Inputs, evidence, and outputs

- Inputs/triggers: API calls, surface action submissions, confirmed chat-plan execution, agent read/simulation tool calls, workflow steps, runtime internal policy checks, exception expiry timers if implemented later.
- Evidence allowed: internal policy catalog/version/exception/history state, simulation results, decision records, rollback targets, and caller AuthContext for computation.
- Evidence forbidden: raw secrets/JWTs/provider keys, raw prompt/model/tool payloads, hidden cross-scope facts in user-visible output.
- Outputs produced: typed policy surfaces/results, simulation result surfaces, decision-card records, activation/rollback/exception commits, runtime decision results, admin-audit/workstream/policy-decision traces, denial/`system_message` results.
- Result/progress/failure surfaces: dashboard/catalog/detail/draft/simulation/decision/exception/history/result/partial-failure surfaces and `system_message` results.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Protected workstream/admin APIs | api_call | browser/service API | api_call | Resolves selected context and returns typed surface/result DTOs. |
| Structured surface action handler | surface_action | browser workstream shell | surface_action | Human-submitted actions. |
| Confirmed chat-plan executor | human_chat_tool_plan | backend plan execution | human_chat_tool_plan | Executes only exact confirmed snapshots with reauthorization and approval-state checks. |
| Agent read/simulation-assist handler | agent_tool_call | governed runtime tool | agent_tool_call | Read/draft/simulation-assist tools for the functional agent. |
| Policy lifecycle workflow | workflow_step | backend workflow | workflow_step | Approval, activation, rollback, exception, expiry, and partial-failure handling. |
| Runtime policy evaluator | internal_call | internal service/component | internal_call | Computes effective decisions for protected runtime behavior. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| governance.policy.search | governance-policy-lifecycle | surface_action, agent_tool_call, api_call, internal_call | observe | none | read-only scoped query |
| governance.policy.read | governance-policy-lifecycle | surface_action, agent_tool_call, api_call, internal_call | observe/evaluate | none | read-only scoped query or runtime check |
| governance.policy.draft | governance-policy-lifecycle | surface_action, agent_tool_call, human_chat_tool_plan, api_call, workflow_step | draft/propose | reason plus submit/confirmation | one draft per policy/scope/idempotency key |
| governance.policy.simulate | governance-policy-lifecycle | surface_action, agent_tool_call, human_chat_tool_plan, api_call, workflow_step, internal_call | evaluate | none; evidence only | simulation result per draft/version/scope/correlation id |
| governance.policy.submit_for_approval | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step | request review | exact submission confirmation | one approval request per draft/idempotency key |
| governance.policy.approve | governance-policy-lifecycle | surface_action, api_call, workflow_step | decide | reviewer authority and decision-card evidence | one immutable decision per decision/idempotency key |
| governance.policy.activate | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step, internal_call | commit approved version | approved decision card plus confirmation | single policy-version activation transaction; partial publication reported |
| governance.policy.rollback | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step, internal_call | restore prior approved version | rollback decision card plus confirmation | single policy-version rollback transaction |
| governance.policy.review_exception | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step | grant/deny/revoke/expire exception | exception decision card plus confirmation | exception state transaction |
| governance.policy.read_history | governance-policy-lifecycle | surface_action, agent_tool_call, api_call, internal_call | observe | none | read-only scoped query |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: all state access is scoped by backend authorization.
- Redaction and sensitive data: result DTOs expose only frontend-safe fields for the caller role/context.
- Tool-boundary or role/capability constraints: adapter-specific authority is checked independently.
- Provider/configuration preconditions for model-backed workers: deterministic paths do not depend on model availability; agent-related calls fail closed when model governance is missing.
- Idempotency/replay/stale handling: idempotency prevents duplicate drafts, decisions, commits, history, and traces; stale versions return conflict surfaces.
- Failure behavior: failed, denied, stale, unsupported, partial-failure, and hard-platform-security outcomes produce traceable safe results.
- Denial behavior: deny closed with safe error code and no hidden target enumeration.

## Audit and work traces

Record worker id/type, adapter/trace source, caller identity, selected AuthContext, policy id/scope/category/version, active/draft/rollback/exception state, simulation refs, decision refs, effective-source and winning-scope explanation, old/new/effective values for commits, reason, idempotency/correlation refs, authorization/redaction decisions, denial/failure codes, result surface/workstream item, and downstream enforcement refs.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: deterministic catalog/read/draft/simulate/decision/activation/rollback/exception/history/runtime checks in `../tests/coverage.md`.
  - denied/forbidden path: hard-platform override, unsupported scope/type, missing reason/context/capability, unapproved activation, expired exception.
  - tenant isolation: cross-tenant/customer hidden targets denied without enumeration.
  - idempotency/replay/stale behavior: replay returns same result; stale version conflicts.
  - approval/confirmation behavior: confirmed chat plans reauthorize and execute exact snapshots only; activation requires approved decision.
  - trace/audit evidence: policy history, decision traces, simulation traces, exception traces, rollback traces, and runtime decision traces are emitted and redacted.
- Manual runtime scenario:
  - caller adapter → deterministic governed tool handler → governance-policy-lifecycle → typed surface/result → decision/history/trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/governance-policy-lifecycle.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
