# TASK-WCTC-03-001: Add catalog classification and prompt guardrails

## Purpose

Extend the shared backend chat tool catalog with classification metadata and prompt-classification guardrails before per-workstream expansion.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- related tests named by the inventory

## Skills

- `capability-first-backend`
- `akka-agent-tool-boundaries`
- `akka-agent-work-trace`

## Expected outputs

- backend catalog classification fields and rationale
- prompt guardrails preserving deterministic surface routing first
- tests for unsafe exposure prevention and classification behavior
- queue update

## Required checks

- `git diff --check`
- targeted backend catalog/guardrail tests

## Done criteria

- Unsupported/high-risk prompts cannot silently become executable steps.
- Catalog entries carry enough metadata for frontend/trace/test reporting.
- Changes and queue update are committed.
