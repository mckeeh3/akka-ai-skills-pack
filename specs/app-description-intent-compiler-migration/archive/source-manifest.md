# Temporary Legacy App-description Source Manifest

## Archive status

- Archived tree at capture time: `specs/app-description-intent-compiler-migration/archive/legacy-app-description/`
- Source tree at capture time: `app-description/`
- Captured for task: `TASK-ADICM-01-001`
- Removed for task: `TASK-ADICM-04-001`
- Capture date: 2026-06-08
- Captured file count: 88 Markdown files and other files under the legacy tree

## Authority status

The copied legacy tree was **temporary migration provenance only**. It was **not authoritative product current intent** and was not a source of truth for generated-app behavior, backend authorization, UI surfaces, workstream agents, policies, tests, or runtime readiness.

The legacy copy has now been removed after reconstruction of the current-intent graph and reconciliation of active specs. The authoritative current intent is the root `app-description/` graph.

## Removal record

`TASK-ADICM-04-001` removed the temporary copied legacy tree so future work cannot accidentally use archived legacy files as product authority. Historical details remain available through git history and completed task notes, not through an active file tree.

Active current-intent and planning work should use:

- `app-description/app.md`
- `app-description/global/**`
- `app-description/domains/core-starter/**`
- current readiness/spec evidence under `specs/full-core-saas-readiness/**` and related active specs

## Verification expectation

Terminal migration verification should prove that:

- no active `app-description/` node depends on the removed legacy copy;
- active non-archive specs do not instruct implementers to use the removed legacy copy as product authority; and
- `specs/app-description-intent-compiler-migration/archive/legacy-app-description/` does not exist.
