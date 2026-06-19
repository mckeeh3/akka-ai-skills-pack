# TASK-ADICM-03-001: Reconcile active specs with new current-intent graph

## Purpose

Update active specs, readiness docs, and relevant task/backlog references so they point to the reconstructed current-intent graph instead of legacy app-description paths.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/pending-task-queue.md`
- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/source-inventory.md`
- `specs/app-description-intent-compiler-migration/sprints/03-spec-reconciliation-sprint.md`
- populated `app-description/` graph
- active `specs/**/README.md`, `specs/**/pending-tasks.md`, and readiness docs that still cite old app-description paths

## Expected outputs

- updated active spec/readiness references to new graph nodes
- follow-up task or pending-question recommendations for unresolved drift
- updated queue status and notes

## Required checks

- `git diff --check`
- focused `rg` before/after evidence for old path references and new graph references
- pending-task contract validation if queue entries are materially changed and validator script exists

## Done criteria

- Active planning artifacts can be followed without relying on legacy app-description taxonomy.
- Any changed pending-task references remain bounded and runnable.
- Material runtime/spec drift is queued, blocked, deferred, or documented as out of scope.
- Changes and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: docs-only cross-cutting spec reconciliation
- Attention category or non-attention reason: non-runtime planning reference repair
- Role-specific dashboard / surface: none directly
- Surface graph node/action edge: none directly
- Governed-tool id and exposure: none directly
- Capability id: current-intent to realization planning references
- AuthContext / roles / tenant scope: preserve existing security/runtime task constraints when editing queues
- Akka substrate: docs/specs only
- API / frontend / realtime path: none directly
- Audit/work trace requirements: preserve existing audit/trace obligations in active tasks
- Local validation path: `git diff --check`, `rg` proof, optional queue validator
