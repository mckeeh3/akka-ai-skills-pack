# TASK-CORE-04-001: Align core workstream API contracts

## Purpose

Define or update typed frontend/API fixture contracts for full-core functional agents and surfaces.

## Required reads

- `docs/workstream-ui-reference-architecture.md`
- `docs/structured-surface-contracts.md`
- `frontend/src/workstream/**`
- `specs/core-app-full-stack-readiness/user-admin-reference-slice.md`
- `specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md`

## Expected outputs

- updates to frontend fixture/API contracts and/or `docs/workstream-ui-reference-architecture.md`
- optional `specs/core-app-full-stack-readiness/core-workstream-api-contracts.md`

## Required checks

- Contracts cover Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
- Surface actions map to capability ids and safe denial/redaction states.
- frontend checks/build if frontend code changes.
- `git diff --check`

## Done criteria

- Workstream UI has realistic full-core contracts instead of placeholders.
- Queue status and changes are committed.
