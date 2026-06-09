# Intent Compiler Skill Contracts

Intent-processing skills should declare which compiler role they perform and produce outputs that preserve current intent, workstream binding, and realization traceability.

This document defines shared semantics, not a required JSON schema. A task-specific skill may use prose, Markdown tables, or structured blocks when the same information is clear.

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
- include done criteria, required checks, and runtime validation path when feature-bearing;
- for browser-rendered structured surfaces or web UI realization, include required reads for `app-description-ui`, `akka-web-ui-ux-design`, `docs/web-ui-style-guide.md`, `docs/web-ui-component-catalog.md`, and `docs/web-ui-quality-checklist.md`, cite the selected app style/named-theme artifact, and confirm the surface-description sufficiency review has passed or is blocking;
- preserve the workstream vertical contract or state a valid docs-only/internal/foundation/cross-cutting exemption.

## Realization contract

Code-generation or implementation skills should:

- verify current intent and task provenance before coding;
- choose Akka/frontend substrates from the accepted current intent and specs;
- for browser-rendered surfaces, preserve the selected UI style guide, named-theme contract, component-catalog anatomy, accessibility/responsive behavior, and surface graph semantics instead of inventing ad hoc page/card styling;
- preserve tenant/customer scope, authorization, policy denials, audit/work traces, provider fail-closed behavior, and frontend secret boundaries;
- implement tests at the smallest level that proves behavior;
- run required checks and local runtime/API/UI validation when in scope;
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

See also [Intent Compiler](intent-compiler.md), [Incremental intent processing](incremental-intent-processing.md), and [Intent to realization flow](intent-to-realization-flow.md).
