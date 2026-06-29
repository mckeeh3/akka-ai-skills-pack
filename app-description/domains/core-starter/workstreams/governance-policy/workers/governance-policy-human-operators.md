# Governance/Policy human operators

workerId: governance-policy-human-operators
workerType: human
reasoningEngine: human
scope: local-workstream
owningDomain: core-starter
owningWorkstream: governance-policy
runtimeReadiness: compile-ready

## Purpose

Authorized SaaS owner admins, tenant admins, policy operators, auditors, and scoped support users inspect and govern policy lifecycle through structured Governance/Policy surfaces and decision cards.

## Responsibility

- Owns/does:
  - SaaS owner admins approve and activate SaaS-wide/default policy versions, exceptions, and rollback decisions.
  - Tenant admins approve and activate tenant-scoped business-governance policy versions and exceptions where authorized.
  - Policy operators draft proposals, run simulations, prepare decision-card evidence, request approvals, and execute approved lifecycle actions when capability grants allow.
  - Auditors and scoped support users inspect authorized policy versions, simulations, decisions, exceptions, rollback records, history, and runtime policy-decision evidence.
  - All write actors provide reason text, review visible consequences, and remain accountable for confirmed submissions, decisions, or confirmed chat plans.
- Does not own/do:
  - Override tenant isolation, backend authorization, secret protection, redaction, audit integrity, human-governance gates, or platform integrity controls.
  - Create complex policy scripts, legal compliance suites, autonomous policy commits, or enterprise delegation models.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: this worker binding plus `../surfaces/surfaces.md`
  - type: human-operating-brief
  - version/governance state: current app-description
  - summary: operate only inside backend-selected `AuthContext`; use dashboard/catalog/detail/draft/simulation/decision/exception/rollback/history surfaces and trace links to make scoped policy decisions.
- Skills:
  - policy catalog search, draft review, simulation interpretation, decision-card review, exception/rollback judgment, reason writing, history review, safe denial recovery.
- Tools:
  - `governance.policy.search`, `governance.policy.read`, and `governance.policy.read_history` via `surface_action` and `api_call`.
  - `governance.policy.draft`, `governance.policy.simulate`, `governance.policy.submit_for_approval`, `governance.policy.approve`, `governance.policy.activate`, `governance.policy.rollback`, and `governance.policy.review_exception` via `surface_action`, `api_call`, `workflow_step`, and bounded confirmed `human_chat_tool_plan` when explicitly offered.
- Policies/rubrics/examples:
  - `../policies/policy-bindings.md`, `../../../../../global/policies/foundation-security-and-governance.md`.
- Evidence profile:
  - allowed: browser-safe policy versions, visible scopes, simulation findings, decision-card evidence, approval/denial state, exception/rollback summaries, runtime-decision trace links, and redacted actor/change summaries for the selected context.
  - forbidden/redacted: hidden tenant/customer/account facts, raw secrets/JWT/provider keys, raw prompts/model payloads, raw tool payloads, raw correlation/idempotency internals, and cross-tenant evidence.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: yes.
  - workstream assistant / functional agent may interpret human text into tool plans: yes, only for cataloged confirmed command plans after deterministic no-mutation routing declines or cannot satisfy the request.
  - consequential tools require confirmation: yes for chat-plan execution and for any surface policy that requires explicit confirmation or approval.

## Authority and scope

- authorityLevel: SaaS owner admins administer SaaS policy; tenant admins administer tenant policy; policy operators prepare and execute within grants; auditors/support observe.
- AuthContext scope: backend-selected SaaS/defaults context or tenant/customer/account/member/role/capability context.
- Allowed decisions: scoped draft submission, simulation request, approval/denial/request-evidence, activation of approved policy versions, scoped exception grant/deny/revoke/expire, rollback approval/execution, scoped history/effective-policy inspection.
- Requires approval when: authority expands, approval gates change, exceptions are granted, active policy versions activate or roll back, trace visibility/retention changes, or managed-agent behavior-shaping policy changes.
- Denied/hidden behavior: hidden targets are omitted or return safe `system_message` feedback without enumeration.
- Retained human authority: humans remain accountable for all policy decisions, writes, exact chat-plan confirmations, activations, rollback, and exception outcomes.

## Supervision and handoffs

- Supervising human workers: tenant organization governance owners and SaaS owner admins according to role scope.
- Supports: Governance/Policy functional agent worker by providing explicit decisions, confirmations, and reason text when needed.
- Handoffs to: Audit/Trace for authorized trace investigation links; Agent Admin, User Admin, and downstream workstreams only through backend-authorized deep links.
- Escalates to: support or SaaS owner admin guidance when visible policy scope is blocked.
- Fallback worker or process: Governance/Policy system worker returns validation, denial, stale, partial-failure, or failure surfaces.

## Inputs, evidence, and outputs

- Inputs/triggers: dashboard/catalog/detail/draft/simulation/decision/exception/rollback/history surface actions, deep links, deterministic surface-intent routes, exact chat-plan confirmation, workflow review tasks.
- Evidence allowed: scoped policy catalog rows, active/draft version summaries, simulation findings, decision-card evidence, runtime-decision summaries, redacted trace refs.
- Evidence forbidden: secrets, raw provider/model/prompt/tool payloads, hidden target existence, cross-tenant/customer facts.
- Outputs produced: read requests, draft submissions, simulation requests, approval/denial/request-evidence decisions, activation commands, exception decisions, rollback commands, history/filter requests, confirmed plan decisions.
- Result/progress/failure surfaces: `../surfaces/surfaces.md` plus `system_message`, result, and partial-failure surfaces.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Governance/Policy structured surfaces | surface_action | browser workstream shell | surface_action | Backend authorizes every action; UI visibility is advisory. |
| Confirmed workstream assistant plan | human_chat_tool_plan | selected Governance/Policy assistant | human_chat_tool_plan | No mutation until exact plan snapshot confirmation and per-step reauthorization. |
| Protected workstream APIs | api_call | browser API client | api_call | Backend resolves selected context and returns typed surfaces/results. |
| Approval/exception/rollback workflow | workflow_step | backend workflow | workflow_step | Pauses for human decision and resumes only under recorded authority. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| governance.policy.search | governance-policy-lifecycle | surface_action, api_call | observe | none | read-only scoped query |
| governance.policy.read | governance-policy-lifecycle | surface_action, api_call | observe | none | read-only scoped query |
| governance.policy.draft | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call | draft/propose | reason plus submit/confirmation | one draft per idempotency key |
| governance.policy.simulate | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step | evaluate | none; evidence only | simulation result per draft/scope/correlation id |
| governance.policy.submit_for_approval | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step | request review | exact submission confirmation | one approval request per draft/idempotency key |
| governance.policy.approve | governance-policy-lifecycle | surface_action, api_call, workflow_step | decide | reviewer authority and decision-card evidence | one decision record per decision/idempotency key |
| governance.policy.activate | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step | commit approved version | approved decision card plus confirmation | single policy-version activation transaction |
| governance.policy.rollback | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step | restore prior version | rollback decision card plus confirmation | single policy-version rollback transaction |
| governance.policy.review_exception | governance-policy-lifecycle | surface_action, human_chat_tool_plan, api_call, workflow_step | grant/deny/revoke exception | exception decision card plus confirmation | exception state transaction |
| governance.policy.read_history | governance-policy-lifecycle | surface_action, api_call | observe | none | read-only scoped query |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: backend-selected context scopes every query, decision, and write.
- Redaction and sensitive data: browser-visible fields are scoped and redacted; hidden facts are omitted.
- Tool-boundary or role/capability constraints: human surface/chat adapters do not grant agent authority and agent tools do not grant human authority.
- Provider/configuration preconditions for model-backed workers: missing model/provider config disables assistant explanation/planning but not deterministic browser paths.
- Idempotency/replay/stale handling: side-effecting commands require idempotency and freshness/version checks where available.
- Failure behavior: validation, forbidden, stale, conflict, partial-failure, and provider-unavailable outcomes return safe `system_message` or typed surface state with trace refs.
- Denial behavior: no hidden scope enumeration or protected-data leakage.

## Audit and work traces

Record worker id/type, actor adapter, selected `AuthContext`, account/member/service identity, requestedBy/confirmedBy for chat plans, reviewer/decision refs, governed tool/capability ids, policy id/scope/category/version, reason for writes, idempotency/correlation refs, authorization decision, redaction level, result state, and linked surface/workstream item.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: catalog/read/draft/simulate/approve/activate/rollback/exception/history in `../tests/coverage.md`.
  - denied/forbidden path: missing context/capability, missing reviewer authority, cross-tenant/customer, hidden target, hard-platform-security override, unapproved activation.
  - tenant isolation: selected context is resolved server-side.
  - idempotency/replay/stale behavior: repeated writes and stale versions do not duplicate commits/history/traces.
  - approval/confirmation behavior: decision-card evidence, exact chat-plan confirmation, and required reason.
  - trace/audit evidence: surface, API, workflow, and chat-plan trace refs.
- Manual runtime scenario:
  - authorized human → surface action or confirmed chat plan → governed tool → governance-policy-lifecycle → protected Akka/API/UI path → decision/history/trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/governance-policy-lifecycle.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
