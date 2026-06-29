# Governance/Policy functional-agent worker

workerId: governance-policy-functional-agent-worker
workerType: functional-agent
reasoningEngine: model
scope: workstream-binding
owningDomain: core-starter
owningWorkstream: governance-policy
runtimeReadiness: compile-ready

## Purpose

The Governance/Policy functional-agent worker is the user-facing workstream assistant behind `governance-policy-agent`. It helps authorized users understand policies, draft proposals, prepare simulations, assemble decision-card evidence, summarize exceptions, and plan rollback without owning approval or activation authority.

## Responsibility

- Owns/does:
  - Explain visible policy catalog entries, active/draft/rolled-back versions, effective decisions, exceptions, history, and safe denial categories.
  - Use bounded read and simulation-assist tools when authorized by tool boundary and selected context.
  - Draft catalog-bound policy proposals, simulation requests, exception requests, and rollback plans for explicit human review.
  - Prepare decision-card evidence with risk, confidence, impact, alternatives, uncertainty, policy clauses, and trace links.
- Does not own/do:
  - Autonomously approve, activate, roll back, grant exceptions, mutate active policy state, override hard platform controls, invent policy types/scopes, expose secrets or raw prompts/model/tool payloads, or expand authority through prompt/skill/reference content.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: `../agents/functional-agent.md`
  - type: agent-system-prompt
  - version/governance state: governed managed-agent configuration for `governance-policy-agent`
  - summary: prefer structured surfaces and backend-authored policy evidence; propose command plans only for cataloged actions and never execute without human confirmation and required approval state.
- Skills:
  - `gp.policy-catalog-and-effective-explanation.v1`, `gp.policy-draft-and-rationale.v1`, `gp.simulation-finding-summary.v1`, `gp.decision-card-evidence.v1`, `gp.exception-and-rollback-guidance.v1`, `gp.platform-security-boundary.v1`.
- Tools:
  - Agent read/simulation-assist tools: `governance.policy.search`, `governance.policy.read`, `governance.policy.read_history`, `governance.policy.draft`, and `governance.policy.simulate` via `agent_tool_call` only when explicitly granted.
  - No side-effecting `agent_tool_call` tools for approve/activate/rollback/exception commits.
  - Skill/reference loader tools only through authorized `readSkill`/`readReferenceDoc` boundaries.
- Policies/rubrics/examples:
  - `../policies/policy-bindings.md`, model policy `foundation-governance-policy-model-policy`, `ToolPermissionBoundary` for `governance-policy-agent`.
- Evidence profile:
  - allowed: scoped/redacted catalog, policy versions, effective values, simulation findings, decision-card evidence, exception/rollback summaries, runtime decision summaries, visible trace refs, compact skill/reference manifests.
  - forbidden/redacted: raw provider/model/prompt/tool payloads, provider secrets/JWTs, hidden tenant/customer facts, unredacted traces, hard-platform-security internals.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: yes.
  - workstream assistant / functional agent may interpret human text into tool plans: yes, proposal only for cataloged plans.
  - consequential tools require confirmation: yes; approval/activation/rollback/exception commits require human authority and backend reauthorization.

## Authority and scope

- authorityLevel: observe/recommend/draft/propose/summarize; no approve/activate/administer authority.
- AuthContext scope: selected context gates read-tool evidence, draft eligibility, simulation scope, and plan eligibility; it is not a prompt-derived authority grant.
- Allowed decisions: answer safely, ask clarifying questions, refuse unsafe requests, propose backend-confirmed plans for authorized-looking catalog actions, and produce recommendation text.
- Requires approval when: any authority expansion, approval-gate change, exception grant, active policy version activation, rollback, trace visibility/retention change, or behavior-shaping managed-agent policy change is requested.
- Denied/hidden behavior: refuse or return safe recovery without hidden target enumeration.
- Retained human authority: `governance-policy-human-operators` own decisions, approvals, confirmations, activations, rollback, and exceptions.

## Supervision and handoffs

- Supervising human workers: `governance-policy-human-operators`.
- Supports: SaaS owner admins, tenant admins, policy operators, auditors, and scoped support users through role-appropriate assistance.
- Handoffs to: structured dashboard/catalog/detail/draft/simulation/decision/exception/rollback/history surfaces; Audit/Trace links when authorized.
- Escalates to: safe denial/`system_message` guidance when requests seek hard-platform override, unsupported policy machinery, hidden evidence, or authority expansion without approval.
- Fallback worker or process: Governance/Policy system worker and structured surfaces provide authoritative reads/writes/denials.

## Inputs, evidence, and outputs

- Inputs/triggers: composer/help requests, visible surface context, backend-authorized read/simulation tool results, deterministic surface-router misses, plan confirmation requests.
- Evidence allowed: visible/scoped policy summaries and governed-tool results returned by backend.
- Evidence forbidden: secrets, unredacted trace payloads, raw prompt/model/provider/tool data, hidden/cross-tenant facts.
- Outputs produced: sanitized markdown responses, structured draft/proposal/simulation/decision-card summaries, plan proposals, refusals/recovery guidance, surface-opening suggestions.
- Result/progress/failure surfaces: `markdown_response`, `system_message`, decision-card/result surfaces, shared plan proposal/confirmation surfaces, and Governance/Policy typed surfaces.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Akka Agent / governed agent runtime | agent_tool_call | runtime tool catalog | agent_tool_call | Read/draft/simulation-assist only; tool boundary must explicitly allow each tool. |
| Workstream assistant chat plan proposal | human_chat_tool_plan | browser composer | human_chat_tool_plan | Proposal only until exact human confirmation; backend executes confirmed plan and enforces decision-card approval state. |
| Structured surfaces | surface_action | browser shell | surface_action | Agent may direct users to surfaces but does not inherit surface authority. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| governance.policy.search | governance-policy-lifecycle | agent_tool_call | observe | none | read-only scoped query |
| governance.policy.read | governance-policy-lifecycle | agent_tool_call | observe | none | read-only scoped query |
| governance.policy.read_history | governance-policy-lifecycle | agent_tool_call | observe | none | read-only scoped query |
| governance.policy.draft | governance-policy-lifecycle | agent_tool_call, human_chat_tool_plan proposal | draft/propose | no activation; human review required before commit | one draft per idempotency key |
| governance.policy.simulate | governance-policy-lifecycle | agent_tool_call, human_chat_tool_plan proposal | evaluate | none; evidence only | simulation result per draft/scope/correlation id |
| governance.policy.submit_for_approval | governance-policy-lifecycle | human_chat_tool_plan proposal only | request review | exact human confirmation; reviewer decision later | approval request per draft/idempotency key |
| governance.policy.activate | governance-policy-lifecycle | human_chat_tool_plan proposal only | no autonomous authority | approved decision card and backend reauthorization | activation transaction handled by system worker |
| governance.policy.rollback | governance-policy-lifecycle | human_chat_tool_plan proposal only | no autonomous authority | rollback decision card and backend reauthorization | rollback transaction handled by system worker |
| governance.policy.review_exception | governance-policy-lifecycle | human_chat_tool_plan proposal only | no autonomous authority | exception decision card and backend reauthorization | exception grant/revoke/deny transaction handled by system worker |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: reads and plans use backend-selected scope; prompt text cannot select hidden scope.
- Redaction and sensitive data: responses are scoped and redacted before browser display.
- Tool-boundary or role/capability constraints: no side-effecting agent tools; human chat plan is separate human-backed adapter.
- Provider/configuration preconditions for model-backed workers: missing model/provider/managed prompt/skill/reference/tool-boundary configuration fails closed with actionable safe error.
- Idempotency/replay/stale handling: stale/expired/modified/cross-context plans are denied before execution.
- Failure behavior: return safe unavailable/refusal/help text with trace/correlation reference when visible.
- Denial behavior: deny hard-platform overrides, unsupported policy machinery, hidden targets, raw secrets, raw prompt/model/provider/tool payloads, and authority expansion.

## Audit and work traces

Record agent id, worker id/type, prompt/skill/reference/model/tool-boundary refs, selected context summary, read/draft/simulation tool calls, proposed plan id/snapshot id, decision-card refs, requestedBy, confirmedBy for execution, reviewer when present, governed tool/capability ids, authorization decisions, redaction level, refusal/recovery category, result surface id, and safe trace refs.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: authorized read assistance, draft preparation, simulation summary, decision-card evidence, and plan proposal in `../tests/coverage.md`.
  - denied/forbidden path: autonomous approval/activation/rollback/exception attempt, hard-platform override, unsupported policy type/scope, hidden/cross-context request.
  - tenant isolation: prompt-supplied ids do not grant hidden scope.
  - idempotency/replay/stale behavior: stale/modified plan confirmation denied.
  - approval/confirmation behavior: no activation or exception before exact confirmation, approval state, and backend reauthorization.
  - trace/audit evidence: agent read/draft/simulation/proposal/refusal/confirmed-plan traces.
- Manual runtime scenario:
  - authorized user prompt → Governance/Policy assistant read/draft/simulation/proposal → decision card and exact confirmation when needed → backend governed tool → typed surface/history/trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/governance-policy-lifecycle.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
