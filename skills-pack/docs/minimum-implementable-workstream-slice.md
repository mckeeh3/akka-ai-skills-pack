# Minimum Implementable Workstream Slice

Use this when a task should implement or plan one small vertical slice without pretending the full workstream is complete. It sits below `./workstream-contract.md`, `./requirements-to-workstream-development-process.md`, and `./capability-first-backend-architecture.md`.

A minimum slice is the smallest useful runtime path that preserves the workstream model:

```text
one workstream id
→ exactly one functional agent id
→ one selected AuthContext and role assumption
→ one dashboard/surface or explicit non-UI trigger
→ one attention category or explicit non-attention reason
→ one surface action/request or composer intent
→ one deterministic surface-intent route or explicit no-route reason for composer-enabled slices
→ one governed capability and governed-tool exposure
→ one Akka/API/UI realization path
→ auth denial + trace/correlation evidence
→ local validation
```

## Required slice fields

| Field | Minimum content |
| --- | --- |
| Workstream | `workstreamId`, display name, classification, and exactly one `functionalAgentId`. |
| Scope | Selected tenant/customer/AuthContext semantics and the role/capability basis for the slice. |
| Surface/dashboard | One dashboard, surface, system-message surface, or explicit non-UI trigger. |
| Attention | One category/lifecycle effect, or an explicit `non-attention` reason. |
| Action edge | One surface request, command, or declared actor adapter such as `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `api_call`, `mcp_tool_call`, or `internal_call`. |
| Surface intent route | For composer-enabled workstreams, one high-confidence route to open/refresh/prepopulate the surface, including prompt examples, prefill fields, ambiguity behavior, and no-mutation guarantee; otherwise an explicit no-route reason. |
| Capability | Capability id, governed-tool id, actor adapter/exposure channel, schemas, side effects, idempotency, approval/policy if any. |
| Backend authority | AuthContext checks, tenant/customer isolation, denial behavior, and redaction. |
| Akka/API/UI path | Selected substrate and how the user/system reaches it. |
| Trace evidence | Audit/work trace, correlation id, source refs, and visible trace link or explicit no-trace reason for non-runtime docs. |
| Tests/validation | Smallest command/runtime-validation smoke that proves success and forbidden behavior. |
| Readiness | Honest target level; do not claim `runtime-ready` without real local runtime evidence. |

## Minimal task brief block

Paste this into backlog or pending-task items before coding generated-SaaS runtime features:

```md
Vertical contract:
- workstream / functional agent: `<workstream-id>` / `<functional-agent-id>`
- scope/auth: `<tenant/customer/AuthContext + roles/capabilities>`
- attention: `<category + lifecycle>` or `non-attention: <reason>`
- dashboard/surface: `<surface-id>` or `non-ui trigger: <trigger>`
- surface graph edge: `<source> -> <action> -> <result surface/system_message>`
- surface intent route: `<prompt examples -> target surface + prefill + no-mutation>` or `not applicable: <reason>`
- capability/governed-tool: `<capability-id>` / `<governed-tool-id>`
- actor adapter / exposure: `surface_action|human_chat_tool_plan|agent_tool_call|workflow_step|timer_invocation|consumer_reaction|api_call|mcp_tool_call|internal_call`
- Akka substrate/API/UI path: `<entity/workflow/view/agent/autonomous agent/consumer/timed action/endpoint/frontend>`
- auth/denial/redaction: `<expected forbidden and redacted behavior>`
- trace/audit: `<trace records and correlation/source refs>`
- local validation: `<mvn/npm/api/browser smoke/manual check>`
- readiness target: `described|surface-ready|backend-ready|frontend-rendered|api-smoked|browser-smoked|manual-ready|runtime-ready`
```

## Expertise deferral pattern

Use this when the slice does not yet implement model-backed workstream-agent behavior:

```text
Expertise readiness: deferred.
Reason: this slice implements backend-authorized surface/action behavior only.
Blocked readiness levels: model-backed `api-smoked`, `browser-smoked`, and `runtime-ready` for model-backed turns.
Safe runtime behavior: composer/model request returns a typed system_message explaining that governed model configuration or expertise is unavailable, with trace/correlation id, and no provider bypass.
```

## Done criteria

A minimum slice is done only when:

- the manifest or task brief names the workstream, functional agent, surface/action, composer surface-intent route or no-route reason, capability/governed-tool, scope, trace, validation, and readiness target;
- success and forbidden behavior both use backend authority rather than frontend-only gating;
- protected data is tenant/customer scoped and redacted;
- any LLM-backed behavior uses the governed Akka Agent path or is explicitly deferred/fail-closed;
- validation evidence matches the claimed readiness level.
