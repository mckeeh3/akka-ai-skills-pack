# Audit/Trace functional-agent worker

workerId: audit-trace-functional-agent-worker
workerType: functional-agent
reasoningEngine: model
scope: workstream-binding
owningDomain: core-starter
owningWorkstream: audit-trace
runtimeReadiness: compile-ready

## Purpose

The Audit/Trace functional-agent worker is the user-facing workstream assistant behind `audit-trace-agent`. In the tenant-admin activity-log scope it provides navigation wording, safe explanation of visible UI states, and retention-setting help without receiving audit evidence retrieval authority.

## Responsibility

- Owns/does:
  - Explain how to use visible activity-log filters and detail/retention surfaces.
  - Explain visible forbidden, validation, stale, retention-expired, and not-found/redacted states without hidden-target enumeration.
  - Direct tenant admins to protected browser surfaces for search/detail/retention work.
- Does not own/do:
  - Search traces, read trace detail, reveal full payloads, open tool-call detail, update retention, export evidence, draft notes, acknowledge suspicious activity, generate audit summaries, or execute chat plans.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: `../agents/functional-agent.md`
  - type: agent-system-prompt
  - version/governance state: governed managed-agent configuration for `audit-trace-agent`
  - summary: assistance only; never retrieve or reveal audit payloads through chat or agent tools.
- Skills:
  - `at.activity-log-navigation.v1`, `at.denial-message-explanation.v1`, `at.retention-settings-help.v1`.
- Tools:
  - No tenant-admin activity-log scope trace-search/detail/payload/retention mutation governed tools. Skill/reference loader tools only if authorized by managed-agent governance and redaction policy.
- Policies/rubrics/examples:
  - `../policies/policy-bindings.md`, model policy `foundation-audit-trace-model-policy`, tool-boundary entries for `audit-trace-agent`.
- Evidence profile:
  - allowed: visible surface state already provided to the browser, safe denial category, documented retention range/default, non-sensitive navigation labels.
  - forbidden/redacted: audit rows not already visible in the surface context, full payloads, trace ids/handles not visible to the user, hidden target existence, secrets/tokens/provider/model/prompt payloads.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: yes.
  - workstream assistant / functional agent may interpret human text into tool plans: no.
  - consequential tools require confirmation: not applicable; no consequential tools are granted.

## Authority and scope

- authorityLevel: recommend/observe visible UI state only; no governed evidence read or mutation authority.
- AuthContext scope: selected tenant context may appear only as visible surface context; not a tool authority basis for the agent.
- Allowed decisions: answer with safe help, ask clarification, direct to browser surfaces, refuse out-of-scope evidence requests.
- Requires approval when: not applicable; no execution path is granted.
- Denied/hidden behavior: refuse and direct to authorized browser surfaces or safe recovery; never confirm hidden trace existence.
- Retained human authority: tenant admin uses protected browser surfaces for all trace search/detail/retention work.

## Supervision and handoffs

- Supervising human workers: `tenant-admin-human` for visible UI assistance.
- Supports: `tenant-admin-human`.
- Handoffs to: protected browser surfaces only; no agent-executed tools.
- Escalates to: safe denial/system-message guidance when requests ask for payloads, exports, notes, summaries, or authority expansion.
- Fallback worker or process: deterministic surfaces and Audit/Trace system worker return authoritative data/denials.

## Inputs, evidence, and outputs

- Inputs/triggers: composer/help requests, visible surface context, denial/system-message context.
- Evidence allowed: visible surface labels, filter names, validation text, retention range/default, safe correlation id when already visible.
- Evidence forbidden: hidden rows/detail, full payloads, tool-call payloads, secrets, raw provider/model/prompt content, cross-tenant facts.
- Outputs produced: safe explanations, markdown/system messages, navigation suggestions, refusal/recovery guidance.
- Result/progress/failure surfaces: explanatory `markdown_response` or `surface-audit-trace-system-message` where implemented; protected evidence remains in browser surfaces.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Akka Agent / governed agent runtime | agent_tool_call | runtime tool catalog | agent_tool_call | No trace search/detail/retention mutation tools granted in this scope. |
| Workstream assistant chat | none for execution | browser composer | agent_turn | Explanation-only; no `human_chat_tool_plan`. |
| Structured surfaces | surface_action | browser shell | surface_action | Agent may direct users to surfaces but does not inherit surface authority. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| search-audit-traces | audit-and-trace-investigation | none for this worker | none | not applicable | not applicable |
| read-trace-detail | audit-and-trace-investigation | none for this worker | none | not applicable | not applicable |
| read-trace-tool-call-detail | audit-and-trace-investigation | none for this worker | none | not applicable | not applicable |
| read-audit-retention-setting | audit-and-trace-investigation | none for this worker | none | not applicable | not applicable |
| update-audit-retention-setting | audit-and-trace-investigation | none for this worker | none | not applicable | not applicable |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: the agent cannot use selected context to retrieve hidden evidence.
- Redaction and sensitive data: visible UI context only; no payload disclosure through chat.
- Tool-boundary or role/capability constraints: no Audit/Trace governed-tool entries in the agent tool boundary for tenant-admin activity-log scope.
- Provider/configuration preconditions for model-backed workers: missing model/provider/skill/reference/tool-boundary config fails closed; browser surfaces remain deterministic where available.
- Idempotency/replay/stale handling: not applicable to mutation; stale/expired plan-like requests are refused because chat plans are not supported.
- Failure behavior: return safe unavailable/refusal/help text with trace refs only if already visible.
- Denial behavior: do not enumerate hidden targets or imply the agent can inspect evidence.

## Audit and work traces

Record agent id, worker id/type, selected visible context summary, prompt/skill/reference/model/tool-boundary refs, no-evidence-tool policy decision, refusal/recovery category, and output surface/message id. Do not store or expose raw payloads in agent traces beyond approved audit retention/redaction policy.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: navigation/explanation help without evidence retrieval.
  - denied/forbidden path: prompt asks for search/detail/full payload/export/note/summary/retention mutation.
  - tenant isolation: hidden/cross-tenant prompt content is not confirmed.
  - idempotency/replay/stale behavior: not applicable to mutation.
  - approval/confirmation behavior: no chat-plan execution surfaces are available.
  - trace/audit evidence: agent refusals and help turns are traced without payload leakage.
- Manual runtime scenario:
  - tenant admin prompt → Audit/Trace assistant explanation/refusal → protected browser surface path remains authoritative → trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/audit-and-trace-investigation.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
