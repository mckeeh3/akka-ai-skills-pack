# Agent Workstream Skills Realignment Plan

## Purpose

Realign the skills pack around the recently introduced agent workstream application model:

```text
secure SaaS foundation
→ functional/context-area agents
→ durable workstreams
→ typed structured surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

The goal is to make this model the default decomposition path for high-level intent, PRDs, app descriptions, backlog generation, UI work, agent work, and focused Akka component implementation.

## Why this plan exists

The pack has strong new assets for agent workstreams and structured surfaces, but alignment is uneven. Some skills still allow page-first, component-first, endpoint-first, or generic capability-first decomposition that can bypass functional agents and surfaces. That makes full-core starter implementation planning too cautious and slice-oriented instead of vertical, workstream-first, and end-to-end.

## Execution model

- Execute one task per fresh harness session.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this README, the selected sprint, matching backlog, selected task entry, and task brief before editing.
- Each task must update `pending-tasks.md` before completion.
- Each task must run required checks or document why a check could not run.
- Each task must make one git commit before being marked `done`.
- At the end of every sprint, run a skills review task that identifies remaining refinement areas and either:
  - confirms the sprint is complete with no new tasks needed, or
  - adds the next sprint/backlog/tasks/pending queue entries.

## Installed-pack dogfooding

The skills pack has been installed into this source repository under `.agents/` for dogfooding. `.agents/` is gitignored and should be treated as installed-pack output, not source.

When reviewing alignment, compare source files under `skills/` and `docs/` against installed-pack behavior under `.agents/skills/` and `.agents/docs/` when useful, but commit changes only to source files and specs.

## Sprint sequence

1. `sprints/01-routing-intake-sprint.md` — align top-level routing and intake so broad intent always flows through agent workstreams and structured surfaces before capabilities/components.
2. `sprints/02-planning-description-sprint.md` — align app-description and PRD/spec/backlog generation around functional agents, surfaces, and surface-action capabilities.
3. `sprints/03-implementation-skills-sprint.md` — align web UI, agent, endpoint, and Akka component implementation skills to consume a standard workstream/surface/capability input contract.
4. `sprints/04-starter-dogfood-sprint.md` — apply the aligned skills to the starter-app planning queue and replace vague slice tasks with end-to-end workstream/surface/capability implementation tasks.

Additional sprints must be added by sprint review tasks until the review concludes the pack is consistently aligned.

## Done state

Realignment is complete when:

- high-level product input is routed through secure SaaS foundation, functional agents, durable workstreams, structured surfaces, and capability contracts before Akka components;
- app-description artifacts keep application meaning in `12-workstreams/` and browser realization in `55-ui/`;
- PRD/spec/backlog generation creates vertical workstream/surface/capability tasks instead of vague component slices;
- web UI skills implement the agent workstream shell and typed structured surfaces by default;
- agent skills distinguish user-facing functional agents from internal backend agents and preserve governed behavior boundaries;
- component skills expect an explicit workstream/surface/capability input contract before coding;
- the starter app task queue reflects full-core workstream-first implementation rather than partial durable slices;
- final review finds no major stale page-first, CRUD-first, chatbot-bolt-on, or component-first default paths remaining.
