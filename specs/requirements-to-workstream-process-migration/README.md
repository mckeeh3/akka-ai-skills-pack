# Requirements-to-Workstream Process Migration

## Purpose

Realign the skills pack's app-development process around the newer secure AI-first SaaS architecture clarity:

```text
any user input / PRD / feature request
→ secure SaaS foundation
→ workstream inventory
→ per-workstream "what needs my attention?" breakdown
→ dashboards
→ surfaces and surface actions
→ governed APIs/capabilities
→ Akka components
→ request-based workstream agents
→ internal/background workers, often Akka AutonomousAgent tasks
→ events/messages/notifications
→ attention projections and audit/work traces
```

The pack should treat this as the default processing model for generated secure AI-first SaaS applications. Large PRDs should be decomposed into one or more workstreams. Each workstream starts from attention needs and dashboard meaning, then expands into surfaces, actions, capabilities, APIs, Akka components, and internal workers. Human users and internal agents are both participants/workers that ask or answer "what do I need to do next?" within governed authority boundaries.

## Background

The current pack already contains strong doctrine for secure AI-first SaaS, agent workstreams, structured surfaces, capability-first backend design, and Akka Autonomous Agents. However, earlier intake/planning guidance was originally closer to traditional CRUD Akka applications with event-driven backend processing. That older framing can still leak through in PRD processing, app-description normalization, solution plans, backlog generation, examples, and queue mechanics.

This initiative migrates the requirements/input processing path so the newer workstream-dashboard-attention-autonomous-task model is prescriptive, guided, and implementation-ready rather than optional or incidental.

Primary source concept document:
- `docs/workstream-dashboard-attention-event-backbone-wip.md`

Related doctrine:
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-component-selection-guide.md`
- `docs/workstream-ui-reference-architecture.md`

## Scope

Affected repository areas likely include:

- top-level routing: `AGENTS.md`, `pack/AGENTS.md`, `skills/README.md`;
- high-level intake skills: `ai-first-saas`, `agent-workstream-apps`, `app-description-input-normalization`, `app-description-intake-router`;
- app-description skills: bootstrap, functional-agent modeling, surface modeling, capability modeling, UI, readiness, generation;
- direct planning skills: `akka-solution-decomposition`, `akka-prd-to-specs-backlog`, revised PRD/change request/spec backlog skills, pending question/task generation;
- planning docs: `prd-to-akka-flow.md`, `module-sprint-planning.md`, `solution-plan-to-implementation-queue.md`, `pending-task-queue.md`, `pending-question-queue.md`;
- examples and seed descriptions, especially AI-first SaaS seed app-description and starter planning references;
- packaging/export lists if new docs or skills become installable assets.

## Non-goals

- Do not replace request-based workstream functional agents with Autonomous Agents by default.
- Do not weaken secure SaaS foundation, local authorization, tenant/customer isolation, audit/work traces, or governed managed-agent runtime requirements.
- Do not remove capability-first backend modeling; the new process uses dashboards/surfaces/attention to discover capabilities, then capability contracts remain the backend authority.
- Do not force every app to use every possible autonomous-agent pattern. Use Autonomous Agents where durable task-oriented internal/background work fits.
- Do not treat this repository as an end-user SaaS app; this is a skills-pack source migration.

## Required future read order

For every task in this mini-project, start with:

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/workstream-dashboard-attention-event-backbone-wip.md`
4. `specs/requirements-to-workstream-process-migration/README.md`
5. `specs/requirements-to-workstream-process-migration/conversation-capture.md`
6. `specs/requirements-to-workstream-process-migration/pending-tasks.md`
7. selected sprint, backlog, and task brief

Then read only the focused skills/docs listed by the selected task.

## Sprint sequence

1. **Audit and target model** — inventory remaining input-processing, CRUD-first, page-first, component-first, and event-only drift; define the target process contract.
2. **Doctrine consolidation** — promote/refine the WIP concepts into canonical docs or canonical sections.
3. **Intake and description-first realignment** — update normalization, intake routing, app-description bootstrap/modeling/readiness to use workstream → attention → dashboard → surfaces → capabilities → autonomous tasks.
4. **PRD/spec/backlog planning realignment** — update solution decomposition, PRD-to-specs, revised/change flows, and planning docs to produce vertical workstream/attention/surface/capability/autonomous-task plans.
5. **Queue and task contract realignment** — ensure generated pending tasks carry the new vertical contract and autonomous task lifecycle/surface expectations.
6. **Examples, seed, and packaging alignment** — update examples, seed app-description references, starter guidance, and packaged docs/manifest as needed.
7. **Verification loop** — verify no major stale process path remains; append follow-up tasks for any gaps.

## Done state

This initiative is complete when:

- any broad app input handled by the skills pack is normalized into workstreams, attention needs, dashboards, surfaces, actions, governed capabilities, agent/worker participants, events/messages/notifications, traces, and selected Akka substrates;
- large PRDs are split by workstream and vertical attention/surface/capability increments rather than CRUD modules or component families first;
- every workstream starts with a "what needs my attention?" dashboard/attention model unless explicitly non-applicable;
- dashboards link to surfaces or invoke governed surface/actions; surfaces map to UI components and governed actions; actions invoke APIs/capabilities; APIs invoke Akka components;
- internal/background agents are represented as workers and usually routed to Akka `AutonomousAgent` when durable task lifecycle fits;
- request-based Akka `Agent` remains the default for immediate user-facing workstream turns;
- planning/backlog/queue artifacts preserve attention categories, dashboard/surface contracts, capability ids, API/exposure paths, selected Akka substrate, autonomous task definitions/results/notifications where applicable, auth/security, audit/work traces, and tests;
- final verification finds no major stale CRUD-first, page-first, chatbot-bolt-on, or component-first default path in app input processing.

## Current recommendations

- Treat `docs/workstream-dashboard-attention-event-backbone-wip.md` as the source concept until canonical docs are updated.
- Add a compact canonical process doc rather than scattering the concept only across existing docs.
- Update skills in dependency order: routing/intake first, then description-first, then PRD/spec/backlog, then queue/task docs, then examples.
- Keep this migration separate from autonomous-agent component API work; this initiative is about generated-app development process realignment.
