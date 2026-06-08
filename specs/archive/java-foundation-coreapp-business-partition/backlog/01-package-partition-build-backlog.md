# Backlog: Java Foundation/Coreapp/Business Package Partition

## Goal

Create a clear outside-in Java package boundary for users who fork the core app and add business-specific SaaS domains, while preserving Akka's standard `api` / `application` / `domain` layering.

## Suggested harness task breakdown

1. Inventory current Java classes and produce the target package map.
2. Move foundation domain records/contracts.
3. Move foundation application services/components and foundation APIs.
4. Move coreapp workstream/domain/application/API classes.
5. Add business package convention docs/package-info and optional boundary checks.
6. Update root docs, app-description/spec maps, and skills-pack guidance.
7. Run terminal verification and append follow-up tasks if gaps remain.

## Dependencies

- Inventory and package map must happen before source moves.
- Foundation migration should happen before coreapp migration where coreapp imports foundation types.
- Documentation and skills-pack updates should happen after package moves stabilize.
- Terminal verification depends on all migration and docs tasks.

## Required checks by phase

- `git diff --check` for every task.
- `mvn test` for Java package moves.
- Frontend tests/typecheck/build only when API DTO names/contracts or frontend docs/tests are affected.
- Search proof for stale old package names after migration tasks.
- Skills-pack install/build checks only when skills-pack packaging or installed docs are touched.

## Acceptance criteria

The backlog is complete when the README done state is satisfied and terminal verification records no material unqueued gaps.
