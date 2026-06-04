# Task Brief: Move Foundation Domain Layer

## Objective

Move common/base domain-layer records and contracts into `ai.first.domain.foundation.*` packages.

## Required reads

- `specs/java-foundation-coreapp-business-partition/classification-and-package-map.md`
- `specs/java-foundation-coreapp-business-partition/sprints/02-foundation-migration-sprint.md`
- current mapped `src/main/java/ai/first/domain/**`
- tests importing moved domain classes

## In scope

- Move domain-layer foundation classes.
- Update package declarations/imports/tests.
- Preserve behavior.

## Out of scope

- Moving application/API classes except imports required by domain package changes.
- Reclassifying classes outside the accepted map unless a precise blocker is recorded.

## Expected outputs

- `src/main/java/ai/first/domain/foundation/**`
- updated imports/tests

## Required checks

- `git diff --check`
- `mvn test`
- search proof for moved old foundation domain packages

## Done criteria

- Foundation domain classes live under `ai.first.domain.foundation.*`.
- Tests pass and queue is updated/committed.
