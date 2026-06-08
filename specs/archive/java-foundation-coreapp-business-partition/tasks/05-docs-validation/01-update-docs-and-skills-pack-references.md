# Task Brief: Update Docs and Skills-Pack References

## Objective

Update root app docs, app-description/spec maps, and skills-pack references to use the foundation/coreapp/business package model.

## Required reads

- `specs/java-foundation-coreapp-business-partition/classification-and-package-map.md`
- `specs/java-foundation-coreapp-business-partition/sprints/05-docs-validation-sprint.md`
- `AGENTS.md`
- `README.md`
- `app-description/**` package/implementation references
- `skills-pack/skills/README.md`
- relevant skills-pack docs mentioning Java packages or extension paths

## In scope

- Update root guidance and app-description/spec references.
- Update skills-pack guidance so generated business-specific work uses `business.<area>` packages inside the standard Akka layers.
- Remove stale guidance for old `security`/`agentfoundation` top-level package partitions where they are no longer correct.

## Out of scope

- Moving more Java files.
- Redesigning unrelated skill content.

## Expected outputs

- Updated docs/app-description/specs/skills-pack references.

## Required checks

- `git diff --check`
- stale package-reference search proof
- skills-pack install/build checks if pack docs/tooling are touched substantially

## Done criteria

- Documentation consistently explains foundation/coreapp/business from an outside-in user perspective.
- Queue is updated/committed.
