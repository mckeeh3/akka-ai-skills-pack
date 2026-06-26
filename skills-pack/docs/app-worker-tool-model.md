# App worker and governed-tool model

## Status and scope

This is the canonical skills-pack doctrine for separating app workers, execution harnesses, actor adapters, governed tools, capabilities, and Akka implementation. Use it with:

- `./workforce-decomposition.md` for worker rosters, authority, supervision, handoffs, and traces;
- `./worker-artifact-contract.md` for reusable `workers/<worker>.md` app-description artifacts and bindings;
- `./agent-workstream-application-architecture.md` for workstreams, functional agents, and the workstream shell;
- `./structured-surface-contracts.md` for human surface contracts and surface actions;
- `./capability-first-backend-architecture.md` for capability and governed-tool contracts;
- `./intent-to-realization-flow.md` for compiling current intent into implementation slices.

The canonical chain is:

```text
worker
→ execution harness
→ actor adapter
→ governed tool
→ capability
→ Akka implementation
```

Do not collapse these layers. A worker is not a tool. A surface is not authorization. An agent runtime is not product authority. A governed tool is not a raw endpoint or Akka method. A capability is not an agent tool. Akka components are implementation substrates selected after the product operation, authority, and exposure semantics are clear.

## Core definitions

| Term | Canonical meaning | Not the same as |
|---|---|---|
| Worker | Human, AI-backed software, or deterministic system participant that does app work under explicit responsibility, behavior profile, authority, evidence, tools, supervision, handoffs, and trace obligations. | A UI page, route, component, endpoint, or generic assistant. |
| Behavior profile | The worker's instructions/prompt, skills, tools, policies, rubrics/examples, evidence profile, assistance mode, and governance/version state. Every worker type has one. | Authorization by itself, a model-only prompt, a UI label, or a raw SDK tool list. |
| Reasoning/execution engine | The substrate that performs the worker's reasoning or deterministic execution: human, model, deterministic logic, external service, or mixed. | Product authority or the worker contract itself. |
| Execution harness | Runtime or interface that shapes how a worker receives context and performs work. | The authority model itself. |
| Actor adapter | Declared exposure path from a worker/harness to a governed tool, with input mediation, confirmation/approval behavior, trace source, and result semantics. | A duplicate business operation. |
| Governed tool | First-class semantic app operation or governed evidence read with actors, AuthContext, schemas, policy, idempotency, side effects, audit, tests, and selected adapters. | A UI button, prompt phrase, raw API route, raw Akka component method, or implicit permission. |
| Capability | Product-level backend ability or grouping that owns one or more governed tools and their shared business contract. | An agent tool or transport endpoint. |
| Akka implementation | Entity, Workflow, View, Agent, AutonomousAgent, Consumer, Timed Action, endpoint, service, and frontend code that realizes capabilities and adapters. | The product semantic contract. |

## Conceptual clarification: workers, models, harnesses, and tools

An app worker is the participant that does work in the product model. For AI-first SaaS modeling, workers are intentionally comparable because every worker has a behavior profile, even when their reasoning or execution engines differ:

- a **human worker** reasons with human judgment; the browser workstream shell, structured surfaces, human-operating prompt, role skills, and governed tools are that worker's execution harness/profile;
- an **AI-backed software worker** reasons through an AI model inside a governed agent runtime; prompts, skills, references, memory, model policy, guardrails, and tool boundaries are its execution harness/profile;
- a **system worker** acts through deterministic logic, schedules, event handling, integrations, or persisted workflows; deterministic instructions, policies, allowed tools, provenance, and failure semantics are its behavior profile.

Surfaces are therefore not merely UI pages. They are the structured harnesses that let a human worker inspect evidence, receive role guidance, choose actions, supply input, confirm consequences, see denials, and receive result surfaces. In the same way, an agent runtime is the structured harness that lets an AI model receive context, select allowed tools, and return bounded outputs. This symmetry is useful for app modeling, but it does not make humans, AI models, and deterministic systems identical.

Use **workstream assistant** as the product/UX term for the selected workstream's user-facing helper, and **functional agent** or **workstream agent** as the technical/governance term for the AI-backed worker/runtime behind that assistant. The assistant framing is intentional: the assistant helps the human worker inside the workstream, but it is constrained by its explicitly exposed tools just as human workers are constrained by structured surfaces and surface actions. Product copy may say assistant; architecture, governance, and Akka implementation should still name the functional-agent worker, managed behavior profile, model/tool boundary, traces, and runtime path.

The shared abstraction between all workers and the backend is the **governed tool**. A governed tool is the app-building-block contract for one semantic operation or evidence read. Human surface actions, confirmed human chat plans, AI `agent_tool_call`s, workflow steps, timer invocations, consumer reactions, APIs, MCP calls, and internal calls are actor adapters or exposure paths to governed tools; they are not separate business operations by default.

## Worker symmetry and differences

Generated AI-first SaaS apps model work through structurally comparable worker contracts while preserving important human/software/system differences.

| Dimension | Human worker | Software agent worker | System worker |
|---|---|---|---|
| Reasoning basis | Human judgment. | AI model reasoning through a governed agent runtime. | Deterministic logic, schedule, event handling, or integration code. |
| Behavior profile | Human-operating prompt, role skills, governed tools, policies/rubrics, examples, evidence profile, assistance mode. | Agent prompt, skills, references, governed/runtime tools, model policy, guardrails, examples, evidence profile. | Deterministic instructions, policy/rules, allowed governed tools, trigger/provenance rules, failure/idempotency profile. |
| Typical harness | Browser workstream shell, structured surfaces, forms, dashboards, decision cards, confirmations, optional confirmed chat-plan adapter. | Akka `Agent`, Akka `AutonomousAgent`, governed prompts, skills, references, memory, tool boundaries, model policy, guardrails, traces. | Workflow, timer, consumer, projection/view, endpoint, policy engine, internal service, integration. |
| Context | Surface payloads, evidence, labels, validation, disabled/denied states, help copy, visible traces. | Prompt/context window, retrieved references, memory, tool results, structured responses, policy/guardrail outputs. | Persisted state, event payloads, schedules, service identity, configuration, correlation/provenance. |
| Governed-tool use through actor adapters | `surface_action` browser adapters and, when explicitly modeled, confirmed `human_chat_tool_plan`. | `agent_tool_call` through a bounded tool catalog and explicit tool boundary. | `workflow_step`, `timer_invocation`, `consumer_reaction`, `api_call`, `mcp_tool_call`, or `internal_call` with provenance. |
| Control | Backend AuthContext, role/capability authorization, scoped payloads, confirmations, approval gates, audit. | Backend AuthContext/service authority, tool boundary, prompt/model policy, approval gates, fail-closed provider/runtime checks, traces. | Caller authority basis, idempotency, provenance, policy checks, audit, retry/compensation rules. |

Human workers and software agent workers may be able to request the same governed tool, but neither inherits the other's authority. Human surface availability does **not** imply AI-agent tool availability. AI-backed workers may use a governed tool only when the agent's tool boundary, AuthContext/service authority, autonomy policy, approval gates, and trace requirements explicitly allow the `agent_tool_call` adapter. Conversely, an agent-tool schema does not create a human affordance; human workers need a declared surface action or confirmed chat-plan adapter with appropriate labels, inputs, confirmations, result surfaces, and denials.

## Worker types

Use the worker taxonomy from workforce decomposition and keep each worker bounded:

| Worker type | Canonical role | Required contract focus |
|---|---|---|
| `human` | Authenticated person or organizational role that inspects, decides, approves, supervises, or audits work. | AuthContext, human-operating prompt, role skills, governed tools, workstream/surface access, direct actions, approval duties, evidence/trace visibility, assistance mode, denial behavior. |
| `functional-agent` | User-facing functional/context-area AI-backed worker for exactly one durable workstream; product UX may present it as the workstream assistant. | Owning workstream, selected human supervisor, prompts/skills/references, allowed tools, result surfaces, traces, fail-closed runtime behavior, assistant-facing UX labels where applicable. |
| `internal-agent` | Bounded specialist AI worker invoked by another worker or component. | Single responsibility, non-responsibilities, allowed evidence/tools, caller boundary, structured output, escalation/failure behavior. |
| `autonomous-agent` | Durable background AI worker with task lifecycle, progress, notifications, cancellation/failure, and acceptance/rejection. | Task start/read/cancel/accept/reject governed tools, progress/result surfaces, attention events, provider fail-closed behavior. |
| `evaluator-agent` | Independent reviewer/judge for quality, policy fit, risk, completeness, or outcomes. | Evaluation criteria, independence from producer, evidence limits, gate/decision outputs, traceability. |
| `system` | Deterministic workflow, timer, consumer, projection, integration, endpoint, or policy participant. | Trigger, authority basis, idempotency, provenance, retries, audit, result/attention effects. |

## Execution harnesses are not authorization

An execution harness shapes how a worker acts, but the backend capability/governed-tool contract remains authoritative.

- Human harnesses include the browser shell, structured surfaces, forms, decision cards, dashboards, inspection surfaces, action result surfaces, human-operating prompts/role guidance, role skills, and explicit chat-plan confirmation UX. They help human workers understand and submit work; they do not authorize it by themselves.
- AI-agent harnesses include Akka `Agent`, Akka `AutonomousAgent`, prompts, skills, reference documents, memory, model policy, guardrails, tool schemas, and runtime tool registration. They instruct and constrain model behavior; they do not grant product authority by themselves.
- System harnesses include workflows, timers, consumers, views/projections, endpoints, integrations, and internal service methods. They execute deterministic work; they still need caller authority, provenance, idempotency, policy, and audit at the boundary.

Frontend visibility, route availability, prompt text, tool descriptions, hidden form fields, and model output are never authorization controls. Every protected governed tool must enforce authenticated account or service identity, selected AuthContext, tenant/customer scope, membership/status, role/scope/capability authorization, approval policy, idempotency, and audit in backend code.

## Actor adapter catalog

Declare the actor adapter for every governed-tool exposure. The adapter records how the caller reaches the governed tool, not a separate product operation.

| Adapter | Caller/harness | Required semantics |
|---|---|---|
| `surface_action` | Human worker using structured surface/browser shell. | Human-facing labels, fields, validation, confirmation UX, disabled/denied states, idempotency key source, result surface/system message, trace source `surface_action`. |
| `human_chat_tool_plan` | Human worker asking in natural language through the selected workstream assistant / functional-agent harness. | Interpret the request through the human worker's behavior profile; proposed tool plan; explicit confirmation bound to that plan; per-tool backend authorization; idempotency/transaction behavior; partial-failure surfaces; `confirmedBy`; trace source `human_chat_tool_plan`. |
| `agent_tool_call` | AI-backed functional/internal/autonomous/evaluator agent through an agent runtime. | Explicit tool-boundary membership, model-facing schema/description, AuthContext or service authority, autonomy/approval policy, safe DTOs, trace source `agent_tool_call`, fail-closed provider/runtime behavior. |
| `workflow_step` | Workflow or process orchestration. | Persisted step state, retry/compensation behavior, approval waits, provenance/correlation, trace source `workflow_step`. |
| `timer_invocation` | Timed action, scheduled job, expiry, reminder, digest, or replay. | Stored authority basis, schedule provenance, idempotent retries, safe no-op behavior, trace source `timer_invocation`. |
| `consumer_reaction` | Event/topic/stream consumer. | Event provenance, duplicate/retry handling, allowed side effects, correlation propagation, trace source `consumer_reaction`. |
| `mcp_tool_call` | Remote model/client through MCP. | Service ACL/JWT, allowed-tool filtering, tenant/context scoping, redaction, remote audit, trace source `mcp_tool_call`. |
| `api_call` | Browser API, external service, or integration API. | Token/service identity validation, AuthContext resolution, scoped errors/denials, audit, trace source `api_call`. |
| `internal_call` | Backend service/component not directly exposed to users or models. | Invariant checks, caller-boundary authorization, idempotency where consequential, audit when required, trace source `internal_call`. |

If a governed tool is reachable through multiple adapters, keep one governed-tool id and declare each adapter separately. Tests should prove shared authorization and side-effect semantics plus adapter-specific confirmation, denial, result, and trace behavior.

## Governed-tool contract

A governed tool is the executable semantic contract between workers/adapters and backend capabilities. Define governed tools before choosing endpoints, agent functions, component methods, or UI controls.

Minimum governed-tool fields:

```yaml
governedToolId:
displayName:
toolType: read_evidence | search_or_list | draft | recommend | evaluate | propose | command | approval | admin | internal_system
capabilityId:
purpose:
allowedWorkerTypes:
allowedActorAdapters:
authorityLevel: observe | recommend | draft | evaluate | propose | execute | approve | administer
authContextScope:
inputSchema:
outputSchema:
validationAndSafeDefaults:
redactionAndEvidenceRules:
confirmationPolicy:
approvalPolicy:
idempotencyAndTransactionBoundary:
sideEffects:
resultSurfacesAndEvents:
partialFailureBehavior:
denialBehavior:
auditAndTraceRequirements:
implementationMapping:
requiredTests:
```

For side-effecting governed tools, default to proposal, approval, or confirmed execution unless an accepted policy grants bounded autonomous authority. For read/evidence governed tools, return scoped and redacted evidence DTOs rather than raw state dumps.

## Capability separation

A capability is the product-level backend grouping. It owns one or more governed tools and their shared business semantics.

```text
capability = product ability or grouping
  → governed tool = semantic operation/query inside the capability
    → actor adapter = selected exposure path for a worker/harness
      → Akka implementation = components/endpoints/UI/agent runtime that realize it
```

Capabilities define business outcome, actors/callers, AuthContext, scope, schemas, validation, data access, side effects, idempotency, policy/approval, audit/work traces, actor adapters/exposure channels, and tests. Governed tools inside the capability are precise executable operations or evidence reads.

Do not create separate business implementations for a human click, confirmed chat plan, and AI tool call. If they perform the same operation, they use the same governed tool with separate actor adapters and traces. If their authority, inputs, side effects, or approval rules differ enough that they are different operations, model distinct governed tools within the capability and explain the difference.

## Akka implementation separation

Akka components implement capability semantics; they do not define product authority by their existence.

| Product need | Common Akka implementation substrate |
|---|---|
| Audit-grade decisions, approvals, policies, traces, lifecycle history | Event Sourced Entity |
| Current-state profile, settings, configuration, cache-like state | Key Value Entity |
| Long-running execution, retries, compensation, approval waits | Workflow |
| Durable model-driven task lifecycle with progress, cancellation/failure, handoff, acceptance/rejection | AutonomousAgent |
| Curated lists, dashboards, evidence, reports, search, attention summaries | View |
| Bounded request/reply reasoning, summarization, planning, recommendation, evaluation, explanation | Akka Agent |
| Event reaction, enrichment, integration, publication, notification side effects | Consumer |
| Expiry, reminders, scheduled checks, digests, replay, retention | Timed Action / Timer |
| Browser/service boundary | HTTP or gRPC endpoint |
| Remote LLM/client boundary | MCP endpoint |
| Human execution harness realization | React/Vite/TypeScript web UI and structured surface renderer |

A governed tool may require multiple Akka components. Example: `inviteOrganizationMember` may use an invitation entity, invitation workflow, email outbox entity, invitation view, admin audit consumer, HTTP endpoint, and invitation surfaces. Those components are the realization of one capability-backed operation, not independent product definitions.

## App-description graph implications

When maintaining app descriptions, make these links first-class rather than implicit. Worker definitions and workstream bindings should use `./worker-artifact-contract.md` so the worker side of the chain is not buried in surface, agent, or tool files:

```text
worker -> execution harness
worker -> governed tools
worker -> supervision/handoff/trace obligations
surface action -> governed tool -> capability
human_chat_tool_plan -> governed tool -> capability
agent_tool_call -> governed tool -> capability
workflow/timer/consumer/API/MCP/internal adapter -> governed tool -> capability
governed tool -> result surface/event/attention/audit trace
capability -> Akka implementation substrate(s)
test -> worker + actor adapter + governed tool + capability + Akka implementation path
manual test -> runtime path + reconciliation outcome
```

Feature-bearing build tasks should inherit this chain from current intent. If a task only names a page, route, endpoint, component, or agent tool and does not identify the responsible worker, actor adapter, governed tool, capability, AuthContext, traces, and validation path, repair the description/task or block before implementation.

## Review checklist

Before compile/build implementation, verify:

- every affected workstream has a worker roster or explicit system-only reason;
- every worker has responsibility, authority, supervision/handoff, evidence, tools, traces, and failure behavior proportional to risk;
- every consequential surface action maps to one governed tool and capability;
- every confirmed human chat plan uses the same governed tool ids as matching surface actions while declaring plan confirmation and partial-failure behavior;
- every AI `agent_tool_call` is explicitly allowed by the agent's tool boundary and does not inherit human surface availability;
- every workflow, timer, consumer, API, MCP, or internal path records an actor adapter and trace source;
- every governed tool defines AuthContext, tenant/customer scope, schemas, idempotency, side effects, policy/approval, denials, result surfaces/events, audit/work traces, implementation mapping, and tests;
- every Akka component is justified by capability shape rather than CRUD/page intuition;
- tests cover allowed, forbidden, tenant-isolation, stale/failure, idempotency, approval/confirmation, audit/trace, and adapter-specific result behavior.

## Anti-patterns

Avoid:

- treating surfaces, routes, prompts, tool descriptions, or hidden fields as authorization;
- treating an Akka component method or endpoint as the governed-tool contract;
- exposing every component method as an `agent_tool_call` because the SDK supports tools;
- granting an AI worker a tool because a human worker has a visible surface action;
- duplicating business semantics separately for surface actions, confirmed chat plans, and agent tools;
- omitting system workers such as timers, consumers, projections, and workflows from authority/provenance/audit maps;
- returning raw state dumps where a scoped read/evidence governed tool is needed;
- marking implementation-ready tasks without a worker, actor adapter, governed tool, capability, Akka implementation path, trace contract, and validation path.
