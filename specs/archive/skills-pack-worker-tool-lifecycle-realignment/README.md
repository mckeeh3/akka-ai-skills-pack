# Skills-Pack Worker/Tool/Lifecycle Realignment

## Purpose

Realign `skills-pack/` so it supports continuous AI-assisted app evolution through a clear lifecycle and a tighter app modeling architecture.

The skills-pack currently has strong focused skills and doctrine, but the overall development lifecycle and the worker/tool/capability abstraction need to become first-class. This mini-project creates that spine and migrates the pack toward it in controlled, sequential tasks.

## Current intent

The pack should treat app development as a never-ending stream of app feature/spec/tweak/adjustment/fix/issue/manual-test inputs. Each input should be routed through a repeatable loop:

```text
interview / intent reconciliation
  -> build-compile / implementation
  -> manual runtime test / reconciliation
  -> back to interview
```

The app-description is the living current-intent graph. It should describe the app from high-level purpose down through domains, workstreams, workers, surfaces, agents, tools, capabilities, Akka components, tests, security, observability, and manual runtime evaluation.

## Architectural thesis

Applications should be modeled as work performed by workers through governed tools.

```text
worker -> actor adapter / harness -> governed tool -> capability -> Akka implementation
```

Workers include human workers, AI-backed software agents, and deterministic system workers. Human workers use surfaces as their governed execution harness. AI-backed workers use agent runtimes as their governed execution harness. System workers use workflows, timers, consumers, integrations, APIs, or internal deterministic logic.

Tools are first-class semantic app building blocks. They are the abstraction layer between workers/adapters and backend capabilities. They are not merely endpoints, UI buttons, or Akka component wrappers.

## Done state

This mini-project is complete when:

- `skills-pack/docs/**` contains canonical doctrine for the three-phase lifecycle, worker/tool model, app-description component graph, compile contract, and manual-test reconciliation.
- `skills-pack/skills/README.md` routes work through the three-phase lifecycle and worker/tool/capability model.
- A standard skill classification/metadata contract exists and is applied to representative skills.
- A pilot set of representative skills is migrated to the new doctrine.
- Major skill families have been migrated or have explicit follow-up tasks.
- Broad orchestrator skills are shorter routing contracts where practical and point to shared docs instead of repeating doctrine.
- App-description, worker decomposition, surface, capability, tool, and realization docs use consistent terminology.
- Pack validation checks pass for changed assets.
- A terminal verification task determines whether the full mini-project is complete or appends follow-up tasks plus a new terminal verification task.

## Non-goals

- Do not rewrite every skill in one task.
- Do not remove public skill names casually.
- Do not implement root SaaS Foundation App runtime behavior as part of this pack-maintenance mini-project.
- Do not treat `skills-pack/.agents/skills/**` as the source of truth.
- Do not collapse tools into endpoints, UI actions, or Akka components.
- Do not allow human surface availability to imply AI-agent tool availability.

## Execution model

Use `specs/skills-pack-worker-tool-lifecycle-realignment/pending-tasks.md`.

Execute exactly one task per fresh harness context. Each implementation task should:

1. mark only that task `in-progress`;
2. perform only the scoped edits;
3. run the required checks;
4. mark the task `done` or `blocked`;
5. commit completed changes when the task criteria pass;
6. report the next runnable task.

Do not run queued tasks in parallel.

## Supporting documents

- `conversation-capture.md` — accepted decisions and important nuance from the discussion.
- `target-architecture.md` — intended conceptual architecture for lifecycle, workers, tools, capabilities, and app-description graph structure.
- `migration-strategy.md` — ordered migration plan and skill family waves.
- `pending-tasks.md` — durable task queue.
- `tasks/*.md` — focused task briefs for queued work.
