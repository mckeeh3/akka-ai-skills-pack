# Task Brief: Relocate Focused Akka Reference Examples

## Objective

Move focused Akka component examples used by skills out of root app source and into the skills-pack internal examples area.

## Required reads

- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- `specs/core-app-first-repo-refactor/asset-migration-inventory.md`
- `skills/README.md` or moved equivalent
- root `src/main/java/com/example/**` and `src/test/java/com/example/**` inventory

## In scope

- Move non-core `com.example` reference examples to `skills-pack/examples/akka-components/` or target path.
- Update skill references to those examples.
- Preserve examples for component families not naturally covered by the core app, including workflows, timed actions, consumers, gRPC, and MCP.

## Out of scope

- Changing core app behavior.
- Updating old historical specs except when required for active path correctness.

## Expected outputs

- Focused reference examples under skills-pack internal examples path.
- Updated skill/example path references.

## Required checks

- `git diff --check`
- search proof that installable guidance no longer points to root `src/main/java/com/example` as canonical generated-app source

## Done criteria

- Root `src/` represents the runnable core app, not mixed reference examples.
- Skills still have focused examples to reference.
