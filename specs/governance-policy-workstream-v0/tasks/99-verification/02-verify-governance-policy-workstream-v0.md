# Task: Verify Governance Policy Workstream v0 completion after seed-boundary repair

## Objective

Re-run terminal verification for the `Governance Policy Workstream v0` mini-project after the seed-boundary validation repair.

## Required inherited reads

- AGENTS.md
- skills/README.md
- specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
- specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
- specs/governance-policy-workstream-v0/README.md
- specs/governance-policy-workstream-v0/conversation-capture.md
- specs/governance-policy-workstream-v0/pending-tasks.md
- specs/governance-policy-workstream-v0/sprints/*.md
- specs/governance-policy-workstream-v0/backlog/*.md
- specs/governance-policy-workstream-v0/tasks/**/*.md
- specs/governance-policy-workstream-v0/workstream-contract.md
- specs/governance-policy-workstream-v0/capability-inventory.md

## Shared-contract verification

Compare completed work against the shared five-core v0 contract, workstream dependency map, workstream contract, capability inventory, and runtime validation evidence. Confirm the queue still has a terminal verification task and no normal runtime path is satisfied by deterministic/demo/mock/simulated/model-less substitutes.

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`

## Done criteria

- Seed-boundary repair has been completed and committed.
- Fullstack starter validation passes or any remaining material gap is converted into bounded follow-up tasks before a new terminal verification task.
- If complete, completion is recorded with no new required work.
