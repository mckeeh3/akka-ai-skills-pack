# Full-Core Five Workstreams

## Purpose

Plan the follow-up from the five-core v0 starter to full implementations of the core secure AI-first SaaS workstreams:

1. My Account
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

This is a source-repository mini-project for the `akka-ai-skills-pack`. It targets repository assets such as `docs/`, `skills/`, `templates/ai-first-saas-starter/`, examples, tests, and validation scripts. The generated outcome should teach and demonstrate **the current skills-pack way**: app requirements decompose through workstreams, surfaces, functional agents, internal agents, governed capabilities/governed-tools, Akka substrate, UI, traces, and local validation.

## Background

The starter template now has a production-oriented five-core v0 shell: one initial `markdown_response` surface per core workstream, real authorization, durable workstream logs, model-backed workstream responses, and explicit full-core/demo follow-up markers. The next step is not to put demo dashboards back into bootstrap. The next step is to design and implement explicit full-core surfaces, actions, capabilities, agent expertise, and tests behind deliberate workstream requests/actions.

## Canonical doctrine to preserve

Future sessions must reference these as the architectural source of truth:

- `docs/requirements-to-workstream-development-process.md` — THE WAY broad requirements become fully functional secure AI-first SaaS apps.
- `docs/agent-workstream-application-architecture.md` — workstream as root app abstraction; one functional/context-area agent per workstream; surface graph; internal workstream agent graph.
- `docs/structured-surface-contracts.md` — typed surface/action/event contracts.
- `docs/capability-first-backend-architecture.md` — governed capabilities/governed-tools before Akka component selection.
- `docs/ai-first-saas-application-architecture.md` — mandatory secure AI-first SaaS foundation and runtime completion doctrine.
- `docs/workstream-expertise-model.md` — functional-agent expertise, governed skills/references/manifests/loaders/tool boundaries/traces.

## Scope

In scope:

- planning and implementation tasks for full-core versions of the five core workstreams;
- typed surface contracts and action maps for dashboards, tables, forms, cards, diffs, timelines, policy/approval surfaces, and system messages;
- governed capability/governed-tool contracts for every surface action and protected query;
- request-based workstream agents for user-facing turns and internal/background agent candidates where durable work fits;
- managed-agent expertise bundles, model bindings, skills/references/manifests, loaders, tool boundaries, and traces;
- backend Akka substrate, HTTP APIs, frontend React surfaces, realtime behavior, tests, local validation, and docs/template sync.

Out of scope:

- replacing the v0 bootstrap shell with rich demo surfaces;
- app-specific domain workstreams beyond the five core foundation workstreams;
- fixture-only, deterministic, mock, simulated, model-less, or service-only provider-bypass normal runtime behavior;
- broad whole-repository refactors not needed for this initiative.

## Execution model

Execute one task per fresh harness session. Start from `pending-tasks.md`, select the first runnable pending task, read its task brief and required doctrine, implement only that task, run required checks, update the queue, and commit one focused change.

## Sprint sequence

1. **Contracts and decomposition** — convert full-core intent into workstream/surface/agent/capability contracts.
2. **Shared runtime substrate** — add reusable rich surface/action/runtime infrastructure needed by all five workstreams.
3. **Five core workstream verticals** — implement My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy one vertical at a time.
4. **Validation and handoff** — run full-stack validation, repair gaps, and document full-core readiness.

## Done state

This mini-project is complete when the starter template and pack guidance demonstrate full-core implementations of all five core workstreams through real local runtime/API/UI paths: backend authorization, durable Akka-backed state/projections, governed capabilities, concrete Akka Agent invocation where model-backed, typed surfaces/actions, audit/work traces, frontend rendering, tests, and local smoke validation.
