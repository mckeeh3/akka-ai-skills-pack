# App-description component graph

The app-description is the file-backed current-intent graph for an app. This document defines the canonical node families and links that must exist before a feature-bearing intent slice is compiled into code. It extends the compact hierarchy in [Current intent model](current-intent-model.md) and uses the worker/tool separation in [App worker and governed-tool model](app-worker-tool-model.md).

## Purpose

Use the component graph to keep product semantics ahead of implementation mechanics:

- model workers, harnesses, actor adapters, governed tools, capabilities, surfaces, agents, traces, tests, and manual scenarios as explicit graph nodes or bindings;
- preserve one governed operation across human surface actions, confirmed human chat plans, AI agent tools, API/workflow/timer/consumer/MCP/internal callers, and Akka implementation paths;
- prevent page-only, component-only, or prompt-only work from becoming the de facto app design;
- make every implementation task traceable from accepted intent to runtime evidence.

## Canonical node families

| Node family | Typical location | Owns | Required links |
|---|---|---|---|
| App | `app-description/app.md` | Objective, operating model, tenant/customer assumptions, global non-goals, cross-domain outcomes. | Domains, global actors/roles/workers/policies/traces, readiness state. |
| Actor and role | `global/actors/**`, `global/roles/**` | Human, AI-backed, service, or system caller identity and role semantics. | Workstream access, workers, AuthContext, capability permissions. |
| Worker | `global/workers/**`, `domains/<domain>/workstreams/<workstream>/workers/**` | Human, functional-agent, internal-agent, autonomous-agent, evaluator-agent, or system participant with behavior profile, reasoning/execution engine, responsibility, authority, evidence, tools, supervision, handoffs, and traces. | Behavior profile, harnesses, actor adapters, governed tools, surfaces/agents/workflows/timers/consumers, policies, traces, tests. |
| Execution harness | Surface, agent, workflow, timer, consumer, endpoint, MCP, or internal implementation binding. | How a worker receives context and performs work. | Worker, actor adapter, governed tool, AuthContext, trace source. |
| Workstream | `domains/<domain>/workstreams/<workstream>/**` | Operational binding for lifecycle/alignment state, access, workers, surfaces, agents, tools, policies, traces, tests, and realization. | Domain, lifecycle state, workers, surfaces, tools, agents, capabilities, traces, tests, realization maps. |
| Surface | `global/surfaces/**`, `workstreams/<workstream>/surfaces/**` | Human-facing structured payload, route/panel/card/form, states, actions, result surfaces, and system messages. | Human worker, `surface_action` adapter, governed tool, capability, frontend route/component, trace, rendering tests. |
| Agent | `global/agents/**`, `workstreams/<workstream>/agents/**` | Functional/internal/autonomous/evaluator agent purpose, prompts, skills/references, model policy, memory, tool boundary, supervision, and failure behavior. | Agent worker, Akka Agent/AutonomousAgent substrate, `agent_tool_call` adapters, governed tools, traces, tests. |
| Governed tool | `global/tools/**`, `workstreams/<workstream>/tools/**` | Semantic app operation or governed evidence read with stable id, schemas, authority, adapters, idempotency, policy, side effects, result surfaces, audit, and tests. | Workers, actor adapters, capability, policies, traces, result surfaces/events, Akka/API/frontend implementation. |
| Capability | `domains/<domain>/capabilities/**` | Product-level backend contract and grouping for governed tools. | Governed tools, AuthContext, data-state, Akka components, endpoints, views, tests, traces. |
| Data/state | `domains/<domain>/data-state/**` | Durable state, lifecycle, invariants, retention, projections, and trace obligations. | Capabilities, Event Sourced Entity/Key Value Entity/View/Workflow state, tests. |
| Policy/security | `global/policies/**`, `workstreams/<workstream>/policies/**`, access files | Authorization, tenant/customer scope, approval, denial, sensitive-data, retention, and risk rules. | Workers, actor adapters, governed tools, capabilities, endpoints, traces, negative tests. |
| Trace/observability | `global/traces/**`, `workstreams/<workstream>/traces/**` | Audit/work trace events, correlation, source, visibility, outcome metrics, and investigation surfaces. | Worker, adapter, tool, capability, Akka component, tests, manual scenarios. |
| Akka component | `workstreams/<workstream>/realization/akka-components.md` or capability realization section | Selected implementation substrates: Entity, Workflow, View, Consumer, Timed Action, Agent, AutonomousAgent, endpoint, MCP, service. | Capability and governed tool, package path, component tests, runtime path. |
| Frontend/API realization | `realization/frontend-routes.md`, `realization/api-contracts.md` | Browser routes/components, structured surface renderer bindings, endpoint contracts, API clients, subscriptions. | Surface, actor adapter, governed tool, capability, AuthContext, UI/API tests. |
| Source alignment | `realization/source-alignment.md` and optional `source-alignment.json` | File-level mapping from app-description graph files to implementation, frontend, test, spec, and manual validation evidence; staleness signal for lifecycle state. | Workstream lifecycle, graph nodes, realization files, source/test/frontend paths, commits/checks/manual scenarios. |
| Test expectation | `workstreams/<workstream>/tests/**`, specs/tasks | Acceptance, regression, negative, idempotency, tenant-isolation, approval/confirmation, trace, UI, and component tests. | Worker + adapter + governed tool + capability + Akka/frontend path. |
| Manual scenario | Workstream tests, specs, task briefs, or manual verification notes | Real local runtime/API/UI path to exercise after automated checks. | Role/AuthContext/tenant setup, surface or trigger, governed tool, expected trace/outcome, reconciliation result. |

## Required graph links

Feature-bearing graph updates should preserve these edges. If a link is intentionally absent, the node should say why. The owning workstream lifecycle record must also be updated when the graph change affects feature-bearing intent or implementation alignment.

```text
app objective
  -> domain
    -> workstream
      -> worker
        -> execution harness
          -> actor adapter
            -> governed tool
              -> capability
                -> Akka implementation substrate(s)
                  -> frontend/API/agent/runtime adapter(s)
                    -> tests
                      -> manual runtime scenario
                        -> audit/work trace and outcome evidence
```

Additional mandatory cross-links:

- `worker -> supervision/handoff/failure behavior -> trace obligations`
- `surface action -> governed tool -> capability -> result surface/event/trace`
- `human_chat_tool_plan -> governed tool -> capability -> confirmation id -> partial-failure surface -> trace source human_chat_tool_plan`
- `agent_tool_call -> governed tool -> capability -> tool boundary -> approval/autonomy policy -> trace source agent_tool_call`
- `workflow_step | timer_invocation | consumer_reaction | api_call | mcp_tool_call | internal_call -> governed tool -> capability -> provenance/correlation trace`
- `capability -> data/state -> Akka substrate -> projection/view/query -> dashboard or evidence surface`
- `test -> worker + adapter + governed tool + AuthContext + tenant/customer scope + trace expectation`
- `manual scenario -> runtime path + expected evidence + reconciliation destination for failures`

## Node contract details

### Worker nodes

Use `./worker-artifact-contract.md` for the reusable `workers/<worker>.md` Markdown shape. A worker node or binding should state:

- worker id and type: `human`, `functional-agent`, `internal-agent`, `autonomous-agent`, `evaluator-agent`, or `system`;
- reasoning/execution engine: `human`, `model`, `deterministic`, `external-service`, or `mixed`;
- behavior profile: instructions/prompt, skills, tools, policies/rubrics/examples, evidence profile, assistance mode, and governance/version state;
- owning workstream or cross-cutting/system-only reason;
- responsibility and non-responsibility;
- authority level and retained human authority where applicable;
- harnesses used by the worker;
- allowed governed tools and actor adapters;
- required evidence, input context, output/result surfaces, supervision, handoff, escalation, and failure behavior;
- trace obligations and tests.

Place reusable worker definitions under `app-description/global/workers/<worker>.md`. Place workstream-specific bindings or local workers under `app-description/domains/<domain>/workstreams/<workstream>/workers/<worker>.md`; bindings should link to the global worker with `Uses:` and record local scope, adapters, tools, surfaces, tests, and realization links.

### Governed-tool nodes

A governed-tool node or binding should state:

- stable governed tool id, display name, type, purpose, and capability id;
- allowed worker types and exact actor adapters;
- AuthContext, tenant/customer scope, permission/role/capability checks, denial behavior;
- input/output schemas, validation, redaction, safe defaults, and evidence DTO limits;
- confirmation, approval, autonomy, idempotency, transaction, and partial-failure semantics;
- side effects, result surfaces/events/attention updates, audit/work trace events, and trace source per adapter;
- implementation mapping to Akka components, endpoints, frontend surfaces, agent tools, workflows/timers/consumers/MCP/internal callers;
- required automated tests and manual runtime scenario.

### Capability nodes

A capability node should remain the backend product contract. It should not be replaced by an agent tool, endpoint, UI button, or Akka method. The capability records shared business semantics and maps each governed tool to implementation substrates and validation obligations.

### Realization nodes

Realization nodes describe how graph semantics compile to repository artifacts. They should identify selected Akka components only after the worker, adapter, governed tool, capability, authority, side effects, and trace contracts are clear.

Every feature-bearing workstream should include `realization/source-alignment.md` once it has or expects implementation. That artifact maps app-description files to source, frontend, test, spec, and manual-validation files so lifecycle alignment can be checked mechanically. If mapped app-description files are newer than mapped source/test files, treat implementation as stale unless an explicit no-code-impact review is recorded. See [App-description source alignment](app-description-source-alignment.md).

## Adapter consistency rules

- A human surface action, confirmed human chat plan, and AI agent tool that perform the same operation must share one governed tool id unless their authority, inputs, side effects, or approval semantics truly differ.
- Human surface availability never grants AI tool authority. AI availability requires an explicit `agent_tool_call` adapter and tool-boundary entry.
- Confirmed human chat execution is human-backed: the model proposes a plan, but deterministic backend authorization and explicit confirmation execute it.
- System paths such as workflows, timers, consumers, endpoints, MCP, and internal calls are workers or harnesses with provenance and authority, not invisible shortcuts.
- Frontend visibility, prompt wording, route availability, hidden fields, and component method names are never authorization controls.

## Minimum graph sufficiency checklist

Before a feature-bearing slice can be compiled, verify:

- [ ] The owning app/domain/workstream or valid cross-cutting/system-only scope is named.
- [ ] The owning workstream has lifecycle/alignment state, including whether implementation is aligned, stale, partially aligned, not started, blocked, or unknown.
- [ ] Responsible workers and worker types are explicit.
- [ ] Harnesses and actor adapters are declared for every exposure path.
- [ ] Consequential operations and governed evidence reads have stable governed tool ids.
- [ ] Governed tools map to capability contracts, not directly to raw endpoints or component methods.
- [ ] Capability, data/state, policy, trace, test, and realization links exist or are intentionally absent with a reason.
- [ ] Human-backed, AI-backed, and system-backed adapters share the governed tool when they share semantics.
- [ ] Authorization, tenant/customer scope, confirmation/approval, idempotency, denial, trace, and partial-failure behavior are captured.
- [ ] Akka substrates are justified by capability shape.
- [ ] Source-alignment entries map feature-bearing app-description files to source/frontend/API/test/validation artifacts or explicitly mark them not-started, description-only, blocked, or unknown.
- [ ] Automated tests and at least one manual runtime scenario identify the worker + adapter + governed tool + capability + implementation path.

## Anti-patterns

Avoid compiling or accepting graph updates that:

- define only a page, route, dashboard, or component without worker/tool/capability context;
- define only an Akka component, endpoint, or method without product capability context;
- define an agent tool as a separate business operation from the matching human surface action;
- grant agent authority because a human has a visible browser action;
- omit system workers and provenance for workflows, timers, consumers, projections, integrations, and internal calls;
- skip negative, tenant-isolation, approval/confirmation, idempotency, trace, or manual runtime validation expectations;
- change feature-bearing workstream intent without updating `realization/source-alignment.md`, flagging the related implementation as stale, or recording an explicit no-code-impact alignment decision.

See also [App-description source alignment](app-description-source-alignment.md), [App-description to code compile contract](app-description-to-code-compile-contract.md), and [Intent to realization flow](intent-to-realization-flow.md).
