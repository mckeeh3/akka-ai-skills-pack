# Task Brief: Move Foundation Application and API Layers

## Objective

Move foundation application services/components/repositories and foundation API endpoints/DTOs into `ai.first.application.foundation.*` and `ai.first.api.foundation.*` packages.

## Required reads

- `specs/java-foundation-coreapp-business-partition/classification-and-package-map.md`
- `specs/java-foundation-coreapp-business-partition/sprints/02-foundation-migration-sprint.md`
- mapped foundation application/API source and tests

## In scope

- Move mapped foundation application classes.
- Move mapped foundation API classes.
- Update imports, package declarations, tests, resources/config if needed.
- Preserve Akka component discovery and endpoint behavior.

## Out of scope

- Moving coreapp-specific workstream classes except imports caused by foundation moves.

## Expected outputs

- `src/main/java/ai/first/application/foundation/**`
- `src/main/java/ai/first/api/foundation/**`
- updated tests/imports/resources

## Required checks

- `git diff --check`
- `mvn test`
- stale old-package search proof for moved foundation application/API packages

## Done criteria

- Foundation application/API classes live under `*.foundation.*` packages.
- Tests pass and queue is updated/committed.
