# Sprint 03: Skills-Pack Isolation and Reference Examples

## Objective

Move all skills-pack development and maintenance assets under top-level `skills-pack/` while preserving focused Akka reference examples for the skills.

## Scope

- Move `skills/`, `pack/`, skills-pack docs, install/package assets, and `akka-context/` as needed.
- Relocate old `com.example` focused Akka examples to `skills-pack/examples/akka-components/` or the target decided in Sprint 01.
- Update internal path references in skills and pack guidance.

## Acceptance criteria

- Skills-pack assets are isolated under `skills-pack/`.
- Focused examples remain findable by skills.
- Install/package validation can locate the moved assets.
