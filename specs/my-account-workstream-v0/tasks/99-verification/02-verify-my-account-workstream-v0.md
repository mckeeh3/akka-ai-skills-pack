# Task: Re-verify My Account Workstream v0 completion

## Objective

Re-run terminal verification for `My Account Workstream v0` after the provider-smoke compatibility follow-up is complete.

## Required inherited reads

- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`
- `specs/my-account-workstream-v0/README.md`
- `specs/my-account-workstream-v0/conversation-capture.md`
- `specs/my-account-workstream-v0/pending-tasks.md`
- `specs/my-account-workstream-v0/workstream-contract.md`
- `specs/my-account-workstream-v0/capability-inventory.md`

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`

## Done criteria

- Task-group goals, mini-project done state, shared five-core contract, and runtime/API/UI validation evidence are all compared against completed work.
- No normal runtime path is satisfied by deterministic/demo/mock/simulated/model-less substitutes.
- If complete, completion is recorded with no new required work.
- If gaps remain, append bounded follow-up tasks before a new terminal verification task.
