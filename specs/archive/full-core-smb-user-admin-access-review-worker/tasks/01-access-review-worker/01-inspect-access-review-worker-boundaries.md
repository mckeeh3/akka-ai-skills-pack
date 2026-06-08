# Task: Inspect access-review worker source boundaries and define implementation map

## Objective

Inspect the starter's User Admin, workstream, agent/worker, seed, frontend surface, and test boundaries. Produce an implementation map and append bounded source-edit tasks for the durable SMB access-review worker.

## Required reads

Use the required reads listed on `TASK-FCSMB-UARW-01-001` in `pending-tasks.md`.

## In scope

- Find current User Admin access-management and guidance runtime files.
- Identify whether existing code has internal worker / `AutonomousAgent` foundation sufficient for this slice.
- Define deterministic lifecycle responsibilities: start, read, cancel, accept result, reject result, idempotency, authorization, tenant isolation, audit/trace.
- Define governed worker responsibilities: model-backed investigation/summarization, scoped evidence use, prompt/skill/reference/tool traces, provider fail-closed behavior.
- Define frontend surface responsibilities for `user_admin.access_review_task.v1`.
- Append source-edit tasks with exact paths and validation commands.

## Out of scope

- Do not implement source edits in this inspection task.
- Do not broaden to enterprise access certification or scheduled campaign management.
- Do not make worker output an access mutation path.

## Expected outputs

- `specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md`
- updated `pending-tasks.md`
- task briefs for appended backend/frontend/validation tasks

## Required checks

- `git diff --check`
- targeted source discovery with `find`/`rg`
- queue evidence search listed in the task entry

## Done criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The map clearly separates deterministic services from governed worker/model responsibilities.
- The queue contains bounded next tasks and a terminal verification loop.

## Commit message

- `full-core-smb: map user admin access review worker`
