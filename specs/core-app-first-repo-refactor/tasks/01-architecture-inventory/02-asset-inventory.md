# Task Brief: Inventory Assets for Migration

## Objective

Classify existing repository assets before broad moves.

## Required reads

- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- `templates/ai-first-saas-starter/**` summary
- top-level `src/`, `frontend/`, `docs/`, `skills/`, `pack/`, `tools/`, `specs/` summaries

## In scope

- Inventory app source, template source, frontend copies, static resources, skills, docs, examples, pack/install assets, validation tools, and active specs.
- Classify each major asset as promote, move, remove, archive, retain, or update-reference.

## Out of scope

- Moving assets.

## Expected outputs

- `specs/core-app-first-repo-refactor/asset-migration-inventory.md`

## Required checks

- `git diff --check`

## Done criteria

- Major duplicate-source areas are classified.
- Active specs with stale path dependencies are identified or explicitly deferred to tooling/guidance tasks.
