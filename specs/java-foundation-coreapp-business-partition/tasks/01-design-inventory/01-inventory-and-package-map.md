# Task Brief: Inventory Classes and Define Package Map

## Objective

Produce the authoritative class inventory and old-to-new package map for the Java package refactor.

## Required reads

- `AGENTS.md`
- `skills-pack/skills/README.md`
- `specs/java-foundation-coreapp-business-partition/README.md`
- `specs/java-foundation-coreapp-business-partition/conversation-capture.md`
- `specs/java-foundation-coreapp-business-partition/sprints/01-design-inventory-sprint.md`
- current `src/main/java/ai/first/**`
- current `src/test/java/ai/first/**` summary

## In scope

- Inventory all current root app Java packages/classes.
- Classify classes as foundation, coreapp, test-only, generated/static-resource unrelated, or ambiguous.
- Define old-to-new package map.
- Identify risky mixed-responsibility classes and recommended order.
- Define stale-reference search patterns and checks for later tasks.

## Out of scope

- Moving production Java files.

## Expected outputs

- `specs/java-foundation-coreapp-business-partition/classification-and-package-map.md`

## Required checks

- `git diff --check`

## Done criteria

- Later migration tasks can use the map without guessing.
- Ambiguous classes are either resolved or identified with a bounded blocker/follow-up recommendation.
