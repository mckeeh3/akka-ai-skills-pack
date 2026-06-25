# Intent to Realization Flow

Intent realization maps the current intent graph to specs, task queues, generated code, tests, and runtime validation evidence. It should not realize stale historical intent or isolated component work without a traceable reason. Use [App-description component graph](app-description-component-graph.md) for canonical graph node families and links, [App worker and governed-tool model](app-worker-tool-model.md) for the worker, execution harness, actor adapter, governed tool, capability, and Akka implementation chain, and [App-description to code compile contract](app-description-to-code-compile-contract.md) for bounded build/compile slices.

## Realization chain

Use this chain for generated app work:

```text
current intent graph
  -> solution/slice spec
    -> build backlog
      -> task brief
        -> implementation changes
          -> tests and checks
            -> local runtime/API/UI validation
              -> audit/work trace and outcome evidence
```

Each downstream artifact should cite the upstream current-intent node or accepted spec that justifies it.

## Phase-aware routing

Use the lifecycle phase to decide which skill family owns the next step:

| Phase | Routing purpose | Typical outputs |
|---|---|---|
| `interview` | Convert user/business input, review findings, or manual-test observations into current-intent graph changes or blockers. | normalized input, app-description deltas, pending questions, description-ready task scope |
| `build-compile` | Compile description-ready graph nodes or one selected queued task into maintained artifacts. | specs, task briefs, docs, code, tests, configuration, automated-check evidence, manual-ready path |
| `manual-test` | Prove or falsify the real runtime/API/UI/agent path and feed findings back into the request stream. | runtime evidence, reconciliation findings, app-description/spec/task/code/test repairs, blockers |
| `cross-phase` | Route broad architecture, foundation, pack-maintenance, or review work without replacing the focused phase skills. | route decision, impact map, readiness summary, doctrine/metadata updates |

The standard routing metadata fields are `phase`, `kind`, `family`, `consumes`, `produces`, and `routes-to`; see [Intent compiler skill contracts](intent-compiler-skill-contracts.md). These fields are descriptive contracts for humans and harnesses, not permission to skip required reads or task-specific checks.

## Worker/tool/capability routing chain

Feature-bearing realization should preserve this route before implementation mechanics are chosen:

```text
worker
  -> execution harness
    -> actor adapter
      -> governed tool
        -> capability
          -> Akka implementation
```

Interview skills should discover or repair missing workers, harnesses, adapters, tools, capabilities, policies, traces, and tests. Build/compile skills should inherit the chain and implement only the selected adapters and substrates. Manual-test skills should verify the same chain through the real path and classify any break at the exact layer that failed.

## Workstream vertical contract

Feature-bearing implementation tasks should inherit or state a vertical contract before coding:

- workstream or explicit internal/foundation/cross-cutting scope;
- responsible worker(s), worker type, actor adapter, authority level, and handoff/escalation path;
- attention category or non-attention reason;
- role-specific dashboard or non-UI trigger;
- human surface graph node and action edge, when user-facing;
- governed tool id for the shared capability-backed operation;
- actor adapter and exposure channel, such as `human-backed` + `surface_action`, `human-backed` + `human_chat_tool_plan`, `ai-backed` + `agent_tool_call`, `service` + `workflow`, or timer/consumer/API/MCP exposure;
- whether the operation is exposed to human-backed actors, AI-backed actors, both, or neither in this workstream;
- capability id/API exposure;
- selected Akka substrate and package path;
- functional-agent delegation/result surface, when agent-backed;
- autonomous task/result/notification mapping, when AutonomousAgent-backed;
- `AuthContext`, authorization denial behavior, and tenant/customer scoping;
- audit/work trace obligations, including trace source, `requestedBy` relationship for AI-mediated human requests, and `confirmedBy`/confirmation id for confirmed human chat tool plans;
- local validation path.

If the contract is missing and cannot be inherited from current intent/specs, repair the task brief or block with a pending question.

## Mapping to Akka and frontend artifacts

Current intent should drive implementation choices:

- Capabilities map to HTTP/gRPC/MCP/API contracts, governed tool contracts, workflows, entities, consumers, timers, views, or agents.
- Workstream surfaces map to frontend routes, structured panels/cards/forms, SSE/WebSocket subscriptions, browser API clients, and human-backed surface-action adapters for governed tools.
- Worker bindings map human roles, functional agents, internal/autonomous/evaluator agents, and system workers to responsibilities, surfaces, capabilities, handoffs, and trace obligations.
- Agent bindings map to concrete Akka `Agent` or `AutonomousAgent` components, model policy, governed prompts/skills/references, explicit `agent_tool_call` adapters, tool boundaries, memory, guardrails, and traces.
- Data-state artifacts map to EventSourcedEntity or KeyValueEntity state, events/commands, views, retention, and tests.
- Policies map to authorization checks, approval workflows, denial responses, audit events, and negative tests.
- Trace bindings map to durable audit/work traces and investigation surfaces.

## Actor-adapter realization rule

Do not realize the same business operation twice because it is reachable through a surface, confirmed human chat plan, and/or AI agent. Compile one governed workstream tool and then realize each declared actor adapter against it. Human surface availability does not automatically grant AI tool availability; an AI-backed workstream agent may perform or propose the operation only when the governed tool is explicitly included in its tool boundary and approval policy. Confirmed human chat execution is a human-backed adapter: the model may propose a plan, but execution requires explicit confirmation bound to that plan and deterministic backend authorization. Tests should cover direct human surface action, confirmed chat plan execution/denial/partial failure when allowed, AI-mediated tool call when allowed, AI denial when not allowed, shared capability authorization, idempotency, result/system-message surfaces, and trace differences.

## Runtime validation doctrine

A feature is complete only when the intended local runtime path works at the stated scope. Prefer the real Akka/API/UI path over duplicated deterministic demos or fixture-only checks.

- Provider-backed user-visible behavior should fail closed when configuration is missing and should not be counted as implemented through mocks.
- Tests may use fixtures and test doubles, but the normal runtime path must remain real and governed.
- Required validation that cannot run should block completion unless the task is explicitly docs-only, planning-only, or non-runtime.
- Completion evidence must name the readiness level achieved: `described`, `surface-ready`, `backend-ready`, `frontend-rendered`, `api-smoked`, `browser-smoked`, `manual-ready`, or `runtime-ready`.
- `runtime-ready` requires evidence for the real path from user/browser/surface action or non-UI trigger through API/endpoint/client, Akka substrate/component/service, authorization, side effect/view/projection, and audit/work trace.
- Unit/service/contract/typecheck/build evidence may support completion, but cannot alone close a user-visible runtime feature.

## Planning outputs

Planning and queue skills should preserve provenance:

- specs should link to current-intent graph nodes;
- backlog items should identify the capability/workstream/surface/agent/tool/component they realize;
- task briefs should carry required reads, done criteria, checks, and validation path;
- pending tasks should execute one bounded realization or maintenance step at a time.

## Drift repair

When code, tests, or runtime behavior drift from current intent, choose one of three explicit repairs:

1. update code/tests/runtime behavior to match current intent;
2. update current intent because implementation discovery changed the accepted product design;
3. block with a pending question because the correct direction is ambiguous.

Do not silently let implementation details become de facto product intent.

See also [Intent Compiler](intent-compiler.md), [Current intent model](current-intent-model.md), [App-description component graph](app-description-component-graph.md), and [App-description to code compile contract](app-description-to-code-compile-contract.md).
