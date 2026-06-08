# TASK-ADICM-01-002: Inventory implementation and source evidence

## Purpose

Inventory source evidence for reconstructing the current-intent graph, using both the temporary legacy app-description archive and the current root implementation.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/intent-compiler.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/ai-first-saas-application-architecture.md`
- `.agents/skills/docs/capability-first-backend-architecture.md`
- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/conversation-capture.md`
- `specs/app-description-intent-compiler-migration/sprints/01-source-baseline-sprint.md`
- temporary legacy app-description archive manifest from `TASK-ADICM-01-001`
- root source directories as inventory targets: `src/main/java/ai/first/`, `src/test/java/ai/first/`, `frontend/src/`, active `specs/`, and root `docs/`

## Expected outputs

- `specs/app-description-intent-compiler-migration/source-inventory.md`
- classification of findings into foundation references, core starter current intent, stale/legacy exclusions, and drift/pending-question candidates
- updated queue status and notes

## Required checks

- `git diff --check`
- focused `rg`/`find` evidence that inventory covers backend, frontend, tests, docs/specs, and legacy archive source classes

## Done criteria

- Future graph reconstruction tasks can proceed without guessing source evidence.
- Foundation doctrine to reference is separated from app-specific current intent to capture.
- Drift and unresolved decisions are explicit.
- Changes and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: docs-only cross-cutting inventory for all five core starter workstreams
- Attention category or non-attention reason: non-runtime evidence inventory
- Role-specific dashboard / surface: none
- Surface graph node/action edge: none
- Governed-tool id and exposure: none
- Capability id: app-description/source evidence mapping
- AuthContext / roles / tenant scope: inventory must flag auth/tenant/trace evidence but not change runtime behavior
- Akka substrate: docs/specs only
- API / frontend / realtime path: none
- Audit/work trace requirements: inventory should identify trace/audit artifacts and gaps
- Local validation path: `git diff --check` plus coverage proof commands
