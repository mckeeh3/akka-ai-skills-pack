# TASK-001: Add canonical three-phase lifecycle doctrine

## Scope

Create the canonical lifecycle document for the skills-pack.

## Required reads

- `skills-pack/AGENTS.md`
- `skills-pack/README.md`
- `skills-pack/skills/README.md`
- `skills-pack/docs/intent-compiler.md`
- `skills-pack/docs/current-intent-model.md`
- `skills-pack/docs/intent-to-realization-flow.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/README.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/conversation-capture.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md`

## Expected outputs

- New `skills-pack/docs/app-development-lifecycle.md`.
- Minimal cross-links from existing lifecycle-adjacent docs if needed.

## Done criteria

- Defines interview, build/compile, and manual runtime test phases.
- Describes the never-ending request stream and reconciliation loop.
- Defines phase inputs, outputs, non-goals, and handoffs.
- Explains how app-description remains the living current-intent graph.
- Distinguishes description-ready, compile-ready, manual-ready, and runtime-ready style readiness without contradicting existing runtime completion doctrine.

## Required checks

- `git diff --check`
- Search proof that the new doc is referenced from at least one routing/current-intent doc or task note.
