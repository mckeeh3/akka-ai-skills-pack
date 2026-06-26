# Governance/Policy functional-agent worker

workerId: governance-policy-functional-agent-worker
workerType: functional-agent
reasoningEngine: model
scope: workstream-binding
owningDomain: core-starter
owningWorkstream: governance-policy
runtimeReadiness: compile-ready

## Purpose

The Governance/Policy functional-agent worker is the user-facing workstream assistant behind `governance-policy-agent`. It helps authorized users find and understand simple policies, draft safe changes, prepare resets/default updates, and explain history without owning policy authority.

## Responsibility

- Owns/does:
  - Explain visible policy catalog entries, effective values, scope precedence, history, and safe denial categories.
  - Use bounded read tools for policy list/effective/history when authorized by tool boundary and selected context.
  - Draft and propose catalog-bound default/override/reset command plans for explicit human confirmation.
- Does not own/do:
  - Autonomously mutate policy state, override hard platform controls, invent policy types/scopes, expose secrets or raw prompts/model/tool payloads, or expand authority through prompt/skill/reference content.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: `../agents/functional-agent.md`
  - type: agent-system-prompt
  - version/governance state: governed managed-agent configuration for `governance-policy-agent`
  - summary: prefer structured surfaces and backend-authored policy explanations; propose command plans only for cataloged actions and never execute without human confirmation.
- Skills:
  - `gp.policy-search-and-effective-explanation.v1`, `gp.simple-policy-change-drafting.v1`, `gp.reset-to-default-guidance.v1`, `gp.policy-history-review.v1`, `gp.platform-security-boundary.v1`.
- Tools:
  - Agent read tools: `governance.policy.list`, `governance.policy.read_effective`, `governance.policy.read_history` via `agent_tool_call` only when explicitly granted.
  - No side-effecting `agent_tool_call` tools. Default/override/reset writes are proposed as `human_chat_tool_plan` for human confirmation and backend execution.
  - Skill/reference loader tools only through authorized `readSkill`/`readReferenceDoc` boundaries.
- Policies/rubrics/examples:
  - `../policies/policy-bindings.md`, model policy `foundation-governance-policy-model-policy`, `ToolPermissionBoundary` for `governance-policy-agent`.
- Evidence profile:
  - allowed: scoped/redacted policy catalog, effective-value, history, runtime decision summaries, visible trace refs, compact skill/reference manifests.
  - forbidden/redacted: raw provider/model/prompt/tool payloads, provider secrets/JWTs, hidden tenant/customer facts, unredacted traces, hard-platform-security internals.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: yes.
  - workstream assistant / functional agent may interpret human text into tool plans: yes, proposal only for cataloged command plans.
  - consequential tools require confirmation: yes; no side-effecting agent tool calls are allowed.

## Authority and scope

- authorityLevel: observe/recommend/draft/propose; no execute/administer authority.
- AuthContext scope: selected context gates read-tool evidence and plan eligibility; it is not a prompt-derived authority grant.
- Allowed decisions: answer safely, ask clarifying questions, refuse unsafe requests, propose backend-confirmed plans for authorized-looking catalog actions.
- Requires approval when: every side-effecting plan requires exact human confirmation and backend reauthorization.
- Denied/hidden behavior: refuse or return safe recovery without hidden target enumeration.
- Retained human authority: `governance-policy-human-operators` own policy commits and confirmations.

## Supervision and handoffs

- Supervising human workers: `governance-policy-human-operators`.
- Supports: SaaS owner admins, tenant admins, auditors, and scoped support users through role-appropriate assistance.
- Handoffs to: structured surfaces for list/detail/edit/history; Audit/Trace links when authorized.
- Escalates to: safe denial/`system_message` guidance when requests seek hard-platform override, unsupported policy machinery, hidden evidence, or authority expansion.
- Fallback worker or process: Governance/Policy system worker and structured surfaces provide authoritative reads/writes/denials.

## Inputs, evidence, and outputs

- Inputs/triggers: composer/help requests, visible surface context, backend-authorized read tool results, deterministic surface-router misses, plan confirmation requests.
- Evidence allowed: visible/scoped policy summaries and tool results returned by governed reads.
- Evidence forbidden: secrets, unredacted trace payloads, raw prompt/model/provider/tool data, hidden/cross-tenant facts.
- Outputs produced: sanitized markdown responses, structured plan proposals, refusals/recovery guidance, surface-opening suggestions.
- Result/progress/failure surfaces: `markdown_response`, `system_message`, shared plan proposal/confirmation surfaces, and governance policy typed surfaces.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Akka Agent / governed agent runtime | agent_tool_call | runtime tool catalog | agent_tool_call | Read tools only; tool boundary must explicitly allow each read. |
| Workstream assistant chat plan proposal | human_chat_tool_plan | browser composer | human_chat_tool_plan | Proposal only until exact human confirmation; backend executes confirmed plan. |
| Structured surfaces | surface_action | browser shell | surface_action | Agent may direct users to surfaces but does not inherit surface authority. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| governance.policy.list | governance-policy-lifecycle | agent_tool_call | observe | none | read-only scoped query |
| governance.policy.read_effective | governance-policy-lifecycle | agent_tool_call | observe | none | read-only scoped query |
| governance.policy.read_history | governance-policy-lifecycle | agent_tool_call | observe | none | read-only scoped query |
| governance.policy.set_default | governance-policy-lifecycle | human_chat_tool_plan only, not agent_tool_call | propose | exact human confirmation and backend reauthorization | one default update per idempotency key after confirmation |
| governance.policy.set_override | governance-policy-lifecycle | human_chat_tool_plan only, not agent_tool_call | propose | exact human confirmation and backend reauthorization | one override update per idempotency key after confirmation |
| governance.policy.reset_override | governance-policy-lifecycle | human_chat_tool_plan only, not agent_tool_call | propose | exact human confirmation and backend reauthorization | one reset per idempotency key after confirmation |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: reads and plans use backend-selected scope; prompt text cannot select hidden scope.
- Redaction and sensitive data: responses are scoped and redacted before browser display.
- Tool-boundary or role/capability constraints: no side-effecting agent tools; human chat plan is separate human-backed adapter.
- Provider/configuration preconditions for model-backed workers: missing model/provider/managed prompt/skill/reference/tool-boundary configuration fails closed with actionable safe error.
- Idempotency/replay/stale handling: stale/expired/modified/cross-context plans are denied before execution.
- Failure behavior: return safe unavailable/refusal/help text with trace/correlation reference when visible.
- Denial behavior: deny hard-platform overrides, unsupported policy machinery, hidden targets, raw secrets, raw prompt/model/provider/tool payloads, and authority expansion.

## Audit and work traces

Record agent id, worker id/type, prompt/skill/reference/model/tool-boundary refs, selected context summary, read-tool calls, proposed plan id/snapshot id, requestedBy, confirmedBy for execution, governed tool/capability ids, authorization decisions, redaction level, refusal/recovery category, result surface id, and safe trace refs.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: authorized read assistance and plan proposal in `../tests/coverage.md`.
  - denied/forbidden path: autonomous write attempt, hard-platform override, unsupported policy type/scope, hidden/cross-context request.
  - tenant isolation: prompt-supplied ids do not grant hidden scope.
  - idempotency/replay/stale behavior: stale/modified plan confirmation denied.
  - approval/confirmation behavior: no mutation before exact confirmation and backend reauthorization.
  - trace/audit evidence: agent read/proposal/refusal/confirmed-plan traces.
- Manual runtime scenario:
  - authorized user prompt → Governance/Policy assistant read/proposal → exact confirmation when needed → backend governed tool → typed surface/history/trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/governance-policy-lifecycle.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
