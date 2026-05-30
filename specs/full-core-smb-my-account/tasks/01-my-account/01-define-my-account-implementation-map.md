# Task: Define My Account vertical slice contracts and implementation map

## Objective

Inspect current My Account, `/api/me`, selected context, authority, profile/settings, personal attention, trace refs, workstream navigation, agent/worker, frontend user-tile/surface, and test boundaries. Produce the SMB My Account implementation map and append bounded source-edit tasks.

## Required reads

Use the required reads listed on `TASK-FCSMB-MA-01-001` in `pending-tasks.md`.

## In scope

- Define My Account vertical slices and capability contracts.
- Discover backend account/context/settings/attention/navigation/runtime/source boundaries.
- Discover frontend user tile, context indicator, My Account surface/action/fixture/test boundaries.
- Identify deterministic responsibilities for `/api/me`, context resolution, profile/settings updates, attention filtering, trace refs, workstream launch authority, tenant isolation, idempotency, redaction, and traces.
- Identify model-backed responsibilities for MyAccountAgent guidance and any later personal digest worker.
- Append bounded backend/frontend/validation tasks with exact paths and commands.

## Out of scope

- Do not implement source edits in this inspection task.
- Do not add My Account to the top rail.
- Do not broaden to identity-provider administration or administrative mutation scope.
- Do not let model-backed guidance update context/profile/authority directly.

## Expected outputs

- `specs/full-core-smb-my-account/my-account-implementation-map.md`
- updated `specs/full-core-smb-my-account/pending-tasks.md`
- task briefs for appended implementation and validation tasks

## Required checks

- `git diff --check`
- targeted `find`/`rg` source discovery commands
- queue evidence search listed in the task entry

## Done criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The map clearly separates deterministic account/context/settings/attention/navigation responsibilities from governed model-backed guidance/worker responsibilities.
- The queue contains bounded next tasks and a terminal verification loop.

## Commit message

- `full-core-smb: map my account full core`
