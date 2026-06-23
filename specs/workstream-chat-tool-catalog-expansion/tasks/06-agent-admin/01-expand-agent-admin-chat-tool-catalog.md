# TASK-WCTC-06-001: Expand Agent Admin chat tool catalog

## Purpose

Add safe confirmed chat tool coverage for Agent Admin actions selected by the inventory.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- relevant Agent Admin seed/runtime/surface/tests

## Skills

- `akka-agent-behavior-profiles`
- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-tool-boundaries`
- `akka-agent-work-trace`

## Expected outputs

- expanded Agent Admin chat tool entries for safe proposal, review, simulation, test, seed, or tool-boundary paths selected by inventory
- approval-gated/blocked handling for activation, rollback, deactivation, and authority expansion when prerequisites are incomplete
- tests and queue update

## Required checks

- `git diff --check`
- targeted backend Agent Admin chat tool tests
- seed/import tests if seed resources change
- frontend tests/typecheck if frontend contracts change

## Done criteria

- Prompt/skill/reference/model/tool-boundary text cannot grant authority.
- High-impact lifecycle changes remain approval-gated or blocked unless fully modeled.
- Changes and queue update are committed.
