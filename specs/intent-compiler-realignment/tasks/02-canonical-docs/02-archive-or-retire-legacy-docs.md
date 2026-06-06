# Task: Archive or Retire Legacy Intent Docs

## Objective

Apply the inventory-approved archive/retirement strategy to pre-intent-compiler documentation.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/intent-compiler-realignment/README.md`
- `specs/intent-compiler-realignment/intent-processing-inventory.md`
- `specs/intent-compiler-realignment/sprints/02-canonical-docs-sprint.md`
- canonical intent compiler docs created by the previous task

## In scope

- Move, mark, or replace legacy docs according to the inventory plan.
- Preserve useful reference material without presenting it as active doctrine.
- Update doc links that would otherwise point to obsolete source-of-truth docs.

## Out of scope

- Skill rewrites except minimal link fixes required by doc movement.

## Expected outputs

- archived/retired legacy docs or clear deprecation notes
- updated active doc links
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- install reference check if doc moves affect skill references

## Done criteria

- Active docs point to the canonical intent compiler model.
- Legacy docs are no longer ambiguous sources of truth.
