# TASK-GPIA-03-001: Wire Governance/Policy events, attention, and surfaces

## Objective

Wire governance impact task lifecycle/results into v3 events, attention, and Governance/Policy structured surfaces.

## In scope

- `workflow.governance_policy.*` and `worker.task.*` events.
- Attention mappings for blocked/failed/impact-ready/accepted/rejected/request-changes states.
- Impact result/decision surfaces with evidence, risks, recommendations, alternatives, no direct activation, and human actions.
- Backend/frontend tests.

## Required checks

- `git diff --check`
- scaffolded backend tests
- frontend tests/typecheck/build if frontend changes

## Commit message

`governance-impact-agent: wire events attention surfaces`
