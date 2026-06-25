# App Development Lifecycle

This document defines the canonical three-phase lifecycle used by the Akka AI skills pack when turning current intent into a runnable AI-first SaaS application or skills-pack-maintenance change. The lifecycle is intentionally iterative: every user request, review finding, manual test result, and runtime observation is part of a never-ending request stream that updates the app's current intent and may start another pass through the phases.

## Lifecycle at a glance

1. **Interview phase**: capture and reconcile intent until the app-description can represent the requested change.
2. **Build/compile phase**: compile the app-description and queued task intent into code, docs, tests, and configuration changes.
3. **Manual runtime test phase**: run the real local/runtime path, reconcile failures back into current intent, and decide whether the change is runtime-ready or needs another loop.

The phases are not a waterfall. A request can move forward, expose a gap, and then loop back with a more precise app-description, task, or implementation fix.

## Continuous request and reconciliation loop

Treat app development as a durable stream of requests rather than a finite prompt-response exchange:

- new product asks, clarification answers, user feedback, review findings, failing tests, and manual runtime observations all become request-stream inputs;
- each input is reconciled against the current app-description and pending task queue before implementation scope is changed;
- contradictions, missing decisions, or unsafe runtime assumptions are captured as questions or blockers instead of being silently implemented;
- completed work updates the living current-intent graph and leaves evidence that the next task can use without replaying the full conversation.

When a manual runtime test or review discovers a mismatch, the result is not merely a bug note. It is a lifecycle input that should update the app-description, backlog, task queue, implementation, or verification notes at the smallest appropriate scope.

## Phase contracts

### 1. Interview phase

**Purpose:** Turn ambiguous or evolving requests into app-description-ready current intent.

**Inputs:** User requests, prior conversation capture, existing app-description graph, relevant specs/backlog, pending questions, security/runtime constraints, and review findings.

**Outputs:** Updated or confirmed current intent, app-description deltas, answered questions, explicitly blocked decisions, and bounded pending tasks when implementation is ready.

**Non-goals:** Do not write speculative runtime code, invent product decisions, or mark implementation readiness only because the conversation sounds plausible.

**Handoff:** The phase hands off when the requested change is **description-ready**: the app-description can state the desired workers, surfaces, capabilities, data, policies, and verification expectations well enough for a bounded build task, or the remaining unknowns are captured as blockers.

### 2. Build/compile phase

**Purpose:** Compile app-description intent and task scope into concrete repository changes.

**Inputs:** Description-ready app-description state, selected pending task, required skill guidance, existing code/docs/tests, and explicit validation requirements.

**Outputs:** Focused source, documentation, app-description, spec, test, or configuration changes; updated task status; validation evidence; and a commit when the task is complete.

**Non-goals:** Do not broaden scope to adjacent queued tasks, replace manual runtime verification with static reasoning, or count mock/demo-only behavior as real application completion.

**Handoff:** The phase hands off when the change is **compile-ready** and then **manual-ready**: compile-ready means repository artifacts are coherent enough for implementation checks; manual-ready means automated checks required by the task pass and the real user/API/runtime path to test has been identified.

### 3. Manual runtime test phase

**Purpose:** Prove the implemented behavior through the real local runtime path at the scope claimed by the task, then reconcile any mismatch.

**Inputs:** Manual-ready implementation, runtime configuration, test user or tenant context, expected behavior, required audit/work-trace evidence, and prior validation output.

**Outputs:** Manual test notes, runtime evidence, failure reproduction details, app-description/backlog/task updates, follow-up tasks, or confirmation that the change is runtime-ready.

**Non-goals:** Do not treat deterministic fixtures, simulations, mocked provider behavior, or screenshots without the governed runtime path as proof of consequential runtime behavior.

**Handoff:** The phase hands off when the change is **runtime-ready** or explicitly not runtime-applicable. Runtime-ready means the real local/API/UI/agent path works at the stated scope with required authorization, provider, audit, trace, and error behavior. For docs-only or planning-only skills-pack work, runtime evidence can be not applicable, but the task must say so and still provide the required repository checks.

## App-description as the living current-intent graph

The app-description is the durable current-intent graph, not a disposable scaffold. It should remain aligned with the latest accepted request stream and implementation reality. Lifecycle work should therefore:

- preserve app-description nodes for product goals, workstreams/workers, surfaces, capabilities/tools, data, policies, integrations, verification expectations, and known blockers;
- update the graph when implementation or manual testing changes the accepted intent;
- avoid treating generated code as the only source of truth when the app-description has not been reconciled;
- make each pending task small enough to compile one coherent slice of the graph into repository changes.

## Readiness vocabulary

Use these readiness terms consistently:

- **Description-ready:** intent is clear enough to update or confirm the app-description and define a bounded task.
- **Compile-ready:** app-description/task inputs are coherent enough for a worker to make implementation or documentation changes without guessing product decisions.
- **Manual-ready:** automated checks required by the task pass and the real runtime path or explicit non-runtime validation path is known.
- **Runtime-ready:** the real local runtime/API/UI/agent path works at the claimed scope, or the task is explicitly non-runtime and has satisfied its stated checks.

These terms are evidence levels, not labels for skipping phases. A change can move backward from manual-ready to description-ready when runtime testing reveals a missing decision or an incorrect model.

## Skills-pack task implications

For skills-pack maintenance tasks:

- planning skills should capture the lifecycle phase and the readiness target in task briefs;
- implementation skills should compile only the selected task and preserve the app-description/current-intent contract;
- verification skills should feed failures and gaps back into the request stream as reconciled current intent, follow-up tasks, or blockers;
- queue updates and commits are part of lifecycle evidence, not administrative afterthoughts.
