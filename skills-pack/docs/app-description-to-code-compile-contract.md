# App-description to code compile contract

This document defines how an accepted current-intent delta becomes a bounded implementation slice. It uses the graph semantics in [App-description component graph](app-description-component-graph.md), the worker/tool chain in [App worker and governed-tool model](app-worker-tool-model.md), and the lifecycle phases in [App Development Lifecycle](app-development-lifecycle.md).

## Scope

Use this contract for build/compile tasks that realize app-description intent as specs, task briefs, documentation, code, tests, configuration, or validation paths. For docs-only skills-pack maintenance, the same contract applies at the documentation level: the task must still name the graph doctrine it changes, the affected downstream contracts, and the required non-runtime checks.

## Compile inputs

A compile task starts only when the requested slice has enough current-intent provenance to avoid guessing:

- accepted current-intent delta or existing app-description graph node(s);
- impacted app/domain/workstream or explicit cross-cutting/foundation/system-only scope;
- owning workstream lifecycle/alignment state, including whether implementation is already aligned, stale, partially aligned, not started, blocked, or unknown;
- responsible workers and worker types;
- harnesses and actor adapters for each exposure path;
- governed tools and capability contracts;
- security, AuthContext, tenant/customer scope, confirmation/approval, idempotency, and denial semantics;
- trace/audit/work events and outcome evidence;
- implementation target area and selected or candidate Akka/frontend/API/agent substrates;
- source-alignment artifact or planned alignment entry mapping app-description files to source/frontend/API/test/validation files;
- required automated checks and runtime-validation scenario/setup path, or an explicit docs-only/non-runtime exemption.

If a feature-bearing task only names a page, route, endpoint, component, or agent tool, repair the graph/task brief or block with a pending question before implementation.

## Minimum compile chain

```text
accepted intent delta
  -> affected graph nodes
  -> workers and harnesses
  -> actor adapters
  -> governed tools
  -> capabilities and data/state
  -> policies, traces, tests, and runtime-validation scenarios
  -> selected Akka substrates
  -> frontend/API/agent/runtime adapters
  -> source-alignment entries
  -> bounded repository changes
  -> automated checks
  -> runtime-validation scenario/run
  -> reconciliation output
```

Every item downstream should cite or inherit the upstream graph node that justifies it.

## Compile stages

### 1. Confirm scope and provenance

- Identify the accepted intent delta or graph nodes being compiled.
- Name the lifecycle phase and readiness target: usually `compile-ready` before edits and `manual-ready` after checks pass.
- Read and update the owning workstream lifecycle/alignment record.
- If the app-description changed since the last compile/alignment review, treat related implementation as `stale-description-changed` unless an explicit no-code-impact review says otherwise.
- Confirm the task is one bounded slice and does not start later queued work.
- List affected current-intent, spec, task, implementation, test, and validation artifacts.
- Read or create the owning workstream `realization/source-alignment.md` entry for the selected slice. If the app-description files are newer than mapped implementation files, begin from `stale-description-changed` unless a no-code-impact review is recorded.

### 2. Derive the worker/tool contract

For each operation, read, trigger, or result surface:

- name the responsible worker(s) and worker type(s);
- name the harness: surface, browser shell, confirmed chat plan, Akka Agent, AutonomousAgent, workflow, timer, consumer, endpoint, MCP, internal service, or integration;
- name the actor adapter: `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `mcp_tool_call`, `api_call`, or `internal_call`;
- map each adapter to one governed tool id;
- map the governed tool to a capability id and capability contract;
- preserve adapter-specific confirmation, approval, autonomy, idempotency, result, partial-failure, denial, and trace behavior.

### 3. Select implementation substrates

Choose Akka and frontend substrates from capability shape, not from page or CRUD intuition.

| Capability shape | Typical substrate |
|---|---|
| Audit-grade facts, decisions, approvals, policy changes, lifecycle history | Event Sourced Entity |
| Current-state profile, settings, config, simple repository | Key Value Entity |
| Long-running plan, approval wait, retry, compensation | Workflow |
| Query, dashboard, search, reporting, alternate lookup | View |
| Event/topic/service-stream reaction | Consumer |
| Deadline, expiry, reminder, digest, replay | Timer + Timed Action |
| Request/response model-backed reasoning | Akka Agent |
| Durable background model-backed task lifecycle | AutonomousAgent |
| Browser/API boundary | HTTP endpoint and API client |
| Remote model/client tool boundary | MCP endpoint |
| Human workstream execution harness | React/Vite structured surface rendering |

A governed tool may compile to multiple substrates. The selected components are implementation evidence for one semantic operation, not independent product definitions.

### 4. Realize adapters without duplicating operations

Implement one shared governed operation and attach declared adapters to it:

- human `surface_action` adapters should call the shared capability/governed-tool path through backend authorization;
- `human_chat_tool_plan` adapters should create a proposed plan, require explicit confirmation bound to that plan, then call the same governed tool with human AuthContext;
- `agent_tool_call` adapters should be registered only when the agent tool boundary explicitly includes the governed tool and policy allows the requested authority;
- workflow/timer/consumer/API/MCP/internal adapters should pass provenance, correlation, authority basis, idempotency, and trace source to the same capability contract.

Do not create parallel human and AI business implementations for the same operation.

### 5. Preserve security, traces, and tests

The implementation slice should include or update the smallest tests that prove:

- allowed and forbidden access for each declared adapter;
- tenant/customer isolation and AuthContext resolution;
- confirmation/approval/autonomy behavior;
- idempotency and transaction boundary;
- stale, duplicate, retry, partial-failure, and denial behavior when applicable;
- audit/work trace source and correlation differences per adapter;
- result surface/system message/attention update behavior;
- component-level behavior and endpoint/API/frontend wiring;
- runtime-validation path at the claimed readiness level.

### 6. Update source alignment

Before final reconciliation, update the owning workstream `realization/source-alignment.md` so the implemented slice records:

- the app-description files that drove the change;
- backend, frontend, resources, specs, tests, and validation files that realize the slice;
- checks and runtime-validation scenario/run evidence used for the latest alignment decision;
- unmapped feature-bearing description files or implementation files, with `not-started`, `description-only`, `blocked`, `generated-output`, or `unknown` reasons;
- no-code-impact review notes when description files changed but implementation did not need to.

If a machine-readable checker exists, update the optional `source-alignment.json` consistently with the Markdown artifact. See [App-description source alignment](app-description-source-alignment.md).

### 7. Reconcile outcomes

After automated checks and runtime validation when applicable:

- update task status and validation evidence;
- update workstream lifecycle/alignment state, including links to source-alignment entries, last compile, last alignment review, last runtime-validation run, blockers, and next action;
- reconcile review or runtime findings as current-intent updates, implementation repairs, test repairs, follow-up tasks, or blockers;
- never let implementation details silently supersede app-description intent.

## Pending-task and task-brief minimum checklist

A queued build/compile task or task brief should include:

- [ ] Task id, source, dependencies, and one-slice scope.
- [ ] Required reads and focused skills.
- [ ] Lifecycle phase and readiness target.
- [ ] Current-intent provenance: app/domain/workstream/global graph nodes or explicit cross-cutting docs-only scope.
- [ ] Workstream lifecycle/alignment state and expected state transition for this task.
- [ ] Responsible worker(s), worker type(s), harnesses, and actor adapters.
- [ ] Governed tool id(s), capability id(s), and selected exposure channels.
- [ ] AuthContext, tenant/customer scope, authorization, denial, confirmation/approval, idempotency, side-effect, partial-failure, and trace obligations.
- [ ] Selected Akka/frontend/API/agent/runtime substrates or a docs-only/non-runtime exemption.
- [ ] Source-alignment entry to create/update, including mapped app-description files and expected source/frontend/API/test/validation files.
- [ ] Expected outputs and changed artifact categories.
- [ ] Required automated checks.
- [ ] Runtime-validation scenario/setup path or explicit statement that runtime evidence is not applicable.
- [ ] Done criteria tied to graph completeness, implementation behavior, tests, and validation evidence.
- [ ] Queue/commit requirements and next-task discovery rule.

## Bounded implementation slice definition

A bounded implementation slice is the smallest coherent set of repository changes that realizes one graph intent increment. It may touch multiple layers when needed, but each touched layer must be justified by the same worker/tool/capability path.

A slice is too broad when it:

- starts unrelated queued tasks;
- adds component families not required by the capability shape;
- changes adjacent workstreams without graph impact justification;
- rewrites style, routing, or architecture not needed for the governed tool path.

A slice is too narrow when it:

- leaves a declared adapter without authorization, result, trace, or test behavior;
- implements only frontend visibility without backend capability enforcement;
- implements only an endpoint/component method without the surface/agent/system adapter that exposes it;
- omits tests or runtime-validation scenario for a feature-bearing behavior.

## Completion evidence levels

Use the readiness vocabulary from [App Development Lifecycle](app-development-lifecycle.md) and [Intent to realization flow](intent-to-realization-flow.md):

- `description-ready`: graph intent is complete enough for a bounded task;
- `compile-ready`: task inputs are coherent enough for implementation without guessing;
- `manual-ready`: required automated checks pass and the runtime-validation path is known;
- `runtime-ready`: the real local API/UI/agent path works at the claimed scope, or the task is explicitly non-runtime and has satisfied its stated checks.

Docs-only skills-pack tasks may complete at `manual-ready` with `runtime evidence: not applicable` when their required repository checks pass and their link/search evidence proves discoverability.

## Compile blockers

Block or create a pending question when safe compilation would require guessing:

- owning workstream or cross-cutting scope;
- worker responsibility, authority, or supervision;
- whether human and AI paths share a governed tool or are distinct operations;
- AuthContext, tenant/customer scope, role/capability authorization, denial behavior;
- approval/confirmation/autonomy threshold;
- side effects, idempotency, transaction boundary, or partial-failure behavior;
- trace/audit/outcome visibility;
- whether a workstream app-description change is no-code-impact or requires stale implementation to be recompiled;
- selected capability contract or Akka substrate;
- required runtime validation path.

## Anti-patterns prevented by this contract

- Page-only tasks that do not identify workers, tools, capabilities, traces, and tests.
- Component-only tasks that select Akka substrates before capability semantics.
- Prompt-only or agent-tool-only tasks that bypass shared governed tools.
- Duplicate human and AI implementations for the same business operation.
- Surface visibility or route access treated as authorization.
- Runtime completion claimed from fixture/demo/mock behavior for normal app paths.
- Missing denial, approval, idempotency, tenant-isolation, trace, or runtime-validation expectations.

See also [App-description source alignment](app-description-source-alignment.md), [Intent to realization flow](intent-to-realization-flow.md), and [Intent compiler skill contracts](intent-compiler-skill-contracts.md).
