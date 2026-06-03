# Task Brief: Dissolve Full-App Template

## Objective

Remove the large full-app template as a maintained duplicate source after root core app parity is established.

## Required reads

- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- `specs/core-app-first-repo-refactor/asset-migration-inventory.md`
- `templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md`
- scaffold/install script references to the template

## In scope

- Remove or archive `templates/ai-first-saas-starter/` according to the path map.
- Remove Java placeholder rendering assumptions from app-source paths.
- Preserve small extension templates only if explicitly classified in the inventory.
- Update immediate build/tool references needed to avoid broken root validation.

## Out of scope

- Full documentation rewrite; that belongs to guidance tasks.

## Expected outputs

- Deleted/archived full-app template duplicate.
- Updated or removed scaffold script references that would now fail.

## Required checks

- `git diff --check`
- search proof for remaining `templates/ai-first-saas-starter` references classified as historical, pending migration, or updated

## Done criteria

- There is no maintained second copy of the full app.
- Remaining template references are intentional and documented.
