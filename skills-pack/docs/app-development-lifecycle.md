# App Development Lifecycle

This document defines the canonical three-phase lifecycle used by the Akka AI skills pack when turning current intent into a runnable AI-first SaaS application or skills-pack-maintenance change. The lifecycle is intentionally iterative: every user request, review finding, runtime-validation result, and runtime observation is part of a never-ending request stream that updates the app's current intent and may start another pass through the phases.

## Lifecycle at a glance

1. **Interview phase**: capture and reconcile intent until the app-description can represent the requested change.
2. **Build/compile phase**: compile the app-description and queued task intent into code, docs, tests, and configuration changes.
3. **Runtime validation phase**: run the real local/runtime path through documented scenarios, reconcile failures back into current intent, and decide whether the change is runtime-ready or needs another loop. Human-operated app testing is one execution mode of runtime validation, not a separate lifecycle phase.

The phases are not a waterfall. A request can move forward, expose a gap, and then loop back with a more precise app-description, task, or implementation fix.

## App-developer input transaction contract

Every app-developer input must produce a concrete state transition. The input may be a high-level PRD, rough business notes, a feature request, a low-level bug report, a failed surface, a runtime-validation observation, or a direct coding ask. The harness should advance it as far as safely possible and then stop with an explicit terminal state.

Allowed terminal states:

- `done` — the requested scope was completed, with evidence and the achieved readiness level.
- `partially-done-blocked` — safe work was completed, then execution stopped with precise blocking questions or issues.
- `decomposed-to-tasks` — the input was converted into executable tasks, runtime-validation tasks, pending questions, or blocked items.
- `blocked-before-work` — no safe state transition was possible without a specific answer, prerequisite, or conflict resolution.

A response must not stop merely because the input was analyzed. It should end with what changed, what task(s) were created, what evidence exists, or the exact question/blocker that prevents the next transition.

Use these stop reasons consistently when relevant:

- `completed`
- `decomposed-to-tasks`
- `blocked-by-product-question`
- `blocked-by-security-question`
- `blocked-by-runtime-config`
- `blocked-by-provider-config`
- `blocked-by-missing-reproduction`
- `blocked-by-conflicting-intent`
- `blocked-by-unsafe-assumption`
- `blocked-by-failing-check`
- `blocked-by-out-of-scope-dependency`
- `runtime-validation-required`

## Coarse-to-fine decomposition contract

For broad app input, decompose from coarse intent toward executable work until the next step would require guessing:

```text
raw input
  -> app/domain/cross-cutting impact
    -> workstreams and responsible workers
      -> execution harnesses, actor adapters, capabilities, and governed tools
        -> surfaces and runtime paths
          -> Akka/frontend/API realization choices
            -> implementation, remediation, or runtime-validation tasks
```

The source of truth remains the app-description/current-intent graph. The decomposition tree is an execution view that creates graph updates, questions, blockers, task briefs, or queue entries. Stop only with a clear question, blocker, unsafe assumption, validation need, or completed transition.

## Continuous request and reconciliation loop

Treat app development as a durable stream of requests rather than a finite prompt-response exchange:

- new product asks, clarification answers, user feedback, review findings, failing tests, and runtime-validation observations all become request-stream inputs;
- each input is reconciled against the current app-description and pending task queue before implementation scope is changed;
- contradictions, missing decisions, or unsafe runtime assumptions are captured as questions or blockers instead of being silently implemented;
- completed work updates the living current-intent graph and leaves evidence that the next task can use without replaying the full conversation.

When a runtime-validation run, runtime-validation/browser test, or review discovers a mismatch, the result is not merely a bug note. It is a lifecycle input that should update the app-description, backlog, task queue, implementation, runtime-validation corpus, or verification notes at the smallest appropriate scope.

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

**Non-goals:** Do not broaden scope to adjacent queued tasks, replace runtime validation with static reasoning, or count mock/demo-only behavior as real application completion.

**Handoff:** The phase hands off when the change is **compile-ready** and then **manual-ready**: compile-ready means repository artifacts are coherent enough for implementation checks; manual-ready means automated checks required by the task pass and the real user/API/runtime path plus any needed runtime-validation scenario/setup prerequisites have been identified.

### 3. Runtime validation phase

**Purpose:** Prove the implemented behavior through documented runtime-validation scenarios on the real local runtime path at the scope claimed by the task, then reconcile any mismatch.

**Inputs:** Manual-ready implementation, runtime configuration, test user or tenant context, expected behavior, runtime-validation scenario or charter, required audit/work-trace evidence, setup prerequisites, and prior validation output.

**Outputs:** Runtime-validation run notes, scenario pass/fail/block results, runtime evidence, failure reproduction details, app-description/backlog/task updates, follow-up remediation tasks, blocked prerequisites, or confirmation that the change is runtime-ready.

**Non-goals:** Do not treat deterministic fixtures, simulations, mocked provider behavior, frontend-only screenshots, or setup shortcuts that bypass the governed runtime path as proof of consequential runtime behavior.

**Handoff:** The phase hands off when the change is **runtime-ready** or explicitly not runtime-applicable. Runtime-ready means the real local/API/UI/agent path works at the stated scope with required authorization, provider, audit, trace, and error behavior. For docs-only or planning-only skills-pack work, runtime evidence can be not applicable, but the task must say so and still provide the required repository checks.

See [Runtime validation](runtime-validation.md) for scenario catalogs, setup prerequisites, execution modes (`human-manual`, `browser-agent`, `api-agent`, `scripted-e2e`), evidence rules, and accumulated validation runs.

## App-description as the living current-intent graph

The app-description is the durable current-intent graph, not a disposable scaffold. It should remain aligned with the latest accepted request stream and implementation reality. Lifecycle work should therefore:

- preserve app-description nodes for product goals, workstreams/workers, surfaces, capabilities/tools, data, policies, integrations, verification expectations, and known blockers;
- update the graph when implementation or runtime validation changes the accepted intent;
- avoid treating generated code as the only source of truth when the app-description has not been reconciled;
- make each pending task small enough to compile one coherent slice of the graph into repository changes.

## Workstream lifecycle state and stale-code flagging

Track lifecycle state per workstream, not only per task. A workstream lifecycle record should summarize the current app-description readiness, implementation alignment, evidence, blockers, and next action for that workstream.

Recommended alignment states:

- `not-started`: intent exists, but no implementation evidence is known.
- `aligned`: implementation and tests reflect the current app-description intent at the stated readiness level.
- `stale-description-changed`: app-description intent changed after the last compile/alignment review, so related code may be stale.
- `stale-code-changed`: code changed without a matching app-description reconciliation.
- `partially-aligned`: some affected paths are aligned, but known gaps remain.
- `unknown`: evidence is insufficient to decide.

Default rule: when a feature-bearing app-description node under a workstream changes, mark that workstream's implementation alignment as `stale-description-changed` and reduce readiness to no higher than `compile-ready` until either:

1. an alignment review proves the change is description-only or no-code-impact; or
2. the affected workstream slice is compiled, checked, and then runtime-validated as needed.

This makes queries such as "which workstreams are stale relative to current intent?" first-class lifecycle operations.

## Readiness vocabulary

Use these readiness terms consistently:

- **Description-ready:** intent is clear enough to update or confirm the app-description and define a bounded task.
- **Compile-ready:** app-description/task inputs are coherent enough for a worker to make implementation or documentation changes without guessing product decisions.
- **Manual-ready:** automated checks required by the task pass and the real runtime path or explicit non-runtime validation path is known.
- **Runtime-ready:** the real local runtime/API/UI/agent path works at the claimed scope, or the task is explicitly non-runtime and has satisfied its stated checks.

These terms are evidence levels, not labels for skipping phases. A change can move backward from manual-ready to description-ready when runtime testing reveals a missing decision or an incorrect model. A workstream can also move backward from runtime-ready/manual-ready to compile-ready when its app-description changes and the implementation is flagged `stale-description-changed`.

## Skills-pack task implications

For skills-pack maintenance tasks:

- planning skills should capture the lifecycle phase and the readiness target in task briefs;
- implementation skills should compile only the selected task and preserve the app-description/current-intent contract;
- verification skills should feed failures and gaps back into the request stream as reconciled current intent, follow-up tasks, or blockers;
- queue updates and commits are part of lifecycle evidence, not administrative afterthoughts.
