# TASK-WCTC-08-001: Expand Governance/Policy chat tool catalog

## Purpose

Add safe confirmed chat tool coverage for Governance/Policy actions selected by the inventory.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `app-description/domains/core-starter/workstreams/governance-policy/**`
- relevant Governance/Policy services/surfaces/tests

## Skills

- `ai-first-saas-policy-governance`
- `ai-first-saas-decision-cards`
- `capability-first-backend`
- `akka-agent-work-trace`

## Expected outputs

- expanded Governance/Policy chat tool entries for safe draft/simulation/impact-analysis/review paths selected by inventory
- activation, rollback, threshold changes, and live authority changes remain approval-gated/blocked unless fully modeled
- tests and queue update

## Required checks

- `git diff --check`
- targeted backend Governance/Policy chat tool tests
- frontend tests/typecheck if frontend contracts change

## Done criteria

- Policy changes remain governed proposals until explicit approval/activation paths are satisfied.
- Chat cannot directly activate or weaken policies.
- Changes and queue update are committed.
