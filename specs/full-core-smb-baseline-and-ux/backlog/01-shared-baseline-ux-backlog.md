# Backlog: Shared Baseline and UX

## Goal

Convert umbrella baseline/UX doctrine into implementation-ready shared contracts and validation tasks for the starter.

## Suggested harness task breakdown

1. Define executable shared baseline contracts and validation map.
2. Review starter/root frontend for shared shell/surface/visual gaps and queue follow-up implementation tasks if needed.
3. Verify the child project and append implementation tasks before completion if material gaps remain.

## Required checks

- `git diff --check`
- targeted `rg` checks proving workstream shell, structured surface, `system_message`, provider fail-closed, trace, visual, and runtime-validation contracts exist in this child project

## Acceptance criteria

- User Admin and later workstream child projects have a concrete shared baseline to inherit.
- Missing starter implementation work is represented as bounded tasks rather than hidden assumptions.
