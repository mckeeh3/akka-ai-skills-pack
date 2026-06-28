# TASK-003: Define app-description graph and compile contract

## Scope

Make workers and tools first-class in the app-description graph and define the app-description-to-code compile contract.

## Required reads

- `skills-pack/docs/current-intent-model.md`
- `skills-pack/docs/intent-compiler.md`
- `skills-pack/docs/incremental-intent-processing.md`
- `skills-pack/docs/intent-to-realization-flow.md`
- `skills-pack/docs/intent-compiler-skill-contracts.md`
- `skills-pack/docs/app-development-lifecycle.md` if present
- `skills-pack/docs/app-worker-tool-model.md` if present
- `specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md`

## Expected outputs

- New `skills-pack/docs/app-description-component-graph.md`.
- New `skills-pack/docs/app-description-to-code-compile-contract.md`.
- Focused updates to `intent-to-realization-flow.md` and/or `intent-compiler-skill-contracts.md` if needed.

## Done criteria

- Defines canonical app-description node families, including workers and tools.
- Defines required graph links among workers, surfaces, agents, tools, capabilities, Akka components, tests, traces, and manual scenarios.
- Defines how an accepted current-intent delta becomes a bounded implementation slice.
- Prevents page-only, component-only, and duplicate human/AI operation implementations.
- Includes a minimum compile checklist suitable for pending-task/task-brief use.

## Required checks

- `git diff --check`
- Search proof that new docs are linked from existing intent/realization docs.
