# TASK-WCTC-07-001: Expand Audit/Trace chat tool catalog

## Purpose

Add safe confirmed chat tool coverage for Audit/Trace actions selected by the inventory.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `app-description/domains/core-starter/workstreams/audit-trace/**`
- relevant Audit/Trace services/surfaces/tests

## Skills

- `capability-first-backend`
- `ai-first-saas-audit-trace`
- `akka-agent-work-trace`

## Expected outputs

- expanded Audit/Trace chat tool entries for safe search/detail/timeline/failure-evidence/investigation-note paths selected by inventory
- export/raw evidence handling remains approval-gated/surface-only unless fully modeled
- tests and queue update

## Required checks

- `git diff --check`
- targeted backend Audit/Trace chat tool tests
- frontend tests/typecheck if frontend contracts change

## Done criteria

- Trace data remains scoped, redacted, and browser-safe.
- Export or privileged raw evidence paths cannot execute through chat unless full approval/redaction policy is modeled.
- Changes and queue update are committed.
