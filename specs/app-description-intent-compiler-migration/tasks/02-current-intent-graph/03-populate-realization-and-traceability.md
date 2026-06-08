# TASK-ADICM-02-003: Populate realization and traceability mappings

## Purpose

Map current-intent graph nodes to known Akka, API, frontend, test, and validation artifacts without implementing runtime gaps.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/source-inventory.md`
- `specs/app-description-intent-compiler-migration/sprints/02-current-intent-graph-sprint.md`
- populated `app-description/` graph from `TASK-ADICM-02-002`
- relevant source/test/frontend paths identified by the inventory

## Expected outputs

- realization files under each workstream such as `realization/akka-components.md`, `realization/api-contracts.md`, and `realization/frontend-routes.md`
- app/domain/workstream traceability notes linking current intent to current implementation evidence
- drift list or follow-up task recommendations where implementation does not match current intent
- updated queue status and notes

## Required checks

- `git diff --check`
- focused `rg` proof that realization mappings mention backend, API, frontend, tests, auth/tenant scope, and audit/work-trace paths where applicable

## Done criteria

- Current-intent graph can be traced to known implementation/test artifacts or explicit gaps.
- Runtime gaps are not counted as implemented by description alone.
- Changes and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: docs-only realization mapping for all five core workstreams
- Attention category or non-attention reason: non-runtime traceability mapping
- Role-specific dashboard / surface: mapped where implemented/described
- Surface graph node/action edge: mapped to frontend/API artifacts where known
- Governed-tool id and exposure: mapped where known; gaps recorded
- Capability id: core starter capability realization mapping
- AuthContext / roles / tenant scope: mapping must include auth/tenant evidence or gaps
- Akka substrate: docs/specs only; maps entities/workflows/views/consumers/timers/agents/endpoints/frontend/tests
- API / frontend / realtime path: mapped where known
- Audit/work trace requirements: mapped or gap-recorded
- Local validation path: `git diff --check` plus traceability proof
