# Task: Define Governance/Policy vertical slice contracts and implementation map

## Objective

Inspect current Governance/Policy, policy/proposal, simulation, decision, workstream, agent/worker, frontend surface, and test boundaries. Produce the SMB Governance/Policy implementation map and append bounded source-edit tasks.

## Required reads

Use the required reads listed on `TASK-FCSMB-GP-01-001` in `pending-tasks.md`.

## In scope

- Define Governance/Policy vertical slices and capability contracts.
- Discover backend governance/policy/proposal/runtime/source boundaries.
- Discover frontend surface/action/fixture/test boundaries.
- Identify deterministic responsibilities for policy inventory, proposal lifecycle, simulation, approval/rejection, activation/rollback, authorization, tenant isolation, idempotency, redaction, and traces.
- Identify model-backed responsibilities for GovernancePolicyAgent guidance and any later policy-impact analysis worker.
- Append bounded backend/frontend/validation tasks with exact paths and commands.

## Out of scope

- Do not implement source edits in this inspection task.
- Do not broaden to enterprise compliance, policy-as-code suites, SIEM, legal hold, or governance-office workflows.
- Do not let model-backed guidance approve, activate, or rollback policy changes directly.

## Expected outputs

- `specs/full-core-smb-governance-policy/governance-policy-implementation-map.md`
- updated `specs/full-core-smb-governance-policy/pending-tasks.md`
- task briefs for appended implementation and validation tasks

## Required checks

- `git diff --check`
- targeted `find`/`rg` source discovery commands
- queue evidence search listed in the task entry

## Done criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The map clearly separates deterministic policy/proposal/simulation/decision responsibilities from governed model-backed guidance/worker responsibilities.
- The queue contains bounded next tasks and a terminal verification loop.

## Commit message

- `full-core-smb: map governance policy full core`
