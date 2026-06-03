# Task Brief: Define Target Layout and Path Map

## Objective

Create the authoritative target-layout document for the core-app-first repository structure.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/core-app-first-repo-refactor/README.md`
- `specs/core-app-first-repo-refactor/conversation-capture.md`
- `specs/core-app-first-repo-refactor/sprints/01-architecture-inventory-sprint.md`
- current top-level tree, `templates/ai-first-saas-starter/README.md`, and `templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md`

## In scope

- Document target root app paths.
- Document target `skills-pack/` paths.
- Document old-to-new path map.
- Define fixed Java package/package-path policy for the core app.
- Define top-level domain extension directories/registries at a conceptual level.

## Out of scope

- Moving files.
- Updating all docs to the new paths.

## Expected outputs

- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`

## Required checks

- `git diff --check`

## Done criteria

- Future migration tasks can use the path map without guessing.
- The document explicitly states that the full-app template is not a maintained source after the migration.
