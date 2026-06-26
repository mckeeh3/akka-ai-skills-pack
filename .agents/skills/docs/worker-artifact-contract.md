# Worker artifact contract

## Status and scope

This document defines the reusable current-intent Markdown shape for worker artifacts under:

```text
app-description/global/workers/<worker>.md
app-description/domains/<domain>/workstreams/<workstream>/workers/<worker>.md
```

Use it with `./workforce-decomposition.md` and `./app-worker-tool-model.md`. A worker artifact answers: who or what performs, supervises, evaluates, or deterministically carries out app work; which behavior profile shapes that worker's instructions, skills, tools, policies, and evidence; under what authority; through which harnesses and actor adapters; with which governed tools, handoffs, traces, failures, and tests.

All worker types have behavior profiles. Humans, model-backed agents, and deterministic system participants differ by reasoning/execution engine, not by whether they have instructions, skills, tools, policies, evidence scope, or trace obligations. For a human worker, the reasoning engine is the authenticated human and the behavior profile is a role/workstream operating brief plus skills and governed tools. For a model-backed worker, the reasoning engine is a configured model and the behavior profile includes prompt/skill/reference/model/tool-boundary governance. For a deterministic worker, the execution engine is workflow/timer/consumer/projection/service logic and the behavior profile records deterministic instructions, policies, allowed tools, provenance, and failure behavior.

A worker artifact is not an implementation file. It is the product contract that downstream surface, agent, capability, Akka, frontend, API, test, and manual-runtime artifacts preserve.

## Global definition vs workstream binding

Use two levels when a worker is reused:

```text
app-description/global/workers/<worker>.md
```

Defines the reusable worker identity: display name, worker type, reasoning/execution engine, broad responsibility, default behavior profile, default authority boundary, reusable harness/tool constraints, supervision expectations, and standard trace obligations.

```text
app-description/domains/<domain>/workstreams/<workstream>/workers/<worker>.md
```

Binds that worker to one workstream: exact work units, selected AuthContext scope, workstream-specific behavior profile entries, surfaces/agents/tools used in that workstream, handoffs, result surfaces, local denials, tests, and realization links.

If a worker is local to one workstream, the workstream file may be the only artifact. If a global worker is bound into a workstream, the workstream artifact should start with `Uses: app-description/global/workers/<worker>.md` and then state only the local binding details and overrides.

## File naming and ids

- File name should match `workerId` where practical: `access-review-triage-worker.md`.
- Use stable kebab-case ids for app-description files and manifest references.
- Use domain-language display names for humans and agents; avoid generic names such as `ai-agent`, `bot`, or `worker-1`.
- A functional-agent worker owns exactly one workstream. Internal, autonomous, evaluator, and system workers usually support a workstream without becoming left-rail workstreams.

## Minimum Markdown shape

```markdown
# <Display name>

workerId: <stable-worker-id>
workerType: human | functional-agent | internal-agent | autonomous-agent | evaluator-agent | system
reasoningEngine: human | model | deterministic | external-service | mixed
scope: global | workstream-binding | local-workstream
owningDomain: <domain-id or none>
owningWorkstream: <workstream-id or cross-cutting/system-only reason>
Uses: <optional link to global worker definition>
runtimeReadiness: draft | description-ready | compile-ready | manual-ready | runtime-ready | blocked

## Purpose

One or two sentences describing the job this worker performs in product language.

## Responsibility

- Owns/does:
  - <work unit or decision>
- Does not own/do:
  - <explicit non-responsibility>

## Behavior profile

- Instructions/prompt:
  - artifact id/path:
  - type: human-operating-brief | agent-system-prompt | deterministic-instruction | policy-instruction
  - version/governance state:
  - summary:
- Skills:
  - <skill id/path, purpose, version/governance state>
- Tools:
  - <governed tool id, allowed actor adapters, confirmation/approval policy>
- Policies/rubrics/examples:
  - <policy/rubric/example ids>
- Evidence profile:
  - allowed:
  - forbidden/redacted:
- Assistance mode:
  - workstream agent may explain role guidance: <yes/no>
  - workstream agent may interpret human text into tool plans: <yes/no/not applicable>
  - consequential tools require confirmation: <yes/no/not applicable>

For human workers, the prompt/instructions and skills shape role guidance, surface copy, decision support, and workstream-agent assistance. For model-backed workers, the prompt/skills/tools shape model runtime behavior. For deterministic workers, the instructions/skills/tools define deterministic policy, trigger, and allowed operation semantics.

## Authority and scope

- authorityLevel: observe | recommend | draft | evaluate | propose | execute | approve | administer
- AuthContext scope: <organization/tenant/customer/member/role/capability/service basis>
- Allowed decisions: <decisions or none>
- Requires approval when: <conditions>
- Denied/hidden behavior: <what happens when unavailable or unauthorized>
- Retained human authority: <required for AI/system workers where applicable>

## Supervision and handoffs

- Supervising human workers: <ids or none>
- Supports: <worker/workstream ids>
- Handoffs to: <ids and artifact produced>
- Escalates to: <ids and trigger>
- Fallback worker or process: <id/process>

## Inputs, evidence, and outputs

- Inputs/triggers: <surface action, prompt, schedule, event, workflow step, API, MCP, internal call>
- Evidence allowed: <scoped/redacted evidence>
- Evidence forbidden: <raw secrets, cross-tenant data, tokens, etc.>
- Outputs produced: <recommendation, draft, decision card, task result, state change, event>
- Result/progress/failure surfaces: <surface ids or none>

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| <surface/agent/workflow/timer/consumer/API/MCP/internal> | surface_action | browser | surface_action | <confirmation/result behavior> |
| <agent runtime> | agent_tool_call | runtime tool catalog | agent_tool_call | <tool boundary/approval behavior> |

Allowed adapters include `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `api_call`, `mcp_tool_call`, and `internal_call`.

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| <tool-id> | <capability-id> | <adapter list> | <level> | <policy> | <boundary> |

If this worker shares an operation with another human, AI, or system worker, reuse the same governed tool id and list distinct actor adapters and trace sources. Do not duplicate business semantics per channel.

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation:
- Redaction and sensitive data:
- Tool-boundary or role/capability constraints:
- Provider/configuration preconditions for model-backed workers:
- Idempotency/replay/stale handling:
- Failure behavior:
- Denial behavior:

## Audit and work traces

Record required trace events and fields, including:

- worker id and worker type;
- actor adapter and trace source;
- AuthContext, tenant/customer scope, account/member/service identity;
- requestedBy and confirmedBy when human request or confirmation initiates work;
- behavior profile id/version and prompt/instruction, skill, reference, model, tool, data, and policy usage where applicable;
- governed tool id, capability id, correlation/idempotency keys;
- allowed, denied, failed, stale, escalated, approved, completed, cancelled, or accepted/rejected outcome.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path:
  - denied/forbidden path:
  - tenant isolation:
  - idempotency/replay/stale behavior:
  - approval/confirmation behavior:
  - trace/audit evidence:
- Manual runtime scenario:
  - worker → adapter → governed tool → capability → Akka/API/UI path → trace/view evidence

## Realization links

- Surfaces:
- Agents:
- Tools:
- Capabilities:
- Policies:
- Traces:
- Tests:
- Akka components/API/frontend source-alignment:
```

## Worker-type notes

### Human worker

Emphasize role/AuthContext access, human-operating prompt/instructions, role skills, governed tools, surfaces used, direct actions, approval duties, denied/hidden states, trace visibility, and human accountability. A human text request handled by a workstream agent is still human-backed when the agent interprets the request through the human worker's behavior profile, proposes a plan, obtains required confirmation, and invokes allowed governed tools through `human_chat_tool_plan`.

### Functional-agent worker

Emphasize the single owning workstream, managed AgentDefinition or equivalent behavior profile, prompt/skill/reference/model/tool governance, allowed governed tools, result surfaces, human supervision, and provider/configuration fail-closed behavior.

### Internal-agent worker

Emphasize bounded specialist responsibility, caller boundary, allowed evidence/tools, structured output, no independent product authority, escalation, and result/partial-failure surfaces.

### Autonomous-agent worker

Emphasize durable task lifecycle: start/read/progress/cancel/fail/complete/accept/reject, notifications, task snapshots, result surfaces, cancellation/failure semantics, dependencies, and provider fail-closed behavior.

### Evaluator-agent worker

Emphasize independence from the producer, evaluation rubric, evidence limits, structured judgment output, gate/decision-card semantics, and traceability.

### System worker

Emphasize deterministic trigger, service or stored authority basis, provenance/correlation, idempotency, retries/compensation, stale no-op behavior, failure surfacing, and audit.

## Review checklist

Before compiling a feature-bearing slice, verify:

- [ ] Worker id, type, scope, responsibility, and non-responsibilities are explicit.
- [ ] Reasoning/execution engine and behavior profile are explicit, including instructions/prompt, skills, tools, policies, evidence scope, and assistance mode.
- [ ] Authority and AuthContext/service scope are stated; UI/prompt visibility is not treated as authorization.
- [ ] Supervision, handoffs, escalation, fallback, and result artifacts are named.
- [ ] Harnesses and actor adapters are listed for every exposure path.
- [ ] Governed tools and capabilities are linked; shared operations reuse one governed tool id.
- [ ] AI-backed workers do not inherit human permissions implicitly.
- [ ] System workers have provenance, idempotency, audit, and failure behavior.
- [ ] Trace fields and tests cover allowed, denied, tenant-isolation, stale/failure, approval/confirmation, idempotency, and runtime evidence.
