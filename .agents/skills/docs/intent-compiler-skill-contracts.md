# Intent Compiler Skill Contracts

Intent-processing skills should declare which compiler role they perform and produce outputs that preserve current intent, workstream binding, and realization traceability. Use [App-description component graph](app-description-component-graph.md) for graph node families and [App-description to code compile contract](app-description-to-code-compile-contract.md) for bounded build/compile task expectations.

This document defines shared semantics, not a required JSON schema. A task-specific skill may use prose, Markdown tables, or structured blocks when the same information is clear.

## Standard skill classification fields

Use these fields in task briefs, skill front matter, routing notes, or manifest metadata when precise routing is needed. Existing public skill names and the current manifest `category` field remain valid; bulk manifest schema migration is deferred until a later validation task can update installer checks and all entries consistently.

| Field | Required meaning | Typical values |
|---|---|---|
| `phase` | Lifecycle phase where the skill primarily operates. Use `cross-phase` only when a skill intentionally routes or spans phases. | `interview`, `build-compile`, `manual-test`, `cross-phase` |
| `kind` | The skill's routing shape or responsibility style. | `orchestrator`, `focused`, `planning`, `testing`, `verification`, `docs` |
| `family` | The domain of concern used for smallest-safe-skill selection. | `app-description`, `worker`, `tool`, `capability`, `agent`, `web-ui`, `akka-component`, `endpoint`, `queue`, `verification`, `foundation`, `business-intake` |
| `consumes` | Artifact, graph, runtime, or evidence inputs the skill expects before acting. | user request, current-intent graph nodes, worker roster, governed-tool contract, capability contract, task brief, code, runtime evidence |
| `produces` | Durable outputs the skill is responsible for creating or updating. | app-description delta, normalized input, spec/backlog, task brief, code/tests, validation notes, reconciliation findings |
| `routes-to` | Focused next skills or families that should receive the output when more work remains. | skill ids or family names |

### Phase fit

- **Interview** skills capture, normalize, and reconcile current intent until the app-description can represent the requested change.
- **Build/compile** skills compile description-ready intent or a selected queued task into repository artifacts, checks, and manual-ready evidence.
- **Manual-test** skills run or interpret the real local/API/UI/agent path and reconcile failures into intent, specs, tasks, code, tests, or blockers.
- **Cross-phase** skills route across phases, preserve doctrine, or coordinate architecture/foundation decisions without replacing focused skills.

### Worker/tool/capability route

When a skill handles feature-bearing generated-app behavior, its `consumes`, `produces`, and `routes-to` fields should preserve this chain:

```text
worker -> execution harness -> actor adapter -> governed tool -> capability -> Akka implementation
```

A skill may focus on one link, but it should not erase adjacent links. For example, a web UI skill consumes a surface and `surface_action` adapter and routes to the shared governed tool/capability path; an agent-tool skill consumes an explicit `agent_tool_call` adapter and tool-boundary grant; an Akka component skill consumes the capability shape and produces implementation evidence rather than product authority.

## Skill role taxonomy

Each intent-facing skill should be one or more of:

- **intake/router**: classify user input and choose the smallest safe next skill;
- **normalizer**: turn flexible input into a current-intent delta;
- **capture/editor**: update app-description/current-intent graph artifacts;
- **impact assessor**: identify affected graph nodes, specs, tasks, code, tests, and validation paths;
- **planner**: materialize specs, backlogs, pending questions, task briefs, or pending tasks;
- **realizer**: generate or modify code/tests/configuration/runtime surfaces;
- **validator/reviewer**: compare artifacts or runtime behavior against current intent;
- **drift repairer**: reconcile mismatches between user input, intent artifacts, specs, queues, code, and validation evidence.

## Intake and normalization contract

Output should include:

- user increment summary;
- inferred intent kind and operation;
- affected app/global/domain/workstream nodes;
- current-state normalized delta;
- auth/security, tenant/customer, policy, trace, and test implications;
- realization implications if code/spec/task work is requested or required;
- unresolved ambiguities and whether they block action;
- recommended next skill or route.

The output should not force the user to know internal taxonomy.

## App-description capture contract

Changes to current intent artifacts should:

- edit the smallest complete set of app/global/domain/workstream files;
- for new or substantially changed browser-rendered workstream dashboards or structured surfaces, record the surface-description sufficiency review question and either a yes answer or the need for another description pass before implementation;
- preserve global definition plus workstream binding separation;
- remove or replace superseded current-state statements;
- link workstream artifacts to capabilities, surfaces, agents, tools, policies, traces, tests, and realization files;
- avoid appending conversation chronology to canonical intent;
- summarize changed graph nodes and downstream realization impact.

## Planning and queue contract

Specs, backlogs, task briefs, pending questions, and pending tasks should:

- cite current-intent graph provenance;
- keep one task bounded enough for a fresh harness session;
- name required reads and focused skills;
- carry dependencies and blockers explicitly;
- include done criteria, required checks, canonical runtime path, readiness target, and runtime-validation smoke path when feature-bearing;
- for browser-rendered structured surfaces or web UI realization, include required reads for `app-description-ui`, `akka-web-ui-ux-design`, `docs/web-ui-style-guide.md`, `docs/web-ui-component-catalog.md`, and `docs/web-ui-quality-checklist.md`, cite the selected app style/named-theme artifact, and confirm the surface-description sufficiency review has passed or is blocking;
- preserve the workstream vertical contract or state a valid docs-only/internal/foundation/cross-cutting exemption;
- include the minimum checklist from [App-description to code compile contract](app-description-to-code-compile-contract.md) for feature-bearing build/compile tasks.

## Realization contract

Code-generation or implementation skills should:

- verify current intent and task provenance before coding;
- choose Akka/frontend substrates from the accepted current intent and specs;
- for browser-rendered surfaces, preserve the selected UI style guide, named-theme contract, component-catalog anatomy, accessibility/responsive behavior, and surface graph semantics instead of inventing ad hoc page/card styling;
- preserve tenant/customer scope, authorization, policy denials, audit/work traces, provider fail-closed behavior, and frontend secret boundaries;
- implement tests at the smallest level that proves behavior;
- run required checks and local runtime/API/UI validation when in scope;
- record runtime evidence for feature-bearing changes, including readiness level, browser/surface/API/Akka path, role/AuthContext/tenant setup, denial case, provider configured/fail-closed status, trace/audit evidence, and commands/runtime-validation-smoke results;
- update or report drift in upstream intent/spec/task artifacts.

## Review and summary contract

Review outputs should report:

- changed current-intent graph nodes;
- generated or changed code/spec/test/runtime artifacts;
- validation commands and results;
- unresolved pending questions, risks, or drift;
- next safe route.

## Superseded source handling

If a user input replaces earlier intent, the active artifact should state only the accepted current intent. Historical context belongs in commits, task notes, archived docs, or explicit migration records.

See also [Intent Compiler](intent-compiler.md), [Incremental intent processing](incremental-intent-processing.md), [Intent to realization flow](intent-to-realization-flow.md), [App-description component graph](app-description-component-graph.md), and [App-description to code compile contract](app-description-to-code-compile-contract.md).
