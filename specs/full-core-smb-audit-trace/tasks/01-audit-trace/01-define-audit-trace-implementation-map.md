# Task: Define Audit/Trace vertical slice contracts and implementation map

## Objective

Inspect current Audit/Trace, trace repository/projection, workstream, agent/worker, frontend surface, and test boundaries. Produce the SMB Audit/Trace implementation map and append bounded source-edit tasks.

## Required reads

Use the required reads listed on `TASK-FCSMB-AT-01-001` in `pending-tasks.md`.

## In scope

- Define Audit/Trace vertical slices and capability contracts.
- Discover backend trace/audit/runtime/source boundaries.
- Discover frontend surface/action/fixture/test boundaries.
- Identify deterministic responsibilities for trace search/detail/timeline reads, redaction, correlation, authorization, tenant isolation, failure evidence shaping, and trace-link routing.
- Identify model-backed responsibilities for AuditTraceAgent guidance and any later audit-summary worker.
- Append bounded backend/frontend/validation tasks with exact paths and commands.

## Out of scope

- Do not implement source edits in this inspection task.
- Do not broaden to SIEM, legal hold, e-discovery, or enterprise compliance-suite features.
- Do not let model-backed guidance bypass trace authorization or redaction.

## Expected outputs

- `specs/full-core-smb-audit-trace/audit-trace-implementation-map.md`
- updated `specs/full-core-smb-audit-trace/pending-tasks.md`
- task briefs for appended implementation and validation tasks

## Required checks

- `git diff --check`
- targeted `find`/`rg` source discovery commands
- queue evidence search listed in the task entry

## Done criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The map clearly separates deterministic trace authorization/redaction/correlation responsibilities from governed model-backed explanation/worker responsibilities.
- The queue contains bounded next tasks and a terminal verification loop.

## Commit message

- `full-core-smb: map audit trace full core`
