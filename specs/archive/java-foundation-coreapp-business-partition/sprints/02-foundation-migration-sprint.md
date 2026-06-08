# Sprint 02: Foundation Package Migration

## Objective

Move common/base/platform Java code into `*.foundation.*` packages while preserving behavior.

## Scope

- Foundation domain records and shared contracts.
- Foundation application services/components/repositories.
- Foundation API endpoints/DTOs where they are base/platform endpoints.
- Tests and imports for the moved foundation classes.

## Acceptance criteria

- Foundation code lives under `api/application/domain.foundation.*`.
- `mvn test` passes or any blocker is recorded precisely.
- No business/coreapp behavior is changed beyond package/import updates.
