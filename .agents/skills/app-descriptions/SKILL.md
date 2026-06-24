---
name: app-descriptions
description: Orchestrate intent-compiler app-description work across intake, current-intent graph editing, impact/readiness, generation, and review summaries when a request spans more than one app-description concern.
---

# App Descriptions

Use this as the top-level skill for app-description intent work.

This skill is for a harness operating model where the app description is the file-backed **current-intent graph** for the application. The maintained runnable implementation should stay consistent with that graph.

## Lifecycle classification

- Phase: cross-phase.
- Kind: orchestrator/router for app-description intent work.
- Family: app-description.
- Living-graph contract: coordinate maintenance of the app-description as the file-backed current-intent graph across app, global, domain, workstream, worker, execution-harness, actor-adapter, governed-tool, capability, trace, test, readiness, and realization nodes.
- Build/compile handoff: route planning, generation, code, tests, and validation through `../docs/app-description-to-code-compile-contract.md`; do not let generated output supersede unreconciled graph intent.

## Goal

Compile incremental user intent into current app-description artifacts, plans, code, tests, and validation evidence in a way that is:

- current-intent first
- lifecycle-aware: distinguish interview/current-intent work, build/compile work, and manual/runtime validation handoff
- workstream-centered for user-facing SaaS behavior
- explicit about app/global/domain/workstream graph nodes, including workers, execution harnesses, actor adapters, governed tools, capabilities, traces, tests, and realization links
- clear about behavior, tests, security, observability, and realization
- safe about readiness before generation
- suitable for prompt/response review, diff review, and focused file maintenance

## Required reading

Read only the smallest set needed for the request:

- target project path: `AGENTS.md`, when present
- `../README.md`, when present
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-development-lifecycle.md` when routing between interview, build/compile, and manual/runtime-validation concerns
- `../docs/app-worker-tool-model.md` and `../docs/app-description-component-graph.md` when workers, surfaces, tools, capabilities, agents, system triggers, or implementation readiness are in scope
- `../docs/app-description-to-code-compile-contract.md` when planning, generation, code, tests, or validation are requested
- `../docs/intent-to-realization-flow.md` when planning, generation, code, tests, or validation are requested
- `../docs/ai-first-saas-application-architecture.md` and `../ai-first-saas/SKILL.md` only when product intent involves delegated work, agents, decisions, governance, supervision, audit, or outcomes
- `../core-saas-foundation/SKILL.md` when starting or assessing generated SaaS foundation scope
- `../docs/web-ui-style-guide.md` when generated SaaS browser UI style or implementation is in scope
- current target-project `app-description/**`, `specs/**`, and queues when needed for maintenance, impact, readiness, planning, or realization

## Use this skill when

Use this top-level skill when the task spans more than one app-description concern, such as:

- a new app idea that needs a current-intent graph
- a broad product prompt that may need AI-first operating-model interpretation before graph updates
- a feature request that affects behavior, tests, and security together
- a bug or validation finding that requires drift repair between intent, specs, code, and tests
- a request to assess readiness and possibly generate the app
- a review question asking what changed or whether the app is ready

If the task is already clearly narrowed to one concern, load the focused companion skill directly.

## Companion skills

Load the companion skill that matches the current compiler step. Users do not need to name these skills; infer them from ordinary product language.

- `app-description-input-normalization`
  - convert flexible user input into a current-intent delta envelope
- `app-description-intake-router`
  - classify incremental intent and route to the smallest safe compiler skill
- `app-description-bootstrap`
  - create the initial app/global/domain/workstream graph for a new app or sparse app idea
- `app-description-functional-agent-modeling`
  - define or revise role-authorized workstream agents, authority, prompts, skills, tools, surfaces, traces, and tests
- `app-description-surface-modeling`
  - define or revise structured workstream surfaces, typed payloads, reusable surface bindings, actions, states, traces, and rendering tests
- `app-description-capability-modeling`
  - define or revise domain capabilities, actors, outcomes, authorization, and downstream realization links
- `app-description-behavior-specification`
  - update behavior rules, transitions, invariants, forbidden behavior, and no-op semantics
- `app-description-test-specification`
  - update acceptance, regression, negative, idempotency, security, and operational verification expectations
- `app-description-auth-security`
  - update identity, authorization, trust-boundary, sensitive-data, and denial-behavior rules
- `app-description-observability`
  - update logs, metrics, traces, audit events, health signals, alerts, and diagnosability expectations
- `app-description-ui`
  - update workstream UI, structured surfaces, surface actions, routes/deep links, frontend API contracts, realtime states, accessibility, responsive behavior, and style guide
- `app-description-change-impact`
  - determine affected graph nodes, specs, tasks, implementation areas, generated outputs, and validation paths
- `app-description-readiness-assessment`
  - decide whether current intent is `not-ready`, `ready-with-assumptions`, or `ready`
- `app-generate-app`
  - realize current intent as maintained runnable outputs with tests and validation
- `app-description-change-summary`
  - summarize current-intent changes after a revision request
- `app-description-readiness-summary`
  - summarize why current intent is or is not ready for generation

## Workstream graph ownership invariant

For generated full-stack SaaS, do not change capabilities, UI, readiness, generation scope, or implementation planning for a user-facing request without checking the relevant workstream graph nodes first.

Workstreams bind together access, workers, execution harnesses, surfaces, agents, tools, policies, traces, tests, capabilities, realization files, and validation expectations. Global artifacts define reusable contracts; workstream files define why and how those contracts are used in a specific workstream. For consequential operations, model one shared governed tool id in the capability/workstream tool binding and attach actor-specific adapters to it: human `surface_action`/browser-tool, confirmed `human_chat_tool_plan`, AI `agent_tool_call`, API/workflow/timer/consumer/MCP/internal exposure as applicable. Treat human workers, AI-backed functional/internal/autonomous workers, and system workers as distinct graph implications; none inherits authority from another worker's harness.

## Decision guide

### 1. The user is starting a new app description

Start with:

- `ai-first-saas` when the idea includes delegated work, agents, decisions, governance, supervision, audit, or outcomes
- `core-saas-foundation` when secure SaaS foundation scope is in play
- `app-description-bootstrap`
- then `app-description-intake-router`

### 2. The user is revising app meaning

Start with:

- `app-description-input-normalization` when the request is broad, mixed, or ambiguous
- `app-description-intake-router`
- then workstream/agent/surface/capability/behavior/test/security/observability companion skills in the smallest order that preserves traceability

For user-facing generated-SaaS requests, route through functional-agent and structured-surface modeling before capability/UI realization when the input names work areas, admin/operations portals, command centers, queues, approvals, decisions, audit timelines, workflow status, forms, tables, actions, or agent/chat areas.

### 3. The user is tightening production concerns

Start with:

- `app-description-input-normalization` when the request is broad, mixed, or ambiguous
- `app-description-intake-router`
- then `app-description-auth-security`, `app-description-observability`, `app-description-test-specification`, or policy/tool-boundary companion skills as applicable

### 4. The user asks whether the app is ready

Start with:

- `app-description-readiness-assessment`
- then `app-description-readiness-summary`

### 5. The user asks to plan or generate the app

Start with:

- readiness/impact assessment if current intent completeness is uncertain
- planning skills when the user needs specs/backlogs/tasks
- `app-generate-app` only after current intent and realization scope are clear

### 6. The user asks what changed

Start with:

- `app-description-change-summary`

## Current-intent editing rules

- Edit the smallest complete set of app/global/domain/workstream files.
- Use global definitions plus workstream-specific bindings for reusable surfaces, agents, tools, policies, and traces.
- Replace superseded statements rather than appending conversation chronology.
- Preserve the worker → execution harness → actor adapter → governed tool → capability → realization/test/trace chain; do not collapse it into pages, endpoints, prompts, or component methods.
- Preserve links from workstream nodes to workers, capabilities, governed tools, actor adapters, realization files, tests, and traces.
- Treat unresolved worker responsibility, authority, policy, data, trace, acceptance, or runtime-validation ambiguity as pending-question material.

## Final review checklist

Before finishing, verify:

- the request is routed to the smallest suitable companion skill and lifecycle phase
- current-intent graph nodes are identified or the reason they cannot be identified is explicit
- worker, execution-harness, actor-adapter, governed-tool, capability, trace, test, and realization implications are captured before build/compile work is treated as safe
- broad AI-first product input is interpreted before CRUD/component decomposition
- workstream bindings are preserved for user-facing generated-SaaS changes, including global definition plus workstream binding separation for governed tools, surfaces, agents, policies, and traces
- behavior, tests, security, UI, observability, and realization are kept separate when they change separately
- secure SaaS foundation and runtime-completion doctrine are preserved when in scope
- readiness is not skipped before generation
- review answers distinguish current-intent changes from generated/runtime changes

## Response style

When answering:

- identify whether the task is intake, normalization, current-intent maintenance, readiness, planning, generation, drift repair, or review
- load only the smallest relevant companion skills
- keep the interaction natural and prompt/response oriented
- preserve current-intent primacy throughout
