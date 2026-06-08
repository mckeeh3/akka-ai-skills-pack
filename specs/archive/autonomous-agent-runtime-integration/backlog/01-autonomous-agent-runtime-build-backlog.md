# Backlog: AutonomousAgent Runtime Integration

## Goal

Create bounded tasks for the first real durable internal/background agent vertical: User Admin Access Review AutonomousAgent.

## Task breakdown

1. Define runtime contract and confirm Akka SDK patterns.
2. Implement backend AutonomousAgent task lifecycle and governed capability paths.
3. Wire task lifecycle events to v3 event backbone and attention/surfaces.
4. Validate scaffolded runtime behavior, provider fail-closed, and no fake success.
5. Update docs/handoff.
6. Verify completion.

## Required checks

Use targeted subsets of:

- `git diff --check`
- official Akka SDK docs/examples review for AutonomousAgent APIs
- scaffolded backend Maven tests for task lifecycle/runtime/fail-closed behavior
- frontend tests/typecheck/build for surfaces
- focused `rg` checks for `AutonomousAgent`, provider fail-closed, no fake success, event/attention linkage
