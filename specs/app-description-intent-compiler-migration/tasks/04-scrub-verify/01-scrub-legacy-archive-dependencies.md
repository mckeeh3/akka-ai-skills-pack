# TASK-ADICM-04-001: Scrub legacy archive dependencies and remove temporary archive

## Purpose

Remove temporary archive dependence from active content after the current-intent graph has been reconstructed and active specs have been reconciled.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/intent-compiler.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/source-inventory.md`
- `specs/app-description-intent-compiler-migration/sprints/04-scrub-verify-sprint.md`
- populated `app-description/` graph
- active specs touched by reconciliation
- temporary archive manifest

## Expected outputs

- active app-description/spec content scrubbed of archive-as-authority references
- temporary legacy archive removed or isolated as non-authoritative migration evidence according to README done state
- updated inventory/migration notes if needed to explain the scrub
- updated queue status and notes

## Required checks

- `git diff --check`
- `rg` proof that active `app-description/` and active specs do not instruct readers to use archived legacy docs as authority
- `find` proof that the temporary archive was removed or isolated as explicitly non-authoritative

## Done criteria

- Archived legacy docs are no longer part of real current-intent content.
- Active docs state current intent directly.
- The next terminal verification task can evaluate the mini-project without relying on the archive.
- Changes and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: docs-only migration scrub
- Attention category or non-attention reason: non-runtime cleanup
- Role-specific dashboard / surface: none
- Surface graph node/action edge: none
- Governed-tool id and exposure: none
- Capability id: current-intent authority cleanup
- AuthContext / roles / tenant scope: no runtime behavior change
- Akka substrate: docs/specs only
- API / frontend / realtime path: none
- Audit/work trace requirements: cleanup provenance in task notes
- Local validation path: `git diff --check` plus archive-dependency proof commands
