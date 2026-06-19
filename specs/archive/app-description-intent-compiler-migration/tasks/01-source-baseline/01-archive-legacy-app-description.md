# TASK-ADICM-01-001: Archive legacy app-description as temporary migration input

## Purpose

Safely preserve the current `app-description/` tree as temporary migration provenance before reconstructing it into the intent-compiler graph shape.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/conversation-capture.md`
- `specs/app-description-intent-compiler-migration/sprints/01-source-baseline-sprint.md`
- `specs/app-description-intent-compiler-migration/backlog/01-app-description-intent-compiler-migration-build-backlog.md`
- `app-description/README.md`
- `app-description/00-system/app-manifest.md`

## Expected outputs

- temporary archive of current `app-description/` under this mini-project or another explicitly documented temporary path
- source manifest explaining archive purpose, non-authority status, and planned removal/scrub requirement
- updated queue status and notes

## Required checks

- `git diff --check`
- `find`/`rg` proof that the archive exists and is labelled temporary/non-authoritative

## Done criteria

- Current legacy docs are recoverable for migration source analysis.
- The archive is clearly not product authority.
- The task records that the archive must be removed/scrubbed before mini-project completion.
- Changes and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: docs-only cross-cutting source-baseline task
- Attention category or non-attention reason: non-runtime migration input capture
- Role-specific dashboard / surface: none
- Surface graph node/action edge: none
- Governed-tool id and exposure: none
- Capability id: app-description migration provenance
- AuthContext / roles / tenant scope: no runtime behavior change
- Akka substrate: docs/specs only
- API / frontend / realtime path: none
- Audit/work trace requirements: migration manifest/provenance only
- Local validation path: `git diff --check` plus archive/non-authority proof
