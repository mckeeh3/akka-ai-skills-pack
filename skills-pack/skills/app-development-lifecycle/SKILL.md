---
name: app-development-lifecycle
description: "Explain and apply the canonical three-phase app development lifecycle for continuous app evolution: interview, build/compile, and runtime validation. Use when users ask about the app development process, lifecycle phases, request-stream handling, readiness levels, or how app-description changes become implemented and runtime-validated."
---

# App Development Lifecycle

Use this skill when the user asks about the app development lifecycle, development process, request stream, phase model, readiness vocabulary, or how a feature/spec/tweak/fix moves from intent to code to runtime validation.

## Lifecycle classification

- phase: cross-phase
- kind: focused doctrine explainer
- family: lifecycle / app-description / realization / verification
- consumes: user lifecycle/process question, current app-description/spec/task context when relevant
- produces: concise lifecycle explanation, phase routing guidance, readiness clarification, or next safe process step
- routes-to: `app-descriptions`, `app-description-input-normalization`, `app-description-intake-router`, `app-generate-app`, `akka-solution-decomposition`, `akka-do-next-pending-task`, `akka-runtime-feature-verification`, `akka-manual-failure-reconciliation`

## Required reading

Read first:

- `../docs/app-development-lifecycle.md`

When applying the lifecycle to a concrete app change, also read the smallest relevant subset:

- `../docs/app-description-component-graph.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/app-worker-tool-model.md`
- `../docs/runtime-validation.md` when runtime-validation scenarios, setup prerequisites, execution modes, or accumulated validation runs are in scope
- `../docs/runtime-validation-task-authoring.md` when creating runtime-validation tasks, seed plans, WorkOS test-user setup, or human UI scripts
- `../docs/runtime-validation-reconciliation.md`
- target-project `app-description/**`, `specs/**`, or selected pending-task brief when the user asks about a specific change

## Core answer

The canonical app development lifecycle has three iterative phases:

```text
1. Interview / intent reconciliation
2. Build / compile / implement
3. Runtime validation / reconciliation (`runtime-validation run` legacy wording), driven by generated workstream-surface scenarios and seed plans
```

It is not a waterfall. Every feature request, bug report, issue, tweak, runtime-validation observation, review finding, or clarification is part of a continuous request stream that updates current intent and may start another pass through the loop. Track this state per workstream so app-description changes can flag implementation as stale until reviewed or recompiled. Every input should advance to `done`, `partially-done-blocked`, `decomposed-to-tasks`, or `blocked-before-work`; do not merely analyze and stop without a concrete state transition.

### 1. Interview / intent reconciliation

Capture and reconcile intent until the app-description can represent the requested change. The app-description is the living current-intent graph, not a disposable generation scaffold.

Typical outputs:

- accepted intent delta;
- affected app-description nodes;
- workers, surfaces, tools, capabilities, policies, tests, and observability implications;
- assumptions, blockers, or pending questions;
- bounded tasks when implementation is ready.

Readiness target: `description-ready`.

### 2. Build / compile / implement

Compile description-ready intent or a selected queued task into repository changes.

Typical outputs:

- docs/spec/task/code/test/config changes;
- implementation slices that preserve `worker -> execution harness -> actor adapter -> governed tool -> capability -> Akka implementation`;
- validation evidence and task status updates;
- a commit when the task is complete.

Readiness targets: `compile-ready` and then `manual-ready`.

### 3. Runtime validation / reconciliation

Exercise the real local runtime path through documented runtime-validation scenarios and reconcile any mismatch back into app-description, specs, tasks, code, seed/setup plans, runtime-validation runs, or blockers. Runtime-validation tasks are first-class generated acceptance contracts: start from the workstream surface or non-UI trigger, run the local app from empty persistence, seed scenario prerequisites through the declared CLI/setup plan, then have the human/browser/API/scripted executor perform the validation.

Typical outputs:

- runtime-validation scenario/run notes;
- seed plan and setup prerequisite evidence;
- human UI validation script or executor instructions;
- runtime evidence;
- failure classification;
- remediation tasks, blocked questions, or confirmation that behavior is runtime-ready.

Readiness target: `runtime-ready`.

## Workstream lifecycle/alignment tracking

Each feature-bearing workstream should have lifecycle state in the app-description or companion lifecycle artifact. At minimum track:

- readiness: `draft`, `description-ready`, `compile-ready`, `manual-ready`, `runtime-ready`, or `blocked`;
- implementation alignment: `not-started`, `aligned`, `stale-description-changed`, `stale-code-changed`, `partially-aligned`, or `unknown`;
- latest app-description version/digest or changed-node list;
- implementation evidence and last compile/alignment/runtime-validation notes;
- blockers and next recommended action.

Default rule: when a workstream app-description change affects feature-bearing intent, mark related code as `stale-description-changed` and reduce readiness to no higher than `compile-ready` unless an alignment review explicitly records no code impact.

## Routing guidance

- If the request is ambiguous or changes product behavior, route to Interview/app-description work first.
- If current intent is clear and a bounded task exists, route to Build/compile implementation.
- If a workstream changed in app-description, first route to alignment review or compile; do not assume existing code is current.
- If implementation is present but confidence depends on real behavior, route to runtime validation.
- If runtime validation discovers a mismatch, classify it before patching: app-description gap, implementation gap, test gap, provider/config blocker, seed/demo-data gap, UX/state gap, or expectation change.

## Guardrails

- Do not skip app-description reconciliation for consequential behavior changes.
- Do not count mocked, fixture-only, demo-only, or model-less behavior as runtime-ready.
- Do not treat frontend visibility as authorization.
- Do not duplicate a business operation separately for human surface actions and AI agent tools; compile one governed tool with explicit actor adapters.
- For docs-only or skills-pack maintenance tasks, runtime evidence can be not applicable, but required repository checks still apply.
