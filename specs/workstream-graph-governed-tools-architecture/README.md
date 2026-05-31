# Workstream Graph and Governed-Tools Architecture

## Purpose

Bake the workstream graph, role-specific dashboard, internal agent graph, workstream expertise, and governed-tool decomposition model into the skills pack as the normal way this pack consumes requirements and produces generated Akka applications.

This is not an optional enhancement. It is the architecture the pack should apply from requirements ingestion through design, implementation planning, generated code, runtime execution, and incremental change handling.

## Canonical decomposition model

For new PRDs, feature requests, fixes, and incremental changes against an existing app, the pack should decompose input as:

```text
input / PRD / change request
→ determine affected workstream(s)
→ for each affected workstream, determine role-specific dashboard surface(s)
→ identify attention items / work-to-do indicators
→ build or revise the human surface graph
→ build or revise the internal workstream agent graph
→ define governed-tools inside capability files and surface/action maps
→ map governed-tools to Akka substrate and exposure channels
→ validate through surface graph, governed-tool, runtime/API/UI, agent, audit, and trace tests
```

## Core concepts

### Workstream

A bounded area of consequential work. A large PRD may decompose into one workstream or many workstreams. Incremental input may add a workstream, modify one workstream, or affect multiple workstreams and shared governed-tools.

### Role-specific dashboard surface

A dashboard is a specific type of surface. It is not a generic analytics dashboard. Its primary objective is:

> Show what requires this actor's attention and what work can or should be done next in this workstream.

Dashboard behavior is role-driven and AuthContext-driven. The same workstream can have different dashboards for Tenant Admin, Customer Admin, Auditor, SaaS Owner support, or other roles.

### Attention item

An item that requires attention, work, decision, investigation, review, correction, approval, escalation, or monitoring. Attention may be backed by projections/views, computed sources, policy evaluation, internal-agent results, external state, or mixed evidence.

### Surface graph

The human work tree for a workstream. The dashboard is the trunk. Surface nodes are branches. Surface actions are graph edges. Edges may show another surface, invoke a browser-tool, create a system-message surface, update dashboard attention, start internal-agent work, open traces, or route to approval/decision surfaces.

### Internal workstream agent graph

Each workstream has an internal virtual dashboard agent view of the workstream. It asks what requires agent attention, what can be done by internal workers, what should be delegated, and what must be escalated to humans. Internal worker agents perform bounded delegated work using governed-tools, then resolve work, produce results/proposals, or create human attention items.

### Workstream expertise skills

Each workstream has one or more governed workstream skills or expertise documents. These describe the role-based workstream, dashboard purpose, surface graph, surface behavior, available governed-tools, authority boundaries, denials, and examples of what users can do. Workstream agents use this information to handle chat requests and help users understand and operate the workstream.

### Governed-tools

Use qualified tool terms only. Avoid ambiguous bare `tool` in architecture guidance.

- `governed-tool`: semantic executable operation with actor/caller rules, AuthContext, input/output schemas, side effects, idempotency, approval/policy, audit/work trace, and implementation mapping.
- `browser-tool`: governed-tool exposed to humans through surface actions and browser APIs.
- `agent-tool`: governed-tool exposed to request-based or internal agents through Akka `@FunctionTool`, MCP, component tools, or equivalent.
- `internal-tool`: governed-tool used by workflows, timers, consumers, internal services, or internal worker agents without direct browser exposure.
- `MCP-tool`, `workflow-tool`, `timer-tool`, or `consumer-tool`: qualified exposure terms when precision is needed.

For now, governed-tools live inside existing capability files and surface/action maps. Do not create a new top-level app-description layer such as `11-governed-tools/` unless a later accepted decision changes this.

### Capability

A product-level ability or grouping of related governed-tools. Capability files remain the app-description location where governed-tools are defined and linked to surfaces, actors, authority, traces, and Akka realization.

## Incremental input model

Requirements ingestion is not one-shot. For an app that already exists, every feature request, fix, adjustment, revised PRD, manual test finding, or support issue should be reconciled against the existing workstream graph:

- Does this affect one workstream or multiple workstreams?
- Which role-specific dashboards gain, lose, or change attention items?
- Which surface graph nodes or edges change?
- Which governed-tools are added, changed, reused, deprecated, or split?
- Which internal workstream agent graph nodes/delegations change?
- Which workstream expertise skills/references must be updated so agents can explain and operate the new behavior?
- Which Akka components, UI surfaces, APIs, tests, traces, and pending tasks are affected?

## Scope

In scope:

- Core architecture/doctrine docs.
- Requirements ingestion and PRD/spec/backlog skills.
- App-description skills and app-description architecture docs.
- Surface, workstream, internal-agent, governed-tool, and workstream-expertise guidance.
- Seed examples and canonical references that future agents use for decomposition.
- Queue/task docs where vertical contracts need graph/governed-tool fields.

Out of scope:

- Do not build an end-user Akka app in this mini-project.
- Do not create a new top-level governed-tools app-description directory.
- Do not reintroduce unqualified `tool` terminology where it can be confused with Akka agent tools.
- Do not weaken the secure SaaS foundation, managed-agent governance, authorization, audit, trace, or runtime-completion doctrines.

## Execution model

- Execute one task per fresh harness context.
- Read this README, `conversation-capture.md`, selected sprint, matching backlog, selected queue entry, and task brief before editing.
- Each task updates `pending-tasks.md` and makes one focused commit when complete.
- Verification tasks append new bounded tasks plus a new terminal verification task when gaps remain.

## Sprint sequence

1. `sprints/01-doctrine-vocabulary-sprint.md` — define canonical vocabulary and bake the graph/governed-tool model into core architecture docs.
2. `sprints/02-app-description-model-sprint.md` — update app-description structure and focused skills for role-specific dashboards, surface graphs, workstream expertise skills, internal agent graphs, and governed-tools inside capability/surface maps.
3. `sprints/03-intake-planning-sprint.md` — update PRD/intake/change/backlog/pending flows for both new-app and incremental-input decomposition.
4. `sprints/04-examples-and-implementation-routing-sprint.md` — update seed examples, UI/API guidance, agent guidance, and implementation routing to demonstrate the graph/governed-tool model.
5. `sprints/99-verification-sprint.md` — verify active guidance consistently applies the model and append follow-up tasks if needed.

## Done state

Complete means active pack guidance consistently treats the workstream graph and governed-tools model as the way this pack does requirements ingestion and implementation planning:

- broad PRDs decompose first into one or more workstreams;
- incremental inputs reconcile against existing workstream graphs instead of replanning from scratch;
- every workstream has role-specific dashboard surfaces that answer what requires attention;
- dashboard attention sources can be projections/views, computed sources, internal-agent results, policy/evidence, or external state, with traceability/freshness expectations;
- surface graphs are first-class in requirements, app-description, planning, UI/API, and testing guidance;
- internal virtual dashboard agents and workstream agent graphs are first-class for deciding what internal workers can do before humans are involved;
- workstream expertise skills/references describe role-based workstream behavior, dashboards, surfaces, governed-tools, denials, and user-help semantics;
- governed-tools live inside capability files and surface/action maps and are mapped to browser-tools, agent-tools, internal-tools, workflow/timer/consumer/MCP exposures, and Akka substrate;
- active skills avoid ambiguous bare `tool` language in architecture contexts where qualified terms are needed;
- planning queues carry workstream graph, governed-tool, workstream-expertise, AuthContext, audit/trace, and runtime validation context;
- canonical examples and seed app-description illustrate the model.

## Open concerns

No blocking user questions remain. A later implementation task may discover naming or file-structure details that require bounded follow-up tasks.
