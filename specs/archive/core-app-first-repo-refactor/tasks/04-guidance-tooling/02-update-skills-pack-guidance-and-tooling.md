# Task Brief: Update Skills-Pack Guidance and Tooling

## Objective

Update skills-pack docs, installed-pack guidance, install scripts, and validation tooling for the new layout and product model.

## Required reads

- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- moved `skills-pack/skills/README.md`
- moved `skills-pack/pack/AGENTS.md`
- moved install/package scripts
- `docs/pending-task-queue.md` or moved equivalent if path changes

## In scope

- Replace scaffold-first/full-app-template guidance with fork-and-extend guidance.
- Update path references from old root paths to `skills-pack/` or top-level app paths.
- Update validation scripts for root app and skills-pack packaging.
- Preserve guidance that downstream users should add `domain-specific` extensions without editing skills-pack internals unless maintaining the pack.

## Out of scope

- Full skill content redesign beyond layout/model correctness.

## Expected outputs

- Updated skills-pack docs/scripts/tooling.

## Required checks

- `git diff --check`
- package/install dry-run or equivalent
- search proof for stale scaffold/template claims

## Done criteria

- Installed-pack and source-pack guidance consistently describe the new core-app-first workflow.
