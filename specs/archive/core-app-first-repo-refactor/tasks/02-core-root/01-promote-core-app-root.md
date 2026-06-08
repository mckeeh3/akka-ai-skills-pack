# Task Brief: Promote Core App to Repository Root

## Objective

Make the root `pom.xml`, `src/`, and `frontend/` the canonical runnable core app source according to the path map.

## Required reads

- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- `specs/core-app-first-repo-refactor/asset-migration-inventory.md`
- `templates/ai-first-saas-starter/README.md`
- relevant backend/frontend package and build files

## In scope

- Reconcile root backend/frontend with the current canonical core app implementation.
- Apply fixed Java package/path policy if selected by the path map.
- Preserve static asset build behavior.
- Update app-local build files needed for root validation.

## Out of scope

- Moving skills-pack assets.
- Deleting the old template before parity is proven.

## Expected outputs

- Updated root `pom.xml`, `src/**`, `frontend/**`, and app build resources as needed.

## Required checks

- `git diff --check`
- root backend focused tests or full `mvn test` when practical
- frontend `npm test`, `npm run typecheck`, and `npm run build` from root `frontend/` when practical

## Done criteria

- Root app is the canonical runnable core app at the current migrated scope.
- No duplicate root/template delta remains unaccounted for in notes or follow-up tasks.
