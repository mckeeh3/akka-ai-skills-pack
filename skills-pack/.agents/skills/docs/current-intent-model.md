# Current Intent Model

Current intent is the clean, consolidated description of what the application should be now. It is compiled from incremental input, but it should not retain obsolete alternatives, conversation chronology, or superseded user phrasing.

## App-description intent graph

Represent app intent as a file-backed graph. Directory names identify artifact types; file names identify concrete artifact instances. [App-description component graph](app-description-component-graph.md) defines the canonical node families and required links in more detail.

```text
app-description/
  app.md
  global/
    actors/<actor>.md
    roles/<role>.md
    workers/<worker>.md
    policies/<policy>.md
    surfaces/<surface-pattern>.md
    agents/<agent>.md
    tools/<tool>.md
    traces/<trace-pattern>.md
  domains/<domain>/
    domain.md
    capabilities/<capability>.md
    data-state/<state-object>.md
    workstreams/<workstream>/
      workstream.md
      access.md
      behavior.md
      workers/<worker-binding-or-local-worker>.md
      surfaces/<surface-instance-or-binding>.md
      agents/<agent-binding-or-local-agent>.md
      tools/<tool-binding-or-local-tool>.md
      policies/<policy-binding-or-local-policy>.md
      traces/<trace-binding-or-local-trace>.md
      tests/<test-expectation>.md
      realization/akka-components.md
      realization/frontend-routes.md
      realization/api-contracts.md
```

This hierarchy expresses primary ownership, not exclusive reuse. Cross-links are expected when a workstream uses global definitions or shared domain capabilities.

## Graph node responsibilities

- `app.md`: objective, operating model, tenant/customer assumptions, global non-goals, and cross-domain outcomes.
- `global/actors` and `global/roles`: canonical actor and role definitions reused across domains and workstreams, including human-backed actors, AI-backed actors, and service actors where applicable.
- `global/workers`: canonical worker definitions reused across domains and workstreams, including human workers, functional-agent workers, internal-agent workers, autonomous-agent workers, evaluator-agent workers, and deterministic system workers.
- `global/policies`, `global/surfaces`, `global/agents`, `global/tools`, `global/traces`: reusable definitions that answer "what is this artifact?". Global tools define governed workstream operations; surface actions, confirmed human chat tool plans, agent tools, APIs, workflows, timers, consumers, MCP-tools, and internal calls are actor- or caller-specific exposure adapters unless explicitly local-only.
- `domains/<domain>/domain.md`: domain purpose, boundaries, owned capabilities, and data/state responsibilities.
- `capabilities/<capability>.md`: business capability contract, actors, outcomes, authorization, and realization references.
- `data-state/<state-object>.md`: durable state, lifecycle, invariants, retention, and trace obligations.
- `workstreams/<workstream>/**`: the operational binding where purpose, access, surfaces, agents, tools, policies, traces, tests, and realization become executable.

## Workstream binding model

A global artifact defines the reusable contract. A workstream binding defines why and how that contract is used in a specific workstream.

Global tool definition:

```text
app-description/global/tools/create-invitation.md
```

Workstream binding:

```text
app-description/domains/admin/workstreams/user-onboarding/tools/create-invitation.md
```

The binding should state:

- `Uses:` link to the global artifact, or identify a local-only artifact;
- the shared governed tool id and capability id;
- the human-backed surface exposure, such as the surface action, form, confirmation, result surface, and trace source;
- the human-backed chat exposure, if allowed, including proposed-plan detail, explicit confirmation binding, idempotency/transaction behavior, result/partial-failure surfaces, and trace source `human_chat_tool_plan`;
- the AI-backed actor exposure, such as the agent tool schema, tool boundary entry, approval policy, and trace source;
- any workflow, timer, consumer, MCP endpoint, API, or internal path that invokes it;
- workstream-specific authorization and approval rules per actor adapter;
- allowed inputs, effects, and denial behavior;
- audit/work trace events, including `requestedBy` when an AI-backed action is initiated from a human supervisor request and `confirmedBy`/confirmation id when a human chat tool plan is executed;
- tests and runtime validation expectations.

## Centrality of workstreams

Workstreams are the main operational unit for generated AI-first SaaS apps. They bind together:

- purpose and desired outcomes;
- authorized roles and access state;
- worker roster, including human, functional-agent, internal/autonomous/evaluator agent, and system workers;
- worker responsibilities, authority, supervision, handoffs, and failure behavior;
- human surfaces and action edges;
- functional agents and authority limits;
- human-backed and AI-backed actor adapters;
- governed tools and capability APIs;
- policies, approvals, and exception paths;
- traces, audit, and outcome metrics;
- Akka components, frontend routes, API contracts, and tests.

Access should be modeled at workstream level first, then compiled into surface visibility, capability permission, tool permission, endpoint/component authorization, and trace obligations.

## Editing principle

When new input changes intent, update the affected current graph nodes to their new intended state. Do not append historical notes such as "previously we thought..." unless the artifact is explicitly a changelog, task record, or migration note.

See also [Incremental intent processing](incremental-intent-processing.md), [Intent to realization flow](intent-to-realization-flow.md), and [App-description component graph](app-description-component-graph.md).

## Lifecycle relationship

Use [App Development Lifecycle](app-development-lifecycle.md) as the canonical three-phase routing doctrine for moving current intent through interview, build/compile, and manual runtime test phases. The current-intent model remains the living app-description graph that each phase reads from and reconciles back into.
