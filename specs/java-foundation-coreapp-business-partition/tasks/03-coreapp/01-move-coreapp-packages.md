# Task Brief: Move Core App Packages

## Objective

Move built-in five-core-app workstream and operational app Java classes into `ai.first.{api,application,domain}.coreapp.*` packages.

## Required reads

- `specs/java-foundation-coreapp-business-partition/classification-and-package-map.md`
- `specs/java-foundation-coreapp-business-partition/sprints/03-coreapp-migration-sprint.md`
- mapped coreapp source and tests
- `app-description/12-workstreams/functional-agents.md`

## In scope

- Move mapped coreapp classes for My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, and supporting operational app services.
- Update imports, package declarations, tests, resources/config if needed.
- Preserve existing runtime/API behavior.

## Out of scope

- Adding new workstreams or business features.
- Moving already-migrated foundation classes except import repairs.

## Expected outputs

- `src/main/java/ai/first/api/coreapp/**`
- `src/main/java/ai/first/application/coreapp/**`
- `src/main/java/ai/first/domain/coreapp/**` if coreapp-specific domain types exist
- updated tests/imports/resources

## Required checks

- `git diff --check`
- `mvn test`
- frontend tests/typecheck/build if API DTO/package references affect frontend contract tests
- stale old-package search proof for moved coreapp packages

## Done criteria

- Built-in core app workstream/operational code lives under `*.coreapp.*` packages.
- Existing checks pass and queue is updated/committed.
