# Task Brief: Move Skills-Pack Assets Under skills-pack

## Objective

Isolate skills-pack development and maintenance assets under top-level `skills-pack/`.

## Required reads

- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- `specs/core-app-first-repo-refactor/asset-migration-inventory.md`
- `install.sh`
- `pack/AGENTS.md`
- `skills/README.md`

## In scope

- Move skills, pack assets, skills-pack docs, installer/package manifests, and `akka-context/` according to the path map.
- Update direct path references required for pack installation or skill loading.
- Keep project-only `.agents/skills/project-discussed-idea-to-pending-project` out of installable pack assets.

## Out of scope

- Relocating focused Java examples; separate task.
- Rewriting every skill beyond path correctness.

## Expected outputs

- `skills-pack/**` containing skills-pack source/maintenance assets.
- Updated install/package references.

## Required checks

- `git diff --check`
- install/package dry-run or equivalent path-resolution check if available

## Done criteria

- Skills-pack assets are visibly isolated under `skills-pack/`.
- Core app root no longer contains skills-pack maintenance directories except app docs/specs/tools.
