# Audit/Trace functional-agent worker

workerId: audit-trace-functional-agent-worker
workerType: functional-agent
reasoningEngine: model
scope: workstream-binding
owningDomain: core-starter
owningWorkstream: audit-trace
runtimeReadiness: description-ready

## Purpose

The Audit/Trace functional-agent worker is the user-facing investigation assistant behind `audit-trace-agent`. It helps authorized tenant admins and scoped SaaS support operators search, correlate, explain denials, review support-access evidence, and summarize investigations from authorized/redacted trace evidence.

## Responsibility

- Owns/does:
  - Explain visible Audit/Trace surface states, redaction, trace gaps, denials, and support-access review states.
  - Propose confirmed read-only chat plans for trace search/read/correlation/denial investigation/summary.
  - Use bounded read-only `agent_tool_call` adapters where the tool-boundary grants exact scope and model-safe result payloads.
  - Produce evidence-cited investigation summaries with scope/redaction disclaimers and unresolved unknowns.
- Does not own/do:
  - Widen tenant/support scope, approve support access, approve exports, reveal hidden targets, mutate trace records, change retention, delete traces, bypass redaction, or expose secrets/provider/prompt/raw payloads.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: `../agents/functional-agent.md`
  - type: agent-system-prompt
  - version/governance state: governed managed-agent configuration for `audit-trace-agent`
  - summary: read-only investigation assistant; authority comes only from governed tools, selected AuthContext, support-access scope, confirmation/approval gates, and redaction policy.
- Skills:
  - `at.trace-search-investigation.v1`, `at.timeline-correlation.v1`, `at.denial-investigation.v1`, `at.support-access-review.v1`, `at.redaction-export-boundaries.v1`, `at.runtime-validation-evidence.v1`.
- Tools:
  - read-only search/detail/correlation/denial/summary governed tools through confirmed `human_chat_tool_plan` and bounded `agent_tool_call`; export request only as approval-gated surface handoff where policy allows.
- Evidence profile:
  - allowed: authorized/redacted trace summaries, safe handles, correlation/timeline summaries, denial reason summaries, support-access review facts, runtime-validation evidence refs, visible surface context.
  - forbidden/redacted: hidden records, cross-tenant data, secrets/tokens/provider credentials, frontend-secret material, raw prompt/model outputs, raw sensitive payloads without grant, support-only notes outside scope.

## Authority and scope

- authorityLevel: observe/recommend/read-only investigation; no autonomous mutation or approval authority.
- AuthContext scope: selected tenant/Organization plus optional customer/account; support operators require active support-access/platform support scope.
- Allowed decisions: ask clarification, propose read-only plan, execute confirmed plan, refuse unsafe requests, summarize authorized evidence, route to approval-required surfaces.
- Requires approval when: export/support-access policy requires it, sensitive/raw access is requested, or plan/tool boundary marks the operation approval-required.
- Denied/hidden behavior: refuse safely, return redacted/not-found/approval-required/forbidden system-message, never enumerate hidden targets.
- Retained human authority: users confirm chat plans and approve/request export/support-access through governed surfaces; backend enforces policy.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Workstream assistant chat | `human_chat_tool_plan` | browser composer + confirmation surface | `human_chat_tool_plan` | Read-only plan must name tools, scope, redaction, result surfaces, and correlation id before confirmation. |
| Akka Agent / governed agent runtime | `agent_tool_call` | runtime tool catalog | `agent_tool_call` | Bounded model-safe reads only; missing grant fails closed and traces denial. |
| Structured surfaces | `surface_action` | browser workstream shell | `surface_action` | Agent may route users to surfaces but does not inherit human surface authority. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| `search-audit-traces` | `audit-and-trace-investigation` | `human_chat_tool_plan`, bounded `agent_tool_call` | read redacted summaries | chat confirmation unless bounded non-sensitive call | read-only; search trace emitted |
| `search-work-traces` | `audit-and-trace-investigation` | `human_chat_tool_plan`, bounded `agent_tool_call` | read work traces | chat confirmation unless bounded non-sensitive call | read-only |
| `read-audit-trace-detail` | `audit-and-trace-investigation` | confirmed `human_chat_tool_plan`; bounded redacted `agent_tool_call` | read authorized detail | confirmation; sensitive fields redacted unless granted | read-only detail trace emitted |
| `read-work-trace-detail` | `audit-and-trace-investigation` | confirmed `human_chat_tool_plan`; bounded redacted `agent_tool_call` | read authorized work trace | confirmation | read-only |
| `lookup-trace-correlation` | `audit-and-trace-investigation` | `human_chat_tool_plan`, bounded `agent_tool_call` | read timeline/correlation | confirmation when spanning sensitive evidence | read-only |
| `investigate-denied-trace-access` | `audit-and-trace-investigation` | `human_chat_tool_plan`, bounded `agent_tool_call` | read denial evidence | confirmation when requested by chat | read-only |
| `summarize-investigation-evidence` | `audit-and-trace-investigation` | `human_chat_tool_plan`, bounded `agent_tool_call` | generate summary from authorized evidence | confirmation for chat plan | result surface; no trace mutation beyond summary evidence |
| `request-redacted-trace-export` | `audit-and-trace-investigation` | surface handoff only for this worker | request only, no approval | approval gate where policy allows | idempotent export request/result workflow |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation and support-access checks are backend-enforced for every tool call.
- Missing model/provider/skill/reference/tool-boundary config fails closed with actionable error and trace evidence.
- Prompt/skill/reference text cannot expand authority.
- Tool results are model-safe summaries by default; sensitive detail is surfaced only through role-gated browser surfaces.
- Partial failures return a structured result surface with per-tool outcome and trace refs.

## Audit and work traces

Record agent id, worker id/type, prompt/skill/reference/model/tool-boundary refs, `requestedBy`, `confirmedBy` and confirmation id where applicable, selected AuthContext/support scope, governed tool ids, actor adapter, authorization decision, redaction class, result surface, partial-failure summary, denial/refusal reason, correlation/causation ids, and output summary.

## Tests and manual runtime scenarios

- Authorized read-only chat plan proposal → confirmation → search/correlation/detail/summary tools → redacted result surface and trace chain.
- Missing confirmation or missing tool-boundary grant → no tool execution, safe denial, denial trace.
- Cross-tenant/support-scope prompt → refusal/denial without hidden target enumeration.
- Summary generation cites only authorized evidence and records redaction/unknowns.
- Export/support-access request routes to approval-required surface and cannot be self-approved by the agent.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/audit-and-trace-investigation.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
