# Task: Define Agent Admin vertical slice contracts and implementation map

## Objective

Inspect current Agent Admin, managed-agent runtime, seed, tool-boundary, workstream, frontend surface, and test boundaries. Produce the SMB Agent Admin implementation map and append bounded source-edit tasks.

## Required reads

Use the required reads listed on `TASK-FCSMB-AA-01-001` in `pending-tasks.md`.

## In scope

- Define Agent Admin vertical slices and capability contracts.
- Discover backend managed-agent/Agent Admin/runtime/source boundaries.
- Discover frontend surface/action/fixture/test boundaries.
- Identify deterministic responsibilities for reads, redaction, proposal lifecycle, activation/rollback, seed idempotency, provider readiness, and ToolPermissionBoundary enforcement.
- Identify model-backed responsibilities for AgentAdminAgent guidance and any later prompt-risk/behavior-review worker.
- Append bounded backend/frontend/validation tasks with exact paths and commands.

## Out of scope

- Do not implement source edits in this inspection task.
- Do not broaden to enterprise marketplace, arbitrary plugin/tool authoring, or model procurement.
- Do not let model-backed guidance mutate/activate behavior directly.

## Expected outputs

- `specs/full-core-smb-agent-admin/agent-admin-implementation-map.md`
- updated `specs/full-core-smb-agent-admin/pending-tasks.md`
- task briefs for appended implementation and validation tasks

## Required checks

- `git diff --check`
- targeted `find`/`rg` source discovery commands
- queue evidence search listed in the task entry

## Done criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The map clearly separates deterministic lifecycle/authorization/redaction responsibilities from governed model-backed guidance/worker responsibilities.
- The queue contains bounded next tasks and a terminal verification loop.

## Commit message

- `full-core-smb: map agent admin full core`
