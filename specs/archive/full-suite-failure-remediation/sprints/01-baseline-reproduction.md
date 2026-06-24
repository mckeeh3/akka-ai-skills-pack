# Sprint 01: Baseline Reproduction

## Goal

Reproduce the current exact full-suite failure list and classify each failure before making implementation changes.

## Scope

- Run `npm --prefix frontend test -- --run`.
- Run `npm --prefix frontend run typecheck`.
- Run `mvn test` or targeted suite/class runs if a full run is too expensive.
- Create `failure-inventory.md` with current failures, suspected owner, materiality, likely fix path, and task assignment.

## Completion signal

Sprint 01 is complete when all later tasks know which failures are still present and which clusters they own.
